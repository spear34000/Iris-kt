package com.spear.iriskt.core

import iriskt.bot.annotations.*
import iriskt.bot.models.ChatContext
import iriskt.bot.models.ErrorContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.functions
import kotlin.reflect.full.hasAnnotation

/**
 * ì»¨íŠ¸ë¡¤ëŸ¬ ê´€ë¦??´ë˜?? */
class ControllerManager(private val bot: Any) {
    private val logger = LoggerManager.defaultLogger
    private val controllers = mutableListOf<Any>()
    private val messageHandlers = mutableMapOf<String, MutableList<CommandHandler>>()
    private val eventHandlers = mutableMapOf<String, MutableList<EventHandler>>()
    private val bootstrapHandlers = mutableListOf<BootstrapHandler>()
    private val scheduleHandlers = mutableListOf<ScheduleHandler>()
    private val scope = CoroutineScope(Dispatchers.Default)

    /**
     * ì»¨íŠ¸ë¡¤ëŸ¬ ?±ë¡
     */
    fun registerController(controller: Any) {
        controllers.add(controller)
        processController(controller)
    }

    /**
     * ì»¨íŠ¸ë¡¤ëŸ¬ ì²˜ë¦¬
     */
    private fun processController(controller: Any) {
        val controllerClass = controller::class
        
        // ë¶€?¸ìŠ¤?¸ë© ?¸ë“¤???±ë¡
        if (controllerClass.hasAnnotation<BootstrapController>()) {
            registerBootstrapHandlers(controller)
        }
        
        // ë°°ì¹˜ ?¸ë“¤???±ë¡
        if (controllerClass.hasAnnotation<BatchController>()) {
            registerScheduleHandlers(controller)
        }
        
        // ë©”ì‹œì§€ ?¸ë“¤???±ë¡
        if (controllerClass.hasAnnotation<MessageController>() || 
            controllerClass.hasAnnotation<ChatController>() || 
            controllerClass.hasAnnotation<Controller>()) {
            registerMessageHandlers(controller)
        }
        
        // ?´ë²¤???¸ë“¤???±ë¡
        registerEventHandlers(controller)
    }

    /**
     * ë¶€?¸ìŠ¤?¸ë© ?¸ë“¤???±ë¡
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
        
        // ?°ì„ ?œìœ„???°ë¼ ?•ë ¬
        bootstrapHandlers.sortBy { it.priority }
    }

    /**
     * ?¤ì?ì¤??¸ë“¤???±ë¡
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
     * ë©”ì‹œì§€ ?¸ë“¤???±ë¡
     */
    private fun registerMessageHandlers(controller: Any) {
        val controllerClass = controller::class
        val prefix = controllerClass.findAnnotation<Prefix>()?.prefix ?: ""
        
        controller::class.functions.forEach { function ->
            // ë´?ëª…ë ¹???±ë¡
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
            
            // ?„ì?ë§?ëª…ë ¹???±ë¡
            function.findAnnotation<HelpCommand>()?.let { annotation ->
                val commandName = prefix + annotation.command
                val handler = CommandHandler(
                    controller = controller,
                    function = function,
                    command = commandName,
                    description = "?„ì?ë§?
                )
                
                messageHandlers.getOrPut(commandName) { mutableListOf() }.add(handler)
            }
        }
    }

    /**
     * ?´ë²¤???¸ë“¤???±ë¡
     */
    private fun registerEventHandlers(controller: Any) {
        controller::class.functions.forEach { function ->
            // ë©”ì‹œì§€ ?€?…ë³„ ?¸ë“¤???±ë¡
            registerMessageTypeHandlers(controller, function)
            
            // ?¼ë“œ ?€?…ë³„ ?¸ë“¤???±ë¡
            registerFeedTypeHandlers(controller, function)
        }
    }

