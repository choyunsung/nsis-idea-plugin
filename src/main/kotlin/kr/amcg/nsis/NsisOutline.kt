package kr.amcg.nsis

/** Section / Function / !macro 처럼 여닫는 블록 하나 */
data class NsisRegion(
    val kind: Kind,
    val name: String,
    val startOffset: Int,
    val endOffset: Int,
    val nameOffset: Int,
    val nameLength: Int,
) {
    enum class Kind(val display: String) {
        SECTION("Section"),
        SECTION_GROUP("SectionGroup"),
        FUNCTION("Function"),
        MACRO("!macro"),
        PAGE_EX("PageEx"),
    }
}

data class NsisProblem(val startOffset: Int, val endOffset: Int, val message: String)

/** `!define NAME`, `Var name`, `Function name` 처럼 이름이 붙는 선언 */
data class NsisSymbol(val name: String, val offset: Int, val detail: String = "")

/** `File "assets\x.bmp"` 처럼 파일 경로를 가리키는 인자 */
data class NsisPathRef(val path: String, val startOffset: Int, val endOffset: Int, val directive: String)

class NsisAnalysis(
    val regions: List<NsisRegion>,
    val problems: List<NsisProblem>,
    val defines: List<NsisSymbol>,
    val variables: List<NsisSymbol>,
    val functions: List<NsisSymbol>,
    val sections: List<NsisSymbol>,
    val macros: List<NsisSymbol>,
    val pathRefs: List<NsisPathRef>,
    val hasUnicodeTrue: Boolean,
    val firstNonAsciiOffset: Int,
)

/**
 * NSIS 소스를 줄 단위로 훑어 구조·선언·문제를 뽑아낸다.
 * PSI 파서를 깊게 만들지 않고 구조뷰·코드접기·검사·자동완성을 한 곳에서 먹여 살리는 역할.
 */
object NsisOutline {

    private val OPENERS = mapOf(
        "section" to NsisRegion.Kind.SECTION,
        "sectiongroup" to NsisRegion.Kind.SECTION_GROUP,
        "subsection" to NsisRegion.Kind.SECTION_GROUP,
        "function" to NsisRegion.Kind.FUNCTION,
        "!macro" to NsisRegion.Kind.MACRO,
        "pageex" to NsisRegion.Kind.PAGE_EX,
    )

    private val CLOSERS = mapOf(
        "sectionend" to NsisRegion.Kind.SECTION,
        "sectiongroupend" to NsisRegion.Kind.SECTION_GROUP,
        "subsectionend" to NsisRegion.Kind.SECTION_GROUP,
        "functionend" to NsisRegion.Kind.FUNCTION,
        "!macroend" to NsisRegion.Kind.MACRO,
        "pageexend" to NsisRegion.Kind.PAGE_EX,
    )

    private val CONDITIONALS = setOf(
        "!ifdef", "!ifndef", "!if", "!ifmacrodef", "!ifmacrondef", "!else", "!endif",
    )

    private class Open(
        val kind: NsisRegion.Kind,
        val name: String,
        val startOffset: Int,
        val nameOffset: Int,
        val nameLength: Int,
    )

    fun analyze(text: CharSequence): NsisAnalysis {
        val regions = ArrayList<NsisRegion>()
        val problems = ArrayList<NsisProblem>()
        val defines = ArrayList<NsisSymbol>()
        val variables = ArrayList<NsisSymbol>()
        val functions = ArrayList<NsisSymbol>()
        val sections = ArrayList<NsisSymbol>()
        val macros = ArrayList<NsisSymbol>()
        val pathRefs = ArrayList<NsisPathRef>()
        val stack = ArrayList<Open>()

        var hasUnicodeTrue = false
        var firstNonAscii = -1
        var sawConditional = false
        var inBlockComment = false

        val len = text.length
        var offset = 0

        while (offset <= len) {
            var lineEnd = offset
            while (lineEnd < len && text[lineEnd] != '\n') lineEnd++
            val raw = text.subSequence(offset, lineEnd).toString()

            if (firstNonAscii < 0) {
                for (k in raw.indices) {
                    if (raw[k].code > 127) { firstNonAscii = offset + k; break }
                }
            }

            val (code, stillInBlockComment) = stripComments(raw, inBlockComment)
            inBlockComment = stillInBlockComment
            processLine(
                code, offset, stack, regions, problems, defines, variables,
                functions, sections, macros, pathRefs,
                onUnicodeTrue = { hasUnicodeTrue = true },
                onConditional = { sawConditional = true },
            )

            if (lineEnd >= len) break
            offset = lineEnd + 1
        }

        // 조건부 컴파일이 섞인 파일은 정적으로 짝을 맞출 수 없으니 균형 검사를 접는다
        // (!ifdef 로 갈라지는 Section 이 흔한 오탐 원인)
        if (!sawConditional) {
            for (open in stack) {
                problems += NsisProblem(
                    open.startOffset,
                    open.startOffset + open.kind.display.length,
                    "${open.kind.display} 블록이 닫히지 않았습니다 — ${closerFor(open.kind)} 가 없습니다",
                )
            }
        }

        return NsisAnalysis(
            regions = regions.sortedBy { it.startOffset },
            problems = problems,
            defines = defines,
            variables = variables,
            functions = functions,
            sections = sections,
            macros = macros,
            pathRefs = pathRefs,
            hasUnicodeTrue = hasUnicodeTrue,
            firstNonAsciiOffset = firstNonAscii,
        )
    }

