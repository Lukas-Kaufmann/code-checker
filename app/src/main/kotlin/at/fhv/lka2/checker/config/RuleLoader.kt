package at.fhv.lka2.checker.config

import at.fhv.lka2.checker.lowerCaseFirstLetter
import at.fhv.lka2.checker.model.CRule
import at.fhv.lka2.checker.model.JavaRule
import at.fhv.lka2.checker.model.Rule
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.reflections.Reflections
import java.io.File
import kotlin.reflect.KClass

object RuleLoader {

    private const val DEFAULT_CONFIG_PATH = "./checker/config.yaml"
    private const val CONFIG_PATH_ENV = "CHECKER_CONFIG_PATH"

    private val mapper = jacksonObjectMapper()

    private fun <T : Any> getAllSubclassesOf(baseClass: KClass<T>): List<KClass<*>> {
        val reflections = Reflections("at.fhv.lka2.checker")

        val allClasses: Set<Class<*>> =
            reflections.getSubTypesOf(baseClass.java)

        return allClasses.map { it.kotlin }
    }

    private fun loadConfig(filePath: String): Map<String, Any> {
        val mapper = ObjectMapper(YAMLFactory())
        val file = File(filePath)
        if (!file.exists()) return emptyMap()
        return mapper.readValue(File(filePath))
    }

    fun loadRules(): List<Rule<*>> {
        val path = System.getenv(CONFIG_PATH_ENV)?.takeIf { it.isNotBlank() } ?: DEFAULT_CONFIG_PATH
        val configs = loadConfig(path)

        val allJavaRules = getAllSubclassesOf(JavaRule::class)
        val configuredJavaRules = allJavaRules.map { ruleClass ->
            val configKey = ruleClass.simpleName!!.removeSuffix("JavaRule").lowerCaseFirstLetter()
            val javaRule =
                ruleClass.supertypes.firstOrNull { it.classifier == JavaRule::class }?.arguments?.firstOrNull()?.type?.classifier as KClass<*>

            val ruleConfig =
                configs[configKey]?.let { mapper.convertValue(it, javaRule.java) }
            val constructor = ruleClass.constructors.first()
            if (ruleConfig == null) {
                //callBy substitutes the missing argument with the default argument
                constructor.callBy(emptyMap()) as JavaRule<*>
            } else {
                constructor.call(ruleConfig) as JavaRule<*>
            }
        }.filter { it.isEnabled() }

        val allCRules = getAllSubclassesOf(CRule::class)
        val configuredCRules = allCRules.map { ruleClass ->
            val configKey = ruleClass.simpleName!!.removeSuffix("CRule").lowerCaseFirstLetter()

            val cRule =
                ruleClass.supertypes.firstOrNull { it.classifier == CRule::class }?.arguments?.firstOrNull()?.type?.classifier as KClass<*>

            val ruleConfig =
                configs[configKey]?.let { mapper.convertValue(it, cRule.java) }
            val constructor = ruleClass.constructors.first()
            if (ruleConfig == null) {
                //callBy substitutes the missing argument with the default argument
                constructor.callBy(emptyMap()) as CRule<*>
            } else {
                constructor.call(ruleConfig) as CRule<*>
            }
        }.filter { it.isEnabled() }
        return configuredJavaRules + configuredCRules
    }
}
