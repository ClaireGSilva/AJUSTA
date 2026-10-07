package com.example.knowledge

import com.example.model.ComplexityLevel
import com.example.model.RiskLevel

data class GarmentDefinition(
    val id: String,
    val namePt: String,
    val nameEn: String,
    val category: String,
    val commonAlterations: List<String>,
    val structuralFactors: String,
    val baseLaborFactor: Double = 1.0
)

data class AlterationDefinition(
    val id: String,
    val namePt: String,
    val nameEn: String,
    val complexity: ComplexityLevel,
    val riskLevel: RiskLevel,
    val defaultBasePrice: Double,
    val requiresFitting: Boolean,
    val processSteps: List<String> = emptyList(),
    val toolsAndNeedles: String = "Agulha universal e máquina reta",
    val checkListItems: List<String>,
    val risks: List<String>,
    val limitations: String = "Intervenção sujeita à margem interna de tecido existente."
)

data class FabricDefinition(
    val id: String,
    val namePt: String,
    val nameEn: String,
    val category: String,
    val difficultyFactor: Double,
    val behaviorNotes: String,
    val alterationBehavior: String,
    val careInstructions: String,
    val recommendedNeedleAndThread: String,
    val riskLevel: RiskLevel
)

object KnowledgeBase {

    val garments: Map<String, GarmentDefinition> = listOf(
        GarmentDefinition(
            id = "jeans",
            namePt = "Calça jeans",
            nameEn = "Jeans",
            category = "Pernas",
            commonAlterations = listOf("waist_adjustment", "hem_original", "taper_leg", "zipper_replacement", "crotch_adjustment"),
            structuralFactors = "Costuras pesadas, pesponto duplo contrastante (linha ocre 36), rebites nos bolsos e cós anatômico curvo.",
            baseLaborFactor = 1.15
        ),
        GarmentDefinition(
            id = "pants",
            namePt = "Calça alfaiataria",
            nameEn = "Tailored Pants",
            category = "Pernas",
            commonAlterations = listOf("waist_adjustment", "hem_invisible", "hip_adjustment", "zipper_replacement", "crotch_adjustment"),
            structuralFactors = "Forro de cós entretelado, folga interna traseira (margem de 3 a 5 cm), vinco central e bainha invisível.",
            baseLaborFactor = 1.25
        ),
        GarmentDefinition(
            id = "shirt",
            namePt = "Camisa social",
            nameEn = "Dress Shirt",
            category = "Superior",
            commonAlterations = listOf("sleeve_adjustment_placket", "side_seam_adjustment", "hem_curved", "dart_addition"),
            structuralFactors = "Carcela de punho com botão intermediário, pala dupla nas costas, entretelas coladas no colarinho e punhos.",
            baseLaborFactor = 1.1
        ),
        GarmentDefinition(
            id = "blouse",
            namePt = "Blusa / Camisete fluida",
            nameEn = "Blouse",
            category = "Superior",
            commonAlterations = listOf("side_seam_adjustment", "bust_dart_adjustment", "rolled_hem", "shoulder_strap_adjustment"),
            structuralFactors = "Tecidos finos, decotes com revel ou viés francês, pences de busto orientadas para o ápice mamário.",
            baseLaborFactor = 1.05
        ),
        GarmentDefinition(
            id = "dress",
            namePt = "Vestido de festa / Noiva",
            nameEn = "Dress / Gown",
            category = "Corpo inteiro",
            commonAlterations = listOf("rolled_hem", "waist_adjustment", "invisible_zipper_replacement", "bust_dart_adjustment", "shoulder_strap_adjustment"),
            structuralFactors = "Zíper invisível dorsal ou lateral, forros múltiplos (cetim/tule), barbatanas de sustentação e recorte princesa.",
            baseLaborFactor = 1.35
        ),
        GarmentDefinition(
            id = "skirt",
            namePt = "Saia lápis / evasê",
            nameEn = "Skirt",
            category = "Inferior",
            commonAlterations = listOf("waist_adjustment", "hem_invisible", "invisible_zipper_replacement", "side_seam_adjustment"),
            structuralFactors = "Cós reto ou entretelado, fenda traseira transpassada para caminhada, forro preso no zíper.",
            baseLaborFactor = 1.0
        ),
        GarmentDefinition(
            id = "blazer",
            namePt = "Blazer / Paletó estruturado",
            nameEn = "Blazer / Suit Jacket",
            category = "Estruturado",
            commonAlterations = listOf("shoulder_reconstruction", "sleeve_adjustment_buttons", "side_seam_adjustment", "lining_replacement"),
            structuralFactors = "Ombreiras estruturadas, entretela flutuante no peito (canvas), forro integral, lapelas pespontadas e fendas posteriores.",
            baseLaborFactor = 1.75
        ),
        GarmentDefinition(
            id = "jacket",
            namePt = "Jaqueta / Casaco couro",
            nameEn = "Jacket",
            category = "Estruturado",
            commonAlterations = listOf("heavy_zipper_replacement", "sleeve_adjustment_zipper", "tear_repair_leather"),
            structuralFactors = "Zíperes tratorados metálicos, couro ou lona pesada, forro matelassê e costuras duplas travadas.",
            baseLaborFactor = 1.45
        ),
        GarmentDefinition(
            id = "coat",
            namePt = "Sobretudo / Trench Coat",
            nameEn = "Overcoat",
            category = "Pesado",
            commonAlterations = listOf("sleeve_adjustment", "hem_invisible", "lining_replacement", "button_reinforcement"),
            structuralFactors = "Lã batida pesada, passadores reforçados, botões de massa com contra-botão interno de alívio de tração.",
            baseLaborFactor = 1.6
        ),
        GarmentDefinition(
            id = "suit",
            namePt = "Costume / Terno completo",
            nameEn = "Full Suit",
            category = "Alfaiataria",
            commonAlterations = listOf("shoulder_reconstruction", "waist_adjustment", "crotch_adjustment", "hem_invisible"),
            structuralFactors = "Equilíbrio de proporção entre o casaco e a calça; cada ajuste no casaco deve harmonizar com a linha do ombro.",
            baseLaborFactor = 2.0
        )
    ).associateBy { it.id }

