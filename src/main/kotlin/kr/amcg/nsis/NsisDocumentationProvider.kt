package kr.amcg.nsis

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.psi.PsiElement
import com.intellij.openapi.util.text.StringUtil

/**
 * 명령어 위에서 Ctrl/⌘+Q 를 누르면 문법 한 줄과 설명을 띄운다.
 * 사전에 없는 낱말은 NSIS 공식 문서 링크로 넘긴다.
 */
class NsisDocumentationProvider : AbstractDocumentationProvider() {

    override fun getQuickNavigateInfo(element: PsiElement?, originalElement: PsiElement?): String? {
        val key = keyOf(originalElement ?: element) ?: return null
        return NsisKeywords.DOCS[key]?.first
    }

    override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? {
        val target = originalElement ?: element ?: return null
        if (target.containingFile !is NsisFile) return null
        val word = target.text?.trim().orEmpty()
        if (word.isEmpty()) return null
        val key = keyOf(target) ?: return null

        val doc = NsisKeywords.DOCS[key]
        val display = NsisKeywords.DISPLAY[key] ?: word
        val sb = StringBuilder()

        sb.append("<div class='definition'><pre>")
        sb.append(StringUtil.escapeXmlEntities(doc?.first ?: display))
        sb.append("</pre></div>")

        if (doc != null) {
            sb.append("<div class='content'>")
            sb.append(StringUtil.escapeXmlEntities(doc.second))
            sb.append("</div>")
        } else if (!isKnown(key)) {
            return null
        }

        sb.append("<table class='sections'><tr><td valign='top' class='section'><p>문서</p></td><td valign='top'>")
        sb.append("<a href='https://nsis.sourceforge.io/Docs/'>NSIS User Manual</a>")
        sb.append("</td></tr></table>")
        return sb.toString()
    }

    private fun isKnown(key: String): Boolean =
        key in NsisKeywords.INSTRUCTIONS ||
            key in NsisKeywords.BLOCK_KEYWORDS ||
            key in NsisKeywords.PREPROCESSOR_DIRECTIVES

    private fun keyOf(element: PsiElement?): String? {
        val t = element?.text?.trim()?.lowercase() ?: return null
        if (t.isEmpty()) return null
        return t
    }
}
