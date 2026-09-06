package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.models.BudgetLimitEntity
import com.example.data.models.HabitEntity
import com.example.data.models.HabitLogEntity
import com.example.data.models.NotificationItemEntity
import com.example.data.models.TaskEntity
import com.example.data.models.TransactionEntity
import com.example.data.models.UserProfileEntity
import com.example.data.models.WalletEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ZenithDao {

  // --- Wallets / Accounts ---
  @Query("SELECT * FROM wallets")
  fun getAllWallets(): Flow<List<WalletEntity>>

  @Query("SELECT * FROM wallets WHERE id = :id")
  suspend fun getWalletById(id: String): WalletEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateWallet(wallet: WalletEntity)

  @Query("UPDATE wallets SET balance = :newBalance WHERE id = :id")
  suspend fun updateWalletBalance(id: String, newBalance: Double)

  @Delete
  suspend fun deleteWallet(wallet: WalletEntity)

  // --- Habits ---
  @Query("SELECT * FROM habits ORDER BY id ASC")
  fun getAllHabits(): Flow<List<HabitEntity>>

  @Query("SELECT * FROM habits WHERE id = :id")
  suspend fun getHabitById(id: Long): HabitEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertHabit(habit: HabitEntity): Long

  @Update
  suspend fun updateHabit(habit: HabitEntity)

  @Delete
  suspend fun deleteHabit(habit: HabitEntity)

  // --- Habit Logs ---
  @Query("SELECT * FROM habit_logs WHERE habitId = :habitId AND dateString = :dateString LIMIT 1")
  suspend fun getHabitLog(habitId: Long, dateString: String): HabitLogEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertHabitLog(log: HabitLogEntity): Long

  @Query("DELETE FROM habit_logs WHERE habitId = :habitId AND dateString = :dateString")
  suspend fun deleteHabitLog(habitId: Long, dateString: String)

  // --- Tasks ---
  @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, createdAt DESC")
  fun getAllTasks(): Flow<List<TaskEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTask(task: TaskEntity): Long

  @Update
  suspend fun updateTask(task: TaskEntity)

  @Delete
  suspend fun deleteTask(task: TaskEntity)

  // --- Transactions ---
  @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
  fun getAllTransactions(): Flow<List<TransactionEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTransaction(transaction: TransactionEntity): Long

  @Delete
  suspend fun deleteTransaction(transaction: TransactionEntity)

  // --- Budgets ---
  @Query("SELECT * FROM budget_limits")
  fun getAllBudgets(): Flow<List<BudgetLimitEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateBudget(budget: BudgetLimitEntity)

  // --- Notifications ---
  @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
  fun getAllNotifications(): Flow<List<NotificationItemEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertNotification(notification: NotificationItemEntity): Long

  @Query("UPDATE notifications SET isRead = 1")
  suspend fun markAllNotificationsAsRead()

  @Query("DELETE FROM notifications")
  suspend fun clearAllNotifications()

  // --- User Profile ---
  @Query("SELECT * FROM user_profile WHERE id = 1")
  fun getUserProfile(): Flow<UserProfileEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateProfile(profile: UserProfileEntity)
}
