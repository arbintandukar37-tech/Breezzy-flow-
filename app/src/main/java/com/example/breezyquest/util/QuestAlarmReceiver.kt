package com.example.breezyquest.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class QuestAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val questId = intent.getLongExtra("quest_id", 0L)
        val questTitle = intent.getStringExtra("quest_title") ?: "Quest"
        val questCategory = intent.getStringExtra("quest_category") ?: "Missions"
        val questPriority = intent.getStringExtra("quest_priority") ?: "MEDIUM"
        val startTime = intent.getStringExtra("start_time") ?: ""
        val xpReward = intent.getIntExtra("xp_reward", 20)
        val isLeadTime = intent.getBooleanExtra("is_lead_time", false)
        val leadMinutes = intent.getIntExtra("lead_minutes", 5)

        val prefs = context.getSharedPreferences("breezy_quest_prefs", Context.MODE_PRIVATE)
        val questRemindersEnabled = prefs.getBoolean("quest_start_reminders", true)

        if (!questRemindersEnabled) {
            return
        }

        NotificationHelper.sendRecognizableQuestNotification(
            context = context,
            questId = questId,
            questTitle = questTitle,
            questCategory = questCategory,
            questPriority = questPriority,
            startTime = startTime,
            xpReward = xpReward,
            isLeadTime = isLeadTime,
            leadMinutes = leadMinutes
        )
    }
}
