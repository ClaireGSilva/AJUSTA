package com.example.ui.screens.flow

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.Lightbulb
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.components.AjustaTopBar
import com.example.ui.components.StepIndicator
import com.example.ui.theme.AtelierBlack
import com.example.ui.theme.AtelierBrass
import com.example.ui.theme.AtelierBrassContainer
import com.example.ui.theme.AtelierLightBorder
import com.example.ui.theme.AtelierLinen
import com.example.ui.theme.AtelierOffBlack
import com.example.ui.theme.AtelierSand
import com.example.ui.theme.AtelierWarmGray
import com.example.ui.theme.AtelierWarmWhite

@Composable
fun Step1PhotoScreen(
    viewModel: FlowViewModel,
    onOpenLiveCamera: () -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val photoUris by viewModel.selectedPhotoUris.collectAsState()

    // Gallery Picker
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        uris.forEach { viewModel.addPhotoUri(it) }
    }

    val slots = listOf(
        Pair("Frente", "Caimento geral"),
        Pair("Costas", "Linha traseira"),
        Pair("Detalhe", "Ponto de ajuste")
    )

    Scaffold(
        containerColor = AtelierWarmWhite,
        topBar = {
            AjustaTopBar(
                title = "Etapa 1 de 4",
                subtitle = "Registro visual da peça",
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
                StepIndicator(currentStep = 1, totalSteps = 4)

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Mostre a peça",
                    style = MaterialTheme.typography.headlineMedium.copy(fontFamily = FontFamily.Serif),
                    color = AtelierBlack
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Fotografe a frente da roupa, as costas ou o ponto exato que precisa de ajuste. Você pode adicionar até 3 fotos.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AtelierWarmGray,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Primary Actions: Câmera CameraX & Galeria
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onOpenLiveCamera,
                        modifier = Modifier
                            .weight(1.3f)
                            .height(54.dp)
                            .testTag("step1_camerax_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AtelierBlack)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = AtelierBrass,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Abrir Câmera",
                            style = MaterialTheme.typography.titleSmall,
                            color = AtelierWarmWhite
                        )
                    }

                    OutlinedButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                            .testTag("step1_gallery_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, AtelierLightBorder),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AtelierBlack)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = null,
                            tint = AtelierBlack,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Galeria")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 3 Guided Visual Angle Slots (Frente, Costas, Detalhe)
                Text(
                    text = "ÂNGULOS RECOMENDADOS DE ATELIER",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                    color = AtelierWarmGray
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    slots.forEachIndexed { index, (slotName, slotSubtitle) ->
                        val uri = photoUris.getOrNull(index)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(160.dp)
                        ) {
                            if (uri != null) {
                                // Captured Slot
                                Card(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(14.dp)),
                                    border = BorderStroke(1.5.dp, AtelierBrass),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        AsyncImage(
                                            model = uri,
                                            contentDescription = slotName,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )

                                        // Badge
                                        Surface(
                                            color = AtelierBlack.copy(alpha = 0.7f),
                                            shape = RoundedCornerShape(bottomEnd = 8.dp),
                                            modifier = Modifier.align(Alignment.TopStart)
                                        ) {
                                            Text(
                                                text = slotName,
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = AtelierWarmWhite,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }

                                        // Remove Button
                                        IconButton(
                                            onClick = { viewModel.removePhotoUri(uri) },
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(4.dp)
                                                .size(24.dp)
                                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Remover",
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            } else {
                                // Empty Slot
                                Card(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable { onOpenLiveCamera() }
                                        .testTag("photo_slot_$index"),
                                    colors = CardDefaults.cardColors(containerColor = AtelierLinen),
                                    border = BorderStroke(1.dp, AtelierLightBorder),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(8.dp),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(AtelierBrassContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = null,
                                                tint = AtelierBrass,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = slotName,
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = AtelierBlack
                                        )

                                        Text(
                                            text = slotSubtitle,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = AtelierWarmGray,
                                            fontSize = 10.sp,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Guidance box
                Surface(
                    color = AtelierLinen,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, AtelierLightBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Lightbulb,
                            contentDescription = null,
                            tint = AtelierBrass,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "« Fotos claras ajudam a IA a entender melhor a peça. Para problemas de caimento, uma foto usando a roupa pode ajudar. »",
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Serif),
                            color = AtelierOffBlack,
                            lineHeight = 20.sp
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
                    onClick = onNext,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("step1_continue_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AtelierBlack)
                ) {
                    Text(
                        text = if (photoUris.isEmpty()) "Continuar sem fotos" else "Continuar para o problema →",
                        style = MaterialTheme.typography.titleSmall,
                        color = AtelierWarmWhite
                    )
                }
            }
        }
    }
}
