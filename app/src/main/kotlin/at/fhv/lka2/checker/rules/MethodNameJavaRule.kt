package at.fhv.lka2.checker.rules

import at.fhv.lka2.checker.config.RuleConfig
import at.fhv.lka2.checker.getSourceText
import at.fhv.lka2.checker.model.CRule
import at.fhv.lka2.checker.model.JavaRule
import at.fhv.lka2.checker.model.Violation
import com.github.javaparser.ast.body.MethodDeclaration
import org.eclipse.cdt.core.dom.ast.IASTDeclaration
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionDefinition
import java.io.File

data class MethodNameConfig(
    override val enabled: Boolean = true,
    val pattern: Regex = "[a-z][a-zA-Z]*".toRegex()
) : RuleConfig

class MethodNameJavaRule(config: MethodNameConfig = MethodNameConfig()) :
    JavaRule<MethodNameConfig>(config) {

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

data class FunctionNameRuleConfig(
    override val enabled: Boolean = true,
    val pattern: Regex = "[a-z][a-zA-Z]*".toRegex()
) : RuleConfig

class FunctionNameCRule(config: FunctionNameRuleConfig = FunctionNameRuleConfig()) :
    CRule<FunctionNameRuleConfig>(config) {

    override fun visit(declaration: IASTDeclaration): Int {
        val sourceText = declaration.getSourceText()
        if (declaration is CPPASTFunctionDefinition) {

            val functionName = declaration.declarator.name.toString()

            if (!functionName.matches(config.pattern)) {
                addViolation(
                    Violation(
                        Violation.Location(
                            File(declaration.fileLocation.fileName),
                            declaration.fileLocation.startingLineNumber
                        ), "Function $functionName does not match pattern ${config.pattern}"
                    )
                )
            }
        }
        return super.visit(declaration)
    }
}
