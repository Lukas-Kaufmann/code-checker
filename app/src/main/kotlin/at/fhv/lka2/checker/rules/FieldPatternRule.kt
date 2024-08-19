package at.fhv.lka2.checker.rules

import at.fhv.lka2.checker.model.Rule
import at.fhv.lka2.checker.model.Violation
import com.github.javaparser.ast.body.FieldDeclaration
import java.io.File

class FieldPatternRule : Rule() {

    override fun visit(field: FieldDeclaration, violations: MutableList<Violation>) {
        field.variables.forEach { variable ->
            val name = variable.nameAsString
            if (!name.matches(Regex("^[a-z][a-zA-Z0-9]*$"))) {
                violations.add(
                    Violation(
                        this,
                        Violation.Location(
                            File(field.findCompilationUnit().get().storage.get().fileName),
                            field.begin.get().line
                        ),
                        "Class variable '$name' does not follow camelCase convention"
                    )
                )
            }
        }

        super.visit(field, violations)
    }
}