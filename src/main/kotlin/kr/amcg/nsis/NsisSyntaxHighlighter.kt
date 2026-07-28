package kr.amcg.nsis

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors as D
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.tree.IElementType

object NsisColors {
    val LINE_COMMENT: TextAttributesKey = createTextAttributesKey("NSIS_LINE_COMMENT", D.LINE_COMMENT)
    val BLOCK_COMMENT: TextAttributesKey = createTextAttributesKey("NSIS_BLOCK_COMMENT", D.BLOCK_COMMENT)
    val STRING: TextAttributesKey = createTextAttributesKey("NSIS_STRING", D.STRING)
    val NUMBER: TextAttributesKey = createTextAttributesKey("NSIS_NUMBER", D.NUMBER)
    val INSTRUCTION: TextAttributesKey = createTextAttributesKey("NSIS_INSTRUCTION", D.KEYWORD)
    val BLOCK_KEYWORD: TextAttributesKey = createTextAttributesKey("NSIS_BLOCK_KEYWORD", D.KEYWORD)
    val PREPROCESSOR: TextAttributesKey = createTextAttributesKey("NSIS_PREPROCESSOR", D.METADATA)
    val VARIABLE: TextAttributesKey = createTextAttributesKey("NSIS_VARIABLE", D.INSTANCE_FIELD)
    val DEFINE_REF: TextAttributesKey = createTextAttributesKey("NSIS_DEFINE_REF", D.STATIC_FIELD)
    val IDENTIFIER: TextAttributesKey = createTextAttributesKey("NSIS_IDENTIFIER", D.IDENTIFIER)
    val OPERATOR: TextAttributesKey = createTextAttributesKey("NSIS_OPERATOR", D.OPERATION_SIGN)
    val BAD_CHARACTER: TextAttributesKey = createTextAttributesKey("NSIS_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER)
}

class NsisSyntaxHighlighter : SyntaxHighlighterBase() {

    override fun getHighlightingLexer(): Lexer = NsisLexer()

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
        val key = when (tokenType) {
            NsisTokenTypes.LINE_COMMENT -> NsisColors.LINE_COMMENT
            NsisTokenTypes.BLOCK_COMMENT -> NsisColors.BLOCK_COMMENT
            NsisTokenTypes.STRING -> NsisColors.STRING
            NsisTokenTypes.NUMBER -> NsisColors.NUMBER
            NsisTokenTypes.INSTRUCTION -> NsisColors.INSTRUCTION
            NsisTokenTypes.BLOCK_KEYWORD -> NsisColors.BLOCK_KEYWORD
            NsisTokenTypes.PREPROCESSOR -> NsisColors.PREPROCESSOR
            NsisTokenTypes.VARIABLE -> NsisColors.VARIABLE
            NsisTokenTypes.DEFINE_REF -> NsisColors.DEFINE_REF
            NsisTokenTypes.IDENTIFIER -> NsisColors.IDENTIFIER
            NsisTokenTypes.OPERATOR -> NsisColors.OPERATOR
            NsisTokenTypes.BAD_CHARACTER -> NsisColors.BAD_CHARACTER
            else -> null
        }
        return if (key == null) TextAttributesKey.EMPTY_ARRAY else arrayOf(key)
    }
}

class NsisSyntaxHighlighterFactory : SyntaxHighlighterFactory() {
    override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?): SyntaxHighlighter =
        NsisSyntaxHighlighter()
}
