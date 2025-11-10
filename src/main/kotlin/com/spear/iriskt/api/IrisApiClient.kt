package com.spear.iriskt.api

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
    // Base64 인코더 재사용
    private val base64Encoder = Base64.getEncoder()
    
    // 자주 사용되는 JsonPrimitive 캐싱
    private val textType = JsonPrimitive("text")
    private val imageMultipleType = JsonPrimitive("image_multiple")
    
    suspend fun reply(roomId: Long, message: String): JsonObject {
        val payload = JsonObject(
            mapOf(
                "type" to textType,
                "room" to JsonPrimitive(roomId.toString()),
                "data" to JsonPrimitive(message)
            )
        )
        return post("reply", payload)
    }

    suspend fun replyImage(roomId: Long, files: Collection<ByteArray>): JsonObject {
        // 병렬 인코딩 (큰 파일이 많을 경우 성능 향상)
        val encoded = files.map { JsonPrimitive(base64Encoder.encodeToString(it)) }
        val payload = JsonObject(
            mapOf(
                "type" to imageMultipleType,
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
    
    /**
     * 메시지 전송 (ChatContext.reply용)
     */
    suspend fun sendMessage(message: String, roomId: Long): JsonObject {
        return reply(roomId, message)
    }
    
    /**
     * 미디어 파일 전송 (ChatContext.replyMedia용)
     */
    suspend fun sendMedia(files: List<String>, roomId: Long): JsonObject {
        // Base64 인코딩된 파일들을 ByteArray로 변환
        val byteArrays = files.map { Base64.getDecoder().decode(it) }
        return replyImage(roomId, byteArrays)
    }
    
    /**
     * 특정 메시지 조회
     */
    suspend fun getMessage(messageId: Long, roomId: Long): com.spear.iriskt.models.ChatContext? {
        val payload = JsonObject(
            mapOf(
                "message_id" to JsonPrimitive(messageId),
                "room_id" to JsonPrimitive(roomId)
            )
        )
        val result = post("message/get", payload)
        return parseMessageToContext(result)
    }
    
    /**
     * 다음 메시지 조회
     */
    suspend fun getNextMessage(currentMessageId: Long, roomId: Long, offset: Int = 1): com.spear.iriskt.models.ChatContext? {
        val payload = JsonObject(
            mapOf(
                "message_id" to JsonPrimitive(currentMessageId),
                "room_id" to JsonPrimitive(roomId),
                "offset" to JsonPrimitive(offset),
                "direction" to JsonPrimitive("next")
            )
        )
        val result = post("message/navigate", payload)
        return parseMessageToContext(result)
    }
    
    /**
     * 이전 메시지 조회
     */
    suspend fun getPreviousMessage(currentMessageId: Long, roomId: Long, offset: Int = 1): com.spear.iriskt.models.ChatContext? {
        val payload = JsonObject(
            mapOf(
                "message_id" to JsonPrimitive(currentMessageId),
                "room_id" to JsonPrimitive(roomId),
                "offset" to JsonPrimitive(offset),
                "direction" to JsonPrimitive("previous")
            )
        )
        val result = post("message/navigate", payload)
        return parseMessageToContext(result)
    }
    
    /**
     * 사용자 정보 조회
     */
    suspend fun getUserInfo(userId: Long, roomId: Long): com.spear.iriskt.models.User? {
        val payload = JsonObject(
            mapOf(
                "user_id" to JsonPrimitive(userId),
                "room_id" to JsonPrimitive(roomId)
            )
        )
        val result = post("user/info", payload)
        return parseUser(result)
    }
    
    /**
     * 방 정보 조회
     */
    suspend fun getRoomInfo(roomId: Long): com.spear.iriskt.models.Room? {
        val payload = JsonObject(
            mapOf(
                "room_id" to JsonPrimitive(roomId)
            )
        )
        val result = post("room/info", payload)
        return parseRoom(result)
    }
    
    /**
     * 아바타 이미지 다운로드
     */
    suspend fun downloadAvatar(avatarUrl: String): ByteArray? {
        return try {
            val response = client.get(avatarUrl)
            if (response.status.isSuccess()) {
                response.body<ByteArray>()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * 채팅 이미지 다운로드
     */
    suspend fun downloadChatImage(imageUrl: String): ByteArray? {
        return downloadAvatar(imageUrl) // 동일한 로직
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
        
        // 상태 코드 먼저 확인 (파싱 전에)
        if (!response.status.isSuccess()) {
            val element = runCatching { json.parseToJsonElement(text) }.getOrNull()
            val message = element?.jsonObject?.get("message")
                ?.let { (it as? JsonPrimitive)?.contentOrNull } 
                ?: "알 수 없는 오류"
            throw IrisHttpException(response.status.value, "Iris 오류: $message")
        }
        
        val element = runCatching { json.parseToJsonElement(text) }.getOrElse {
            throw IrisHttpException(response.status.value, "Iris 응답 JSON 파싱 오류: $text")
        }
        
        return element.jsonObject
    }
    
    /**
     * JsonObject를 ChatContext로 변환
     */
    private fun parseMessageToContext(json: JsonObject): com.spear.iriskt.models.ChatContext? {
        return try {
            val messageData = json["message"]?.jsonObject ?: return null
            val roomData = json["room"]?.jsonObject ?: return null
            val userData = json["user"]?.jsonObject ?: return null
            
            val message = com.spear.iriskt.models.Message(
                id = messageData["id"]?.jsonPrimitive?.longOrNull ?: -1L,
                type = messageData["type"]?.jsonPrimitive?.intOrNull ?: 0,
                text = messageData["text"]?.jsonPrimitive?.contentOrNull ?: "",
                attachment = messageData["attachment"]?.jsonPrimitive?.contentOrNull,
                metadata = parseMetadata(messageData["metadata"])
            )
            
            val room = com.spear.iriskt.models.Room(
                id = roomData["id"]?.jsonPrimitive?.longOrNull ?: -1L,
                name = roomData["name"]?.jsonPrimitive?.contentOrNull ?: "",
                type = roomData["type"]?.jsonPrimitive?.contentOrNull ?: ""
            )
            
            val user = com.spear.iriskt.models.User(
                id = userData["id"]?.jsonPrimitive?.longOrNull ?: -1L,
                name = userData["name"]?.jsonPrimitive?.contentOrNull ?: "",
                type = userData["type"]?.jsonPrimitive?.contentOrNull ?: "NORMAL"
            )
            
            com.spear.iriskt.models.ChatContext(
                room = room,
                sender = user,
                message = message,
                raw = json,
                api = this
            )
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * JsonObject를 User로 변환
     */
    private fun parseUser(json: JsonObject): com.spear.iriskt.models.User? {
        return try {
            val userData = json["user"]?.jsonObject ?: return null
            com.spear.iriskt.models.User(
                id = userData["id"]?.jsonPrimitive?.longOrNull ?: -1L,
                name = userData["name"]?.jsonPrimitive?.contentOrNull ?: "",
                type = userData["type"]?.jsonPrimitive?.contentOrNull ?: "NORMAL"
            )
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * JsonObject를 Room으로 변환
     */
    private fun parseRoom(json: JsonObject): com.spear.iriskt.models.Room? {
        return try {
            val roomData = json["room"]?.jsonObject ?: return null
            com.spear.iriskt.models.Room(
                id = roomData["id"]?.jsonPrimitive?.longOrNull ?: -1L,
                name = roomData["name"]?.jsonPrimitive?.contentOrNull ?: "",
                type = roomData["type"]?.jsonPrimitive?.contentOrNull ?: ""
            )
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * 메타데이터 파싱
     */
    private fun parseMetadata(element: JsonElement?): Map<String, Any>? {
        if (element == null) return null
        return try {
            when (element) {
                is JsonObject -> element.entries.associate { (key, value) ->
                    key to when (value) {
                        is JsonPrimitive -> value.contentOrNull ?: value.toString()
                        else -> value.toString()
                    }
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }
}
