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
