package at.fhv.lka2.checker.rules

import at.fhv.lka2.checker.config.RuleConfig
import at.fhv.lka2.checker.model.CRule
import at.fhv.lka2.checker.model.JavaRule
import at.fhv.lka2.checker.model.Violation
import java.io.File

data class MaxLinesRuleConfig(
    override val enabled: Boolean = true,
    val maxLines: Int = 2000,
) : RuleConfig

/**
 * MaxLinesJavaRule: Enforces a maximum number of lines in a Java source file.
 *
 * This rule checks whether a Java source file exceeds a configurable maximum number of lines. The purpose of this rule is to promote smaller, more manageable files that adhere to the principle of separation of concerns. Large files can make code harder to navigate and maintain. By default, the rule flags files with more than 2000 lines.
 *
 * Configuration:
 * - `maxLines`: The maximum number of allowed lines in a file (default: 2000)
 *
 * Example of compliant code:
 * A Java file with fewer than or exactly 2000 lines.
 *
 * Example of non-compliant code:
 * A Java file with more than 2000 lines.
 *
 * Refactoring tip for violations:
 * Break down large files into smaller ones by separating concerns, extracting classes, or modularizing code.
 */
class MaxLinesJavaRule(config: MaxLinesRuleConfig = MaxLinesRuleConfig()) : JavaRule<MaxLinesRuleConfig>(config) {

    override fun check(file: File): MutableList<Violation> {
        val lines = file.readLines().size
        return if (lines > config.maxLines) {
            mutableListOf(
                Violation(
                    Violation.Location(file, 0),
                    "File '${file.absolutePath} has more than the maximum amount of lines. ($lines instead of max ${config.maxLines}). Keep files small so that concerns are separated.",
                )
            )
        } else {
            mutableListOf()
        }
    }
}

/**
 * MaxLinesCRule: Enforces a maximum number of lines in a C/C++ source file.
 *
 * This rule checks whether a C/C++ source file exceeds a configurable maximum number of lines. The rule is designed to encourage developers to break down large files into smaller, more modular components, promoting maintainability and separation of concerns. By default, files with more than 2000 lines are flagged.
 *
 * Configuration:
 * - `maxLines`: The maximum number of allowed lines in a file (default: 2000)
 *
 * Example of compliant code:
 * A C/C++ source file with fewer than or exactly 2000 lines.
 *
 * Example of non-compliant code:
 * A C/C++ source file with more than 2000 lines.
 *
 * Refactoring tip for violations:
 * Split large files into smaller ones by creating separate modules, classes, or functions that encapsulate related functionality.
 */
class MaxLinesCRule(config: MaxLinesRuleConfig = MaxLinesRuleConfig()) : CRule<MaxLinesRuleConfig>(config) {

    override fun check(file: File): MutableList<Violation> {
        val lines = file.readLines().size
        return if (lines > config.maxLines) {
            mutableListOf(
                Violation(
                    Violation.Location(file, 0),
                    "File '${file.absolutePath} has more than the maximum amount of lines. ($lines instead of max ${config.maxLines}). Keep files small so that concerns are separated.",
                )
            )
        } else {
            mutableListOf()
        }
    }
}