    /**
     * ë©”ì‹œì§€ ?€?…ë³„ ?¸ë“¤???±ë¡
     */
    private fun registerMessageTypeHandlers(controller: Any, function: KFunction<*>) {
        val eventType = when {
            function.hasAnnotation<OnMessage>() -> "message"
            function.hasAnnotation<OnNormalMessage>() -> "normal_message"
            function.hasAnnotation<OnPhotoMessage>() -> "photo_message"
            function.hasAnnotation<OnImageMessage>() -> "image_message"
            function.hasAnnotation<OnVideoMessage>() -> "video_message"
            function.hasAnnotation<OnAudioMessage>() -> "audio_message"
            function.hasAnnotation<OnFileMessage>() -> "file_message"
            function.hasAnnotation<OnMapMessage>() -> "map_message"
            function.hasAnnotation<OnEmoticonMessage>() -> "emoticon_message"
            function.hasAnnotation<OnProfileMessage>() -> "profile_message"
            function.hasAnnotation<OnMultiPhotoMessage>() -> "multi_photo_message"
            function.hasAnnotation<OnNewMultiPhotoMessage>() -> "new_multi_photo_message"
            function.hasAnnotation<OnReplyMessage>() -> "reply_message"
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
     * ?¼ë“œ ?€?…ë³„ ?¸ë“¤???±ë¡
     */
    private fun registerFeedTypeHandlers(controller: Any, function: KFunction<*>) {
        val eventType = when {
            function.hasAnnotation<OnFeedMessage>() -> "feed"
            function.hasAnnotation<OnInviteUserFeed>() -> "invite_user_feed"
            function.hasAnnotation<OnLeaveUserFeed>() -> "leave_user_feed"
            function.hasAnnotation<OnDeleteMessageFeed>() -> "delete_message_feed"
            function.hasAnnotation<OnHideMessageFeed>() -> "hide_message_feed"
            function.hasAnnotation<OnPromoteManagerFeed>() -> "promote_manager_feed"
            function.hasAnnotation<OnDemoteManagerFeed>() -> "demote_manager_feed"
            function.hasAnnotation<OnHandOverHostFeed>() -> "hand_over_host_feed"
            function.hasAnnotation<OnOpenChatJoinUserFeed>() -> "open_chat_join_user_feed"
            function.hasAnnotation<OnOpenChatKickedUserFeed>() -> "open_chat_kicked_user_feed"
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
     * ë¶€?¸ìŠ¤?¸ë© ?¤í–‰
     */
    suspend fun runBootstrap() {
        for (handler in bootstrapHandlers) {
            try {
                handler.invoke()
            } catch (e: Exception) {
                logger.error("ë¶€?¸ìŠ¤?¸ë© ?¤í–‰ ì¤??¤ë¥˜ ë°œìƒ", e)
            }
        }
    }

    /**
     * ?¤ì?ì¤??¤í–‰
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
                            logger.error("?¤ì?ì¤??‘ì—… ?¤í–‰ ì¤??¤ë¥˜ ë°œìƒ", e)
                        }
                    }
                }
            }
        }
    }

    /**
     * ë©”ì‹œì§€ ì²˜ë¦¬
     */
    suspend fun handleMessage(context: ChatContext) {
        val command = context.message.command
        
        // ëª…ë ¹???¸ë“¤???¤í–‰
        messageHandlers[command]?.forEach { handler ->
            try {
                if (checkConditions(handler, context)) {
                    handler.invoke(context)
                }
            } catch (e: Exception) {
                logger.error("ë©”ì‹œì§€ ?¸ë“¤???¤í–‰ ì¤??¤ë¥˜ ë°œìƒ", e)
                handleError(ErrorContext("message", handler.function, e, listOf(context)))
            }
        }
        
        // ?´ë²¤???¸ë“¤???¤í–‰
        val messageType = getMessageType(context)
        eventHandlers[messageType]?.forEach { handler ->
            try {
                if (checkConditions(handler, context)) {
                    handler.invoke(context)
                }
            } catch (e: Exception) {
                logger.error("?´ë²¤???¸ë“¤???¤í–‰ ì¤??¤ë¥˜ ë°œìƒ", e)
                handleError(ErrorContext("event", handler.function, e, listOf(context)))
            }
        }
    }

    /**
     * ë©”ì‹œì§€ ?€???•ì¸
     */
    private fun getMessageType(context: ChatContext): String {
        return when (context.message.type) {
            1 -> "normal_message"
            2 -> "photo_message"
            // ì¶”ê??ì¸ ë©”ì‹œì§€ ?€??ë§¤í•‘
            else -> "message"
        }
    }

    /**
     * ì¡°ê±´ ?•ì¸
     */
    private fun checkConditions(handler: Any, context: ChatContext): Boolean {
        val function = when (handler) {
            is CommandHandler -> handler.function
            is EventHandler -> handler.function
            else -> return true
        }
        
        // HasParam ì¡°ê±´ ?•ì¸
        if (function.hasAnnotation<HasParam>() && context.message.param.isBlank()) {
            return false
        }
        
        // IsReply ì¡°ê±´ ?•ì¸
        if (function.hasAnnotation<IsReply>() && context.message.metadata?.get("reply_id") == null) {
            context.reply("ë©”ì„¸ì§€???µì¥?˜ì—¬ ?”ì²­?˜ì„¸??")
            return false
        }
        
        // IsAdmin ì¡°ê±´ ?•ì¸
        if (function.hasAnnotation<IsAdmin>() && context.sender.type != "HOST" && context.sender.type != "MANAGER") {
            context.reply("ê´€ë¦¬ìë§??¬ìš©?????ˆëŠ” ê¸°ëŠ¥?…ë‹ˆ??")
            return false
        }
        
        // IsNotBanned ì¡°ê±´ ?•ì¸
        if (function.hasAnnotation<IsNotBanned>() && bot is iriskt.bot.Bot) {
            if (bot.isBannedUser(context.sender.id)) {
                return false
            }
        }
        
        // HasRole ì¡°ê±´ ?•ì¸
        function.findAnnotation<HasRole>()?.let { annotation ->
            if (!annotation.roles.contains(context.sender.type)) {
                context.reply("ê¶Œí•œ???†ìŠµ?ˆë‹¤.")
                return false
            }
        }
        
        // AllowedRoom ì¡°ê±´ ?•ì¸
        function.findAnnotation<AllowedRoom>()?.let { annotation ->
            if (!annotation.rooms.contains(context.room.name)) {
                context.reply("??ë°©ì—?œëŠ” ?¬ìš©?????†ëŠ” ê¸°ëŠ¥?…ë‹ˆ??")
                return false
            }
        }
        
        return true
    }

    /**
     * ?ëŸ¬ ì²˜ë¦¬
     */
    private suspend fun handleError(errorContext: ErrorContext) {
        eventHandlers["error"]?.forEach { handler ->
            try {
                handler.invoke(errorContext)
            } catch (e: Exception) {
                logger.error("?ëŸ¬ ?¸ë“¤???¤í–‰ ì¤??¤ë¥˜ ë°œìƒ", e)
            }
        }
    }

    /**
     * ëª…ë ¹???¸ë“¤???´ë˜??     */
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
     * ?´ë²¤???¸ë“¤???´ë˜??     */
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
     * ë¶€?¸ìŠ¤?¸ë© ?¸ë“¤???´ë˜??     */
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
     * ?¤ì?ì¤??¸ë“¤???´ë˜??     */
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
