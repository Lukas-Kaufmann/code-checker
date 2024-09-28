package at.fhv.lka2.checker.rules

import at.fhv.lka2.checker.config.RuleConfig
import at.fhv.lka2.checker.model.CRule
import at.fhv.lka2.checker.model.JavaRule
import at.fhv.lka2.checker.model.Violation
import java.io.File


data class MaxLineLengthConfig(override val enabled: Boolean = true, val maxLineLength: Int = 80) : RuleConfig

class MaxLineLengthJavaRule(config: MaxLineLengthConfig = MaxLineLengthConfig()) :
    JavaRule<MaxLineLengthConfig>(config) {

    override fun check(file: File): MutableList<Violation> {
        val lines = file.readLines()
        return lines.mapIndexedNotNull { lineNumber, line ->
            if (line.length > config.maxLineLength) {
                Violation(
                    Violation.Location(file, lineNumber),
                    "File '${file.absolutePath} has a line too long at line $lineNumber.",
                )
            } else {
                null
            }
        }.toMutableList()
    }
}


class MaxLineLengthCRule(config: MaxLineLengthConfig = MaxLineLengthConfig()) :
    CRule<MaxLineLengthConfig>(config) {

    override fun check(file: File): MutableList<Violation> {
        val lines = file.readLines()
        return lines.mapIndexedNotNull { lineNumber, line ->
            if (line.length > config.maxLineLength) {
                Violation(
                    Violation.Location(file, lineNumber),
                    "File '${file.absolutePath} has a line too long at line $lineNumber.",
                )
            } else {
                null
            }
        }.toMutableList()
    }
}
