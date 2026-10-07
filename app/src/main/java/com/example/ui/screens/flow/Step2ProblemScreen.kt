package com.example.ui.screens.flow

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AjustaTopBar
import com.example.ui.components.StepIndicator
import com.example.ui.theme.AtelierBlack
import com.example.ui.theme.AtelierBrass
import com.example.ui.theme.AtelierBrassContainer
import com.example.ui.theme.AtelierLightBorder
import com.example.ui.theme.AtelierLinen
import com.example.ui.theme.AtelierOffBlack
import com.example.ui.theme.AtelierWarmGray
import com.example.ui.theme.AtelierWarmWhite
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Step2ProblemScreen(
    viewModel: FlowViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val context = LocalContext.current
    val currentCategory by viewModel.problemCategory.collectAsState()
    val currentDescription by viewModel.problemDescription.collectAsState()

    val problemOptions = listOf(
        "Ficou larga",
        "Ficou apertada",
        "Ficou comprida",
        "Ficou curta",
        "Não veste bem",
        "Preciso reparar",
        "Quero mudar o modelo",
        "Outro"
    )

    // Voice recognition launcher
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                val newDesc = if (currentDescription.isNotBlank()) {
                    "$currentDescription $spokenText"
                } else {
                    spokenText
                }
                viewModel.setProblemDescription(newDesc)
            }
        }
    }

    Scaffold(
        containerColor = AtelierWarmWhite,
        topBar = {
            AjustaTopBar(
                title = "Etapa 2 de 4",
                subtitle = "Identificação do problema",
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
                StepIndicator(currentStep = 2, totalSteps = 4)

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "O que está acontecendo?",
                    style = MaterialTheme.typography.headlineMedium.copy(fontFamily = FontFamily.Serif),
                    color = AtelierBlack
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Selecione a situação principal e sinta-se à vontade para detalhar com suas palavras ou por voz.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AtelierWarmGray
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Chips of problems
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    problemOptions.forEach { option ->
                        val isSelected = (currentCategory == option)
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setProblemCategory(option) },
                            label = {
                                Text(
                                    text = option,
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
                            modifier = Modifier.testTag("problem_chip_$option")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Free Text Input
                Text(
                    text = "Descreva com mais detalhes (opcional):",
                    style = MaterialTheme.typography.labelMedium,
                    color = AtelierBlack
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = currentDescription,
                    onValueChange = { viewModel.setProblemDescription(it) },
                    placeholder = {
                        Text(
                            text = "Ex: Sobrando tecido na cintura atrás, ou repuxando perto do zíper...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AtelierWarmGray
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .testTag("step2_description_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = AtelierWarmWhite,
                        unfocusedContainerColor = AtelierWarmWhite,
                        focusedBorderColor = AtelierBrass,
                        unfocusedBorderColor = AtelierLightBorder,
                        cursorColor = AtelierBlack
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Microphone voice input button
                OutlinedButton(
                    onClick = {
                        try {
                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(
                                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                                )
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.forLanguageTag("pt-BR"))
                                putExtra(RecognizerIntent.EXTRA_PROMPT, "Descreva o que está acontecendo com a roupa...")
                            }
                            speechLauncher.launch(intent)
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "Reconhecimento de voz indisponível no dispositivo. Digite no campo acima.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("step2_voice_button"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, AtelierBrass),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AtelierBrass)
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = null,
                        tint = AtelierBrass,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🎙️ Descrever por voz",
                        style = MaterialTheme.typography.labelLarge,
                        color = AtelierBrass
                    )
                }
            }

            // Bottom CTA
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
            ) {
                Button(
                    onClick = onNext,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("step2_continue_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AtelierBlack)
                ) {
                    Text(
                        text = "Avançar para contexto →",
                        style = MaterialTheme.typography.titleSmall,
                        color = AtelierWarmWhite
                    )
                }
            }
        }
    }
}
