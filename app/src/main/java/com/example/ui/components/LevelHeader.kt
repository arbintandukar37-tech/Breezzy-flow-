package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.UserProfileEntity
import com.example.ui.theme.BreezyAmber
import com.example.ui.theme.BreezyBg
import com.example.ui.theme.BreezyBorder
import com.example.ui.theme.BreezyMint
import com.example.ui.theme.BreezySky
import com.example.ui.theme.BreezySunset
import com.example.ui.theme.BreezySurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun LevelHeader(
  profile: UserProfileEntity?,
  unreadNotifications: Int,
  onNotificationsClick: () -> Unit,
  onCoachModeChange: (String) -> Unit,
  onTriggerCoachPing: () -> Unit,
  modifier: Modifier = Modifier
) {
  val level = profile?.level ?: 3
  val totalXp = profile?.totalXp ?: 240
  val currentMode = profile?.coachMode ?: "AGGRESSIVE"

  val levelTitles = listOf(
    "Gentle Breeze",
    "Flow Seeker",
    "Habit Pilot",
    "Flow Architect",
    "Breezy Titan",
    "Apex Master"
  )
  val title = levelTitles.getOrElse(level) { "Breezy Master" }

  val xpInCurrentLevel = totalXp % 100
  val xpProgress = (xpInCurrentLevel / 100f).coerceIn(0f, 1f)

  var coachMenuExpanded by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(BreezyBg)
      .padding(horizontal = 16.dp, vertical = 10.dp)
  ) {
    // Top Row: App branding + Coach mode + Notification bell
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(
                Brush.linearGradient(listOf(BreezySky, BreezyMint))
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              Icons.Default.Air,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "BREEZY FLOW",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.1.sp
          )
        }
        Text(
          text = "Habits · Eisenhower Tasks · NRs Ledger",
          color = TextMuted,
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium
        )
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Coach Mode Selector Chip
        Box {
          val (modeIcon, modeColor, modeLabel) = when (currentMode) {
            "HYPE" -> Triple(Icons.Default.Whatshot, BreezyAmber, "HYPE")
            "ZEN" -> Triple(Icons.Default.Psychology, BreezySky, "ZEN")
            else -> Triple(Icons.Default.Whatshot, BreezySunset, "SAVAGE")
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .background(modeColor.copy(alpha = 0.12f))
              .border(1.dp, modeColor.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
              .clickable { coachMenuExpanded = true }
              .padding(horizontal = 10.dp, vertical = 5.dp)
              .testTag("coach_mode_selector")
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = modeIcon,
                contentDescription = null,
                tint = modeColor,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = modeLabel,
                color = modeColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          DropdownMenu(
            expanded = coachMenuExpanded,
            onDismissRequest = { coachMenuExpanded = false },
            modifier = Modifier.background(BreezySurface)
          ) {
            DropdownMenuItem(
              text = {
                Text(
                  "😈 Aggressive / Savage Mode (Roasts)",
                  color = BreezySunset,
                  fontWeight = FontWeight.Bold
                )
              },
              onClick = {
                onCoachModeChange("AGGRESSIVE")
                coachMenuExpanded = false
              }
            )
            DropdownMenuItem(
              text = {
                Text(
                  "⚡ Hype Beast Mode (High Energy)",
                  color = BreezyAmber,
                  fontWeight = FontWeight.Bold
                )
              },
              onClick = {
                onCoachModeChange("HYPE")
                coachMenuExpanded = false
              }
            )
            DropdownMenuItem(
              text = {
                Text(
                  "🧘 Zen Mindful Mode (Peaceful Himalayan)",
                  color = BreezySky,
                  fontWeight = FontWeight.Bold
                )
              },
              onClick = {
                onCoachModeChange("ZEN")
                coachMenuExpanded = false
              }
            )
          }
        }

        // Instant Ping Button (to test notifications on demand!)
        Box(
          modifier = Modifier
            .clip(CircleShape)
            .background(BreezySurface)
            .border(1.dp, BreezyBorder, CircleShape)
            .clickable { onTriggerCoachPing() }
            .padding(7.dp)
            .testTag("trigger_instant_ping_btn"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Air,
            contentDescription = "Trigger Instant Coach Ping",
            tint = BreezySky,
            modifier = Modifier.size(16.dp)
          )
        }

        // Notification Bell Icon with Badge
        Box {
          IconButton(
            onClick = onNotificationsClick,
            modifier = Modifier
              .size(36.dp)
              .testTag("notification_bell_button")
          ) {
            Icon(
              Icons.Default.Notifications,
              contentDescription = "Notification Center",
              tint = TextPrimary,
              modifier = Modifier.size(20.dp)
            )
          }

          if (unreadNotifications > 0) {
            Box(
              modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 4.dp, end = 4.dp)
                .size(16.dp)
                .clip(CircleShape)
                .background(BreezySunset),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = if (unreadNotifications > 9) "9+" else "$unreadNotifications",
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Black
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Level Progress Bar
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .shadow(elevation = 2.dp, shape = RoundedCornerShape(14.dp), spotColor = Color(0x0D000000))
        .clip(RoundedCornerShape(14.dp))
        .background(BreezySurface)
        .border(1.dp, BreezyBorder, RoundedCornerShape(14.dp))
        .padding(horizontal = 12.dp, vertical = 9.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Level Badge
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(BreezyMint)
            .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
          Text(
            text = "LVL $level",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black
          )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = title,
              color = TextPrimary,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold
            )
            Text(
              text = "$totalXp XP ($xpInCurrentLevel/100)",
              color = BreezySky,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(5.dp))

          LinearProgressIndicator(
            progress = { xpProgress },
            modifier = Modifier
              .fillMaxWidth()
              .height(6.dp)
              .clip(RoundedCornerShape(3.dp)),
            color = BreezySky,
            trackColor = Color(0xFFE2E8F0),
            strokeCap = StrokeCap.Round
          )
        }
      }
    }
  }
}
