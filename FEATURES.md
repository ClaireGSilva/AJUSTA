# FEATURES — Ajusta

> **Inventário Completo de Funcionalidades do Aplicativo**  
> *Versão:* 1.0.0 | *Build:* Produção / Android 24+

---

## 📱 Mapa Geral de Funcionalidades

```
Ajusta
 ├── 1. Diagnóstico Visual Inteligente (CameraX + Gemini AI)
 ├── 2. Motor de Precificação e Complexidade (BRL R$)
 ├── 3. Matriz de Decisão "Vale a Pena Reformar?"
 ├── 4. Ficha Técnica / Ordem de Serviço Digital (WhatsApp)
 ├── 5. Chat com Consultora Mestre em Alfaiataria (Multi-Modelo)
 ├── 6. Módulo Ateliê Pro (Gestão de Ordens de Serviço)
 ├── 7. Guia de Profissionais e Ateliês
 ├── 8. Central de Tutoriais DIY e Faça Você Mesmo
 ├── 9. Base de Conhecimento e Guia de Cuidados com Tecidos
 ├── 10. Histórico de Análises & Acompanhamento de Peças
 ├── 11. Sistema de Lembretes & Notificações Locais (AlarmManager)
 └── 12. Gestão de Temas (Claro / Escuro / Sistema) e Perfil Híbrido
```

---

## 1. Diagnóstico Visual Inteligente
* **Captura Multimodal (CameraX):** Permite captura ou upload de múltiplas fotos da peça (visão frontal, visão traseira, detalhe do defeito e close na textura do tecido).
* **Análise Multiespectral por IA:** Identifica o tipo de peça (calça social, jeans, blazer, vestido de festa, camisa, jaqueta, casaco), características visíveis e categoria de defeito.
* **Detecção de Riscos Estruturais:** Avalia se o ajuste oferece risco à peça (desalinhamento de costura original, marcas de ferro permanente, tecido desfiante, falta de margem interna de tecido).
* **Veredito Técnico Preliminar:** Explicação didática de causa provável e possíveis soluções de costura.

## 2. Motor Algorítmico de Precificação Calibrado
* **Base de Custos do Mercado Brasileiro:** Faixa de preço calculada em Reais (R$) com valores mínimo e máximo.
* **Fatores Multiplicadores:**
  * Categoria da alteração (bainha simples vs galoneira vs zíper embutido vs pence vs forro completo).
  * Fator de Tecido (seda, couro, alfaiataria fina, jeans com pesponto grosso, malha elástica).
  * Fator de Acabamento (costura francesa, debrum, overloque industrial, pesponto duplo).
* **Discriminação de Custos:** Exibição clara dos componentes que encarecem ou barateiam o conserto.

## 3. Matriz "Vale a Pena Reformar?"
* **Entrada de Valor Original da Peça:** O usuário pode informar quanto pagou na roupa (ou se é de brechó/vintage).
* **Cálculo de Proporcionalidade:** Compara o custo do conserto em relação ao valor da peça.
* **Score de Recomendação:**
  * *Altamente Recomendado:* Custo do reparo < 30% do valor da peça ou peça de alta durabilidade.
  * *Equilibrado / Razoável:* Custo entre 30% e 60%.
  * *Avaliar com Cautela:* Custo > 60% com recomendações alternativas (ex: customização criativa ou doação).
* **Pontos a Favor e Pontos de Confirmação:** Listagem objetiva de prós e contras para a tomada de decisão.

## 4. Ficha de Atendimento & Compartilhamento WhatsApp
* **Exportação Formatada:** Resumo executivo com número identificador, fotos anexadas, diagnóstico preliminar, pontos de atenção para a costureira e faixa de preço esperada.
* **Envio para WhatsApp em 1 Clique:** Formatação automática com quebras de linha limpas e emojis profissionais para envio direto ao contato da costureira.
* **Campo de Observações Adicionais:** Permite registrar anotações do cliente antes da visita física.

