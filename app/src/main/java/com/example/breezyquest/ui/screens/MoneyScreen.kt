package com.example.breezyquest.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.breezyquest.data.model.RecurringTransactionEntity
import com.example.breezyquest.data.model.TransactionEntity
import com.example.breezyquest.data.model.TransactionType
import com.example.breezyquest.ui.components.AddBudgetDialog
import com.example.breezyquest.ui.components.AddRecurringDialog
import com.example.breezyquest.ui.components.AddTransactionDialog
import com.example.breezyquest.ui.components.EditBalanceDialog
import com.example.breezyquest.ui.viewmodel.BudgetAlertLevel
import com.example.breezyquest.ui.viewmodel.BudgetWithUsage
import com.example.breezyquest.ui.viewmodel.MainViewModel
import com.example.ui.theme.BreezeCyan
import com.example.ui.theme.CardSurface
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.EmeraldIncome
import com.example.ui.theme.QuestGold
import com.example.ui.theme.RoseExpense
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun MoneyScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val metrics by viewModel.financialMetrics.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val budgetsWithUsage by viewModel.budgetsWithUsage.collectAsState()
    val recurringList by viewModel.recurring.collectAsState()

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("Transactions", "Category Budgets", "Recurring")

    var showAddDialog by remember { mutableStateOf<TransactionType?>(null) }
    var showAddBudgetDialog by remember { mutableStateOf(false) }
    var showAddRecurringDialog by remember { mutableStateOf(false) }
    var showEditBalanceDialog by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    var filterType by remember { mutableStateOf<TransactionType?>(null) }

    if (showAddDialog != null) {
        AddTransactionDialog(
            initialType = showAddDialog!!,
            currency = metrics.currency,
            onDismiss = { showAddDialog = null },
            onConfirm = { title, amount, type, category, note ->
                viewModel.addTransaction(title, amount, type, category, note = note)
            }
        )
    }

    if (showAddBudgetDialog) {
        AddBudgetDialog(
            existingCategories = budgetsWithUsage.map { it.budget.category },
            currency = metrics.currency,
            onDismiss = { showAddBudgetDialog = false },
            onConfirm = { cat, limit ->
                viewModel.addBudget(cat, limit)
            }
        )
    }

    if (showAddRecurringDialog) {
        AddRecurringDialog(
            currency = metrics.currency,
            onDismiss = { showAddRecurringDialog = false },
            onConfirm = { title, amt, isInc, cat, freq, day, note ->
                viewModel.addRecurring(title, amt, isInc, cat, freq, day, note)
            }
        )
    }

    if (showEditBalanceDialog) {
        EditBalanceDialog(
            currentBalance = metrics.currentBalance,
            currency = metrics.currency,
            onDismiss = { showEditBalanceDialog = false },
            onConfirm = { viewModel.updateStartingBalance(it) }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepObsidian)
    ) {
        // Top Balance Summary Bar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        Text(
                            text = "Current Balance",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${metrics.currency} ${String.format("%,.0f", metrics.currentBalance)}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    IconButton(
                        onClick = { showEditBalanceDialog = true },
                        modifier = Modifier.size(36.dp).background(CardSurface, CircleShape)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Balance", tint = BreezeCyan, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { showAddDialog = TransactionType.INCOME },
                        modifier = Modifier.weight(1f).height(44.dp).testTag("money_add_income_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldIncome),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Income", fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                    Button(
                        onClick = { showAddDialog = TransactionType.EXPENSE },
                        modifier = Modifier.weight(1f).height(44.dp).testTag("money_add_expense_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = RoseExpense),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Expense", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        // Tabs
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = DarkSurface,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = BreezeCyan
                )
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            title,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == index) BreezeCyan else TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> {
                // Transactions tab
                val filteredTransactions = transactions.filter { tx ->
                    (filterType == null || tx.type == filterType) &&
                        (searchQuery.isBlank() || tx.title.contains(searchQuery, ignoreCase = true) || tx.category.contains(searchQuery, ignoreCase = true))
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        // Search & Filter Bar
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search by title or category...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("transaction_search_input"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = BreezeCyan
                            )
                        )
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = filterType == null,
                                onClick = { filterType = null },
                                label = { Text("All (${transactions.size})") }
                            )
                            FilterChip(
                                selected = filterType == TransactionType.INCOME,
                                onClick = { filterType = TransactionType.INCOME },
                                label = { Text("Income") },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = EmeraldIncome, selectedLabelColor = Color.Black)
                            )
                            FilterChip(
                                selected = filterType == TransactionType.EXPENSE,
                                onClick = { filterType = TransactionType.EXPENSE },
                                label = { Text("Expenses") },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = RoseExpense, selectedLabelColor = Color.White)
                            )
                        }
                    }

                    if (filteredTransactions.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                                Text("No transactions found", color = TextMuted)
                            }
                        }
                    } else {
                        items(filteredTransactions, key = { it.id }) { tx ->
                            TransactionItemCard(
                                tx = tx,
                                currency = metrics.currency,
                                onDelete = { viewModel.deleteTransaction(tx) }
                            )
                        }
                    }
                    item { Spacer(modifier = Modifier.height(40.dp)) }
                }
            }
            1 -> {
                // Budgets tab
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                Text("Category Budgets", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text("Remaining this month: ${metrics.currency} ${metrics.totalRemainingBudget.toInt()}", fontSize = 12.sp, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            Button(
                                onClick = { showAddBudgetDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BreezeCyan),
                                modifier = Modifier.testTag("set_budget_button")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Set Budget", fontWeight = FontWeight.Bold, color = Color.Black, maxLines = 1)
                            }
                        }
                    }

                    items(budgetsWithUsage) { item ->
                        BudgetItemCard(
                            item = item,
                            currency = metrics.currency,
                            onDelete = { viewModel.deleteBudget(item.budget) }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(40.dp)) }
                }
            }
            2 -> {
                // Recurring tab
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                Text("Scheduled Transactions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text("Automate your monthly bills & salaries", fontSize = 12.sp, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            Button(
                                onClick = { showAddRecurringDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BreezeCyan)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add", fontWeight = FontWeight.Bold, color = Color.Black, maxLines = 1)
                            }
                        }
                    }

                    if (recurringList.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                                Text("No recurring transactions yet", color = TextMuted)
                            }
                        }
                    } else {
                        items(recurringList) { rec ->
                            RecurringItemCard(
                                item = rec,
                                currency = metrics.currency,
                                onDelete = { viewModel.deleteRecurring(rec) }
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(40.dp)) }
                }
            }
        }
    }
}

