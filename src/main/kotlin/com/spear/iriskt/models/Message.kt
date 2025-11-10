package com.spear.iriskt.models

data class Message(
    val id: Long = -1,
    val type: Int = 0,
    val text: String = "",
    val attachment: String? = null,
    val metadata: Map<String, Any>? = null,
    private val api: iriskt.bot.api.IrisApiClient? = null
) {
    /**
     * 메시지의 명령어 부분 (첫 번째 단어)
     */
    val command: String
        get() = text.trim().split("\\s+".toRegex()).firstOrNull() ?: ""

    /**
     * 메시지의 매개변수 부분 (나머지 메시지)
     */
    val param: String
        get() {
            val parts = text.trim().split("\\s+".toRegex(), 2)
            return if (parts.size > 1) parts[1].trim() else ""
        }

    /**
     * 메시지에 매개변수가 있는지 확인
     */
    val hasParam: Boolean
        get() = param.isNotBlank()
    
    /**
     * 이미지 정보 (있는 경우)
     */
    val image: ChatImage?
        get() {
            // 메타데이터에서 이미지 URL 추출
            val imageUrls = when {
                metadata?.containsKey("image_urls") == true -> {
                    @Suppress("UNCHECKED_CAST")
                    (metadata["image_urls"] as? List<String>) ?: emptyList()
                }
                metadata?.containsKey("image_url") == true -> {
                    listOf(metadata["image_url"] as? String ?: "")
                }
                attachment?.startsWith("http") == true -> {
                    listOf(attachment)
                }
                else -> emptyList()
            }
            
            return if (imageUrls.isNotEmpty()) {
                ChatImage(imageUrls, api)
            } else {
                null
            }
        }
    
    /**
     * 답장 메시지 여부
     */
    val isReply: Boolean
        get() = metadata?.containsKey("reply_id") == true
    
    /**
     * 사진 메시지 여부
     */
    val isPhoto: Boolean
        get() = type == 2 || type == 27
    
    /**
     * 비디오 메시지 여부
     */
    val isVideo: Boolean
        get() = type == 3
    
    /**
     * 오디오 메시지 여부
     */
    val isAudio: Boolean
        get() = type == 4
    
    /**
     * 파일 메시지 여부
     */
    val isFile: Boolean
        get() = type == 5
    
    /**
     * 이모티콘 메시지 여부
     */
    val isEmoticon: Boolean
        get() = type == 18
}
