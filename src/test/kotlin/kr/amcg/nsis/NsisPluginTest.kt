package kr.amcg.nsis

import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * 실제 IDE 플랫폼을 띄워(헤드리스) 렉서·검사·자동완성·구조뷰가 붙는지 확인한다.
 */
class NsisPluginTest : BasePlatformTestCase() {

    private var savedMakensis: String = ""

    override fun setUp() {
        super.setUp()
        // NSIS 를 못 찾은 상태로 고정한다. 개발 머신에 NSIS 가 깔렸는지에 따라 결과가
        // 달라지면 안 되고, 테스트 VFS 는 임시 폴더 밖(/opt/homebrew 등) 접근을 막는다.
        savedMakensis = NsisSettings.getInstance().makensisPath
        NsisSettings.getInstance().makensisPath = "/존재하지-않는/makensis"
    }

    override fun tearDown() {
        try {
            NsisSettings.getInstance().makensisPath = savedMakensis
        } finally {
            super.tearDown()
        }
    }

    // ---------- 렉서 ----------

    private fun lex(text: String): List<Pair<IElementType, String>> {
        val lexer = NsisLexer()
        lexer.start(text)
        val out = ArrayList<Pair<IElementType, String>>()
        while (true) {
            val t = lexer.tokenType ?: break
            out += t to text.substring(lexer.tokenStart, lexer.tokenEnd)
            lexer.advance()
        }
        return out
    }

    fun `test 줄 첫 낱말만 명령어로 인식한다`() {
        val tokens = lex("SetOutPath \$INSTDIR\nDetailPrint SetOutPath")
        val instructions = tokens.filter { it.first == NsisTokenTypes.INSTRUCTION }.map { it.second }
        // 두 줄의 첫 낱말만 명령어. 인자로 쓰인 SetOutPath 는 식별자여야 한다.
        assertEquals(listOf("SetOutPath", "DetailPrint"), instructions)
        val ids = tokens.filter { it.first == NsisTokenTypes.IDENTIFIER }.map { it.second }
        assertTrue("인자 SetOutPath 는 식별자여야 한다: $ids", ids.contains("SetOutPath"))
    }

    fun `test 대소문자를 가리지 않는다`() {
        val tokens = lex("setoutpath \$INSTDIR")
        assertEquals(NsisTokenTypes.INSTRUCTION, tokens.first().first)
    }

    fun `test 변수 정의참조 전처리기 문자열을 구분한다`() {
        val tokens = lex("!define X \"a\$\\\"b\" \${MUI_ICON} \$R0 \$\$")
        val kinds = tokens.map { it.first }
        assertTrue(kinds.contains(NsisTokenTypes.PREPROCESSOR))
        assertTrue(kinds.contains(NsisTokenTypes.DEFINE_REF))
        assertTrue(kinds.contains(NsisTokenTypes.VARIABLE))
        // $\" 이스케이프가 문자열을 조기 종료시키면 안 된다
        val strings = tokens.filter { it.first == NsisTokenTypes.STRING }.map { it.second }
        assertEquals(listOf("\"a\$\\\"b\""), strings)
    }

    fun `test 렉서가 입력 전체를 빠짐없이 덮는다`() {
        val text = "; c\nSection \"a\"\n  File /r \"x\\y.ttf\"  ; t\n/* b\nc */\nSectionEnd\n"
        val lexer = NsisLexer()
        lexer.start(text)
        var pos = 0
        while (lexer.tokenType != null) {
            assertEquals("토큰 사이에 구멍이 있다", pos, lexer.tokenStart)
            assertTrue("토큰 길이가 0 이면 무한 루프가 난다", lexer.tokenEnd > lexer.tokenStart)
            pos = lexer.tokenEnd
            lexer.advance()
        }
        assertEquals("입력 끝까지 못 갔다", text.length, pos)
    }

    // ---------- 파일 타입 / PSI ----------

    fun `test nsi 파일이 NSIS 로 인식된다`() {
        val file = myFixture.configureByText("a.nsi", "Name \"x\"")
        assertTrue(file is NsisFile)
        assertEquals(NsisFileType, file.fileType)
    }

    // ---------- 검사 ----------

    private fun warnings(fileName: String, text: String): List<String> {
        myFixture.configureByText(fileName, text)
        return myFixture.doHighlighting().mapNotNull { it.description }
    }

    fun `test 닫히지 않은 Section 을 경고한다`() {
        val w = warnings("a.nsi", "Section \"a\"\n  DetailPrint \"x\"\n")
        assertTrue("경고가 없다: $w", w.any { it.contains("SectionEnd") })
    }

