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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.HabitEntity
import com.example.ui.theme.BreezyAmber
import com.example.ui.theme.BreezyBg
import com.example.ui.theme.BreezyBorder
import com.example.ui.theme.BreezyMint
import com.example.ui.theme.BreezySky
import com.example.ui.theme.BreezySunset
import com.example.ui.theme.BreezySurface
import com.example.ui.theme.BreezySurfaceVariant
import com.example.ui.theme.BreezyViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HabitsScreen(
  habits: List<HabitEntity>,
  onToggleHabit: (HabitEntity) -> Unit,
  onAddHabit: (String, String, Int, Long, String) -> Unit,
  onDeleteHabit: (HabitEntity) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedCategory by remember { mutableStateOf("ALL") }
  var showAddDialog by remember { mutableStateOf(false) }

  val categories = listOf("ALL", "MORNING", "FITNESS", "MINDFULNESS", "TECH", "WELLNESS")

  val filteredHabits = if (selectedCategory == "ALL") {
    habits
  } else {
    habits.filter { it.category.equals(selectedCategory, ignoreCase = true) }
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = BreezyBg,
    floatingActionButton = {
      FloatingActionButton(
        onClick = { showAddDialog = true },
        containerColor = BreezySky,
        contentColor = Color.White,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
          .padding(bottom = 70.dp)
          .testTag("add_habit_fab")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Add, contentDescription = "Add Habit")
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "New Habit",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
        }
      }
    }
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
    ) {
      // Category Filter Chips
      LazyRow(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(categories) { cat ->
          val isSelected = cat == selectedCategory
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .background(if (isSelected) BreezySky else BreezySurface)
              .border(
                1.dp,
                if (isSelected) BreezySky else BreezyBorder,
                RoundedCornerShape(20.dp)
              )
              .clickable { selectedCategory = cat }
              .padding(horizontal = 14.dp, vertical = 7.dp)
              .testTag("filter_cat_$cat")
          ) {
            Text(
              text = cat,
              color = if (isSelected) Color.White else TextSecondary,
              fontSize = 12.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
          }
        }
      }

      // Habits List
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(top = 4.dp, bottom = 120.dp)
      ) {
        items(filteredHabits, key = { it.id }) { habit ->
          HabitDetailCard(
            habit = habit,
            onToggle = { onToggleHabit(habit) },
            onDelete = { onDeleteHabit(habit) }
          )
        }
      }
    }
  }

  if (showAddDialog) {
    AddHabitDialog(
      onDismiss = { showAddDialog = false },
      onConfirm = { name, cat, targetDays, color, icon ->
        onAddHabit(name, cat, targetDays, color, icon)
        showAddDialog = false
      }
    )
  }
}

