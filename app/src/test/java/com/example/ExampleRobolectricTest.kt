package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Ajusta", appName)
  }

  @Test
  fun `test CameraX output file creation`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val photoFile = com.example.camera.CameraXManager.createOutputPhotoFile(context)
    org.junit.Assert.assertNotNull("Photo file should not be null", photoFile)
    org.junit.Assert.assertTrue("Photo file name should start with AJUSTA_", photoFile.name.startsWith("AJUSTA_"))
    org.junit.Assert.assertTrue("Photo file name should end with .jpg", photoFile.name.endsWith(".jpg"))
  }

  @Test
  fun `test Room Database persistence of GarmentAnalysis and DiagnosticResult`() = kotlinx.coroutines.runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = androidx.room.Room.inMemoryDatabaseBuilder(
      context,
      com.example.data.local.AppDatabase::class.java
    ).allowMainThreadQueries().build()

    val repository = com.example.data.repository.GarmentRepository(
      dao = db.garmentAnalysisDao(),
      diagnosticDao = db.diagnosticResultDao()
    )

    // 1. Create a test GarmentAnalysis
    val analysis = com.example.ai.DiagnosticEngine.generateDeterministicAnalysis(
      garmentName = "Calça jeans",
      problemDescription = "Cós largo na cintura",
      problemCategory = "Ficou larga",
      fabric = "Jeans",
      pricePaid = 180.0,
      photoUris = listOf("content://media/photo1.jpg")
    )

    // 2. Persist GarmentAnalysis in Room
    repository.saveAnalysis(analysis, isTicket = true, ticketNotes = "Ajustar 3 cm na costura central")

    // 3. Retrieve and assert GarmentAnalysis
    val retrievedAnalysis = repository.getAnalysisById(analysis.id)
    org.junit.Assert.assertNotNull("Retrieved analysis should not be null", retrievedAnalysis)
    org.junit.Assert.assertEquals(analysis.id, retrievedAnalysis?.id)
    org.junit.Assert.assertEquals("Calça jeans", retrievedAnalysis?.garmentType)
    org.junit.Assert.assertEquals(analysis.problemCategory, retrievedAnalysis?.problemCategory)
    org.junit.Assert.assertEquals(analysis.priceEstimate.minPrice, retrievedAnalysis?.priceEstimate?.minPrice)
    org.junit.Assert.assertTrue(retrievedAnalysis!!.alterationOptions.isNotEmpty())

    // 4. Create a test DiagnosticResult
    val primaryOption = analysis.alterationOptions.first()
    val plan = com.example.model.AlterationPlan(
      primaryAlteration = primaryOption,
      alternativeAlterations = analysis.alterationOptions.drop(1),
      stepByStepInspection = listOf("Verificar cós", "Marcar pence")
    )
    val diagnosticResult = com.example.model.DiagnosticResult(
      id = java.util.UUID.randomUUID().toString(),
      analysis = analysis,
      status = com.example.model.DiagnosticStatus.SUCCESS,
      alterationPlan = plan,
      technicalVerdict = "Calça jeans • Ajuste de cintura recomendado",
      executionTimeMs = 350L
    )

    // 5. Persist DiagnosticResult in Room
    repository.saveDiagnosticResult(diagnosticResult)

    // 6. Retrieve and assert DiagnosticResult
    val retrievedResult = repository.getDiagnosticResultById(diagnosticResult.id)
    org.junit.Assert.assertNotNull("Retrieved diagnostic result should not be null", retrievedResult)
    org.junit.Assert.assertEquals(diagnosticResult.id, retrievedResult?.id)
    org.junit.Assert.assertEquals(com.example.model.DiagnosticStatus.SUCCESS, retrievedResult?.status)
    org.junit.Assert.assertEquals(diagnosticResult.technicalVerdict, retrievedResult?.technicalVerdict)
    org.junit.Assert.assertEquals(350L, retrievedResult?.executionTimeMs)
    org.junit.Assert.assertEquals(primaryOption.id, retrievedResult?.alterationPlan?.primaryAlteration?.id)

    // 7. Delete test
    repository.deleteDiagnosticResult(diagnosticResult.id)
    org.junit.Assert.assertNull(repository.getDiagnosticResultById(diagnosticResult.id))
    org.junit.Assert.assertNull(repository.getAnalysisById(analysis.id))

    db.close()
  }

  @Test
  fun `test Analyses Dashboard querying and flow stream`() = kotlinx.coroutines.runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = androidx.room.Room.inMemoryDatabaseBuilder(
      context,
      com.example.data.local.AppDatabase::class.java
    ).allowMainThreadQueries().build()

    val repository = com.example.data.repository.GarmentRepository(
      dao = db.garmentAnalysisDao(),
      diagnosticDao = db.diagnosticResultDao()
    )

    // Populate with 2 demo cases
    val case1 = com.example.data.demo.DemoDataProvider.demoCases[0]
    val case2 = com.example.data.demo.DemoDataProvider.demoCases[1]

    repository.saveAnalysis(case1, isTicket = true, ticketNotes = "Ajuste na cintura")
    repository.saveAnalysis(case2, isTicket = false)

    // Flow emission check
    val list = repository.allAnalyses.first()
    org.junit.Assert.assertEquals(2, list.size)
    org.junit.Assert.assertTrue(list.any { it.id == case1.id })
    org.junit.Assert.assertTrue(list.any { it.id == case2.id })

    // Verify ticket flag is preserved
    val retrievedCase1 = list.first { it.id == case1.id }
    org.junit.Assert.assertTrue(retrievedCase1.isTicketGenerated)

    db.close()
  }

  @Test
  fun `test Dashboard search and filter by garment type and date`() = kotlinx.coroutines.runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = androidx.room.Room.inMemoryDatabaseBuilder(
      context,
      com.example.data.local.AppDatabase::class.java
    ).allowMainThreadQueries().build()

    val repository = com.example.data.repository.GarmentRepository(
      dao = db.garmentAnalysisDao(),
      diagnosticDao = db.diagnosticResultDao()
    )

    val oneDayMillis = 24 * 60 * 60 * 1000L
    val now = System.currentTimeMillis()

    // 1. Calça jeans analyzed today
    val jeans = com.example.data.demo.DemoDataProvider.demoCases[0].copy(
      id = "test_jeans_today",
      garmentType = "Calça jeans",
      timestamp = now
    )

    // 2. Vestido de festa analyzed 15 days ago
    val dress = com.example.data.demo.DemoDataProvider.demoCases[1].copy(
      id = "test_dress_past",
      garmentType = "Vestido de festa",
      timestamp = now - (15 * oneDayMillis)
    )

    // 3. Camisa social analyzed 40 days ago
    val shirt = com.example.data.demo.DemoDataProvider.demoCases[2].copy(
      id = "test_shirt_old",
      garmentType = "Camisa social",
      timestamp = now - (40 * oneDayMillis)
    )

    repository.saveAnalysis(jeans)
    repository.saveAnalysis(dress)
    repository.saveAnalysis(shirt)

    val all = repository.allAnalyses.first()
    org.junit.Assert.assertEquals(3, all.size)

    // Test Garment Type filtering
    val jeansOnly = all.filter { it.garmentType.contains("jeans", ignoreCase = true) }
    org.junit.Assert.assertEquals(1, jeansOnly.size)
    org.junit.Assert.assertEquals("Calça jeans", jeansOnly.first().garmentType)

    val dressOnly = all.filter { it.garmentType.contains("vestido", ignoreCase = true) }
    org.junit.Assert.assertEquals(1, dressOnly.size)

    // Test Date range filtering: Last 7 days
    val last7Days = all.filter { it.timestamp >= (now - 7 * oneDayMillis) }
    org.junit.Assert.assertEquals(1, last7Days.size)
    org.junit.Assert.assertEquals("test_jeans_today", last7Days.first().id)

    // Test Date range filtering: Last 30 days
    val last30Days = all.filter { it.timestamp >= (now - 30 * oneDayMillis) }
    org.junit.Assert.assertEquals(2, last30Days.size)

    db.close()
  }

  @Test
  fun `test DiagnosisShareHelper native share text formatting`() {
    val analysis = com.example.data.demo.DemoDataProvider.demoCases[0]
    val shareText = com.example.util.DiagnosisShareHelper.buildShareableDiagnosisText(analysis)

    org.junit.Assert.assertNotNull(shareText)
    org.junit.Assert.assertTrue("Should contain app branding", shareText.contains("AJUSTA"))
    org.junit.Assert.assertTrue("Should contain garment type", shareText.contains(analysis.garmentType))
    org.junit.Assert.assertTrue("Should contain problem summary", shareText.contains(analysis.problemSummary))
    org.junit.Assert.assertTrue("Should contain recommended solution", shareText.contains(analysis.possibleSolution))
    org.junit.Assert.assertTrue("Should contain price estimate", shareText.contains(analysis.priceEstimate.formattedRange))
    org.junit.Assert.assertTrue("Should contain risk level", shareText.contains(analysis.riskAssessment.level.label))
  }

  @Test
  fun `test Gemini Chatbot model options and message structure`() {
    val proModel = com.example.ai.ChatModelOption.PRO
    val flashModel = com.example.ai.ChatModelOption.FLASH
    val liteModel = com.example.ai.ChatModelOption.FLASH_LITE

    org.junit.Assert.assertEquals("gemini-3.1-pro-preview", proModel.modelId)
    org.junit.Assert.assertEquals("gemini-3.5-flash", flashModel.modelId)
    org.junit.Assert.assertEquals("gemini-3.1-flash-lite-preview", liteModel.modelId)

    val userMsg = com.example.ai.ChatMessage(
      sender = com.example.ai.ChatSender.USER,
      text = "Como ajustar cós de calça jeans?"
    )
    val aiMsg = com.example.ai.ChatMessage(
      sender = com.example.ai.ChatSender.ATELIER_AI,
      text = "Recomenda-se pence traseira ou redução no cós central."
    )

    org.junit.Assert.assertEquals(com.example.ai.ChatSender.USER, userMsg.sender)
    org.junit.Assert.assertEquals(com.example.ai.ChatSender.ATELIER_AI, aiMsg.sender)
  }

  @Test
  fun `test PriceEstimate default factors`() {
    val estimate = com.example.model.PriceEstimate(minPrice = 30, maxPrice = 60, basePrice = 45.0)
    org.junit.Assert.assertEquals(30, estimate.minPrice)
    org.junit.Assert.assertEquals(60, estimate.maxPrice)
    org.junit.Assert.assertEquals(45.0, estimate.basePrice, 0.01)
    org.junit.Assert.assertEquals(1.0, estimate.garmentFactor, 0.01)
    org.junit.Assert.assertEquals(1.0, estimate.complexityFactor, 0.01)
  }

  @Test
  fun `test RepairTutorialsData library integrity`() {
    val tutorials = com.example.knowledge.RepairTutorialsData.tutorials
    org.junit.Assert.assertTrue("Should have multiple repair tutorials", tutorials.size >= 4)
    tutorials.forEach { tut ->
      org.junit.Assert.assertTrue("Title must not be blank", tut.title.isNotBlank())
      org.junit.Assert.assertTrue("Tools needed must not be empty", tut.toolsNeeded.isNotEmpty())
      org.junit.Assert.assertTrue("Steps must not be empty", tut.steps.isNotEmpty())
      org.junit.Assert.assertTrue("Estimated time must be positive", tut.estimatedTimeMinutes > 0)
    }
  }

  @Test
  fun `test GeminiService generateRepairTutorial fallback`() = kotlinx.coroutines.test.runTest {
    val tutorial = com.example.ai.GeminiService.generateRepairTutorial("alça de vestido de festa")
    org.junit.Assert.assertNotNull(tutorial)
    org.junit.Assert.assertTrue("Should contain topic in title", tutorial.title.contains("alça de vestido de festa", ignoreCase = true))
    org.junit.Assert.assertTrue("Should contain steps", tutorial.steps.isNotEmpty())
    org.junit.Assert.assertTrue("Should contain required tools", tutorial.toolsNeeded.isNotEmpty())
  }

  @Test
  fun `test GeminiService generateStyleAndCustomization fallback`() = kotlinx.coroutines.test.runTest {
    val analysis = com.example.data.demo.DemoDataProvider.demoCases[0]
    val styleSuggestion = com.example.ai.GeminiService.generateStyleAndCustomization(analysis)

    org.junit.Assert.assertNotNull(styleSuggestion)
    org.junit.Assert.assertTrue("Style concept should not be blank", styleSuggestion.styleConcept.isNotBlank())
    org.junit.Assert.assertTrue("Should have customization ideas", styleSuggestion.customizationIdeas.isNotEmpty())
    org.junit.Assert.assertTrue("Should have accessory combinations", styleSuggestion.accessoryCombinations.isNotEmpty())
    org.junit.Assert.assertTrue("Should have wardrobe looks", styleSuggestion.wardrobeLooks.isNotEmpty())
  }

  @Test
  fun `test GeminiService estimateRegionalCost with BRL and EUR`() = kotlinx.coroutines.test.runTest {
    val analysis = com.example.data.demo.DemoDataProvider.demoCases[0]

    // Test BRL in São Paulo
    val brlEstimate = com.example.ai.GeminiService.estimateRegionalCost(
      analysis = analysis,
      region = "São Paulo - Jardins",
      currencyCode = "BRL"
    )
    org.junit.Assert.assertNotNull(brlEstimate)
    org.junit.Assert.assertEquals("BRL", brlEstimate.currencyCode)
    org.junit.Assert.assertEquals("R$", brlEstimate.currencySymbol)
    org.junit.Assert.assertTrue("Min price must be > 0", brlEstimate.minPrice > 0)
    org.junit.Assert.assertTrue("Max price >= min price", brlEstimate.maxPrice >= brlEstimate.minPrice)
    org.junit.Assert.assertTrue("Formatted range contains R$", brlEstimate.formattedRange.contains("R$"))

    // Test EUR in Lisboa
    val eurEstimate = com.example.ai.GeminiService.estimateRegionalCost(
      analysis = analysis,
      region = "Lisboa, Portugal",
      currencyCode = "EUR"
    )
    org.junit.Assert.assertNotNull(eurEstimate)
    org.junit.Assert.assertEquals("EUR", eurEstimate.currencyCode)
    org.junit.Assert.assertEquals("€", eurEstimate.currencySymbol)
    org.junit.Assert.assertTrue("Formatted range contains €", eurEstimate.formattedRange.contains("€"))
    org.junit.Assert.assertTrue("Regional context must explain Europe", eurEstimate.regionalMarketContext.contains("Europa") || eurEstimate.regionalMarketContext.contains("Lisboa"))
  }

  @Test
  fun `test NotificationHelper channel creation and reminder scheduling`() {
    val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()

    // Create channels
    com.example.notification.NotificationHelper.createNotificationChannels(context)

    val notificationManager = context.getSystemService(android.content.Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
      val repairChannel = notificationManager.getNotificationChannel(com.example.notification.NotificationHelper.CHANNEL_REPAIRS_ID)
      val tipsChannel = notificationManager.getNotificationChannel(com.example.notification.NotificationHelper.CHANNEL_TIPS_ID)

      org.junit.Assert.assertNotNull(repairChannel)
      org.junit.Assert.assertNotNull(tipsChannel)
      org.junit.Assert.assertEquals("Progresso de Reparos", repairChannel.name)
      org.junit.Assert.assertEquals("Dicas & Tutoriais de Costura", tipsChannel.name)
    }

    // Schedule reminder and cancel without throwing
    com.example.notification.NotificationHelper.scheduleReminder(
      context = context,
      title = "Teste de Reparo",
      message = "Mensagem de teste de lembrete",
      delaySeconds = 60,
      requestCode = 9999
    )

    com.example.notification.NotificationHelper.cancelReminder(context, 9999)
  }

  @Test
  fun `test repair progress bar calculation and status tracking`() {
    val completedOrder = com.example.model.ProfessionalOrder(
      id = "test_completed",
      clientName = "Cliente Teste",
      garmentType = "Trench Coat",
      clientRequest = "Ajuste",
      preliminaryDiagnosis = "Diagnóstico",
      procedureSteps = listOf("Passo 1", "Passo 2"),
      materialsNeeded = "Linha",
      complexity = "Alta",
      attentionPoints = emptyList(),
      status = com.example.model.OrderStatus.CONCLUIDO
    )
    val inProgressOrder = completedOrder.copy(status = com.example.model.OrderStatus.EM_ANDAMENTO)
    val proofOrder = completedOrder.copy(status = com.example.model.OrderStatus.PROVA_PENDENTE)
    val budgetOrder = completedOrder.copy(status = com.example.model.OrderStatus.ORCAMENTO)

    fun calculateProgress(order: com.example.model.ProfessionalOrder): Pair<Int, Int> {
      val percentCompleted = when (order.status) {
        com.example.model.OrderStatus.CONCLUIDO -> 100
        com.example.model.OrderStatus.PROVA_PENDENTE -> 75
        com.example.model.OrderStatus.EM_ANDAMENTO -> 45
        com.example.model.OrderStatus.ORCAMENTO -> 15
      }
      return Pair(percentCompleted, 100 - percentCompleted)
    }

    val (comp100, pend0) = calculateProgress(completedOrder)
    org.junit.Assert.assertEquals(100, comp100)
    org.junit.Assert.assertEquals(0, pend0)

    val (comp75, pend25) = calculateProgress(proofOrder)
    org.junit.Assert.assertEquals(75, comp75)
    org.junit.Assert.assertEquals(25, pend25)

    val (comp45, pend55) = calculateProgress(inProgressOrder)
    org.junit.Assert.assertEquals(45, comp45)
    org.junit.Assert.assertEquals(55, pend55)

    val (comp15, pend85) = calculateProgress(budgetOrder)
    org.junit.Assert.assertEquals(15, comp15)
    org.junit.Assert.assertEquals(85, pend85)
  }

  @Test
  fun `test GarmentRepairStage percentage completed and pending`() {
    val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
    com.example.data.GarmentProgressManager.init(context)

    // Test stage percent logic
    val stages = com.example.model.GarmentRepairStage.values()
    stages.forEach { stage ->
      val completed = stage.percentCompleted
      val pending = 100 - completed
      org.junit.Assert.assertTrue("Percent completed must be between 0 and 100", completed in 0..100)
      org.junit.Assert.assertEquals(100, completed + pending)
    }

    // Test GarmentProgressManager updating stage
    val testGarmentId = "test_garment_123"
    com.example.data.GarmentProgressManager.setStage(testGarmentId, com.example.model.GarmentRepairStage.EM_ANDAMENTO)
    val retrievedStage = com.example.data.GarmentProgressManager.getStage(testGarmentId)
    org.junit.Assert.assertEquals(com.example.model.GarmentRepairStage.EM_ANDAMENTO, retrievedStage)
    org.junit.Assert.assertEquals(65, retrievedStage.percentCompleted)

    // Update to completed
    com.example.data.GarmentProgressManager.setStage(testGarmentId, com.example.model.GarmentRepairStage.CONCLUIDO)
    org.junit.Assert.assertEquals(100, com.example.data.GarmentProgressManager.getStage(testGarmentId).percentCompleted)
  }

  @Test
  fun `test ThemeManager toggling and mode setting`() {
    val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
    com.example.ui.theme.ThemeManager.init(context)

    com.example.ui.theme.ThemeManager.setThemeMode(com.example.ui.theme.AppThemeMode.LIGHT)
    org.junit.Assert.assertEquals(com.example.ui.theme.AppThemeMode.LIGHT, com.example.ui.theme.ThemeManager.themeModeState.value)

    com.example.ui.theme.ThemeManager.setThemeMode(com.example.ui.theme.AppThemeMode.DARK)
    org.junit.Assert.assertEquals(com.example.ui.theme.AppThemeMode.DARK, com.example.ui.theme.ThemeManager.themeModeState.value)

    com.example.ui.theme.ThemeManager.setThemeMode(com.example.ui.theme.AppThemeMode.SYSTEM)
    org.junit.Assert.assertEquals(com.example.ui.theme.AppThemeMode.SYSTEM, com.example.ui.theme.ThemeManager.themeModeState.value)

    // Test toggle sequence: SYSTEM -> LIGHT -> DARK -> SYSTEM
    com.example.ui.theme.ThemeManager.setThemeMode(com.example.ui.theme.AppThemeMode.SYSTEM)
    com.example.ui.theme.ThemeManager.toggleTheme()
    org.junit.Assert.assertEquals(com.example.ui.theme.AppThemeMode.LIGHT, com.example.ui.theme.ThemeManager.themeModeState.value)
    com.example.ui.theme.ThemeManager.toggleTheme()
    org.junit.Assert.assertEquals(com.example.ui.theme.AppThemeMode.DARK, com.example.ui.theme.ThemeManager.themeModeState.value)
  }
}
