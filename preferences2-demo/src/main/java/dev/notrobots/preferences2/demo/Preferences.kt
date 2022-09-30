package dev.notrobots.preferences2.demo

import dev.notrobots.preferences2.annotations.*

@BooleanPreference(true)
const val BOOLEAN_TEST = "boolean_test"
@StringPreference
const val STRING_TEST = "string_test"
@EnumPreference
const val ENUM_TEST = "enum_test"
@FloatPreference(5.05F)
const val FLOAT_TEST = "float_test"
@IntPreference(500)
const val INT_TEST = "int_test"
@LongPreference(600)
const val LONG_TEST = "long_test"
@StringSetPreference
const val STRING_SET_TEST = "string_set_test"
@JSONObjectPreference
const val JSON_TEST = "json_test"