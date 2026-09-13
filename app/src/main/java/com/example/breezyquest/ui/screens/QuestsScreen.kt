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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.breezyquest.data.model.QuestEntity
import com.example.breezyquest.data.model.QuestPriority
import com.example.breezyquest.data.model.QuestRepeat
import com.example.breezyquest.ui.components.AddQuestDialog
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

enum class QuestFilter {
    ALL, TODAY, UPCOMING, COMPLETED
}

@Composable
fun QuestsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val quests by viewModel.quests.collectAsState()
    val todayDate = viewModel.getTodayDate()

    var activeFilter by remember { mutableStateOf(QuestFilter.TODAY) }
    var searchQuery by remember { mutableStateOf("") }
    var showAddQuestDialog by remember { mutableStateOf(false) }
    var editingQuest by remember { mutableStateOf<QuestEntity?>(null) }

    if (showAddQuestDialog || editingQuest != null) {
        AddQuestDialog(
            initialQuest = editingQuest,
            onDismiss = {
                showAddQuestDialog = false
                editingQuest = null
            },
            onConfirm = { title, desc, start, due, priority, cat, repeat, xp ->
                if (editingQuest != null) {
                    viewModel.updateQuest(
                        editingQuest!!.copy(
                            title = title,
                            description = desc,
                            startTime = start,
                            dueTime = due,
                            priority = priority,
                            category = cat,
                            repeat = repeat,
                            xpReward = xp
                        )
                    )
                } else {
                    viewModel.addQuest(
                        title = title,
                        description = desc,
                        date = todayDate,
                        startTime = start,
                        dueTime = due,
                        priority = priority,
                        category = cat,
                        repeat = repeat,
                        xpReward = xp
                    )
                }
            }
        )
    }

    val filteredQuests = quests.filter { q ->
        val matchesSearch = searchQuery.isBlank() || q.title.contains(searchQuery, ignoreCase = true) || q.description.contains(searchQuery, ignoreCase = true)
        val matchesFilter = when (activeFilter) {
            QuestFilter.ALL -> true
            QuestFilter.TODAY -> q.date == todayDate && !q.isCompleted
            QuestFilter.UPCOMING -> q.date >= todayDate && !q.isCompleted
            QuestFilter.COMPLETED -> q.isCompleted
        }
        matchesSearch && matchesFilter
    }

    val totalCompleted = quests.count { it.isCompleted }
    val completionRate = if (quests.isNotEmpty()) ((totalCompleted.toFloat() / quests.size) * 100).toInt() else 0

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddQuestDialog = true },
                containerColor = QuestGold,
                contentColor = Color.Black,
                modifier = Modifier.testTag("add_quest_fab")
            ) {
                Row(modifier = Modifier.padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, contentDescription = "Add Quest")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Quest", fontWeight = FontWeight.Bold)
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
            // Quest Questboard Hero Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("⚔️ Quest Hub", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                                Text("Conquer daily missions & earn XP", fontSize = 12.sp, color = TextSecondary)
                            }
                            Surface(shape = RoundedCornerShape(12.dp), color = QuestGold.copy(alpha = 0.2f)) {
                                Text(
                                    "$totalCompleted / ${quests.size} Done ($completionRate%)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = QuestGold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search quests...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("quest_search_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = QuestGold
                    )
                )
            }

            // Filter Chips
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuestFilter.values().forEach { filter ->
                        FilterChip(
                            selected = activeFilter == filter,
                            onClick = { activeFilter = filter },
                            label = { Text(filter.name.lowercase().capitalize(), fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = QuestGold,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }
            }

            if (filteredQuests.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        Text("No quests match your criteria. Tap + to add one!", color = TextMuted)
                    }
                }
            } else {
                items(filteredQuests, key = { it.id }) { quest ->
                    QuestDetailCard(
                        quest = quest,
                        onToggle = { viewModel.toggleQuest(quest) },
                        onEdit = { editingQuest = quest },
                        onDelete = { viewModel.deleteQuest(quest) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(70.dp)) }
        }
    }
}

@Composable
fun QuestDetailCard(
    quest: QuestEntity,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("quest_card_${quest.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    IconButton(
                        onClick = onToggle,
                        modifier = Modifier.size(36.dp).padding(top = 2.dp)
                    ) {
                        Icon(
                            imageVector = if (quest.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = "Complete Quest",
                            tint = if (quest.isCompleted) EmeraldIncome else TextSecondary,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = quest.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (quest.isCompleted) TextMuted else TextPrimary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (quest.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = quest.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (quest.isCompleted) TextMuted else TextSecondary,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Quest", tint = TextMuted, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Quest", tint = TextMuted, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Meta row: Priority, Repeat, Time, XP
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false).padding(end = 8.dp)
                ) {
                    // Priority
                    val priorityColor = when (quest.priority) {
                        QuestPriority.LOW -> BreezeCyan
                        QuestPriority.MEDIUM -> QuestGold
                        QuestPriority.HIGH -> RoseExpense
                        QuestPriority.EPIC -> PurpleMagic
                    }
                    Surface(shape = RoundedCornerShape(6.dp), color = priorityColor.copy(alpha = 0.2f)) {
                        Text(
                            quest.priority.name,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = priorityColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            maxLines = 1
                        )
                    }

                    if (quest.repeat != QuestRepeat.NONE) {
                        Surface(shape = RoundedCornerShape(6.dp), color = CardSurface) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Repeat, contentDescription = null, modifier = Modifier.size(10.dp), tint = TextSecondary)
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(quest.repeat.name.lowercase().capitalize(), fontSize = 10.sp, color = TextSecondary, maxLines = 1)
                            }
                        }
                    }

                    if (quest.startTime.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(12.dp), tint = TextSecondary)
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(quest.startTime, fontSize = 11.sp, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (quest.isCompleted) CardSurface else QuestGold.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "+${quest.xpReward} XP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (quest.isCompleted) TextMuted else QuestGold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        maxLines = 1
                    )
                }
            }
        }
    }
}
