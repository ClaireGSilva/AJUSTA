package com.example.ui.screens.flow

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Composable
fun Step4AnalysisScreen(
    viewModel: FlowViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        if (uiState is AnalysisUiState.Idle) {
            viewModel.startAnalysis()
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is AnalysisUiState.Success) {
            onSuccess()
        }
    }

    Scaffold(
        containerColor = AtelierWarmWhite,
        topBar = {
            AjustaTopBar(
                title = "Análise Técnica",
                subtitle = "Consultoria de caimento",
                onBackClick = onBack
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is AnalysisUiState.Loading, is AnalysisUiState.Idle -> {
                    LoadingView()
                }

                is AnalysisUiState.LowConfidence -> {
                    LowConfidenceView(
                        onProceedAnyway = onSuccess,
                        onRetry = { viewModel.startAnalysis() }
                    )
                }

                is AnalysisUiState.Error -> {
                    ErrorView(
                        message = state.message,
                        onRetry = { viewModel.startAnalysis() }
                    )
                }

                is AnalysisUiState.Success -> {
                    // Redirected to diagnostic screen via LaunchedEffect
                    LoadingView()
                }
            }
        }
    }
}

@Composable
private fun LoadingView() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.testTag("analysis_loading_view")
    ) {
        Box(
            modifier = Modifier
                .size(110.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(AtelierLinen),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(90.dp),
                color = AtelierBrass,
                strokeWidth = 2.dp
            )
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = AtelierBrass,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Analisando sua peça…",
            style = MaterialTheme.typography.headlineMedium.copy(fontFamily = FontFamily.Serif),
            color = AtelierBlack,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Avaliando linhas de tensão, proporções de modelagem, compatibilidade do tecido e tabela de alfaiataria.",
            style = MaterialTheme.typography.bodyMedium,
            color = AtelierWarmGray,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Atelier progressive steps card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AtelierLinen),
            border = BorderStroke(1.dp, AtelierLightBorder),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                LoadingStepItem(text = "1. Inspecionando proporções e caimento geral")
                LoadingStepItem(text = "2. Identificando pontos de tensão e linhas de costura")
                LoadingStepItem(text = "3. Consultando tabela técnica de alfaiataria")
                LoadingStepItem(text = "4. Calculando estimativa independente de custo")
            }
        }
    }
}

@Composable
private fun LoadingStepItem(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(AtelierBrass)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = AtelierOffBlack
        )
    }
}

@Composable
private fun LowConfidenceView(
    onProceedAnyway: () -> Unit,
    onRetry: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.testTag("analysis_low_confidence_view")
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(AtelierBrassContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = AtelierBrass,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Precisamos de mais informações para analisar melhor",
            style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.Serif),
            color = AtelierBlack,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "A foto fornecida não revelou nitidamente o ponto exato da costura ou da sobra. Geramos uma análise preliminar baseada na descrição informada.",
            style = MaterialTheme.typography.bodyMedium,
            color = AtelierWarmGray,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onProceedAnyway,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("low_confidence_view_result_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AtelierBlack)
        ) {
            Text("Ver diagnóstico preliminar", color = AtelierWarmWhite)
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("low_confidence_retry_button"),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.size(8.dp))
            Text("Tentar novamente", color = AtelierBlack)
        }
    }
}

@Composable
private fun ErrorView(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.testTag("analysis_error_view")
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(AtelierLinen),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = AtelierTerracotta,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Não conseguimos analisar essa imagem",
            style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.Serif),
            color = AtelierBlack,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Ocorreu uma instabilidade na conexão com o serviço de visão. Você pode tentar novamente para obter seu diagnóstico.",
            style = MaterialTheme.typography.bodyMedium,
            color = AtelierWarmGray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("error_retry_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AtelierBlack)
        ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = AtelierWarmWhite)
            Spacer(modifier = Modifier.size(8.dp))
            Text("Tentar novamente", color = AtelierWarmWhite)
        }
    }
}
