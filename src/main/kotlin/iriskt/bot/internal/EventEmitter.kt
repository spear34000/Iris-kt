package iriskt.bot.internal

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import iriskt.bot.models.ErrorContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import mu.KotlinLogging
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors

private val logger = KotlinLogging.logger {}

class EventEmitter(maxConcurrency: Int? = null, dispatcher: CoroutineDispatcher = Dispatchers.Default) {
    private val executor = maxConcurrency?.let { Executors.newFixedThreadPool(it) }
    private val eventDispatcher = executor?.asCoroutineDispatcher() ?: dispatcher
    private val scope = CoroutineScope(SupervisorJob() + eventDispatcher)
    private val listeners = ConcurrentHashMap<String, CopyOnWriteArrayList<suspend (Any) -> Unit>>()

    fun register(name: String, handler: suspend (Any) -> Unit) {
        val key = name.lowercase()
        listeners.computeIfAbsent(key) { CopyOnWriteArrayList() }.add(handler)
    }

    fun emit(name: String, payload: Any) {
        val key = name.lowercase()
        val handlers = listeners[key] ?: return
        handlers.forEach { handler ->
            scope.launch {
                runCatching { handler(payload) }.onFailure { error ->
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
        executor?.shutdownNow()
    }
}
