package com.bijoy.bangla.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bijoy.bangla.BijoyApp
import com.bijoy.bangla.data.HistoryItem
import com.bijoy.bangla.data.KeyboardTheme
import com.bijoy.bangla.data.SavedChatLayout
import com.bijoy.bangla.data.update.DownloadState
import com.bijoy.bangla.data.update.OtaUpdateManager
import com.bijoy.bangla.data.update.UpdateStatus
import com.bijoy.bangla.engine.KeyboardLayoutType
import com.bijoy.bangla.ime.KeyboardScreen
import com.bijoy.bangla.ime.ThemePalettes
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BijoyBanglaApp()
        }
    }
}

// App Design Color Palette matching screenshot
val BgDark = Color(0xFF0B0E1B)
val CardDark = Color(0xFF14192D)
val CardBorder = Color(0xFF1E284D)
val AccentPurple = Color(0xFF7B2FFE)
val AccentCyan = Color(0xFF00E5FF)
val TextMuted = Color(0xFF8E99B7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BijoyBanglaApp() {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    var currentSubScreen by remember { mutableStateOf<String?>(null) } // "LAYOUT", "CHAT_MEMORY", "HISTORY", "UPDATES"

    Scaffold(
        containerColor = BgDark,
        bottomBar = {
            if (currentSubScreen == null) {
                NavigationBar(
                    containerColor = Color(0xFF090D1A),
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text("Settings") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AccentPurple,
                            selectedTextColor = AccentCyan,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted,
                            indicatorColor = Color(0xFF1A1F36)
                        )
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = { Icon(Icons.Default.Palette, contentDescription = "Themes") },
                        label = { Text("Themes") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AccentPurple,
                            selectedTextColor = AccentCyan,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted,
                            indicatorColor = Color(0xFF1A1F36)
                        )
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = { Icon(Icons.Default.GridView, contentDescription = "More") },
                        label = { Text("More") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AccentPurple,
                            selectedTextColor = AccentCyan,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted,
                            indicatorColor = Color(0xFF1A1F36)
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BgDark)
        ) {
            when {
                currentSubScreen == "LAYOUT" -> {
                    KeyboardLayoutSettingsScreen(
                        onBack = { currentSubScreen = null }
                    )
                }
                currentSubScreen == "CHAT_MEMORY" -> {
                    ChatMemorySubScreen(
                        context = context,
                        onBack = { currentSubScreen = null }
                    )
                }
                currentSubScreen == "HISTORY" -> {
                    HistorySubScreen(
                        context = context,
                        onBack = { currentSubScreen = null }
                    )
                }
                currentSubScreen == "UPDATES" -> {
                    OtaUpdateSubScreen(
                        context = context,
                        onBack = { currentSubScreen = null }
                    )
                }
                selectedTab == 0 -> {
                    KeyboardSettingsMainScreen(
                        context = context,
                        onNavigateToThemes = { selectedTab = 1 },
                        onNavigateToLayout = { currentSubScreen = "LAYOUT" },
                        onNavigateToChatMemory = { currentSubScreen = "CHAT_MEMORY" },
                        onNavigateToUpdates = { currentSubScreen = "UPDATES" }
                    )
                }
                selectedTab == 1 -> {
                    ThemeAndAppearanceScreen(context = context)
                }
                selectedTab == 2 -> {
                    MoreOptionsScreen(
                        context = context,
                        onNavigateToChatMemory = { currentSubScreen = "CHAT_MEMORY" },
                        onNavigateToHistory = { currentSubScreen = "HISTORY" },
                        onNavigateToUpdates = { currentSubScreen = "UPDATES" }
                    )
                }
            }
        }
    }
}

// ======================================================================================
// SCREEN 4: KEYBOARD SETTINGS (Main Settings matching Screen 4 in screenshot)
// ======================================================================================
@Composable
fun KeyboardSettingsMainScreen(
    context: Context,
    onNavigateToThemes: () -> Unit,
    onNavigateToLayout: () -> Unit,
    onNavigateToChatMemory: () -> Unit,
    onNavigateToUpdates: () -> Unit
) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // --- 1. Glowing Gradient Header Banner (Matching Screenshot Card) ---
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF5A189A), Color(0xFF7B2FFE), Color(0xFF00B4D8))
                        )
                    )
                    .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .border(1.5.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Keyboard,
                            contentDescription = "Keyboard",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "Bijoy Bangla Keyboard",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Smart • Fast • Beautiful • 60 FPS",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFD0F4FF)
                        )
                    }
                }
            }
        }

        // --- 2. System Activation Setup Quick Bar ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
                        context.startActivity(intent)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E284D)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.ToggleOn, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("1. Enable IME", fontSize = 12.sp, color = Color.White)
                }

                Button(
                    onClick = { imm.showInputMethodPicker() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("2. Set Default", fontSize = 12.sp, color = Color.White)
                }
            }
        }

        // --- 3. Menu Options List (Matching Screenshot 4) ---
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(CardDark)
                    .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
            ) {
                // 1. Languages
                CyberMenuItem(
                    icon = Icons.Default.Language,
                    iconBg = Color(0xFF7B2FFE),
                    title = "Languages",
                    trailingText = "English, বাংলা",
                    onClick = onNavigateToChatMemory
                )
                Divider(color = CardBorder, thickness = 0.5.dp)

                // 2. Theme & Appearance
                CyberMenuItem(
                    icon = Icons.Default.Palette,
                    iconBg = Color(0xFFFF9100),
                    title = "Theme & Appearance",
                    trailingText = BijoyApp.prefs.theme.title,
                    onClick = onNavigateToThemes
                )
                Divider(color = CardBorder, thickness = 0.5.dp)

                // 3. Keyboard Layout
                CyberMenuItem(
                    icon = Icons.Default.Keyboard,
                    iconBg = Color(0xFF00B4D8),
                    title = "Keyboard Layout",
                    trailingText = BijoyApp.prefs.currentLayoutId,
                    onClick = onNavigateToLayout
                )
                Divider(color = CardBorder, thickness = 0.5.dp)

                // 4. Typing & Gestures
                var showTypingDialog by remember { mutableStateOf(false) }
                CyberMenuItem(
                    icon = Icons.Default.TouchApp,
                    iconBg = Color(0xFF00E676),
                    title = "Typing & Gestures",
                    trailingText = if (BijoyApp.prefs.isVibrationEnabled) "Haptic On" else "Silent",
                    onClick = { showTypingDialog = true }
                )
                Divider(color = CardBorder, thickness = 0.5.dp)

                if (showTypingDialog) {
                    AlertDialog(
                        onDismissRequest = { showTypingDialog = false },
                        containerColor = CardDark,
                        title = { Text("Typing & Gestures", color = Color.White, fontWeight = FontWeight.Bold) },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Vibration on Keypress", color = Color.White)
                                    Switch(
                                        checked = BijoyApp.prefs.isVibrationEnabled,
                                        onCheckedChange = { BijoyApp.prefs.isVibrationEnabled = it }
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Double space for dot (.)", color = Color.White)
                                    Switch(
                                        checked = BijoyApp.prefs.isDoubleSpacePeriodEnabled,
                                        onCheckedChange = { BijoyApp.prefs.isDoubleSpacePeriodEnabled = it }
                                    )
                                }
                            }
                        },
                        confirmButton = {
                            Button(onClick = { showTypingDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)) {
                                Text("OK", color = Color.White)
                            }
                        }
                    )
                }

                // 5. Text Correction
                var showCorrectionDialog by remember { mutableStateOf(false) }
                CyberMenuItem(
                    icon = Icons.Default.Spellcheck,
                    iconBg = Color(0xFFE040FB),
                    title = "Text Correction",
                    trailingText = if (BijoyApp.prefs.isAutoCorrectionEnabled) "Active" else "Off",
                    onClick = { showCorrectionDialog = true }
                )
                Divider(color = CardBorder, thickness = 0.5.dp)

                if (showCorrectionDialog) {
                    AlertDialog(
                        onDismissRequest = { showCorrectionDialog = false },
                        containerColor = CardDark,
                        title = { Text("Text Correction", color = Color.White, fontWeight = FontWeight.Bold) },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Auto-Correction (Juktakkhor)", color = Color.White)
                                    Switch(
                                        checked = BijoyApp.prefs.isAutoCorrectionEnabled,
                                        onCheckedChange = { BijoyApp.prefs.isAutoCorrectionEnabled = it }
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Suggest Next Word", color = Color.White)
                                    Switch(
                                        checked = BijoyApp.prefs.isSuggestNextWordEnabled,
                                        onCheckedChange = { BijoyApp.prefs.isSuggestNextWordEnabled = it }
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Auto-Capitalization", color = Color.White)
                                    Switch(
                                        checked = BijoyApp.prefs.isAutoCapitalizationEnabled,
                                        onCheckedChange = { BijoyApp.prefs.isAutoCapitalizationEnabled = it }
                                    )
                                }
                            }
                        },
                        confirmButton = {
                            Button(onClick = { showCorrectionDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)) {
                                Text("OK", color = Color.White)
                            }
                        }
                    )
                }

                // 6. Emoji & GIF
                CyberMenuItem(
                    icon = Icons.Default.EmojiEmotions,
                    iconBg = Color(0xFFFFD600),
                    title = "Emoji & GIF",
                    trailingText = if (BijoyApp.prefs.isEmojiPanelEnabled) "Enabled" else "Hidden",
                    onClick = {
                        BijoyApp.prefs.isEmojiPanelEnabled = !BijoyApp.prefs.isEmojiPanelEnabled
                        Toast.makeText(context, "Emoji panel: ${if (BijoyApp.prefs.isEmojiPanelEnabled) "Enabled" else "Disabled"}", Toast.LENGTH_SHORT).show()
                    }
                )
                Divider(color = CardBorder, thickness = 0.5.dp)

                // 7. Sound & Haptic Feedback
                var showSoundDialog by remember { mutableStateOf(false) }
                CyberMenuItem(
                    icon = Icons.Default.VolumeUp,
                    iconBg = Color(0xFF00E5FF),
                    title = "Sound & Haptic Feedback",
                    trailingText = if (BijoyApp.prefs.isSoundEnabled) "Sound On" else "Haptic",
                    onClick = { showSoundDialog = true }
                )
                Divider(color = CardBorder, thickness = 0.5.dp)

                if (showSoundDialog) {
                    AlertDialog(
                        onDismissRequest = { showSoundDialog = false },
                        containerColor = CardDark,
                        title = { Text("Sound & Haptics", color = Color.White, fontWeight = FontWeight.Bold) },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Sound on Keypress", color = Color.White)
                                    Switch(
                                        checked = BijoyApp.prefs.isSoundEnabled,
                                        onCheckedChange = { BijoyApp.prefs.isSoundEnabled = it }
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Vibration Feedback", color = Color.White)
                                    Switch(
                                        checked = BijoyApp.prefs.isVibrationEnabled,
                                        onCheckedChange = { BijoyApp.prefs.isVibrationEnabled = it }
                                    )
                                }
                            }
                        },
                        confirmButton = {
                            Button(onClick = { showSoundDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)) {
                                Text("OK", color = Color.White)
                            }
                        }
                    )
                }

                // 8. Advanced Settings & OTA Updates
                CyberMenuItem(
                    icon = Icons.Default.SettingsSuggest,
                    iconBg = Color(0xFF00B4D8),
                    title = "Advanced Settings & Updates",
                    trailingText = "OTA Update >",
                    onClick = onNavigateToUpdates
                )
            }
        }
    }
}

