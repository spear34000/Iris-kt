# Iris-kt 프로젝트 구조

이 문서는 Iris-kt 프로젝트의 디렉토리 구조와 각 파일의 역할을 설명합니다.

## 디렉토리 구조

```
Iris-kt/
├── src/main/kotlin/com/spear/iriskt/
│   ├── Bot.kt                          # 메인 Bot 클래스
│   ├── annotations/                    # 어노테이션 정의
│   │   ├── Command.kt                  # 명령어 관련 어노테이션
│   │   ├── CommandAnnotations.kt       # 추가 명령어 어노테이션
│   │   ├── CommandProcessor.kt         # 명령어 프로세서
│   │   ├── Condition.kt                # 조건부 실행 어노테이션
│   │   ├── Controller.kt               # 컨트롤러 타입 어노테이션
│   │   ├── FeedType.kt                 # 피드 타입 어노테이션
│   │   ├── MessageController.kt        # 메시지 컨트롤러 기본 클래스
│   │   ├── MessageType.kt              # 메시지 타입 어노테이션
│   │   └── Schedule.kt                 # 스케줄링 어노테이션
│   ├── api/                            # API 클라이언트
│   │   └── IrisApiClient.kt            # IRIS API 클라이언트
│   ├── core/                           # 핵심 기능
│   │   ├── BatchScheduler.kt           # 배치 스케줄러
│   │   ├── ConditionManager.kt         # 조건 관리자
│   │   ├── Config.kt                   # 설정 관리
│   │   ├── ControllerManager.kt        # 컨트롤러 관리자
│   │   ├── IrisBot.kt                  # IrisBot 구현
│   │   ├── IrisLink.kt                 # KakaoLink 클라이언트
│   │   ├── KakaoLinkExceptions.kt      # KakaoLink 예외 클래스
│   │   ├── Logger.kt                   # 로거 관리
│   │   └── ThrottleManager.kt          # 스로틀링 관리자
│   ├── internal/                       # 내부 구현
│   │   └── EventEmitter.kt             # 이벤트 에미터
│   ├── models/                         # 데이터 모델
│   │   ├── ChatContext.kt              # 채팅 컨텍스트
│   │   ├── ChatImage.kt                # 채팅 이미지
│   │   ├── ChatModels.kt               # 채팅 모델
│   │   ├── ErrorContext.kt             # 에러 컨텍스트
│   │   ├── FeedType.kt                 # 피드 타입
│   │   ├── Message.kt                  # 메시지 모델
│   │   ├── MessageType.kt              # 메시지 타입
│   │   ├── Room.kt                     # 방 모델
│   │   └── User.kt                     # 사용자 모델
│   ├── util/                           # 유틸리티
│   │   ├── BotUtils.kt                 # Bot 유틸리티 함수
│   │   ├── Decorators.kt               # 함수형 데코레이터
│   │   └── KeyValueStore.kt            # 키-값 저장소
│   └── android/                        # Android 관련 (선택)
│       ├── AndroidBotManager.kt
│       ├── AndroidBotService.kt
│       └── BotScript.kt
├── examples/                           # 예제 코드
│   ├── README.md                       # 예제 설명
│   ├── SimpleExample.kt                # 기본 예제
│   ├── ControllerExample.kt            # 컨트롤러 예제
│   ├── DecoratorExample.kt             # 데코레이터 예제
│   └── KakaoLinkExample.kt             # KakaoLink 예제
├── docs/                               # 문서
│   ├── API_REFERENCE_KR.md             # API 레퍼런스 (한국어)
│   ├── MIGRATION_FROM_NODE_IRIS.md     # 마이그레이션 가이드
│   └── PROJECT_STRUCTURE.md            # 프로젝트 구조 (이 문서)
├── build.gradle.kts                    # Gradle 빌드 설정
├── settings.gradle.kts                 # Gradle 설정
├── gradle.properties                   # Gradle 속성
├── gradlew                             # Gradle Wrapper (Unix)
├── gradlew.bat                         # Gradle Wrapper (Windows)
├── README.md                           # 프로젝트 README
└── LICENSE                             # 라이선스

```

## 주요 파일 설명

### 핵심 클래스

#### Bot.kt
- 봇의 메인 클래스
- WebSocket 연결 관리
- 이벤트 핸들러 등록
- API 클라이언트, 스케줄러, IrisLink 접근 제공

#### IrisApiClient.kt
- IRIS 서버와의 HTTP 통신
- 메시지 전송, 이미지 전송, 쿼리 등

#### IrisLink.kt
- KakaoLink 템플릿 메시지 전송
- 예외 처리

### 어노테이션

#### Controller.kt
컨트롤러 타입 정의:
- `@Controller` / `@ChatController`
- `@MessageController`
- `@NewMemberController`
- `@DeleteMemberController`
- `@FeedController`
- `@UnknownController`
- `@ErrorController`
- `@BatchController`
- `@BootstrapController`

