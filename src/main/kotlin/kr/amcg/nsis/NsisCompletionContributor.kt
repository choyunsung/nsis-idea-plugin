package kr.amcg.nsis

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext

/**
 * 커서 앞 글자를 보고 무엇을 제안할지 고른다.
 *
 *  `!`  → 전처리기 지시자      `$`  → 내장 상수·사용자 변수
 *  `${` → !define · LogicLib   줄 첫 낱말 → NSIS 명령
 *  그 밖에는 그 줄의 명령에 맞춰 (예: `!insertmacro` 뒤엔 MUI 매크로)
 */
class NsisCompletionContributor : CompletionContributor() {

    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().withLanguage(NsisLanguage),
            object : CompletionProvider<CompletionParameters>() {
                override fun addCompletions(
                    parameters: CompletionParameters,
                    context: ProcessingContext,
                    result: CompletionResultSet,
                ) = complete(parameters, result)
            },
        )
    }

    private fun complete(parameters: CompletionParameters, result: CompletionResultSet) {
        val text = parameters.originalFile.text
        val offset = parameters.offset.coerceIn(0, text.length)

        var lineStart = offset
        while (lineStart > 0 && text[lineStart - 1] != '\n') lineStart--

        var prefixStart = offset
        while (prefixStart > lineStart && !text[prefixStart - 1].isWhitespace()) prefixStart--

        val prefix = text.substring(prefixStart, offset)
        val beforePrefix = text.substring(lineStart, prefixStart)
        val isFirstWord = beforePrefix.isBlank()

        val out = result.withPrefixMatcher(prefix)
        val analysis = NsisOutline.analyze(text)

        when {
            prefix.startsWith("\${") -> addDefines(out, analysis)
            prefix.startsWith("$") -> addVariables(out, analysis)
            prefix.startsWith("!") -> addDirectives(out)
            isFirstWord -> {
                addCommands(out)
                addDirectives(out)
            }
            else -> addByContext(out, beforePrefix, analysis)
        }
    }

    private fun addCommands(out: CompletionResultSet) {
        for (cmd in NsisKeywords.ALL_COMMANDS) {
            val doc = NsisKeywords.DOCS[cmd.lowercase()]
            var e = LookupElementBuilder.create(cmd).bold()
            if (doc != null) e = e.withTailText("  " + doc.first.removePrefix(cmd).trim(), true)
            out.addElement(e)
        }
    }

    private fun addDirectives(out: CompletionResultSet) {
        for (d in NsisKeywords.PREPROCESSOR_DIRECTIVES) {
            val doc = NsisKeywords.DOCS[d]
            var e = LookupElementBuilder.create(d).withIcon(AllIcons.Nodes.Field)
            if (doc != null) e = e.withTailText("  " + doc.first.removePrefix(d).trim(), true)
            out.addElement(e)
        }
    }

    private fun addVariables(out: CompletionResultSet, analysis: NsisAnalysis) {
        for (v in NsisKeywords.VARIABLES) {
            out.addElement(
                LookupElementBuilder.create(v)
                    .withIcon(AllIcons.Nodes.Variable)
                    .withTypeText("내장", true),
            )
        }
        for (v in analysis.variables) {
            out.addElement(
                LookupElementBuilder.create(v.name)
                    .withIcon(AllIcons.Nodes.Variable)
                    .withTypeText("Var", true),
            )
        }
    }

    private fun addDefines(out: CompletionResultSet, analysis: NsisAnalysis) {
        for (d in analysis.defines) {
            out.addElement(
                LookupElementBuilder.create("\${${d.name}}")
                    .withIcon(AllIcons.Nodes.Field)
                    .withTypeText("!define", true),
            )
        }
        for (d in NsisKeywords.MUI_DEFINES) {
            out.addElement(
                LookupElementBuilder.create("\${$d}")
                    .withIcon(AllIcons.Nodes.Field)
                    .withTypeText("MUI2", true),
            )
        }
        for (d in NsisKeywords.LOGICLIB) {
            out.addElement(
                LookupElementBuilder.create("\${$d}")
                    .withIcon(AllIcons.Nodes.Method)
                    .withTypeText("LogicLib", true),
            )
        }
    }

    private fun addByContext(out: CompletionResultSet, beforePrefix: String, analysis: NsisAnalysis) {
        val head = NsisOutline.nextArg(beforePrefix, 0)?.value?.lowercase()
        when (head) {
            "!insertmacro" -> {
                for (m in NsisKeywords.MACROS) {
                    out.addElement(LookupElementBuilder.create(m).withTypeText("MUI2", true))
                }
                for (m in analysis.macros) {
                    out.addElement(LookupElementBuilder.create(m.name).withTypeText("!macro", true))
                }
            }

            "!define", "!undef", "!ifdef", "!ifndef" -> {
                for (d in NsisKeywords.MUI_DEFINES) {
                    out.addElement(LookupElementBuilder.create(d).withTypeText("MUI2", true))
                }
                for (d in analysis.defines) {
                    out.addElement(LookupElementBuilder.create(d.name).withTypeText("!define", true))
                }
            }

            "call" -> for (f in analysis.functions) {
                out.addElement(LookupElementBuilder.create(f.name).withIcon(AllIcons.Nodes.Method))
            }

            else -> {
                addVariables(out, analysis)
                addDefines(out, analysis)
            }
        }
    }
}
