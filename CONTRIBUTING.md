# Contributing to Iris-kt

Iris-kt에 기여해주셔서 감사합니다! 🎉

## 행동 강령

이 프로젝트는 모든 기여자가 존중받는 환경을 유지하기 위해 행동 강령을 따릅니다. 참여함으로써 이 강령을 준수하는 데 동의하는 것으로 간주됩니다.

## 기여 방법

### 버그 리포트

버그를 발견하셨나요? 다음 정보를 포함하여 이슈를 생성해주세요:

1. 버그에 대한 명확한 설명
2. 재현 단계
3. 예상 동작과 실제 동작
4. 환경 정보 (OS, Kotlin 버전, JDK 버전 등)
5. 관련 로그나 스크린샷

### 기능 제안

새로운 기능을 제안하고 싶으신가요?

1. 먼저 이슈를 생성하여 제안을 논의해주세요
2. 기능의 필요성과 사용 사례를 설명해주세요
3. 가능하다면 구현 방법도 제안해주세요

### Pull Request

1. **Fork** 저장소를 포크합니다
2. **Branch** 새로운 브랜치를 생성합니다
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. **Commit** 변경 사항을 커밋합니다
   ```bash
   git commit -m 'Add some amazing feature'
   ```
4. **Push** 브랜치에 푸시합니다
   ```bash
   git push origin feature/amazing-feature
   ```
5. **Pull Request** PR을 생성합니다

### 커밋 메시지 가이드

명확한 커밋 메시지를 작성해주세요:

- `feat:` 새로운 기능
- `fix:` 버그 수정
- `docs:` 문서 변경
- `style:` 코드 포맷팅
- `refactor:` 코드 리팩토링
- `test:` 테스트 추가
- `chore:` 빌드 작업, 패키지 관리

예시:
```
feat: Add HTTP webhook mode support
fix: Fix memory leak in EventEmitter
docs: Update API reference for ChatContext
```

## 개발 환경 설정

### 요구사항

- JDK 17 이상
- Kotlin 1.9.24
- Gradle 8.9

### 설정

1. 저장소 클론
   ```bash
   git clone https://github.com/spear34000/Iris-kt.git
   cd Iris-kt
   ```

2. 빌드
   ```bash
   ./gradlew build
   ```

3. 테스트
   ```bash
   ./gradlew test
   ```

## 코딩 스타일

- Kotlin 공식 코딩 컨벤션을 따릅니다
- 들여쓰기는 4 스페이스를 사용합니다
- 의미 있는 변수명과 함수명을 사용합니다
- 복잡한 로직에는 주석을 추가합니다
- KDoc 주석으로 공개 API를 문서화합니다

## 테스트

- 새로운 기능에는 테스트를 추가해주세요
- 모든 테스트가 통과하는지 확인해주세요
- 테스트 커버리지를 유지하거나 향상시켜주세요

## 문서

- 새로운 기능은 문서화해주세요
- API 변경 사항은 API 레퍼런스를 업데이트해주세요
- 예제 코드를 추가하면 더욱 좋습니다

## 질문이 있으신가요?

- GitHub Issues를 통해 질문해주세요
- 또는 이메일로 연락주세요

## 라이선스

기여하신 코드는 MIT 라이선스 하에 배포됩니다.

---

다시 한번 기여해주셔서 감사합니다! 🙏
