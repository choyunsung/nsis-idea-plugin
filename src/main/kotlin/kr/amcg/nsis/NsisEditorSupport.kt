package kr.amcg.nsis

import com.intellij.lang.ASTNode
import com.intellij.lang.Commenter
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement

class NsisCommenter : Commenter {
    // NSIS 는 ; 와 # 둘 다 줄 주석이지만, 토글에는 관례적인 ; 를 쓴다.
    override fun getLineCommentPrefix(): String = ";"
    override fun getBlockCommentPrefix(): String = "/*"
    override fun getBlockCommentSuffix(): String = "*/"
    override fun getCommentedBlockCommentPrefix(): String? = null
    override fun getCommentedBlockCommentSuffix(): String? = null
}

class NsisFoldingBuilder : FoldingBuilderEx(), DumbAware {

    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
        if (root !is NsisFile) return emptyArray()
        val analysis = NsisOutline.analyze(root.text)
        val out = ArrayList<FoldingDescriptor>()
        val textLength = root.textLength

        for (r in analysis.regions) {
            if (r.startOffset < 0 || r.endOffset > textLength || r.endOffset <= r.startOffset) continue
            // 한 줄짜리는 접을 이유가 없다
            if (document.getLineNumber(r.endOffset) <= document.getLineNumber(r.startOffset)) continue
            val node = root.node.findLeafElementAt(r.startOffset) ?: continue
            val label = if (r.name.isBlank()) r.kind.display else "${r.kind.display} ${r.name}"
            out += FoldingDescriptor(node, TextRange(r.startOffset, r.endOffset), null, "$label …")
        }
        return out.toTypedArray()
    }

    override fun getPlaceholderText(node: ASTNode): String = "…"

    override fun isCollapsedByDefault(node: ASTNode): Boolean = false
}
