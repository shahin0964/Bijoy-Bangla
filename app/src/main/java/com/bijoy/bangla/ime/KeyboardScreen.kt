package com.bijoy.bangla.ime

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bijoy.bangla.BijoyApp
import com.bijoy.bangla.engine.KeyIconType
import com.bijoy.bangla.engine.KeyModel
import com.bijoy.bangla.engine.KeyboardLayoutType
import com.bijoy.bangla.engine.LayoutDefinitions

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
    onOpenSettings: () -> Unit
) {
    val colors = ThemePalettes.get(BijoyApp.prefs.theme)
    var pressedKey by remember { mutableStateOf<KeyModel?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.background)
            .padding(horizontal = 4.dp, vertical = 4.dp)
    ) {
        // --- 1. Top Accessory & Suggestion Bar ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .background(colors.accessoryBackground, RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Layout Switcher Pills
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(
                    KeyboardLayoutType.ENGLISH,
                    KeyboardLayoutType.AVRO,
                    KeyboardLayoutType.BIJOY,
                    KeyboardLayoutType.SMART_MOBILE
                ).forEach { layout ->
                    val isSelected = currentLayout == layout
                    Box(
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) colors.actionKeyBackground else Color.Transparent)
                            .clickable { onLayoutChanged(layout) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = layout.shortLabel,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else colors.secondaryTextColor
                        )
                    }
                }
            }

            // Middle: Active Contact Context Pill or Avro candidates
            if (avroCandidates.isNotEmpty()) {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    avroCandidates.forEach { cand ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(colors.keyBackground)
                                .clickable { onCandidateSelected(cand) }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
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
            } else if (!activeContactName.isNullOrBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x22007AFF))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "💬 $activeContactName",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF007AFF)
                    )
                }
            }

            // Right: Clipboard & Settings Shortcuts
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ContentPaste,
                    contentDescription = "Paste",
                    tint = colors.secondaryTextColor,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onClipboardPaste() }
                        .padding(2.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = colors.secondaryTextColor,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onOpenSettings() }
                        .padding(2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // --- 2. Main Keyboard Content / Emoji Panel ---
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
            // Rows depending on currentLayout
            val rows = when (currentLayout) {
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

            // Render Rows 1, 2, 3
            rows.forEachIndexed { index, rowKeys ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Slight indent on row 2 for standard iOS staggering
                    if (index == 1 && (currentLayout == KeyboardLayoutType.ENGLISH || currentLayout == KeyboardLayoutType.AVRO)) {
                        Spacer(modifier = Modifier.weight(0.5f))
                    }

                    rowKeys.forEach { key ->
                        IosKeyButton(
                            key = key,
                            isShifted = isShifted,
                            isCapsLock = isCapsLock,
                            colors = colors,
                            modifier = Modifier
                                .weight(key.widthWeight)
                                .fillMaxHeight()
                                .padding(horizontal = 2.5.dp),
                            onTap = { onKeyPressed(key) },
                            onLongPress = {
                                if (key.iconType == KeyIconType.SHIFT) {
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

            // --- 3. Bottom Row (Row 4: 123, 🌐, Space, ১২৩/., Return) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .padding(vertical = 3.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 123 / ABC Switch Key
                val symbolLabel = if (currentLayout == KeyboardLayoutType.SYMBOLS_EN || currentLayout == KeyboardLayoutType.SYMBOLS_BN) "ABC" else "123"
                IosKeyButton(
                    key = KeyModel(symbolLabel, isSpecial = true, iconType = KeyIconType.SYMBOLS),
                    isShifted = false,
                    isCapsLock = false,
                    colors = colors,
                    modifier = Modifier
                        .weight(1.3f)
                        .fillMaxHeight()
                        .padding(horizontal = 2.5.dp),
                    onTap = {
                        if (currentLayout == KeyboardLayoutType.SYMBOLS_EN || currentLayout == KeyboardLayoutType.SYMBOLS_BN) {
                            onLayoutChanged(KeyboardLayoutType.ENGLISH)
                        } else {
                            onLayoutChanged(KeyboardLayoutType.SYMBOLS_EN)
                        }
                    }
                )

                // Globe Key (🌐) for layout switching
                IosKeyButton(
                    key = KeyModel("", isSpecial = true, iconType = KeyIconType.GLOBE),
                    isShifted = false,
                    isCapsLock = false,
                    colors = colors,
                    modifier = Modifier
                        .weight(1.0f)
                        .fillMaxHeight()
                        .padding(horizontal = 2.5.dp),
                    onTap = {
                        val nextLayout = when (currentLayout) {
                            KeyboardLayoutType.ENGLISH -> KeyboardLayoutType.AVRO
                            KeyboardLayoutType.AVRO -> KeyboardLayoutType.BIJOY
                            KeyboardLayoutType.BIJOY -> KeyboardLayoutType.SMART_MOBILE
                            KeyboardLayoutType.SMART_MOBILE -> KeyboardLayoutType.ENGLISH
                            else -> KeyboardLayoutType.ENGLISH
                        }
                        onLayoutChanged(nextLayout)
                    }
                )

                // Space Bar
                val spaceLabel = when (currentLayout) {
                    KeyboardLayoutType.ENGLISH -> "English"
                    KeyboardLayoutType.AVRO -> "অভ্র ফোনেটিক"
                    KeyboardLayoutType.BIJOY -> "বিজয় বাংলা"
                    KeyboardLayoutType.SMART_MOBILE -> "সহজ বাংলা"
                    KeyboardLayoutType.SYMBOLS_EN -> "space"
                    KeyboardLayoutType.SYMBOLS_BN -> "স্পেস"
                    else -> "space"
                }
                IosKeyButton(
                    key = KeyModel(spaceLabel, iconType = KeyIconType.SPACE),
                    isShifted = false,
                    isCapsLock = false,
                    colors = colors,
                    modifier = Modifier
                        .weight(4.4f)
                        .fillMaxHeight()
                        .padding(horizontal = 2.5.dp),
                    onTap = {
                        onKeyPressed(KeyModel(" ", iconType = KeyIconType.SPACE))
                    }
                )

                // Emoji / Bangla Number Switcher
                IosKeyButton(
                    key = KeyModel(if (currentLayout == KeyboardLayoutType.SYMBOLS_BN) "123" else "১২৩", isSpecial = true),
                    isShifted = false,
                    isCapsLock = false,
                    colors = colors,
                    modifier = Modifier
                        .weight(1.0f)
                        .fillMaxHeight()
                        .padding(horizontal = 2.5.dp),
                    onTap = {
                        if (currentLayout == KeyboardLayoutType.SYMBOLS_BN) {
                            onLayoutChanged(KeyboardLayoutType.SYMBOLS_EN)
                        } else {
                            onLayoutChanged(KeyboardLayoutType.SYMBOLS_BN)
                        }
                    }
                )

                // Emoji Icon Key
                IosKeyButton(
                    key = KeyModel("", isSpecial = true, iconType = KeyIconType.EMOJI),
                    isShifted = false,
                    isCapsLock = false,
                    colors = colors,
                    modifier = Modifier
                        .weight(1.0f)
                        .fillMaxHeight()
                        .padding(horizontal = 2.5.dp),
                    onTap = {
                        onLayoutChanged(KeyboardLayoutType.EMOJI)
                    }
                )

                // Return / Action Key (iOS Blue button)
                IosKeyButton(
                    key = KeyModel(actionLabel, isSpecial = true, iconType = KeyIconType.RETURN),
                    isShifted = false,
                    isCapsLock = false,
                    colors = colors,
                    isActionKey = true,
                    modifier = Modifier
                        .weight(1.8f)
                        .fillMaxHeight()
                        .padding(horizontal = 2.5.dp),
                    onTap = {
                        onKeyPressed(KeyModel("", isSpecial = true, iconType = KeyIconType.RETURN))
                    }
                )
            }
        }
    }
}

@Composable
fun IosKeyButton(
    key: KeyModel,
    isShifted: Boolean,
    isCapsLock: Boolean,
    colors: KeyboardThemeColors,
    modifier: Modifier = Modifier,
    isActionKey: Boolean = false,
    onTap: () -> Unit,
    onLongPress: (() -> Unit)? = null
) {
    var isPressed by remember { mutableStateOf(false) }

    val bg = when {
        isActionKey -> colors.actionKeyBackground
        key.isSpecial -> colors.specialKeyBackground
        else -> colors.keyBackground
    }
    val shadowColor = if (isActionKey) Color(0xFF0056B3) else if (key.isSpecial) colors.specialKeyShadow else colors.keyShadow
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
            .shadow(
                elevation = if (isPressed) 0.5.dp else 1.5.dp,
                shape = RoundedCornerShape(5.dp),
                spotColor = shadowColor,
                ambientColor = shadowColor
            )
            .clip(RoundedCornerShape(5.dp))
            .background(if (isPressed) bg.copy(alpha = 0.8f) else bg)
            .pointerInput(key) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onTap() },
                    onLongPress = { onLongPress?.invoke() }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        when (key.iconType) {
            KeyIconType.SHIFT -> {
                Icon(
                    imageVector = if (isCapsLock) Icons.Default.VerticalAlignTop else Icons.Default.ArrowUpward,
                    contentDescription = "Shift",
                    tint = if (isShifted || isCapsLock) Color(0xFF007AFF) else textColor,
                    modifier = Modifier.size(19.dp)
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
                Text(
                    text = key.primaryText.ifEmpty { "return" },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor
                )
            }
            KeyIconType.SPACE -> {
                Text(
                    text = key.primaryText,
                    fontSize = 13.sp,
                    color = colors.secondaryTextColor,
                    fontWeight = FontWeight.Normal
                )
            }
            else -> {
                Text(
                    text = displayChar,
                    fontSize = if (displayChar.length > 2) 13.sp else 20.sp,
                    fontWeight = if (key.isSpecial) FontWeight.Medium else FontWeight.Normal,
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
            .height(220.dp)
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
                    .clip(RoundedCornerShape(6.dp))
                    .background(colors.specialKeyBackground)
                    .clickable { onBackToLetters() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "ABC",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.keyTextColor
                )
            }

            Text(
                text = "Emojis",
                fontSize = 14.sp,
                color = colors.secondaryTextColor
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(8),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(4.dp)
        ) {
            items(LayoutDefinitions.popularEmojis) { emoji ->
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clickable { onEmojiSelected(emoji) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = emoji,
                        fontSize = 22.sp
                    )
                }
            }
        }
    }
}
