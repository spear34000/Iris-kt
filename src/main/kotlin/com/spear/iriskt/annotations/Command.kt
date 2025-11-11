package com.spear.iriskt.annotations

/**
 * 봇 명령어를 등록하는 어노테이션
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class BotCommand(
    val command: String,
    val description: String = ""
)

/**
 * 컨트롤러가 이벤트를 수신할 경우 자동으로 실행되는 명령어로 등록하는 어노테이션
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Command

/**
 * 도움말 명령어를 등록하는 어노테이션
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class HelpCommand(
    val command: String = "help"
)

/**
 * 컨트롤러의 기본 prefix를 설정하는 어노테이션
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Prefix(
    val prefix: String
)

/**
 * 특정 메소드에만 prefix를 설정하는 어노테이션
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class MethodPrefix(
    val prefix: String
)
