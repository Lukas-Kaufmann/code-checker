package at.fhv.lka2.checker.rules

import at.fhv.lka2.checker.config.RuleConfig
import at.fhv.lka2.checker.model.CRule
import at.fhv.lka2.checker.model.JavaRule
import at.fhv.lka2.checker.model.Violation
import com.github.javaparser.ast.body.MethodDeclaration
import org.eclipse.cdt.core.dom.ast.IASTDeclaration
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionDefinition
import java.io.File

data class LongParameterListRuleConfig(override val enabled: Boolean = true, val maximumParameters: Int = 11) :
    RuleConfig

/**
 * LongParameterListJavaRule: Enforces a maximum number of parameters for methods in Java code.
 *
 * This rule checks whether the number of parameters in a Java method exceeds a configurable limit. By default, it flags methods with more than 11 parameters, encouraging better design practices such as:
 * - Reducing complexity by passing fewer arguments
 * - Improving readability and maintainability of methods
 * - Encouraging method refactoring and the use of objects or parameter objects instead of long parameter lists
 *
 * Configuration:
 * - `maximumParameters`: The maximum number of allowed parameters in a method (default: 11)
 *
 * Example of a compliant method:
 * ```java
 * public void createUser(String name, String email, int age) {
 *     // method body
 * }
 * ```
 *
 * Example of a non-compliant method:
 * ```java
 * public void processOrder(String orderId, String userId, Date orderDate, String shippingAddress, String billingAddress,
 *                          int productId, int quantity, double price, double tax, double discount, String comments) {
 *     // Violation: Too many parameters (12), exceeding the allowed maximum of 11
 * }
 * ```
 *
 * Refactoring tip for violations:
 * Consider grouping related parameters into objects or reducing unnecessary parameters by using parameter objects or data classes.
 */
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

/**
 * LongParameterListCRule: Enforces a maximum number of parameters for functions in C/C++ code.
 *
 * This rule checks whether the number of parameters in a C/C++ function exceeds a configurable limit. By default, it flags functions with more than 11 parameters. This rule encourages the use of more structured approaches to managing function arguments, such as:
 * - Reducing complexity by minimizing the number of function arguments
 * - Enhancing code readability and maintainability
 * - Encouraging the use of structures or parameter objects to group related parameters
 *
 * Configuration:
 * - `maximumParameters`: The maximum number of allowed parameters in a function (default: 11)
 *
 * Example of a compliant function:
 * ```cpp
 * void createUser(const std::string& name, const std::string& email, int age) {
 *     // function body
 * }
 * ```
 *
 * Example of a non-compliant function:
 * ```cpp
 * void processOrder(const std::string& orderId, const std::string& userId, const std::string& orderDate,
 *                   const std::string& shippingAddress, const std::string& billingAddress,
 *                   int productId, int quantity, double price, double tax, double discount, const std::string& comments) {
 *     // Violation: Too many parameters (12), exceeding the allowed maximum of 11
 * }
 * ```
 *
 * Refactoring tip for violations:
 * Consider organizing parameters into structures or reducing the number of parameters by refactoring the function's design.
 */
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
