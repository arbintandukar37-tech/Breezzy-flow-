package com.example.breezyquest.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.breezyquest.data.model.TransactionType
import com.example.breezyquest.ui.components.AddQuestDialog
import com.example.breezyquest.ui.components.ScheduleMonthQuestsDialog
import com.example.breezyquest.ui.viewmodel.MainViewModel
import com.example.ui.theme.BreezeCyan
import com.example.ui.theme.CardSurface
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DeepObsidian
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

@Composable
fun CalendarScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val transactions by viewModel.transactions.collectAsState()
    val quests by viewModel.quests.collectAsState()
    val habits by viewModel.habits.collectAsState()
    val habitLogs by viewModel.habitLogs.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val metrics by viewModel.financialMetrics.collectAsState()

    var selectedCal by remember { mutableStateOf(Calendar.getInstance()) }
    val todayDateStr = viewModel.getTodayDate()
    var selectedDateStr by remember { mutableStateOf(todayDateStr) }

    var showScheduleMonthDialog by remember { mutableStateOf(false) }
    var showAddQuestDialog by remember { mutableStateOf(false) }

    val monthYearFormat = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()) }
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    // Prepare calendar days for the current displayed month
    val calendarMonthDays = remember(selectedCal.get(Calendar.MONTH), selectedCal.get(Calendar.YEAR)) {
        val cal = selectedCal.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 1 = Sunday
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        val days = mutableListOf<CalendarDay>()
        // Blank days before month starts (assuming Sunday start)
        for (i in 1 until firstDayOfWeek) {
            days.add(CalendarDay(0, ""))
        }
        for (day in 1..maxDays) {
            cal.set(Calendar.DAY_OF_MONTH, day)
            days.add(CalendarDay(day, dateFormat.format(cal.time)))
        }
        days
    }

    if (showScheduleMonthDialog) {
        ScheduleMonthQuestsDialog(
            initialYear = selectedCal.get(Calendar.YEAR),
            initialMonth = selectedCal.get(Calendar.MONTH),
            onDismiss = { showScheduleMonthDialog = false },
            onConfirm = { title, desc, start, due, prio, cat, pattern, yr, mo, xp ->
                viewModel.scheduleMonthQuests(
                    title = title,
                    description = desc,
                    startTime = start,
                    dueTime = due,
                    priority = prio,
                    category = cat,
                    pattern = pattern,
                    year = yr,
                    month = mo,
                    xpReward = xp
                )
            }
        )
    }

    if (showAddQuestDialog) {
        AddQuestDialog(
            initialDate = selectedDateStr,
            onDismiss = { showAddQuestDialog = false },
            onConfirmWithDate = { title, desc, date, start, due, prio, cat, rep, xp ->
                viewModel.addQuest(
                    title = title,
                    description = desc,
                    date = date,
                    startTime = start,
                    dueTime = due,
                    priority = prio,
                    category = cat,
                    repeat = rep,
                    xpReward = xp
                )
            }
        )
    }

    // Data for selected date
    val dayQuests = quests.filter { it.date == selectedDateStr }
    val dayLogs = habitLogs.filter { it.date == selectedDateStr && it.completed }
    val dayTransactions = transactions.filter { it.date == selectedDateStr }
    val dayNotes = notes.filter { it.date == selectedDateStr }

    val dayIncome = dayTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    val dayExpense = dayTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DeepObsidian)
            .testTag("calendar_screen_root"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Month Selector Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            val newCal = selectedCal.clone() as Calendar
                            newCal.add(Calendar.MONTH, -1)
                            selectedCal = newCal
                        }) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Month", tint = TextPrimary)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = monthYearFormat.format(selectedCal.time),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            val totalQuestsThisMonth = quests.count { q ->
                                q.date.startsWith(SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(selectedCal.time))
                            }
                            Text(
                                text = "$totalQuestsThisMonth quests scheduled this month",
                                fontSize = 11.sp,
                                color = QuestGold
                            )
                        }

                        IconButton(onClick = {
                            val newCal = selectedCal.clone() as Calendar
                            newCal.add(Calendar.MONTH, 1)
                            selectedCal = newCal
                        }) {
                            Icon(Icons.Default.ChevronRight, contentDescription = "Next Month", tint = TextPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Day of week labels
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa").forEach { d ->
                            Text(
                                text = d,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Days Grid
                    calendarMonthDays.chunked(7).forEach { week ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            week.forEach { day ->
                                if (day.dayNumber == 0) {
                                    Spacer(modifier = Modifier.weight(1f).height(44.dp))
                                } else {
                                    val isSelected = day.dateStr == selectedDateStr
                                    val isToday = day.dateStr == todayDateStr
                                    val dayQuestsForCell = quests.filter { it.date == day.dateStr }
                                    val questCount = dayQuestsForCell.size
                                    val allQuestsCompleted = dayQuestsForCell.isNotEmpty() && dayQuestsForCell.all { it.isCompleted }
                                    val hasActivity = questCount > 0 ||
                                        transactions.any { it.date == day.dateStr } ||
                                        habitLogs.any { it.date == day.dateStr }

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(44.dp)
                                            .clickable { selectedDateStr = day.dateStr }
                                            .padding(2.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(
                                                    if (isSelected) BreezeCyan else (if (isToday) CardSurface else Color.Transparent),
                                                    CircleShape
                                                )
                                                .then(
                                                    if (isToday && !isSelected) Modifier.border(1.dp, BreezeCyan, CircleShape) else Modifier
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    text = "${day.dayNumber}",
                                                    fontSize = 12.sp,
                                                    fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isSelected) Color.Black else TextPrimary
                                                )
                                                if (questCount > 0) {
                                                    // Show mini quest indicator badge
                                                    Box(
                                                        modifier = Modifier
                                                            .size(12.dp)
                                                            .background(
                                                                if (isSelected) Color.Black else (if (allQuestsCompleted) EmeraldIncome else QuestGold),
                                                                CircleShape
                                                            ),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(
                                                            text = if (questCount > 9) "9+" else "$questCount",
                                                            fontSize = 7.sp,
                                                            fontWeight = FontWeight.ExtraBold,
                                                            color = if (isSelected) BreezeCyan else Color.Black
                                                        )
                                                    }
                                                } else if (hasActivity && !isSelected) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(4.dp)
                                                            .background(BreezeCyan, CircleShape)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            // Fill remaining columns in the last row if less than 7
                            for (extra in week.size until 7) {
                                Spacer(modifier = Modifier.weight(1f).height(44.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 📅 Button to Schedule Entire Month Quests in Advance
                    Button(
                        onClick = { showScheduleMonthDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("schedule_month_quests_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = QuestGold),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "📅 Schedule Entire Month in Advance",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Selected Date Details Hero Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Timeline for $selectedDateStr",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                            Text(
                                text = if (selectedDateStr == todayDateStr) "Today's Agenda" else "Scheduled Plan",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }

                        Button(
                            onClick = { showAddQuestDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = BreezeCyan),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("add_quest_for_date_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+ Quest", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp), color = CardSurface) {
                            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("⚔️ Quests", fontSize = 11.sp, color = TextSecondary)
                                Text("${dayQuests.count { it.isCompleted }}/${dayQuests.size}", fontWeight = FontWeight.Bold, color = QuestGold)
                            }
                        }
                        Surface(modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp), color = CardSurface) {
                            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🔥 Habits", fontSize = 11.sp, color = TextSecondary)
                                Text("${dayLogs.size} done", fontWeight = FontWeight.Bold, color = EmeraldIncome)
                            }
                        }
                        Surface(modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp), color = CardSurface) {
                            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("💰 Net", fontSize = 11.sp, color = TextSecondary)
                                val net = dayIncome - dayExpense
                                Text(
                                    "${metrics.currency} ${net.toInt()}",
                                    fontWeight = FontWeight.Bold,
                                    color = if (net >= 0) EmeraldIncome else RoseExpense
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quests on this date
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⚔️ Scheduled Quests (${dayQuests.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }

        if (dayQuests.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurface
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("No quests scheduled for this date.", color = TextMuted, fontSize = 12.sp)
                        Text(
                            text = "+ Tap '+ Quest' to add",
                            color = BreezeCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { showAddQuestDialog = true }
                        )
                    }
                }
            }
        } else {
            items(dayQuests, key = { it.id }) { q ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            IconButton(
                                onClick = { viewModel.toggleQuest(q) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (q.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = "Toggle completion",
                                    tint = if (q.isCompleted) EmeraldIncome else QuestGold
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = q.title,
                                    fontWeight = FontWeight.Bold,
                                    color = if (q.isCompleted) TextMuted else TextPrimary,
                                    fontSize = 14.sp
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (q.startTime.isNotBlank()) {
                                        Text("⏰ ${q.startTime}", fontSize = 11.sp, color = BreezeCyan)
                                    }
                                    Text("• ${q.category}", fontSize = 11.sp, color = TextSecondary)
                                    Text("• +${q.xpReward} XP", fontSize = 11.sp, color = QuestGold)
                                }
                            }
                        }

                        IconButton(
                            onClick = { viewModel.deleteQuest(q) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Quest", tint = TextMuted, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }

        // Financial items on this date
        item {
            Text("💰 Finances (${dayTransactions.size})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
        if (dayTransactions.isEmpty()) {
            item {
                Text("No transactions recorded on this date.", color = TextMuted, fontSize = 12.sp, modifier = Modifier.padding(start = 6.dp))
            }
        } else {
            items(dayTransactions, key = { it.id }) { tx ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(tx.title, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            Text("${tx.category} • ${tx.time}", fontSize = 11.sp, color = TextSecondary)
                        }
                        Text(
                            "${if (tx.type == TransactionType.INCOME) "+" else "-"} ${metrics.currency} ${tx.amount.toInt()}",
                            fontWeight = FontWeight.Bold,
                            color = if (tx.type == TransactionType.INCOME) EmeraldIncome else RoseExpense
                        )
                    }
                }
            }
        }

        // Notes on this date
        item {
            Text("📝 Notes (${dayNotes.size})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
        if (dayNotes.isEmpty()) {
            item {
                Text("No notes attached to this date.", color = TextMuted, fontSize = 12.sp, modifier = Modifier.padding(start = 6.dp))
            }
        } else {
            items(dayNotes, key = { it.id }) { n ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(n.title, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(n.content, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 2)
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(50.dp)) }
    }
}

data class CalendarDay(
    val dayNumber: Int,
    val dateStr: String
)
