package at.fhv.lka2.checker.rules

import at.fhv.lka2.checker.config.RuleConfig
import at.fhv.lka2.checker.getSourceText
import at.fhv.lka2.checker.model.CRule
import at.fhv.lka2.checker.model.JavaRule
import at.fhv.lka2.checker.model.Violation
import com.github.javaparser.ast.body.MethodDeclaration
import org.eclipse.cdt.core.dom.ast.IASTDeclaration
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionDefinition
import java.io.File

data class LongParameterListRuleConfig(override val enabled: Boolean = true, val maximumParameters: Int = 11) :
    RuleConfig

class LongParameterListJavaRule(config: LongParameterListRuleConfig = LongParameterListRuleConfig()) :
    JavaRule<LongParameterListRuleConfig>(config) {

    override fun visit(method: MethodDeclaration, violations: MutableList<Violation>) {

        val parameterCount = method.parameters.size
        if (parameterCount > config.maximumParameters) {
            violations.add(
                Violation(
                    Violation.Location(
                        File(method.findCompilationUnit().get().storage.get().fileName),
                        method.begin.get().line
                    ),
                    "Method '${method.nameAsString}' has $parameterCount parameters. More than the allowed maximum of ${config.maximumParameters}"
                )
            )
        }

        super.visit(method, violations)
    }
}

class LongParameterListCRule(config: LongParameterListRuleConfig = LongParameterListRuleConfig()) :
    CRule<LongParameterListRuleConfig>(config) {

    override fun visit(declaration: IASTDeclaration): Int {
        if (declaration is CPPASTFunctionDefinition) {

            val parameterCount = declaration.declarator.children.size
            if (parameterCount > config.maximumParameters) {
                addViolation(
                    Violation(
                        Violation.Location(
                            File(declaration.fileLocation.fileName),
                            declaration.fileLocation.startingLineNumber
                        ),
                        "Function '${declaration.declarator.name}' has $parameterCount parameters. More than the allowed maximum of ${config.maximumParameters}"
                    )
                )
            }
        }
        return super.visit(declaration)
    }
}
