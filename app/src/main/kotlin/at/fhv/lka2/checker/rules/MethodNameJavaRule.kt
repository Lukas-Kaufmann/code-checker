package at.fhv.lka2.checker.rules

import at.fhv.lka2.checker.config.RuleConfig
import at.fhv.lka2.checker.model.JavaRule
import at.fhv.lka2.checker.model.Violation
import com.github.javaparser.ast.body.MethodDeclaration
import java.io.File

data class MethodNameJavaRuleConfig(
    override val enabled: Boolean = true,
    val pattern: Regex = "[a-z][a-zA-Z]*".toRegex()
) : RuleConfig

class MethodNameJavaRule(config: MethodNameJavaRuleConfig = MethodNameJavaRuleConfig()) :
    JavaRule<MethodNameJavaRuleConfig>(config) {

    override fun visit(method: MethodDeclaration, arg: MutableList<Violation>) {
        if (!method.nameAsString.matches(config.pattern)) {
            arg.add(
                Violation(
                    Violation.Location(
                        File(method.findCompilationUnit().get().storage.get().fileName),
                        method.begin.get().line
                    ),
                    "Method name '${method.nameAsString}' does not follow Pattern: ${config.pattern}"
                )
            )
        }
        super.visit(method, arg)
    }
}