    fun `test 오타 난 전처리기 지시자를 경고한다`() {
        val w = warnings("a.nsi", "!inclde \"MUI2.nsh\"\n")
        assertTrue("경고가 없다: $w", w.any { it.contains("!inclde") })
    }

    fun `test 없는 파일 참조를 경고한다`() {
        val w = warnings("a.nsi", "Section \"s\"\n  File \"없는파일.ttf\"\nSectionEnd\n")
        assertTrue("경고가 없다: $w", w.any { it.contains("대상이 없습니다") })
    }

    // ---------- 참조 (⌘+클릭) ----------

    /** 참조 해석은 부모 폴더가 있어야 하므로 실제 프로젝트 파일로 만들어 연다. */
    private fun openInProject(name: String, text: String): PsiFile {
        val psi = myFixture.addFileToProject(name, text)
        myFixture.configureFromExistingVirtualFile(psi.virtualFile)
        return myFixture.file
    }

    /** ⌘+클릭이 데려가는 곳. 없으면 null. */
    private fun gotoTargetAt(file: PsiFile, needle: String): String? {
        val offset = file.text.indexOf(needle)
        assertTrue("본문에 '$needle' 이 없다", offset >= 0)
        val element = file.findElementAt(offset)
        val targets = NsisGotoDeclarationHandler()
            .getGotoDeclarationTargets(element, offset, myFixture.editor)
        return (targets?.firstOrNull() as? PsiFile)?.name
    }

    fun `test 프로젝트 안 include 는 그 파일로 이어진다`() {
        myFixture.addFileToProject("헬퍼.nsh", "; helper\n")
        val file = openInProject("a.nsi", "!include \"헬퍼.nsh\"\n")
        assertEquals("헬퍼.nsh", gotoTargetAt(file, "헬퍼"))
    }

    fun `test File 인자도 그 파일로 이어진다`() {
        myFixture.addFileToProject("logo.ico", "")
        val file = openInProject("a.nsi", "Section\n  File \"logo.ico\"\nSectionEnd\n")
        assertEquals("logo.ico", gotoTargetAt(file, "logo"))
    }

    fun `test 없는 대상은 이동할 곳이 없다`() {
        val file = openInProject("a.nsi", "!include \"sub/없는헤더.nsh\"\n")
        assertNull("없는 파일인데 이동 대상이 잡혔다", gotoTargetAt(file, "sub"))
    }

    fun `test 경로 인자가 아닌 곳에서는 이동하지 않는다`() {
        val file = openInProject("a.nsi", "Name \"내 프로그램\"\n")
        assertNull("경로가 아닌데 이동 대상이 잡혔다", gotoTargetAt(file, "내"))
    }

    // ---------- 문서 ----------

    fun `test 명령 위에서 문서 팝업이 뜬다`() {
        val file = myFixture.configureByText("a.nsi", "Sleep 1000\n")
        val element = file.findElementAt(0)
        val doc = NsisDocumentationProvider().generateDoc(element, element)
        assertNotNull("문서가 없다", doc)
        assertTrue("문법이 안 보인다: $doc", doc!!.contains("Sleep milliseconds"))
        assertTrue("설명이 안 보인다: $doc", doc.contains("밀리초"))
    }

    fun `test 모든 명령에 설명이 붙어 있다`() {
        val documented = NsisKeywords.DOCS.keys
        val missing = NsisKeywords.ALL_COMMANDS.map { it.lowercase() }
            .filter { it !in documented }
        assertTrue("설명 없는 명령: $missing", missing.isEmpty())
    }

    fun `test 전처리기 지시자에도 설명이 붙어 있다`() {
        val missing = NsisKeywords.PREPROCESSOR_DIRECTIVES.filter { it !in NsisKeywords.DOCS }
        assertTrue("설명 없는 지시자: $missing", missing.isEmpty())
    }

    fun `test 표준 헤더 include 는 경고하지 않는다`() {
        // MUI2.nsh 등은 NSIS 설치본의 Include 폴더에 있다. 스크립트 폴더에 없다고 경고하면 오탐.
        val w = warnings("a.nsi", "!include \"MUI2.nsh\"\n!include \"WinMessages.nsh\"\n!include \"LogicLib.nsh\"\n")
        assertTrue("오탐이 났다: $w", w.none { it.contains("include") })
    }

    fun `test 경로가 붙은 include 는 없으면 경고한다`() {
        val w = warnings("a.nsi", "!include \"sub/없는헤더.nsh\"\n")
        assertTrue("경고가 없다: $w", w.any { it.contains("찾지 못했습니다") })
    }

    fun `test nonfatal include 는 없어도 경고하지 않는다`() {
        val w = warnings("a.nsi", "!include /nonfatal \"sub/없는헤더.nsh\"\n")
        assertTrue("오탐이 났다: $w", w.none { it.contains("찾지 못했습니다") })
    }

