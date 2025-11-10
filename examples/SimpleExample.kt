package com.spear.iriskt.examples

import com.spear.iriskt.Bot
import com.spear.iriskt.BotOptions
import com.spear.iriskt.models.ChatContext
import kotlinx.coroutines.runBlocking

/**
 * irispy-client 호환 방식 봇 예제
 */
fun main() = runBlocking {
    val irisUrl = System.getenv("IRIS_URL") ?: error("IRIS_URL 환경 변수를 설정하세요")
    
    val bot = Bot(
        botName = "SimpleBot",
        irisUrl = irisUrl,
        options = BotOptions(maxWorkers = 4)
    )

    // 이벤트 핸들러 등록
    // chat : 모든 메시지
    // message : 일반 메시지
    // new_member : 새 멤버 참여
    // del_member : 멤버 퇴장
    // unknown : 알 수 없는 이벤트
    // error : 오류 발생
    bot.onEvent("message") { payload ->
        if (payload is ChatContext) {
            when (payload.message.command) {
                "안녕" -> payload.reply("안녕하세요!")
                "시간" -> payload.reply("현재 시각: ${java.time.LocalDateTime.now()}")
                "도움말" -> payload.reply("사용 가능한 명령어: 안녕, 시간, 도움말")
            }
        }
    }

    bot.run()
}
