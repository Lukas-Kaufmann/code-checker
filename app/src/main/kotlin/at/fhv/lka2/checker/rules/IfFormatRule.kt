package at.fhv.lka2.checker.rules

import at.fhv.lka2.checker.config.RuleConfig
import at.fhv.lka2.checker.getSourceText
import at.fhv.lka2.checker.model.CRule
import at.fhv.lka2.checker.model.JavaRule
import at.fhv.lka2.checker.model.Violation
import com.github.javaparser.ast.stmt.IfStmt
import org.eclipse.cdt.core.dom.ast.IASTStatement
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTIfStatement
import java.io.File

data class IfFormatRuleConfig(override val enabled: Boolean = true) : RuleConfig

private val singleIfPattern = "if \\(\\S.*\\S\\) \\{(.|\\n)*\\}".toRegex()

private val bracketedPattern = "\\{(.|\\n)*\\}".toRegex()

/**
 * IfFormatJavaRule: Enforces a specific format for `if` statements in Java code.
 *
 * This rule checks whether `if` statements in Java code follow a predefined format. It ensures that:
 * - `if` statements follow the pattern `if \\(\\S.*\\S\\) \\{(.|\\n)*\\}` to ensure correct parentheses and curly braces
 * - `else` blocks are enclosed within curly braces, even for single-line statements
 *
 * The purpose of this rule is to:
 * - Promote readability and maintainability by enforcing consistent formatting for `if` and `else` blocks
 * - Prevent error-prone `if` statements, especially when braces are omitted
 *
 * Configuration:
 * - No configurable properties beyond enabling or disabling the rule (default: enabled)
 *
 * Example of a compliant `if` statement:
 * ```java
 * if (condition) {
 *     // body
 * } else {
 *     // else body
 * }
 * ```
 *
 * Example of non-compliant `if` statements:
 * ```java
 * if(condition) { // Violation: Missing spaces inside parentheses
 *     // body
 * }
 * if (condition) statement; // Violation: No curly braces for single statement
 * ```
 *
 * Refactoring tip for violations:
 * Ensure that all `if` and `else` blocks are properly formatted with correct spacing, parentheses, and braces.
 */
class IfFormatJavaRule(config: IfFormatRuleConfig = IfFormatRuleConfig()) : JavaRule<IfFormatRuleConfig>(config) {

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

/**
 * IfFormatCRule: Enforces a specific format for `if` statements in C/C++ code.
 *
 * This rule checks whether `if` statements in C/C++ code conform to a predefined format. It ensures that:
 * - `if` statements follow the pattern `if \\(\\S.*\\S\\) \\{(.|\\n)*\\}` to verify proper formatting and use of curly braces
 * - `else` clauses, if present, are enclosed in braces, even for single-line `else` statements
 *
 * The purpose of this rule is to:
 * - Ensure that `if` and `else` blocks are formatted consistently for improved readability
 * - Reduce the risk of errors by enforcing the use of braces for all `if` and `else` statements
 *
 * Configuration:
 * - No configurable properties beyond enabling or disabling the rule (default: enabled)
 *
 * Example of a compliant `if` statement:
 * ```c
 * if (condition) {
 *     // body
 * } else {
 *     // else body
 * }
 * ```
 *
 * Example of non-compliant `if` statements:
 * ```c
 * if(condition) { // Violation: Missing spaces inside parentheses
 *     // body
 * }
 * if (condition) statement; // Violation: No curly braces for single statement
 * ```
 *
 * Refactoring tip for violations:
 * Ensure that all `if` and `else` blocks are properly formatted with the correct spacing, parentheses, and braces.
 */
class IfFormatCRule(config: IfFormatRuleConfig = IfFormatRuleConfig()) : CRule<IfFormatRuleConfig>(config) {
    override fun visit(statement: IASTStatement): Int {
        if (statement is CPPASTIfStatement) {
            val sourceText = statement.getSourceText()
            if (!sourceText.matches(singleIfPattern)) {
                addViolation(
                    Violation(
                        Violation.Location(
                            File(statement.fileLocation.fileName),
                            statement.fileLocation.startingLineNumber
                        ), "Invalid format for if pattern"
                    )
                )
            }

            statement.elseClause?.let {
                val elseSourceText = it.getSourceText()
                if (!elseSourceText.matches(bracketedPattern)) {
                    addViolation(Violation(
                        Violation.Location(
                            File(statement.fileLocation.fileName),
                            statement.fileLocation.startingLineNumber
                        ), "Invalid format for else clause in if statement"
                    ))
                }
            }
        }

        return super.visit(statement)
    }
}