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