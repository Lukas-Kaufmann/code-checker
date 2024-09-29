package at.fhv.lka2.checker.rules

import at.fhv.lka2.checker.config.RuleConfig
import at.fhv.lka2.checker.model.JavaRule
import at.fhv.lka2.checker.model.Violation
import com.github.javaparser.ast.expr.VariableDeclarationExpr
import java.io.File

data class VariableNameJavaRuleConfig(
    override val enabled: Boolean = true,
    val pattern: Regex = "[a-z][a-zA-Z0-9]*".toRegex()
) : RuleConfig

class VariableNameJavaRule(config: VariableNameJavaRuleConfig = VariableNameJavaRuleConfig()) :
    JavaRule<VariableNameJavaRuleConfig>(config) {

    override fun visit(variables: VariableDeclarationExpr, arg: MutableList<Violation>) {
        variables.variables.forEach { variable ->

            if (!variable.nameAsString.matches(config.pattern)) {
                arg.add(
                    Violation(
                        Violation.Location(
                            File(variable.findCompilationUnit().get().storage.get().fileName),
                            variable.begin.get().line
                        ),
                        "Variable name '${variable.nameAsString}' does not follow Pattern: ${config.pattern}"
                    )
                )
            }
        }
        super.visit(variables, arg)
    }
}
