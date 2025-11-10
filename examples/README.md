# Iris-kt 예제 모음

이 디렉토리에는 Iris-kt 라이브러리의 다양한 사용 예제가 포함되어 있습니다.

## 📁 예제 목록

### 1. SimpleExample.kt
**irispy-client 호환 방식 봇 예제**

가장 기본적인 봇 구현 방식입니다. 이벤트 핸들러를 직접 등록하여 메시지를 처리합니다.

```bash
# 실행 방법
export IRIS_URL="ws://your-iris-server-url"
./gradlew run -PmainClass=com.spear.iriskt.examples.SimpleExampleKt
```

**주요 기능:**
- 기본 이벤트 핸들러 등록
- 간단한 명령어 처리
- 메시지 응답

---

### 2. ControllerExample.kt
**컨트롤러 방식 봇 예제 (권장)**

어노테이션 기반의 컨트롤러 방식으로 구조화된 봇을 구현합니다.

```bash
# 실행 방법
export IRIS_URL="ws://your-iris-server-url"
export KAKAOLINK_APP_KEY="your-app-key"
export KAKAOLINK_ORIGIN="https://your-origin.com"
./gradlew run -PmainClass=com.spear.iriskt.examples.ControllerExampleKt
```

**주요 기능:**
- `@MessageController`: 메시지 명령어 처리
- `@BatchController`: 주기적 작업 실행
- `@FeedController`: 피드 이벤트 처리
- `@BootstrapController`: 초기화 작업
- 어노테이션 기반 조건부 실행 (`@HasParam`, `@IsAdmin`, `@Throttle` 등)
- KakaoLink 통합

---

### 3. DecoratorExample.kt
**함수형 데코레이터 사용 예제**

함수형 프로그래밍 스타일로 데코레이터를 조합하여 핸들러를 구성합니다.

```bash
# 실행 방법
export IRIS_URL="ws://your-iris-server-url"
./gradlew run -PmainClass=com.spear.iriskt.examples.DecoratorExampleKt
```

**주요 기능:**
- `Decorators.hasParam`: 파라미터 필수 검증
- `Decorators.isAdmin`: 관리자 권한 검증
- `Decorators.isReply`: 답장 메시지 검증
- `Decorators.isNotBanned`: 차단 사용자 필터링
- `Decorators.hasRole`: 역할 기반 접근 제어
- `Decorators.allowedRoom`: 방 제한
- `Decorators.compose`: 여러 데코레이터 조합

---

### 4. KakaoLinkExample.kt
**카카오링크 전송 예제**

KakaoLink를 사용하여 템플릿 메시지를 전송하는 예제입니다.

```bash
# 실행 방법
export IRIS_URL="ws://your-iris-server-url"
export KAKAOLINK_APP_KEY="your-app-key"
export KAKAOLINK_ORIGIN="https://your-origin.com"
./gradlew run -PmainClass=com.spear.iriskt.examples.KakaoLinkExampleKt
```

**주요 기능:**
- IrisLink 초기화
- 템플릿 메시지 전송
- 예외 처리 (`KakaoLinkException` 계열)

---

## 🚀 실행 방법

### 환경 변수 설정

모든 예제를 실행하기 전에 필요한 환경 변수를 설정해야 합니다:

**필수:**
```bash
export IRIS_URL="ws://your-iris-server-url"
```

**선택 (KakaoLink 사용 시):**
```bash
export KAKAOLINK_APP_KEY="your-kakao-app-key"
export KAKAOLINK_ORIGIN="https://your-service.com"
```

### Gradle로 실행

```bash
# 프로젝트 루트에서
./gradlew run -PmainClass=com.spear.iriskt.examples.SimpleExampleKt
```

### IDE에서 실행

1. IntelliJ IDEA 또는 Android Studio에서 프로젝트 열기
2. 예제 파일의 `main` 함수 옆의 실행 버튼 클릭
3. 환경 변수 설정 (Run Configuration)

---

## 📚 학습 순서 권장

1. **SimpleExample.kt** - 기본 개념 이해
2. **DecoratorExample.kt** - 함수형 데코레이터 학습
3. **ControllerExample.kt** - 컨트롤러 방식 학습 (실전 권장)
4. **KakaoLinkExample.kt** - KakaoLink 통합

---

## 💡 팁

### 1. 로그 레벨 조정
```kotlin
Bot(
    botName = "MyBot",
    irisUrl = irisUrl,
    options = BotOptions(
        logLevel = LogLevel.DEBUG // DEBUG, INFO, WARN, ERROR
    )
)
```

### 2. 차단 사용자 설정
```kotlin
Bot(
    botName = "MyBot",
    irisUrl = irisUrl,
    options = BotOptions(
        bannedUsers = setOf(123456789L, 987654321L)
    )
)
```

### 3. 워커 수 조정
```kotlin
Bot(
    botName = "MyBot",
    irisUrl = irisUrl,
    options = BotOptions(
        maxWorkers = 8 // 동시 처리 가능한 이벤트 수
    )
)
```

---

## 🐛 문제 해결

### WebSocket 연결 실패
- IRIS_URL 환경 변수가 올바르게 설정되었는지 확인
- 네트워크 연결 상태 확인
- 방화벽 설정 확인

### KakaoLink 전송 실패
- KAKAOLINK_APP_KEY와 KAKAOLINK_ORIGIN이 올바른지 확인
- 템플릿 ID가 유효한지 확인
- 템플릿 인자가 올바른지 확인

### 명령어가 동작하지 않음
- Prefix 설정 확인 (`@Prefix("!")`)
- 명령어 이름 확인
- 조건부 어노테이션 확인 (`@HasParam`, `@IsAdmin` 등)

---

## 📖 추가 자료

- [메인 README](../README.md)
- [irispy-client](https://github.com/irisdev/irispy-client)
