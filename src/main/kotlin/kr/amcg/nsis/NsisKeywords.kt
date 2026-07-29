package kr.amcg.nsis

/**
 * NSIS 어휘 사전. 모든 조회 키는 **소문자**다 (NSIS 는 대소문자를 안 가림).
 * 화면 표시·자동완성에는 [DISPLAY] 로 원래 표기를 복원한다.
 */
object NsisKeywords {

    /** 블록을 여닫는 키워드 */
    private val BLOCK_NAMES = listOf(
        "Section", "SectionEnd", "SectionGroup", "SectionGroupEnd",
        "SubSection", "SubSectionEnd", "Function", "FunctionEnd",
        "PageEx", "PageExEnd",
    )

    /** 스크립트 속성 (컴파일 타임) */
    private val ATTRIBUTES = listOf(
        "Name", "OutFile", "Unicode", "Caption", "SubCaption", "BrandingText", "Icon",
        "InstallDir", "InstallDirRegKey", "InstallButtonText", "InstallColors", "InstProgressFlags",
        "InstType", "InstTypeGetText", "InstTypeSetText", "SectionIn",
        "UninstallCaption", "UninstallIcon", "UninstallText", "UninstallSubCaption",
        "RequestExecutionLevel", "ManifestDPIAware", "ManifestSupportedOS", "ManifestLongPathAware",
        "XPStyle", "WindowIcon", "AutoCloseWindow", "ChangeUI", "CheckBitmap", "CompletedText",
        "ComponentText", "CRCCheck", "DetailsButtonText", "DirText", "DirVar", "DirVerify",
        "FileErrorText", "LicenseBkColor", "LicenseData", "LicenseForceSelection", "LicenseText",
        "LoadLanguageFile", "MiscButtonText", "Page", "UninstPage", "ReserveFile", "SpaceTexts",
        "AllowRootDirInstall", "AllowSkipFiles", "BGFont", "BGGradient",
        "SetCompressor", "SetCompress", "SetCompressionLevel", "SetDatablockOptimize",
        "SetOverwrite", "SetDateSave", "SetPluginUnload", "SetFont",
        "VIAddVersionKey", "VIProductVersion", "VIFileVersion", "Target",
    )

    /** 런타임 명령 */
    private val RUNTIME = listOf(
        "Abort", "AddSize", "BringToFront", "Call", "CallInstDLL", "ClearErrors", "CopyFiles",
        "CreateDirectory", "CreateFont", "CreateShortCut", "Delete", "DeleteINISec", "DeleteINIStr",
        "DeleteRegKey", "DeleteRegValue", "DetailPrint", "EnableWindow", "EnumRegKey", "EnumRegValue",
        "Exch", "Exec", "ExecShell", "ExecShellWait", "ExecWait", "ExpandEnvStrings",
        "File", "FileClose", "FileOpen", "FileRead", "FileReadByte", "FileReadUTF16LE", "FileReadWord",
        "FileSeek", "FileWrite", "FileWriteByte", "FileWriteUTF16LE", "FileWriteWord",
        "FindClose", "FindFirst", "FindNext", "FindWindow", "FlushINI",
        "GetCurInstType", "GetCurrentAddress", "GetDlgItem", "GetDLLVersion", "GetDLLVersionLocal",
        "GetErrorLevel", "GetFileTime", "GetFileTimeLocal", "GetFullPathName", "GetFunctionAddress",
        "GetInstDirError", "GetKnownFolderPath", "GetLabelAddress", "GetTempFileName", "GetWinVer",
        "Goto", "HideWindow", "IfAbort", "IfErrors", "IfFileExists", "IfRebootFlag", "IfRtlLanguage",
        "IfShellVarContextAll", "IfSilent", "IntCmp", "IntCmpU", "IntFmt", "IntOp",
        "IntPtrCmp", "IntPtrCmpU", "IntPtrOp", "IsWindow", "LockWindow", "LogSet", "LogText",
        "MessageBox", "Nop", "Pop", "Push", "Quit", "ReadEnvStr", "ReadINIStr",
        "ReadRegDWORD", "ReadRegStr", "Reboot", "RegDLL", "Rename", "RMDir", "SearchPath",
        "SendMessage", "SetAutoClose", "SetBrandingImage", "SetCtlColors", "SetDetailsPrint",
        "SetDetailsView", "SetErrorLevel", "SetErrors", "SetFileAttributes", "SetOutPath",
        "SetRebootFlag", "SetRegView", "SetShellVarContext", "SetSilent", "SetStaticBkColor",
        "ShowInstDetails", "ShowUninstDetails", "SilentInstall", "SilentUnInstall", "Sleep",
        "StrCmp", "StrCmpS", "StrCpy", "StrLen", "UnRegDLL", "Var",
        "WriteINIStr", "WriteRegBin", "WriteRegDWORD", "WriteRegExpandStr", "WriteRegMultiStr",
        "WriteRegNone", "WriteRegStr", "WriteUninstaller",
    )

    /** !전처리기 지시자 (앞의 ! 포함) */
    val PREPROCESSOR_DIRECTIVES = listOf(
        "!include", "!addincludedir", "!addplugindir", "!define", "!undef", "!ifdef", "!ifndef",
        "!if", "!ifmacrodef", "!ifmacrondef", "!else", "!endif", "!insertmacro", "!macro",
        "!macroend", "!macroundef", "!appendfile", "!cd", "!delfile", "!echo", "!error", "!warning",
        "!execute", "!makensis", "!packhdr", "!finalize", "!uninstfinalize", "!system", "!tempfile",
        "!searchparse", "!searchreplace", "!verbose", "!pragma", "!getdllversion", "!gettlbversion",
        "!assert",
    )

