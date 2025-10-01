package iriskt.bot.models

import iriskt.bot.api.IrisApiClient
import java.io.InputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

data class Message(
    val id: Long,
    val type: Int,
    val text: String,
    val attachment: String?,
    val metadata: JsonElement?
) {
    val command: String get() = text.split(" ").firstOrNull() ?: ""
    val param: String get() = text.substringAfter(" ", "").trim()
    val hasParam: Boolean get() = param.isNotEmpty()

    val image: ChatImage? get() {
        val attachmentData = attachment ?: return null
        return try {
            val json = kotlinx.serialization.json.Json.parseToJsonElement(attachmentData).jsonObject
            ChatImage(
                urls = json["urls"]?.jsonArray?.map { it.jsonPrimitive.content } ?: emptyList()
            )
        } catch (e: Exception) {
            null
        }
    }
}

data class Avatar(
    val id: Long,
    private val _url: String? = null,
    private val _imageData: ByteArray? = null
) {
    val url: String? get() = _url
    val imageData: ByteArray? get() = _imageData

    suspend fun getUrl(): String? = _url

    suspend fun getImageData(): ByteArray? = _imageData
}

data class ChatImage(
    val urls: List<String>
) {
    suspend fun getImageData(): List<ByteArray> {
        // 실제로는 URL에서 이미지 데이터를 가져와야 함
        return emptyList()
    }
}

data class User(
    val id: Long,
    val name: String,
    val avatar: Avatar? = null
) {
    suspend fun getName(): String = name

    suspend fun getType(): String {
        // 실제로는 서버에서 사용자 권한을 조회해야 함
        return "NORMAL"
    }
}

data class Room(
    val id: Long,
    val name: String
) {
    suspend fun getType(): String {
        // 실제로는 서버에서 방 타입을 조회해야 함
        return "NORMAL"
    }
}

class ChatContext internal constructor(
    val room: Room,
    val sender: User,
    val message: Message,
    val raw: JsonObject,
    private val api: IrisApiClient
) {
    suspend fun reply(message: String, roomId: Long? = null) {
        val target = roomId ?: room.id
        api.reply(target, message)
    }

    suspend fun replyImage(files: Collection<Any>, roomId: Long? = null) {
        val target = roomId ?: room.id
        val buffers = withContext(Dispatchers.IO) {
            files.map { element ->
                when (element) {
                    is ByteArray -> element
                    is InputStream -> element.readBytes()
                    else -> error("지원하지 않는 타입입니다 ${element::class.java}")
                }
            }
        }
        api.replyImage(target, buffers)
    }

    suspend fun getSource(): ChatContext? {
        // 실제로는 이전 메시지의 ChatContext를 반환해야 함
        return null
    }

    suspend fun getNextChat(n: Int = 1): ChatContext? {
        // 실제로는 다음 메시지의 ChatContext를 반환해야 함
        return null
    }

    suspend fun getPreviousChat(n: Int = 1): ChatContext? {
        // 실제로는 이전 메시지의 ChatContext를 반환해야 함
        return null
    }
}

data class ErrorContext(
    val event: String,
    val handler: suspend (Any) -> Unit,
    val exception: Throwable,
    val payload: Any
)
