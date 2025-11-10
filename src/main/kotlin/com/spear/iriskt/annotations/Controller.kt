package com.spear.iriskt.annotations

/**
 * ì»¨íŠ¸ë¡¤ëŸ¬ ?´ë˜?¤ë? ?œì‹œ?˜ëŠ” ?´ë…¸?Œì´??
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Controller

/**
 * ë©”ì‹œì§€ ì»¨íŠ¸ë¡¤ëŸ¬ ?´ë˜?¤ë? ?œì‹œ?˜ëŠ” ?´ë…¸?Œì´??
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class MessageController

/**
 * ??ë©¤ë²„ ?´ë²¤??ì»¨íŠ¸ë¡¤ëŸ¬ ?´ë˜?¤ë? ?œì‹œ?˜ëŠ” ?´ë…¸?Œì´??
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class NewMemberController

/**
 * ë©¤ë²„ ?´ì¥ ?´ë²¤??ì»¨íŠ¸ë¡¤ëŸ¬ ?´ë˜?¤ë? ?œì‹œ?˜ëŠ” ?´ë…¸?Œì´??
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DeleteMemberController

/**
 * ?¼ë“œ ?´ë²¤??ì»¨íŠ¸ë¡¤ëŸ¬ ?´ë˜?¤ë? ?œì‹œ?˜ëŠ” ?´ë…¸?Œì´??
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class FeedController

/**
 * ?????†ëŠ” ?´ë²¤??ì»¨íŠ¸ë¡¤ëŸ¬ ?´ë˜?¤ë? ?œì‹œ?˜ëŠ” ?´ë…¸?Œì´??
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class UnknownController

/**
 * ?ëŸ¬ ?´ë²¤??ì»¨íŠ¸ë¡¤ëŸ¬ ?´ë˜?¤ë? ?œì‹œ?˜ëŠ” ?´ë…¸?Œì´??
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ErrorController

/**
 * ë°°ì¹˜ ì²˜ë¦¬ ì»¨íŠ¸ë¡¤ëŸ¬ ?´ë˜?¤ë? ?œì‹œ?˜ëŠ” ?´ë…¸?Œì´??
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class BatchController

/**
 * ë¶€?¸ìŠ¤?¸ë© ì»¨íŠ¸ë¡¤ëŸ¬ ?´ë˜?¤ë? ?œì‹œ?˜ëŠ” ?´ë…¸?Œì´??
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class BootstrapController

/**
 * ì±„íŒ… ?´ë²¤??ì»¨íŠ¸ë¡¤ëŸ¬ ?´ë˜?¤ë? ?œì‹œ?˜ëŠ” ?´ë…¸?Œì´??
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ChatController
