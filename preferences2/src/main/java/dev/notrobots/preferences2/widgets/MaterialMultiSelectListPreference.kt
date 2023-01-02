package dev.notrobots.preferences2.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.preference.MultiSelectListPreference

/**
 * Material MultiSelectListPreference widget.
 *
 * This class does not override the behaviour of the MultiSelectListPreference class, it's only used
 * to avoid forcing the MaterialAlertDialog with the standard MultiSelectListPreference class.
 */
open class MaterialMultiSelectListPreference(
    context: Context,
    attrs: AttributeSet?
) : MultiSelectListPreference(context, attrs)