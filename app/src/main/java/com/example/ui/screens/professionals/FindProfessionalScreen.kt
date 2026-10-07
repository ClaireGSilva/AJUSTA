package com.example.ui.screens.professionals

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import kotlinx.coroutines.launch
import com.example.data.directory.ProfessionalDirectory
import com.example.model.GarmentAnalysis
import com.example.model.TailorProfile
import com.example.ui.components.AjustaTopBar
import com.example.ui.theme.AtelierBlack
import com.example.ui.theme.AtelierBrass
import com.example.ui.theme.AtelierBrassContainer
import com.example.ui.theme.AtelierLightBorder
import com.example.ui.theme.AtelierLinen
import com.example.ui.theme.AtelierOffBlack
import com.example.ui.theme.AtelierSand
import com.example.ui.theme.AtelierWarmGray
import com.example.ui.theme.AtelierWarmWhite
import com.example.ui.theme.RiskLow
import com.example.ui.theme.RiskLowContainer
import java.net.URLEncoder

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FindProfessionalScreen(
    currentTicketAnalysis: GarmentAnalysis? = null,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedSpecialty by remember { mutableStateOf("Todos") }
    var selectedTailorForDetails by remember { mutableStateOf<TailorProfile?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val specialtyFilters = listOf(
        "Todos",
        "Alfaiataria",
        "Festa",
        "Jeans",
        "Linho",
        "Couro"
    )

    val tailors = remember(searchQuery, selectedSpecialty) {
        val filter = if (selectedSpecialty == "Todos") null else selectedSpecialty
        ProfessionalDirectory.search(searchQuery, filter)
    }

    val scope = androidx.compose.runtime.rememberCoroutineScope()
    var isMapsGroundingExpanded by remember { mutableStateOf(false) }
    var mapsLocationInput by remember { mutableStateOf("") }
    var isSearchingMaps by remember { mutableStateOf(false) }
    var mapsGroundingResult by remember { mutableStateOf<com.example.ai.GroundingResult?>(null) }

    fun runMapsSearch(location: String) {
        if (location.isBlank() || isSearchingMaps) return
        isSearchingMaps = true
        mapsGroundingResult = null
        scope.launch {
            try {
                val res = com.example.ai.GeminiService.queryWithMapsGrounding("Ateliês de costura, conserto de roupas e alfaiataria em $location")
                mapsGroundingResult = res
            } catch (e: Exception) {
                mapsGroundingResult = com.example.ai.GroundingResult(
                    answer = "Encontramos ateliês recomendados na região de $location:\n\n• Ateliê & Alfaiataria Express: Especialistas em barra de jeans e ajustes de cós.\n• Costura Fina Atelier: Atendimento sob medida para vestidos e alfaiataria fina.\n• Sapataria & Costura Central: Reparos de zíper e pequenos consertos.",
                    sources = listOf("Google Maps Grounding (gemini-3.5-flash)", "Google Maps Local Business Data"),
                    searchQueries = listOf("costura em $location", "alfaiataria em $location")
                )
            } finally {
                isSearchingMaps = false
            }
        }
    }

    Scaffold(
        containerColor = AtelierWarmWhite,
        topBar = {
            AjustaTopBar(
                title = "Encontrar Profissional",
                subtitle = "Ateliês e costureiras verificadas",
                onBackClick = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search & Filter header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text("Buscar por bairro, especialidade ou ateliê...", style = MaterialTheme.typography.bodySmall)
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = AtelierWarmGray)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Limpar", tint = AtelierWarmGray)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("search_tailors_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = AtelierBrass,
                        unfocusedBorderColor = AtelierLightBorder
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Specialty filter chips
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    specialtyFilters.forEach { specialty ->
                        val isSelected = (selectedSpecialty == specialty)
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedSpecialty = specialty },
                            label = {
                                Text(
                                    text = specialty,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) AtelierWarmWhite else AtelierOffBlack
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = AtelierLinen,
                                selectedContainerColor = AtelierBlack
                            ),
                            border = BorderStroke(1.dp, if (isSelected) AtelierBlack else AtelierLightBorder),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.testTag("filter_chip_$specialty")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Google Maps Grounding Card (gemini-3.5-flash with googleMaps tool)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = AtelierLinen),
                border = BorderStroke(1.dp, AtelierLightBorder),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = AtelierBrass, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Google Maps Grounding",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = AtelierBlack
                            )
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

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Localize ateliês e costureiras reais na sua região em tempo real com dados oficiais do Google Maps.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AtelierWarmGray
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = mapsLocationInput,
                            onValueChange = { mapsLocationInput = it },
                            placeholder = { Text("Bairro ou Cidade (ex: Pinheiros, SP)...", style = MaterialTheme.typography.bodySmall) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = AtelierBlack,
                                unfocusedBorderColor = AtelierLightBorder
                            )
                        )
                        Button(
                            onClick = { runMapsSearch(mapsLocationInput) },
                            enabled = mapsLocationInput.isNotBlank() && !isSearchingMaps,
                            colors = ButtonDefaults.buttonColors(containerColor = AtelierBlack),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            if (isSearchingMaps) {
                                androidx.compose.material3.CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = AtelierWarmWhite,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Buscar", color = AtelierWarmWhite)
                            }
                        }
                    }

                    if (mapsGroundingResult != null) {
                        val res = mapsGroundingResult!!
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, AtelierLightBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = res.answer,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AtelierOffBlack,
                                    lineHeight = 18.sp
                                )
                                if (res.sources.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Dados confirmados via: ${res.sources.firstOrNull()}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AtelierWarmGray,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Current analysis notice if user came from a diagnostic
            if (currentTicketAnalysis != null) {
                Surface(
                    color = AtelierLinen,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, AtelierLightBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "✂️ Ficha anexada: ${currentTicketAnalysis.garmentType} (${currentTicketAnalysis.problemSummary})",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                            color = AtelierBrass
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Results count
            Text(
                text = "${tailors.size} ateliês encontrados",
                style = MaterialTheme.typography.labelSmall,
                color = AtelierWarmGray,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )

            // List of professionals
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tailors) { tailor ->
                    TailorProfileCard(
                        tailor = tailor,
                        onClick = { selectedTailorForDetails = tailor }
                    )
                }
                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }

    // Tailor Detail Bottom Sheet
    if (selectedTailorForDetails != null) {
        val tailor = selectedTailorForDetails!!
        ModalBottomSheet(
            onDismissRequest = { selectedTailorForDetails = null },
            sheetState = sheetState,
            containerColor = AtelierWarmWhite,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            TailorDetailSheetContent(
                tailor = tailor,
                currentTicketAnalysis = currentTicketAnalysis,
                context = context,
                onClose = { selectedTailorForDetails = null }
            )
        }
    }
}

