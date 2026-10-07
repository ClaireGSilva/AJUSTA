package com.example.ui.screens.camera

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.camera.CameraXManager
import com.example.ui.theme.AtelierBlack
import com.example.ui.theme.AtelierBrass
import com.example.ui.theme.AtelierBrassContainer
import com.example.ui.theme.AtelierLightBorder
import com.example.ui.theme.AtelierLinen
import com.example.ui.theme.AtelierOffBlack
import com.example.ui.theme.AtelierWarmGray
import com.example.ui.theme.AtelierWarmWhite

@Composable
fun CameraCaptureScreen(
    currentPhotoCount: Int = 0,
    maxPhotos: Int = 3,
    onPhotoCaptured: (Uri) -> Unit,
    onDone: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
        if (!granted) {
            Toast.makeText(context, "Permissão de câmera é necessária para fotografar.", Toast.LENGTH_SHORT).show()
        }
    }

    // Gallery Picker as fallback or option inside camera
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        uris.forEach { onPhotoCaptured(it) }
        if (uris.isNotEmpty()) onDone()
    }

    if (!hasCameraPermission) {
        CameraPermissionRationaleView(
            onRequestPermission = { permissionLauncher.launch(Manifest.permission.CAMERA) },
            onPickFromGallery = { galleryLauncher.launch("image/*") },
            onBack = onBack
        )
    } else {
        CameraViewfinder(
            currentPhotoCount = currentPhotoCount,
            maxPhotos = maxPhotos,
            onPhotoCaptured = onPhotoCaptured,
            onOpenGallery = { galleryLauncher.launch("image/*") },
            onDone = onDone,
            onBack = onBack
        )
    }
}

@Composable
private fun CameraPermissionRationaleView(
    onRequestPermission: () -> Unit,
    onPickFromGallery: () -> Unit,
    onBack: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AtelierWarmWhite
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Fechar")
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(AtelierLinen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = AtelierBrass,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Text(
                    text = "Acesso à Câmera",
                    style = MaterialTheme.typography.headlineMedium.copy(fontFamily = FontFamily.Serif),
                    color = AtelierBlack,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Para que a consultoria do Ajusta possa inspecionar linhas de tensão, caimento e detalhes da costura, autorize o acesso à câmera.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AtelierWarmGray,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Surface(
                    color = AtelierLinen,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, AtelierLightBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "« As imagens são usadas estritamente para o diagnóstico técnico de alfaiataria no seu dispositivo. »",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Serif,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        ),
                        color = AtelierOffBlack,
                        modifier = Modifier.padding(14.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onRequestPermission,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("camera_grant_permission_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AtelierBlack)
                ) {
                    Text("Autorizar Câmera", color = AtelierWarmWhite)
                }

                OutlinedButton(
                    onClick = onPickFromGallery,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("camera_fallback_gallery_button"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, AtelierBlack)
                ) {
                    Text("Selecionar da Galeria", color = AtelierBlack)
                }
            }
        }
    }
}

