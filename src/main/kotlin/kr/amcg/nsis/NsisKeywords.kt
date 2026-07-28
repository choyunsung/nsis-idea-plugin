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
    )
}