## 5. Consultora Mestre em Alfaiataria (Chat AI)
* **Ateliê Virtual Conversacional:** Chat interativo com sistema de persona especializada em alfaiataria clássica, tipos de tecido, caimento e truques de costura.
* **Seletor de 3 Motores Gemini:**
  * ⚡ *Flash Lite (gemini-3.1-flash-lite-preview):* Respostas ultrarrápidas para dúvidas cotidianas.
  * ⚖️ *Flash 3.5 (gemini-3.5-flash):* Modelo equilibrado padrão com excelente raciocínio.
  * 🎓 *Pro 3.1 (gemini-3.1-pro-preview):* Raciocínio profundo e aconselhamento técnico para alfaiataria pesada.
* **Respostas com Sugestões de Ação e Citações de Fontes:** Interface com balões elegantes e marcação de tempo.

## 6. Módulo Ateliê Pro (Gestão para Costureiras e Alfaiates)
* **Cadastro de Ordens de Serviço (OS):** Registro de nome do cliente, telefone, tipo de peça, pedido detalhado, materiais necessários e preço acertado.
* **Quadro de Status Operacional:** Controle dos estágios da peça:
  * `PENDENTE` (Aguardando avaliação física)
  * `EM_ANDAMENTO` (Em corte/costura)
  * `PRONTO_PROVA` (Pronto para prova do cliente)
  * `CONCLUIDO` (Finalizado)
  * `ENTREGUE` (Faturado e retirado)
* **Barra de Progresso Visual:** Indicador percentual interativo do andamento da peça.

## 7. Diretório de Profissionais e Ateliês
* **Catálogo Especializado:** Listagem de ateliês, alfaiates e costureiras cadastrados por especialidade (Bainhas Express, Alfaiataria Fina, Vestidos de Festa, Couro & Jeans).
* **Filtros e Contato Rápido:** Visualização de endereço, especialidades e botão direto para contato telefônico ou mensagem.

## 8. Central DIY (Faça Você Mesmo)
* **Tutoriais Ilustrados Passo a Passo:** Guias para ajustes populares (ex: pregar botão de alfaiataria, barra italiana, conserto invisível de rasgos, ajuste de cós com elástico).
* **Indicadores de Nível:** Fácil, Médio e Avançado com estimativa de tempo de execução.
* **Lista de Materiais Necessários:** Checklist para o usuário separar antes de iniciar o reparo.

## 9. Base de Conhecimento Têxtil
* **Guia de Tecidos:** Fibras naturais (algodão, linho, lã, seda), sintéticas (poliéster, poliamida) e artificiais (viscose, modal).
* **Glossário da Costura:** Termos explicados de forma simples (pences, viés, pesponto, entretela, overloque).
* **Instruções de Lavagem e Passadoria:** Simbologia internacional e cuidados de conservação.

## 10. Histórico e Acompanhamento de Peças
* **Persistência Completa Local (Room):** Todas as análises realizadas ficam salvas mesmo sem conexão com a internet.
* **Busca e Filtro por Categoria:** Encontre rapidamente peças salvas por data ou tipo de vestimenta.
* **Revisão de Diagnóstico:** Reabra diagnósticos antigos a qualquer momento com todos os parâmetros calculados.

## 11. Sistema de Lembretes & Notificações Locais
* **Canais Dedicados Android:**
  * `ajusta_repairs_channel` (Progresso e prazos de ateliê).
  * `ajusta_tips_channel` (Dicas de costura e lembretes de cuidados).
* **Agendador via AlarmManager:** Funciona offline e sem depender de servidores push, garantindo total privacidade e funcionamento garantido.

## 12. Gestão de Temas e Acessibilidade
* **Dual Theme Dinâmico:** Tema claro refinado e tema escuro contrastante e descansado para os olhos.
* **Alternador Rápido:** Botão `ThemeToggleButton` e seletor com chips em todas as telas centrais.
