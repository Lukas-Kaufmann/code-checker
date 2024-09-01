package at.fhv.lka2.checker.rules

import at.fhv.lka2.checker.model.JavaRule
import at.fhv.lka2.checker.config.RuleConfig
import at.fhv.lka2.checker.model.Violation
import at.fhv.lka2.checker.rules.FieldPatternJavaRule.FieldPatternRuleConfig
import com.github.javaparser.ast.body.FieldDeclaration
import java.io.File

/**
 * FieldPatternRule: Enforces a specific naming pattern for class fields.
 *
 * This rule checks if class field names conform to a specified regular expression pattern.
 * By default, it enforces the pattern "^[a-z][a-zA-Z0-9]*$", which means:
 * - The field name must start with a lowercase letter
 * - It can be followed by any number of letters (uppercase or lowercase) or digits
 *
 * Examples of compliant field names:
 * - userName
 * - age
 * - firstName123
 * - x
 *
 * Examples of non-compliant field names:
 * - UserName (starts with uppercase)
 * - _age (starts with underscore)
 * - first_name (contains underscore)
 * - 123field (starts with a number)
 *
 * Example of compliant code:
 * ```java
 * class User {
 *     private String userName;
 *     private int age;
 *     protected List<String> hobbies;
 * }
 * ```
 *
 * Example of code with violations:
 * ```java
 * class User {
 *     private String UserName; // Violation: Starts with uppercase
 *     private int _age; // Violation: Starts with underscore
 *     protected List<String> user_hobbies; // Violation: Contains underscore
 * }
 * ```
 *
 * @property config The [FieldPatternRuleConfig] for this rule.
 */
class FieldPatternJavaRule(config: FieldPatternRuleConfig = FieldPatternRuleConfig()) : JavaRule<FieldPatternRuleConfig>(config) {

    /**
     * Configuration for the [FieldPatternJavaRule].
     *
     * @property enabled Whether this rule is active (default: true)
     * @property pattern The regex pattern to match field names against (default: "^[a-z][a-zA-Z0-9]*$")
     */
    data class FieldPatternRuleConfig(
        override val enabled: Boolean = true,
        val pattern: String = "^[a-z][a-zA-Z0-9]*$"
    ) : RuleConfig

    override fun visit(field: FieldDeclaration, violations: MutableList<Violation>) {
        field.variables.forEach { variable ->
            val name = variable.nameAsString
            if (!name.matches(config.pattern.toRegex())) {
                violations.add(
                    Violation(
                        Violation.Location(
                            File(field.findCompilationUnit().get().storage.get().fileName),
                            field.begin.get().line
                        ),
                        "Class variable '$name' does not follow Pattern: ${config.pattern}"
                    )
                )
            }
        }

        super.visit(field, violations)
    }
}