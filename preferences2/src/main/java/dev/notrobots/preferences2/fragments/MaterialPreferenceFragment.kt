package dev.notrobots.preferences2.fragments

import android.os.Bundle
import androidx.preference.*
import androidx.preference.PreferenceFragmentCompat
import dev.notrobots.preferences2.dialogs.MaterialEditTextPreferenceDialog
import dev.notrobots.preferences2.dialogs.MaterialListPreferenceDialog
import dev.notrobots.preferences2.dialogs.MaterialMultiSelectListPreferenceDialog
import dev.notrobots.preferences2.widgets.MaterialEditTextPreference
import dev.notrobots.preferences2.widgets.MaterialListPreference
import dev.notrobots.preferences2.widgets.MaterialMultiSelectListPreference
import kotlin.reflect.KParameter

/**
 * Base PreferenceFragment class that handles the material preference widgets.
 */
abstract class MaterialPreferenceFragment : PreferenceFragmentCompat() {
    override fun onDisplayPreferenceDialog(preference: Preference) {
        when (preference) {
            is MaterialEditTextPreference -> createAndShowDialogFragment<MaterialEditTextPreferenceDialog>(preference.key, this)
            is MaterialMultiSelectListPreference ->  createAndShowDialogFragment<MaterialMultiSelectListPreferenceDialog>(preference.key, this)
            is MaterialListPreference -> createAndShowDialogFragment<MaterialListPreferenceDialog>(preference.key, this)

            else -> super.onDisplayPreferenceDialog(preference)
        }
    }

    private inline fun <reified T : PreferenceDialogFragmentCompat> createAndShowDialogFragment(key: String, parent: PreferenceFragmentCompat): T {
        val type = T::class
        val noArgsConstructor = type.constructors.singleOrNull {
            it.parameters.all(KParameter::isOptional)
        } ?: throw IllegalArgumentException("Class should have a single no-arg constructor: $type")
        val instance = noArgsConstructor.call()
        val bundle = Bundle(1).apply {
            putString("key", key)
        }

        instance.arguments = bundle
        instance.setTargetFragment(parent, 0)
        instance.show(parent.parentFragmentManager, "androidx.preference.PreferenceFragment.DIALOG")

        return instance
    }
}