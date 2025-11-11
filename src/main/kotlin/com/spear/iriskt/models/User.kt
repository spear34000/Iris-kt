package com.spear.iriskt.models

import com.spear.iriskt.api.IrisApiClient

/**
 * 사용자를 표현합니다
 */
data class User(
    val id: Long,
    val name: String,
    val type: String = "NORMAL",
    val avatar: Avatar? = null,
    private val api: IrisApiClient? = null,
    private val roomId: Long? = null
) {
    /**
     * 사용자 이름 조회
     */
    suspend fun getName(): String {
        if (api != null && roomId != null) {
            val userInfo = api.getUserInfo(id, roomId)
            return userInfo?.name ?: name
        }
        return name
    }
    
    /**
     * 사용자 권한 조회
     */
    suspend fun getType(): String {
        if (api != null && roomId != null) {
            val userInfo = api.getUserInfo(id, roomId)
            return userInfo?.type ?: type
        }
        return type
    }
    
    /**
     * 관리자 여부 확인
     */
    fun isAdmin(): Boolean {
        return type == "HOST" || type == "MANAGER"
    }
    
    /**
     * 방장 여부 확인
     */
    fun isHost(): Boolean {
        return type == "HOST"
    }
    
    /**
     * 일반 사용자 여부 확인
     */
    fun isNormal(): Boolean {
        return type == "NORMAL"
    }
    
    /**
     * 봇 여부 확인
     */
    fun isBot(): Boolean {
        return type == "BOT"
    }
}

/**
 * 사용자의 아바타를 표현합니다
 */
data class Avatar(
    val id: String = "",
    val url: String = "",
    var img: ByteArray? = null,
    private val api: IrisApiClient? = null
) {
    /**
     * 아바타 URL 조회
     */
    suspend fun getUrl(): String {
        return url
    }
    
    /**
     * 아바타 이미지 다운로드
     */
    suspend fun getImg(): ByteArray? {
        if (img != null) return img
        
        if (api != null && url.isNotEmpty()) {
            img = api.downloadAvatar(url)
        }
        
        return img
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Avatar

        if (id != other.id) return false
        if (url != other.url) return false
        if (img != null) {
            if (other.img == null) return false
            if (!img.contentEquals(other.img)) return false
        } else if (other.img != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + url.hashCode()
        result = 31 * result + (img?.contentHashCode() ?: 0)
        return result
    }
}
