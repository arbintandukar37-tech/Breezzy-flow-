package com.example.breezyquest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.breezyquest.data.model.HabitEntity
import com.example.breezyquest.ui.components.AddHabitDialog
import com.example.breezyquest.ui.components.HabitStreakBadge
import com.example.breezyquest.ui.components.getHabitStreakTier
import com.example.breezyquest.ui.viewmodel.MainViewModel
import com.example.ui.theme.BreezeCyan
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CardSurfaceVariant
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.EmeraldIncome
import com.example.ui.theme.QuestGold
import com.example.ui.theme.RoseExpense
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HabitsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val habits by viewModel.habits.collectAsState()
    val todayHabitLogs by viewModel.todayHabitLogs.collectAsState()
    val allLogs by viewModel.habitLogs.collectAsState()

    var showAddHabitDialog by remember { mutableStateOf(false) }
    var editingHabit by remember { mutableStateOf<HabitEntity?>(null) }

    if (showAddHabitDialog || editingHabit != null) {
        AddHabitDialog(
            initialHabit = editingHabit,
            onDismiss = {
                showAddHabitDialog = false
                editingHabit = null
            },
            onConfirm = { name, emoji, freq, reminder, color ->
                if (editingHabit != null) {
                    viewModel.updateExistingHabit(
                        editingHabit!!.copy(
                            name = name,
                            iconEmoji = emoji,
                            frequency = freq,
                            reminderTime = reminder,
                            colorHex = color
                        )
                    )
                } else {
                    viewModel.addHabit(name, emoji, freq, reminder, color)
                }
            }
        )
    }

    // Calculate last 7 days dates
    val past7Days = remember {
        val list = mutableListOf<String>()
        val cal = Calendar.getInstance()
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        for (i in 6 downTo 0) {
            val c = Calendar.getInstance()
            c.add(Calendar.DAY_OF_YEAR, -i)
            list.add(format.format(c.time))
        }
        list
    }

    val dayNames = remember {
        val list = mutableListOf<String>()
        val cal = Calendar.getInstance()
        val format = SimpleDateFormat("EEE", Locale.getDefault())
        for (i in 6 downTo 0) {
            val c = Calendar.getInstance()
            c.add(Calendar.DAY_OF_YEAR, -i)
            list.add(format.format(c.time).take(2))
        }
        list
    }

    val maxStreak = habits.maxOfOrNull { it.currentStreak } ?: 0
    val bestStreakOverall = habits.maxOfOrNull { it.bestStreak } ?: 0
    val totalDoneToday = habits.count { todayHabitLogs[it.id] == true }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddHabitDialog = true },
                containerColor = BreezeCyan,
                contentColor = Color.Black,
                modifier = Modifier.testTag("add_habit_fab")
            ) {
                Row(modifier = Modifier.padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, contentDescription = "Add Habit")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Habit", fontWeight = FontWeight.Bold)
                }
            }
        },
        containerColor = DeepObsidian,
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Habit Flow Hero Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        val maxTier = getHabitStreakTier(maxStreak)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🔥", fontSize = 22.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Habit Flow",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Text(
                                    text = "Build unbreakable daily discipline",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            HabitStreakBadge(streak = maxStreak)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Stats 3-column
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), color = CardSurface) {
                                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Today", fontSize = 11.sp, color = TextSecondary, maxLines = 1)
                                    Text("$totalDoneToday / ${habits.size}", fontWeight = FontWeight.Bold, color = EmeraldIncome, fontSize = 14.sp, maxLines = 1)
                                }
                            }
                            Surface(modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), color = CardSurface) {
                                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Active Streak", fontSize = 11.sp, color = TextSecondary, maxLines = 1)
                                    Text("$maxStreak d", fontWeight = FontWeight.Bold, color = maxTier.color, fontSize = 14.sp, maxLines = 1)
                                }
                            }
                            Surface(modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), color = CardSurface) {
                                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Best Record", fontSize = 11.sp, color = TextSecondary, maxLines = 1)
                                    Text("$bestStreakOverall d", fontWeight = FontWeight.Bold, color = BreezeCyan, fontSize = 14.sp, maxLines = 1)
                                }
                            }
                        }
                    }
                }
            }

            // 🧊 Streak Freeze Protection Card (Duolingo-style streak shield)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("streak_freeze_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f).padding(end = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(BreezeCyan.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🧊", fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Streak Freeze Active",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "2 Freezes equipped • Keeps streak safe if a day is missed",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = BreezeCyan.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "2 Available",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BreezeCyan,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // Day Header Indicator Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Habits Tracker (${habits.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Text("Past 7 Days", style = MaterialTheme.typography.labelSmall, color = TextSecondary, maxLines = 1)
                }
            }

            if (habits.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        Text("No habits tracked yet. Tap + New Habit to start!", color = TextMuted)
                    }
                }
            } else {
                items(habits, key = { it.id }) { habit ->
                    val isDoneToday = todayHabitLogs[habit.id] == true
                    val habitPastLogs = allLogs.filter { it.habitId == habit.id }.associateBy { it.date }

                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("habit_card_${habit.id}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier.size(40.dp).background(CardSurface, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(habit.iconEmoji, fontSize = 20.sp)
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = habit.name,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            // Dynamic Streak Badge with Tier Icon & Color
                                            HabitStreakBadge(
                                                streak = habit.currentStreak,
                                                compact = true
                                            )
                                            Text(
                                                text = "Best: ${habit.bestStreak}d",
                                                fontSize = 11.sp,
                                                color = TextSecondary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    // Big Check Button for Today
                                    Surface(
                                        modifier = Modifier
                                            .clickable { viewModel.toggleHabitToday(habit) }
                                            .testTag("toggle_habit_${habit.id}"),
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isDoneToday) EmeraldIncome else CardSurface
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = if (isDoneToday) Icons.Default.Check else Icons.Default.RadioButtonUnchecked,
                                                contentDescription = null,
                                                tint = if (isDoneToday) Color.Black else TextSecondary,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (isDoneToday) "Done! 🔥" else "Check-in",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isDoneToday) Color.Black else TextPrimary,
                                                maxLines = 1
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = { editingHabit = habit },
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TextMuted, modifier = Modifier.size(15.dp))
                                    }
                                    IconButton(
                                        onClick = { viewModel.deleteHabit(habit) },
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(15.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // 7-day mini bubble grid
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                past7Days.forEachIndexed { idx, dayStr ->
                                    val isDayDone = habitPastLogs[dayStr]?.completed == true
                                    val isToday = idx == 6
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            dayNames[idx],
                                            fontSize = 10.sp,
                                            color = if (isToday) BreezeCyan else TextMuted,
                                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                            maxLines = 1
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(26.dp)
                                                .background(
                                                    if (isDayDone) EmeraldIncome else CardSurface,
                                                    CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isDayDone) {
                                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(13.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Streak Mastery Tiers Info Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "🔥 Streak Mastery Tiers",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        val sampleTiers = listOf(1, 3, 7, 14, 30, 60)
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            sampleTiers.forEach { s ->
                                HabitStreakBadge(
                                    streak = s,
                                    compact = true,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(70.dp)) }
        }
    }
}
