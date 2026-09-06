package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Send
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
import com.example.data.models.SubTaskCodec
import com.example.data.models.TaskEntity
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
import com.example.ui.theme.QuadrantDelegate
import com.example.ui.theme.QuadrantDoFirst
import com.example.ui.theme.QuadrantEliminate
import com.example.ui.theme.QuadrantSchedule
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TasksScreen(
  tasks: List<TaskEntity>,
  onToggleTask: (TaskEntity) -> Unit,
  onToggleSubtask: (TaskEntity, String) -> Unit,
  onAddTask: (String, String, String, String, String, List<String>) -> Unit,
  onDeleteTask: (TaskEntity) -> Unit,
  modifier: Modifier = Modifier
) {
  var isMatrixView by remember { mutableStateOf(true) }
  var quickAddText by remember { mutableStateOf("") }
  var showAddDialog by remember { mutableStateOf(false) }

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
          .testTag("add_task_fab")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Add, contentDescription = "Add Task")
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "New Task",
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
      // Top Control Bar: Mode switch
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (isMatrixView) "Eisenhower Matrix" else "Priority Checklist",
          color = TextPrimary,
          fontSize = 18.sp,
          fontWeight = FontWeight.Black
        )

        // View Mode Switcher
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(BreezySurface)
            .border(1.dp, BreezyBorder, RoundedCornerShape(12.dp))
            .padding(3.dp)
        ) {
          Row {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(if (isMatrixView) BreezySky else Color.Transparent)
                .clickable { isMatrixView = true }
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .testTag("matrix_view_toggle")
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  Icons.Default.GridView,
                  contentDescription = "Matrix",
                  tint = if (isMatrixView) Color.White else TextSecondary,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Matrix",
                  color = if (isMatrixView) Color.White else TextSecondary,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(if (!isMatrixView) BreezySky else Color.Transparent)
                .clickable { isMatrixView = false }
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .testTag("list_view_toggle")
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  Icons.Default.List,
                  contentDescription = "List",
                  tint = if (!isMatrixView) Color.White else TextSecondary,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "List",
                  color = if (!isMatrixView) Color.White else TextSecondary,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }

      // Quick Add Command Bar (Light & Clean)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp)
          .shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp), spotColor = Color(0x0A000000))
          .clip(RoundedCornerShape(16.dp))
          .background(BreezySurface)
          .border(1.dp, BreezyBorder, RoundedCornerShape(16.dp))
          .padding(horizontal = 14.dp, vertical = 2.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedTextField(
            value = quickAddText,
            onValueChange = { quickAddText = it },
            placeholder = {
              Text("Quick add task... (e.g. /urgent Momo prep /today)", color = TextMuted, fontSize = 13.sp)
            },
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = Color.Transparent,
              unfocusedBorderColor = Color.Transparent,
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary
            ),
            modifier = Modifier
              .weight(1f)
              .testTag("quick_add_task_input")
          )

          IconButton(
            onClick = {
              if (quickAddText.isNotBlank()) {
                val isUrgent = quickAddText.contains("/urgent", ignoreCase = true)
                val isDoFirst = isUrgent || quickAddText.contains("/high", ignoreCase = true)
                val quadrant = if (isDoFirst) "DO_FIRST" else "SCHEDULE"
                val cleanedTitle = quickAddText
                  .replace("/urgent", "", ignoreCase = true)
                  .replace("/high", "", ignoreCase = true)
                  .replace("/today", "", ignoreCase = true)
                  .trim()

                onAddTask(
                  if (cleanedTitle.isNotBlank()) cleanedTitle else quickAddText,
                  "Added via Quick Bar",
                  quadrant,
                  if (isDoFirst) "HIGH" else "MEDIUM",
                  if (quickAddText.contains("/today", ignoreCase = true)) "Today" else "Upcoming",
                  emptyList()
                )
                quickAddText = ""
              }
            },
            enabled = quickAddText.isNotBlank(),
            modifier = Modifier.testTag("submit_quick_task_btn")
          ) {
            Icon(
              Icons.Default.Send,
              contentDescription = "Send",
              tint = if (quickAddText.isNotBlank()) BreezySky else Color(0xFFCBD5E1)
            )
          }
        }
      }

      // Quick Command Shortcuts Chips
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        QuickCommandChip("/today") { quickAddText += " /today " }
        QuickCommandChip("/urgent") { quickAddText += " /urgent " }
        QuickCommandChip("/high") { quickAddText += " /high " }
      }

      // Main Content: Matrix vs List
      if (isMatrixView) {
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp),
          contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp)
        ) {
          // Quadrant 1: DO FIRST (Urgent & Important)
          item {
            QuadrantSection(
              title = "DO FIRST (Urgent & Important)",
              subtitle = "High crisis & high impact tasks",
              accentColor = QuadrantDoFirst,
              lightBg = BreezySunsetLight,
              tasks = tasks.filter { it.quadrant == "DO_FIRST" },
              onToggleTask = onToggleTask,
              onToggleSubtask = onToggleSubtask,
              onDeleteTask = onDeleteTask
            )
          }

          // Quadrant 2: SCHEDULE (Not Urgent & Important)
          item {
            QuadrantSection(
              title = "SCHEDULE (Important, Not Urgent)",
              subtitle = "Long term goals & personal growth",
              accentColor = QuadrantSchedule,
              lightBg = BreezySkyLight,
              tasks = tasks.filter { it.quadrant == "SCHEDULE" },
              onToggleTask = onToggleTask,
              onToggleSubtask = onToggleSubtask,
              onDeleteTask = onDeleteTask
            )
          }

          // Quadrant 3: DELEGATE (Urgent, Not Important)
          item {
            QuadrantSection(
              title = "DELEGATE (Urgent, Low Impact)",
              subtitle = "Interruptions & secondary requests",
              accentColor = QuadrantDelegate,
              lightBg = BreezyAmberLight,
              tasks = tasks.filter { it.quadrant == "DELEGATE" },
              onToggleTask = onToggleTask,
              onToggleSubtask = onToggleSubtask,
              onDeleteTask = onDeleteTask
            )
          }

          // Quadrant 4: ELIMINATE / ROUTINE (Neither)
          item {
            QuadrantSection(
              title = "ROUTINE / ELIMINATE",
              subtitle = "Low priority or minor chores",
              accentColor = QuadrantEliminate,
              lightBg = Color(0xFFF1F5F9),
              tasks = tasks.filter { it.quadrant == "ELIMINATE" },
              onToggleTask = onToggleTask,
              onToggleSubtask = onToggleSubtask,
              onDeleteTask = onDeleteTask
            )
          }
        }
      } else {
        // Plain List Mode
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp),
          contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp)
        ) {
          items(tasks, key = { it.id }) { task ->
            TaskMatrixCard(
              task = task,
              onToggle = { onToggleTask(task) },
              onToggleSubtask = { subId -> onToggleSubtask(task, subId) },
              onDelete = { onDeleteTask(task) }
            )
          }
        }
      }
    }
  }

  if (showAddDialog) {
    AddTaskDialog(
      onDismiss = { showAddDialog = false },
      onConfirm = { title, notes, quad, prio, due, subtasks ->
        onAddTask(title, notes, quad, prio, due, subtasks)
        showAddDialog = false
      }
    )
  }
}

