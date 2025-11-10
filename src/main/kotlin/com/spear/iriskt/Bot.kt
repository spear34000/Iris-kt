package com.spear.iriskt

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.serialization.kotlinx.json.json
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import com.spear.iriskt.core.BatchScheduler
import com.spear.iriskt.core.Config
import com.spear.iriskt.core.IrisLink
import com.spear.iriskt.internal.EventEmitter
import com.spear.iriskt.api.IrisApiClient
import com.spear.iriskt.models.ChatContext
import com.spear.iriskt.models.ErrorContext
import com.spear.iriskt.models.Message
import com.spear.iriskt.models.Room
import com.spear.iriskt.models.User
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import java.io.Closeable
import java.net.URI

data class BotOptions(
    val maxWorkers: Int = 4,
    val httpMode: Boolean = false,
    val port: Int? = null,
    val webhookPath: String? = null,
    val logLevel: LogLevel = LogLevel.INFO,
    val bannedUsers: Set<Long> = emptySet(),
    val kakaoLinkAppKey: String? = null,
    val kakaoLinkOrigin: String? = null,
    val autoRegisterControllers: Boolean = false,
    val saveChatLogs: Boolean = false,
    val chatLogPath: String = "./chat_logs"
)

enum class LogLevel {
    DEBUG, INFO, WARN, ERROR
}

private val logger = com.spear.iriskt.core.LoggerManager.defaultLogger

