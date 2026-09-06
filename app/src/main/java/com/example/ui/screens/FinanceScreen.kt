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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.BudgetLimitEntity
import com.example.data.models.TransactionEntity
import com.example.data.models.WalletEntity
import com.example.ui.theme.BankBlue
import com.example.ui.theme.BreezyAmber
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
import com.example.ui.theme.CashGreen
import com.example.ui.theme.EsewaGreen
import com.example.ui.theme.KhaltiPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun FinanceScreen(
  wallets: List<WalletEntity>,
  transactions: List<TransactionEntity>,
  budgets: List<BudgetLimitEntity>,
  walletBalances: Map<String, Double>,
  onUpdateWalletBalance: (String, Double) -> Unit,
  onAddWallet: (String, Double, Long, String) -> Unit,
  onAddTransaction: (String, Double, String, String, String, String, String, String) -> Unit,
  onDeleteTransaction: (TransactionEntity) -> Unit,
  onSetBudget: (String, Double) -> Unit,
  modifier: Modifier = Modifier
) {
  var showAddDialog by remember { mutableStateOf(false) }
  var showBudgetDialog by remember { mutableStateOf(false) }
  var showAddWalletDialog by remember { mutableStateOf(false) }
  var walletToEdit by remember { mutableStateOf<WalletEntity?>(null) }
  var filterType by remember { mutableStateOf("ALL") }

  val totalIncome = transactions.filter { it.type == "INCOME" }.sumOf { it.amount }
  val totalExpense = transactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }
  val totalNetWorth = if (wallets.isNotEmpty()) wallets.sumOf { it.balance } else walletBalances.values.sum()

  val filteredTransactions = when (filterType) {
    "EXPENSE" -> transactions.filter { it.type == "EXPENSE" }
    "INCOME" -> transactions.filter { it.type == "INCOME" }
    else -> transactions
  }

  // Analytics for Where & How money was spent
  val expenseTransactions = transactions.filter { it.type == "EXPENSE" }
  val whereSpentMap = expenseTransactions
    .filter { it.whereSpent.isNotBlank() }
    .groupBy { it.whereSpent }
    .mapValues { entry -> entry.value.sumOf { it.amount } }
    .toList()
    .sortedByDescending { it.second }
    .take(4)

  val howSpentMap = expenseTransactions
    .filter { it.howSpent.isNotBlank() }
    .groupBy { it.howSpent }
    .mapValues { entry -> entry.value.sumOf { it.amount } }
    .toList()
    .sortedByDescending { it.second }
    .take(4)

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = BreezyBg,
    floatingActionButton = {
      FloatingActionButton(
        onClick = { showAddDialog = true },
        containerColor = BreezyMint,
        contentColor = Color.White,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
          .padding(bottom = 70.dp)
          .testTag("add_transaction_fab")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Add, contentDescription = "Log NRs")
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Log Spend / Inflow",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
        }
      }
    }
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp)
    ) {
      // 1. Total Net Worth Hero Card (Breezy Vibrant Gradient)
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(24.dp), spotColor = Color(0x1A10B981))
            .clip(RoundedCornerShape(24.dp))
            .background(
              Brush.linearGradient(
                colors = listOf(Color(0xFFE8F8F0), Color(0xFFF0FDF4), Color(0xFFE0F2FE))
              )
            )
            .border(1.dp, BreezyMint.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
            .padding(20.dp)
        ) {
          Column {
            Text(
              text = "TOTAL NET WORTH (NEPALESE RUPEES)",
              color = Color(0xFF065F46),
              fontSize = 11.sp,
              fontWeight = FontWeight.Black,
              letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "रू ${String.format("%,.2f", totalNetWorth)}",
              color = TextPrimary,
              fontSize = 28.sp,
              fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              // Income Box
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(12.dp))
                  .background(BreezySurface)
                  .border(1.dp, BreezyMint.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                  .padding(10.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    Icons.Default.ArrowUpward,
                    contentDescription = null,
                    tint = BreezyMint,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Column {
                    Text("Total Inflow", color = TextSecondary, fontSize = 10.sp)
                    Text(
                      text = "+ रू ${totalIncome.toInt()}",
                      color = Color(0xFF047857),
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
              }

              // Expense Box
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(12.dp))
                  .background(BreezySurface)
                  .border(1.dp, BreezySunset.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                  .padding(10.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = BreezySunset,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Column {
                    Text("Total Outflow", color = TextSecondary, fontSize = 10.sp)
                    Text(
                      text = "- रू ${totalExpense.toInt()}",
                      color = BreezySunset,
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
              }
            }
          }
        }
      }

      // 2. "Where My Money Is" - Account & Wallet Balances (User can enter & edit)
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.Savings,
                contentDescription = null,
                tint = BreezyMint,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Where My Money Is",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
              )
            }
            Text(
              text = "Tap any card to enter or update your balance",
              color = TextSecondary,
              fontSize = 11.sp
            )
          }

          TextButton(
            onClick = { showAddWalletDialog = true },
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            modifier = Modifier.testTag("add_account_btn")
          ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = BreezySky, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Add Account", color = BreezySky, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Display user's wallets in pairs
        val displayWallets = if (wallets.isNotEmpty()) wallets else listOf(
          WalletEntity("CASH", "Physical Cash", 12500.0, 0xFF10B981L, "Payments"),
          WalletEntity("BANK", "Bank (NIC/Nabil)", 85400.0, 0xFF0284C7L, "AccountBalance"),
          WalletEntity("ESEWA", "eSewa Wallet", 9800.0, 0xFF059669L, "QrCode"),
          WalletEntity("KHALTI", "Khalti Wallet", 4350.0, 0xFF8B5CF6L, "AccountBalanceWallet")
        )

        displayWallets.chunked(2).forEach { pair ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            pair.forEach { wallet ->
              WalletDetailCard(
                wallet = wallet,
                onEditBalance = { walletToEdit = wallet },
                modifier = Modifier.weight(1f)
              )
            }
            if (pair.size == 1) {
              Spacer(modifier = Modifier.weight(1f))
            }
          }
          Spacer(modifier = Modifier.height(8.dp))
        }
      }

      // 3. "Where & How I Spend" Insights & Breakdown
      if (whereSpentMap.isNotEmpty() || howSpentMap.isNotEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .shadow(elevation = 1.dp, shape = RoundedCornerShape(20.dp), spotColor = Color(0x0A000000))
              .clip(RoundedCornerShape(20.dp))
              .background(BreezySurface)
              .border(1.dp, BreezyBorder, RoundedCornerShape(20.dp))
              .padding(16.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  Icons.Default.Storefront,
                  contentDescription = null,
                  tint = BreezyAmber,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Where & How I Spend Money",
                  color = TextPrimary,
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold
                )
              }

              if (whereSpentMap.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Place, contentDescription = null, tint = BreezySunset, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Top Places Spent (कहाँ खर्च भयो)", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  }
                  whereSpentMap.forEach { (place, amt) ->
                    val ratio = if (totalExpense > 0) (amt / totalExpense).toFloat() else 0f
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Text(text = place, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                      Text(
                        text = "रू ${amt.toInt()} (${(ratio * 100).toInt()}%)",
                        color = BreezySunset,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                      )
                    }
                    LinearProgressIndicator(
                      progress = { ratio.coerceIn(0f, 1f) },
                      modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                      color = BreezySunset,
                      trackColor = Color(0xFFF1F5F9),
                      strokeCap = StrokeCap.Round
                    )
                  }
                }
              }

              if (howSpentMap.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CreditCard, contentDescription = null, tint = BreezySky, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Payment Mode (कसरी तिरेँ - How Spent)", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  }
                  LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(howSpentMap) { (method, amt) ->
                      Box(
                        modifier = Modifier
                          .clip(RoundedCornerShape(10.dp))
                          .background(BreezySkyLight)
                          .padding(horizontal = 10.dp, vertical = 6.dp)
                      ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                          Text(text = method, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                          Spacer(modifier = Modifier.width(6.dp))
                          Text(text = "रू ${amt.toInt()}", color = BreezySky, fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }

      // 4. Smart Budget Guardrails with Dynamic Color Shift (Green -> Yellow -> Red)
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              Icons.Default.Shield,
              contentDescription = null,
              tint = BreezySky,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Monthly Budget Guardrails",
              color = TextPrimary,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold
            )
          }

          IconButton(
            onClick = { showBudgetDialog = true },
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              Icons.Default.Edit,
              contentDescription = "Edit Budget",
              tint = BreezySky,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      items(budgets) { budget ->
        val spent = transactions
          .filter { it.type == "EXPENSE" && it.category.equals(budget.category, ignoreCase = true) }
          .sumOf { it.amount }

        val ratio = (spent / budget.monthlyLimitNrs).coerceIn(0.0, 1.5).toFloat()
        val percent = (ratio * 100).toInt()

        val guardrailColor = when {
          percent >= 100 -> BreezySunset
          percent >= 70 -> BreezyAmber
          else -> BreezyMint
        }

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp), spotColor = Color(0x0A000000))
            .clip(RoundedCornerShape(16.dp))
            .background(BreezySurface)
            .border(1.dp, guardrailColor.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
            .padding(14.dp)
        ) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = budget.category,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
              )

              Row(verticalAlignment = Alignment.CenterVertically) {
                if (percent >= 100) {
                  Icon(
                    Icons.Default.Warning,
                    contentDescription = "Breached",
                    tint = BreezySunset,
                    modifier = Modifier.size(14.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                  text = "रू ${spent.toInt()} / रू ${budget.monthlyLimitNrs.toInt()} ($percent%)",
                  color = guardrailColor,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
              progress = { ratio.coerceAtMost(1f) },
              modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
              color = guardrailColor,
              trackColor = Color(0xFFE2E8F0),
              strokeCap = StrokeCap.Round
            )

            if (percent >= 100) {
              Text(
                text = "⚠️ Guardrail Breached! Savage Coach alert triggered.",
                color = BreezySunset,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 4.dp)
              )
            }
          }
        }
      }

      // 5. Transaction Ledger Section
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Spending & Transaction Ledger",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )

          // Filter chips
          Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf("ALL", "EXPENSE", "INCOME").forEach { type ->
              val isSel = filterType == type
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(12.dp))
                  .background(if (isSel) BreezyMint else BreezySurfaceVariant)
                  .clickable { filterType = type }
                  .padding(horizontal = 8.dp, vertical = 4.dp)
                  .testTag("filter_tx_$type")
              ) {
                Text(
                  text = when (type) {
                    "EXPENSE" -> "Outflow"
                    "INCOME" -> "Inflow"
                    else -> "All"
                  },
                  color = if (isSel) Color.White else TextSecondary,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }

      if (filteredTransactions.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "No entries logged yet. Tap '+ Log Spend / Inflow' below.",
              color = TextMuted,
              fontSize = 13.sp
            )
          }
        }
      } else {
        items(filteredTransactions, key = { it.id }) { tx ->
          TransactionCard(
            transaction = tx,
            onDelete = { onDeleteTransaction(tx) }
          )
        }
      }
    }
  }

  // Dialogs
  if (showAddDialog) {
    AddTransactionDialog(
      availableWallets = if (wallets.isNotEmpty()) wallets else listOf(
        WalletEntity("CASH", "Physical Cash", 12500.0, 0xFF10B981L, "Payments"),
        WalletEntity("BANK", "Bank (NIC/Nabil)", 85400.0, 0xFF0284C7L, "AccountBalance"),
        WalletEntity("ESEWA", "eSewa Wallet", 9800.0, 0xFF059669L, "QrCode"),
        WalletEntity("KHALTI", "Khalti Wallet", 4350.0, 0xFF8B5CF6L, "AccountBalanceWallet")
      ),
      onDismiss = { showAddDialog = false },
      onConfirm = { title, amt, type, cat, wallet, whereSpent, howSpent, notes ->
        onAddTransaction(title, amt, type, cat, wallet, whereSpent, howSpent, notes)
        showAddDialog = false
      }
    )
  }

  if (walletToEdit != null) {
    EditWalletBalanceDialog(
      wallet = walletToEdit!!,
      onDismiss = { walletToEdit = null },
      onConfirm = { newBal ->
        onUpdateWalletBalance(walletToEdit!!.id, newBal)
        walletToEdit = null
      }
    )
  }

  if (showAddWalletDialog) {
    AddWalletDialog(
      onDismiss = { showAddWalletDialog = false },
      onConfirm = { name, initialBal, colorHex, iconName ->
        onAddWallet(name, initialBal, colorHex, iconName)
        showAddWalletDialog = false
      }
    )
  }

  if (showBudgetDialog) {
    SetBudgetDialog(
      onDismiss = { showBudgetDialog = false },
      onConfirm = { cat, limit ->
        onSetBudget(cat, limit)
        showBudgetDialog = false
      }
    )
  }
}

