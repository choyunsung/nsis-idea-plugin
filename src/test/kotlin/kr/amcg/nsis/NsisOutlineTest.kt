package kr.amcg.nsis

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * [NsisOutline] 은 플랫폼에 의존하지 않는 순수 함수라 그냥 JUnit 으로 검증한다.
 * 구조뷰·코드접기·검사가 전부 여기 결과를 쓰므로 여기가 맞으면 나머지가 따라온다.
 */
class NsisOutlineTest {

    @Test
    fun `Section 과 Function 을 찾고 이름을 떼어낸다`() {
        val a = NsisOutline.analyze(
            """
            Function .onInit
              StrCpy ${'$'}0 "x"
            FunctionEnd

            Section "AMCG Fonts" SecMain
              DetailPrint "hi"
            SectionEnd
            """.trimIndent(),
        )
        assertEquals(2, a.regions.size)
        assertEquals(NsisRegion.Kind.FUNCTION, a.regions[0].kind)
        assertEquals(".onInit", a.regions[0].name)
        assertEquals(NsisRegion.Kind.SECTION, a.regions[1].kind)
        assertEquals("AMCG Fonts", a.regions[1].name)
        assertTrue(a.problems.isEmpty())
    }

    @Test
    fun `Section 의 o 스위치와 따옴표 없는 이름을 처리한다`() {
        val a = NsisOutline.analyze("Section /o -Hidden SecX\nSectionEnd")
        assertEquals(1, a.regions.size)
        // 앞의 - 는 숨김 표시라 이름에서 뗀다
        assertEquals("Hidden", a.regions[0].name)
    }

    @Test
    fun `닫히지 않은 Section 을 잡는다`() {
        val a = NsisOutline.analyze("Section \"a\"\n  DetailPrint \"x\"\n")
        assertEquals(1, a.problems.size)
        assertTrue(a.problems[0].message.contains("SectionEnd"))
    }

    @Test
    fun `조건부 컴파일이 있으면 균형 검사를 접는다`() {
        // !ifdef 로 갈라지면 정적으로 짝을 맞출 수 없다 — 오탐을 내면 안 된다
        val a = NsisOutline.analyze(
            """
            !ifdef A
            Section "a"
            !else
            Section "b"
            !endif
            SectionEnd
            """.trimIndent(),
        )
        assertTrue("조건부 컴파일 파일에서 오탐이 났다: ${a.problems}", a.problems.isEmpty())
    }

    @Test
    fun `주석과 문자열 안의 내용은 구조로 오해하지 않는다`() {
        val a = NsisOutline.analyze(
            """
            ; Section "주석 속" 은 무시
            # Function 도 무시
            Name "Section 이라는 글자가 든 문자열"
            /* Section
               여러 줄 주석 */
            Section "진짜"
            SectionEnd
            """.trimIndent(),
        )
        assertEquals(1, a.regions.size)
        assertEquals("진짜", a.regions[0].name)
    }

    @Test
    fun `주석을 지워도 줄 안 위치가 밀리지 않는다`() {
        // stripComments 가 길이를 유지해야 오프셋을 그대로 절대 위치로 쓸 수 있다
        val text = "Section \"이름\"   ; 꼬리 주석\nSectionEnd"
        val a = NsisOutline.analyze(text)
        val r = a.regions.single()
        assertEquals("이름", r.name)
        // nameOffset 이 실제로 그 이름을 가리키는지 원문에서 잘라 확인
        assertEquals("\"이름\"", text.substring(r.nameOffset, r.nameOffset + r.nameLength))
    }

    @Test
    fun `File 과 !include 의 경로 인자를 수집하고 스위치는 건너뛴다`() {
        val a = NsisOutline.analyze(
            """
            !include "MUI2.nsh"
            File /oname=out.dat "fonts\AMCG-Regular.ttf"
            File "${'$'}INSTDIR\runtime.dat"
            """.trimIndent(),
        )
        val paths = a.pathRefs.map { it.path }
        assertTrue("MUI2.nsh 를 못 잡았다: $paths", paths.contains("MUI2.nsh"))
        assertTrue("스위치 뒤 경로를 못 잡았다: $paths", paths.contains("fonts\\AMCG-Regular.ttf"))
        // ${'$'}INSTDIR 처럼 런타임에 정해지는 경로는 검사 대상이 아니다
        assertTrue("변수 경로를 걸렀어야 한다: $paths", paths.none { it.contains("runtime.dat") })
    }

    @Test
    fun `MUI 경로 define 도 경로로 취급한다`() {
        val a = NsisOutline.analyze("""!define MUI_ICON "assets\amcg.ico"""")
        assertEquals(listOf("assets\\amcg.ico"), a.pathRefs.map { it.path })
        // 경로가 아닌 define 은 수집하지 않는다
        val b = NsisOutline.analyze("!define MUI_ABORTWARNING")
        assertTrue(b.pathRefs.isEmpty())
        assertEquals(listOf("MUI_ABORTWARNING"), b.defines.map { it.name })
    }

    @Test
    fun `Unicode 선언과 비ASCII 위치를 알아낸다`() {
        val withUni = NsisOutline.analyze("Unicode true\nName \"한글\"")
        assertTrue(withUni.hasUnicodeTrue)
        assertTrue(withUni.firstNonAsciiOffset >= 0)

        val withoutUni = NsisOutline.analyze("Name \"한글\"")
        assertTrue(!withoutUni.hasUnicodeTrue)
        assertTrue(withoutUni.firstNonAsciiOffset >= 0)

        val ascii = NsisOutline.analyze("Name \"plain\"")
        assertEquals(-1, ascii.firstNonAsciiOffset)
    }

    @Test
    fun `매크로와 Var 선언을 모은다`() {
        val a = NsisOutline.analyze(
            """
            Var /GLOBAL FontReg
            !macro DoThing param
              DetailPrint "${'$'}{param}"
            !macroend
            """.trimIndent(),
        )
        assertEquals(listOf("${'$'}FontReg"), a.variables.map { it.name })
        assertEquals(listOf("DoThing"), a.macros.map { it.name })
        assertEquals(NsisRegion.Kind.MACRO, a.regions.single().kind)
    }
}
