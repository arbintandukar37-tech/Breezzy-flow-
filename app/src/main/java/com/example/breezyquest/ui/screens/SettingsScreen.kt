package com.example.breezyquest.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.breezyquest.ui.viewmodel.MainViewModel
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.BreezeCyan
import com.example.ui.theme.CardSurface
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.EmeraldIncome
import com.example.ui.theme.QuestGold
import com.example.ui.theme.RoseExpense
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.ThemeMode
import com.example.ui.theme.VibrantLightBlue
import com.example.ui.theme.VibrantLightPink

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val metrics by viewModel.financialMetrics.collectAsState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var showPinDialog by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }
    var showExportDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    val currencies = listOf("Rs.", "$", "€", "£", "₹", "¥")

    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = { showPinDialog = false },
            title = { Text("Set 4-Digit Security PIN", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Enter a 4-digit code to protect your financial and habit logs.", fontSize = 13.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = { if (it.length <= 4) pinInput = it },
                        label = { Text("4-Digit PIN") },
                        placeholder = { Text("1234") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("pin_setup_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (pinInput.length == 4) {
                            viewModel.setAppLock(true, pinInput)
                            showPinDialog = false
                            Toast.makeText(context, "PIN lock enabled!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = pinInput.length == 4
                ) {
                    Text("Save PIN")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinDialog = false }) { Text("Cancel") }
            },
            containerColor = DarkSurface
        )
    }

    if (showExportDialog) {
        val exportJson = viewModel.exportDataAsJson()
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("Data Export & Backup", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Your complete local database in JSON format. Copy to clipboard for safe-keeping:", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(DeepObsidian, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(exportJson.take(500) + "...", fontSize = 10.sp, color = TextPrimary)
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    clipboardManager.setText(AnnotatedString(exportJson))
                    Toast.makeText(context, "Copied backup JSON to clipboard!", Toast.LENGTH_SHORT).show()
                    showExportDialog = false
                }) {
                    Text("Copy to Clipboard")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) { Text("Close") }
            },
            containerColor = DarkSurface
        )
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Sample Data?", fontWeight = FontWeight.Bold, color = RoseExpense) },
            text = {
                Text("This will restore default sample transactions, quests, habits, and budgets.", color = TextSecondary)
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetDataToSample()
                        showResetDialog = false
                        Toast.makeText(context, "Restored default sample data!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoseExpense)
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text("Cancel") }
            },
            containerColor = DarkSurface
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DeepObsidian),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⚙️", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("App Settings & Preferences", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                    }
                    Text("Customize theme, currency, security, and preferences", fontSize = 12.sp, color = TextSecondary)
                }
            }
        }

        // Appearance & Theme Setting
        item {
            val themeMode by viewModel.themeMode.collectAsState()
            Card(
                modifier = Modifier.fillMaxWidth().testTag("appearance_theme_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Palette, contentDescription = null, tint = BreezeCyan)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Theme & Appearance", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Choose your preferred style and visual accents", fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))

                    // Light Mode Option
                    ThemeOptionCard(
                        title = "☀️ Light Mode",
                        description = "Vibrant light blue, light pink, crisp white & cool grey",
                        selected = themeMode == ThemeMode.LIGHT,
                        previewColors = listOf(
                            Color(0xFF0284C7), // Light Blue
                            Color(0xFFEC4899), // Light Pink
                            Color(0xFFFFFFFF), // White
                            Color(0xFF94A3B8)  // Grey
                        ),
                        onClick = { viewModel.setThemeMode(ThemeMode.LIGHT) },
                        testTag = "theme_option_light"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Dark Mode Option
                    ThemeOptionCard(
                        title = "🌙 Dark Mode",
                        description = "Deep obsidian canvas with neon cyan & quest gold",
                        selected = themeMode == ThemeMode.DARK,
                        previewColors = listOf(
                            Color(0xFF0A0E17), // Deep Obsidian
                            Color(0xFF06B6D4), // Breeze Cyan
                            Color(0xFFF59E0B), // Quest Gold
                            Color(0xFF243248)  // Card Slate
                        ),
                        onClick = { viewModel.setThemeMode(ThemeMode.DARK) },
                        testTag = "theme_option_dark"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // System Default Option
                    ThemeOptionCard(
                        title = "🌓 System Default",
                        description = "Automatically match your Android device's system theme",
                        selected = themeMode == ThemeMode.SYSTEM,
                        previewColors = listOf(
                            Color(0xFF0284C7),
                            Color(0xFF06B6D4),
                            Color(0xFF64748B),
                            Color(0xFFE2E8F0)
                        ),
                        onClick = { viewModel.setThemeMode(ThemeMode.SYSTEM) },
                        testTag = "theme_option_system"
                    )
                }
            }
        }

        // Currency Setting
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AttachMoney, contentDescription = null, tint = BreezeCyan)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Currency Symbol", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Select your primary currency representation:", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        currencies.forEach { sym ->
                            FilterChip(
                                selected = metrics.currency == sym,
                                onClick = { viewModel.setCurrency(sym) },
                                label = { Text(sym, fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BreezeCyan,
                                    selectedLabelColor = Color.Black
                                )
                            )
                        }
                    }
                }
            }
        }

        // Security & App Lock
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("app_lock_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f).padding(end = 8.dp)
                        ) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = QuestGold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("4-Digit PIN Lock", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(if (userProfile?.isPinEnabled == true) "Protected by 4-digit code" else "Disabled", fontSize = 12.sp, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }

                        Switch(
                            checked = userProfile?.isPinEnabled == true,
                            onCheckedChange = { enabled ->
                                if (enabled) {
                                    showPinDialog = true
                                } else {
                                    viewModel.setAppLock(false, "")
                                    Toast.makeText(context, "PIN Lock turned off", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = QuestGold, checkedTrackColor = QuestGold.copy(alpha = 0.5f))
                        )
                    }
                }
            }
        }

        // Dedicated Notification Preferences & Controls
        item {
            val questStartReminders by viewModel.questStartReminders.collectAsState()
            val questLeadReminder5Min by viewModel.questLeadReminder5Min.collectAsState()

            Card(
                modifier = Modifier.fillMaxWidth().testTag("notification_preferences_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = BreezeCyan)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Notification Preferences", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Full control over scheduled quest alerts and timing", fontSize = 12.sp, color = TextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 1. Quest Start Reminders Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                            Text("Quest Start Reminders", fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 14.sp)
                            Text(
                                "Receive an instantly recognizable alert on your lock screen and notification tray the moment any scheduled quest kicks off",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                lineHeight = 16.sp
                            )
                        }
                        Switch(
                            checked = questStartReminders,
                            onCheckedChange = { enabled ->
                                viewModel.setQuestStartReminders(enabled)
                                Toast.makeText(
                                    context,
                                    if (enabled) "Quest Start Reminders Enabled" else "Quest Start Reminders Disabled",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = BreezeCyan,
                                checkedTrackColor = BreezeCyan.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.testTag("quest_start_reminders_switch")
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = BorderSubtle.copy(alpha = 0.5f))

                    // 2. Remind me 5 minutes before Toggle (Optional)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Remind me 5 minutes before", fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = CircleShape,
                                    color = QuestGold.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "PREPARE",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = QuestGold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                "Get an early 5-minute heads-up so you have time to prepare your setup, notes, and environment before quest kickoff",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                lineHeight = 16.sp
                            )
                        }
                        Switch(
                            checked = questLeadReminder5Min,
                            enabled = questStartReminders,
                            onCheckedChange = { enabled ->
                                viewModel.setQuestLeadReminder5Min(enabled)
                                Toast.makeText(
                                    context,
                                    if (enabled) "5-Min Early Prep Reminder Enabled" else "5-Min Early Prep Reminder Disabled",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = QuestGold,
                                checkedTrackColor = QuestGold.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.testTag("quest_lead_reminder_5min_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Live Test Banners for Instant Lock Screen / Notification Tray Verification
                    Text("Instant Notification Preview & Test:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.testNotification()
                                Toast.makeText(context, "⚡ Sent Quest Kickoff alert to tray!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f).testTag("test_quest_kickoff_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BreezeCyan)
                        ) {
                            Text("⚔️ Test Kickoff", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.testLeadNotification(leadMinutes = 5)
                                Toast.makeText(context, "⏳ Sent 5-min prep alert to tray!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f).testTag("test_lead_reminder_button"),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, QuestGold)
                        ) {
                            Text("⏳ Test 5-Min Prep", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = QuestGold)
                        }
                    }
                }
            }
        }

        // Data Management
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Data Management", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showExportDialog = true }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, tint = BreezeCyan)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Backup & Export Data", fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            Text("Export all transactions, quests, and habits to JSON", fontSize = 11.sp, color = TextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showResetDialog = true }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = RoseExpense)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Restore Sample Demo Data", fontWeight = FontWeight.SemiBold, color = RoseExpense)
                            Text("Re-populate initial sample database entries", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }
            }
        }

        // About
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = BreezeCyan)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("About Breezy Quest", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Version 1.0.0 (Release)", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    Text("100% Offline-First • Room Database • Jetpack Compose • Zero Tracking", fontSize = 12.sp, color = TextSecondary)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(40.dp)) }
    }
}

@Composable
private fun ThemeOptionCard(
    title: String,
    description: String,
    selected: Boolean,
    previewColors: List<Color>,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(12.dp),
        color = if (selected) BreezeCyan.copy(alpha = 0.12f) else CardSurface,
        border = if (selected) BorderStroke(2.dp, BreezeCyan) else BorderStroke(1.dp, BorderSubtle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        color = if (selected) BreezeCyan else TextPrimary,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (selected) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = CircleShape,
                            color = BreezeCyan
                        ) {
                            Text(
                                text = "ACTIVE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Palette Preview Dots
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    previewColors.forEach { col ->
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(col, CircleShape)
                                .border(1.dp, Color.Gray.copy(alpha = 0.3f), CircleShape)
                        )
                    }
                }
            }

            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = BreezeCyan,
                    unselectedColor = TextMuted
                )
            )
        }
    }
}

