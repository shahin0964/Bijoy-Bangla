package com.bijoy.bangla.ime

import androidx.compose.ui.graphics.Color
import com.bijoy.bangla.data.KeyboardTheme

data class KeyboardThemeColors(
    val background: Color,
    val keyBackground: Color,
    val keyShadow: Color,
    val specialKeyBackground: Color,
    val specialKeyShadow: Color,
    val actionKeyBackground: Color,
    val actionKeyText: Color,
    val keyTextColor: Color,
    val secondaryTextColor: Color,
    val popupBackground: Color,
    val accessoryBackground: Color,
    val candidateText: Color
)

object ThemePalettes {
    val Light = KeyboardThemeColors(
        background = Color(0xFFD1D5DC),
        keyBackground = Color(0xFFFFFFFF),
        keyShadow = Color(0xFF898A8D),
        specialKeyBackground = Color(0xFFACB3BA),
        specialKeyShadow = Color(0xFF898A8D),
        actionKeyBackground = Color(0xFF007AFF),
        actionKeyText = Color(0xFFFFFFFF),
        keyTextColor = Color(0xFF000000),
        secondaryTextColor = Color(0xFF6B7280),
        popupBackground = Color(0xFFFFFFFF),
        accessoryBackground = Color(0xFFE5E7EB),
        candidateText = Color(0xFF1F2937)
    )

    val Dark = KeyboardThemeColors(
        background = Color(0xFF1B1B1E),
        keyBackground = Color(0xFF4A4A4C),
        keyShadow = Color(0xFF1F1F21),
        specialKeyBackground = Color(0xFF2C2C2E),
        specialKeyShadow = Color(0xFF1F1F21),
        actionKeyBackground = Color(0xFF007AFF),
        actionKeyText = Color(0xFFFFFFFF),
        keyTextColor = Color(0xFFFFFFFF),
        secondaryTextColor = Color(0xFF9CA3AF),
        popupBackground = Color(0xFF545458),
        accessoryBackground = Color(0xFF242426),
        candidateText = Color(0xFFE5E7EB)
    )

    val Oled = KeyboardThemeColors(
        background = Color(0xFF000000),
        keyBackground = Color(0xFF262628),
        keyShadow = Color(0xFF0D0D0D),
        specialKeyBackground = Color(0xFF18181A),
        specialKeyShadow = Color(0xFF0D0D0D),
        actionKeyBackground = Color(0xFF007AFF),
        actionKeyText = Color(0xFFFFFFFF),
        keyTextColor = Color(0xFFFFFFFF),
        secondaryTextColor = Color(0xFF8E8E93),
        popupBackground = Color(0xFF3A3A3C),
        accessoryBackground = Color(0xFF121214),
        candidateText = Color(0xFFFFFFFF)
    )

    fun get(theme: KeyboardTheme): KeyboardThemeColors {
        return when (theme) {
            KeyboardTheme.IOS_LIGHT -> Light
            KeyboardTheme.IOS_DARK -> Dark
            KeyboardTheme.OLED_BLACK -> Oled
        }
    }
}
