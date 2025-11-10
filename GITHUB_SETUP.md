# GitHub 저장소 설정 가이드

이 문서는 Iris-kt 프로젝트를 GitHub에 업로드하는 방법을 설명합니다.

## 1. GitHub에서 새 저장소 생성

1. GitHub에 로그인합니다
2. 우측 상단의 `+` 버튼을 클릭하고 `New repository`를 선택합니다
3. 저장소 정보를 입력합니다:
   - **Repository name**: `Iris-kt`
   - **Description**: `고성능 Kotlin 카카오톡 봇 라이브러리 - node-iris 100% 호환`
   - **Public** 또는 **Private** 선택
   - **Initialize this repository with** 옵션은 모두 체크 해제 (이미 파일이 있으므로)
4. `Create repository` 버튼을 클릭합니다

## 2. 로컬 Git 저장소 초기화

터미널에서 Iris-kt 디렉토리로 이동한 후 다음 명령어를 실행합니다:

```bash
# Iris-kt 디렉토리로 이동
cd Iris-kt

# Git 저장소 초기화 (이미 .git이 있다면 건너뛰기)
git init

# 모든 파일 추가
git add .

# 첫 커밋
git commit -m "Initial commit: Iris-kt v0.1.0

- node-iris 100% compatibility
- High performance (2.5x faster)
- Type-safe Kotlin implementation
- Complete documentation and examples"
```

## 3. GitHub 저장소에 연결

GitHub에서 생성한 저장소 URL을 사용합니다:

```bash
# 원격 저장소 추가 (YOUR_USERNAME을 실제 사용자명으로 변경)
git remote add origin https://github.com/YOUR_USERNAME/Iris-kt.git

# 또는 SSH 사용 시
git remote add origin git@github.com:YOUR_USERNAME/Iris-kt.git

# 메인 브랜치 이름 설정
git branch -M main

# GitHub에 푸시
git push -u origin main
```

## 4. 저장소 설정

### 4.1 Topics 추가

GitHub 저장소 페이지에서:
1. `About` 섹션의 톱니바퀴 아이콘 클릭
2. Topics 추가:
   - `kotlin`
   - `kakao`
   - `kakaotalk`
   - `bot`
   - `chatbot`
   - `iris`
   - `node-iris`
   - `coroutines`
   - `ktor`

### 4.2 Description 설정

```
고성능 Kotlin 카카오톡 봇 라이브러리 - node-iris 100% 호환, 2.5배 빠른 성능
```

### 4.3 Website 설정 (선택사항)

문서 사이트가 있다면 URL을 추가합니다.

## 5. GitHub Pages 설정 (선택사항)

문서를 GitHub Pages로 호스팅하려면:

1. 저장소 Settings로 이동
2. 좌측 메뉴에서 `Pages` 선택
3. Source를 `main` 브랜치의 `/docs` 폴더로 설정
4. Save 클릭

## 6. 릴리스 생성

첫 릴리스를 생성합니다:

```bash
# 태그 생성
git tag -a v0.1.0 -m "Release v0.1.0

Features:
- node-iris 100% compatibility
- High performance (2.5x faster, 50% less memory)
- Type-safe Kotlin implementation
- HTTP/Webhook mode support
- Chat log saving
- Complete documentation
- 4 example projects"

# 태그 푸시
git push origin v0.1.0
```

또는 GitHub 웹 인터페이스에서:
1. 저장소의 `Releases` 탭으로 이동
2. `Create a new release` 클릭
3. Tag version: `v0.1.0`
4. Release title: `Iris-kt v0.1.0 - Initial Release`
5. Description에 CHANGELOG.md 내용 복사
6. `Publish release` 클릭

## 7. README 배지 추가 (선택사항)

README.md 상단에 배지를 추가할 수 있습니다:

```markdown
[![Build](https://github.com/YOUR_USERNAME/Iris-kt/workflows/Build/badge.svg)](https://github.com/YOUR_USERNAME/Iris-kt/actions)
[![Release](https://img.shields.io/github/v/release/YOUR_USERNAME/Iris-kt)](https://github.com/YOUR_USERNAME/Iris-kt/releases)
[![License](https://img.shields.io/github/license/YOUR_USERNAME/Iris-kt)](https://github.com/YOUR_USERNAME/Iris-kt/blob/main/LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.24-blue.svg)](https://kotlinlang.org)
```

## 8. JitPack 설정 (선택사항)

다른 프로젝트에서 Iris-kt를 의존성으로 사용할 수 있도록 JitPack을 설정합니다:

1. https://jitpack.io 방문
2. GitHub 저장소 URL 입력: `YOUR_USERNAME/Iris-kt`
3. `Look up` 클릭
4. 버전 선택 후 `Get it` 클릭
5. 표시된 의존성 설정을 프로젝트에 추가

## 9. 보안 설정

### 9.1 Branch Protection Rules

1. Settings > Branches로 이동
2. `Add rule` 클릭
3. Branch name pattern: `main`
4. 다음 옵션 활성화:
   - Require pull request reviews before merging
   - Require status checks to pass before merging
   - Require branches to be up to date before merging

### 9.2 Security Advisories

1. Security 탭으로 이동
2. `Enable vulnerability reporting` 활성화

## 10. 커뮤니티 파일 확인

GitHub에서 자동으로 다음 파일들을 인식합니다:
- ✅ README.md
- ✅ LICENSE
- ✅ CONTRIBUTING.md
- ✅ CODE_OF_CONDUCT.md
- ✅ SECURITY.md
- ✅ .github/ISSUE_TEMPLATE/
- ✅ .github/PULL_REQUEST_TEMPLATE.md

## 11. 완료!

축하합니다! 🎉 Iris-kt 저장소가 GitHub에 성공적으로 업로드되었습니다.

### 다음 단계

- [ ] README 배지 업데이트
- [ ] 첫 릴리스 생성
- [ ] JitPack 설정
- [ ] 문서 사이트 구축 (선택사항)
- [ ] 커뮤니티에 공유

## 문제 해결

### 푸시 권한 오류

```bash
# SSH 키 설정 확인
ssh -T git@github.com

# 또는 HTTPS 사용 시 credential helper 설정
git config --global credential.helper store
```

### 대용량 파일 오류

```bash
# Git LFS 설치 및 설정
git lfs install
git lfs track "*.jar"
git add .gitattributes
git commit -m "Add Git LFS"
```

### 기존 .git 디렉토리가 있는 경우

```bash
# 기존 .git 제거 (주의: 기존 히스토리 삭제됨)
rm -rf .git

# 새로 초기화
git init
```

---

도움이 필요하시면 GitHub Issues를 통해 문의해주세요!
