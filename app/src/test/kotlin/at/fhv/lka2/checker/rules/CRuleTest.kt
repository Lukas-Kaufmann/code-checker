package at.fhv.lka2.checker.rules

import io.kotest.core.spec.style.FreeSpec
import java.io.File

class CRuleTest : FreeSpec({

    "a c rule" - {

        "should do foo" {
            val rule = VariableNameCRule()
            rule.check(File("/Users/lukas/code-checker/violations/src/main/c/VariableNamingViolation.c"))
        }
    }
})