package com.example.breezyquest.data.repository

import com.example.breezyquest.data.local.BreezyQuestDao
import com.example.breezyquest.data.local.HabitDao
import com.example.breezyquest.data.model.AchievementEntity
import com.example.breezyquest.data.model.BudgetEntity
import com.example.breezyquest.data.model.GoalEntity
import com.example.breezyquest.data.model.HabitEntity
import com.example.breezyquest.data.model.HabitLogEntity
import com.example.breezyquest.data.model.NoteEntity
import com.example.breezyquest.data.model.QuestEntity
import com.example.breezyquest.data.model.RecurringTransactionEntity
import com.example.breezyquest.data.model.TransactionEntity
import com.example.breezyquest.data.model.UserProfileEntity
import kotlinx.coroutines.flow.Flow

class LifeQuestRepository(
    private val dao: BreezyQuestDao,
    private val habitDao: HabitDao? = null
) {

    val allTransactions: Flow<List<TransactionEntity>> = dao.getAllTransactions()
    val allBudgets: Flow<List<BudgetEntity>> = dao.getAllBudgets()
    val allQuests: Flow<List<QuestEntity>> = dao.getAllQuests()
    val allHabits: Flow<List<HabitEntity>> = habitDao?.getAllHabits() ?: dao.getAllHabits()
    val allHabitLogs: Flow<List<HabitLogEntity>> = dao.getAllHabitLogs()
    val allNotes: Flow<List<NoteEntity>> = dao.getAllNotes()
    val allGoals: Flow<List<GoalEntity>> = dao.getAllGoals()
    val allRecurring: Flow<List<RecurringTransactionEntity>> = dao.getAllRecurring()
    val userProfile: Flow<UserProfileEntity?> = dao.getUserProfile()
    val allAchievements: Flow<List<AchievementEntity>> = dao.getAllAchievements()

    fun getQuestsForDate(date: String): Flow<List<QuestEntity>> = dao.getQuestsForDate(date)
    fun getHabitLogsForDate(date: String): Flow<List<HabitLogEntity>> = dao.getHabitLogsForDate(date)

    suspend fun addTransaction(transaction: TransactionEntity): Long = dao.insertTransaction(transaction)
    suspend fun updateTransaction(transaction: TransactionEntity) = dao.updateTransaction(transaction)
    suspend fun deleteTransaction(transaction: TransactionEntity) = dao.deleteTransaction(transaction)

    suspend fun addBudget(budget: BudgetEntity): Long = dao.insertBudget(budget)
    suspend fun updateBudget(budget: BudgetEntity) = dao.updateBudget(budget)
    suspend fun deleteBudget(budget: BudgetEntity) = dao.deleteBudget(budget)

    suspend fun addQuest(quest: QuestEntity): Long = dao.insertQuest(quest)
    suspend fun updateQuest(quest: QuestEntity) = dao.updateQuest(quest)
    suspend fun deleteQuest(quest: QuestEntity) = dao.deleteQuest(quest)

    suspend fun addHabit(habit: HabitEntity): Long = habitDao?.insertHabit(habit) ?: dao.insertHabit(habit)
    suspend fun updateHabit(habit: HabitEntity) = habitDao?.updateHabit(habit) ?: dao.updateHabit(habit)
    suspend fun deleteHabit(habit: HabitEntity) = habitDao?.deleteHabit(habit) ?: dao.deleteHabit(habit)

    suspend fun updateHabitCompletionStatus(habitId: Long, isCompleted: Boolean) {
        habitDao?.updateCompletionStatus(habitId, isCompleted)
    }

    suspend fun updateHabitStreaks(habitId: Long, currentStreak: Int, bestStreak: Int) {
        habitDao?.updateStreaks(habitId, currentStreak, bestStreak)
    }

    suspend fun toggleHabitLog(habitId: Long, date: String, isDone: Boolean) {
        if (isDone) {
            dao.insertHabitLog(HabitLogEntity(habitId = habitId, date = date, completed = true))
        } else {
            dao.deleteHabitLog(habitId, date)
        }
    }

    suspend fun addNote(note: NoteEntity): Long = dao.insertNote(note)
    suspend fun updateNote(note: NoteEntity) = dao.updateNote(note)
    suspend fun deleteNote(note: NoteEntity) = dao.deleteNote(note)

    suspend fun addGoal(goal: GoalEntity): Long = dao.insertGoal(goal)
    suspend fun updateGoal(goal: GoalEntity) = dao.updateGoal(goal)
    suspend fun deleteGoal(goal: GoalEntity) = dao.deleteGoal(goal)

    suspend fun addRecurring(recurring: RecurringTransactionEntity): Long = dao.insertRecurring(recurring)
    suspend fun deleteRecurring(recurring: RecurringTransactionEntity) = dao.deleteRecurring(recurring)

    suspend fun updateProfile(profile: UserProfileEntity) = dao.insertOrUpdateProfile(profile)
    suspend fun updateAchievement(achievement: AchievementEntity) = dao.updateAchievement(achievement)

    suspend fun resetAllData() {
        dao.clearTransactions()
        dao.clearBudgets()
        dao.clearQuests()
        dao.clearHabits()
        dao.clearHabitLogs()
        dao.clearNotes()
        dao.clearGoals()
        dao.clearRecurring()
    }
}