    private fun closerFor(kind: NsisRegion.Kind): String = when (kind) {
        NsisRegion.Kind.SECTION -> "SectionEnd"
        NsisRegion.Kind.SECTION_GROUP -> "SectionGroupEnd"
        NsisRegion.Kind.FUNCTION -> "FunctionEnd"
        NsisRegion.Kind.MACRO -> "!macroend"
        NsisRegion.Kind.PAGE_EX -> "PageExEnd"
    }

    @Suppress("LongParameterList")
    private fun processLine(
        code: String,
        lineOffset: Int,
        stack: ArrayList<Open>,
        regions: ArrayList<NsisRegion>,
        problems: ArrayList<NsisProblem>,
        defines: ArrayList<NsisSymbol>,
        variables: ArrayList<NsisSymbol>,
        functions: ArrayList<NsisSymbol>,
        sections: ArrayList<NsisSymbol>,
        macros: ArrayList<NsisSymbol>,
        pathRefs: ArrayList<NsisPathRef>,
        onUnicodeTrue: () -> Unit,
        onConditional: () -> Unit,
    ) {
        val first = nextArg(code, 0) ?: return
        val word = first.value.lowercase()
        val afterFirst = first.end

        if (word in CONDITIONALS) onConditional()

        CLOSERS[word]?.let { kind ->
            val open = stack.lastOrNull()
            if (open == null || open.kind != kind) {
                problems += NsisProblem(
                    lineOffset + first.start,
                    lineOffset + first.end,
                    if (open == null) "짝이 되는 ${kind.display} 가 없습니다"
                    else "${open.kind.display} 블록 안에서 ${first.value} 로 닫고 있습니다",
                )
            } else {
                stack.removeAt(stack.size - 1)
                regions += NsisRegion(
                    kind = open.kind,
                    name = open.name,
                    startOffset = open.startOffset,
                    endOffset = lineOffset + first.end,
                    nameOffset = open.nameOffset,
                    nameLength = open.nameLength,
                )
            }
            return
        }

        OPENERS[word]?.let { kind ->
            // Section [/o] [name]  /  Function [un.]name  /  !macro name params
            var arg = nextArg(code, afterFirst)
            while (arg != null && arg.value.startsWith("/")) arg = nextArg(code, arg.end)
            val rawName = arg?.value.orEmpty()
            val displayName = rawName.trimStart('!', '-').ifEmpty {
                if (kind == NsisRegion.Kind.SECTION) "(이름 없음)" else ""
            }
            val nameOffset = lineOffset + (arg?.start ?: first.start)
            val nameLength = (arg?.end ?: first.end) - (arg?.start ?: first.start)
            stack += Open(kind, displayName, lineOffset + first.start, nameOffset, nameLength)

            val sym = NsisSymbol(displayName, nameOffset)
            when (kind) {
                NsisRegion.Kind.FUNCTION -> functions += sym
                NsisRegion.Kind.MACRO -> macros += sym
                NsisRegion.Kind.SECTION, NsisRegion.Kind.SECTION_GROUP -> sections += sym
                else -> {}
            }
            return
        }

        when (word) {
            "unicode" -> {
                val v = nextArg(code, afterFirst)?.value?.lowercase()
                if (v == null || v == "true") onUnicodeTrue()
            }

            "!define" -> {
                var a = nextArg(code, afterFirst)
                while (a != null && a.value.startsWith("/")) a = nextArg(code, a.end)
                val nameArg = a
                if (nameArg != null) {
                    defines += NsisSymbol(nameArg.value, lineOffset + nameArg.start)
                    if (nameArg.value.uppercase() in NsisKeywords.PATH_DEFINES) {
                        val p = nextArg(code, nameArg.end)
                        if (p != null) addPathRef(pathRefs, p, lineOffset, "!define " + nameArg.value)
                    }
                }
            }

            "var" -> {
                var a = nextArg(code, afterFirst)
                while (a != null && a.value.startsWith("/")) a = nextArg(code, a.end)
                val nameArg = a
                if (nameArg != null) variables += NsisSymbol("$" + nameArg.value, lineOffset + nameArg.start)
            }
        }

        if (word in NsisKeywords.PATH_DIRECTIVES) {
            // 마지막 인자를 경로로 본다 (/r, /nonfatal, /oname=… 같은 스위치는 건너뜀)
            var arg = nextArg(code, afterFirst)
            var last: Arg? = null
            while (arg != null) {
                if (!arg.value.startsWith("/")) last = arg
                arg = nextArg(code, arg.end)
            }
            last?.let { addPathRef(pathRefs, it, lineOffset, first.value) }
        }
    }

