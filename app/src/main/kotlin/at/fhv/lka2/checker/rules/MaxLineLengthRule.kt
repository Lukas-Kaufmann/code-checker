package at.fhv.lka2.checker.rules

import at.fhv.lka2.checker.config.RuleConfig
import at.fhv.lka2.checker.model.CRule
import at.fhv.lka2.checker.model.JavaRule
import at.fhv.lka2.checker.model.Violation
import java.io.File


data class MaxLineLengthConfig(override val enabled: Boolean = true, val maxLineLength: Int = 80) : RuleConfig

/**
 * MaxLineLengthJavaRule: Enforces a maximum line length in Java source code files.
 *
 * This rule checks whether any line in a Java source file exceeds a configurable maximum length. By default, it flags lines that are longer than 80 characters. Limiting line length helps improve code readability and maintainability, making the code easier to view on various devices and within different environments (e.g., editors with smaller window sizes).
 *
 * Configuration:
 * - `maxLineLength`: The maximum allowed length of a line (default: 80 characters)
 *
 * Example of compliant code:
 * ```java
 * public void createUser(String name, String email) {
 *     System.out.println("User: " + name + ", Email: " + email);
 * }
 * ```
 *
 * Example of non-compliant code:
 * ```java
 * public void createUser(String name, String email) { System.out.println("User: " + name + ", Email: " + email); } // Too long
 * ```
 *
 * Refactoring tip for violations:
 * Split long lines into multiple lines to adhere to the maximum line length, using appropriate line breaks or refactoring.
 */
class MaxLineLengthJavaRule(config: MaxLineLengthConfig = MaxLineLengthConfig()) :
    JavaRule<MaxLineLengthConfig>(config) {

    override fun check(file: File): MutableList<Violation> {
        val lines = file.readLines()
        return lines.mapIndexedNotNull { lineNumber, line ->
            if (line.length > config.maxLineLength) {
                Violation(
                    Violation.Location(file, lineNumber),
                    "File '${file.absolutePath} has a line too long at line $lineNumber.",
                )
            } else {
                null
            }
        }.toMutableList()
    }
}

/**
 * MaxLineLengthCRule: Enforces a maximum line length in C/C++ source code files.
 *
 * This rule checks whether any line in a C/C++ source file exceeds a configurable maximum length. By default, it flags lines that are longer than 80 characters. Limiting line length improves code readability and ensures consistency across different editors and display environments, making code more manageable.
 *
 * Configuration:
 * - `maxLineLength`: The maximum allowed length of a line (default: 80 characters)
 *
 * Example of compliant code:
 * ```cpp
 * void createUser(const std::string& name, const std::string& email) {
 *     std::cout << "User: " << name << ", Email: " << email << std::endl;
 * }
 * ```
 *
 * Example of non-compliant code:
 * ```cpp
 * void createUser(const std::string& name, const std::string& email) { std::cout << "User: " << name << ", Email: " << email << std::endl; } // Too long
 * ```
 *
 * Refactoring tip for violations:
 * Break long lines into multiple lines or refactor code to adhere to the maximum line length.
 */
class MaxLineLengthCRule(config: MaxLineLengthConfig = MaxLineLengthConfig()) :
    CRule<MaxLineLengthConfig>(config) {

    override fun check(file: File): MutableList<Violation> {
        val lines = file.readLines()
        return lines.mapIndexedNotNull { lineNumber, line ->
            if (line.length > config.maxLineLength) {
                Violation(
                    Violation.Location(file, lineNumber),
                    "File '${file.absolutePath} has a line too long at line $lineNumber.",
                )
            } else {
                null
            }
        }.toMutableList()
    }
}
