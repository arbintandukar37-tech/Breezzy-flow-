package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.NotificationItemEntity
import com.example.ui.theme.BreezyAmber
import com.example.ui.theme.BreezyMint
import com.example.ui.theme.BreezySky
import com.example.ui.theme.BreezySunset
import com.example.ui.theme.BreezySurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun InAppNotificationBanner(
  notification: NotificationItemEntity?,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  AnimatedVisibility(
    visible = notification != null,
    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
    exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
    modifier = modifier
  ) {
    if (notification != null) {
      val (accentColor, badgeText) = when (notification.type) {
        "COACH_ROAST" -> Pair(BreezySunset, "COACH ROAST")
        "FINANCE_BUDGET" -> Pair(BreezySunset, "BUDGET GUARD")
        "HABIT_ALERT" -> Pair(BreezyMint, "HABIT DISCIPLINE")
        "ACHIEVEMENT" -> Pair(BreezySky, "LEVEL UP")
        else -> Pair(BreezyAmber, "FLOW ALERT")
      }

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp)
          .shadow(elevation = 6.dp, shape = RoundedCornerShape(18.dp), spotColor = accentColor.copy(alpha = 0.25f))
          .clip(RoundedCornerShape(18.dp))
          .background(BreezySurface)
          .border(1.5.dp, accentColor, RoundedCornerShape(18.dp))
          .testTag("in_app_notification_banner")
          .clickable { onDismiss() }
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
              .background(accentColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (notification.type == "FINANCE_BUDGET") Icons.Default.Warning else Icons.Default.NotificationsActive,
              contentDescription = "Alert",
              tint = accentColor,
              modifier = Modifier.size(20.dp)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(accentColor)
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = badgeText,
                  color = Color.White,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Black
                )
              }

              Text(
                text = notification.title,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
              )
            }

            Text(
              text = notification.message,
              color = TextSecondary,
              fontSize = 12.sp,
              modifier = Modifier.padding(top = 4.dp),
              lineHeight = 16.sp
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .size(28.dp)
              .testTag("dismiss_notification_button")
          ) {
            Icon(
              Icons.Default.Close,
              contentDescription = "Dismiss",
              tint = Color(0xFF94A3B8),
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }
    }
  }
}