@Composable
private fun CameraViewfinder(
    currentPhotoCount: Int,
    maxPhotos: Int,
    onPhotoCaptured: (Uri) -> Unit,
    onOpenGallery: () -> Unit,
    onDone: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    var flashMode by remember { mutableIntStateOf(ImageCapture.FLASH_MODE_AUTO) }
    var capturedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var capturedPhotoBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isCapturing by remember { mutableStateOf(false) }
    var sessionPhotoCount by remember { mutableIntStateOf(currentPhotoCount) }

    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setFlashMode(flashMode)
            .build()
    }

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var camera by remember { mutableStateOf<Camera?>(null) }

    val previewView = remember {
        PreviewView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    // Bind Camera Lifecycle
    LaunchedEffect(lensFacing, flashMode) {
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build().also {
            it.surfaceProvider = previewView.surfaceProvider
        }
        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        imageCapture.flashMode = flashMode

        try {
            cameraProvider.unbindAll()
            camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (e: Exception) {
            Toast.makeText(context, "Erro ao iniciar câmera: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // CameraX Live Preview View
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        // Fashion Framing Overlay Guide
        FramingOverlayGuide(
            photoIndex = sessionPhotoCount,
            modifier = Modifier.fillMaxSize()
        )

        // Top Control Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(44.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color.White
                )
            }

            Surface(
                color = Color.Black.copy(alpha = 0.6f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Foto ${sessionPhotoCount + 1} de $maxPhotos",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = AtelierWarmWhite,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Flash Toggle
                IconButton(
                    onClick = {
                        flashMode = when (flashMode) {
                            ImageCapture.FLASH_MODE_AUTO -> ImageCapture.FLASH_MODE_ON
                            ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_OFF
                            else -> ImageCapture.FLASH_MODE_AUTO
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = when (flashMode) {
                            ImageCapture.FLASH_MODE_ON -> Icons.Default.FlashOn
                            ImageCapture.FLASH_MODE_AUTO -> Icons.Default.FlashAuto
                            else -> Icons.Default.FlashOff
                        },
                        contentDescription = "Flash",
                        tint = if (flashMode != ImageCapture.FLASH_MODE_OFF) AtelierBrass else Color.White
                    )
                }

                // Lens Switch
                IconButton(
                    onClick = {
                        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                            CameraSelector.LENS_FACING_FRONT
                        } else {
                            CameraSelector.LENS_FACING_BACK
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.FlipCameraAndroid,
                        contentDescription = "Inverter Câmera",
                        tint = Color.White
                    )
                }
            }
        }

        // Bottom Capture Controls
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.7f))
                .padding(vertical = 24.dp, horizontal = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gallery button
                IconButton(
                    onClick = onOpenGallery,
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                        .testTag("camera_gallery_shortcut")
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = "Galeria",
                        tint = Color.White
                    )
                }

                // Shutter Button
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .border(3.dp, AtelierBrass, CircleShape)
                        .clickable(enabled = !isCapturing && sessionPhotoCount < maxPhotos) {
                            isCapturing = true
                            CameraXManager.takePhoto(
                                context = context,
                                imageCapture = imageCapture,
                                onImageCaptured = { uri, bitmap ->
                                    isCapturing = false
                                    capturedPhotoUri = uri
                                    capturedPhotoBitmap = bitmap
                                },
                                onError = {
                                    isCapturing = false
                                    Toast.makeText(context, "Erro ao capturar foto: ${it.message}", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                        .testTag("camera_shutter_button"),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCapturing) {
                        CircularProgressIndicator(
                            color = AtelierBrass,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(36.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                    }
                }

                // Done / Advance Button if at least 1 photo was taken
                if (sessionPhotoCount > 0) {
                    Button(
                        onClick = onDone,
                        colors = ButtonDefaults.buttonColors(containerColor = AtelierBrass),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("camera_finish_button")
                    ) {
                        Text("Avançar", color = AtelierWarmWhite, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Spacer(modifier = Modifier.size(50.dp))
                }
            }
        }

        // Captured Photo Review Overlay Sheet
        AnimatedVisibility(
            visible = capturedPhotoUri != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            if (capturedPhotoUri != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AtelierWarmWhite),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Foto capturada!",
                            style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Serif),
                            color = AtelierBlack
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            AsyncImage(
                                model = capturedPhotoUri,
                                contentDescription = "Foto capturada",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    capturedPhotoUri = null
                                    capturedPhotoBitmap = null
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Repetir", color = AtelierBlack)
                            }

                            Button(
                                onClick = {
                                    val uri = capturedPhotoUri!!
                                    onPhotoCaptured(uri)
                                    sessionPhotoCount++
                                    capturedPhotoUri = null
                                    capturedPhotoBitmap = null

                                    if (sessionPhotoCount >= maxPhotos) {
                                        onDone()
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AtelierBlack)
                            ) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = AtelierWarmWhite)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Usar Foto", color = AtelierWarmWhite)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FramingOverlayGuide(
    photoIndex: Int,
    modifier: Modifier = Modifier
) {
    val guideLabel = when (photoIndex) {
        0 -> "FRENTE DA PEÇA / CAIMENTO GERAL"
        1 -> "COSTAS OU LATERAL"
        else -> "DETALHE DO PONTO DE AJUSTE"
    }

    Box(
        modifier = modifier.padding(horizontal = 32.dp, vertical = 110.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outline bounding box
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(1.5.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
        ) {
            Surface(
                color = Color.Black.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
            ) {
                Text(
                    text = guideLabel,
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = AtelierWarmWhite,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Text(
                text = "Mantenha a peça inteira visível ou foque na linha que necessita de costura",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
            )
        }
    }
}
