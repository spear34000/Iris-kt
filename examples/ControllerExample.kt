package com.spear.iriskt.examples

import com.spear.iriskt.Bot
import com.spear.iriskt.BotOptions
import com.spear.iriskt.LogLevel
import com.spear.iriskt.annotations.*
import com.spear.iriskt.core.IrisLink
import com.spear.iriskt.core.LoggerManager
import com.spear.iriskt.models.ChatContext
import com.spear.iriskt.util.BotUtils
import kotlinx.coroutines.runBlocking

/**
 * 컨트롤러 방식 봇 예제 (권장)
 */

@MessageController
@Prefix("!")
class CustomMessageController {
    private val logger = LoggerManager.getLogger("CustomMessageController")
    private var kakaoLink: IrisLink? = null

    init {
        val appKey = System.getenv("KAKAOLINK_APP_KEY")
        val origin = System.getenv("KAKAOLINK_ORIGIN")
        if (appKey != null && origin != null) {
            kakaoLink = IrisLink(appKey, origin)
        }
    }

    @BotCommand("안녕", "인사 명령어")
    suspend fun hello(context: ChatContext) {
        context.reply("안녕하세요!")
    }

    @BotCommand("카카오링크", "카카오링크 전송 테스트")
    suspend fun linkCommand(context: ChatContext) {
        val link = kakaoLink
        if (link == null) {
            context.reply("카카오링크가 설정되지 않았습니다.")
            return
        }

        try {
            link.send(
                receiverName = context.room.name,
                templateId = 123417,
                templateArgs = mapOf("TEXT" to "테스트")
            )
            context.reply("카카오링크를 전송했습니다.")
        } catch (e: Exception) {
            logger.error("카카오링크 전송 실패", e)
            context.reply("카카오링크 전송에 실패했습니다.")
        }
    }

    @BotCommand("반복", "메시지 반복")
    @HasParam
    suspend fun echo(context: ChatContext) {
        context.reply("반복: ${context.message.param}")
    }

    @BotCommand("관리자", "관리자 전용 명령어")
    @IsAdmin
    suspend fun adminOnly(context: ChatContext) {
        context.reply("관리자만 사용할 수 있는 명령어입니다!")
    }

    @BotCommand("제한", "사용 빈도 제한 테스트")
    @Throttle(maxCalls = 3, timeWindowMs = 60000)
    suspend fun throttleTest(context: ChatContext) {
        context.reply("1분에 3번만 사용할 수 있습니다.")
    }

    @BotCommand("답장", "답장 전용 명령어")
    @IsReply
    suspend fun replyOnly(context: ChatContext) {
        context.reply("답장 메시지를 확인했습니다!")
    }
}

@BatchController
class CustomBatchController {
    private val logger = LoggerManager.getLogger("CustomBatchController")

    @Schedule(interval = 60000) // 1분마다
    suspend fun periodicTask() {
        logger.info("주기적 작업 실행 중...")
    }

    @ScheduleMessage("reminder")
    suspend fun handleReminder(scheduledMessage: BatchScheduler.ScheduledMessage) {
        logger.info("리마인더: ${scheduledMessage.message}")
    }
}

@FeedController
class CustomFeedController {
    @OnInviteUserFeed
    suspend fun onUserJoin(context: ChatContext) {
        context.reply("새로운 멤버를 환영합니다! 🎉")
    }

    @OnPromoteManagerFeed
    suspend fun onManagerPromote(context: ChatContext) {
        context.reply("새로운 관리자가 임명되었습니다! 👑")
    }
}

@BootstrapController
class CustomBootstrapController {
    private val logger = LoggerManager.getLogger("CustomBootstrapController")

    @Bootstrap(priority = 1)
    suspend fun initialize() {
        logger.info("봇 초기화 중...")
    }

    @Bootstrap(priority = 2)
    suspend fun loadConfig() {
        logger.info("설정 로드 중...")
    }
}

class App {
    private val logger = LoggerManager.getLogger("App")
    private lateinit var bot: Bot

    fun start() = runBlocking {
        val irisUrl = System.getenv("IRIS_URL") ?: error("IRIS_URL 환경 변수를 설정하세요")

        bot = Bot(
            botName = "Node-Iris-Kt",
            irisUrl = irisUrl,
            options = BotOptions(
                maxWorkers = 8,
                logLevel = LogLevel.DEBUG,
                httpMode = false, // WebSocket 모드
                bannedUsers = setOf(123456789L, 987654321L),
                kakaoLinkAppKey = System.getenv("KAKAOLINK_APP_KEY"),
                kakaoLinkOrigin = System.getenv("KAKAOLINK_ORIGIN")
            )
        )

        // 컨트롤러 등록
        // autoRegisterControllers가 false일 때 수동으로 컨트롤러 등록
        // bot.registerControllers(listOf(
        //     CustomMessageController::class,
        //     CustomBatchController::class,
        //     CustomFeedController::class,
        //     CustomBootstrapController::class
        // ))

        logger.info("${bot} 시작 중...")
        bot.run()
    }

    fun stop() {
        logger.info("봇 종료 중...")
        bot.close()
    }
}

fun main() {
    val app = App()

    // 종료 시그널 처리
    Runtime.getRuntime().addShutdownHook(Thread {
        println("종료 시그널 수신, 봇 종료 중...")
        app.stop()
    })

    app.start()
}
