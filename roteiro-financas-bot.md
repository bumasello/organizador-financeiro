# Roteiro — Bot de Finanças no Telegram (Java + Claude)

> Projeto de estudo construído com o modo professor do Claude Code.
> **Para o professor:** guie cada etapa com explicações e perguntas. Não entregue o código pronto, a menos que eu peça. Revise o que eu escrever e aponte conceitos de Java que vale aprofundar.

---

## 1. Objetivo

Um bot no Telegram que recebe mensagens em linguagem natural sobre gastos, as transforma em registros estruturados e responde **quanto ainda posso gastar no mês**.

Exemplos:
- "Comprei pipoca no cartão nubank: 5,50" → registra o gasto
- "Tatuagem 1800 em 10x no nubank" → registra a compra e as 10 parcelas
- `/disponivel` → quanto sobra neste mês, depois do aporte e das faturas

O Claude **interpreta** a mensagem de gasto. O código Java **executa**, **valida** e **guarda**. Todo dado vai para o banco, nada fica só no contexto do chat. O resto (comandos, consultas, contas) é Java puro, sem IA.

Para o estudo, o projeto deve exercitar o que o EBX cobra: **interfaces, herança e polimorfismo, generics e coleções, JDBC**.

---

## 2. Stack

| Item | Escolha |
|---|---|
| Linguagem | Java 21 (records, `var`, text blocks, enums) |
| Build | Maven |
| Telegram | TelegramBots v7+ (long polling). Fixar a versão: a API mudou muito da v6 para a v7 |
| IA | SDK oficial da Anthropic para Java, com modelo Haiku |
| Banco | SQLite via JDBC |
| JSON | Jackson |
| Testes | JUnit 5 desde a Etapa 2 |
| Onde roda | bspnode (Oracle), em container |

Variáveis de ambiente:
- `TELEGRAM_BOT_TOKEN`
- `ANTHROPIC_API_KEY`
- `TELEGRAM_ADMIN_CHAT_ID`: o meu chat ID, o único que aprova usuários novos

> O EBX do treino de Developer (TEB801) roda em Java 8: lá não existem `record`, `var` nem text blocks.

---

## 3. Estrutura

```
organizador-financeiro/
├── pom.xml
└── src/main/java/br/bruno/financas/
    ├── App.java                         // ponto de entrada, sobe o bot
    ├── telegram/FinancasBot.java        // recebe e responde mensagens
    ├── conversa/                        // estado da conversa de cada usuário (padrão State)
    ├── comandos/                        // um comando por classe, despachados pelo nome
    ├── ai/ClaudeService.java            // system prompt, chamada à API, loop de tools
    ├── ai/tools/                        // interface Tool + uma classe por tool
    ├── domain/                          // records e enums do domínio
    ├── domain/CalculadoraDeFatura.java  // regra de fatura e parcelas, sem banco
    └── repo/                            // interfaces de repositório + implementação SQLite
```

Ideias de OO que guiam a estrutura:
- **`Tool` é uma interface** (`nome()`, `schema()`, `executar(...)`). O `ClaudeService` guarda um `Map<String, Tool>` e despacha pelo nome, sem `if`/`switch`.
- **Comandos** seguem a mesma ideia: um `Map<String, Comando>`.
- **Repositórios são interfaces**, com uma implementação SQLite e uma em memória para os testes.
- **Estado da conversa** segue o padrão State: cada estado é uma classe que decide o que fazer com a mensagem que chega.

---

## 4. Fluxo de uma mensagem

```
Telegram ──► FinancasBot ──► usuário aprovado? ── não ──► ignora
                                  │ sim
                                  ▼
                      estado da conversa do usuário
                     │                         │
              começa com "/"            texto livre ou /gasto
                     ▼                         ▼
           Comando (Java puro)          ClaudeService ──► API Claude
                     │                         ▲               │
                     ▼                         │        tool_use: registrar_gasto
                 repositórios                  │               ▼
                     │                         └──── Tool ──► validação ──► repositórios ──► SQLite
                     ▼
FinancasBot ──► Telegram: "Anotado: R$ 5,50 no Nubank (Lazer), fatura de novembro"
```

1. O bot recebe o texto e confere se o usuário está aprovado.
2. Comando (`/disponivel`, `/perfil`…) → Java puro. Texto livre → Claude.
3. O Claude responde com `tool_use` (`registrar_gasto`).
4. O Java **valida**. Se falta algo ou o valor é inválido, devolve um `tool_result` de erro ("falta o cartão"), e o Claude pergunta ao usuário.
5. Com tudo válido, grava a compra e as parcelas e devolve um `tool_result` de sucesso. O Claude gera a confirmação.