@Composable
fun QuickCommandChip(text: String, onClick: () -> Unit) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(8.dp))
      .background(BreezySkyLight)
      .clickable { onClick() }
      .padding(horizontal = 10.dp, vertical = 4.dp)
  ) {
    Text(
      text = text,
      color = BreezySky,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold
    )
  }
}

@Composable
fun QuadrantSection(
  title: String,
  subtitle: String,
  accentColor: Color,
  lightBg: Color,
  tasks: List<TaskEntity>,
  onToggleTask: (TaskEntity) -> Unit,
  onToggleSubtask: (TaskEntity, String) -> Unit,
  onDeleteTask: (TaskEntity) -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .shadow(elevation = 2.dp, shape = RoundedCornerShape(18.dp), spotColor = Color(0x0A000000))
      .clip(RoundedCornerShape(18.dp))
      .background(BreezySurface)
      .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
      .padding(14.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = title,
          color = accentColor,
          fontSize = 13.sp,
          fontWeight = FontWeight.Black,
          letterSpacing = 0.5.sp
        )
        Text(
          text = subtitle,
          color = TextSecondary,
          fontSize = 11.sp
        )
      }

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(10.dp))
          .background(lightBg)
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Text(
          text = "${tasks.count { it.isCompleted }}/${tasks.size}",
          color = accentColor,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    if (tasks.isEmpty()) {
      Text(
        text = "No tasks in this quadrant",
        color = TextMuted,
        fontSize = 12.sp,
        modifier = Modifier.padding(vertical = 6.dp)
      )
    } else {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        tasks.forEach { task ->
          TaskMatrixCard(
            task = task,
            onToggle = { onToggleTask(task) },
            onToggleSubtask = { subId -> onToggleSubtask(task, subId) },
            onDelete = { onDeleteTask(task) }
          )
        }
      }
    }
  }
}

