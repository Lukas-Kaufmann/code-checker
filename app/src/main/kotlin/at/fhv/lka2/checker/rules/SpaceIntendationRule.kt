package at.fhv.lka2.checker.rules

import at.fhv.lka2.checker.config.RuleConfig
import at.fhv.lka2.checker.model.CRule
import at.fhv.lka2.checker.model.JavaRule
import at.fhv.lka2.checker.model.Violation
import java.io.File

data class SpaceIndentationRuleConfig(override val enabled: Boolean = true) : RuleConfig

class SpaceIndentationJavaRule(config: SpaceIndentationRuleConfig = SpaceIndentationRuleConfig()) :
    JavaRule<SpaceIndentationRuleConfig>(config) {

    //lines should start with
    override fun check(file: File): MutableList<Violation> {
        val lines = file.readLines()

        return lines.mapIndexedNotNull { lineNumber, line ->
            val leadingSpaces = line.takeWhile { it == ' ' }.length
            if (!line.startsWith('\t') && leadingSpaces % 4 != 0) {
                Violation(
                    Violation.Location(file, lineNumber),
                    "Not 4 spaces of indentation used at '${file.absolutePath} line $lineNumber.",
                )
            } else {
                null
            }
        }.toMutableList()
    }
}

class SpaceIndentationCRule(config: SpaceIndentationRuleConfig = SpaceIndentationRuleConfig()) :
    CRule<SpaceIndentationRuleConfig>(config) {

    //lines should start with
    override fun check(file: File): MutableList<Violation> {
        val lines = file.readLines()

        return lines.mapIndexedNotNull { lineNumber, line ->
            val leadingSpaces = line.takeWhile { it == ' ' }.length
            if (!line.startsWith('\t') && leadingSpaces % 4 != 0) {
                Violation(
                    Violation.Location(file, lineNumber),
                    "Not 4 spaces of indentation used at '${file.absolutePath} line $lineNumber.",
                )
            } else {
                null
            }
        }.toMutableList()
    }
}
