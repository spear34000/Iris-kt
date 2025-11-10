package com.spear.iriskt.annotations

/**
 * 컨트롤러 ?�래?��? ?�시?�는 ?�노?�이??
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Controller

/**
 * 메시지 컨트롤러 ?�래?��? ?�시?�는 ?�노?�이??
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class MessageController

/**
 * 멤버 입장 이벤트 컨트롤러 (자발적 입장)
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class JoinMemberController

/**
 * 멤버 퇴장 이벤트 컨트롤러 (자발적 퇴장)
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class LeaveMemberController

/**
 * 멤버 강제 퇴장 이벤트 컨트롤러 (강퇴)
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ForcedExitMemberController

/**
 * 새 멤버 이벤트 컨트롤러 (하위 호환용 - JoinMemberController 사용 권장)
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Deprecated("Use JoinMemberController instead", ReplaceWith("JoinMemberController"))
annotation class NewMemberController

/**
 * 멤버 삭제 이벤트 컨트롤러 (하위 호환용 - LeaveMemberController 또는 ForcedExitMemberController 사용 권장)
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Deprecated("Use LeaveMemberController or ForcedExitMemberController instead")
annotation class DeleteMemberController

/**
 * ?�드 ?�벤??컨트롤러 ?�래?��? ?�시?�는 ?�노?�이??
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class FeedController

/**
 * ?????�는 ?�벤??컨트롤러 ?�래?��? ?�시?�는 ?�노?�이??
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class UnknownController

/**
 * ?�러 ?�벤??컨트롤러 ?�래?��? ?�시?�는 ?�노?�이??
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ErrorController

/**
 * 배치 처리 컨트롤러 ?�래?��? ?�시?�는 ?�노?�이??
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class BatchController

/**
 * 부?�스?�랩 컨트롤러 ?�래?��? ?�시?�는 ?�노?�이??
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class BootstrapController

/**
 * 채팅 ?�벤??컨트롤러 ?�래?��? ?�시?�는 ?�노?�이??
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ChatController