@Composable
fun TaskMatrixCard(
  task: TaskEntity,
  onToggle: () -> Unit,
  onToggleSubtask: (String) -> Unit,
  onDelete: () -> Unit
) {
  var isExpanded by remember { mutableStateOf(false) }
  val subtasks = remember(task.subtasksRaw) {
    SubTaskCodec.deserialize(task.subtasksRaw)
  }

  val quadColor = when (task.quadrant) {
    "DO_FIRST" -> QuadrantDoFirst
    "SCHEDULE" -> QuadrantSchedule
    "DELEGATE" -> QuadrantDelegate
    else -> QuadrantEliminate
  }

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(Color(0xFFFAFCFF))
      .border(
        1.dp,
        if (task.isCompleted) BreezyBorder else quadColor.copy(alpha = 0.45f),
        RoundedCornerShape(14.dp)
      )
      .padding(12.dp)
      .testTag("task_item_${task.id}")
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Toggle check
        Box(
          modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(if (task.isCompleted) BreezyMint else Color(0xFFF1F5F9))
            .border(
              1.5.dp,
              if (task.isCompleted) BreezyMint else Color(0xFFCBD5E1),
              CircleShape
            )
            .clickable { onToggle() }
            .testTag("toggle_task_${task.id}"),
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
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
          )

          if (task.notes.isNotBlank()) {
            Text(
              text = task.notes,
              color = TextSecondary,
              fontSize = 11.sp,
              maxLines = 1
            )
          }
        }

        if (subtasks.isNotEmpty()) {
          Row(
            modifier = Modifier
              .clickable { isExpanded = !isExpanded }
              .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "${subtasks.count { it.isDone }}/${subtasks.size}",
              color = BreezySky,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
            Icon(
              imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
              contentDescription = "Expand Subtasks",
              tint = BreezySky,
              modifier = Modifier.size(16.dp)
            )
          }
        }

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

      // Expandable Subtasks Checklist
      AnimatedVisibility(visible = isExpanded && subtasks.isNotEmpty()) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, start = 34.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          subtasks.forEach { subItem ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleSubtask(subItem.id) },
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = if (subItem.isDone) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                contentDescription = null,
                tint = if (subItem.isDone) BreezyMint else Color(0xFF94A3B8),
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = subItem.title,
                color = if (subItem.isDone) TextMuted else TextPrimary,
                fontSize = 12.sp
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun AddTaskDialog(
  onDismiss: () -> Unit,
  onConfirm: (title: String, notes: String, quadrant: String, priority: String, due: String, subtasks: List<String>) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var notes by remember { mutableStateOf("") }
  var selectedQuadrant by remember { mutableStateOf("DO_FIRST") }
  var dueDate by remember { mutableStateOf("Today") }
  var subtaskInput by remember { mutableStateOf("") }

  val quadrants = listOf(
    "DO_FIRST" to "Do First (Urgent & Imp)",
    "SCHEDULE" to "Schedule (Not Urgent & Imp)",
    "DELEGATE" to "Delegate (Urgent, Low Imp)",
    "ELIMINATE" to "Routine (Eliminate)"
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = BreezySurface,
    title = {
      Text(
        text = "⚡ Add Matrix Task",
        color = TextPrimary,
        fontSize = 18.sp,
        fontWeight = FontWeight.Black
      )
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Task Title") },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BreezySky,
            unfocusedBorderColor = BreezyBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedLabelColor = BreezySky
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("task_title_input")
        )

        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("Notes / Context") },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BreezySky,
            unfocusedBorderColor = BreezyBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedLabelColor = BreezySky
          ),
          modifier = Modifier.fillMaxWidth()
        )

        Text("Eisenhower Quadrant", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          quadrants.forEach { (quadKey, label) ->
            val isSelected = quadKey == selectedQuadrant
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) BreezySky else BreezySurfaceVariant)
                .clickable { selectedQuadrant = quadKey }
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Text(
                text = label,
                color = if (isSelected) Color.White else TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        OutlinedTextField(
          value = subtaskInput,
          onValueChange = { subtaskInput = it },
          label = { Text("Add Subtasks (comma separated)") },
          placeholder = { Text("e.g. Step 1, Step 2, Step 3") },
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
          if (title.isNotBlank()) {
            val subtasks = subtaskInput.split(",").map { it.trim() }.filter { it.isNotBlank() }
            onConfirm(title, notes, selectedQuadrant, "HIGH", dueDate, subtasks)
          }
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = BreezySky,
          contentColor = Color.White
        ),
        enabled = title.isNotBlank(),
        modifier = Modifier.testTag("submit_task_button")
      ) {
        Text("Save Task", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = TextSecondary)
      }
    }
  )
}
