package com.spear.iriskt.models

/**
 * 피드 타입 상수
 * 
 * 카카오톡 피드 이벤트 타입
 */
object FeedType {
    /** 퇴장 (자발적 퇴장) - "닉네임님이 나갔습니다." */
    const val LEAVE = 2
    
    /** 입장 (자발적 입장) - "닉네임님이 들어왔습니다." */
    const val JOIN = 4
    
    /** 내보내기 (강제 퇴장) - "닉네임님을 내보냈습니다." */
    const val FORCED_EXIT = 6
    
    /** 메시지 삭제 */
    const val DELETE_MESSAGE = 3
    
    /** 메시지 숨김 */
    const val HIDE_MESSAGE = 4
    
    /** 관리자 승급 */
    const val PROMOTE_MANAGER = 5
    
    /** 관리자 강등 */
    const val DEMOTE_MANAGER = 6
    
    /** 방장 위임 */
    const val HAND_OVER_HOST = 7
    
    /** 오픈채팅 사용자 입장 */
    const val OPEN_CHAT_JOIN = 8
    
    /** 오픈채팅 사용자 추방 */
    const val OPEN_CHAT_KICKED = 9
    
    // 하위 호환용 (Deprecated)
    @Deprecated("Use JOIN instead", ReplaceWith("JOIN"))
    const val INVITE_USER = 4
    
    @Deprecated("Use LEAVE instead", ReplaceWith("LEAVE"))
    const val LEAVE_USER = 2
    
    /**
     * 피드 타입 설명 조회
     */
    fun getDescription(type: Int): String = when (type) {
        LEAVE -> "퇴장 (자발적)"
        JOIN -> "입장 (자발적)"
        FORCED_EXIT -> "내보내기 (강제 퇴장)"
        DELETE_MESSAGE -> "메시지 삭제"
        HIDE_MESSAGE -> "메시지 숨김"
        PROMOTE_MANAGER -> "관리자 승급"
        DEMOTE_MANAGER -> "관리자 강등"
        HAND_OVER_HOST -> "방장 위임"
        OPEN_CHAT_JOIN -> "오픈채팅 입장"
        OPEN_CHAT_KICKED -> "오픈채팅 추방"
        else -> "알 수 없음 ($type)"
    }
    
    /**
     * 멤버 관련 피드인지 확인
     */
    fun isMemberEvent(type: Int): Boolean = type in listOf(LEAVE, JOIN, FORCED_EXIT)
    
    /**
     * 관리 관련 피드인지 확인
     */
    fun isManagementEvent(type: Int): Boolean = type in listOf(PROMOTE_MANAGER, DEMOTE_MANAGER, HAND_OVER_HOST)
}
