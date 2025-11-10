package com.spear.iriskt.internal

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import com.spear.iriskt.models.ErrorContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import mu.KotlinLogging
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors

private val logger = KotlinLogging.logger {}

class EventEmitter(maxConcurrency: Int? = null, dispatcher: CoroutineDispatcher = Dispatchers.Default) {
    private val executor = maxConcurrency?.let { 
        Executors.newFixedThreadPool(it) { runnable ->
            Thread(runnable).apply {
                isDaemon = true
                name = "EventEmitter-${Thread.currentThread().id}"
            }
        }
    }
    private val eventDispatcher = executor?.asCoroutineDispatcher() ?: dispatcher
    private val scope = CoroutineScope(SupervisorJob() + eventDispatcher)
    private val listeners = ConcurrentHashMap<String, CopyOnWriteArrayList<suspend (Any) -> Unit>>()
    
    // 이벤트 이름 캐싱 (lowercase 연산 최소화)
    private val eventNameCache = ConcurrentHashMap<String, String>()

    fun register(name: String, handler: suspend (Any) -> Unit) {
        val key = eventNameCache.computeIfAbsent(name) { it.lowercase() }
        listeners.computeIfAbsent(key) { CopyOnWriteArrayList() }.add(handler)
    }

    fun emit(name: String, payload: Any) {
        val key = eventNameCache.computeIfAbsent(name) { it.lowercase() }
        val handlers = listeners[key] ?: return
        
        // 핸들러가 없으면 바로 리턴
        if (handlers.isEmpty()) return
        
        // 핸들러 실행 (병렬 처리)
        handlers.forEach { handler ->
            scope.launch {
                runCatching { 
                    handler(payload) 
                }.onFailure { error ->
                    if (key == "error") {
                        logger.error(error) { "error 핸들러에서 예외가 발생했습니다" }
                    } else {
                        val context = ErrorContext(event = key, handler = handler, exception = error, payload = payload)
                        emit("error", context)
                    }
                }
            }
        }
    }

    fun close() {
        scope.cancel()
        executor?.shutdown()
    }
}
