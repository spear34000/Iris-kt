package com.spear.iriskt.annotations

import iriskt.bot.models.ChatContext
import iriskt.bot.models.User
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

/**
 * ?�노?�이??기반 명령??처리 ?�스?�의 기본 ?�래??
 */
abstract class BaseController {
    protected val logger = iriskt.bot.core.LoggerManager.getLogger(this::class.simpleName ?: "Controller")

    /**
     * 명령??처리 ??공통 로직
     */
    protected open suspend fun beforeCommand(context: ChatContext): Boolean {
        return true
    }

    /**
     * 명령??처리 ??공통 로직
     */
    protected open suspend fun afterCommand(context: ChatContext) {
        // 기본 구현?� 비어?�음
    }

    /**
     * 명령???�행 ?�한??관리하???�래??
     */
    protected val throttleManager = ThrottleManager()

    /**
     * 명령???�행 ?�한 ?�인
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
 * 메시지 컨트롤러 ?�래??
 * @MessageController ?�노?�이?�과 ?�께 ?�용?�니??
 */
@MessageController
abstract class BaseMessageController : BaseController() {

    /**
     * 명령???�두??(기본�? ?�음)
     */
    protected open val prefix: String = ""

    /**
     * 명령???�들???�록
     */
    fun registerHandlers(bot: iriskt.bot.Bot) {
        logger.warn("${bot::class.simpleName ?: "Bot"} ?�들???�동 ?�록??구현?��? ?�았?�니??)
    }

    /**
     * 명령??처리 (기본 구현)
     */
    protected open suspend fun handleCommand(context: ChatContext, command: String, param: String): Boolean {
        return false
    }

    /**
     * ?????�는 명령??처리
     */
    protected open suspend fun handleUnknownCommand(context: ChatContext, command: String) {
        context.reply("??'$command' 명령?��? 찾을 ???�습?�다.")
    }
}
