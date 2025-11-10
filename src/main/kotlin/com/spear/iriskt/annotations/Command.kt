package com.spear.iriskt.annotations

/**
 * ë´?ëª…ë ¹?´ë? ?±ë¡?˜ëŠ” ?´ë…¸?Œì´?? */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class BotCommand(
    val command: String,
    val description: String = ""
)

/**
 * ì»¨íŠ¸ë¡¤ëŸ¬???´ë²¤?¸ê? ?˜ì‹ ??ê²½ìš° ?ë™?¼ë¡œ ?¤í–‰?˜ëŠ” ëª…ë ¹?´ë¡œ ?±ë¡?˜ëŠ” ?´ë…¸?Œì´?? */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Command

/**
 * ?„ì?ë§?ëª…ë ¹?´ë? ?±ë¡?˜ëŠ” ?´ë…¸?Œì´?? */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class HelpCommand(
    val command: String = "?„ì?ë§?
)

/**
 * ì»¨íŠ¸ë¡¤ëŸ¬??ê¸°ë³¸ prefixë¥??¤ì •?˜ëŠ” ?´ë…¸?Œì´?? */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Prefix(
    val prefix: String
)

/**
 * ?¹ì • ë©”ì†Œ?œì—ë§?prefixë¥??¤ì •?˜ëŠ” ?´ë…¸?Œì´?? */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class MethodPrefix(
    val prefix: String
)
