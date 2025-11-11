package com.spear.iriskt.models

import com.spear.iriskt.api.IrisApiClient
import kotlinx.serialization.json.JsonObject

/**
 * 채팅 이벤트의 컨텍스트를 나타냅니다
 */
data class ChatContext(
    val room: Room,
    val sender: User,
    val message: Message,
    val raw: JsonObject? = null,
    val api: IrisApiClient? = null
) {
    /**
     * 채팅방에 ?�장??보냅?�다.
     */
    suspend fun reply(message: String, roomId: Long? = null) {
        api?.sendMessage(message, roomId ?: room.id)
    }

    /**
     * 채팅방에 미디???�일??보냅?�다.
     */
    suspend fun replyMedia(files: List<String>, roomId: Long? = null) {
        api?.sendMedia(files, roomId ?: room.id)
    }

    /**
     * 답장하는 메시지의 ChatContext를 반환합니다
     */
    suspend fun getSource(): ChatContext? {
        // TODO: metadata에서 reply_id 추출 구현 필요
        return null
    }

    /**
     * 채팅 기록?�서 ?�음 메시지??ChatContext�?반환?�니??
     */
    suspend fun getNextChat(n: Int = 1): ChatContext? {
        return api?.getNextMessage(message.id, room.id, n)
    }

    /**
     * 채팅 기록?�서 ?�전 메시지??ChatContext�?반환?�니??
     */
    suspend fun getPreviousChat(n: Int = 1): ChatContext? {
        return api?.getPreviousMessage(message.id, room.id, n)
    }
}
