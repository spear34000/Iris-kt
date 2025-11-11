package com.spear.iriskt.core

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

enum class LogLevel(val value: Int) {
    DEBUG(0),
    INFO(1),
    WARN(2),
    ERROR(3)
}

class Logger(
    val name: String = "Bot",
    private val level: LogLevel = LogLevel.INFO
) {

    fun debug(message: String) {
        log(LogLevel.DEBUG, message)
    }

    fun info(message: String) {
        log(LogLevel.INFO, message)
    }

    fun warn(message: String) {
        log(LogLevel.WARN, message)
    }

    fun error(message: String, throwable: Throwable? = null) {
        log(LogLevel.ERROR, message, throwable)
    }

    private fun log(level: LogLevel, message: String, throwable: Throwable? = null) {
        if (level.value >= this.level.value) {
            val timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            val levelStr = level.name.padEnd(5)

            val logMessage = "[$timestamp] [$levelStr] [$name] $message"

            when (level) {
                LogLevel.DEBUG, LogLevel.INFO -> println(logMessage)
                LogLevel.WARN -> System.err.println("WARN: $logMessage")
                LogLevel.ERROR -> {
                    System.err.println("ERROR: $logMessage")
                    throwable?.printStackTrace()
                }
            }
        }
    }
}

// ?�역 로거 ?�스?�스
object LoggerManager {
    var defaultLogger = Logger("IrisBot")

    fun getLogger(name: String): Logger {
        return Logger(name)
    }

    fun setLogLevel(level: LogLevel) {
        defaultLogger = Logger(defaultLogger.name, level)
    }
}
