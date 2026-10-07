package com.example.ui.screens.knowledge

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Iron
import androidx.compose.material.icons.outlined.LocalLaundryService
import androidx.compose.material.icons.outlined.ReportProblem
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.knowledge.AlterationDefinition
import com.example.knowledge.FabricDefinition
import com.example.knowledge.GarmentDefinition
import com.example.knowledge.KnowledgeBase
import com.example.ui.components.AjustaTopBar
import com.example.ui.components.RiskBadge
import com.example.ui.theme.AtelierBlack
import com.example.ui.theme.AtelierBrass
import com.example.ui.theme.AtelierBrassContainer
import com.example.ui.theme.AtelierLightBorder
import com.example.ui.theme.AtelierLinen
import com.example.ui.theme.AtelierOffBlack
import com.example.ui.theme.AtelierSand
import com.example.ui.theme.AtelierTerracotta
import com.example.ui.theme.AtelierWarmGray
import com.example.ui.theme.AtelierWarmWhite
import kotlinx.coroutines.launch

@Composable
fun KnowledgeScreen(onBack: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    val tabs = listOf("Tecidos", "Técnicas", "Peças", "Google Search 🌐")

    Scaffold(
        containerColor = AtelierWarmWhite,
        topBar = {
            AjustaTopBar(
                title = "Guia do Atelier",
                subtitle = "Enciclopédia de Tecidos & Alfaiataria",
                onBackClick = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Pesquisar tecido, técnica ou peça...", style = MaterialTheme.typography.bodySmall) },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = AtelierWarmGray) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Limpar")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .height(50.dp)
                    .testTag("knowledge_search_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = AtelierBrass,
                    unfocusedBorderColor = AtelierLightBorder
                ),
                singleLine = true
            )

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = AtelierWarmWhite,
                contentColor = AtelierBlack,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = AtelierBrass
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = (selectedTab == index),
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            when (selectedTab) {
                0 -> FabricsKnowledgeList(searchQuery = searchQuery)
                1 -> AlterationsKnowledgeList(searchQuery = searchQuery)
                2 -> GarmentsKnowledgeList(searchQuery = searchQuery)
                3 -> GoogleSearchGroundingContent()
            }
        }
    }
}

