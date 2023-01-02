package dev.notrobots.preferences2.processor

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import dev.notrobots.preferences2.annotations.*
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import kotlin.reflect.KClass

@SupportedSourceVersion(SourceVersion.RELEASE_8)
class Preferences2Processor : AbstractProcessor() {
    private val outputFileName = "Preferences2"
    private val outputFilePackage = "dev.notrobots.preferences2"

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(
            BooleanPreference::class.java.canonicalName,
            EnumPreference::class.java.canonicalName,
            FloatPreference::class.java.canonicalName,
            IntPreference::class.java.canonicalName,
            LongPreference::class.java.canonicalName,
            StringPreference::class.java.canonicalName,
            StringSetPreference::class.java.canonicalName,
            JSONObjectPreference::class.java.canonicalName,
        )
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        if (annotations?.isNotEmpty() == true) {
            val outputFile = FileSpec.builder(outputFilePackage, outputFileName)
                .addImport("dev.notrobots.preferences2.extensions", "getEnum")
                .addImport("dev.notrobots.preferences2.extensions", "putEnum")
                .addImport("dev.notrobots.preferences2.extensions", "getJSONObject")
                .addImport("dev.notrobots.preferences2.extensions", "putJSONObject")
                .addImport("dev.notrobots.preferences2.extensions", "edit")
                .addImport("dev.notrobots.preferences2.util", "parseEnum")
                .addImport("android.content", "SharedPreferences")
                .indent("\t")

            for (a in supportedAnnotationTypes) {
                @Suppress("UNCHECKED_CAST")
                val annotationClass = Class.forName(a) as Class<Annotation>
                val annotatedElements = roundEnv.getElementsAnnotatedWith(annotationClass)

                for (element in annotatedElements) {
                    val annotation = element.getAnnotation(annotationClass)
                    val functions = generatePreferenceFunctions(element, annotation, annotationClass.kotlin)

                    for (function in functions) {
                        outputFile.addFunction(function)
                    }
                }
            }

            outputFile.build().writeTo(processingEnv.filer)
        }

