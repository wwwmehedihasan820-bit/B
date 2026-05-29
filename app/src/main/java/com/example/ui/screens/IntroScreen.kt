package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.data.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntroScreen(
    availableSaves: List<SavedGame>,
    onStartGame: (String, IndustryType, Difficulty) -> Unit,
    onLoadGame: (String) -> Unit,
    onDeleteGame: (String) -> Unit
) {
    var step by remember { mutableStateOf(1) } // 1: Welcome/Saves, 2: Choose Industry, 3: Launch Setups
    var companyNameInput by remember { mutableStateOf("") }
    var selectedIndustry by remember { mutableStateOf(IndustryType.TECH) }
    var selectedDifficulty by remember { mutableStateOf(Difficulty.MEDIUM) }

    val bgGradient = Brush.verticalGradient(
        colors = listOf(RichBlack, DeepCharcoal)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Subtle gold ambient glow in background
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.TopEnd)
                .background(Brush.radialGradient(listOf(CorporateGold.copy(alpha = 0.08f), Color.Transparent)))
        )

        AnimatedContent(
            targetState = step,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "StepTransition"
        ) { currentStep ->
            when (currentStep) {
                1 -> WelcomeAndSavesStep(
                    availableSaves = availableSaves,
                    onStartNewGameClick = { step = 2 },
                    onLoadGame = onLoadGame,
                    onDeleteGame = onDeleteGame
                )
                2 -> ChooseIndustryStep(
                    selected = selectedIndustry,
                    onSelected = { selectedIndustry = it },
                    onBack = { step = 1 },
                    onNext = { step = 3 }
                )
                3 -> ConfigureEmpireStep(
                    companyName = companyNameInput,
                    onCompanyNameChanged = { companyNameInput = it },
                    selectedIndustry = selectedIndustry,
                    selectedDifficulty = selectedDifficulty,
                    onDifficultySelected = { selectedDifficulty = it },
                    onBack = { step = 2 },
                    onLaunch = {
                        onStartGame(companyNameInput, selectedIndustry, selectedDifficulty)
                    }
                )
            }
        }
    }
}

