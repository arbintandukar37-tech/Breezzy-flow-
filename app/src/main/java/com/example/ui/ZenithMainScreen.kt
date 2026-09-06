package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.InAppNotificationBanner
import com.example.ui.components.LevelHeader
import com.example.ui.screens.DashboardOverviewScreen
import com.example.ui.screens.FinanceScreen
import com.example.ui.screens.HabitsScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.theme.BreezyBg
import com.example.ui.theme.BreezyMint
import com.example.ui.theme.BreezySky
import com.example.ui.theme.BreezySunset
import com.example.ui.theme.BreezySurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary

@Composable
fun ZenithMainScreen(
  viewModel: ZenithViewModel,
  onRequestNotificationPermission: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableIntStateOf(0) }

  val habits by viewModel.habits.collectAsState()
  val tasks by viewModel.tasks.collectAsState()
  val transactions by viewModel.transactions.collectAsState()
  val budgets by viewModel.budgets.collectAsState()
  val notifications by viewModel.notifications.collectAsState()
  val userProfile by viewModel.userProfile.collectAsState()
  val activeBanner by viewModel.activeBanner.collectAsState()
  val consistencyRate by viewModel.consistencyRate.collectAsState()
  val walletBalances by viewModel.walletBalances.collectAsState()
  val wallets by viewModel.wallets.collectAsState()

  val unreadNotificationsCount = notifications.count { !it.isRead }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = BreezyBg,
    bottomBar = {
      NavigationBar(
        containerColor = BreezySurface,
        tonalElevation = 6.dp
      ) {
        val navItems = listOf(
          Triple("Overview", Icons.Default.Home, "nav_overview"),
          Triple("Habits", Icons.Default.LocalFireDepartment, "nav_habits"),
          Triple("Tasks", Icons.Default.GridView, "nav_tasks"),
          Triple("NRs", Icons.Default.AccountBalanceWallet, "nav_finance"),
          Triple("Coach", Icons.Default.Notifications, "nav_coach")
        )

        navItems.forEachIndexed { index, (label, icon, testTag) ->
          val isSelected = selectedTab == index
          val activeColor = when (index) {
            0 -> BreezySky
            1 -> BreezyMint
            2 -> BreezySky
            3 -> BreezyMint
            else -> BreezySunset
          }

          NavigationBarItem(
            selected = isSelected,
            onClick = {
              selectedTab = index
              if (index == 4) {
                viewModel.markAllNotificationsRead()
              }
            },
            icon = {
              Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) activeColor else TextMuted
              )
            },
            label = {
              Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) activeColor else TextMuted
              )
            },
            colors = NavigationBarItemDefaults.colors(
              indicatorColor = activeColor.copy(alpha = 0.12f)
            ),
            modifier = Modifier.testTag(testTag)
          )
        }
      }
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      // 1. Top Level & XP Bar + Coach mode selector + Notification bell
      LevelHeader(
        profile = userProfile,
        unreadNotifications = unreadNotificationsCount,
        onNotificationsClick = {
          selectedTab = 4
          viewModel.markAllNotificationsRead()
        },
        onCoachModeChange = { mode ->
          viewModel.setCoachMode(mode)
        },
        onTriggerCoachPing = {
          onRequestNotificationPermission()
          viewModel.triggerInstantCoachPing()
        }
      )

      // 2. Real-time In-App Live Notification Banner
      InAppNotificationBanner(
        notification = activeBanner,
        onDismiss = { viewModel.dismissBanner() }
      )

      // 3. Tab Screens
      Box(modifier = Modifier.fillMaxSize()) {
        when (selectedTab) {
          0 -> DashboardOverviewScreen(
            profile = userProfile,
            habits = habits,
            tasks = tasks,
            transactions = transactions,
            walletBalances = walletBalances,
            consistencyRate = consistencyRate,
            onToggleHabit = { viewModel.toggleHabit(it) },
            onToggleTask = { viewModel.toggleTask(it) },
            onNavigateToHabits = { selectedTab = 1 },
            onNavigateToTasks = { selectedTab = 2 },
            onNavigateToFinance = { selectedTab = 3 },
            onTriggerCoachPing = {
              onRequestNotificationPermission()
              viewModel.triggerInstantCoachPing()
            }
          )

          1 -> HabitsScreen(
            habits = habits,
            onToggleHabit = { viewModel.toggleHabit(it) },
            onAddHabit = { name, cat, days, color, icon ->
              viewModel.addHabit(name, cat, days, color, icon)
            },
            onDeleteHabit = { viewModel.deleteHabit(it) }
          )


          2 -> TasksScreen(
            tasks = tasks,
            onToggleTask = { viewModel.toggleTask(it) },
            onToggleSubtask = { task, subId -> viewModel.toggleSubtask(task, subId) },
            onAddTask = { title, notes, quad, prio, due, subtasks ->
              viewModel.addTask(title, notes, quad, prio, due, subtasks)
            },
            onDeleteTask = { viewModel.deleteTask(it) }
          )

          3 -> FinanceScreen(
            wallets = wallets,
            transactions = transactions,
            budgets = budgets,
            walletBalances = walletBalances,
            onUpdateWalletBalance = { walletId, newBal -> viewModel.setWalletBalance(walletId, newBal) },
            onAddWallet = { name, bal, colorHex, iconName -> viewModel.addCustomWallet(name, bal, colorHex, iconName) },
            onAddTransaction = { title, amount, type, cat, wallet, whereSpent, howSpent, notes ->
              viewModel.addTransaction(title, amount, type, cat, wallet, whereSpent, howSpent, notes)
            },
            onDeleteTransaction = { viewModel.deleteTransaction(it) },
            onSetBudget = { cat, limit -> viewModel.setBudgetLimit(cat, limit) }
          )

          4 -> NotificationsScreen(
            notifications = notifications,
            currentCoachMode = userProfile?.coachMode ?: "AGGRESSIVE",
            onSetCoachMode = { viewModel.setCoachMode(it) },
            onTriggerInstantPing = {
              onRequestNotificationPermission()
              viewModel.triggerInstantCoachPing()
            },
            onClearAll = { viewModel.clearAllNotifications() },
            onMarkAllRead = { viewModel.markAllNotificationsRead() }
          )
        }
      }
    }
  }
}
