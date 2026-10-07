package com.example.model

enum class GarmentRepairStage(
    val id: String,
    val label: String,
    val shortLabel: String,
    val percentCompleted: Int,
    val description: String
) {
    ORCADO(
        id = "orcado",
        label = "Orçamento Preliminar",
        shortLabel = "Orç.",
        percentCompleted = 15,
        description = "15% Concluído • 85% Pendente (Diagnóstico salvo, aguardando início do reparo)"
    ),
    FICHA_EMITIDA(
        id = "ficha",
        label = "Ficha Gerada",
        shortLabel = "Ficha",
        percentCompleted = 35,
        description = "35% Concluído • 65% Pendente (Ficha técnica pronta para levar ao atelier/costureira)"
    ),
    EM_ANDAMENTO(
        id = "em_andamento",
        label = "Em Execução / Costura",
        shortLabel = "Costura",
        percentCompleted = 65,
        description = "65% Concluído • 35% Pendente (Peça em alteração na bancada/máquina de costura)"
    ),
    PROVA_PENDENTE(
        id = "prova",
        label = "Prova Agendada",
        shortLabel = "Prova",
        percentCompleted = 85,
        description = "85% Concluído • 15% Pendente (Ajuste alfinetado/montado, aguardando prova de caimento)"
    ),
    CONCLUIDO(
        id = "concluido",
        label = "Reparo Concluído",
        shortLabel = "Pronto ✓",
        percentCompleted = 100,
        description = "100% Concluído • Peça ajustada, acabamento revisado e pronta para uso"
    )
}
