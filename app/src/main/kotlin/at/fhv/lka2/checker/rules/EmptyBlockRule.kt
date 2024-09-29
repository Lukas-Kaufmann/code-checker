package at.fhv.lka2.checker.rules

import at.fhv.lka2.checker.config.RuleConfig
import at.fhv.lka2.checker.model.JavaRule
import at.fhv.lka2.checker.model.Violation
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import java.io.File

data class EmptyBlockRuleConfig(
    override val enabled: Boolean = true,
) : RuleConfig

private val emptyBracketed = "\\{\\s*\\}".toRegex()

/**
 * EmptyBlockJavaRule: Detects and flags empty code blocks.
 *
 * This rule scans the code for empty blocks (i.e., `{}`), which typically indicate incomplete or unnecessary code.
 * Empty blocks can reduce code clarity and suggest unfinished implementation. This rule helps identify and remove
 * such blocks to maintain clean and concise code.
 *
 * The purpose of this rule is to:
 * - Prevent the use of empty blocks that might confuse other developers or suggest incomplete functionality
 * - Encourage the removal or proper handling of empty code blocks
 *
 * Configuration:
 * - No configurable properties beyond enabling or disabling the rule (default: enabled)
 *
 * Example of a violation:
 * ```
 * public class User {
 *     public void process() {
 *         // Empty block
 *     }
 * }
 * // Violation: Empty block '{}' detected.
 * ```
 *
 * Refactoring tip for violations:
 * If the block is not needed, consider removing it entirely. If it is part of incomplete functionality,
 * ensure to add the required implementation or comments indicating its purpose.
 */
class EmptyBlockJavaRule(config: EmptyBlockRuleConfig = EmptyBlockRuleConfig()) :
    JavaRule<EmptyBlockRuleConfig>(config) {

    override fun check(file: File): MutableList<Violation> {
        val fileText = file.readText()

        val mutableList = mutableListOf<Violation>()

        if (!fileText.matches(emptyBracketed)) {
            mutableList.add(
                Violation(
                    Violation.Location(
                        file,
                        0
                    ),
                    "Empty block '{}' detected."
                )
            )
        }
        return mutableList
    }
}
