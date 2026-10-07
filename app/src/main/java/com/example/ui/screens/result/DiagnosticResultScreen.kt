package com.example.ui.screens.result

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AlterationOption
import com.example.model.ComplexityLevel
import com.example.model.GarmentAnalysis
import com.example.model.RiskLevel
import com.example.ui.components.AjustaTopBar
import com.example.util.DiagnosisShareHelper
import com.example.ui.components.ConfidenceBadge
import com.example.ui.components.PriceRangeCard
import com.example.ui.components.RegionalPriceCalculatorCard
import com.example.ui.components.RiskBadge
import com.example.ui.components.StyleSuggestionCard
import com.example.ui.components.WorthItCard
import com.example.ui.screens.flow.FlowViewModel
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
import com.example.ui.theme.RiskHigh
import com.example.ui.theme.RiskHighContainer

@Composable
fun DiagnosticResultScreen(
    analysis: GarmentAnalysis,
    viewModel: FlowViewModel,
    onBack: () -> Unit,
    onCreateServiceTicket: () -> Unit,
    onFindTailors: () -> Unit = {}
) {
    val context = LocalContext.current
    var isSaved by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AjustaTopBar(
                title = "Seu diagnóstico",
                subtitle = analysis.garmentType,
                onBackClick = onBack,
                actions = {
                    com.example.ui.components.ThemeToggleButton()
                    IconButton(
                        onClick = {
                            DiagnosisShareHelper.shareDiagnosis(context, analysis)
                        },
                        modifier = Modifier.testTag("share_diagnosis_top_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Compartilhar diagnóstico",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    IconButton(
                        onClick = {
                            viewModel.saveCurrentAnalysis {
                                isSaved = true
                                Toast.makeText(context, "Diagnóstico salvo em Minhas Peças!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.testTag("save_analysis_button")
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Salvar",
                            tint = if (isSaved) AtelierBrass else MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header: Peça, Problema provável, Confiança
            item {
                Spacer(modifier = Modifier.height(4.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("diagnostic_header_card"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "PEÇA ANALISADA",
                                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            ConfidenceBadge(analysis.confidence)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = analysis.garmentType,
                            style = MaterialTheme.typography.headlineMedium.copy(fontFamily = FontFamily.Serif),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "PROBLEMA PROVÁVEL",
                            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = analysis.problemSummary,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // O que observamos
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "O que observamos",
                            style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.Serif),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = analysis.whatWeObserved,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 23.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "O que provavelmente está acontecendo:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = analysis.probableCause,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Possível solução
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Possível solução",
                            style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.Serif),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = analysis.possibleSolution,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 23.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "O que precisa ser confirmado presencialmente:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = AtelierBrass
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = analysis.whatNeedsConfirmation,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // ⚠️ Antes de alterar (Limitações)
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.WarningAmber,
                            contentDescription = null,
                            tint = AtelierTerracotta,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "⚠️ Antes de alterar",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = analysis.beforeYouAlterLimitations,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            // High Risk Banner (if applicable)
            if (analysis.riskAssessment.level == RiskLevel.HIGH) {
                item {
                    Surface(
                        color = RiskHighContainer,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, RiskHigh.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = RiskHigh,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "« Recomendamos avaliação presencial de uma profissional antes de realizar qualquer alteração. »",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif
                                ),
                                color = RiskHigh,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            // Possíveis Ajustes (Cards)
            item {
                Text(
                    text = "POSSÍVEIS AJUSTES",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                    color = AtelierWarmGray
                )
                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    analysis.alterationOptions.forEach { opt ->
                        AlterationOptionCard(option = opt)
                    }
                }
            }

            // Price Range Card (Engine-driven)
            item {
                PriceRangeCard(estimate = analysis.priceEstimate)
            }

            // Estimativa de Custo Regional & Moeda com IA (Gemini)
            item {
                RegionalPriceCalculatorCard(analysis = analysis)
            }

            // Vale a pena ajustar? ("Antes de decidir")
            item {
                WorthItCard(recommendation = analysis.recommendation)
            }

            // Consultoria de Estilo, Customização & Acessórios (Gemini)
            item {
                StyleSuggestionCard(analysis = analysis)
            }

            // Primary Actions: "Criar ficha do serviço" e "Procurar profissional próximo"
            item {
                Column(
                    modifier = Modifier.padding(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onCreateServiceTicket,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("create_service_ticket_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = AtelierBrass
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Criar ficha do serviço",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            DiagnosisShareHelper.shareDiagnosis(context, analysis)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("share_diagnosis_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, AtelierBrass)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = AtelierBrass
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Compartilhar diagnóstico",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    OutlinedButton(
                        onClick = onFindTailors,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("result_find_tailor_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Text(
                            text = "👩‍🔧 Encontrar profissional próximo",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun AlterationOptionCard(option: AlterationOption) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("alteration_card_${option.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = option.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = option.complexity.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(option.complexity.iconColorHex)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = option.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            if (option.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Nota: ${option.notes}",
                    style = MaterialTheme.typography.bodySmall,
                    color = AtelierWarmGray
                )
            }

            if (option.possibleRisks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        text = "Riscos: ",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = AtelierTerracotta
                    )
                    Text(
                        text = option.possibleRisks.joinToString("; "),
                        style = MaterialTheme.typography.labelSmall,
                        color = AtelierOffBlack
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (option.requiresFitting) "Prova: Recomendada" else "Prova: Opcional",
                    style = MaterialTheme.typography.labelSmall,
                    color = AtelierBrass
                )
            }
        }
    }
}