        return true
    }

    private fun generatePreferenceFunctions(
        element: Element,
        annotation: Annotation,
        annotationClass: KClass<*>
    ): List<FunSpec> {
        val propName = element.simpleName.toString()
        val propType = element.asType().toString()
        val propIsString = (propType == "kotlin.String" || propType == "java.lang.String")

        if (element.modifiers.containsAll(REQUIRED_MODIFIERS) && propIsString) {
            val propValue = (element as VariableElement).constantValue
            var functionName = snakeToPascalCase(propName).replaceFirstChar(Char::titlecase)
            val defaultParameterType: TypeName?
            val defaultParameterValue: String?
            val valueParameterType: TypeName
            val getStatement: String
            val putStatement: String
            val isEnum = annotation is EnumPreference

            when (annotation) {
                is IntPreference -> {
                    defaultParameterType = INT
                    defaultParameterValue = annotation.defaultValue.toString()
                    valueParameterType = INT
                    getStatement = "return getInt(\"$propValue\", default)"    // XXX: Should this use the constant reference or just a plain string with the contant value?
                    putStatement = "putInt(\"$propValue\", value)"

                    if (annotation.functionName.isNotBlank()) {
                        functionName = annotation.functionName
                    }
                }
                is BooleanPreference -> {
                    defaultParameterType = BOOLEAN
                    defaultParameterValue = annotation.defaultValue.toString()
                    valueParameterType = BOOLEAN
                    getStatement = "return getBoolean(\"$propValue\", default)"
                    putStatement = "putBoolean(\"$propValue\", value)"

                    if (annotation.functionName.isNotBlank()) {
                        functionName = annotation.functionName
                    }
                }
                is LongPreference -> {
                    defaultParameterType = LONG
                    defaultParameterValue = annotation.defaultValue.toString() + "L"
                    valueParameterType = LONG
                    getStatement = "return getLong(\"$propValue\", default)"
                    putStatement = "putLong(\"$propValue\", value)"

                    if (annotation.functionName.isNotBlank()) {
                        functionName = annotation.functionName
                    }
                }
                is FloatPreference -> {
                    defaultParameterType = FLOAT
                    defaultParameterValue = annotation.defaultValue.toString() + "F"
                    valueParameterType = FLOAT
                    getStatement = "return getFloat(\"$propValue\", default)"
                    putStatement = "putFloat(\"$propValue\", value)"

                    if (annotation.functionName.isNotBlank()) {
                        functionName = annotation.functionName
                    }
                }
                is EnumPreference -> {
                    defaultParameterType = GENERIC_ENUM
                    defaultParameterValue = null
                    valueParameterType = GENERIC_ENUM
                    getStatement = "return getEnum(\"$propValue\", default)"

                    putStatement = if (annotation.storeString) {
                        "putEnum(\"$propValue\", value, String::class)"
                    } else {
                        "putEnum(\"$propValue\", value, Int::class)"
                    }

                    if (annotation.functionName.isNotBlank()) {
                        functionName = annotation.functionName
                    }
                }
                is StringPreference -> {
                    defaultParameterType = STRING
                    defaultParameterValue = "\"${annotation.defaultValue}\""
                    valueParameterType = STRING
                    getStatement = "return getString(\"$propValue\", default) ?: default"
                    putStatement = "putString(\"$propValue\", value)"

                    if (annotation.functionName.isNotBlank()) {
                        functionName = annotation.functionName
                    }
                }
                is StringSetPreference -> {
                    defaultParameterType = SET.parameterizedBy(STRING)
                    defaultParameterValue = "setOf<String>()"
                    valueParameterType = SET.parameterizedBy(STRING)
                    getStatement = "return getStringSet(\"$propValue\", default) ?: default"
                    putStatement = "putStringSet(\"$propValue\", value)"

                    if (annotation.functionName.isNotBlank()) {
                        functionName = annotation.functionName
                    }
                }
                is JSONObjectPreference -> {
                    defaultParameterType = JSON_OBJECT
                    defaultParameterValue = "JSONObject()"
                    valueParameterType = JSON_OBJECT
                    getStatement = "return getJSONObject(\"$propValue\", default)"
                    putStatement = "putJSONObject(\"$propValue\", value)"

                    if (annotation.functionName.isNotBlank()) {
                        functionName = annotation.functionName
                    }
                }

                else -> throw Exception("Unknown annotation type $annotationClass")
            }

            val getPreference = FunSpec.builder("get$functionName")
                .receiver(SHARED_PREFERENCES)
            val putPreference = FunSpec.builder("put$functionName")
                .receiver(SHARED_PREFERENCES)
            val putPreferenceWithEditor = FunSpec.builder("put$functionName")
                .receiver(SHARED_PREFERENCES_EDITOR)
            val valueParameter = ParameterSpec
                .builder("value", valueParameterType)
                .build()
            val functions = mutableListOf<FunSpec>()

            if (isEnum) {
                functions.add(
                    // fun <E : Enum<E>> SharedPreferences.getPreference(): E
                    FunSpec.builder("get$functionName")
                        .receiver(SHARED_PREFERENCES)
                        .addModifiers(KModifier.INLINE)
                        .addTypeVariable(GENERIC_ENUM.copy(reified = true))
                        .returns(GENERIC_ENUM)
                        .addStatement("return getEnum(\"$propValue\")")
                        .build()
                )
                functions.add(
                    // fun SharedPreferences.putPreference(value: String, commit: Boolean = false)
                    FunSpec.builder("put$functionName")
                        .receiver(SHARED_PREFERENCES)
                        .addParameter(
                            ParameterSpec
                                .builder("value", STRING)
                                .build()
                        )
                        .addParameter(
                            ParameterSpec
                                .builder("commit", BOOLEAN)
                                .defaultValue("false")
                                .build()
                        )
                        .addStatement("edit(commit) { putString(\"$propValue\", value) }")
                        .build()
                )
                functions.add(
                    // fun SharedPreferences.Editor.putPreference(value: String): SharedPreferences.Editor
                    FunSpec.builder("put$functionName")
                        .receiver(SHARED_PREFERENCES_EDITOR)
                        .addParameter(
                            ParameterSpec
                                .builder("value", STRING)
                                .build()
                        )
                        .returns(SHARED_PREFERENCES_EDITOR)
                        .addStatement("return putString(\"$propValue\", value)")
                        .build()
                )
                functions.add(
                    // fun SharedPreferences.putPreference(value: Int, commit: Boolean = false)
                    FunSpec.builder("put$functionName")
                        .receiver(SHARED_PREFERENCES)
                        .addParameter(
                            ParameterSpec
                                .builder("value", INT)
                                .build()
                        )
                        .addParameter(
                            ParameterSpec
                                .builder("commit", BOOLEAN)
                                .defaultValue("false")
                                .build()
                        )
                        .addStatement("edit(commit) { putInt(\"$propValue\", value) }")
                        .build()
                )
                functions.add(
                    // fun SharedPreferences.Editor.putPreference(value: Int): SharedPreferences.Editor
                    FunSpec.builder("put$functionName")
                        .receiver(SHARED_PREFERENCES_EDITOR)
                        .addParameter(
                            ParameterSpec
                                .builder("value", INT)
                                .build()
                        )
                        .returns(SHARED_PREFERENCES_EDITOR)
                        .addStatement("return putInt(\"$propValue\", value)")
                        .build()
                )

                getPreference.addModifiers(KModifier.INLINE)
                getPreference.addTypeVariable(GENERIC_ENUM.copy(reified = true))
                putPreference.addTypeVariable(GENERIC_ENUM)
                putPreferenceWithEditor.addTypeVariable(GENERIC_ENUM)
            }

            if (defaultParameterType != null) {
                val defaultParameter = ParameterSpec
                    .builder("default", defaultParameterType)

                if (defaultParameterValue != null) {
                    defaultParameter.defaultValue(defaultParameterValue)
                }

                getPreference.addParameter(defaultParameter.build())
                getPreference.returns(defaultParameterType)
            }

            putPreference.addParameter(valueParameter)
            putPreferenceWithEditor.addParameter(valueParameter)
            getPreference.addStatement(getStatement)
            putPreference.addParameter(
                ParameterSpec
                    .builder("commit", BOOLEAN)
                    .defaultValue("false")
                    .build()
            )
            putPreference.addStatement("return edit(commit) { $putStatement }")
            putPreferenceWithEditor.addStatement("return $putStatement")
            putPreferenceWithEditor.returns(SHARED_PREFERENCES_EDITOR)

            functions.add(getPreference.build())
            functions.add(putPreference.build())
            functions.add(putPreferenceWithEditor.build())

            return functions
        } else {
            throw Exception("Field \"$propName\" must be a String and marked as STATIC and FINAL;")
        }
    }

    private fun snakeToPascalCase(fieldName: String): String {
        var result = fieldName.lowercase()
        val underscoreRegex = Regex("[_\\s]")

        for (word in underscoreRegex.findAll(result)) {
            val index = word.range.first + 1
            val range = index..index
            val char = result[index]

            result = result.replaceRange(range, char.uppercase())
        }

        return result.replace(underscoreRegex, "")
    }

    companion object {
        val SHARED_PREFERENCES = ClassName("android.content", "SharedPreferences")
        val JSON_OBJECT = ClassName("org.json", "JSONObject")
        val SHARED_PREFERENCES_EDITOR = ClassName("android.content", "SharedPreferences.Editor")
        val REQUIRED_MODIFIERS = listOf(
            Modifier.STATIC,
            Modifier.FINAL
        )
        val GENERIC_ENUM = "E".let {
            val e = TypeVariableName(it)    //<E>
            val enum = ENUM.parameterizedBy(e)  //Enum<E>

            TypeVariableName(it, enum)    //<E : Enum<E>>
        }
    }
}