@Composable
fun WalletDetailCard(
  wallet: WalletEntity,
  onEditBalance: () -> Unit,
  modifier: Modifier = Modifier
) {
  val iconVector: ImageVector = when (wallet.iconName) {
    "AccountBalance" -> Icons.Default.AccountBalance
    "QrCode" -> Icons.Default.QrCode
    "AccountBalanceWallet" -> Icons.Default.AccountBalanceWallet
    "Savings" -> Icons.Default.Savings
    else -> Icons.Default.Payments
  }

  val accentColor = when (wallet.id) {
    "CASH" -> CashGreen
    "BANK" -> BankBlue
    "ESEWA" -> EsewaGreen
    "KHALTI" -> KhaltiPurple
    else -> Color(wallet.colorHex)
  }

  Box(
    modifier = modifier
      .shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp), spotColor = Color(0x0A000000))
      .clip(RoundedCornerShape(16.dp))
      .background(BreezySurface)
      .border(1.dp, BreezyBorder, RoundedCornerShape(16.dp))
      .clickable { onEditBalance() }
      .padding(12.dp)
      .testTag("wallet_card_${wallet.id}")
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
              .size(26.dp)
              .clip(CircleShape)
              .background(accentColor.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(iconVector, contentDescription = null, tint = accentColor, modifier = Modifier.size(15.dp))
          }
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = wallet.name,
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
          )
        }

        Box(
          modifier = Modifier
            .clip(CircleShape)
            .background(BreezySurfaceVariant)
            .padding(4.dp)
        ) {
          Icon(Icons.Default.Edit, contentDescription = "Edit Balance", tint = TextSecondary, modifier = Modifier.size(11.dp))
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "रू ${String.format("%,.2f", wallet.balance)}",
        color = TextPrimary,
        fontSize = 15.sp,
        fontWeight = FontWeight.Black
      )

      Spacer(modifier = Modifier.height(2.dp))

      Text(
        text = "Tap to enter balance",
        color = BreezySky,
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium
      )
    }
  }
}

