# IRIS Kotlin Bot

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.24-blue.svg)](https://kotlinlang.org)
[![Gradle](https://img.shields.io/badge/Gradle-8.9-green.svg)](https://gradle.org)
[![Ktor](https://img.shields.io/badge/Ktor-2.3.9-purple.svg)](https://ktor.io)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Python `irispy-client`를 Kotlin으로 이식한 고성능 카카오톡 봇 개발 라이브러리입니다. 코루틴 기반 비동기 처리와 직관적인 API로 안정적인 자동화를 구축할 수 있습니다.

---

## 📑 목차

- [핵심 특징](#-핵심-특징)
- [시스템 요구사항](#-시스템-요구사항)
- [설치 및 설정](#-설치-및-설정)
- [JitPack 연동](#-jitpack-연동)
- [빠른 시작](#-빠른-시작)
- [주요 컴포넌트 개요](#-주요-컴포넌트-개요)
- [고급 기능 활용](#-고급-기능-활용)
- [문제 해결 가이드](#-문제-해결-가이드)
- [기여 방법](#-기여-방법)
- [라이선스](#-라이선스)

---

## ⚡ 핵심 특징

- **고성능 비동기 처리**: `kotlinx.coroutines` 기반 병렬 이벤트 처리.
- **타입 안정성**: `kotlinx.serialization`과 구조화된 모델(`ChatContext`, `Message` 등) 제공.
- **유연한 명령어 시스템**: 어노테이션 기반 검증(`@HasParam`, `@HasRole`, `@Throttle` 등) 지원.
- **카카오링크 지원**: `IrisLink`로 템플릿 메시지 전송 및 예외 처리.
- **스케줄링**: `BatchScheduler`로 예약 메시지/반복 작업 수행.
- **클린 로깅**: `LoggerManager`와 `kotlin-logging`으로 일관된 로깅.

---

## 🔧 시스템 요구사항

- **JDK**: 17 이상
- **Kotlin**: 1.9.24
- **Gradle**: 8.9 (Wrapper 포함)
- **네트워크**: IRIS 서버(WebSocket) 연결 가능 환경

---

## 🛠 설치 및 설정

### 1. 저장소 클론

```bash
git clone https://github.com/사용자/irisKt.git
cd irisKt
```

### 2. 의존성 확인

`build.gradle.kts`는 주요 라이브러리를 이미 포함하고 있습니다.

```kotlin
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("io.ktor:ktor-client-core:2.3.9")
    implementation("io.ktor:ktor-client-cio:2.3.9")
    implementation("io.ktor:ktor-client-websockets:2.3.9")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.9")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.9")
    implementation("io.github.microutils:kotlin-logging:3.0.5")
    implementation("org.slf4j:slf4j-simple:2.0.13")
}

kotlin {
    jvmToolchain(17)
}
```

### 3. 환경 변수 설정

```powershell
$env:IRIS_ENDPOINT = "ws://your-iris-server-url"
```

```bash
export IRIS_ENDPOINT="ws://your-iris-server-url"
```

### 4. 빌드

```bash
./gradlew.bat build
```

---

## 🔄 JitPack 연동

> ⚠️ **GitHub 저장소를 공개(Public)로 전환**해야 JitPack에서 빌드할 수 있습니다.

### 1. JitPack 활성화

- 저장소가 GitHub에 올라간 상태에서 [JitPack](https://jitpack.io)에 로그인 후 프로젝트를 검색합니다.
- 최초 빌드를 실행하면 JitPack이 Gradle 프로젝트를 분석하고 결과를 제공합니다.
- JitPack URL은 `https://jitpack.io/#<GitHub_사용자>/<레포지토리>` 형식입니다.

### 2. Gradle 설정 (Kotlin DSL)

`settings.gradle.kts` 혹은 하위 프로젝트 `build.gradle.kts`에 JitPack 저장소를 추가합니다.

```kotlin
dependencyResolutionManagement {
    repositories {
        maven("https://jitpack.io")
    }
}
```

라이브러리 의존성 선언은 다음과 같습니다. (예: `1.0.0` 태그를 배포한 경우)

```kotlin
dependencies {
    implementation("com.github.<GitHub_사용자>:irisKt:1.0.0")
}
```

> ❗ `<GitHub_사용자>`와 버전(`1.0.0`)은 실제 사용자명과 릴리스 태그로 교체하세요.

### 3. 새 버전 배포 절차

1. `build.gradle.kts`와 소스를 커밋 후 GitHub에 푸시합니다.
2. `git tag v1.0.0 && git push origin v1.0.0`처럼 **태그**를 푸시합니다.
3. JitPack 사이트에서 해당 버전을 선택해 빌드를 트리거합니다.
4. 빌드가 성공하면 위 `implementation` 좌표로 바로 사용할 수 있습니다.

> 💡 릴리스 로그를 `README`나 GitHub Releases에 정리하면 사용자 혼선을 줄일 수 있습니다.

---

## 🚀 빠른 시작

### 최소 예제

```kotlin
import iriskt.bot.Bot
import iriskt.bot.models.ChatContext

suspend fun main() {
    val endpoint = System.getenv("IRIS_ENDPOINT") ?: error("IRIS_ENDPOINT 환경 변수를 설정하세요")
    Bot(botName = "QuickBot", irisUrl = endpoint).use { bot ->
        bot.onEvent("chat") { payload ->
            if (payload is ChatContext) {
                println("[${payload.room.name}] ${payload.sender.name}: ${payload.message.text}")
                if (payload.message.command == "안녕") {
                    payload.reply("안녕하세요! 😊")
                }
            }
        }
        bot.run()
    }
}
```

### 옵션 활용 예제

```kotlin
import iriskt.bot.Bot
import iriskt.bot.BotOptions
import iriskt.bot.models.ChatContext
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val endpoint = System.getenv("IRIS_ENDPOINT") ?: error("IRIS_ENDPOINT 환경 변수를 설정하세요")
    val bot = Bot(
        botName = "AdvancedBot",
        irisUrl = endpoint,
        options = BotOptions(
            maxWorkers = 8,
            bannedUsers = setOf(123456789L, 987654321L),
            kakaoLinkAppKey = "your-kakao-app-key",
            kakaoLinkOrigin = "https://your-service.com"
        )
    )

    bot.onEvent("chat") { payload ->
        if (payload is ChatContext) {
            when (payload.message.command) {
                "도움말" -> payload.reply("사용 가능한 명령어: 안녕, 시간, 링크")
                "시간" -> payload.reply("현재 시각: ${java.time.LocalDateTime.now()}")
            }
        }
    }

    bot.run()
}
```

---

## 🧩 주요 컴포넌트 개요

- **`iriskt.bot.Bot`**: 웹소켓 이벤트 수신, 핸들러 등록, API/스케줄러 접근 제공.
- **`iriskt.bot.api.IrisApiClient`**: REST API 호출(`reply`, `replyImage`, `query`, `decrypt`).
- **`iriskt.bot.core.IrisLink`**: KakaoLink 템플릿 전송 및 예외(`KakaoLinkException` 계열) 처리.
- **`iriskt.bot.core.BatchScheduler`**: 예약 메시지 작업 (`scheduleMessage`, `scheduleMessageAt`).
- **`iriskt.bot.models.ChatContext`**: 메시지/사용자/방 정보와 응답 메서드 제공.
- **`iriskt.bot.internal.EventEmitter`**: 비동기 이벤트 디스패치, 에러 전파.
- **어노테이션**: `@HasParam`, `@IsAdmin`, `@HasRole`, `@Throttle`, `@IsReply`, `@IsNotBanned`, `@AllowedRoom`.

---

## 🔍 고급 기능 활용

### 1. 메시지 메타데이터 활용

```kotlin
bot.onEvent("chat") { payload ->
    if (payload is ChatContext) {
        payload.message.metadata?.let { meta ->
            println("수신 메타데이터: $meta")
        }
    }
}
```

### 2. 예약 메시지

```kotlin
val scheduler = bot.getScheduler()
scheduler.scheduleMessage(
    id = "meeting-reminder",
    roomId = 10001L,
    message = "10분 후 회의가 시작됩니다.",
    delayMillis = 600000
)
```

### 3. KakaoLink 템플릿 전송

```kotlin
val irisLink = bot.getIrisLink()
irisLink.send(
    receiverName = "홍길동",
    templateId = 12345,
    templateArgs = mapOf("message" to "IRIS Bot에서 전송한 링크입니다")
)
```

---

## 🛡 문제 해결 가이드

- **빌드 오류**: `./gradlew.bat clean build` 실행 후 발생 로그 확인.
- **실행 오류**: IRIS 서버 주소와 네트워크 연결, 인증 정보를 재검토.
- **웹소켓 연결 반복 종료**: 방화벽, SSL 설정, 서버 로그를 점검.
- **KakaoLink 실패**: `kakaoLinkAppKey`, `kakaoLinkOrigin`, 템플릿 매핑을 다시 확인.

---

## 🤝 기여 방법

- 이슈를 등록할 때는 재현 절차와 로그를 함께 제공해주세요.
- Pull Request는 테스트 결과와 변경 이유를 상세히 작성해주세요.
- 새로운 기능 제안은 Discussions 탭을 통해 논의 후 진행하면 효율적입니다.

---

## 🪪 라이선스

이 프로젝트는 [MIT License](LICENSE) 하에 배포됩니다.

> 기반 프로젝트: [irispy-client](https://github.com/irisdev/irispy-client)
