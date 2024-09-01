package at.fhv.lka2.checker.config

import at.fhv.lka2.checker.rules.FieldPatternJavaRule
import at.fhv.lka2.checker.rules.MethodLengthJavaRule
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainAll

class RuleLoaderTest : FreeSpec({
    "the ruleLoader" - {

        "should load rules" {
            RuleLoader.loadRules().map { it::class } shouldContainAll listOf(
                FieldPatternJavaRule::class,
                MethodLengthJavaRule::class
            )
        }

        "should only load enabled rules" {

        }

        "should configure the rules" {

        }
    }
})
