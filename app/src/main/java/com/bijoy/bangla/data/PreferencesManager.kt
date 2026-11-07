package com.bijoy.bangla.data

import android.content.Context
import android.content.SharedPreferences

enum class KeyboardTheme(val id: String, val title: String) {
    IOS_LIGHT("ios_light", "iOS Light"),
    IOS_DARK("ios_dark", "iOS Dark"),
    OLED_BLACK("oled_black", "OLED Black")
}

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("bijoy_bangla_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_THEME = "keyboard_theme"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_SOUND_VOLUME = "sound_volume"
        private const val KEY_VIBRATION_ENABLED = "vibration_enabled"
        private const val KEY_POPUP_ENABLED = "popup_enabled"
        private const val KEY_CURRENT_LAYOUT = "current_layout"
        private const val KEY_SMART_CHAT_MEMORY = "smart_chat_memory_enabled"
        private const val KEY_HISTORY_ENABLED = "history_logging_enabled"
        private const val KEY_DOUBLE_SPACE_PERIOD = "double_space_period"
        private const val KEY_AUTO_CAPITALIZATION = "auto_capitalization"
    }

    var theme: KeyboardTheme
        get() {
            val id = prefs.getString(KEY_THEME, KeyboardTheme.IOS_LIGHT.id)
            return KeyboardTheme.values().firstOrNull { it.id == id } ?: KeyboardTheme.IOS_LIGHT
        }
        set(value) = prefs.edit().putString(KEY_THEME, value.id).apply()

    var isSoundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND_ENABLED, value).apply()

    var soundVolume: Float
        get() = prefs.getFloat(KEY_SOUND_VOLUME, 0.7f)
        set(value) = prefs.edit().putFloat(KEY_SOUND_VOLUME, value).apply()

    var isVibrationEnabled: Boolean
        get() = prefs.getBoolean(KEY_VIBRATION_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_VIBRATION_ENABLED, value).apply()

    var isPopupEnabled: Boolean
        get() = prefs.getBoolean(KEY_POPUP_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_POPUP_ENABLED, value).apply()

    var currentLayoutId: String
        get() = prefs.getString(KEY_CURRENT_LAYOUT, "ENGLISH") ?: "ENGLISH"
        set(value) = prefs.edit().putString(KEY_CURRENT_LAYOUT, value).apply()

    var isSmartChatMemoryEnabled: Boolean
        get() = prefs.getBoolean(KEY_SMART_CHAT_MEMORY, true)
        set(value) = prefs.edit().putBoolean(KEY_SMART_CHAT_MEMORY, value).apply()

    var isHistoryEnabled: Boolean
        get() = prefs.getBoolean(KEY_HISTORY_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_HISTORY_ENABLED, value).apply()

    var isDoubleSpacePeriodEnabled: Boolean
        get() = prefs.getBoolean(KEY_DOUBLE_SPACE_PERIOD, true)
        set(value) = prefs.edit().putBoolean(KEY_DOUBLE_SPACE_PERIOD, value).apply()

    var isAutoCapitalizationEnabled: Boolean
        get() = prefs.getBoolean(KEY_AUTO_CAPITALIZATION, true)
        set(value) = prefs.edit().putBoolean(KEY_AUTO_CAPITALIZATION, value).apply()
}
