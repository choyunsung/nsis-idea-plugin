package kr.amcg.nsis

import com.intellij.icons.AllIcons
import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.structureView.StructureViewBuilder
import com.intellij.ide.structureView.StructureViewModel
import com.intellij.ide.structureView.StructureViewModelBase
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.lang.PsiStructureViewFactory
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.psi.PsiFile
import javax.swing.Icon

class NsisStructureViewFactory : PsiStructureViewFactory {
    override fun getStructureViewBuilder(psiFile: PsiFile): StructureViewBuilder? {
        if (psiFile !is NsisFile) return null
        return object : TreeBasedStructureViewBuilder() {
            override fun createStructureViewModel(editor: Editor?): StructureViewModel =
                NsisStructureViewModel(psiFile)

            override fun isRootNodeShown(): Boolean = false
        }
    }
}

class NsisStructureViewModel(file: NsisFile) :
    StructureViewModelBase(file, NsisFileTreeElement(file)),
    StructureViewModel.ElementInfoProvider {

    override fun isAlwaysShowsPlus(element: StructureViewTreeElement): Boolean = false

    override fun isAlwaysLeaf(element: StructureViewTreeElement): Boolean =
        element !is NsisFileTreeElement
}

class NsisFileTreeElement(private val file: NsisFile) : StructureViewTreeElement {

    override fun getValue(): Any = file

    override fun navigate(requestFocus: Boolean) = file.navigate(requestFocus)
    override fun canNavigate(): Boolean = file.canNavigate()
    override fun canNavigateToSource(): Boolean = file.canNavigateToSource()

    override fun getPresentation(): ItemPresentation =
        PresentationData(file.name, null, NsisIcons.FILE, null)

    override fun getChildren(): Array<TreeElement> =
        NsisOutline.analyze(file.text).regions
            .map { NsisRegionTreeElement(file, it) as TreeElement }
            .toTypedArray()
}

class NsisRegionTreeElement(
    private val file: NsisFile,
    private val region: NsisRegion,
) : StructureViewTreeElement {

    override fun getValue(): Any = region

    override fun navigate(requestFocus: Boolean) {
        val vf = file.virtualFile ?: return
        OpenFileDescriptor(file.project, vf, region.nameOffset).navigate(requestFocus)
    }

    override fun canNavigate(): Boolean = file.virtualFile != null
    override fun canNavigateToSource(): Boolean = canNavigate()

    override fun getPresentation(): ItemPresentation = PresentationData(
        region.name.ifBlank { region.kind.display },
        region.kind.display,
        iconFor(region.kind),
        null,
    )

    override fun getChildren(): Array<TreeElement> = TreeElement.EMPTY_ARRAY

    private fun iconFor(kind: NsisRegion.Kind): Icon = when (kind) {
        NsisRegion.Kind.SECTION -> AllIcons.Nodes.Class
        NsisRegion.Kind.SECTION_GROUP -> AllIcons.Nodes.Package
        NsisRegion.Kind.FUNCTION -> AllIcons.Nodes.Method
        NsisRegion.Kind.MACRO -> AllIcons.Nodes.Field
        NsisRegion.Kind.PAGE_EX -> AllIcons.Nodes.Variable
    }
}
