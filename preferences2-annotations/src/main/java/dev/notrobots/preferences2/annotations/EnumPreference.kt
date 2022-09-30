package dev.notrobots.preferences2.annotations

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class EnumPreference(
    /**
     * Whether the enum value will be saved as a String or an Int.
     *
     * The value will be saved as a String by default.
     */
    val storeString: Boolean = true,
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