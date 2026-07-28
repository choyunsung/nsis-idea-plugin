package kr.amcg.nsis

import com.intellij.execution.RunContentExecutor
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.OSProcessHandler
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.nio.charset.StandardCharsets

/**
 * 열려 있는 .nsi 를 makensis 로 컴파일하고 결과를 실행 콘솔에 흘린다.
 * makensis 는 설정(NSIS 페이지) → PATH → 표준 설치 경로 순으로 찾는다.
 */
class CompileNsisAction : AnAction() {

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE)
        e.presentation.isEnabledAndVisible = e.project != null && file != null && isNsis(file)
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
        if (!isNsis(file)) return

        FileDocumentManager.getInstance().saveAllDocuments()

        val exe = NsisSettings.getInstance().resolveMakensis()
        if (exe == null) {
            notify(
                project,
                "makensis 를 찾지 못했습니다. Settings → Tools → NSIS 에서 경로를 지정하세요.",
                NotificationType.ERROR,
            )
            return
        }

        val workDir = file.parent?.path
        val cmd = GeneralCommandLine(exe)
            .withParameters(file.path)
            .withCharset(StandardCharsets.UTF_8)
        if (workDir != null) cmd.withWorkDirectory(workDir)

        try {
            val handler = OSProcessHandler(cmd)
            RunContentExecutor(project, handler)
                .withTitle("makensis: ${file.name}")
                .withActivateToolWindow(true)
                .withStop({ handler.destroyProcess() }, { !handler.isProcessTerminated })
                .run()
        } catch (t: Throwable) {
            notify(project, "makensis 실행 실패: ${t.message}", NotificationType.ERROR)
        }
    }

    private fun isNsis(file: VirtualFile): Boolean {
        val ext = file.extension?.lowercase()
        return ext == "nsi" || ext == "nsh"
    }

    private fun notify(project: Project, message: String, type: NotificationType) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("NSIS")
            .createNotification(message, type)
            .notify(project)
    }
}
