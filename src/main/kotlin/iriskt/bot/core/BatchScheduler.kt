package iriskt.bot.core

import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

data class ScheduledMessage(
    val id: String,
    val roomId: Long,
    val message: String,
    val scheduledTime: Long,
    val metadata: Map<String, Any> = emptyMap()
)

class BatchScheduler private constructor() {

    private val scheduledMessages = ConcurrentHashMap<String, ScheduledMessage>()
    private val jobCounter = AtomicLong(0)
    private val schedulerScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    companion object {
        @Volatile
        private var instance: BatchScheduler? = null

        fun getInstance(): BatchScheduler {
            return instance ?: synchronized(this) {
                instance ?: BatchScheduler().also { instance = it }
            }
        }
    }

    fun scheduleMessage(
        id: String,
        roomId: Long,
        message: String,
        delayMillis: Long,
        metadata: Map<String, Any> = emptyMap()
    ): String {
        val scheduledTime = System.currentTimeMillis() + delayMillis
        return scheduleMessageAt(id, roomId, message, scheduledTime, metadata)
    }

    fun scheduleMessageAt(
        id: String,
        roomId: Long,
        message: String,
        scheduledTime: Long,
        metadata: Map<String, Any> = emptyMap()
    ): String {
        val scheduledMessage = ScheduledMessage(id, roomId, message, scheduledTime, metadata)
        scheduledMessages[id] = scheduledMessage

        val delayMillis = (scheduledTime - System.currentTimeMillis()).coerceAtLeast(0)

        schedulerScope.launch {
            delay(delayMillis)
            executeScheduledMessage(scheduledMessage)
        }

        return id
    }

    private suspend fun executeScheduledMessage(scheduledMessage: ScheduledMessage) {
        scheduledMessages.remove(scheduledMessage.id)
        // 실제로는 여기서 메시지를 전송해야 함
        // 현재는 로그만 출력
        LoggerManager.defaultLogger.info("예약 메시지 실행: ${scheduledMessage.message} (방 ID: ${scheduledMessage.roomId})")
    }

    fun cancelMessage(id: String): Boolean {
        return scheduledMessages.remove(id) != null
    }

    fun getScheduledMessage(id: String): ScheduledMessage? {
        return scheduledMessages[id]
    }

    fun getAllScheduledMessages(): List<ScheduledMessage> {
        return scheduledMessages.values.toList()
    }

    fun clearAll() {
        scheduledMessages.clear()
        schedulerScope.coroutineContext.cancelChildren()
    }

    fun getScheduledCount(): Int {
        return scheduledMessages.size
    }
}
