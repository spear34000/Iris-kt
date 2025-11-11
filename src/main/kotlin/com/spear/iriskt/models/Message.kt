package com.spear.iriskt.models

import com.spear.iriskt.api.IrisApiClient
import kotlinx.serialization.json.JsonElement

data class Message(
    val id: Long = -1,
    val type: Int = 0,
    val text: String = "",
    val attachment: String? = null,
    val metadata: JsonElement? = null,
    private val api: IrisApiClient? = null
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
            val imageUrls = if (attachment?.startsWith("http") == true) {
                listOf(attachment)
            } else {
                emptyList()
            }
            
            return if (imageUrls.isNotEmpty()) {
                ChatImage(url = imageUrls, api = api)
            } else {
                null
            }
        }
    
    /**
     * 답장 메시지 여부 (type = 26)
     */
    val isReply: Boolean
        get() = type == 26
    
    /**
     * 텍스트 메시지 여부 (type = 1, 첨부파일 없음)
     */
    val isText: Boolean
        get() = type == 1 && attachment == null
    
    /**
     * 링크 메시지 여부 (type = 1, 첨부파일 있음)
     */
    val isLink: Boolean
        get() = type == 1 && attachment != null
    
    /**
     * 사진 메시지 여부 (type = 2)
     */
    val isPhoto: Boolean
        get() = type == 2
    
    /**
     * 비디오 메시지 여부 (type = 3)
     */
    val isVideo: Boolean
        get() = type == 3
    
    /**
     * 연락처 메시지 여부 (type = 4)
     */
    val isContact: Boolean
        get() = type == 4
    
    /**
     * 오디오 메시지 여부 (type = 5)
     */
    val isAudio: Boolean
        get() = type == 5
    
    /**
     * 이모티콘 메시지 여부 (type = 6)
     */
    val isEmoticon: Boolean
        get() = type == 6
    
    /**
     * 이모티콘 썸네일 메시지 여부 (type = 12, 20)
     */
    val isEmoticonThumbnail: Boolean
        get() = type == 12 || type == 20
    
    /**
     * 투표 메시지 여부 (type = 14)
     */
    val isVote: Boolean
        get() = type == 14
    
    /**
     * 프로필 메시지 여부 (type = 17)
     */
    val isProfile: Boolean
        get() = type == 17
    
    /**
     * 파일 메시지 여부 (type = 18)
     */
    val isFile: Boolean
        get() = type == 18
    
    /**
     * 검색 메시지 여부 (type = 23)
     */
    val isSearch: Boolean
        get() = type == 23
    
    /**
     * 공지 메시지 여부 (type = 24)
     */
    val isNotice: Boolean
        get() = type == 24
    
    /**
     * 묶음사진 메시지 여부 (type = 27)
     */
    val isMultiPhoto: Boolean
        get() = type == 27
    
    /**
     * 보이스톡 메시지 여부 (type = 51)
     */
    val isVoiceTalk: Boolean
        get() = type == 51
    
    /**
     * 투표 등록 메시지 여부 (type = 97)
     */
    val isVoteRegister: Boolean
        get() = type == 97
    
    /**
     * 공유 메시지 여부 (type = 98)
     */
    val isShare: Boolean
        get() = type == 98
}
