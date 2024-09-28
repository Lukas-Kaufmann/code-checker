package at.fhv.lka2.checker.rules

import at.fhv.lka2.checker.config.RuleConfig
import at.fhv.lka2.checker.getSourceText
import at.fhv.lka2.checker.model.JavaRule
import at.fhv.lka2.checker.model.Violation
import com.github.javaparser.ast.stmt.TryStmt

data class TryCatchFormatRuleConfig(override val enabled: Boolean = true) : RuleConfig


class TryCatchFormatJavaRule(config: TryCatchFormatRuleConfig = TryCatchFormatRuleConfig()) : JavaRule<TryCatchFormatRuleConfig>(config) {

    private val singleIfPattern = "try \\{(.|\\n)*\\} catch (.*) \\{(.|\\n)*\\}".toRegex()

    override fun visit(tryStmt: TryStmt, arg: MutableList<Violation>) {
        val file = tryStmt.findCompilationUnit().get().storage.get().path
        val start = tryStmt.begin.get()
        val end = tryStmt.end.get()

        val sourceText = file.getSourceText(start, end)

        if (!sourceText.matches(singleIfPattern)) {
            arg.add(Violation(Violation.Location(file.toFile(), start.line), "Invalid format for try catch statement"))
        }
        super.visit(tryStmt, arg)
    }
}
