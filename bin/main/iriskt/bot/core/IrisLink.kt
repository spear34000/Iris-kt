package iriskt.bot.core

import java.nio.charset.StandardCharsets
import java.net.URLEncoder

/**
 * 카카오링크 관련 예외 클래스들
 */
open class KakaoLinkException(message: String) : Exception(message)
open class KakaoLinkReceiverNotFoundException(message: String) : KakaoLinkException(message)
open class KakaoLinkLoginException(message: String) : KakaoLinkException(message)
open class KakaoLink2FAException(message: String) : KakaoLinkException(message)
open class KakaoLinkSendException(message: String) : KakaoLinkException(message)

/**
 * 카카오링크 메시지 전송을 위한 클래스
 */
class IrisLink(
    private val defaultAppKey: String? = null,
    private val defaultOrigin: String? = null
) {
    private var isInitialized = false

    /**
     * 초기화 및 로그인
     */
    suspend fun init(): Boolean {
        return try {
            // 실제로는 IRIS 서버에 로그인해야 함
            isInitialized = true
            LoggerManager.defaultLogger.info("IrisLink 초기화 완료")
            true
        } catch (e: Exception) {
            LoggerManager.defaultLogger.error("IrisLink 초기화 실패", e)
            false
        }
    }

    /**
     * 카카오링크 메시지 전송
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
            throw KakaoLinkException("IrisLink가 초기화되지 않았습니다. 먼저 init()을 호출하세요.")
        }

        val actualAppKey = appKey ?: defaultAppKey
        val actualOrigin = origin ?: defaultOrigin

        if (actualAppKey == null || actualOrigin == null) {
            throw KakaoLinkException("app_key 또는 origin은 비어있을 수 없습니다")
        }

        return try {
            val encodedReceiver = URLEncoder.encode(receiverName, StandardCharsets.UTF_8)
            val encodedArgs = URLEncoder.encode(templateArgs.toString(), StandardCharsets.UTF_8)
            LoggerManager.defaultLogger.info("카카오링크 전송: $receiverName <- $templateArgs")
            // 실제 구현에서는 HTTP 요청을 보내야 함
            true
        } catch (e: Exception) {
            LoggerManager.defaultLogger.error("카카오링크 전송 실패", e)
            throw KakaoLinkSendException("전송 실패: ${e.message}")
        }
    }

    /**
     * 초기화 상태 확인
     */
    fun isReady(): Boolean = isInitialized
}

/**
 * 검색 범위
 */
enum class SearchScope {
    ALL,
    FRIENDS,
    CHATROOMS
}

/**
 * 방 타입
 */
enum class RoomType {
    ALL, OpenMultiChat, MultiChat, DirectChat
}
