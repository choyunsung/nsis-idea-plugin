package kr.amcg.nsis

import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet

class NsisTokenType(debugName: String) : IElementType(debugName, NsisLanguage) {
    override fun toString(): String = "NSIS:" + super.toString()
}

object NsisTokenTypes {
    @JvmField val LINE_COMMENT = NsisTokenType("LINE_COMMENT")
    @JvmField val BLOCK_COMMENT = NsisTokenType("BLOCK_COMMENT")
    @JvmField val STRING = NsisTokenType("STRING")
    @JvmField val NUMBER = NsisTokenType("NUMBER")

    /** `$INSTDIR`, `$0`, `$R1`, `$$`, `$\n` */
    @JvmField val VARIABLE = NsisTokenType("VARIABLE")

    /** `${MUI_ICON}`, `${If}`, `$(LangString)` */
    @JvmField val DEFINE_REF = NsisTokenType("DEFINE_REF")

    /** `!include`, `!define`, `!insertmacro` … */
    @JvmField val PREPROCESSOR = NsisTokenType("PREPROCESSOR")

    /** 줄 첫 낱말이 알려진 NSIS 명령일 때 */
    @JvmField val INSTRUCTION = NsisTokenType("INSTRUCTION")

    /** Section / SectionEnd / Function / FunctionEnd … */
    @JvmField val BLOCK_KEYWORD = NsisTokenType("BLOCK_KEYWORD")

    @JvmField val IDENTIFIER = NsisTokenType("IDENTIFIER")
    @JvmField val OPERATOR = NsisTokenType("OPERATOR")
    @JvmField val BAD_CHARACTER = NsisTokenType("BAD_CHARACTER")

    @JvmField val COMMENTS: TokenSet = TokenSet.create(LINE_COMMENT, BLOCK_COMMENT)
    @JvmField val STRINGS: TokenSet = TokenSet.create(STRING)
}
