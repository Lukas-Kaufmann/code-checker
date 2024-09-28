package at.fhv.lka2.checker.rules

import at.fhv.lka2.checker.config.RuleConfig
import at.fhv.lka2.checker.getSourceText
import at.fhv.lka2.checker.model.CRule
import at.fhv.lka2.checker.model.JavaRule
import at.fhv.lka2.checker.model.Violation
import com.github.javaparser.ast.stmt.SwitchStmt
import org.eclipse.cdt.core.dom.ast.IASTStatement
import org.eclipse.cdt.core.dom.ast.IASTSwitchStatement
import java.io.File

data class SwitchFormatRuleConfig(override val enabled: Boolean = true) : RuleConfig

private val switchPattern = "switch (\\S.*\\S) \\{(.|\\n)*\\}".toRegex()

class SwitchFormatJavaRule(config: SwitchFormatRuleConfig = SwitchFormatRuleConfig()) :
    JavaRule<SwitchFormatRuleConfig>(config) {

    override fun visit(switchStmt: SwitchStmt, arg: MutableList<Violation>) {
        val file = switchStmt.findCompilationUnit().get().storage.get().path
        val start = switchStmt.begin.get()
        val end = switchStmt.end.get()

        val sourceText = file.getSourceText(start, end)

        if (!sourceText.matches(switchPattern)) {
            arg.add(Violation(Violation.Location(file.toFile(), start.line), "Invalid format for switch statement"))
        }
        super.visit(switchStmt, arg)
    }
}

class SwitchFormatCRule(config: SwitchFormatRuleConfig = SwitchFormatRuleConfig()): CRule<SwitchFormatRuleConfig>(config) {
    override fun visit(statement: IASTStatement?): Int {
        if (statement is IASTSwitchStatement) {
            val sourceText = statement.getSourceText()
            if(!sourceText.matches(switchPattern)) {
                addViolation(
                    Violation(
                        Violation.Location(
                            File(statement.fileLocation.fileName),
                            statement.fileLocation.startingLineNumber
                        ),
                        "Invalid format for switch statement"
                    )
                )
            }
        }
        return super.visit(statement)
    }
}