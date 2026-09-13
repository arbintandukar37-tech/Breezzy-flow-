package com.example.breezyquest.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionType {
    INCOME, EXPENSE
}

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val type: TransactionType,
    val category: String,
    val date: String, // YYYY-MM-DD
    val time: String, // HH:mm
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String,
    val monthlyLimit: Double,
    val monthYear: String // YYYY-MM
)

enum class QuestPriority {
    LOW, MEDIUM, HIGH, EPIC
}

enum class QuestRepeat {
    NONE, DAILY, WEEKLY, MONTHLY
}

@Entity(tableName = "quests")
data class QuestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val date: String, // YYYY-MM-DD
    val startTime: String = "", // e.g. "07:00 PM"
    val dueTime: String = "", // e.g. "09:00 PM"
    val priority: QuestPriority = QuestPriority.MEDIUM,
    val category: String = "Personal",
    val repeat: QuestRepeat = QuestRepeat.NONE,
    val reminderMinutesBefore: Int = 15,
    val xpReward: Int = 20,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null
)

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val iconEmoji: String = "🔥",
    val frequency: String = "Daily",
    val reminderTime: String = "08:00 AM",
    val targetDaysPerWeek: Int = 7,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val totalCompletions: Int = 0,
    val isCompleted: Boolean = false, // Track completion status
    val colorHex: String = "#06B6D4" // Cyan
)

@Entity(tableName = "habit_logs", primaryKeys = ["habitId", "date"])
data class HabitLogEntity(
    val habitId: Long,
    val date: String, // YYYY-MM-DD
    val completed: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val category: String = "General", // Study, Finance, Ideas, Planning, Journal, Important
    val date: String, // YYYY-MM-DD
    val isPinned: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val deadline: String = "", // YYYY-MM-DD
    val category: String = "Financial",
    val isCompleted: Boolean = false
)

@Entity(tableName = "recurring_transactions")
data class RecurringTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val isIncome: Boolean,
    val category: String,
    val frequency: String = "Monthly", // Daily, Weekly, Monthly
    val dayOfMonth: Int = 1,
    val nextDueDate: String = "",
    val note: String = ""
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val startingBalance: Double = 25000.0,
    val manualBalanceAdjustment: Double = 0.0,
    val monthlyEarningTarget: Double = 50000.0,
    val currency: String = "Rs.",
    val xp: Int = 0,
    val level: Int = 1,
    val pinCode: String = "",
    val isPinEnabled: Boolean = false,
    val notificationsEnabled: Boolean = true
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val isUnlocked: Boolean = false,
    val unlockedAt: String = ""
)
