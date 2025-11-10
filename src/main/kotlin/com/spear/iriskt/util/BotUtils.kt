package com.spear.iriskt.util

import com.spear.iriskt.Bot
import com.spear.iriskt.annotations.*
import com.spear.iriskt.core.BatchScheduler
import com.spear.iriskt.core.ThrottleManager
import com.spear.iriskt.models.ChatContext
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.functions
import kotlin.reflect.full.hasAnnotation

/**
 * Bot 유틸리티 함수 모음
 */
object BotUtils {
    private val throttleManager = ThrottleManager()
    private val registeredCommands = mutableMapOf<String, CommandInfo>()
    private val registeredControllers = mutableListOf<ControllerInfo>()
    private val batchControllers = mutableListOf<KClass<*>>()
    private val bootstrapControllers = mutableListOf<KClass<*>>()
    private val roomRestrictions = mutableMapOf<String, List<String>>()

    /**
     * 컨텍스트를 스케줄에 추가
     * @param context 채팅 컨텍스트
     * @param delayMillis 지연 시간 (밀리초)
     * @param key 스케줄 키
     */
    fun addContextToSchedule(context: ChatContext, delayMillis: Long, key: String) {
        val scheduler = BatchScheduler.getInstance()
        scheduler.scheduleMessage(
            id = key,
            roomId = context.room.id,
            message = context.message.text,
            delayMillis = delayMillis,
            metadata = mapOf(
                "userId" to context.sender.id.toString(),
                "userName" to context.sender.name,
                "roomName" to context.room.name
            )
        )
    }

    /**
     * 메시지 스케줄링
     * @param id 스케줄 ID
     * @param roomId 방 ID
     * @param message 메시지 내용
     * @param time 실행 시간 (밀리초)
     * @param metadata 메타데이터
     */
    fun scheduleMessage(
        id: String,
        roomId: Long,
        message: String,
        time: Long,
        metadata: Map<String, String> = emptyMap()
    ) {
        val scheduler = BatchScheduler.getInstance()
        scheduler.scheduleMessage(id, roomId, message, time, metadata)
    }

    /**
     * 특정 사용자의 스로틀 해제
     * @param userId 사용자 ID
     * @param commandName 명령어 이름
     */
    fun clearUserThrottle(userId: Long, commandName: String) {
        throttleManager.clearUserThrottle(userId, commandName)
    }

    /**
     * 모든 사용자의 스로틀 해제
     * @param commandName 명령어 이름
     */
    fun clearAllThrottle(commandName: String) {
        throttleManager.clearAllThrottle(commandName)
    }
    
    /**
     * 만료된 스로틀 데이터 정리
     */
    fun cleanupThrottle() {
        throttleManager.cleanup()
    }

    /**
     * 등록된 명령어 목록 조회
     * @return 명령어 정보 맵
     */
    fun getRegisteredCommands(): Map<String, CommandInfo> {
        return registeredCommands.toMap()
    }

    /**
     * 등록된 컨트롤러 목록 조회
     * @return 컨트롤러 정보 리스트
     */
    fun getRegisteredControllers(): List<ControllerInfo> {
        return registeredControllers.toList()
    }

    /**
     * 배치 컨트롤러 목록 조회
     * @return 배치 컨트롤러 클래스 리스트
     */
    fun getBatchControllers(): List<KClass<*>> {
        return batchControllers.toList()
    }

    /**
     * 부트스트랩 컨트롤러 목록 조회
     * @return 부트스트랩 컨트롤러 클래스 리스트
     */
    fun getBootstrapControllers(): List<KClass<*>> {
        return bootstrapControllers.toList()
    }