#### Command.kt
명령어 관련:
- `@BotCommand` - 봇 명령어 등록
- `@Command` - 무조건 호출되는 명령어
- `@HelpCommand` - 도움말 명령어
- `@Prefix` - 컨트롤러 prefix
- `@MethodPrefix` - 메소드별 prefix

#### Condition.kt / CommandAnnotations.kt
조건부 실행:
- `@HasParam` - 파라미터 필수
- `@IsAdmin` - 관리자만
- `@HasRole` - 특정 역할만
- `@IsNotBanned` - 차단되지 않은 사용자
- `@IsReply` - 답장 메시지만
- `@AllowedRoom` - 특정 방만
- `@Throttle` - 사용 빈도 제한

#### MessageType.kt
메시지 타입별 처리:
- `@OnMessage`
- `@OnNormalMessage`
- `@OnPhotoMessage`
- `@OnVideoMessage`
- 등등...

#### FeedType.kt
피드 타입별 처리:
- `@OnFeedMessage`
- `@OnInviteUserFeed`
- `@OnLeaveUserFeed`
- `@OnPromoteManagerFeed`
- 등등...

#### Schedule.kt
스케줄링:
- `@Schedule` - 주기적 실행
- `@ScheduleMessage` - 스케줄된 메시지 처리
- `@Bootstrap` - 부트스트랩 실행

### 모델

#### ChatContext.kt
채팅 컨텍스트:
- 방, 발신자, 메시지 정보
- `reply()`, `replyMedia()` 등의 메서드

#### Message.kt
메시지 정보:
- ID, 타입, 내용, 첨부파일
- `command`, `param`, `hasParam` 등의 계산된 속성

#### User.kt
사용자 정보:
- ID, 이름
- `getName()`, `getType()` 메서드

#### Room.kt
방 정보:
- ID, 이름
- `getType()` 메서드

### 유틸리티

#### BotUtils.kt
유틸리티 함수 모음:
- 스케줄링 관련
- 스로틀링 관리
- 정보 조회
- 디버깅

#### Decorators.kt
함수형 데코레이터:
- `hasParam`, `isAdmin`, `isReply` 등
- `compose` - 데코레이터 조합

#### ThrottleManager.kt
스로틀링 관리:
- 사용 빈도 제한
- 스로틀 해제

### 관리자

#### ControllerManager.kt
컨트롤러 관리:
- 컨트롤러 등록
- 핸들러 처리
- 조건 검증

#### BatchScheduler.kt
배치 스케줄러:
- 주기적 작업 실행
- 메시지 스케줄링

#### EventEmitter.kt
이벤트 에미터:
- 비동기 이벤트 디스패치
- 에러 전파

## 의존성

### 주요 라이브러리

- **Kotlin**: 1.9.24
- **Kotlinx Coroutines**: 1.8.1 - 비동기 처리
- **Kotlinx Serialization**: 1.6.3 - JSON 직렬화
- **Ktor Client**: 2.3.9 - HTTP/WebSocket 클라이언트
- **Kotlin Logging**: 3.0.5 - 로깅
- **SLF4J**: 2.0.13 - 로깅 구현

### Android 의존성 (선택)

- **AndroidX Core**: 1.12.0
- **AppCompat**: 1.6.1
- **Material**: 1.10.0
- **ConstraintLayout**: 2.1.4
- **Lifecycle**: 2.6.2
- **Preference**: 1.2.1

## 빌드 설정

### build.gradle.kts

주요 설정:
- Kotlin 플러그인
- Android 플러그인 (선택)
- Serialization 플러그인
- 의존성 관리

### settings.gradle.kts

프로젝트 설정:
- 저장소 설정
- 프로젝트 이름

## 개발 가이드

### 새 기능 추가

1. **새 어노테이션 추가**
   - `annotations/` 디렉토리에 어노테이션 정의
   - `ControllerManager.kt`에 처리 로직 추가

2. **새 모델 추가**
   - `models/` 디렉토리에 데이터 클래스 정의
   - 필요시 직렬화 어노테이션 추가

3. **새 유틸리티 추가**
   - `util/` 디렉토리에 함수 추가
   - `BotUtils` 또는 별도 파일로 구성

4. **새 예제 추가**
   - `examples/` 디렉토리에 예제 파일 추가
   - `examples/README.md` 업데이트

### 테스트

```bash
# 빌드
./gradlew build

# 테스트
./gradlew test

# 특정 예제 실행
./gradlew run -PmainClass=com.spear.iriskt.examples.SimpleExampleKt
```

### 문서 업데이트

문서 수정 시:
1. `README.md` - 메인 문서
2. `docs/API_REFERENCE_KR.md` - API 레퍼런스
3. `examples/README.md` - 예제 설명
4. `docs/MIGRATION_FROM_NODE_IRIS.md` - 마이그레이션 가이드

## 참고 자료

- [메인 README](../README.md)
- [API 레퍼런스](./API_REFERENCE_KR.md)
- [예제 모음](../examples/README.md)
- [마이그레이션 가이드](./MIGRATION_FROM_NODE_IRIS.md)
