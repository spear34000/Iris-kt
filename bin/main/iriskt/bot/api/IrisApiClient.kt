package iriskt.bot.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import java.util.Base64

class IrisHttpException(val status: Int, message: String) : RuntimeException(message)

class IrisApiClient(
    private val baseUrl: String,
    private val client: HttpClient,
    private val json: Json
) {
    suspend fun reply(roomId: Long, message: String): JsonObject {
        val payload = JsonObject(
            mapOf(
                "type" to JsonPrimitive("text"),
                "room" to JsonPrimitive(roomId.toString()),
                "data" to JsonPrimitive(message)
            )
        )
        return post("reply", payload)
    }

    suspend fun replyImage(roomId: Long, files: Collection<ByteArray>): JsonObject {
        val encoded = files.map { JsonPrimitive(Base64.getEncoder().encodeToString(it)) }
        val payload = JsonObject(
            mapOf(
                "type" to JsonPrimitive("image_multiple"),
                "room" to JsonPrimitive(roomId.toString()),
                "data" to JsonArray(encoded)
            )
        )
        return post("reply", payload)
    }

    suspend fun decrypt(enc: Int, ciphertext: String, userId: Long): String? {
        val payload = JsonObject(
            mapOf(
                "enc" to JsonPrimitive(enc),
                "b64_ciphertext" to JsonPrimitive(ciphertext),
                "user_id" to JsonPrimitive(userId)
            )
        )
        val res = post("decrypt", payload)
        val plain = res["plain_text"] ?: return null
        return (plain as? JsonPrimitive)?.contentOrNull
    }

    suspend fun query(statement: String, bind: List<JsonElement>? = null): List<JsonObject> {
        val payload = JsonObject(
            mapOf(
                "query" to JsonPrimitive(statement),
                "bind" to (bind?.let { JsonArray(it) } ?: JsonArray(emptyList()))
            )
        )
        val res = post("query", payload)
        val data = res["data"] as? JsonArray ?: return emptyList()
        return data.mapNotNull { it as? JsonObject }
    }

    suspend fun getInfo(): JsonObject {
        val response = client.get("$baseUrl/config")
        return parse(response)
    }

    private suspend fun post(path: String, payload: JsonObject): JsonObject {
        val response = client.post("$baseUrl/$path") {
            contentType(ContentType.Application.Json)
            setBody(payload)
        }
        return parse(response)
    }

    private suspend fun parse(response: HttpResponse): JsonObject {
        val text = response.body<String>()
        val element = runCatching { json.parseToJsonElement(text) }.getOrElse {
            throw IrisHttpException(response.status.value, "Iris 응답 JSON 파싱 오류: $text")
        }
        if (!response.status.isSuccess()) {
            val message = element.jsonObject["message"]?.let { (it as? JsonPrimitive)?.contentOrNull } ?: "알 수 없는 오류"
            throw IrisHttpException(response.status.value, "Iris 오류: $message")
        }
        return element.jsonObject
    }
}
