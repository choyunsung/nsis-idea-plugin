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

    /**
     * NSIS 표준 헤더(`MUI2.nsh` · `LogicLib.nsh` · `WinMessages.nsh` …)가 들어 있는 `Include` 폴더.
     *
     * `NSISDIR` 환경 변수를 먼저 보고, 없으면 makensis 위치에서 유도한다.
     * - 윈도 표준 설치: `<NSIS>\makensis.exe` → `<NSIS>\Include`
     * - Homebrew 등 유닉스: `<prefix>/bin/makensis` → `<prefix>/share/nsis/Include`
     *
     * NSIS 가 설치돼 있지 않으면 null — 이 경우 검사 쪽에서 표준 헤더를 경고하지 않는다.
     */
    fun resolveIncludeDir(): File? {
        System.getenv("NSISDIR")?.trim()?.takeIf { it.isNotEmpty() }?.let { d ->
            File(d, "Include").takeIf { it.isDirectory }?.let { return it }
        }
        val binDir = resolveMakensis()?.let { File(it).absoluteFile.parentFile } ?: return null
        val candidates = buildList {
            add(File(binDir, "Include"))
            binDir.parentFile?.let { add(File(it, "share${File.separatorChar}nsis${File.separatorChar}Include")) }
        }
        return candidates.firstOrNull { it.isDirectory }
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
