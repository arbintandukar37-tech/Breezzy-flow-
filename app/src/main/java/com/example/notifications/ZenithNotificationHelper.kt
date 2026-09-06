package com.example.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import kotlin.random.Random

object ZenithNotificationHelper {

  const val CHANNEL_ID = "breezy_flow_channel"
  private const val CHANNEL_NAME = "Breezy Flow Accountability & Alerts"
  private const val CHANNEL_DESC = "Instant accountability reminders, habit streaks, task matrix deadlines, and NRs budget alerts."


  fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val importance = NotificationManager.IMPORTANCE_HIGH
      val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
        description = CHANNEL_DESC
        enableVibration(true)
        vibrationPattern = longArrayOf(0, 250, 150, 250)
      }
      val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.createNotificationChannel(channel)
    }
  }

  fun sendSystemNotification(
    context: Context,
    title: String,
    message: String,
    notificationId: Int = Random.nextInt(1000, 99999)
  ): Boolean {
    createNotificationChannel(context)

    // Check permission on Android 13+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
        != PackageManager.PERMISSION_GRANTED
      ) {
        return false
      }
    }

    val intent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent = PendingIntent.getActivity(
      context,
      0,
      intent,
      PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(android.R.drawable.ic_dialog_alert)
      .setContentTitle(title)
      .setContentText(message)
      .setStyle(NotificationCompat.BigTextStyle().bigText(message))
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setAutoCancel(true)
      .setContentIntent(pendingIntent)
      .setVibrate(longArrayOf(0, 250, 100, 250))

    try {
      with(NotificationManagerCompat.from(context)) {
        notify(notificationId, builder.build())
      }
      return true
    } catch (_: SecurityException) {
      return false
    }
  }

  // Insane Accountability Coach Quotes
  val SAVAGE_QUOTES = listOf(
    "Oi आलु! You haven't checked your habits today. Are you waiting for a Dashain holiday to be disciplined?",
    "Bro spent another रू 400 on C-Momo while your gym habit is weeping in the corner. Lock in!",
    "Your 6-day streak is hanging by a thread! Put down the reels and do 20 pushups now.",
    "Procrastinating on your 'Do First' Eisenhower task again? The matrix sees everything, saathi.",
    "Your wallet balance just took a hit. Cook at home today or start looking for side hustles in Kathmandu!",
    "Breezy Flow Coach detected 0 minutes of coding today. That startup won't build itself!"

  )

  val HYPE_QUOTES = listOf(
    "⚡ UNSTOPPABLE BEAST! You're dominating your habits today. Keep the fire burning!",
    "🔥 +50 XP INCOMING! Complete one more task and ascend to the next Zenith Level!",
    "🚀 Peak discipline mode activated. Your consistency ring is glowing like Everest at dawn!",
    "💪 Financial discipline master: You are building true wealth in Nepalese Rupees step by step!"
  )

  val ZEN_QUOTES = listOf(
    "🧘 Inhale peace, exhale distraction. Take a sip of Himalayan water and focus on the present moment.",
    "🌿 Small mindful actions daily build an unshakeable life. One habit at a time.",
    "☕ Enjoy your morning chiya, reflect upon your matrix priorities, and move with serene clarity."
  )

  fun getRandomQuote(mode: String): String {
    return when (mode) {
      "HYPE" -> HYPE_QUOTES.random()
      "ZEN" -> ZEN_QUOTES.random()
      else -> SAVAGE_QUOTES.random()
    }
  }
}
