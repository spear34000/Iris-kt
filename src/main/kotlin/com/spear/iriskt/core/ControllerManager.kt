package com.spear.iriskt.core

import com.spear.iriskt.annotations.*
import com.spear.iriskt.models.ChatContext
import com.spear.iriskt.models.ErrorContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.functions
import kotlin.reflect.full.hasAnnotation

/**
 * 컨트롤러 관�??�래?? */
class ControllerManager(private val bot: Any) {
    private val logger = LoggerManager.defaultLogger
    private val controllers = mutableListOf<Any>()
    private val messageHandlers = mutableMapOf<String, MutableList<CommandHandler>>()
    private val eventHandlers = mutableMapOf<String, MutableList<EventHandler>>()
    private val bootstrapHandlers = mutableListOf<BootstrapHandler>()
    private val scheduleHandlers = mutableListOf<ScheduleHandler>()
    private val scope = CoroutineScope(Dispatchers.Default)

    /**
     * 컨트롤러 ?�록
     */
    fun registerController(controller: Any) {
        controllers.add(controller)
        processController(controller)
    }

    /**
     * 컨트롤러 처리
     */
    private fun processController(controller: Any) {
        val controllerClass = controller::class
        
        // 부?�스?�랩 ?�들???�록
        if (controllerClass.hasAnnotation<BootstrapController>()) {
            registerBootstrapHandlers(controller)
        }
        
        // 배치 ?�들???�록
        if (controllerClass.hasAnnotation<BatchController>()) {
            registerScheduleHandlers(controller)
        }
        
        // 메시지 ?�들???�록
        if (controllerClass.hasAnnotation<MessageController>() || 
            controllerClass.hasAnnotation<ChatController>() || 
            controllerClass.hasAnnotation<Controller>()) {
            registerMessageHandlers(controller)
        }
        
        // ?�벤???�들???�록
        registerEventHandlers(controller)
    }

    /**
     * 부?�스?�랩 ?�들???�록
     */
    private fun registerBootstrapHandlers(controller: Any) {
        controller::class.functions.forEach { function ->
            function.findAnnotation<Bootstrap>()?.let { annotation ->
                bootstrapHandlers.add(
                    BootstrapHandler(
                        controller = controller,
                        function = function,
                        priority = annotation.priority
                    )
                )
            }
        }
        
        // ?�선?�위???�라 ?�렬
        bootstrapHandlers.sortBy { it.priority }
    }

    /**
     * ?��?�??�들???�록
     */
    private fun registerScheduleHandlers(controller: Any) {
        controller::class.functions.forEach { function ->
            function.findAnnotation<Schedule>()?.let { annotation ->
                scheduleHandlers.add(
                    ScheduleHandler(
                        controller = controller,
                        function = function,
                        interval = annotation.interval
                    )
                )
            }
        }
    }

    /**
     * 메시지 ?�들???�록
     */
    private fun registerMessageHandlers(controller: Any) {
        val controllerClass = controller::class
        val prefix = controllerClass.findAnnotation<Prefix>()?.prefix ?: ""
        
        controller::class.functions.forEach { function ->
            // �?명령???�록
            function.findAnnotation<BotCommand>()?.let { annotation ->
                val commandName = prefix + annotation.command
                val handler = CommandHandler(
                    controller = controller,
                    function = function,
                    command = commandName,
                    description = annotation.description
                )
                
                messageHandlers.getOrPut(commandName) { mutableListOf() }.add(handler)
            }
            
            // ?��?�?명령???�록
            function.findAnnotation<HelpCommand>()?.let { annotation ->
                val commandName = prefix + annotation.command
                val handler = CommandHandler(
                    controller = controller,
                    function = function,
                    command = commandName,
                    description = "?��?�?
                )
                
                messageHandlers.getOrPut(commandName) { mutableListOf() }.add(handler)
            }
        }
    }

    /**
     * ?�벤???�들???�록
     */
    private fun registerEventHandlers(controller: Any) {
        controller::class.functions.forEach { function ->
            // 메시지 ?�?�별 ?�들???�록
            registerMessageTypeHandlers(controller, function)
            
            // ?�드 ?�?�별 ?�들???�록
            registerFeedTypeHandlers(controller, function)
        }
    }

