package at.fhv.lka2.checker.rules

import at.fhv.lka2.checker.config.RuleConfig
import at.fhv.lka2.checker.getSourceText
import at.fhv.lka2.checker.model.CRule
import at.fhv.lka2.checker.model.JavaRule
import at.fhv.lka2.checker.model.Violation
import com.github.javaparser.ast.stmt.WhileStmt
import org.eclipse.cdt.core.dom.ast.IASTStatement
import org.eclipse.cdt.core.dom.ast.IASTWhileStatement
import java.io.File

data class WhileFormatRuleConfig(override val enabled: Boolean = true) : RuleConfig

private val whilePattern = "while \\(\\S.*\\S\\) \\{(.|\\n)*\\}".toRegex()

/**
 * WhileFormatJavaRule: Enforces proper formatting of while statements in Java source files.
 *
 * This rule checks that the while statements follow a specific format:
 * - The structure should be: `while (condition) { ... }`
 * - There should be no extra spaces in the parentheses or braces.
 *
 * The regex pattern used for validation is: `while \\(\\S.*\\S\\) \\{(.|\\n)*\\}`.
 *
 * Violations are reported when the actual formatting of a while statement does not conform
 * to this pattern.
 */
class WhileFormatJavaRule(config: WhileFormatRuleConfig = WhileFormatRuleConfig()) :
    JavaRule<WhileFormatRuleConfig>(config) {

    override fun visit(whileStatement: WhileStmt, arg: MutableList<Violation>) {
        val file = whileStatement.findCompilationUnit().get().storage.get().path
        val start = whileStatement.begin.get()
        val end = whileStatement.end.get()

        val sourceText = file.getSourceText(start, end)

        if (!sourceText.matches(whilePattern)) {
            arg.add(Violation(Violation.Location(file.toFile(), start.line), "Invalid format for while statement"))
        }
        super.visit(whileStatement, arg)
    }
}

/**
 * WhileFormatCRule: Enforces proper formatting of while statements in C/C++ source files.
 *
 * This rule checks that while statements adhere to the required format:
 * - The expected structure is: `while (condition) { ... }`
 * - Formatting should not have any unnecessary spaces in the parentheses or braces.
 *
 * The regex pattern used for validation is: `while \\(\\S.*\\S\\) \\{(.|\\n)*\\}`.
 *
 * Violations are reported for any while statement that does not match the expected format.
 */
class WhileFormatCRule(config: WhileFormatRuleConfig = WhileFormatRuleConfig()) : CRule<WhileFormatRuleConfig>(config) {

    override fun visit(statement: IASTStatement?): Int {
        if (statement is IASTWhileStatement) {
            val sourceText = statement.getSourceText()

            if (!sourceText.matches(whilePattern)) {
                addViolation(
                    Violation(
                        Violation.Location(
                            File(statement.fileLocation.fileName),
                            statement.fileLocation.startingLineNumber
                        ),
                        "Invalid format for while statement"
                    )
                )
            }
        }
        return super.visit(statement)
    }
}