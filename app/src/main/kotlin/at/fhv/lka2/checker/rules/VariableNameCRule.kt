package at.fhv.lka2.checker.rules

import at.fhv.lka2.checker.config.RuleConfig
import at.fhv.lka2.checker.model.CRule
import at.fhv.lka2.checker.model.Violation
import org.eclipse.cdt.core.dom.ast.*
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTDeclarator
import java.io.File

/**
 * VariableNameCRule: Enforces variable naming conventions in C/C++ source files.
 *
 * This rule checks that the names of variables conform to a specified naming pattern.
 * By default, the expected pattern is that variable names should start with a lowercase letter
 * followed by any combination of letters and digits.
 *
 * Configuration:
 * - `enabled`: Boolean flag indicating whether this rule is active (default: true).
 * - `pattern`: A regular expression string defining the valid naming pattern for variables
 *   (default: "^[a-z][a-zA-Z0-9]*$").
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
class VariableNameCRule(config: VariableNameRuleConfig = VariableNameRuleConfig()) :
    CRule<VariableNameCRule.VariableNameRuleConfig>(config) {

    /**
     * Configuration for the [VariableNameRuleConfig].
     *
     * @property enabled Whether this rule is active (default: true)
     * @property max The maximum statements a function can have (default: 50)
     */
    data class VariableNameRuleConfig(
        override val enabled: Boolean = true,
        val pattern: String = "^[a-z][a-zA-Z0-9]*$"
    ) : RuleConfig

    override fun visit(declarator: IASTDeclarator): Int {
        if (declarator is CPPASTDeclarator) {

            if (!declarator.name.toString().matches(config.pattern.toRegex())) {
                addViolation(
                    Violation(
                        Violation.Location(
                            File(declarator.fileLocation.fileName),
                            declarator.fileLocation.startingLineNumber
                        ),
                        "Variable '${declarator.name}' does not follow Pattern: ${config.pattern}"
                    )
                )
            }
        }

        return super.visit(declarator)
    }
}