    /** $ 로 시작하는 내장 상수·레지스터 */
    val VARIABLES = listOf(
        "\$INSTDIR", "\$OUTDIR", "\$CMDLINE", "\$LANGUAGE", "\$PLUGINSDIR", "\$HWNDPARENT",
        "\$PROGRAMFILES", "\$PROGRAMFILES32", "\$PROGRAMFILES64",
        "\$COMMONFILES", "\$COMMONFILES32", "\$COMMONFILES64",
        "\$DESKTOP", "\$EXEDIR", "\$EXEFILE", "\$EXEPATH", "\$WINDIR", "\$SYSDIR", "\$TEMP",
        "\$STARTMENU", "\$SMPROGRAMS", "\$SMSTARTUP", "\$QUICKLAUNCH", "\$DOCUMENTS", "\$SENDTO",
        "\$RECENT", "\$FAVORITES", "\$MUSIC", "\$PICTURES", "\$VIDEOS", "\$NETHOOD", "\$FONTS",
        "\$TEMPLATES", "\$APPDATA", "\$LOCALAPPDATA", "\$PRINTHOOD", "\$INTERNET_CACHE",
        "\$COOKIES", "\$HISTORY", "\$PROFILE", "\$USERPROFILE", "\$ADMINTOOLS",
        "\$RESOURCES", "\$RESOURCES_LOCALIZED", "\$CDBURN_AREA",
    ) + (0..9).map { "\$$it" } + (0..9).map { "\$R$it" }

    /** MUI2 가 읽는 !define 이름 */
    val MUI_DEFINES = listOf(
        "MUI_ICON", "MUI_UNICON", "MUI_ABORTWARNING", "MUI_UNABORTWARNING", "MUI_BGCOLOR",
        "MUI_HEADERIMAGE", "MUI_HEADERIMAGE_BITMAP", "MUI_HEADERIMAGE_BITMAP_NOSTRETCH",
        "MUI_HEADERIMAGE_UNBITMAP", "MUI_HEADERIMAGE_UNBITMAP_NOSTRETCH", "MUI_HEADERIMAGE_RIGHT",
        "MUI_WELCOMEFINISHPAGE_BITMAP", "MUI_UNWELCOMEFINISHPAGE_BITMAP",
        "MUI_WELCOMEPAGE_TITLE", "MUI_WELCOMEPAGE_TEXT", "MUI_WELCOMEPAGE_TITLE_3LINES",
        "MUI_FINISHPAGE_TITLE", "MUI_FINISHPAGE_TEXT", "MUI_FINISHPAGE_TITLE_3LINES",
        "MUI_FINISHPAGE_NOAUTOCLOSE", "MUI_FINISHPAGE_RUN", "MUI_FINISHPAGE_RUN_TEXT",
        "MUI_FINISHPAGE_SHOWREADME", "MUI_FINISHPAGE_SHOWREADME_TEXT", "MUI_FINISHPAGE_LINK",
        "MUI_FINISHPAGE_LINK_LOCATION",
        "MUI_LICENSEPAGE_CHECKBOX", "MUI_LICENSEPAGE_CHECKBOX_TEXT", "MUI_LICENSEPAGE_RADIOBUTTONS",
        "MUI_LICENSEPAGE_TEXT_TOP", "MUI_LICENSEPAGE_TEXT_BOTTOM",
        "MUI_COMPONENTSPAGE_NODESC", "MUI_COMPONENTSPAGE_SMALLDESC", "MUI_COMPONENTSPAGE_TEXT_TOP",
        "MUI_DIRECTORYPAGE_TEXT_TOP", "MUI_DIRECTORYPAGE_TEXT_DESTINATION",
        "MUI_DIRECTORYPAGE_VARIABLE",
        "MUI_STARTMENUPAGE_DEFAULTFOLDER", "MUI_STARTMENUPAGE_NODISABLE",
        "MUI_STARTMENUPAGE_REGISTRY_ROOT", "MUI_STARTMENUPAGE_REGISTRY_KEY",
        "MUI_STARTMENUPAGE_REGISTRY_VALUENAME",
        "MUI_INSTFILESPAGE_COLORS", "MUI_INSTFILESPAGE_PROGRESSBAR",
        "MUI_CUSTOMFUNCTION_GUIINIT", "MUI_CUSTOMFUNCTION_ABORT",
        "MUI_PAGE_HEADER_TEXT", "MUI_PAGE_HEADER_SUBTEXT", "MUI_PAGE_CUSTOMFUNCTION_PRE",
        "MUI_PAGE_CUSTOMFUNCTION_SHOW", "MUI_PAGE_CUSTOMFUNCTION_LEAVE",
        "MUI_LANGDLL_DISPLAY", "MUI_LANGDLL_REGISTRY_ROOT", "MUI_LANGDLL_REGISTRY_KEY",
        "MUI_LANGDLL_REGISTRY_VALUENAME",
    )

    /** !insertmacro 로 부르는 MUI2 매크로 + LogicLib 구문 */
    val MACROS = listOf(
        "MUI_PAGE_WELCOME", "MUI_PAGE_LICENSE", "MUI_PAGE_COMPONENTS", "MUI_PAGE_DIRECTORY",
        "MUI_PAGE_STARTMENU", "MUI_PAGE_INSTFILES", "MUI_PAGE_FINISH",
        "MUI_UNPAGE_WELCOME", "MUI_UNPAGE_CONFIRM", "MUI_UNPAGE_LICENSE", "MUI_UNPAGE_COMPONENTS",
        "MUI_UNPAGE_DIRECTORY", "MUI_UNPAGE_INSTFILES", "MUI_UNPAGE_FINISH",
        "MUI_LANGUAGE", "MUI_RESERVEFILE_LANGDLL", "MUI_LANGDLL_DISPLAY",
        "MUI_DESCRIPTION_TEXT", "MUI_FUNCTION_DESCRIPTION_BEGIN", "MUI_FUNCTION_DESCRIPTION_END",
        "MUI_STARTMENU_WRITE_BEGIN", "MUI_STARTMENU_WRITE_END", "MUI_STARTMENU_GETFOLDER",
    )

