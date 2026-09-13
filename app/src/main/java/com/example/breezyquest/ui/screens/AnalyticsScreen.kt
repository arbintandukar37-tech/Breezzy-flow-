package com.example.breezyquest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.breezyquest.data.model.TransactionType
import com.example.breezyquest.ui.components.CategoryChartColors
import com.example.breezyquest.ui.components.DonutSlice
import com.example.breezyquest.ui.components.IncomeExpenseBarChart
import com.example.breezyquest.ui.components.SpendingDonutChart
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

enum class AnalyticsTimeframe {
    DAILY, WEEKLY, MONTHLY, YEARLY
}

@Composable
fun AnalyticsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val transactions by viewModel.transactions.collectAsState()
    val metrics by viewModel.financialMetrics.collectAsState()

    var timeframe by remember { mutableStateOf(AnalyticsTimeframe.MONTHLY) }

    // Filter transactions by timeframe
    val todayDate = viewModel.getTodayDate()
    val filteredTx = remember(transactions, timeframe) {
        when (timeframe) {
            AnalyticsTimeframe.DAILY -> transactions.filter { it.date == todayDate }
            AnalyticsTimeframe.WEEKLY -> transactions.take(20) // recent
            AnalyticsTimeframe.MONTHLY -> transactions.filter { it.date.startsWith(todayDate.take(7)) }
            AnalyticsTimeframe.YEARLY -> transactions.filter { it.date.startsWith(todayDate.take(4)) }
        }
    }

    val totalIncome = filteredTx.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    val totalExpense = filteredTx.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    val netSavings = (totalIncome - totalExpense).coerceAtLeast(0.0)

    // Category breakdown for expenses
    val expenseCategories = filteredTx.filter { it.type == TransactionType.EXPENSE }
        .groupBy { it.category }
        .mapValues { entry -> entry.value.sumOf { it.amount } }
        .toList()
        .sortedByDescending { it.second }

    val donutSlices = remember(expenseCategories, totalExpense) {
        expenseCategories.mapIndexed { index, (cat, amount) ->
            val pct = if (totalExpense > 0) ((amount / totalExpense) * 100).toFloat() else 0f
            DonutSlice(
                label = cat,
                amount = amount,
                percentage = pct,
                color = CategoryChartColors[index % CategoryChartColors.size],
                emoji = when (cat.lowercase()) {
                    "food" -> "🍔"
                    "transport" -> "🚕"
                    "education" -> "📚"
                    "bills" -> "💡"
                    "shopping" -> "🛍️"
                    "entertainment" -> "🎬"
                    "health" -> "💊"
                    else -> "📦"
                }
            )
        }
    }

    val topCategory = expenseCategories.firstOrNull()?.first ?: "None"
    val savingsRatePct = if (totalIncome > 0) ((netSavings / totalIncome) * 100).toInt() else 0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DeepObsidian),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("analytics_overview_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("📊", fontSize = 22.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Financial Analytics", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                            }
                            Text("Deep dive into your cashflow & spending patterns", fontSize = 12.sp, color = TextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Timeframe Chips
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AnalyticsTimeframe.values().forEach { tf ->
                            FilterChip(
                                selected = timeframe == tf,
                                onClick = { timeframe = tf },
                                label = { Text(tf.name.lowercase().capitalize(), fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BreezeCyan,
                                    selectedLabelColor = Color.Black
                                )
                            )
                        }
                    }
                }
            }
        }

        // Cashflow Bar Comparison
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Cashflow Comparison", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("Income vs Expenses vs Savings", fontSize = 12.sp, color = TextSecondary)

                    Spacer(modifier = Modifier.height(16.dp))

                    IncomeExpenseBarChart(
                        income = totalIncome,
                        expense = totalExpense,
                        savings = netSavings,
                        currency = metrics.currency
                    )
                }
            }
        }

        // Donut Chart Card (Spending by category)
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("category_donut_chart_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Spending by Category", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("Category distribution and percentage breakdown", fontSize = 12.sp, color = TextSecondary)

                    Spacer(modifier = Modifier.height(16.dp))

                    SpendingDonutChart(
                        slices = donutSlices,
                        totalAmount = totalExpense,
                        currency = metrics.currency
                    )
                }
            }
        }

        // 4 Key Insight Metrics
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp), color = CardSurface) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Top Expense", fontSize = 11.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(topCategory, fontWeight = FontWeight.Bold, color = RoseExpense, fontSize = 14.sp)
                    }
                }
                Surface(modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp), color = CardSurface) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Savings Rate", fontSize = 11.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("$savingsRatePct%", fontWeight = FontWeight.Bold, color = EmeraldIncome, fontSize = 14.sp)
                    }
                }
            }
        }

        // Detailed Category Table
        item {
            Text("Detailed Breakdown", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
        }

        if (expenseCategories.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                    Text("No expense entries in this period", color = TextMuted)
                }
            }
        } else {
            items(donutSlices) { slice ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(slice.color, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("${slice.emoji} ${slice.label}", fontWeight = FontWeight.SemiBold, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "${metrics.currency} ${slice.amount.toInt()}",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text("${slice.percentage.toInt()}% of expenses", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(40.dp)) }
    }
}
