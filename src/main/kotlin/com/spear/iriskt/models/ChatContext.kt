package com.spear.iriskt.models

import iriskt.bot.api.IrisApiClient
import kotlinx.coroutines.future.await
import kotlinx.serialization.json.JsonObject

/**
 * 채팅 ?�벤?�의 컨텍?�트�??��??�니??
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
     * ?�장?�는 메시지??ChatContext�?반환?�니??
     */
    suspend fun getSource(): ChatContext? {
        val replyId = message.metadata?.get("reply_id") as? Long ?: return null
        return api?.getMessage(replyId, room.id)
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
