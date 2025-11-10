# Security Policy

## Supported Versions

현재 지원되는 버전:

| Version | Supported          |
| ------- | ------------------ |
| 0.1.x   | :white_check_mark: |

## Reporting a Vulnerability

보안 취약점을 발견하셨나요? 다음 방법으로 보고해주세요:

1. **공개 이슈를 생성하지 마세요** - 보안 문제는 비공개로 처리됩니다
2. GitHub Security Advisory를 통해 보고해주세요
3. 또는 이메일로 직접 연락주세요

### 보고 시 포함할 정보

- 취약점에 대한 설명
- 재현 단계
- 영향 범위
- 가능한 해결 방법 (선택사항)

### 응답 시간

- 초기 응답: 48시간 이내
- 상태 업데이트: 7일마다
- 수정 배포: 심각도에 따라 다름

## Security Best Practices

Iris-kt를 사용할 때 다음 보안 모범 사례를 따르세요:

### 1. 환경 변수 사용

민감한 정보는 환경 변수로 관리하세요:

```kotlin
val bot = Bot(
    botName = "MyBot",
    irisUrl = System.getenv("IRIS_URL")!!,
    options = BotOptions(
        kakaoLinkAppKey = System.getenv("KAKAOLINK_APP_KEY"),
        kakaoLinkOrigin = System.getenv("KAKAOLINK_ORIGIN")
    )
)
```

### 2. 사용자 입력 검증

사용자 입력은 항상 검증하세요:

```kotlin
@BotCommand("search", "검색")
@HasParam
suspend fun search(context: ChatContext) {
    val query = context.message.param
    if (query.length > 100) {
        context.reply("검색어가 너무 깁니다")
        return
    }
    // 검색 로직
}
```

### 3. 권한 확인

민감한 작업은 권한을 확인하세요:

```kotlin
@BotCommand("admin", "관리자 명령")
@IsAdmin
suspend fun adminCommand(context: ChatContext) {
    // 관리자만 실행 가능
}
```

### 4. 스로틀링 사용

남용을 방지하기 위해 스로틀링을 사용하세요:

```kotlin
@BotCommand("api", "API 호출")
@Throttle(maxCalls = 10, timeWindowMs = 60000)
suspend fun apiCall(context: ChatContext) {
    // 1분에 10번만 호출 가능
}
```

### 5. 로그 관리

민감한 정보를 로그에 남기지 마세요:

```kotlin
// ❌ 나쁜 예
logger.info("User password: ${password}")

// ✅ 좋은 예
logger.info("User authenticated: ${userId}")
```

## Disclosure Policy

보안 취약점이 수정되면:

1. 패치가 포함된 새 버전을 릴리스합니다
2. CHANGELOG에 보안 수정 사항을 기록합니다
3. GitHub Security Advisory를 게시합니다
4. 보고자에게 크레딧을 제공합니다 (원하는 경우)

## Contact

보안 관련 문의: GitHub Security Advisory 또는 이메일

---

보안 문제를 책임감 있게 보고해주셔서 감사합니다! 🙏
