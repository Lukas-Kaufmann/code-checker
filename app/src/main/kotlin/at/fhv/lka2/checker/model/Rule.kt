package at.fhv.lka2.checker.model

import at.fhv.lka2.checker.config.RuleConfig
import java.io.File

interface Rule<T : RuleConfig> {
    val config: T

    fun isEnabled(): Boolean = config.enabled
    fun check(file: File): MutableList<Violation>
}
