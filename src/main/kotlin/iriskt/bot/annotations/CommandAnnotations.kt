package iriskt.bot.annotations

/**
 * 메시지 컨트롤러 클래스를 표시하는 어노테이션
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class MessageController

/**
 * 특정 방에서만 명령어를 허용하는 어노테이션
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class AllowedRoom(vararg val roomNames: String)

/**
 * 매개변수가 필요한 명령어 표시 어노테이션
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class HasParam

/**
 * 관리자만 사용할 수 있는 명령어 표시 어노테이션
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class IsAdmin

/**
 * 특정 역할이 필요한 명령어 표시 어노테이션
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class HasRole(vararg val roles: String)

/**
 * 명령어 실행 제한을 설정하는 어노테이션
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Throttle(
    val maxCalls: Int = 5,
    val timeWindowMs: Long = 60000L // 1분
)

/**
 * 차단되지 않은 사용자만 사용할 수 있는 명령어 표시 어노테이션
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class IsNotBanned

/**
 * 답장 메시지에서만 사용할 수 있는 명령어 표시 어노테이션
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class IsReply
