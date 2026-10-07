package com.example.ui.screens.professional

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DesignServices
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
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
import com.example.model.OrderStatus
import com.example.model.ProfessionalOrder
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
fun ProfessionalDashboardScreen(
    viewModel: ProfessionalViewModel,
    onBack: () -> Unit,
    onNewOrder: () -> Unit,
    onOpenOrder: (ProfessionalOrder) -> Unit
) {
    val orders by viewModel.orders.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Pedidos", "Clientes", "Orçamentos", "Serviços")

    val pendingOrdersCount = orders.count { it.status == OrderStatus.EM_ANDAMENTO || it.status == OrderStatus.PROVA_PENDENTE }
    val budgetOrdersCount = orders.count { it.status == OrderStatus.ORCAMENTO }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AjustaTopBar(
                title = "Atelier Profissional",
                subtitle = "Painel de Costura & Alfaiataria",
                onBackClick = onBack,
                actions = {
                    com.example.ui.components.ThemeToggleButton()
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewOrder,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                modifier = Modifier.testTag("pro_fab_new_order")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Novo Atendimento")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Dashboard metric summary cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Em andamento",
                    count = pendingOrdersCount.toString(),
                    icon = Icons.AutoMirrored.Filled.Assignment,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Orçamentos",
                    count = budgetOrdersCount.toString(),
                    icon = Icons.Default.AttachMoney,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Clientes",
                    count = orders.map { it.clientName }.distinct().size.toString(),
                    icon = Icons.Default.People,
                    modifier = Modifier.weight(1f)
                )
            }

            // Overall repairs progress summary bar
            OverallRepairsProgressBar(orders = orders)

            Spacer(modifier = Modifier.height(10.dp))

            // Tabs: Pedidos, Clientes, Orçamentos, Serviços
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = AtelierBrass
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = (selectedTab == index),
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tab Content
            when (selectedTab) {
                0 -> OrdersList(
                    orders = orders,
                    onOpenOrder = onOpenOrder,
                    onUpdateStatus = { id, newStatus -> viewModel.updateOrderStatus(id, newStatus) }
                )
                1 -> ClientsList(orders = orders, onOpenOrder = onOpenOrder)
                2 -> BudgetList(orders = orders.filter { it.status == OrderStatus.ORCAMENTO }, onOpenOrder = onOpenOrder)
                3 -> ServicesCatalog()
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    count: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AtelierBrass,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = count,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun OrdersList(
    orders: List<ProfessionalOrder>,
    onOpenOrder: (ProfessionalOrder) -> Unit,
    onUpdateStatus: (String, OrderStatus) -> Unit
) {
    if (orders.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Nenhum atendimento registrado. Toque no botão '+' para abrir uma nova ordem.",
                style = MaterialTheme.typography.bodyMedium,
                color = AtelierWarmGray
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(orders) { order ->
                OrderCard(
                    order = order,
                    onClick = { onOpenOrder(order) },
                    onUpdateStatus = { newStatus -> onUpdateStatus(order.id, newStatus) }
                )
            }
            item { Spacer(modifier = Modifier.height(72.dp)) }
        }
    }
}

@Composable
private fun OrderCard(
    order: ProfessionalOrder,
    onClick: () -> Unit,
    onUpdateStatus: (OrderStatus) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("order_card_${order.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.clientName,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Surface(
                    color = when (order.status) {
                        OrderStatus.ORCAMENTO -> AtelierBrassContainer
                        OrderStatus.EM_ANDAMENTO -> MaterialTheme.colorScheme.surfaceVariant
                        OrderStatus.PROVA_PENDENTE -> Color(0xFFFDECE9)
                        OrderStatus.CONCLUIDO -> Color(0xFFE5EFE8)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = order.status.label,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = when (order.status) {
                            OrderStatus.ORCAMENTO -> AtelierBrass
                            OrderStatus.EM_ANDAMENTO -> MaterialTheme.colorScheme.onSurface
                            OrderStatus.PROVA_PENDENTE -> Color(0xFF9E3827)
                            OrderStatus.CONCLUIDO -> Color(0xFF3D6B52)
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${order.garmentType} • ${order.clientRequest}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Complexidade: ${order.complexity}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (order.agreedPrice != null) {
                    Text(
                        text = "R$ ${order.agreedPrice.toInt()}",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        ),
                        color = AtelierBrass
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            Spacer(modifier = Modifier.height(10.dp))

            // Barra de Progresso Visual de Reparos Concluídos vs Pendentes por Peça
            GarmentRepairProgressBar(
                order = order,
                onUpdateStatus = onUpdateStatus
            )
        }
    }
}

@Composable
fun GarmentRepairProgressBar(
    order: ProfessionalOrder,
    onUpdateStatus: (OrderStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    val (progressFraction, percentCompleted, statusDescription) = when (order.status) {
        OrderStatus.CONCLUIDO -> Triple(1.0f, 100, "100% Concluído • Peça finalizada e pronta para entrega")
        OrderStatus.PROVA_PENDENTE -> Triple(0.75f, 75, "75% Concluído • 25% Pendente (Ajuste montado, aguardando prova)")
        OrderStatus.EM_ANDAMENTO -> Triple(0.45f, 45, "45% Concluído • 55% Pendente (Em execução na bancada/máquina)")
        OrderStatus.ORCAMENTO -> Triple(0.15f, 15, "15% Concluído • 85% Pendente (Orçamento preliminar em avaliação)")
    }

    val percentPending = 100 - percentCompleted

    val progressColor = when (order.status) {
        OrderStatus.CONCLUIDO -> Color(0xFF3D6B52)
        OrderStatus.PROVA_PENDENTE -> AtelierTerracotta
        OrderStatus.EM_ANDAMENTO -> AtelierBrass
        OrderStatus.ORCAMENTO -> AtelierWarmGray
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("repair_progress_bar_${order.id}")
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

        // Visual Progress Bar
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
            text = statusDescription,
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
            OrderStatus.values().forEach { st ->
                val isSelected = order.status == st
                Surface(
                    color = if (isSelected) progressColor else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, if (isSelected) progressColor else MaterialTheme.colorScheme.outline),
                    modifier = Modifier
                        .clickable { onUpdateStatus(st) }
                        .testTag("btn_set_status_${order.id}_${st.name}")
                ) {
                    Text(
                        text = when (st) {
                            OrderStatus.ORCAMENTO -> "Orç."
                            OrderStatus.EM_ANDAMENTO -> "Em Andam."
                            OrderStatus.PROVA_PENDENTE -> "Prova"
                            OrderStatus.CONCLUIDO -> "Pronto ✓"
                        },
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
private fun OverallRepairsProgressBar(
    orders: List<ProfessionalOrder>,
    modifier: Modifier = Modifier
) {
    val totalOrders = orders.size
    val completedOrders = orders.count { it.status == OrderStatus.CONCLUIDO }
    val inProofOrders = orders.count { it.status == OrderStatus.PROVA_PENDENTE }
    val inProgressOrders = orders.count { it.status == OrderStatus.EM_ANDAMENTO }
    val budgetOrders = orders.count { it.status == OrderStatus.ORCAMENTO }
    val pendingOrders = totalOrders - completedOrders

    val overallRate = if (totalOrders > 0) (completedOrders * 100 / totalOrders) else 0

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .testTag("overall_repairs_progress_card")
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
                        tint = AtelierBrass,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Rastreamento Geral de Reparos",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "$overallRate% Concluído",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = AtelierBrass
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Bar Visual Geral
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(MaterialTheme.colorScheme.outlineVariant)
            ) {
                val fraction = if (totalOrders > 0) completedOrders.toFloat() / totalOrders else 0f
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction)
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(AtelierBrass)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "✓ $completedOrders finalizados",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                    color = Color(0xFF3D6B52)
                )
                Text(
                    text = "⏳ $pendingOrders pendentes ($inProofOrders prova, $inProgressOrders execução, $budgetOrders orç.)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ClientsList(
    orders: List<ProfessionalOrder>,
    onOpenOrder: (ProfessionalOrder) -> Unit
) {
    val clients = orders.groupBy { it.clientName }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(clients.toList()) { (clientName, clientOrders) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onOpenOrder(clientOrders.first()) },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, AtelierLightBorder)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(AtelierLinen),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = clientName.take(1).uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            color = AtelierBlack
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = clientName,
                            style = MaterialTheme.typography.titleSmall,
                            color = AtelierBlack
                        )
                        Text(
                            text = "${clientOrders.size} serviço(s) • Tel: ${clientOrders.first().clientPhone.ifBlank { "Não informado" }}",
                            style = MaterialTheme.typography.bodySmall,
                            color = AtelierWarmGray
                        )
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(72.dp)) }
    }
}

@Composable
private fun BudgetList(
    orders: List<ProfessionalOrder>,
    onOpenOrder: (ProfessionalOrder) -> Unit
) {
    if (orders.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Nenhum orçamento em aberto no momento.",
                style = MaterialTheme.typography.bodyMedium,
                color = AtelierWarmGray
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(orders) { order ->
                OrderCard(order = order, onClick = { onOpenOrder(order) })
            }
            item { Spacer(modifier = Modifier.height(72.dp)) }
        }
    }
}

@Composable
private fun ServicesCatalog() {
    val commonServices = listOf(
        Pair("Barra simples máquina", "R$ 30–45"),
        Pair("Barra original Jeans", "R$ 45–60"),
        Pair("Ajuste de cós / cintura jeans", "R$ 50–80"),
        Pair("Ajuste de quadril e gancho", "R$ 60–90"),
        Pair("Subida de manga com carcela", "R$ 50–75"),
        Pair("Ajuste de ombro blazer", "R$ 90–160"),
        Pair("Troca de zíper comum / invisível", "R$ 35–50"),
        Pair("Bainha de lenço vestido seda", "R$ 80–140")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "Tabela de Preços do Atelier",
                style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Serif),
                color = AtelierBlack
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Valores de referência para orçamentos e agendamentos rápidos.",
                style = MaterialTheme.typography.bodySmall,
                color = AtelierWarmGray
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        items(commonServices) { (name, price) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, AtelierLightBorder),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = AtelierOffBlack
                    )
                    Text(
                        text = price,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        ),
                        color = AtelierBrass
                    )
                }
            }
        }
        item { Spacer(modifier = Modifier.height(72.dp)) }
    }
}
