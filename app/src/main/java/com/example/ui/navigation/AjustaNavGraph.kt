package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.flow.FlowViewModel
import com.example.ui.screens.flow.Step1PhotoScreen
import com.example.ui.screens.flow.Step2ProblemScreen
import com.example.ui.screens.flow.Step3ContextScreen
import com.example.ui.screens.camera.CameraCaptureScreen
import com.example.ui.screens.chat.AtelierChatScreen
import com.example.ui.screens.diy.DiyRepairScreen
import com.example.ui.screens.flow.Step4AnalysisScreen
import com.example.ui.screens.history.HistoryScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.knowledge.KnowledgeScreen
import com.example.ui.screens.notifications.NotificationSettingsScreen
import com.example.ui.screens.professional.NewOrderScreen
import com.example.ui.screens.professional.ProfessionalDashboardScreen
import com.example.ui.screens.professional.ProfessionalViewModel
import com.example.ui.screens.professional.TechnicalSheetScreen
import com.example.ui.screens.professionals.FindProfessionalScreen
import com.example.ui.screens.result.DiagnosticResultScreen
import com.example.ui.screens.ticket.ServiceTicketScreen

object AjustaDestinations {
    const val HOME = "home"
    const val STEP1_PHOTO = "step1_photo"
    const val CAMERA_CAPTURE = "camera_capture"
    const val STEP2_PROBLEM = "step2_problem"
    const val STEP3_CONTEXT = "step3_context"
    const val STEP4_ANALYSIS = "step4_analysis"
    const val RESULT = "result"
    const val SERVICE_TICKET = "service_ticket"
    const val HISTORY = "history"
    const val KNOWLEDGE = "knowledge"
    const val FIND_PROFESSIONAL = "find_professional"
    const val CHAT = "chat"
    const val DIY_REPAIRS = "diy_repairs"
    const val NOTIFICATIONS = "notifications"
    const val PRO_DASHBOARD = "pro_dashboard"
    const val PRO_NEW_ORDER = "pro_new_order"
    const val PRO_TECH_SHEET = "pro_tech_sheet"
}

