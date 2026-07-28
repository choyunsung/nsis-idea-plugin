package kr.amcg.nsis

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import java.io.File

/**
 * NSIS 스크립트 검사.
 *
 * PSI 트리가 평평하므로 파일 요소에서 한 번만 돌고, 위치는 [NsisOutline] 이 계산한
 * 절대 오프셋을 그대로 쓴다.
 */
class NsisAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element !is NsisFile) return

        val text = element.text
        val analysis = NsisOutline.analyze(text)
        val fileLength = text.length

        fun range(start: Int, end: Int): TextRange? {
            if (start < 0 || end > fileLength || end <= start) return null
            return TextRange(start, end)
        }

        // 1) 짝이 안 맞는 블록
        for (p in analysis.problems) {
            val r = range(p.startOffset, p.endOffset) ?: continue
            holder.newAnnotation(HighlightSeverity.WARNING, p.message).range(r).create()
        }

        // 2) 유니코드 / BOM — 비ASCII 를 쓰면서 준비가 안 된 스크립트는 설치 화면 글자가 깨진다
        if (analysis.firstNonAsciiOffset >= 0) {
            val head = range(analysis.firstNonAsciiOffset, minOf(analysis.firstNonAsciiOffset + 1, fileLength))
            if (head != null) {
                if (!analysis.hasUnicodeTrue) {
                    holder.newAnnotation(
                        HighlightSeverity.WARNING,
                        "비ASCII 문자가 있는데 `Unicode true` 가 없습니다 — 설치 화면 문구가 깨질 수 있습니다",
                    ).range(head).create()
                }
                val vf = element.virtualFile
                if (vf != null && vf.bom == null) {
                    holder.newAnnotation(
                        HighlightSeverity.WEAK_WARNING,
                        "이 파일에 BOM 이 없습니다 — NSIS 유니코드 빌드는 소스를 UTF-8 BOM(또는 UTF-16LE)으로 읽어야 " +
                            "라이선스·마법사 문구가 깨지지 않습니다",
                    ).range(head).create()
                }
            }
        }

        // 3) 없는 파일을 가리키는 경로 인자
        val baseDir = element.virtualFile?.parent?.path
        if (baseDir != null) {
            for (ref in analysis.pathRefs) {
                val r = range(ref.startOffset, ref.endOffset) ?: continue
                val normalized = ref.path.replace('\\', File.separatorChar).replace('/', File.separatorChar)
                val candidate = File(normalized).let { if (it.isAbsolute) it else File(baseDir, normalized) }
                if (!candidate.exists()) {
                    holder.newAnnotation(
                        HighlightSeverity.WARNING,
                        "${ref.directive} 대상이 없습니다: ${candidate.path}",
                    ).range(r).create()
                }
            }
        }

        // 4) 알 수 없는 !전처리기 지시자 (오타 잡기)
        annotateUnknownDirectives(text, holder, ::range)
    }

    private fun annotateUnknownDirectives(
        text: String,
        holder: AnnotationHolder,
        range: (Int, Int) -> TextRange?,
    ) {
        val known = NsisKeywords.PREPROCESSOR_DIRECTIVES.toSet()
        val regex = Regex("""(?m)^\s*(![A-Za-z_][A-Za-z0-9_]*)""")
        for (m in regex.findAll(text)) {
            val g = m.groups[1] ?: continue
            val word = g.value.lowercase()
            if (word in known) continue
            val r = range(g.range.first, g.range.last + 1) ?: continue
            holder.newAnnotation(
                HighlightSeverity.WARNING,
                "알 수 없는 전처리기 지시자입니다: ${g.value}",
            ).range(r).create()
        }
    }
}