    /** `${...}` 로 쓰는 LogicLib / 헤더 매크로 */
    val LOGICLIB = listOf(
        "If", "ElseIf", "Else", "EndIf", "Unless", "EndUnless", "AndIf", "OrIf",
        "Select", "Case", "Case2", "Case3", "CaseElse", "EndSelect",
        "Switch", "EndSwitch", "Default",
        "For", "ForEach", "Next", "ExitFor", "Do", "Loop", "While", "EndWhile",
        "DoWhile", "LoopWhile", "DoUntil", "LoopUntil", "ExitDo", "Continue", "Break",
        "GetParent", "GetFileName", "GetBaseName", "GetFileExt", "GetParameters", "GetOptions",
        "WordFind", "WordReplace", "StrRep", "StrLoc", "TrimNewLines", "GetSize", "GetTime",
        "GetSectionSize", "UnSelectSection", "SelectSection", "SetSectionFlag", "ClearSectionFlag",
    )

    val BLOCK_KEYWORDS: Set<String> = BLOCK_NAMES.map { it.lowercase() }.toSet()
    val INSTRUCTIONS: Set<String> = (ATTRIBUTES + RUNTIME).map { it.lowercase() }.toSet()

    /** 소문자 키 → 원래 표기 */
    val DISPLAY: Map<String, String> =
        (BLOCK_NAMES + ATTRIBUTES + RUNTIME).associateBy { it.lowercase() }

    val ALL_COMMANDS: List<String> = BLOCK_NAMES + ATTRIBUTES + RUNTIME

    /** 경로 인자를 받는 지시자 — 존재하지 않는 파일 경고에 쓴다 */
    val PATH_DIRECTIVES: Set<String> = setOf(
        "file", "!include", "icon", "uninstallicon", "licensedata", "reservefile",
        "checkbitmap", "changeui", "loadlanguagefile", "!addplugindir", "!addincludedir",
    )

    /** 경로 값을 갖는 MUI !define 이름 */
    val PATH_DEFINES: Set<String> = setOf(
        "MUI_ICON", "MUI_UNICON", "MUI_HEADERIMAGE_BITMAP", "MUI_HEADERIMAGE_UNBITMAP",
        "MUI_WELCOMEFINISHPAGE_BITMAP", "MUI_UNWELCOMEFINISHPAGE_BITMAP",
    )