**O prompt pede, o Java exige.** A garantia de que nada incompleto entra no banco está na validação, não no prompt.

---

## 5. Modelo de dados

### Decisões que definem o modelo

- **Gasto no cartão conta pelo mês da fatura**, não pela data da compra. "Quanto posso gastar" é pergunta de caixa.
- **A fatura é identificada pelo mês em que vence**, porque o salário daquele mês é que paga.
- **Compra feita no dia do fechamento vai para a fatura seguinte**: a compra entra na fatura cujo fechamento é *estritamente maior* que a data dela.
- **Compra e parcela são separadas.** A compra é o que aconteceu. A parcela é quando o dinheiro sai.
- **Pix, débito e dinheiro** viram uma compra com **1 parcela**, no mês da própria data. Assim o `/disponivel` tem uma regra só.
- **Assinatura, débito automático e carro são recorrências.** Não existe "despesa fixa" como conceito.
- **A renda não é recorrência**, fica no perfil.
- **Ajustes de renda e de aporte** (13º, salário curto por férias) ficam numa tabela só, com o tipo como enum `RENDA`/`APORTE`. Eles **somam** ao valor do perfil, com várias linhas por mês, cada uma com descrição e valor com sinal.

### Tabelas

| tabela | o que guarda |
|---|---|
| `usuario` | chat ID do Telegram, situação (pendente/aprovado), renda, dia do salário, aporte mensal, meta da reserva, estado da conversa |
| `meio_pagamento` | nome, tipo (crédito ou saída imediata), dia de fechamento e dia de vencimento (só para crédito) |
| `recorrencia` | molde: descrição, valor, meio, categoria, dia da cobrança, mês de início, mês de fim (opcional) |
| `compra` | data, descrição, valor total, meio, categoria, número de parcelas, de qual recorrência veio (opcional) |
| `parcela` | compra, número (3 de 10), valor, **mês da fatura** |
| `ajuste` | usuário, mês, tipo (`RENDA`/`APORTE`), valor com sinal, descrição |

> **Exercício aberto:** escrever as colunas, os tipos, as chaves estrangeiras e as restrições de cada tabela.

### Regras

**Em que fatura cai uma compra no crédito**
1. Achar o primeiro fechamento estritamente depois da data da compra.
2. A partir dele, achar o vencimento: se o dia de vencimento é maior que o de fechamento, ele cai no mesmo mês. Se não, cai no mês seguinte.
3. A parcela 1 vai para o mês desse vencimento. A parcela *i* vai para esse mês + (*i* − 1).

Cuidados: cartão que fecha dia 30 ou 31 em fevereiro, e a divisão em `BigDecimal` (R$ 100 em 3x: para onde vai o centavo?).

**Recorrências viram compras sob demanda, não por agendamento**
- Antes de qualquer consulta de um mês, gerar as compras que faltam das recorrências ativas até aquele mês. Se o bot ficar fora do ar, ele se atualiza na primeira consulta.
- Gerar tem que ser **idempotente**: uma restrição de unicidade sobre (recorrência, mês) garante isso no banco.
- Compra gerada é histórico: mudar o valor da recorrência só afeta o futuro.

**Disponível no mês**

```
disponível = renda do perfil + ajustes de RENDA do mês
           − (aporte do perfil + ajustes de APORTE do mês)
           − soma das parcelas com mês da fatura = mês
```

Exemplo de dezembro de 2026: −salário curto e +13º como ajustes de renda, e +13º como ajuste de aporte (o 13º vai inteiro para a reserva).

### Tipos Java que aparecem

- `BigDecimal` para dinheiro, sempre com `RoundingMode` na divisão.
- `LocalDate` para datas, `YearMonth` para o mês da fatura (no SQLite, texto `"2026-12"`).
- Fuso fixo `America/Sao_Paulo` no código: o bspnode fica em Londres.
- Enums gravados como texto (`name()`), nunca como número (`ordinal()`), com `CHECK` no banco.

---

## 6. Comandos

