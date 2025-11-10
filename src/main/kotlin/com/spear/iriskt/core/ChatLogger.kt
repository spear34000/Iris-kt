package com.spear.iriskt.core

import com.spear.iriskt.models.ChatContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * 채팅 로그 저장
 */
class ChatLogger(
    private val logPath: String = "./chat_logs",
    private val enabled: Boolean = true
) {
    private val logger = LoggerManager.getLogger("ChatLogger")
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    
    init {
        if (enabled) {
            val logDir = File(logPath)
            if (!logDir.exists()) {
                logDir.mkdirs()
                logger.info("채팅 로그 디렉토리 생성: $logPath")
            }
        }
    }
    
    /**
     * 채팅 로그 저장
     */
    suspend fun log(context: ChatContext) {
        if (!enabled) return
        
        withContext(Dispatchers.IO) {
            try {
                val date = dateFormat.format(Date())
                val time = timeFormat.format(Date())
                val roomName = context.room.name.replace("[^a-zA-Z0-9가-힣]".toRegex(), "_")
                val logFile = File(logPath, "${date}_${roomName}.log")
                
                val logEntry = buildString {
                    append("[$time] ")
                    append("[${context.sender.name}] ")
                    append("${context.message.text}")
                    if (context.message.attachment != null) {
                        append(" [첨부: ${context.message.attachment}]")
                    }
                    append("\n")
                }
                
                logFile.appendText(logEntry)
            } catch (e: Exception) {
                logger.error("채팅 로그 저장 실패", e)
            }
        }
    }
    
    /**
     * 특정 날짜의 로그 조회
     */
    suspend fun getLog(roomName: String, date: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val cleanRoomName = roomName.replace("[^a-zA-Z0-9가-힣]".toRegex(), "_")
                val logFile = File(logPath, "${date}_${cleanRoomName}.log")
                if (logFile.exists()) {
                    logFile.readText()
                } else {
                    null
                }
            } catch (e: Exception) {
                logger.error("채팅 로그 조회 실패", e)
                null
            }
        }
    }
    
    /**
     * 로그 파일 목록 조회
     */
    suspend fun listLogs(): List<String> {
        return withContext(Dispatchers.IO) {
            try {
                val logDir = File(logPath)
                logDir.listFiles()?.map { it.name }?.sorted() ?: emptyList()
            } catch (e: Exception) {
                logger.error("로그 파일 목록 조회 실패", e)
                emptyList()
            }
        }
    }
    
    /**
     * 오래된 로그 삭제 (일 단위)
     */
    suspend fun cleanOldLogs(daysToKeep: Int = 30) {
        withContext(Dispatchers.IO) {
            try {
                val logDir = File(logPath)
                val cutoffTime = System.currentTimeMillis() - (daysToKeep * 24 * 60 * 60 * 1000L)
                
                logDir.listFiles()?.forEach { file ->
                    if (file.lastModified() < cutoffTime) {
                        file.delete()
                        logger.info("오래된 로그 삭제: ${file.name}")
                    }
                }
            } catch (e: Exception) {
                logger.error("오래된 로그 삭제 실패", e)
            }
        }
    }
}
