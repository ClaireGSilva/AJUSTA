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

## 2. Banco em Nuvem: Google Cloud Firestore (Híbrido / Sincronizado)

* **Provedor:** Firebase Firestore
* **Modo de Operação:** Espelhamento sob demanda acionado pelo usuário autenticado.
* **Modelo de Segurança:** Zero-Trust (acesso estritamente isolado por UID do usuário).

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

### Regras de Segurança (`firestore.rules`)
O arquivo oficial `firestore.rules` já está incluído na raiz do projeto pronto para implantação:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Apenas o proprietário autenticado pode ler e gravar seus próprios dados
    match /users/{userId}/{document=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

### Como o Novo Comprador Conecta Seu Próprio Firestore
1. Acesse o [Firebase Console](https://console.firebase.google.com/) no seu projeto.
2. No menu lateral, acesse **Build > Firestore Database** e clique em **Criar banco de dados**.
3. Selecione a localização mais adequada (ex: `southamerica-east1` para Brasil ou `us-east1`) e crie no modo de produção.
4. Na aba **Regras (Rules)**, cole o conteúdo do arquivo `firestore.rules` da raiz deste projeto e clique em **Publicar**.
5. Baixe seu `google-services.json` no console e salve em `/app/google-services.json` (substituindo o arquivo de exemplo `app/google-services.json.template`).
6. Pronto! A sincronização em nuvem estará 100% funcional sem qualquer intervenção ou dependência de contas prévias.

* **Desacoplamento e Resiliência:** Se o Firebase não estiver configurado no projeto (ou o arquivo `google-services.json` estiver ausente), o app continua operando perfeitamente no modo local com Room sem nenhuma falha em tempo de execução.