    /** 자주 쓰는 명령의 한 줄 설명 + 문법 (문서 팝업·자동완성 타입텍스트) */
    val DOCS: Map<String, Pair<String, String>> = mapOf(
        "name" to ("Name name_string" to "설치 프로그램 이름. 창 제목과 기본 캡션에 쓰인다."),
        "outfile" to ("OutFile install_output.exe" to "makensis 가 만들어 낼 설치 실행 파일 경로."),
        "unicode" to ("Unicode true|false" to "유니코드 설치본 생성. 한국어 등 비ASCII 문자를 쓰면 반드시 true. 소스 파일도 UTF-8 BOM 또는 UTF-16LE 로 저장해야 한다."),
        "installdir" to ("InstallDir definstalldir" to "기본 설치 경로 (\$INSTDIR 초깃값)."),
        "installdirregkey" to ("InstallDirRegKey root_key subkey key_name" to "레지스트리에 기록된 이전 설치 경로를 \$INSTDIR 기본값으로 읽어온다."),
        "requestexecutionlevel" to ("RequestExecutionLevel none|user|highest|admin" to "UAC 실행 수준. 시스템 폴더에 쓰려면 admin."),
        "setcompressor" to ("SetCompressor [/SOLID] [/FINAL] zlib|bzip2|lzma" to "압축 방식. /SOLID lzma 가 가장 작다."),
        "setoutpath" to ("SetOutPath outpath" to "이후 File 명령의 대상 폴더(\$OUTDIR). 없으면 만든다."),
        "file" to ("File [/nonfatal] [/a] ([/r] file|/oname=out.dat infile)" to "빌드 시점에 파일을 설치본에 넣고, 설치 시 \$OUTDIR 에 푼다."),
        "delete" to ("Delete [/REBOOTOK] file" to "파일 삭제. 잠겨 있으면 /REBOOTOK 으로 재부팅 시 삭제 예약."),
        "rmdir" to ("RMDir [/r] [/REBOOTOK] directory_name" to "폴더 삭제. /r 은 하위까지."),
        "createdirectory" to ("CreateDirectory path_to_create" to "폴더 생성(중간 경로 포함)."),
        "createshortcut" to ("CreateShortCut link.lnk target.file [params] [icon.file] [icon_index]" to "바로 가기 생성."),
        "writeregstr" to ("WriteRegStr root_key subkey key_name value" to "레지스트리 문자열 값 기록."),
        "writeregdword" to ("WriteRegDWORD root_key subkey key_name value" to "레지스트리 DWORD 값 기록."),
        "deleteregvalue" to ("DeleteRegValue root_key subkey key_name" to "레지스트리 값 삭제."),
        "deleteregkey" to ("DeleteRegKey [/ifempty] root_key subkey" to "레지스트리 키 삭제."),
        "readregstr" to ("ReadRegStr user_var root_key subkey key_name" to "레지스트리 문자열 읽기. 실패 시 에러 플래그."),
        "writeuninstaller" to ("WriteUninstaller [Path\\]Uninst.exe" to "제거 프로그램 생성. Section 안에서만 쓸 수 있다."),
        "messagebox" to ("MessageBox mb_option_list messagebox_text [/SD ret] [ret label]" to "메시지 상자. /SD 는 무인 설치 시 기본 응답."),
        "detailprint" to ("DetailPrint user_message" to "설치 로그 창에 한 줄 출력."),
        "sendmessage" to ("SendMessage hwnd msg wparam lparam [user_var] [/TIMEOUT=ms]" to "윈도 메시지 전송. 폰트 설치 후 WM_FONTCHANGE 브로드캐스트 등에 쓴다."),
        "exec" to ("Exec command" to "프로그램 실행(기다리지 않음)."),
        "execwait" to ("ExecWait command [user_var]" to "프로그램 실행 후 종료까지 대기."),
        "execshell" to ("ExecShell action command [parameters] [SW_SHOWDEFAULT]" to "ShellExecute 로 실행."),
        "execshellwait" to ("ExecShellWait action command [parameters] [SW_SHOWDEFAULT]" to "ExecShell 과 같지만 그 프로그램이 끝날 때까지 기다린다."),
        "strcpy" to ("StrCpy user_var string [max_len] [start_offset]" to "문자열 대입/자르기."),
        "strcmp" to ("StrCmp str1 str2 jump_if_equal [jump_if_not_equal]" to "문자열 비교 후 분기(대소문자 무시)."),
        "intop" to ("IntOp user_var value1 OP [value2]" to "정수 연산 (+ - * / % | & ^ ~ ! << >> >>>)."),
        "push" to ("Push string" to "스택에 넣기."),
        "pop" to ("Pop user_var" to "스택에서 꺼내기."),
        "call" to ("Call function_name | :label | user_var" to "함수 호출."),
        "goto" to ("Goto label" to "레이블로 점프."),
        "abort" to ("Abort [user_message]" to "현재 페이지/설치를 중단."),
        "quit" to ("Quit" to "설치 프로그램 즉시 종료."),
        "iffileexists" to ("IfFileExists file_to_check_for jump_if_present [jump_otherwise]" to "파일/폴더 존재 확인 후 분기."),
        "iferrors" to ("IfErrors jump_if_error [jump_if_no_error]" to "에러 플래그 확인 후 분기. ClearErrors 로 먼저 지운다."),
        "clearerrors" to ("ClearErrors" to "에러 플래그 해제."),
        "setshellvarcontext" to ("SetShellVarContext current|all" to "\$SMPROGRAMS·\$DESKTOP 등을 사용자용/전체용 중 무엇으로 볼지 결정."),
        "setregview" to ("SetRegView 32|64|default" to "64비트 윈도에서 레지스트리 뷰 선택."),
        "var" to ("Var [/GLOBAL] var_name" to "사용자 변수 선언. Section/Function 밖이면 자동으로 전역."),
        "section" to ("Section [/o] [([!]|[-])section_name] [section_index_output]" to "설치 섹션 시작. - 로 시작하면 숨김, ! 는 굵게, /o 는 기본 해제."),
        "sectionend" to ("SectionEnd" to "Section 블록 끝."),
        "function" to ("Function [un.]function_name" to "함수 정의 시작. un. 접두사는 제거 프로그램용."),
        "functionend" to ("FunctionEnd" to "Function 블록 끝."),
        "page" to ("Page custom|license|components|directory|instfiles|uninstConfirm [pre] [show] [leave]" to "설치 마법사 페이지 추가. MUI2 를 쓰면 !insertmacro MUI_PAGE_* 로 대체한다."),
        "!include" to ("!include [/NONFATAL] [/CHARSET=…] file" to "헤더 파일 포함. MUI2.nsh, LogicLib.nsh 등."),
        "!define" to ("!define [/ifndef|/redef] name [value]" to "컴파일 타임 상수 정의. \${name} 으로 참조."),
        "!insertmacro" to ("!insertmacro macro_name [parameters]" to "매크로 펼치기."),
        "!macro" to ("!macro macro_name [parameters]" to "매크로 정의 시작."),
        "!macroend" to ("!macroend" to "매크로 정의 끝."),
        "!ifdef" to ("!ifdef name […]" to "정의돼 있으면 컴파일."),
        "!endif" to ("!endif" to "조건부 컴파일 끝."),

        // ---------- 나머지 전처리기 ----------
        "!ifndef" to ("!ifndef name […]" to "정의돼 있지 **않으면** 컴파일. 헤더 중복 포함 방지에 쓴다."),
        "!if" to ("!if [/FileExists] value [op value]" to "컴파일 타임 조건 분기. op 는 =, !=, <, >, &&, || 등."),
        "!else" to ("!else [if|ifdef|ifndef|ifmacrodef …]" to "조건부 컴파일의 반대 갈래. 뒤에 조건을 이어 붙이면 else-if 가 된다."),
        "!ifmacrodef" to ("!ifmacrodef name […]" to "매크로가 정의돼 있으면 컴파일."),
        "!ifmacrondef" to ("!ifmacrondef name […]" to "매크로가 정의돼 있지 않으면 컴파일."),
        "!undef" to ("!undef name" to "!define 로 만든 컴파일 타임 상수를 지운다."),
        "!macroundef" to ("!macroundef macro_name" to "!macro 로 만든 매크로 정의를 지운다."),
        "!addincludedir" to ("!addincludedir directory" to "!include 가 헤더를 찾을 폴더를 검색 경로에 더한다."),
        "!addplugindir" to ("!addplugindir [/x86-ansi|/x86-unicode|/amd64-unicode] directory" to "플러그인 DLL 을 찾을 폴더를 더한다."),
        "!echo" to ("!echo message" to "컴파일 로그에 메시지를 찍는다."),
        "!warning" to ("!warning [message]" to "컴파일 경고를 낸다. 빌드는 계속된다."),
        "!error" to ("!error [message]" to "컴파일을 오류로 세운다."),
        "!assert" to ("!assert expression" to "컴파일 타임 조건이 거짓이면 빌드를 세운다."),
        "!verbose" to ("!verbose 0|1|2|3|4|push|pop" to "컴파일 로그 수준. push/pop 으로 잠깐 낮췄다 되돌린다."),
        "!pragma" to ("!pragma warning disable|enable|error|default code" to "컴파일러 동작 조정 — 특정 경고 끄기 등."),
        "!system" to ("!system command [compare value]" to "빌드 중 시스템 명령을 실행한다."),
        "!execute" to ("!execute command [compare value]" to "!system 과 같다. 빌드 중 명령 실행."),
        "!makensis" to ("!makensis parameters [compare value]" to "중첩 makensis 실행 — 다른 스크립트를 빌드한다."),
        "!cd" to ("!cd new_path" to "컴파일러의 현재 폴더를 바꾼다. 이후 상대 경로의 기준이 된다."),
        "!tempfile" to ("!tempfile define" to "임시 파일 경로를 만들어 상수에 담는다."),
        "!delfile" to ("!delfile [/nonfatal] file" to "빌드 중 파일을 지운다."),
        "!appendfile" to ("!appendfile [/CHARSET=…] [/RawNL] file text" to "빌드 중 파일 끝에 한 줄 덧붙인다."),
        "!searchparse" to ("!searchparse [/ignorecase] [/noerrors] [/file] source string define […]" to "문자열이나 파일에서 값을 뽑아 컴파일 타임 상수로 만든다."),
        "!searchreplace" to ("!searchreplace define source search replace" to "치환 결과를 컴파일 타임 상수로 만든다."),
        "!getdllversion" to ("!getdllversion [/noerrors] localfile define_prefix" to "빌드 머신의 DLL 버전을 상수로 읽는다. VIProductVersion 채울 때 쓴다."),
        "!gettlbversion" to ("!gettlbversion [/noerrors] localfile define_prefix" to "타입 라이브러리 버전을 상수로 읽는다."),
        "!packhdr" to ("!packhdr tempfile command" to "헤더 블록을 외부 압축기로 다시 압축한다."),
        "!finalize" to ("!finalize command [compare value]" to "설치 실행 파일을 다 만든 뒤 명령 실행 — 코드 서명에 쓴다."),
        "!uninstfinalize" to ("!uninstfinalize command [compare value]" to "언인스톨러를 다 만든 뒤 명령 실행 — 코드 서명에 쓴다."),

        // ---------- 블록 ----------
        "sectiongroup" to ("SectionGroup [/e] [un.]name [section_index_output]" to "섹션 묶음 시작. /e 는 처음부터 펼쳐 보인다."),
        "sectiongroupend" to ("SectionGroupEnd" to "섹션 묶음 끝."),
        "subsection" to ("SubSection [/e] [un.]name [section_index_output]" to "SectionGroup 의 옛 이름. 새 스크립트는 SectionGroup 을 쓴다."),
        "subsectionend" to ("SubSectionEnd" to "SubSection 끝. 새 스크립트는 SectionGroupEnd 를 쓴다."),
        "pageex" to ("PageEx [un.]type" to "페이지 하나를 세밀하게 설정하는 블록. type 은 license, components, directory, instfiles 등."),
        "pageexend" to ("PageExEnd" to "PageEx 블록 끝."),
        "sectionin" to ("SectionIn insttype_index […] [RO]" to "이 섹션이 포함될 설치 유형 번호. RO 를 붙이면 사용자가 해제할 수 없다."),
        "addsize" to ("AddSize size_kb" to "이 섹션이 쓸 디스크 용량을 KB 만큼 더한다. File 로 안 넣는 파일을 만들 때 필요."),
        "uninstpage" to ("UninstPage page_type [pre] [show] [leave]" to "제거 마법사에 페이지를 더한다."),

        // ---------- 창·문구·모양 ----------
        "caption" to ("Caption caption" to "설치 창 제목."),
        "subcaption" to ("SubCaption page_number text" to "각 페이지 제목의 뒷부분."),
        "uninstallcaption" to ("UninstallCaption caption" to "제거 프로그램 창 제목."),
        "uninstallsubcaption" to ("UninstallSubCaption page_number text" to "제거 페이지 제목의 뒷부분."),
        "uninstalltext" to ("UninstallText text [subtext]" to "제거 확인 페이지 문구."),
        "brandingtext" to ("BrandingText [/TRIMLEFT|/TRIMRIGHT|/TRIMCENTER] text" to "설치 창 아래쪽에 깔리는 문구."),
        "completedtext" to ("CompletedText text" to "설치가 끝났을 때 표시할 문구."),
        "componenttext" to ("ComponentText [text] [subtext1] [subtext2]" to "컴포넌트 선택 페이지 안내 문구."),
        "dirtext" to ("DirText [text] [subtext] [browse_button_text] [browse_dlg_text]" to "설치 폴더 선택 페이지 문구."),
        "licensetext" to ("LicenseText text [button_text]" to "라이선스 페이지 문구."),
        "licensedata" to ("LicenseData license.txt|license.rtf" to "라이선스 페이지에 띄울 텍스트/RTF 파일."),
        "licensebkcolor" to ("LicenseBkColor color | /gray | /windows" to "라이선스 상자 배경색."),
        "licenseforceselection" to ("LicenseForceSelection checkbox|radiobuttons|off [text] [text]" to "동의를 강제하는 방식. 동의 전에는 다음 버튼이 잠긴다."),
        "installbuttontext" to ("InstallButtonText text" to "설치 버튼 문구."),
        "miscbuttontext" to ("MiscButtonText [back] [next] [cancel] [close]" to "뒤로·다음·취소·닫기 버튼 문구."),
        "detailsbuttontext" to ("DetailsButtonText text" to "자세히 보기 버튼 문구."),
        "fileerrortext" to ("FileErrorText [text] [text_no_ignore]" to "파일 쓰기 실패 대화상자 문구."),
        "spacetexts" to ("SpaceTexts required_text available_text | none" to "필요 공간·남은 공간 문구. none 이면 감춘다."),
        "installcolors" to ("InstallColors /windows | foreground background" to "설치 로그 창 글자·배경색."),
        "instprogressflags" to ("InstProgressFlags [flag […]]" to "진행 막대 모양. smooth, colored 를 쓴다."),
        "icon" to ("Icon icon.ico" to "설치 실행 파일 아이콘."),
        "uninstallicon" to ("UninstallIcon icon.ico" to "제거 프로그램 아이콘."),
        "windowicon" to ("WindowIcon on|off" to "창 제목줄 아이콘 표시 여부."),
        "xpstyle" to ("XPStyle on|off" to "비주얼 스타일(테마) 매니페스트를 넣는다."),
        "checkbitmap" to ("CheckBitmap bitmap.bmp" to "컴포넌트 트리의 체크 표시 비트맵."),
        "changeui" to ("ChangeUI all|dialog_id ui_file.exe" to "대화상자 리소스를 다른 UI 파일 것으로 갈아 끼운다."),
        "bgfont" to ("BGFont [font_face [height] [weight] [/ITALIC] [/UNDERLINE] [/STRIKE]] " to "배경 그라디언트에 얹을 글꼴."),
        "bggradient" to ("BGGradient off | top_color bottom_color [text_color]" to "전체 화면 배경 그라디언트."),
        "setfont" to ("SetFont font_face height" to "설치 프로그램 기본 글꼴."),
        "autoclosewindow" to ("AutoCloseWindow true|false" to "설치가 끝나면 창을 자동으로 닫는다."),
        "showinstdetails" to ("ShowInstDetails hide|show|nevershow" to "설치 로그를 펼쳐 보일지."),
        "showuninstdetails" to ("ShowUninstDetails hide|show|nevershow" to "제거 로그를 펼쳐 보일지."),

        // ---------- 설치 동작 설정 ----------
        "insttype" to ("InstType [un.]name | /NOCUSTOM | /CUSTOMSTRING=str | /COMPONENTSONLYONCUSTOM" to "설치 유형(전체·최소 등)을 만든다. SectionIn 이 이 번호를 가리킨다."),
        "insttypegettext" to ("InstTypeGetText insttype_index user_var" to "설치 유형 이름을 읽는다."),
        "insttypesettext" to ("InstTypeSetText insttype_index text" to "설치 유형 이름을 바꾼다."),
        "allowrootdirinstall" to ("AllowRootDirInstall true|false" to "드라이브 루트(C:\\) 설치를 허용할지."),
        "allowskipfiles" to ("AllowSkipFiles on|off" to "파일 오류 대화상자에 무시 버튼을 띄울지."),
        "crccheck" to ("CRCCheck on|off|force" to "실행 시 설치본 무결성 검사."),
        "dirvar" to ("DirVar user_var" to "설치 폴더를 담을 변수. 안 쓰면 \$INSTDIR."),
        "dirverify" to ("DirVerify auto|leave" to "폴더 유효성을 언제 볼지. leave 면 다음 버튼을 누를 때까지 미룬다."),
        "setoverwrite" to ("SetOverwrite on|off|try|ifnewer|ifdiff|lastused" to "이후 File 명령의 덮어쓰기 규칙."),
        "setcompress" to ("SetCompress auto|force|off" to "이후 File 데이터를 압축할지."),
        "setcompressionlevel" to ("SetCompressionLevel 0-9" to "zlib/bzip2 압축 수준. lzma 는 SetCompressor 로 정한다."),
        "setdatablockoptimize" to ("SetDatablockOptimize on|off" to "같은 데이터를 한 번만 담아 설치본을 줄인다."),
        "setdatesave" to ("SetDateSave on|off" to "File 이 원본 타임스탬프를 보존할지."),
        "setpluginunload" to ("SetPluginUnload manual|alwaysoff" to "구버전 플러그인 언로드 방식. 요즘은 쓸 일이 없다."),
        "reservefile" to ("ReserveFile [/nonfatal] [/r] [/x pattern] file […]" to "압축 앞쪽에 미리 담을 파일. .onInit 에서 쓰는 플러그인에 필요."),
        "loadlanguagefile" to ("LoadLanguageFile language.nlf" to "언어 파일을 읽어 \$(문자열) 을 쓸 수 있게 한다."),
        "silentinstall" to ("SilentInstall normal|silent|silentlog" to "설치를 무인 모드로 만들지."),
        "silentuninstall" to ("SilentUnInstall normal|silent" to "제거를 무인 모드로 만들지."),
        "target" to ("Target x86-ansi|x86-unicode|amd64-unicode|arm64-unicode" to "만들어 낼 설치본의 아키텍처."),
        "manifestdpiaware" to ("ManifestDPIAware true|false|system|per-monitor" to "매니페스트 DPI 인식 설정. 고해상도에서 흐릿함을 막는다."),
        "manifestsupportedos" to ("ManifestSupportedOS none|all|WinVista|Win7|Win8|Win8.1|Win10|Win11" to "매니페스트에 적을 지원 OS."),
        "manifestlongpathaware" to ("ManifestLongPathAware true|false" to "260자를 넘는 경로를 허용한다 (Win10 1607+)."),

        // ---------- 버전 리소스 ----------
        "viproductversion" to ("VIProductVersion x.x.x.x" to "제품 버전 리소스. VIAddVersionKey 를 쓰려면 먼저 있어야 한다."),
        "vifileversion" to ("VIFileVersion x.x.x.x" to "파일 버전 리소스."),
        "viaddversionkey" to ("VIAddVersionKey [/LANG=lang_id] key value" to "버전 정보 항목. ProductName, CompanyName, FileDescription, LegalCopyright 등."),

        // ---------- 파일·폴더 ----------
        "copyfiles" to ("CopyFiles [/SILENT] [/FILESONLY] source destination [size_kb]" to "대상 컴퓨터 **안에서** 파일을 복사한다. 설치본에 담는 File 과 다르다."),
        "rename" to ("Rename [/REBOOTOK] source target" to "파일 이름을 바꾸거나 옮긴다. /REBOOTOK 이면 잠긴 파일을 재부팅 때 처리."),
        "searchpath" to ("SearchPath user_var filename" to "PATH 에서 파일을 찾아 전체 경로를 담는다."),
        "getfullpathname" to ("GetFullPathName [/SHORT] user_var path_or_file" to "상대 경로를 절대 경로로 바꾼다."),
        "gettempfilename" to ("GetTempFileName user_var [base_dir]" to "겹치지 않는 임시 파일 이름을 만든다."),
        "setfileattributes" to ("SetFileAttributes filename attribute[|attribute…]" to "파일 속성 지정. NORMAL, READONLY, HIDDEN, SYSTEM 등."),
        "getfiletime" to ("GetFileTime file user_var(high) user_var(low)" to "대상 컴퓨터 파일의 수정 시각."),
        "getfiletimelocal" to ("GetFileTimeLocal localfile user_var(high) user_var(low)" to "빌드 머신 파일의 수정 시각을 컴파일 타임에 읽는다."),

        // ---------- 파일 입출력 ----------
        "fileopen" to ("FileOpen user_var(handle) filename r|w|a" to "파일을 연다. 실패하면 에러 플래그가 선다."),
        "fileclose" to ("FileClose handle" to "FileOpen 으로 연 파일을 닫는다."),
        "fileread" to ("FileRead handle user_var [maxlen]" to "한 줄 읽는다. 줄바꿈까지 포함된다."),
        "filewrite" to ("FileWrite handle string" to "문자열을 쓴다. 줄바꿈은 직접 붙여야 한다."),
        "filereadbyte" to ("FileReadByte handle user_var" to "1바이트를 숫자로 읽는다."),
        "filewritebyte" to ("FileWriteByte handle value" to "1바이트를 쓴다."),
        "filereadword" to ("FileReadWord handle user_var" to "2바이트(워드)를 읽는다."),
        "filewriteword" to ("FileWriteWord handle value" to "2바이트(워드)를 쓴다."),
        "filereadutf16le" to ("FileReadUTF16LE handle user_var [maxlen]" to "UTF-16LE 로 한 줄 읽는다."),
        "filewriteutf16le" to ("FileWriteUTF16LE [/BOM] handle string" to "UTF-16LE 로 쓴다. /BOM 은 파일 앞에 BOM 을 넣는다."),
        "fileseek" to ("FileSeek handle offset [SET|CUR|END] [user_var]" to "파일 안 위치를 옮긴다."),
        "findfirst" to ("FindFirst user_var(handle) user_var(filename) filespec" to "파일 찾기를 시작한다. 없으면 핸들이 빈 값."),
        "findnext" to ("FindNext handle user_var(filename)" to "다음 파일 이름. 더 없으면 에러 플래그."),
        "findclose" to ("FindClose handle" to "FindFirst 로 연 찾기 핸들을 닫는다."),

        // ---------- INI ----------
        "readinistr" to ("ReadINIStr user_var ini_file section entry_name" to "INI 값을 읽는다."),
        "writeinistr" to ("WriteINIStr ini_file section entry_name value" to "INI 값을 쓴다."),
        "deleteinistr" to ("DeleteINIStr ini_file section entry_name" to "INI 항목 하나를 지운다."),
        "deleteinisec" to ("DeleteINISec ini_file section" to "INI 섹션을 통째로 지운다."),
        "flushini" to ("FlushINI ini_file" to "INI 캐시를 디스크로 내린다."),

        // ---------- 레지스트리 ----------
        "readregdword" to ("ReadRegDWORD user_var root_key sub_key name" to "레지스트리 DWORD 를 읽는다."),
        "writeregexpandstr" to ("WriteRegExpandStr root_key subkey key_name value" to "REG_EXPAND_SZ 로 쓴다. %VAR% 가 나중에 펼쳐진다."),
        "writeregbin" to ("WriteRegBin root_key subkey key_name hexstring" to "REG_BINARY 로 쓴다."),
        "writeregmultistr" to ("WriteRegMultiStr /REGEDIT5 root_key subkey key_name hexstring" to "REG_MULTI_SZ 로 쓴다."),
        "writeregnone" to ("WriteRegNone root_key subkey key_name" to "REG_NONE 값을 만든다."),
        "enumregkey" to ("EnumRegKey user_var root_key subkey index" to "하위 키 이름을 번호로 훑는다. 없으면 빈 문자열."),
        "enumregvalue" to ("EnumRegValue user_var root_key subkey index" to "값 이름을 번호로 훑는다."),

        // ---------- 문자열·정수 ----------
        "strlen" to ("StrLen user_var str" to "문자열 길이."),
        "strcmps" to ("StrCmpS str1 str2 jump_if_equal [jump_if_not_equal]" to "대소문자를 **가리는** 문자열 비교. StrCmp 는 안 가린다."),
        "intcmp" to ("IntCmp val1 val2 jump_equal [jump_less] [jump_more]" to "정수 비교 분기."),
        "intcmpu" to ("IntCmpU val1 val2 jump_equal [jump_less] [jump_more]" to "부호 없는 정수로 비교."),
        "intfmt" to ("IntFmt user_var format numberstring" to "정수를 형식 문자열로. 예: \"0x%08X\"."),
        "intptrop" to ("IntPtrOp user_var value1 OP [value2]" to "포인터 크기 정수 연산. 64비트에서 잘리지 않는다."),
        "intptrcmp" to ("IntPtrCmp val1 val2 jump_equal [jump_less] [jump_more]" to "포인터 크기 정수 비교."),
        "intptrcmpu" to ("IntPtrCmpU val1 val2 jump_equal [jump_less] [jump_more]" to "부호 없는 포인터 크기 정수 비교."),
        "expandenvstrings" to ("ExpandEnvStrings user_var string" to "%PATH% 같은 환경 변수 표기를 펼친다."),
        "readenvstr" to ("ReadEnvStr user_var name" to "환경 변수를 읽는다."),

        // ---------- 스택·흐름 ----------
        "exch" to ("Exch [user_var | stack_index]" to "스택 맨 위 값을 변수나 다른 스택 항목과 맞바꾼다."),
        "nop" to ("Nop" to "아무 것도 하지 않는다."),
        "sleep" to ("Sleep milliseconds" to "지정한 밀리초만큼 멈춘다."),
        "reboot" to ("Reboot" to "즉시 재부팅한다. 돌아오지 않는다."),
        "setrebootflag" to ("SetRebootFlag true|false" to "재부팅 필요 플래그를 세우거나 내린다."),
        "ifrebootflag" to ("IfRebootFlag jump_if_set [jump_if_not_set]" to "재부팅 플래그가 섰는지 본다."),
        "ifabort" to ("IfAbort jump_if_abort [jump_if_not]" to "설치가 중단됐는지 본다. .onInstFailed 계열에서 쓴다."),
        "ifsilent" to ("IfSilent jump_if_silent [jump_if_not]" to "무인 설치 모드인지 본다."),
        "setsilent" to ("SetSilent silent|normal" to "무인 모드를 켜고 끈다. .onInit 에서만 된다."),
        "ifrtllanguage" to ("IfRtlLanguage jump_if_rtl [jump_if_not]" to "현재 언어가 오른쪽→왼쪽 쓰기인지 본다."),
        "ifshellvarcontextall" to ("IfShellVarContextAll jump_if_all [jump_if_current]" to "SetShellVarContext 가 all 인지 본다."),
        "seterrors" to ("SetErrors" to "에러 플래그를 세운다."),
        "seterrorlevel" to ("SetErrorLevel error_level" to "설치 프로그램 종료 코드를 정한다."),
        "geterrorlevel" to ("GetErrorLevel user_var" to "지금 설정된 종료 코드를 읽는다."),
        "getinstdirerror" to ("GetInstDirError user_var" to ".onVerifyInstDir 뒤 폴더 오류 원인. 0=없음, 1=잘못된 경로, 2=공간 부족."),
        "getcurinsttype" to ("GetCurInstType user_var" to "지금 고른 설치 유형 번호."),
        "getcurrentaddress" to ("GetCurrentAddress user_var" to "지금 명령의 주소. Call 로 되돌아올 때 쓴다."),
        "getlabeladdress" to ("GetLabelAddress user_var label" to "레이블 주소를 얻는다."),
        "getfunctionaddress" to ("GetFunctionAddress user_var function" to "함수 주소를 얻는다. Call \$0 으로 간접 호출한다."),
        "callinstdll" to ("CallInstDLL dllfile [/NOUNLOAD] function" to "설치 DLL 함수를 부른다. 요즘은 플러그인 호출을 쓴다."),
        "regdll" to ("RegDLL dllfile [entrypoint]" to "DLL 을 등록한다 (DllRegisterServer)."),
        "unregdll" to ("UnRegDLL dllfile" to "DLL 등록을 해제한다."),
        "getdllversion" to ("GetDLLVersion filename user_var(high) user_var(low)" to "대상 컴퓨터 DLL 의 버전을 읽는다."),
        "getdllversionlocal" to ("GetDLLVersionLocal localfile user_var(high) user_var(low)" to "빌드 머신 DLL 의 버전을 컴파일 타임에 읽는다."),
        "getknownfolderpath" to ("GetKnownFolderPath user_var folder_id" to "KNOWNFOLDERID 로 폴더 경로를 얻는다."),
        "getwinver" to ("GetWinVer user_var Major|Minor|Build|ServicePack" to "윈도 버전을 읽는다."),

        // ---------- 창·컨트롤 ----------
        "bringtofront" to ("BringToFront" to "설치 창을 맨 앞으로 올린다."),
        "hidewindow" to ("HideWindow" to "설치 창을 숨긴다."),
        "lockwindow" to ("LockWindow on|off" to "화면 다시 그리기를 잠근다. 컨트롤을 여러 개 바꿀 때 깜빡임을 막는다."),
        "enablewindow" to ("EnableWindow hwnd 1|0" to "컨트롤을 켜거나 끈다."),
        "iswindow" to ("IsWindow hwnd jump_if_window [jump_if_not]" to "핸들이 살아 있는 창인지 본다."),
        "findwindow" to ("FindWindow user_var window_class [title] [parent] [child_after]" to "창 핸들을 찾는다."),
        "getdlgitem" to ("GetDlgItem user_var dialog item_id" to "대화상자 안 컨트롤의 핸들을 얻는다."),
        "setctlcolors" to ("SetCtlColors hwnd [/BRANDING] [text_color] [bg_color]" to "컨트롤 글자·배경색을 바꾼다."),
        "setstaticbkcolor" to ("SetStaticBkColor hwnd color" to "정적 컨트롤 배경색."),
        "setbrandingimage" to ("SetBrandingImage [/IMGID=id] [/RESIZETOFIT] image.bmp" to "브랜딩 이미지를 갈아 끼운다."),
        "createfont" to ("CreateFont user_var(handle) face [height] [weight] [/ITALIC] [/UNDERLINE] [/STRIKE]" to "글꼴 핸들을 만든다. SendMessage 로 컨트롤에 씌운다."),
        "setautoclose" to ("SetAutoClose true|false" to "설치가 끝나면 창을 자동으로 닫을지 (실행 중 변경)."),
        "setdetailsview" to ("SetDetailsView show|hide" to "자세히 목록을 보이거나 감춘다."),
        "setdetailsprint" to ("SetDetailsPrint none|listonly|textonly|both|lastused" to "진행 문구를 어디에 찍을지."),
        "logset" to ("LogSet on|off" to "install.log 기록. NSIS_CONFIG_LOG 로 빌드된 makensis 라야 동작한다."),
        "logtext" to ("LogText text" to "로그 파일에 한 줄 남긴다."),
    )
}