@Composable
fun WelcomeAndSavesStep(
    availableSaves: List<SavedGame>,
    onStartNewGameClick: () -> Unit,
    onLoadGame: (String) -> Unit,
    onDeleteGame: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Visual Brand Header
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "Billionaire Icon",
            modifier = Modifier.size(72.dp),
            tint = CorporateGold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "BILLIONAIRE\nEMPIRE",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = SoftGold,
            textAlign = TextAlign.Center,
            lineHeight = 44.sp,
            style = MaterialTheme.typography.headlineLarge
        )
        
        Text(
            text = "Strategic Corporate Sandbox Simulator",
            fontSize = 14.sp,
            color = TextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Actions
        Button(
            onClick = onStartNewGameClick,
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(56.dp)
                .testTag("start_new_game_btn"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CorporateGold,
                contentColor = RichBlack
            )
        ) {
            Icon(Icons.Default.Add, contentDescription = "New")
            Spacer(modifier = Modifier.width(8.dp))
            Text("FOUND A NEW CORPORATION", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        if (availableSaves.isNotEmpty()) {
            Spacer(modifier = Modifier.height(32.dp))
            Divider(color = BorderAccent.copy(alpha = 0.5f), modifier = Modifier.fillMaxWidth(0.85f))
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "CONTINUE ACTIVE CAMPAIGNS",
                fontSize = 12.sp,
                color = CorporateGold,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                modifier = Modifier.fillMaxWidth(0.85f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            availableSaves.forEach { save ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .padding(vertical = 6.dp)
                        .clickable { onLoadGame(save.id) }
                        .testTag("save_slot_${save.id}"),
                    colors = CardDefaults.cardColors(containerColor = SoftCardGray),
                    border = BorderStroke(1.dp, BorderAccent)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = save.companyName.uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = TextLight,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "${save.industry} • Turn ${save.currentTurn}",
                                fontSize = 12.sp,
                                color = TextMuted,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Text(
                                text = "Assets: $${String.format("%,.0f", save.cash)}",
                                fontSize = 12.sp,
                                color = NeonTeal,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        IconButton(
                            onClick = { onDeleteGame(save.id) },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Save",
                                tint = AlertRed.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChooseIndustryStep(
    selected: IndustryType,
    onSelected: (IndustryType) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = CorporateGold)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "SELECT REVENUE SECTOR",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SoftGold,
                letterSpacing = 1.sp
            )
        }

        Text(
            text = "Your choice dictates base product complexity, marketing margins, and starting R&D barriers.",
            fontSize = 13.sp,
            color = TextMuted,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(IndustryType.values()) { industry ->
                val isSel = industry == selected
                val border = if (isSel) {
                    BorderStroke(2.dp, CorporateGold)
                } else {
                    BorderStroke(1.dp, BorderAccent)
                }
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelected(industry) }
                        .testTag("industry_${industry.name}"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSel) SoftCardGray else DeepCharcoal
                    ),
                    border = border
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val icon = when (industry) {
                            IndustryType.TECH -> Icons.Default.Settings
                            IndustryType.CARS -> Icons.Default.PlayArrow
                            IndustryType.FASHION -> Icons.Default.Favorite
                            IndustryType.RESTAURANTS -> Icons.Default.Home
                            IndustryType.GAMING -> Icons.Default.Star
                            IndustryType.REAL_ESTATE -> Icons.Default.Place
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) CorporateGold.copy(alpha = 0.2f) else SoftCardGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = industry.label,
                                tint = if (isSel) CorporateGold else TextMuted
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = industry.label,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) SoftGold else TextLight,
                                fontSize = 16.sp
                            )
                            Text(
                                text = industry.desc,
                                fontSize = 12.sp,
                                color = TextMuted,
                                modifier = Modifier.padding(top = 4.dp),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("industry_next_btn"),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CorporateGold, contentColor = RichBlack)
        ) {
            Text("CONTINUE SETUP", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigureEmpireStep(
    companyName: String,
    onCompanyNameChanged: (String) -> Unit,
    selectedIndustry: IndustryType,
    selectedDifficulty: Difficulty,
    onDifficultySelected: (Difficulty) -> Unit,
    onBack: () -> Unit,
    onLaunch: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = CorporateGold)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "COMPANY INCORPORATION",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SoftGold,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Name input
        Text(
            text = "CORPORATE NOMINATION",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = CorporateGold,
            letterSpacing = 1.sp
        )
        
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = companyName,
            onValueChange = onCompanyNameChanged,
            placeholder = { Text("e.g. Apex AI, Tesla, Runway Inc.", color = TextMuted) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("company_name_field"),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CorporateGold,
                unfocusedBorderColor = BorderAccent,
                focusedContainerColor = DeepCharcoal,
                unfocusedContainerColor = DeepCharcoal,
                cursorColor = CorporateGold,
                focusedTextColor = TextLight,
                unfocusedTextColor = TextLight
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Difficulty / Strategy selectors
        Text(
            text = "STARTING VENTURE FUNDING",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = CorporateGold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(12.dp))

        Difficulty.values().forEach { difficulty ->
            val isSel = difficulty == selectedDifficulty
            val border = if (isSel) BorderStroke(1.5.dp, CorporateGold) else BorderStroke(1.dp, BorderAccent)
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clickable { onDifficultySelected(difficulty) }
                    .testTag("difficulty_${difficulty.name}"),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSel) SoftCardGray else DeepCharcoal
                ),
                border = border
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = difficulty.label,
                            fontWeight = FontWeight.Bold,
                            color = if (isSel) SoftGold else TextLight,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Fund: $${String.format("%,.0f", difficulty.startingCash)}",
                            fontWeight = FontWeight.Bold,
                            color = NeonTeal,
                            fontSize = 14.sp
                        )
                    }
                    Text(
                        text = difficulty.description,
                        fontSize = 11.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(24.dp))

        // Launch Action
        Button(
            onClick = {
                if (companyName.isNotBlank()) {
                    onLaunch()
                }
            },
            enabled = companyName.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("incorporate_btn"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CorporateGold,
                contentColor = RichBlack,
                disabledContainerColor = BorderAccent,
                disabledContentColor = TextMuted
            )
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = "Launch")
            Spacer(modifier = Modifier.width(8.dp))
            Text("LAUNCH REVENUE OPERATIONS", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}
