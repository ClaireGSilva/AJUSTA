package com.example.ui.screens.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.ai.ChatMessage
import com.example.ai.ChatModelOption
import com.example.ai.ChatSender
import com.example.ai.GeminiService
import com.example.ui.components.AjustaTopBar
import com.example.ui.theme.AtelierBlack
import com.example.ui.theme.AtelierBrass
import com.example.ui.theme.AtelierBrassContainer
import com.example.ui.theme.AtelierLightBorder
import com.example.ui.theme.AtelierLinen
import com.example.ui.theme.AtelierOffBlack
import com.example.ui.theme.AtelierWarmGray
import com.example.ui.theme.AtelierWarmWhite
import kotlinx.coroutines.launch

@Composable
fun AtelierChatScreen(
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var selectedModel by remember { mutableStateOf(ChatModelOption.FLASH) }
    var inputText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                sender = ChatSender.ATELIER_AI,
                text = "Olá! Sou a Consultora Mestre em Alfaiataria do Ajusta. Posso tirar dúvidas sobre caimento, viabilidade de reformas, tipos de costura e estimativa de complexidade para suas roupas. Como posso te ajudar hoje?"
            )
        )
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val quickQuestions = listOf(
        "Como diminuir a cintura de uma calça jeans?",
        "Bainha invisível ou italiana em calça social?",
        "Vale a pena ajustar um blazer nos ombros?",
        "Como consertar um zíper travado?"
    )

    fun sendMessage(userText: String) {
        if (userText.isBlank() || isLoading) return
        val userMsg = ChatMessage(sender = ChatSender.USER, text = userText.trim())
        messages.add(userMsg)
        inputText = ""
        isLoading = true

        scope.launch {
            try {
                val reply = GeminiService.sendChatMessage(
                    messages = messages.toList(),
                    model = selectedModel.modelId
                )
                messages.add(ChatMessage(sender = ChatSender.ATELIER_AI, text = reply))
            } catch (e: Exception) {
                // Fallback realistic response based on tailoring knowledge
                val fallbackReply = getFallbackTailorReply(userText)
                messages.add(
                    ChatMessage(
                        sender = ChatSender.ATELIER_AI,
                        text = "$fallbackReply\n\n*(Nota: Resposta fornecida pelo guia técnico de ateliê do Ajusta)*"
                    )
                )
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        containerColor = AtelierWarmWhite,
        topBar = {
            AjustaTopBar(
                title = "Consultora de Ateliê",
                subtitle = "Tire dúvidas técnicas de alfaiataria",
                onBackClick = onBack,
                actions = {
                    IconButton(
                        onClick = {
                            messages.clear()
                            messages.add(
                                ChatMessage(
                                    sender = ChatSender.ATELIER_AI,
                                    text = "Conversa reiniciada. Pode enviar uma nova dúvida de modelagem ou costura!"
                                )
                            )
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reiniciar conversa", tint = AtelierBlack)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .navigationBarsPadding()
        ) {
            // Model Selector Header
            Surface(
                color = Color.White,
                border = BorderStroke(1.dp, AtelierLightBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    Text(
                        text = "MODELO DE INTELIGÊNCIA ARTIFICIAL",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                        color = AtelierWarmGray
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ChatModelOption.entries.forEach { option ->
                            val isSelected = selectedModel == option
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedModel = option },
                                label = { Text("${option.badge} ${option.displayName}") },
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
                    }
                }
            }

            // Quick Questions (if few messages)
            if (messages.size <= 2) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    quickQuestions.forEach { question ->
                        Surface(
                            color = AtelierLinen,
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, AtelierLightBorder),
                            modifier = Modifier.clickable { sendMessage(question) }
                        ) {
                            Text(
                                text = question,
                                style = MaterialTheme.typography.labelSmall,
                                color = AtelierBlack,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // Chat Messages Thread
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(messages) { msg ->
                    ChatBubble(message = msg)
                }

                if (isLoading) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = AtelierBrass
                            )
                            Text(
                                text = "Consultora de ateliê digitando...",
                                style = MaterialTheme.typography.bodySmall,
                                color = AtelierWarmGray
                            )
                        }
                    }
                }
            }

            // Input Bar
            Surface(
                color = Color.White,
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_field"),
                        placeholder = { Text("Pergunte sobre ajustes ou tecidos...", color = AtelierWarmGray, fontSize = 14.sp) },
                        maxLines = 3,
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AtelierBlack,
                            unfocusedBorderColor = AtelierLightBorder,
                            focusedContainerColor = AtelierWarmWhite,
                            unfocusedContainerColor = AtelierWarmWhite
                        )
                    )

                    IconButton(
                        onClick = { sendMessage(inputText) },
                        enabled = inputText.isNotBlank() && !isLoading,
                        modifier = Modifier
                            .size(46.dp)
                            .background(
                                color = if (inputText.isNotBlank() && !isLoading) AtelierBlack else AtelierLinen,
                                shape = CircleShape
                            )
                            .testTag("chat_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Enviar mensagem",
                            tint = if (inputText.isNotBlank() && !isLoading) AtelierWarmWhite else AtelierWarmGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    val isUser = message.sender == ChatSender.USER

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(AtelierLinen),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = AtelierBrass,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            color = if (isUser) AtelierBlack else Color.White,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            border = if (isUser) null else BorderStroke(1.dp, AtelierLightBorder),
            modifier = Modifier.fillMaxWidth(if (isUser) 0.82f else 0.88f)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                if (!isUser) {
                    Text(
                        text = "Consultora Ajusta",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = AtelierBrass
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isUser) AtelierWarmWhite else AtelierOffBlack,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

private fun getFallbackTailorReply(query: String): String {
    val lower = query.lowercase()
    return when {
        lower.contains("cintura") || lower.contains("cós") -> {
            "Para ajustes no cós: Se a sobra for de até 4 cm, a melhor abordagem é desmanchar o centro das costas e recolocar a presilha central. Se a diferença for maior (5 a 8 cm), recomenda-se dividir a redução entre as laterais e o traseiro para não aproximar demais os bolsos."
        }
        lower.contains("ombro") || lower.contains("blazer") -> {
            "Ajustes de ombro em blazers são procedimentos de alta complexidade estrutural, pois exigem desmanchar a manga, realinhar a ombreira e o entretelamento de crina. Vale a pena principalmente em peças de alfaiataria fina de lã fria ou linho nobre."
        }
        lower.contains("bainha") || lower.contains("barra") -> {
            "Para calças de alfaiataria, a bainha invisível feita à mão é o padrão clássico que não marca a parte externa. A bainha italiana (com dobra virada de 3,5 a 4,5 cm) é excelente para calças de corte reto ou tecidos pesados, dando caimento e peso."
        }
        lower.contains("zíper") -> {
            "Se o zíper travou em tecido fino como seda ou viscose, passe grafite de lápis ou uma vela branca seca nos dentes para lubrificar. Se os dentes estiverem desalinhados ou o cursor abriu, a troca por um zíper invisível de nylon é o procedimento mais seguro e barato."
        }
        else -> {
            "Excelente dúvida! Como consultora técnica, recomendo sempre levar a peça com o calçado ou lingerie que você pretende usar na ocasião. As margens internas de costura são o fator determinante para decidir se a peça pode ser solta ou apenas apertada."
        }
    }
}
