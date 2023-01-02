# Preferences 2

An extension of Android's SharedPreferences classes.

## Getting started

In your project's build.gradle

```gradle
repositories {
	maven { url "https://jitpack.io" }
}
```

In your module's build.gradle

```gradle
plugins {
    id 'kotlin-kapt'
}

...

dependencies {
    var pref2 = 'com.github.notrobots.preferences2'

    implementation "$pref2:preferences2:$version"
    implementation "$pref2:preferences2-annotations:$version"
    kapt "$pref2:preferences2-processor:$version"
}
```

You can find all the versions [here](https://github.com/notrobots/preferences2/tags) 

## Defining a preference entry

Define a preference key using a static final string in any of the following ways:

```kotlin
// Object
object Preferences {
    @BooleanPreference
    const val MY_BOOLEAN_PREF = "boolean_pref_key"
}

// Top level delcaration
@BooleanPreference
const val MY_BOOLEAN_PREF = "boolean_pref_key"

// Companion object
class PrefTest1 {
    companion object {
        @BooleanPreference
        const val MY_BOOLEAN_PREF = "boolean_pref_key"
        
        // or
        
        @BooleanPreference
        val MY_BOOLEAN_PREF = "boolean_pref_key"
    }
}
```

For each annotated field 3 functions are generated, in the above example the functions would be:

```kotlin
public fun SharedPreferences.getMyBooleanPref(default: Boolean = false): Boolean {
    return getBoolean("boolean_pref_key", default)
}

public fun SharedPreferences.putMyBooleanPref(value: Boolean, commit: Boolean = false) {
    edit(commit) { 
        putBoolean("boolean_pref_key", value) 
    }
}

public fun SharedPreferences.Editor.putMyBooleanPref(value: Boolean): SharedPreferences.Editor {
    return putBoolean("boolean_pref_key", value)
}
```

You can customize the default value either by calling the getter and passing in the value or setting the `defaultValue` field in the annotaton.

```kotlin
@BooleanPreference(defaultValue = true)
const val MY_BOOLEAN_PREF = "boolean_pref_key"

// Will generate:
public fun SharedPreferences.getMyBooleanPref(default: Boolean = true): Boolean {
    return getBoolean("boolean_pref_key", default)
}
```

You can also customize the generated function names by setting the `functionaName` field's value in the annotation.

```kotlin
@BooleanPreference(defaultValue = true, functionName = "DifferentName")
const val MY_BOOLEAN_PREF = "boolean_pref_key"

// Will generate:
public fun SharedPreferences.getDifferentName(default: Boolean = true): Boolean {
    return getBoolean("boolean_pref_key", default)
}
```

Note that the defined function name will be used as is, it is recommended to start the name with an uppercase letter.

## Material 3 Preferences

You can use Material 3's styled preferences by using the MaterialPreferenceFragment class for your fragments and by using the provided material widgets.

Replace PreferenceFragmentCompat with MaterialPreferenceFragment in your fragment's class:

```kotlin
class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, SettingsFragment())
            .commit()
    }
    
    class SettingsFragment : MaterialPreferenceFragment() { // <--- 
        
    }
}
```

Replace the standard preference widgets with the material ones:

```xml
<!-- Replaces ListPreference -->
<MaterialListPreference
    android:title="Material List Preference"
    android:key="material_list"
    android:entryValues="@array/my_values"
    app:entries="@array/my_entries"/>

<!-- Replaces MultiSelectListPreference -->
<dev.notrobots.preferences2.widgets.MaterialMultiSelectListPreference
    android:entries="@array/entries"
    android:key="material_multi_select_list"
    android:title="MaterialMultiSelectList"
    app:entryValues="@array/values" />

<!-- Replaces EditTextPreference -->
<dev.notrobots.preferences2.widgets.MaterialEditTextPreference
    android:title="Material EditText"
    android:key="material_edit_text"/>
    
<!-- Replaces SwitchPreferenceCompat -->
<dev.notrobots.preferences2.widgets.MaterialSwitchPreference
    android:title="Material Switch"
    android:key="material_switch"/>
```

## Extensions

The library provides a few new functions that can be used to store more types.

#### Enum types

```kotlin
// get
prefs.getEnum<Animal>("animal", Animal.Cat)     // Value or Animal.Cat if not defined
prefs.getEnum<Animal>("animal")                 // Value or first value if not defined

// put
prefs.putEnum("animal", Animal.Duck)
```

You can also decide how your enum is stored, either as a String or Int.

```kotlin
// The stored value will be the enum value's inex
prefs.putEnum("animal", Animal.Duck, storedType = Int::class)

// The stored value will be the enum value's name (default)
prefs.putEnum("animal", Animal.Duck, storedType = String::class)
```

The getter will automatically detect which type was stored.

#### JSON objects (using org.json)

```kotlin
val json = JSONObject(mapOf(
    "value" to "Hello",
    "name" to "world"
))
val default = JSONObject(mapOf(
    "value" to "",
    "name" to ""
))

prefs.putJSONObject("json", json)
prefs.getJSONObject("json", default)
```

## Utilities

The library also provides two utilities that can parse values.

```kotlin
enum class Animal{
    Dog,
    Cat,
    Duck,
    Otter
}

parseEnum<Animal>("Dog")                       // Animal.Dog
parseEnum<Animal>("DOG", ignoreCase = true)    // Animal.Dog
parseEnum<Animal>(0)                           // Animal.Dog
```