@Composable
fun HabitDetailCard(
  habit: HabitEntity,
  onToggle: () -> Unit,
  onDelete: () -> Unit
) {
  val iconVector = when (habit.iconName) {
    "SelfImprovement", "meditation" -> Icons.Default.SelfImprovement
    "FitnessCenter", "fitness", "run" -> Icons.Default.FitnessCenter
    "MenuBook", "book" -> Icons.Default.MenuBook
    "LocalDrink", "water", "tea" -> Icons.Default.LocalDrink
    "Terminal", "code" -> Icons.Default.Terminal
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
      .shadow(elevation = 2.dp, shape = RoundedCornerShape(18.dp), spotColor = Color(0x0A000000))
      .clip(RoundedCornerShape(18.dp))
      .background(BreezySurface)
      .border(
        1.dp,
        if (habit.isCompletedToday) BreezyMint.copy(alpha = 0.5f) else BreezyBorder,
        RoundedCornerShape(18.dp)
      )
      .padding(16.dp)
      .testTag("habit_card_${habit.id}")
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Category Icon
      Box(
        modifier = Modifier
          .size(44.dp)
          .clip(RoundedCornerShape(14.dp))
          .background(accentColor.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = iconVector,
          contentDescription = habit.name,
          tint = accentColor,
          modifier = Modifier.size(24.dp)
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = habit.name,
          color = if (habit.isCompletedToday) TextMuted else TextPrimary,
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(3.dp))

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(Color(0xFFFEF3C7))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.LocalFireDepartment,
                contentDescription = null,
                tint = Color(0xFFD97706),
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(2.dp))
              Text(
                text = "${habit.streakCount}d",
                color = Color(0xFFB45309),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black
              )
            }
          }

          Text(
            text = "Best: ${habit.bestStreak}d",
            color = TextSecondary,
            fontSize = 11.sp
          )

          Text(text = "•", color = Color(0xFFCBD5E1), fontSize = 11.sp)

          Text(
            text = "${habit.targetDaysPerWeek}x/wk",
            color = BreezySky,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      // Checkbox Button
      Box(
        modifier = Modifier
          .size(38.dp)
          .clip(CircleShape)
          .background(if (habit.isCompletedToday) BreezyMint else Color(0xFFF1F5F9))
          .border(
            2.dp,
            if (habit.isCompletedToday) BreezyMint else Color(0xFFCBD5E1),
            CircleShape
          )
          .clickable { onToggle() }
          .testTag("toggle_habit_${habit.id}"),
        contentAlignment = Alignment.Center
      ) {
        if (habit.isCompletedToday) {
          Icon(
            Icons.Default.Check,
            contentDescription = "Completed",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      Spacer(modifier = Modifier.width(4.dp))

      IconButton(
        onClick = onDelete,
        modifier = Modifier.size(28.dp)
      ) {
        Icon(
          Icons.Default.Delete,
          contentDescription = "Delete",
          tint = Color(0xFF94A3B8),
          modifier = Modifier.size(16.dp)
        )
      }
    }
  }
}

@Composable
fun AddHabitDialog(
  onDismiss: () -> Unit,
  onConfirm: (name: String, category: String, targetDaysPerWeek: Int, colorHex: Long, iconName: String) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("MORNING") }
  var targetDays by remember { mutableIntStateOf(7) }
  var selectedIcon by remember { mutableStateOf("SelfImprovement") }
  var selectedColor by remember { mutableLongStateOf(0xFF0284C7L) }

  val iconOptions = listOf(
    "SelfImprovement" to Icons.Default.SelfImprovement,
    "FitnessCenter" to Icons.Default.FitnessCenter,
    "MenuBook" to Icons.Default.MenuBook,
    "LocalDrink" to Icons.Default.LocalDrink,
    "Terminal" to Icons.Default.Terminal,
    "Nightlight" to Icons.Default.Nightlight
  )

  val colorOptions = listOf(
    0xFF0284C7L to BreezySky,
    0xFF10B981L to BreezyMint,
    0xFFF59E0BL to BreezyAmber,
    0xFFFF5757L to BreezySunset,
    0xFF8B5CF6L to BreezyViolet
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = BreezySurface,
    title = {
      Text(
        text = "🌱 Create New Habit",
        color = TextPrimary,
        fontSize = 18.sp,
        fontWeight = FontWeight.Black
      )
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Habit Name (e.g. 5 AM Chiya & Run)") },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BreezySky,
            unfocusedBorderColor = BreezyBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedLabelColor = BreezySky
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("habit_name_input")
        )

        Text("Category", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          items(listOf("MORNING", "FITNESS", "MINDFULNESS", "TECH", "WELLNESS")) { cat ->
            val isSelected = cat == category
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) BreezySky else BreezySurfaceVariant)
                .clickable { category = cat }
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Text(
                text = cat,
                color = if (isSelected) Color.White else TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        Text("Icon", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          iconOptions.forEach { (iconKey, vector) ->
            val isSelected = iconKey == selectedIcon
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) BreezySky.copy(alpha = 0.15f) else BreezySurfaceVariant)
                .border(
                  1.5.dp,
                  if (isSelected) BreezySky else Color.Transparent,
                  RoundedCornerShape(8.dp)
                )
                .clickable { selectedIcon = iconKey },
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = vector,
                contentDescription = iconKey,
                tint = if (isSelected) BreezySky else TextSecondary,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }

        Text("Color Theme", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          colorOptions.forEach { (hexVal, col) ->
            val isSelected = hexVal == selectedColor
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(col)
                .border(
                  if (isSelected) 3.dp else 1.dp,
                  if (isSelected) TextPrimary else Color.Transparent,
                  CircleShape
                )
                .clickable { selectedColor = hexVal }
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (name.isNotBlank()) {
            onConfirm(name, category, targetDays, selectedColor, selectedIcon)
          }
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = BreezySky,
          contentColor = Color.White
        ),
        enabled = name.isNotBlank(),
        modifier = Modifier.testTag("submit_habit_button")
      ) {
        Text("Create Habit", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = TextSecondary)
      }
    }
  )
}
