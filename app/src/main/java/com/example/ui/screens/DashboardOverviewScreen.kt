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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.HabitEntity
import com.example.data.models.TaskEntity
import com.example.data.models.TransactionEntity
import com.example.data.models.UserProfileEntity
import com.example.ui.components.LiveConsistencyRing
import com.example.ui.theme.BankBlue
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
import com.example.ui.theme.BreezySurfaceVariant
import com.example.ui.theme.BreezyViolet
import com.example.ui.theme.BreezyVioletLight
import com.example.ui.theme.CashGreen
import com.example.ui.theme.EsewaGreen
import com.example.ui.theme.KhaltiPurple
import com.example.ui.theme.QuadrantDelegate
import com.example.ui.theme.QuadrantDoFirst
import com.example.ui.theme.QuadrantEliminate
import com.example.ui.theme.QuadrantSchedule
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun DashboardOverviewScreen(
  profile: UserProfileEntity?,
  habits: List<HabitEntity>,
  tasks: List<TaskEntity>,
  transactions: List<TransactionEntity>,
  walletBalances: Map<String, Double>,
  consistencyRate: Float,
  onToggleHabit: (HabitEntity) -> Unit,
  onToggleTask: (TaskEntity) -> Unit,
  onNavigateToHabits: () -> Unit,
  onNavigateToTasks: () -> Unit,
  onNavigateToFinance: () -> Unit,
  onTriggerCoachPing: () -> Unit,
  modifier: Modifier = Modifier
) {
  val totalHabits = habits.size
  val completedHabits = habits.count { it.isCompletedToday }
  val totalTasks = tasks.size
  val completedTasks = tasks.count { it.isCompleted }
  val streakDays = profile?.globalStreak ?: 7
  val coachMode = profile?.coachMode ?: "AGGRESSIVE"


  val totalIncome = transactions.filter { it.type == "INCOME" }.sumOf { it.amount }
  val totalExpense = transactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }
  val netWorth = walletBalances.values.sum()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(BreezyBg)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    contentPadding = PaddingValues(top = 6.dp, bottom = 100.dp)
  ) {
    // 1. Live Consistency Ring Component
    item {
      LiveConsistencyRing(
        completionRate = consistencyRate,
        streakDays = streakDays,
        habitsDone = completedHabits,
        totalHabits = totalHabits,
        tasksDone = completedTasks,
        totalTasks = totalTasks
      )
    }

    // 2. Accountability Coach Quote & Ping Card (Light & Vibrant)
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .shadow(elevation = 2.dp, shape = RoundedCornerShape(20.dp), spotColor = Color(0x15FF5B5B))
          .clip(RoundedCornerShape(20.dp))
          .background(
            Brush.linearGradient(
              listOf(Color(0xFFFFF1F2), Color(0xFFFFFBEB))
            )
          )
          .border(1.dp, Color(0xFFFFCDD2), RoundedCornerShape(20.dp))
          .padding(16.dp)
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
                  .size(28.dp)
                  .clip(CircleShape)
                  .background(BreezySunset.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Whatshot,
                  contentDescription = null,
                  tint = BreezySunset,
                  modifier = Modifier.size(16.dp)
                )
              }
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "COACH ROAST · $coachMode MODE",
                color = Color(0xFFBE123C),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.8.sp
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(BreezySunset)
                .clickable { onTriggerCoachPing() }
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = "Ping Me",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          val coachQuote = when (coachMode) {
            "HYPE" -> "⚡ 'Every single habit checked off compounds into greatness! Keep this blazing flow!'"
            "ZEN" -> "🧘 'Like the quiet mountain stream, true discipline flows without turbulence.'"
            else -> "😈 'Oye bideshi, 7 habits scheduled and you haven\\'t finished morning meditation yet? Stop scrolling and move!'"
          }

          Text(
            text = coachQuote,
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 18.sp
          )
        }
      }
    }

    // 3. Quick Habit Check-in Section
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.LocalFireDepartment,
            contentDescription = null,
            tint = BreezyMint,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Today's Habits ($completedHabits/$totalHabits)",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
        }

        IconButton(
          onClick = onNavigateToHabits,
          modifier = Modifier.size(28.dp)
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "View all habits",
            tint = BreezySky,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }

    items(habits.take(4), key = { it.id }) { habit ->
      DashboardHabitRow(
        habit = habit,
        onToggle = { onToggleHabit(habit) }
      )
    }

    // 4. Eisenhower Task Matrix Quick Peek
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.ElectricBolt,
            contentDescription = null,
            tint = BreezySunset,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Do First Tasks (Urgent)",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
        }

        IconButton(
          onClick = onNavigateToTasks,
          modifier = Modifier.size(28.dp)
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "View matrix",
            tint = BreezySky,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }

    val urgentTasks = tasks.filter { it.quadrant == "DO_FIRST" }.take(3)
    if (urgentTasks.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(BreezySurface)
            .border(1.dp, BreezyBorder, RoundedCornerShape(14.dp))
            .padding(16.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "All urgent tasks cleared! You're in pure flow.",
            color = TextMuted,
            fontSize = 13.sp
          )
        }
      }
    } else {
      items(urgentTasks, key = { it.id }) { task ->
        DashboardTaskRow(
          task = task,
          onToggle = { onToggleTask(task) }
        )
      }
    }

    // 5. Nepalese Rupees Finance Pulse (Light & Vibrant)
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.AccountBalanceWallet,
            contentDescription = null,
            tint = BreezyMint,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "NRs Financial Pulse",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
        }

        IconButton(
          onClick = onNavigateToFinance,
          modifier = Modifier.size(28.dp)
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "View Finance",
            tint = BreezySky,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }

    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onNavigateToFinance() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BreezySurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BreezyBorder, RoundedCornerShape(20.dp))
            .padding(18.dp)
        ) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "TOTAL NET BALANCE",
                  color = TextMuted,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 0.5.sp
                )
                Text(
                  text = "रू ${String.format("%,.2f", netWorth)}",
                  color = TextPrimary,
                  fontSize = 24.sp,
                  fontWeight = FontWeight.Black
                )
              }

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(10.dp))
                  .background(BreezyMintLight)
                  .padding(horizontal = 10.dp, vertical = 6.dp)
              ) {
                Text(
                  text = "+रू ${totalIncome.toInt()} Inflow",
                  color = Color(0xFF065F46),
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Wallets Quick Pills
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              WalletQuickPill(
                label = "Cash",
                amount = walletBalances["CASH"] ?: 0.0,
                color = CashGreen,
                modifier = Modifier.weight(1f)
              )
              WalletQuickPill(
                label = "Bank",
                amount = walletBalances["BANK"] ?: 0.0,
                color = BankBlue,
                modifier = Modifier.weight(1f)
              )
              WalletQuickPill(
                label = "eSewa",
                amount = walletBalances["ESEWA"] ?: 0.0,
                color = EsewaGreen,
                modifier = Modifier.weight(1f)
              )
              WalletQuickPill(
                label = "Khalti",
                amount = walletBalances["KHALTI"] ?: 0.0,
                color = KhaltiPurple,
                modifier = Modifier.weight(1f)
              )
            }
          }
        }
      }
    }

    // 6. Badges & Milestone Rewards Row (Light & Vibrant)
    item {
      Text(
        text = "Badges & Milestones Unlocked",
        color = TextPrimary,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(6.dp))
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        item {
          BadgeCard(
            title = "7-Day Flame",
            desc = "Consistency master",
            icon = Icons.Default.LocalFireDepartment,
            color = BreezyAmber,
            lightBg = BreezyAmberLight
          )
        }
        item {
          BadgeCard(
            title = "Momo Guardian",
            desc = "Under budget cap",
            icon = Icons.Default.Shield,
            color = BreezyMint,
            lightBg = BreezyMintLight
          )
        }
        item {
          BadgeCard(
            title = "Flow Pilot",
            desc = "Level 3 reached",
            icon = Icons.Default.Air,
            color = BreezySky,
            lightBg = BreezySkyLight
          )
        }
        item {
          BadgeCard(
            title = "Matrix Cleared",
            desc = "High priority 100%",
            icon = Icons.Default.ElectricBolt,
            color = BreezyViolet,
            lightBg = BreezyVioletLight
          )
        }
      }
    }
  }
}

