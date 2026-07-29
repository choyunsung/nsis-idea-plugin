package kr.amcg.nsis

import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import java.io.File

/**
 * 스크립트가 가리키는 경로를 실제 파일로 옮긴다.
 *
 * 검사([NsisAnnotator])와 ⌘+클릭 이동([NsisPathReferenceProvider])이 같은 규칙을 봐야
 * "경고는 없는데 열리진 않는다" 같은 어긋남이 안 생긴다.
 *
 * 해석은 [VirtualFile] 로 한다 — 디스크를 직접 뒤지면 아직 저장 안 된 파일이나
 * 로컬이 아닌 파일 시스템을 놓친다.
 */
object NsisPaths {

    /** NSIS 는 역슬래시를 쓰지만 VFS 경로는 항상 `/` 다. */
    fun normalize(path: String): String = path.replace('\\', '/')

    /** NSIS 설치본의 `Include` 폴더. 설치를 못 찾으면 null. */
    fun includeDir(): VirtualFile? {
        val dir = NsisSettings.getInstance().resolveIncludeDir() ?: return null
        return LocalFileSystem.getInstance().findFileByIoFile(dir)
    }

    /**
     * `!include` 가 실제로 뒤지는 폴더들 — 스크립트 폴더, 스크립트가 `!addincludedir` 로
     * 추가한 폴더, 그리고 NSIS 설치본의 `Include` 폴더 순.
     */
    fun searchPath(baseDir: VirtualFile, refs: List<NsisPathRef>): List<VirtualFile> {
        val dirs = ArrayList<VirtualFile>()
        dirs += baseDir
        for (r in refs) {
            if (!r.directive.equals("!addincludedir", ignoreCase = true)) continue
            find(baseDir, normalize(r.path))?.takeIf { it.isDirectory }?.let { dirs += it }
        }
        includeDir()?.let { dirs += it }
        return dirs
    }

    /** 참조가 가리키는 실제 파일. 못 찾으면 null. */
    fun resolve(ref: NsisPathRef, baseDir: VirtualFile, allRefs: List<NsisPathRef>): VirtualFile? {
        val n = normalize(ref.path)
        if (File(n).isAbsolute) return LocalFileSystem.getInstance().findFileByPath(n)
        if (ref.directive.equals("!include", ignoreCase = true)) {
            return searchPath(baseDir, allRefs).firstNotNullOfOrNull { it.findFileByRelativePath(n) }
        }
        return baseDir.findFileByRelativePath(n)
    }

    private fun find(baseDir: VirtualFile, path: String): VirtualFile? =
        if (File(path).isAbsolute) {
            LocalFileSystem.getInstance().findFileByPath(path)
        } else {
            baseDir.findFileByRelativePath(path)
        }

    /**
     * `!include` 를 못 찾았을 때 경고할지.
     *
     * NSIS 설치를 못 찾은 상태에서 `MUI2.nsh` 처럼 폴더 없이 이름만 쓴 헤더는
     * 표준 헤더인지 오타인지 가릴 방법이 없으므로 경고하지 않는다 —
     * 없는 근거가 없는데 경고부터 띄우면 정상 스크립트가 온통 노란 줄이 된다.
     * 반면 `sub/foo.nsh` 처럼 경로가 붙은 참조는 프로젝트 안을 가리키므로 그대로 확인한다.
     */
    fun shouldWarnMissingInclude(path: String): Boolean =
        normalize(path).contains('/') || includeDir() != null
}
