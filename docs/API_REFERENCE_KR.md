# Iris-kt API 레퍼런스

> 본 문서는 [@tsuki-chat/node-iris](https://github.com/Tsuki-Chat/node-iris) 레퍼런스를 기반으로 작성되었습니다.

## 목차

- [Bot 클래스](#bot-클래스)
- [ChatContext 클래스](#chatcontext-클래스)
- [Message 클래스](#message-클래스)
- [User 클래스](#user-클래스)
- [Room 클래스](#room-클래스)
- [IrisLink 클래스](#irislink-클래스)
- [어노테이션](#어노테이션)
- [유틸리티 함수](#유틸리티-함수)
- [예외 클래스](#예외-클래스)

---

## Bot 클래스

카카오톡 봇의 메인 클래스입니다.

### 생성자

```kotlin
Bot(
    botName: String,
    irisUrl: String,
    options: BotOptions = BotOptions()
)
```

**파라미터:**
- `botName`: 봇 이름
- `irisUrl`: IRIS 서버 URL (예: `ws://localhost:8080`)
- `options`: 봇 옵션 설정

**BotOptions:**
```kotlin
data class BotOptions(
    val maxWorkers: Int = 4,              // 최대 워커 수
    val httpMode: Boolean = false,        // HTTP 모드 사용 여부
    val port: Int? = null,                // 웹훅 포트
    val webhookPath: String? = null,      // 웹훅 경로
    val logLevel: LogLevel = LogLevel.INFO, // 로그 레벨
    val bannedUsers: Set<Long> = emptySet(), // 차단 사용자 목록
    val kakaoLinkAppKey: String? = null,  // 카카오링크 앱 키
    val kakaoLinkOrigin: String? = null   // 카카오링크 Origin
)
```

### 메서드

#### onEvent
```kotlin
fun onEvent(name: String, handler: suspend (Any) -> Unit)
```
이벤트 핸들러를 등록합니다.

**이벤트 타입:**
- `chat`: 모든 메시지
- `message`: 일반 메시지
- `new_member`: 새 멤버 참여
- `del_member`: 멤버 퇴장
- `unknown`: 알 수 없는 이벤트
- `error`: 오류 발생

**예제:**
```kotlin
bot.onEvent("message") { payload ->
    if (payload is ChatContext) {
        println("메시지: ${payload.message.text}")
    }
}
```

#### run
```kotlin
suspend fun run()
```
봇을 실행합니다 (비동기).

#### close
```kotlin
fun close()
```
봇을 중지합니다.

#### api
```kotlin
fun api(): IrisApiClient
```
API 클라이언트를 반환합니다.

#### getScheduler
```kotlin
fun getScheduler(): BatchScheduler
```
스케줄러를 반환합니다.

#### getIrisLink
```kotlin
fun getIrisLink(): IrisLink
```
IrisLink 인스턴스를 반환합니다.

#### isBannedUser
```kotlin
fun isBannedUser(userId: Long): Boolean
```
사용자가 차단되었는지 확인합니다.

---

## ChatContext 클래스

채팅 컨텍스트 정보를 담고 있는 클래스입니다.

### 속성

```kotlin
data class ChatContext(
    val room: Room,              // 채팅방 정보
    val sender: User,            // 발신자 정보
    val message: Message,        // 메시지 정보
    val raw: JsonObject,         // 원시 데이터
    val api: IrisApiClient       // API 인스턴스
)
```

### 메서드

#### reply
```kotlin
suspend fun reply(message: String, roomId: Long? = null)
```
메시지에 답장합니다.

**예제:**
```kotlin
context.reply("안녕하세요!")
```

#### replyMedia
```kotlin
suspend fun replyMedia(files: List<ByteArray>, roomId: Long? = null)
```
미디어 파일을 전송합니다.

**예제:**
```kotlin
val imageData = File("image.jpg").readBytes()
context.replyMedia(listOf(imageData))
```

#### getSource
```kotlin
suspend fun getSource(): ChatContext?
```
답장 원본 메시지를 반환합니다.

**예제:**
```kotlin
val source = context.getSource()
if (source != null) {
    context.reply("원본: ${source.message.text}")
}
```

#### getNextChat
```kotlin
suspend fun getNextChat(n: Int = 1): ChatContext?
```
다음 메시지를 반환합니다.

#### getPreviousChat
```kotlin
suspend fun getPreviousChat(n: Int = 1): ChatContext?
```
이전 메시지를 반환합니다.

---

## Message 클래스

메시지 정보를 담고 있는 클래스입니다.

### 속성

```kotlin
data class Message(
    val id: Long,                    // 메시지 ID
    val type: Int,                   // 메시지 타입
    val text: String,                // 메시지 내용
    val attachment: String?,         // 첨부 파일
    val metadata: JsonElement?       // 추가 데이터
)
```

### 계산된 속성

#### command
```kotlin
val command: String
```
메시지의 첫 단어 (명령어)를 반환합니다.

**예제:**
```kotlin
// 메시지: "!안녕 세상"
println(message.command) // "!안녕"
```

#### param
```kotlin
val param: String
```
명령어를 제외한 나머지 부분을 반환합니다.

**예제:**
```kotlin
// 메시지: "!안녕 세상"
println(message.param) // "세상"
```

#### hasParam
```kotlin
val hasParam: Boolean
```
매개변수가 있는지 확인합니다.

#### image
```kotlin
val image: ChatImage?
```
이미지 정보를 반환합니다.

---

## User 클래스

사용자 정보를 담고 있는 클래스입니다.

### 속성

```kotlin
data class User(
    val id: Long,        // 사용자 ID
    val name: String     // 사용자 이름
)
```

### 메서드

#### getName
```kotlin
suspend fun getName(): String
```
사용자 이름을 조회합니다.

#### getType
```kotlin
suspend fun getType(): String
```
사용자 권한을 조회합니다.

**권한 타입:**
- `HOST`: 방장
- `MANAGER`: 관리자
- `NORMAL`: 일반 사용자
- `BOT`: 봇

**예제:**
```kotlin
val userType = user.getType()
if (userType == "HOST") {
    println("방장입니다")
}
```

---

## Room 클래스

채팅방 정보를 담고 있는 클래스입니다.

### 속성

```kotlin
data class Room(
    val id: Long,        // 방 ID
    val name: String     // 방 이름
)
```

### 메서드

#### getType
```kotlin
suspend fun getType(): String
```
방 타입을 조회합니다.

---

## IrisLink 클래스

카카오링크 전송을 위한 클래스입니다.

### 생성자

```kotlin
IrisLink(
    defaultAppKey: String?,
    defaultOrigin: String?
)
```

**파라미터:**
- `defaultAppKey`: 카카오 앱 키
- `defaultOrigin`: Origin URL

### 메서드

#### init
```kotlin
suspend fun init()
```
IrisLink를 초기화합니다.

#### send
```kotlin
suspend fun send(
    receiverName: String,
    templateId: Int,
    templateArgs: Map<String, Any>,
    options: Map<String, Any> = emptyMap()
)
```
카카오링크 메시지를 전송합니다.

**파라미터:**
- `receiverName`: 받는 사람 이름 (채팅방 이름)
- `templateId`: 템플릿 ID
- `templateArgs`: 템플릿 인자
- `options`: 추가 옵션

**예제:**
```kotlin
val link = IrisLink(appKey, origin)
link.init()

link.send(
    receiverName = "내 채팅방",
    templateId = 12345,
    templateArgs = mapOf(
        "title" to "제목",
        "description" to "설명"
    )
)
```

---

## 어노테이션

### 클래스 어노테이션

#### 컨트롤러 타입

```kotlin
@Controller                  // 모든 채팅 이벤트 처리
@ChatController             // 모든 채팅 이벤트 처리
@MessageController          // 메시지 이벤트 처리
@NewMemberController        // 새 멤버 입장 이벤트 처리
@DeleteMemberController     // 멤버 퇴장 이벤트 처리
@FeedController            // 피드 이벤트 처리
@UnknownController         // 알 수 없는 명령어 처리
@ErrorController           // 에러 이벤트 처리
@BatchController           // 스케줄, 배치 처리
@BootstrapController       // 봇 시작시 우선 실행
```

#### Prefix 설정

```kotlin
@Prefix("!")  // 컨트롤러의 기본 prefix 설정
```

**예제:**
```kotlin
@MessageController
@Prefix("!")
class MyController {
    @BotCommand("안녕", "인사")
    suspend fun hello(context: ChatContext) {
        // "!안녕" 명령어로 실행됨
        context.reply("안녕하세요!")
    }
}
```

### 메소드 어노테이션

#### 명령어 등록

```kotlin
@BotCommand("명령어", "설명")  // 봇 명령어 등록
@HelpCommand("도움말")         // 도움말 명령어
@Command                       // 이벤트 수신 시 무조건 호출
```

**예제:**
```kotlin
@BotCommand("시간", "현재 시간 조회")
suspend fun getTime(context: ChatContext) {
    context.reply("현재 시각: ${LocalDateTime.now()}")
}
```

#### Prefix 설정

```kotlin
@MethodPrefix("특정메소드!")  // 특정 메소드에만 prefix 설정
```

#### 조건부 실행

```kotlin
@HasParam                    // 파라미터 필수
@IsAdmin                     // 관리자만
@HasRole(["HOST", "MANAGER"]) // 특정 역할만
@IsNotBanned                 // 차단되지 않은 사용자
@IsReply                     // 답장 메시지만
@AllowedRoom(["방ID1", "방ID2"]) // 특정 방에서만
@Throttle(maxCalls = 3, timeWindowMs = 60000) // 사용 빈도 제한
```

**예제:**
```kotlin
@BotCommand("에코", "메시지 반복")
@HasParam
suspend fun echo(context: ChatContext) {
    context.reply("에코: ${context.message.param}")
}

@BotCommand("관리", "관리자 전용")
@IsAdmin
suspend fun adminCommand(context: ChatContext) {
    context.reply("관리자 명령어입니다")
}

@BotCommand("제한", "사용 빈도 제한")
@Throttle(maxCalls = 3, timeWindowMs = 60000)
suspend fun throttled(context: ChatContext) {
    context.reply("1분에 3번만 사용 가능")
}
```

#### 메시지 타입별

```kotlin
@OnMessage                  // 모든 메시지
@OnNormalMessage           // 일반 텍스트 메시지
@OnPhotoMessage            // 사진 메시지
@OnImageMessage            // 이미지 메시지
@OnVideoMessage            // 비디오 메시지
@OnAudioMessage            // 오디오 메시지
@OnFileMessage             // 파일 메시지
@OnMapMessage              // 지도 메시지
@OnEmoticonMessage         // 이모티콘 메시지
@OnProfileMessage          // 프로필 메시지
@OnMultiPhotoMessage       // 다중 사진 메시지
@OnNewMultiPhotoMessage    // 새로운 다중 사진 메시지
@OnReplyMessage            // 답장 메시지
```

**예제:**
```kotlin
@OnPhotoMessage
suspend fun onPhoto(context: ChatContext) {
    context.reply("사진을 받았습니다!")
}
```

#### 피드 타입별

```kotlin
@OnFeedMessage              // 피드 메시지
@OnInviteUserFeed          // 사용자 초대 피드
@OnLeaveUserFeed           // 사용자 퇴장 피드
@OnDeleteMessageFeed       // 메시지 삭제 피드
@OnHideMessageFeed         // 메시지 숨김 피드
@OnPromoteManagerFeed      // 관리자 승급 피드
@OnDemoteManagerFeed       // 관리자 강등 피드
@OnHandOverHostFeed        // 방장 위임 피드
@OnOpenChatJoinUserFeed    // 오픈채팅 사용자 입장 피드
@OnOpenChatKickedUserFeed  // 오픈채팅 사용자 추방 피드
```

**예제:**
```kotlin
@OnInviteUserFeed
suspend fun onUserJoin(context: ChatContext) {
    context.reply("${context.sender.name}님 환영합니다! 🎉")
}
```

#### 배치 및 부트스트랩

```kotlin
@Schedule(interval = 5000)   // 주기적 실행 (밀리초)
@ScheduleMessage("key")      // 스케줄된 메시지 처리
@Bootstrap(priority = 1)     // 부트스트랩 실행 (낮은 숫자 우선)
```

**예제:**
```kotlin
@BatchController
class ScheduledTasks {
    @Schedule(interval = 60000) // 1분마다
    suspend fun periodicTask() {
        println("주기적 작업 실행")
    }
}

@BootstrapController
class InitTasks {
    @Bootstrap(priority = 1)
    suspend fun init() {
        println("초기화 작업")
    }
}
```

---

## 유틸리티 함수

### BotUtils

#### 스케줄링 관련

```kotlin
// 컨텍스트를 스케줄에 추가
BotUtils.addContextToSchedule(
    context = context,
    delayMillis = 60000,
    key = "reminder"
)

// 메시지 스케줄링
BotUtils.scheduleMessage(
    id = "meeting-reminder",
    roomId = 10001L,
    message = "10분 후 회의가 시작됩니다.",
    time = 600000,
    metadata = mapOf("type" to "reminder")
)
```

#### 스로틀링 관리

```kotlin
// 특정 사용자의 스로틀 해제
BotUtils.clearUserThrottle(
    userId = 123456789L,
    commandName = "명령어"
)

// 모든 사용자의 스로틀 해제
BotUtils.clearAllThrottle(commandName = "명령어")
```

#### 정보 조회

```kotlin
// 등록된 명령어 목록
val commands = BotUtils.getRegisteredCommands()

// 등록된 컨트롤러 목록
val controllers = BotUtils.getRegisteredControllers()

// 배치 컨트롤러 목록
val batchControllers = BotUtils.getBatchControllers()

// 부트스트랩 컨트롤러 목록
val bootstrapControllers = BotUtils.getBootstrapControllers()

// 부트스트랩 메소드 목록
val bootstrapMethods = BotUtils.getBootstrapMethods(controllerClass)

// 스케줄 메소드 목록
val scheduleMethods = BotUtils.getScheduleMethods(controllerClass)

// 스케줄 메시지 메소드 목록
val scheduleMessageMethods = BotUtils.getScheduleMessageMethods(controllerClass)
```

#### 디버깅

```kotlin
// 데코레이터 메타데이터 디버깅
BotUtils.debugDecoratorMetadata()

// 방 제한 설정 디버깅
BotUtils.debugRoomRestrictions()
```

### Decorators (함수형 데코레이터)

```kotlin
// 파라미터 필수
val handler = Decorators.hasParam { context ->
    context.reply("파라미터: ${context.message.param}")
}

// 관리자만
val adminHandler = Decorators.isAdmin { context ->
    context.reply("관리자 명령어")
}

// 답장만
val replyHandler = Decorators.isReply { context ->
    context.reply("답장 확인")
}

// 차단되지 않은 사용자만
val notBannedHandler = Decorators.isNotBanned(bot) { context ->
    context.reply("실행 가능")
}

// 특정 역할만
val roleHandler = Decorators.hasRole(
    roles = listOf("HOST", "MANAGER"),
    onFail = { it.reply("권한 없음") }
) { context ->
    context.reply("실행됨")
}

// 특정 방만
val roomHandler = Decorators.allowedRoom(
    rooms = listOf("테스트방"),
    onFail = { it.reply("이 방에서는 사용 불가") }
) { context ->
    context.reply("실행됨")
}

// 여러 데코레이터 조합
val composedHandler = Decorators.compose(
    { handler -> Decorators.isNotBanned(bot, handler) },
    { handler -> Decorators.isAdmin(handler) },
    { handler -> Decorators.hasParam(handler) }
) { context ->
    context.reply("모든 조건 만족")
}
```

---

## 예외 클래스

### KakaoLink 예외

```kotlin
// 일반 예외
KakaoLinkException(message: String, cause: Throwable? = null)

// 받는 사람을 찾을 수 없음
KakaoLinkReceiverNotFoundException(receiverName: String)

// 로그인 실패
KakaoLinkLoginException(message: String, cause: Throwable? = null)

// 2단계 인증 필요
KakaoLink2FAException(message: String = "2단계 인증이 필요합니다")

// 메시지 전송 실패
KakaoLinkSendException(message: String, cause: Throwable? = null)

// 템플릿을 찾을 수 없음
KakaoLinkTemplateNotFoundException(templateId: Int)

// 잘못된 템플릿 인자
KakaoLinkInvalidTemplateArgsException(message: String)
```

**예제:**
```kotlin
try {
    link.send(
        receiverName = "내 채팅방",
        templateId = 12345,
        templateArgs = mapOf("key" to "value")
    )
} catch (e: KakaoLinkReceiverNotFoundException) {
    println("받는 사람을 찾을 수 없습니다")
} catch (e: KakaoLinkSendException) {
    println("메시지 전송 실패")
} catch (e: KakaoLinkException) {
    println("카카오링크 오류: ${e.message}")
}
```

---

## 참고 자료

- [node-iris GitHub](https://github.com/Tsuki-Chat/node-iris)
- [irispy-client GitHub](https://github.com/irisdev/irispy-client)
- [메인 README](../README.md)
- [예제 모음](../examples/README.md)
