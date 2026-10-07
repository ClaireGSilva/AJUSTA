package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.model.GarmentAnalysis
import com.example.model.RegionalPriceEstimate
import com.example.ui.theme.AtelierBlack
import com.example.ui.theme.AtelierBrass
import com.example.ui.theme.AtelierBrassContainer
import com.example.ui.theme.AtelierLightBorder
import com.example.ui.theme.AtelierLinen
import com.example.ui.theme.AtelierOffBlack
import com.example.ui.theme.AtelierSand
import com.example.ui.theme.AtelierWarmGray
import com.example.ui.theme.AtelierWarmWhite
import kotlinx.coroutines.launch

@Composable
fun RegionalPriceCalculatorCard(
    analysis: GarmentAnalysis,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var selectedCurrency by remember { mutableStateOf("BRL") }
    var regionInput by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var regionalEstimate by remember { mutableStateOf<RegionalPriceEstimate?>(null) }
    var isExpanded by remember { mutableStateOf(false) }

    val currencyOptions = listOf(
        Pair("BRL", "R$ Real"),
        Pair("EUR", "€ Euro"),
        Pair("USD", "$ Dólar"),
        Pair("GBP", "£ Libra")
    )

    val quickRegionSuggestions = listOf(
        "São Paulo - Jardins",
        "Rio de Janeiro - Zona Sul",
        "Curitiba - Centro",
        "Belo Horizonte - Savassi",
        "Interior / Cidade Pequena",
        "Lisboa (Portugal)",
        "Nova York (EUA)"
    )

    fun calculateEstimate(reg: String = regionInput, curr: String = selectedCurrency) {
        if (isLoading) return
        isLoading = true
        scope.launch {
            try {
                val res = GeminiService.estimateRegionalCost(
                    analysis = analysis,
                    region = reg,
                    currencyCode = curr
                )
                regionalEstimate = res
            } finally {
                isLoading = false
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("regional_price_calculator_card"),
        colors = CardDefaults.cardColors(containerColor = AtelierWarmWhite),
        border = BorderStroke(1.dp, AtelierBrass),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AtelierLinen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = null,
                            tint = AtelierBrass,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Estimativa Regional & Moeda",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = AtelierBlack
                        )
                        Text(
                            text = "Custo médio ajustado por IA",
                            style = MaterialTheme.typography.labelSmall,
                            color = AtelierWarmGray
                        )
                    }
                }

                Surface(
                    color = AtelierBrass,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "Gemini IA",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Os preços de ateliê variam significativamente por cidade, bairro e moeda. Informe sua localização para calcular o custo médio local:",
                style = MaterialTheme.typography.bodySmall,
                color = AtelierOffBlack,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Currency Selector Chips
            Text(
                text = "MOEDA DE REFERÊNCIA",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp, fontWeight = FontWeight.Bold),
                color = AtelierWarmGray
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                currencyOptions.forEach { (code, label) ->
                    val isSelected = selectedCurrency == code
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedCurrency = code
                            if (regionalEstimate != null) {
                                calculateEstimate(curr = code)
                            }
                        },
                        label = { Text(label, style = MaterialTheme.typography.labelMedium) },
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
                        modifier = Modifier.testTag("currency_chip_$code")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Region input field
            Text(
                text = "SUA REGIÃO, CIDADE OU BAIRRO",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp, fontWeight = FontWeight.Bold),
                color = AtelierWarmGray
            )
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = regionInput,
                onValueChange = { regionInput = it },
                placeholder = {
                    Text(
                        "Ex: São Paulo - Pinheiros, Curitiba, Lisboa...",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = AtelierBrass,
                        modifier = Modifier.size(18.dp)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("region_input_field"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = AtelierBlack,
                    unfocusedBorderColor = AtelierLightBorder
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Quick suggestion chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                quickRegionSuggestions.forEach { suggestion ->
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, AtelierLightBorder),
                        modifier = Modifier.testTag("quick_region_${suggestion.take(5)}")
                    ) {
                        Text(
                            text = suggestion,
                            style = MaterialTheme.typography.labelSmall,
                            color = AtelierBrass,
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .clickable {
                                    regionInput = suggestion
                                    if (suggestion.contains("EUR") || suggestion.contains("Lisboa")) {
                                        selectedCurrency = "EUR"
                                    } else if (suggestion.contains("EUA") || suggestion.contains("USD")) {
                                        selectedCurrency = "USD"
                                    }
                                    calculateEstimate(reg = suggestion)
                                }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Button
            Button(
                onClick = { calculateEstimate() },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("calculate_regional_cost_button"),
                colors = ButtonDefaults.buttonColors(containerColor = AtelierBlack),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = AtelierWarmWhite,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Consultando mercado regional...", color = AtelierWarmWhite)
                } else {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = AtelierBrass, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (regionalEstimate == null) "Calcular Estimativa Regional" else "Atualizar Estimativa",
                        color = AtelierWarmWhite,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }

            // Results Section
            if (regionalEstimate != null) {
                val est = regionalEstimate!!

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = AtelierLightBorder)
                Spacer(modifier = Modifier.height(16.dp))

                // Price Headline Card
                Surface(
                    color = AtelierLinen,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, AtelierBrass),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "FAIXA ESTIMADA EM ${est.region.uppercase()}",
                                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp, fontWeight = FontWeight.Bold),
                                color = AtelierWarmGray
                            )
                            Surface(
                                color = AtelierBrassContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = est.currencyCode,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = AtelierBrass,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = est.formattedRange,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = AtelierBlack
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "(Média: ${est.currencySymbol} ${est.averagePrice.toInt()})",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = AtelierBrass,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Schedule,
                                contentDescription = null,
                                tint = AtelierWarmGray,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Prazo típico na região: ${est.averageTurnaroundDays}",
                                style = MaterialTheme.typography.labelSmall,
                                color = AtelierOffBlack
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Regional Market Context
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, AtelierLightBorder),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "💡 Panorama do Mercado Local",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = AtelierBlack
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = est.regionalMarketContext,
                            style = MaterialTheme.typography.bodySmall,
                            color = AtelierOffBlack,
                            lineHeight = 19.sp
                        )

                        if (est.factorsImpactingPrice.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Fatores que impactam o valor nesta região:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = AtelierBrass
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            est.factorsImpactingPrice.forEach { factor ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                ) {
                                    Text("•", color = AtelierBrass, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = factor,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = AtelierWarmGray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