    private fun addPathRef(out: ArrayList<NsisPathRef>, arg: Arg, lineOffset: Int, directive: String) {
        val v = arg.value
        if (v.isEmpty() || v.contains('$') || v.contains('*') || v.contains('?')) return
        out += NsisPathRef(v, lineOffset + arg.start, lineOffset + arg.end, directive)
    }

    // ---------- 문자열/주석을 존중하는 최소 파싱 도우미 ----------

    class Arg(val value: String, val start: Int, val end: Int)

    /**
     * 인용부호를 존중하며 [from] 다음 인자 하나를 떼어낸다.
     * [Arg.start]/[Arg.end] 는 따옴표를 **포함한** 줄 안의 위치, [Arg.value] 는 벗겨낸 알맹이.
     */
    fun nextArg(s: String, from: Int): Arg? {
        var i = from
        while (i < s.length && s[i].isWhitespace()) i++
        if (i >= s.length) return null
        val start = i
        val q = s[i]
        if (q == '"' || q == '\'' || q == '`') {
            i++
            val sb = StringBuilder()
            while (i < s.length && s[i] != q) {
                sb.append(s[i]); i++
            }
            val end = if (i < s.length) i + 1 else i
            return Arg(sb.toString(), start, end)
        }
        while (i < s.length && !s[i].isWhitespace()) i++
        return Arg(s.substring(start, i), start, i)
    }

    /**
     * 주석을 공백으로 지운 줄을 돌려준다. **원본과 길이가 같게** 유지해서
     * 줄 안 위치를 그대로 절대 오프셋으로 더할 수 있게 한다.
     */
    private fun stripComments(line: String, blockCommentOpen: Boolean): Pair<String, Boolean> {
        val sb = StringBuilder(line.length)
        var inBlock = blockCommentOpen
        var quote = ' '
        var i = 0
        while (i < line.length) {
            val c = line[i]
            if (inBlock) {
                if (c == '*' && i + 1 < line.length && line[i + 1] == '/') {
                    sb.append("  "); i += 2; inBlock = false
                } else {
                    sb.append(' '); i++
                }
                continue
            }
            if (quote != ' ') {
                if (c == '$' && i + 1 < line.length && line[i + 1] == '\\') {
                    val take = minOf(3, line.length - i)
                    sb.append(line, i, i + take); i += take
                    continue
                }
                if (c == quote) quote = ' '
                sb.append(c); i++
                continue
            }
            when {
                c == '"' || c == '\'' || c == '`' -> { quote = c; sb.append(c); i++ }
                c == ';' || c == '#' -> {
                    while (i < line.length) { sb.append(' '); i++ }
                }
                c == '/' && i + 1 < line.length && line[i + 1] == '*' -> {
                    sb.append("  "); i += 2; inBlock = true
                }
                else -> { sb.append(c); i++ }
            }
        }
        return sb.toString() to inBlock
    }
}
