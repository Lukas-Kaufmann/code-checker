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
