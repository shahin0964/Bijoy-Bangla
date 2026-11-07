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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bijoy.bangla.BijoyApp
import com.bijoy.bangla.data.HistoryItem
import com.bijoy.bangla.data.KeyboardTheme
import com.bijoy.bangla.data.SavedChatLayout
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BijoyBanglaApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BijoyBanglaApp() {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) } // 0: Settings, 1: Chat Memory, 2: History

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Bijoy Bangla",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF2F2F7),
                    titleContentColor = Color.Black
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFFFFFFFF),
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("সেটিংস") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.ChatBubbleOutline, contentDescription = "Memory") },
                    label = { Text("চ্যাট মেমোরি") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.History, contentDescription = "History") },
                    label = { Text("হিস্ট্রি") }
                )
            }
        },
        containerColor = Color(0xFFF2F2F7)
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> SettingsScreen(context)
                1 -> ChatMemoryScreen(context)
                2 -> HistoryScreen(context)
            }
        }
    }
}

@Composable
fun SettingsScreen(context: Context) {
    val prefs = BijoyApp.prefs
    var theme by remember { mutableStateOf(prefs.theme) }
    var soundEnabled by remember { mutableStateOf(prefs.isSoundEnabled) }
    var vibrationEnabled by remember { mutableStateOf(prefs.isVibrationEnabled) }
    var popupEnabled by remember { mutableStateOf(prefs.isPopupEnabled) }
    var chatMemoryEnabled by remember { mutableStateOf(prefs.isSmartChatMemoryEnabled) }
    var testText by remember { mutableStateOf("") }

    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Setup Wizard Card ---
        item {
            IosGroupCard(title = "কীবোর্ড সেটআপ গাইড") {
                IosActionRow(
                    title = "১. সিস্টেমে কীবোর্ড চালু করুন",
                    subtitle = "অ্যান্ড্রয়েড সেটিংস থেকে Bijoy Bangla অন করুন",
                    icon = Icons.Default.ToggleOn,
                    iconTint = Color(0xFF007AFF)
                ) {
                    val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
                    context.startActivity(intent)
                }

                Divider(color = Color(0xFFE5E5EA), thickness = 0.5.dp)

                IosActionRow(
                    title = "২. ডিফল্ট কীবোর্ড নির্বাচন করুন",
                    subtitle = "Bijoy Bangla কে মূল কীবোর্ড হিসেবে সেট করুন",
                    icon = Icons.Default.Keyboard,
                    iconTint = Color(0xFF34C759)
                ) {
                    imm.showInputMethodPicker()
                }

                Divider(color = Color(0xFFE5E5EA), thickness = 0.5.dp)

                IosActionRow(
                    title = "৩. চ্যাট মেমোরি পারমিশন",
                    subtitle = "Accessibility অন করে নির্দিষ্ট চ্যাট মনে রাখুন",
                    icon = Icons.Default.Security,
                    iconTint = Color(0xFFFF9500)
                ) {
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    context.startActivity(intent)
                }
            }
        }

        // --- Interactive Keyboard Test Box ---
        item {
            IosGroupCard(title = "কীবোর্ড লাইভ টেস্ট করুন") {
                OutlinedTextField(
                    value = testText,
                    onValueChange = { testText = it },
                    placeholder = { Text("এখানে ক্লিক করে কীবোর্ড ট্রাই করুন...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        // --- Themes ---
        item {
            IosGroupCard(title = "আইফোন থিম ও লুক") {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("পছন্দের iOS থিম সিলেক্ট করুন", fontSize = 13.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        KeyboardTheme.values().forEach { t ->
                            val isSelected = theme == t
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Color(0xFF007AFF) else Color(0xFFE5E5EA))
                                    .clickable {
                                        theme = t
                                        prefs.theme = t
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = t.title,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- Sound & Haptics ---
        item {
            IosGroupCard(title = "সাউন্ড ও ফিডব্যাক") {
                IosSwitchRow(
                    title = "iOS কীবোর্ড সাউন্ড",
                    checked = soundEnabled,
                    icon = Icons.Default.VolumeUp,
                    iconTint = Color(0xFF007AFF),
                    onCheckedChange = {
                        soundEnabled = it
                        prefs.isSoundEnabled = it
                    }
                )

                Divider(color = Color(0xFFE5E5EA), thickness = 0.5.dp)

                IosSwitchRow(
                    title = "হ্যাপটিক ভাইব্রেশন",
                    checked = vibrationEnabled,
                    icon = Icons.Default.Vibration,
                    iconTint = Color(0xFFFF2D55),
                    onCheckedChange = {
                        vibrationEnabled = it
                        prefs.isVibrationEnabled = it
                    }
                )

                Divider(color = Color(0xFFE5E5EA), thickness = 0.5.dp)

                IosSwitchRow(
                    title = "কী প্রেস পপআপ প্রিভিউ",
                    checked = popupEnabled,
                    icon = Icons.Default.TouchApp,
                    iconTint = Color(0xFF5856D6),
                    onCheckedChange = {
                        popupEnabled = it
                        prefs.isPopupEnabled = it
                    }
                )
            }
        }

        // --- Features ---
        item {
            IosGroupCard(title = "স্মার্ট ফিচার") {
                IosSwitchRow(
                    title = "ব্যক্তি ভিত্তিক স্মার্ট চ্যাট মেমোরি",
                    checked = chatMemoryEnabled,
                    icon = Icons.Default.Psychology,
                    iconTint = Color(0xFF34C759),
                    onCheckedChange = {
                        chatMemoryEnabled = it
                        prefs.isSmartChatMemoryEnabled = it
                    }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

@Composable
fun ChatMemoryScreen(context: Context) {
    val chatManager = BijoyApp.chatManager
    var mappings by remember { mutableStateOf(chatManager.getAllSavedMappings()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F2FF)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = Color(0xFF007AFF),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "আইফোনের মতো প্রতিটি চ্যাটের ভাষা স্বয়ংক্রিয়ভাবে সেভ থাকে। মেসেঞ্জার, হোয়াটসঅ্যাপ বা ইমোতে যে ব্যক্তির সাথে যে ভাষা রাখবেন, পরে চ্যাটে ঢুকলেই সেই ভাষা লোড হবে।",
                        fontSize = 13.sp,
                        color = Color(0xFF1C3A63),
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
                    text = "সংরক্ষিত চ্যাট তালিকা (${mappings.size})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                if (mappings.isNotEmpty()) {
                    TextButton(onClick = {
                        chatManager.clearAll()
                        mappings = emptyList()
                        Toast.makeText(context, "সব মেমোরি ক্লিয়ার করা হয়েছে", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("সব মুছুন", color = Color.Red, fontSize = 13.sp)
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
                        Icon(
                            imageVector = Icons.Default.Forum,
                            contentDescription = "Empty",
                            tint = Color.LightGray,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "এখনও কোনো চ্যাটের ভাষা সেভ হয়নি",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "মেসেঞ্জার/হোয়াটসঅ্যাপে চ্যাট করার সময় কীবোর্ড ভাষা পাল্টালে এখানে দেখতে পাবেন",
                            color = Color.LightGray,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        } else {
            items(mappings) { item ->
                SavedChatCard(item) {
                    chatManager.removeMapping(item.key)
                    mappings = chatManager.getAllSavedMappings()
                }
            }
        }
    }
}

@Composable
fun SavedChatCard(item: SavedChatLayout, onDelete: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.contactName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Text(
                    text = item.packageName,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFE5F1FF))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = item.layoutId,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF007AFF)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryScreen(context: Context) {
    val dbHelper = BijoyApp.dbHelper
    var searchQuery by remember { mutableStateOf("") }
    var historyItems by remember { mutableStateOf(dbHelper.getAllRecords(searchQuery)) }
    var isHistoryEnabled by remember { mutableStateOf(BijoyApp.prefs.isHistoryEnabled) }

    val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Search & Privacy Switch
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    IosSwitchRow(
                        title = "টাইপিং হিস্ট্রি সংরক্ষণ চালু রাখুন",
                        checked = isHistoryEnabled,
                        icon = Icons.Default.HistoryEdu,
                        iconTint = Color(0xFF007AFF),
                        onCheckedChange = {
                            isHistoryEnabled = it
                            BijoyApp.prefs.isHistoryEnabled = it
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            historyItems = dbHelper.getAllRecords(it)
                        },
                        placeholder = { Text("হিস্ট্রি সার্চ করুন...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
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
                    text = "রেকর্ডসমূহ (${historyItems.size})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                if (historyItems.isNotEmpty()) {
                    TextButton(onClick = {
                        dbHelper.clearAllHistory()
                        historyItems = emptyList()
                        Toast.makeText(context, "সব হিস্ট্রি ক্লিয়ার হয়েছে", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("সব মুছুন", color = Color.Red, fontSize = 13.sp)
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = "Empty",
                            tint = Color.LightGray,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "কোনো হিস্ট্রি পাওয়া যায়নি",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        } else {
            items(historyItems) { record ->
                HistoryRecordCard(
                    record = record,
                    dateFormatted = dateFormat.format(Date(record.timestamp)),
                    onCopy = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Copied Text", record.content))
                        Toast.makeText(context, "কপি করা হয়েছে!", Toast.LENGTH_SHORT).show()
                    },
                    onDelete = {
                        dbHelper.deleteRecord(record.id)
                        historyItems = dbHelper.getAllRecords(searchQuery)
                    }
                )
            }
        }
    }
}

@Composable
fun HistoryRecordCard(
    record: HistoryItem,
    dateFormatted: String,
    onCopy: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFE5E5EA))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = record.layoutUsed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                    }
                    if (record.contactName != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "👤 ${record.contactName}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF007AFF)
                        )
                    }
                }

                Text(
                    text = dateFormatted,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = record.content,
                fontSize = 15.sp,
                color = Color.Black,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onCopy, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = Color(0xFF007AFF),
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = Color.Red,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// Grouped iOS Card Component
@Composable
fun IosGroupCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title.uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Gray,
            modifier = Modifier.padding(start = 12.dp, bottom = 6.dp)
        )
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(content = content)
        }
    }
}

@Composable
fun IosActionRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Open",
            tint = Color(0xFFC7C7CC)
        )
    }
}

@Composable
fun IosSwitchRow(
    title: String,
    checked: Boolean,
    icon: ImageVector,
    iconTint: Color,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                fontSize = 15.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF34C759)
            )
        )
    }
}
