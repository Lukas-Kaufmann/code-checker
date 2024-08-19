package at.fhv.lka2.checker.model

import at.fhv.lka2.checker.rules.InvalidSyntaxRule
import com.github.javaparser.JavaParser
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import java.io.File

abstract class Rule : VoidVisitorAdapter<MutableList<Violation>>() {

    fun check(file: File): MutableList<Violation> {
        val violations = mutableListOf<Violation>()
        val javaParser = JavaParser()
        val parseResult = javaParser.parse(file)

        if (parseResult.isSuccessful) {
            val cu = parseResult.result.get()
            this.visit(cu, violations)
        } else {
            violations.add(
                Violation(
                    InvalidSyntaxRule(),
                    Violation.Location(file, 0),
                    "Failed to parse file: ${parseResult.problems}"
                )
            )
        }

        return violations
    }
}
