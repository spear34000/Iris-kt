package com.spear.iriskt.models

/**
 * 채팅방을 표현합니다
 */
data class Room(
    val id: Long,
    val name: String,
    val type: String = "",
    private val api: iriskt.bot.api.IrisApiClient? = null
) {
    /**
     * 방 타입 조회
     */
    suspend fun getType(): String {
        if (api != null) {
            val roomInfo = api.getRoomInfo(id)
            return roomInfo?.type ?: type
        }
        return type
    }
    
    /**
     * 오픈채팅 여부 확인
     */
    fun isOpenChat(): Boolean {
        return type.contains("Open", ignoreCase = true)
    }
    
    /**
     * 단체채팅 여부 확인
     */
    fun isMultiChat(): Boolean {
        return type.contains("Multi", ignoreCase = true)
    }
    
    /**
     * 1:1 채팅 여부 확인
     */
    fun isDirectChat(): Boolean {
        return type.contains("Direct", ignoreCase = true)
    }
}
