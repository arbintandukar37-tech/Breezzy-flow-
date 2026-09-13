package com.example.breezyquest.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.breezyquest.data.model.TransactionType
import com.example.breezyquest.ui.components.AddGoalDialog
import com.example.breezyquest.ui.components.AddHabitDialog
import com.example.breezyquest.ui.components.AddNoteDialog
import com.example.breezyquest.ui.components.AddQuestDialog
import com.example.breezyquest.ui.components.AddTransactionDialog
import com.example.breezyquest.ui.components.PinLockScreen
import com.example.breezyquest.ui.components.WeeklyReviewDialog
import com.example.breezyquest.ui.screens.AnalyticsScreen
import com.example.breezyquest.ui.screens.CalendarScreen
import com.example.breezyquest.ui.screens.GamificationScreen
import com.example.breezyquest.ui.screens.GoalsScreen
import com.example.breezyquest.ui.screens.HabitsScreen
import com.example.breezyquest.ui.screens.HomeScreen
import com.example.breezyquest.ui.screens.MoneyScreen
import com.example.breezyquest.ui.screens.NotesScreen
import com.example.breezyquest.ui.screens.QuestsScreen
import com.example.breezyquest.ui.screens.SettingsScreen
import com.example.breezyquest.ui.screens.StudyScreen
import com.example.breezyquest.ui.viewmodel.MainViewModel
import com.example.ui.theme.AppThemeStyle
import com.example.ui.theme.BreezeCyan
import com.example.ui.theme.ButtonTextOnPrimary
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
import com.example.ui.theme.ThemeMode
import com.example.ui.theme.VibrantLightPink