@Composable
fun AjustaNavGraph(
    navController: NavHostController = rememberNavController(),
    flowViewModel: FlowViewModel = viewModel(),
    proViewModel: ProfessionalViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = AjustaDestinations.HOME
    ) {
        // Home
        composable(AjustaDestinations.HOME) {
            HomeScreen(
                onStartFlow = { mode ->
                    flowViewModel.resetFlow()
                    if (mode == "adjust") {
                        flowViewModel.setProblemCategory("Ficou larga")
                    }
                    navController.navigate(AjustaDestinations.STEP1_PHOTO)
                },
                onOpenDemo = { demoId ->
                    flowViewModel.loadDemoAnalysis(demoId)
                    navController.navigate(AjustaDestinations.RESULT)
                },
                onNavigateToFindProfessional = {
                    navController.navigate(AjustaDestinations.FIND_PROFESSIONAL)
                },
                onNavigateToProfessional = {
                    navController.navigate(AjustaDestinations.PRO_DASHBOARD)
                },
                onNavigateToHistory = {
                    navController.navigate(AjustaDestinations.HISTORY)
                },
                onNavigateToKnowledge = {
                    navController.navigate(AjustaDestinations.KNOWLEDGE)
                },
                onNavigateToChat = {
                    navController.navigate(AjustaDestinations.CHAT)
                },
                onNavigateToDiyRepairs = {
                    navController.navigate(AjustaDestinations.DIY_REPAIRS)
                },
                onNavigateToNotifications = {
                    navController.navigate(AjustaDestinations.NOTIFICATIONS)
                }
            )
        }

        // Step 1: Photo
        composable(AjustaDestinations.STEP1_PHOTO) {
            Step1PhotoScreen(
                viewModel = flowViewModel,
                onOpenLiveCamera = { navController.navigate(AjustaDestinations.CAMERA_CAPTURE) },
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate(AjustaDestinations.STEP2_PROBLEM) }
            )
        }

        // CameraX Live Capture
        composable(AjustaDestinations.CAMERA_CAPTURE) {
            val photoUris by flowViewModel.selectedPhotoUris.collectAsState()
            CameraCaptureScreen(
                currentPhotoCount = photoUris.size,
                maxPhotos = 3,
                onPhotoCaptured = { uri ->
                    flowViewModel.addPhotoUri(uri)
                },
                onDone = {
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Step 2: Problem
        composable(AjustaDestinations.STEP2_PROBLEM) {
            Step2ProblemScreen(
                viewModel = flowViewModel,
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate(AjustaDestinations.STEP3_CONTEXT) }
            )
        }

        // Step 3: Context
        composable(AjustaDestinations.STEP3_CONTEXT) {
            Step3ContextScreen(
                viewModel = flowViewModel,
                onBack = { navController.popBackStack() },
                onStartAnalysis = {
                    flowViewModel.startAnalysis()
                    navController.navigate(AjustaDestinations.STEP4_ANALYSIS)
                }
            )
        }

        // Step 4: Analysis
        composable(AjustaDestinations.STEP4_ANALYSIS) {
            Step4AnalysisScreen(
                viewModel = flowViewModel,
                onBack = { navController.popBackStack() },
                onSuccess = {
                    navController.navigate(AjustaDestinations.RESULT) {
                        popUpTo(AjustaDestinations.STEP4_ANALYSIS) { inclusive = true }
                    }
                }
            )
        }

        // Result
        composable(AjustaDestinations.RESULT) {
            val analysis by flowViewModel.currentAnalysis.collectAsState()
            if (analysis != null) {
                DiagnosticResultScreen(
                    analysis = analysis!!,
                    viewModel = flowViewModel,
                    onBack = {
                        if (!navController.popBackStack()) {
                            navController.navigate(AjustaDestinations.HOME)
                        }
                    },
                    onCreateServiceTicket = {
                        navController.navigate(AjustaDestinations.SERVICE_TICKET)
                    },
                    onFindTailors = {
                        navController.navigate(AjustaDestinations.FIND_PROFESSIONAL)
                    }
                )
            } else {
                navController.navigate(AjustaDestinations.HOME) {
                    popUpTo(AjustaDestinations.HOME) { inclusive = true }
                }
            }
        }

        // Service Ticket
        composable(AjustaDestinations.SERVICE_TICKET) {
            val analysis by flowViewModel.currentAnalysis.collectAsState()
            if (analysis != null) {
                ServiceTicketScreen(
                    analysis = analysis!!,
                    viewModel = flowViewModel,
                    onBack = { navController.popBackStack() },
                    onFindTailor = {
                        navController.navigate(AjustaDestinations.FIND_PROFESSIONAL)
                    }
                )
            } else {
                navController.navigate(AjustaDestinations.HOME)
            }
        }

        // Find Professional (Tailors and Seamstresses Directory & Maps)
        composable(AjustaDestinations.FIND_PROFESSIONAL) {
            val analysis by flowViewModel.currentAnalysis.collectAsState()
            FindProfessionalScreen(
                currentTicketAnalysis = analysis,
                onBack = { navController.popBackStack() }
            )
        }

        // History & Analyses Dashboard
        composable(AjustaDestinations.HISTORY) {
            HistoryScreen(
                onBack = { navController.popBackStack() },
                onSelectAnalysis = { selected ->
                    flowViewModel.currentAnalysis.value = selected
                    navController.navigate(AjustaDestinations.RESULT)
                },
                onOpenTicket = { selected ->
                    flowViewModel.currentAnalysis.value = selected
                    navController.navigate(AjustaDestinations.SERVICE_TICKET)
                },
                onStartNewAnalysis = {
                    flowViewModel.resetFlow()
                    navController.navigate(AjustaDestinations.STEP1_PHOTO)
                }
            )
        }

        // Knowledge Guide
        composable(AjustaDestinations.KNOWLEDGE) {
            KnowledgeScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // Atelier Chat (Multi-turn Gemini Chatbot)
        composable(AjustaDestinations.CHAT) {
            AtelierChatScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // DIY Repair Tutorials (Costura manual e ajustes simples com Gemini)
        composable(AjustaDestinations.DIY_REPAIRS) {
            DiyRepairScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // Local Notifications (Progresso de Reparos & Dicas de Costura)
        composable(AjustaDestinations.NOTIFICATIONS) {
            NotificationSettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // Professional Dashboard
        composable(AjustaDestinations.PRO_DASHBOARD) {
            ProfessionalDashboardScreen(
                viewModel = proViewModel,
                onBack = { navController.popBackStack() },
                onNewOrder = { navController.navigate(AjustaDestinations.PRO_NEW_ORDER) },
                onOpenOrder = { order ->
                    proViewModel.selectOrder(order)
                    navController.navigate(AjustaDestinations.PRO_TECH_SHEET)
                }
            )
        }

        // Professional New Order
        composable(AjustaDestinations.PRO_NEW_ORDER) {
            NewOrderScreen(
                viewModel = proViewModel,
                onBack = { navController.popBackStack() },
                onOrderCreated = { order ->
                    proViewModel.selectOrder(order)
                    navController.navigate(AjustaDestinations.PRO_TECH_SHEET) {
                        popUpTo(AjustaDestinations.PRO_NEW_ORDER) { inclusive = true }
                    }
                }
            )
        }

        // Professional Technical Sheet
        composable(AjustaDestinations.PRO_TECH_SHEET) {
            val order by proViewModel.currentOrder.collectAsState()
            if (order != null) {
                TechnicalSheetScreen(
                    order = order!!,
                    viewModel = proViewModel,
                    onBack = { navController.popBackStack() }
                )
            } else {
                navController.navigate(AjustaDestinations.PRO_DASHBOARD)
            }
        }
    }
}
