package at.fhv.lka2.checker.rules

import at.fhv.lka2.checker.config.RuleConfig
import at.fhv.lka2.checker.getSourceText
import at.fhv.lka2.checker.model.JavaRule
import at.fhv.lka2.checker.model.Violation
import com.github.javaparser.ast.stmt.WhileStmt

data class WhileFormatRuleConfig(override val enabled: Boolean = true) : RuleConfig


class WhileFormatJavaRule(config: WhileFormatRuleConfig = WhileFormatRuleConfig()) : JavaRule<WhileFormatRuleConfig>(config) {

    private val singleIfPattern = "while \\(\\S.*\\S\\) \\{(.|\\n)*\\}".toRegex()

    override fun visit(whileStatement: WhileStmt, arg: MutableList<Violation>) {
        val file = whileStatement.findCompilationUnit().get().storage.get().path
        val start = whileStatement.begin.get()
        val end = whileStatement.end.get()

        val sourceText = file.getSourceText(start, end)

        if (!sourceText.matches(singleIfPattern)) {
            arg.add(Violation(Violation.Location(file.toFile(), start.line), "Invalid format for while statement"))
        }
        super.visit(whileStatement, arg)
    }
}
