package dev.notrobots.preferences2.annotations

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class JSONObjectPreference(
    /**
     * The name that will be used in the generated functions.
     *
     * The functions will have a name like:
     * + get(functionName)
     * + put(functionName)
     *
     * By default the processor will take the annotated field's name and
     * convert it into camelcase and capitalize it.
     */
    val functionName: String = ""
)