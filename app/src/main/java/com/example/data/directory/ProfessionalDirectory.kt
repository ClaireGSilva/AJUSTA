package com.example.data.directory

import com.example.model.TailorProfile
import com.example.model.TailorReview
import com.example.model.TailorServiceOffer

object ProfessionalDirectory {

    val verifiedProfessionals: List<TailorProfile> = listOf(
        TailorProfile(
            id = "atelier_sartoria_jardins",
            name = "Mestre Antônio Silveira",
            atelierName = "Sartoria Silveira & Alfaiataria Fina",
            subtitle = "Alfaiate mestre com formação clássica italiana",
            address = "Rua Oscar Freire, 1120 - Jardins",
            neighborhood = "Jardins",
            city = "São Paulo",
            state = "SP",
            distanceKm = 1.2,
            rating = 4.9f,
            reviewCount = 84,
            specialties = listOf("Alfaiataria Masculina", "Blazers Estruturados", "Ternos", "Lã Fria"),
            phone = "+551130829910",
            whatsapp = "5511984219900",
            openingHours = "Seg a Sex: 09h às 19h | Sáb: 09h às 14h",
            experienceYears = 32,
            isVerified = true,
            bio = "Especialista em desmonte e reconstrução de ombros, forrações em seda pura e ajustes de caimento anatômico com prova tradicional.",
            latitude = -23.5629,
            longitude = -46.6687,
            services = listOf(
                TailorServiceOffer("Ajuste de ombro e cava estruturada", "R$ 120–180", "4 a 6 dias úteis"),
                TailorServiceOffer("Ajuste de cós e gancho alfaiataria", "R$ 60–90", "2 a 3 dias úteis"),
                TailorServiceOffer("Barra invisível feita à mão", "R$ 40–55", "1 a 2 dias úteis"),
                TailorServiceOffer("Subida de manga com carcela", "R$ 65–85", "3 a 4 dias úteis")
            ),
            reviews = listOf(
                TailorReview("Rodrigo Mello", 5.0f, "Há 2 semanas", "Salvou um blazer de lã fria importado que parecia grande demais. O ombro ficou perfeito.", "Blazer de alfaiataria"),
                TailorReview("Felipe Barreto", 4.8f, "Há 1 mês", "Atendimento impecável, explicou tudo sobre a margem interna antes de cortar.", "Costume 2 peças")
            )
        ),
        TailorProfile(
            id = "atelier_haute_couture_pinheiros",
            name = "Clara Vasconcellos",
            atelierName = "Atelier Clara Vasconcellos Haute Couture",
            subtitle = "Especialista em vestidos de festa, seda pura e noivas",
            address = "Rua dos Pinheiros, 740 - Pinheiros",
            neighborhood = "Pinheiros",
            city = "São Paulo",
            state = "SP",
            distanceKm = 2.4,
            rating = 5.0f,
            reviewCount = 112,
            specialties = listOf("Vestidos de Festa", "Seda & Chiffon", "Bainha de Lenço", "Bordados & Renda"),
            phone = "+551138125540",
            whatsapp = "5511991204488",
            openingHours = "Seg a Sex: 10h às 19h (Com hora marcada)",
            experienceYears = 19,
            isVerified = true,
            bio = "Ateliê dedicado a tecidos nobres, sedas esvoaçantes e vestidos de alta exigência. Atendimento exclusivo com marcação no corpo.",
            latitude = -23.5670,
            longitude = -46.6890,
            services = listOf(
                TailorServiceOffer("Bainha de lenço em seda / chiffon", "R$ 90–150", "3 a 5 dias úteis"),
                TailorServiceOffer("Ajuste de decote e pence de busto", "R$ 80–130", "3 a 4 dias úteis"),
                TailorServiceOffer("Troca de zíper invisível com acabamento em viés", "R$ 60–90", "2 a 3 dias úteis"),
                TailorServiceOffer("Ajuste lateral com forro de festa", "R$ 100–160", "4 a 6 dias úteis")
            ),
            reviews = listOf(
                TailorReview("Juliana Costa", 5.0f, "Há 4 dias", "Minha madrinha de casamento indicou e foi incrível. Ajustou meu vestido de seda pura sem marcar nenhuma agulha.", "Vestido longo de seda"),
                TailorReview("Camila Prado", 5.0f, "Há 3 semanas", "Pontualidade britânica e acabamento dos deuses.", "Vestido em crepe georgette")
            )
        ),
        TailorProfile(
            id = "atelier_express_denim_itaim",
            name = "Marcos & Helena Duarte",
            atelierName = "Duarte Denim Lab & Reparos Express",
            subtitle = "Especialistas em jeans premium, barra original e sarja",
            address = "Rua Joaquim Floriano, 466 - Itaim Bibi",
            neighborhood = "Itaim Bibi",
            city = "São Paulo",
            state = "SP",
            distanceKm = 3.1,
            rating = 4.8f,
            reviewCount = 146,
            specialties = listOf("Jeans & Denim", "Barra Original", "Cós Anatômico", "Ajuste de Gancho"),
            phone = "+551131682200",
            whatsapp = "5511973418822",
            openingHours = "Seg a Sáb: 08h30 às 20h",
            experienceYears = 16,
            isVerified = true,
            bio = "Maquinário industrial pesado de pesponto e linha ocre original. Especialistas em preservar o desbotado de fábrica e rebites.",
            latitude = -23.5840,
            longitude = -46.6780,
            services = listOf(
                TailorServiceOffer("Barra original mantendo a lavagem", "R$ 45–60", "Mesmo dia ou 24h"),
                TailorServiceOffer("Ajuste de cós pelo centro com reposicionamento de presilha", "R$ 55–80", "1 a 2 dias úteis"),
                TailorServiceOffer("Afunilamento de perna alinhando pelo fio", "R$ 50–70", "1 a 2 dias úteis"),
                TailorServiceOffer("Cerzimento técnico de rasgo entrepernas", "R$ 35–50", "24h")
            ),
            reviews = listOf(
                TailorReview("Marcelo Faria", 5.0f, "Ontem", "A barra original ficou idêntica à de fábrica. Não dá para notar que foi encurtada!", "Calça jeans selvedge"),
                TailorReview("Beatriz Ramos", 4.7f, "Há 1 semana", "Muito rápidos, deixei de manhã e peguei no fim da tarde.", "Calça flare jeans")
            )
        ),
        TailorProfile(
            id = "atelier_leblon_ipanema",
            name = "Dona Cecília Marinho",
            atelierName = "Atelier Marinho — Linho & Alfaiataria Leve",
            subtitle = "Especialista em linho puro, viscose, vestidos e camisaria",
            address = "Avenida Visconde de Pirajá, 550 - Ipanema",
            neighborhood = "Ipanema",
            city = "Rio de Janeiro",
            state = "SP", // Available in search
            distanceKm = 4.8,
            rating = 4.9f,
            reviewCount = 98,
            specialties = listOf("Linho Puro", "Camisaria", "Vestidos de Verão", "Bainhas Especiais"),
            phone = "+552125123300",
            whatsapp = "5521998776655",
            openingHours = "Seg a Sex: 09h às 18h",
            experienceYears = 26,
            isVerified = true,
            bio = "Tradição em peças de linho que desfiam com facilidade. Chuleio reforçado e respeito à fibra natural para evitar encolhimento pós-ajuste.",
            latitude = -22.9838,
            longitude = -43.2080,
            services = listOf(
                TailorServiceOffer("Ajuste de cintura e quadril em linho", "R$ 60–85", "2 a 3 dias úteis"),
                TailorServiceOffer("Barra dobrada italiana em calça de linho", "R$ 45–60", "2 dias úteis"),
                TailorServiceOffer("Encurtamento de manga de camisa com carcela", "R$ 50–75", "2 a 3 dias úteis"),
                TailorServiceOffer("Ajuste de pences de camisa e blusa", "R$ 40–55", "1 a 2 dias úteis")
            ),
            reviews = listOf(
                TailorReview("Lucas Brandão", 5.0f, "Há 5 dias", "Ajustou três camisas de linho minhas com perfeição.", "Camisas sociais linho"),
                TailorReview("Fernanda Diniz", 4.9f, "Há 2 semanas", "Dona Cecília é um tesouro. Dá aula de costura só de olhar a peça.", "Vestido midi linho")
            )
        ),
        TailorProfile(
            id = "atelier_couro_especiais",
            name = "Gilberto Siqueira",
            atelierName = "Couro Nobre & Reformas Especiais",
            subtitle = "Alfaiataria em couro natural, camurça e jaquetas pesadas",
            address = "Alameda Lorena, 1450 - Cerqueira César",
            neighborhood = "Cerqueira César",
            city = "São Paulo",
            state = "SP",
            distanceKm = 1.9,
            rating = 4.9f,
            reviewCount = 67,
            specialties = listOf("Couro Legítimo", "Camurça", "Jaquetas Biker", "Troca de Zíper Tratorado"),
            phone = "+551130614420",
            whatsapp = "5511985552311",
            openingHours = "Seg a Sex: 10h às 18h30",
            experienceYears = 28,
            isVerified = true,
            bio = "Oficina especializada em pelica, couro bovino e jaquetas estruturadas. Máquinas de coluna com transporte duplo que não deixam marcas indesejadas no couro.",
            latitude = -23.5601,
            longitude = -46.6660,
            services = listOf(
                TailorServiceOffer("Troca de zíper tratorado YKK em jaqueta de couro", "R$ 90–140", "3 a 5 dias úteis"),
                TailorServiceOffer("Ajuste de manga de jaqueta com zíper de punho", "R$ 110–170", "5 a 7 dias úteis"),
                TailorServiceOffer("Ajuste lateral de jaqueta ou colete de couro", "R$ 130–200", "5 a 7 dias úteis"),
                TailorServiceOffer("Substituição de forro integral", "R$ 140–220", "6 a 8 dias úteis")
            ),
            reviews = listOf(
                TailorReview("Thiago Neves", 5.0f, "Há 1 semana", "Trabalho cirúrgico na minha jaqueta de couro vintage de 1990. Vale cada centavo.", "Jaqueta biker couro"),
                TailorReview("Tatiana Moura", 4.9f, "Há 1 mês", "Difícil achar quem mexa em couro com essa competência.", "Saia de couro pelica")
            )
        )
    )

    fun search(query: String, specialtyFilter: String? = null): List<TailorProfile> {
        val q = query.trim().lowercase()
        return verifiedProfessionals.filter { profile ->
            val matchesQuery = q.isEmpty() ||
                profile.name.lowercase().contains(q) ||
                profile.atelierName.lowercase().contains(q) ||
                profile.neighborhood.lowercase().contains(q) ||
                profile.city.lowercase().contains(q) ||
                profile.specialties.any { it.lowercase().contains(q) } ||
                profile.subtitle.lowercase().contains(q)

            val matchesSpecialty = specialtyFilter == null || specialtyFilter == "Todos" ||
                profile.specialties.any { it.contains(specialtyFilter, ignoreCase = true) }

            matchesQuery && matchesSpecialty
        }
    }

    fun getById(id: String): TailorProfile? = verifiedProfessionals.firstOrNull { it.id == id }
}
