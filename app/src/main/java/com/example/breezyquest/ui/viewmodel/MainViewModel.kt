package com.example.breezyquest.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.breezyquest.data.local.BreezyQuestDatabase
import com.example.breezyquest.data.model.AchievementEntity
import com.example.breezyquest.data.model.BudgetEntity
import com.example.breezyquest.data.model.GoalEntity
import com.example.breezyquest.data.model.HabitEntity
import com.example.breezyquest.data.model.HabitLogEntity
import com.example.breezyquest.data.model.NoteEntity
import com.example.breezyquest.data.model.QuestEntity
import com.example.breezyquest.data.model.QuestPriority
import com.example.breezyquest.data.model.RecurringTransactionEntity
import com.example.breezyquest.data.model.TransactionEntity
import com.example.breezyquest.data.model.TransactionType
import com.example.breezyquest.data.model.UserProfileEntity
import com.example.breezyquest.data.repository.LifeQuestRepository
import com.example.breezyquest.util.NotificationHelper
import com.example.ui.theme.AppThemeStyle
import com.example.ui.theme.ThemeMode
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

data class StudySubject(
    val id: String,
    val name: String,
    val iconEmoji: String,
    val studiedMinutes: Int,
    val weeklyGoalMinutes: Int
)

data class StudySession(
    val id: String = UUID.randomUUID().toString(),
    val subjectName: String,
    val minutes: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val date: String,
    val notes: String = ""
)

enum class FocusSessionType(val title: String, val defaultMinutes: Int) {
    POMODORO("Pomodoro", 25),
    DEEP_STUDY("Deep Study", 45),
    SHORT_BREAK("Short Break", 5),
    LONG_BREAK("Long Break", 15)
}

data class BudgetWithUsage(
    val budget: BudgetEntity,
    val spent: Double,
    val remaining: Double,
    val percentageUsed: Float,
    val alertStatus: BudgetAlertLevel
)

enum class BudgetAlertLevel {
    NORMAL, WARNING_75, WARNING_90, EXCEEDED
}

data class FinancialMetrics(
    val currentBalance: Double,
    val todaySpending: Double,
    val monthlyIncome: Double,
    val monthlyExpenses: Double,
    val monthlySavings: Double,
    val totalBudget: Double,
    val totalRemainingBudget: Double,
    val earningTarget: Double,
    val earningEarned: Double,
    val earningProgressPercent: Float,
    val earningRemaining: Double,
    val requiredPerDay: Double,
    val daysRemainingInMonth: Int,
    val currency: String
)

