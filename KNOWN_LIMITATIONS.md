# KNOWN LIMITATIONS — Ajusta

> **Mapeamento Transparente de Limites Conhecidos e Funcionalidades Futuras (TODO)**  
> *Versão:* 1.0.0 | *Princípio:* Transparência e Integridade na Transferência

---

## ⚠️ 1. Limitações Técnicas do Sistema Atual

Em conformidade com o princípio de transferibilidade sem falsas promessas, listamos abaixo as fronteiras do sistema atual:

### 1.1 Análise Visual de Vestuário
* **Dependência da Qualidade da Imagem:** A precisão do diagnóstico de tramas e costuras depende da iluminação do ambiente e do foco da câmera. Imagens com pouca luz ou resolução muito baixa podem retornar classificações genéricas de tecido.
* **Medição Não Métrica:** A IA não realiza medição volumétrica tridimensional com régua ou laser. Ela aponta defeitos de caimento (ex: "cós folgado", "barra arrastando no chão"), mas a quantidade exata de centímetros a ser retirada requer a marcação física com alfinetes na prova presencial.

### 1.2 Catálogo de Ateliês e Profissionais
* **Base Estática Curada:** O diretório atual baseia-se em profissionais e ateliês pré-catalogados por região e especialidade. Não há, no momento, um sistema de busca em tempo real com raio GPS ou integração viva com a Google Maps SDK.

### 1.3 Notificações
* **Lembretes Locais via Sistema Operacional:** O sistema de notificações utiliza o `AlarmManager` do próprio aparelho. Não há servidor de envio remoto (como Firebase Cloud Messaging - FCM) enviando mensagens em massa de marketing.

---

## 📌 2. Itens Marcados como TODO / NOT YET DEFINED

Os recursos abaixo representam oportunidades de expansão de produto identificadas na modelagem, mas propositalmente não implementadas para manter o projeto lean, sem custos de infraestrutura e livre de dependências externas desnecessárias:

| Funcionalidade | Status | Descrição para Evolução Futura |
|---|---|---|
| **Google Maps Live Tracking** | `TODO / NOT YET DEFINED` | Integração do mapa visual interativo para traçar rotas automáticas até o ateliê mais próximo. |
| **Gateway de Pagamento no App (PIX / Cartão)** | `TODO / NOT YET DEFINED` | Processamento de pagamentos de ordens de serviço diretamente no aplicativo através de Stripe, Mercado Pago ou Asaas. |
| **Painel Web SaaS para Ateliês** | `TODO / NOT YET DEFINED` | Dashboard em navegador web sincronizado com o Firestore para alfaiates gerenciarem seus pedidos em desktop. |
| **Notificações Push Remotas (FCM)** | `TODO / NOT YET DEFINED` | Envio de mensagens promocionais e avisos a partir de console central na nuvem. |
| **Exportação de PDF da Ordem de Serviço** | `TODO / NOT YET DEFINED` | Geração direta de arquivo .PDF imprimível além do texto formatado atual para WhatsApp. |

---

## 💡 3. Resiliência Operacional

Nenhuma das limitações acima impede ou compromete a utilização do aplicativo pelo usuário final ou profissional de costura:
* A experiência principal (fotografar $\rightarrow$ diagnosticar $\rightarrow$ estimar preço $\rightarrow$ avaliar se vale a pena $\rightarrow$ gerar ficha) funciona de ponta a ponta com estabilidade e robustez comprovada.
