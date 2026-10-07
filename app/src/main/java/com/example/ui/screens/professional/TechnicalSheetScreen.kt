package com.example.ui.screens.professional

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ProfessionalOrder
import com.example.ui.components.AjustaTopBar
import com.example.ui.theme.AtelierBlack
import com.example.ui.theme.AtelierBrass
import com.example.ui.theme.AtelierBrassContainer
import com.example.ui.theme.AtelierLightBorder
import com.example.ui.theme.AtelierLinen
import com.example.ui.theme.AtelierOffBlack
import com.example.ui.theme.AtelierWarmGray
import com.example.ui.theme.AtelierWarmWhite

@Composable
fun TechnicalSheetScreen(
    order: ProfessionalOrder,
    viewModel: ProfessionalViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    var editableDiagnosis by remember { mutableStateOf(order.preliminaryDiagnosis) }
    var editableMaterials by remember { mutableStateOf(order.materialsNeeded) }
    var editableComplexity by remember { mutableStateOf(order.complexity) }
    var editableSteps by remember { mutableStateOf(order.procedureSteps.joinToString("\n")) }
    var editableAttention by remember { mutableStateOf(order.attentionPoints.joinToString("\n")) }

    fun buildTechnicalSummary(): String {
        return buildString {
            appendLine("═════════════════════════════════════════")
            appendLine("      FICHA TÉCNICA DE ALFAIATARIA")
            appendLine("              ATELIER AJUSTA")
            appendLine("═════════════════════════════════════════")
            appendLine("Atendimento: #${order.id.take(8)}")
            appendLine("Cliente: ${order.clientName} | Tel: ${order.clientPhone}")
            appendLine("Peça: ${order.garmentType}")
            appendLine("Solicitação: ${order.clientRequest}")
            appendLine("Valor Acordado: ${order.agreedPrice?.let { "R$ $it" } ?: "A definir"}")
            appendLine("─────────────────────────────────────────")
            appendLine("DIAGNÓSTICO PRELIMINAR:")
            appendLine(editableDiagnosis)
            appendLine("─────────────────────────────────────────")
            appendLine("PROCEDIMENTO:")
            appendLine(editableSteps)
            appendLine("─────────────────────────────────────────")
            appendLine("MATERIAIS NECESSÁRIOS:")
            appendLine(editableMaterials)
            appendLine("─────────────────────────────────────────")
            appendLine("COMPLEXIDADE: $editableComplexity")
            appendLine("PONTOS DE ATENÇÃO:")
            appendLine(editableAttention)
            if (order.measurements.isNotEmpty()) {
                appendLine("─────────────────────────────────────────")
                appendLine("MEDIDAS REGISTRADAS:")
                order.measurements.forEach { (k, v) -> appendLine("• $k: $v") }
            }
            appendLine("═════════════════════════════════════════")
        }
    }

    Scaffold(
        containerColor = AtelierWarmWhite,
        topBar = {
            AjustaTopBar(
                title = "Ficha Técnica Profissional",
                subtitle = "${order.clientName} • ${order.garmentType}",
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Card principal de identificação
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("technical_sheet_card"),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, AtelierLightBorder),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "FICHA TÉCNICA",
                            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp, fontWeight = FontWeight.Bold),
                            color = AtelierBrass
                        )
                        Surface(
                            color = AtelierBrassContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "ORDEM #${order.id.take(6).uppercase()}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = AtelierBrass,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = AtelierLightBorder)
                    Spacer(modifier = Modifier.height(12.dp))

                    TechRow("Cliente", order.clientName)
                    TechRow("Telefone", order.clientPhone.ifBlank { "Não informado" })
                    TechRow("Peça", order.garmentType)
                    TechRow("Solicitação", order.clientRequest)
                    if (order.agreedPrice != null) {
                        TechRow("Valor Acordado", "R$ ${order.agreedPrice.toInt()}")
                    }

                    if (order.measurements.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Medidas:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = AtelierWarmGray
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            order.measurements.forEach { (name, value) ->
                                Text(
                                    text = "$name: $value",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AtelierBlack
                                )
                            }
                        }
                    }
                }
            }

            // Diagnóstico preliminar (Editável)
            Text(
                text = "Diagnóstico Preliminar (Editável):",
                style = MaterialTheme.typography.titleSmall,
                color = AtelierBlack
            )
            OutlinedTextField(
                value = editableDiagnosis,
                onValueChange = { editableDiagnosis = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tech_sheet_diagnosis_input"),
                shape = RoundedCornerShape(12.dp),
                colors = fieldColors()
            )

            // Procedimento provável (8 passos editáveis)
            Text(
                text = "Procedimento Provável (8 Etapas de Ateliê):",
                style = MaterialTheme.typography.titleSmall,
                color = AtelierBlack
            )
            OutlinedTextField(
                value = editableSteps,
                onValueChange = { editableSteps = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .testTag("tech_sheet_steps_input"),
                shape = RoundedCornerShape(12.dp),
                colors = fieldColors()
            )

            // Materiais
            Text(
                text = "Materiais Necessários:",
                style = MaterialTheme.typography.titleSmall,
                color = AtelierBlack
            )
            OutlinedTextField(
                value = editableMaterials,
                onValueChange = { editableMaterials = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tech_sheet_materials_input"),
                shape = RoundedCornerShape(12.dp),
                colors = fieldColors()
            )

            // Complexidade
            Text(
                text = "Complexidade:",
                style = MaterialTheme.typography.titleSmall,
                color = AtelierBlack
            )
            OutlinedTextField(
                value = editableComplexity,
                onValueChange = { editableComplexity = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tech_sheet_complexity_input"),
                shape = RoundedCornerShape(12.dp),
                colors = fieldColors()
            )

            // Pontos de atenção
            Text(
                text = "Pontos de Atenção & Riscos:",
                style = MaterialTheme.typography.titleSmall,
                color = AtelierBlack
            )
            OutlinedTextField(
                value = editableAttention,
                onValueChange = { editableAttention = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .testTag("tech_sheet_attention_input"),
                shape = RoundedCornerShape(12.dp),
                colors = fieldColors()
            )

            // Botões de ação: Salvar alterações, Compartilhar, Copiar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        val updated = order.copy(
                            preliminaryDiagnosis = editableDiagnosis,
                            procedureSteps = editableSteps.split("\n").filter { it.isNotBlank() },
                            materialsNeeded = editableMaterials,
                            complexity = editableComplexity,
                            attentionPoints = editableAttention.split("\n").filter { it.isNotBlank() }
                        )
                        viewModel.updateOrder(updated)
                        Toast.makeText(context, "Ficha técnica atualizada no sistema!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("tech_sheet_save_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AtelierBlack)
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = null, tint = AtelierWarmWhite)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Salvar alterações técnicas", color = AtelierWarmWhite)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, buildTechnicalSummary())
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Enviar Ficha Técnica"))
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("tech_sheet_share_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, AtelierBlack)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = AtelierBlack)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Compartilhar", color = AtelierBlack)
                    }

                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Ficha Técnica Ajusta", buildTechnicalSummary())
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Ficha técnica copiada!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("tech_sheet_copy_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, AtelierLightBorder)
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, tint = AtelierWarmGray)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copiar", color = AtelierOffBlack)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TechRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = AtelierWarmGray)
        Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium), color = AtelierBlack)
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    focusedBorderColor = AtelierBrass,
    unfocusedBorderColor = AtelierLightBorder
)
