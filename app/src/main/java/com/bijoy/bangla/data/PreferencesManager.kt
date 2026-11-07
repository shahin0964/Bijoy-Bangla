package com.bijoy.bangla.data

import android.content.Context
import android.content.SharedPreferences

enum class KeyboardTheme(val id: String, val title: String) {
    DYNAMIC("dynamic", "Dynamic"),
    DARK_GLOW("dark_glow", "Dark Glow"),
    NATURE("nature", "Nature"),
    SUNSET("sunset", "Sunset"),
    OCEAN("ocean", "Ocean"),
    NEON("neon", "Neon"),
    MINIMAL("minimal", "Minimal"),
    LIGHT("light", "Light"),
    GLASS("glass", "Glass"),
    // Backward compatibility aliases
    IOS_LIGHT("light", "Light"),
    IOS_DARK("dark_glow", "Dark Glow"),
    OLED_BLACK("minimal", "Minimal");

    companion object {
        fun fromId(id: String?): KeyboardTheme {
            return values().firstOrNull { it.id.equals(id, ignoreCase = true) || it.name.equals(id, ignoreCase = true) }
                ?: DYNAMIC
        }
    }
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
        private const val KEY_SHOW_NUMBER_ROW = "show_number_row"
        private const val KEY_ONE_HAND_MODE = "one_hand_mode"
        private const val KEY_ONE_HAND_SIDE = "one_hand_side"
        private const val KEY_KEY_SIZE = "key_size"
        private const val KEY_AUTO_CORRECTION = "auto_correction"
        private const val KEY_SUGGEST_NEXT_WORD = "suggest_next_word"
        private const val KEY_EMOJI_PANEL = "emoji_panel"
        private const val KEY_CLIPBOARD = "clipboard_enabled"
        private const val KEY_THEME_TRANSPARENCY = "theme_transparency"
        private const val KEY_BLUR_EFFECT = "blur_effect"
    }

    var theme: KeyboardTheme
        get() = KeyboardTheme.fromId(prefs.getString(KEY_THEME, KeyboardTheme.DYNAMIC.id))
        set(value) = prefs.edit().putString(KEY_THEME, value.id).apply()

    var isSoundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND_ENABLED, false)
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

    var isShowNumberRow: Boolean
        get() = prefs.getBoolean(KEY_SHOW_NUMBER_ROW, false)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_NUMBER_ROW, value).apply()

    var isOneHandMode: Boolean
        get() = prefs.getBoolean(KEY_ONE_HAND_MODE, false)
        set(value) = prefs.edit().putBoolean(KEY_ONE_HAND_MODE, value).apply()

    var oneHandSide: String
        get() = prefs.getString(KEY_ONE_HAND_SIDE, "RIGHT") ?: "RIGHT"
        set(value) = prefs.edit().putString(KEY_ONE_HAND_SIDE, value).apply()

    var keySize: String
        get() = prefs.getString(KEY_KEY_SIZE, "Normal") ?: "Normal"
        set(value) = prefs.edit().putString(KEY_KEY_SIZE, value).apply()

    var isAutoCorrectionEnabled: Boolean
        get() = prefs.getBoolean(KEY_AUTO_CORRECTION, true)
        set(value) = prefs.edit().putBoolean(KEY_AUTO_CORRECTION, value).apply()

    var isSuggestNextWordEnabled: Boolean
        get() = prefs.getBoolean(KEY_SUGGEST_NEXT_WORD, true)
        set(value) = prefs.edit().putBoolean(KEY_SUGGEST_NEXT_WORD, value).apply()

    var isEmojiPanelEnabled: Boolean
        get() = prefs.getBoolean(KEY_EMOJI_PANEL, true)
        set(value) = prefs.edit().putBoolean(KEY_EMOJI_PANEL, value).apply()

    var isClipboardEnabled: Boolean
        get() = prefs.getBoolean(KEY_CLIPBOARD, true)
        set(value) = prefs.edit().putBoolean(KEY_CLIPBOARD, value).apply()

    var themeTransparency: Float
        get() = prefs.getFloat(KEY_THEME_TRANSPARENCY, 0.80f)
        set(value) = prefs.edit().putFloat(KEY_THEME_TRANSPARENCY, value).apply()

    var isBlurEffectEnabled: Boolean
        get() = prefs.getBoolean(KEY_BLUR_EFFECT, true)
        set(value) = prefs.edit().putBoolean(KEY_BLUR_EFFECT, value).apply()

    fun resetToDefaults() {
        prefs.edit().clear().apply()
        theme = KeyboardTheme.DYNAMIC
        isPopupEnabled = true
        isVibrationEnabled = true
        isSoundEnabled = false
        isShowNumberRow = false
        isOneHandMode = false
        keySize = "Normal"
        isAutoCorrectionEnabled = true
        isSuggestNextWordEnabled = true
        isAutoCapitalizationEnabled = true
        isEmojiPanelEnabled = true
        isClipboardEnabled = true
        isSmartChatMemoryEnabled = true
        isHistoryEnabled = true
        themeTransparency = 0.80f
        isBlurEffectEnabled = true
    }
}
