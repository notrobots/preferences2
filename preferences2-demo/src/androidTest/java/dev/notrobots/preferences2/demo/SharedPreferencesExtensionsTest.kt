package dev.notrobots.preferences2.demo

import androidx.preference.PreferenceManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dev.notrobots.preferences2.extensions.getEnum
import dev.notrobots.preferences2.extensions.putEnum
import dev.notrobots.preferences2.util.parseEnum
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SharedPreferencesExtensionsTest {
    @Test
    fun testParseEnumWithIndex() {
        assert(parseEnum<Animal>(2) == Animal.Duck)
        assert(parseEnum<Animal>(3) == Animal.Otter)
    }

    @Test
    fun testParseEnumWithName() {
        assert(parseEnum<Animal>("Duck") == Animal.Duck)
        assert(parseEnum<Animal>("Otter") == Animal.Otter)
        assert(parseEnum<Animal>("cat", true) == Animal.Cat)
        assert(parseEnum<Animal>("OTTER", true) == Animal.Otter)
    }

    @Test
    fun testSharedPreferencesWithEnum() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val pref = PreferenceManager.getDefaultSharedPreferences(context)

        assert(pref.getEnum<Animal>("enum_test") == Animal.Dog)

        pref.edit().remove("enum_test").commit()
        assert(pref.getEnum("enum_test", Animal.Cat) == Animal.Cat)

        pref.putEnum("enum_test", Animal.Otter)
        assert(pref.getEnum("enum_test", Animal.Otter) == Animal.Otter)
    }

    private enum class Animal{
        Dog,
        Cat,
        Duck,
        Otter
    }
}