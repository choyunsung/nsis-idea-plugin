package kr.amcg.nsis

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import javax.swing.Icon

class NsisColorSettingsPage : ColorSettingsPage {

    override fun getIcon(): Icon = NsisIcons.FILE

    override fun getHighlighter(): SyntaxHighlighter = NsisSyntaxHighlighter()

    override fun getDemoText(): String = """
        ; AMCG 브랜드 서체 설치 프로그램 (NSIS / MUI2)
        Unicode true
        !include "MUI2.nsh"

        Name "AMCG 브랜드 서체"
        OutFile "AMCG-Font-Installer.exe"
        InstallDir ${'$'}FONTS
        RequestExecutionLevel admin
        SetCompressor /SOLID lzma

        Var FontReg

        !define MUI_ICON "assets\amcg.ico"
        !define MUI_ABORTWARNING
        !insertmacro MUI_PAGE_WELCOME
        !insertmacro MUI_LANGUAGE "Korean"

        Function .onInit
          StrCpy ${'$'}FontReg "Software\Microsoft\Windows NT\CurrentVersion\Fonts"
        FunctionEnd

        Section "AMCG Fonts" SecMain
          SetOutPath "${'$'}FONTS"
          File "fonts\AMCG-Regular.ttf"
          WriteRegStr HKLM "${'$'}FontReg" "AMCG (TrueType)" "AMCG-Regular.ttf"
          SendMessage ${'$'}{HWND_BROADCAST} ${'$'}{WM_FONTCHANGE} 0 0 /TIMEOUT=2000
          IntOp ${'$'}0 ${'$'}0 + 1
        SectionEnd
    """.trimIndent()

    override fun getAdditionalHighlightingTagToDescriptorMap(): MutableMap<String, TextAttributesKey>? = null

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = DESCRIPTORS

    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    override fun getDisplayName(): String = "NSIS"

    companion object {
        private val DESCRIPTORS = arrayOf(
            AttributesDescriptor("주석//줄 주석 (; #)", NsisColors.LINE_COMMENT),
            AttributesDescriptor("주석//블록 주석", NsisColors.BLOCK_COMMENT),
            AttributesDescriptor("명령어", NsisColors.INSTRUCTION),
            AttributesDescriptor("블록 키워드 (Section·Function)", NsisColors.BLOCK_KEYWORD),
            AttributesDescriptor("전처리기 지시자 (!include)", NsisColors.PREPROCESSOR),
            AttributesDescriptor("변수 (${'$'}INSTDIR)", NsisColors.VARIABLE),
            AttributesDescriptor("정의 참조 (${'$'}{MUI_ICON})", NsisColors.DEFINE_REF),
            AttributesDescriptor("문자열", NsisColors.STRING),
            AttributesDescriptor("숫자", NsisColors.NUMBER),
            AttributesDescriptor("식별자", NsisColors.IDENTIFIER),
            AttributesDescriptor("연산자", NsisColors.OPERATOR),
            AttributesDescriptor("잘못된 문자", NsisColors.BAD_CHARACTER),
        )
    }
}
