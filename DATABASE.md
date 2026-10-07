# DATABASE — Ajusta

> **Documentação de Persistência de Dados (Local e Nuvem)**  
> *Versão:* 1.0.0 | *Tecnologias:* Room Database 2.7.0 (SQLite) & Cloud Firestore

---

## 🗄️ Estratégia de Persistência Híbrida

O **Ajusta** adota uma estratégia **Offline-First**:
* O banco de dados primário é o **Room Database local** (`ajusta_database`). O aplicativo funciona com 100% de suas funcionalidades sem internet e sem exigir cadastro.
* Caso o usuário decida fazer login com sua conta Google, o serviço `FirestoreSyncService` espelha as análises e ordens de serviço no **Google Cloud Firestore**.

---

## 1. Banco de Dados Local: Room SQLite

* **Nome do Banco:** `ajusta_database`
* **Versão do Schema:** `2`
* **Localização no Código:** `com.example.data.local.AppDatabase`
* **Mapeador ORM:** AndroidX Room com KSP (`com.google.devtools.ksp`)

### 📋 Tabelas e Entidades

#### Tabela 1: `saved_analyses` (Entidade: `GarmentAnalysisEntity`)
Armazena todos os diagnósticos de vestuário realizados pelo usuário.

| Campo | Tipo SQLite | Descrição |
|---|---|---|
| `id` | `TEXT` (PK) | Identificador único UUID da análise |
| `garmentType` | `TEXT` | Tipo da peça (ex: "Calça Social", "Blazer", "Jeans") |
| `visibleCharacteristics` | `TEXT` | Características observadas pela visão computacional |
| `problemSummary` | `TEXT` | Resumo executivo do defeito ou necessidade de ajuste |
| `problemCategory` | `TEXT` | Categoria (ex: "Bainha", "Cintura", "Zíper", "Manga") |
| `confidence` | `TEXT` | Nível de confiança da IA (ALTA, MEDIA, BAIXA) |
| `whatWeObserved` | `TEXT` | Detalhamento do que foi visualmente identificado |
| `probableCause` | `TEXT` | Causa mecânica ou estrutural provável do defeito |
| `possibleSolution` | `TEXT` | Procedimento de costura ou alfaiataria recomendado |
| `whatNeedsConfirmation` | `TEXT` | O que depende de avaliação presencial no corpo |
| `beforeYouAlterLimitations` | `TEXT` | Limitações prévias (ex: tecido cortado sem sobra) |
| `alterationOptionsJson` | `TEXT` | Array JSON de opções de ajuste alternativas |
| `riskLevel` | `TEXT` | Nível de risco do conserto (BAIXO, MEDIO, ALTO) |
| `riskSummary` | `TEXT` | Resumo explicativo do risco |
| `specificRisksJson` | `TEXT` | Array JSON com riscos pontuais |
| `requiresInPersonEvaluation` | `INTEGER` (Boolean) | Se é estritamente obrigatória a prova presencial |
| `priceMin` | `INTEGER` | Valor mínimo estimado em Reais (R$) |
| `priceMax` | `INTEGER` | Valor máximo estimado em Reais (R$) |
| `priceBase` | `REAL` | Preço base de partida da categoria |
| `priceGarmentFactor` | `REAL` | Multiplicador da complexidade da peça |
| `priceComplexityFactor` | `REAL` | Multiplicador da dificuldade do conserto |
| `priceFabricFactor` | `REAL` | Multiplicador do tipo de tecido |
| `priceFinishFactor` | `REAL` | Multiplicador do acabamento exigido |
| `priceDisclaimer` | `TEXT` | Nota legal sobre a natureza estimativa do preço |
| `recommendationSummary` | `TEXT` | Parecer da matriz "Vale a Pena Reformar?" |
| `pointsInFavorJson` | `TEXT` | Lista JSON de motivos a favor da reforma |
| `pointsToConfirmJson` | `TEXT` | Lista JSON de alertas antes de autorizar o serviço |
| `photoUrisSerialized` | `TEXT` | URIs locais das fotos anexadas à análise |
| `fabric` | `TEXT` (Nullable) | Nome do tecido identificado |
| `userPaidPrice` | `REAL` (Nullable) | Valor pago pelo cliente na compra da peça (R$) |
| `timestamp` | `INTEGER` | Data e hora de criação da análise (Unix Epoch ms) |
| `isTicketGenerated` | `INTEGER` | Indicador se a Ficha de Atendimento foi gerada |
| `ticketNotes` | `TEXT` | Anotações adicionais do cliente para a costureira |

