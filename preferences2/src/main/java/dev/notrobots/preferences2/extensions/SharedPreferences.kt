package dev.notrobots.preferences2.extensions

import android.annotation.SuppressLint
import android.content.SharedPreferences
import dev.notrobots.preferences2.util.parseEnum
import org.json.JSONObject
import kotlin.reflect.KClass

/**
 * Retrieve an Enum value from the preferences.
 *
 * Value can be stored as either an integer which represents the enum value's index; or a
 * string which represents the enum value's name.
 *
 * Note: The string case will be ignored
 *
 * @param name The name of the preference to retrieve.
 *
 * @return Returns the preference value if it exists, or the first value.
 */
inline fun <reified E : Enum<E>> SharedPreferences.getEnum(name: String): E {
    val values = E::class.java.enumConstants!!

    require(values.isNotEmpty()) {
        "Enum type ${E::class} has no values"
    }

    return getEnum(name, values[0])
}

/**
 * Retrieve an Enum value from the preferences.
 *
 * Value can be stored as either an integer which represents the enum value's index; or a
 * string which represents the enum value's name.
 *
 * Note: The string case will be ignored
 *
 * @param name The name of the preference to retrieve.
 * @param default Value to return if this preference does not exist.
 *
 * @return Returns the preference value if it exists, or [default].
 */
inline fun <reified E : Enum<E>> SharedPreferences.getEnum(name: String, default: E): E {
    val keys = all
    val value = keys[name]
    val exists = keys.contains(name)

    return if (!exists) {
        default
    } else if (value is Int) {
        parseEnum(value)
    } else if (value is String) {
        parseEnum(value, true)
    } else {
        throw ClassCastException("Preference value must be either Int or String")
    }
}

/**
 * Retrieve an Enum value from the preferences.
 *
 * Value can be stored as either an integer which represents the enum value's index; or a
 * string which represents the enum value's name.
 *
 * Note: The string case will be ignored
 *
 * @param name The name of the preference to retrieve.
 * @param default Value to return if this preference does not exist.
 *
 * @return Returns the preference value if it exists, or [default].
 */
inline fun <reified E : Enum<E>> SharedPreferences.getEnum(name: String, default: String): E {
    return getEnum(name, parseEnum<E>(default))
}

/**
 * Retrieve an Enum value from the preferences.
 *
 * Value can be stored as either an integer which represents the enum value's index; or a
 * string which represents the enum value's name.
 *
 * Note: The string case will be ignored
 *
 * @param name The name of the preference to retrieve.
 * @param default Value to return if this preference does not exist.
 *
 * @return Returns the preference value if it exists, or [default].
 */
inline fun <reified E : Enum<E>> SharedPreferences.getEnum(name: String, default: Int): E {
    return getEnum(name, parseEnum<E>(default))
}

/**
 * Set an enum value in the preferences editor, to be written back once commit() or apply() are called.
 *
 * @param name The name of the preference to modify.
 * @param value The new value for the preference.
 * @param storedType The type that will be stored, String by default
 */
fun <E : Enum<E>> SharedPreferences.putEnum(
    name: String,
    value: E,
    storedType: KClass<*> = String::class
) {
    edit { putEnum(name, value, storedType) }
}

/**
 * Set an enum value in the preferences editor, to be written back once commit() or apply() are called.
 *
 * @param name The name of the preference to modify.
 * @param value The new value for the preference.
 * @param storedType The type that will be stored, String by default
 */
fun <E : Enum<E>> SharedPreferences.Editor.putEnum(
    name: String,
    value: E,
    storedType: KClass<*> = String::class
): SharedPreferences.Editor {
    return when (storedType) {
        String::class -> putString(name, value.toString())
        Int::class -> putInt(name, value.ordinal)

        else -> throw Exception("Stored type must be either String or Int")
    }
}

/**
 * Set an enum value in the preferences editor, to be written back once commit() or apply() are called.
 *
 * @param name The name of the preference to modify.
 * @param value The new value for the preference.
 */
fun SharedPreferences.putEnum(
    name: String,
    value: String
) {
    edit { putString(name, value) }
}

/**
 * Set an enum value in the preferences editor, to be written back once commit() or apply() are called.
 *
 * @param name The name of the preference to modify.
 * @param value The new value for the preference.
 */
fun SharedPreferences.Editor.putEnum(
    name: String,
    value: String
): SharedPreferences.Editor {
    return putString(name, value)
}

/**
 * Set an enum value in the preferences editor, to be written back once commit() or apply() are called.
 *
 * @param name The name of the preference to modify.
 * @param value The new value for the preference.
 */
fun SharedPreferences.putEnum(
    name: String,
    value: Int
) {
    edit { putInt(name, value) }
}

/**
 * Set an enum value in the preferences editor, to be written back once commit() or apply() are called.
 *
 * @param name The name of the preference to modify.
 * @param value The new value for the preference.
 */
fun SharedPreferences.Editor.putEnum(
    name: String,
    value: Int
): SharedPreferences.Editor {
    return putInt(name, value)
}

/**
 * Retrieve a JSONObject value from the preferences.
 *
 * The value is stored as a string and can also be retrieved using `getString`.
 *
 * @param name The name of the preference to retrieve.
 * @param default Value to return if this preference does not exist.
 */
fun SharedPreferences.getJSONObject(name: String, default: JSONObject = JSONObject()): JSONObject {
    val v = getString(name, null)

    return if (v != null) JSONObject(v) else default
}

/**
 * Set a JSONObject value in the preferences editor, to be written back once commit() or apply() are called.
 *
 * The value is stored as a string and can also be set using `putString`.
 *
 * @param name The name of the preference to modify.
 * @param value The new value for the preference.
 */
fun SharedPreferences.putJSONObject(name: String, value: JSONObject) {
    return edit { putJSONObject(name, value) }
}

/**
 * Set a JSONObject value in the preferences editor, to be written back once commit() or apply() are called.
 *
 * The value is stored as a string and can also be set using `putString`.
 *
 * @param name The name of the preference to modify.
 * @param value The new value for the preference.
 */
fun SharedPreferences.Editor.putJSONObject(name: String, value: JSONObject): SharedPreferences.Editor {
    return putString(name, value.toString(0))
}

@SuppressLint("ApplySharedPref")
inline fun SharedPreferences.edit(
    commit: Boolean = false,
    action: SharedPreferences.Editor.() -> Unit
) {
    edit().also(action).also {
        if (commit) {
            it.commit()
        } else {
            it.apply()
        }
    }
}