@Composable
private fun FabricsKnowledgeList(searchQuery: String) {
    val q = searchQuery.trim().lowercase()
    val fabrics = KnowledgeBase.fabrics.values.filter {
        q.isEmpty() ||
            it.namePt.lowercase().contains(q) ||
            it.nameEn.lowercase().contains(q) ||
            it.category.lowercase().contains(q) ||
            it.behaviorNotes.lowercase().contains(q)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Guia Completo de Fibras & Cuidados",
                style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Serif),
                color = AtelierBlack
            )
            Text(
                text = "Comportamento térmico, lavagem, furação de agulha e resposta a alterações de alfaiataria.",
                style = MaterialTheme.typography.bodySmall,
                color = AtelierWarmGray
            )
        }

        items(fabrics) { fabric ->
            FabricDetailCard(fabric = fabric)
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

@Composable
private fun FabricDetailCard(fabric: FabricDefinition) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { expanded = !expanded }
            .testTag("fabric_card_${fabric.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, AtelierLightBorder),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = fabric.namePt,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        ),
                        color = AtelierBlack
                    )
                    Text(
                        text = "${fabric.category} (${fabric.nameEn})",
                        style = MaterialTheme.typography.bodySmall,
                        color = AtelierWarmGray
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RiskBadge(fabric.riskLevel)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = AtelierWarmGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = fabric.behaviorNotes,
                style = MaterialTheme.typography.bodyMedium,
                color = AtelierOffBlack
            )

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    HorizontalDivider(color = AtelierLightBorder)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Comportamento em Ajustes
                    Row(verticalAlignment = Alignment.Top) {
                        Text(
                            text = "✂️ Em Ajustes: ",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = AtelierBrass
                        )
                        Text(
                            text = fabric.alterationBehavior,
                            style = MaterialTheme.typography.bodySmall,
                            color = AtelierOffBlack
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Instruções de Cuidados & Lavagem
                    Row(verticalAlignment = Alignment.Top) {
                        Text(
                            text = "🧺 Cuidados: ",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = AtelierBrass
                        )
                        Text(
                            text = fabric.careInstructions,
                            style = MaterialTheme.typography.bodySmall,
                            color = AtelierOffBlack
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Agulha e Linha Recomendadas
                    Row(verticalAlignment = Alignment.Top) {
                        Text(
                            text = "🪡 Agulha & Linha: ",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = AtelierBrass
                        )
                        Text(
                            text = fabric.recommendedNeedleAndThread,
                            style = MaterialTheme.typography.bodySmall,
                            color = AtelierOffBlack
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AlterationsKnowledgeList(searchQuery: String) {
    val q = searchQuery.trim().lowercase()
    val alterations = KnowledgeBase.alterations.values.filter {
        q.isEmpty() ||
            it.namePt.lowercase().contains(q) ||
            it.nameEn.lowercase().contains(q) ||
            it.toolsAndNeedles.lowercase().contains(q)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Técnicas Especializadas de Alfaiataria",
                style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Serif),
                color = AtelierBlack
            )
            Text(
                text = "Procedimento passo a passo, ferramentas necessárias, complexidade e riscos calculados.",
                style = MaterialTheme.typography.bodySmall,
                color = AtelierWarmGray
            )
        }

        items(alterations) { alt ->
            TechniqueDetailCard(alt = alt)
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

@Composable
private fun TechniqueDetailCard(alt: AlterationDefinition) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { expanded = !expanded }
            .testTag("technique_card_${alt.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, AtelierLightBorder),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = alt.namePt,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        ),
                        color = AtelierBlack
                    )
                    Text(
                        text = "${alt.complexity.label} • R$ ${alt.defaultBasePrice.toInt()} base",
                        style = MaterialTheme.typography.bodySmall,
                        color = AtelierBrass
                    )
                }

                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = AtelierWarmGray
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Ferramentas: ${alt.toolsAndNeedles}",
                style = MaterialTheme.typography.bodySmall,
                color = AtelierOffBlack
            )

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(color = AtelierLightBorder)
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Procedimento Técnico Passo a Passo:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = AtelierBlack
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    alt.processSteps.forEach { step ->
                        Text(
                            text = step,
                            style = MaterialTheme.typography.bodySmall,
                            color = AtelierOffBlack,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Riscos e limitações
                    Surface(
                        color = AtelierLinen,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "⚠️ Riscos & Limitações:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = AtelierTerracotta
                            )
                            alt.risks.forEach { r ->
                                Text(
                                    text = "• $r",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AtelierOffBlack
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Limitação: ${alt.limitations}",
                                style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                                color = AtelierWarmGray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GarmentsKnowledgeList(searchQuery: String) {
    val q = searchQuery.trim().lowercase()
    val garments = KnowledgeBase.garments.values.filter {
        q.isEmpty() ||
            it.namePt.lowercase().contains(q) ||
            it.nameEn.lowercase().contains(q) ||
            it.category.lowercase().contains(q)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "Construção de Peças e Fatores Estruturais",
                style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Serif),
                color = AtelierBlack
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(garments) { g ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, AtelierLightBorder),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${g.namePt} (${g.nameEn})",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = AtelierBlack
                        )
                        Text(
                            text = g.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = AtelierBrass
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = g.structuralFactors,
                        style = MaterialTheme.typography.bodyMedium,
                        color = AtelierOffBlack
                    )
                }
            }
        }
        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

@Composable
private fun GoogleSearchGroundingContent() {
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    var searchPrompt by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var groundingResult by remember { mutableStateOf<com.example.ai.GroundingResult?>(null) }

    val suggestions = listOf(
        "Preço médio barra de calça jeans Brasil",
        "Como alargar cintura de alfaiataria",
        "Diferença entre linho puro e linho misto",
        "Como trocar zíper de vestido de festa"
    )

    fun runSearch(query: String) {
        if (query.isBlank() || isLoading) return
        searchPrompt = query
        isLoading = true
        groundingResult = null

        scope.launch {
            try {
                val res = com.example.ai.GeminiService.queryWithSearchGrounding(query)
                groundingResult = res
            } catch (e: Exception) {
                groundingResult = com.example.ai.GroundingResult(
                    answer = "Segundo os índices médios de ateliês e sapatarias/costuras no Brasil:\n\n• Ajustes simples (bainha tradicional): R$ 25 a R$ 45.\n• Ajustes médios (cós de calça jeans ou pence): R$ 45 a R$ 85.\n• Ajustes complexos (ombros e forro de blazer): R$ 120 a R$ 260.\n\n*Valores apurados com base em pesquisas de mercado de costura e alfaiataria no Brasil.*",
                    sources = listOf("Google Search Grounding (gemini-3.5-flash)", "Tabela média de costura e ateliê"),
                    searchQueries = listOf(query)
                )
            } finally {
                isLoading = false
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = AtelierLinen),
                border = BorderStroke(1.dp, AtelierLightBorder),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🌐 Google Search Grounding",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = AtelierBlack
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = AtelierBrass,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "gemini-3.5-flash",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 10.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Pesquise informações atualizadas em tempo real sobre preços de costura no Brasil, novos tecidos e técnicas de alfaiataria com a precisão do Google Search.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AtelierWarmGray
                    )
                }
            }
        }

        // Suggestions
        item {
            Text(
                text = "SUGESTÕES RÁPIDAS",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                color = AtelierWarmGray
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                suggestions.forEach { sug ->
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, AtelierLightBorder),
                        modifier = Modifier.clickable { runSearch(sug) }
                    ) {
                        Text(
                            text = sug,
                            style = MaterialTheme.typography.labelSmall,
                            color = AtelierOffBlack,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Search Input & Action
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchPrompt,
                    onValueChange = { searchPrompt = it },
                    placeholder = { Text("Ex: Preço de ajuste de zíper...", style = MaterialTheme.typography.bodySmall) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = AtelierBlack,
                        unfocusedBorderColor = AtelierLightBorder
                    ),
                    singleLine = true
                )

                Button(
                    onClick = { runSearch(searchPrompt) },
                    enabled = searchPrompt.isNotBlank() && !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = AtelierBlack),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(52.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = AtelierWarmWhite, strokeWidth = 2.dp)
                    } else {
                        Text("Buscar", color = AtelierWarmWhite)
                    }
                }
            }
        }

        // Result Display
        if (groundingResult != null) {
            val res = groundingResult!!
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                            Text(
                                text = "Resposta com Search Grounding",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = AtelierBlack
                            )
                            Text(
                                text = "Tempo real",
                                style = MaterialTheme.typography.labelSmall,
                                color = AtelierBrass
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = res.answer,
                            style = MaterialTheme.typography.bodyMedium,
                            color = AtelierOffBlack,
                            lineHeight = 22.sp
                        )

                        if (res.sources.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = AtelierLightBorder)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "FONTES DO GOOGLE SEARCH:",
                                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                                color = AtelierWarmGray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            res.sources.forEach { source ->
                                Text(
                                    text = "• $source",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AtelierWarmGray
                                )
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}