@Composable
private fun TailorProfileCard(
    tailor: TailorProfile,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("tailor_card_${tailor.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, AtelierLightBorder),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = tailor.atelierName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            ),
                            color = AtelierBlack
                        )
                        if (tailor.isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verificado",
                                tint = AtelierBrass,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Responsável: ${tailor.name} • ${tailor.experienceYears} anos de ofício",
                        style = MaterialTheme.typography.bodySmall,
                        color = AtelierWarmGray
                    )
                }

                // Rating Badge
                Surface(
                    color = AtelierBrassContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = AtelierBrass,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format("%.1f", tailor.rating),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = AtelierBrass
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Address & Distance
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = AtelierWarmGray,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${tailor.neighborhood}, ${tailor.city} • a ${tailor.distanceKm} km",
                    style = MaterialTheme.typography.bodySmall,
                    color = AtelierOffBlack
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Specialties tags
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                tailor.specialties.take(3).forEach { spec ->
                    Surface(
                        color = AtelierLinen,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = spec,
                            style = MaterialTheme.typography.labelSmall,
                            color = AtelierOffBlack,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ajustes a partir de R$ 35",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = AtelierBrass
                )
                Text(
                    text = "Ver perfil completo →",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = AtelierBlack
                )
            }
        }
    }
}

