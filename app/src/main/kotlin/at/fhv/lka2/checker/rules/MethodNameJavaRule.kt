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

/**
 * MethodNameJavaRule: Enforces a naming convention for Java method names.
 *
 * This rule ensures that Java method names follow a specified naming pattern, which is configurable through a regular expression. By default, method names must start with a lowercase letter and can be followed by any combination of uppercase or lowercase letters (e.g., `myMethod`).
 *
 * Configuration:
 * - `pattern`: The regex pattern that method names must match (default: `[a-z][a-zA-Z]*`)
 *
 * Examples of compliant method names:
 * - `myMethod`
 * - `calculateTotal`
 *
 * Examples of non-compliant method names:
 * - `MyMethod` (starts with an uppercase letter)
 * - `calculate_total` (contains an underscore)
 *
 * Example of compliant code:
 * ```java
 * class Calculator {
 *     public int calculateTotal(int a, int b) {
 *         return a + b;
 *     }
 * }
 * ```
 *
 * Example of non-compliant code:
 * ```java
 * class Calculator {
 *     public int CalculateTotal(int a, int b) { // Violation: Method name starts with an uppercase letter.
 *         return a + b;
 *     }
 * }
 * ```
 */
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

/**
 * FunctionNameCRule: Enforces a naming convention for C/C++ function names.
 *
 * This rule ensures that C/C++ function names follow a specified naming pattern, which is configurable through a regular expression. By default, function names must start with a lowercase letter and can be followed by any combination of uppercase or lowercase letters (e.g., `myFunction`).
 *
 * Configuration:
 * - `pattern`: The regex pattern that function names must match (default: `[a-z][a-zA-Z]*`)
 *
 * Examples of compliant function names:
 * - `myFunction`
 * - `calculateTotal`
 *
 * Examples of non-compliant function names:
 * - `MyFunction` (starts with an uppercase letter)
 * - `calculate_total` (contains an underscore)
 *
 * Example of compliant code:
 * ```cpp
 * int calculateTotal(int a, int b) {
 *     return a + b;
 * }
 * ```
 *
 * Example of non-compliant code:
 * ```cpp
 * int CalculateTotal(int a, int b) { // Violation: Function name starts with an uppercase letter.
 *     return a + b;
 * }
 * ```
 */
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
