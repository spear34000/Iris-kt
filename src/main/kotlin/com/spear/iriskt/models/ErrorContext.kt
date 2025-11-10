package com.spear.iriskt.models

import kotlin.reflect.KFunction

/**
 * ?¤ë¥˜ ?´ë²¤?¸ì˜ ì»¨í…?¤íŠ¸ë¥??˜í??…ë‹ˆ??
 */
data class ErrorContext(
    val event: String,
    val func: KFunction<*>,
    val exception: Exception,
    val args: List<Any>
)
