# ARCHITECTURE — Ajusta

> **Padrões de Arquitetura, Organização de Código e Fluxo de Dados**  
> *Versão:* 1.0.0 | *Plataforma:* Android Nativo (Kotlin 2.2.10)

---

## 🏛️ Filosofia Arquitetural

O **Ajusta** foi estruturado segundo os princípios de **Clean Architecture** e **MVVM (Model-View-ViewModel)** com **Unidirectional Data Flow (UDF)**. 

Essa separação garante:
1. **Alta Testabilidade:** A camada de regras de negócio é pura em Kotlin e desacoplada da interface visual.
2. **Transferibilidade Limpa:** Qualquer engenheiro Android consegue navegar intuitivamente pelo código sem curvas de aprendizado obscuras.
3. **Resiliência a Mudanças:** Trocar a fonte de dados (ex: migrar de Room para uma API REST corporativa) ou o provedor de IA não afeta as telas de UI.

---

## 📐 Diagrama de Camadas

```
┌────────────────────────────────────────────────────────┐
│                      UI LAYER                          │
│   Jetpack Compose Composables + Material 3 Design      │
│   Screens: Camera, Result, Chat, History, Pro, DIY...  │
└──────────────────────────▲─────────────────────────────┘
                           │ (StateFlow / UI Events)
┌──────────────────────────┴─────────────────────────────┐
│                   VIEWMODEL LAYER                      │
│   HomeViewModel, ResultViewModel, ChatViewModel...     │
│   Gerenciamento de Estado, Coroutines e Lifecycle      │
└──────────────────────────▲─────────────────────────────┘
                           │ (Suspending Calls / Flows)
┌──────────────────────────┴─────────────────────────────┐
│                    DOMAIN / ENGINES                    │
│   • PriceEstimateEngine (Cálculo algorítmico R$)       │
│   • RecommendationEngine ("Vale a Pena Reformar?")     │
│   • DiagnosticEngine (Regras de triagem têxtil)        │
│   • GarmentVisionAnalyzer (Interpretação multimodal)   │
└──────────────────────────▲─────────────────────────────┘
                           │ (Data abstraction)
┌──────────────────────────┴─────────────────────────────┐
│                       DATA LAYER                       │
│   ┌─────────────────────────┐ ┌──────────────────────┐ │
│   │   Local (Room SQLite)   │ │  Cloud (Firestore)   │ │
│   │ AppDatabase / Daos /    │ │ FirestoreSyncService │ │
│   │ Entities / Converters   │ │ (Opcional / Nuvem)   │ │
│   └─────────────────────────┘ └──────────────────────┘ │
│   ┌─────────────────────────┐ ┌──────────────────────┐ │
│   │       AI Services       │ │    Auth Services     │ │
│   │ GeminiService (OkHttp)  │ │ FirebaseAuthService  │ │
│   └─────────────────────────┘ └──────────────────────┘ │
└────────────────────────────────────────────────────────┘
```

---

## 📁 Estrutura de Pacotes (`com.example`)

A árvore de código está organizada de forma coesa por responsabilidades funcionais:

