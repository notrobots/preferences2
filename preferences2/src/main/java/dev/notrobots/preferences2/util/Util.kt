package dev.notrobots.preferences2.util

/**
 * Parses the given CharSequence into the specified Enum type.
 *
 * @param value Values to be parsed
 * @param ignoreCase True to ignore the case, false otherwise
 */
inline fun <reified E : Enum<E>> parseEnum(value: CharSequence?, ignoreCase: Boolean = false): E {
    val values = E::class.java.enumConstants!!

    require(values.isNotEmpty()) {
        "Enum type ${E::class} has no values"
    }

    return values.find {
        it.name.equals(value.toString(), ignoreCase)
    } ?: throw Exception("Enum type ${E::class} has no value \"$value\"")
}

/**
 * Parses the given Int into the specified Enum type.
 *
 * @param value Values to be parsed
 */
inline fun <reified E : Enum<E>> parseEnum(value: Int): E {
    val values = E::class.java.enumConstants!!

    require(values.isNotEmpty()) {
        "Enum type ${E::class} has no values"
    }

    require(value > 0 && value < values.size) {
        "Index out of range (0..${values.size}"
    }

    return values[value]
}