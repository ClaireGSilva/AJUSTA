package com.example.ui.screens.professional

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ProfessionalOrder
import com.example.ui.components.AjustaTopBar
import com.example.ui.theme.AtelierBlack
import com.example.ui.theme.AtelierBrass
import com.example.ui.theme.AtelierLightBorder
import com.example.ui.theme.AtelierLinen
import com.example.ui.theme.AtelierOffBlack
import com.example.ui.theme.AtelierWarmGray
import com.example.ui.theme.AtelierWarmWhite

@Composable
fun NewOrderScreen(
    viewModel: ProfessionalViewModel,
    onBack: () -> Unit,
    onOrderCreated: (ProfessionalOrder) -> Unit
) {
    var clientName by remember { mutableStateOf("") }
    var clientPhone by remember { mutableStateOf("") }
    var garmentType by remember { mutableStateOf("") }
    var clientRequest by remember { mutableStateOf("") }
    var customPrice by remember { mutableStateOf("") }
    var waistMeasurement by remember { mutableStateOf("") }
    var hipMeasurement by remember { mutableStateOf("") }
    var lengthMeasurement by remember { mutableStateOf("") }
    var diagnosisText by remember { mutableStateOf("") }

    Scaffold(
        containerColor = AtelierWarmWhite,
        topBar = {
            AjustaTopBar(
                title = "Novo Atendimento",
                subtitle = "Entrada de Peça no Atelier",
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

            Text(
                text = "Dados do Cliente",
                style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Serif),
                color = AtelierBlack
            )

            OutlinedTextField(
                value = clientName,
                onValueChange = { clientName = it },
                label = { Text("Nome do cliente") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("new_order_client_name"),
                shape = RoundedCornerShape(12.dp),
                colors = defaultFieldColors()
            )

            OutlinedTextField(
                value = clientPhone,
                onValueChange = { clientPhone = it },
                label = { Text("Telefone / WhatsApp") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("new_order_client_phone"),
                shape = RoundedCornerShape(12.dp),
                colors = defaultFieldColors()
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Identificação da Peça & Pedido",
                style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Serif),
                color = AtelierBlack
            )

            OutlinedTextField(
                value = garmentType,
                onValueChange = { garmentType = it },
                label = { Text("Tipo da peça (ex: Calça jeans, Vestido, Blazer)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("new_order_garment_type"),
                shape = RoundedCornerShape(12.dp),
                colors = defaultFieldColors()
            )

            OutlinedTextField(
                value = clientRequest,
                onValueChange = { clientRequest = it },
                label = { Text("Solicitação do cliente (o que precisa ajustar)") },
                placeholder = { Text("Ex: Reduzir cintura 4cm e encurtar barra...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .testTag("new_order_client_request"),
                shape = RoundedCornerShape(12.dp),
                colors = defaultFieldColors()
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Medidas Registradas (cm)",
                style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Serif),
                color = AtelierBlack
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = waistMeasurement,
                    onValueChange = { waistMeasurement = it },
                    label = { Text("Cintura") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("new_order_waist"),
                    shape = RoundedCornerShape(12.dp),
                    colors = defaultFieldColors()
                )
                OutlinedTextField(
                    value = hipMeasurement,
                    onValueChange = { hipMeasurement = it },
                    label = { Text("Quadril") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("new_order_hip"),
                    shape = RoundedCornerShape(12.dp),
                    colors = defaultFieldColors()
                )
                OutlinedTextField(
                    value = lengthMeasurement,
                    onValueChange = { lengthMeasurement = it },
                    label = { Text("Comprim.") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("new_order_length"),
                    shape = RoundedCornerShape(12.dp),
                    colors = defaultFieldColors()
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Orçamento & Diagnóstico Preliminar",
                style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Serif),
                color = AtelierBlack
            )

            OutlinedTextField(
                value = customPrice,
                onValueChange = { customPrice = it },
                label = { Text("Preço Cobrado (R$)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("new_order_price"),
                shape = RoundedCornerShape(12.dp),
                colors = defaultFieldColors()
            )

            OutlinedTextField(
                value = diagnosisText,
                onValueChange = { diagnosisText = it },
                label = { Text("Diagnóstico preliminar da profissional") },
                placeholder = { Text("Ex: Cós anatômico com presilha central a ser desmontada.") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .testTag("new_order_diagnosis"),
                shape = RoundedCornerShape(12.dp),
                colors = defaultFieldColors()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    val priceNum = customPrice.toDoubleOrNull()
                    val measurements = mutableMapOf<String, String>()
                    if (waistMeasurement.isNotBlank()) measurements["Cintura"] = "$waistMeasurement cm"
                    if (hipMeasurement.isNotBlank()) measurements["Quadril"] = "$hipMeasurement cm"
                    if (lengthMeasurement.isNotBlank()) measurements["Comprimento"] = "$lengthMeasurement cm"

                    viewModel.saveNewOrder(
                        clientName = clientName.ifBlank { "Cliente Sem Nome" },
                        clientPhone = clientPhone,
                        garmentType = garmentType.ifBlank { "Peça de Roupa" },
                        clientRequest = clientRequest.ifBlank { "Ajuste de caimento" },
                        preliminaryDiagnosis = diagnosisText.ifBlank { "Intervenção técnica de modelagem e costura." },
                        agreedPrice = priceNum,
                        measurements = measurements,
                        onSaved = onOrderCreated
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("new_order_submit_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AtelierBlack)
            ) {
                Text(
                    text = "Gerar Ficha Técnica Profissional →",
                    style = MaterialTheme.typography.titleSmall,
                    color = AtelierWarmWhite
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun defaultFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    focusedBorderColor = AtelierBrass,
    unfocusedBorderColor = AtelierLightBorder,
    cursorColor = AtelierBlack
)