class Bot(
    private val botName: String,
    irisUrl: String,
    options: BotOptions = BotOptions()
) : Closeable {
    private val config = Config
    private val scheduler = BatchScheduler.getInstance()
    private val irisLink = IrisLink(options.kakaoLinkAppKey, options.kakaoLinkOrigin)

    private val emitter = EventEmitter(options.maxWorkers)
    private val endpoint = irisUrl.removeSuffix("/")
    private val websocketEndpoint = buildWebSocketEndpoint(endpoint)
    
    // HTTP 클라이언트 재사용 (싱글톤)
    private val httpClient by lazy { createHttpClient() }
    private val jsonParser by lazy { createJsonParser() }
    private val apiClient by lazy { IrisApiClient(endpoint, httpClient, jsonParser) }
    
    // Webhook 서버 (HTTP 모드용)
    private var webhookServer: com.spear.iriskt.core.WebhookServer? = null
    
    // 채팅 로거
    private val chatLogger by lazy {
        com.spear.iriskt.core.ChatLogger(
            logPath = options.chatLogPath,
            enabled = options.saveChatLogs
        )
    }
    
    private val reconnectDelayMillis = 3000L
    private var closed = false

    // 옵션 노출
    val options = options
    
    // 차단 사용자 빠른 조회를 위한 Set (이미 Set이지만 명시)
    private val bannedUsersSet = options.bannedUsers
    
    // 봇 이름 접근자
    val name: String get() = botName

    init {
        // 초기??로그
        logger.info("Bot 초기?? $botName")
        logger.info("IRIS URL: $irisUrl")
        logger.info("최�? ?�커 ?? ${options.maxWorkers}")
        if (options.bannedUsers.isNotEmpty()) {
            logger.info("차단???�용???? ${options.bannedUsers.size}")
        }
    }

    suspend fun run() {
        logger.info("Bot 시작: $botName")

        // IrisLink 초기화 시도
        try {
            irisLink.init()
            logger.info("IrisLink 초기화 성공")
        } catch (e: Exception) {
            logger.warn("IrisLink 초기화 실패: ${e.message}")
        }
        
        // 컨트롤러 자동 등록
        if (options.autoRegisterControllers) {
            logger.info("컨트롤러 자동 등록 활성화")
            // 자동 등록 로직은 ControllerManager에서 처리
        }
        
        // HTTP 모드 또는 WebSocket 모드
        if (options.httpMode) {
            runHttpMode()
        } else {
            runWebSocketMode()
        }
    }
    
    /**
     * WebSocket 모드 실행
     */
    private suspend fun runWebSocketMode() {
        logger.info("WebSocket 모드로 실행")
        
        while (!closed) {
            try {
                // WebSocket 클라이언트 재사용
                httpClient.webSocket(websocketEndpoint) {
                    logger.info("WebSocket connection established")
                    for (frame in incoming) {
                        when (frame) {
                            is Frame.Text -> handleFrame(frame.readText())
                            else -> Unit
                        }
                    }
                }
            } catch (ex: CancellationException) {
                throw ex
            } catch (ex: Exception) {
                if (closed) return
                logger.error("WebSocket 연결 오류", ex)
                logger.info("WebSocket connection error")
                logger.info("Reconnecting in ${reconnectDelayMillis / 1000} seconds")
                delay(reconnectDelayMillis)
            }
        }
    }
    
    /**
     * HTTP/Webhook 모드 실행
     */
    private suspend fun runHttpMode() {
        logger.info("HTTP/Webhook 모드로 실행")
        
        val port = options.port ?: 3000
        val path = options.webhookPath ?: "/webhook/message"
        
        webhookServer = com.spear.iriskt.core.WebhookServer(port, path) { payload ->
            handleFrame(payload)
        }
        
        webhookServer?.start()
        
        // 서버가 종료될 때까지 대기
        while (!closed) {
            delay(1000)
        }
    }

    fun onEvent(name: String, handler: suspend (Any) -> Unit) {
        emitter.register(name, handler)
    }

    override fun close() {
        logger.info("Bot 종료: $botName")
        closed = true
        emitter.close()
        scheduler.clearAll()
        webhookServer?.stop()
        httpClient.close()
    }
    
    /**
     * 봇 중지 (별칭)
     */
    fun stop() {
        close()
    }

    fun api(): IrisApiClient = apiClient

    fun getScheduler(): BatchScheduler = scheduler

    fun getIrisLink(): IrisLink = irisLink

    fun isBannedUser(userId: Long): Boolean {
        return bannedUsersSet.contains(userId)
    }

    fun getConfig(): Config = config
    
    // 컨트롤러 관리자
    private val controllerManager by lazy { 
        com.spear.iriskt.core.ControllerManager(this)
    }
    
    // 등록된 명령어 목록
    private val registeredCommands = mutableMapOf<String, CommandInfo>()
    
    /**
     * 컨트롤러 등록
     */
    fun registerController(controller: Any) {
        logger.info("컨트롤러 등록: ${controller::class.simpleName}")
        controllerManager.registerController(controller)
    }
    
    /**
     * 여러 컨트롤러 등록
     */
    fun registerControllers(controllers: List<Any>) {
        controllers.forEach { registerController(it) }
    }
    
    /**
     * 컨트롤러 클래스로 등록
     */
    fun registerControllers(vararg controllerClasses: kotlin.reflect.KClass<*>) {
        controllerClasses.forEach { kClass ->
            try {
                val instance = kClass.constructors.first().call()
                registerController(instance)
            } catch (e: Exception) {
                logger.error("컨트롤러 인스턴스 생성 실패: ${kClass.simpleName}", e)
            }
        }
    }
    
    /**
     * 등록된 명령어 목록 조회
     */
    fun getRegisteredCommands(): Map<String, CommandInfo> {
        return registeredCommands.toMap()
    }
    
    /**
     * 명령어 정보
     */
    data class CommandInfo(
        val name: String,
        val description: String,
        val controller: String
    )

    private fun handleFrame(payload: String) {
        val envelope = runCatching { jsonParser.parseToJsonElement(payload).jsonObject }.getOrElse {
            logger.error("WebSocket 메시지 파싱 오류", it)
            return
        }
        val request = IrisRequest(
            msg = envelope["msg"]?.jsonPrimitive?.contentOrNull,
            room = envelope["room"]?.jsonPrimitive?.contentOrNull,
            sender = envelope["sender"]?.jsonPrimitive?.contentOrNull,
            raw = extractRaw(envelope)
        )
        processRequest(request)
    }

    // 빈 JsonObject 캐싱
    private val emptyJsonObject = JsonObject(emptyMap())
    
    private fun extractRaw(envelope: JsonObject): JsonObject {
        // raw 필드 우선 확인
        envelope["raw"]?.let { raw ->
            if (raw is JsonObject) return raw
        }
        
        // json 필드 확인
        envelope["json"]?.let { json ->
            when (json) {
                is JsonObject -> return json
                is JsonPrimitive -> {
                    json.contentOrNull?.let { text ->
                        runCatching { jsonParser.parseToJsonElement(text) }.getOrNull()
                            ?.let { if (it is JsonObject) return it }
                    }
                }
                is JsonArray -> {
                    runCatching { jsonParser.parseToJsonElement(json.toString()) }.getOrNull()
                        ?.let { if (it is JsonObject) return it }
                }
            }
        }
        
        return emptyJsonObject
    }

    private fun processRequest(request: IrisRequest) {
        val raw = request.raw
        val roomId = raw.longField("chat_id") ?: raw.longField("room_id") ?: request.room?.toLongOrNull() ?: -1L
        val roomName = request.room ?: raw["room"]?.jsonPrimitive?.contentOrNull ?: ""
        val userId = raw.longField("user_id") ?: -1L
        val userName = request.sender ?: raw["user"]?.jsonPrimitive?.contentOrNull ?: ""
        val messageId = raw.longField("id") ?: -1L
        val messageType = raw.intField("type") ?: 0
        val messageText = raw["message"]?.jsonPrimitive?.contentOrNull ?: request.msg.orEmpty()
        val attachment = raw["attachment"]?.jsonPrimitive?.contentOrNull
        val metadata = parseMetadata(raw)

        val context = ChatContext(
            room = Room(id = roomId, name = roomName),
            sender = User(id = userId, name = userName),
            message = Message(
                id = messageId,
                type = messageType,
                text = messageText,
                attachment = attachment,
                metadata = metadata
            ),
            raw = raw,
            api = apiClient
        )
        
        // 채팅 로그 저장
        if (options.saveChatLogs) {
            kotlinx.coroutines.GlobalScope.launch {
                chatLogger.log(context)
            }
        }
        
        emitter.emit("chat", context)
        val origin = (metadata as? JsonObject)?.let { it["origin"]?.jsonPrimitive?.contentOrNull?.uppercase() }
        when (origin) {
            "MSG" -> emitter.emit("message", context)
            "NEWMEM" -> emitter.emit("new_member", context)
            "DELMEM" -> emitter.emit("del_member", context)
        }
    }

    private fun parseMetadata(raw: JsonObject): JsonElement? {
        val value = raw["v"] ?: return null
        return when (value) {
            is JsonObject, is JsonArray -> value
            is JsonPrimitive -> {
                val text = value.contentOrNull ?: return null
                runCatching { jsonParser.parseToJsonElement(text) }.getOrNull()
            }
            else -> null
        }
    }

    private fun JsonObject.longField(vararg keys: String): Long? = keys.firstNotNullOfOrNull { key ->
        val element = this[key] ?: return@firstNotNullOfOrNull null
        when (element) {
            is JsonPrimitive -> element.longOrNull ?: element.contentOrNull?.toLongOrNull()
            else -> null
        }
    }

    private fun JsonObject.intField(vararg keys: String): Int? = keys.firstNotNullOfOrNull { key ->
        val element = this[key] ?: return@firstNotNullOfOrNull null
        when (element) {
            is JsonPrimitive -> element.intOrNull ?: element.contentOrNull?.toIntOrNull()
            else -> null
        }
    }

    private fun createHttpClient(): HttpClient = HttpClient(CIO) {
        install(WebSockets) {
            pingInterval = 20_000 // 20초마다 ping
            maxFrameSize = Long.MAX_VALUE
        }
        install(ContentNegotiation) {
            json(jsonParser)
        }
        engine {
            maxConnectionsCount = 100
            endpoint {
                maxConnectionsPerRoute = 10
                pipelineMaxSize = 20
                keepAliveTime = 5000
                connectTimeout = 5000
                connectAttempts = 3
            }
        }
    }

    private fun createJsonParser(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
        coerceInputValues = true
    }

    companion object {
        private fun buildWebSocketEndpoint(httpUrl: String): String {
            val uri = URI(httpUrl)
            val scheme = when (uri.scheme) {
                "http" -> "ws"
                "https" -> "wss"
                else -> uri.scheme
            }
            return URI(
                scheme,
                uri.userInfo,
                uri.host,
                uri.port,
                (uri.path.takeIf { it.isNotEmpty() } ?: "/") + if (uri.path.endsWith("/")) "ws" else "/ws",
                uri.query,
                uri.fragment
            ).toString()
        }
    }
}

private data class IrisRequest(
    val msg: String?,
    val room: String?,
    val sender: String?,
    val raw: JsonObject
)
