package com.spear.iriskt.core

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.cio.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

/**
 * Webhook 서버 (HTTP 모드용)
 */
class WebhookServer(
    private val port: Int,
    private val path: String,
    private val onMessage: suspend (String) -> Unit
) {
    private val logger = LoggerManager.getLogger("WebhookServer")
    private var server: ApplicationEngine? = null
    
    fun start() {
        logger.info("Webhook 서버 시작: http://0.0.0.0:$port$path")
        
        server = embeddedServer(CIO, port = port) {
            routing {
                post(path) {
                    try {
                        val body = call.receiveText()
                        logger.debug("Webhook 수신: $body")
                        
                        onMessage(body)
                        
                        call.respond(HttpStatusCode.OK, mapOf("status" to "ok"))
                    } catch (e: Exception) {
                        logger.error("Webhook 처리 오류", e)
                        call.respond(
                            HttpStatusCode.InternalServerError,
                            mapOf("status" to "error", "message" to e.message)
                        )
                    }
                }
                
                get("/health") {
                    call.respond(HttpStatusCode.OK, mapOf("status" to "healthy"))
                }
            }
        }.start(wait = false)
        
        logger.info("Webhook 서버 시작 완료")
    }
    
    fun stop() {
        logger.info("Webhook 서버 중지")
        server?.stop(1000, 2000)
        server = null
    }
}