@Composable
fun TransactionItemCard(
    tx: TransactionEntity,
    currency: String,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            if (tx.type == TransactionType.INCOME) EmeraldIncome.copy(alpha = 0.2f) else RoseExpense.copy(alpha = 0.2f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (tx.type == TransactionType.INCOME) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                        contentDescription = null,
                        tint = if (tx.type == TransactionType.INCOME) EmeraldIncome else RoseExpense,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        tx.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        "${tx.category} • ${tx.date} ${tx.time}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (tx.note.isNotBlank()) {
                        Text(
                            tx.note,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            fontSize = 10.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${if (tx.type == TransactionType.INCOME) "+" else "-"} $currency ${String.format("%,.0f", tx.amount)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (tx.type == TransactionType.INCOME) EmeraldIncome else RoseExpense,
                    maxLines = 1
                )
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp).padding(start = 4.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun BudgetItemCard(
    item: BudgetWithUsage,
    currency: String,
    onDelete: () -> Unit
) {
    val b = item.budget
    val progressRatio = (item.percentageUsed / 100f).coerceIn(0f, 1f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(
                        b.category,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        "Budget: $currency ${b.monthlyLimit.toInt()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (item.alertStatus != BudgetAlertLevel.NORMAL) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when (item.alertStatus) {
                                BudgetAlertLevel.EXCEEDED -> RoseExpense.copy(alpha = 0.2f)
                                BudgetAlertLevel.WARNING_90 -> RoseExpense.copy(alpha = 0.2f)
                                BudgetAlertLevel.WARNING_75 -> QuestGold.copy(alpha = 0.2f)
                                else -> Color.Transparent
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(12.dp), tint = if (item.alertStatus == BudgetAlertLevel.WARNING_75) QuestGold else RoseExpense)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = when (item.alertStatus) {
                                        BudgetAlertLevel.EXCEEDED -> "Over Budget!"
                                        BudgetAlertLevel.WARNING_90 -> "90% Used"
                                        BudgetAlertLevel.WARNING_75 -> "75% Used"
                                        else -> ""
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (item.alertStatus == BudgetAlertLevel.WARNING_75) QuestGold else RoseExpense,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Budget", tint = TextMuted, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { progressRatio },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = when {
                    item.percentageUsed > 100f -> RoseExpense
                    item.percentageUsed >= 90f -> RoseExpense
                    item.percentageUsed >= 75f -> QuestGold
                    else -> BreezeCyan
                },
                trackColor = CardSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Spent: $currency ${item.spent.toInt()} (${item.percentageUsed.toInt()}%)",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.weight(1f, fill = false),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "Remaining: $currency ${item.remaining.toInt()}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (item.remaining <= 0) RoseExpense else EmeraldIncome,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.End,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun RecurringItemCard(
    item: RecurringTransactionEntity,
    currency: String,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text(
                    item.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "${item.frequency} on day ${item.dayOfMonth} • ${item.category}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${if (item.isIncome) "+" else "-"} $currency ${item.amount.toInt()}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (item.isIncome) EmeraldIncome else RoseExpense,
                    maxLines = 1
                )
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
