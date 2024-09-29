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

/**
 * ForFormatJavaRule: Enforces a specific format for `for` loops in Java code.
 *
 * This rule checks if the format of `for` statements in Java code follows a predefined pattern. By default, the rule expects
 * `for` loops to match the following regular expression: `for \\(\\S.*; .*; .*\\) \\{(.|\\n)*\\}`.
 * The pattern ensures:
 * - Proper spacing and structure within the parentheses
 * - The body of the `for` loop is enclosed with curly braces
 *
 * The purpose of this rule is to:
 * - Promote readability by enforcing a consistent structure for `for` loops
 * - Prevent poorly formatted or difficult-to-read `for` loops in the codebase
 *
 * Configuration:
 * - No configurable properties beyond enabling or disabling the rule (default: enabled)
 *
 * Example of a compliant `for` loop:
 * ```java
 * for (int i = 0; i < 10; i++) {
 *     System.out.println(i);
 * }
 * ```
 *
 * Example of a non-compliant `for` loop:
 * ```java
 * for(int i=0;i<10;i++) { System.out.println(i); }
 * // Violation: Invalid format for for statement
 * ```
 *
 * Refactoring tip for violations:
 * Ensure proper formatting for the `for` loop by adding appropriate spaces within the parentheses and
 */
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


/**
 * ForFormatCRule: Enforces a specific format for `for` loops in C/C++ code.
 *
 * This rule checks if the format of `for` statements in C/C++ code conforms to a predefined pattern. Similar to its Java counterpart,
 * the rule expects the `for` loops to match the following regular expression: `for \\(\\S.*; .*; .*\\) \\{(.|\\n)*\\}`.
 * The pattern ensures:
 * - Proper structure of the `for` loop header
 * - The loop body is enclosed within curly braces
 *
 * The purpose of this rule is to:
 * - Promote uniform and readable code through consistent formatting of `for` loops
 * - Prevent poorly formatted `for` statements in the C/C++ codebase
 *
 * Configuration:
 * - No configurable properties beyond enabling or disabling the rule (default: enabled)
 *
 * Example of a compliant `for` loop:
 * ```c
 * for (int i = 0; i < 10; i++) {
 *     printf("%d\n", i);
 * }
 * ```
 *
 * Example of a non-compliant `for` loop:
 * ```c
 * for(int i=0;i<10;i++) { printf("%d\n", i); }
 * // Violation: Invalid format for for statement
 * ```
 *
 * Refactoring tip for violations:
 * Ensure proper formatting of the `for` loop header and body. Add appropriate spaces and structure the loop body for readability.
 *
 * @property config The [ForFormatRuleConfig] for this rule
 */
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
