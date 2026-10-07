package com.example.ui.screens.flow

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AjustaTopBar
import com.example.ui.components.StepIndicator
import com.example.ui.theme.AtelierBlack
import com.example.ui.theme.AtelierBrass
import com.example.ui.theme.AtelierLightBorder
import com.example.ui.theme.AtelierLinen
import com.example.ui.theme.AtelierOffBlack
import com.example.ui.theme.AtelierWarmGray
import com.example.ui.theme.AtelierWarmWhite

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Step3ContextScreen(
    viewModel: FlowViewModel,
    onBack: () -> Unit,
    onStartAnalysis: () -> Unit
) {
    val selectedFabric by viewModel.selectedFabric.collectAsState()
    val selectedPricePaid by viewModel.selectedPricePaid.collectAsState()

    val fabricOptions = listOf(
        "Algodão",
        "Jeans",
        "Linho",
        "Viscose",
        "Seda",
        "Lã",
        "Malha",
        "Elastano",
        "Outro",
        "Não sei"
    )

    val priceOptions = listOf(
        Pair("Até R$50", 40.0),
        Pair("R$50–100", 75.0),
        Pair("R$100–250", 175.0),
        Pair("R$250–500", 350.0),
        Pair("R$500+", 600.0),
        Pair("Prefiro não informar", null)
    )

    Scaffold(
        containerColor = AtelierWarmWhite,
        topBar = {
            AjustaTopBar(
                title = "Etapa 3 de 4",
                subtitle = "Contexto opcional",
                onBackClick = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                StepIndicator(currentStep = 3, totalSteps = 4)

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Contexto da peça",
                    style = MaterialTheme.typography.headlineMedium.copy(fontFamily = FontFamily.Serif),
                    color = AtelierBlack
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Estas informações ajudam a refinar a estimativa de preço e a avaliação de risco do tecido. Ambas são opcionais.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AtelierWarmGray
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Question 1: Fabric
                Text(
                    text = "Você sabe qual é o tecido?",
                    style = MaterialTheme.typography.titleSmall,
                    color = AtelierBlack
                )

                Spacer(modifier = Modifier.height(10.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    fabricOptions.forEach { fabric ->
                        val isSelected = (selectedFabric == fabric)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                viewModel.setFabric(if (isSelected) null else fabric)
                            },
                            label = {
                                Text(
                                    text = fabric,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isSelected) AtelierWarmWhite else AtelierOffBlack
                                )
                            },
                            leadingIcon = if (isSelected) {
                                {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = AtelierWarmWhite,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = AtelierLinen,
                                selectedContainerColor = AtelierBlack
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) AtelierBlack else AtelierLightBorder
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("fabric_chip_$fabric")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Question 2: Price paid
                Text(
                    text = "Quanto você pagou aproximadamente?",
                    style = MaterialTheme.typography.titleSmall,
                    color = AtelierBlack
                )
                Text(
                    text = "Usado apenas para avaliar a relação de custo-benefício.",
                    style = MaterialTheme.typography.bodySmall,
                    color = AtelierWarmGray
                )

                Spacer(modifier = Modifier.height(10.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    priceOptions.forEach { (label, value) ->
                        val isSelected = (selectedPricePaid == value)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                viewModel.setPricePaid(value)
                            },
                            label = {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isSelected) AtelierWarmWhite else AtelierOffBlack
                                )
                            },
                            leadingIcon = if (isSelected) {
                                {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = AtelierWarmWhite,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = AtelierLinen,
                                selectedContainerColor = AtelierBlack
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) AtelierBlack else AtelierLightBorder
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("price_chip_$label")
                        )
                    }
                }
            }

            // Bottom CTA
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
            ) {
                Button(
                    onClick = onStartAnalysis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("step3_analyze_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AtelierBlack)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = AtelierBrass
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "Analisar peça",
                        style = MaterialTheme.typography.titleSmall,
                        color = AtelierWarmWhite
                    )
                }
            }
        }
    }
}
