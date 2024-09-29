package at.fhv.lka2.checker.rules

import at.fhv.lka2.checker.config.RuleConfig
import at.fhv.lka2.checker.model.JavaRule
import at.fhv.lka2.checker.model.Violation
import com.github.javaparser.ast.PackageDeclaration
import java.io.File

data class PackageNameRuleConfig(
    override val enabled: Boolean = true,
    val patternRegex: Regex = "^[a-z][a-z0-9]*(\\.[a-z][a-z0-9]*)*\$".toRegex()
) : RuleConfig

/**
 * PackageNameJavaRule: Enforces a naming convention for Java package names.
 *
 * This rule checks that Java package names conform to a specified regular expression pattern. By default, package names must start with a lowercase letter and can be followed by lowercase letters or digits, separated by periods. The default pattern is `^[a-z][a-z0-9]*(\\.[a-z][a-z0-9]*)*$`, which allows for hierarchical package structures.
 *
 * Configuration:
 * - `patternRegex`: The regex pattern that package names must match (default: `^[a-z][a-z0-9]*(\\.[a-z][a-z0-9]*)*$`)
 *
 * Examples of compliant package names:
 * - `com.example.project`
 * - `org.openai`
 * - `net.myapp`
 *
 * Examples of non-compliant package names:
 * - `Com.Example` (starts with an uppercase letter)
 * - `example.123project` (contains digits immediately after the period)
 * - `org.openAI` (contains an uppercase letter after the first character)
 *
 * Example of compliant code:
 * ```java
 * package com.example.project;
 *
 * public class MyClass {
 * }
 * ```
 *
 * Example of non-compliant code:
 * ```java
 * package Com.Example; // Violation: Package name starts with an uppercase letter.
 *
 * public class MyClass {
 * }
 * ```
 */
class PackageNameJavaRule(config: PackageNameRuleConfig = PackageNameRuleConfig()) :
    JavaRule<PackageNameRuleConfig>(config) {

    override fun visit(packageDeclaration: PackageDeclaration, arg: MutableList<Violation>) {
        if (!packageDeclaration.nameAsString.matches(config.patternRegex)) {
            arg.add(
                Violation(
                    Violation.Location(
                        File(packageDeclaration.findCompilationUnit().get().storage.get().fileName),
                        packageDeclaration.begin.get().line
                    ),
                    "Package ${packageDeclaration.nameAsString} doesn't match the pattern ${config.patternRegex}"
                )
            )
        }
        super.visit(packageDeclaration, arg)
    }
}