    val alterations: Map<String, AlterationDefinition> = listOf(
        AlterationDefinition(
            id = "hem_invisible",
            namePt = "Barra invisível clássica",
            nameEn = "Blind Hem",
            complexity = ComplexityLevel.LOW,
            riskLevel = RiskLevel.LOW,
            defaultBasePrice = 35.0,
            requiresFitting = true,
            toolsAndNeedles = "Agulha manual nº 9 ou máquina com sapata de ponto invisível, fio 100% poliéster ultrafino.",
            processSteps = listOf(
                "1. Prova com o calçado definitivo em piso plano nivelado.",
                "2. Marcação com alfinetes horizontais a 1 cm do chão (ou na quebra desejada do vinco).",
                "3. Refilar o excesso deixando margem de 4 cm para dobra interna.",
                "4. Chulear a borda aberta com overloque de 3 fios para não engrossar a dobra.",
                "5. Passar o vinco da barra com ferro a vapor usando sapata de teflon e pano de passar.",
                "6. Executar o ponto invisível pegando apenas 1 fio da trama externa a cada 1,5 cm."
            ),
            checkListItems = listOf("Altura do calçado que usará com a peça", "Espessura da linha para não marcar na frente", "Simetria de ambas as pernas"),
            risks = listOf("Corte irreversível se marcada curta demais", "Marcas aparentes de ponto no tecido externo se a tensão for excessiva"),
            limitations = "Exige no mínimo 3 cm de sobra interna de tecido se a intenção for alongar a barra."
        ),
        AlterationDefinition(
            id = "hem_original",
            namePt = "Barra original de Jeans (Euro Hem)",
            nameEn = "Original Denim Hem",
            complexity = ComplexityLevel.MEDIUM,
            riskLevel = RiskLevel.LOW,
            defaultBasePrice = 48.0,
            requiresFitting = true,
            toolsAndNeedles = "Agulha jeans nº 100/16, linha de pesponto pesado, martelo de costura para nivelar junções.",
            processSteps = listOf(
                "1. Medir o comprimento desejado com o tênis ou bota de uso frequente.",
                "2. Descontar exatamente a largura da bainha original dobrada (geralmente 1,2 a 1,5 cm).",
                "3. Cortar o excesso de perna e desacoplar a barra original mantendo 1 cm de margem de costura.",
                "4. Encaixar a borda desfiada original na nova extremidade da perna alinhando as costuras laterais.",
                "5. Martelar as intersecções das costuras grossas para evitar quebra de agulha.",
                "6. Pespontar rente à costura de fábrica e passar com ferro quente e peso para assentar."
            ),
            checkListItems = listOf("Preservação do desbotado e puído autêntico da borda", "Alinhamento das costuras interna e externa da perna"),
            risks = listOf("Volume aumentado na junção das costuras laterais", "Desalinhamento se a perna tiver corte afunilado"),
            limitations = "Se a perna for muito afunilada em relação à boca original, pode haver sobra de tecido na circunferência."
        ),
        AlterationDefinition(
            id = "rolled_hem",
            namePt = "Bainha de lenço em tecidos nobres (Baby Hem)",
            nameEn = "Baby Rolled Hem",
            complexity = ComplexityLevel.HIGH,
            riskLevel = RiskLevel.MEDIUM,
            defaultBasePrice = 85.0,
            requiresFitting = true,
            toolsAndNeedles = "Agulha microtex 60/8 ou 70/10, calcador de bainha estreita ou ponto manual de lenço, linha de bordar ou seda nº 100.",
            processSteps = listOf(
                "1. Vestir a peça com a roupa íntima e calçado adequados e marcar a curva circular com alfinetes.",
                "2. Cortar com tesoura de lâmina afiada ou cortador rotativo mantendo apenas 6 mm de margem.",
                "3. Passar uma primeira costura reta a 3 mm da borda bruta para criar sustentação.",
                "4. Dobrar o tecido rente à costura, passar a ferro frio ou morno e refilar o excesso microscópico.",
                "5. Dobrar novamente embutindo a borda desfiada (formando um rolinho de menos de 2 mm).",
                "6. Pespontar exatamente sobre a dobra anterior com tensão baixa para não franzir o tecido fluido."
            ),
            checkListItems = listOf("Fluidez do caimento sem ondulações indesejadas", "Ausência de fios desfiados aparentes", "Nivelamento em relação ao chão"),
            risks = listOf("Tecido escorregar durante a passagem da máquina", "Ondulação em cortes diagonais de viés"),
            limitations = "Requer paciência técnica extrema; cada metro de bainha de lenço leva de 30 a 50 minutos de execução cuidadosa."
        ),
        AlterationDefinition(
            id = "waist_adjustment",
            namePt = "Ajuste de cós e cintura pelo centro posterior",
            nameEn = "Center Back Waistband Adjustment",
            complexity = ComplexityLevel.MEDIUM,
            riskLevel = RiskLevel.MEDIUM,
            defaultBasePrice = 55.0,
            requiresFitting = true,
            toolsAndNeedles = "Desmanchador de costura cirúrgico, agulha adequada ao tecido, entretela de cós, ferro a vapor.",
            processSteps = listOf(
                "1. Pinçar a sobra no corpo na linha central das costas e travar com alfinetes verticais.",
                "2. Desmanchar a presilha traseira central e a costura do cós em cerca de 15 cm para cada lado.",
                "3. Abrir o gancho traseiro até a altura do início dos bolsos.",
                "4. Traçar o novo diagrama de curva com giz de alfaiate unindo a redução da cintura ao gancho suavemente.",
                "5. Costurar com ponto de reforço, cortar o excesso de cós embutindo as pontas cortadas.",
                "6. Recosturar o cós, rebater pespontos de fábrica e recolocar a presilha central cobrindo a costura."
            ),
            checkListItems = listOf("Margem interna de tecido disponível", "Continuidade dos bolsos traseiros", "Presilhas de cinto"),
            risks = listOf("Aproximação excessiva dos bolsos traseiros caso a redução exceda 6 cm", "Descasamento da costura do gancho"),
            limitations = "Reduções superiores a 6 cm exigem dividir o ajuste entre o centro das costas e as duas laterais."
        ),
        AlterationDefinition(
            id = "crotch_adjustment",
            namePt = "Ajuste de gancho / cavalo de calça",
            nameEn = "Crotch Depth & Inseam Adjustment",
            complexity = ComplexityLevel.HIGH,
            riskLevel = RiskLevel.HIGH,
            defaultBasePrice = 75.0,
            requiresFitting = true,
            toolsAndNeedles = "Régua curva francesa de alfaiate, alfinetes finos, agulha média, ferro de passar com almofada de alfaiate.",
            processSteps = listOf(
                "1. Avaliar se o gancho está baixo (sobrando pano em formato de bolsa) ou repuxando ao sentar.",
                "2. Alfinetar a curva do entrepernas subindo a junção central em direção ao corpo.",
                "3. Desmanchar as costuras do entrepernas em 20 cm em ambas as pernas.",
                "4. Redesenhar a curva anatômica respeitando a profundidade da bacia.",
                "5. Costurar com ponto elástico ou duplo reforçado para suportar a tensão ao sentar.",
                "6. Abrir a costura a ferro na almofada de alfaiate moldando a forma esférica."
            ),
            checkListItems = listOf("Conforto imediato ao sentar e dobrar os joelhos", "Linha do vinco mantida perfeitamente vertical"),
            risks = listOf("A calça ficar desconfortável ao sentar se cavar demais o gancho", "Repuxo dianteiro em formato de bigode de gato"),
            limitations = "Soltar o gancho só é viável se a confecção deixou sobra interna de tecido na costura do fundo."
        ),
        AlterationDefinition(
            id = "shoulder_reconstruction",
            namePt = "Reconstrução de ombro e cava em paletó / blazer",
            nameEn = "Shoulder & Armhole Reconstruction",
            complexity = ComplexityLevel.SPECIALIST,
            riskLevel = RiskLevel.HIGH,
            defaultBasePrice = 160.0,
            requiresFitting = true,
            toolsAndNeedles = "Abridor de costuras, almofada de ombro (tailor's ham), ferro profissional com vapor seco, linha de alinhavo de algodão cru.",
            processSteps = listOf(
                "1. Marcar a nova posição do acrômio (osso da ponta do ombro) no corpo vestido com camisa social.",
                "2. Abrir o forro inferior do blazer e soltar completamente a cabeça da manga e ombreira.",
                "3. Cortar o excesso de tecido no topo do ombro e na pala, refazendo o contorno da cava.",
                "4. Reposicionar ou modelar nova ombreira compatível com a nova largura de costas.",
                "5. Alinhavar a manga com ponto frouxo embebendo a folga da cabeça da manga para não enrugar.",
                "6. Fazer prova no cliente conferindo caimento da lapela e mobilidade dos braços.",
                "7. Costurar definitivamente, aplicar tira de reforço (fita de viés de peito), fechar forro à mão."
            ),
            checkListItems = listOf("Estrutura das ombreiras", "Desmonte do forro", "Geometria da cabeça da manga"),
            risks = listOf("Pode distorcer a linha do peito e o caimento da lapela se mal executado", "Perda definitiva de formato se cortar sem alinhavo prévio"),
            limitations = "Exige alfaiate experiente. Não tente fazer em casa ou com profissionais que não tenham bancada de alfaiataria."
        ),
        AlterationDefinition(
            id = "sleeve_adjustment_placket",
            namePt = "Encurtamento de manga social com subida de carcela",
            nameEn = "Sleeve Shortening with Placket Relocation",
            complexity = ComplexityLevel.MEDIUM,
            riskLevel = RiskLevel.MEDIUM,
            defaultBasePrice = 65.0,
            requiresFitting = true,
            toolsAndNeedles = "Desmanchador fino, entretela termocolante leve para carcela, agulha 70/10, ferro com ponta fina.",
            processSteps = listOf(
                "1. Marcar o comprimento ideal da manga (osso estilóide do punho com o braço dobrado a 90°).",
                "2. Desmanchar cuidadosamente o punho e a carcela pontuda da manga sem rasgar o tecido da fita.",
                "3. Cortar o excesso do comprimento na manga.",
                "4. Abrir a nova fenda vertical proporcionalmente à altura cortada para recolocação da carcela.",
                "5. Remontar a carcela dobrando as bordas e represetando a ponta piramidal com pesponto limpo.",
                "6. Fazer as pregas de punho simétricas e recosturar o punho original com costura embutida."
            ),
            checkListItems = listOf("Abertura da carcela e botões de punho", "Se o ajuste deve ser feito pelo ombro ou pelo punho"),
            risks = listOf("Carcela ficar curta demais se não for desmanchada e reposicionada", "Desproporção das pregas"),
            limitations = "Se o encurtamento for superior a 7 cm, a manga pode afunilar demais no antebraço."
        ),
        AlterationDefinition(
            id = "invisible_zipper_replacement",
            namePt = "Troca de zíper invisível com acabamento em viés",
            nameEn = "Invisible Zipper Replacement with Bias Binding",
            complexity = ComplexityLevel.MEDIUM,
            riskLevel = RiskLevel.LOW,
            defaultBasePrice = 50.0,
            requiresFitting = false,
            toolsAndNeedles = "Calcador especial para zíper invisível, zíper YKK invisível na cor exata, agulha fina 70/10.",
            processSteps = listOf(
                "1. Desmanchar o zíper antigo preservando os forros e os acabamentos do decote.",
                "2. Passar a ferro brando os dentes do novo zíper invisível para abrir a espiral plástica.",
                "3. Alinhavar as fitas do zíper nas margens de costura garantindo casamento milimétrico na cintura.",
                "4. Costurar com o calcador de canaleta passando a agulha rente à base dos dentes.",
                "5. Fechar a parte inferior da costura da saia abaixo da trava do zíper sem deixar bico.",
                "6. Embutir as pontas superiores sob o forro ou debrum do decote com acabamento limpo."
            ),
            checkListItems = listOf("Comprimento e espessura do cursor", "Tipo (comum, invisível ou tratorado)", "Cor da fita"),
            risks = listOf("Descasamento da linha da cintura de um lado para o outro", "Agulha perfurar o dente do zíper"),
            limitations = "Se o tecido estiver esgarçado nas margens antigas, é preciso aplicar entretela termocolante de reforço."
        ),
        AlterationDefinition(
            id = "bust_dart_adjustment",
            namePt = "Pence de busto e ajuste de recorte princesa",
            nameEn = "Bust Dart & Princess Seam Alteration",
            complexity = ComplexityLevel.HIGH,
            riskLevel = RiskLevel.MEDIUM,
            defaultBasePrice = 65.0,
            requiresFitting = true,
            toolsAndNeedles = "Alfinetes de cabeça de vidro, giz de alfaiate, almofada de passar busto, agulha 70/10.",
            processSteps = listOf(
                "1. Prova com o sutiã que a cliente usará com a peça, marcando o ponto mais alto do busto.",
                "2. Verificar a distância entre a ponta da pence e o ápice (deve terminar a 2 a 2,5 cm antes do mamilo).",
                "3. Soltar a costura da pence antiga e passar com ferro e vapor para desmanchar as marcas.",
                "4. Redesenhar a pence apontando diretamente para o ápice mamário.",
                "5. Costurar afinando gradualmente até o último ponto terminar na beiradinha sem nó grosso.",
                "6. Passar a ferro sobre a almofada côncava de busto para criar a curvatura tridimensional suave."
            ),
            checkListItems = listOf("Posicionamento do sutiã definitivo", "Simetria de altura de busto esquerdo e direito"),
            risks = listOf("Bico ou papo na ponta da pence se a costura terminar abruptamente", "Marcas de costura anterior"),
            limitations = "Tecidos brilhantes como cetim podem conservar furinhos microscópicos da costura desmanchada."
        ),
        AlterationDefinition(
            id = "taper_leg",
            namePt = "Afunilamento de pernas alinhando pelo fio",
            nameEn = "Trouser Leg Tapering Balanced by Grainline",
            complexity = ComplexityLevel.MEDIUM,
            riskLevel = RiskLevel.MEDIUM,
            defaultBasePrice = 50.0,
            requiresFitting = true,
            toolsAndNeedles = "Régua longa de alfaiate, agulha adequada, alfinetes, giz de alfaiataria.",
            processSteps = listOf(
                "1. Vestir a calça pelo direito e alfinetar uniformemente na costura interna e externa da perna.",
                "2. Dividir a redução igualmente entre entrepernas e lateral para não torcer o fio do tecido.",
                "3. Marcar a linha suave desde o joelho até a boca da barra.",
                "4. Costurar em ambas as margens e abrir as costuras no ferro a vapor.",
                "5. Refazer a bainha no diâmetro menor ajustado."
            ),
            checkListItems = listOf("Fio da perna sem torção ao caminhar", "Passagem confortável do pé na boca da barra"),
            risks = listOf("Perna torcida se o ajuste for feito apenas por um dos lados", "Repuxo no joelho ao sentar"),
            limitations = "A boca da barra não pode ser menor que a circunferência do calcanhar e peito do pé."
        ),
        AlterationDefinition(
            id = "tear_repair_leather",
            namePt = "Cerzimento técnico e reforço de couro",
            nameEn = "Leather Tear Structural Repair",
            complexity = ComplexityLevel.SPECIALIST,
            riskLevel = RiskLevel.HIGH,
            defaultBasePrice = 90.0,
            requiresFitting = false,
            toolsAndNeedles = "Cola especial de contato para couro (flexível), entretela de reforço de poliéster, tinta de acabamento para couro.",
            processSteps = listOf(
                "1. Abrir o forro interno para acessar o verso do rasgo no couro.",
                "2. Cortar remendo de reforço em tecido fino e resistente com 1,5 cm a mais que a fenda.",
                "3. Aplicar película fina de adesivo de couro no verso e aproximar as bordas do corte milimetricamente.",
                "4. Pressionar com peso plano por 20 minutos para vulcanização da colagem.",
                "5. Fechar forro com ponto invisível manual.",
                "6. Aplicar cera pigmentada ou verniz de couro no lado externo para disfarçar o traço."
            ),
            checkListItems = listOf("Resistência mecânica à tração após secagem", "Cor da tinta correspondente ao tom original"),
            risks = listOf("O rasgo permanecer sutilmente perceptível sob reflexo de luz", "Endurecimento da área se usar cola inadequada"),
            limitations = "Couro não aceita desmanche de agulha: furos anteriores são permanentes."
        )
    ).associateBy { it.id }

