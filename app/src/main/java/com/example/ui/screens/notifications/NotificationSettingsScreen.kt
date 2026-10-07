package com.example.ui.screens.notifications

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.core.content.ContextCompat
import com.example.notification.NotificationHelper
import com.example.ui.components.AjustaTopBar
import com.example.ui.components.ThemeSelectorRow
import com.example.ui.components.ThemeToggleButton
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

data class ScheduledReminderItem(
    val id: Int,
    val title: String,
    val description: String,
    val scheduledFor: String
)

@Composable
fun NotificationSettingsScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // Permission state
    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
        if (isGranted) {
            Toast.makeText(context, "Notificações ativadas com sucesso!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Permissão de notificação negada.", Toast.LENGTH_SHORT).show()
        }
    }

    var repairRemindersEnabled by remember { mutableStateOf(true) }
    var tipsRemindersEnabled by remember { mutableStateOf(true) }

    val activeReminders = remember {
        mutableStateListOf(
            ScheduledReminderItem(
                id = 3001,
                title = "Calça de Alfaiataria (Barra)",
                description = "Verificar com o Ateliê se a prova está pronta.",
                scheduledFor = "Amanhã às 14:00"
            ),
            ScheduledReminderItem(
                id = 3002,
                title = "Dica Semanal: Cuidados com Linho",
                description = "Como passar e armazenar linho puro sem quebrar a fibra.",
                scheduledFor = "Sexta-feira às 10:00"
            )
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AjustaTopBar(
                title = "Lembretes & Notificações",
                subtitle = "Progresso de reparos e dicas de costura",
                onBackClick = onBack,
                actions = {
                    com.example.ui.components.ThemeToggleButton()
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Permission Banner (if not granted on Android 13+)
            if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        border = BorderStroke(1.dp, AtelierBrass),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = AtelierBrass,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Permissão Necessária",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Para ser avisado sobre o prazo de retirada de roupas em ateliês e receber dicas de reparo, autorize o envio de notificações.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("request_notification_permission_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Ativar Notificações no Dispositivo")
                            }
                        }
                    }
                }
            } else {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = RiskLowContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Outlined.CheckCircle, contentDescription = null, tint = RiskLow, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Notificações locais ativas e sincronizadas com seu ateliê.",
                                style = MaterialTheme.typography.bodySmall,
                                color = RiskLow,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Section 1: Lembretes de Reparos em Andamento
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Reparos em Andamento",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Alertas sobre prazos de prova e retirada de peças no ateliê.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = repairRemindersEnabled,
                                onCheckedChange = { repairRemindersEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                    checkedTrackColor = AtelierBrass,
                                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                modifier = Modifier.testTag("switch_repair_reminders")
                            )
                        }

                        if (repairRemindersEnabled) {
                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "AÇÕES DE LEMBRETE RÁPIDO",
                                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp, fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        NotificationHelper.showRepairNotification(
                                            context = context,
                                            title = "🪡 Ajusta: Peça Pronta no Ateliê!",
                                            message = "Seu Blazer de Lã teve o ajuste de manga concluído e está disponível para retirada."
                                        )
                                        Toast.makeText(context, "Notificação de teste enviada!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("btn_test_repair_notification_now"),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, AtelierBrass)
                                ) {
                                    Text("Testar Agora", color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.labelSmall)
                                }

                                OutlinedButton(
                                    onClick = {
                                        NotificationHelper.scheduleReminder(
                                            context = context,
                                            title = "⏰ Ajusta: Lembrete de Prova",
                                            message = "Hora de verificar o andamento do ajuste da sua calça jeans!",
                                            delaySeconds = 10,
                                            isTip = false
                                        )
                                        activeReminders.add(
                                            0,
                                            ScheduledReminderItem(
                                                id = System.currentTimeMillis().toInt(),
                                                title = "Calça Jeans (Ajuste de Cós)",
                                                description = "Lembrete de verificação com a costureira.",
                                                scheduledFor = "Em 10 segundos"
                                            )
                                        )
                                        Toast.makeText(context, "Lembrete agendado para 10 segundos!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("btn_schedule_repair_test"),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                                ) {
                                    Text("Agendar (10s)", color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }

            // Section 2: Dicas & Tutoriais Agendados
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Dicas de Costura Agendadas",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Receba técnicas de alfaiataria manual e segredos de costura caseira.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = tipsRemindersEnabled,
                                onCheckedChange = { tipsRemindersEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                    checkedTrackColor = AtelierBrass,
                                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                modifier = Modifier.testTag("switch_tips_reminders")
                            )
                        }

                        if (tipsRemindersEnabled) {
                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "AÇÕES DE DICA PROGRAMADA",
                                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp, fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        val tipsList = listOf(
                                            "💡 Dica do Ateliê: Passe sabonete seco em vez de giz comum para marcar tecido escuro. O sabonete desliza macio e sai 100% com vapor!",
                                            "🪡 Segredo de Alfaiate: Para botões não caírem, faça a haste de linha enrolada entre o botão e o tecido com a ajuda de um palito.",
                                            "⚡ Zíper emperrado? Esfregue grafite de lápis macio (2B) nos dentes do zíper para lubrificação seca instantânea."
                                        )
                                        NotificationHelper.showSewingTipNotification(
                                            context = context,
                                            title = "✨ Dica de Costura do Dia",
                                            message = tipsList.random()
                                        )
                                        Toast.makeText(context, "Dica de costura enviada!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("btn_test_tip_notification_now"),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, AtelierBrass)
                                ) {
                                    Text("Enviar Dica Agora", color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.labelSmall)
                                }

                                OutlinedButton(
                                    onClick = {
                                        NotificationHelper.scheduleReminder(
                                            context = context,
                                            title = "🧵 Ajusta: Dica Semanal Agendada",
                                            message = "Aprenda a fazer ponto invisível à mão em barras de calça social!",
                                            delaySeconds = 15,
                                            isTip = true
                                        )
                                        activeReminders.add(
                                            0,
                                            ScheduledReminderItem(
                                                id = System.currentTimeMillis().toInt(),
                                                title = "Dica: Ponto Invisível Manual",
                                                description = "Tutorial passo a passo para fazer barra sem máquina.",
                                                scheduledFor = "Em 15 segundos"
                                            )
                                        )
                                        Toast.makeText(context, "Dica agendada para daqui a 15 segundos!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("btn_schedule_tip_test"),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                                ) {
                                    Text("Agendar Dica (15s)", color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }

            // Section 3: Lembretes Ativos
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LEMBRETES PROGRAMADOS (${activeReminders.size})",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp, fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(activeReminders.size) { index ->
                val reminder = activeReminders[index]
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reminder_item_${reminder.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.Top, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surface),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Alarm,
                                    contentDescription = null,
                                    tint = AtelierBrass,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = reminder.title,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = reminder.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "⏰ Programado para: ${reminder.scheduledFor}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AtelierBrass
                                )
                            }
                        }

                        Button(
                            onClick = {
                                NotificationHelper.cancelReminder(context, reminder.id)
                                activeReminders.removeAt(index)
                                Toast.makeText(context, "Lembrete cancelado.", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(4.dp)
                        ) {
                            Text("Excluir", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            // Theme Selector Row inside settings
            item {
                Spacer(modifier = Modifier.height(8.dp))
                ThemeSelectorRow()
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
