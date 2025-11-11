package com.spear.iriskt.annotations

/**
 * 주기적으로 실행을 위한 어노테이션
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Schedule(
    val interval: Long = 0 // 밀리초 단위
)

/**
 * 스케줄된 메시지 처리를 위한 어노테이션
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ScheduleMessage(
    val key: String
)

/**
 * 봇 시작 시 부트스트랩 실행을 위한 어노테이션
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Bootstrap(
    val priority: Int = 0 // 낮은 숫자 우선
)
