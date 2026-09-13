package com.example.breezyquest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.breezyquest.data.model.AchievementEntity
import com.example.breezyquest.ui.viewmodel.MainViewModel
import com.example.ui.theme.BreezeCyan
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CardSurfaceVariant
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.EmeraldIncome
import com.example.ui.theme.PurpleMagic
import com.example.ui.theme.QuestGold
import com.example.ui.theme.QuestGoldGlow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun GamificationScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val achievements by viewModel.achievements.collectAsState()

    val level = userProfile?.level ?: 1
    val xp = userProfile?.xp ?: 0
    val levelTitle = viewModel.getLevelTitle(level)
    val nextLevelTitle = viewModel.getLevelTitle(level + 1)
    val xpInLevel = xp % 200
    val xpProgress = (xpInLevel / 200f).coerceIn(0f, 1f)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DeepObsidian),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero RPG Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("rpg_hero_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(CardSurfaceVariant, DarkSurface)
                            )
                        )
                        .padding(22.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(
                                    Brush.radialGradient(listOf(QuestGoldGlow, QuestGold)),
                                    CircleShape
                                )
                                .border(3.dp, QuestGoldGlow, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👑", fontSize = 38.sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "LEVEL $level",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = QuestGold
                        )

                        Text(
                            text = levelTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Progress Bar
                        LinearProgressIndicator(
                            progress = { xpProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            color = QuestGold,
                            trackColor = CardSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("$xpInLevel / 200 XP", fontSize = 12.sp, color = TextSecondary)
                            Text("Next: $nextLevelTitle", fontSize = 12.sp, color = BreezeCyan, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // How to earn XP Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("⚔️ How to Level Up in Breezy Quest", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(10.dp))

                    XpRewardRow("⚔️ Complete Daily Quest", "+20 to +75 XP", "Finish priority tasks & epic missions", QuestGold)
                    Spacer(modifier = Modifier.height(8.dp))
                    XpRewardRow("🔥 Daily Habit Check-off", "+15 XP", "Maintain recurring streaks every single day", BreezeCyan)
                    Spacer(modifier = Modifier.height(8.dp))
                    XpRewardRow("💰 Log Income Entry", "+30 XP", "Add hard-earned revenue toward your target", EmeraldIncome)
                    Spacer(modifier = Modifier.height(8.dp))
                    XpRewardRow("🎯 Advance Goal Progress", "+40 XP", "Fund your future dreams & milestones", PurpleMagic)
                }
            }
        }

        // Achievements Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🏆 Badges & Achievements",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                val unlockedCount = achievements.count { it.isUnlocked }
                Surface(shape = RoundedCornerShape(8.dp), color = QuestGold.copy(alpha = 0.2f)) {
                    Text(
                        "$unlockedCount / ${achievements.size} Unlocked",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = QuestGold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        items(achievements.chunked(2)) { pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                pair.forEach { ach ->
                    Box(modifier = Modifier.weight(1f)) {
                        AchievementBadgeCard(achievement = ach)
                    }
                }
                if (pair.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        item { Spacer(modifier = Modifier.height(40.dp)) }
    }
}

@Composable
fun XpRewardRow(
    title: String,
    xpText: String,
    desc: String,
    accentColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
            Text(desc, fontSize = 11.sp, color = TextSecondary)
        }
        Surface(shape = RoundedCornerShape(8.dp), color = accentColor.copy(alpha = 0.15f)) {
            Text(
                xpText,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = accentColor,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun AchievementBadgeCard(
    achievement: AchievementEntity
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (achievement.isUnlocked) {
                    Modifier.border(1.dp, QuestGoldGlow.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                } else Modifier
            )
            .testTag("achievement_${achievement.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (achievement.isUnlocked) DarkSurface else CardSurface.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        if (achievement.isUnlocked) QuestGold.copy(alpha = 0.2f) else CardSurfaceVariant,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = achievement.iconEmoji,
                    fontSize = 24.sp,
                    color = if (achievement.isUnlocked) Color.Unspecified else TextMuted
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = achievement.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (achievement.isUnlocked) TextPrimary else TextMuted,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = achievement.description,
                style = MaterialTheme.typography.bodySmall,
                color = if (achievement.isUnlocked) TextSecondary else TextMuted,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (achievement.isUnlocked) EmeraldIncome.copy(alpha = 0.2f) else CardSurfaceVariant
            ) {
                Text(
                    text = if (achievement.isUnlocked) "UNLOCKED 🏆" else "LOCKED",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (achievement.isUnlocked) EmeraldIncome else TextMuted,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}
