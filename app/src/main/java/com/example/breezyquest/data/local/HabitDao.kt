package com.example.breezyquest.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.breezyquest.data.model.HabitEntity
import kotlinx.coroutines.flow.Flow

/**
 * Room Data Access Object (DAO) for the 'habits' table.
 * Tracks habit name, current streak, best streak, and completion status.
 */
@Dao
interface HabitDao {

    @Query("SELECT * FROM habits ORDER BY isCompleted ASC, currentStreak DESC")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE id = :id")
    fun getHabitById(id: Long): Flow<HabitEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity): Long

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)

    @Query("UPDATE habits SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateCompletionStatus(id: Long, isCompleted: Boolean)

    @Query("UPDATE habits SET currentStreak = :currentStreak, bestStreak = :bestStreak WHERE id = :id")
    suspend fun updateStreaks(id: Long, currentStreak: Int, bestStreak: Int)

    @Query("UPDATE habits SET currentStreak = :currentStreak, bestStreak = :bestStreak, isCompleted = :isCompleted, totalCompletions = totalCompletions + 1 WHERE id = :id")
    suspend fun recordCompletion(id: Long, currentStreak: Int, bestStreak: Int, isCompleted: Boolean)

    @Query("UPDATE habits SET isCompleted = 0")
    suspend fun resetDailyCompletionStatus()
}
