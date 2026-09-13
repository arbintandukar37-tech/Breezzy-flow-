package com.example.breezyquest.util

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object NotificationHelper {

    const val CHANNEL_QUESTS = "channel_quests_v2"
    const val CHANNEL_HABITS = "channel_habits"
    const val CHANNEL_BUDGETS = "channel_budgets"
    const val CHANNEL_GOALS = "channel_goals"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Quest Channel with HIGH Importance, Lights, and Vibration for Lock Screen & Heads-up Display
            val questChannel = NotificationChannel(
                CHANNEL_QUESTS,
                "Quest Reminders & Starts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Instantly recognizable alerts when scheduled quests kick off or approach"
                enableLights(true)
                lightColor = Color.CYAN
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 150, 250)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }

            val habitChannel = NotificationChannel(
                CHANNEL_HABITS,
                "Habit Streaks",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily habit streak reminders"
            }

            val budgetChannel = NotificationChannel(
                CHANNEL_BUDGETS,
                "Budget Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts when spending approaches or exceeds budget limits"
            }

            val goalChannel = NotificationChannel(
                CHANNEL_GOALS,
                "Earning & Goals",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Updates on your monthly earning targets and life goals"
            }

            manager.createNotificationChannels(listOf(questChannel, habitChannel, budgetChannel, goalChannel))
        }
    }

    /**
     * Dispatches an instantly recognizable Quest Kickoff or Lead-time Notification
     */
    fun sendRecognizableQuestNotification(
        context: Context,
        questId: Long,
        questTitle: String,
        questCategory: String,
        questPriority: String,
        startTime: String,
        xpReward: Int,
        isLeadTime: Boolean,
        leadMinutes: Int = 5
    ) {
        try {
            createNotificationChannels(context)

            val openIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("navigate_to", "quests")
                putExtra("quest_id", questId)
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                (questId * 10 + (if (isLeadTime) 1 else 0)).toInt(),
                openIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val headerPrefix = if (isLeadTime) {
                "⏳ Quest Starting in $leadMinutes Mins!"
            } else {
                "⚔️ QUEST KICKOFF: Active Now!"
            }

            val priorityBadge = when (questPriority.uppercase()) {
                "EPIC" -> "🟣 [EPIC QUEST]"
                "HIGH" -> "🔴 [HIGH PRIORITY]"
                "LOW" -> "🔵 [CHILL QUEST]"
                else -> "🟡 [MEDIUM QUEST]"
            }

            val timeDisplay = if (startTime.isNotBlank()) "⏰ Scheduled: $startTime" else "⏰ Right Now"
            val shortText = if (isLeadTime) {
                "Prepare for \"$questTitle\" • Starts in $leadMinutes min • +$xpReward XP"
            } else {
                "\"$questTitle\" has begun! Step up and claim +$xpReward XP!"
            }

            val bigTextContent = buildString {
                append(priorityBadge)
                append("\n⚔️ Mission: ")
                append(questTitle)
                append("\n$timeDisplay • 📂 Category: ")
                append(questCategory)
                append("\n✨ Complete on time to claim +")
                append(xpReward)
                append(" XP toward your next rank!")
                if (isLeadTime) {
                    append("\nGet ready: You have $leadMinutes minutes before kickoff.")
                } else {
                    append("\nYour quest timer is ticking. Open Breezy Quest and conquer it!")
                }
            }

            // Large icon using app icon asset if available
            val largeIcon = try {
                BitmapFactory.decodeResource(context.resources, R.drawable.breezy_quest_icon)
            } catch (_: Exception) {
                null
            }

            val notificationId = (questId * 10 + (if (isLeadTime) 1 else 0)).toInt().takeIf { it != 0 } ?: 401

            val builder = NotificationCompat.Builder(context, CHANNEL_QUESTS)
                .setSmallIcon(R.drawable.ic_quest_notification)
                .setContentTitle(headerPrefix)
                .setContentText(shortText)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .setBigContentTitle(headerPrefix)
                        .setSummaryText("Breezy Quest • $questCategory")
                        .bigText(bigTextContent)
                )
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setColor(0xFF06B6D4.toInt()) // Vibrant Breeze Cyan accent color
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)

            if (largeIcon != null) {
                builder.setLargeIcon(largeIcon)
            }

            // Interactive Action Button directly on notification tray
            val actionIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("navigate_to", "quests")
            }
            val actionPendingIntent = PendingIntent.getActivity(
                context,
                notificationId + 9999,
                actionIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            builder.addAction(
                R.drawable.ic_quest_notification,
                "Enter Quest ⚔️",
                actionPendingIntent
            )

            val manager = NotificationManagerCompat.from(context)
            manager.notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            Log.w("NotificationHelper", "Notification permission not granted", e)
        } catch (e: Exception) {
            Log.e("NotificationHelper", "Failed to dispatch quest notification", e)
        }
    }

    /**
     * Parses a date ("YYYY-MM-DD") and time ("HH:mm" or "hh:mm a") into epoch milliseconds
     */
    fun parseDateTimeToMillis(dateStr: String, timeStr: String): Long? {
        if (dateStr.isBlank() || timeStr.isBlank()) return null

        val formats = listOf(
            SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US),
            SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US),
            SimpleDateFormat("yyyy-MM-dd h:mm a", Locale.US),
            SimpleDateFormat("yyyy-MM-dd K:mm a", Locale.US)
        )

        val combined = "$dateStr ${timeStr.trim()}"
        for (sdf in formats) {
            try {
                sdf.isLenient = true
                val date = sdf.parse(combined)
                if (date != null) {
                    return date.time
                }
            } catch (_: Exception) {}
        }
        return null
    }

    /**
     * Schedules the kickoff and optional 5-minute lead time alarm for a quest
     */
    fun scheduleQuestReminder(
        context: Context,
        questId: Long,
        questTitle: String,
        questCategory: String,
        questPriority: String,
        dateStr: String,
        startTime: String,
        xpReward: Int
    ) {
        val prefs = context.getSharedPreferences("breezy_quest_prefs", Context.MODE_PRIVATE)
        val remindersEnabled = prefs.getBoolean("quest_start_reminders", true)
        val leadTimeEnabled = prefs.getBoolean("quest_lead_reminder_5min", false)

        if (!remindersEnabled) return

        val targetMillis = parseDateTimeToMillis(dateStr, startTime)
        val now = System.currentTimeMillis()

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        // 1. Kickoff Reminder (Exact or fallback)
        if (targetMillis != null && targetMillis > now) {
            val intent = Intent(context, QuestAlarmReceiver::class.java).apply {
                action = "com.example.breezyquest.ACTION_QUEST_REMINDER"
                putExtra("quest_id", questId)
                putExtra("quest_title", questTitle)
                putExtra("quest_category", questCategory)
                putExtra("quest_priority", questPriority)
                putExtra("start_time", startTime)
                putExtra("xp_reward", xpReward)
                putExtra("is_lead_time", false)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                (questId * 10).toInt(),
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, targetMillis, pendingIntent)
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, targetMillis, pendingIntent)
                }
            } catch (_: SecurityException) {
                // Fallback for devices restricting exact alarms
                alarmManager.set(AlarmManager.RTC_WAKEUP, targetMillis, pendingIntent)
            }
        }

        // 2. Optional 5-Minutes Before Reminder
        if (leadTimeEnabled && targetMillis != null) {
            val fiveMinutesBefore = targetMillis - (5 * 60 * 1000)
            if (fiveMinutesBefore > now) {
                val leadIntent = Intent(context, QuestAlarmReceiver::class.java).apply {
                    action = "com.example.breezyquest.ACTION_QUEST_REMINDER"
                    putExtra("quest_id", questId)
                    putExtra("quest_title", questTitle)
                    putExtra("quest_category", questCategory)
                    putExtra("quest_priority", questPriority)
                    putExtra("start_time", startTime)
                    putExtra("xp_reward", xpReward)
                    putExtra("is_lead_time", true)
                    putExtra("lead_minutes", 5)
                }
                val leadPendingIntent = PendingIntent.getBroadcast(
                    context,
                    (questId * 10 + 1).toInt(),
                    leadIntent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )

                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, fiveMinutesBefore, leadPendingIntent)
                    } else {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, fiveMinutesBefore, leadPendingIntent)
                    }
                } catch (_: SecurityException) {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, fiveMinutesBefore, leadPendingIntent)
                }
            }
        }
    }

    /**
     * Cancels any scheduled alarms for a deleted or completed quest
     */
    fun cancelQuestReminder(context: Context, questId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        // Cancel kickoff alarm
        val intent = Intent(context, QuestAlarmReceiver::class.java).apply {
            action = "com.example.breezyquest.ACTION_QUEST_REMINDER"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            (questId * 10).toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }

        // Cancel lead time alarm
        val leadPendingIntent = PendingIntent.getBroadcast(
            context,
            (questId * 10 + 1).toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )
        if (leadPendingIntent != null) {
            alarmManager.cancel(leadPendingIntent)
        }
    }

    fun showNotification(
        context: Context,
        channelId: String,
        notificationId: Int,
        title: String,
        message: String
    ) {
        try {
            createNotificationChannels(context)

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_quest_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setColor(0xFF06B6D4.toInt())
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            val manager = NotificationManagerCompat.from(context)
            manager.notify(notificationId, builder.build())
        } catch (_: SecurityException) {
        } catch (_: Exception) {
        }
    }
}