    /**
     * 메시지 타입별 핸들러 등록
     */
    private fun registerMessageTypeHandlers(controller: Any, function: KFunction<*>) {
        val eventType = when {
            function.hasAnnotation<OnMessage>() -> "message"
            // 새로운 어노테이션
            function.hasAnnotation<OnTextMessage>() -> "text_message"
            function.hasAnnotation<OnLinkMessage>() -> "link_message"
            function.hasAnnotation<OnPhotoMessage>() -> "photo_message"
            function.hasAnnotation<OnVideoMessage>() -> "video_message"
            function.hasAnnotation<OnContactMessage>() -> "contact_message"
            function.hasAnnotation<OnAudioMessage>() -> "audio_message"
            function.hasAnnotation<OnEmoticonMessage>() -> "emoticon_message"
            function.hasAnnotation<OnEmoticonThumbnailMessage>() -> "emoticon_thumbnail_message"
            function.hasAnnotation<OnVoteMessage>() -> "vote_message"
            function.hasAnnotation<OnProfileMessage>() -> "profile_message"
            function.hasAnnotation<OnFileMessage>() -> "file_message"
            function.hasAnnotation<OnSearchMessage>() -> "search_message"
            function.hasAnnotation<OnNoticeMessage>() -> "notice_message"
            function.hasAnnotation<OnReplyMessage>() -> "reply_message"
            function.hasAnnotation<OnMultiPhotoMessage>() -> "multi_photo_message"
            function.hasAnnotation<OnVoiceTalkMessage>() -> "voice_talk_message"
            function.hasAnnotation<OnVoteRegisterMessage>() -> "vote_register_message"
            function.hasAnnotation<OnShareMessage>() -> "share_message"
            // 하위 호환용 (Deprecated)
            function.hasAnnotation<OnNormalMessage>() -> "text_message"
            function.hasAnnotation<OnImageMessage>() -> "photo_message"
            function.hasAnnotation<OnNewMultiPhotoMessage>() -> "multi_photo_message"
            else -> null                                                        
        }
        
        eventType?.let {
            eventHandlers.getOrPut(it) { mutableListOf() }.add(
                EventHandler(
                    controller = controller,
                    function = function,
                    eventType = it
                )
            )
        }
    }

    /**
     * 피드 타입별 핸들러 등록
     */
    private fun registerFeedTypeHandlers(controller: Any, function: KFunction<*>) {
        val eventType = when {
            function.hasAnnotation<OnFeedMessage>() -> "feed"
            // 새로운 어노테이션
            function.hasAnnotation<OnLeaveFeed>() -> "leave_feed"
            function.hasAnnotation<OnJoinFeed>() -> "join_feed"
            function.hasAnnotation<OnForcedExitFeed>() -> "forced_exit_feed"
            function.hasAnnotation<OnDeleteMessageFeed>() -> "delete_message_feed"
            function.hasAnnotation<OnHideMessageFeed>() -> "hide_message_feed"
            function.hasAnnotation<OnPromoteManagerFeed>() -> "promote_manager_feed"
            function.hasAnnotation<OnDemoteManagerFeed>() -> "demote_manager_feed"
            function.hasAnnotation<OnHandOverHostFeed>() -> "hand_over_host_feed"
            function.hasAnnotation<OnOpenChatJoinFeed>() -> "open_chat_join_feed"
            function.hasAnnotation<OnOpenChatKickedFeed>() -> "open_chat_kicked_feed"
            // 하위 호환용 (Deprecated)
            function.hasAnnotation<OnInviteUserFeed>() -> "join_feed"
            function.hasAnnotation<OnLeaveUserFeed>() -> "leave_feed"
            function.hasAnnotation<OnOpenChatJoinUserFeed>() -> "open_chat_join_feed"
            function.hasAnnotation<OnOpenChatKickedUserFeed>() -> "open_chat_kicked_feed"
            else -> null
        }
        
        eventType?.let {
            eventHandlers.getOrPut(it) { mutableListOf() }.add(
                EventHandler(
                    controller = controller,
                    function = function,
                    eventType = it
                )
            )
        }
    }

    /**
     * 부?�스?�랩 ?�행
     */
    suspend fun runBootstrap() {
        for (handler in bootstrapHandlers) {
            try {
                handler.invoke()
            } catch (e: Exception) {
                logger.error("부?�스?�랩 ?�행 �??�류 발생", e)
            }
        }
    }

    /**
     * ?��?�??�행
     */
    fun startSchedulers() {
        for (handler in scheduleHandlers) {
            if (handler.interval > 0) {
                val scheduler = BatchScheduler.getInstance()
                scheduler.scheduleAtFixedRate(handler.interval) {
                    scope.launch {
                        try {
                            handler.invoke()
                        } catch (e: Exception) {
                            logger.error("?��?�??�업 ?�행 �??�류 발생", e)
                        }
                    }
                }
            }
        }
    }

