package com.example.ui.screens.history

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.LocalAtm
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.GarmentProgressManager
import com.example.data.demo.DemoDataProvider
import com.example.data.local.AppDatabase
import com.example.data.repository.GarmentRepository
import com.example.model.GarmentAnalysis
import com.example.model.GarmentRepairStage
import com.example.model.RiskLevel
import com.example.ui.components.AjustaTopBar
import com.example.ui.components.ConfidenceBadge
import com.example.ui.components.ThemeToggleButton
import com.example.util.DiagnosisShareHelper
import com.example.ui.theme.AtelierBlack
import com.example.ui.theme.AtelierBrass
import com.example.ui.theme.AtelierBrassContainer
import com.example.ui.theme.AtelierLightBorder
import com.example.ui.theme.AtelierLinen
import com.example.ui.theme.AtelierOffBlack
import com.example.ui.theme.AtelierTerracotta
import com.example.ui.theme.AtelierWarmGray
import com.example.ui.theme.AtelierWarmWhite
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    onSelectAnalysis: (GarmentAnalysis) -> Unit,
    onOpenTicket: ((GarmentAnalysis) -> Unit)? = null,
    onStartNewAnalysis: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val database = remember { AppDatabase.getDatabase(context) }
    val repo = remember {
        GarmentRepository(
            dao = database.garmentAnalysisDao(),
            diagnosticDao = database.diagnosticResultDao()
        )
    }

    val savedItems by repo.allAnalyses.collectAsState(initial = emptyList())

    // Search and filter state
    var searchQuery by remember { mutableStateOf("") }
    var selectedGarmentTypeFilter by remember { mutableStateOf("Todas as peças") }
    var selectedDateFilter by remember { mutableStateOf("Todas as datas") }
    var selectedSpecificDateMillis by remember { mutableStateOf<Long?>(null) }
    var sortNewestFirst by remember { mutableStateOf(true) }
    var showDatePickerDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<GarmentAnalysis?>(null) }

    val datePickerState = rememberDatePickerState()

    // Formatted date string for specific date filter
    val formattedSpecificDate = remember(selectedSpecificDateMillis) {
        selectedSpecificDateMillis?.let {
            SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR")).format(Date(it))
        }
    }

    // Filtered and sorted list
    val filteredItems = remember(
        savedItems,
        searchQuery,
        selectedGarmentTypeFilter,
        selectedDateFilter,
        selectedSpecificDateMillis,
        sortNewestFirst
    ) {
        val now = System.currentTimeMillis()
        val query = searchQuery.trim().lowercase()

        savedItems
            .filter { item ->
                // 1. Garment Type Filter
                val matchesType = when (selectedGarmentTypeFilter) {
                    "Todas as peças" -> true
                    "Calça / Jeans" -> item.garmentType.contains("calça", ignoreCase = true) || item.garmentType.contains("jeans", ignoreCase = true)
                    "Vestido" -> item.garmentType.contains("vestido", ignoreCase = true)
                    "Camisa" -> item.garmentType.contains("camisa", ignoreCase = true)
                    "Blazer / Alfaiataria" -> item.garmentType.contains("blazer", ignoreCase = true) || item.garmentType.contains("paletó", ignoreCase = true) || item.garmentType.contains("alfaiataria", ignoreCase = true)
                    "Saia" -> item.garmentType.contains("saia", ignoreCase = true)
                    else -> item.garmentType.contains(selectedGarmentTypeFilter, ignoreCase = true)
                }

                // 2. Date Filter
                val matchesDate = when (selectedDateFilter) {
                    "Todas as datas" -> true
                    "Hoje" -> isSameDay(item.timestamp, now)
                    "Últimos 7 dias" -> item.timestamp >= (now - 7L * 24 * 60 * 60 * 1000L)
                    "Últimos 30 dias" -> item.timestamp >= (now - 30L * 24 * 60 * 60 * 1000L)
                    "Data específica" -> {
                        selectedSpecificDateMillis != null && isSameDay(item.timestamp, selectedSpecificDateMillis!!)
                    }
                    else -> true
                }

                // 3. Search Query Filter (Matches Garment Type, Date String, Fabric or Problem Summary)
                val matchesSearch = if (query.isEmpty()) {
                    true
                } else {
                    val itemDateStr = SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR")).format(Date(item.timestamp))
                    val itemMonthStr = SimpleDateFormat("MMMM yyyy", Locale.forLanguageTag("pt-BR")).format(Date(item.timestamp)).lowercase()
                    val itemDayMonthStr = SimpleDateFormat("dd/MM", Locale.forLanguageTag("pt-BR")).format(Date(item.timestamp))

                    item.garmentType.lowercase().contains(query) ||
                        item.problemSummary.lowercase().contains(query) ||
                        (item.fabric ?: "").lowercase().contains(query) ||
                        item.possibleSolution.lowercase().contains(query) ||
                        itemDateStr.contains(query) ||
                        itemDayMonthStr.contains(query) ||
                        itemMonthStr.contains(query)
                }

                matchesType && matchesDate && matchesSearch
            }
            .sortedBy { if (sortNewestFirst) -it.timestamp else it.timestamp }
    }

    val isFilterActive = searchQuery.isNotBlank() ||
        selectedGarmentTypeFilter != "Todas as peças" ||
        selectedDateFilter != "Todas as datas"

    // Metrics for dashboard cards
    val totalPieces = savedItems.size
    val averageEstimate = if (savedItems.isNotEmpty()) {
        val totalAvg = savedItems.map { (it.priceEstimate.minPrice + it.priceEstimate.maxPrice) / 2.0 }.average()
        "R$ ${totalAvg.toInt()}"
    } else "R$ 0"
    val ticketsCount = savedItems.count { it.isTicketGenerated }

    val progressMap by GarmentProgressManager.progressMapState.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AjustaTopBar(
                title = "Dashboard de Peças",
                subtitle = "Histórico e diagnósticos salvos",
                onBackClick = onBack,
                actions = {
                    ThemeToggleButton()
                }
            )
        },
        floatingActionButton = {
            if (onStartNewAnalysis != null) {
                FloatingActionButton(
                    onClick = onStartNewAnalysis,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = CircleShape,
                    modifier = Modifier.testTag("history_fab_new_analysis")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Nova Análise")
                }
            }
        }
    ) { innerPadding ->
        if (savedItems.isEmpty()) {
            EmptyDashboardView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                onStartNewAnalysis = onStartNewAnalysis,
                onLoadDemos = {
                    scope.launch {
                        DemoDataProvider.demoCases.forEach { demo ->
                            repo.saveAnalysis(demo, isTicket = demo.id.contains("jeans") || demo.id.contains("vestido"))
                        }
                        Toast.makeText(context, "Exemplos de atelier carregados no Room Database.", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Top KPI Summary Cards
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "RESUMO DO SEU ACERVO LOCAL",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DashboardMetricCard(
                            title = "Peças Salvas",
                            value = totalPieces.toString(),
                            icon = Icons.Outlined.Checkroom,
                            modifier = Modifier.weight(1f)
                        )
                        DashboardMetricCard(
                            title = "Média Ajustes",
                            value = averageEstimate,
                            icon = Icons.Outlined.LocalAtm,
                            modifier = Modifier.weight(1f)
                        )
                        DashboardMetricCard(
                            title = "Fichas Ateliê",
                            value = ticketsCount.toString(),
                            icon = Icons.AutoMirrored.Outlined.ReceiptLong,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Overall Repairs Progress Summary Bar across all registered garments
                item {
                    OverallAcervoRepairsProgressBar(
                        items = savedItems,
                        progressMap = progressMap
                    )
                }

                // Search Bar with Date & Garment Type support
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dashboard_search_input"),
                        placeholder = {
                            Text(
                                "Buscar por peça (jeans, vestido) ou data (04/10, outubro)...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Limpar busca")
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AtelierBrass,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                // Filter Section 1: Tipo de Peça
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "FILTRAR POR TIPO DE PEÇA",
                                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp, fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        val garmentTypeOptions = listOf(
                            "Todas as peças",
                            "Calça / Jeans",
                            "Vestido",
                            "Camisa",
                            "Blazer / Alfaiataria",
                            "Saia"
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            garmentTypeOptions.forEach { type ->
                                val isSelected = selectedGarmentTypeFilter == type
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedGarmentTypeFilter = type },
                                    label = { Text(type) },
                                    leadingIcon = if (isSelected) {
                                        { Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                    } else null,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                        selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary,
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = isSelected,
                                        borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                    )
                                )
                            }
                        }
                    }
                }

                // Filter Section 2: Data
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "FILTRAR POR DATA",
                                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp, fontWeight = FontWeight.Bold),
                                color = AtelierWarmGray
                            )

                            // Sort Order Toggle
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { sortNewestFirst = !sortNewestFirst }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SwapVert,
                                    contentDescription = null,
                                    tint = AtelierBrass,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (sortNewestFirst) "Mais recentes" else "Mais antigas",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = AtelierBrass
                                )
                            }
                        }

                        val dateOptions = listOf(
                            "Todas as datas",
                            "Hoje",
                            "Últimos 7 dias",
                            "Últimos 30 dias"
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            dateOptions.forEach { dateOpt ->
                                val isSelected = selectedDateFilter == dateOpt && selectedSpecificDateMillis == null
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        selectedDateFilter = dateOpt
                                        selectedSpecificDateMillis = null
                                    },
                                    label = { Text(dateOpt) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = AtelierBlack,
                                        selectedLabelColor = AtelierWarmWhite,
                                        containerColor = AtelierLinen,
                                        labelColor = AtelierOffBlack
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = isSelected,
                                        borderColor = if (isSelected) AtelierBlack else AtelierLightBorder
                                    )
                                )
                            }

                            // Specific Date Picker Chip
                            val isSpecificDateSelected = selectedSpecificDateMillis != null
                            FilterChip(
                                selected = isSpecificDateSelected,
                                onClick = { showDatePickerDialog = true },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        if (isSpecificDateSelected && formattedSpecificDate != null) {
                                            formattedSpecificDate
                                        } else {
                                            "Escolher dia..."
                                        }
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AtelierBrass,
                                    selectedLabelColor = AtelierWarmWhite,
                                    selectedLeadingIconColor = AtelierWarmWhite,
                                    containerColor = AtelierLinen,
                                    labelColor = AtelierOffBlack
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSpecificDateSelected,
                                    borderColor = if (isSpecificDateSelected) AtelierBrass else AtelierLightBorder
                                )
                            )
                        }
                    }
                }

                // Section header with active filter summary & clear button
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DIAGNÓSTICOS (${filteredItems.size} ${if (filteredItems.size == 1) "encontrado" else "encontrados"})",
                            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                            color = AtelierWarmGray
                        )

                        if (isFilterActive) {
                            TextButton(
                                onClick = {
                                    searchQuery = ""
                                    selectedGarmentTypeFilter = "Todas as peças"
                                    selectedDateFilter = "Todas as datas"
                                    selectedSpecificDateMillis = null
                                }
                            ) {
                                Text(
                                    text = "Limpar filtros",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = AtelierTerracotta
                                )
                            }
                        }
                    }
                }

                // Empty Filter Results State
                if (filteredItems.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = AtelierLinen),
                            border = BorderStroke(1.dp, AtelierLightBorder),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = null,
                                    tint = AtelierWarmGray,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Nenhuma peça corresponde aos filtros",
                                    style = MaterialTheme.typography.titleSmall.copy(fontFamily = FontFamily.Serif),
                                    color = AtelierBlack,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Tente alterar o tipo de peça ou o período selecionado para localizar seus diagnósticos anteriores.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AtelierWarmGray,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = {
                                        searchQuery = ""
                                        selectedGarmentTypeFilter = "Todas as peças"
                                        selectedDateFilter = "Todas as datas"
                                        selectedSpecificDateMillis = null
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AtelierBlack),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Redefinir Busca", color = AtelierWarmWhite)
                                }
                            }
                        }
                    }
                } else {
                    // Analyses Cards with Repair Progress Tracking
                    items(filteredItems) { item ->
                        val stage = progressMap[item.id] ?: GarmentProgressManager.getStage(item.id, item.isTicketGenerated)
                        SavedAnalysisCard(
                            analysis = item,
                            stage = stage,
                            onStageChange = { newStage -> GarmentProgressManager.setStage(item.id, newStage) },
                            onViewDiagnosis = { onSelectAnalysis(item) },
                            onOpenTicket = if (onOpenTicket != null) { { onOpenTicket(item) } } else null,
                            onDeleteClick = { itemToDelete = item }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    // Material 3 Date Picker Dialog
    if (showDatePickerDialog) {
        DatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        val selectedMillis = datePickerState.selectedDateMillis
                        if (selectedMillis != null) {
                            selectedSpecificDateMillis = selectedMillis
                            selectedDateFilter = "Data específica"
                        }
                        showDatePickerDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Filtrar por esta data")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        selectedSpecificDateMillis = null
                        selectedDateFilter = "Todas as datas"
                        showDatePickerDialog = false
                    },
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Text("Limpar", color = MaterialTheme.colorScheme.onSurface)
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    selectedDayContainerColor = MaterialTheme.colorScheme.primary,
                    selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
                    todayDateBorderColor = AtelierBrass,
                    todayContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }

    // Delete Confirmation Dialog
    if (itemToDelete != null) {
        val toDelete = itemToDelete!!
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = {
                Text(
                    text = "Excluir diagnóstico?",
                    style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Serif),
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    text = "Deseja remover \"${toDelete.garmentType}\" do banco de dados local? Esta ação não pode ser desfeita.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            repo.deleteAnalysis(toDelete.id)
                            itemToDelete = null
                            Toast.makeText(context, "Diagnóstico excluído com sucesso.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AtelierTerracotta)
                ) {
                    Text("Excluir", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { itemToDelete = null },
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.onSurface)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

private fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
    val fmt = SimpleDateFormat("yyyyMMdd", Locale.US)
    return fmt.format(Date(timestamp1)) == fmt.format(Date(timestamp2))
}

@Composable
private fun DashboardMetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AtelierBrass,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun OverallAcervoRepairsProgressBar(
    items: List<GarmentAnalysis>,
    progressMap: Map<String, GarmentRepairStage>,
    modifier: Modifier = Modifier
) {
    val total = items.size
    if (total == 0) return

    val stageList = items.map { item ->
        progressMap[item.id] ?: GarmentProgressManager.getStage(item.id, item.isTicketGenerated)
    }

    val completedCount = stageList.count { it == GarmentRepairStage.CONCLUIDO }
    val inProgressCount = stageList.count { it == GarmentRepairStage.EM_ANDAMENTO || it == GarmentRepairStage.PROVA_PENDENTE }
    val budgetCount = stageList.count { it == GarmentRepairStage.ORCADO || it == GarmentRepairStage.FICHA_EMITIDA }
    val pendingCount = total - completedCount

    val overallPercent = (completedCount * 100) / total

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = modifier
            .fillMaxWidth()
            .testTag("overall_acervo_progress_card")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color(0xFF3D6B52),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Progresso Geral de Reparos",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    color = if (overallPercent >= 70) Color(0xFFE5EFE8) else MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "$overallPercent% Concluídos",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (overallPercent >= 70) Color(0xFF3D6B52) else AtelierBrass,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Multi-segment Visual Progress Bar
            val completedFraction = completedCount.toFloat() / total
            val inProgressFraction = inProgressCount.toFloat() / total

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    if (completedFraction > 0f) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(completedFraction)
                                .height(8.dp)
                                .background(Color(0xFF3D6B52))
                        )
                    }
                    if (inProgressFraction > 0f) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(if (completedFraction > 0f) inProgressFraction / (1f - completedFraction) else inProgressFraction)
                                .height(8.dp)
                                .background(AtelierBrass)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "✓ $completedCount concluídas • $pendingCount pendentes",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$inProgressCount em costura/prova • $budgetCount orçadas",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun GarmentAnalysisRepairProgressBar(
    garmentId: String,
    stage: GarmentRepairStage,
    onStageChange: (GarmentRepairStage) -> Unit,
    modifier: Modifier = Modifier
) {
    val percentCompleted = stage.percentCompleted
    val percentPending = 100 - percentCompleted
    val progressFraction = percentCompleted / 100f

    val progressColor = when (stage) {
        GarmentRepairStage.CONCLUIDO -> Color(0xFF3D6B52)
        GarmentRepairStage.PROVA_PENDENTE -> AtelierTerracotta
        GarmentRepairStage.EM_ANDAMENTO -> AtelierBrass
        GarmentRepairStage.FICHA_EMITIDA -> Color(0xFF584175)
        GarmentRepairStage.ORCADO -> AtelierWarmGray
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("garment_repair_progress_$garmentId")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Progresso do Reparo:",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "$percentCompleted% concluído",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = progressColor
                )
            }

            Text(
                text = "$percentPending% pendente",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Visual Two-Tone Progress Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.outlineVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progressFraction)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(progressColor)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = stage.description,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 15.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Quick Stage Progression Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Atualizar:",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            GarmentRepairStage.values().forEach { st ->
                val isSelected = stage == st
                Surface(
                    color = if (isSelected) progressColor else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, if (isSelected) progressColor else MaterialTheme.colorScheme.outline),
                    modifier = Modifier
                        .clickable { onStageChange(st) }
                        .testTag("btn_set_garment_status_${garmentId}_${st.id}")
                ) {
                    Text(
                        text = st.shortLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SavedAnalysisCard(
    analysis: GarmentAnalysis,
    stage: GarmentRepairStage,
    onStageChange: (GarmentRepairStage) -> Unit,
    onViewDiagnosis: () -> Unit,
    onOpenTicket: (() -> Unit)?,
    onDeleteClick: () -> Unit
) {
    val dateFormatted = remember(analysis.timestamp) {
        SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR")).format(Date(analysis.timestamp))
    }
    val context = LocalContext.current
    val primaryPhoto = analysis.photoUris.firstOrNull()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onViewDiagnosis() }
            .testTag("saved_analysis_card_${analysis.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Thumbnail / Avatar + Garment Details + Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Photo Thumbnail or Fashion Silhouette Box
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (!primaryPhoto.isNullOrBlank()) {
                        AsyncImage(
                            model = primaryPhoto,
                            contentDescription = analysis.garmentType,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Checkroom,
                            contentDescription = null,
                            tint = AtelierBrass,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = analysis.garmentType,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        ConfidenceBadge(analysis.confidence)
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Salvo em $dateFormatted",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (!analysis.fabric.isNullOrBlank()) {
                        Text(
                            text = "Tecido: ${analysis.fabric}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                IconButton(
                    onClick = {
                        DiagnosisShareHelper.shareDiagnosis(context, analysis)
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("share_saved_analysis_${analysis.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Compartilhar diagnóstico",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Excluir",
                        tint = AtelierTerracotta
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Problem Summary
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "Diagnóstico do caimento:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = analysis.problemSummary.ifBlank { analysis.probableCause },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Alteration & Price Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Ajuste Recomendado",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = analysis.possibleSolution.take(30),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Estimativa",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = analysis.priceEstimate.formattedRange,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = AtelierBrass
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            Spacer(modifier = Modifier.height(10.dp))

            // Visual Progress Bar: Reparos Concluídos vs Pendentes da Peça Cadastrada
            GarmentAnalysisRepairProgressBar(
                garmentId = analysis.id,
                stage = stage,
                onStageChange = onStageChange
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onViewDiagnosis,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Ver Diagnóstico", style = MaterialTheme.typography.labelMedium)
                }

                if (onOpenTicket != null) {
                    OutlinedButton(
                        onClick = onOpenTicket,
                        modifier = Modifier
                            .weight(0.9f)
                            .height(42.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, if (analysis.isTicketGenerated) AtelierBrass else MaterialTheme.colorScheme.outline)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Assignment,
                            contentDescription = null,
                            tint = if (analysis.isTicketGenerated) AtelierBrass else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (analysis.isTicketGenerated) "Ficha Pronta" else "Ver Ficha",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (analysis.isTicketGenerated) AtelierBrass else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyDashboardView(
    modifier: Modifier = Modifier,
    onStartNewAnalysis: (() -> Unit)?,
    onLoadDemos: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Checkroom,
                contentDescription = null,
                tint = AtelierBrass,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Nenhuma peça salva ainda",
            style = MaterialTheme.typography.headlineSmall.copy(fontFamily = FontFamily.Serif),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Quando você analisa uma roupa no Ajusta, os diagnósticos e fichas técnicas de alfaiataria ficam salvos no Room Database do seu dispositivo.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (onStartNewAnalysis != null) {
            Button(
                onClick = onStartNewAnalysis,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("dashboard_empty_new_analysis_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Analisar Minha Primeira Peça")
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        OutlinedButton(
            onClick = onLoadDemos,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("dashboard_load_demos_button"),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, AtelierBrass)
        ) {
            Text("Carregar Exemplos de Demonstração", color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
