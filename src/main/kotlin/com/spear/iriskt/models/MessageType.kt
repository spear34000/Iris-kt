package com.spear.iriskt.models

/**
 * 메시지 타입 상수
 * 
 * 카카오톡 메시지 타입별 case 번호
 */
object MessageType {
    /** 일반 메시지 (텍스트, att.path 없을 때) */
    const val TEXT = 1
    
    /** 링크 메시지 (att.path 있을 때) */
    const val LINK = 1
    
    /** 사진 */
    const val PHOTO = 2
    
    /** 동영상 */
    const val VIDEO = 3
    
    /** 연락처 */
    const val CONTACT = 4
    
    /** 음성 메시지 */
    const val AUDIO = 5
    
    /** 이모티콘 */
    const val EMOTICON = 6
    
    /** 이모티콘 (썸네일) */
    const val EMOTICON_THUMBNAIL = 12
    
    /** 투표 */
    const val VOTE = 14
    
    /** 카카오 프로필 */
    const val PROFILE = 17
    
    /** 파일 */
    const val FILE = 18
    
    /** 이모티콘 (썸네일) - 중복 */
    const val EMOTICON_THUMBNAIL_ALT = 20
    
    /** #검색 */
    const val SEARCH = 23
    
    /** 공지 */
    const val NOTICE = 24
    
    /** 답장 */
    const val REPLY = 26
    
    /** 묶음사진 */
    const val MULTI_PHOTO = 27
    
    /** 보이스톡 (초대/취소/종료) */
    const val VOICE_TALK = 51
    
    /** 투표 등록 */
    const val VOTE_REGISTER = 97
    
    /** 공지/투표 공유 */
    const val SHARE = 98
    
    /**
     * 메시지 타입 설명 조회
     */
    fun getDescription(type: Int): String = when (type) {
        TEXT -> "일반 메시지/링크"
        PHOTO -> "사진"
        VIDEO -> "동영상"
        CONTACT -> "연락처"
        AUDIO -> "음성 메시지"
        EMOTICON -> "이모티콘"
        EMOTICON_THUMBNAIL, EMOTICON_THUMBNAIL_ALT -> "이모티콘 (썸네일)"
        VOTE -> "투표"
        PROFILE -> "카카오 프로필"
        FILE -> "파일"
        SEARCH -> "#검색"
        NOTICE -> "공지"
        REPLY -> "답장"
        MULTI_PHOTO -> "묶음사진"
        VOICE_TALK -> "보이스톡"
        VOTE_REGISTER -> "투표 등록"
        SHARE -> "공지/투표 공유"
        else -> "알 수 없음 ($type)"
    }
}
