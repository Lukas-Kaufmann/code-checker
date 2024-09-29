package at.fhv.lka2.checker.rules

import at.fhv.lka2.checker.config.RuleConfig
import at.fhv.lka2.checker.model.JavaRule
import at.fhv.lka2.checker.model.Violation
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import java.io.File

data class ClassNameRuleConfig(
    override val enabled: Boolean = true,
    val pattern: Regex = "[A-Z][a-zA-Z]*".toRegex()
) : RuleConfig

/**
 * ClassNameJavaRule: Enforces class naming conventions based on a configurable pattern.
 *
 * This rule checks whether the class or interface names adhere to a specified naming convention.
 * By default, it expects class names to follow the "CamelCase" convention starting with an uppercase letter.
 *
 * The purpose of this rule is to:
 * - Encourage consistent naming conventions across the codebase
 * - Improve readability and maintainability by adhering to a standard naming format
 *
 * Configuration:
 * - pattern: The naming pattern for class names (default: starts with an uppercase letter and is followed by any combination of letters)
 *
 * Example of a compliant class name:
 * ```
 * public class UserAccount { ... }
 * ```
 *
 * Example of a non-compliant class name:
 * ```
 * public class user_account { ... }
 * // Violation: Class name does not follow the specified pattern
 * ```
 *
 * Refactoring tip for violations:
 * Ensure that class names begin with an uppercase letter and follow the defined pattern. Rename classes that violate this convention.
 * For example:
 * ```
 * public class user_account { ... }
 * // Refactor to:
 * public class UserAccount { ... }
 * ```
 */
class ClassNameJavaRule(config: ClassNameRuleConfig = ClassNameRuleConfig()) :
    JavaRule<ClassNameRuleConfig>(config) {

    override fun visit(`class`: ClassOrInterfaceDeclaration, arg: MutableList<Violation>) {
        if (!`class`.nameAsString.matches(config.pattern)) {
            arg.add(
                Violation(
                    Violation.Location(
                        File(`class`.findCompilationUnit().get().storage.get().fileName),
                        `class`.begin.get().line
                    ),
                    "Class name '${`class`.nameAsString}' does not follow Pattern: ${config.pattern}"
                )
            )
        }
        super.visit(`class`, arg)
    }
}