// ======================================================================================
// SCREEN 5: THEME & APPEARANCE (Matching Screen 5 in screenshot)
// ======================================================================================
@Composable
fun ThemeAndAppearanceScreen(context: Context) {
    var selectedTheme by remember { mutableStateOf(BijoyApp.prefs.theme) }
    var transparency by remember { mutableStateOf(BijoyApp.prefs.themeTransparency) }
    var blurEffect by remember { mutableStateOf(BijoyApp.prefs.isBlurEffectEnabled) }
    var selectedSubTab by remember { mutableStateOf(0) } // 0: Themes, 1: Background, 2: Custom

    val themesList = listOf(
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Theme & Appearance",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // --- Top Sub-Tabs: [Themes] [Background] [Custom] ---
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardDark)
                    .padding(4.dp)
            ) {
                listOf("Themes", "Background", "Custom").forEachIndexed { index, title ->
                    val isSelected = selectedSubTab == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) AccentPurple else Color.Transparent)
                            .clickable { selectedSubTab = index }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else TextMuted
                        )
                    }
                }
            }
        }

        // --- 3x3 Themes Grid (Matching Screenshot 5) ---
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                for (row in 0 until 3) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (col in 0 until 3) {
                            val index = row * 3 + col
                            val themeItem = themesList[index]
                            val isSelected = selectedTheme == themeItem

                            ThemeThumbnailCard(
                                theme = themeItem,
                                isSelected = isSelected,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    selectedTheme = themeItem
                                }
                            )
                        }
                    }
                }
            }
        }

        // --- Sliders & Toggles Section ---
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardDark)
                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Transparency Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Transparency", color = Color.White, fontSize = 14.sp)
                    Text("${(transparency * 100).roundToInt()}%", color = AccentCyan, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = transparency,
                    onValueChange = { transparency = it },
                    valueRange = 0.2f..1.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = AccentPurple,
                        activeTrackColor = AccentPurple,
                        inactiveTrackColor = CardBorder
                    )
                )

                Divider(color = CardBorder, thickness = 0.5.dp)

                // Enable Blur Effect Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Enable Blur Effect", color = Color.White, fontSize = 14.sp)
                    Switch(
                        checked = blurEffect,
                        onCheckedChange = { blurEffect = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = AccentPurple
                        )
                    )
                }
            }
        }

        // --- Apply Theme Button ---
        item {
            Button(
                onClick = {
                    BijoyApp.prefs.theme = selectedTheme
                    BijoyApp.prefs.themeTransparency = transparency
                    BijoyApp.prefs.isBlurEffectEnabled = blurEffect
                    Toast.makeText(context, "${selectedTheme.title} Theme Applied!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)
            ) {
                Text(
                    text = "Apply Theme",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun ThemeThumbnailCard(
    theme: KeyboardTheme,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val palette = ThemePalettes.get(theme)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(CardDark)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) AccentCyan else CardBorder,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(palette.background),
            contentAlignment = Alignment.Center
        ) {
            // Miniature preview keys
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                Box(
                    modifier = Modifier
                        .size(width = 16.dp, height = 22.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(palette.keyBackground)
                )
                Box(
                    modifier = Modifier
                        .size(width = 16.dp, height = 22.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(palette.actionKeyBackground)
                )
                Box(
                    modifier = Modifier
                        .size(width = 16.dp, height = 22.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(palette.keyBackground)
                )
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(AccentPurple)
                        .border(1.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = theme.title,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) AccentCyan else Color.White,
            maxLines = 1
        )
    }
}

// ======================================================================================
// SCREEN 6: KEYBOARD LAYOUT (Matching Screen 6 in screenshot with Live Preview)
// ======================================================================================
@Composable
fun KeyboardLayoutSettingsScreen(
    onBack: () -> Unit
) {
    var keyboardType by remember { mutableStateOf(BijoyApp.prefs.currentLayoutId) }
    var oneHandMode by remember { mutableStateOf(BijoyApp.prefs.isOneHandMode) }
    var keySize by remember { mutableStateOf(BijoyApp.prefs.keySize) }
    var keyPopup by remember { mutableStateOf(BijoyApp.prefs.isPopupEnabled) }
    var vibration by remember { mutableStateOf(BijoyApp.prefs.isVibrationEnabled) }
    var sound by remember { mutableStateOf(BijoyApp.prefs.isSoundEnabled) }

    var showLayoutPicker by remember { mutableStateOf(false) }
    var showSizePicker by remember { mutableStateOf(false) }

    // Live preview layout
    var previewLayout by remember { mutableStateOf(KeyboardLayoutType.fromId(keyboardType)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        // Top Bar with Back Arrow
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier
                    .size(26.dp)
                    .clickable { onBack() }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Keyboard Layout",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardDark)
                        .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                ) {
                    // 1. Keyboard Type
                    CyberActionRow(
                        title = "Keyboard Type",
                        value = previewLayout.displayName,
                        onClick = { showLayoutPicker = true }
                    )
                    Divider(color = CardBorder, thickness = 0.5.dp)

                    // 2. One Hand Mode
                    CyberToggleRow(
                        title = "One Hand Mode",
                        subtitle = "Move keyboard to left or right",
                        checked = oneHandMode,
                        onCheckedChange = {
                            oneHandMode = it
                            BijoyApp.prefs.isOneHandMode = it
                        }
                    )
                    Divider(color = CardBorder, thickness = 0.5.dp)

                    // 3. Key Size
                    CyberActionRow(
                        title = "Key Size",
                        value = keySize,
                        onClick = { showSizePicker = true }
                    )
                    Divider(color = CardBorder, thickness = 0.5.dp)

                    // 4. Key Pop-up
                    CyberToggleRow(
                        title = "Key Pop-up",
                        subtitle = "Show key preview",
                        checked = keyPopup,
                        onCheckedChange = {
                            keyPopup = it
                            BijoyApp.prefs.isPopupEnabled = it
                        }
                    )
                    Divider(color = CardBorder, thickness = 0.5.dp)

                    // 5. Vibration on Keypress
                    CyberToggleRow(
                        title = "Vibration on Keypress",
                        checked = vibration,
                        onCheckedChange = {
                            vibration = it
                            BijoyApp.prefs.isVibrationEnabled = it
                        }
                    )
                    Divider(color = CardBorder, thickness = 0.5.dp)

                    // 6. Sound on Keypress
                    CyberToggleRow(
                        title = "Sound on Keypress",
                        checked = sound,
                        onCheckedChange = {
                            sound = it
                            BijoyApp.prefs.isSoundEnabled = it
                        }
                    )
                    Divider(color = CardBorder, thickness = 0.5.dp)

                    // 7. Long Press Actions
                    CyberActionRow(
                        title = "Long Press Actions",
                        value = "Select, Copy, Pasting...",
                        onClick = {}
                    )
                }
            }
        }

        // --- Dialogs for Layout & Size ---
        if (showLayoutPicker) {
            AlertDialog(
                onDismissRequest = { showLayoutPicker = false },
                containerColor = CardDark,
                title = { Text("Select Keyboard Layout", color = Color.White) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            KeyboardLayoutType.ENGLISH,
                            KeyboardLayoutType.SMART_MOBILE,
                            KeyboardLayoutType.AVRO,
                            KeyboardLayoutType.BIJOY
                        ).forEach { lt ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        previewLayout = lt
                                        keyboardType = lt.id
                                        BijoyApp.prefs.currentLayoutId = lt.id
                                        showLayoutPicker = false
                                    }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(lt.displayName, color = Color.White)
                                if (previewLayout == lt) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = AccentCyan)
                                }
                            }
                        }
                    }
                },
                confirmButton = {}
            )
        }

        if (showSizePicker) {
            AlertDialog(
                onDismissRequest = { showSizePicker = false },
                containerColor = CardDark,
                title = { Text("Select Key Size", color = Color.White) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Small", "Normal", "Large").forEach { size ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        keySize = size
                                        BijoyApp.prefs.keySize = size
                                        showSizePicker = false
                                    }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(size, color = Color.White)
                                if (keySize == size) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = AccentCyan)
                                }
                            }
                        }
                    }
                },
                confirmButton = {}
            )
        }

        // --- Live Keyboard Preview at Bottom (Matching Screenshot 6) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardDark)
                .border(1.dp, CardBorder)
        ) {
            KeyboardScreen(
                currentLayout = previewLayout,
                isShifted = false,
                isCapsLock = false,
                actionLabel = "return",
                avroBuffer = "",
                avroCandidates = emptyList(),
                activeContactName = null,
                onKeyPressed = {},
                onCandidateSelected = {},
                onLayoutChanged = { previewLayout = it },
                onClipboardPaste = {},
                onOpenSettings = {}
            )
        }
    }
}

