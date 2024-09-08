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

    private inline fun <reified T : Any> configureRules(configs: Map<String, Any>): List<T> {
        return getAllSubclassesOf(T::class).map { subclass ->
            val configKey = subclass.simpleName!!.removeSuffix(T::class.simpleName.toString()).lowerCaseFirstLetter()
            val ruleType = subclass.supertypes
                .firstOrNull { it.classifier == T::class }
                ?.arguments?.firstOrNull()?.type?.classifier as KClass<*>

            val ruleConfig = configs[configKey]?.let { mapper.convertValue(it, ruleType.java) }
            val constructor = subclass.constructors.first()

            if (ruleConfig == null) {
                constructor.callBy(emptyMap()) as T
            } else {
                constructor.call(ruleConfig) as T
            }
        }.filter { (it as? Rule<*>)?.isEnabled() == true }
    }

    fun loadRules(): List<Rule<*>> {
        val path = System.getenv(CONFIG_PATH_ENV)?.takeIf { it.isNotBlank() } ?: DEFAULT_CONFIG_PATH
        val configs = loadConfig(path)

        val javaRules = configureRules<JavaRule<*>>(configs)
        val cRules = configureRules<CRule<*>>(configs)
        return javaRules + cRules
    }
}
