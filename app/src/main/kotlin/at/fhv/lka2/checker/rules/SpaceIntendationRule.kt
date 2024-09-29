package at.fhv.lka2.checker.rules

import at.fhv.lka2.checker.config.RuleConfig
import at.fhv.lka2.checker.model.CRule
import at.fhv.lka2.checker.model.JavaRule
import at.fhv.lka2.checker.model.Violation
import java.io.File

data class SpaceIndentationRuleConfig(override val enabled: Boolean = true) : RuleConfig

/**
 * SpaceIndentationJavaRule: Enforces consistent indentation using spaces in Java source files.
 *
 * This rule checks that lines in Java source files are indented with a multiple of four spaces. If a line starts with spaces, it must use exactly four spaces for each level of indentation. Tabs are not allowed for indentation.
 *
 * Configuration:
 * - `enabled`: Whether this rule is active (default: true)
 *
 * Examples of compliant lines:
 * ```java
 * public class MyClass {
 *     public void myMethod() {
 *         System.out.println("Hello, World!");
 *     }
 * }
 * ```
 *
 * Examples of non-compliant lines:
 * ```java
 * public class MyClass {
 *  public void myMethod() { // Violation: This line is not indented with a multiple of 4 spaces.
 *      System.out.println("Hello, World!");
 *   }
 * }
 * ```
 */
class SpaceIndentationJavaRule(config: SpaceIndentationRuleConfig = SpaceIndentationRuleConfig()) :
    JavaRule<SpaceIndentationRuleConfig>(config) {

    //lines should start with
    override fun check(file: File): MutableList<Violation> {
        val lines = file.readLines()

        return lines.mapIndexedNotNull { lineNumber, line ->
            val leadingSpaces = line.takeWhile { it == ' ' }.length
            if (!line.startsWith('\t') && leadingSpaces % 4 != 0) {
                Violation(
                    Violation.Location(file, lineNumber),
                    "Not 4 spaces of indentation used at '${file.absolutePath} line $lineNumber.",
                )
            } else {
                null
            }
        }.toMutableList()
    }
}

/**
 * SpaceIndentationCRule: Enforces consistent indentation using spaces in C source files.
 *
 * This rule checks that lines in C source files are indented with a multiple of four spaces. If a line starts with spaces, it must use exactly four spaces for each level of indentation. Tabs are not allowed for indentation.
 *
 * Configuration:
 * - `enabled`: Whether this rule is active (default: true)
 *
 * Examples of compliant lines:
 * ```c
 * void myFunction() {
 *     printf("Hello, World!\n");
 * }
 * ```
 *
 * Examples of non-compliant lines:
 * ```c
 * void myFunction() {
 *  printf("Hello, World!\n"); // Violation: This line is not indented with a multiple of 4 spaces.
 *   }
 * }
 * ```
 */
class SpaceIndentationCRule(config: SpaceIndentationRuleConfig = SpaceIndentationRuleConfig()) :
    CRule<SpaceIndentationRuleConfig>(config) {

    //lines should start with
    override fun check(file: File): MutableList<Violation> {
        val lines = file.readLines()

        return lines.mapIndexedNotNull { lineNumber, line ->
            val leadingSpaces = line.takeWhile { it == ' ' }.length
            if (!line.startsWith('\t') && leadingSpaces % 4 != 0) {
                Violation(
                    Violation.Location(file, lineNumber),
                    "Not 4 spaces of indentation used at '${file.absolutePath} line $lineNumber.",
                )
            } else {
                null
            }
        }.toMutableList()
    }
}