@Composable
fun TransactionCard(
  transaction: TransactionEntity,
  onDelete: () -> Unit
) {
  val isExpense = transaction.type == "EXPENSE"

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp), spotColor = Color(0x0A000000))
      .clip(RoundedCornerShape(16.dp))
      .background(BreezySurface)
      .border(1.dp, BreezyBorder, RoundedCornerShape(16.dp))
      .padding(14.dp)
      .testTag("tx_card_${transaction.id}")
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(if (isExpense) BreezySunsetLight else BreezyMintLight),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = if (isExpense) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
          contentDescription = null,
          tint = if (isExpense) BreezySunset else BreezyMint,
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = transaction.title,
          color = TextPrimary,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(3.dp))

        // Where it was spent (if present)
        if (transaction.whereSpent.isNotBlank()) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 2.dp)
          ) {
            Icon(Icons.Default.Place, contentDescription = null, tint = BreezySunset, modifier = Modifier.size(11.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = transaction.whereSpent,
              color = Color(0xFFC2410C),
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }

        Row(
          horizontalArrangement = Arrangement.spacedBy(5.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (transaction.howSpent.isNotBlank()) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(BreezySkyLight)
                .padding(horizontal = 5.dp, vertical = 1.dp)
            ) {
              Text(text = transaction.howSpent, color = BreezySky, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
          }

          Text(text = transaction.category, color = TextSecondary, fontSize = 11.sp)
          Text(text = "•", color = Color(0xFFCBD5E1), fontSize = 11.sp)
          Text(text = transaction.wallet, color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
          Text(text = "•", color = Color(0xFFCBD5E1), fontSize = 11.sp)
          Text(text = transaction.dateString, color = TextMuted, fontSize = 10.sp)
        }
      }

      Spacer(modifier = Modifier.width(6.dp))

      Column(horizontalAlignment = Alignment.End) {
        Text(
          text = "${if (isExpense) "- रू" else "+ रू"} ${transaction.amount.toInt()}",
          color = if (isExpense) BreezySunset else Color(0xFF047857),
          fontSize = 14.sp,
          fontWeight = FontWeight.Black
        )

        IconButton(
          onClick = onDelete,
          modifier = Modifier.size(26.dp)
        ) {
          Icon(
            Icons.Default.Delete,
            contentDescription = "Delete",
            tint = Color(0xFF94A3B8),
            modifier = Modifier.size(13.dp)
          )
        }
      }
    }
  }
}

