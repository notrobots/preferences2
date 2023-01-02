package dev.notrobots.preferences2.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.preference.SwitchPreferenceCompat
import dev.notrobots.preferences2.R

open class MaterialSwitchPreference(
    context: Context,
    attrs: AttributeSet?
) : SwitchPreferenceCompat(context, attrs) {
    init {
        widgetLayoutResource = R.layout.view_material_switch_preference
    }
}