package com.example.breezyquest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.breezyquest.data.model.HabitEntity
import com.example.breezyquest.data.model.QuestEntity
import com.example.breezyquest.data.model.QuestPriority
import com.example.breezyquest.data.model.QuestRepeat
import com.example.breezyquest.data.model.TransactionType
import com.example.ui.theme.BreezeCyan
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CardSurfaceVariant
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.EmeraldIncome
import com.example.ui.theme.PurpleMagic
import com.example.ui.theme.QuestGold
import com.example.ui.theme.RoseExpense
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

val DefaultIncomeCategories = listOf("Salary", "Freelance", "Business", "Allowance", "Gift", "Other")
val DefaultExpenseCategories = listOf(
    "Food", "Transport", "Education", "Shopping", "Entertainment",
    "Bills", "Technology", "Health", "Clothing", "Other"
)
val NoteCategories = listOf("Study", "Finance", "Ideas", "Planning", "Journal", "Important")

@Composable
fun AddTransactionDialog(
    initialType: TransactionType = TransactionType.EXPENSE,
    currency: String,
    onDismiss: () -> Unit,
    onConfirm: (title: String, amount: Double, type: TransactionType, category: String, note: String) -> Unit
) {
    var type by remember { mutableStateOf(initialType) }
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(if (type == TransactionType.INCOME) "Salary" else "Food") }
    var note by remember { mutableStateOf("") }
    var customCategory by remember { mutableStateOf("") }
    var isCustomCategory by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = DarkSurface,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (type == TransactionType.INCOME) "＋ Add Income" else "− Add Expense",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (type == TransactionType.INCOME) EmeraldIncome else RoseExpense
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Type Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardSurface, RoundedCornerShape(12.dp))
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (type == TransactionType.EXPENSE) RoseExpense else Color.Transparent,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                type = TransactionType.EXPENSE
                                category = "Food"
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "💸 Expense",
                            fontWeight = FontWeight.SemiBold,
                            color = if (type == TransactionType.EXPENSE) Color.White else TextSecondary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (type == TransactionType.INCOME) EmeraldIncome else Color.Transparent,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                type = TransactionType.INCOME
                                category = "Salary"
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "💰 Income",
                            fontWeight = FontWeight.SemiBold,
                            color = if (type == TransactionType.INCOME) Color.White else TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Amount
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount ($currency)") },
                    placeholder = { Text("e.g. 500") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("transaction_amount_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = if (type == TransactionType.INCOME) EmeraldIncome else RoseExpense
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(if (type == TransactionType.INCOME) "Income Source / Title" else "Expense Description") },
                    placeholder = { Text(if (type == TransactionType.INCOME) "e.g. Freelance project" else "e.g. Lunch at bistro") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("transaction_title_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = BreezeCyan
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Categories
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))

                val catList = if (type == TransactionType.INCOME) DefaultIncomeCategories else DefaultExpenseCategories

                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Column {
                        // Display 2 rows of chips
                        catList.chunked(4).forEach { rowCats ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                rowCats.forEach { catName ->
                                    val isSelected = !isCustomCategory && category == catName
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            category = catName
                                            isCustomCategory = false
                                        },
                                        label = { Text(catName, fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = if (type == TransactionType.INCOME) EmeraldIncome else BreezeCyan,
                                            selectedLabelColor = Color.Black
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Custom category field
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    OutlinedTextField(
                        value = customCategory,
                        onValueChange = {
                            customCategory = it
                            isCustomCategory = it.isNotBlank()
                        },
                        label = { Text("Custom Category", fontSize = 12.sp) },
                        placeholder = { Text("e.g. Subscriptions") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Note
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note (Optional)") },
                    placeholder = { Text("Add any extra details...") },
                    singleLine = false,
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val amt = amountText.toDoubleOrNull() ?: 0.0
                        if (amt > 0) {
                            val finalCat = if (isCustomCategory && customCategory.isNotBlank()) customCategory else category
                            val finalTitle = title.ifBlank { finalCat }
                            onConfirm(finalTitle, amt, type, finalCat, note)
                            onDismiss()
                        }
                    },
                    enabled = amountText.toDoubleOrNull() != null && amountText.toDouble() > 0,
                    modifier = Modifier.fillMaxWidth().height(50.dp).testTag("save_transaction_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (type == TransactionType.INCOME) EmeraldIncome else RoseExpense
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (type == TransactionType.INCOME) "Record Income (+30 XP)" else "Record Expense",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun AddQuestDialog(
    initialQuest: QuestEntity? = null,
    initialDate: String = "",
    onDismiss: () -> Unit,
    onConfirmWithDate: ((
        title: String,
        description: String,
        date: String,
        startTime: String,
        dueTime: String,
        priority: QuestPriority,
        category: String,
        repeat: QuestRepeat,
        xpReward: Int
    ) -> Unit)? = null,
    onConfirm: (
        title: String,
        description: String,
        startTime: String,
        dueTime: String,
        priority: QuestPriority,
        category: String,
        repeat: QuestRepeat,
        xpReward: Int
    ) -> Unit = { _, _, _, _, _, _, _, _ -> }
) {
    val todayDefault = remember { java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date()) }
    var questDate by remember { mutableStateOf(if (initialDate.isNotBlank()) initialDate else todayDefault) }
    var title by remember { mutableStateOf(initialQuest?.title ?: "") }
    var description by remember { mutableStateOf(initialQuest?.description ?: "") }
    var startTime by remember { mutableStateOf(initialQuest?.startTime ?: "07:00 PM") }
    var dueTime by remember { mutableStateOf(initialQuest?.dueTime ?: "08:30 PM") }
    var priority by remember { mutableStateOf(initialQuest?.priority ?: QuestPriority.MEDIUM) }
    var category by remember { mutableStateOf(initialQuest?.category ?: "Study") }
    var repeat by remember { mutableStateOf(initialQuest?.repeat ?: QuestRepeat.NONE) }
    var xpReward by remember { mutableStateOf(initialQuest?.xpReward ?: 35) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = DarkSurface,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (initialQuest != null) "⚔️ Edit Quest" else "⚔️ Create Quest",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = QuestGold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Quest Title") },
                    placeholder = { Text("e.g. Complete Physics Chapter 3") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("quest_title_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = QuestGold
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = questDate,
                    onValueChange = { questDate = it },
                    label = { Text("Scheduled Date (YYYY-MM-DD)") },
                    placeholder = { Text(todayDefault) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("quest_date_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = BreezeCyan
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    placeholder = { Text("What needs to be accomplished?") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Start Time ⏰") },
                        placeholder = { Text("07:00 PM") },
                        modifier = Modifier.weight(1f).testTag("quest_start_time_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = BreezeCyan
                        )
                    )
                    OutlinedTextField(
                        value = dueTime,
                        onValueChange = { dueTime = it },
                        label = { Text("Due Time ⌛") },
                        placeholder = { Text("08:30 PM") },
                        modifier = Modifier.weight(1f).testTag("quest_due_time_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }
                Text(
                    text = "🔔 An alert will trigger on your lock screen when this quest kicks off",
                    fontSize = 11.sp,
                    color = BreezeCyan,
                    modifier = Modifier.padding(top = 4.dp, start = 2.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Priority
                Text("Priority Level", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    QuestPriority.values().forEach { p ->
                        FilterChip(
                            selected = priority == p,
                            onClick = {
                                priority = p
                                xpReward = when (p) {
                                    QuestPriority.LOW -> 20
                                    QuestPriority.MEDIUM -> 35
                                    QuestPriority.HIGH -> 50
                                    QuestPriority.EPIC -> 75
                                }
                            },
                            label = { Text(p.name, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = when (p) {
                                    QuestPriority.LOW -> BreezeCyan
                                    QuestPriority.MEDIUM -> QuestGold
                                    QuestPriority.HIGH -> RoseExpense
                                    QuestPriority.EPIC -> PurpleMagic
                                },
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Repeat
                Text("Repeat Schedule", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    QuestRepeat.values().forEach { r ->
                        FilterChip(
                            selected = repeat == r,
                            onClick = { repeat = r },
                            label = { Text(if (r == QuestRepeat.NONE) "One-time" else r.name.lowercase().capitalize(), fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BreezeCyan,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // XP Reward Display
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardSurface, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⭐ XP Reward upon completion:", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                        Text("+$xpReward XP", fontWeight = FontWeight.Bold, color = QuestGold, fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            if (onConfirmWithDate != null) {
                                onConfirmWithDate(
                                    title,
                                    description,
                                    questDate.ifBlank { todayDefault },
                                    startTime,
                                    dueTime,
                                    priority,
                                    category,
                                    repeat,
                                    xpReward
                                )
                            } else {
                                onConfirm(title, description, startTime, dueTime, priority, category, repeat, xpReward)
                            }
                            onDismiss()
                        }
                    },
                    enabled = title.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(50.dp).testTag("save_quest_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = QuestGold),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Deploy Quest ⚔️", fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun AddHabitDialog(
    initialHabit: HabitEntity? = null,
    onDismiss: () -> Unit,
    onConfirm: (name: String, emoji: String, frequency: String, reminderTime: String, colorHex: String) -> Unit
) {
    var name by remember { mutableStateOf(initialHabit?.name ?: "") }
    var emoji by remember { mutableStateOf(initialHabit?.iconEmoji ?: "🔥") }
    var frequency by remember { mutableStateOf(initialHabit?.frequency ?: "Daily") }
    var reminderTime by remember { mutableStateOf(initialHabit?.reminderTime ?: "08:00 AM") }
    var colorHex by remember { mutableStateOf(initialHabit?.colorHex ?: "#06B6D4") }

    val popularHabits = listOf(
        "Study 2 hours" to "📚",
        "Exercise" to "🏃",
        "Read 20 pages" to "📖",
        "Drink 3L water" to "💧",
        "Sleep before 11 PM" to "🌙",
        "Practice coding" to "💻",
        "Save money" to "💰"
    )

    val colorOptions = listOf("#06B6D4", "#10B981", "#F59E0B", "#8B5CF6", "#EC4899", "#3B82F6")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = DarkSurface,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (initialHabit != null) "🔥 Edit Habit" else "🔥 Add Habit",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = BreezeCyan
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick suggestions
                Text("Popular Habits", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Column {
                        popularHabits.chunked(3).forEach { row ->
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                row.forEach { (hName, hEmoji) ->
                                    FilterChip(
                                        selected = name == hName,
                                        onClick = {
                                            name = hName
                                            emoji = hEmoji
                                        },
                                        label = { Text("$hEmoji $hName", fontSize = 11.sp) }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Habit Name") },
                    placeholder = { Text("e.g. Daily Meditation") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("habit_name_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = BreezeCyan
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = emoji,
                        onValueChange = { emoji = it },
                        label = { Text("Icon Emoji") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                    OutlinedTextField(
                        value = reminderTime,
                        onValueChange = { reminderTime = it },
                        label = { Text("Reminder") },
                        modifier = Modifier.weight(1.5f),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Color picker
                Text("Color Accent", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    colorOptions.forEach { hex ->
                        val parsedColor = Color(android.graphics.Color.parseColor(hex))
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(parsedColor, CircleShape)
                                .clickable { colorHex = hex }
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (colorHex == hex) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(Color.White, CircleShape)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            onConfirm(name, emoji, frequency, reminderTime, colorHex)
                            onDismiss()
                        }
                    },
                    enabled = name.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(50.dp).testTag("save_habit_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = BreezeCyan),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Start Habit Journey 🔥", fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun AddBudgetDialog(
    existingCategories: List<String>,
    currency: String,
    onDismiss: () -> Unit,
    onConfirm: (category: String, limit: Double) -> Unit
) {
    var category by remember { mutableStateOf("Food") }
    var customCategory by remember { mutableStateOf("") }
    var isCustom by remember { mutableStateOf(false) }
    var limitText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Category Budget", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Choose category:", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Column {
                        listOf("Food", "Transport", "Entertainment", "Education", "Shopping", "Bills").chunked(3).forEach { row ->
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                row.forEach { c ->
                                    FilterChip(
                                        selected = !isCustom && category == c,
                                        onClick = {
                                            category = c
                                            isCustom = false
                                        },
                                        label = { Text(c, fontSize = 11.sp) }
                                    )
                                }
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = customCategory,
                    onValueChange = {
                        customCategory = it
                        isCustom = it.isNotBlank()
                    },
                    label = { Text("Or Enter Custom Category") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = limitText,
                    onValueChange = { limitText = it },
                    label = { Text("Monthly Budget Limit ($currency)") },
                    placeholder = { Text("e.g. 10000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val limit = limitText.toDoubleOrNull() ?: 0.0
                    val finalCat = if (isCustom && customCategory.isNotBlank()) customCategory else category
                    if (limit > 0) {
                        onConfirm(finalCat, limit)
                        onDismiss()
                    }
                },
                enabled = limitText.toDoubleOrNull() != null && limitText.toDouble() > 0
            ) {
                Text("Set Budget")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        containerColor = DarkSurface
    )
}

@Composable
fun EditBalanceDialog(
    currentBalance: Double,
    currency: String,
    onDismiss: () -> Unit,
    onConfirm: (newBalance: Double) -> Unit
) {
    var balanceText by remember { mutableStateOf(currentBalance.toInt().toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Current Balance", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    "Enter your actual available starting balance. It will automatically adjust as you add income or expenses.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = balanceText,
                    onValueChange = { balanceText = it },
                    label = { Text("Balance ($currency)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newBal = balanceText.toDoubleOrNull() ?: currentBalance
                    onConfirm(newBal)
                    onDismiss()
                }
            ) {
                Text("Save Balance")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        containerColor = DarkSurface
    )
}

@Composable
fun EditEarningTargetDialog(
    currentTarget: Double,
    currency: String,
    onDismiss: () -> Unit,
    onConfirm: (newTarget: Double) -> Unit
) {
    var targetText by remember { mutableStateOf(currentTarget.toInt().toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("🎯 Monthly Earning Target", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    "Set your income target for this month. Every income entry will automatically update your completion progress.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = targetText,
                    onValueChange = { targetText = it },
                    label = { Text("Earning Target ($currency)") },
                    placeholder = { Text("e.g. 50000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newTgt = targetText.toDoubleOrNull() ?: currentTarget
                    if (newTgt > 0) {
                        onConfirm(newTgt)
                        onDismiss()
                    }
                }
            ) {
                Text("Save Target")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        containerColor = DarkSurface
    )
}
