package at.fhv.lka2.checker.rules

import at.fhv.lka2.checker.model.Rule
import at.fhv.lka2.checker.config.RuleConfig
import at.fhv.lka2.checker.model.Violation
import at.fhv.lka2.checker.rules.FieldPatternRule.FieldPatternRuleConfig
import com.github.javaparser.ast.body.FieldDeclaration
import java.io.File

class FieldPatternRule(config: FieldPatternRuleConfig = FieldPatternRuleConfig()) : Rule<FieldPatternRuleConfig>(config) {

    data class FieldPatternRuleConfig(
        override val enabled: Boolean = true,
        val pattern: String = "^[a-z][a-zA-Z0-9]*$"
    ) : RuleConfig

    override fun visit(field: FieldDeclaration, violations: MutableList<Violation>) {
        field.variables.forEach { variable ->
            val name = variable.nameAsString
            if (!name.matches(config.pattern.toRegex())) {
                violations.add(
                    Violation(
                        Violation.Location(
                            File(field.findCompilationUnit().get().storage.get().fileName),
                            field.begin.get().line
                        ),
                        "Class variable '$name' does not follow Pattern: ${config.pattern}"
                    )
                )
            }
        }

        super.visit(field, violations)
    }
}