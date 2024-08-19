package at.fhv.lka2.checker

import at.fhv.lka2.checker.model.Rule
import at.fhv.lka2.checker.model.Violation
import at.fhv.lka2.checker.rules.FieldPatternRule
import at.fhv.lka2.checker.rules.InvalidSyntaxRule
import at.fhv.lka2.checker.rules.MethodLengthRule
import java.io.File
import kotlin.system.exitProcess

fun analyzeDirectory(directory: File, rules: Collection<Rule>): List<Violation> {
    return directory.walk()
        .filter { it.isFile && it.extension == "java" }
        .flatMap { analyzeFile(it, rules) }.toList()
}

private fun analyzeFile(file: File, rules: Collection<Rule>): List<Violation> = rules.flatMap { it.check(file) }

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

    //TODO get by reflection, plus filter basic config enabled
    val rules = listOf(InvalidSyntaxRule(), MethodLengthRule(), FieldPatternRule())

    val violations = analyzeDirectory(directory, rules)

    if (violations.isEmpty()) {
        println("Succeeded! No Violations found")
        exitProcess(0)
    } else {
        println("Failed!")
        violations.forEach { violation ->
            println("${violation.location.file.name}:${violation.location.line} - ${violation.message}")
        }
        //TODO html output
        exitProcess(1)
    }
}
