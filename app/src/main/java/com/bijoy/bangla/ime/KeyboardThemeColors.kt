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
    val candidateText: Color,
    val accentGlow: Color = Color(0xFF00E5FF)
)

object ThemePalettes {

    // 1. Dynamic (Default Futuristic Cyberpunk from screenshot)
    val Dynamic = KeyboardThemeColors(
        background = Color(0xFF0A0E1F),
        keyBackground = Color(0xFF151C36),
        keyShadow = Color(0xFF050812),
        specialKeyBackground = Color(0xFF1E284D),
        specialKeyShadow = Color(0xFF050812),
        actionKeyBackground = Color(0xFF7B2FFE),
        actionKeyText = Color(0xFFFFFFFF),
        keyTextColor = Color(0xFFFFFFFF),
        secondaryTextColor = Color(0xFF8E99B7),
        popupBackground = Color(0xFF1A2346),
        accessoryBackground = Color(0xFF0F152E),
        candidateText = Color(0xFF00E5FF),
        accentGlow = Color(0xFF00E5FF)
    )

    // 2. Dark Glow
    val DarkGlow = KeyboardThemeColors(
        background = Color(0xFF060914),
        keyBackground = Color(0xFF10162B),
        keyShadow = Color(0xFF02040A),
        specialKeyBackground = Color(0xFF161E3B),
        specialKeyShadow = Color(0xFF02040A),
        actionKeyBackground = Color(0xFF00C6FF),
        actionKeyText = Color(0xFFFFFFFF),
        keyTextColor = Color(0xFFFFFFFF),
        secondaryTextColor = Color(0xFF7A88A8),
        popupBackground = Color(0xFF141C38),
        accessoryBackground = Color(0xFF0A0E20),
        candidateText = Color(0xFF00C6FF),
        accentGlow = Color(0xFF00B4D8)
    )

    // 3. Nature
    val Nature = KeyboardThemeColors(
        background = Color(0xFF07140B),
        keyBackground = Color(0xFF0F2516),
        keyShadow = Color(0xFF030A05),
        specialKeyBackground = Color(0xFF173522),
        specialKeyShadow = Color(0xFF030A05),
        actionKeyBackground = Color(0xFF00C853),
        actionKeyText = Color(0xFFFFFFFF),
        keyTextColor = Color(0xFFFFFFFF),
        secondaryTextColor = Color(0xFF81C784),
        popupBackground = Color(0xFF132E1C),
        accessoryBackground = Color(0xFF0A1B10),
        candidateText = Color(0xFF69F0AE),
        accentGlow = Color(0xFF00E676)
    )

    // 4. Sunset
    val Sunset = KeyboardThemeColors(
        background = Color(0xFF170C1A),
        keyBackground = Color(0xFF2C1633),
        keyShadow = Color(0xFF0A050B),
        specialKeyBackground = Color(0xFF3B1D45),
        specialKeyShadow = Color(0xFF0A050B),
        actionKeyBackground = Color(0xFFFF5722),
        actionKeyText = Color(0xFFFFFFFF),
        keyTextColor = Color(0xFFFFFFFF),
        secondaryTextColor = Color(0xFFFFAB91),
        popupBackground = Color(0xFF381B41),
        accessoryBackground = Color(0xFF200F25),
        candidateText = Color(0xFFFF8A65),
        accentGlow = Color(0xFFFF7043)
    )

    // 5. Ocean
    val Ocean = KeyboardThemeColors(
        background = Color(0xFF05111F),
        keyBackground = Color(0xFF0C243F),
        keyShadow = Color(0xFF02070D),
        specialKeyBackground = Color(0xFF123257),
        specialKeyShadow = Color(0xFF02070D),
        actionKeyBackground = Color(0xFF0091EA),
        actionKeyText = Color(0xFFFFFFFF),
        keyTextColor = Color(0xFFFFFFFF),
        secondaryTextColor = Color(0xFF80D8FF),
        popupBackground = Color(0xFF0F2B4B),
        accessoryBackground = Color(0xFF08192E),
        candidateText = Color(0xFF40C4FF),
        accentGlow = Color(0xFF00B0FF)
    )

