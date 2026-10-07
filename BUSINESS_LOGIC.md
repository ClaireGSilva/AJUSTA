# BUSINESS LOGIC — Ajusta

> **Documentação de Algoritmos, Motores de Negócio e Regras de Decisão**  
> *Versão:* 1.0.0 | *Calibração:* Mercado Brasileiro de Serviços Têxteis (BRL R$)

---

## 💼 1. O Motor de Estimativa de Preços (`PriceEstimateEngine`)

O cálculo de estimativa de valores não é uma resposta estática da IA; ele é processado por um algoritmo determinístico no pacote `com.example.pricing.PriceEstimateEngine`, garantindo consistência e coerência financeira.

### 📐 Fórmula Geral:
```
Preço Estimado = Preço Base × Fator de Peça × Fator de Complexidade × Fator de Tecido × Fator de Acabamento
```

### 1.1 Tabela de Preço Base por Categoria de Ajuste:
* **Bainha Simples (Calça/Saia):** R$ 25,00
* **Bainha Original (Jeans com reaproveitamento do acabamento de fábrica):** R$ 38,00
* **Bainha Italiana com dobra:** R$ 35,00
* **Ajuste de Cintura / Cós Simples:** R$ 35,00
* **Ajuste de Cintura com Afunilamento de Pernas:** R$ 55,00
* **Troca de Zíper Comum:** R$ 28,00
* **Troca de Zíper Invisível / Vestido de Festa:** R$ 45,00
* **Troca de Zíper Destacável de Jaqueta / Casaco:** R$ 50,00
* **Ajuste de Manga de Camisa (com carcela e punho):** R$ 40,00
* **Ajuste de Manga de Blazer / Paletó (com forro e botões):** R$ 65,00
* **Ombro / Cava de Blazer:** R$ 80,00
* **Troca de Forro Completo:** R$ 120,00
* **Remendo Invisível / Cerzimento:** R$ 30,00

### 1.2 Fatores Multiplicadores:

#### Fator de Tipo de Peça:
* Peça Básica (Camiseta, Saia Simples): `1.0x`
* Calça Jeans / Calça de Sarja: `1.1x`
* Calça Social de Alfaiataria: `1.25x`
* Camisa Social Masculina / Feminina: `1.2x`
* Blazer / Paletó Estruturado: `1.5x`
* Vestido de Festa / Tecido Fino: `1.65x`
* Casaco Pesado / Sobretudo / Couro: `1.8x`

#### Fator de Tecido:
* Algodão / Sarja / Tricoline: `1.0x`
* Malha / Viscolycra (requer galoneira ou agulha ponta-bola): `1.15x`
* Linho Puro (desfia com facilidade): `1.2x`
* Seda Pura / Cetim / Chiffon (escorrega e marca furo): `1.45x`
* Couro Natural / Sintético / Camurça: `1.6x`

#### Fator de Acabamento:
* Costura Reta Simples: `1.0x`
* Pesponto Duplo / Travete: `1.15x`
* Costura Francesa Embutida: `1.3x`
* Debrum / Acabamento em Viés de Cetim: `1.35x`

### 1.3 Geração da Faixa de Preço:
O algoritmo calcula o valor médio e gera uma faixa com margem de segurança de ±15% a ±20%, arredondada para valores comerciais inteiros (ex: R$ 35 a R$ 45).

---

## ⚖️ 2. A Matriz de Decisão: "Vale a Pena Reformar?"

Implementada em `com.example.ai.RecommendationEngine`, esta regra de negócio ajuda o consumidor a não gastar dinheiro de forma irracional.

### 2.1 Critérios de Avaliação:
1. **Razão Custo/Benefício Financeiro ($R$):**
   * Se o conserto custar **menos de 30%** do valor pago pela peça $\rightarrow$ **Altamente Recomendado (Score Verde)**.
   * Se o conserto custar **entre 30% e 60%** $\rightarrow$ **Equilibrado / Razoável (Score Amarelo)**.
   * Se o conserto custar **mais de 60%** $\rightarrow$ **Avaliar com Cautela (Score Laranja/Vermelho)**.

2. **Fatores Qualitativos de Compensação:**
   * **Peça Vintage / Brechó:** Se a peça for de brechó ou vintage com tecido nobre (ex: blazer 100% lã pura garimpado por R$ 50), o algoritmo eleva o score mesmo que o conserto custe mais que a peça, pois uma peça equivalente nova custaria mais de R$ 600.
   * **Durabilidade do Tecido:** Fibras nobres têm ciclo de vida de vários anos após o conserto.
   * **Valor Afetivo:** O usuário pode sinalizar apego emocional para priorizar a reforma.

---

## 📋 3. Ciclo de Vida da Ordem de Serviço (Módulo Ateliê Pro)

Para as costureiras e oficinas, a máquina de estados obedece ao seguinte fluxo:

```
[ PENDENTE ] ──(Aprovado pelo cliente)──> [ EM_ANDAMENTO ]
                                                 │
                                           (Primeira montagem)
                                                 │
                                                 ▼
[ CONCLUIDO ] <──(Ajuste final)── [ PRONTO_PROVA ]
      │
 (Retirada)
      │
      ▼
 [ ENTREGUE ]
```

Cada transição de estado registra carimbos de data/hora e permite emissão de comprovantes atualizados.
