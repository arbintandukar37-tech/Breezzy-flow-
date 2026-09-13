package com.example.breezyquest.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.breezyquest.data.model.AchievementEntity
import com.example.breezyquest.data.model.BudgetEntity
import com.example.breezyquest.data.model.GoalEntity
import com.example.breezyquest.data.model.HabitEntity
import com.example.breezyquest.data.model.HabitLogEntity
import com.example.breezyquest.data.model.NoteEntity
import com.example.breezyquest.data.model.QuestEntity
import com.example.breezyquest.data.model.QuestPriority
import com.example.breezyquest.data.model.QuestRepeat
import com.example.breezyquest.data.model.RecurringTransactionEntity
import com.example.breezyquest.data.model.TransactionEntity
import com.example.breezyquest.data.model.TransactionType
import com.example.breezyquest.data.model.UserProfileEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Database(
    entities = [
        TransactionEntity::class,
        BudgetEntity::class,
        QuestEntity::class,
        HabitEntity::class,
        HabitLogEntity::class,
        NoteEntity::class,
        GoalEntity::class,
        RecurringTransactionEntity::class,
        UserProfileEntity::class,
        AchievementEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class BreezyQuestDatabase : RoomDatabase() {
    abstract fun dao(): BreezyQuestDao
    abstract fun habitDao(): HabitDao

    companion object {
        @Volatile
        private var INSTANCE: BreezyQuestDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): BreezyQuestDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BreezyQuestDatabase::class.java,
                    "breezy_quest_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.dao())
                }
            }
        }

        suspend fun populateInitialData(dao: BreezyQuestDao) {
            // User Profile — start fresh at Level 1
            dao.insertOrUpdateProfile(
                UserProfileEntity(
                    id = 1,
                    startingBalance = 0.0,
                    manualBalanceAdjustment = 0.0,
                    monthlyEarningTarget = 50000.0,
                    currency = "Rs.",
                    xp = 0,
                    level = 1,
                    pinCode = "",
                    isPinEnabled = false,
                    notificationsEnabled = true
                )
            )

            // Achievements — all locked, ready to be earned
            val achievements = listOf(
                AchievementEntity(
                    id = "first_quest",
                    title = "First Quest",
                    description = "Complete your first quest successfully.",
                    iconEmoji = "\uD83C\uDFC6",
                    isUnlocked = false
                ),
                AchievementEntity(
                    id = "habit_warrior",
                    title = "Habit Warrior",
                    description = "Maintain an uninterrupted 7-day habit streak.",
                    iconEmoji = "\uD83D\uDD25",
                    isUnlocked = false
                ),
                AchievementEntity(
                    id = "money_maker",
                    title = "Money Maker",
                    description = "Earn over Rs. 10,000 in a single month.",
                    iconEmoji = "\uD83D\uDCB0",
                    isUnlocked = false
                ),
                AchievementEntity(
                    id = "budget_master",
                    title = "Budget Master",
                    description = "Stay strictly within all category budgets.",
                    iconEmoji = "\uD83D\uDC8E",
                    isUnlocked = false
                ),
                AchievementEntity(
                    id = "goal_crusher",
                    title = "Goal Crusher",
                    description = "Reach 100% of your monthly earning target.",
                    iconEmoji = "\uD83C\uDFAF",
                    isUnlocked = false
                ),
                AchievementEntity(
                    id = "quest_master",
                    title = "Quest Master",
                    description = "Conquer and complete 100 total quests.",
                    iconEmoji = "\u2694\uFE0F",
                    isUnlocked = false
                ),
                AchievementEntity(
                    id = "study_beast",
                    title = "Study Beast",
                    description = "Complete 30 study habit sessions.",
                    iconEmoji = "\uD83D\uDCDA",
                    isUnlocked = false
                )
            )
            dao.insertAchievements(achievements)
        }
    }
}
