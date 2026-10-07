package com.example.ui.screens.professional

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.repository.ProfessionalRepository
import com.example.model.OrderStatus
import com.example.model.ProfessionalOrder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class ProfessionalViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProfessionalRepository(
        AppDatabase.getDatabase(application).professionalOrderDao()
    )

    val orders: StateFlow<List<ProfessionalOrder>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), getInitialSampleOrders())

    val currentOrder = MutableStateFlow<ProfessionalOrder?>(null)

    fun selectOrder(order: ProfessionalOrder) {
        currentOrder.value = order
    }

    fun saveNewOrder(
        clientName: String,
        clientPhone: String,
        garmentType: String,
        clientRequest: String,
        preliminaryDiagnosis: String,
        agreedPrice: Double?,
        measurements: Map<String, String>,
        complexity: String = "Média",
        onSaved: (ProfessionalOrder) -> Unit
    ) {
        val defaultSteps = listOf(
            "1. Avaliar construção e desfiamento do tecido.",
            "2. Verificar margem interna de costura e folga.",
            "3. Verificar zíper/cós/acabamento original.",
            "4. Marcar com alfinetes e giz no corpo do cliente.",
            "5. Fazer prova para validação de vestibilidade.",
            "6. Executar corte e costura técnica definitiva.",
            "7. Reavaliar alinhamento e assentar a ferro a vapor.",
            "8. Finalizar pespontos e inspecionar acabamento."
        )

        val defaultAttention = listOf(
            "Linha de pesponto com tonalidade idêntica à de fábrica.",
            "Cuidado para não esticar a fibra durante a costura.",
            "Verificar simetria bilateral após a prova."
        )

        val newOrder = ProfessionalOrder(
            id = UUID.randomUUID().toString(),
            clientName = clientName,
            clientPhone = clientPhone,
            garmentType = garmentType,
            clientRequest = clientRequest,
            preliminaryDiagnosis = preliminaryDiagnosis,
            procedureSteps = defaultSteps,
            materialsNeeded = "Linha pesponto 100% poliéster, entretela fina, agulha 90/14.",
            complexity = complexity,
            attentionPoints = defaultAttention,
            agreedPrice = agreedPrice,
            measurements = measurements,
            status = OrderStatus.ORCAMENTO,
            createdAt = System.currentTimeMillis()
        )

        viewModelScope.launch {
            repository.saveOrder(newOrder)
            currentOrder.value = newOrder
            onSaved(newOrder)
        }
    }

    fun updateOrder(order: ProfessionalOrder) {
        currentOrder.value = order
        viewModelScope.launch {
            repository.updateOrder(order)
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        val currentList = orders.value
        val target = currentList.find { it.id == orderId } ?: return
        val updated = target.copy(status = newStatus)
        updateOrder(updated)
    }

    private fun getInitialSampleOrders(): List<ProfessionalOrder> {
        return listOf(
            ProfessionalOrder(
                id = "order_sample_1",
                clientName = "Mariana Alencar",
                clientPhone = "(11) 98765-4321",
                garmentType = "Vestido de festa seda",
                clientRequest = "Ajustar alças e encurtar barra com cauda",
                preliminaryDiagnosis = "Seda pura com forro duplo. Exige corte milimétrico e bainha de lenço.",
                procedureSteps = listOf(
                    "1. Avaliar construção e forro de seda.",
                    "2. Verificar margem de costura no decote.",
                    "3. Verificar zíper invisível lateral.",
                    "4. Marcar altura da barra com salto alto definitivo.",
                    "5. Fazer prova com a cliente.",
                    "6. Executar bainha de lenço à mão.",
                    "7. Reavaliar caimento no manequim.",
                    "8. Finalizar e passar a vapor brando."
                ),
                materialsNeeded = "Fio de seda nº 100, agulha microtex 60/8.",
                complexity = "Alta complexidade",
                attentionPoints = listOf("Tecido escorrega; não utilizar alfinetes grossos que marquem a seda."),
                agreedPrice = 140.0,
                measurements = mapOf("Busto" to "88 cm", "Cintura" to "68 cm", "Comprimento" to "142 cm"),
                status = OrderStatus.EM_ANDAMENTO
            ),
            ProfessionalOrder(
                id = "order_sample_2",
                clientName = "Carlos Eduardo Souza",
                clientPhone = "(11) 97123-8899",
                garmentType = "Calça alfaiataria linho",
                clientRequest = "Aperto de cintura e fazer barra italiana de 4 cm",
                preliminaryDiagnosis = "Linho puro bege. Folga de 4 cm no cós traseiro.",
                procedureSteps = listOf(
                    "1. Avaliar construção do cós entretelado.",
                    "2. Verificar margem interna traseira.",
                    "3. Verificar presilhas de cinto.",
                    "4. Marcar dobra da barra italiana.",
                    "5. Fazer prova de assentamento.",
                    "6. Executar pence traseira e barra italiana dobrada.",
                    "7. Reavaliar vincos dianteiros com ferro.",
                    "8. Finalizar entrega."
                ),
                materialsNeeded = "Linha algodão bege e entretela de cós.",
                complexity = "Média",
                attentionPoints = listOf("Desfia facilmente. Chulear bordas cortadas."),
                agreedPrice = 85.0,
                measurements = mapOf("Cintura" to "86 cm", "Gancho" to "28 cm", "Comprimento barra" to "102 cm"),
                status = OrderStatus.PROVA_PENDENTE
            ),
            ProfessionalOrder(
                id = "order_sample_3",
                clientName = "Helena Vasconcelos",
                clientPhone = "(11) 99887-1122",
                garmentType = "Trench Coat clássico",
                clientRequest = "Ajustar ombros e encurtar mangas com martingale",
                preliminaryDiagnosis = "Gabardine de algodão impermeável com forro tartan.",
                procedureSteps = listOf(
                    "1. Desmanchar cava e ombreira estruturada.",
                    "2. Recolocar manga recuada 1.5 cm.",
                    "3. Reposicionar fivelas de martingale.",
                    "4. Fazer prova final no manequim.",
                    "5. Acabamento térmico e impermeabilização."
                ),
                materialsNeeded = "Linha torçal impermeável, linha de algodão egípcio.",
                complexity = "Alta complexidade",
                attentionPoints = listOf("Cuidado com marcas de agulha no tecido impermeável."),
                agreedPrice = 195.0,
                measurements = mapOf("Ombro a ombro" to "41 cm", "Manga" to "58 cm"),
                status = OrderStatus.CONCLUIDO
            ),
            ProfessionalOrder(
                id = "order_sample_4",
                clientName = "Lucas Fontes",
                clientPhone = "(11) 96543-2109",
                garmentType = "Jaqueta couro bovino",
                clientRequest = "Troca de zíper frontal metálico YKK e forro interno",
                preliminaryDiagnosis = "Couro pesado com zíper dente de metal tratorado nº 8.",
                procedureSteps = listOf(
                    "1. Descortiçar forro na barra inferior.",
                    "2. Remover zíper original sem rasgar o couro.",
                    "3. Encaixar zíper novo com fita adesiva de couro.",
                    "4. Costurar em máquina transporte duplo com agulha ponta lança.",
                    "5. Fechar forro com ponto invisível."
                ),
                materialsNeeded = "Zíper YKK metal envelhecido 65 cm, agulha de couro 110/18.",
                complexity = "Alta complexidade",
                attentionPoints = listOf("Furos no couro são permanentes; sem margem para erro de agulha."),
                agreedPrice = 160.0,
                measurements = mapOf("Comprimento frontal" to "65 cm"),
                status = OrderStatus.ORCAMENTO
            )
        )
    }
}
