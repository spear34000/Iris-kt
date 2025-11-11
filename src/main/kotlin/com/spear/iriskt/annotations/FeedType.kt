package com.spear.iriskt.annotations

/**
 * 피드 메시지에만 반응하는 어노테이션
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnFeedMessage

/**
 * 사용자 초대 피드에 반응하는 어노테이션 (하위 호환용)
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Deprecated("Use OnJoinFeed instead", ReplaceWith("OnJoinFeed"))
annotation class OnInviteUserFeed

/**
 * 사용자 퇴장 피드에 반응하는 어노테이션 (하위 호환용)
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Deprecated("Use OnLeaveFeed instead", ReplaceWith("OnLeaveFeed"))
annotation class OnLeaveUserFeed

/**
 * 멤버 입장 피드 (type=4)
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnJoinFeed

/**
 * 멤버 퇴장 피드 (type=2)
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnLeaveFeed

/**
 * 멤버 강제 퇴장 피드 (type=6)
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnForcedExitFeed

/**
 * 메시지 삭제 피드에 반응하는 어노테이션
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnDeleteMessageFeed

/**
 * 메시지 숨김 피드에 반응하는 어노테이션
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnHideMessageFeed

/**
 * 관리자 승급 피드에 반응하는 어노테이션
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnPromoteManagerFeed

/**
 * 관리자 강등 피드에 반응하는 어노테이션
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnDemoteManagerFeed

/**
 * 방장 위임 피드에 반응하는 어노테이션
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnHandOverHostFeed

/**
 * 오픈채팅 사용자 입장 피드에 반응하는 어노테이션 (하위 호환용)
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Deprecated("Use OnOpenChatJoinFeed instead", ReplaceWith("OnOpenChatJoinFeed"))
annotation class OnOpenChatJoinUserFeed

/**
 * 오픈채팅 사용자 추방 피드에 반응하는 어노테이션 (하위 호환용)
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Deprecated("Use OnOpenChatKickedFeed instead", ReplaceWith("OnOpenChatKickedFeed"))
annotation class OnOpenChatKickedUserFeed

/**
 * 오픈채팅 입장 피드
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnOpenChatJoinFeed

/**
 * 오픈채팅 추방 피드
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnOpenChatKickedFeed
