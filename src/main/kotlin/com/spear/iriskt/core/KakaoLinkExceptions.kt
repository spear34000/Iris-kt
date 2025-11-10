package com.spear.iriskt.core

/**
 * KakaoLink 관련 예외 클래스들
 */

/**
 * KakaoLink 일반 예외
 */
open class KakaoLinkException(message: String, cause: Throwable? = null) : Exception(message, cause)

/**
 * 받는 사람을 찾을 수 없을 때 발생하는 예외
 */
class KakaoLinkReceiverNotFoundException(receiverName: String) :
    KakaoLinkException("받는 사람을 찾을 수 없습니다: $receiverName")

/**
 * 로그인 실패 시 발생하는 예외
 */
class KakaoLinkLoginException(message: String, cause: Throwable? = null) :
    KakaoLinkException("로그인 실패: $message", cause)

/**
 * 2단계 인증 필요 시 발생하는 예외
 */
class KakaoLink2FAException(message: String = "2단계 인증이 필요합니다") :
    KakaoLinkException(message)

/**
 * 메시지 전송 실패 시 발생하는 예외
 */
class KakaoLinkSendException(message: String, cause: Throwable? = null) :
    KakaoLinkException("메시지 전송 실패: $message", cause)

/**
 * 템플릿을 찾을 수 없을 때 발생하는 예외
 */
class KakaoLinkTemplateNotFoundException(templateId: Int) :
    KakaoLinkException("템플릿을 찾을 수 없습니다: $templateId")

/**
 * 잘못된 템플릿 인자 시 발생하는 예외
 */
class KakaoLinkInvalidTemplateArgsException(message: String) :
    KakaoLinkException("잘못된 템플릿 인자: $message")
