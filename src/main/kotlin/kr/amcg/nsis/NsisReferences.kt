package kr.amcg.nsis

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager

/**
 * `!include "MUI2.nsh"` · `File "logo.ico"` 처럼 파일을 가리키는 인자에서
 * ⌘/Ctrl+클릭으로 그 파일을 연다.
 *
 * PSI 를 일부러 평평하게 두는 설계라 인자에 전용 노드가 없고, 잎 토큰은
 * 참조 기여자(`psi.referenceContributor`)를 조회하지 않는다. 그래서 참조 대신
 * 이동 핸들러를 쓴다 — 캐럿 위치를 [NsisOutline] 이 뽑아 둔 경로 참조와 맞춰 본다.
 */
class NsisGotoDeclarationHandler : GotoDeclarationHandler {

    override fun getGotoDeclarationTargets(
        sourceElement: PsiElement?,
        offset: Int,
        editor: Editor?,
    ): Array<PsiElement>? {
        val element = sourceElement ?: return null
        val file = element.containingFile as? NsisFile ?: return null
        // 편집 중인 사본은 virtualFile 이 없을 수 있어 원본 파일로 되짚는다
        val baseDir = (file.originalFile.virtualFile ?: file.virtualFile)?.parent ?: return null

        val all = NsisOutline.analyze(file.text).pathRefs
        val ref = all.firstOrNull { offset >= it.startOffset && offset < it.endOffset } ?: return null

        val target = NsisPaths.resolve(ref, baseDir, all) ?: return null
        val psiManager = PsiManager.getInstance(element.project)
        // 폴더(!addincludedir)면 디렉터리로, 파일이면 파일로 연다
        val psi = if (target.isDirectory) psiManager.findDirectory(target) else psiManager.findFile(target)
        return psi?.let { arrayOf<PsiElement>(it) }
    }
}
