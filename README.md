# IRIS Kotlin Bot

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.24-blue.svg)](https://kotlinlang.org)
[![Gradle](https://img.shields.io/badge/Gradle-8.9-green.svg)](https://gradle.org)
[![Ktor](https://img.shields.io/badge/Ktor-2.3.9-purple.svg)](https://ktor.io)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Python `irispy-client`를 Kotlin으로 이식한 고성능 카카오톡 봇 개발 라이브러리입니다. 코루틴 기반 비동기 처리와 직관적인 API로 안정적인 자동화를 구축할 수 있습니다.

> 🎉 **node-iris 100% 호환**: 모든 기능 완벽 구현 - [기능 완성 보고서](./FEATURES_COMPLETE.md)  
> 🚀 **최적화 완료**: 2.5배 빠른 처리 속도, 50% 메모리 절감 - [최적화 요약 보기](./OPTIMIZATION_SUMMARY.md)  
> 🧹 **코드 정리 완료**: 11개 불필요한 파일 삭제, 100MB+ 절감 - [정리 요약 보기](./CLEANUP_SUMMARY.md)

---

## 📑 목차

- [핵심 특징](#-핵심-특징)
- [시스템 요구사항](#-시스템-요구사항)
- [설치 및 설정](#-설치-및-설정)
- [JitPack 연동](#-jitpack-연동)
- [빠른 시작](#-빠른-시작)
- [API 참조](#-api-참조)
- [어노테이션](#-어노테이션)
- [유틸리티 함수](#-유틸리티-함수)
- [주요 컴포넌트 개요](#-주요-컴포넌트-개요)
- [고급 기능 활용](#-고급-기능-활용)
- [문제 해결 가이드](#-문제-해결-가이드)
- [문서](#-문서)
- [기여 방법](#-기여-방법)
- [라이선스](#-라이선스)

---

## ⚡ 핵심 특징

- **node-iris 100% 호환**: TypeScript node-iris의 모든 기능 완벽 구현
- **고성능 비동기 처리**: `kotlinx.coroutines` 기반 병렬 이벤트 처리
- **최적화된 성능**: HTTP 클라이언트 재사용, 객체 캐싱, 효율적인 동기화로 2.5배 성능 향상
- **타입 안정성**: `kotlinx.serialization`과 구조화된 모델(`ChatContext`, `Message` 등) 제공
- **유연한 명령어 시스템**: 어노테이션 기반 검증(`@HasParam`, `@HasRole`, `@Throttle` 등) 지원
- **HTTP/Webhook 모드**: WebSocket과 HTTP 모드 모두 지원
- **채팅 로그 저장**: 자동 채팅 로그 저장 및 관리
- **카카오링크 지원**: `IrisLink`로 템플릿 메시지 전송 및 예외 처리
- **스케줄링**: `BatchScheduler`로 예약 메시지/반복 작업 수행
- **클린 로깅**: `LoggerManager`와 `kotlin-logging`으로 일관된 로깅
- **메모리 효율**: 객체 풀링, 캐싱, 지연 초기화로 메모리 사용량 50% 감소

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

> ✅ 이미 JitPack 연동을 완료했고 신규 사용자 안내가 불필요하다면 이 섹션은 건너뛰어도 됩니다.

### 1. JitPack 활성화

- 이 레포지토리는 이미 [JitPack](https://jitpack.io/#spear34000/Iris-kt)에 등록되어 있습니다.
- 새로운 태그를 배포했다면 JitPack 대시보드에서 해당 태그를 선택해 빌드를 트리거하면 됩니다.
- JitPack URL은 `https://jitpack.io/#spear34000/Iris-kt`입니다.

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
    implementation("com.github.spear34000:Iris-kt:1.0.0")
}
```

> ❗ 버전(`1.0.0`)은 실제로 배포한 태그(또는 `main-SNAPSHOT`)로 교체하세요.

### 3. 새 버전 배포 절차

1. `build.gradle.kts`와 소스를 커밋 후 GitHub에 푸시합니다.
2. `git tag v1.0.0 && git push origin v1.0.0`처럼 **태그**를 푸시합니다.
3. JitPack 사이트에서 해당 버전을 선택해 빌드를 트리거합니다.
4. 빌드가 성공하면 위 `implementation` 좌표로 바로 사용할 수 있습니다.

> 💡 릴리스 로그를 `README`나 GitHub Releases에 정리하면 사용자 혼선을 줄일 수 있습니다.

---

## 🚀 빠른 시작

> 본 라이브러리는 [@tsuki-chat/node-iris](https://github.com/Tsuki-Chat/node-iris)의 Kotlin 포팅 버전입니다.

### irispy-client 호환 방식

```kotlin
import com.spear.iriskt.Bot
import com.spear.iriskt.BotOptions
import com.spear.iriskt.models.ChatContext
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val irisUrl = System.getenv("IRIS_URL") ?: error("IRIS_URL 환경 변수를 설정하세요")
    
    val bot = Bot(
        botName = "SimpleBot",
        irisUrl = irisUrl,
        options = BotOptions(maxWorkers = 4)
    )

    // 이벤트 핸들러 등록
    // chat : 모든 메시지
    // message : 일반 메시지
    // new_member : 새 멤버 참여
    // del_member : 멤버 퇴장
    // unknown : 알 수 없는 이벤트
    // error : 오류 발생
    bot.onEvent("message") { payload ->
        if (payload is ChatContext) {
            when (payload.message.command) {
                "안녕" -> payload.reply("안녕하세요!")
                "시간" -> payload.reply("현재 시각: ${java.time.LocalDateTime.now()}")
            }
        }
    }

    bot.run()
}
```

### 컨트롤러 방식 (권장)

```kotlin
import com.spear.iriskt.Bot
import com.spear.iriskt.BotOptions
import com.spear.iriskt.LogLevel
import com.spear.iriskt.annotations.*
import com.spear.iriskt.core.IrisLink
import com.spear.iriskt.models.ChatContext
import kotlinx.coroutines.runBlocking

@MessageController
@Prefix("!")
class CustomMessageController {
    private var kakaoLink: IrisLink? = null

    init {
        val appKey = System.getenv("KAKAOLINK_APP_KEY")
        val origin = System.getenv("KAKAOLINK_ORIGIN")
        if (appKey != null && origin != null) {
            kakaoLink = IrisLink(appKey, origin)
        }
    }

    @BotCommand("안녕", "인사 명령어")
    suspend fun hello(context: ChatContext) {
        context.reply("안녕하세요!")
    }

    @BotCommand("카카오링크", "카카오링크 전송 테스트")
    suspend fun linkCommand(context: ChatContext) {
        kakaoLink?.send(
            receiverName = context.room.name,
            templateId = 123417,
            templateArgs = mapOf("TEXT" to "테스트")
        )
    }

    @BotCommand("반복", "메시지 반복")
    @HasParam
    suspend fun echo(context: ChatContext) {
        context.reply("반복: ${context.message.param}")
    }

    @BotCommand("관리자", "관리자 전용 명령어")
    @IsAdmin
    suspend fun adminOnly(context: ChatContext) {
        context.reply("관리자만 사용할 수 있는 명령어입니다!")
    }
}

@BatchController
class CustomBatchController {
    @Schedule(interval = 60000) // 1분마다
    suspend fun periodicTask() {
        println("주기적 작업 실행 중...")
    }
}

@FeedController
class CustomFeedController {
    @OnInviteUserFeed
    suspend fun onUserJoin(context: ChatContext) {
        context.reply("새로운 멤버를 환영합니다! 🎉")
    }

    @OnPromoteManagerFeed
    suspend fun onManagerPromote(context: ChatContext) {
        context.reply("새로운 관리자가 임명되었습니다! 👑")
    }
}

fun main() = runBlocking {
    val irisUrl = System.getenv("IRIS_URL") ?: error("IRIS_URL 환경 변수를 설정하세요")

    val bot = Bot(
        botName = "Node-Iris-Kt",
        irisUrl = irisUrl,
        options = BotOptions(
            maxWorkers = 8,
            logLevel = LogLevel.DEBUG,
            httpMode = false, // WebSocket 모드
            bannedUsers = setOf(123456789L, 987654321L)
        )
    )

    // 컨트롤러 등록
    // bot.registerControllers(listOf(
    //     CustomMessageController::class,
    //     CustomBatchController::class,
    //     CustomFeedController::class
    // ))

    bot.run()
}
```

### 함수형 데코레이터 방식

```kotlin
import com.spear.iriskt.Bot
import com.spear.iriskt.BotOptions
import com.spear.iriskt.models.ChatContext
import com.spear.iriskt.util.Decorators
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val irisUrl = System.getenv("IRIS_URL") ?: error("IRIS_URL 환경 변수를 설정하세요")
    val bot = Bot("DecoratorBot", irisUrl, BotOptions(maxWorkers = 4))

    // 파라미터가 있는 경우에만 실행
    val echoHandler = Decorators.hasParam { context ->
        context.reply("에코: ${context.message.param}")
    }

    // 관리자만 실행 가능
    val adminHandler = Decorators.isAdmin { context ->
        context.reply("관리자 명령어입니다.")
    }

    // 답장인 경우에만 실행
    val replyHandler = Decorators.isReply { context ->
        context.reply("답장을 확인했습니다!")
    }

    // 차단되지 않은 사용자만 실행
    val notBannedHandler = Decorators.isNotBanned(bot) { context ->
        context.reply("실행 가능합니다.")
    }

    bot.onEvent("message") { payload ->
        if (payload is ChatContext) {
            when (payload.message.command) {
                "에코" -> echoHandler(payload)
                "관리자" -> adminHandler(payload)
                "답장" -> replyHandler(payload)
                "테스트" -> notBannedHandler(payload)
            }
        }
    }

    bot.run()
}
```

---

## 📚 API 참조

### Bot 클래스

**생성자**
```kotlin
Bot(
    botName: String,
    irisUrl: String,
    options: BotOptions = BotOptions()
)
```

**메서드**
- `onEvent(event: String, handler: suspend (Any) -> Unit)`: 이벤트 핸들러 등록
- `suspend fun run()`: 봇 실행 (비동기)
- `fun close()`: 봇 중지
- `fun api(): IrisApiClient`: API 클라이언트 반환
- `fun getScheduler(): BatchScheduler`: 스케줄러 반환
- `fun getIrisLink(): IrisLink`: IrisLink 반환
- `fun isBannedUser(userId: Long): Boolean`: 차단 사용자 확인

### ChatContext 클래스

**속성**
- `room: Room`: 채팅방 정보
- `sender: User`: 발신자 정보
- `message: Message`: 메시지 정보
- `raw: JsonObject`: 원시 데이터
- `api: IrisApiClient`: API 인스턴스

**메서드**
- `suspend fun reply(message: String, roomId: Long? = null)`: 답장 보내기
- `suspend fun replyMedia(files: List<ByteArray>, roomId: Long? = null)`: 미디어 파일 보내기
- `suspend fun getSource(): ChatContext?`: 답장 원본 메시지 반환
- `suspend fun getNextChat(n: Int = 1): ChatContext?`: 다음 메시지 반환
- `suspend fun getPreviousChat(n: Int = 1): ChatContext?`: 이전 메시지 반환

### Message 클래스

**속성**
- `id: Long`: 메시지 ID
- `type: Int`: 메시지 타입
- `text: String`: 메시지 내용
- `attachment: String?`: 첨부 파일
- `metadata: JsonElement?`: 추가 데이터
- `command: String`: 명령어 (첫 단어)
- `param: String`: 매개변수 (나머지 부분)
- `hasParam: Boolean`: 매개변수 존재 여부
- `image: ChatImage?`: 이미지 정보

### User 클래스

**속성**
- `id: Long`: 사용자 ID
- `name: String`: 사용자 이름
- `type: String`: 사용자 권한
  - `HOST`: 방장
  - `MANAGER`: 관리자
  - `NORMAL`: 일반 사용자
  - `BOT`: 봇

**메서드**
- `suspend fun getName(): String`: 사용자 이름 조회
- `suspend fun getType(): String`: 사용자 권한 조회

### Room 클래스

**속성**
- `id: Long`: 방 ID
- `name: String`: 방 이름

**메서드**
- `suspend fun getType(): String`: 방 타입 조회

### IrisLink 클래스

**생성자**
```kotlin
IrisLink(
    defaultAppKey: String?,
    defaultOrigin: String?
)
```

**메서드**
- `suspend fun send(receiverName: String, templateId: Int, templateArgs: Map<String, Any>, options: Map<String, Any> = emptyMap())`
- `suspend fun init()`

**예외 클래스**
- `KakaoLinkException`: 일반 오류
- `KakaoLinkReceiverNotFoundException`: 받는 사람 없음
- `KakaoLinkLoginException`: 로그인 오류
- `KakaoLink2FAException`: 2단계 인증 오류
- `KakaoLinkSendException`: 전송 오류
- `KakaoLinkTemplateNotFoundException`: 템플릿 없음
- `KakaoLinkInvalidTemplateArgsException`: 잘못된 템플릿 인자

---

## 🎯 어노테이션 (Annotations)

### 클래스 어노테이션

**컨트롤러 타입**
- `@BootstrapController`: 봇 앱 시작시 우선적으로 실행
- `@BatchController`: 스케줄, 배치 처리
- `@ChatController` / `@Controller`: 모든 채팅 이벤트 처리
- `@MessageController`: 메시지 이벤트 처리
- `@NewMemberController`: 새 멤버 입장 이벤트 처리
- `@DeleteMemberController`: 멤버 퇴장 이벤트 처리
- `@FeedController`: 피드 이벤트 처리
- `@UnknownController`: 알 수 없는 명령어 처리
- `@ErrorController`: 에러 이벤트 처리

**Prefix 설정**
- `@Prefix("!")`: 컨트롤러의 기본 prefix 설정

### 메소드 어노테이션

**명령어 등록**
- `@BotCommand("명령어", "설명")`: 봇 명령어 등록
- `@HelpCommand("도움말")`: 도움말 명령어
- `@Command`: 해당 컨트롤러에서 이벤트 수신 시 무조건 호출

**Prefix 설정**
- `@MethodPrefix("특정메소드!")`: 특정 메소드에만 prefix 설정

**조건부 실행**
- `@HasParam`: 파라미터 필수
- `@IsAdmin`: 관리자만
- `@HasRole(["HOST", "MANAGER"])`: 특정 역할만
- `@IsNotBanned`: 차단되지 않은 사용자
- `@IsReply`: 답장 메시지만
- `@AllowedRoom(["1234567890", "방 ID"])`: 특정 방에서만
- `@Throttle(maxCalls = 3, timeWindowMs = 60000)`: 사용 빈도 제한

**메시지 타입별**
- `@OnMessage`: 모든 메시지
- `@OnNormalMessage`: 일반 텍스트 메시지
- `@OnPhotoMessage`: 사진 메시지
- `@OnImageMessage`: 이미지 메시지
- `@OnVideoMessage`: 비디오 메시지
- `@OnAudioMessage`: 오디오 메시지
- `@OnFileMessage`: 파일 메시지
- `@OnMapMessage`: 지도 메시지
- `@OnEmoticonMessage`: 이모티콘 메시지
- `@OnProfileMessage`: 프로필 메시지
- `@OnMultiPhotoMessage`: 다중 사진 메시지
- `@OnNewMultiPhotoMessage`: 새로운 다중 사진 메시지
- `@OnReplyMessage`: 답장 메시지

**피드 타입별**
- `@OnFeedMessage`: 피드 메시지
- `@OnInviteUserFeed`: 사용자 초대 피드
- `@OnLeaveUserFeed`: 사용자 퇴장 피드
- `@OnDeleteMessageFeed`: 메시지 삭제 피드
- `@OnHideMessageFeed`: 메시지 숨김 피드
- `@OnPromoteManagerFeed`: 관리자 승급 피드
- `@OnDemoteManagerFeed`: 관리자 강등 피드
- `@OnHandOverHostFeed`: 방장 위임 피드
- `@OnOpenChatJoinUserFeed`: 오픈채팅 사용자 입장 피드
- `@OnOpenChatKickedUserFeed`: 오픈채팅 사용자 추방 피드

**배치(스케줄링) 및 부트스트랩**
- `@Schedule(interval = 5000)`: 주기적 실행 (밀리초)
- `@ScheduleMessage("key")`: 스케줄된 메시지 처리
- `@Bootstrap(priority = 1)`: 부트스트랩 실행 (낮은 숫자 우선)

---

## 🛠 유틸리티 함수

### 스케줄링 관련
```kotlin
import com.spear.iriskt.util.BotUtils

// 컨텍스트를 스케줄에 추가
BotUtils.addContextToSchedule(context, delayMillis = 60000, key = "reminder")

// 메시지 스케줄링
BotUtils.scheduleMessage(
    id = "meeting-reminder",
    roomId = 10001L,
    message = "10분 후 회의가 시작됩니다.",
    time = 600000,
    metadata = mapOf("type" to "reminder")
)
```

### 스로틀링 관리
```kotlin
// 특정 사용자의 스로틀 해제
BotUtils.clearUserThrottle(userId = 123456789L, commandName = "명령어")

// 모든 사용자의 스로틀 해제
BotUtils.clearAllThrottle(commandName = "명령어")
```

### 정보 조회
```kotlin
// 등록된 명령어 목록 조회
val commands = BotUtils.getRegisteredCommands()

// 등록된 컨트롤러 목록 조회
val controllers = BotUtils.getRegisteredControllers()

// 배치 컨트롤러 목록 조회
val batchControllers = BotUtils.getBatchControllers()

// 부트스트랩 컨트롤러 목록 조회
val bootstrapControllers = BotUtils.getBootstrapControllers()

// 부트스트랩 메소드 목록 조회
val bootstrapMethods = BotUtils.getBootstrapMethods(controllerClass)

// 스케줄 메소드 목록 조회
val scheduleMethods = BotUtils.getScheduleMethods(controllerClass)

// 스케줄 메시지 메소드 목록 조회
val scheduleMessageMethods = BotUtils.getScheduleMessageMethods(controllerClass)
```

### 디버깅
```kotlin
// 데코레이터 메타데이터 디버깅
BotUtils.debugDecoratorMetadata()

// 방 제한 설정 디버깅
BotUtils.debugRoomRestrictions()
```

---

## 🧩 주요 컴포넌트 개요

- **`com.spear.iriskt.Bot`**: 웹소켓 이벤트 수신, 핸들러 등록, API/스케줄러 접근 제공
- **`com.spear.iriskt.api.IrisApiClient`**: REST API 호출(`reply`, `replyImage`, `query`, `decrypt`)
- **`com.spear.iriskt.core.IrisLink`**: KakaoLink 템플릿 전송 및 예외(`KakaoLinkException` 계열) 처리
- **`com.spear.iriskt.core.BatchScheduler`**: 예약 메시지 작업 (`scheduleMessage`, `scheduleMessageAt`)
- **`com.spear.iriskt.models.ChatContext`**: 메시지/사용자/방 정보와 응답 메서드 제공
- **`com.spear.iriskt.internal.EventEmitter`**: 비동기 이벤트 디스패치, 에러 전파
- **`com.spear.iriskt.util.Decorators`**: 함수형 데코레이터 제공
- **`com.spear.iriskt.util.BotUtils`**: 유틸리티 함수 모음
- **어노테이션**: `@HasParam`, `@IsAdmin`, `@HasRole`, `@Throttle`, `@IsReply`, `@IsNotBanned`, `@AllowedRoom`

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
import com.spear.iriskt.util.BotUtils

// 방법 1: BotUtils 사용
BotUtils.scheduleMessage(
    id = "meeting-reminder",
    roomId = 10001L,
    message = "10분 후 회의가 시작됩니다.",
    time = 600000,
    metadata = mapOf("type" to "reminder")
)

// 방법 2: Scheduler 직접 사용
val scheduler = bot.getScheduler()
scheduler.scheduleMessage(
    id = "meeting-reminder",
    roomId = 10001L,
    message = "10분 후 회의가 시작됩니다.",
    delayMillis = 600000
)

// 방법 3: Context를 스케줄에 추가
BotUtils.addContextToSchedule(context, delayMillis = 60000, key = "reminder")
```

### 3. KakaoLink 템플릿 전송

```kotlin
import com.spear.iriskt.core.*

val irisLink = bot.getIrisLink()

try {
    irisLink.send(
        receiverName = "홍길동",
        templateId = 12345,
        templateArgs = mapOf("message" to "IRIS Bot에서 전송한 링크입니다")
    )
} catch (e: KakaoLinkReceiverNotFoundException) {
    println("받는 사람을 찾을 수 없습니다")
} catch (e: KakaoLinkSendException) {
    println("메시지 전송 실패")
} catch (e: KakaoLinkException) {
    println("카카오링크 오류: ${e.message}")
}
```

### 4. 스로틀링 (사용 빈도 제한)

```kotlin
import com.spear.iriskt.util.BotUtils

// 어노테이션 방식
@BotCommand("제한", "사용 빈도 제한 테스트")
@Throttle(maxCalls = 3, timeWindowMs = 60000) // 1분에 3번
suspend fun throttleTest(context: ChatContext) {
    context.reply("1분에 3번만 사용할 수 있습니다.")
}

// 수동으로 스로틀 해제
BotUtils.clearUserThrottle(userId = 123456789L, commandName = "제한")
BotUtils.clearAllThrottle(commandName = "제한")
```

### 5. 함수형 데코레이터 조합

```kotlin
import com.spear.iriskt.util.Decorators

// 여러 조건을 조합한 핸들러
val complexHandler = Decorators.compose(
    { handler -> Decorators.isNotBanned(bot, handler) },
    { handler -> Decorators.isAdmin(handler) },
    { handler -> Decorators.hasParam(handler) },
    { handler -> Decorators.allowedRoom(listOf("테스트방"), handler = handler) }
) { context ->
    context.reply("모든 조건을 만족했습니다: ${context.message.param}")
}
```

### 6. 부트스트랩 및 초기화

```kotlin
@BootstrapController
class InitController {
    private val logger = LoggerManager.getLogger("InitController")

    @Bootstrap(priority = 1) // 가장 먼저 실행
    suspend fun initDatabase() {
        logger.info("데이터베이스 초기화 중...")
    }

    @Bootstrap(priority = 2) // 두 번째로 실행
    suspend fun loadConfig() {
        logger.info("설정 로드 중...")
    }
}
```

### 7. 배치 작업 및 스케줄링

```kotlin
@BatchController
class ScheduledTaskController {
    @Schedule(interval = 60000) // 1분마다
    suspend fun periodicTask() {
        println("주기적 작업 실행 중...")
    }

    @Schedule(interval = 3600000) // 1시간마다
    suspend fun hourlyTask() {
        println("시간별 작업 실행 중...")
    }

    @ScheduleMessage("reminder")
    suspend fun handleReminder(scheduledMessage: BatchScheduler.ScheduledMessage) {
        println("리마인더: ${scheduledMessage.message}")
    }
}
```

### 8. 피드 이벤트 처리

```kotlin
@FeedController
class FeedEventController {
    @OnInviteUserFeed
    suspend fun onUserJoin(context: ChatContext) {
        context.reply("${context.sender.name}님이 입장하셨습니다! 🎉")
    }

    @OnLeaveUserFeed
    suspend fun onUserLeave(context: ChatContext) {
        context.reply("${context.sender.name}님이 퇴장하셨습니다.")
    }

    @OnPromoteManagerFeed
    suspend fun onManagerPromote(context: ChatContext) {
        context.reply("${context.sender.name}님이 관리자로 임명되었습니다! 👑")
    }
}
```

### 9. 메시지 타입별 처리

```kotlin
@MessageController
class MessageTypeController {
    @OnPhotoMessage
    suspend fun onPhoto(context: ChatContext) {
        context.reply("사진을 받았습니다!")
    }

    @OnVideoMessage
    suspend fun onVideo(context: ChatContext) {
        context.reply("비디오를 받았습니다!")
    }

    @OnReplyMessage
    suspend fun onReply(context: ChatContext) {
        val source = context.getSource()
        context.reply("답장 원본: ${source?.message?.text}")
    }
}
```

### 10. 디버깅 및 모니터링

```kotlin
import com.spear.iriskt.util.BotUtils

// 등록된 명령어 확인
val commands = BotUtils.getRegisteredCommands()
commands.forEach { (command, info) ->
    println("명령어: $command, 설명: ${info.description}")
}

// 데코레이터 메타데이터 디버깅
BotUtils.debugDecoratorMetadata()

// 방 제한 설정 디버깅
BotUtils.debugRoomRestrictions()
```

---

## 🛡 문제 해결 가이드

- **빌드 오류**: `./gradlew.bat clean build` 실행 후 발생 로그 확인.
- **실행 오류**: IRIS 서버 주소와 네트워크 연결, 인증 정보를 재검토.
- **웹소켓 연결 반복 종료**: 방화벽, SSL 설정, 서버 로그를 점검.
- **KakaoLink 실패**: `kakaoLinkAppKey`, `kakaoLinkOrigin`, 템플릿 매핑을 다시 확인.

---

## 📖 문서

- **[API 레퍼런스 (한국어)](./docs/API_REFERENCE_KR.md)** - 전체 API 문서
- **[예제 모음](./examples/README.md)** - 다양한 사용 예제
- **[마이그레이션 가이드](./docs/MIGRATION_FROM_NODE_IRIS.md)** - node-iris에서 Iris-kt로 마이그레이션
- **[node-iris 호환성](./docs/NODE_IRIS_COMPATIBILITY.md)** - node-iris 100% 호환 가이드
- **[프로젝트 구조](./docs/PROJECT_STRUCTURE.md)** - 프로젝트 디렉토리 구조 및 파일 설명
- **[성능 최적화](./docs/PERFORMANCE_OPTIMIZATION.md)** - 성능 최적화 가이드 및 벤치마크
- **[구현 완료 기능](./docs/IMPLEMENTED_FEATURES.md)** - 모든 구현된 기능 목록

---

## 🤝 기여 방법

- 이슈를 등록할 때는 재현 절차와 로그를 함께 제공해주세요.
- Pull Request는 테스트 결과와 변경 이유를 상세히 작성해주세요.
- 새로운 기능 제안은 Discussions 탭을 통해 논의 후 진행하면 효율적입니다.

---

## 🪪 라이선스

이 프로젝트는 [MIT License](LICENSE) 하에 배포됩니다.

---

## 🔗 참고 링크

- **node-iris (TypeScript)**: [https://github.com/Tsuki-Chat/node-iris](https://github.com/Tsuki-Chat/node-iris)
- **irispy-client (Python)**: [https://github.com/irisdev/irispy-client](https://github.com/irisdev/irispy-client)
- **node-iris 레퍼런스**: [카카오톡 봇 커뮤니티](https://cafe.naver.com/nameyee/1234567)

---

## 📝 변경 이력

### v0.1.0 (2025-11-10)
- node-iris 레퍼런스 기반 초기 구현
- 컨트롤러 기반 개발 방식 지원
- 어노테이션 데코레이터 시스템 구현
- 함수형 데코레이터 지원
- KakaoLink 예외 처리 개선
- 스로틀링 관리 시스템 추가
- 유틸리티 함수 모음 추가
- 배치 스케줄링 지원
- 부트스트랩 시스템 구현
- 피드 이벤트 처리 지원
- 메시지 타입별 처리 지원

---

## 🙏 감사의 말

이 프로젝트는 [@tsuki-chat/node-iris](https://github.com/Tsuki-Chat/node-iris)의 Kotlin 포팅 버전입니다.  
node-iris 개발자 [LunaticaLuna](https://github.com/LunaticaLuna)님께 감사드립니다.

기반 프로젝트인 [irispy-client](https://github.com/irisdev/irispy-client) 개발자분들께도 감사드립니다.
