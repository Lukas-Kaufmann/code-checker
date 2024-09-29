package at.fhv.lka2.checker.rules

import at.fhv.lka2.checker.config.RuleConfig
import at.fhv.lka2.checker.model.JavaRule
import at.fhv.lka2.checker.model.Violation
import com.github.javaparser.ast.PackageDeclaration
import java.io.File

data class PackageNameRuleConfig(
    override val enabled: Boolean = true,
    val patternRegex: Regex = "^[a-z][a-z0-9]*(\\.[a-z][a-z0-9]*)*\$".toRegex()
) : RuleConfig

class PackageNameJavaRule(config: PackageNameRuleConfig = PackageNameRuleConfig()) :
    JavaRule<PackageNameRuleConfig>(config) {

    override fun visit(packageDeclaration: PackageDeclaration, arg: MutableList<Violation>) {
        if (!packageDeclaration.nameAsString.matches(config.patternRegex)) {
            arg.add(
                Violation(
                    Violation.Location(
                        File(packageDeclaration.findCompilationUnit().get().storage.get().fileName),
                        packageDeclaration.begin.get().line
                    ),
                    "Package ${packageDeclaration.nameAsString} doesn't match the pattern ${config.patternRegex}"
                )
            )
        }
        super.visit(packageDeclaration, arg)
    }
}
