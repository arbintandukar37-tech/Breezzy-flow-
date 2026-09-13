package com.example.breezyquest.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.breezyquest.ui.viewmodel.FocusSessionType
import com.example.breezyquest.ui.viewmodel.MainViewModel
import com.example.breezyquest.ui.viewmodel.StudySubject
import com.example.ui.theme.BreezeCyan
import com.example.ui.theme.CardSurface
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.EmeraldIncome
import com.example.ui.theme.QuestGold
import com.example.ui.theme.RoseExpense
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun StudyScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val subjects by viewModel.studySubjects.collectAsState()
    val todayMins by viewModel.todayStudyMinutes.collectAsState()
    val studyStreak by viewModel.studyStreakDays.collectAsState()

    val isRunning by viewModel.isFocusTimerRunning.collectAsState()
    val secondsLeft by viewModel.focusTimerSecondsLeft.collectAsState()
    val totalSeconds by viewModel.focusTimerTotalSeconds.collectAsState()
    val activeSubject by viewModel.focusActiveSubject.collectAsState()
    val sessionType by viewModel.focusSessionType.collectAsState()

    var showAddSubjectDialog by remember { mutableStateOf(false) }
    var editingSubject by remember { mutableStateOf<StudySubject?>(null) }

    val formattedMinutes = String.format("%02d", secondsLeft / 60)
    val formattedSeconds = String.format("%02d", secondsLeft % 60)
    val timerProgress = if (totalSeconds > 0) (secondsLeft.toFloat() / totalSeconds.toFloat()) else 0f
    val animatedProgress by animateFloatAsState(targetValue = timerProgress, label = "timerProgress")

    val hoursStudiedToday = todayMins / 60
    val remMinsStudiedToday = todayMins % 60
    val todayStudyStr = if (hoursStudiedToday > 0) "${hoursStudiedToday}h ${remMinsStudiedToday}m" else "${remMinsStudiedToday}m"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("study_screen_root"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Focus / Pomodoro Timer Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("focus_pomodoro_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⏱️", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Focus Mode",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = QuestGold.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "+50 XP on Complete",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = QuestGold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Mode switch pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            FocusSessionType.DEEP_STUDY,
                            FocusSessionType.POMODORO,
                            FocusSessionType.SHORT_BREAK,
                            FocusSessionType.LONG_BREAK
                        ).forEach { type ->
                            val isSelected = sessionType == type
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        viewModel.setFocusConfig(activeSubject, type)
                                    },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) BreezeCyan else CardSurface
                            ) {
                                Text(
                                    text = "${type.title.split(" ")[0]} (${type.defaultMinutes}m)",
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.Black else TextSecondary,
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Circular Countdown Timer
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(170.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = { 1f },
                            modifier = Modifier.fillMaxSize(),
                            color = CardSurface,
                            strokeWidth = 10.dp
                        )
                        CircularProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier.fillMaxSize(),
                            color = BreezeCyan,
                            strokeWidth = 10.dp
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$formattedMinutes:$formattedSeconds",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = activeSubject,
                                fontSize = 12.sp,
                                color = BreezeCyan,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(horizontal = 14.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Timer Control Buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Reset
                        IconButton(
                            onClick = { viewModel.resetFocusTimer() },
                            modifier = Modifier
                                .size(44.dp)
                                .background(CardSurface, CircleShape)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reset Timer", tint = TextSecondary)
                        }

                        // Play/Pause Main Button
                        Button(
                            onClick = {
                                if (isRunning) viewModel.pauseFocusTimer() else viewModel.startFocusTimer()
                            },
                            modifier = Modifier
                                .height(46.dp)
                                .testTag("focus_timer_toggle_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = BreezeCyan),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(
                                imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isRunning) "Pause" else "▶ Start Focus",
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                fontSize = 14.sp
                            )
                        }

                        // Complete Now
                        IconButton(
                            onClick = { viewModel.completeFocusSession() },
                            modifier = Modifier
                                .size(44.dp)
                                .background(EmeraldIncome.copy(alpha = 0.2f), CircleShape)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Complete Session", tint = EmeraldIncome)
                        }
                    }
                }
            }
        }

        // Daily Study & Streak Stats Banner
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(38.dp).background(BreezeCyan.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📚", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Today's Study", fontSize = 11.sp, color = TextSecondary)
                            Text(todayStudyStr, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BreezeCyan)
                        }
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(38.dp).background(QuestGold.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🔥", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Study Streak", fontSize = 11.sp, color = TextSecondary)
                            Text("$studyStreak Days", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = QuestGold)
                        }
                    }
                }
            }
        }

        // Subjects Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📖", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Subjects Tracker",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Button(
                    onClick = { showAddSubjectDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CardSurface),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = BreezeCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Custom", fontSize = 12.sp, color = BreezeCyan, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Subjects List with Progress Bars
        items(subjects) { subject ->
            val hours = subject.studiedMinutes / 60
            val remMins = subject.studiedMinutes % 60
            val goalHours = subject.weeklyGoalMinutes / 60
            val studiedStr = if (hours > 0) "${hours}h ${remMins}m" else "${remMins}m"
            val progressPercent = if (subject.weeklyGoalMinutes > 0) {
                (subject.studiedMinutes.toFloat() / subject.weeklyGoalMinutes.toFloat()).coerceIn(0f, 1f)
            } else 0f
            val percentDisplay = (progressPercent * 100).toInt()

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.setFocusConfig("${subject.name} — Session", sessionType)
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(36.dp).background(CardSurface, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(subject.iconEmoji, fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = subject.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Study Time: $studiedStr • Goal: ${goalHours}h",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (progressPercent >= 0.8f) EmeraldIncome.copy(alpha = 0.2f) else BreezeCyan.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "$percentDisplay%",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (progressPercent >= 0.8f) EmeraldIncome else BreezeCyan,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            IconButton(
                                onClick = { editingSubject = subject },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TextSecondary, modifier = Modifier.size(16.dp))
                            }
                            IconButton(
                                onClick = { viewModel.deleteStudySubject(subject.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RoseExpense, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { progressPercent },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (progressPercent >= 0.8f) EmeraldIncome else BreezeCyan,
                        trackColor = CardSurface
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }

    if (showAddSubjectDialog) {
        var newName by remember { mutableStateOf("") }
        var newEmoji by remember { mutableStateOf("📚") }
        var newGoalHours by remember { mutableStateOf("10") }

        AlertDialog(
            onDismissRequest = { showAddSubjectDialog = false },
            title = { Text("Add Study Subject", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Subject Name (e.g. History)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BreezeCyan,
                            unfocusedBorderColor = CardSurface
                        )
                    )
                    OutlinedTextField(
                        value = newEmoji,
                        onValueChange = { newEmoji = it },
                        label = { Text("Icon Emoji") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BreezeCyan,
                            unfocusedBorderColor = CardSurface
                        )
                    )
                    OutlinedTextField(
                        value = newGoalHours,
                        onValueChange = { newGoalHours = it },
                        label = { Text("Weekly Goal (Hours)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BreezeCyan,
                            unfocusedBorderColor = CardSurface
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isNotBlank()) {
                            val hrs = newGoalHours.toIntOrNull() ?: 10
                            viewModel.addCustomStudySubject(newName, newEmoji, hrs)
                            showAddSubjectDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BreezeCyan)
                ) {
                    Text("Add Subject", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSubjectDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = DarkSurface
        )
    }

    if (editingSubject != null) {
        val subj = editingSubject!!
        var editName by remember(subj.id) { mutableStateOf(subj.name) }
        var editEmoji by remember(subj.id) { mutableStateOf(subj.iconEmoji) }
        var editGoalHours by remember(subj.id) { mutableStateOf((subj.weeklyGoalMinutes / 60).toString()) }

        AlertDialog(
            onDismissRequest = { editingSubject = null },
            title = { Text("Edit Subject", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Subject Name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BreezeCyan,
                            unfocusedBorderColor = CardSurface
                        )
                    )
                    OutlinedTextField(
                        value = editEmoji,
                        onValueChange = { editEmoji = it },
                        label = { Text("Icon Emoji") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BreezeCyan,
                            unfocusedBorderColor = CardSurface
                        )
                    )
                    OutlinedTextField(
                        value = editGoalHours,
                        onValueChange = { editGoalHours = it },
                        label = { Text("Weekly Goal (Hours)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BreezeCyan,
                            unfocusedBorderColor = CardSurface
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editName.isNotBlank()) {
                            val hrs = editGoalHours.toIntOrNull() ?: (subj.weeklyGoalMinutes / 60)
                            viewModel.updateStudySubject(subj.id, editName, editEmoji, hrs)
                            editingSubject = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BreezeCyan)
                ) {
                    Text("Save", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingSubject = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = DarkSurface
        )
    }
}
