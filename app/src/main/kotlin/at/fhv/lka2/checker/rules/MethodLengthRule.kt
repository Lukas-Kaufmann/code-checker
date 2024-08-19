package at.fhv.lka2.checker.rules

import at.fhv.lka2.checker.model.Rule
import at.fhv.lka2.checker.model.Violation
import com.github.javaparser.ast.body.MethodDeclaration
import java.io.File

class MethodLengthRule : Rule() {

    override fun visit(n: MethodDeclaration, violations: MutableList<Violation>) {
        if (n.body.isPresent && n.body.get().statements.size > 50) {
            violations.add(
                Violation(
                    this,
                    Violation.Location(File(n.findCompilationUnit().get().storage.get().fileName), n.begin.get().line),
                    "Method '${n.nameAsString}' is too long (${n.body.get().statements.size} lines)"
                )
            )
        }
        super.visit(n, violations)
    }
}
