# NSIS Support — JetBrains IDE 플러그인

NSIS(Nullsoft Scriptable Install System) 설치 스크립트(`.nsi`, `.nsh`)를 위한 JetBrains IDE 플러그인.

플랫폼 모듈(`com.intellij.modules.platform`)만 쓰기 때문에 IntelliJ IDEA뿐 아니라
**PyCharm · WebStorm · CLion · GoLand** 등 모든 JetBrains IDE 에 설치된다.

## 기능

| 기능 | 내용 |
|---|---|
| 문법 강조 | 명령어 / `!` 전처리기 / `$변수` / `${정의}` / 문자열 / 숫자 / 주석(`;` `#` `/* */`) |
| 구조 뷰 | `Section` · `SectionGroup` · `Function` · `!macro` · `PageEx` 목록과 이름 |
| 코드 접기 | 위 블록 단위로 접기 |
| 자동 완성 | NSIS 명령 약 200개, `!` 지시자, `$` 내장 상수, MUI2 define·매크로, LogicLib `${If}` 계열, 그리고 **현재 파일의** `!define` · `Var` · `Function` · `!macro` |
| 문서 팝업 | **모든 명령·지시자**(234개)의 문법 한 줄 + 설명 (⌘Q / Ctrl+Q) |
| 파일로 이동 | `!include "MUI2.nsh"` · `File "logo.ico"` 등 경로 인자에서 ⌘/Ctrl+클릭 |
| 검사 | 아래 참조 |
| 빌드 | 에디터 우클릭 → **Compile with makensis** |

### `!include` 해석

`!include` 는 NSIS 와 같은 순서로 찾는다 — **스크립트 폴더 → `!addincludedir` 로 더한 폴더 →
NSIS 설치본의 `Include` 폴더**. 마지막 폴더 덕분에 `MUI2.nsh` · `LogicLib.nsh` 같은 표준 헤더도
⌘+클릭으로 열린다.

NSIS 설치본은 `NSISDIR` 환경 변수 → `makensis` 위치에서 찾는다
(윈도 `<NSIS>\Include`, Homebrew 등 유닉스 `<prefix>/share/nsis/Include`).

### 검사(Inspection) 항목

- **닫히지 않은 블록** — `Section` ↔ `SectionEnd`, `Function` ↔ `FunctionEnd`, `!macro` ↔ `!macroend` 등.
  `!ifdef` 류 조건부 컴파일이 있는 파일은 정적으로 짝을 맞출 수 없어 이 검사를 자동으로 끈다(오탐 방지).
- **없는 파일 참조** — `File`, `Icon`, `LicenseData`, `!define MUI_ICON` 등이 가리키는 경로가
  실제로 없으면 경고. 윈도 역슬래시 경로를 현재 OS 경로로 바꿔서 확인한다.
  `!include` 는 위 검색 경로 전체를 뒤진 뒤에 판단하며, **NSIS 설치본을 못 찾았고 폴더 없이 이름만 쓴**
  헤더(`MUI2.nsh`)는 표준 헤더인지 오타인지 가릴 근거가 없으므로 경고하지 않는다.
  `!include /nonfatal` 은 "없어도 괜찮다"는 선언이므로 검사에서 뺀다.
- **유니코드 준비 상태** — 비ASCII 문자가 있는데 `Unicode true` 가 없으면 경고.
  추가로 **BOM 이 없으면** 약한 경고 — NSIS 유니코드 빌드는 소스를 UTF-8 BOM(또는 UTF-16LE)으로 읽어야
  라이선스·마법사 문구가 깨지지 않는다.
- **오타 난 전처리기 지시자** — `!inclde` 같은 알 수 없는 `!` 지시자.

## 요구 사항

- **IDE 2026.2 (build 262) 이상** — `since-build=262`, until-build 없음(상위 버전 자동 허용)
- 빌드에는 JDK 만 있으면 된다(Gradle 은 wrapper 가 알아서 받는다).
  macOS 에 JDK 가 따로 없다면 JetBrains IDE 에 들어 있는 JBR 을 쓰면 된다.

## 빌드

```bash
export JAVA_HOME="/Applications/PyCharm.app/Contents/jbr/Contents/Home"   # 또는 WebStorm.app
./gradlew buildPlugin
```

Gradle wrapper 를 커밋해 두었으므로 Gradle 을 따로 설치할 필요는 없다 —
`./gradlew` 가 `gradle/wrapper/gradle-wrapper.properties` 에 적힌 배포본(9.6.1)을 처음 한 번 내려받는다.

산출물: `build/distributions/nsis-idea-plugin-1.0.0.zip`

처음 빌드할 때 Gradle 이 IntelliJ Platform SDK(약 1~2GB)를 내려받는다.

검증 완료 조합: **Gradle 9.6.1 · JBR 25 · IntelliJ Platform 2026.2 ·
Kotlin 2.4.10 · IntelliJ Platform Gradle Plugin 2.18.1** — 컴파일 경고 0,
`verifyPluginProjectConfiguration` 지적 사항 0.

> Java/Kotlin 타깃은 일부러 지정하지 않는다. IntelliJ Platform 플러그인이 대상 플랫폼에 맞는
> 값을 `afterEvaluate` 에서 설정하므로 빌드 스크립트에서 못박아도 덮어써진다.
> `sinceBuild` 를 대상 플랫폼과 맞춰 두는 쪽이 맞다.

### 설치

IDE → **Settings → Plugins → ⚙︎ → Install Plugin from Disk…** 에서 위 zip 선택.

### IDE 에서 바로 띄워 보기

```bash
./gradlew runIde
```

## 설정

**Settings → Tools → NSIS** 에서 `makensis` 경로를 지정한다.
비워 두면 PATH → `/opt/homebrew/bin` → `/usr/local/bin` → (윈도) `C:\Program Files (x86)\NSIS`
순으로 자동 탐색한다.

## 구조

```
NsisLanguage.kt          Language · FileType · PsiFile
NsisTokenTypes.kt        토큰 종류
NsisLexer.kt             손으로 쓴 렉서 (줄 첫 낱말만 명령어로 인식)
NsisParserDefinition.kt  평평한 PSI — 구조는 NsisOutline 이 담당
NsisOutline.kt           줄 단위 분석기: 블록·선언·경로참조·문제점을 한 번에 수집
NsisKeywords.kt          명령/지시자/상수/MUI2/LogicLib 사전 + 문서
NsisSyntaxHighlighter.kt 색상 매핑
NsisColorSettingsPage.kt 색 설정 미리보기
NsisEditorSupport.kt     주석 토글 · 코드 접기
NsisStructureView.kt     구조 뷰
NsisCompletionContributor.kt  자동 완성
NsisAnnotator.kt         검사
NsisDocumentationProvider.kt  문서 팝업
NsisSettings.kt          makensis 경로 설정
CompileNsisAction.kt     makensis 실행
```

설계 메모: NSIS 는 줄 단위 명령 나열이라 깊은 PSI 트리를 만들 이득이 적다.
그래서 파서는 토큰을 평평하게 담기만 하고, 구조·검사·자동완성에 필요한 정보는
`NsisOutline` 이 텍스트를 한 번 훑어 모두 뽑아낸다. 문자열·주석 안의 내용은
`stripComments` 가 **길이를 유지한 채** 공백으로 지우기 때문에 줄 안 위치를
그대로 절대 오프셋으로 쓸 수 있다.

## 라이선스

MIT
