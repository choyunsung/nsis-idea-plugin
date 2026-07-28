package kr.amcg.nsis

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet

/**
 * NSIS 는 줄 단위 명령 나열이라 PSI 트리를 깊게 만들 이유가 적다.
 * 여기서는 토큰을 평평하게 담기만 하고, 구조(Section/Function/!macro)는
 * [NsisOutline] 이 텍스트에서 직접 뽑아 구조뷰·접기·검사에 쓴다.
 */
class NsisParserDefinition : ParserDefinition {

    override fun createLexer(project: Project?) = NsisLexer()

    override fun createParser(project: Project?): PsiParser = PsiParser { root, builder ->
        val mark = builder.mark()
        while (!builder.eof()) {
            builder.advanceLexer()
        }
        mark.done(root)
        builder.treeBuilt
    }

    override fun getFileNodeType(): IFileElementType = FILE

    override fun getWhitespaceTokens(): TokenSet = TokenSet.create(TokenType.WHITE_SPACE)

    override fun getCommentTokens(): TokenSet = NsisTokenTypes.COMMENTS

    override fun getStringLiteralElements(): TokenSet = NsisTokenTypes.STRINGS

    override fun createElement(node: ASTNode): PsiElement = ASTWrapperPsiElement(node)

    override fun createFile(viewProvider: FileViewProvider): PsiFile = NsisFile(viewProvider)

    companion object {
        val FILE = IFileElementType(NsisLanguage)
    }
}
