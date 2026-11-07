package com.bijoy.bangla.ime

import android.content.Intent
import android.speech.RecognizerIntent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bijoy.bangla.BijoyApp
import com.bijoy.bangla.data.HistoryItem
import com.bijoy.bangla.data.KeyboardTheme
import com.bijoy.bangla.engine.KeyIconType
import com.bijoy.bangla.engine.KeyModel
import com.bijoy.bangla.engine.KeyboardLayoutType
import com.bijoy.bangla.engine.LayoutDefinitions
import kotlin.math.roundToInt

data class GboardPopupState(
    val char: String,
    val hintText: String = "",
    val rootX: Float = 0f,
    val rootY: Float = 0f,
    val keyWidth: Float = 0f,
    val keyHeight: Float = 0f
)

data class KeyBounds(val left: Float, val top: Float, val width: Float, val height: Float)

@Composable
fun KeyboardScreen(
    currentLayout: KeyboardLayoutType,
    isShifted: Boolean,
    isCapsLock: Boolean,
    actionLabel: String,
    avroBuffer: String,
    avroCandidates: List<String>,
    activeContactName: String?,
    onKeyPressed: (KeyModel) -> Unit,
    onCandidateSelected: (String) -> Unit,
    onLayoutChanged: (KeyboardLayoutType) -> Unit,
    onClipboardPaste: () -> Unit,
    onOpenSettings: () -> Unit,
    onVoiceInput: (() -> Unit)? = null,
    onCycleTheme: (() -> Unit)? = null
) {
    var currentThemeState by remember { mutableStateOf(BijoyApp.prefs.theme) }
    val colors = ThemePalettes.get(currentThemeState)
    var activePopup by remember { mutableStateOf<GboardPopupState?>(null) }
    val density = LocalDensity.current

    // Quick toolbar sub-panels
    var showLayoutSelector by remember { mutableStateOf(false) }
    var showClipboardBar by remember { mutableStateOf(false) }
    var recentHistory by remember { mutableStateOf<List<HistoryItem>>(emptyList()) }

    // Dynamic key height based on KeySize setting
    val keyRowHeight = when (BijoyApp.prefs.keySize.lowercase()) {
        "small" -> 42.dp
        "large" -> 54.dp
        else -> 48.dp
    }

    // Pre-remember layout rows to ensure zero allocations on recomposition for solid 60 FPS
    val rows = remember(currentLayout) {
        when (currentLayout) {
            KeyboardLayoutType.ENGLISH -> listOf(
                LayoutDefinitions.englishRow1,
                LayoutDefinitions.englishRow2,
                LayoutDefinitions.englishRow3
            )
            KeyboardLayoutType.AVRO -> listOf(
                LayoutDefinitions.avroRow1,
                LayoutDefinitions.avroRow2,
                LayoutDefinitions.avroRow3
            )
            KeyboardLayoutType.BIJOY -> listOf(
                LayoutDefinitions.bijoyRow1,
                LayoutDefinitions.bijoyRow2,
                LayoutDefinitions.bijoyRow3
            )
            KeyboardLayoutType.SMART_MOBILE -> listOf(
                LayoutDefinitions.smartRow1,
                LayoutDefinitions.smartRow2,
                LayoutDefinitions.smartRow3
            )
            KeyboardLayoutType.SYMBOLS_EN -> listOf(
                LayoutDefinitions.symbolsEnRow1,
                LayoutDefinitions.symbolsEnRow2,
                LayoutDefinitions.symbolsEnRow3
            )
            KeyboardLayoutType.SYMBOLS_BN -> listOf(
                LayoutDefinitions.symbolsBnRow1,
                LayoutDefinitions.symbolsBnRow2,
                LayoutDefinitions.symbolsBnRow3
            )
            else -> listOf(
                LayoutDefinitions.englishRow1,
                LayoutDefinitions.englishRow2,
                LayoutDefinitions.englishRow3
            )
        }
    }

    val numberRow = remember(currentLayout) {
        if (currentLayout == KeyboardLayoutType.BIJOY ||
            currentLayout == KeyboardLayoutType.SMART_MOBILE ||
            currentLayout == KeyboardLayoutType.SYMBOLS_BN
        ) {
            listOf("১", "২", "৩", "৪", "৫", "৬", "৭", "৮", "৯", "০").map { KeyModel(primaryText = it) }
        } else {
            listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0").map { KeyModel(primaryText = it) }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp)
        ) {
            // --- 1. TOP ACCESSORY & TOOLBAR (Matching Screen 1, 2, 3 in design) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .background(colors.accessoryBackground, RoundedCornerShape(10.dp))
                    .padding(horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // If Avro candidate suggestions are active, show them prominently
                if (avroCandidates.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .horizontalScroll(rememberScrollState()),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        avroCandidates.forEach { cand ->
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(colors.keyBackground)
                                    .clickable { onCandidateSelected(cand) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = cand,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.candidateText
                                )
                            }
                        }
                    }
                } else if (showClipboardBar) {
                    // Clipboard Quick Strip
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .horizontalScroll(rememberScrollState()),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📋 Paste:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.accentGlow,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        if (recentHistory.isEmpty()) {
                            Text(
                                text = "No recent clips",
                                fontSize = 12.sp,
                                color = colors.secondaryTextColor
                            )
                        } else {
                            recentHistory.forEach { item ->
                                Box(
                                    modifier = Modifier
                                        .padding(end = 4.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(colors.keyBackground)
                                        .clickable {
                                            onKeyPressed(KeyModel(primaryText = item.content))
                                            showClipboardBar = false
                                        }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = item.content.take(18) + if (item.content.length > 18) "…" else "",
                                        fontSize = 12.sp,
                                        color = colors.keyTextColor
                                    )
                                }
                            }
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = colors.secondaryTextColor,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { showClipboardBar = false }
                    )
                } else if (showLayoutSelector) {
                    // Quick Layout Chooser Strip
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .horizontalScroll(rememberScrollState()),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf(
                            KeyboardLayoutType.ENGLISH,
                            KeyboardLayoutType.SMART_MOBILE,
                            KeyboardLayoutType.AVRO,
                            KeyboardLayoutType.BIJOY
                        ).forEach { layout ->
                            val isSelected = currentLayout == layout
                            Box(
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) colors.actionKeyBackground else colors.keyBackground)
                                    .clickable {
                                        onLayoutChanged(layout)
                                        showLayoutSelector = false
                                    }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = layout.displayName,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else colors.secondaryTextColor
                                )
                            }
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = colors.secondaryTextColor,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { showLayoutSelector = false }
                    )
                } else {
                    // Full Screenshot Toolbar: [Grid] [Emoji] [GIF] [Clipboard] [Palette] [Settings] [Mic]
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // 1. Grid / 4 Squares (Layout Selector)
                        Icon(
                            imageVector = Icons.Default.GridView,
                            contentDescription = "Layouts",
                            tint = if (showLayoutSelector) colors.accentGlow else colors.secondaryTextColor,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { showLayoutSelector = !showLayoutSelector }
                                .padding(2.dp)
                        )

                        // 2. Emoji
                        Icon(
                            imageVector = Icons.Default.SentimentSatisfied,
                            contentDescription = "Emojis",
                            tint = colors.secondaryTextColor,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { onLayoutChanged(KeyboardLayoutType.EMOJI) }
                                .padding(2.dp)
                        )

                        // 3. GIF / Symbols
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .border(1.dp, colors.secondaryTextColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                .clickable {
                                    if (currentLayout == KeyboardLayoutType.SYMBOLS_EN || currentLayout == KeyboardLayoutType.SYMBOLS_BN) {
                                        onLayoutChanged(KeyboardLayoutType.ENGLISH)
                                    } else {
                                        onLayoutChanged(KeyboardLayoutType.SYMBOLS_EN)
                                    }
                                }
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "GIF",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.secondaryTextColor
                            )
                        }

                        // 4. Clipboard
                        Icon(
                            imageVector = Icons.Default.ContentPaste,
                            contentDescription = "Clipboard",
                            tint = if (showClipboardBar) colors.accentGlow else colors.secondaryTextColor,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    showClipboardBar = !showClipboardBar
                                    if (showClipboardBar) {
                                        recentHistory = BijoyApp.historyDb.getRecentRecords(5)
                                    }
                                }
                                .padding(2.dp)
                        )

                        // 5. Palette (Themes)
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Theme",
                            tint = colors.secondaryTextColor,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    val themes = listOf(
                                        KeyboardTheme.DYNAMIC,
                                        KeyboardTheme.DARK_GLOW,
                                        KeyboardTheme.NATURE,
                                        KeyboardTheme.SUNSET,
                                        KeyboardTheme.OCEAN,
                                        KeyboardTheme.NEON,
                                        KeyboardTheme.MINIMAL,
                                        KeyboardTheme.LIGHT,
                                        KeyboardTheme.GLASS
                                    )
                                    val nextIndex = (themes.indexOf(currentThemeState) + 1) % themes.size
                                    val nextTheme = themes[nextIndex]
                                    BijoyApp.prefs.theme = nextTheme
                                    currentThemeState = nextTheme
                                    onCycleTheme?.invoke()
                                }
                                .padding(2.dp)
                        )

                        // 6. Settings
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = colors.secondaryTextColor,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { onOpenSettings() }
                                .padding(2.dp)
                        )

                        // 7. Mic (Voice Input)
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Input",
                            tint = colors.secondaryTextColor,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    if (onVoiceInput != null) {
                                        onVoiceInput()
                                    } else {
                                        onClipboardPaste()
                                    }
                                }
                                .padding(2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Active Contact Pill (if detected from Messenger/WhatsApp)
            if (!activeContactName.isNullOrBlank() && avroCandidates.isEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(colors.accentGlow.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "💬 $activeContactName (${currentLayout.shortLabel})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = colors.accentGlow
                        )
                    }
                }
            }

            // --- 2. MAIN KEYBOARD CONTENT / EMOJI PANEL ---
            if (currentLayout == KeyboardLayoutType.EMOJI) {
                EmojiPanel(
                    colors = colors,
                    onEmojiSelected = { emoji ->
                        onKeyPressed(KeyModel(primaryText = emoji))
                    },
                    onBackToLetters = {
                        onLayoutChanged(KeyboardLayoutType.ENGLISH)
                    }
                )
            } else {
                // Optional Dedicated Number Row (Always show numbers toggle in settings)
                if (BijoyApp.prefs.isShowNumberRow) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(keyRowHeight - 6.dp)
                            .padding(vertical = 1.5.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        numberRow.forEach { key ->
                            GboardKeyButton(
                                key = key,
                                isShifted = isShifted,
                                isCapsLock = isCapsLock,
                                colors = colors,
                                modifier = Modifier
                                    .weight(1.0f)
                                    .fillMaxHeight()
                                    .padding(horizontal = 1.5.dp),
                                onTouchDown = { bounds, displayChar ->
                                    if (BijoyApp.prefs.isPopupEnabled) {
                                        activePopup = GboardPopupState(
                                            char = displayChar,
                                            hintText = "",
                                            rootX = bounds.left,
                                            rootY = bounds.top,
                                            keyWidth = bounds.width,
                                            keyHeight = bounds.height
                                        )
                                    }
                                },
                                onTouchUp = { activePopup = null },
                                onTap = { onKeyPressed(key) }
                            )
                        }
                    }
                }

                // Render Rows 1, 2, 3
                rows.forEachIndexed { index, rowKeys ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(keyRowHeight)
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (index == 1 && (currentLayout == KeyboardLayoutType.ENGLISH || currentLayout == KeyboardLayoutType.AVRO)) {
                            Spacer(modifier = Modifier.weight(0.5f))
                        }

                        rowKeys.forEach { key ->
                            GboardKeyButton(
                                key = key,
                                isShifted = isShifted,
                                isCapsLock = isCapsLock,
                                colors = colors,
                                modifier = Modifier
                                    .weight(key.widthWeight)
                                    .fillMaxHeight()
                                    .padding(horizontal = 2.dp),
                                onTouchDown = { bounds, displayChar ->
                                    if (BijoyApp.prefs.isPopupEnabled && key.iconType == KeyIconType.NONE) {
                                        activePopup = GboardPopupState(
                                            char = displayChar,
                                            hintText = key.hintText,
                                            rootX = bounds.left,
                                            rootY = bounds.top,
                                            keyWidth = bounds.width,
                                            keyHeight = bounds.height
                                        )
                                    }
                                },
                                onTouchUp = {
                                    activePopup = null
                                },
                                onTap = { onKeyPressed(key) },
                                onLongPress = {
                                    if (key.hintText.isNotEmpty()) {
                                        onKeyPressed(KeyModel(primaryText = key.hintText))
                                    } else if (key.iconType == KeyIconType.SHIFT) {
                                        onKeyPressed(KeyModel("", isSpecial = true, iconType = KeyIconType.SHIFT))
                                    }
                                }
                            )
                        }

                        if (index == 1 && (currentLayout == KeyboardLayoutType.ENGLISH || currentLayout == KeyboardLayoutType.AVRO)) {
                            Spacer(modifier = Modifier.weight(0.5f))
                        }
                    }
                }

                // --- 3. BOTTOM ROW (Row 4: ?123, 🌐, Space "English ▾", ., ➔) ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(keyRowHeight)
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val symbolLabel = if (currentLayout == KeyboardLayoutType.SYMBOLS_EN || currentLayout == KeyboardLayoutType.SYMBOLS_BN) "ABC" else "?123"

                    // 1. ?123 or ABC
                    GboardKeyButton(
                        key = KeyModel(symbolLabel, isSpecial = true, iconType = KeyIconType.SYMBOLS),
                        isShifted = false,
                        isCapsLock = false,
                        colors = colors,
                        modifier = Modifier
                            .weight(1.3f)
                            .fillMaxHeight()
                            .padding(horizontal = 2.dp),
                        onTouchDown = { _, _ -> },
                        onTouchUp = { },
                        onTap = {
                            if (currentLayout == KeyboardLayoutType.SYMBOLS_EN || currentLayout == KeyboardLayoutType.SYMBOLS_BN) {
                                onLayoutChanged(KeyboardLayoutType.ENGLISH)
                            } else {
                                onLayoutChanged(KeyboardLayoutType.SYMBOLS_EN)
                            }
                        }
                    )

                    // 2. Globe 🌐
                    GboardKeyButton(
                        key = KeyModel("", isSpecial = true, iconType = KeyIconType.GLOBE),
                        isShifted = false,
                        isCapsLock = false,
                        colors = colors,
                        modifier = Modifier
                            .weight(1.0f)
                            .fillMaxHeight()
                            .padding(horizontal = 2.dp),
                        onTouchDown = { _, _ -> },
                        onTouchUp = { },
                        onTap = {
                            val nextLayout = when (currentLayout) {
                                KeyboardLayoutType.ENGLISH -> KeyboardLayoutType.SMART_MOBILE
                                KeyboardLayoutType.SMART_MOBILE -> KeyboardLayoutType.AVRO
                                KeyboardLayoutType.AVRO -> KeyboardLayoutType.BIJOY
                                KeyboardLayoutType.BIJOY -> KeyboardLayoutType.ENGLISH
                                else -> KeyboardLayoutType.ENGLISH
                            }
                            onLayoutChanged(nextLayout)
                        }
                    )

                    // 3. Spacebar with Language Dropdown ("English ▾", "বাংলা ▾", etc.)
                    val spaceLanguageLabel = when (currentLayout) {
                        KeyboardLayoutType.ENGLISH -> "English ▾"
                        KeyboardLayoutType.SMART_MOBILE -> "বাংলা ▾"
                        KeyboardLayoutType.AVRO -> "বাংলা (Avro) ▾"
                        KeyboardLayoutType.BIJOY -> "বাংলা (বিজয়) ▾"
                        KeyboardLayoutType.SYMBOLS_EN -> "English ▾"
                        KeyboardLayoutType.SYMBOLS_BN -> "বাংলা ▾"
                        else -> "English ▾"
                    }

                    GboardKeyButton(
                        key = KeyModel(spaceLanguageLabel, iconType = KeyIconType.SPACE),
                        isShifted = false,
                        isCapsLock = false,
                        colors = colors,
                        modifier = Modifier
                            .weight(4.4f)
                            .fillMaxHeight()
                            .padding(horizontal = 2.dp),
                        onTouchDown = { _, _ -> },
                        onTouchUp = { },
                        onTap = {
                            onKeyPressed(KeyModel(" ", iconType = KeyIconType.SPACE))
                        },
                        onLongPress = {
                            showLayoutSelector = true
                        }
                    )

                    // 4. Dot "." key
                    GboardKeyButton(
                        key = KeyModel(".", isSpecial = true),
                        isShifted = false,
                        isCapsLock = false,
                        colors = colors,
                        modifier = Modifier
                            .weight(1.0f)
                            .fillMaxHeight()
                            .padding(horizontal = 2.dp),
                        onTouchDown = { _, _ -> },
                        onTouchUp = { },
                        onTap = {
                            onKeyPressed(KeyModel("."))
                        }
                    )

                    // 5. Action / Return Key (Glowing Blue/Purple Pill with ➔ Arrow)
                    GboardKeyButton(
                        key = KeyModel(actionLabel, isSpecial = true, iconType = KeyIconType.RETURN),
                        isShifted = false,
                        isCapsLock = false,
                        colors = colors,
                        isActionKey = true,
                        modifier = Modifier
                            .weight(1.8f)
                            .fillMaxHeight()
                            .padding(horizontal = 2.dp),
                        onTouchDown = { _, _ -> },
                        onTouchUp = { },
                        onTap = {
                            onKeyPressed(KeyModel("", isSpecial = true, iconType = KeyIconType.RETURN))
                        }
                    )
                }
            }
        }

        // --- 4. GBOARD-STYLE FLOATING POPUP OVERLAY (60 FPS GPU-accelerated) ---
        activePopup?.let { popup ->
            val popupWidthDp = 58.dp
            val popupHeightDp = 64.dp

            val popupWidthPx = with(density) { popupWidthDp.toPx() }
            val popupHeightPx = with(density) { popupHeightDp.toPx() }

            val targetXPx = popup.rootX + (popup.keyWidth / 2f) - (popupWidthPx / 2f)
            val targetYPx = popup.rootY - popupHeightPx + with(density) { 6.dp.toPx() }

            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = targetXPx.roundToInt().coerceAtLeast(0),
                            y = targetYPx.roundToInt()
                        )
                    }
                    .size(width = popupWidthDp, height = popupHeightDp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(12.dp),
                        spotColor = Color(0x66000000),
                        ambientColor = Color(0x44000000)
                    )
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.keyBackground)
                    .border(1.dp, colors.accentGlow.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Secondary hint on top-right (like Gboard)
                if (popup.hintText.isNotEmpty()) {
                    Text(
                        text = popup.hintText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.accentGlow,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 4.dp, end = 6.dp)
                    )
                }

                // Main Character displayed in Gboard large bold style
                Text(
                    text = popup.char,
                    fontSize = if (popup.char.length > 1) 22.sp else 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.keyTextColor,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun GboardKeyButton(
    key: KeyModel,
    isShifted: Boolean,
    isCapsLock: Boolean,
    colors: KeyboardThemeColors,
    modifier: Modifier = Modifier,
    isActionKey: Boolean = false,
    onTouchDown: (KeyBounds, String) -> Unit,
    onTouchUp: () -> Unit,
    onTap: () -> Unit,
    onLongPress: (() -> Unit)? = null
) {
    var isPressed by remember { mutableStateOf(false) }
    var currentBounds by remember { mutableStateOf(KeyBounds(0f, 0f, 0f, 0f)) }

    val bg = when {
        isActionKey -> colors.actionKeyBackground
        key.isSpecial -> colors.specialKeyBackground
        else -> colors.keyBackground
    }
    val shadowColor = if (isActionKey) colors.accentGlow.copy(alpha = 0.4f) else if (key.isSpecial) colors.specialKeyShadow else colors.keyShadow
    val textColor = when {
        isActionKey -> colors.actionKeyText
        else -> colors.keyTextColor
    }

    val displayChar = when {
        key.iconType != KeyIconType.NONE -> ""
        isShifted && key.shiftedText.isNotEmpty() -> key.shiftedText
        else -> key.primaryText
    }

    Box(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                val pos = coordinates.positionInRoot()
                val size = coordinates.size
                currentBounds = KeyBounds(pos.x, pos.y, size.width.toFloat(), size.height.toFloat())
            }
            .shadow(
                elevation = if (isPressed) 0.5.dp else 1.8.dp,
                shape = RoundedCornerShape(7.dp),
                spotColor = shadowColor,
                ambientColor = shadowColor
            )
            .clip(RoundedCornerShape(7.dp))
            .background(
                if (isActionKey) {
                    Brush.horizontalGradient(
                        listOf(Color(0xFF7B2FFE), Color(0xFF00C6FF))
                    )
                } else if (isPressed) {
                    bg.copy(alpha = 0.75f)
                } else {
                    bg
                }
            )
            .border(
                width = if (isActionKey) 1.dp else 0.5.dp,
                color = if (isActionKey) Color(0xFF00E5FF).copy(alpha = 0.6f) else colors.keyShadow.copy(alpha = 0.3f),
                shape = RoundedCornerShape(7.dp)
            )
            .pointerInput(key, displayChar) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        onTouchDown(currentBounds, displayChar)
                        tryAwaitRelease()
                        isPressed = false
                        onTouchUp()
                    },
                    onTap = { onTap() },
                    onLongPress = { onLongPress?.invoke() }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Small Top-Right Hint (Gboard style top row number hint or secondary shifted hint)
        if (key.hintText.isNotEmpty() && key.iconType == KeyIconType.NONE) {
            Text(
                text = key.hintText,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                color = colors.secondaryTextColor.copy(alpha = 0.8f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 2.dp, end = 4.dp)
            )
        }

        when (key.iconType) {
            KeyIconType.SHIFT -> {
                Icon(
                    imageVector = if (isCapsLock) Icons.Default.VerticalAlignTop else Icons.Default.ArrowUpward,
                    contentDescription = "Shift",
                    tint = if (isShifted || isCapsLock) colors.accentGlow else textColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            KeyIconType.BACKSPACE -> {
                Icon(
                    imageVector = Icons.Default.Backspace,
                    contentDescription = "Delete",
                    tint = textColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            KeyIconType.GLOBE -> {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "Globe",
                    tint = textColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            KeyIconType.EMOJI -> {
                Icon(
                    imageVector = Icons.Default.SentimentSatisfied,
                    contentDescription = "Emoji",
                    tint = textColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            KeyIconType.RETURN -> {
                // Futuristic Glowing Arrow ➔ matching the screenshot
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = key.primaryText.ifEmpty { "Return" },
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            KeyIconType.SPACE -> {
                Text(
                    text = key.primaryText,
                    fontSize = 13.sp,
                    color = colors.secondaryTextColor,
                    fontWeight = FontWeight.Medium
                )
            }
            else -> {
                Text(
                    text = displayChar,
                    fontSize = if (displayChar.length > 2) 13.sp else 19.sp,
                    fontWeight = if (key.isSpecial) FontWeight.Medium else FontWeight.SemiBold,
                    color = textColor,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun EmojiPanel(
    colors: KeyboardThemeColors,
    onEmojiSelected: (String) -> Unit,
    onBackToLetters: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(235.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.actionKeyBackground)
                    .clickable { onBackToLetters() }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "ABC",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Text(
                text = "Frequently Used Emojis",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = colors.secondaryTextColor
            )

            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = colors.secondaryTextColor,
                modifier = Modifier
                    .size(22.dp)
                    .clickable { onBackToLetters() }
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(8),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(4.dp)
        ) {
            itemsIndexed(
                items = LayoutDefinitions.popularEmojis,
                key = { index, emoji -> "${index}_$emoji" }
            ) { _, emoji ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { onEmojiSelected(emoji) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = emoji,
                        fontSize = 24.sp
                    )
                }
            }
        }
    }
}
