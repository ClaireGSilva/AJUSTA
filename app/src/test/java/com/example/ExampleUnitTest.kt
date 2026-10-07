package com.example

import com.example.data.directory.ProfessionalDirectory
import com.example.knowledge.KnowledgeBase
import com.example.pricing.PriceEstimateEngine
import com.example.pricing.PricingContext
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testProfessionalDirectorySearch() {
    val all = ProfessionalDirectory.verifiedProfessionals
    assertTrue("Directory should have verified tailors", all.isNotEmpty())

    val alfaiataria = ProfessionalDirectory.search("", "Alfaiataria")
    assertTrue("Should find alfaiataria specialists", alfaiataria.isNotEmpty())

    val jardins = ProfessionalDirectory.search("Jardins")
    assertTrue("Should find tailors in Jardins", jardins.isNotEmpty())

    val tailor = ProfessionalDirectory.getById(all.first().id)
    assertNotNull("Tailor should be fetchable by ID", tailor)
  }

  @Test
  fun testExpandedKnowledgeBaseFabrics() {
    val fabrics = KnowledgeBase.fabrics
    assertTrue("Should contain silk blend", fabrics.containsKey("silk_blend"))
    assertTrue("Should contain polyester", fabrics.containsKey("polyester"))
    assertTrue("Should contain viscose", fabrics.containsKey("viscose"))
    assertTrue("Should contain velvet", fabrics.containsKey("velvet"))

    val silkBlend = fabrics["silk_blend"]!!
    assertNotNull(silkBlend.careInstructions)
    assertTrue(silkBlend.careInstructions.isNotBlank())
    assertNotNull(silkBlend.alterationBehavior)
    assertTrue(silkBlend.alterationBehavior.isNotBlank())
  }

  @Test
  fun testExpandedKnowledgeBaseAlterations() {
    val alterations = KnowledgeBase.alterations
    assertTrue("Should contain baby rolled hem", alterations.containsKey("rolled_hem"))
    assertTrue("Should contain shoulder reconstruction", alterations.containsKey("shoulder_reconstruction"))
    assertTrue("Should contain placket sleeve adjustment", alterations.containsKey("sleeve_adjustment_placket"))

    val rolledHem = alterations["rolled_hem"]!!
    assertTrue(rolledHem.processSteps.isNotEmpty())
    assertTrue(rolledHem.toolsAndNeedles.isNotBlank())
    assertTrue(rolledHem.risks.isNotEmpty())
  }

  @Test
  fun testGarmentVisionAnalyzerStructure() = kotlinx.coroutines.runBlocking {
    val result: com.example.model.DiagnosticResult = com.example.ai.GarmentVisionAnalyzer.analyze(
      bitmaps = emptyList(),
      problemCategory = "Ficou larga",
      problemDescription = "Cós sobrando na cintura atrás",
      fabric = "Jeans",
      pricePaid = 150.0
    )

    assertNotNull(result)
    assertNotNull(result.analysis)
    val analysis = result.analysis
    assertEquals("Calça jeans", analysis.garmentType)
    assertTrue(analysis.whatWeObserved.isNotBlank())
    assertTrue(analysis.probableCause.isNotBlank())
    assertTrue(analysis.possibleSolution.isNotBlank())
    assertTrue(analysis.whatNeedsConfirmation.isNotBlank())
    assertTrue(analysis.alterationOptions.isNotEmpty())
    assertNotNull(analysis.priceEstimate)
    assertNotNull(analysis.riskAssessment)
    assertNotNull(analysis.recommendation)
  }
}