    /**
     * 메시지 처리
     */
    suspend fun handleMessage(context: ChatContext) {
        val command = context.message.command
        
        // 명령???�들???�행
        messageHandlers[command]?.forEach { handler ->
            try {
                if (checkConditions(handler, context)) {
                    handler.invoke(context)
                }
            } catch (e: Exception) {
                logger.error("메시지 ?�들???�행 �??�류 발생", e)
                handleError(ErrorContext("message", handler.function, e, listOf(context)))
            }
        }
        
        // ?�벤???�들???�행
        val messageType = getMessageType(context)
        eventHandlers[messageType]?.forEach { handler ->
            try {
                if (checkConditions(handler, context)) {
                    handler.invoke(context)
                }
            } catch (e: Exception) {
                logger.error("?�벤???�들???�행 �??�류 발생", e)
                handleError(ErrorContext("event", handler.function, e, listOf(context)))
            }
        }
    }

    /**
     * 메시지 타입 확인
     */
    private fun getMessageType(context: ChatContext): String {
        val type = context.message.type
        val hasAttachment = context.message.attachment?.path?.isNotEmpty() == true
        
        return when (type) {
            1 -> if (hasAttachment) "link_message" else "text_message"
            2 -> "photo_message"
            3 -> "video_message"
            4 -> "contact_message"
            5 -> "audio_message"
            6 -> "emoticon_message"
            12, 20 -> "emoticon_thumbnail_message"
            14 -> "vote_message"
            17 -> "profile_message"
            18 -> "file_message"
            23 -> "search_message"
            24 -> "notice_message"
            26 -> "reply_message"
            27 -> "multi_photo_message"
            51 -> "voice_talk_message"
            97 -> "vote_register_message"
            98 -> "share_message"
            else -> "message"
        }
    }

    /**
     * 조건 ?�인
     */
    private fun checkConditions(handler: Any, context: ChatContext): Boolean {
        val function = when (handler) {
            is CommandHandler -> handler.function
            is EventHandler -> handler.function
            else -> return true
        }
        
        // HasParam 조건 ?�인
        if (function.hasAnnotation<HasParam>() && context.message.param.isBlank()) {
            return false
        }
        
        // IsReply 조건 ?�인
        if (function.hasAnnotation<IsReply>() && context.message.metadata?.get("reply_id") == null) {
            context.reply("메세지???�장?�여 ?�청?�세??")
            return false
        }
        
        // IsAdmin 조건 ?�인
        if (function.hasAnnotation<IsAdmin>() && context.sender.type != "HOST" && context.sender.type != "MANAGER") {
            context.reply("관리자�??�용?????�는 기능?�니??")
            return false
        }
        
        // IsNotBanned 조건 확인
        if (function.hasAnnotation<IsNotBanned>() && bot is com.spear.iriskt.Bot) {
            if (bot.isBannedUser(context.sender.id)) {
                return false
            }
        }
        
        // HasRole 조건 ?�인
        function.findAnnotation<HasRole>()?.let { annotation ->
            if (!annotation.roles.contains(context.sender.type)) {
                context.reply("권한???�습?�다.")
                return false
            }
        }
        
        // AllowedRoom 조건 ?�인
        function.findAnnotation<AllowedRoom>()?.let { annotation ->
            if (!annotation.rooms.contains(context.room.name)) {
                context.reply("??방에?�는 ?�용?????�는 기능?�니??")
                return false
            }
        }
        
        return true
    }

    /**
     * ?�러 처리
     */
    private suspend fun handleError(errorContext: ErrorContext) {
        eventHandlers["error"]?.forEach { handler ->
            try {
                handler.invoke(errorContext)
            } catch (e: Exception) {
                logger.error("?�러 ?�들???�행 �??�류 발생", e)
            }
        }
    }

    /**
     * 명령???�들???�래??     */
    data class CommandHandler(
        val controller: Any,
        val function: KFunction<*>,
        val command: String,
        val description: String
    ) {
        suspend fun invoke(context: ChatContext) {
            function.call(controller, context)
        }
    }

    /**
     * ?�벤???�들???�래??     */
    data class EventHandler(
        val controller: Any,
        val function: KFunction<*>,
        val eventType: String
    ) {
        suspend fun invoke(context: Any) {
            function.call(controller, context)
        }
    }

    /**
     * 부?�스?�랩 ?�들???�래??     */
    data class BootstrapHandler(
        val controller: Any,
        val function: KFunction<*>,
        val priority: Int
    ) {
        suspend fun invoke() {
            function.call(controller)
        }
    }

    /**
     * ?��?�??�들???�래??     */
    data class ScheduleHandler(
        val controller: Any,
        val function: KFunction<*>,
        val interval: Long
    ) {
        suspend fun invoke() {
            function.call(controller)
        }
    }
}