    // 6. Neon
    val Neon = KeyboardThemeColors(
        background = Color(0xFF130722),
        keyBackground = Color(0xFF230F3E),
        keyShadow = Color(0xFF08020E),
        specialKeyBackground = Color(0xFF33165A),
        specialKeyShadow = Color(0xFF08020E),
        actionKeyBackground = Color(0xFFE040FB),
        actionKeyText = Color(0xFFFFFFFF),
        keyTextColor = Color(0xFFFFFFFF),
        secondaryTextColor = Color(0xFFEA80FC),
        popupBackground = Color(0xFF2D1350),
        accessoryBackground = Color(0xFF1A0A30),
        candidateText = Color(0xFFF50057),
        accentGlow = Color(0xFFE040FB)
    )

    // 7. Minimal
    val Minimal = KeyboardThemeColors(
        background = Color(0xFF121418),
        keyBackground = Color(0xFF1E222A),
        keyShadow = Color(0xFF0B0D10),
        specialKeyBackground = Color(0xFF292E39),
        specialKeyShadow = Color(0xFF0B0D10),
        actionKeyBackground = Color(0xFF3F51B5),
        actionKeyText = Color(0xFFFFFFFF),
        keyTextColor = Color(0xFFECEFF1),
        secondaryTextColor = Color(0xFF90A4AE),
        popupBackground = Color(0xFF252A34),
        accessoryBackground = Color(0xFF181B21),
        candidateText = Color(0xFF9FA8DA),
        accentGlow = Color(0xFF7986CB)
    )

    // 8. Light
    val Light = KeyboardThemeColors(
        background = Color(0xFFE9ECF2),
        keyBackground = Color(0xFFFFFFFF),
        keyShadow = Color(0xFFBCC2CD),
        specialKeyBackground = Color(0xFFD6DBE5),
        specialKeyShadow = Color(0xFFBCC2CD),
        actionKeyBackground = Color(0xFF007AFF),
        actionKeyText = Color(0xFFFFFFFF),
        keyTextColor = Color(0xFF1F2937),
        secondaryTextColor = Color(0xFF6B7280),
        popupBackground = Color(0xFFFFFFFF),
        accessoryBackground = Color(0xFFF3F5F9),
        candidateText = Color(0xFF007AFF),
        accentGlow = Color(0xFF007AFF)
    )

    // 9. Glass
    val Glass = KeyboardThemeColors(
        background = Color(0xFF0E1324),
        keyBackground = Color(0xFF1A2340).copy(alpha = 0.85f),
        keyShadow = Color(0xFF060914),
        specialKeyBackground = Color(0xFF243057).copy(alpha = 0.85f),
        specialKeyShadow = Color(0xFF060914),
        actionKeyBackground = Color(0xFF7B2FFE),
        actionKeyText = Color(0xFFFFFFFF),
        keyTextColor = Color(0xFFFFFFFF),
        secondaryTextColor = Color(0xFFA0AEC0),
        popupBackground = Color(0xFF1F2B4E),
        accessoryBackground = Color(0xFF131A32),
        candidateText = Color(0xFF9D4EDD),
        accentGlow = Color(0xFF7B2FFE)
    )

    fun get(theme: KeyboardTheme): KeyboardThemeColors {
        return when (theme) {
            KeyboardTheme.DYNAMIC -> Dynamic
            KeyboardTheme.DARK_GLOW -> DarkGlow
            KeyboardTheme.NATURE -> Nature
            KeyboardTheme.SUNSET -> Sunset
            KeyboardTheme.OCEAN -> Ocean
            KeyboardTheme.NEON -> Neon
            KeyboardTheme.MINIMAL -> Minimal
            KeyboardTheme.LIGHT -> Light
            KeyboardTheme.GLASS -> Glass
            KeyboardTheme.IOS_LIGHT -> Light
            KeyboardTheme.IOS_DARK -> DarkGlow
            KeyboardTheme.OLED_BLACK -> Minimal
        }
    }
}
