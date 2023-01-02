package dev.notrobots.preferences2.demo

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import dev.notrobots.preferences2.*
import dev.notrobots.preferences2.fragments.MaterialPreferenceFragment
import org.json.JSONObject

private const val APP_TAG = "Preferences2"

class SharedPreferencesTest : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)



        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val testSet1 = setOf("1", "2", "3")
        val testSet2 = setOf("3", "4", "5")
        val testJson1 = JSONObject(mapOf("test" to 10))
        val testJson2 = JSONObject(mapOf("hello" to "hola"))

        pref.edit().clear().commit()

        try {
            // Refer to Preferences.kt for the default values
            require(pref.getStringTest("None") == "None")
            pref.putStringTest("Hi", true)
            require(pref.getStringTest() == "Hi")

            require(pref.getIntTest() == 500)
            pref.putIntTest(101, true)
            require(pref.getIntTest() == 101)

            require(pref.getLongTest() == 600L)
            pref.putLongTest(101L, true)
            require(pref.getLongTest() == 101L)

            require(pref.getFloatTest() == 5.05F)
            pref.putFloatTest(10.10F, true)
            require(pref.getFloatTest() == 10.10F)

            require(pref.getEnumTest(Values.First) == Values.First)
            pref.putEnumTest(Values.Fifth, true)
            require(pref.getEnumTest(Values.First) == Values.Fifth)

            require(pref.getBooleanTest())
            pref.putBooleanTest(false, true)
            require(!pref.getBooleanTest())

            require(pref.getStringSetTest(testSet1).containsAll(testSet1))
            pref.putStringSetTest(testSet2, true)
            require(pref.getStringSetTest().containsAll(testSet2))

            require(pref.getJsonTest(testJson1)["test"] == 10)
            pref.putJsonTest(testJson2, true)
            require(pref.getJsonTest()["hello"] == "hola")

            Log.d(APP_TAG, "ALL TEST PASSED")
        } catch (e: Exception) {
            Log.d(APP_TAG, "TEST FAILED: ${e.message}")
            Log.d(APP_TAG, e.stackTraceToString())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_test, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_settings_m2 -> supportFragmentManager.beginTransaction()
                .replace(R.id.container, Material2PreferenceFragment())
                .commit()

            R.id.menu_settings_m3 -> supportFragmentManager.beginTransaction()
                .replace(R.id.container, Material3PreferenceFragment())
                .commit()

            else -> return false
        }

        return true
    }

    class Material2PreferenceFragment : MaterialPreferenceFragment() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.settings_m2)
        }
    }

    class Material3PreferenceFragment : MaterialPreferenceFragment() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.settings_m3)
        }
    }
}