    val fabrics: Map<String, FabricDefinition> = listOf(
        FabricDefinition(
            id = "cotton",
            namePt = "Algodão puro",
            nameEn = "100% Cotton",
            category = "Fibra Natural Vegetal",
            difficultyFactor = 1.0,
            behaviorNotes = "Tecido estável, respirável, resistente à tração e fácil de costurar.",
            alterationBehavior = "Excelente para ajustes. Aceita ferro quente, não escorrega e pences assentam perfeitamente. Atenção ao encolhimento pós-lavagem (até 3-5%) antes de fazer a barra definitiva.",
            careInstructions = "Lavar em temperatura até 40°C. Passar a ferro quente (até 200°C) com vapor abundante. Secar à sombra para preservar as cores.",
            recommendedNeedleAndThread = "Agulha Universal 80/12, linha 100% poliéster ou algodão mercerizado nº 120.",
            riskLevel = RiskLevel.LOW
        ),
        FabricDefinition(
            id = "denim",
            namePt = "Jeans / Denim",
            nameEn = "Denim",
            category = "Sarja de Algodão Pesada",
            difficultyFactor = 1.15,
            behaviorNotes = "Trama pesada e encorpada, geralmente em sarja 3x1 com urdume tingido em índigo.",
            alterationBehavior = "Exige agulhas grossas para furar camadas múltiplas (como junção de cós e gancho). As marcas de lavagem e desbotamento de fábrica dificultam soltar costuras sem deixar faixas escuras visíveis.",
            careInstructions = "Lavar do avesso em água fria para preservar o índigo. Evitar secadora para não encolher o elastano.",
            recommendedNeedleAndThread = "Agulha Jeans / Denim 90/14 a 110/18, linha de pesponto pesado (nº 36 ou 50) e linha de bobina comum nº 120.",
            riskLevel = RiskLevel.LOW
        ),
        FabricDefinition(
            id = "linen",
            namePt = "Linho puro e misto",
            nameEn = "Linen / Linen Blend",
            category = "Fibra Natural Nobre",
            difficultyFactor = 1.25,
            behaviorNotes = "Fibra vegetal rústica e elegante de alta respirabilidade e rigidez natural.",
            alterationBehavior = "Desfia com grande facilidade nas bordas cortadas. Exige chuleio ou costura francesa imediata. Amassa com facilidade e pode relaxar com o calor do corpo durante o uso.",
            careInstructions = "Lavar no ciclo delicado com sabão neutro. Passar ainda ligeiramente úmido com ferro bem quente e vapor abundante. Não torcer.",
            recommendedNeedleAndThread = "Agulha Universal 80/12 ou 90/14 afiada, linha 100% poliéster nº 120, acabamento com overloque de 3 fios.",
            riskLevel = RiskLevel.MEDIUM
        ),
        FabricDefinition(
            id = "viscose",
            namePt = "Viscose e Rayon",
            nameEn = "Viscose / Rayon",
            category = "Fibra Celulósica Regenerada",
            difficultyFactor = 1.3,
            behaviorNotes = "Fluida, sedosa, de toque suave e excelente caimento drapeado.",
            alterationBehavior = "Escorrega na mesa de corte. Sujeita a encolhimento significativo na primeira lavagem (até 8%). Nunca altere uma peça de viscose virgem sem que ela tenha sido previamente lavada.",
            careInstructions = "Lavar à mão ou saco protetor em água fria. Nunca usar secadora. Passar pelo avesso em temperatura média (máx 150°C) sem esticar a fibra.",
            recommendedNeedleAndThread = "Agulha fina Microtex 70/10 ou Universal 70/10, linha fina de poliéster nº 120 ou 150.",
            riskLevel = RiskLevel.MEDIUM
        ),
        FabricDefinition(
            id = "silk",
            namePt = "Seda pura",
            nameEn = "Pure Mulberry Silk",
            category = "Fibra Proteica Nobre",
            difficultyFactor = 1.85,
            behaviorNotes = "Fibra nobre de brilho acetinado, ultra macia, leve e extremamente delicada.",
            alterationBehavior = "Cada furo de agulha é irreversível. Desmanchar costuras antigas deixa furinhos visíveis. Exige corte em tesoura com lâmina micro-serrilhada e alfinetes de seda ultrafinos.",
            careInstructions = "Lavagem a seco profissional recomendada. Se em casa, lavar à mão com xampu neutro em água fria. Passar pelo avesso com ferro morno (seda) sem borrifar água diretamente para não manchar.",
            recommendedNeedleAndThread = "Agulha Microtex 60/8 ou 70/10, alfinetes de seda 0.4mm, linha 100% seda ou poliéster ultrafino nº 150.",
            riskLevel = RiskLevel.HIGH
        ),
        FabricDefinition(
            id = "silk_blend",
            namePt = "Mistos de seda (Seda com algodão/poliéster)",
            nameEn = "Silk Blend",
            category = "Misto Nobre",
            difficultyFactor = 1.45,
            behaviorNotes = "Combina o brilho e caimento da seda com a estabilidade do algodão ou durabilidade do poliéster.",
            alterationBehavior = "Mais dócil que a seda pura, mas ainda sensível a marcas de agulha e ferro quente. Mantém boa estabilidade em pences e barras de lenço.",
            careInstructions = "Ciclo delicado à mão ou lavagem a seco. Passar com pano protetor em temperatura média.",
            recommendedNeedleAndThread = "Agulha Microtex 70/10, linha poliéster fina nº 120.",
            riskLevel = RiskLevel.MEDIUM
        ),
        FabricDefinition(
            id = "polyester",
            namePt = "Poliéster e microfibra",
            nameEn = "Polyester / Microfiber",
            category = "Fibra Sintética",
            difficultyFactor = 1.15,
            behaviorNotes = "Resistente, não amassa, de secagem rápida e alta durabilidade de cor.",
            alterationBehavior = "Termoplástico: derrete ou brilha se passar com ferro muito quente. Costuras podem repuxar (puckering) se a tensão da linha estiver alta. Use sempre sapata de teflon.",
            careInstructions = "Lavar em máquina até 40°C. Ferro morno (máximo 110°C / ponto sintético) sempre com pano de passar ou sapata protetora.",
            recommendedNeedleAndThread = "Agulha Universal ou Microtex 75/11, linha 100% poliéster nº 120 com tensão suave.",
            riskLevel = RiskLevel.LOW
        ),
        FabricDefinition(
            id = "wool",
            namePt = "Lã fria e Lã batida",
            nameEn = "Wool / Worsted Wool",
            category = "Fibra Animal Estruturada",
            difficultyFactor = 1.55,
            behaviorNotes = "Fibra nobre de alfaiataria com excelente memória elástica e capacidade de moldagem com ferro a vapor.",
            alterationBehavior = "Perfeita para alfaiataria clássica: aceita embeber folga (acomodar sobra sem franzir) e moldar curvas anatômicas no vapor. Exige acabamento interno com forro.",
            careInstructions = "Lavagem a seco em lavanderia especializada. Escovar com cerdas naturais. Passar com ferro a vapor e pano úmido (sapata de alfaiate).",
            recommendedNeedleAndThread = "Agulha Universal 80/12 para lã fria ou 90/14 para lã batida, linha de poliéster nº 120 e linha de alinhavo de algodão cru.",
            riskLevel = RiskLevel.MEDIUM
        ),
        FabricDefinition(
            id = "knit",
            namePt = "Malha e Jersey",
            nameEn = "Knit / Jersey",
            category = "Estrutura Entrelaçada / Elástica",
            difficultyFactor = 1.35,
            behaviorNotes = "Tecido elástico por estrutura de laçadas, extremamente confortável e maleável.",
            alterationBehavior = "Se costurado com ponto reto tradicional, a costura arrebenta ao vestir. Exige ponto overloque, ponto zigue-zague elástico ou máquina galoneira para barras.",
            careInstructions = "Lavar em ciclo suave. Secar na horizontal em varal de chão para não esticar o comprimento. Ferro brando.",
            recommendedNeedleAndThread = "Agulha Ponta Bola (Ballpoint / Stretch) 75/11 ou 80/12, linha de poliéster com fio de helanca no looper da overloque.",
            riskLevel = RiskLevel.MEDIUM
        ),
        FabricDefinition(
            id = "elastane",
            namePt = "Elastano / Spandex / Lycra",
            nameEn = "Spandex / Elastane",
            category = "Filamento Sintético Ultra-elástico",
            difficultyFactor = 1.4,
            behaviorNotes = "Capacidade de esticar até 500% e retornar ao tamanho original sem deformar.",
            alterationBehavior = "Costuras tendem a ondular se puxadas durante o transporte da máquina. Exige agulha Stretch especial e pressão reduzida do calcador.",
            careInstructions = "Nunca usar amaciante em excesso ou água fervente, pois dissolve a elasticidade da fibra. Secar à sombra longe de fontes de calor.",
            recommendedNeedleAndThread = "Agulha Stretch 75/11 com ponta especial anti-pulo de ponto, linha 100% poliéster texturizada.",
            riskLevel = RiskLevel.MEDIUM
        ),
        FabricDefinition(
            id = "velvet",
            namePt = "Veludo e Veludo Cotelê",
            nameEn = "Velvet / Corduroy",
            category = "Tecido com Pelo / Felpa",
            difficultyFactor = 1.6,
            behaviorNotes = "Superfície felpuda com direção de pelo que altera o brilho e a tonalidade conforme a luz.",
            alterationBehavior = "Ferro quente direto esmaga o pelo para sempre, deixando uma mancha brilhosa irreparável. As duas camadas de tecido escorregam entre si ao costurar.",
            careInstructions = "Passar somente pelo avesso sobre toalha felpuda ou tábua de agulhas de veludo usando apenas vapor flutuante. Limpeza a seco preferencial.",
            recommendedNeedleAndThread = "Agulha Universal 80/12 ou Microtex 80/12, alfinetagem farta nas margens, alinhavo prévio obrigatório.",
            riskLevel = RiskLevel.HIGH
        ),
        FabricDefinition(
            id = "leather",
            namePt = "Couro legítimo e Pelica",
            nameEn = "Genuine Leather / Suede",
            category = "Pele Natural Nobre",
            difficultyFactor = 2.2,
            behaviorNotes = "Material denso, não elástico, respirável e extremamente resistente.",
            alterationBehavior = "Não aceita alfinetes comuns (use clipes de costura). Cada furo de agulha perfura a pele em definitivo. Requer máquina com transporte duplo ou calcador de teflon/rolete.",
            careInstructions = "Hidratar com creme específico para couro a cada 6 meses. Nunca molhar ou guardar em saco plástico fechado. Arejar à sombra.",
            recommendedNeedleAndThread = "Agulha ponta lança para couro (Leather needle) 90/14 a 110/18, linha de nylon ou poliéster de alta tenacidade, calcador de teflon.",
            riskLevel = RiskLevel.HIGH
        )
    ).associateBy { it.id }
}
