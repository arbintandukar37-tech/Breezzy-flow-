package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.ZenithDao
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Database(
  entities = [
    WalletEntity::class,
    HabitEntity::class,
    HabitLogEntity::class,
    TaskEntity::class,
    TransactionEntity::class,
    BudgetLimitEntity::class,
    NotificationItemEntity::class,
    UserProfileEntity::class
  ],
  version = 2,
  exportSchema = false
)
abstract class ZenithDatabase : RoomDatabase() {

  abstract fun zenithDao(): ZenithDao

  companion object {
    @Volatile
    private var INSTANCE: ZenithDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope): ZenithDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          ZenithDatabase::class.java,
          "zenith_life.db"
        )
          .addCallback(DatabaseCallback(scope))
          .fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        instance
      }
    }

    private class DatabaseCallback(
      private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
      override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        INSTANCE?.let { database ->
          scope.launch(Dispatchers.IO) {
            populateInitialData(database.zenithDao())
          }
        }
      }
    }

    suspend fun populateInitialData(dao: ZenithDao) {
      val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

      // Initial Habits
      val habit1 = dao.insertHabit(
        HabitEntity(
          name = "Morning Chiya & 10m Meditation",
          category = "Nepali Routine",
          targetDaysPerWeek = 7,
          streakCount = 6,
          bestStreak = 14,
          xpValue = 30,
          colorHex = 0xFFF59E0B,
          iconName = "tea",
          isCompletedToday = true,
          lastCompletedDate = todayStr
        )
      )
      dao.insertHabitLog(HabitLogEntity(habitId = habit1, dateString = todayStr))

      dao.insertHabit(
        HabitEntity(
          name = "Daily 5km Run / Gym Session",
          category = "Fitness",
          targetDaysPerWeek = 5,
          streakCount = 4,
          bestStreak = 10,
          xpValue = 40,
          colorHex = 0xFFEF4444,
          iconName = "fitness",
          isCompletedToday = false
        )
      )

      dao.insertHabit(
        HabitEntity(
          name = "Drink 3L Himalayan Spring Water",
          category = "Health",
          targetDaysPerWeek = 7,
          streakCount = 8,
          bestStreak = 21,
          xpValue = 25,
          colorHex = 0xFF06B6D4,
          iconName = "water",
          isCompletedToday = true,
          lastCompletedDate = todayStr
        )
      )

      dao.insertHabit(
        HabitEntity(
          name = "Code 1 Hour & Build Zenith App",
          category = "Productivity",
          targetDaysPerWeek = 6,
          streakCount = 12,
          bestStreak = 18,
          xpValue = 50,
          colorHex = 0xFF10B981,
          iconName = "code",
          isCompletedToday = false
        )
      )

      // Initial Tasks (Eisenhower Matrix)
      val subtasks1 = listOf(
        SubTaskItem("st1", "Review Monthly NRs Expenses", true),
        SubTaskItem("st2", "Transfer emergency fund to NIC Asia Bank", false)
      )
      dao.insertTask(
        TaskEntity(
          title = "Pay Kathmandu Flat Rent (Bhado)",
          notes = "Urgent: Landlord requested via eSewa before 7 PM",
          quadrant = "DO_FIRST",
          isCompleted = false,
          dueDateText = "Today",
          priority = "HIGH",
          subtasksRaw = SubTaskCodec.serialize(subtasks1),
          xpReward = 35
        )
      )

      val subtasks2 = listOf(
        SubTaskItem("st3", "Design high-contrast habit streak flame", true),
        SubTaskItem("st4", "Tune sound & vibration for Savage Mode coach", true),
        SubTaskItem("st5", "Add NRs Momo category budget alert", true)
      )
      dao.insertTask(
        TaskEntity(
          title = "Polishing Zenith Life UI Architecture",
          notes = "High impact: Eisenhower matrix + live consistency ring",
          quadrant = "SCHEDULE",
          isCompleted = false,
          dueDateText = "Tomorrow",
          priority = "HIGH",
          subtasksRaw = SubTaskCodec.serialize(subtasks2),
          xpReward = 45
        )
      )

      dao.insertTask(
        TaskEntity(
          title = "Ask friend to return loaned NRs 1,500",
          notes = "Pathao ride loan last Friday",
          quadrant = "DELEGATE",
          isCompleted = false,
          dueDateText = "This Week",
          priority = "MEDIUM",
          xpReward = 20
        )
      )

      dao.insertTask(
        TaskEntity(
          title = "Clean desk and sort Kathmandu cables",
          notes = "Low priority weekend routine",
          quadrant = "ELIMINATE",
          isCompleted = true,
          dueDateText = "Whenever",
          priority = "LOW",
          xpReward = 15
        )
      )

      // Initial Wallets / Accounts (Where money is kept)
      dao.insertOrUpdateWallet(
        WalletEntity(
          id = "CASH",
          name = "Physical Cash",
          balance = 12500.0,
          colorHex = 0xFF10B981L,
          iconName = "Payments"
        )
      )
      dao.insertOrUpdateWallet(
        WalletEntity(
          id = "BANK",
          name = "Bank (NIC/Nabil)",
          balance = 85400.0,
          colorHex = 0xFF0284C7L,
          iconName = "AccountBalance"
        )
      )
      dao.insertOrUpdateWallet(
        WalletEntity(
          id = "ESEWA",
          name = "eSewa Wallet",
          balance = 9800.0,
          colorHex = 0xFF059669L,
          iconName = "QrCode"
        )
      )
      dao.insertOrUpdateWallet(
        WalletEntity(
          id = "KHALTI",
          name = "Khalti Wallet",
          balance = 4350.0,
          colorHex = 0xFF8B5CF6L,
          iconName = "AccountBalanceWallet"
        )
      )

      // Initial Transactions in NRs (Nepalese Rupees)
      val now = System.currentTimeMillis()
      dao.insertTransaction(
        TransactionEntity(
          title = "Buff Steam Momo & Coke",
          whereSpent = "Everest Momo Center, Jhamsikhel",
          howSpent = "eSewa QR Scan",
          amount = 350.0,
          type = "EXPENSE",
          category = "Momo & Khaja",
          wallet = "ESEWA",
          dateString = "Today, 1:30 PM",
          notes = "Quick lunch break",
          timestamp = now - 3600000 * 4
        )
      )
      dao.insertTransaction(
        TransactionEntity(
          title = "Pathao Ride to Thamel",
          whereSpent = "Pathao Nepal (Bike Ride)",
          howSpent = "Khalti Pay",
          amount = 220.0,
          type = "EXPENSE",
          category = "Petrol & Pathao",
          wallet = "KHALTI",
          dateString = "Today, 10:15 AM",
          notes = "Commute during traffic",
          timestamp = now - 3600000 * 7
        )
      )
      dao.insertTransaction(
        TransactionEntity(
          title = "Freelance Mobile UI Project Payment",
          whereSpent = "Upwork Client Transfer",
          howSpent = "Fonepay Direct Deposit",
          amount = 45000.0,
          type = "INCOME",
          category = "Salary & Freelance",
          wallet = "BANK",
          dateString = "Yesterday",
          notes = "Sprint 1 milestone released",
          timestamp = now - 86400000
        )
      )
      dao.insertTransaction(
        TransactionEntity(
          title = "Special Masala Chiya with friends",
          whereSpent = "Local Chiya Pasal (Basantapur)",
          howSpent = "Cash Note",
          amount = 80.0,
          type = "EXPENSE",
          category = "Chiya & Coffee",
          wallet = "CASH",
          dateString = "Yesterday",
          notes = "Chiya guff",
          timestamp = now - 90000000
        )
      )
      dao.insertTransaction(
        TransactionEntity(
          title = "Ason Bazzar Fresh Tarkari & Fruits",
          whereSpent = "Ason Bazzar Sabji Mandi",
          howSpent = "Physical Cash",
          amount = 1250.0,
          type = "EXPENSE",
          category = "Groceries",
          wallet = "CASH",
          dateString = "2 days ago",
          notes = "Fresh weekly veggies",
          timestamp = now - 172800000
        )
      )

      // Initial Budgets in NRs
      dao.insertOrUpdateBudget(BudgetLimitEntity("Momo & Khaja", 5000.0))
      dao.insertOrUpdateBudget(BudgetLimitEntity("Petrol & Pathao", 4000.0))
      dao.insertOrUpdateBudget(BudgetLimitEntity("Rent (Bhado)", 18000.0))
      dao.insertOrUpdateBudget(BudgetLimitEntity("Groceries", 12000.0))
      dao.insertOrUpdateBudget(BudgetLimitEntity("Chiya & Coffee", 2000.0))

      // Initial Notifications (Accountability Coach & Insane Pings)
      dao.insertNotification(
        NotificationItemEntity(
          title = "🔥 Streak On Fire!",
          message = "You have an active 6-day streak in Zenith Life! Don't let your discipline slip today.",
          type = "ACHIEVEMENT",
          coachMode = "HYPE",
          timestamp = now - 1800000
        )
      )
      dao.insertNotification(
        NotificationItemEntity(
          title = "😈 Savage Coach Roast",
          message = "Oi! You haven't done your 'Daily 5km Run' yet. Are you waiting for a Dashain holiday to start running?",
          type = "COACH_ROAST",
          coachMode = "AGGRESSIVE",
          timestamp = now - 3600000 * 2
        )
      )
      dao.insertNotification(
        NotificationItemEntity(
          title = "⚠️ NRs Budget Guardrail Alert",
          message = "You spent रू 350 on Momo today. Your monthly Momo & Khaja budget is at 74% capacity. Watch out!",
          type = "FINANCE_BUDGET",
          coachMode = "AGGRESSIVE",
          timestamp = now - 3600000 * 5
        )
      )

      // User Profile
      dao.insertOrUpdateProfile(
        UserProfileEntity(
          id = 1,
          name = "Zenith Master",
          totalXp = 380,
          level = 4,
          coachMode = "AGGRESSIVE",
          globalStreak = 6
        )
      )
    }
  }
}
