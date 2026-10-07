package com.example.ui.screens.flow

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.DiagnosticEngine
import com.example.ai.GarmentVisionAnalyzer
import com.example.data.demo.DemoDataProvider
import com.example.data.local.AppDatabase
import com.example.data.repository.GarmentRepository
import com.example.model.ConfidenceLevel
import com.example.model.DiagnosticResult
import com.example.model.DiagnosticStatus
import com.example.model.GarmentAnalysis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AnalysisUiState {
    object Idle : AnalysisUiState
    object Loading : AnalysisUiState
    data class Success(val analysis: GarmentAnalysis) : AnalysisUiState
    data class LowConfidence(val analysis: GarmentAnalysis) : AnalysisUiState
    data class Error(val message: String) : AnalysisUiState
}

class FlowViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = GarmentRepository(
        dao = database.garmentAnalysisDao(),
        diagnosticDao = database.diagnosticResultDao()
    )

    // Flow input state
    val selectedPhotoUris = MutableStateFlow<List<Uri>>(emptyList())
    val problemCategory = MutableStateFlow("Ficou larga")
    val problemDescription = MutableStateFlow("")
    val selectedFabric = MutableStateFlow<String?>("Jeans")
    val selectedPricePaid = MutableStateFlow<Double?>(120.0)

    // Analysis result
    private val _uiState = MutableStateFlow<AnalysisUiState>(AnalysisUiState.Idle)
    val uiState: StateFlow<AnalysisUiState> = _uiState.asStateFlow()

    // Current active analysis for result/ticket screens
    val currentAnalysis = MutableStateFlow<GarmentAnalysis?>(null)
    val currentDiagnosticResult = MutableStateFlow<DiagnosticResult?>(null)

    fun addPhotoUri(uri: Uri) {
        val current = selectedPhotoUris.value.toMutableList()
        if (!current.contains(uri)) {
            current.add(uri)
            selectedPhotoUris.value = current
        }
    }

    fun removePhotoUri(uri: Uri) {
        selectedPhotoUris.value = selectedPhotoUris.value.filter { it != uri }
    }

    fun setProblemCategory(category: String) {
        problemCategory.value = category
    }

    fun setProblemDescription(desc: String) {
        problemDescription.value = desc
    }

    fun setFabric(fabric: String?) {
        selectedFabric.value = fabric
    }

    fun setPricePaid(price: Double?) {
        selectedPricePaid.value = price
    }

    fun loadDemoAnalysis(demoId: String) {
        val demo = DemoDataProvider.getById(demoId) ?: DemoDataProvider.demoCases.first()
        currentAnalysis.value = demo
        _uiState.value = AnalysisUiState.Success(demo)
    }

    fun startAnalysis() {
        viewModelScope.launch {
            _uiState.value = AnalysisUiState.Loading

            try {
                // Decode bitmaps from URIs (if any)
                val bitmaps = mutableListOf<Bitmap>()
                val context = getApplication<Application>().applicationContext
                for (uri in selectedPhotoUris.value) {
                    try {
                        context.contentResolver.openInputStream(uri)?.use { stream ->
                            val bmp = BitmapFactory.decodeStream(stream)
                            if (bmp != null) bitmaps.add(bmp)
                        }
                    } catch (_: Exception) {}
                }

                val diagnosticResult = GarmentVisionAnalyzer.analyze(
                    bitmaps = bitmaps,
                    photoUris = selectedPhotoUris.value.map { it.toString() },
                    problemCategory = problemCategory.value,
                    problemDescription = problemDescription.value,
                    fabric = selectedFabric.value,
                    pricePaid = selectedPricePaid.value
                )

                currentDiagnosticResult.value = diagnosticResult
                val analysis = diagnosticResult.analysis
                currentAnalysis.value = analysis

                if (diagnosticResult.status == DiagnosticStatus.LOW_CONFIDENCE || analysis.confidence == ConfidenceLevel.LOW) {
                    _uiState.value = AnalysisUiState.LowConfidence(analysis)
                } else {
                    _uiState.value = AnalysisUiState.Success(analysis)
                }
            } catch (e: Exception) {
                // Fallback to deterministic engine
                val fallback = DiagnosticEngine.generateDeterministicAnalysis(
                    garmentName = "Peça de vestuário",
                    problemDescription = problemDescription.value,
                    problemCategory = problemCategory.value,
                    fabric = selectedFabric.value,
                    pricePaid = selectedPricePaid.value,
                    photoUris = selectedPhotoUris.value.map { it.toString() }
                )
                val fallbackResult = DiagnosticResult(
                    analysis = fallback,
                    status = DiagnosticStatus.FALLBACK,
                    technicalVerdict = fallback.probableCause
                )
                currentDiagnosticResult.value = fallbackResult
                currentAnalysis.value = fallback
                _uiState.value = AnalysisUiState.Success(fallback)
            }
        }
    }

    fun saveCurrentAnalysis(ticketNotes: String = "", onSaved: () -> Unit = {}) {
        val analysis = currentAnalysis.value ?: return
        val diagnosticResult = currentDiagnosticResult.value
        viewModelScope.launch {
            if (diagnosticResult != null) {
                repository.saveDiagnosticResult(
                    result = diagnosticResult,
                    isTicket = ticketNotes.isNotBlank(),
                    ticketNotes = ticketNotes
                )
            } else {
                repository.saveAnalysis(
                    analysis = analysis,
                    isTicket = ticketNotes.isNotBlank(),
                    ticketNotes = ticketNotes
                )
            }
            onSaved()
        }
    }

    fun resetFlow() {
        selectedPhotoUris.value = emptyList()
        problemCategory.value = "Ficou larga"
        problemDescription.value = ""
        selectedFabric.value = null
        selectedPricePaid.value = null
        _uiState.value = AnalysisUiState.Idle
    }
}
