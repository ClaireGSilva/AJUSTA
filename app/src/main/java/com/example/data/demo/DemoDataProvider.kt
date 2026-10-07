package com.example.data.demo

import com.example.model.AlterationOption
import com.example.model.ComplexityLevel
import com.example.model.ConfidenceLevel
import com.example.model.GarmentAnalysis
import com.example.model.PriceEstimate
import com.example.model.RiskAssessment
import com.example.model.RiskLevel
import com.example.model.WorthItRecommendation

object DemoDataProvider {

    val demoCases: List<GarmentAnalysis> = listOf(
        // 1. Calça jeans larga na cintura
        GarmentAnalysis(
            id = "demo_jeans_cintura",
            garmentType = "Calça jeans",
            visibleCharacteristics = "Jeans denim médio com lavagem stone wash, cinco bolsos tradicionais e passadores de cinto duplos.",
            problemSummary = "Cintura excessivamente larga",
            problemCategory = "Ficou larga",
            confidence = ConfidenceLevel.HIGH,
            whatWeObserved = "Sobra visível de aproximadamente 4 a 6 cm de tecido na parte posterior do cós, criando uma folga em formato de arco (gapping) quando em posição ereta.",
            probableCause = "Incompatibilidade anatômica entre a razão cintura-quadril da modelagem padrão e as medidas do usuário, somada ao afrouxamento da trama de algodão pós-lavagens.",
            possibleSolution = "Ajuste pelo centro do cós traseiro com pence embutida ou pinçamento nas costuras laterais mantendo a simetria das presilhas.",
            whatNeedsConfirmation = "Abertura interna do cós anatômico, espessura da costura rebatida central e proximidade com a etiqueta traseira de couro.",
            beforeYouAlterLimitations = "A construção interna da peça não pode ser confirmada apenas pela fotografia. Uma profissional deverá verificar margens de costura, cós, zíperes e acabamento antes da execução.",
            alterationOptions = listOf(
                AlterationOption(
                    id = "waist_center_seam",
                    name = "Ajuste pelo cós traseiro",
                    complexity = ComplexityLevel.MEDIUM,
                    description = "Desmanche parcial do cós posterior, redução de tecido e recolocação da presilha central com pesponto ocre.",
                    requiresFitting = true,
                    notes = "Solução mais discreta e limpa para calças com presilha central.",
                    possibleRisks = listOf("Leve aproximação dos bolsos traseiros se a redução for superior a 5 cm.")
                ),
                AlterationOption(
                    id = "waist_side_darts",
                    name = "Pences laterais no cós",
                    complexity = ComplexityLevel.LOW,
                    description = "Criação de pequenas pences discretas nas laterais acima do quadril sem desmanche do cós.",
                    requiresFitting = true,
                    notes = "Procedimento mais rápido e de menor custo.",
                    possibleRisks = listOf("Pequeno volume extra na parte interna lateral.")
                )
            ),
            riskAssessment = RiskAssessment(
                level = RiskLevel.MEDIUM,
                summary = "Complexidade técnica média. Preserva a estrutura geral da calça quando executado por profissional com agulha pesada para denim.",
                specificRisks = listOf("Desalinhamento do pesponto duplo contrastante", "Espessura na junção das costuras do gancho"),
                requiresInPersonEvaluation = false
            ),
            priceEstimate = PriceEstimate(
                minPrice = 45,
                maxPrice = 75,
                basePrice = 50.0,
                garmentFactor = 1.15,
                complexityFactor = 1.0,
                fabricFactor = 1.15,
                finishFactor = 1.0,
                formattedRange = "R$ 45–75",
                disclaimer = "Estimativa inicial. O valor real depende da construção da peça, tecido, acabamento, região e profissional."
            ),
            recommendation = WorthItRecommendation(
                garmentPricePaid = 180.0,
                estimateRatioPercent = 33,
                pointsInFavor = listOf(
                    "Calça jeans é peça durável que suporta anos de uso contínuo.",
                    "O ajuste na cintura resolve o desconforto diário sem alterar as pernas.",
                    "Evita o uso incômodo de cinto apertando excesso de tecido."
                ),
                pointsToConfirm = listOf(
                    "Verifique se o quadril e gancho estão confortáveis antes de mexer na cintura.",
                    "Confirme se a costureira possui linha de pesponto da mesma tonalidade."
                ),
                summary = "O ajuste representa cerca de 33% do valor da calça. É uma das intervenções mais recomendadas em alfaiataria jeans."
            ),
            fabric = "Jeans / Denim",
            userPaidPrice = 180.0
        ),

        // 2. Vestido comprido
        GarmentAnalysis(
            id = "demo_vestido_comprido",
            garmentType = "Vestido longo",
            visibleCharacteristics = "Vestido longo fluido em viscose com saia evasê, fenda lateral e forro embutido.",
            problemSummary = "Comprimento excessivo arrastando no chão",
            problemCategory = "Ficou comprida",
            confidence = ConfidenceLevel.HIGH,
            whatWeObserved = "A barra do vestido repousa no piso com dobra excessiva de cerca de 7 cm, cobrindo totalmente os pés e calçado.",
            probableCause = "Comprimento de fábrica projetado para estaturas elevadas combinadas com salto alto.",
            possibleSolution = "Bainha invisível na barra da peça principal e encurtamento proporcional do forro interno.",
            whatNeedsConfirmation = "Altura exata do calçado que a cliente usará na ocasião e terminação da fenda lateral.",
            beforeYouAlterLimitations = "A construção interna da peça não pode ser confirmada apenas pela fotografia. Uma profissional deverá verificar margens de costura, cós, zíperes e acabamento antes da execução.",
            alterationOptions = listOf(
                AlterationOption(
                    id = "dress_invisible_hem",
                    name = "Barra invisível feita à mão ou máquina de bainha",
                    complexity = ComplexityLevel.LOW,
                    description = "Corte do excesso de tecido com nivelamento circular no corpo e acabamento com ponto invisível.",
                    requiresFitting = true,
                    notes = "O forro deve terminar 2 cm mais curto que o tecido externo.",
                    possibleRisks = listOf("Corte irreversível se marcada sem o calçado definitivo.")
                )
            ),
            riskAssessment = RiskAssessment(
                level = RiskLevel.LOW,
                summary = "Baixo risco. Intervenção clássica e segura que não altera o tronco ou decote.",
                specificRisks = listOf("Desnivelamento se não for medida com a postura ereta."),
                requiresInPersonEvaluation = false
            ),
            priceEstimate = PriceEstimate(
                minPrice = 40,
                maxPrice = 70,
                basePrice = 35.0,
                garmentFactor = 1.3,
                complexityFactor = 0.9,
                fabricFactor = 1.25,
                finishFactor = 1.2,
                formattedRange = "R$ 40–70",
                disclaimer = "Estimativa inicial. O valor real depende da construção da peça, tecido, acabamento, região e profissional."
            ),
            recommendation = WorthItRecommendation(
                garmentPricePaid = 260.0,
                estimateRatioPercent = 21,
                pointsInFavor = listOf(
                    "Permite caminhar com segurança sem pisar ou danificar a barra.",
                    "Valoriza o caimento fluido da saia."
                ),
                pointsToConfirm = listOf(
                    "Leve o calçado exato na hora da marcação com alfinetes."
                ),
                summary = "Custo muito baixo em relação ao benefício imediato de uso da peça."
            ),
            fabric = "Viscose",
            userPaidPrice = 260.0
        ),

        // 3. Camisa com mangas longas
        GarmentAnalysis(
            id = "demo_camisa_manga",
            garmentType = "Camisa social",
            visibleCharacteristics = "Camisa social em algodão egípcio com colarinho estruturado, carcela de manga clássica e botões madreperola.",
            problemSummary = "Manga longa cobrindo a palma da mão",
            problemCategory = "Ficou comprida",
            confidence = ConfidenceLevel.HIGH,
            whatWeObserved = "O punho da camisa desce além do osso do punho (estilóide), dobrando sobre o início dos dedos.",
            probableCause = "Padrão de modelagem industrial com braço alongado.",
            possibleSolution = "Subir o punho retirando o excesso pelo antebraço e refazendo a carcela e as pregas de punho.",
            whatNeedsConfirmation = "Comprimento e largura da carcela de manga existente e espaço para reposicionamento do botão intermediário.",
            beforeYouAlterLimitations = "A construção interna da peça não pode ser confirmada apenas pela fotografia. Uma profissional deverá verificar margens de costura, cós, zíperes e acabamento antes da execução.",
            alterationOptions = listOf(
                AlterationOption(
                    id = "sleeve_cuff_raise",
                    name = "Encurtamento de manga com subida de carcela",
                    complexity = ComplexityLevel.MEDIUM,
                    description = "Descostura do punho, corte preciso do excesso, reconstrução da carcela e refixação com costura limpa.",
                    requiresFitting = true,
                    notes = "Mantém a proporcionalidade da abertura clássica da camisa social.",
                    possibleRisks = listOf("Carcela menor se o encurtamento for superior a 6 cm.")
                )
            ),
            riskAssessment = RiskAssessment(
                level = RiskLevel.MEDIUM,
                summary = "Risco moderado. Exige habilidade para reconstruir a carcela sem deformar as pregas do punho.",
                specificRisks = listOf("Assimetria entre manga esquerda e direita."),
                requiresInPersonEvaluation = false
            ),
            priceEstimate = PriceEstimate(
                minPrice = 45,
                maxPrice = 80,
                basePrice = 50.0,
                garmentFactor = 1.1,
                complexityFactor = 1.0,
                fabricFactor = 1.0,
                finishFactor = 1.15,
                formattedRange = "R$ 45–80",
                disclaimer = "Estimativa inicial. O valor real depende da construção da peça, tecido, acabamento, região e profissional."
            ),
            recommendation = WorthItRecommendation(
                garmentPricePaid = 190.0,
                estimateRatioPercent = 32,
                pointsInFavor = listOf(
                    "Camisas sob medida ou ajustadas transformam a postura profissional.",
                    "Permite usar sob blazer com a sobra elegante de 1 a 1,5 cm de punho."
                ),
                pointsToConfirm = listOf(
                    "Certifique-se de que a camisa já foi lavada ao menos uma vez (algodão pode encolher)."
                ),
                summary = "Excelente investimento para alinhar o caimento de camisaria masculina ou feminina."
            ),
            fabric = "Algodão",
            userPaidPrice = 190.0
        ),

        // 4. Blazer apertado no ombro
        GarmentAnalysis(
            id = "demo_blazer_ombro",
            garmentType = "Blazer de alfaiataria",
            visibleCharacteristics = "Blazer estruturado em lã fria com forro de acetato, ombreiras embutidas e lapela notched.",
            problemSummary = "Aperto excessivo nos ombros e limitação de movimento",
            problemCategory = "Ficou apertada",
            confidence = ConfidenceLevel.HIGH,
            whatWeObserved = "Rugas de tração nas costas (linha de omoplata) e costura de cava repuxando para dentro do músculo deltoide.",
            probableCause = "Estrutura do blazer desenhada para compleição física mais estreita; pouca ou nenhuma folga de mobilidade.",
            possibleSolution = "Avaliação da margem interna das costas e cava para soltura ou reencaixe da cabeça de manga.",
            whatNeedsConfirmation = "Existe margem de sobra de tecido interna (sobra de costura) nas costas ou na cava? Se não houver sobra, soltar é impossível.",
            beforeYouAlterLimitations = "A construção interna da peça não pode ser confirmada apenas pela fotografia. Uma profissional deverá verificar margens de costura, cós, zíperes e acabamento antes da execução.",
            alterationOptions = listOf(
                AlterationOption(
                    id = "blazer_shoulder_let_out",
                    name = "Soltura de ombro e costura central das costas",
                    complexity = ComplexityLevel.HIGH,
                    description = "Abertura do forro, checagem da margem interna da alfaiataria e reposicionamento com ferro a vapor.",
                    requiresFitting = true,
                    notes = "Depende estritamente da existência de sobras de costura deixadas pela confecção.",
                    possibleRisks = listOf("Marcas visíveis de agulha no tecido externo", "Limitação caso a margem interna seja inferior a 1 cm.")
                )
            ),
            riskAssessment = RiskAssessment(
                level = RiskLevel.HIGH,
                summary = "Alto risco de alfaiataria estrutural. Recomendamos avaliação presencial de uma profissional antes de realizar qualquer alteração.",
                specificRisks = listOf(
                    "Desmonte de ombreiras e entretelas internas pode alterar permanentemente a silhueta da peça.",
                    "Se o tecido não tiver margem interna, não há como alargar."
                ),
                requiresInPersonEvaluation = true
            ),
            priceEstimate = PriceEstimate(
                minPrice = 80,
                maxPrice = 160,
                basePrice = 90.0,
                garmentFactor = 1.7,
                complexityFactor = 1.45,
                fabricFactor = 1.5,
                finishFactor = 1.3,
                formattedRange = "R$ 80–160",
                disclaimer = "Estimativa inicial. O valor real depende da construção da peça, tecido, acabamento, região e profissional."
            ),
            recommendation = WorthItRecommendation(
                garmentPricePaid = 450.0,
                estimateRatioPercent = 26,
                pointsInFavor = listOf(
                    "Blazer é peça de alto valor agregado e elegância atemporal.",
                    "Se houver margem de costura, devolverá o conforto de uso imediato."
                ),
                pointsToConfirm = listOf(
                    "Exige inspeção presencial por alfaiate ou costureira sênior antes de desmanchar.",
                    "Verifique se o tecido não está desgastado na região das axilas."
                ),
                summary = "Apenas prossiga após a profissional abrir a bainha do forro e confirmar a existência física de margem de tecido."
            ),
            fabric = "Lã / Alfaiataria",
            userPaidPrice = 450.0
        ),

        // 5. Zíper danificado
        GarmentAnalysis(
            id = "demo_ziper_danificado",
            garmentType = "Jaqueta / Saia / Calça",
            visibleCharacteristics = "Peça com fecho de zíper de metal ou nylon travado com dentes desalinhados.",
            problemSummary = "Zíper emperrado abrindo no meio",
            problemCategory = "Preciso reparar",
            confidence = ConfidenceLevel.HIGH,
            whatWeObserved = "Cursor do zíper frouxo que não conecta os dentes laterais, permitindo que o fecho abra espontaneamente sob pressão.",
            probableCause = "Desgaste mecânico natural das abas metálicas do cursor ou desalinhamento dos dentes inferiores.",
            possibleSolution = "Troca do cursor (reparo simples) ou substituição completa do zíper por peça nova de mesma medida e cor.",
            whatNeedsConfirmation = "Se os dentes do trilho estão intactos (permitindo apenas a troca do cursor) ou se é necessária a substituição da fita inteira.",
            beforeYouAlterLimitations = "A construção interna da peça não pode ser confirmada apenas pela fotografia. Uma profissional deverá verificar margens de costura, cós, zíperes e acabamento antes da execução.",
            alterationOptions = listOf(
                AlterationOption(
                    id = "zipper_slider_swap",
                    name = "Ajuste ou troca do cursor (fecho)",
                    complexity = ComplexityLevel.LOW,
                    description = "Substituição somente da trava/cursor metálico sem desmanchar a costura da peça.",
                    requiresFitting = false,
                    notes = "Procedimento rápido de 15 minutos se o trilho estiver saudável.",
                    possibleRisks = emptyList()
                ),
                AlterationOption(
                    id = "zipper_full_replace",
                    name = "Troca completa do zíper",
                    complexity = ComplexityLevel.LOW,
                    description = "Desmanche do zíper antigo, posicionamento de novo fecho da marca YKK e recostura embutida.",
                    requiresFitting = false,
                    notes = "Garante durabilidade de longo prazo.",
                    possibleRisks = listOf("Cuidados com tecidos finos para não repuxar na costura.")
                )
            ),
            riskAssessment = RiskAssessment(
                level = RiskLevel.LOW,
                summary = "Baixo risco. Reparo clássico sem interferência na modelagem corporal da peça.",
                specificRisks = listOf("Necessidade de encontrar zíper com cor idêntica à original."),
                requiresInPersonEvaluation = false
            ),
            priceEstimate = PriceEstimate(
                minPrice = 25,
                maxPrice = 50,
                basePrice = 30.0,
                garmentFactor = 1.0,
                complexityFactor = 0.85,
                fabricFactor = 1.0,
                finishFactor = 1.0,
                formattedRange = "R$ 25–50",
                disclaimer = "Estimativa inicial. O valor real depende da construção da peça, tecido, acabamento, região e profissional."
            ),
            recommendation = WorthItRecommendation(
                garmentPricePaid = 120.0,
                estimateRatioPercent = 31,
                pointsInFavor = listOf(
                    "Recupera 100% da usabilidade de uma peça parada no armário.",
                    "Custo muito acessível comparado à compra de uma roupa nova."
                ),
                pointsToConfirm = listOf(
                    "Teste se o cursor original pode ser apenas regulado com alicate de bico."
                ),
                summary = "Excelente custo-benefício para reativar roupas queridas do guarda-roupa."
            ),
            fabric = "Tecido misto",
            userPaidPrice = 120.0
        )
    )

    fun getById(id: String): GarmentAnalysis? = demoCases.firstOrNull { it.id == id }
}
