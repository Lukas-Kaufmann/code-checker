package at.fhv.lka2.checker.rules

import at.fhv.lka2.checker.config.RuleConfig
import at.fhv.lka2.checker.model.JavaRule
import at.fhv.lka2.checker.model.Violation
import com.github.javaparser.ast.NodeList
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.VariableDeclarator
import com.github.javaparser.ast.expr.VariableDeclarationExpr
import java.io.File

data class ConstNameJavaRuleConfig(
    override val enabled: Boolean = true,
    val pattern: Regex = "[A-Z][A-Z_]*".toRegex()
) : RuleConfig

/**
 * ConstNameJavaRule: Enforces naming conventions for constant variables.
 *
 * This rule checks whether the names of `final` fields and variables follow a specified pattern, typically used for constants.
 * By default, it expects constant names to be in uppercase with words separated by underscores, such as `MY_CONSTANT`.
 *
 * The purpose of this rule is to:
 * - Encourage consistent naming of constants in code
 * - Improve readability by distinguishing constants from other variables
 * - Adhere to common coding standards across the codebase
 *
 * Configuration:
 * - pattern: The naming pattern for constant names (default: uppercase letters with optional underscores)
 *
 * Example of a compliant constant name:
 * ```
 * public static final int MAX_LIMIT = 100;
 * ```
 *
 * Example of a non-compliant constant name:
 * ```
 * public static final int maxLimit = 100;
 * // Violation: Constant name does not follow the pattern
 * ```
 *
 * Refactoring tip for violations:
 * Ensure that constant names are in uppercase with optional underscores to separate words. Rename variables that violate this convention.
 * For example:
 * ```
 * public static final int maxLimit = 100;
 * // Refactor to:
 * public static final int MAX_LIMIT = 100;
 * ```
 */
class ConstNameJavaRule(config: ConstNameJavaRuleConfig = ConstNameJavaRuleConfig()) :
    JavaRule<ConstNameJavaRuleConfig>(config) {

    override fun visit(field: FieldDeclaration, arg: MutableList<Violation>) {
        if (field.isFinal) {
            checkVariables(field.variables, arg)
        }

        super.visit(field, arg)
    }

    private fun checkVariables(variables: NodeList<VariableDeclarator>, violations: MutableList<Violation>) {
        variables.forEach { variable ->
            if (!variable.nameAsString.matches(config.pattern)) {
                violations.add(
                    Violation(
                        Violation.Location(
                            File(variable.findCompilationUnit().get().storage.get().fileName),
                            variable.begin.get().line
                        ),
                        "constant name '${variable.nameAsString}' does not follow Pattern: ${config.pattern}"
                    )
                )
            }
        }
    }

    override fun visit(variables: VariableDeclarationExpr, arg: MutableList<Violation>) {
        if (variables.isFinal) {
            checkVariables(variables.variables, arg)
        }
        super.visit(variables, arg)
    }
}
