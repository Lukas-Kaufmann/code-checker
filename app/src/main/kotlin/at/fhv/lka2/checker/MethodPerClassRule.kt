package at.fhv.lka2.checker

import at.fhv.lka2.checker.config.RuleConfig
import at.fhv.lka2.checker.model.JavaRule
import at.fhv.lka2.checker.model.Violation
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import java.io.File

data class MethodPerClassRuleConfig(override val enabled: Boolean = true, val maximumMethods: Int = 20) : RuleConfig


class MethodPerClassJavaRule(config: MethodPerClassRuleConfig = MethodPerClassRuleConfig()) :
    JavaRule<MethodPerClassRuleConfig>(config) {


    override fun visit(clazz: ClassOrInterfaceDeclaration, violations: MutableList<Violation>) {


        if (clazz.methods.size > config.maximumMethods) {

            violations.add(
                Violation(
                    Violation.Location(
                        File(clazz.findCompilationUnit().get().storage.get().fileName),
                        clazz.begin.get().line
                    ),
                    "Class ${clazz.nameAsString} has ${clazz.methods.size} methods. More than the maximum of ${config.maximumMethods}"
                )
            )

        }

        super.visit(clazz, violations)
    }

}
