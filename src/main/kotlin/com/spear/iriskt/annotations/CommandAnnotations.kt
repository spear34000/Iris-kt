package com.spear.iriskt.annotations

/**
 * ë©”ì‹œì§€ ì»¨íŠ¸ë¡¤ëŸ¬ ?´ë˜?¤ë? ?œì‹œ?˜ëŠ” ?´ë…¸?Œì´??
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class MessageController

/**
 * ?¹ì • ë°©ì—?œë§Œ ëª…ë ¹?´ë? ?ˆìš©?˜ëŠ” ?´ë…¸?Œì´??
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class AllowedRoom(vararg val roomNames: String)

/**
 * ë§¤ê°œë³€?˜ê? ?„ìš”??ëª…ë ¹???œì‹œ ?´ë…¸?Œì´??
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class HasParam

/**
 * ê´€ë¦¬ìë§??¬ìš©?????ˆëŠ” ëª…ë ¹???œì‹œ ?´ë…¸?Œì´??
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class IsAdmin

/**
 * ?¹ì • ??• ???„ìš”??ëª…ë ¹???œì‹œ ?´ë…¸?Œì´??
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class HasRole(vararg val roles: String)

/**
 * ëª…ë ¹???¤í–‰ ?œí•œ???¤ì •?˜ëŠ” ?´ë…¸?Œì´??
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Throttle(
    val maxCalls: Int = 5,
    val timeWindowMs: Long = 60000L // 1ë¶?
)

/**
 * ì°¨ë‹¨?˜ì? ?Šì? ?¬ìš©?ë§Œ ?¬ìš©?????ˆëŠ” ëª…ë ¹???œì‹œ ?´ë…¸?Œì´??
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class IsNotBanned

/**
 * ?µì¥ ë©”ì‹œì§€?ì„œë§??¬ìš©?????ˆëŠ” ëª…ë ¹???œì‹œ ?´ë…¸?Œì´??
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class IsReply
