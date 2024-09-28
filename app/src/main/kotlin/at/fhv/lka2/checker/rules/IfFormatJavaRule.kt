package at.fhv.lka2.checker.rules

import at.fhv.lka2.checker.config.RuleConfig
import at.fhv.lka2.checker.getSourceText
import at.fhv.lka2.checker.model.JavaRule
import at.fhv.lka2.checker.model.Violation
import com.github.javaparser.ast.stmt.IfStmt
import kotlin.math.sin

data class IfFormatRuleConfig(override val enabled: Boolean = true) : RuleConfig


class IfFormatJavaRule(config: IfFormatRuleConfig = IfFormatRuleConfig()) : JavaRule<IfFormatRuleConfig>(config) {

    private val singleIfPattern = "if \\(\\S.*\\S\\) \\{(.|\\n)*\\}".toRegex()

    private val bracketedPattern = "\\{(.|\\n)*\\}".toRegex()

    override fun visit(ifStatement: IfStmt, arg: MutableList<Violation>) {
        val file = ifStatement.findCompilationUnit().get().storage.get().path
        val start = ifStatement.begin.get()
        val end = ifStatement.end.get()

        val sourceText = file.getSourceText(start, end)

        if (!sourceText.matches(singleIfPattern)) {
            arg.add(Violation(Violation.Location(file.toFile(), start.line), "Invalid format for if pattern"))
        }

        if (ifStatement.hasElseBlock()) {
            val elseStatement = ifStatement.elseStmt.get()
            val elseSourceText = file.getSourceText(elseStatement.begin.get(), elseStatement.end.get())
            if (!elseSourceText.matches(bracketedPattern)) {
                arg.add(Violation(Violation.Location(file.toFile(), start.line), "Invalid format for if pattern"))
            }
        }
        super.visit(ifStatement, arg)
    }
}
