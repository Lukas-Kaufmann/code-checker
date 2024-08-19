package at.fhv.lka2.checker

import at.fhv.lka2.checker.model.Violation
import at.fhv.lka2.checker.rules.InvalidSyntaxRule
import at.fhv.lka2.checker.rules.MethodLengthRule
import com.github.javaparser.JavaParser
import java.io.File
import kotlin.system.exitProcess

fun analyzeDirectory(directory: File): List<Violation> {
    return directory.walk()
        .filter { it.isFile && it.extension == "java" }
        .flatMap { analyzeFile(it) }.toList()
}

private fun analyzeFile(file: File): List<Violation> {
    val javaParser = JavaParser()
    val violations = mutableListOf<Violation>()

    val result = javaParser.parse(file)
    if (result.isSuccessful) {
        val cu = result.result.get()
        val visitor = MethodLengthRule()
        visitor.visit(cu, violations)
    } else {
        violations.add(
            Violation(
                InvalidSyntaxRule(),
                Violation.Location(file, 0),
                "Failed to parse file: ${result.problems}"
            )
        )
    }

    return violations
}

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

    val violations = analyzeDirectory(directory)

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
