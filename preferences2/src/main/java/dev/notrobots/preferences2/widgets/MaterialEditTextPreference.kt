package dev.notrobots.preferences2.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.preference.EditTextPreference

/**
 * Material EditTextPreference widget.
 *
 * This class does not override the behaviour of the EditTextPreference class, it's only used
 * to avoid forcing the MaterialAlertDialog with the standard EditTextPreference class.
 */
open class MaterialEditTextPreference(
    context: Context,
    attrs: AttributeSet?
) : EditTextPreference(context, attrs)