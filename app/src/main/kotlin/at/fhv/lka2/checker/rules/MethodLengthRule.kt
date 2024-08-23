package at.fhv.lka2.checker.rules

import at.fhv.lka2.checker.config.RuleConfig
import at.fhv.lka2.checker.model.JavaRule
import at.fhv.lka2.checker.model.Violation
import at.fhv.lka2.checker.rules.MethodLengthRule.MethodLengthRuleConfig
import com.github.javaparser.ast.body.MethodDeclaration
import java.io.File

/**
 * MethodLengthRule: Enforces a maximum length for method bodies.
 *
 * This rule checks if method bodies exceed a specified number of statements.
 * By default, it flags methods with more than 50 statements as too long.
 *
 * The rule aims to encourage:
 * - Improved readability by keeping methods concise
 * - Better code organization through method decomposition
 * - Easier testing and maintenance of smaller, focused methods
 *
 * Configuration:
 * - maxLength: The maximum number of statements allowed in a method (default: 50)
 *
 * Example of a compliant method:
 * ```
 * public void processUser(User user) {
 *     validateUser(user);
 *     updateUserDetails(user);
 *     notifyUser(user);
 *     // ... (up to 50 statements in total)
 * }
 * ```
 *
 * Example of a non-compliant method:
 * ```
 * public void processLargeDataSet(List<Data> dataSet) {
 *     // Method body with more than 50 statements
 *     for (Data data : dataSet) {
 *         // ... (lengthy processing logic)
 *     }
 *     // ... (additional logic)
 *     // Violation: This method is too long (e.g., 60 statements)
 * }
 * ```
 *
 * Refactoring tip for violations:
 * Consider breaking down long methods into smaller, more focused methods.
 * For example:
 * ```
 * public void processLargeDataSet(List<Data> dataSet) {
 *     for (Data data : dataSet) {
 *         processIndividualData(data);
 *     }
 *     summarizeResults();
 * }
 *
 * private void processIndividualData(Data data) {
 *     // ... (processing logic for a single data item)
 * }
 *
 * private void summarizeResults() {
 *     // ... (logic for summarizing the processed data)
 * }
 * ```
 *
 * @property config The [MethodLengthRuleConfig] for this rule
 */
class MethodLengthRule(config: MethodLengthRuleConfig = MethodLengthRuleConfig()) : JavaRule<MethodLengthRuleConfig>(config) {

    /**
     * Configuration for the [MethodLengthRuleConfig].
     *
     * @property enabled Whether this rule is active (default: true)
     * @property max The maximum statements a function can have (default: 50)
     */
    data class MethodLengthRuleConfig(
        override val enabled: Boolean = true,
        val maxLength: Int = 50
    ) : RuleConfig

    override fun visit(n: MethodDeclaration, violations: MutableList<Violation>) {
        if (n.body.isPresent && n.body.get().statements.size > 50) {
            violations.add(
                Violation(
                    Violation.Location(File(n.findCompilationUnit().get().storage.get().fileName), n.begin.get().line),
                    "Method '${n.nameAsString}' is too long (${n.body.get().statements.size} lines)"
                )
            )
        }
        super.visit(n, violations)
    }
}
