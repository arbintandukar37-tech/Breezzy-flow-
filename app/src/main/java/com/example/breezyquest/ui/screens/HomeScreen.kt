package com.example.breezyquest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.breezyquest.data.model.HabitEntity
import com.example.breezyquest.data.model.QuestEntity
import com.example.breezyquest.data.model.QuestPriority
import com.example.breezyquest.data.model.TransactionType
import com.example.breezyquest.ui.components.EditBalanceDialog
import com.example.breezyquest.ui.components.EditEarningTargetDialog
import com.example.breezyquest.ui.components.HabitStreakBadge
import com.example.breezyquest.ui.viewmodel.MainViewModel
import com.example.ui.theme.BreezeCyan
import com.example.ui.theme.BreezeCyanGlow
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CardSurfaceVariant
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.EmeraldIncome
import com.example.ui.theme.PurpleMagic
import com.example.ui.theme.QuestGold
import com.example.ui.theme.QuestGoldGlow
import com.example.ui.theme.RoseExpense
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToMoney: () -> Unit,
    onNavigateToQuests: () -> Unit,
    onNavigateToHabits: () -> Unit,
    onNavigateToStudy: () -> Unit,
    onNavigateToGoals: () -> Unit,
    onNavigateToGamification: () -> Unit,
    onOpenAddQuest: () -> Unit,
    onOpenAddHabit: () -> Unit,
    onOpenAddTransaction: (TransactionType) -> Unit,
    onOpenWeeklyReview: () -> Unit,
    modifier: Modifier = Modifier
) {
    val metrics by viewModel.financialMetrics.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val todayQuests by viewModel.todayQuests.collectAsState()
    val habits by viewModel.habits.collectAsState()
    val todayHabitLogs by viewModel.todayHabitLogs.collectAsState()
    val todayStudyMins by viewModel.todayStudyMinutes.collectAsState()
    val studyStreak by viewModel.studyStreakDays.collectAsState()

    var showEditBalance by remember { mutableStateOf(false) }
    var showEditTarget by remember { mutableStateOf(false) }

    // Completed state for Today's Focus quick items
    var stretchingDone by remember { mutableStateOf(false) }
    var physicsRevisionDone by remember { mutableStateOf(false) }
    var dailyQuestsDone by remember { mutableStateOf(false) }

    val level = userProfile?.level ?: 1
    val xp = userProfile?.xp ?: 0
    val levelTitle = viewModel.getLevelTitle(level)
    val xpInLevel = xp % 200
    val xpProgress = (xpInLevel / 200f).coerceIn(0f, 1f)

    // Calculate Today's Net
    val todaySpending = metrics.todaySpending
    val todayNet = (355.0).coerceAtLeast(0.0) // Master draft target net (+Rs. 355)

    val hoursStudied = todayStudyMins / 60
    val remMinsStudied = todayStudyMins % 60
    val studyStr = if (hoursStudied > 0) "${hoursStudied}h ${remMinsStudied}m" else "${remMinsStudied}m"

    val completedQuestsCount = todayQuests.count { it.isCompleted }
    val totalQuestsCount = if (todayQuests.isNotEmpty()) todayQuests.size else 8
    val completedHabitsCount = habits.count { todayHabitLogs[it.id] == true }
    val totalHabitsCount = if (habits.isNotEmpty()) habits.size else 10

    if (showEditBalance) {
        EditBalanceDialog(
            currentBalance = metrics.currentBalance,
            currency = metrics.currency,
            onDismiss = { showEditBalance = false },
            onConfirm = { viewModel.updateStartingBalance(it) }
        )
    }

    if (showEditTarget) {
        EditEarningTargetDialog(
            currentTarget = metrics.earningTarget,
            currency = metrics.currency,
            onDismiss = { showEditTarget = false },
            onConfirm = { viewModel.updateEarningTarget(it) }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DeepObsidian)
            .testTag("home_screen_root"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ==========================================
        // 1. ⚡ BREEZY QUEST RPG COMMAND HEADER
        // ==========================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToGamification() }
                    .testTag("home_rpg_header_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(CardSurface, CardSurfaceVariant)
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(BreezeCyan.copy(alpha = 0.2f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("⚡", fontSize = 20.sp)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Breezy Quest",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary
                                )
                            }

                            // 🔥 Streak Badge
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = QuestGold.copy(alpha = 0.2f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("🔥", fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "7 Day Streak",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = QuestGold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("👑", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "LVL $level",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = QuestGold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• $levelTitle",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(
                                text = "$xpInLevel / 200 XP",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BreezeCyan
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // XP Progress Bar
                        LinearProgressIndicator(
                            progress = { xpProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = QuestGold,
                            trackColor = CardSurface
                        )
                    }
                }
            }
        }

        // ==========================================
        // 2. 📊 TODAY'S OVERVIEW (MATERIAL3 RESPONSIVE CARDS)
        // ==========================================
        item {
            val questsProgress = if (totalQuestsCount > 0) (completedQuestsCount.toFloat() / totalQuestsCount.toFloat()).coerceIn(0f, 1f) else 0f
            val habitsProgress = if (totalHabitsCount > 0) (completedHabitsCount.toFloat() / totalHabitsCount.toFloat()).coerceIn(0f, 1f) else 0f
            val studyProgress = (todayStudyMins.toFloat() / 120f).coerceIn(0f, 1f) // Target 2 hours

            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val isExpanded = maxWidth >= 600.dp

                if (isExpanded) {
                    // Wide screen: 4 cards in a row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OverviewCard(
                            modifier = Modifier.weight(1f).testTag("overview_quests_card"),
                            icon = "⚔️",
                            title = "Quests",
                            value = "$completedQuestsCount / $totalQuestsCount",
                            subtitle = "${totalQuestsCount - completedQuestsCount} pending today",
                            progress = questsProgress,
                            accentColor = BreezeCyan,
                            onClick = onNavigateToQuests
                        )
                        OverviewCard(
                            modifier = Modifier.weight(1f).testTag("overview_habits_card"),
                            icon = "🔥",
                            title = "Habits",
                            value = "$completedHabitsCount / $totalHabitsCount",
                            subtitle = "7-day streak active",
                            progress = habitsProgress,
                            accentColor = QuestGold,
                            onClick = onNavigateToHabits
                        )
                        OverviewCard(
                            modifier = Modifier.weight(1f).testTag("overview_study_card"),
                            icon = "📚",
                            title = "Study",
                            value = studyStr,
                            subtitle = "Goal: 2h / day",
                            progress = studyProgress,
                            accentColor = PurpleMagic,
                            onClick = onNavigateToStudy
                        )
                        OverviewCard(
                            modifier = Modifier.weight(1f).testTag("overview_finance_card").testTag("overview_net_card"),
                            icon = "💰",
                            title = "Finance",
                            value = "+${metrics.currency} 355",
                            subtitle = "Balance: ${metrics.currency} ${metrics.currentBalance.toInt()}",
                            progress = 0.75f,
                            accentColor = EmeraldIncome,
                            onClick = onNavigateToMoney
                        )
                    }
                } else {
                    // Compact mobile screen: 2x2 grid
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OverviewCard(
                                modifier = Modifier.weight(1f).testTag("overview_quests_card"),
                                icon = "⚔️",
                                title = "Quests",
                                value = "$completedQuestsCount / $totalQuestsCount",
                                subtitle = "${totalQuestsCount - completedQuestsCount} pending",
                                progress = questsProgress,
                                accentColor = BreezeCyan,
                                onClick = onNavigateToQuests
                            )
                            OverviewCard(
                                modifier = Modifier.weight(1f).testTag("overview_habits_card"),
                                icon = "🔥",
                                title = "Habits",
                                value = "$completedHabitsCount / $totalHabitsCount",
                                subtitle = "7-day streak active",
                                progress = habitsProgress,
                                accentColor = QuestGold,
                                onClick = onNavigateToHabits
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OverviewCard(
                                modifier = Modifier.weight(1f).testTag("overview_study_card"),
                                icon = "📚",
                                title = "Study",
                                value = studyStr,
                                subtitle = "Goal: 2h / day",
                                progress = studyProgress,
                                accentColor = PurpleMagic,
                                onClick = onNavigateToStudy
                            )
                            OverviewCard(
                                modifier = Modifier.weight(1f).testTag("overview_finance_card").testTag("overview_net_card"),
                                icon = "💰",
                                title = "Finance",
                                value = "+${metrics.currency} 355",
                                subtitle = "Bal: ${metrics.currency} ${metrics.currentBalance.toInt()}",
                                progress = 0.75f,
                                accentColor = EmeraldIncome,
                                onClick = onNavigateToMoney
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // 3. 🎯 TODAY'S FOCUS (PRIORITIZED TASKS)
        // ==========================================
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🎯", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Today's Focus",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = "Auto-prioritized",
                        fontSize = 11.sp,
                        color = BreezeCyan,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Focus Item 1: Stretching
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (!stretchingDone) {
                                stretchingDone = true
                                viewModel.grantXp(25, "🔥 Stretching Completed! +25 XP")
                            }
                        },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (stretchingDone) CardSurface else DarkSurface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(QuestGold.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🔥", fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Stretching",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (stretchingDone) TextMuted else TextPrimary
                                )
                                Text(
                                    text = if (stretchingDone) "Completed today" else "Complete today",
                                    fontSize = 12.sp,
                                    color = if (stretchingDone) EmeraldIncome else TextSecondary
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (stretchingDone) EmeraldIncome.copy(alpha = 0.2f) else QuestGold.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = if (stretchingDone) "✓ Done" else "+25 XP",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (stretchingDone) EmeraldIncome else QuestGold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Focus Item 2: Physics Revision
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onNavigateToStudy()
                        },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(BreezeCyan.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("📚", fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Physics Revision",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "45 min • Chapter 5 Electromagnetism",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = BreezeCyan.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "+50 XP",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = BreezeCyan,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.PlayArrow, contentDescription = "Start Focus", tint = BreezeCyan, modifier = Modifier.size(20.dp))
                        }
                    }
                }

                // Focus Item 3: Complete Daily Quests
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToQuests() },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(PurpleMagic.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("⚔️", fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Complete Daily Quests",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "2 remaining today",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PurpleMagic.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "+100 XP",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PurpleMagic,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // 4. ⚡ QUICK ACTIONS (＋ Quest, ＋ Habit, ＋ Study, ＋ Expense)
        // ==========================================
        item {
            Column {
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // ＋ Quest
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onOpenAddQuest() },
                        shape = RoundedCornerShape(12.dp),
                        color = CardSurface
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("⚔️", fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("＋ Quest", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }

                    // ＋ Habit
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onOpenAddHabit() },
                        shape = RoundedCornerShape(12.dp),
                        color = CardSurface
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🔥", fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("＋ Habit", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }

                    // ＋ Study
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToStudy() },
                        shape = RoundedCornerShape(12.dp),
                        color = CardSurface
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("📚", fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("＋ Study", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }

                    // ＋ Expense
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onOpenAddTransaction(TransactionType.EXPENSE) },
                        shape = RoundedCornerShape(12.dp),
                        color = CardSurface
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("💸", fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("＋ Expense", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }
                }
            }
        }

        // ==========================================
        // 5. ⚔️ TODAY'S QUESTS
        // ==========================================
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⚔️", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Today's Quests",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(${todayQuests.count { it.isCompleted }}/${todayQuests.size})",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                TextButton(onClick = onNavigateToQuests) {
                    Text("View All", color = BreezeCyan, fontSize = 13.sp)
                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = BreezeCyan, modifier = Modifier.size(14.dp))
                }
            }
        }

        if (todayQuests.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                        Text("No quests scheduled for today! Tap + Quest to create one.", color = TextMuted, fontSize = 13.sp)
                    }
                }
            }
        } else {
            items(todayQuests.take(4)) { quest ->
                QuestItemCard(
                    quest = quest,
                    onToggle = { viewModel.toggleQuest(quest) }
                )
            }
        }

        // ==========================================
        // 6. 🔥 HABIT FLOW
        // ==========================================
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🔥", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Habit Flow",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$completedHabitsCount/${habits.size} done",
                        style = MaterialTheme.typography.bodySmall,
                        color = QuestGold,
                        fontWeight = FontWeight.Bold
                    )
                }
                TextButton(onClick = onNavigateToHabits) {
                    Text("Habit Hub", color = BreezeCyan, fontSize = 13.sp)
                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = BreezeCyan, modifier = Modifier.size(14.dp))
                }
            }
        }

        items(habits.take(4)) { habit ->
            val isDone = todayHabitLogs[habit.id] == true
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.toggleHabitToday(habit) }
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(CardSurface, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(habit.iconEmoji, fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = habit.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDone) TextMuted else TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                HabitStreakBadge(streak = habit.currentStreak, compact = true)
                                Text("• ${habit.reminderTime}", fontSize = 12.sp, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(if (isDone) EmeraldIncome else CardSurface, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = "Check Habit",
                            tint = if (isDone) Color.White else TextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // ==========================================
        // 7. 📈 WEEKLY REVIEW BANNER
        // ==========================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenWeeklyReview() }
                    .testTag("weekly_review_banner_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(BreezeCyan.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Insights, contentDescription = null, tint = BreezeCyan, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Weekly Growth Review",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "34 quests • 11h study • +820 XP",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                    TextButton(onClick = onOpenWeeklyReview) {
                        Text("Open", color = BreezeCyan, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun QuestItemCard(
    quest: QuestEntity,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            if (quest.isCompleted) EmeraldIncome.copy(alpha = 0.2f)
                            else CardSurface,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (quest.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = "Toggle Quest",
                        tint = if (quest.isCompleted) EmeraldIncome else TextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = quest.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = if (quest.isCompleted) TextMuted else TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (quest.startTime.isNotBlank()) {
                            Text(
                                text = "⏰ ${quest.startTime}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (quest.priority) {
                                QuestPriority.EPIC -> PurpleMagic.copy(alpha = 0.2f)
                                QuestPriority.HIGH -> RoseExpense.copy(alpha = 0.2f)
                                QuestPriority.MEDIUM -> QuestGold.copy(alpha = 0.2f)
                                QuestPriority.LOW -> BreezeCyan.copy(alpha = 0.2f)
                            }
                        ) {
                            Text(
                                text = quest.priority.name,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (quest.priority) {
                                    QuestPriority.EPIC -> PurpleMagic
                                    QuestPriority.HIGH -> RoseExpense
                                    QuestPriority.MEDIUM -> QuestGold
                                    QuestPriority.LOW -> BreezeCyan
                                },
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = QuestGold.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "+${quest.xpReward} XP",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = QuestGold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun OverviewCard(
    modifier: Modifier = Modifier,
    icon: String,
    title: String,
    value: String,
    subtitle: String,
    progress: Float,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardSurfaceVariant)
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
                            .size(28.dp)
                            .background(accentColor.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(icon, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "→",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = accentColor,
                trackColor = CardSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

