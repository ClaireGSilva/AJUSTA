package com.example.knowledge

import com.example.model.RepairTutorial
import com.example.model.TutorialStep

object RepairTutorialsData {
    val tutorials: List<RepairTutorial> = listOf(
        RepairTutorial(
            id = "pregar_botao",
            title = "Pregar Botão com Haste de Linha",
            subtitle = "Para camisas, casacos e calças sem soltar novamente",
            category = "Botões",
            difficulty = "Muito Fácil",
            estimatedTimeMinutes = 8,
            toolsNeeded = listOf(
                "Agulha de mão média (nº 7 ou 8)",
                "Linha poliéster correspondente à cor da peça",
                "Palito de dente ou fósforo (para fazer a haste)",
                "Tesoura pequena de ponta fina"
            ),
            steps = listOf(
                TutorialStep(
                    stepNumber = 1,
                    title = "Preparar a linha dupla",
                    description = "Corte cerca de 50 cm de linha, passe pela agulha e una as duas pontas com um nó firme.",
                    tip = "Linha dupla garante o dobro da resistência a puxões."
                ),
                TutorialStep(
                    stepNumber = 2,
                    title = "Primeiro ponto de ancoragem",
                    description = "Passe a agulha pelo avesso do tecido onde o botão ficará e dê um pontinho pequeno no lugar para prender a linha.",
                    tip = "Nunca dê o nó pelo lado de fora da peça."
                ),
                TutorialStep(
                    stepNumber = 3,
                    title = "Posicionar o espaçador (haste)",
                    description = "Coloque o botão sobre o tecido e posicione um palito de dente sobre ele. Passe a linha pelos furos por cima do palito de 5 a 6 vezes.",
                    tip = "O palito cria o espaço necessário para a casa do botão entrar sem repuxar o tecido."
                ),
                TutorialStep(
                    stepNumber = 4,
                    title = "Enrolar a haste de linha",
                    description = "Retire o palito. Puxe o botão levemente para cima e enrole a linha firmemente 4 a 5 voltas ao redor dos fios entre o botão e o tecido.",
                    tip = "Essa haste é o segredo da alfaiataria: evita que o botão rasgue o tecido."
                ),
                TutorialStep(
                    stepNumber = 5,
                    title = "Rematar no avesso",
                    description = "Passe a agulha para o avesso, dê dois nós cegos bem rentes ao tecido e corte o excesso de linha.",
                    tip = "Dê um pingo de esmalte incolor no nó final para travamento definitivo."
                )
            ),
            commonMistakes = listOf(
                "Apertar o botão colado demais no tecido (dificulta abotoar e rasga a casa)",
                "Usar linha fraca de algodão para botões de casaco pesado",
                "Fazer nós frouxos que se desmancham na primeira lavagem"
            ),
            tailorTip = "Se o tecido estiver esgarçado no local do botão, costure um botãozinho plano pequeno pelo avesso para distribuir a tração!"
        ),

        RepairTutorial(
            id = "bainha_invisivel",
            title = "Bainha Invisível Feita à Mão",
            subtitle = "Ajuste o comprimento de calças sociais e vestidos sem marca externa",
            category = "Bainhas",
            difficulty = "Fácil",
            estimatedTimeMinutes = 20,
            toolsNeeded = listOf(
                "Agulha fina (nº 9)",
                "Linha da cor exata do tecido",
                "Alfinetes de cabeça",
                "Ferro de passar roupa",
                "Fita métrica ou régua"
            ),
            steps = listOf(
                TutorialStep(
                    stepNumber = 1,
                    title = "Marcar e vincar com ferro",
                    description = "Com o calçado apropriado, dobre a barra na altura desejada e prenda com alfinetes. Passe o ferro quente para marcar o vinco exato da dobra.",
                    tip = "O vinco a ferro é 50% do acabamento de uma boa barra."
                ),
                TutorialStep(
                    stepNumber = 2,
                    title = "Dobrar a sobra interna",
                    description = "Deixe uma sobra interna de 3 a 4 cm. Dobre 0,5 cm da borda para dentro e passe o ferro novamente.",
                    tip = "Isso esconde a borda crua do tecido e impede o desfiamento."
                ),
                TutorialStep(
                    stepNumber = 3,
                    title = "O ponto invisível (ponto espinha de peixe)",
                    description = "Prenda a linha na borda dobrada. Na peça principal, pegue apenas 1 ou 2 fios minúsculos do tecido externo com a ponta da agulha.",
                    tip = "Quanto menos fios você pegar, mais invisível ficará do lado de fora."
                ),
                TutorialStep(
                    stepNumber = 4,
                    title = "Avançar em zigue-zague frouxo",
                    description = "Avance 1 cm para a frente e passe a agulha na barra interna. Repita pegando 1 fio no tecido e prendendo na barra.",
                    tip = "Nunca puxe a linha com força excessiva para não franzir a parte externa da calça."
                ),
                TutorialStep(
                    stepNumber = 5,
                    title = "Remate e conferência",
                    description = "Faça o remate na costura lateral interna e passe a ferro pelo avesso com um pano protetor.",
                    tip = "Pelo lado direito, os pontos devem ser totalmente imperceptíveis a olho nu."
                )
            ),
            commonMistakes = listOf(
                "Puxar a linha demais causando pequenas 'covinhas' na frente da calça",
                "Pegar muitos fios do tecido externo, deixando pontos visíveis",
                "Cortar a barra antes de passar o ferro e provar"
            ),
            tailorTip = "Use linha de poliéster de alta qualidade. Se for tecido muito fino (seda ou viscose), use agulha extra fina para não abrir buracos."
        ),

        RepairTutorial(
            id = "destravar_ajustar_ziper",
            title = "Destravar e Ajustar Zíper Frouxo",
            subtitle = "Recupere zíper que abre sozinho ou emperrou no tecido",
            category = "Zíper",
            difficulty = "Muito Fácil",
            estimatedTimeMinutes = 5,
            toolsNeeded = listOf(
                "Lápis grafite 2B ou vela de parafina branca",
                "Alicate comum pequeno",
                "Pinça de sobrancelha",
                "Cotonete com óleo mineral ou vaselina (opcional)"
            ),
            steps = listOf(
                TutorialStep(
                    stepNumber = 1,
                    title = "Identificar o problema",
                    description = "Se emperrou no tecido: use uma pinça para puxar gentilmente o tecido preso enquanto mexe o cursor para trás com cuidado.",
                    tip = "Nunca force o zíper para a frente se tiver tecido preso."
                ),
                TutorialStep(
                    stepNumber = 2,
                    title = "Lubrificação dos dentes",
                    description = "Passe a ponta de grafite de um lápis nos dentes acima e abaixo do cursor, ou esfregue a lateral de uma vela branca seca.",
                    tip = "O grafite é um lubrificante seco excelente que não mancha o tecido."
                ),
                TutorialStep(
                    stepNumber = 3,
                    title = "Se o zíper abre sozinho (cursor frouxo)",
                    description = "Quando os dentes não fecham atrás do cursor, significa que a boca do cursor alargou com o uso.",
                    tip = "Não precisa trocar o zíper inteiro!"
                ),
                TutorialStep(
                    stepNumber = 4,
                    title = "Apertar o cursor com alicate",
                    description = "Com o cursor na base, use o alicate para apertar de leve as duas abas laterais traseiras do cursor metálico.",
                    tip = "Aperte suavemente aos poucos e teste. Se apertar muito, o cursor trava."
                ),
                TutorialStep(
                    stepNumber = 5,
                    title = "Movimento de assentamento",
                    description = "Abra e feche o zíper de 3 a 5 vezes para que a cera ou o grafite se espalhe uniformemente pelos trilhos.",
                    tip = "O zíper voltará a correr suave e trancar perfeitamente."
                )
            ),
            commonMistakes = listOf(
                "Puxar com força bruta quando o cursor mastiga o tecido (rasga o tecido)",
                "Apertar o alicate no centro do cursor (pode quebrar a peça)",
                "Usar óleo de cozinha (fica rançoso e mancha para sempre a roupa)"
            ),
            tailorTip = "Se o cursor for de plástico e quebrou, você pode comprar apenas o cursor compatível em armarinhos por centavos e encaixar na trava superior!"
        ),

        RepairTutorial(
            id = "pence_cintura_manual",
            title = "Ajustar Cintura Larga com Pence Manual",
            subtitle = "Reduza de 2 a 4 cm no cós traseiro de saia ou calça social",
            category = "Ajustes",
            difficulty = "Médio",
            estimatedTimeMinutes = 25,
            toolsNeeded = listOf(
                "Giz de alfaiate ou sabonete seco para riscar",
                "Alfinetes",
                "Agulha de mão média",
                "Linha resistente da cor da peça",
                "Fita métrica"
            ),
            steps = listOf(
                TutorialStep(
                    stepNumber = 1,
                    title = "Medir o excesso no corpo",
                    description = "Vista a peça pelo avesso. Com as mãos, pince o excesso de tecido no centro das costas ou nas duas laterais e prenda com alfinetes.",
                    tip = "Lembre-se de deixar 1 dedo de folga para sentar confortavelmente."
                ),
                TutorialStep(
                    stepNumber = 2,
                    title = "Desenhar o triângulo da pence",
                    description = "Tire a peça e marque com giz um triângulo: a base fica no cós (ex: 2 cm de cada lado) e o vértice desce cerca de 8 a 10 cm morrendo em zero.",
                    tip = "O segredo de uma pence suave é o vértice morrer gradualmente em zero, sem fazer bico no tecido."
                ),
                TutorialStep(
                    stepNumber = 3,
                    title = "Alinhavar o traçado",
                    description = "Faça pontos largos de alinhavo sobre a linha de giz para prender as duas camadas antes da costura final.",
                    tip = "Experimente a peça pelo direito para conferir o caimento antes de costurar definitivo."
                ),
                TutorialStep(
                    stepNumber = 4,
                    title = "Costura em ponto atrás manual",
                    description = "Costure do cós até a ponta usando o 'Ponto Atrás' (backstitch), que imita a firmeza da máquina de costura.",
                    tip = "O ponto atrás é extremamente resistente e aguenta a tração da cintura."
                ),
                TutorialStep(
                    stepNumber = 5,
                    title = "Passar a ferro",
                    description = "Abra a peça pelo avesso e passe a ferro tombando a sobra da pence para a costura central das costas.",
                    tip = "A pence assentada pelo ferro fica plana e discreta."
                )
            ),
            commonMistakes = listOf(
                "Fazer a pence curta demais (cria um 'papo' ou bico feio na nádega)",
                "Usar ponto corrido frouxo que arrebenta ao sentar",
                "Cortar a sobra de tecido (mantenha a sobra para poder soltar no futuro se engordar)"
            ),
            tailorTip = "Nunca corte a sobra interna da pence! Se você dobrar e passar com ferro, ela fica invisível e preserva o valor da roupa."
        ),

        RepairTutorial(
            id = "cerzimento_invisivel",
            title = "Cerzimento de Pequenos Furos e Rasgos",
            subtitle = "Recupere suéteres de lã, malhas e camisas sem deixar remendo visível",
            category = "Cerzimento",
            difficulty = "Fácil",
            estimatedTimeMinutes = 15,
            toolsNeeded = listOf(
                "Agulha de bordado sem ponta ou agulha fina",
                "Fio retirado da própria peça (da barra interna ou bolso)",
                "Tesourinha afiada",
                "Pedaço de entretela termocolante fina (opcional)"
            ),
            steps = listOf(
                TutorialStep(
                    stepNumber = 1,
                    title = "Limpar a área do furo",
                    description = "Apare fiapos soltos com a tesourinha, deixando as bordas do furinho limpas e alinhadas.",
                    tip = "Em malhas de lã, identifique os pontos soltos que podem desfiar."
                ),
                TutorialStep(
                    stepNumber = 2,
                    title = "Obter o fio idêntico",
                    description = "Se possível, desfie 20 cm de linha de uma sobra interna da costura ou da barra da própria roupa.",
                    tip = "O fio original da própria trama torna o conserto 100% invisível!"
                ),
                TutorialStep(
                    stepNumber = 3,
                    title = "Criar os fios da urdidura (verticais)",
                    description = "Prenda a linha a 2 mm da borda e passe fios paralelos de um lado ao outro do furo, cobrindo o espaço sem repuxar.",
                    tip = "Mantenha a mesma tensão do tecido original."
                ),
                TutorialStep(
                    stepNumber = 4,
                    title = "Tecer a trama (horizontais)",
                    description = "Passe a agulha por cima e por baixo dos fios verticais alternadamente, recriando a malha do tecido ponto a ponto.",
                    tip = "Use a cabeça da agulha para empurrar os fios suavemente, fechando a fresta."
                ),
                TutorialStep(
                    stepNumber = 5,
                    title = "Assentamento com vapor",
                    description = "Prenda a linha no avesso sem dar nós volumosos. Aplique vapor do ferro a 1 cm de distância para que os fios se unam.",
                    tip = "O vapor 'reidrata' as fibras têxteis, camuflando a costura."
                )
            ),
            commonMistakes = listOf(
                "Puxar o furo como um 'fuxico', deixando uma prega visível",
                "Usar linha de cor diferente mesmo que parecida (a luz do sol denuncia)",
                "Dar nós grossos que incomodam na pele"
            ),
            tailorTip = "Para buraquinhos de traça em blusas de lã, o cerzimento suíço (duplicating stitch) recria o elo da malha exatamente como foi tricotada."
        )
    )
}
