package at.fhv.lka2.checker.rules

import at.fhv.lka2.checker.config.RuleConfig
import at.fhv.lka2.checker.getSourceText
import at.fhv.lka2.checker.model.CRule
import at.fhv.lka2.checker.model.JavaRule
import at.fhv.lka2.checker.model.Violation
import com.github.javaparser.ast.stmt.IfStmt
import org.eclipse.cdt.core.dom.ast.IASTStatement
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTIfStatement
import java.io.File

data class IfFormatRuleConfig(override val enabled: Boolean = true) : RuleConfig

private val singleIfPattern = "if \\(\\S.*\\S\\) \\{(.|\\n)*\\}".toRegex()

private val bracketedPattern = "\\{(.|\\n)*\\}".toRegex()

class IfFormatJavaRule(config: IfFormatRuleConfig = IfFormatRuleConfig()) : JavaRule<IfFormatRuleConfig>(config) {

    override fun visit(ifStatement: IfStmt, arg: MutableList<Violation>) {
        val file = ifStatement.findCompilationUnit().get().storage.get().path
        val start = ifStatement.begin.get()
        val end = ifStatement.end.get()

        val sourceText = file.getSourceText(start, end)

        if (!sourceText.matches(singleIfPattern)) {
            arg.add(Violation(Violation.Location(file.toFile(), start.line), "Invalid format for if pattern"))
        }

        if (ifStatement.hasElseBlock()) {
            val elseStatement = ifStatement.elseStmt.get()
            val elseSourceText = file.getSourceText(elseStatement.begin.get(), elseStatement.end.get())
            if (!elseSourceText.matches(bracketedPattern)) {
                arg.add(Violation(Violation.Location(file.toFile(), start.line), "Invalid format for if pattern"))
            }
        }
        super.visit(ifStatement, arg)
    }
}

class IfFormatCRule(config: IfFormatRuleConfig = IfFormatRuleConfig()) : CRule<IfFormatRuleConfig>(config) {
    override fun visit(statement: IASTStatement): Int {
        if (statement is CPPASTIfStatement) {
            val sourceText = statement.getSourceText()
            if (!sourceText.matches(singleIfPattern)) {
                addViolation(
                    Violation(
                        Violation.Location(
                            File(statement.fileLocation.fileName),
                            statement.fileLocation.startingLineNumber
                        ), "Invalid format for if pattern"
                    )
                )
            }

            statement.elseClause?.let {
                val elseSourceText = it.getSourceText()
                if (!elseSourceText.matches(bracketedPattern)) {
                    addViolation(Violation(
                        Violation.Location(
                            File(statement.fileLocation.fileName),
                            statement.fileLocation.startingLineNumber
                        ), "Invalid format for else clause in if statement"
                    ))
                }
            }
        }

        return super.visit(statement)
    }
}