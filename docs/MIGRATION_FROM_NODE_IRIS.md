# node-iris에서 Iris-kt로 마이그레이션 가이드

이 문서는 TypeScript/JavaScript의 node-iris에서 Kotlin의 Iris-kt로 마이그레이션하는 방법을 설명합니다.

## 목차

- [기본 개념 비교](#기본-개념-비교)
- [Bot 초기화](#bot-초기화)
- [이벤트 핸들러](#이벤트-핸들러)
- [컨트롤러 방식](#컨트롤러-방식)
- [데코레이터](#데코레이터)
- [KakaoLink](#kakaolink)
- [스케줄링](#스케줄링)
- [주요 차이점](#주요-차이점)

---

## 기본 개념 비교

### TypeScript (node-iris)
```typescript
import Bot from "@tsuki-chat/node-iris";

const bot = new Bot("appName", process.env.IRIS_URL, {
  maxWorkers: 4
});
```

### Kotlin (Iris-kt)
```kotlin
import com.spear.iriskt.Bot
import com.spear.iriskt.BotOptions

val bot = Bot(
    botName = "appName",
    irisUrl = System.getenv("IRIS_URL") ?: error("IRIS_URL required"),
    options = BotOptions(maxWorkers = 4)
)
```

---

## Bot 초기화

### node-iris
```typescript
const bot = new Bot("appName", process.env.IRIS_URL, {
  maxWorkers: 4,
  saveChatLogs: true,
  logLevel: "debug",
  httpMode: true,
  webhookPort: 3022,
  webhookPath: "/webhook/message"
});

await bot.run();
```

### Iris-kt
```kotlin
val bot = Bot(
    botName = "appName",
    irisUrl = System.getenv("IRIS_URL") ?: error("IRIS_URL required"),
    options = BotOptions(
        maxWorkers = 4,
        logLevel = LogLevel.DEBUG,
        httpMode = true,
        port = 3022,
        webhookPath = "/webhook/message"
    )
)

runBlocking {
    bot.run()
}
```

---

## 이벤트 핸들러

### node-iris
```typescript
bot.on('message', async (context: ChatContext) => {
  if (context.message.command === '안녕') {
    await context.reply('안녕하세요!');
  }
});
```

### Iris-kt
```kotlin
bot.onEvent("message") { payload ->
    if (payload is ChatContext) {
        if (payload.message.command == "안녕") {
            payload.reply("안녕하세요!")
        }
    }
}
```

---

## 컨트롤러 방식

### node-iris
```typescript
@MessageController
@Prefix("!")
export class CustomMessageController {
  @BotCommand("안녕", "인사 명령어")
  async hello(context: ChatContext) {
    await context.reply("안녕하세요!");
  }

  @BotCommand("반복", "메시지 반복")
  @HasParam
  async echo(context: ChatContext) {
    await context.reply(`반복: ${context.message.param}`);
  }

  @BotCommand("관리자", "관리자 전용 명령어")
  @IsAdmin
  async adminOnly(context: ChatContext) {
    await context.reply("관리자만 사용할 수 있는 명령어입니다!");
  }
}
```

### Iris-kt
```kotlin
@MessageController
@Prefix("!")
class CustomMessageController {
    @BotCommand("안녕", "인사 명령어")
    suspend fun hello(context: ChatContext) {
        context.reply("안녕하세요!")
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
```

---

## 데코레이터

### node-iris (함수형)
```typescript
import { decorators } from '@tsuki-chat/node-iris';

const echoHandler = decorators.hasParam(async (context: ChatContext) => {
  await context.reply(`에코: ${context.message.param}`);
});

const adminHandler = decorators.isAdmin(async (context: ChatContext) => {
  await context.reply('관리자 명령어입니다.');
});
```

### Iris-kt (함수형)
```kotlin
import com.spear.iriskt.util.Decorators

val echoHandler = Decorators.hasParam { context ->
    context.reply("에코: ${context.message.param}")
}

val adminHandler = Decorators.isAdmin { context ->
    context.reply("관리자 명령어입니다.")
}
```

### 데코레이터 조합

#### node-iris
```typescript
// node-iris에서는 직접 조합 기능이 제공되지 않음
// 수동으로 중첩해야 함
const handler = decorators.isAdmin(
  decorators.hasParam(async (context) => {
    await context.reply('실행됨');
  })
);
```

#### Iris-kt
```kotlin
// Iris-kt는 compose 함수 제공
val handler = Decorators.compose(
    { h -> Decorators.isAdmin(h) },
    { h -> Decorators.hasParam(h) }
) { context ->
    context.reply("실행됨")
}
```

---

## KakaoLink

### node-iris
```typescript
import { KakaoLink } from '@tsuki-chat/node-iris';

const link = new KakaoLink(
  process.env.IRIS_URL,
  process.env.KAKAOLINK_APP_KEY,
  process.env.KAKAOLINK_ORIGIN
);

try {
  await link.send(
    '내 채팅방',
    12345,
    { key: 'value' }
  );
} catch (error) {
  if (error instanceof KakaoLinkSendException) {
    console.error('메시지 전송 실패');
  }
}
```

### Iris-kt
```kotlin
import com.spear.iriskt.core.IrisLink
import com.spear.iriskt.core.KakaoLinkSendException

val link = IrisLink(
    defaultAppKey = System.getenv("KAKAOLINK_APP_KEY"),
    defaultOrigin = System.getenv("KAKAOLINK_ORIGIN")
)

try {
    link.send(
        receiverName = "내 채팅방",
        templateId = 12345,
        templateArgs = mapOf("key" to "value")
    )
} catch (e: KakaoLinkSendException) {
    println("메시지 전송 실패")
}
```

---

## 스케줄링

### node-iris
```typescript
@BatchController
class CustomBatchController {
  @Schedule(60000) // 1분마다
  async periodicTask() {
    console.log("주기적 작업 실행 중...");
  }

  @ScheduleMessage("reminder")
  async handleReminder(scheduledMessage: ScheduledMessage) {
    console.log("리마인더:", scheduledMessage.message);
  }
}
```

### Iris-kt
```kotlin
@BatchController
class CustomBatchController {
    @Schedule(interval = 60000) // 1분마다
    suspend fun periodicTask() {
        println("주기적 작업 실행 중...")
    }

    @ScheduleMessage("reminder")
    suspend fun handleReminder(scheduledMessage: BatchScheduler.ScheduledMessage) {
        println("리마인더: ${scheduledMessage.message}")
    }
}
```

---

## 주요 차이점

### 1. 비동기 처리

**node-iris (async/await):**
```typescript
async function handler(context: ChatContext) {
  await context.reply("메시지");
}
```

**Iris-kt (suspend):**
```kotlin
suspend fun handler(context: ChatContext) {
    context.reply("메시지")
}
```

### 2. 타입 시스템

**node-iris (TypeScript):**
```typescript
interface BotOptions {
  maxWorkers?: number;
  logLevel?: LogLevel;
}
```

**Iris-kt (Kotlin):**
```kotlin
data class BotOptions(
    val maxWorkers: Int = 4,
    val logLevel: LogLevel = LogLevel.INFO
)
```

### 3. Null 안전성

**node-iris:**
```typescript
const param = context.message.param; // string | undefined
if (param) {
  await context.reply(param);
}
```

**Iris-kt:**
```kotlin
val param = context.message.param // String (non-null)
if (param.isNotBlank()) {
    context.reply(param)
}
```

### 4. 컬렉션

**node-iris:**
```typescript
const bannedUsers = new Set([123456789, 987654321]);
const rooms = ['방1', '방2'];
```

**Iris-kt:**
```kotlin
val bannedUsers = setOf(123456789L, 987654321L)
val rooms = listOf("방1", "방2")
```

### 5. 환경 변수

**node-iris:**
```typescript
const irisUrl = process.env.IRIS_URL;
```

**Iris-kt:**
```kotlin
val irisUrl = System.getenv("IRIS_URL")
```

### 6. 메인 함수

**node-iris:**
```typescript
async function main() {
  const app = new App();
  await app.start();
}

if (require.main === module) {
  main().catch((error) => {
    console.error('Failed to start:', error);
    process.exit(1);
  });
}
```

**Iris-kt:**
```kotlin
fun main() = runBlocking {
    val app = App()
    app.start()
}

// 또는
suspend fun main() {
    val app = App()
    app.start()
}
```

---

## 마이그레이션 체크리스트

- [ ] Bot 초기화 코드 변환
- [ ] 이벤트 핸들러를 `onEvent`로 변환
- [ ] `async/await`를 `suspend`로 변환
- [ ] TypeScript 타입을 Kotlin 타입으로 변환
- [ ] 컨트롤러 클래스 변환
- [ ] 데코레이터/어노테이션 변환
- [ ] KakaoLink 코드 변환
- [ ] 스케줄링 코드 변환
- [ ] 예외 처리 변환
- [ ] 환경 변수 접근 방식 변환
- [ ] 테스트 코드 작성

---

## 추가 리소스

- [Iris-kt API 레퍼런스](./API_REFERENCE_KR.md)
- [예제 모음](../examples/README.md)
- [node-iris GitHub](https://github.com/Tsuki-Chat/node-iris)
- [Kotlin 코루틴 가이드](https://kotlinlang.org/docs/coroutines-guide.html)

---

## 도움이 필요하신가요?

- GitHub Issues: [Iris-kt Issues](https://github.com/spear34000/Iris-kt/issues)
- node-iris Issues: [node-iris Issues](https://github.com/Tsuki-Chat/node-iris/issues)