---

#### Tabela 2: `diagnostic_results` (Entidade: `DiagnosticResultEntity`)
Armazena diagnósticos técnicos complementares vinculados a uma análise.

| Campo | Tipo SQLite | Descrição |
|---|---|---|
| `id` | `TEXT` (PK) | UUID do resultado diagnóstico |
| `analysisId` | `TEXT` (FK Index) | Referência cruzada para `saved_analyses.id` |
| `status` | `TEXT` | Status da avaliação técnica |
| `technicalVerdict` | `TEXT` | Parecer de corte e modelagem |
| `executionTimeMs` | `INTEGER` | Tempo de inferência do motor de diagnóstico |
| `alterationPlanJson` | `TEXT` | JSON com o passo a passo técnico de execução |
| `timestamp` | `INTEGER` | Carimbo de data/hora |

---

#### Tabela 3: `professional_orders` (Entidade: `ProfessionalOrderEntity`)
Armazena as ordens de serviço cadastradas na aba **Ateliê Pro**.

| Campo | Tipo SQLite | Descrição |
|---|---|---|
| `id` | `TEXT` (PK) | Identificador UUID da Ordem de Serviço |
| `clientName` | `TEXT` | Nome do cliente |
| `clientPhone` | `TEXT` | Telefone / WhatsApp do cliente |
| `garmentType` | `TEXT` | Peça deixada no ateliê |
| `clientRequest` | `TEXT` | Pedido específico do cliente |
| `preliminaryDiagnosis` | `TEXT` | Diagnóstico técnico do ateliê |
| `procedureStepsJson` | `TEXT` | Etapas do processo de costura (JSON) |
| `materialsNeeded` | `TEXT` | Linhas, zíperes, entretelas ou aviamentos necessários |
| `complexity` | `TEXT` | Baixa, Média ou Alta complexidade |
| `attentionPointsJson` | `TEXT` | Pontos de atenção na prova (JSON) |
| `agreedPrice` | `REAL` (Nullable) | Valor final acertado com o cliente (R$) |
| `measurementsJson` | `TEXT` | Medidas anatômicas coletadas (cintura, quadril, etc.) |
| `status` | `TEXT` | `PENDENTE`, `EM_ANDAMENTO`, `PRONTO_PROVA`, `CONCLUIDO`, `ENTREGUE` |
| `photoUrisSerialized` | `TEXT` | Fotos anexadas da peça |
| `createdAt` | `INTEGER` | Timestamp de entrada da peça no ateliê |

---

## 2. Banco em Nuvem: Google Cloud Firestore (Opcional)

* **Provedor:** Firebase Firestore
* **Modo de Operação:** Espelhamento sob demanda acionado pelo usuário autenticado.

### Estrutura de Documentos no Firestore:

```
users/
 └── {userId}/
      ├── saved_analyses/
      │    └── {analysisId}   -> Objeto serializado compatível com GarmentAnalysis
      │
      └── professional_orders/
           └── {orderId}      -> Objeto serializado compatível com ProfessionalOrder
```

* **Regras de Segurança Básicas:** Leituras e gravações vinculadas estritamente ao `request.auth.uid == userId`.
* **Desacoplamento:** Se o Firebase não estiver configurado no projeto (ou o arquivo `google-services.json` estiver ausente), o app continua operando no modo local com Room sem falhas em tempo de execução.
