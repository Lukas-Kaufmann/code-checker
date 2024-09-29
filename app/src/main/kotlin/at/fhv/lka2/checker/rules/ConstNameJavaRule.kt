package at.fhv.lka2.checker.rules

import at.fhv.lka2.checker.config.RuleConfig
import at.fhv.lka2.checker.model.JavaRule
import at.fhv.lka2.checker.model.Violation
import com.github.javaparser.ast.NodeList
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.VariableDeclarator
import com.github.javaparser.ast.expr.VariableDeclarationExpr
import java.io.File

data class ConstNameJavaRuleConfig(
    override val enabled: Boolean = true,
    val pattern: Regex = "[A-Z][A-Z_]*".toRegex()
) : RuleConfig

class ConstNameJavaRule(config: ConstNameJavaRuleConfig = ConstNameJavaRuleConfig()) :
    JavaRule<ConstNameJavaRuleConfig>(config) {

    override fun visit(field: FieldDeclaration, arg: MutableList<Violation>) {
        if (field.isFinal) {
            checkVariables(field.variables, arg)
        }

        super.visit(field, arg)
    }

    private fun checkVariables(variables: NodeList<VariableDeclarator>, violations: MutableList<Violation>) {
        variables.forEach { variable ->
            if (!variable.nameAsString.matches(config.pattern)) {
                violations.add(
                    Violation(
                        Violation.Location(
                            File(variable.findCompilationUnit().get().storage.get().fileName),
                            variable.begin.get().line
                        ),
                        "constant name '${variable.nameAsString}' does not follow Pattern: ${config.pattern}"
                    )
                )
            }
        }
    }

    override fun visit(variables: VariableDeclarationExpr, arg: MutableList<Violation>) {
        if (variables.isFinal) {
            checkVariables(variables.variables, arg)
        }
        super.visit(variables, arg)
    }
}
