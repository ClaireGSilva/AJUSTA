package com.example.ui.screens.diy

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.GeminiService
import com.example.knowledge.RepairTutorialsData
import com.example.model.RepairTutorial
import com.example.model.TutorialStep
import com.example.ui.components.AjustaTopBar
import com.example.ui.theme.AtelierBlack
import com.example.ui.theme.AtelierBrass
import com.example.ui.theme.AtelierBrassContainer
import com.example.ui.theme.AtelierLightBorder
import com.example.ui.theme.AtelierLinen
import com.example.ui.theme.AtelierOffBlack
import com.example.ui.theme.AtelierTerracotta
import com.example.ui.theme.AtelierWarmGray
import com.example.ui.theme.AtelierWarmWhite
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiyRepairScreen(
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var selectedCategory by remember { mutableStateOf("Todos") }
    var selectedTutorialForSheet by remember { mutableStateOf<RepairTutorial?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Custom AI prompt state
    var customPrompt by remember { mutableStateOf("") }
    var isGeneratingAiTutorial by remember { mutableStateOf(false) }

    val allTutorials = remember {
        mutableStateListOf<RepairTutorial>().apply {
            addAll(RepairTutorialsData.tutorials)
        }
    }

    val categories = listOf("Todos", "Botões", "Bainhas", "Zíper", "Ajustes", "Cerzimento")

    val filteredTutorials = remember(selectedCategory, allTutorials.size) {
        if (selectedCategory == "Todos") {
            allTutorials
        } else {
            allTutorials.filter { it.category.equals(selectedCategory, ignoreCase = true) }
        }
    }

    fun requestAiTutorial(promptText: String) {
        if (promptText.isBlank() || isGeneratingAiTutorial) return
        isGeneratingAiTutorial = true
        scope.launch {
            try {
                val newTutorial = GeminiService.generateRepairTutorial(promptText.trim())
                allTutorials.add(0, newTutorial)
                selectedTutorialForSheet = newTutorial
                customPrompt = ""
            } finally {
                isGeneratingAiTutorial = false
            }
        }
    }

    Scaffold(
        containerColor = AtelierWarmWhite,
        topBar = {
            AjustaTopBar(
                title = "Dicas de Reparo",
                subtitle = "Tutoriais manuais passo a passo",
                onBackClick = onBack
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Description Card
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, AtelierLightBorder),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(AtelierLinen),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Construction,
                                    contentDescription = null,
                                    tint = AtelierBrass,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Atelier em Casa",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = AtelierBlack
                                )
                                Text(
                                    text = "Pequenos reparos e ajustes seguros sem máquina",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AtelierWarmGray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Aprenda as técnicas essenciais de alfaiataria manual para salvar suas roupas favoritas: botões firmes com haste, barras invisíveis, destravar zíperes e recuperar furos.",
                            style = MaterialTheme.typography.bodySmall,
                            color = AtelierOffBlack,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Gemini Custom Tutorial Generator
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AtelierLinen),
                    border = BorderStroke(1.dp, AtelierLightBorder),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = AtelierBrass,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Gerar Tutorial com Gemini",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = AtelierBlack
                                )
                            }
                            Surface(
                                color = AtelierBrass,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "IA sob demanda",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Precisa de ajuda com um reparo específico? Peça um passo a passo sob medida para a IA:",
                            style = MaterialTheme.typography.bodySmall,
                            color = AtelierWarmGray
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = customPrompt,
                                onValueChange = { customPrompt = it },
                                placeholder = {
                                    Text(
                                        "Ex: alça de vestido de seda, forro de bolso...",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 13.sp
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("repair_custom_prompt_input"),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = AtelierBlack,
                                    unfocusedBorderColor = AtelierLightBorder
                                )
                            )

                            Button(
                                onClick = { requestAiTutorial(customPrompt) },
                                enabled = customPrompt.isNotBlank() && !isGeneratingAiTutorial,
                                colors = ButtonDefaults.buttonColors(containerColor = AtelierBlack),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .height(52.dp)
                                    .testTag("repair_generate_tutorial_button")
                            ) {
                                if (isGeneratingAiTutorial) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = AtelierWarmWhite,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text("Gerar", color = AtelierWarmWhite)
                                }
                            }
                        }
                    }
                }
            }

            // Category Filter Chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { category ->
                        val isSelected = selectedCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = category },
                            label = { Text(category) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AtelierBlack,
                                selectedLabelColor = AtelierWarmWhite,
                                containerColor = Color.White,
                                labelColor = AtelierOffBlack
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) AtelierBlack else AtelierLightBorder
                            ),
                            modifier = Modifier.testTag("category_filter_$category")
                        )
                    }
                }
            }

            // Section count
            item {
                Text(
                    text = "TUTORIAIS DISPONÍVEIS (${filteredTutorials.size})",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                    color = AtelierWarmGray
                )
            }

            // Tutorial Cards
            items(filteredTutorials) { tutorial ->
                TutorialCard(
                    tutorial = tutorial,
                    onClick = { selectedTutorialForSheet = tutorial }
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Interactive Step-by-Step Bottom Sheet
    if (selectedTutorialForSheet != null) {
        val tutorial = selectedTutorialForSheet!!
        ModalBottomSheet(
            onDismissRequest = { selectedTutorialForSheet = null },
            sheetState = sheetState,
            containerColor = AtelierWarmWhite,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            TutorialDetailContent(
                tutorial = tutorial,
                onClose = { selectedTutorialForSheet = null }
            )
        }
    }
}

@Composable
private fun TutorialCard(
    tutorial: RepairTutorial,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("tutorial_card_${tutorial.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, AtelierLightBorder),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = AtelierLinen,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = tutorial.category.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = AtelierBrass,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontSize = 10.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Timer,
                        contentDescription = null,
                        tint = AtelierWarmGray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${tutorial.estimatedTimeMinutes} min • ${tutorial.difficulty}",
                        style = MaterialTheme.typography.labelSmall,
                        color = AtelierWarmGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = tutorial.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = AtelierBlack
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = tutorial.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = AtelierWarmGray,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${tutorial.steps.size} etapas ilustradas",
                    style = MaterialTheme.typography.labelSmall,
                    color = AtelierOffBlack
                )

                Text(
                    text = "Ver passo a passo →",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = AtelierBrass
                )
            }
        }
    }
}

