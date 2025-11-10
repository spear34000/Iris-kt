package com.spear.iriskt.examples

import com.spear.iriskt.core.*
import kotlinx.coroutines.runBlocking

/**
 * irispy-client 호환 방식 카카오링크 예제
 */
fun main() = runBlocking {
    val irisUrl = System.getenv("IRIS_URL") ?: error("IRIS_URL 환경 변수를 설정하세요")
    val appKey = System.getenv("KAKAOLINK_APP_KEY") ?: error("KAKAOLINK_APP_KEY 환경 변수를 설정하세요")
    val origin = System.getenv("KAKAOLINK_ORIGIN") ?: error("KAKAOLINK_ORIGIN 환경 변수를 설정하세요")

    val link = IrisLink(appKey, origin)

    try {
        // 초기화
        link.init()
        println("IrisLink 초기화 성공")

        // 메시지 전송
        link.send(
            receiverName = "내 채팅방",
            templateId = 12345,
            templateArgs = mapOf("key" to "value")
        )
        println("메시지 전송 성공")

    } catch (e: KakaoLinkReceiverNotFoundException) {
        println("받는 사람을 찾을 수 없습니다: ${e.message}")
    } catch (e: KakaoLinkLoginException) {
        println("로그인 실패: ${e.message}")
    } catch (e: KakaoLink2FAException) {
        println("2단계 인증이 필요합니다: ${e.message}")
    } catch (e: KakaoLinkSendException) {
        println("메시지 전송 실패: ${e.message}")
    } catch (e: KakaoLinkTemplateNotFoundException) {
        println("템플릿을 찾을 수 없습니다: ${e.message}")
    } catch (e: KakaoLinkInvalidTemplateArgsException) {
        println("잘못된 템플릿 인자: ${e.message}")
    } catch (e: KakaoLinkException) {
        println("카카오링크 오류: ${e.message}")
    } catch (e: Exception) {
        println("알 수 없는 오류: ${e.message}")
        e.printStackTrace()
    }
}
