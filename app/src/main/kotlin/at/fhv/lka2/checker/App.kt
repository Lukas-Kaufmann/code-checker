package at.fhv.lka2.checker

import at.fhv.lka2.checker.config.RuleLoader
import at.fhv.lka2.checker.model.JavaRule
import at.fhv.lka2.checker.model.Violation
import java.io.File
import kotlin.system.exitProcess

fun analyzeDirectory(directory: File, rules: Collection<JavaRule<*>>): List<Violation> {
    return directory.walk()
        .filter { it.isFile && it.extension.lowercase() in listOf("java", "c", "h") }
        .flatMap { analyzeFile(it, rules) }.toList()
}

private fun analyzeFile(file: File, rules: Collection<JavaRule<*>>): List<Violation> =
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
        violations.forEach { violation ->
            println("${violation.location.file.absolutePath}:${violation.location.line} - ${violation.message}")
        }
        //TODO html output
        exitProcess(1)
    }
}
