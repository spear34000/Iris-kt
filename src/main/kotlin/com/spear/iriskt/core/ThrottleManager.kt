package com.spear.iriskt.core

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * 명령어 실행 빈도 제한을 관리하는 클래스 (최적화 버전)
 */
class ThrottleManager {
    // ConcurrentHashMap 사용으로 Mutex 제거 (성능 향상)
    private val throttleData = ConcurrentHashMap<String, ThrottleInfo>()

    /**
     * 특정 키에 대한 실행 허용 여부 확인
     * @param key 고유 키 (일반적으로 "userId_commandName")
     * @param maxCalls 시간 창 내 최대 호출 횟수
     * @param timeWindowMs 시간 창 (밀리초)
     * @return 실행 허용 여부
     */
    fun isAllowed(key: String, maxCalls: Int, timeWindowMs: Long): Boolean {
        val now = System.currentTimeMillis()
        val info = throttleData.computeIfAbsent(key) { ThrottleInfo() }

        synchronized(info) {
            // 시간 창을 벗어난 호출 기록 제거
            info.callTimes.removeIf { it < now - timeWindowMs }

            return if (info.callTimes.size < maxCalls) {
                info.callTimes.add(now)
                info.count.incrementAndGet()
                true
            } else {
                false
            }
        }
    }

    /**
     * 특정 사용자의 특정 명령어에 대한 스로틀 해제
     * @param userId 사용자 ID
     * @param commandName 명령어 이름
     */
    fun clearUserThrottle(userId: Long, commandName: String) {
        val key = "${userId}_$commandName"
        throttleData.remove(key)
    }

    /**
     * 특정 명령어에 대한 모든 사용자의 스로틀 해제
     * @param commandName 명령어 이름
     */
    fun clearAllThrottle(commandName: String) {
        val suffix = "_$commandName"
        throttleData.keys.removeIf { it.endsWith(suffix) }
    }

    /**
     * 모든 스로틀 데이터 초기화
     */
    fun clearAll() {
        throttleData.clear()
    }

    /**
     * 스로틀 정보 조회
     * @param key 고유 키
     * @return 스로틀 정보 (없으면 null)
     */
    fun getThrottleInfo(key: String): ThrottleInfo? {
        return throttleData[key]?.copy()
    }
    
    /**
     * 만료된 스로틀 데이터 정리 (메모리 최적화)
     */
    fun cleanup(timeWindowMs: Long = 3600000) {
        val now = System.currentTimeMillis()
        throttleData.entries.removeIf { (_, info) ->
            synchronized(info) {
                info.callTimes.removeIf { it < now - timeWindowMs }
                info.callTimes.isEmpty()
            }
        }
    }

    data class ThrottleInfo(
        val callTimes: MutableList<Long> = mutableListOf(),
        val count: AtomicInteger = AtomicInteger(0)
    ) {
        fun copy(): ThrottleInfo = synchronized(this) {
            ThrottleInfo(callTimes.toMutableList(), AtomicInteger(count.get()))
        }
    }
}
