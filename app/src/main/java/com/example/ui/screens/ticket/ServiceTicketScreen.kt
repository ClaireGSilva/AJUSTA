package com.example.ui.screens.ticket

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.model.GarmentAnalysis
import com.example.ui.components.AjustaTopBar
import com.example.ui.screens.flow.FlowViewModel
import com.example.ui.theme.AtelierBlack
import com.example.ui.theme.AtelierBrass
import com.example.ui.theme.AtelierBrassContainer
import com.example.ui.theme.AtelierLightBorder
import com.example.ui.theme.AtelierLinen
import com.example.ui.theme.AtelierOffBlack
import com.example.ui.theme.AtelierWarmGray
import com.example.ui.theme.AtelierWarmWhite

@Composable
fun ServiceTicketScreen(
    analysis: GarmentAnalysis,
    viewModel: FlowViewModel,
    onBack: () -> Unit,
    onFindTailor: () -> Unit = {}
) {
    val context = LocalContext.current
    var customNotes by remember { mutableStateOf("") }
    var isSaved by remember { mutableStateOf(false) }

    val primaryAlteration = analysis.alterationOptions.firstOrNull()
    val pointsToCheck = listOf(
        "Margem de costura interna original",
        "Estrutura do cós e presilhas de cinto",
        "Funcionamento e alinhamento do zíper",
        "Simetria e posicionamento de bolsos",
        "Tipo de acabamento e linha correspondente"
    )

    fun generateShareableSummary(): String {
        return buildString {
            appendLine("═══════════════════════════════════")
            appendLine("   AJUSTA — FICHA DE ALTERAÇÃO")
            appendLine("═══════════════════════════════════")
            appendLine("Peça: ${analysis.garmentType}")
            appendLine("Problema: ${analysis.problemSummary}")
            appendLine("Alteração provável: ${primaryAlteration?.name ?: analysis.possibleSolution}")
            appendLine("Complexidade: ${primaryAlteration?.complexity?.label ?: "Média"}")
            appendLine("Prova: ${if (primaryAlteration?.requiresFitting != false) "Recomendada" else "Opcional"}")
            appendLine("Estimativa de referência: ${analysis.priceEstimate.formattedRange}")
            appendLine("───────────────────────────────────")
            appendLine("PONTOS A VERIFICAR:")
            pointsToCheck.forEach { appendLine("• $it") }
            if (customNotes.isNotBlank()) {
                appendLine("───────────────────────────────────")
                appendLine("OBSERVAÇÕES DO CLIENTE:")
                appendLine(customNotes)
            }
            appendLine("───────────────────────────────────")
            appendLine("Gerado pelo Ajusta — Consultor de Caimento")
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AjustaTopBar(
                title = "Ficha do Serviço",
                subtitle = "Pronta para levar à costureira",
                onBackClick = onBack,
                actions = {
                    com.example.ui.components.ThemeToggleButton()
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Printable / Atelier Style Ticket Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("service_ticket_sheet"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(22.dp)) {
                    // Ticket Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "FICHA DE ALTERAÇÃO",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 2.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = AtelierBrass
                        )
                        Surface(
                            color = AtelierBrassContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "ATELIER",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = AtelierBrass,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Fields
                    TicketField(label = "Peça", value = analysis.garmentType)
                    TicketField(label = "Problema", value = analysis.problemSummary)
                    TicketField(
                        label = "Alteração provável",
                        value = primaryAlteration?.name ?: analysis.possibleSolution
                    )
                    TicketField(
                        label = "Complexidade",
                        value = primaryAlteration?.complexity?.label ?: "🟡 Média"
                    )
                    TicketField(
                        label = "Prova",
                        value = if (primaryAlteration?.requiresFitting != false) "Recomendada no corpo" else "Opcional"
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Pontos a verificar pela profissional:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    pointsToCheck.forEach { point ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(text = "•", color = AtelierBrass, modifier = Modifier.padding(end = 8.dp))
                            Text(
                                text = point,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Estimativa de referência:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = analysis.priceEstimate.formattedRange,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            ),
                            color = AtelierBrass
                        )
                    }
                }
            }

            // Client Extra Notes field
            Text(
                text = "Observações adicionais para a profissional:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            OutlinedTextField(
                value = customNotes,
                onValueChange = { customNotes = it },
                placeholder = {
                    Text(
                        "Ex: Preferência por manter o pesponto original ou usar linha escura...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp)
                    .testTag("ticket_custom_notes_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = AtelierBrass,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                )
            )

            // 3 Required Action Buttons: Salvar, Compartilhar, Copiar resumo
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Button 1: Salvar
                Button(
                    onClick = {
                        viewModel.saveCurrentAnalysis(ticketNotes = customNotes) {
                            isSaved = true
                            Toast.makeText(context, "Ficha salva com sucesso em 'Minhas Peças'!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("ticket_save_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Check else Icons.Default.Bookmark,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isSaved) "Ficha salva ✓" else "Salvar ficha",
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                // Button 2: Compartilhar
                OutlinedButton(
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, generateShareableSummary())
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Enviar Ficha do Ajusta")
                        context.startActivity(shareIntent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("ticket_share_button"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Compartilhar com costureira", color = MaterialTheme.colorScheme.onSurface)
                }

                // Button 3: Copiar resumo
                OutlinedButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Ficha Ajusta", generateShareableSummary())
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Resumo copiado para a área de transferência!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("ticket_copy_button"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Copiar resumo", color = MaterialTheme.colorScheme.onSurface)
                }

                // Button 4: Agendar Lembrete de Retirada
                OutlinedButton(
                    onClick = {
                        com.example.notification.NotificationHelper.scheduleReminder(
                            context = context,
                            title = "⏰ Ajusta: Lembrete de Reparo",
                            message = "Verifique o andamento do ajuste de ${analysis.garmentType} com o ateliê!",
                            delaySeconds = 3 * 24 * 3600L,
                            isTip = false
                        )
                        Toast.makeText(context, "Lembrete agendado para o prazo estimado de entrega (3 dias)!", Toast.LENGTH_LONG).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("ticket_schedule_reminder_button"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, AtelierBrass)
                ) {
                    Icon(imageVector = Icons.Default.Alarm, contentDescription = null, tint = AtelierBrass)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Agendar lembrete de retirada (3 dias)", color = MaterialTheme.colorScheme.onSurface)
                }

                // Button 5: Buscar costureira próxima com a ficha
                OutlinedButton(
                    onClick = onFindTailor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("ticket_find_tailor_button"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, AtelierBrass)
                ) {
                    Text("👩‍🔧 Encontrar ateliê para executar este serviço", color = AtelierBrass, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TicketField(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(130.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}