data class DaySummary(
    val date: String,
    val quests: List<QuestEntity>,
    val habitsCompletedCount: Int,
    val totalHabitsCount: Int,
    val totalIncome: Double,
    val totalExpense: Double,
    val notesCount: Int
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LifeQuestRepository

    init {
        val database = BreezyQuestDatabase.getDatabase(application, viewModelScope)
        repository = LifeQuestRepository(database.dao(), database.habitDao())
        NotificationHelper.createNotificationChannels(application)
    }

    val transactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val budgets: StateFlow<List<BudgetEntity>> = repository.allBudgets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val quests: StateFlow<List<QuestEntity>> = repository.allQuests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val habits: StateFlow<List<HabitEntity>> = repository.allHabits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val habitLogs: StateFlow<List<HabitLogEntity>> = repository.allHabitLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notes: StateFlow<List<NoteEntity>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val goals: StateFlow<List<GoalEntity>> = repository.allGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recurring: StateFlow<List<RecurringTransactionEntity>> = repository.allRecurring
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val achievements: StateFlow<List<AchievementEntity>> = repository.allAchievements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // App PIN Lock state
    val isAppUnlocked = MutableStateFlow(true)

    // Theme Mode Preference
    private val prefs = application.getSharedPreferences("breezy_quest_prefs", Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(
        when (prefs.getString("theme_mode", "DARK")) {
            "LIGHT" -> ThemeMode.LIGHT
            "SYSTEM" -> ThemeMode.SYSTEM
            else -> ThemeMode.DARK
        }
    )
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        prefs.edit().putString("theme_mode", mode.name).apply()
    }

    fun toggleThemeMode() {
        val nextMode = if (_themeMode.value == ThemeMode.LIGHT) ThemeMode.DARK else ThemeMode.LIGHT
        setThemeMode(nextMode)
    }

    // 🎨 5 Complete App Themes (Breezy Light, Neon Quest, Sakura Breeze, Forest Focus, Sunset Energy)
    private val _appTheme = MutableStateFlow(
        try {
            AppThemeStyle.valueOf(prefs.getString("app_theme_style", AppThemeStyle.NEON_QUEST.name) ?: AppThemeStyle.NEON_QUEST.name)
        } catch (e: Exception) {
            AppThemeStyle.NEON_QUEST
        }
    )
    val appTheme: StateFlow<AppThemeStyle> = _appTheme.asStateFlow()

    fun setAppTheme(theme: AppThemeStyle) {
        _appTheme.value = theme
        prefs.edit().putString("app_theme_style", theme.name).apply()
        // Synchronize light/dark base mode
        if (theme.isDark && _themeMode.value == ThemeMode.LIGHT) {
            _themeMode.value = ThemeMode.DARK
        } else if (!theme.isDark && _themeMode.value == ThemeMode.DARK) {
            _themeMode.value = ThemeMode.LIGHT
        }
    }

    // 📚 Study System & Subjects Tracking
    private val _studySubjects = MutableStateFlow<List<StudySubject>>(emptyList())
    val studySubjects: StateFlow<List<StudySubject>> = _studySubjects.asStateFlow()

    private val _todayStudyMinutes = MutableStateFlow(0)
    val todayStudyMinutes: StateFlow<Int> = _todayStudyMinutes.asStateFlow()

    private val _studyStreakDays = MutableStateFlow(0)
    val studyStreakDays: StateFlow<Int> = _studyStreakDays.asStateFlow()

    // ⏱️ Focus / Pomodoro Timer State
    private var focusTimerJob: Job? = null
    val isFocusTimerRunning = MutableStateFlow(false)
    val focusTimerSecondsLeft = MutableStateFlow(45 * 60)
    val focusTimerTotalSeconds = MutableStateFlow(45 * 60)
    val focusActiveSubject = MutableStateFlow("Physics — Chapter 5")
    val focusSessionType = MutableStateFlow(FocusSessionType.DEEP_STUDY)

    fun startFocusTimer() {
        if (isFocusTimerRunning.value) return
        isFocusTimerRunning.value = true
        focusTimerJob?.cancel()
        focusTimerJob = viewModelScope.launch {
            while (isFocusTimerRunning.value && focusTimerSecondsLeft.value > 0) {
                delay(1000L)
                focusTimerSecondsLeft.value = (focusTimerSecondsLeft.value - 1).coerceAtLeast(0)
            }
            if (focusTimerSecondsLeft.value == 0) {
                completeFocusSession()
            }
        }
    }

    fun pauseFocusTimer() {
        isFocusTimerRunning.value = false
        focusTimerJob?.cancel()
    }

    fun resetFocusTimer() {
        isFocusTimerRunning.value = false
        focusTimerJob?.cancel()
        focusTimerSecondsLeft.value = focusTimerTotalSeconds.value
    }

    fun setFocusConfig(subject: String, type: FocusSessionType, customMinutes: Int? = null) {
        pauseFocusTimer()
        focusActiveSubject.value = subject
        focusSessionType.value = type
        val mins = customMinutes ?: type.defaultMinutes
        val totalSecs = mins * 60
        focusTimerTotalSeconds.value = totalSecs
        focusTimerSecondsLeft.value = totalSecs
    }

    fun completeFocusSession() {
        isFocusTimerRunning.value = false
        focusTimerJob?.cancel()
        val durationMinutes = (focusTimerTotalSeconds.value - focusTimerSecondsLeft.value) / 60
        val actualMinutes = if (durationMinutes > 0) durationMinutes else (focusTimerTotalSeconds.value / 60)

        // Increment today's study minutes
        _todayStudyMinutes.value += actualMinutes

        // Increment subject's study minutes
        val subjectName = focusActiveSubject.value
        val updated = _studySubjects.value.map { subj ->
            if (subjectName.contains(subj.name, ignoreCase = true)) {
                subj.copy(studiedMinutes = subj.studiedMinutes + actualMinutes)
            } else {
                subj
            }
        }
        _studySubjects.value = updated

        // Award +50 XP
        grantXp(50, "🎉 Focus Session Complete! +50 XP ⏱️")

        // Reset timer
        focusTimerSecondsLeft.value = focusTimerTotalSeconds.value
    }

    fun addCustomStudySubject(name: String, emoji: String, weeklyGoalHours: Int) {
        val newSubj = StudySubject(
            id = UUID.randomUUID().toString(),
            name = name,
            iconEmoji = emoji.ifBlank { "📚" },
            studiedMinutes = 0,
            weeklyGoalMinutes = weeklyGoalHours * 60
        )
        _studySubjects.value = _studySubjects.value + newSubj
        viewModelScope.launch {
            _eventFlow.emit("Added Subject: $name 📚")
        }
    }

    fun deleteStudySubject(id: String) {
        _studySubjects.value = _studySubjects.value.filter { it.id != id }
        viewModelScope.launch {
            _eventFlow.emit("Subject removed")
        }
    }

    fun updateStudySubject(id: String, name: String, emoji: String, weeklyGoalHours: Int) {
        _studySubjects.value = _studySubjects.value.map { subj ->
            if (subj.id == id) {
                subj.copy(
                    name = name,
                    iconEmoji = emoji.ifBlank { "📚" },
                    weeklyGoalMinutes = weeklyGoalHours * 60
                )
            } else subj
        }
        viewModelScope.launch {
            _eventFlow.emit("Subject updated: $name")
        }
    }

    fun updateQuest(quest: QuestEntity) {
        viewModelScope.launch {
            repository.updateQuest(quest)
            _eventFlow.emit("Quest updated: ${quest.title}")
        }
    }

    fun updateExistingHabit(habit: HabitEntity) {
        viewModelScope.launch {
            repository.updateHabit(habit)
            _eventFlow.emit("Habit updated: ${habit.name}")
        }
    }

    fun updateExistingGoal(goal: GoalEntity) {
        viewModelScope.launch {
            repository.updateGoal(goal)
            _eventFlow.emit("Goal updated: ${goal.title}")
        }
    }

    // Quest Notification Preferences
    private val _questStartReminders = MutableStateFlow(
        prefs.getBoolean("quest_start_reminders", true)
    )
    val questStartReminders: StateFlow<Boolean> = _questStartReminders.asStateFlow()

    private val _questLeadReminder5Min = MutableStateFlow(
        prefs.getBoolean("quest_lead_reminder_5min", false)
    )
    val questLeadReminder5Min: StateFlow<Boolean> = _questLeadReminder5Min.asStateFlow()

    fun setQuestStartReminders(enabled: Boolean) {
        _questStartReminders.value = enabled
        prefs.edit().putBoolean("quest_start_reminders", enabled).apply()
    }

    fun setQuestLeadReminder5Min(enabled: Boolean) {
        _questLeadReminder5Min.value = enabled
        prefs.edit().putBoolean("quest_lead_reminder_5min", enabled).apply()
    }

    // Event flow for celebration toasts/alerts
    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun getTodayDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    fun getCurrentMonthYear(): String {
        return SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
    }

    // Calculated Financial Metrics
    val financialMetrics: StateFlow<FinancialMetrics> = combine(
        transactions,
        budgets,
        userProfile
    ) { txList, budgetList, profile ->
        val todayStr = getTodayDate()
        val currentMonth = getCurrentMonthYear()
        val curr = profile?.currency ?: "Rs."
        val startingBal = profile?.startingBalance ?: 25000.0

        var totalInc = 0.0
        var totalExp = 0.0
        var monthInc = 0.0
        var monthExp = 0.0
        var todayExp = 0.0

        for (tx in txList) {
            if (tx.type == TransactionType.INCOME) {
                totalInc += tx.amount
                if (tx.date.startsWith(currentMonth)) {
                    monthInc += tx.amount
                }
            } else {
                totalExp += tx.amount
                if (tx.date.startsWith(currentMonth)) {
                    monthExp += tx.amount
                }
                if (tx.date == todayStr) {
                    todayExp += tx.amount
                }
            }
        }

        val balance = startingBal + totalInc - totalExp
        val monthlySavings = monthInc - monthExp

        var totalBudgetAmount = 0.0
        for (b in budgetList) {
            totalBudgetAmount += b.monthlyLimit
        }
        val totalRemaining = (totalBudgetAmount - monthExp).coerceAtLeast(0.0)

        val target = profile?.monthlyEarningTarget ?: 50000.0
        val earned = monthInc
        val progressPercent = if (target > 0) ((earned / target) * 100f).coerceIn(0.0, 100.0).toFloat() else 0f
        val remainingToEarn = (target - earned).coerceAtLeast(0.0)

        // Days remaining in month
        val cal = Calendar.getInstance()
        val totalDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val currentDay = cal.get(Calendar.DAY_OF_MONTH)
        val daysRemaining = (totalDays - currentDay + 1).coerceAtLeast(1)
        val requiredPerDay = if (remainingToEarn > 0) remainingToEarn / daysRemaining else 0.0

        FinancialMetrics(
            currentBalance = balance,
            todaySpending = todayExp,
            monthlyIncome = monthInc,
            monthlyExpenses = monthExp,
            monthlySavings = monthlySavings,
            totalBudget = totalBudgetAmount,
            totalRemainingBudget = totalRemaining,
            earningTarget = target,
            earningEarned = earned,
            earningProgressPercent = progressPercent,
            earningRemaining = remainingToEarn,
            requiredPerDay = requiredPerDay,
            daysRemainingInMonth = daysRemaining,
            currency = curr
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        FinancialMetrics(
            28450.0, 1200.0, 42000.0, 13550.0, 28450.0, 25000.0, 11450.0,
            50000.0, 38000.0, 76f, 12000.0, 520.0, 23, "Rs."
        )
    )

    // Budgets with live spending & warning statuses
    val budgetsWithUsage: StateFlow<List<BudgetWithUsage>> = combine(
        budgets,
        transactions
    ) { budgetList, txList ->
        val currentMonth = getCurrentMonthYear()
        val expenseTxThisMonth = txList.filter { it.type == TransactionType.EXPENSE && it.date.startsWith(currentMonth) }

        budgetList.map { budget ->
            val spentOnCategory = expenseTxThisMonth
                .filter { it.category.equals(budget.category, ignoreCase = true) }
                .sumOf { it.amount }
            val remaining = (budget.monthlyLimit - spentOnCategory).coerceAtLeast(0.0)
            val percentage = if (budget.monthlyLimit > 0) {
                ((spentOnCategory / budget.monthlyLimit) * 100).toFloat()
            } else 0f

            val alertLevel = when {
                spentOnCategory > budget.monthlyLimit -> BudgetAlertLevel.EXCEEDED
                percentage >= 90f -> BudgetAlertLevel.WARNING_90
                percentage >= 75f -> BudgetAlertLevel.WARNING_75
                else -> BudgetAlertLevel.NORMAL
            }

            BudgetWithUsage(
                budget = budget,
                spent = spentOnCategory,
                remaining = remaining,
                percentageUsed = percentage,
                alertStatus = alertLevel
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Today's Quests
    val todayQuests: StateFlow<List<QuestEntity>> = quests.combine(
        MutableStateFlow(getTodayDate())
    ) { questList, todayStr ->
        questList.filter { it.date == todayStr }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Habit completion map for today: habitId -> isCompleted
    val todayHabitLogs: StateFlow<Map<Long, Boolean>> = habitLogs.combine(
        MutableStateFlow(getTodayDate())
    ) { logs, todayStr ->
        logs.filter { it.date == todayStr }.associate { it.habitId to it.completed }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // RPG Level Title calculator
    fun getLevelTitle(level: Int): String {
        return when {
            level <= 1 -> "Rookie Quester"
            level <= 3 -> "Quest Novice"
            level <= 6 -> "Habit Tracker"
            level <= 9 -> "Budget Knight"
            level <= 12 -> "Earning Champion"
            level <= 15 -> "Money Master"
            level <= 19 -> "Life Grandmaster"
            else -> "Legendary Sage"
        }
    }

    // --- Actions ---

    fun addTransaction(
        title: String,
        amount: Double,
        type: TransactionType,
        category: String,
        date: String = getTodayDate(),
        time: String = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date()),
        note: String = ""
    ) {
        viewModelScope.launch {
            val tx = TransactionEntity(
                title = title,
                amount = amount,
                type = type,
                category = category,
                date = date,
                time = time,
                note = note
            )
            repository.addTransaction(tx)

            // Gamification check: stay under budget or earning milestone
            if (type == TransactionType.INCOME) {
                grantXp(30, "Income Recorded! +30 XP")
                // Check earning milestone notification
                userProfile.value?.let { profile ->
                    val newEarned = (financialMetrics.value.monthlyIncome + amount)
                    if (newEarned >= profile.monthlyEarningTarget && profile.monthlyEarningTarget > 0) {
                        NotificationHelper.showNotification(
                            getApplication(),
                            NotificationHelper.CHANNEL_GOALS,
                            101,
                            "🎯 Target Reached!",
                            "Awesome! You hit your monthly earning target of ${profile.currency} ${profile.monthlyEarningTarget.toInt()}!"
                        )
                        grantXp(100, "Goal Milestone Reached! +100 XP")
                        unlockAchievement("goal_crusher")
                    } else if (newEarned >= profile.monthlyEarningTarget * 0.8) {
                        NotificationHelper.showNotification(
                            getApplication(),
                            NotificationHelper.CHANNEL_GOALS,
                            102,
                            "🎯 Earning Target Close!",
                            "You're at ${(newEarned / profile.monthlyEarningTarget * 100).toInt()}% of your monthly earning goal!"
                        )
                    }
                }
            } else {
                // Check budget warnings
                budgets.value.find { it.category.equals(category, ignoreCase = true) }?.let { budget ->
                    val currentSpent = transactions.value
                        .filter { it.type == TransactionType.EXPENSE && it.category.equals(category, ignoreCase = true) }
                        .sumOf { it.amount } + amount
                    val ratio = currentSpent / budget.monthlyLimit
                    if (ratio >= 1.0) {
                        NotificationHelper.showNotification(
                            getApplication(),
                            NotificationHelper.CHANNEL_BUDGETS,
                            201,
                            "⚠️ Budget Exceeded",
                            "Your ${budget.category} budget of ${budget.monthlyLimit.toInt()} has been exceeded!"
                        )
                    } else if (ratio >= 0.9) {
                        NotificationHelper.showNotification(
                            getApplication(),
                            NotificationHelper.CHANNEL_BUDGETS,
                            202,
                            "⚠️ Budget Alert",
                            "You've used ${(ratio * 100).toInt()}% of your ${budget.category} budget."
                        )
                    }
                }
            }
        }
    }

    fun deleteTransaction(tx: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(tx)
        }
    }

    fun addBudget(category: String, limit: Double) {
        viewModelScope.launch {
            val budget = BudgetEntity(
                category = category,
                monthlyLimit = limit,
                monthYear = getCurrentMonthYear()
            )
            repository.addBudget(budget)
            _eventFlow.emit("Budget set for $category: Rs. ${limit.toInt()}")
        }
    }

    fun updateBudget(budget: BudgetEntity) {
        viewModelScope.launch {
            repository.updateBudget(budget)
        }
    }

    fun deleteBudget(budget: BudgetEntity) {
        viewModelScope.launch {
            repository.deleteBudget(budget)
        }
    }

    fun addQuest(
        title: String,
        description: String = "",
        date: String = getTodayDate(),
        startTime: String = "",
        dueTime: String = "",
        priority: QuestPriority = QuestPriority.MEDIUM,
        category: String = "General",
        repeat: com.example.breezyquest.data.model.QuestRepeat = com.example.breezyquest.data.model.QuestRepeat.NONE,
        xpReward: Int = 20
    ) {
        viewModelScope.launch {
            val quest = QuestEntity(
                title = title,
                description = description,
                date = date,
                startTime = startTime,
                dueTime = dueTime,
                priority = priority,
                category = category,
                repeat = repeat,
                xpReward = xpReward
            )
            val insertedId = repository.addQuest(quest)
            _eventFlow.emit("Quest Created: $title (+$xpReward XP)")

            // Schedule kickoff and optional 5-minute lead notification
            if (startTime.isNotBlank()) {
                val qId = if (insertedId > 0) insertedId else (quest.id.takeIf { it != 0L } ?: System.currentTimeMillis())
                NotificationHelper.scheduleQuestReminder(
                    context = getApplication(),
                    questId = qId,
                    questTitle = title,
                    questCategory = category,
                    questPriority = priority.name,
                    dateStr = date,
                    startTime = startTime,
                    xpReward = xpReward
                )
            }
        }
    }

    /**
     * Schedules quests in advance across an entire month based on a repetition pattern.
     * The resulting quests are immediately placed on corresponding calendar dates.
     */
    fun scheduleMonthQuests(
        title: String,
        description: String = "",
        startTime: String = "07:30 AM",
        dueTime: String = "09:00 AM",
        priority: QuestPriority = QuestPriority.MEDIUM,
        category: String = "Study",
        pattern: com.example.breezyquest.ui.components.MonthSchedulePattern = com.example.breezyquest.ui.components.MonthSchedulePattern.EVERY_DAY,
        year: Int,
        month: Int,
        xpReward: Int = 30
    ) {
        viewModelScope.launch {
            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            var count = 0
            for (day in 1..maxDays) {
                cal.set(Calendar.DAY_OF_MONTH, day)
                val dow = cal.get(Calendar.DAY_OF_WEEK)
                val matches = when (pattern) {
                    com.example.breezyquest.ui.components.MonthSchedulePattern.EVERY_DAY -> true
                    com.example.breezyquest.ui.components.MonthSchedulePattern.WEEKDAYS -> dow in Calendar.MONDAY..Calendar.FRIDAY
                    com.example.breezyquest.ui.components.MonthSchedulePattern.WEEKENDS -> dow == Calendar.SATURDAY || dow == Calendar.SUNDAY
                    com.example.breezyquest.ui.components.MonthSchedulePattern.MON_WED_FRI -> dow == Calendar.MONDAY || dow == Calendar.WEDNESDAY || dow == Calendar.FRIDAY
                    com.example.breezyquest.ui.components.MonthSchedulePattern.TUE_THU_SAT -> dow == Calendar.TUESDAY || dow == Calendar.THURSDAY || dow == Calendar.SATURDAY
                    com.example.breezyquest.ui.components.MonthSchedulePattern.WEEKLY -> dow == Calendar.MONDAY
                }
                if (matches) {
                    val dateStr = dateFormat.format(cal.time)
                    val quest = QuestEntity(
                        title = title,
                        description = description,
                        date = dateStr,
                        startTime = startTime,
                        dueTime = dueTime,
                        priority = priority,
                        category = category,
                        repeat = com.example.breezyquest.data.model.QuestRepeat.DAILY,
                        xpReward = xpReward
                    )
                    repository.addQuest(quest)
                    count++
                }
            }
            _eventFlow.emit("⚔️ Scheduled $count quests for ${SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(cal.time)}!")
        }
    }

    fun toggleQuest(quest: QuestEntity) {
        viewModelScope.launch {
            val updated = quest.copy(
                isCompleted = !quest.isCompleted,
                completedAt = if (!quest.isCompleted) System.currentTimeMillis() else null
            )
            repository.updateQuest(updated)
            if (updated.isCompleted) {
                grantXp(quest.xpReward, "Quest Completed! +${quest.xpReward} XP ⚔️")
                unlockAchievement("first_quest")
                NotificationHelper.cancelQuestReminder(getApplication(), quest.id)
            }
        }
    }

    fun deleteQuest(quest: QuestEntity) {
        viewModelScope.launch {
            NotificationHelper.cancelQuestReminder(getApplication(), quest.id)
            repository.deleteQuest(quest)
        }
    }

    fun addHabit(
        name: String,
        iconEmoji: String = "🔥",
        frequency: String = "Daily",
        reminderTime: String = "08:00 AM",
        colorHex: String = "#06B6D4"
    ) {
        viewModelScope.launch {
            val habit = HabitEntity(
                name = name,
                iconEmoji = iconEmoji,
                frequency = frequency,
                reminderTime = reminderTime,
                colorHex = colorHex
            )
            repository.addHabit(habit)
            _eventFlow.emit("Habit Created: $name")
        }
    }

    fun toggleHabitToday(habit: HabitEntity) {
        viewModelScope.launch {
            val todayStr = getTodayDate()
            val isDone = todayHabitLogs.value[habit.id] == true
            val willBeDone = !isDone

            repository.toggleHabitLog(habit.id, todayStr, willBeDone)

            // Gather all completed dates for this habit
            val completedDates = habitLogs.value
                .filter { it.habitId == habit.id && it.completed && it.date != todayStr }
                .map { it.date }
                .toMutableSet()
            if (willBeDone) {
                completedDates.add(todayStr)
            }

            val newStreak = com.example.breezyquest.ui.components.calculateConsecutiveStreak(completedDates, todayStr)
            val newBest = maxOf(habit.bestStreak, newStreak)
            val newTotal = completedDates.size

            repository.updateHabit(
                habit.copy(
                    currentStreak = newStreak,
                    bestStreak = newBest,
                    totalCompletions = newTotal,
                    isCompleted = willBeDone
                )
            )
            repository.updateHabitCompletionStatus(habit.id, willBeDone)
            repository.updateHabitStreaks(habit.id, newStreak, newBest)

            if (willBeDone) {
                grantXp(15, "Habit Completed! +15 XP 🔥")
                if (newStreak >= 7) {
                    unlockAchievement("habit_warrior")
                }
            }
        }
    }

    fun deleteHabit(habit: HabitEntity) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    fun addNote(
        title: String,
        content: String,
        category: String = "General",
        isPinned: Boolean = false,
        date: String = getTodayDate()
    ) {
        viewModelScope.launch {
            val note = NoteEntity(
                title = title,
                content = content,
                category = category,
                date = date,
                isPinned = isPinned,
                updatedAt = System.currentTimeMillis()
            )
            repository.addNote(note)
            _eventFlow.emit("Note Saved: $title")
        }
    }

    fun updateNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.updateNote(note.copy(updatedAt = System.currentTimeMillis()))
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    fun addGoal(
        title: String,
        targetAmount: Double,
        currentAmount: Double = 0.0,
        deadline: String = "",
        category: String = "Financial"
    ) {
        viewModelScope.launch {
            val goal = GoalEntity(
                title = title,
                targetAmount = targetAmount,
                currentAmount = currentAmount,
                deadline = deadline,
                category = category
            )
            repository.addGoal(goal)
            _eventFlow.emit("Goal Created: $title")
        }
    }

    fun updateGoalProgress(goal: GoalEntity, addedAmount: Double) {
        viewModelScope.launch {
            val newAmount = (goal.currentAmount + addedAmount).coerceAtLeast(0.0)
            val isCompleted = newAmount >= goal.targetAmount
            repository.updateGoal(
                goal.copy(
                    currentAmount = newAmount,
                    isCompleted = isCompleted
                )
            )
            if (isCompleted && !goal.isCompleted) {
                grantXp(80, "Goal Conquered: ${goal.title}! +80 XP 🎯")
            } else {
                _eventFlow.emit("Progress updated: +${addedAmount.toInt()}")
            }
        }
    }

    fun deleteGoal(goal: GoalEntity) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
        }
    }

    fun addRecurring(
        title: String,
        amount: Double,
        isIncome: Boolean,
        category: String,
        frequency: String = "Monthly",
        dayOfMonth: Int = 1,
        note: String = ""
    ) {
        viewModelScope.launch {
            val recurringItem = RecurringTransactionEntity(
                title = title,
                amount = amount,
                isIncome = isIncome,
                category = category,
                frequency = frequency,
                dayOfMonth = dayOfMonth,
                nextDueDate = "Day $dayOfMonth each $frequency",
                note = note
            )
            repository.addRecurring(recurringItem)
            _eventFlow.emit("Recurring ${if (isIncome) "Income" else "Bill"} scheduled: $title")
        }
    }

    fun deleteRecurring(item: RecurringTransactionEntity) {
        viewModelScope.launch {
            repository.deleteRecurring(item)
        }
    }

    fun updateStartingBalance(newBalance: Double) {
        viewModelScope.launch {
            val profile = userProfile.value ?: UserProfileEntity()
            repository.updateProfile(profile.copy(startingBalance = newBalance))
            _eventFlow.emit("Balance updated: Rs. ${newBalance.toInt()}")
        }
    }

    fun updateEarningTarget(newTarget: Double) {
        viewModelScope.launch {
            val profile = userProfile.value ?: UserProfileEntity()
            repository.updateProfile(profile.copy(monthlyEarningTarget = newTarget))
            _eventFlow.emit("Monthly Earning Target set: Rs. ${newTarget.toInt()}")
        }
    }

    fun updateCurrency(newCurrency: String) {
        viewModelScope.launch {
            val profile = userProfile.value ?: UserProfileEntity()
            repository.updateProfile(profile.copy(currency = newCurrency))
            _eventFlow.emit("Currency updated to $newCurrency")
        }
    }

    fun setPinCode(pin: String, isEnabled: Boolean) {
        viewModelScope.launch {
            val profile = userProfile.value ?: UserProfileEntity()
            repository.updateProfile(profile.copy(pinCode = pin, isPinEnabled = isEnabled))
            isAppUnlocked.value = !isEnabled
            _eventFlow.emit(if (isEnabled) "App Lock Enabled" else "App Lock Disabled")
        }
    }

    fun unlockWithPin(enteredPin: String): Boolean {
        val correctPin = userProfile.value?.pinCode ?: ""
        if (enteredPin == correctPin || correctPin.isEmpty()) {
            isAppUnlocked.value = true
            return true
        }
        return false
    }

    fun grantXp(amount: Int, message: String) {
        viewModelScope.launch {
            val profile = userProfile.value ?: UserProfileEntity()
            val newXp = profile.xp + amount
            val newLevel = (newXp / 200) + 1
            val didLevelUp = newLevel > profile.level

            repository.updateProfile(
                profile.copy(
                    xp = newXp,
                    level = newLevel
                )
            )

            if (didLevelUp) {
                val title = getLevelTitle(newLevel)
                _eventFlow.emit("🎉 LEVEL UP! Level $newLevel — $title!")
                NotificationHelper.showNotification(
                    getApplication(),
                    NotificationHelper.CHANNEL_QUESTS,
                    999,
                    "🎉 Level Up!",
                    "You achieved Level $newLevel — $title!"
                )
            } else {
                _eventFlow.emit(message)
            }
        }
    }

    private fun unlockAchievement(id: String) {
        viewModelScope.launch {
            val todayStr = getTodayDate()
            achievements.value.find { it.id == id && !it.isUnlocked }?.let { badge ->
                val updated = badge.copy(isUnlocked = true, unlockedAt = todayStr)
                repository.updateAchievement(updated)
                _eventFlow.emit("🏆 Badge Unlocked: ${badge.title}!")
                grantXp(50, "Achievement Unlocked! +50 XP 🏆")
            }
        }
    }

    fun resetData() {
        viewModelScope.launch {
            repository.resetAllData()
            _eventFlow.emit("All data has been reset.")
        }
    }

    fun addGoalProgress(goal: GoalEntity, addedAmount: Double) {
        updateGoalProgress(goal, addedAmount)
    }

    fun toggleNotePin(note: NoteEntity) {
        updateNote(note.copy(isPinned = !note.isPinned))
    }

    fun setAppLock(isEnabled: Boolean, pin: String) {
        setPinCode(pin, isEnabled)
    }

    fun setCurrency(newCurrency: String) {
        updateCurrency(newCurrency)
    }

    fun testNotification(
        title: String = "Complete Physics Chapter 3",
        message: String = "Quest is starting now!"
    ) {
        NotificationHelper.sendRecognizableQuestNotification(
            context = getApplication(),
            questId = 999L,
            questTitle = title,
            questCategory = "Study & Growth",
            questPriority = "EPIC",
            startTime = "Right Now",
            xpReward = 50,
            isLeadTime = false
        )
    }

    fun testLeadNotification(
        title: String = "Complete Physics Chapter 3",
        leadMinutes: Int = 5
    ) {
        NotificationHelper.sendRecognizableQuestNotification(
            context = getApplication(),
            questId = 998L,
            questTitle = title,
            questCategory = "Study & Growth",
            questPriority = "HIGH",
            startTime = "In $leadMinutes mins",
            xpReward = 50,
            isLeadTime = true,
            leadMinutes = leadMinutes
        )
    }

    fun resetDataToSample() {
        resetData()
    }

    fun exportDataAsJson(): String {
        return buildString {
            append("{\n")
            append("  \"app\": \"Breezy Quest\",\n")
            append("  \"exportedAt\": \"${getTodayDate()}\",\n")
            append("  \"transactionsCount\": ${transactions.value.size},\n")
            append("  \"questsCount\": ${quests.value.size},\n")
            append("  \"habitsCount\": ${habits.value.size},\n")
            append("  \"budgetsCount\": ${budgets.value.size},\n")
            append("  \"goalsCount\": ${goals.value.size},\n")
            append("  \"notesCount\": ${notes.value.size}\n")
            append("}")
        }
    }

    fun getDaySummary(dateStr: String): DaySummary {
        val qList = quests.value.filter { it.date == dateStr }
        val logsForDay = habitLogs.value.filter { it.date == dateStr && it.completed }
        val txForDay = transactions.value.filter { it.date == dateStr }
        val notesForDay = notes.value.filter { it.date == dateStr }

        val inc = txForDay.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val exp = txForDay.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

        return DaySummary(
            date = dateStr,
            quests = qList,
            habitsCompletedCount = logsForDay.size,
            totalHabitsCount = habits.value.size,
            totalIncome = inc,
            totalExpense = exp,
            notesCount = notesForDay.size
        )
    }
}
