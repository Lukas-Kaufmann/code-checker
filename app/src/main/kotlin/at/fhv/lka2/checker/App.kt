package at.fhv.lka2.checker

import at.fhv.lka2.checker.config.RuleLoader
import at.fhv.lka2.checker.model.JavaRule
import at.fhv.lka2.checker.model.Rule
import at.fhv.lka2.checker.model.Violation
import java.io.File
import kotlin.system.exitProcess

fun analyzeDirectory(directory: File, rules: Collection<Rule<*>>): List<Violation> {
    val (javaRules, cRules) = rules.partition { rule -> rule is JavaRule<*> }
    val (javaFiles, cFiles) = directory.walk()
        .filter { it.isFile && it.extension.lowercase() in listOf("java", "c", "h") }
        .partition { it.extension.lowercase() == "java" }

    return javaFiles.flatMap { analyzeFile(it, javaRules) } + cFiles.flatMap { analyzeFile(it, cRules) }
}

private fun analyzeFile(file: File, rules: Collection<Rule<*>>): List<Violation> =
    rules.flatMap { it.check(file) }

fun main(args: Array<String>) {
    val directory = if (args.isNotEmpty()) {
        File(args[0])
    } else {
        File(".")
    }

    if (!directory.exists() || !directory.isDirectory) {
        println("Error: Invalid directory specified.")
        exitProcess(1)
    }

    val rules = RuleLoader.loadRules()
    val violations = analyzeDirectory(directory, rules)

    if (violations.isEmpty()) {
        println("Succeeded! No Violations found")
        exitProcess(0)
    } else {
        println("Failed!")
        violations.groupBy { it.location.file.absolutePath }.forEach { (path, violations) ->
            println("$path - ${violations.size} Violations")
            violations.forEach { violation ->
                println("\t${violation.message} at ${violation.location.file.absolutePath}:${violation.location.line}")
            }
        }
        //TODO html output
        exitProcess(1)
    }
}
