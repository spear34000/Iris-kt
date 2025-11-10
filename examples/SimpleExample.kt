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
    // message : 모든 메시지
    // text_message : 일반 텍스트 메시지 (type = 1, 첨부파일 없음)
    // link_message : 링크 메시지 (type = 1, 첨부파일 있음)
    // photo_message : 사진 메시지 (type = 2)
    // video_message : 동영상 메시지 (type = 3)
    // join_feed : 멤버 입장 피드 (type = 4)
    // leave_feed : 멤버 퇴장 피드 (type = 2)
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
    
    // 사진 메시지 처리
    bot.onEvent("photo_message") { payload ->
        if (payload is ChatContext) {
            payload.reply("사진을 받았습니다! 📷")
        }
    }
    
    // 멤버 입장 처리
    bot.onEvent("join_feed") { payload ->
        if (payload is ChatContext) {
            payload.reply("${payload.sender.name}님 환영합니다! 🎉")
        }
    }

    bot.run()
}

    
    // 메시지 타입별 처리 (타입 체크 메서드 사용)
    bot.onEvent("message") { payload ->
        if (payload is ChatContext) {
            when {
                payload.message.isPhoto -> payload.reply("사진을 받았습니다! 📷")
                payload.message.isVideo -> payload.reply("동영상을 받았습니다! 🎥")
                payload.message.isLink -> payload.reply("링크를 받았습니다! 🔗")
                payload.message.isReply -> payload.reply("답장 메시지입니다!")
                payload.message.isEmoticon -> payload.reply("이모티콘! 😊")
            }
        }
    }
