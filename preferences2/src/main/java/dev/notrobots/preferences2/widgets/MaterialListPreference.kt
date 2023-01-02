package dev.notrobots.preferences2.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.preference.ListPreference

/**
 * Material ListPreference widget.
 *
 * This class does not override the behaviour of the ListPreference class, it's only used
 * to avoid forcing the MaterialAlertDialog with the standard ListPreference class.
 */
open class MaterialListPreference(
    context: Context,
    attrs: AttributeSet?
) : ListPreference(context, attrs)