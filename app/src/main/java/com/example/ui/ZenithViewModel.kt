package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.ZenithDatabase
import com.example.data.models.BudgetLimitEntity
import com.example.data.models.HabitEntity
import com.example.data.models.HabitLogEntity
import com.example.data.models.NotificationItemEntity
import com.example.data.models.SubTaskCodec
import com.example.data.models.SubTaskItem
import com.example.data.models.TaskEntity
import com.example.data.models.TransactionEntity
import com.example.data.models.UserProfileEntity
import com.example.data.models.WalletEntity
import com.example.notifications.ZenithNotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ZenithViewModel(application: Application) : AndroidViewModel(application) {

  private val database = ZenithDatabase.getDatabase(application, viewModelScope)
  private val dao = database.zenithDao()

  val habits: StateFlow<List<HabitEntity>> = dao.getAllHabits()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val tasks: StateFlow<List<TaskEntity>> = dao.getAllTasks()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val transactions: StateFlow<List<TransactionEntity>> = dao.getAllTransactions()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val budgets: StateFlow<List<BudgetLimitEntity>> = dao.getAllBudgets()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val notifications: StateFlow<List<NotificationItemEntity>> = dao.getAllNotifications()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val userProfile: StateFlow<UserProfileEntity?> = dao.getUserProfile()
    .stateIn(
      viewModelScope,
      SharingStarted.WhileSubscribed(5000),
      UserProfileEntity(id = 1, totalXp = 380, level = 4, coachMode = "AGGRESSIVE", globalStreak = 6)
    )

  // Wallet Entities StateFlow (Where user holds money)
  val wallets: StateFlow<List<WalletEntity>> = dao.getAllWallets()
    .stateIn(
      viewModelScope,
      SharingStarted.WhileSubscribed(5000),
      listOf(
        WalletEntity(id = "CASH", name = "Physical Cash", balance = 12500.0, colorHex = 0xFF10B981L, iconName = "Payments"),
        WalletEntity(id = "BANK", name = "Bank (NIC/Nabil)", balance = 85400.0, colorHex = 0xFF0284C7L, iconName = "AccountBalance"),
        WalletEntity(id = "ESEWA", name = "eSewa Wallet", balance = 9800.0, colorHex = 0xFF059669L, iconName = "QrCode"),
        WalletEntity(id = "KHALTI", name = "Khalti Wallet", balance = 4350.0, colorHex = 0xFF8B5CF6L, iconName = "AccountBalanceWallet")
      )
    )

  // In-app live floating notification banner
  private val _activeBanner = MutableStateFlow<NotificationItemEntity?>(null)
  val activeBanner: StateFlow<NotificationItemEntity?> = _activeBanner.asStateFlow()

  // Dynamic Level & XP State
  val totalXp: StateFlow<Int> = userProfile.combine(habits) { profile, _ ->
    profile?.totalXp ?: 380
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 380)

  val consistencyRate: StateFlow<Float> = habits.combine(tasks) { habitList, _ ->
    if (habitList.isEmpty()) 0f
    else habitList.count { it.isCompletedToday }.toFloat() / habitList.size.toFloat()
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.5f)

  // Wallet balances mapped by wallet ID
  val walletBalances: StateFlow<Map<String, Double>> = wallets.map { walletList ->
    if (walletList.isEmpty()) {
      mapOf("CASH" to 12500.0, "BANK" to 85400.0, "ESEWA" to 9800.0, "KHALTI" to 4350.0)
    } else {
      walletList.associate { it.id to it.balance }
    }
  }.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5000),
    mapOf("CASH" to 12500.0, "BANK" to 85400.0, "ESEWA" to 9800.0, "KHALTI" to 4350.0)
  )

  init {
    ZenithNotificationHelper.createNotificationChannel(application)
    viewModelScope.launch(Dispatchers.IO) {
      val existing = dao.getWalletById("CASH")
      if (existing == null) {
        dao.insertOrUpdateWallet(WalletEntity("CASH", "Physical Cash", 12500.0, 0xFF10B981L, "Payments"))
        dao.insertOrUpdateWallet(WalletEntity("BANK", "Bank (NIC/Nabil)", 85400.0, 0xFF0284C7L, "AccountBalance"))
        dao.insertOrUpdateWallet(WalletEntity("ESEWA", "eSewa Wallet", 9800.0, 0xFF059669L, "QrCode"))
        dao.insertOrUpdateWallet(WalletEntity("KHALTI", "Khalti Wallet", 4350.0, 0xFF8B5CF6L, "AccountBalanceWallet"))
      }
    }
  }

  fun dismissBanner() {
    _activeBanner.value = null
  }

  private fun postLiveAlert(title: String, message: String, type: String, coachMode: String) {
    viewModelScope.launch(Dispatchers.IO) {
      val notification = NotificationItemEntity(
        title = title,
        message = message,
        type = type,
        coachMode = coachMode
      )
      dao.insertNotification(notification)
      _activeBanner.value = notification

      // Also dispatch to Android OS system notification bar
      ZenithNotificationHelper.sendSystemNotification(
        getApplication(),
        title = title,
        message = message
      )

      // Auto dismiss in-app banner after 5.5 seconds
      delay(5500)
      if (_activeBanner.value?.id == notification.id || _activeBanner.value?.title == title) {
        _activeBanner.value = null
      }
    }
  }

  // --- Habit Management ---
  fun toggleHabit(habit: HabitEntity) {
    viewModelScope.launch(Dispatchers.IO) {
      val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
      val isNowCompleted = !habit.isCompletedToday
      val newStreak = if (isNowCompleted) habit.streakCount + 1 else maxOf(0, habit.streakCount - 1)
      val newBest = maxOf(habit.bestStreak, newStreak)

      val updatedHabit = habit.copy(
        isCompletedToday = isNowCompleted,
        streakCount = newStreak,
        bestStreak = newBest,
        lastCompletedDate = if (isNowCompleted) todayStr else habit.lastCompletedDate
      )
      dao.updateHabit(updatedHabit)

      if (isNowCompleted) {
        dao.insertHabitLog(HabitLogEntity(habitId = habit.id, dateString = todayStr))
        addXp(habit.xpValue)

        val profile = userProfile.value
        val mode = profile?.coachMode ?: "AGGRESSIVE"

        val alertTitle = if (mode == "AGGRESSIVE") "🔥 Habit Crushed! (+${habit.xpValue} XP)" else "✨ Habit Completed!"
        val alertMsg = if (mode == "AGGRESSIVE") {
          "You locked in '${habit.name}'! Streak increased to $newStreak days. Keep this beast mode alive!"
        } else {
          "Great job finishing '${habit.name}'. Consistency is your superpower."
        }
        postLiveAlert(alertTitle, alertMsg, "HABIT_ALERT", mode)
      } else {
        dao.deleteHabitLog(habit.id, todayStr)
        addXp(-habit.xpValue)
      }
    }
  }

  fun addHabit(
    name: String,
    category: String,
    targetDaysPerWeek: Int,
    colorHex: Long,
    iconName: String
  ) {
    viewModelScope.launch(Dispatchers.IO) {
      dao.insertHabit(
        HabitEntity(
          name = name.trim(),
          category = category,
          targetDaysPerWeek = targetDaysPerWeek,
          streakCount = 0,
          bestStreak = 0,
          xpValue = 30,
          colorHex = colorHex,
          iconName = iconName,
          isCompletedToday = false
        )
      )
      addXp(15)
      val mode = userProfile.value?.coachMode ?: "AGGRESSIVE"
      postLiveAlert("🎯 New Habit Forged!", "'$name' added to your daily matrix. Don't let it slip!", "HABIT_ALERT", mode)
    }
  }

  fun deleteHabit(habit: HabitEntity) {
    viewModelScope.launch(Dispatchers.IO) {
      dao.deleteHabit(habit)
    }
  }

  // --- Task Matrix Management ---
  fun toggleTask(task: TaskEntity) {
    viewModelScope.launch(Dispatchers.IO) {
      val isNowDone = !task.isCompleted
      val updatedTask = task.copy(isCompleted = isNowDone)
      dao.updateTask(updatedTask)

      if (isNowDone) {
        addXp(task.xpReward)
        val mode = userProfile.value?.coachMode ?: "AGGRESSIVE"
        val msg = if (mode == "AGGRESSIVE") {
          "Boom! Task '${task.title}' annihilated (+${task.xpReward} XP). One less excuse today!"
        } else {
          "Completed '${task.title}'. Your task matrix is getting cleaner!"
        }
        postLiveAlert("✅ Task Matrix Complete", msg, "TASK_URGENT", mode)
      } else {
        addXp(-task.xpReward)
      }
    }
  }

  fun toggleSubtask(task: TaskEntity, subtaskId: String) {
    viewModelScope.launch(Dispatchers.IO) {
      val currentSubtasks = SubTaskCodec.deserialize(task.subtasksRaw)
      val updatedList = currentSubtasks.map {
        if (it.id == subtaskId) it.copy(isDone = !it.isDone) else it
      }
      val allDone = updatedList.isNotEmpty() && updatedList.all { it.isDone }
      val updatedTask = task.copy(
        subtasksRaw = SubTaskCodec.serialize(updatedList),
        isCompleted = if (allDone) true else task.isCompleted
      )
      dao.updateTask(updatedTask)
      if (allDone && !task.isCompleted) {
        addXp(task.xpReward)
        postLiveAlert("🚀 All Subtasks Finished!", "All checklist steps for '${task.title}' are done!", "TASK_URGENT", "HYPE")
      }
    }
  }

  fun addTask(
    title: String,
    notes: String,
    quadrant: String,
    priority: String,
    dueDateText: String,
    subtaskTitles: List<String>
  ) {
    viewModelScope.launch(Dispatchers.IO) {
      val subtasks = subtaskTitles.mapIndexed { idx, subTitle ->
        SubTaskItem(id = UUID.randomUUID().toString(), title = subTitle, isDone = false)
      }
      dao.insertTask(
        TaskEntity(
          title = title.trim(),
          notes = notes.trim(),
          quadrant = quadrant,
          priority = priority,
          dueDateText = dueDateText,
          subtasksRaw = SubTaskCodec.serialize(subtasks),
          xpReward = if (quadrant == "DO_FIRST") 35 else 20
        )
      )
      addXp(10)
      val mode = userProfile.value?.coachMode ?: "AGGRESSIVE"
      postLiveAlert("📌 Task Scheduled", "'$title' logged into Eisenhower Matrix ($quadrant).", "TASK_URGENT", mode)
    }
  }

  fun deleteTask(task: TaskEntity) {
    viewModelScope.launch(Dispatchers.IO) {
      dao.deleteTask(task)
    }
  }

  // --- NRs Finance Management ---

  fun setWalletBalance(walletId: String, newBalance: Double) {
    viewModelScope.launch(Dispatchers.IO) {
      dao.updateWalletBalance(walletId, newBalance)
      val wallet = dao.getWalletById(walletId)
      val walletName = wallet?.name ?: walletId
      val mode = userProfile.value?.coachMode ?: "AGGRESSIVE"
      postLiveAlert(
        title = "💰 Balance Set: $walletName",
        message = "Current balance in $walletName set to रू ${String.format("%,.2f", newBalance)}.",
        type = "FINANCE_BUDGET",
        coachMode = mode
      )
    }
  }

  fun addCustomWallet(name: String, balance: Double, colorHex: Long, iconName: String) {
    viewModelScope.launch(Dispatchers.IO) {
      val id = "WALLET_" + UUID.randomUUID().toString().take(6).uppercase()
      dao.insertOrUpdateWallet(
        WalletEntity(
          id = id,
          name = name.trim(),
          balance = balance,
          colorHex = colorHex,
          iconName = iconName
        )
      )
      postLiveAlert(
        title = "🏦 Account Added",
        message = "Added account '$name' with balance रू ${String.format("%,.2f", balance)}.",
        type = "FINANCE_BUDGET",
        coachMode = "ZEN"
      )
    }
  }

  fun addTransaction(
    title: String,
    amount: Double,
    type: String,
    category: String,
    wallet: String,
    whereSpent: String = "",
    howSpent: String = "",
    notes: String = ""
  ) {
    viewModelScope.launch(Dispatchers.IO) {
      val timeText = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date())
      dao.insertTransaction(
        TransactionEntity(
          title = title.trim(),
          whereSpent = whereSpent.trim(),
          howSpent = howSpent.trim(),
          amount = amount,
          type = type,
          category = category,
          wallet = wallet,
          dateString = timeText,
          notes = notes.trim()
        )
      )
      addXp(10)

      // Directly update the balance of the wallet/location
      val targetWallet = dao.getWalletById(wallet)
      if (targetWallet != null) {
        val updatedBal = if (type == "EXPENSE") {
          targetWallet.balance - amount
        } else {
          targetWallet.balance + amount
        }
        dao.updateWalletBalance(wallet, updatedBal)
      }

      val mode = userProfile.value?.coachMode ?: "AGGRESSIVE"

      if (type == "EXPENSE") {
        // Check budget guardrails
        val currentBudget = budgets.value.find { it.category.equals(category, ignoreCase = true) }
        val categoryExpenses = transactions.value
          .filter { it.type == "EXPENSE" && it.category.equals(category, ignoreCase = true) }
          .sumOf { it.amount } + amount

        if (currentBudget != null) {
          val percent = (categoryExpenses / currentBudget.monthlyLimitNrs) * 100
          if (percent >= 100) {
            val alertTitle = "🚨 NRs BUDGET GUARDRAIL BREACHED!"
            val alertMsg = if (mode == "AGGRESSIVE") {
              "Oi! You spent रू $amount on '$category'. Total रू $categoryExpenses has EXCEEDED your limit of रू ${currentBudget.monthlyLimitNrs.toInt()}! Put the wallet away!"
            } else {
              "Warning: Monthly limit of रू ${currentBudget.monthlyLimitNrs.toInt()} for '$category' has been exceeded."
            }
            postLiveAlert(alertTitle, alertMsg, "FINANCE_BUDGET", mode)
            return@launch
          } else if (percent >= 75) {
            val alertTitle = "⚠️ Budget Limit Warning (75%+)"
            val alertMsg = "'$category' spending reached रू ${categoryExpenses.toInt()} (${percent.toInt()}% of budget). Be cautious!"
            postLiveAlert(alertTitle, alertMsg, "FINANCE_BUDGET", mode)
            return@launch
          }
        }

        val locationTag = if (whereSpent.isNotBlank()) " at $whereSpent" else ""
        val methodTag = if (howSpent.isNotBlank()) " via $howSpent" else ""
        val expenseMsg = if (mode == "AGGRESSIVE") {
          "Logged रू $amount for '$title'$locationTag$methodTag ($wallet). Keep an eye on your Kathmandu kharcha!"
        } else {
          "Logged expense: रू $amount for $title$locationTag$methodTag."
        }
        postLiveAlert("💸 NRs Expense Logged", expenseMsg, "FINANCE_BUDGET", mode)
      } else {
        postLiveAlert("💰 NRs Inflow Received!", "+ रू $amount added to your $wallet balance! Net worth growing.", "FINANCE_BUDGET", "HYPE")
      }
    }
  }

  fun deleteTransaction(tx: TransactionEntity) {
    viewModelScope.launch(Dispatchers.IO) {
      val targetWallet = dao.getWalletById(tx.wallet)
      if (targetWallet != null) {
        val revertedBal = if (tx.type == "EXPENSE") {
          targetWallet.balance + tx.amount
        } else {
          targetWallet.balance - tx.amount
        }
        dao.updateWalletBalance(tx.wallet, revertedBal)
      }
      dao.deleteTransaction(tx)
    }
  }

  fun setBudgetLimit(category: String, limitNrs: Double) {
    viewModelScope.launch(Dispatchers.IO) {
      dao.insertOrUpdateBudget(BudgetLimitEntity(category, limitNrs))
      postLiveAlert("🛡️ Guardrail Updated", "Monthly cap for $category set to रू ${limitNrs.toInt()}.", "FINANCE_BUDGET", "ZEN")
    }
  }

  // --- Accountability Coach & Insane Pings ---
  fun setCoachMode(mode: String) {
    viewModelScope.launch(Dispatchers.IO) {
      val current = userProfile.value ?: UserProfileEntity()
      dao.insertOrUpdateProfile(current.copy(coachMode = mode))
      val modeName = when (mode) {
        "AGGRESSIVE" -> "😈 Aggressive / Savage Roast Mode"
        "HYPE" -> "⚡ Hype Beast Dopamine Mode"
        else -> "🧘 Zen Himalayan Master Mode"
      }
      postLiveAlert("Accountability Coach Switched", "Now active in $modeName.", "COACH_ROAST", mode)
    }
  }

  fun triggerInstantCoachPing() {
    viewModelScope.launch(Dispatchers.IO) {
      val mode = userProfile.value?.coachMode ?: "AGGRESSIVE"
      val quote = ZenithNotificationHelper.getRandomQuote(mode)
      val title = when (mode) {
        "AGGRESSIVE" -> "😈 Savage Accountability Coach"
        "HYPE" -> "⚡ Zenith Hype Beast"
        else -> "🧘 Zen Mindset Ping"
      }
      postLiveAlert(title, quote, "COACH_ROAST", mode)
    }
  }

  fun markAllNotificationsRead() {
    viewModelScope.launch(Dispatchers.IO) {
      dao.markAllNotificationsAsRead()
    }
  }

  fun clearAllNotifications() {
    viewModelScope.launch(Dispatchers.IO) {
      dao.clearAllNotifications()
    }
  }

  private suspend fun addXp(amount: Int) {
    val current = userProfile.value ?: UserProfileEntity()
    val newXp = maxOf(0, current.totalXp + amount)
    val newLevel = maxOf(1, (newXp / 100) + 1)
    dao.insertOrUpdateProfile(current.copy(totalXp = newXp, level = newLevel))

    if (newLevel > current.level) {
      postLiveAlert("🏆 LEVEL UP! LEVEL $newLevel REACHED!", "You unlocked new badges and ascended in the Zenith Ecosystem!", "ACHIEVEMENT", "HYPE")
    }
  }
}
