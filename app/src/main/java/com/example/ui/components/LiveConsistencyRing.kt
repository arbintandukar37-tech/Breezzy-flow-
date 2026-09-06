package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BreezyAmber
import com.example.ui.theme.BreezyAmberLight
import com.example.ui.theme.BreezyBorder
import com.example.ui.theme.BreezyMint
import com.example.ui.theme.BreezyMintLight
import com.example.ui.theme.BreezySky
import com.example.ui.theme.BreezySkyLight
import com.example.ui.theme.BreezySunset
import com.example.ui.theme.BreezySurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun LiveConsistencyRing(
  completionRate: Float,
  streakDays: Int,
  habitsDone: Int,
  totalHabits: Int,
  tasksDone: Int,
  totalTasks: Int,
  modifier: Modifier = Modifier
) {
  val animatedProgress by animateFloatAsState(
    targetValue = completionRate.coerceIn(0f, 1f),
    animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
    label = "consistency_ring_progress"
  )

  val percent = (animatedProgress * 100).toInt()

  Box(
    modifier = modifier
      .fillMaxWidth()
      .shadow(elevation = 3.dp, shape = RoundedCornerShape(24.dp), spotColor = Color(0x1A0284C7))
      .clip(RoundedCornerShape(24.dp))
      .background(
        Brush.linearGradient(
          colors = listOf(BreezySurface, Color(0xFFF0F9FF))
        )
      )
      .border(1.dp, BreezyBorder, RoundedCornerShape(24.dp))
      .padding(20.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Left side: stats breakdown
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(BreezyAmberLight)
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = "Streak flame",
                tint = BreezyAmber,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "$streakDays DAY STREAK",
                color = Color(0xFFB45309),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "Daily Discipline Ring",
          color = TextPrimary,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold
        )

        Text(
          text = if (percent == 100) "Unstoppable! Perfect flow today." else "$habitsDone of $totalHabits habits completed",
          color = TextSecondary,
          fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          // Habits Pill
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(BreezyMintLight)
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = BreezyMint,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Habits: $habitsDone/$totalHabits",
                color = Color(0xFF065F46),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          // Tasks Pill
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(BreezySkyLight)
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint = BreezySky,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Tasks: $tasksDone/$totalTasks",
                color = Color(0xFF075985),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.width(16.dp))

      // Right side: Circular Consistency Ring
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .size(110.dp)
          .testTag("consistency_ring_indicator")
      ) {
        val sweepAngle = animatedProgress * 360f

        Canvas(modifier = Modifier.size(96.dp)) {
          // Track background
          drawArc(
            color = Color(0xFFE2E8F0),
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
          )

          // Glowing active arc with breezy vibrant gradient
          drawArc(
            brush = Brush.sweepGradient(
              listOf(BreezySky, BreezyMint, BreezyAmber, BreezySunset, BreezySky)
            ),
            startAngle = -90f,
            sweepAngle = if (sweepAngle == 0f) 0.1f else sweepAngle,
            useCenter = false,
            style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
          )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "$percent%",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black
          )
          Text(
            text = "FLOW",
            color = BreezySky,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
          )
        }
      }
    }
  }
}
