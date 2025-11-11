package com.spear.iriskt.models

/**
 * 에러 이벤트의 컨텍스트를 표현합니다
 */
data class ErrorContext(
    val event: String,
    val exception: Exception,
    val payload: Any? = null
)
