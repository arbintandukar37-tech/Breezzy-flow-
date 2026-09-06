package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val category: String, // "Fitness", "Mind", "Productivity", "Nepali Routine", "Health"
  val targetDaysPerWeek: Int = 7,
  val streakCount: Int = 0,
  val bestStreak: Int = 0,
  val xpValue: Int = 25,
  val colorHex: Long = 0xFF10B981,
  val iconName: String = "fitness", // "fitness", "water", "book", "tea", "code", "meditation", "run"
  val isCompletedToday: Boolean = false,
  val lastCompletedDate: String = "" // "YYYY-MM-DD"
)

@Entity(tableName = "habit_logs")
data class HabitLogEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val habitId: Long,
  val dateString: String,
  val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "tasks")
data class TaskEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val title: String,
  val notes: String = "",
  val quadrant: String, // "DO_FIRST" (Urgent & Imp), "SCHEDULE" (Not Urgent & Imp), "DELEGATE" (Urgent & Not Imp), "ELIMINATE" (Routine/Low)
  val isCompleted: Boolean = false,
  val dueDateText: String = "Today",
  val priority: String = "HIGH", // "HIGH", "MEDIUM", "LOW"
  val subtasksRaw: String = "", // formatted delimited string "id::title::0||id2::title2::1"
  val xpReward: Int = 20,
  val createdAt: Long = System.currentTimeMillis()
)

data class SubTaskItem(
  val id: String,
  val title: String,
  val isDone: Boolean
)

object SubTaskCodec {
  fun serialize(items: List<SubTaskItem>): String {
    return items.joinToString("||") { "${it.id}::${it.title}::${if (it.isDone) "1" else "0"}" }
  }

  fun deserialize(raw: String): List<SubTaskItem> {
    if (raw.isBlank()) return emptyList()
    return raw.split("||").mapNotNull { entry ->
      val parts = entry.split("::")
      if (parts.size >= 3) {
        SubTaskItem(id = parts[0], title = parts[1], isDone = parts[2] == "1")
      } else null
    }
  }
}

@Entity(tableName = "wallets")
data class WalletEntity(
  @PrimaryKey val id: String, // "CASH", "BANK", "ESEWA", "KHALTI", or custom ID
  val name: String,
  val balance: Double, // How much money the user has in this account/location
  val colorHex: Long = 0xFF10B981L,
  val iconName: String = "Payments" // "Payments", "AccountBalance", "QrCode", "AccountBalanceWallet"
)

@Entity(tableName = "transactions")
data class TransactionEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val title: String,
  val whereSpent: String = "", // Where the money was spent (e.g. "Bhatbhateni", "Local Chiya", "Everest Momo")
  val howSpent: String = "", // How it was spent / Payment Mode (e.g. "eSewa QR", "Cash Note", "Fonepay Bank QR", "Khalti")
  val amount: Double, // in NRs (Nepalese Rupees)
  val type: String, // "EXPENSE", "INCOME"
  val category: String, // "Momo & Khaja", "Chiya & Coffee", "Rent (Bhado)", "Petrol & Pathao", "Groceries", "Salary & Freelance", "Shopping", "Tech & Bills"
  val wallet: String, // "CASH", "BANK", "ESEWA", "KHALTI"
  val dateString: String,
  val notes: String = "",
  val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "budget_limits")
data class BudgetLimitEntity(
  @PrimaryKey val category: String,
  val monthlyLimitNrs: Double
)

@Entity(tableName = "notifications")
data class NotificationItemEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val title: String,
  val message: String,
  val type: String, // "HABIT_ALERT", "TASK_URGENT", "FINANCE_BUDGET", "COACH_ROAST", "ACHIEVEMENT"
  val coachMode: String, // "AGGRESSIVE", "HYPE", "ZEN"
  val timestamp: Long = System.currentTimeMillis(),
  val isRead: Boolean = false
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
  @PrimaryKey val id: Long = 1,
  val name: String = "Zenith Master",
  val totalXp: Int = 240,
  val level: Int = 3,
  val coachMode: String = "AGGRESSIVE", // "AGGRESSIVE", "HYPE", "ZEN"
  val globalStreak: Int = 6
)