    fun `test Unicode true 없는 비ASCII 를 경고한다`() {
        val w = warnings("a.nsi", "Name \"한글 이름\"\n")
        assertTrue("경고가 없다: $w", w.any { it.contains("Unicode true") })
    }

    fun `test Unicode true 가 있으면 그 경고는 안 뜬다`() {
        val w = warnings("a.nsi", "Unicode true\nName \"한글 이름\"\n")
        assertTrue("오탐이 났다: $w", w.none { it.contains("Unicode true") })
    }

    fun `test 정상 스크립트에는 블록 경고가 없다`() {
        val w = warnings(
            "a.nsi",
            """
            Unicode true
            Name "ok"
            Function .onInit
            FunctionEnd
            Section "s"
            SectionEnd
            """.trimIndent(),
        )
        assertTrue("오탐이 났다: $w", w.none { it.contains("닫히지 않") || it.contains("짝이 되는") })
    }

    // ---------- 자동 완성 ----------

    /**
     * 후보가 정확히 하나면 IDE 가 목록을 띄우지 않고 바로 삽입한다
     * (completeBasic() 이 null 을 준다). 두 경우를 모두 성공으로 친다.
     */
    private fun assertCompletes(text: String, expected: String) {
        myFixture.configureByText("a.nsi", text)
        val elements = myFixture.completeBasic()
        if (elements == null) {
            val after = myFixture.editor.document.text
            assertTrue("후보가 하나여서 자동 삽입됐는데 결과에 $expected 가 없다: $after", after.contains(expected))
        } else {
            val items = elements.map { it.lookupString }
            assertTrue("$expected 가 후보에 없다: $items", items.contains(expected))
        }
    }

    fun `test 줄 첫머리에서 NSIS 명령을 제안한다`() {
        assertCompletes("SetOut<caret>", "SetOutPath")
    }

    fun `test 명령 후보가 여럿일 때 목록이 뜬다`() {
        myFixture.configureByText("a.nsi", "WriteReg<caret>")
        val items = myFixture.completeBasic()?.map { it.lookupString }.orEmpty()
        assertTrue("WriteRegStr 가 없다: $items", items.contains("WriteRegStr"))
        assertTrue("WriteRegDWORD 가 없다: $items", items.contains("WriteRegDWORD"))
    }

    fun `test 느낌표 뒤에는 전처리기 지시자를 제안한다`() {
        assertCompletes("!inse<caret>", "!insertmacro")
    }

    fun `test 달러 뒤에는 내장 상수를 제안한다`() {
        assertCompletes("SetOutPath \$INSTD<caret>", "\$INSTDIR")
    }

    fun `test 같은 파일의 define 을 제안한다`() {
        assertCompletes("!define MY_OWN_THING 1\nDetailPrint \${MY_O<caret>", "MY_OWN_THING")
    }

    fun `test insertmacro 뒤에는 MUI 매크로를 제안한다`() {
        assertCompletes("!insertmacro MUI_PAGE_W<caret>", "MUI_PAGE_WELCOME")
    }

    // ---------- 구조 뷰 / 접기 ----------

    fun `test 구조뷰에 Section 과 Function 이 뜬다`() {
        val file = myFixture.configureByText(
            "a.nsi",
            "Function .onInit\nFunctionEnd\nSection \"AMCG Fonts\" SecMain\nSectionEnd\n",
        ) as NsisFile
        val children = NsisFileTreeElement(file).children
        val names = children.map { it.presentation.presentableText }
        assertEquals(listOf(".onInit", "AMCG Fonts"), names)
    }

    fun `test 블록 접기 영역을 만든다`() {
        // 에디터의 fold 영역은 데몬이 돌아야 생기므로 빌더를 직접 호출해 검증한다
        val file = myFixture.configureByText(
            "a.nsi",
            "Section \"a\"\n  DetailPrint \"x\"\n  DetailPrint \"y\"\nSectionEnd\n",
        )
        val descriptors = NsisFoldingBuilder()
            .buildFoldRegions(file, myFixture.editor.document, false)
        assertEquals("접기 영역이 하나 나와야 한다", 1, descriptors.size)
        assertTrue(
            "접기 라벨이 이상하다: ${descriptors[0].placeholderText}",
            descriptors[0].placeholderText.orEmpty().contains("Section a"),
        )
    }

    fun `test 본문이 없는 블록은 접지 않는다`() {
        val file = myFixture.configureByText("a.nsi", "Section \"a\"\nSectionEnd\n")
        val descriptors = NsisFoldingBuilder()
            .buildFoldRegions(file, myFixture.editor.document, false)
        assertEquals(0, descriptors.size)
    }
}