```
com.example/
 ├── MainActivity.kt            # Entrada da aplicação e Host de Navegação
 │
 ├── ai/                        # Integração com Inteligência Artificial
 │    ├── GeminiService.kt          # Cliente HTTP para a Google Gemini REST API v1beta
 │    ├── GarmentVisionAnalyzer.kt  # Extrator e parser de diagnósticos multimodais
 │    ├── AlterationPlanner.kt      # Planejador de passos técnicos de costura
 │    ├── DiagnosticEngine.kt       # Regras lógicas de classificação e diagnóstico
 │    └── RecommendationEngine.kt   # Avaliação de viabilidade "Vale a Pena?"
 │
 ├── auth/                      # Camada de Autenticação
 │    └── FirebaseAuthService.kt    # Gestão de credenciais, Google Sign-In e estado de perfil
 │
 ├── camera/                    # Captura de Imagens
 │    └── CameraPreview.kt          # Componente CameraX com controle de ciclo de vida
 │
 ├── data/                      # Persistência e Fontes de Dados
 │    ├── GarmentProgressManager.kt # Gerenciamento de status e progresso de reparos
 │    ├── local/                    # Banco de dados local Room
 │    │    ├── AppDatabase.kt           # Classe principal do Room Database (v2)
 │    │    ├── Entities.kt              # Tabelas (saved_analyses, diagnostic_results, professional_orders)
 │    │    ├── Daos.kt                  # Interfaces DAO com consultas SQL otimizadas
 │    │    └── JsonConverters.kt        # Conversores para tipos complexos e listas
 │    ├── cloud/                    # Sincronização em nuvem
 │    │    └── FirestoreSyncService.kt  # Sincronizador assíncrono para Firebase Firestore
 │    ├── directory/                # Catálogo de dados estáticos de ateliês e profissionais
 │    └── demo/                     # Carga inicial e mock controlado para testes de interface
 │
 ├── knowledge/                 # Base de Conhecimento Têxtil
 │    ├── FabricKnowledgeBase.kt    # Dicionário de tecidos, tramas e cuidados
 │    └── DIYTutorials.kt           # Passo a passo estruturado de tutoriais
 │
 ├── model/                     # Modelos de Domínio Tipados
 │    ├── GarmentAnalysis.kt        # Modelo central de diagnóstico de vestimenta
 │    ├── PriceEstimate.kt          # Estrutura com discriminação de preço e fatores
 │    ├── RiskAssessment.kt         # Níveis de risco e limitações de alteração
 │    ├── ProfessionalOrder.kt      # Ordem de serviço do Módulo Pro
 │    └── WorthItRecommendation.kt   # Veredito de viabilidade econômica
 │
 ├── notification/              # Lembretes Locais
 │    ├── NotificationHelper.kt         # Inicializador de canais e emissor de notificações
 │    └── RepairNotificationReceiver.kt # BroadcastReceiver do AlarmManager
 │
 ├── pricing/                   # Motor de Cálculos Financeiros
 │    └── PriceEstimateEngine.kt    # Algoritmo de precificação em Reais (R$)
 │
 ├── ui/                        # Camada de Apresentação (Jetpack Compose)
 │    ├── components/               # Componentes reutilizáveis (TopBar, ThemeToggle, Cards)
 │    ├── screens/                  # Telas do aplicativo organizadas por fluxo:
 │    │    ├── home/                    # Painel principal e atalhos rápidos
 │    │    ├── camera/                  # Interface de captura e seleção de fotos
 │    │    ├── result/                  # Apresentação do diagnóstico técnico e preços
 │    │    ├── chat/                    # Ateliê AI conversacional
 │    │    ├── ticket/                  # Ficha de atendimento e exportação WhatsApp
 │    │    ├── professional/            # Quadro Kanban de gestão de pedidos
 │    │    ├── professionals/           # Catálogo de ateliês recomendados
 │    │    ├── diy/                     # Tutoriais de costura passo a passo
 │    │    ├── history/                 # Histórico de análises salvas
 │    │    ├── knowledge/               # Glossário de tecidos e acabamentos
 │    │    └── notifications/           # Configurações de lembretes e canais
 │    └── theme/                    # Theme.kt, Color.kt, Type.kt (Material Design 3)
 │
 └── util/                      # Utilitários de formato, extensões e auxiliares
```

---

## 🔄 Fluxo Unidirecional de Dados (UDF)

1. **Ação do Usuário (Event):** O usuário clica no botão "Analisar Peça".
2. **ViewModel:** Dispara uma coroutine em `Dispatchers.IO` através do `viewModelScope`.
3. **Serviços / Motores:** `GarmentVisionAnalyzer` aciona `GeminiService`, que faz a requisição via HTTP OkHttp e decodifica a resposta JSON.
4. **Cálculo de Domínio:** O resultado alimenta `PriceEstimateEngine` e `RecommendationEngine`.
5. **Persistência:** O resultado é persistido no banco local via `GarmentAnalysisDao` no Room SQLite.
6. **Emissão de Estado (State):** O ViewModel atualiza um `MutableStateFlow<UiState>`.
7. **Recomposição da View:** A tela em Jetpack Compose coleta o fluxo via `collectAsStateWithLifecycle()` e renderiza o estado final.

---

## ⚡ Concorrência e Tratamento de Recursos

* **Coroutines e Flow:** Toda I/O (disco, rede, decodificação de imagens) roda rigorosamente fora da Main Thread (`Dispatchers.IO`).
* **Segurança de Memória com Bitmaps:** As fotos capturadas na câmera são redimensionadas e comprimidas em JPEG 80% antes da conversão para Base64, evitando qualquer risco de `OutOfMemoryError`.
* **Gerenciamento de Ciclo de Vida:** Uso de `LifecycleOwner` no CameraX e cancelamento automático de coroutines ao descartar ViewModels.
