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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EventNote
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.breezyquest.data.model.QuestPriority
import com.example.ui.theme.BreezeCyan
import com.example.ui.theme.CardSurface
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.EmeraldIncome
import com.example.ui.theme.PurpleMagic
import com.example.ui.theme.QuestGold
import com.example.ui.theme.RoseExpense
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

enum class MonthSchedulePattern(val title: String, val description: String) {
    EVERY_DAY("Every Day", "Schedules a quest for every single day in the month"),
    WEEKDAYS("Weekdays Only", "Monday to Friday (School & Work days)"),
    WEEKENDS("Weekends", "Saturday & Sunday"),
    MON_WED_FRI("Mon, Wed, Fri", "Alternating focus days"),
    TUE_THU_SAT("Tue, Thu, Sat", "Alternating secondary days"),
    WEEKLY("Once a Week", "Repeats weekly on the selected day")
}

@Composable
fun ScheduleMonthQuestsDialog(
    initialYear: Int,
    initialMonth: Int, // 0-indexed
    onDismiss: () -> Unit,
    onConfirm: (
        title: String,
        description: String,
        startTime: String,
        dueTime: String,
        priority: QuestPriority,
        category: String,
        pattern: MonthSchedulePattern,
        year: Int,
        month: Int,
        xpReward: Int
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("07:30 AM") }
    var dueTime by remember { mutableStateOf("09:00 AM") }
    var priority by remember { mutableStateOf(QuestPriority.MEDIUM) }
    var category by remember { mutableStateOf("Study") }
    var selectedPattern by remember { mutableStateOf(MonthSchedulePattern.EVERY_DAY) }
    var xpReward by remember { mutableStateOf(30) }

    val monthCalendar = remember(initialYear, initialMonth) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, initialYear)
        cal.set(Calendar.MONTH, initialMonth)
        cal
    }
    val monthName = remember(monthCalendar) {
        SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(monthCalendar.time)
    }
    val daysInMonth = remember(monthCalendar) {
        monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    // Estimate count based on pattern
    val estimatedQuestsCount = remember(selectedPattern, daysInMonth) {
        val cal = monthCalendar.clone() as Calendar
        var count = 0
        for (d in 1..daysInMonth) {
            cal.set(Calendar.DAY_OF_MONTH, d)
            val dow = cal.get(Calendar.DAY_OF_WEEK)
            val matches = when (selectedPattern) {
                MonthSchedulePattern.EVERY_DAY -> true
                MonthSchedulePattern.WEEKDAYS -> dow in Calendar.MONDAY..Calendar.FRIDAY
                MonthSchedulePattern.WEEKENDS -> dow == Calendar.SATURDAY || dow == Calendar.SUNDAY
                MonthSchedulePattern.MON_WED_FRI -> dow == Calendar.MONDAY || dow == Calendar.WEDNESDAY || dow == Calendar.FRIDAY
                MonthSchedulePattern.TUE_THU_SAT -> dow == Calendar.TUESDAY || dow == Calendar.THURSDAY || dow == Calendar.SATURDAY
                MonthSchedulePattern.WEEKLY -> dow == Calendar.MONDAY
            }
            if (matches) count++
        }
        count
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = DarkSurface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📅", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Schedule Month Ahead",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = QuestGold
                            )
                            Text(
                                text = "Plan $monthName in advance",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Quest Title") },
                    placeholder = { Text("e.g. Physics Revision & Problem Sets") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("month_quest_title_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = QuestGold
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    placeholder = { Text("e.g. Daily chapter practice for board exams") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Times
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Start Time ⏰") },
                        placeholder = { Text("07:30 AM") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                    OutlinedTextField(
                        value = dueTime,
                        onValueChange = { dueTime = it },
                        label = { Text("Due Time ⌛") },
                        placeholder = { Text("09:00 AM") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scheduling Pattern
                Text(
                    text = "Repeat Across $monthName",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    MonthSchedulePattern.values().forEach { pat ->
                        val isSelected = selectedPattern == pat
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedPattern = pat },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) CardSurface else DarkSurface,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, QuestGold) else androidx.compose.foundation.BorderStroke(0.5.dp, TextMuted.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isSelected) "●" else "○",
                                    color = if (isSelected) QuestGold else TextSecondary,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Column {
                                    Text(
                                        text = pat.title,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) QuestGold else TextPrimary,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = pat.description,
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Category & Priority
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Category", fontSize = 11.sp, color = TextSecondary)
                        Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf("Study", "Quests", "Health").forEach { cat ->
                                FilterChip(
                                    selected = category == cat,
                                    onClick = { category = cat },
                                    label = { Text(cat, fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = BreezeCyan,
                                        selectedLabelColor = Color.Black
                                    )
                                )
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Priority", fontSize = 11.sp, color = TextSecondary)
                        Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(QuestPriority.MEDIUM, QuestPriority.HIGH).forEach { prio ->
                                FilterChip(
                                    selected = priority == prio,
                                    onClick = { priority = prio },
                                    label = { Text(prio.name, fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = QuestGold,
                                        selectedLabelColor = Color.Black
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Summary Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardSurface, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🗓️ Quests to generate:", color = TextSecondary, fontSize = 12.sp)
                            Text(
                                "$estimatedQuestsCount Quests",
                                fontWeight = FontWeight.ExtraBold,
                                color = QuestGold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Each quest is automatically placed on its corresponding date in the calendar and tracks daily completion.",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            onConfirm(
                                title,
                                description,
                                startTime,
                                dueTime,
                                priority,
                                category,
                                selectedPattern,
                                initialYear,
                                initialMonth,
                                xpReward
                            )
                            onDismiss()
                        }
                    },
                    enabled = title.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("confirm_schedule_month_quests_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = QuestGold),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Schedule $estimatedQuestsCount Quests for $monthName ⚔️",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}