@Composable
private fun TutorialDetailContent(
    tutorial: RepairTutorial,
    onClose: () -> Unit
) {
    val completedSteps = remember { mutableStateMapOf<Int, Boolean>() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(bottom = 36.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = AtelierBrassContainer,
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "${tutorial.category.uppercase()} • ${tutorial.difficulty.uppercase()}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = AtelierBrass,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = 11.sp
                )
            }

            IconButton(onClick = onClose) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Fechar", tint = AtelierBlack)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = tutorial.title,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = AtelierBlack
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = tutorial.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = AtelierWarmGray
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tools Needed Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AtelierLinen),
            border = BorderStroke(1.dp, AtelierLightBorder),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "🧰 MATERIAIS NECESSÁRIOS",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp, fontWeight = FontWeight.Bold),
                    color = AtelierBlack
                )
                Spacer(modifier = Modifier.height(8.dp))
                tutorial.toolsNeeded.forEach { tool ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Text(text = "•", color = AtelierBrass, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = tool,
                            style = MaterialTheme.typography.bodySmall,
                            color = AtelierOffBlack
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Steps Progress Headline
        val finishedCount = completedSteps.values.count { it }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PASSO A PASSO",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp, fontWeight = FontWeight.Bold),
                color = AtelierWarmGray
            )

            Text(
                text = "$finishedCount de ${tutorial.steps.size} concluídos",
                style = MaterialTheme.typography.labelSmall,
                color = if (finishedCount == tutorial.steps.size) AtelierBrass else AtelierWarmGray
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Steps List
        tutorial.steps.forEach { step ->
            val isChecked = completedSteps[step.stepNumber] ?: false
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = if (isChecked) AtelierLinen else Color.White),
                border = BorderStroke(1.dp, if (isChecked) AtelierBrass else AtelierLightBorder),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { completedSteps[step.stepNumber] = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = AtelierBrass,
                            checkmarkColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Etapa ${step.stepNumber}: ${step.title}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = AtelierBlack
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = step.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = AtelierOffBlack,
                            lineHeight = 20.sp
                        )

                        if (!step.tip.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                color = AtelierWarmWhite,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, AtelierLightBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "💡 ${step.tip}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AtelierWarmGray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Common Mistakes
        if (tutorial.commonMistakes.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, AtelierLightBorder),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "⚠️ ERROS COMUNS PARA EVITAR",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp, fontWeight = FontWeight.Bold),
                        color = AtelierTerracotta
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    tutorial.commonMistakes.forEach { mistake ->
                        Text(
                            text = "❌ $mistake",
                            style = MaterialTheme.typography.bodySmall,
                            color = AtelierOffBlack,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Tailor Master Tip
        if (tutorial.tailorTip.isNotBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                color = AtelierLinen,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, AtelierBrass),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "✨ DICA DO MESTRE ALFAIATE",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = AtelierBrass
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = tutorial.tailorTip,
                        style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                        color = AtelierOffBlack,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onClose,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AtelierBlack)
        ) {
            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = AtelierWarmWhite)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Concluir Reparo", color = AtelierWarmWhite, style = MaterialTheme.typography.titleSmall)
        }
    }
}