@Composable
fun DashboardHabitRow(
  habit: HabitEntity,
  onToggle: () -> Unit
) {
  val iconVector = when (habit.iconName) {
    "SelfImprovement" -> Icons.Default.SelfImprovement
    "FitnessCenter" -> Icons.Default.FitnessCenter
    "MenuBook" -> Icons.Default.MenuBook
    "LocalDrink" -> Icons.Default.LocalDrink
    "Terminal" -> Icons.Default.Terminal
    "Nightlight" -> Icons.Default.Nightlight
    else -> Icons.Default.CheckCircle
  }

  val accentColor = when (habit.colorHex) {
    0xFF10B981L -> BreezyMint
    0xFF0284C7L, 0xFF06B6D4L -> BreezySky
    0xFFF59E0BL -> BreezyAmber
    0xFFFF5757L, 0xFFEF4444L -> BreezySunset
    0xFF8B5CF6L -> BreezyViolet
    else -> Color(habit.colorHex)
  }

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp), spotColor = Color(0x0A000000))
      .clip(RoundedCornerShape(16.dp))
      .background(BreezySurface)
      .border(
        1.dp,
        if (habit.isCompletedToday) BreezyMint.copy(alpha = 0.4f) else BreezyBorder,
        RoundedCornerShape(16.dp)
      )
      .clickable { onToggle() }
      .padding(14.dp)
      .testTag("dashboard_habit_item_${habit.id}")
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(accentColor.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = iconVector,
          contentDescription = null,
          tint = accentColor,
          modifier = Modifier.size(22.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = habit.name,
          color = if (habit.isCompletedToday) TextMuted else TextPrimary,
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            Icons.Default.LocalFireDepartment,
            contentDescription = null,
            tint = BreezyAmber,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(2.dp))
          Text(
            text = "${habit.streakCount} day streak",
            color = Color(0xFFB45309),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "•", color = Color(0xFFCBD5E1), fontSize = 11.sp)
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = habit.category,
            color = TextSecondary,
            fontSize = 11.sp
          )
        }
      }

      Box(
        modifier = Modifier
          .size(30.dp)
          .clip(CircleShape)
          .background(if (habit.isCompletedToday) BreezyMint else Color(0xFFF1F5F9))
          .border(
            1.5.dp,
            if (habit.isCompletedToday) BreezyMint else Color(0xFFCBD5E1),
            CircleShape
          ),
        contentAlignment = Alignment.Center
      ) {
        if (habit.isCompletedToday) {
          Icon(
            Icons.Default.Check,
            contentDescription = "Done",
            tint = Color.White,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
}

@Composable
fun DashboardTaskRow(
  task: TaskEntity,
  onToggle: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .shadow(elevation = 1.dp, shape = RoundedCornerShape(14.dp), spotColor = Color(0x0A000000))
      .clip(RoundedCornerShape(14.dp))
      .background(BreezySurface)
      .border(1.dp, if (task.isCompleted) BreezyBorder else QuadrantDoFirst.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
      .clickable { onToggle() }
      .padding(12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(24.dp)
          .clip(CircleShape)
          .background(if (task.isCompleted) BreezyMint else Color(0xFFF1F5F9))
          .border(
            1.5.dp,
            if (task.isCompleted) BreezyMint else Color(0xFFCBD5E1),
            CircleShape
          ),
        contentAlignment = Alignment.Center
      ) {
        if (task.isCompleted) {
          Icon(
            Icons.Default.Check,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(14.dp)
          )
        }
      }

      Spacer(modifier = Modifier.width(10.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = task.title,
          color = if (task.isCompleted) TextMuted else TextPrimary,
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold
        )
        Text(
          text = "Due: ${task.dueDateText} · Urgent & High Impact",
          color = QuadrantDoFirst,
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium
        )
      }
    }
  }
}


@Composable
fun WalletQuickPill(
  label: String,
  amount: Double,
  color: Color,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(10.dp))
      .background(color.copy(alpha = 0.08f))
      .border(1.dp, color.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
      .padding(vertical = 6.dp, horizontal = 4.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = label,
        color = color,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = "रू ${amount.toInt()}",
        color = TextPrimary,
        fontSize = 11.sp,
        fontWeight = FontWeight.Black
      )
    }
  }
}

@Composable
fun BadgeCard(
  title: String,
  desc: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  color: Color,
  lightBg: Color
) {
  Box(
    modifier = Modifier
      .width(130.dp)
      .shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp), spotColor = Color(0x0A000000))
      .clip(RoundedCornerShape(16.dp))
      .background(BreezySurface)
      .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
      .padding(12.dp)
  ) {
    Column {
      Box(
        modifier = Modifier
          .size(34.dp)
          .clip(CircleShape)
          .background(lightBg),
        contentAlignment = Alignment.Center
      ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(text = title, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
      Text(text = desc, color = TextSecondary, fontSize = 10.sp)
    }
  }
}
