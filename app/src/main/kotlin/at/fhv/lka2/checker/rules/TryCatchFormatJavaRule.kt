package at.fhv.lka2.checker.rules

import at.fhv.lka2.checker.config.RuleConfig
import at.fhv.lka2.checker.getSourceText
import at.fhv.lka2.checker.model.JavaRule
import at.fhv.lka2.checker.model.Violation
import com.github.javaparser.ast.stmt.TryStmt

data class TryCatchFormatRuleConfig(override val enabled: Boolean = true) : RuleConfig

/**
 * TryCatchFormatJavaRule: Enforces formatting standards for try-catch statements in Java source files.
 *
 * This rule checks that the formatting of try-catch statements adheres to a specific pattern.
 * A valid try-catch statement must follow the format:
 *
 * ```java
 * try {
 *     // code that may throw an exception
 * } catch (ExceptionType e) {
 *     // exception handling code
 * }
 * ```
 *
 * Configuration:
 * - `enabled`: Whether this rule is active (default: true)
 *
 * Examples of compliant try-catch statements:
 * ```java
 * try {
 *     int result = riskyOperation();
 * } catch (Exception e) {
 *     handleException(e);
 * }
 * ```
 *
 * Examples of non-compliant try-catch statements:
 * ```java
 * try{ // Violation: Missing space after "try"
 *     int result = riskyOperation();
 * } catch(Exception e) { // Violation: Missing space after "catch" and around parentheses
 *     handleException(e);
 * }
 * ```
 */
class TryCatchFormatJavaRule(config: TryCatchFormatRuleConfig = TryCatchFormatRuleConfig()) :
    JavaRule<TryCatchFormatRuleConfig>(config) {

    private val singleIfPattern = "try \\{(.|\\n)*\\} catch (.*) \\{(.|\\n)*\\}".toRegex()

    override fun visit(tryStmt: TryStmt, arg: MutableList<Violation>) {
        val file = tryStmt.findCompilationUnit().get().storage.get().path
        val start = tryStmt.begin.get()
        val end = tryStmt.end.get()

        val sourceText = file.getSourceText(start, end)

        if (!sourceText.matches(singleIfPattern)) {
            arg.add(Violation(Violation.Location(file.toFile(), start.line), "Invalid format for try catch statement"))
        }
        super.visit(tryStmt, arg)
    }
}