// ======================================================================================
// SCREEN 7: MORE OPTIONS (Matching Screen 7 in screenshot)
// ======================================================================================
@Composable
fun MoreOptionsScreen(
    context: Context,
    onNavigateToChatMemory: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToUpdates: () -> Unit
) {
    var emojiPanel by remember { mutableStateOf(BijoyApp.prefs.isEmojiPanelEnabled) }
    var clipboard by remember { mutableStateOf(BijoyApp.prefs.isClipboardEnabled) }
    var autoCaps by remember { mutableStateOf(BijoyApp.prefs.isAutoCapitalizationEnabled) }
    var autoCorrect by remember { mutableStateOf(BijoyApp.prefs.isAutoCorrectionEnabled) }
    var nextWord by remember { mutableStateOf(BijoyApp.prefs.isSuggestNextWordEnabled) }
    var numberRow by remember { mutableStateOf(BijoyApp.prefs.isShowNumberRow) }
    var smartMemory by remember { mutableStateOf(BijoyApp.prefs.isSmartChatMemoryEnabled) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "More Options",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardDark)
                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            ) {
                // 1. Emoji & GIF Panel
                CyberToggleIconRow(
                    icon = Icons.Default.EmojiEmotions,
                    iconBg = Color(0xFF7B2FFE),
                    title = "Emoji & GIF Panel",
                    subtitle = "Quick access to emojis & GIFs",
                    checked = emojiPanel,
                    onCheckedChange = {
                        emojiPanel = it
                        BijoyApp.prefs.isEmojiPanelEnabled = it
                    }
                )
                Divider(color = CardBorder, thickness = 0.5.dp)

                // 2. Clipboard
                CyberToggleIconRow(
                    icon = Icons.Default.ContentPaste,
                    iconBg = Color(0xFF00B4D8),
                    title = "Clipboard",
                    subtitle = "Paste copied text",
                    checked = clipboard,
                    onCheckedChange = {
                        clipboard = it
                        BijoyApp.prefs.isClipboardEnabled = it
                    }
                )
                Divider(color = CardBorder, thickness = 0.5.dp)

                // 3. Auto-Capitalization
                CyberToggleIconRow(
                    icon = Icons.Default.TextFields,
                    iconBg = Color(0xFF00E5FF),
                    title = "Auto-Capitalization",
                    subtitle = "Start sentences with caps",
                    checked = autoCaps,
                    onCheckedChange = {
                        autoCaps = it
                        BijoyApp.prefs.isAutoCapitalizationEnabled = it
                    }
                )
                Divider(color = CardBorder, thickness = 0.5.dp)

                // 4. Auto-Correction
                CyberToggleIconRow(
                    icon = Icons.Default.Spellcheck,
                    iconBg = Color(0xFFFF9100),
                    title = "Auto-Correction",
                    subtitle = "Fix typos automatically",
                    checked = autoCorrect,
                    onCheckedChange = {
                        autoCorrect = it
                        BijoyApp.prefs.isAutoCorrectionEnabled = it
                    }
                )
                Divider(color = CardBorder, thickness = 0.5.dp)

                // 5. Suggest Next Word
                CyberToggleIconRow(
                    icon = Icons.Default.Lightbulb,
                    iconBg = Color(0xFF00E676),
                    title = "Suggest Next Word",
                    subtitle = "Predict words while typing",
                    checked = nextWord,
                    onCheckedChange = {
                        nextWord = it
                        BijoyApp.prefs.isSuggestNextWordEnabled = it
                    }
                )
                Divider(color = CardBorder, thickness = 0.5.dp)

                // 6. Show Number Row
                CyberToggleIconRow(
                    icon = Icons.Default.Numbers,
                    iconBg = Color(0xFFFFD600),
                    title = "Show Number Row",
                    subtitle = "Always show numbers",
                    checked = numberRow,
                    onCheckedChange = {
                        numberRow = it
                        BijoyApp.prefs.isShowNumberRow = it
                    }
                )
                Divider(color = CardBorder, thickness = 0.5.dp)

                // 7. Per-Chat Language Memory (Messenger / WhatsApp)
                CyberToggleIconRow(
                    icon = Icons.Default.Forum,
                    iconBg = Color(0xFF7B2FFE),
                    title = "Per-Chat Language Memory",
                    subtitle = "Remember language per person in Messenger",
                    checked = smartMemory,
                    onCheckedChange = {
                        smartMemory = it
                        BijoyApp.prefs.isSmartChatMemoryEnabled = it
                    }
                )
            }
        }

        // Additional Manager Shortcuts
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardDark)
                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            ) {
                CyberMenuItem(
                    icon = Icons.Default.ChatBubbleOutline,
                    iconBg = Color(0xFF7B2FFE),
                    title = "Manage Saved Chat Languages",
                    trailingText = "${BijoyApp.chatManager.getAllSavedMappings().size} Saved",
                    onClick = onNavigateToChatMemory
                )
                Divider(color = CardBorder, thickness = 0.5.dp)

                CyberMenuItem(
                    icon = Icons.Default.History,
                    iconBg = Color(0xFF00B4D8),
                    title = "Typing & Clipboard History",
                    trailingText = "View Database",
                    onClick = onNavigateToHistory
                )
                Divider(color = CardBorder, thickness = 0.5.dp)

                CyberMenuItem(
                    icon = Icons.Default.SystemUpdate,
                    iconBg = Color(0xFF00E676),
                    title = "Check for Updates (OTA)",
                    trailingText = "GitHub Repository",
                    onClick = onNavigateToUpdates
                )
                Divider(color = CardBorder, thickness = 0.5.dp)

                // Reset Settings Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            BijoyApp.prefs.resetToDefaults()
                            emojiPanel = true
                            clipboard = true
                            autoCaps = true
                            autoCorrect = true
                            nextWord = true
                            numberRow = false
                            smartMemory = true
                            Toast.makeText(context, "Settings restored to defaults", Toast.LENGTH_SHORT).show()
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFF3D00).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFFFF3D00), modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Reset Settings", color = Color(0xFFFF5252), fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                            Text("Restore default settings", color = TextMuted, fontSize = 12.sp)
                        }
                    }
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(18.dp))
                }
            }
        }

        // Done Button matching screenshot
        item {
            Button(
                onClick = {
                    Toast.makeText(context, "Preferences saved!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Done", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

// ======================================================================================
// REUSABLE UI COMPONENTS
// ======================================================================================
@Composable
fun CyberMenuItem(
    icon: ImageVector,
    iconBg: Color,
    title: String,
    trailingText: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = trailingText,
                fontSize = 13.sp,
                color = TextMuted
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun CyberActionRow(
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(value, color = AccentCyan, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
fun CyberToggleRow(
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            if (subtitle != null) {
                Text(subtitle, color = TextMuted, fontSize = 12.sp)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AccentPurple
            )
        )
    }
}

@Composable
fun CyberToggleIconRow(
    icon: ImageVector,
    iconBg: Color,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = TextMuted, fontSize = 12.sp)
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AccentPurple
            )
        )
    }
}

// ======================================================================================
// SUBSCREEN: CHAT CONTEXT MEMORY
// ======================================================================================
@Composable
fun ChatMemorySubScreen(
    context: Context,
    onBack: () -> Unit
) {
    val chatManager = BijoyApp.chatManager
    var mappings by remember { mutableStateOf(chatManager.getAllSavedMappings()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier
                    .size(26.dp)
                    .clickable { onBack() }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Per-Chat Language Memory",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(CardDark)
                        .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "💡 আইফোনের মতো স্মার্ট মেমোরি",
                            fontWeight = FontWeight.Bold,
                            color = AccentCyan,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "মেসেঞ্জার, হোয়াটসঅ্যাপ বা ইমোতে নির্দিষ্ট ব্যক্তির সাথে যে ভাষা রাখবেন, পরে ঐ চ্যাটে ঢুকলেই সেই ভাষা স্বয়ংক্রিয়ভাবে সক্রিয় হবে।",
                            color = TextMuted,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Saved Chats (${mappings.size})",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    if (mappings.isNotEmpty()) {
                        TextButton(onClick = {
                            chatManager.clearAll()
                            mappings = emptyList()
                            Toast.makeText(context, "Memory cleared", Toast.LENGTH_SHORT).show()
                        }) {
                            Text("Clear All", color = Color(0xFFFF5252), fontSize = 12.sp)
                        }
                    }
                }
            }

            if (mappings.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Forum, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No chats recorded yet", color = TextMuted, fontSize = 14.sp)
                        }
                    }
                }
            } else {
                items(mappings, key = { it.key }) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardDark)
                            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.contactName, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            Text(item.packageName, color = TextMuted, fontSize = 11.sp)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AccentPurple)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(item.layoutId, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = {
                            chatManager.removeMapping(item.key)
                            mappings = chatManager.getAllSavedMappings()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFFF5252), modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

// ======================================================================================
// SUBSCREEN: TYPING HISTORY & CLIPBOARD
// ======================================================================================
@Composable
fun HistorySubScreen(
    context: Context,
    onBack: () -> Unit
) {
    val dbHelper = BijoyApp.historyDb
    var searchQuery by remember { mutableStateOf("") }
    var historyItems by remember { mutableStateOf(dbHelper.getAllRecords(searchQuery)) }
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier
                    .size(26.dp)
                    .clickable { onBack() }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Typing History & Clipboard",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        historyItems = dbHelper.getAllRecords(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search records...", color = TextMuted) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = AccentPurple,
                        unfocusedBorderColor = CardBorder,
                        focusedContainerColor = CardDark,
                        unfocusedContainerColor = CardDark
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Records (${historyItems.size})", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    if (historyItems.isNotEmpty()) {
                        TextButton(onClick = {
                            dbHelper.clearAllHistory()
                            historyItems = emptyList()
                            Toast.makeText(context, "All history cleared", Toast.LENGTH_SHORT).show()
                        }) {
                            Text("Clear All", color = Color(0xFFFF5252), fontSize = 12.sp)
                        }
                    }
                }
            }

            if (historyItems.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No typing history recorded", color = TextMuted, fontSize = 14.sp)
                    }
                }
            } else {
                items(historyItems, key = { it.id }) { record ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardDark)
                            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (record.isClipboard) "📋 Clipboard" else "⌨️ Typed",
                                color = AccentCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = dateFormat.format(Date(record.timestamp)),
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = record.content,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Copied", record.content))
                                    Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = AccentCyan, modifier = Modifier.size(18.dp))
                            }
                            IconButton(
                                onClick = {
                                    dbHelper.deleteRecord(record.id)
                                    historyItems = dbHelper.getAllRecords(searchQuery)
                                }
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFFF5252), modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ======================================================================================
// SUBSCREEN: OTA UPDATE MANAGER
// ======================================================================================
@Composable
fun OtaUpdateSubScreen(
    context: Context,
    onBack: () -> Unit
) {
    val currentVersion = remember { OtaUpdateManager.getCurrentVersion(context) }
    var updateStatus by remember { mutableStateOf<UpdateStatus>(UpdateStatus.Idle) }
    var downloadState by remember { mutableStateOf<DownloadState>(DownloadState.Idle) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier
                    .size(26.dp)
                    .clickable { onBack() }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "In-App Updates (OTA)",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardDark)
                .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Current Version", color = TextMuted, fontSize = 12.sp)
                        Text(currentVersion, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF00E676).copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Connected: shahin0964", color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Divider(color = CardBorder, thickness = 0.5.dp)

                when (val status = updateStatus) {
                    is UpdateStatus.Idle -> {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    updateStatus = UpdateStatus.Checking
                                    updateStatus = OtaUpdateManager.checkForUpdate(currentVersion)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Check for Updates", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                    is UpdateStatus.Checking -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(color = AccentCyan, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Checking GitHub repository...", color = Color.White, fontSize = 13.sp)
                        }
                    }
                    is UpdateStatus.NoUpdateAvailable -> {
                        Text("You're on the latest version! (${status.currentVersion})", color = Color(0xFF00E676), fontSize = 13.sp)
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    updateStatus = UpdateStatus.Checking
                                    updateStatus = OtaUpdateManager.checkForUpdate(currentVersion)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E284D)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Check Again", color = Color.White)
                        }
                    }
                    is UpdateStatus.UpdateAvailable -> {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("🚀 New Update Available: ${status.release.tagName}", color = AccentCyan, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            if (status.release.body.isNotBlank()) {
                                Text("Changelog:\n${status.release.body}", color = TextMuted, fontSize = 12.sp)
                            }

                            when (val ds = downloadState) {
                                is DownloadState.Idle -> {
                                    Button(
                                        onClick = {
                                            val apkAsset = status.release.assets.firstOrNull { it.name.endsWith(".apk") }
                                            if (apkAsset != null) {
                                                coroutineScope.launch {
                                                    OtaUpdateManager.downloadApk(
                                                        context = context,
                                                        downloadUrl = apkAsset.browserDownloadUrl,
                                                        fileName = apkAsset.name
                                                    ).collect { state ->
                                                        downloadState = state
                                                    }
                                                }
                                            } else {
                                                Toast.makeText(context, "No APK asset found in release", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Download and Install Update", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                                is DownloadState.Downloading -> {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        LinearProgressIndicator(
                                            progress = { (ds.progressPercent / 100f).coerceIn(0f, 1f) },
                                            modifier = Modifier.fillMaxWidth(),
                                            color = AccentCyan,
                                            trackColor = CardBorder
                                        )
                                        Text("Downloading... ${ds.progressPercent}%", color = Color.White, fontSize = 12.sp)
                                    }
                                }
                                is DownloadState.Success -> {
                                    Button(
                                        onClick = { OtaUpdateManager.installApk(context, ds.file) },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Default.DownloadDone, contentDescription = null, tint = Color.White)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Install APK Now", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                                is DownloadState.Error -> {
                                    Text("Download failed: ${ds.message}", color = Color(0xFFFF5252), fontSize = 12.sp)
                                }
                            }
                        }
                    }
                    is UpdateStatus.Error -> {
                        Text("Update check error: ${status.message}", color = Color(0xFFFF5252), fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
