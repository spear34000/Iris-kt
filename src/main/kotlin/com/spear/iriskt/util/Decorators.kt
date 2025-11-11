package com.spear.iriskt.util

import com.spear.iriskt.Bot
import com.spear.iriskt.models.ChatContext

/**
 * irispy-client 호환 함수형 데코레이터
 */
object Decorators {
    
    /**
     * 파라미터가 있는 경우에만 실행
     */
    fun hasParam(handler: suspend (ChatContext) -> Unit): suspend (ChatContext) -> Unit {
        return { context ->
            if (context.message.param.isNotBlank()) {
                handler(context)
            } else {
                context.reply("파라미터가 필요합니다.")
            }
        }
    }

    /**
     * 관리자만 실행 가능
     */
    fun isAdmin(handler: suspend (ChatContext) -> Unit): suspend (ChatContext) -> Unit {
        return { context ->
            if (context.sender.type == "HOST" || context.sender.type == "MANAGER") {
                handler(context)
            } else {
                context.reply("관리자만 사용할 수 있는 명령어입니다.")
            }
        }
    }

    /**
     * 답장인 경우에만 실행
     */
    fun isReply(handler: suspend (ChatContext) -> Unit): suspend (ChatContext) -> Unit {
        return { context ->
            if (context.message.isReply) {
                handler(context)
            } else {
                context.reply("답장 메시지에서만 사용할 수 있습니다.")
            }
        }
    }

    /**
     * 차단되지 않은 사용자만 실행
     */
    fun isNotBanned(
        bot: Bot,
        handler: suspend (ChatContext) -> Unit
    ): suspend (ChatContext) -> Unit {
        // 차단 사용자 Set을 미리 가져와서 캐싱
        val bannedUsers = bot.options.bannedUsers
        return { context ->
            if (!bannedUsers.contains(context.sender.id)) {
                handler(context)
            }
        }
    }

    /**
     * 특정 역할만 실행 가능
     */
    fun hasRole(
        roles: List<String>,
        onFail: suspend (ChatContext) -> Unit = { it.reply("권한이 없습니다.") },
        handler: suspend (ChatContext) -> Unit
    ): suspend (ChatContext) -> Unit {
        return { context ->
            if (roles.contains(context.sender.type)) {
                handler(context)
            } else {
                onFail(context)
            }
        }
    }

    /**
     * 특정 방에서만 실행 가능
     */
    fun allowedRoom(
        rooms: List<String>,
        onFail: suspend (ChatContext) -> Unit = { it.reply("이 방에서는 사용할 수 없는 명령어입니다.") },
        handler: suspend (ChatContext) -> Unit
    ): suspend (ChatContext) -> Unit {
        return { context ->
            if (rooms.contains(context.room.name) || rooms.contains(context.room.id.toString())) {
                handler(context)
            } else {
                onFail(context)
            }
        }
    }

    /**
     * 여러 데코레이터를 조합
     */
    fun compose(
        vararg decorators: (suspend (ChatContext) -> Unit) -> suspend (ChatContext) -> Unit,
        handler: suspend (ChatContext) -> Unit
    ): suspend (ChatContext) -> Unit {
        return decorators.fold(handler) { acc, decorator ->
            decorator(acc)
        }
    }
}

/**
 * 함수형 데코레이터 사용 예제
 */
object DecoratorExamples {
    
    /**
     * 단일 데코레이터 사용
     */
    val echoHandler = Decorators.hasParam { context ->
        context.reply("에코: ${context.message.param}")
    }

    /**
     * 관리자 전용 핸들러
     */
    val adminHandler = Decorators.isAdmin { context ->
        context.reply("관리자 명령어입니다.")
    }

    /**
     * 답장 전용 핸들러
     */
    val replyHandler = Decorators.isReply { context ->
        context.reply("답장을 확인했습니다!")
    }

    /**
     * 여러 데코레이터 조합
     */
    fun createComposedHandler(bot: Bot) = Decorators.compose(
        { handler -> Decorators.isNotBanned(bot, handler) },
        { handler -> Decorators.isAdmin(handler) },
        { handler -> Decorators.hasParam(handler) }
    ) { context ->
        context.reply("모든 조건을 만족했습니다: ${context.message.param}")
    }

    /**
     * 특정 방 제한 핸들러
     */
    val roomRestrictedHandler = Decorators.allowedRoom(
        rooms = listOf("테스트방", "개발방"),
        onFail = { it.reply("이 명령어는 테스트방과 개발방에서만 사용할 수 있습니다.") }
    ) { context ->
        context.reply("허용된 방에서 실행되었습니다.")
    }

    /**
     * 역할 제한 핸들러
     */
    val roleRestrictedHandler = Decorators.hasRole(
        roles = listOf("HOST", "MANAGER"),
        onFail = { it.reply("방장 또는 관리자만 사용할 수 있습니다.") }
    ) { context ->
        context.reply("관리자 권한으로 실행되었습니다.")
    }
}
