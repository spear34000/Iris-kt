package com.spear.iriskt.core

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

/**
 * 배치 ?��?줄러 ?�래??(?��???
 */
class BatchScheduler private constructor() {
    private val logger = LoggerManager.defaultLogger
    private val scheduledMessages = ConcurrentHashMap<String, ScheduledMessage>()
    private val scheduledTasks = ConcurrentHashMap<String, Job>()
    private val jobCounter = AtomicLong(0)
    
    // IO 작업에 최적화된 디스패처 사용
    private val schedulerScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val messageHandlers = ConcurrentHashMap<String, suspend (Any) -> Unit>()

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

        val job = schedulerScope.launch {
            delay(delayMillis)
            executeScheduledMessage(scheduledMessage)
        }
        
        // Job 추적 (취소 가능하도록)
        scheduledTasks[id] = job

        return id
    }

    private suspend fun executeScheduledMessage(scheduledMessage: ScheduledMessage) {
        scheduledMessages.remove(scheduledMessage.id)
        // ?�제로는 ?�기??메시지�??�송?�야 ??
        // ?�재??로그�?출력
        logger.info("?�약 메시지 ?�행: ${scheduledMessage.message} (�?ID: ${scheduledMessage.roomId})")
    }

    fun cancelMessage(id: String): Boolean {
        val removed = scheduledMessages.remove(id) != null
        scheduledTasks[id]?.cancel()
        scheduledTasks.remove(id)
        return removed
    }

    fun getScheduledMessage(id: String): ScheduledMessage? {
        return scheduledMessages[id]
    }

    fun getAllScheduledMessages(): List<ScheduledMessage> {
        return scheduledMessages.values.toList()
    }

    fun clearAll() {
        scheduledMessages.clear()
        scheduledTasks.values.forEach { it.cancel() }
        scheduledTasks.clear()
        schedulerScope.coroutineContext.cancelChildren()
    }
    
    fun shutdown() {
        clearAll()
        schedulerScope.cancel()
    }

    fun getScheduledCount(): Int {
        return scheduledMessages.size
    }
    
    /**
     * ?�정 ?�간 ?�에 ?�업????�??�행?�니??
     */
    fun scheduleOnce(delayMs: Long, task: suspend () -> Unit): String {
        val taskId = "task-${jobCounter.incrementAndGet()}"
        val job = schedulerScope.launch {
            try {
                delay(delayMs)
                task()
            } catch (e: Exception) {
                logger.error("?��?�??�업 ?�행 �??�류 발생", e)
            } finally {
                scheduledTasks.remove(taskId)
            }
        }
        scheduledTasks[taskId] = job
        return taskId
    }
    
    /**
     * ?�정 간격?�로 ?�업??반복 ?�행?�니??
     */
    fun scheduleAtFixedRate(intervalMs: Long, task: suspend () -> Unit): String {
        val taskId = "task-${jobCounter.incrementAndGet()}"
        val job = schedulerScope.launch {
            try {
                while (true) {
                    task()
                    delay(intervalMs)
                }
            } catch (e: Exception) {
                logger.error("반복 ?��?�??�업 ?�행 �??�류 발생", e)
                scheduledTasks.remove(taskId)
            }
        }
        scheduledTasks[taskId] = job
        return taskId
    }
    
    /**
     * ?�정 ?�업??취소?�니??
     */
    fun cancelTask(taskId: String): Boolean {
        val job = scheduledTasks[taskId] ?: return false
        job.cancel()
        scheduledTasks.remove(taskId)
        return true
    }
    
    /**
     * 메시지 ?�들?��? ?�록?�니??
     */
    fun registerMessageHandler(key: String, handler: suspend (Any) -> Unit) {
        messageHandlers[key] = handler
    }
    
    /**
     * 메시지 ?�들?��? ?�거?�니??
     */
    fun removeMessageHandler(key: String) {
        messageHandlers.remove(key)
    }
    
    /**
     * 메시지�?처리?�니??
     */
    suspend fun handleMessage(key: String, data: Any): Boolean {
        val handler = messageHandlers[key] ?: return false
        try {
            handler(data)
            return true
        } catch (e: Exception) {
            logger.error("메시지 ?�들???�행 �??�류 발생", e)
            return false
        }
    }
}