| grupo | comando | o que faz | usa IA? |
|---|---|---|---|
| início | `/start` | cria o usuário como **pendente**; o admin aprova | não |
| configuração | `/perfil` | renda, dia do salário | não |
| | `/objetivo` | meta da reserva e aporte mensal | não |
| | `/cartoes` | meios de pagamento, com fechamento e vencimento | não |
| | `/recorrentes` | listar, cadastrar e cancelar recorrências | não |
| | `/ajuste` | ajuste de renda ou de aporte num mês | não |
| dia a dia | *texto livre* | registra um gasto | **sim** |
| | `/gasto` | o mesmo, pela entrada explícita | **sim** |
| | `/desfazer` | apaga a última compra e as parcelas dela | não |
| consulta | `/disponivel` | quanto ainda dá para gastar no mês | não |
| | `/resumo` | total do mês por categoria | não |
| | `/extrato` | últimas compras | não |
| | `/fatura` | quanto vai vir em cada cartão | não |
| | `/ajuda` | lista os comandos | não |

Os comandos são registrados no @BotFather (`/setcommands`) para aparecer no menu.

Os comandos de configuração são fluxos de várias perguntas. O passo em que cada usuário está fica salvo no banco (o **estado da conversa**), para sobreviver a um restart.

---

## 7. System prompt (ponto de partida)

> **Preencher com a conversa de finanças:** abrir a sessão de gestão financeira no Claude Code e pedir:
> *"Resuma todas as regras que combinamos nesta conversa (categorias, cartões, formatos, comportamentos) em um system prompt para um bot que registra gastos via tools."*
> Depois, substituir as partes marcadas com `[...]` abaixo.

```
Você é um assistente de gestão financeira pessoal do Bruno.
Você recebe mensagens curtas pelo Telegram descrevendo gastos.

Regras:
- Sempre que a mensagem descrever um gasto, chame a tool registrar_gasto.
- Valores usam vírgula como separador decimal (5,50 = 5.50).
- Compras parceladas ("em 10x") informam o número de parcelas; à vista é 1.
- Meios de pagamento cadastrados: {MEIOS_DE_PAGAMENTO}
- Categorias permitidas: [Alimentação, Lazer, Transporte, ...]
- Se faltar alguma informação essencial (valor ou meio de pagamento), pergunte antes de registrar.
- Se a tool devolver erro, explique ao usuário e pergunte o que falta.
- Nunca faça contas de totais: quem soma é o sistema.
- [outras regras da conversa original]

Responda de forma curta, em português, confirmando o que foi feito.
Data de hoje: {DATA_ATUAL}  (fuso America/Sao_Paulo)
```

---

## 8. Tools

### `registrar_gasto`
```json
{
  "name": "registrar_gasto",
  "description": "Registra uma compra do Bruno.",
  "input_schema": {
    "type": "object",
    "properties": {
      "descricao":      { "type": "string" },
      "valor":          { "type": "number", "description": "valor total da compra" },
      "meio_pagamento": { "type": "string", "enum": ["{MEIOS_DE_PAGAMENTO}"] },
      "categoria":      { "type": "string", "enum": ["[categorias]"] },
      "parcelas":       { "type": "integer", "minimum": 1, "description": "1 se à vista" },
      "data":           { "type": "string", "description": "YYYY-MM-DD; hoje se não informado" }
    },
    "required": ["descricao", "valor", "meio_pagamento", "categoria"]
  }
}
```

Cuidados:
- Os `enum` do schema ajudam o modelo, mas **a validação é do Java**: meio desconhecido, categoria desconhecida, valor ≤ 0 ou parcelas < 1 voltam como `tool_result` de erro.
- `"valor": number` é lido pelo Jackson como `double` por padrão. Resolver isso antes de chegar ao `BigDecimal`.
- A confirmação deve dizer em que fatura a compra caiu.

### `consultar_gastos` (extra)
Consultas em linguagem livre ("quanto gastei de iFood em outubro?"). As consultas comuns já são comandos. Se entrar, quem soma é o SQL e a tool devolve o total pronto.

---

## 9. Etapas de construção

### Etapa 1 — Bot eco, só para mim
- **Meta:** o bot devolve exatamente o que recebe, e só para o meu chat ID.
- **Conceitos:** Maven e dependências, criar o bot no @BotFather, long polling, variáveis de ambiente.
- **Antes de começar:** acertar o `pom.xml` (`maven.compiler.release` 21, `groupId` do projeto).
- **Pronto quando:** mando "oi" e recebo "oi"; outra conta é ignorada.

