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

/**
 * SwitchFormatJavaRule: Enforces formatting standards for switch statements in Java source files.
 *
 * This rule checks that the formatting of switch statements adheres to a specific pattern.
 * A valid switch statement must follow the format:
 *
 * ```java
 * switch (expression) {
 *     // case statements
 * }
 * ```
 *
 * Configuration:
 * - `enabled`: Whether this rule is active (default: true)
 *
 * Examples of compliant switch statements:
 * ```java
 * switch (value) {
 *     case 1:
 *         System.out.println("One");
 *         break;
 *     case 2:
 *         System.out.println("Two");
 *         break;
 * }
 * ```
 *
 * Examples of non-compliant switch statements:
 * ```java
 * switch(value) { // Violation: Missing space after "switch" and around parentheses
 *     case 1:
 *         System.out.println("One");
 * }
 * ```
 */
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

/**
 * SwitchFormatCRule: Enforces formatting standards for switch statements in C source files.
 *
 * This rule checks that the formatting of switch statements adheres to a specific pattern.
 * A valid switch statement must follow the format:
 *
 * ```c
 * switch (expression) {
 *     // case statements
 * }
 * ```
 *
 * Configuration:
 * - `enabled`: Whether this rule is active (default: true)
 *
 * Examples of compliant switch statements:
 * ```c
 * switch (value) {
 *     case 1:
 *         printf("One\n");
 *         break;
 *     case 2:
 *         printf("Two\n");
 *         break;
 * }
 * ```
 *
 * Examples of non-compliant switch statements:
 * ```c
 * switch(value) { // Violation: Missing space after "switch" and around parentheses
 *     case 1:
 *         printf("One\n");
 * }
 * ```
 */
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