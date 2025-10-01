# IRIS Kotlin Bot 

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.24-blue.svg)](https://kotlinlang.org)
[![Gradle](https://img.shields.io/badge/Gradle-8.9-green.svg)](https://gradle.org)
[![Ktor](https://img.shields.io/badge/Ktor-2.3.9-purple.svg)](https://ktor.io)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

**Python irispy-client를 기반으로 한 Kotlin 카카오톡 봇 개발 라이브러리**

>  **참고**: 이 프로젝트는 [irispy-client](https://github.com/dolidolih/irispy-client)의 Kotlin 포트입니다.

##  목차

- [특징](#특징)
- [요구사항](#요구사항)
- [설치](#설치)
- [빠른 시작](#빠른-시작)
- [API 문서](#api-문서)
- [예제](#예제)
- [기여](#기여)
- [라이선스](#라이선스)

##  특징

-  **고성능**: Kotlin 코루틴 기반 비동기 처리
-  **타입 안전성**: Kotlin의 강력한 타입 시스템 활용
-  **간편한 사용법**: 직관적인 API와 어노테이션 기반 명령어 처리
-  **실제 카카오 연동**: 실제 irispy-client와 동일한 KakaoLink 기능
-  **다양한 기능**: 메시지 처리, 이미지 전송, 스케줄링 등
-  **안전성**: 적절한 예외 처리 및 에러 복구

##  요구사항

- **Java**: 17 이상
- **Kotlin**: 1.9.0 이상
- **Kotlin**: 1.9.24
- **Gradle**: 8.9 이상

### 프로젝트 설정

1. `build.gradle.kts`에 의존성 추가:
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

2. 환경 변수 설정:
```bash
# PowerShell
$env:IRIS_ENDPOINT = "ws://your-iris-server-url"

# 또는 Linux/macOS
export IRIS_ENDPOINT="ws://your-iris-server-url"
```

## 🚀 빠른 시작

### 기본 사용법

```kotlin
import iriskt.bot.Bot
import iriskt.bot.models.ChatContext

fun main() {
    val bot = Bot("MyBot", System.getenv("IRIS_ENDPOINT"))

    // 메시지 이벤트 핸들러
    bot.onEvent("message") { payload ->
        if (payload is ChatContext) {
            println("[${payload.room.name}] ${payload.sender.name}: ${payload.message.text}")

            // 명령어 처리
            when (payload.message.command) {
                "안녕" -> payload.reply("안녕하세요!")
                "도움말" -> payload.reply("사용 가능한 명령어: 안녕, 도움말")
            }
        }
    }

    // 봇 실행
    bot.run()
}
```

### 고급 사용법

```kotlin
import iriskt.bot.Bot
import iriskt.bot.core.BatchScheduler
import iriskt.bot.models.ChatContext
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val bot = Bot(
        botName = "AdvancedBot",
        irisUrl = System.getenv("IRIS_ENDPOINT"),
        options = BotOptions(
            maxWorkers = 8,
            bannedUsers = setOf(123456789L, 987654321L),
            kakaoLinkAppKey = "your_kakao_app_key"
        )
    )

    // 스케줄러 설정
    val scheduler = bot.getScheduler()
    scheduler.scheduleMessage(
        id = "reminder",
        roomId = 12345L,
        message = "회의 시작입니다!",
        delayMillis = 300000 // 5분 후
    )

    // 이벤트 핸들러 등록
    bot.onEvent("chat") { payload ->
        if (payload is ChatContext) {
            handleMessage(payload)
        }
    }

    bot.run()
}

suspend fun handleMessage(context: ChatContext) {
    val command = context.message.command
    val param = context.message.param

    when (command) {
        "안녕" -> context.reply("안녕하세요! ${context.sender.name}님!")
        "시간" -> context.reply("현재 시간: ${java.time.LocalTime.now()}")
        "정보" -> {
            val userType = context.sender.getType()
            val roomType = context.room.getType()
            context.reply("사용자: ${context.sender.name} ($userType), 방: ${context.room.name} ($roomType)")
        }
        else -> {
            if (command.isNotEmpty()) {
                context.reply("❓ '$command' 명령어를 찾을 수 없습니다.")
            }
        }
    }
}
```

## 📋 주요 기능

### 🎮 이벤트 시스템

| 이벤트명 | 설명 | 사용 시기 |
|---------|------|-----------|
| `chat` | 모든 채팅 메시지 | 모든 메시지를 처리할 때 |
| `message` | 일반 텍스트 메시지 | 텍스트 메시지만 처리할 때 |
| `new_member` | 새 멤버 참여 | 멤버 참여 알림 등 |
| `del_member` | 멤버 퇴장 | 멤버 퇴장 처리 등 |
| `error` | 오류 발생 | 예외 처리 및 로그 기록 |

### 🔧 명령어 처리

Message 클래스에는 편리한 명령어 처리 기능이 포함되어 있습니다:

```kotlin
// 메시지에서 명령어 추출
val command = context.message.command  // 첫 번째 단어
val param = context.message.param      // 나머지 부분
val hasParam = context.message.hasParam // 매개변수 존재 여부
```

### 👤 사용자 권한 시스템

```kotlin
// 사용자 권한 조회
val userType = context.sender.getType()  // "HOST", "MANAGER", "NORMAL", "BOT"

// 권한별 처리
when (userType) {
    "HOST", "MANAGER" -> {
        // 관리자 명령어 처리
    }
    else -> {
        // 일반 사용자 명령어 처리
    }
}
```

### 🏠 방 관리

```kotlin
// 방 타입 조회
val roomType = context.room.getType()  // "NORMAL", "OPEN", etc.

// 방별 처리
when (roomType) {
    "OPEN" -> {
        // 공개 채팅방 처리
    }
    else -> {
        // 일반 채팅방 처리
    }
}
```

## ⚙️ 설정 옵션

Bot 생성 시 다양한 옵션을 설정할 수 있습니다:

```kotlin
val bot = Bot(
    botName = "MyBot",
    irisUrl = "ws://localhost:8080",
    options = BotOptions(
        maxWorkers = 4,              // 최대 워커 스레드 수
        bannedUsers = setOf(123L),   // 차단 사용자 목록
        kakaoLinkAppKey = "key",      // 카카오링크 앱 키
        kakaoLinkOrigin = "origin"    // 카카오링크 도메인
    )
)
```

## 🔗 API 참조

### Bot 클래스

```kotlin
class Bot(
    botName: String,
    irisUrl: String,
    options: BotOptions = BotOptions()
)
```

**메서드:**
- `onEvent(name: String, handler: suspend (Any) -> Unit)`: 이벤트 핸들러 등록
- `run()`: 봇 실행
- `close()`: 봇 종료
- `api()`: IrisApiClient 인스턴스 반환
- `getScheduler()`: BatchScheduler 인스턴스 반환
- `getIrisLink()`: IrisLink 인스턴스 반환
- `isBannedUser(userId: Long)`: 사용자 차단 여부 확인

### IrisApiClient 클래스

```kotlin
class IrisApiClient(
    baseUrl: String,
    client: HttpClient,
    json: Json
)
```

**메서드:**
- `reply(roomId: Long, message: String)`: 메시지 답장
- `replyImage(roomId: Long, files: Collection<ByteArray>)`: 이미지 답장
- `decrypt(enc: Int, ciphertext: String, userId: Long)`: 메시지 복호화
- `query(statement: String, bind: List<JsonElement>? = null)`: 데이터베이스 쿼리
- `getInfo()`: 서버 정보 조회

## 📚 고급 예제

### 이미지 처리

```kotlin
bot.onEvent("message") { payload ->
    if (payload is ChatContext) {
        // 이미지 메시지 확인
        context.message.image?.let { image ->
            context.reply("📸 이미지를 받았습니다! ${image.urls.size}개의 이미지")
        }
    }
}
```

### 스케줄링

```kotlin
val scheduler = bot.getScheduler()

// 1시간 후 메시지 예약
scheduler.scheduleMessage(
    id = "reminder",
    roomId = 12345L,
    message = "회의 시작입니다!",
    delayMillis = 3600000 // 1시간
)

// 특정 시간에 메시지 예약
scheduler.scheduleMessageAt(
    id = "daily",
    roomId = 12345L,
    message = "좋은 아침입니다!",
    scheduledTime = getNextMorningTime()
)
```

### 카카오링크 활용

```kotlin
val bot = Bot("MyBot", endpoint, BotOptions(kakaoLinkAppKey = "your_key"))

bot.onEvent("message") { payload ->
    if (payload is ChatContext && payload.message.command == "링크") {
        val irisLink = bot.getIrisLink()

        irisLink.send(
            receiverName = payload.sender.name,
            templateId = 12345,
            templateArgs = mapOf("message" to payload.message.param)
        )
    }
}
```

## 🚨 오류 처리

### 일반적인 오류들

```kotlin
bot.onEvent("error") { payload ->
    if (payload is ErrorContext) {
        logger.error("봇 오류: ${payload.exception.message}", payload.exception)
    }
}
```

### 문제 해결 가이드

**빌드 오류:**
```bash
# Gradle Wrapper 생성
gradle wrapper --gradle-version 8.9

# 의존성 새로고침
./gradlew.bat dependencies --refresh-dependencies
```

**실행 오류:**
- 환경 변수 `IRIS_ENDPOINT`가 설정되었는지 확인
- IRIS 서버가 실행 중인지 확인
- 네트워크 연결 상태 확인

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 제공됩니다.

## 👥 기여

기여를 환영합니다! 이슈를 등록하거나 Pull Request를 보내주세요.

---

**기반**: [irispy-client](https://github.com/irisdev/irispy-client) 프로젝트에서 포팅되었습니다.

1. `build.gradle.kts`에 의존성 추가:
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

2. 환경 변수 설정:
```bash
# PowerShell
$env:IRIS_ENDPOINT = "ws://your-iris-server-url"

# 또는 Linux/macOS
export IRIS_ENDPOINT="ws://your-iris-server-url"
```

## 🚀 빠른 시작

### 기본 사용법

```kotlin
import iriskt.bot.Bot
import iriskt.bot.models.ChatContext

fun main() {
    val bot = Bot("MyBot", System.getenv("IRIS_ENDPOINT"))

    // 메시지 이벤트 핸들러
    bot.onEvent("message") { payload ->
        if (payload is ChatContext) {
            println("[${payload.room.name}] ${payload.sender.name}: ${payload.message.text}")

            // 명령어 처리
            when (payload.message.command) {
                "안녕" -> payload.reply("안녕하세요!")
                "도움말" -> payload.reply("사용 가능한 명령어: 안녕, 도움말")
            }
        }
    }

    // 봇 실행
    bot.run()
}
```

### 고급 사용법

```kotlin
import iriskt.bot.Bot
import iriskt.bot.core.BatchScheduler
import iriskt.bot.models.ChatContext
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val bot = Bot(
        botName = "AdvancedBot",
        irisUrl = System.getenv("IRIS_ENDPOINT"),
        options = BotOptions(
            maxWorkers = 8,
            bannedUsers = setOf(123456789L, 987654321L),
            kakaoLinkAppKey = "your_kakao_app_key"
        )
    )

    // 스케줄러 설정
    val scheduler = bot.getScheduler()
    scheduler.scheduleMessage(
        id = "reminder",
        roomId = 12345L,
        message = "회의 시작입니다!",
        delayMillis = 300000 // 5분 후
    )

    // 이벤트 핸들러 등록
    bot.onEvent("chat") { payload ->
        if (payload is ChatContext) {
            handleMessage(payload)
        }
    }

    bot.run()
}

suspend fun handleMessage(context: ChatContext) {
    val command = context.message.command
    val param = context.message.param

    when (command) {
        "안녕" -> context.reply("안녕하세요! ${context.sender.name}님!")
        "시간" -> context.reply("현재 시간: ${java.time.LocalTime.now()}")
        "정보" -> {
            val userType = context.sender.getType()
            val roomType = context.room.getType()
            context.reply("사용자: ${context.sender.name} ($userType), 방: ${context.room.name} ($roomType)")
        }
        else -> {
            if (command.isNotEmpty()) {
                context.reply("❓ '$command' 명령어를 찾을 수 없습니다.")
            }
        }
    }
}
```

## 📋 주요 기능

### 🎮 이벤트 시스템

| 이벤트명 | 설명 | 사용 시기 |
|---------|------|-----------|
| `chat` | 모든 채팅 메시지 | 모든 메시지를 처리할 때 |
| `message` | 일반 텍스트 메시지 | 텍스트 메시지만 처리할 때 |
| `new_member` | 새 멤버 참여 | 멤버 참여 알림 등 |
| `del_member` | 멤버 퇴장 | 멤버 퇴장 처리 등 |
| `error` | 오류 발생 | 예외 처리 및 로그 기록 |

### 🔧 명령어 처리

Message 클래스에는 편리한 명령어 처리 기능이 포함되어 있습니다:

```kotlin
// 메시지에서 명령어 추출
val command = context.message.command  // 첫 번째 단어
val param = context.message.param      // 나머지 부분
val hasParam = context.message.hasParam // 매개변수 존재 여부
```

### 👤 사용자 권한 시스템

```kotlin
// 사용자 권한 조회
val userType = context.sender.getType()  // "HOST", "MANAGER", "NORMAL", "BOT"

// 권한별 처리
when (userType) {
    "HOST", "MANAGER" -> {
        // 관리자 명령어 처리
    }
    else -> {
        // 일반 사용자 명령어 처리
    }
}
```

### 🏠 방 관리

```kotlin
// 방 타입 조회
val roomType = context.room.getType()  // "NORMAL", "OPEN", etc.

// 방별 처리
when (roomType) {
    "OPEN" -> {
        // 공개 채팅방 처리
    }
    else -> {
        // 일반 채팅방 처리
    }
}
```

## ⚙️ 설정 옵션

Bot 생성 시 다양한 옵션을 설정할 수 있습니다:

```kotlin
val bot = Bot(
    botName = "MyBot",
    irisUrl = "ws://localhost:8080",
    options = BotOptions(
        maxWorkers = 4,              // 최대 워커 스레드 수
        bannedUsers = setOf(123L),   // 차단 사용자 목록
        kakaoLinkAppKey = "key",      // 카카오링크 앱 키
        kakaoLinkOrigin = "origin"    // 카카오링크 도메인
    )
)
```

## 🔗 API 참조

### Bot 클래스

```kotlin
class Bot(
    botName: String,
    irisUrl: String,
    options: BotOptions = BotOptions()
)
```

**메서드:**
- `onEvent(name: String, handler: suspend (Any) -> Unit)`: 이벤트 핸들러 등록
- `run()`: 봇 실행
- `close()`: 봇 종료
- `api()`: IrisApiClient 인스턴스 반환
- `getScheduler()`: BatchScheduler 인스턴스 반환
- `getIrisLink()`: IrisLink 인스턴스 반환
- `isBannedUser(userId: Long)`: 사용자 차단 여부 확인

### IrisApiClient 클래스

```kotlin
class IrisApiClient(
    baseUrl: String,
    client: HttpClient,
    json: Json
)
```

**메서드:**
- `reply(roomId: Long, message: String)`: 메시지 답장
- `replyImage(roomId: Long, files: Collection<ByteArray>)`: 이미지 답장
- `decrypt(enc: Int, ciphertext: String, userId: Long)`: 메시지 복호화
- `query(statement: String, bind: List<JsonElement>? = null)`: 데이터베이스 쿼리
- `getInfo()`: 서버 정보 조회

## 📚 고급 예제

### 이미지 처리

```kotlin
bot.onEvent("message") { payload ->
    if (payload is ChatContext) {
        // 이미지 메시지 확인
        context.message.image?.let { image ->
            context.reply("📸 이미지를 받았습니다! ${image.urls.size}개의 이미지")
        }
    }
}
```

### 스케줄링

```kotlin
val scheduler = bot.getScheduler()

// 1시간 후 메시지 예약
scheduler.scheduleMessage(
    id = "reminder",
    roomId = 12345L,
    message = "회의 시작입니다!",
    delayMillis = 3600000 // 1시간
)

// 특정 시간에 메시지 예약
scheduler.scheduleMessageAt(
    id = "daily",
    roomId = 12345L,
    message = "좋은 아침입니다!",
    scheduledTime = getNextMorningTime()
)
```

### 카카오링크 활용

```kotlin
val bot = Bot("MyBot", endpoint, BotOptions(kakaoLinkAppKey = "your_key"))

bot.onEvent("message") { payload ->
    if (payload is ChatContext && payload.message.command == "링크") {
        val irisLink = bot.getIrisLink()

        irisLink.send(
            receiverName = payload.sender.name,
            templateId = 12345,
            templateArgs = mapOf("message" to payload.message.param)
        )
    }
}
```

## 🚨 오류 처리

### 일반적인 오류들

```kotlin
bot.onEvent("error") { payload ->
    if (payload is ErrorContext) {
        logger.error("봇 오류: ${payload.exception.message}", payload.exception)
    }
}
```

### 문제 해결 가이드

**빌드 오류:**
```bash
# Gradle Wrapper 생성
gradle wrapper --gradle-version 8.9

# 의존성 새로고침
./gradlew.bat dependencies --refresh-dependencies
```

**실행 오류:**
- 환경 변수 `IRIS_ENDPOINT`가 설정되었는지 확인
- IRIS 서버가 실행 중인지 확인
- 네트워크 연결 상태 확인

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 제공됩니다.

## 👥 기여

기여를 환영합니다! 이슈를 등록하거나 Pull Request를 보내주세요.

---

**기반**: [irispy-client](https://github.com/irisdev/irispy-client) 프로젝트에서 포팅되었습니다.
