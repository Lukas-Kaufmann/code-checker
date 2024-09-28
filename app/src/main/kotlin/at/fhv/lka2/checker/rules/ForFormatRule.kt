package at.fhv.lka2.checker.rules

import at.fhv.lka2.checker.config.RuleConfig
import at.fhv.lka2.checker.getSourceText
import at.fhv.lka2.checker.model.CRule
import at.fhv.lka2.checker.model.JavaRule
import at.fhv.lka2.checker.model.Violation
import com.github.javaparser.ast.stmt.ForStmt
import org.eclipse.cdt.core.dom.ast.IASTForStatement
import org.eclipse.cdt.core.dom.ast.IASTStatement
import java.io.File

data class ForFormatRuleConfig(override val enabled: Boolean = true) : RuleConfig

private val forPattern = "for \\(\\S.*; .*; .*\\) \\{(.|\\n)*\\}".toRegex()

class ForFormatJavaRule(config: ForFormatRuleConfig = ForFormatRuleConfig()) : JavaRule<ForFormatRuleConfig>(config) {

    override fun visit(forStatement: ForStmt, arg: MutableList<Violation>) {
        val file = forStatement.findCompilationUnit().get().storage.get().path
        val start = forStatement.begin.get()
        val end = forStatement.end.get()

        val sourceText = file.getSourceText(start, end)

        if (!sourceText.matches(forPattern)) {
            arg.add(Violation(Violation.Location(file.toFile(), start.line), "Invalid format for for statement"))
        }

        super.visit(forStatement, arg)
    }
}

class ForFormatCRule(config: ForFormatRuleConfig = ForFormatRuleConfig()) : CRule<ForFormatRuleConfig>(config) {

    override fun visit(statement: IASTStatement): Int {
        if (statement is IASTForStatement) {
            val sourceText = statement.getSourceText()

            if (!sourceText.matches(forPattern)) {
                addViolation(
                    Violation(
                        Violation.Location(
                            File(statement.fileLocation.fileName),
                            statement.fileLocation.startingLineNumber
                        ),
                        "Invalid format for for statement"
                    )
                )
            }
        }
        return super.visit(statement)
    }
}