@Composable
fun EditWalletBalanceDialog(
  wallet: WalletEntity,
  onDismiss: () -> Unit,
  onConfirm: (Double) -> Unit
) {
  var balanceText by remember { mutableStateOf(wallet.balance.toInt().toString()) }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = BreezySurface,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = BreezyMint)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Enter Money in ${wallet.name}",
          color = TextPrimary,
          fontSize = 17.sp,
          fontWeight = FontWeight.Black
        )
      }
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
          text = "Specify how much money you currently have in this account / location.",
          color = TextSecondary,
          fontSize = 12.sp
        )

        OutlinedTextField(
          value = balanceText,
          onValueChange = { balanceText = it },
          label = { Text("Current Balance in NRs (रू)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BreezyMint,
            unfocusedBorderColor = BreezyBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedLabelColor = BreezyMint
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("wallet_balance_input")
        )

        // Quick adjustment chips
        Text(text = "Quick Presets", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          listOf("1000", "5000", "10000", "25000", "50000").forEach { p ->
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(BreezySkyLight)
                .clickable { balanceText = p }
                .padding(horizontal = 7.dp, vertical = 4.dp)
            ) {
              Text("रू $p", color = BreezySky, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val amt = balanceText.toDoubleOrNull() ?: 0.0
          if (amt >= 0) {
            onConfirm(amt)
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = BreezyMint, contentColor = Color.White),
        modifier = Modifier.testTag("save_wallet_balance_btn")
      ) {
        Text("Save Balance", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = TextSecondary)
      }
    }
  )
}

@Composable
fun AddWalletDialog(
  onDismiss: () -> Unit,
  onConfirm: (name: String, balance: Double, colorHex: Long, iconName: String) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var balanceText by remember { mutableStateOf("0") }
  var selectedIcon by remember { mutableStateOf("AccountBalance") }
  var selectedColor by remember { mutableLongStateOf(0xFF0284C7L) }

  val presetNames = listOf("Siddhartha Bank", "Locker Cash", "ConnectIPS", "Global IME", "Savings Pot")

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = BreezySurface,
    title = {
      Text("🏦 Add Money Location / Account", color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Black)
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Account Name (e.g. Nabil Bank, Cash Box)") },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BreezySky,
            unfocusedBorderColor = BreezyBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          ),
          modifier = Modifier.fillMaxWidth().testTag("new_account_name_input")
        )

        // Suggestion chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          items(presetNames) { p ->
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(BreezySurfaceVariant)
                .clickable { name = p }
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(p, color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
          }
        }

        OutlinedTextField(
          value = balanceText,
          onValueChange = { balanceText = it },
          label = { Text("Initial Money in NRs (रू)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BreezySky,
            unfocusedBorderColor = BreezyBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          ),
          modifier = Modifier.fillMaxWidth().testTag("new_account_balance_input")
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val amt = balanceText.toDoubleOrNull() ?: 0.0
          if (name.isNotBlank()) {
            onConfirm(name.trim(), amt, selectedColor, selectedIcon)
          }
        },
        enabled = name.isNotBlank(),
        colors = ButtonDefaults.buttonColors(containerColor = BreezySky, contentColor = Color.White),
        modifier = Modifier.testTag("confirm_new_account_btn")
      ) {
        Text("Add Account", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = TextSecondary)
      }
    }
  )
}

