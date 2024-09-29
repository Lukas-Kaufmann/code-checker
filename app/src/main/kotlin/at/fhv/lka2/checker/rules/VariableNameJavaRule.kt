package at.fhv.lka2.checker.rules

import at.fhv.lka2.checker.config.RuleConfig
import at.fhv.lka2.checker.model.JavaRule
import at.fhv.lka2.checker.model.Violation
import com.github.javaparser.ast.expr.VariableDeclarationExpr
import java.io.File

data class VariableNameJavaRuleConfig(
    override val enabled: Boolean = true,
    val pattern: Regex = "[a-z][a-zA-Z0-9]*".toRegex()
) : RuleConfig

/**
 * VariableNameJavaRule: Enforces variable naming conventions in Java source files.
 *
 * This rule checks that the names of variables conform to a specified naming pattern.
 * By default, the expected pattern is that variable names should start with a lowercase letter
 * followed by any combination of letters and digits.
 *
 * Configuration:
 * - `enabled`: Boolean flag indicating whether this rule is active (default: true).
 * - `pattern`: A regular expression defining the valid naming pattern for variables
 *   (default: "[a-z][a-zA-Z0-9]*").
 *
 * Examples of compliant variable names:
 * - `myVariable`
 * - `count1`
 * - `dataValue`
 *
 * Examples of non-compliant variable names:
 * - `MyVariable` // Violation: Starts with an uppercase letter
 * - `1stVariable` // Violation: Starts with a digit
 * - `data-value` // Violation: Contains an invalid character '-'
 */
class VariableNameJavaRule(config: VariableNameJavaRuleConfig = VariableNameJavaRuleConfig()) :
    JavaRule<VariableNameJavaRuleConfig>(config) {

    override fun visit(variables: VariableDeclarationExpr, arg: MutableList<Violation>) {
        variables.variables.forEach { variable ->

            if (!variable.nameAsString.matches(config.pattern)) {
                arg.add(
                    Violation(
                        Violation.Location(
                            File(variable.findCompilationUnit().get().storage.get().fileName),
                            variable.begin.get().line
                        ),
                        "Variable name '${variable.nameAsString}' does not follow Pattern: ${config.pattern}"
                    )
                )
            }
        }
        super.visit(variables, arg)
    }
}
