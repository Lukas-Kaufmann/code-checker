package at.fhv.lka2.checker.model

import at.fhv.lka2.checker.config.RuleConfig
import org.eclipse.cdt.core.dom.ast.*
import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage
import org.eclipse.cdt.core.model.ILanguage
import org.eclipse.cdt.core.parser.*
import java.io.File

abstract class CRule<T : RuleConfig>(override val config: T) : ASTGenericVisitor(true), Rule<T> {

    fun addViolation(violation: Violation) {
        violations.add(violation)
    }

    private val violations = mutableListOf<Violation>()

    override fun check(file: File): MutableList<Violation> {
        violations.clear()
        val translationUnit: IASTTranslationUnit = parse(file)

        translationUnit.accept(this)
        return violations.toMutableList()
    }

    private fun parse(file: File): IASTTranslationUnit {
        val fc = FileContent.create(file.path, file.readText().toCharArray())
        val macroDefinitions = mutableMapOf<String, String>()
        val includeSearchPaths = mutableListOf<String>()
        val si = ScannerInfo(macroDefinitions, includeSearchPaths.toTypedArray())
        val ifcp: IncludeFileContentProvider = IncludeFileContentProvider.getEmptyFilesProvider()
        val idx = null
        val options = ILanguage.OPTION_IS_SOURCE_UNIT
        val log = DefaultLogService()
        return GPPLanguage.getDefault().getASTTranslationUnit(fc, si, ifcp, idx, options, log)
    }
}