    /**
     * 부트스트랩 메소드 목록 조회
     * @param controllerClass 컨트롤러 클래스
     * @return 부트스트랩 메소드 정보 리스트
     */
    fun getBootstrapMethods(controllerClass: KClass<*>): List<MethodInfo> {
        return controllerClass.functions
            .filter { it.hasAnnotation<Bootstrap>() }
            .map { function ->
                val annotation = function.findAnnotation<Bootstrap>()!!
                MethodInfo(
                    name = function.name,
                    priority = annotation.priority
                )
            }
            .sortedBy { it.priority }
    }

    /**
     * 스케줄 메소드 목록 조회
     * @param controllerClass 컨트롤러 클래스
     * @return 스케줄 메소드 정보 리스트
     */
    fun getScheduleMethods(controllerClass: KClass<*>): List<ScheduleMethodInfo> {
        return controllerClass.functions
            .filter { it.hasAnnotation<Schedule>() }
            .map { function ->
                val annotation = function.findAnnotation<Schedule>()!!
                ScheduleMethodInfo(
                    name = function.name,
                    interval = annotation.interval
                )
            }
    }

    /**
     * 스케줄 메시지 메소드 목록 조회
     * @param controllerClass 컨트롤러 클래스
     * @return 스케줄 메시지 메소드 정보 리스트
     */
    fun getScheduleMessageMethods(controllerClass: KClass<*>): List<ScheduleMessageMethodInfo> {
        return controllerClass.functions
            .filter { it.hasAnnotation<ScheduleMessage>() }
            .map { function ->
                val annotation = function.findAnnotation<ScheduleMessage>()!!
                ScheduleMessageMethodInfo(
                    name = function.name,
                    key = annotation.key
                )
            }
    }

    /**
     * 데코레이터 메타데이터 디버깅
     */
    fun debugDecoratorMetadata() {
        println("=== Registered Commands ===")
        registeredCommands.forEach { (command, info) ->
            println("Command: $command")
            println("  Description: ${info.description}")
            println("  Controller: ${info.controllerClass.simpleName}")
            println("  Method: ${info.methodName}")
            println()
        }

        println("=== Registered Controllers ===")
        registeredControllers.forEach { info ->
            println("Controller: ${info.controllerClass.simpleName}")
            println("  Type: ${info.type}")
            println("  Prefix: ${info.prefix}")
            println()
        }
    }

    /**
     * 방 제한 설정 디버깅
     */
    fun debugRoomRestrictions() {
        println("=== Room Restrictions ===")
        roomRestrictions.forEach { (command, rooms) ->
            println("Command: $command")
            println("  Allowed Rooms: ${rooms.joinToString(", ")}")
            println()
        }
    }

    /**
     * 명령어 등록 (내부 사용)
     */
    internal fun registerCommand(
        command: String,
        description: String,
        controllerClass: KClass<*>,
        methodName: String
    ) {
        registeredCommands[command] = CommandInfo(description, controllerClass, methodName)
    }

    /**
     * 컨트롤러 등록 (내부 사용)
     */
    internal fun registerController(
        controllerClass: KClass<*>,
        type: String,
        prefix: String = ""
    ) {
        registeredControllers.add(ControllerInfo(controllerClass, type, prefix))

        when {
            controllerClass.hasAnnotation<BatchController>() -> batchControllers.add(controllerClass)
            controllerClass.hasAnnotation<BootstrapController>() -> bootstrapControllers.add(controllerClass)
        }
    }

    /**
     * 방 제한 설정 (내부 사용)
     */
    internal fun setRoomRestriction(command: String, rooms: List<String>) {
        roomRestrictions[command] = rooms
    }

    data class CommandInfo(
        val description: String,
        val controllerClass: KClass<*>,
        val methodName: String
    )

    data class ControllerInfo(
        val controllerClass: KClass<*>,
        val type: String,
        val prefix: String
    )

    data class MethodInfo(
        val name: String,
        val priority: Int
    )

    data class ScheduleMethodInfo(
        val name: String,
        val interval: Long
    )

    data class ScheduleMessageMethodInfo(
        val name: String,
        val key: String
    )
}
