package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Watch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.model.StyleCustomizationSuggestion
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
fun StyleSuggestionCard(
    analysis: GarmentAnalysis,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var suggestion by remember { mutableStateOf<StyleCustomizationSuggestion?>(null) }
    var hasRequested by remember { mutableStateOf(false) }

    fun loadStyleSuggestions() {
        if (isLoading) return
        isLoading = true
        hasRequested = true
        scope.launch {
            try {
                val res = GeminiService.generateStyleAndCustomization(analysis)
                suggestion = res
            } finally {
                isLoading = false
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("style_suggestion_card"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(AtelierLinen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = AtelierBrass,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Estilo & Customização",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = AtelierBlack
                        )
                        Text(
                            text = "Consultoria de moda & combinações com Gemini",
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
                        text = "gemini-3.5-flash",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (!hasRequested && suggestion == null) {
                Text(
                    text = "Descubra como combinar esta peça ajustada com acessórios (sapatos, cintos, joias) e ideias criativas de customização para multiplicar os looks do seu guarda-roupa.",
                    style = MaterialTheme.typography.bodySmall,
                    color = AtelierOffBlack,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { loadStyleSuggestions() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("generate_style_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = AtelierBlack),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = AtelierBrass, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Gerar Sugestões de Estilo & Acessórios",
                        style = MaterialTheme.typography.labelMedium,
                        color = AtelierWarmWhite
                    )
                }
            } else if (isLoading) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = AtelierBrass,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Consultora de estilo criando propostas...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AtelierWarmGray
                    )
                }
            } else if (suggestion != null) {
                val data = suggestion!!

                // Style Concept
                Surface(
                    color = AtelierLinen,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, AtelierLightBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "SILHUETA & CONCEITO DE ESTILO",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                            color = AtelierBrass
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = data.styleConcept,
                            style = MaterialTheme.typography.bodyMedium,
                            color = AtelierOffBlack,
                            lineHeight = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Accessory Combinations
                Text(
                    text = "COMBINAÇÕES COM ACESSÓRIOS",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp, fontWeight = FontWeight.Bold),
                    color = AtelierWarmGray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    data.accessoryCombinations.forEach { acc ->
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, AtelierLightBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Surface(
                                    color = AtelierBrassContainer,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = acc.category.uppercase(),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = AtelierBrass,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        fontSize = 10.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = acc.itemDescription,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium),
                                        color = AtelierBlack
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "💡 ${acc.tip}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = AtelierWarmGray,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Customization Ideas (Upcycling)
                Text(
                    text = "IDEIAS DE CUSTOMIZAÇÃO & PERSONALIZAÇÃO",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp, fontWeight = FontWeight.Bold),
                    color = AtelierWarmGray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    data.customizationIdeas.forEach { idea ->
                        Surface(
                            color = AtelierWarmWhite,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, AtelierLightBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "✂️ ${idea.title}",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = AtelierBlack
                                    )
                                    Text(
                                        text = idea.difficulty,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AtelierWarmGray
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = idea.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AtelierOffBlack,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Wardrobe Looks
                Text(
                    text = "LOOKS COMPLETOS COM SEU GUARDA-ROUPA",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp, fontWeight = FontWeight.Bold),
                    color = AtelierWarmGray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    data.wardrobeLooks.forEach { look ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, AtelierLightBorder),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = look.occasion,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = AtelierBrass
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = look.pairingDescription,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = AtelierBlack,
                                    lineHeight = 20.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Por que funciona: ${look.whyItWorks}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AtelierWarmGray,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }
                }

                if (data.colorPalette.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "PALETA DE CORES COMPLEMENTARES",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp, fontWeight = FontWeight.Bold),
                        color = AtelierWarmGray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        data.colorPalette.forEach { colorName ->
                            Surface(
                                color = AtelierLinen,
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, AtelierLightBorder)
                            ) {
                                Text(
                                    text = colorName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AtelierOffBlack,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedButton(
                    onClick = { loadStyleSuggestions() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, AtelierLightBorder)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = AtelierBlack, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Gerar Nova Proposta de Estilo", color = AtelierBlack, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}