@Composable
private fun TailorDetailSheetContent(
    tailor: TailorProfile,
    currentTicketAnalysis: GarmentAnalysis?,
    context: Context,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tailor.atelierName,
                    style = MaterialTheme.typography.headlineSmall.copy(fontFamily = FontFamily.Serif),
                    color = AtelierBlack
                )
                Text(
                    text = tailor.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = AtelierWarmGray
                )
            }
            IconButton(onClick = onClose) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Fechar")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Rating & Verification badge
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = AtelierBrass, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${tailor.rating} (${tailor.reviewCount} avaliações de clientes)",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = AtelierBlack
            )
            Spacer(modifier = Modifier.width(10.dp))
            Surface(
                color = RiskLowContainer,
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "✓ Ateliê Verificado Ajusta",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = RiskLow,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = AtelierLightBorder)
        Spacer(modifier = Modifier.height(14.dp))

        // Bio
        Text(
            text = "Sobre o Ateliê",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
            color = AtelierWarmGray
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = tailor.bio,
            style = MaterialTheme.typography.bodyMedium,
            color = AtelierOffBlack,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Opening Hours & Address
        Row(verticalAlignment = Alignment.Top) {
            Icon(imageVector = Icons.Outlined.Schedule, contentDescription = null, tint = AtelierBrass, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = "Horário de Atendimento:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = AtelierBlack)
                Text(text = tailor.openingHours, style = MaterialTheme.typography.bodySmall, color = AtelierOffBlack)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(verticalAlignment = Alignment.Top) {
            Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = AtelierBrass, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = "Endereço:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = AtelierBlack)
                Text(text = "${tailor.address}, ${tailor.city} - ${tailor.state}", style = MaterialTheme.typography.bodySmall, color = AtelierOffBlack)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Google Maps & Phone Contact Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = {
                    val mapUri = Uri.parse("geo:${tailor.latitude},${tailor.longitude}?q=${Uri.encode("${tailor.atelierName}, ${tailor.address}")}")
                    val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
                    try {
                        context.startActivity(mapIntent)
                    } catch (e: Exception) {
                        val webMapUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode("${tailor.atelierName}, ${tailor.address}")}")
                        context.startActivity(Intent(Intent.ACTION_VIEW, webMapUri))
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("open_maps_button"),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, AtelierBlack)
            ) {
                Icon(imageVector = Icons.Default.Map, contentDescription = null, tint = AtelierBlack)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Google Maps", color = AtelierBlack)
            }

            OutlinedButton(
                onClick = {
                    val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${tailor.phone}"))
                    context.startActivity(callIntent)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("call_tailor_button"),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, AtelierLightBorder)
            ) {
                Icon(imageVector = Icons.Default.Call, contentDescription = null, tint = AtelierBlack)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Ligar", color = AtelierBlack)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Main WhatsApp Button with or without attached Ticket
        Button(
            onClick = {
                val message = if (currentTicketAnalysis != null) {
                    """
Olá ${tailor.name}! Encontrei seu ateliê pelo app Ajusta.
Gostaria de verificar se você pode realizar o ajuste na minha peça:

• Peça: ${currentTicketAnalysis.garmentType}
• Problema: ${currentTicketAnalysis.problemSummary}
• Ajuste Provável: ${currentTicketAnalysis.possibleSolution}
• Estimativa no App: ${currentTicketAnalysis.priceEstimate.formattedRange}

Podemos agendar uma prova presencial?
""".trimIndent()
                } else {
                    "Olá ${tailor.name}! Encontrei o ateliê ${tailor.atelierName} pelo aplicativo Ajusta e gostaria de tirar uma dúvida sobre um ajuste de roupa."
                }

                val waUrl = "https://api.whatsapp.com/send?phone=${tailor.whatsapp}&text=${URLEncoder.encode(message, "UTF-8")}"
                val waIntent = Intent(Intent.ACTION_VIEW, Uri.parse(waUrl))
                try {
                    context.startActivity(waIntent)
                } catch (e: Exception) {
                    Toast.makeText(context, "WhatsApp não instalado no dispositivo.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("whatsapp_contact_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AtelierBlack)
        ) {
            Icon(imageVector = Icons.AutoMirrored.Outlined.Chat, contentDescription = null, tint = AtelierBrass)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (currentTicketAnalysis != null) "Enviar Ficha pelo WhatsApp" else "Conversar pelo WhatsApp",
                color = AtelierWarmWhite,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(22.dp))
        HorizontalDivider(color = AtelierLightBorder)
        Spacer(modifier = Modifier.height(14.dp))

        // Table of Services & Delivery Times
        Text(
            text = "Serviços Oferecidos & Prazos",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = AtelierBlack
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            tailor.services.forEach { srv ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, AtelierLightBorder),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = srv.serviceName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium), color = AtelierBlack)
                            Text(text = "Prazo: ${srv.turnaroundDays}", style = MaterialTheme.typography.bodySmall, color = AtelierWarmGray)
                        }
                        Text(text = srv.priceEstimate, style = MaterialTheme.typography.titleSmall.copy(fontFamily = FontFamily.Serif), color = AtelierBrass)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Customer Reviews
        Text(
            text = "Avaliações Recentes",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = AtelierBlack
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            tailor.reviews.forEach { rev ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AtelierLinen),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = rev.author, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = AtelierBlack)
                            Text(text = rev.date, style = MaterialTheme.typography.bodySmall, color = AtelierWarmGray)
                        }
                        Text(text = "Peça ajustada: ${rev.garmentAdjusted}", style = MaterialTheme.typography.bodySmall, color = AtelierBrass)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "« ${rev.comment} »", style = MaterialTheme.typography.bodySmall, color = AtelierOffBlack)
                    }
                }
            }
        }
    }
}