@Composable
fun AddTransactionDialog(
  availableWallets: List<WalletEntity>,
  onDismiss: () -> Unit,
  onConfirm: (
    title: String,
    amount: Double,
    type: String,
    category: String,
    wallet: String,
    whereSpent: String,
    howSpent: String,
    notes: String
  ) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var amountText by remember { mutableStateOf("") }
  var selectedType by remember { mutableStateOf("EXPENSE") }
  var selectedCategory by remember { mutableStateOf("Momo & Khaja") }
  var selectedWallet by remember { mutableStateOf(availableWallets.firstOrNull()?.id ?: "CASH") }
  var whereSpent by remember { mutableStateOf("") }
  var howSpent by remember { mutableStateOf("eSewa QR") }
  var notes by remember { mutableStateOf("") }

  val categories = listOf(
    "Momo & Khaja",
    "Chiya & Coffee",
    "Rent (Bhado)",
    "Petrol & Pathao",
    "Groceries",
    "Salary & Freelance",
    "Shopping",
    "Tech & Bills"
  )

  val wherePresets = listOf(
    "Bhatbhateni",
    "Everest Momo",
    "Local Chiya Pasal",
    "Pathao Nepal",
    "Ason Bazzar",
    "Daraz Online",
    "Pharmacy",
    "Kathmandu Mart"
  )

  val howPresets = listOf(
    "eSewa QR",
    "Cash Note",
    "Fonepay / Bank QR",
    "Khalti Pay",
    "ATM Card",
    "Bank Transfer"
  )

  val presetAmounts = listOf("100", "250", "500", "1200", "5000")

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = BreezySurface,
    title = {
      Text(
        text = if (selectedType == "EXPENSE") "💸 Log Spending (Where & How)" else "💰 Log Money Inflow",
        color = TextPrimary,
        fontSize = 17.sp,
        fontWeight = FontWeight.Black
      )
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(9.dp)
      ) {
        // Type Switch: Expense vs Income
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .background(if (selectedType == "EXPENSE") BreezySunset else BreezySurfaceVariant)
              .clickable { selectedType = "EXPENSE" }
              .padding(vertical = 7.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              "Expense (खर्च)",
              color = if (selectedType == "EXPENSE") Color.White else TextSecondary,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
          }

          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .background(if (selectedType == "INCOME") BreezyMint else BreezySurfaceVariant)
              .clickable { selectedType = "INCOME" }
              .padding(vertical = 7.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              "Income (आम्दानी)",
              color = if (selectedType == "INCOME") Color.White else TextSecondary,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
          }
        }

        // Amount
        OutlinedTextField(
          value = amountText,
          onValueChange = { amountText = it },
          label = { Text("Amount in NRs (रू)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BreezyMint,
            unfocusedBorderColor = BreezyBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedLabelColor = BreezyMint
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("tx_amount_input")
        )

        // Quick Amount Presets
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
          presetAmounts.forEach { p ->
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(BreezySkyLight)
                .clickable { amountText = p }
                .padding(horizontal = 7.dp, vertical = 3.dp)
            ) {
              Text("रू $p", color = BreezySky, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
          }
        }

        // WHERE did you spend it?
        if (selectedType == "EXPENSE") {
          OutlinedTextField(
            value = whereSpent,
            onValueChange = { whereSpent = it },
            label = { Text("Where did you spend it? (स्थान / पसल)") },
            placeholder = { Text("e.g. Bhatbhateni, Everest Momo") },
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = BreezySunset,
              unfocusedBorderColor = BreezyBorder,
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary,
              focusedLabelColor = BreezySunset
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("tx_where_input")
          )

          // Where quick suggestions
          LazyRow(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            items(wherePresets) { w ->
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(if (whereSpent == w) BreezySunsetLight else BreezySurfaceVariant)
                  .border(
                    1.dp,
                    if (whereSpent == w) BreezySunset else Color.Transparent,
                    RoundedCornerShape(6.dp)
                  )
                  .clickable { whereSpent = w }
                  .padding(horizontal = 7.dp, vertical = 3.dp)
              ) {
                Text(
                  text = w,
                  color = if (whereSpent == w) BreezySunset else TextSecondary,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Medium
                )
              }
            }
          }

          // HOW did you spend it? (Payment Mode)
          Text("How did you pay? (कसरी तिरेँ)", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(howPresets) { m ->
              val isSel = howSpent == m
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isSel) BreezySky else BreezySurfaceVariant)
                  .clickable { howSpent = m }
                  .padding(horizontal = 9.dp, vertical = 5.dp)
              ) {
                Text(
                  text = m,
                  color = if (isSel) Color.White else TextSecondary,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }

        // Title / Description
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Description / What was bought") },
          placeholder = { Text("e.g. Buff Steam Momo, Groceries") },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BreezyMint,
            unfocusedBorderColor = BreezyBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedLabelColor = BreezyMint
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("tx_title_input")
        )

        // Account / Wallet Selection (Shows current available balance)
        Text("From Which Account / Wallet?", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          items(availableWallets) { w ->
            val isSel = w.id == selectedWallet
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSel) BreezyMint else BreezySurfaceVariant)
                .clickable { selectedWallet = w.id }
                .padding(horizontal = 9.dp, vertical = 5.dp)
                .testTag("select_wallet_${w.id}")
            ) {
              Text(
                text = "${w.name} (रू ${w.balance.toInt()})",
                color = if (isSel) Color.White else TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        // Category
        Text("Category", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          items(categories) { cat ->
            val isSel = cat == selectedCategory
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSel) BreezySky else BreezySurfaceVariant)
                .clickable { selectedCategory = cat }
                .padding(horizontal = 9.dp, vertical = 5.dp)
            ) {
              Text(
                text = cat,
                color = if (isSel) Color.White else TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val amt = amountText.toDoubleOrNull() ?: 0.0
          val desc = if (title.isNotBlank()) title.trim() else if (whereSpent.isNotBlank()) whereSpent else selectedCategory
          if (amt > 0) {
            onConfirm(desc, amt, selectedType, selectedCategory, selectedWallet, whereSpent, howSpent, notes)
          }
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = BreezyMint,
          contentColor = Color.White
        ),
        enabled = (amountText.toDoubleOrNull() ?: 0.0) > 0,
        modifier = Modifier.testTag("submit_tx_btn")
      ) {
        Text("Save Entry", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = TextSecondary)
      }
    }
  )
}