enum class NavSection(val title: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    MONEY("Money", Icons.Default.MonetizationOn),
    QUESTS("Quests", Icons.Default.Shield),
    HABITS("Habits", Icons.Default.LocalFireDepartment),
    STUDY("Study", Icons.Default.MenuBook),
    CALENDAR("Calendar", Icons.Default.CalendarMonth),
    NOTES("Notes", Icons.Default.Description),
    GOALS("Goals", Icons.Default.Flag),
    ANALYTICS("Analytics", Icons.Default.BarChart),
    GAMIFICATION("RPG Level", Icons.Default.EmojiEvents),
    SETTINGS("Settings", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val metrics by viewModel.financialMetrics.collectAsState()

    var isLocked by remember { mutableStateOf(userProfile?.isPinEnabled == true) }
    var currentSection by remember { mutableStateOf(NavSection.HOME) }

    // Quick Add Bottom Sheet
    var showQuickAddSheet by remember { mutableStateOf(false) }
    var showMoreMenuSheet by remember { mutableStateOf(false) }
    var showWeeklyReviewDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }

    // Dialog state
    var showAddTransactionType by remember { mutableStateOf<TransactionType?>(null) }
    var showAddQuestDialog by remember { mutableStateOf(false) }
    var showAddHabitDialog by remember { mutableStateOf(false) }
    var showAddNoteDialog by remember { mutableStateOf(false) }
    var showAddGoalDialog by remember { mutableStateOf(false) }

    if (isLocked && userProfile?.isPinEnabled == true) {
        PinLockScreen(
            onUnlock = { entered ->
                val ok = entered == userProfile?.pinCode
                if (ok) isLocked = false
                ok
            }
        )
        return
    }

    if (showWeeklyReviewDialog) {
        WeeklyReviewDialog(onDismiss = { showWeeklyReviewDialog = false })
    }

    // 5 Themes Selection Dialog
    if (showThemeDialog) {
        val currentAppTheme by viewModel.appTheme.collectAsState()
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🎨", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select App Theme", fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    AppThemeStyle.values().forEach { themeStyle ->
                        val isSelected = currentAppTheme == themeStyle
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setAppTheme(themeStyle)
                                    showThemeDialog = false
                                },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) CardSurfaceVariant else CardSurface,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, BreezeCyan) else null
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = {
                                        viewModel.setAppTheme(themeStyle)
                                        showThemeDialog = false
                                    },
                                    colors = RadioButtonDefaults.colors(selectedColor = BreezeCyan)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "${themeStyle.icon} ${themeStyle.displayName}",
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = themeStyle.subtitle,
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Close", color = BreezeCyan, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = DarkSurface
        )
    }

    // Modal dialogs
    if (showAddTransactionType != null) {
        AddTransactionDialog(
            initialType = showAddTransactionType!!,
            currency = metrics.currency,
            onDismiss = { showAddTransactionType = null },
            onConfirm = { title, amt, type, cat, note ->
                viewModel.addTransaction(title, amt, type, cat, note = note)
            }
        )
    }

    if (showAddQuestDialog) {
        AddQuestDialog(
            onDismiss = { showAddQuestDialog = false },
            onConfirm = { title, desc, start, due, prio, cat, rep, xp ->
                viewModel.addQuest(
                    title = title,
                    description = desc,
                    date = viewModel.getTodayDate(),
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

    if (showAddHabitDialog) {
        AddHabitDialog(
            onDismiss = { showAddHabitDialog = false },
            onConfirm = { name, emoji, freq, reminder, col ->
                viewModel.addHabit(name, emoji, freq, reminder, col)
            }
        )
    }

    if (showAddNoteDialog) {
        AddNoteDialog(
            onDismiss = { showAddNoteDialog = false },
            onConfirm = { title, content, cat, pin ->
                viewModel.addNote(title, content, cat, pin)
            }
        )
    }

    if (showAddGoalDialog) {
        AddGoalDialog(
            currency = metrics.currency,
            onDismiss = { showAddGoalDialog = false },
            onConfirm = { title, tgt, cur, dead, cat ->
                viewModel.addGoal(title, tgt, cur, dead, cat)
            }
        )
    }

    // Quick Add Bottom Sheet
    if (showQuickAddSheet) {
        ModalBottomSheet(
            onDismissRequest = { showQuickAddSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = DarkSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "⚡ Quick Actions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))

                QuickActionItem("💰 Log Income", "Add revenue towards monthly earning target", EmeraldIncome) {
                    showQuickAddSheet = false
                    showAddTransactionType = TransactionType.INCOME
                }
                Spacer(modifier = Modifier.height(10.dp))
                QuickActionItem("💸 Record Expense", "Track spending against category budgets", RoseExpense) {
                    showQuickAddSheet = false
                    showAddTransactionType = TransactionType.EXPENSE
                }
                Spacer(modifier = Modifier.height(10.dp))
                QuickActionItem("⚔️ Create Quest", "Assign tasks, priorities, times and XP rewards", QuestGold) {
                    showQuickAddSheet = false
                    showAddQuestDialog = true
                }
                Spacer(modifier = Modifier.height(10.dp))
                QuickActionItem("🔥 New Habit", "Establish daily discipline & build streaks", BreezeCyan) {
                    showQuickAddSheet = false
                    showAddHabitDialog = true
                }
                Spacer(modifier = Modifier.height(10.dp))
                QuickActionItem("⏱️ Focus / Pomodoro", "Launch study timer & track subject goals", PurpleMagic) {
                    showQuickAddSheet = false
                    currentSection = NavSection.STUDY
                }
                Spacer(modifier = Modifier.height(10.dp))
                QuickActionItem("🎯 Set Goal", "Lock in savings milestone or personal target", PurpleMagic) {
                    showQuickAddSheet = false
                    showAddGoalDialog = true
                }
                Spacer(modifier = Modifier.height(10.dp))
                QuickActionItem("📝 Capture Note", "Jot down ideas, study summaries & thoughts", VibrantLightPink) {
                    showQuickAddSheet = false
                    showAddNoteDialog = true
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    // More Navigation Sheet
    if (showMoreMenuSheet) {
        ModalBottomSheet(
            onDismissRequest = { showMoreMenuSheet = false },
            containerColor = DarkSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text("All Modules", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(14.dp))

                val moreItems = listOf(
                    NavSection.STUDY to "Subjects tracker, weekly goals & Pomodoro focus",
                    NavSection.CALENDAR to "Unified day-by-day activity timeline",
                    NavSection.NOTES to "Strategic notes, study ideas & planning",
                    NavSection.GOALS to "Long-term savings goals & milestones",
                    NavSection.ANALYTICS to "Cashflow charts & category breakdowns",
                    NavSection.GAMIFICATION to "RPG Leveling, XP breakdown & Badges",
                    NavSection.SETTINGS to "5 App Themes, PIN security & export"
                )

                moreItems.forEach { (sec, desc) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                currentSection = sec
                                showMoreMenuSheet = false
                            }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(CardSurface, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(sec.icon, contentDescription = null, tint = BreezeCyan, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Text(sec.title, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(desc, fontSize = 11.sp, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = BreezeCyan.copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("⚡", fontSize = 18.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Breezy Quest",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = currentSection.title,
                                style = MaterialTheme.typography.labelSmall,
                                color = BreezeCyan,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                actions = {
                    // Weekly Review Icon Button
                    IconButton(onClick = { showWeeklyReviewDialog = true }) {
                        Text("📈", fontSize = 18.sp)
                    }

                    // 5 Themes Quick Selector Dialog
                    val currentAppTheme by viewModel.appTheme.collectAsState()
                    IconButton(
                        onClick = { showThemeDialog = true },
                        modifier = Modifier.testTag("theme_selector_button")
                    ) {
                        Text(currentAppTheme.icon, fontSize = 18.sp)
                    }

                    // Quick RPG Level Tag
                    val lvl = userProfile?.level ?: 14
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = QuestGold.copy(alpha = 0.2f),
                        modifier = Modifier
                            .clickable { currentSection = NavSection.GAMIFICATION }
                            .padding(end = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("👑", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("LVL $lvl", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = QuestGold)
                        }
                    }

                    // Quick Theme Light/Dark Mode Switcher (☀️/🌙)
                    val themeMode by viewModel.themeMode.collectAsState()
                    IconButton(
                        onClick = { viewModel.toggleThemeMode() },
                        modifier = Modifier.testTag("theme_quick_toggle_button")
                    ) {
                        Text(
                            text = if (themeMode == ThemeMode.LIGHT) "☀️" else "🌙",
                            fontSize = 18.sp
                        )
                    }

                    IconButton(onClick = { currentSection = NavSection.SETTINGS }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TextSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                tonalElevation = 8.dp
            ) {
                val primaryNavs = listOf(
                    NavSection.HOME,
                    NavSection.MONEY,
                    NavSection.QUESTS,
                    NavSection.HABITS
                )

                primaryNavs.forEach { section ->
                    val isSelected = currentSection == section
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentSection = section },
                        icon = {
                            Icon(
                                imageVector = section.icon,
                                contentDescription = section.title
                            )
                        },
                        label = {
                            Text(
                                text = section.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ButtonTextOnPrimary,
                            selectedTextColor = BreezeCyan,
                            indicatorColor = BreezeCyan,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_item_${section.name.lowercase()}")
                    )
                }

                // More Menu Button (Section 35: More remains highlighted with label "More")
                val isMoreActive = currentSection !in primaryNavs
                NavigationBarItem(
                    selected = isMoreActive,
                    onClick = { showMoreMenuSheet = true },
                    icon = { Icon(Icons.Default.MoreHoriz, contentDescription = "More") },
                    label = {
                        Text(
                            text = "More",
                            fontSize = 11.sp,
                            fontWeight = if (isMoreActive) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ButtonTextOnPrimary,
                        selectedTextColor = BreezeCyan,
                        indicatorColor = BreezeCyan,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("nav_item_more")
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showQuickAddSheet = true },
                containerColor = BreezeCyan,
                contentColor = ButtonTextOnPrimary,
                modifier = Modifier.testTag("quick_add_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Quick Add", tint = ButtonTextOnPrimary)
            }
        },
        containerColor = DeepObsidian,
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentSection) {
                NavSection.HOME -> HomeScreen(
                    viewModel = viewModel,
                    onNavigateToMoney = { currentSection = NavSection.MONEY },
                    onNavigateToQuests = { currentSection = NavSection.QUESTS },
                    onNavigateToHabits = { currentSection = NavSection.HABITS },
                    onNavigateToStudy = { currentSection = NavSection.STUDY },
                    onNavigateToGoals = { currentSection = NavSection.GOALS },
                    onNavigateToGamification = { currentSection = NavSection.GAMIFICATION },
                    onOpenAddQuest = { showAddQuestDialog = true },
                    onOpenAddHabit = { showAddHabitDialog = true },
                    onOpenAddTransaction = { type -> showAddTransactionType = type },
                    onOpenWeeklyReview = { showWeeklyReviewDialog = true }
                )
                NavSection.MONEY -> MoneyScreen(viewModel = viewModel)
                NavSection.QUESTS -> QuestsScreen(viewModel = viewModel)
                NavSection.HABITS -> HabitsScreen(viewModel = viewModel)
                NavSection.STUDY -> StudyScreen(viewModel = viewModel)
                NavSection.CALENDAR -> CalendarScreen(viewModel = viewModel)
                NavSection.NOTES -> NotesScreen(viewModel = viewModel)
                NavSection.GOALS -> GoalsScreen(viewModel = viewModel)
                NavSection.ANALYTICS -> AnalyticsScreen(viewModel = viewModel)
                NavSection.GAMIFICATION -> GamificationScreen(viewModel = viewModel)
                NavSection.SETTINGS -> SettingsScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun QuickActionItem(
    title: String,
    desc: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = CardSurface
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(accentColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                Text(desc, color = TextSecondary, fontSize = 11.sp)
            }
        }
    }
}