### Etapa 2 — Domínio e regra de fatura (sem banco)
- **Meta:** records e enums do domínio e a `CalculadoraDeFatura`, que recebe data, meio, valor e parcelas e devolve a lista de parcelas.
- **Conceitos:** records, enums com campos e métodos (diferença para o C#), `BigDecimal` e `RoundingMode`, `LocalDate`, `YearMonth`, JUnit 5.
- **Pronto quando:** os testes passam para: compra antes, no dia e depois do fechamento; vencimento no mês seguinte ao fechamento; virada de ano; fechamento dia 30/31 em fevereiro; parcelamento com centavo sobrando; Pix numa parcela só.

### Etapa 3 — Banco
- **Meta:** as seis tabelas e os repositórios, como interfaces com implementação SQLite.
- **Conceitos:** JDBC, `PreparedStatement`, try-with-resources, chaves estrangeiras, `CHECK`, unicidade, `INSERT OR IGNORE`, `SUM` com mês vazio, transação (compra + parcelas gravam juntas ou nada grava).
- **Pronto quando:** testes salvam e leem compras com parcelas, e gerar as recorrências duas vezes dá o mesmo resultado.

### Etapa 4 — Usuários, comandos e estado da conversa (sem IA)
- **Meta:** `/start` com aprovação, `/perfil`, `/objetivo`, `/cartoes`, `/recorrentes`, `/ajuste`.
- **Conceitos:** interface `Comando` + `Map`, padrão State, estado persistido no banco.
- **Pronto quando:** consigo me cadastrar inteiro pelo Telegram, reinicio o bot no meio de um cadastro e ele continua de onde parou.

### Etapa 5 — Consultas
- **Meta:** `/disponivel`, `/resumo`, `/extrato`, `/fatura` e `/desfazer`, com compras lançadas por um comando provisório.
- **Conceitos:** agregação no SQL (`SUM`, `GROUP BY`), Streams para formatar, geração sob demanda das recorrências.
- **Pronto quando:** o `/disponivel` de dezembro de 2026 bate com a conta feita à mão, incluindo o salário curto e o 13º.

### Etapa 6 — Claude sem tools
- **Meta:** `ClaudeService.responder(String)` devolve o texto do Claude e o bot repassa.
- **Conceitos:** cliente HTTP do SDK, builder pattern, system prompt, tratamento de exceções.
- **Opcional:** fazer a primeira chamada com `java.net.http.HttpClient` + Jackson, para ver o JSON cru antes do SDK.
- **Pronto quando:** pergunto algo no Telegram e o Claude responde.

### Etapa 7 — Tool `registrar_gasto` ⭐
- **Meta:** o Claude chama a tool, o Java valida e grava, o Claude confirma. Quando falta informação, o bot pergunta e completa com a resposta.
- **Conceitos:** interface `Tool`, loop de mensagens (`tool_use` → `tool_result`), desserializar JSON com Jackson, validação, `tool_result` de erro.
- **Pronto quando:** "Comprei pipoca no cartão nubank: 5,50" grava uma compra e uma parcela na fatura certa; "pipoca 5,50" faz o bot perguntar o meio de pagamento e gravar depois da resposta.

### Etapa 8 — Robustez e deploy no bspnode
- **Meta:** erros não derrubam o bot, e ele roda 24/7 no bspnode.
- **Conceitos:** logging (SLF4J), falhas e timeouts da API, Docker, limite de heap (`-Xmx`) para não competir com a coleta do mazetick.
- **Pronto quando:** a API do Claude fora do ar não derruba o bot (os comandos continuam funcionando) e ele sobe sozinho depois de um restart da máquina.

### Extras (depois)
- Tool `consultar_gastos` para perguntas livres.
- `/objetivo` mostrando quanto falta para a meta da reserva.
- Testes com Mockito nas tools.

---

## 10. Decisões já tomadas

- Os dados ficam no SQLite, não no histórico de conversa. Todo dado vai para o banco.
- O bot pode perguntar o que faltar. A garantia vem da validação no Java, não do prompt.
- O bot tem usuários, mesmo que só eu use. Usuário novo nasce pendente.
- Só o registro de gasto usa IA. Comandos e consultas são Java puro.
- Haiku como modelo, por custo e velocidade.
- Dinheiro sempre em `BigDecimal`. Quem soma é o SQL, nunca o modelo.
- Mês da fatura, fatura identificada pelo vencimento, compra no dia do fechamento vai para a seguinte.
- Recorrências para assinaturas e débitos automáticos; renda no perfil; ajustes de renda e de aporte numa tabela só.
- Roda no bspnode, não no mazeserver.

## 11. Em aberto

- **Contexto da pergunta pendente:** quando o bot pergunta "qual cartão?", a resposta chega numa mensagem nova. Como guardar as mensagens daquele registro até ele ser gravado ou expirar? Decidir na Etapa 7.
- Regra do centavo na divisão das parcelas.
- O perfil fica em colunas de `usuario` ou numa tabela própria?
