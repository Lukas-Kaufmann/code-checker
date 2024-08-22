package at.fhv.lka2.checker.model

import at.fhv.lka2.checker.config.RuleConfig
import com.github.javaparser.JavaParser
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import java.io.File

abstract class JavaRule<T : RuleConfig>(override val config: T) : VoidVisitorAdapter<MutableList<Violation>>(),
    Rule<T> {

    override fun check(file: File): MutableList<Violation> {
        val violations = mutableListOf<Violation>()
        val javaParser = JavaParser()
        val parseResult = javaParser.parse(file)

        if (parseResult.isSuccessful) {
            val cu = parseResult.result.get()
            this.visit(cu, violations)
        } else {
            violations.add(
                Violation(
                    Violation.Location(file, 0),
                    "Failed to parse file: ${parseResult.problems}"
                )
            )
        }

        return violations
    }
}
