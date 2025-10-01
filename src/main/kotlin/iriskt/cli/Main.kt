package iriskt.cli

import iriskt.bot.Bot
import iriskt.bot.models.ChatContext
import iriskt.bot.models.ErrorContext
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val irisUrl = System.getenv("IRIS_ENDPOINT") ?: error("IRIS_ENDPOINT 환경 변수를 설정하세요")
    Bot("IrisBot", irisUrl).use { bot ->
        bot.onEvent("chat") { payload ->
            if (payload is ChatContext) {
                println("[${'$'}{payload.room.name}] ${'$'}{payload.sender.name}: ${'$'}{payload.message.text}")
            }
        }
        bot.onEvent("error") { payload ->
            if (payload is ErrorContext) {
                System.err.println("[${'$'}{payload.event}] ${'$'}{payload.exception.message}")
            }
        }
        bot.run()
    }
}
