package at.fhv.lka2.checker.rules

import at.fhv.lka2.checker.config.RuleConfig
import at.fhv.lka2.checker.model.JavaRule
import at.fhv.lka2.checker.model.Violation
import at.fhv.lka2.checker.rules.MethodLengthRule.MethodLengthRuleConfig
import com.github.javaparser.ast.body.MethodDeclaration
import java.io.File

class MethodLengthRule(config: MethodLengthRuleConfig = MethodLengthRuleConfig()) : JavaRule<MethodLengthRuleConfig>(config) {

    data class MethodLengthRuleConfig(
        override val enabled: Boolean = true,
        val maxLength: Int = 50
    ) : RuleConfig

    override fun visit(n: MethodDeclaration, violations: MutableList<Violation>) {
        if (n.body.isPresent && n.body.get().statements.size > 50) {
            violations.add(
                Violation(
                    Violation.Location(File(n.findCompilationUnit().get().storage.get().fileName), n.begin.get().line),
                    "Method '${n.nameAsString}' is too long (${n.body.get().statements.size} lines)"
                )
            )
        }
        super.visit(n, violations)
    }
}
