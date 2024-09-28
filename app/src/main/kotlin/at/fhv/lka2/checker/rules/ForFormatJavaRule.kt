package at.fhv.lka2.checker.rules

import at.fhv.lka2.checker.config.RuleConfig
import at.fhv.lka2.checker.getSourceText
import at.fhv.lka2.checker.model.JavaRule
import at.fhv.lka2.checker.model.Violation
import com.github.javaparser.ast.stmt.ForStmt

data class ForFormatRuleConfig(override val enabled: Boolean = true) : RuleConfig


class ForFormatJavaRule(config: ForFormatRuleConfig = ForFormatRuleConfig()) : JavaRule<ForFormatRuleConfig>(config) {

    private val singleIfPattern = "for \\(\\S.*; .*; .*\\) \\{(.|\\n)*\\}".toRegex()

    override fun visit(forStatement: ForStmt, arg: MutableList<Violation>) {
        val file = forStatement.findCompilationUnit().get().storage.get().path
        val start = forStatement.begin.get()
        val end = forStatement.end.get()

        val sourceText = file.getSourceText(start, end)

        if (!sourceText.matches(singleIfPattern)) {
            arg.add(Violation(Violation.Location(file.toFile(), start.line), "Invalid format for for statement"))
        }

        super.visit(forStatement, arg)
    }
}
