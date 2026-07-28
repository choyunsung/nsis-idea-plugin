package kr.amcg.nsis

import com.intellij.lang.Language
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.util.IconLoader
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.psi.FileViewProvider
import javax.swing.Icon

/** NSIS(Nullsoft Scriptable Install System) 스크립트 언어. 명령어는 대소문자를 가리지 않는다. */
object NsisLanguage : Language("NSIS") {
    private fun readResolve(): Any = NsisLanguage
    override fun getDisplayName(): String = "NSIS"
    override fun isCaseSensitive(): Boolean = false
}

object NsisIcons {
    @JvmField
    val FILE: Icon = IconLoader.getIcon("/icons/nsis.svg", NsisIcons::class.java)
}

object NsisFileType : LanguageFileType(NsisLanguage) {
    override fun getName(): String = "NSIS"
    override fun getDescription(): String = "NSIS install script"
    override fun getDefaultExtension(): String = "nsi"
    override fun getIcon(): Icon = NsisIcons.FILE
}

class NsisFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, NsisLanguage) {
    override fun getFileType() = NsisFileType
    override fun toString(): String = "NSIS script"
}
