package at.fhv.lka2.checker.rules

import at.fhv.lka2.checker.config.RuleConfig
import at.fhv.lka2.checker.model.JavaRule
import at.fhv.lka2.checker.model.Violation
import com.github.javaparser.ast.stmt.SwitchStmt
import com.github.javaparser.ast.stmt.TryStmt

data class SwitchFormatRuleConfig(override val enabled: Boolean = true) : RuleConfig


class SwitchFormatJavaRule(config: SwitchFormatRuleConfig = SwitchFormatRuleConfig()) :
    JavaRule<SwitchFormatRuleConfig>(config) {

    private val singleIfPattern = "switch (\\S.*\\S) \\{(.|\\n)*\\}".toRegex()

    override fun visit(switchStmt: SwitchStmt, arg: MutableList<Violation>) {
        val file = switchStmt.findCompilationUnit().get().storage.get().path
        val start = switchStmt.begin.get()
        val end = switchStmt.end.get()

        val sourceText = file.getSourceText(start, end)

        if (!sourceText.matches(singleIfPattern)) {
            arg.add(Violation(Violation.Location(file.toFile(), start.line), "Invalid format for switch statement"))
        }
        super.visit(switchStmt, arg)
    }
}