@Composable
fun SetBudgetDialog(
  onDismiss: () -> Unit,
  onConfirm: (category: String, limit: Double) -> Unit
) {
  var selectedCategory by remember { mutableStateOf("Momo & Khaja") }
  var limitText by remember { mutableStateOf("5000") }

  val categories = listOf("Momo & Khaja", "Petrol & Pathao", "Rent (Bhado)", "Groceries", "Chiya & Coffee")

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = BreezySurface,
    title = {
      Text(
        text = "🛡️ Set Budget Guardrail Cap",
        color = TextPrimary,
        fontSize = 18.sp,
        fontWeight = FontWeight.Black
      )
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Category", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          items(categories) { cat ->
            val isSel = cat == selectedCategory
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSel) BreezySky else BreezySurfaceVariant)
                .clickable { selectedCategory = cat }
                .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
              Text(
                text = cat,
                color = if (isSel) Color.White else TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        OutlinedTextField(
          value = limitText,
          onValueChange = { limitText = it },
          label = { Text("Monthly Limit in NRs (रू)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BreezySky,
            unfocusedBorderColor = BreezyBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedLabelColor = BreezySky
          ),
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val limit = limitText.toDoubleOrNull() ?: 0.0
          if (limit > 0) {
            onConfirm(selectedCategory, limit)
          }
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = BreezySky,
          contentColor = Color.White
        )
      ) {
        Text("Update Guardrail", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = TextSecondary)
      }
    }
  )
}
