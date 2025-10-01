package iriskt.bot.annotations

import iriskt.bot.models.ChatContext
import iriskt.bot.models.User
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

/**
 * 명령어 실행 제한을 관리하는 클래스
 */
class ThrottleManager {
    private val callCounts = ConcurrentHashMap<String, MutableList<Long>>()
    private val mutex = Mutex()

    suspend fun isAllowed(key: String, maxCalls: Int, timeWindowMs: Long): Boolean {
        return mutex.withLock {
            val now = System.currentTimeMillis()
            val calls = callCounts.getOrPut(key) { mutableListOf() }

            // 시간 창 밖의 호출 기록 제거
            calls.removeAll { now - it > timeWindowMs }

            // 호출 횟수 확인
            if (calls.size >= maxCalls) {
                return@withLock false
            }

            // 호출 기록 추가
            calls.add(now)
            true
        }
    }
}

/**
 * 어노테이션 기반 명령어 처리 시스템
 */
object CommandProcessor {
    private val throttleManager = ThrottleManager()

    /**
     * 등록된 모든 명령어 핸들러를 처리
     */
    suspend fun processCommand(context: ChatContext, command: String, param: String): Boolean {
        // 명령어 매핑을 찾기 위해 모든 등록된 핸들러를 확인해야 함
        // 실제 구현에서는 등록 시점에 매핑을 구성해야 함

        return false // 아직 구현되지 않음
    }

    /**
     * 메서드에 적용된 어노테이션들을 확인하고 조건을 검증
     */
    suspend fun validateCommandConditions(
        context: ChatContext,
        method: KFunction<*>
    ): Boolean {
        // @HasParam 검증
        if (method.hasAnnotation<HasParam>() && !context.message.hasParam) {
            context.reply("❌ 이 명령어는 매개변수가 필요합니다.")
            return false
        }

        // @IsAdmin 검증
        if (method.hasAnnotation<IsAdmin>()) {
            val userType = context.sender.getType()
            if (userType != "HOST" && userType != "MANAGER") {
                context.reply("❌ 이 명령어는 관리자만 사용할 수 있습니다.")
                return false
            }
        }

        // @HasRole 검증
        method.findAnnotation<HasRole>()?.let { hasRole ->
            val userType = context.sender.getType()
            if (userType !in hasRole.roles) {
                context.reply("❌ 이 명령어는 ${hasRole.roles.joinToString(", ")} 권한이 필요합니다.")
                return false
            }
        }

        // @IsNotBanned 검증
        if (method.hasAnnotation<IsNotBanned>()) {
            // 실제로는 Bot.isBannedUser()를 사용해야 함
            // 현재는 간단히 통과
        }

        // @AllowedRoom 검증
        method.findAnnotation<AllowedRoom>()?.let { allowedRoom ->
            if (context.room.name !in allowedRoom.roomNames) {
                context.reply("❌ 이 명령어는 ${allowedRoom.roomNames.joinToString(", ")} 방에서만 사용할 수 있습니다.")
                return false
            }
        }

        // @Throttle 검증
        method.findAnnotation<Throttle>()?.let { throttle ->
            // 실제로는 사용자별/명령어별 제한을 구현해야 함
            // 현재는 간단히 통과
        }

        // @IsReply 검증
        if (method.hasAnnotation<IsReply>()) {
            // 실제로는 메시지가 답장인지 확인해야 함
            // 현재는 간단히 통과
        }

        return true
    }

    /**
     * 명령어 실행 제한 확인
     */
    suspend fun checkThrottle(
        userId: Long,
        command: String,
        method: KFunction<*>
    ): Boolean {
        method.findAnnotation<Throttle>()?.let { throttle ->
            val key = "${userId}_$command"
            return throttleManager.isAllowed(key, throttle.maxCalls, throttle.timeWindowMs)
        }
        return true
    }
}

/**
 * 어노테이션 기반 이벤트 처리 시스템
 */
class AnnotationEventHandler(
    private val bot: iriskt.bot.Bot
) {
    private val commandHandlers = ConcurrentHashMap<String, CommandHandlerInfo>()

    init {
        // 실제로는 리플렉션을 통해 어노테이션된 메서드들을 찾아야 함
        // 현재는 수동으로 등록하는 방식으로 구현
    }

    /**
     * 명령어 핸들러 등록
     */
    fun registerCommandHandler(
        command: String,
        handler: suspend (ChatContext) -> Unit,
        method: KFunction<*>? = null
    ) {
        commandHandlers[command] = CommandHandlerInfo(handler, method)
    }

    /**
     * 메시지 처리
     */
    suspend fun handleMessage(context: ChatContext): Boolean {
        val command = context.message.command
        val handlerInfo = commandHandlers[command] ?: return false

        // 어노테이션 조건 검증
        if (handlerInfo.method != null) {
            if (!CommandProcessor.validateCommandConditions(context, handlerInfo.method)) {
                return true // 조건 불만족으로 처리 완료
            }

            // 쓰로틀링 확인
            if (!CommandProcessor.checkThrottle(
                    context.sender.id,
                    command,
                    handlerInfo.method
                )) {
                context.reply("⏰ 명령어 사용 제한을 초과했습니다. 잠시 후 다시 시도해주세요.")
                return true
            }
        }

        // 핸들러 실행
        try {
            handlerInfo.handler(context)
        } catch (e: Exception) {
            context.reply("❌ 명령어 실행 중 오류가 발생했습니다: ${e.message}")
        }

        return true
    }
}

/**
 * 명령어 핸들러 정보
 */
private data class CommandHandlerInfo(
    val handler: suspend (ChatContext) -> Unit,
    val method: KFunction<*>?
)
