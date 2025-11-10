package com.spear.iriskt.examples

import com.spear.iriskt.Bot
import com.spear.iriskt.BotOptions
import com.spear.iriskt.models.ChatContext
import com.spear.iriskt.util.Decorators
import kotlinx.coroutines.runBlocking

/**
 * 함수형 데코레이터 사용 예제
 */
fun main() = runBlocking {
    val irisUrl = System.getenv("IRIS_URL") ?: error("IRIS_URL 환경 변수를 설정하세요")
    
    val bot = Bot(
        botName = "DecoratorBot",
        irisUrl = irisUrl,
        options = BotOptions(
            maxWorkers = 4,
            bannedUsers = setOf(123456789L)
        )
    )

    // 파라미터가 있는 경우에만 실행
    val echoHandler = Decorators.hasParam { context ->
        context.reply("에코: ${context.message.param}")
    }

    // 관리자만 실행 가능
    val adminHandler = Decorators.isAdmin { context ->
        context.reply("관리자 명령어입니다.")
    }

    // 답장인 경우에만 실행
    val replyHandler = Decorators.isReply { context ->
        context.reply("답장을 확인했습니다!")
    }

    // 차단되지 않은 사용자만 실행
    val notBannedHandler = Decorators.isNotBanned(bot) { context ->
        context.reply("실행 가능합니다.")
    }

    // 특정 역할만 실행 가능
    val roleHandler = Decorators.hasRole(
        roles = listOf("HOST", "MANAGER"),
        onFail = { it.reply("방장 또는 관리자만 사용할 수 있습니다.") }
    ) { context ->
        context.reply("관리자 권한으로 실행되었습니다.")
    }

    // 특정 방에서만 실행 가능
    val roomHandler = Decorators.allowedRoom(
        rooms = listOf("테스트방", "개발방")
    ) { context ->
        context.reply("허용된 방에서 실행되었습니다.")
    }

    // 여러 데코레이터 조합
    val composedHandler = Decorators.compose(
        { handler -> Decorators.isNotBanned(bot, handler) },
        { handler -> Decorators.isAdmin(handler) },
        { handler -> Decorators.hasParam(handler) }
    ) { context ->
        context.reply("모든 조건을 만족했습니다: ${context.message.param}")
    }

    // 이벤트 핸들러 등록
    bot.onEvent("message") { payload ->
        if (payload is ChatContext) {
            when (payload.message.command) {
                "에코" -> echoHandler(payload)
                "관리자" -> adminHandler(payload)
                "답장" -> replyHandler(payload)
                "테스트" -> notBannedHandler(payload)
                "역할" -> roleHandler(payload)
                "방" -> roomHandler(payload)
                "조합" -> composedHandler(payload)
            }
        }
    }

    bot.run()
}
