package com.example.breezyquest.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.breezyquest.data.model.GoalEntity
import com.example.breezyquest.ui.components.AddGoalDialog
import com.example.breezyquest.ui.components.AddProgressDialog
import com.example.breezyquest.ui.components.EditEarningTargetDialog
import com.example.breezyquest.ui.viewmodel.MainViewModel
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

@Composable
fun GoalsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val metrics by viewModel.financialMetrics.collectAsState()
    val goals by viewModel.goals.collectAsState()

    var showEditTargetDialog by remember { mutableStateOf(false) }
    var showAddGoalDialog by remember { mutableStateOf(false) }
    var editingGoal by remember { mutableStateOf<GoalEntity?>(null) }
    var selectedGoalForProgress by remember { mutableStateOf<GoalEntity?>(null) }

    if (showEditTargetDialog) {
        EditEarningTargetDialog(
            currentTarget = metrics.earningTarget,
            currency = metrics.currency,
            onDismiss = { showEditTargetDialog = false },
            onConfirm = { viewModel.updateEarningTarget(it) }
        )
    }

    if (showAddGoalDialog || editingGoal != null) {
        AddGoalDialog(
            initialGoal = editingGoal,
            currency = metrics.currency,
            onDismiss = {
                showAddGoalDialog = false
                editingGoal = null
            },
            onConfirm = { title, target, current, deadline, cat ->
                if (editingGoal != null) {
                    viewModel.updateExistingGoal(
                        editingGoal!!.copy(
                            title = title,
                            targetAmount = target,
                            currentAmount = current,
                            deadline = deadline,
                            category = cat
                        )
                    )
                } else {
                    viewModel.addGoal(title, target, current, deadline, cat)
                }
            }
        )
    }

    if (selectedGoalForProgress != null) {
        AddProgressDialog(
            goal = selectedGoalForProgress!!,
            currency = metrics.currency,
            onDismiss = { selectedGoalForProgress = null },
            onConfirm = { added ->
                viewModel.addGoalProgress(selectedGoalForProgress!!, added)
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddGoalDialog = true },
                containerColor = QuestGold,
                contentColor = Color.Black,
                modifier = Modifier.testTag("add_goal_fab")
            ) {
                Row(modifier = Modifier.padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, contentDescription = "Add Goal")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Goal", fontWeight = FontWeight.Bold)
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Earning Target Deep-Dive Hero Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("monthly_earning_target_card"),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🎯", fontSize = 22.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Monthly Earning Target", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                                Text("Automated income progression tracker", fontSize = 12.sp, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            IconButton(onClick = { showEditTargetDialog = true }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Target", tint = QuestGold)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                Text(
                                    "${metrics.currency} ${String.format("%,.0f", metrics.earningEarned)}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = EmeraldIncome,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text("Target: ${metrics.currency} ${String.format("%,.0f", metrics.earningTarget)}", style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            Text("${metrics.earningProgressPercent.toInt()}%", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold, color = QuestGold, maxLines = 1)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LinearProgressIndicator(
                            progress = { metrics.earningProgressPercent / 100f },
                            modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)),
                            color = QuestGold,
                            trackColor = CardSurface
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Stats Grid 3-column
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), color = CardSurface) {
                                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Remaining", fontSize = 11.sp, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text("${metrics.currency} ${String.format("%,.0f", metrics.earningRemaining)}", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                            Surface(modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), color = CardSurface) {
                                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Need / Day", fontSize = 11.sp, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text("${metrics.currency} ${String.format("%,.0f", metrics.requiredPerDay)}", fontWeight = FontWeight.Bold, color = BreezeCyan, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                            Surface(modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), color = CardSurface) {
                                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Days Left", fontSize = 11.sp, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text("${metrics.daysRemainingInMonth} Days", fontWeight = FontWeight.Bold, color = QuestGold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }
                    }
                }
            }

            // Life Goals Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("🎯 Long-Term Life Goals", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("Track savings, milestones & assets", fontSize = 12.sp, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }

            if (goals.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        Text("No active goals set. Tap + New Goal to build your future!", color = TextMuted)
                    }
                }
            } else {
                items(goals, key = { it.id }) { goal ->
                    GoalItemCard(
                        goal = goal,
                        currency = metrics.currency,
                        onAddProgress = { selectedGoalForProgress = goal },
                        onEdit = { editingGoal = goal },
                        onDelete = { viewModel.deleteGoal(goal) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(70.dp)) }
        }
    }
}

@Composable
fun GoalItemCard(
    goal: GoalEntity,
    currency: String,
    onAddProgress: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val progress = ((goal.currentAmount / goal.targetAmount) * 100f).coerceIn(0.0, 100.0).toFloat()
    val isFinished = goal.currentAmount >= goal.targetAmount

    Card(
        modifier = Modifier.fillMaxWidth().testTag("goal_card_${goal.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = RoundedCornerShape(6.dp), color = BreezeCyan.copy(alpha = 0.2f)) {
                            Text(goal.category, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BreezeCyan, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), maxLines = 1)
                        }
                        if (isFinished) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(shape = RoundedCornerShape(6.dp), color = EmeraldIncome.copy(alpha = 0.2f)) {
                                Text("COMPLETED 🏆", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldIncome, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), maxLines = 1)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        goal.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Goal", tint = TextMuted, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Goal", tint = TextMuted, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    "$currency ${goal.currentAmount.toInt()} / ${goal.targetAmount.toInt()}",
                    fontWeight = FontWeight.Bold,
                    color = if (isFinished) EmeraldIncome else TextPrimary,
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f, fill = false).padding(end = 8.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text("${progress.toInt()}%", fontWeight = FontWeight.ExtraBold, color = QuestGold, fontSize = 18.sp, maxLines = 1)
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress / 100f },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = if (isFinished) EmeraldIncome else QuestGold,
                trackColor = CardSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Target: ${goal.deadline}",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    modifier = Modifier.weight(1f, fill = false).padding(end = 8.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Button(
                    onClick = onAddProgress,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = QuestGold),
                    modifier = Modifier.height(36.dp).testTag("add_progress_btn_${goal.id}")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Progress", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
                }
            }
        }
    }
}
