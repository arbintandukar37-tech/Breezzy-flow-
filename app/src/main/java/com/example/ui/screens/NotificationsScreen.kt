package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.NotificationItemEntity
import com.example.ui.theme.BreezyAmber
import com.example.ui.theme.BreezyAmberLight
import com.example.ui.theme.BreezyBg
import com.example.ui.theme.BreezyBorder
import com.example.ui.theme.BreezyMint
import com.example.ui.theme.BreezyMintLight
import com.example.ui.theme.BreezySky
import com.example.ui.theme.BreezySkyLight
import com.example.ui.theme.BreezySunset
import com.example.ui.theme.BreezySunsetLight
import com.example.ui.theme.BreezySurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotificationsScreen(
  notifications: List<NotificationItemEntity>,
  currentCoachMode: String,
  onSetCoachMode: (String) -> Unit,
  onTriggerInstantPing: () -> Unit,
  onClearAll: () -> Unit,
  onMarkAllRead: () -> Unit,
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(BreezyBg)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp)
  ) {
    // 1. Hero Card: Coach Personality Matrix (Light & Vibrant)
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .shadow(elevation = 3.dp, shape = RoundedCornerShape(24.dp), spotColor = Color(0x150284C7))
          .clip(RoundedCornerShape(24.dp))
          .background(
            Brush.verticalGradient(
              listOf(Color(0xFFFFF1F2), Color(0xFFF8FAFD))
            )
          )
          .border(1.dp, BreezySunset.copy(alpha = 0.35f), RoundedCornerShape(24.dp))
          .padding(18.dp)
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(BreezySunset.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  Icons.Default.Whatshot,
                  contentDescription = null,
                  tint = BreezySunset,
                  modifier = Modifier.size(20.dp)
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "ACCOUNTABILITY COACH",
                  color = BreezySunset,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Black,
                  letterSpacing = 1.sp
                )
                Text(
                  text = "Personality & Hyper-Contextual Pings",
                  color = TextPrimary,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // 3 Mode Switchers
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            CoachModeCard(
              modeKey = "AGGRESSIVE",
              title = "😈 Aggressive / Savage Roast Mode",
              desc = "Funny, sarcastic, brutal wake-up roasts in Nepali & English when you procrastinate or overspend.",
              isSelected = currentCoachMode == "AGGRESSIVE",
              accentColor = BreezySunset,
              lightBg = BreezySunsetLight,
              onClick = { onSetCoachMode("AGGRESSIVE") }
            )

            CoachModeCard(
              modeKey = "HYPE",
              title = "⚡ Hype Beast Dopamine Mode",
              desc = "Electric high-energy encouragement, victory sounds, and relentless positivity.",
              isSelected = currentCoachMode == "HYPE",
              accentColor = BreezyAmber,
              lightBg = BreezyAmberLight,
              onClick = { onSetCoachMode("HYPE") }
            )

            CoachModeCard(
              modeKey = "ZEN",
              title = "🧘 Zen Mindful Master",
              desc = "Gentle, stoic, peaceful reflections inspired by the calm Himalayas.",
              isSelected = currentCoachMode == "ZEN",
              accentColor = BreezySky,
              lightBg = BreezySkyLight,
              onClick = { onSetCoachMode("ZEN") }
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Big Test Ping Button
          Button(
            onClick = { onTriggerInstantPing() },
            colors = ButtonDefaults.buttonColors(
              containerColor = BreezySky,
              contentColor = Color.White
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("send_test_ping_button")
          ) {
            Icon(Icons.Default.ElectricBolt, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Send Instant Coach Notification", fontWeight = FontWeight.Black, fontSize = 13.sp)
          }
        }
      }
    }

    // 2. Notification History Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            Icons.Default.NotificationsActive,
            contentDescription = null,
            tint = BreezyMint,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Alert History (${notifications.size})",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
        }

        if (notifications.isNotEmpty()) {
          Text(
            text = "Clear All",
            color = BreezySky,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
              .clickable { onClearAll() }
              .padding(4.dp)
              .testTag("clear_notifications_btn")
          )
        }
      }
    }

    // 3. Notification Feed Items
    if (notifications.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 30.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              Icons.Default.Notifications,
              contentDescription = null,
              tint = Color(0xFFCBD5E1),
              modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "No alerts logged yet",
              color = TextSecondary,
              fontSize = 14.sp
            )
            Text(
              text = "Tap 'Send Instant Coach Notification' to test!",
              color = TextMuted,
              fontSize = 12.sp
            )
          }
        }
      }
    } else {
      items(notifications, key = { it.id }) { item ->
        NotificationHistoryCard(item = item)
      }
    }
  }
}

@Composable
fun CoachModeCard(
  modeKey: String,
  title: String,
  desc: String,
  isSelected: Boolean,
  accentColor: Color,
  lightBg: Color,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .shadow(elevation = 1.dp, shape = RoundedCornerShape(14.dp), spotColor = Color(0x0A000000))
      .clip(RoundedCornerShape(14.dp))
      .background(if (isSelected) lightBg else BreezySurface)
      .border(
        1.5.dp,
        if (isSelected) accentColor else BreezyBorder,
        RoundedCornerShape(14.dp)
      )
      .clickable { onClick() }
      .padding(12.dp)
      .testTag("coach_mode_option_$modeKey")
  ) {
    Row(verticalAlignment = Alignment.Top) {
      Box(
        modifier = Modifier
          .padding(top = 2.dp)
          .size(16.dp)
          .clip(CircleShape)
          .background(if (isSelected) accentColor else Color.Transparent)
          .border(2.dp, if (isSelected) accentColor else Color(0xFF94A3B8), CircleShape)
      )

      Spacer(modifier = Modifier.width(10.dp))

      Column {
        Text(
          text = title,
          color = if (isSelected) TextPrimary else TextSecondary,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = desc,
          color = TextSecondary,
          fontSize = 11.sp,
          lineHeight = 15.sp
        )
      }
    }
  }
}

@Composable
fun NotificationHistoryCard(item: NotificationItemEntity) {
  val (accentColor, icon, lightBg) = when (item.type) {
    "COACH_ROAST" -> Triple(BreezySunset, Icons.Default.Whatshot, BreezySunsetLight)
    "FINANCE_BUDGET" -> Triple(BreezySunset, Icons.Default.Warning, BreezySunsetLight)
    "HABIT_ALERT" -> Triple(BreezyMint, Icons.Default.ElectricBolt, BreezyMintLight)
    "ACHIEVEMENT" -> Triple(BreezySky, Icons.Default.Shield, BreezySkyLight)
    else -> Triple(BreezyAmber, Icons.Default.Notifications, BreezyAmberLight)
  }

  val dateFormatted = SimpleDateFormat("hh:mm a, dd MMM", Locale.getDefault()).format(Date(item.timestamp))

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp), spotColor = Color(0x0A000000))
      .clip(RoundedCornerShape(16.dp))
      .background(BreezySurface)
      .border(1.dp, BreezyBorder, RoundedCornerShape(16.dp))
      .padding(14.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.Top
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(lightBg),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = accentColor,
          modifier = Modifier.size(18.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = item.title,
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = dateFormatted,
            color = TextMuted,
            fontSize = 10.sp
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = item.message,
          color = TextSecondary,
          fontSize = 12.sp,
          lineHeight = 16.sp
        )
      }
    }
  }
}
