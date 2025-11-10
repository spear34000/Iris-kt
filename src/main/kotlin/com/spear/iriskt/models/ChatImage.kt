package com.spear.iriskt.models

/**
 * 채팅 메시지의 이미지를 표현합니다
 */
data class ChatImage(
    val url: List<String> = emptyList(),
    var img: List<ByteArray> = emptyList(),
    private val api: iriskt.bot.api.IrisApiClient? = null
) {
    /**
     * 이미지 URL 목록 조회
     */
    fun getUrls(): List<String> = url
    
    /**
     * 첫 번째 이미지 URL 조회
     */
    fun getFirstUrl(): String? = url.firstOrNull()
    
    /**
     * 이미지 개수
     */
    fun count(): Int = url.size
    
    /**
     * 이미지가 있는지 확인
     */
    fun hasImages(): Boolean = url.isNotEmpty()
    
    /**
     * 모든 이미지 다운로드
     */
    suspend fun getImg(): List<ByteArray> {
        if (img.isNotEmpty()) return img
        
        if (api != null && url.isNotEmpty()) {
            img = url.mapNotNull { imageUrl ->
                api.downloadChatImage(imageUrl)
            }
        }
        
        return img
    }
    
    /**
     * 특정 인덱스의 이미지 다운로드
     */
    suspend fun getImgAt(index: Int): ByteArray? {
        if (index < 0 || index >= url.size) return null
        
        // 이미 다운로드된 경우
        if (img.size > index) return img[index]
        
        // 다운로드
        if (api != null) {
            return api.downloadChatImage(url[index])
        }
        
        return null
    }
    
    /**
     * 첫 번째 이미지 다운로드
     */
    suspend fun getFirstImg(): ByteArray? {
        return getImgAt(0)
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChatImage

        if (url != other.url) return false
        if (img.size != other.img.size) return false
        for (i in img.indices) {
            if (!img[i].contentEquals(other.img[i])) return false
        }

        return true
    }

    override fun hashCode(): Int {
        var result = url.hashCode()
        result = 31 * result + img.fold(0) { acc, bytes -> 31 * acc + bytes.contentHashCode() }
        return result
    }
}
