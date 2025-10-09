package iriskt.bot.annotations

import iriskt.bot.models.ChatContext
import iriskt.bot.models.User
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

/**
 * 어노테이션 기반 명령어 처리 시스템의 기본 클래스
 */
abstract class BaseController {
    protected val logger = iriskt.bot.core.LoggerManager.getLogger(this::class.simpleName ?: "Controller")

    /**
     * 명령어 처리 전 공통 로직
     */
    protected open suspend fun beforeCommand(context: ChatContext): Boolean {
        return true
    }

    /**
     * 명령어 처리 후 공통 로직
     */
    protected open suspend fun afterCommand(context: ChatContext) {
        // 기본 구현은 비어있음
    }

    /**
     * 명령어 실행 제한을 관리하는 클래스
     */
    protected val throttleManager = ThrottleManager()

    /**
     * 명령어 실행 제한 확인
     */
    protected suspend fun checkThrottle(
        userId: Long,
        command: String,
        method: KFunction<*>
    ): Boolean {
        val throttle = method.findAnnotation<Throttle>()
        if (throttle != null) {
            val key = "${userId}_$command"
            return throttleManager.isAllowed(key, throttle.maxCalls, throttle.timeWindowMs)
        }
        return true
    }
}

/**
 * 메시지 컨트롤러 클래스
 * @MessageController 어노테이션과 함께 사용됩니다.
 */
@MessageController
abstract class BaseMessageController : BaseController() {

    /**
     * 명령어 접두사 (기본값: 없음)
     */
    protected open val prefix: String = ""

    /**
     * 명령어 핸들러 등록
     */
    fun registerHandlers(bot: iriskt.bot.Bot) {
        logger.warn("${bot::class.simpleName ?: "Bot"} 핸들러 자동 등록이 구현되지 않았습니다")
    }

    /**
     * 명령어 처리 (기본 구현)
     */
    protected open suspend fun handleCommand(context: ChatContext, command: String, param: String): Boolean {
        return false
    }

    /**
     * 알 수 없는 명령어 처리
     */
    protected open suspend fun handleUnknownCommand(context: ChatContext, command: String) {
        context.reply("❓ '$command' 명령어를 찾을 수 없습니다.")
    }
}
