package kr.amcg.nsis

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.util.SystemInfo
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder
import java.io.File
import javax.swing.JComponent
import javax.swing.JPanel

@Service(Service.Level.APP)
@State(name = "NsisSettings", storages = [Storage("nsis.xml")])
class NsisSettings : PersistentStateComponent<NsisSettings.State> {

    class State {
        @JvmField
        var makensisPath: String = ""
    }

    private var state = State()

    override fun getState(): State = state
    override fun loadState(s: State) {
        state = s
    }

    var makensisPath: String
        get() = state.makensisPath
        set(v) {
            state.makensisPath = v
        }

    /** 설정값 → PATH → 흔한 설치 경로 순으로 makensis 를 찾는다. */
    fun resolveMakensis(): String? {
        makensisPath.trim().takeIf { it.isNotEmpty() }?.let { p ->
            return if (File(p).canExecute()) p else null
        }
        val candidates = buildList {
            if (SystemInfo.isWindows) {
                add("C:\\Program Files (x86)\\NSIS\\makensis.exe")
                add("C:\\Program Files\\NSIS\\makensis.exe")
            } else {
                add("/opt/homebrew/bin/makensis")
                add("/usr/local/bin/makensis")
                add("/usr/bin/makensis")
            }
        }
        candidates.firstOrNull { File(it).canExecute() }?.let { return it }
        return findOnPath()
    }

    private fun findOnPath(): String? {
        val exe = if (SystemInfo.isWindows) "makensis.exe" else "makensis"
        val path = System.getenv("PATH") ?: return null
        return path.split(File.pathSeparatorChar)
            .asSequence()
            .map { File(it, exe) }
            .firstOrNull { it.canExecute() }
            ?.absolutePath
    }

    companion object {
        fun getInstance(): NsisSettings = service<NsisSettings>()
    }
}

class NsisConfigurable : Configurable {

    private var panel: JPanel? = null
    private val field = TextFieldWithBrowseButton()

    override fun getDisplayName(): String = "NSIS"

    override fun createComponent(): JComponent {
        val detected = NsisSettings.getInstance().resolveMakensis() ?: "찾지 못함"
        val p = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("makensis 경로:"), field, 1, false)
            .addComponentToRightColumn(JBLabel("<html><small>비워 두면 자동 탐색 — 현재: $detected</small></html>"))
            .addComponentFillVertically(JPanel(), 0)
            .panel
        panel = p
        return p
    }

    override fun isModified(): Boolean = field.text != NsisSettings.getInstance().makensisPath

    override fun apply() {
        NsisSettings.getInstance().makensisPath = field.text.trim()
    }

    override fun reset() {
        field.text = NsisSettings.getInstance().makensisPath
    }

    override fun disposeUIResources() {
        panel = null
    }
}
