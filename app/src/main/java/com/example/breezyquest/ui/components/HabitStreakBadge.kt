package com.example.breezyquest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class HabitStreakTier(
    val streak: Int,
    val icon: String,
    val label: String,
    val color: Color,
    val backgroundColor: Color
)

fun getHabitStreakTier(streak: Int): HabitStreakTier {
    return when {
        streak <= 0 -> HabitStreakTier(
            streak = 0,
            icon = "🌱",
            label = "Starting",
            color = Color(0xFF94A3B8),
            backgroundColor = Color(0xFF1E293B)
        )
        streak in 1..2 -> HabitStreakTier(
            streak = streak,
            icon = "🔥",
            label = "Spark",
            color = Color(0xFFFB923C), // Amber Orange
            backgroundColor = Color(0x33FB923C)
        )
        streak in 3..6 -> HabitStreakTier(
            streak = streak,
            icon = "🔥",
            label = "Flame",
            color = Color(0xFFFBBF24), // Quest Gold
            backgroundColor = Color(0x33FBBF24)
        )
        streak in 7..13 -> HabitStreakTier(
            streak = streak,
            icon = "⚡",
            label = "Supercharged",
            color = Color(0xFF06B6D4), // Cyan
            backgroundColor = Color(0x3306B6D4)
        )
        streak in 14..29 -> HabitStreakTier(
            streak = streak,
            icon = "💎",
            label = "Diamond",
            color = Color(0xFF38BDF8), // Diamond Sky
            backgroundColor = Color(0x3338BDF8)
        )
        streak in 30..59 -> HabitStreakTier(
            streak = streak,
            icon = "👑",
            label = "Master",
            color = Color(0xFFA855F7), // Purple
            backgroundColor = Color(0x33A855F7)
        )
        else -> HabitStreakTier(
            streak = streak,
            icon = "🌟",
            label = "Legendary",
            color = Color(0xFF10B981), // Emerald
            backgroundColor = Color(0x3310B981)
        )
    }
}

/**
 * Calculates the exact consecutive day streak for a habit by scanning
 * backward from today (or yesterday if today is not completed yet).
 */
fun calculateConsecutiveStreak(completedDates: Set<String>, todayStr: String): Int {
    if (completedDates.isEmpty()) return 0
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    var streak = 0
    val isTodayDone = completedDates.contains(todayStr)

    if (isTodayDone) {
        streak++
        var offset = 1
        while (true) {
            val c = Calendar.getInstance()
            c.add(Calendar.DAY_OF_YEAR, -offset)
            val prevDate = sdf.format(c.time)
            if (completedDates.contains(prevDate)) {
                streak++
                offset++
            } else {
                break
            }
        }
    } else {
        // Today is not done yet. Check if yesterday was completed to keep current active streak alive
        val yesterdayCal = Calendar.getInstance()
        yesterdayCal.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayStr = sdf.format(yesterdayCal.time)
        if (completedDates.contains(yesterdayStr)) {
            streak++
            var offset = 2
            while (true) {
                val c = Calendar.getInstance()
                c.add(Calendar.DAY_OF_YEAR, -offset)
                val prevDate = sdf.format(c.time)
                if (completedDates.contains(prevDate)) {
                    streak++
                    offset++
                } else {
                    break
                }
            }
        } else {
            streak = 0
        }
    }
    return streak
}

@Composable
fun HabitStreakBadge(
    streak: Int,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val tier = getHabitStreakTier(streak)
    Row(
        modifier = modifier
            .background(tier.backgroundColor, RoundedCornerShape(8.dp))
            .border(1.dp, tier.color.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(horizontal = 7.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(tier.icon, fontSize = 12.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = if (compact) "${streak}d" else "${streak}d • ${tier.label}",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = tier.color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
