package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.breezyquest.util.NotificationHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class QuestNotificationUnitTest {

    @Test
    fun testParseDateTimeToMillis() {
        val dateStr = "2026-09-08"
        val timeStr = "07:00 PM"
        val millis = NotificationHelper.parseDateTimeToMillis(dateStr, timeStr)
        assertNotNull(millis)
        assertTrue(millis!! > 0)
    }

    @Test
    fun testParse24HourFormat() {
        val dateStr = "2026-09-08"
        val timeStr = "19:00"
        val millis = NotificationHelper.parseDateTimeToMillis(dateStr, timeStr)
        assertNotNull(millis)
        assertTrue(millis!! > 0)
    }

    @Test
    fun testSendRecognizableNotificationDoesNotCrash() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Verify dispatching does not crash or throw exceptions
        NotificationHelper.sendRecognizableQuestNotification(
            context = context,
            questId = 42L,
            questTitle = "Conquer Algorithms",
            questCategory = "Study",
            questPriority = "EPIC",
            startTime = "07:00 PM",
            xpReward = 75,
            isLeadTime = false
        )

        NotificationHelper.sendRecognizableQuestNotification(
            context = context,
            questId = 42L,
            questTitle = "Conquer Algorithms",
            questCategory = "Study",
            questPriority = "EPIC",
            startTime = "07:00 PM",
            xpReward = 75,
            isLeadTime = true,
            leadMinutes = 5
        )
        assertTrue(true)
    }

    @Test
    fun testNotificationPreferencesInSharedPreferences() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = context.getSharedPreferences("breezy_quest_prefs", Context.MODE_PRIVATE)

        prefs.edit().putBoolean("quest_start_reminders", true).apply()
        assertEquals(true, prefs.getBoolean("quest_start_reminders", false))

        prefs.edit().putBoolean("quest_lead_reminder_5min", true).apply()
        assertEquals(true, prefs.getBoolean("quest_lead_reminder_5min", false))
    }
}
