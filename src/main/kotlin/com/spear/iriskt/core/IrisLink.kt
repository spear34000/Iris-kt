package com.spear.iriskt.core

import java.nio.charset.StandardCharsets
import java.net.URLEncoder

/**
 * ì¹´ì¹´?¤ë§??ê´€???ˆì™¸ ?´ë˜?¤ë“¤
 */
open class KakaoLinkException(message: String) : Exception(message)
open class KakaoLinkReceiverNotFoundException(message: String) : KakaoLinkException(message)
open class KakaoLinkLoginException(message: String) : KakaoLinkException(message)
open class KakaoLink2FAException(message: String) : KakaoLinkException(message)
open class KakaoLinkSendException(message: String) : KakaoLinkException(message)

/**
 * ì¹´ì¹´?¤ë§??ë©”ì‹œì§€ ?„ì†¡???„í•œ ?´ë˜??
 */
class IrisLink(
    private val defaultAppKey: String? = null,
    private val defaultOrigin: String? = null
) {
    private var isInitialized = false

    /**
     * ì´ˆê¸°??ë°?ë¡œê·¸??
     */
    suspend fun init(): Boolean {
        return try {
            // ?¤ì œë¡œëŠ” IRIS ?œë²„??ë¡œê·¸?¸í•´????
            isInitialized = true
            LoggerManager.defaultLogger.info("IrisLink ì´ˆê¸°???„ë£Œ")
            true
        } catch (e: Exception) {
            LoggerManager.defaultLogger.error("IrisLink ì´ˆê¸°???¤íŒ¨", e)
            false
        }
    }

    /**
     * ì¹´ì¹´?¤ë§??ë©”ì‹œì§€ ?„ì†¡
     */
    suspend fun send(
        receiverName: String,
        templateId: Int,
        templateArgs: Map<String, Any>,
        appKey: String? = null,
        origin: String? = null,
        searchExact: Boolean = true,
        searchFrom: SearchScope = SearchScope.ALL,
        searchRoomType: RoomType = RoomType.ALL
    ): Boolean {
        if (!isInitialized) {
            throw KakaoLinkException("IrisLinkê°€ ì´ˆê¸°?”ë˜ì§€ ?Šì•˜?µë‹ˆ?? ë¨¼ì? init()???¸ì¶œ?˜ì„¸??")
        }

        val actualAppKey = appKey ?: defaultAppKey
        val actualOrigin = origin ?: defaultOrigin

        if (actualAppKey == null || actualOrigin == null) {
            throw KakaoLinkException("app_key ?ëŠ” origin?€ ë¹„ì–´?ˆì„ ???†ìŠµ?ˆë‹¤")
        }

        return try {
            val encodedReceiver = URLEncoder.encode(receiverName, StandardCharsets.UTF_8)
            val encodedArgs = URLEncoder.encode(templateArgs.toString(), StandardCharsets.UTF_8)
            LoggerManager.defaultLogger.info(
                "ì¹´ì¹´?¤ë§???„ì†¡ ì¤€ë¹? receiver=$encodedReceiver, templateId=$templateId, args=$encodedArgs, " +
                    "searchExact=$searchExact, searchFrom=$searchFrom, searchRoomType=$searchRoomType, appKey=$actualAppKey, origin=$actualOrigin"
            )
            // ?¤ì œ êµ¬í˜„?ì„œ??HTTP ?”ì²­??ë³´ë‚´????
            true
        } catch (e: Exception) {
            LoggerManager.defaultLogger.error("ì¹´ì¹´?¤ë§???„ì†¡ ?¤íŒ¨", e)
            throw KakaoLinkSendException("?„ì†¡ ?¤íŒ¨: ${e.message}")
        }
    }

    /**
     * ì´ˆê¸°???íƒœ ?•ì¸
     */
    fun isReady(): Boolean = isInitialized
}

/**
 * ê²€??ë²”ìœ„
 */
enum class SearchScope {
    ALL,
    FRIENDS,
    CHATROOMS
}

/**
 * ë°??€??
 */
enum class RoomType {
    ALL, OpenMultiChat, MultiChat, DirectChat
}
