package at.fhv.lka2.checker.config

import at.fhv.lka2.checker.rules.FieldPatternRule
import at.fhv.lka2.checker.rules.MethodLengthRule
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainAll

class RuleLoaderTest : FreeSpec({
    "the ruleLoader" - {

        "should load rules" {
            RuleLoader.loadRules().map { it::class } shouldContainAll listOf(
                FieldPatternRule::class,
                MethodLengthRule::class
            )
        }

        "should only load enabled rules" {

        }

        "should configure the rules" {

        }
    }
})
