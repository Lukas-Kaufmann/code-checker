package at.fhv.lka2.checker.rules

import at.fhv.lka2.checker.model.Violation
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldHaveSize
import kotlin.io.path.deleteIfExists
import kotlin.io.path.writeText

class FieldPatternRuleTest : FreeSpec({

    val rule = FieldPatternRule()

    fun analyzeCode(code: String): List<Violation> {
        val file = kotlin.io.path.createTempFile()
        file.writeText(code)
        val violations = rule.check(file.toFile())
        file.deleteIfExists()
        return violations
    }

    "valid variable names" {
        val code = """
            class TestClass {
                private int validName;
                public String anotherValidName;
                protected double yetAnotherValidName;
            }
        """.trimIndent()

        val violations = analyzeCode(code)
        violations shouldHaveSize 0
    }

    "invalid variable names" {
        val code = """
            class TestClass {
                private int InvalidName;
                public String ANOTHERNAME;
                protected double _yetAnotherName;
                int Abc;
            }
        """.trimIndent()

        val violations = analyzeCode(code)
        violations shouldHaveSize 4
        val messages = violations.map { it.message }
        messages shouldContainAll listOf(
            "Class variable 'InvalidName' does not follow Pattern: ^[a-z][a-zA-Z0-9]*\$",
            "Class variable 'ANOTHERNAME' does not follow Pattern: ^[a-z][a-zA-Z0-9]*\$",
            "Class variable '_yetAnotherName' does not follow Pattern: ^[a-z][a-zA-Z0-9]*\$",
            "Class variable 'Abc' does not follow Pattern: ^[a-z][a-zA-Z0-9]*\$"
        )
    }

    "mixed valid and invalid names" {
        val code = """
            class TestClass {
                private int validName;
                public String InvalidName;
                protected double _anotherInvalidName;
            }
        """.trimIndent()

        val violations = analyzeCode(code)
        violations shouldHaveSize 2
        val messages = violations.map { it.message }
        messages shouldContainAll listOf(
            "Class variable 'InvalidName' does not follow Pattern: ^[a-z][a-zA-Z0-9]*\$",
            "Class variable '_anotherInvalidName' does not follow Pattern: ^[a-z][a-zA-Z0-9]*\$",
        )
    }

    "empty class" {
        val code = """
            class TestClass {
            }
        """.trimIndent()

        val violations = analyzeCode(code)
        violations shouldHaveSize 0
    }

    "interface" {
        val code = """
            interface TestInterface {
                int INVALID_NON_STATIC_VARIABLE = 1;
                String validVariable = "test";
            }
        """.trimIndent()

        val violations = analyzeCode(code)
        violations shouldHaveSize 1
        val messages = violations.map { it.message }
        println(messages)
        messages shouldContainAll listOf(
            "Class variable 'INVALID_NON_STATIC_VARIABLE' does not follow Pattern: ^[a-z][a-zA-Z0-9]*\$",
        )
    }
})


