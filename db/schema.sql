-- Schema do bot de finanças (PostgreSQL / Supabase).
-- Rodar uma vez, inteiro, no SQL Editor do Supabase.
--
-- Tudo fica no schema "financas", fora do "public". O Supabase publica o
-- "public" pela API REST; aqui o acesso é só pelo JDBC do bot.
--
-- Convenções:
--   * dinheiro em NUMERIC(12,2): volta para o Java como BigDecimal com escala 2
--   * "mês" (YearMonth no Java) em DATE, sempre no dia 1, garantido por CHECK
--   * enums do Java em TEXT com CHECK, gravados pelo name()

-- Para recomeçar do zero (APAGA TUDO):
-- DROP SCHEMA IF EXISTS financas CASCADE;

BEGIN;

CREATE SCHEMA financas;
SET search_path TO financas;

-- ---------------------------------------------------------------------------
-- usuario: quem conversa com o bot, o perfil e o estado da conversa
-- ---------------------------------------------------------------------------
CREATE TABLE usuario (
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    telegram_chat_id   BIGINT        NOT NULL UNIQUE,
    nome               TEXT,
    situacao           TEXT          NOT NULL DEFAULT 'PENDENTE'
                       CHECK (situacao IN ('PENDENTE', 'APROVADO', 'BLOQUEADO')),
    renda_mensal       NUMERIC(12,2) CHECK (renda_mensal >= 0),
    dia_salario        SMALLINT      CHECK (dia_salario BETWEEN 1 AND 31),
    aporte_mensal      NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (aporte_mensal >= 0),
    meta_reserva       NUMERIC(12,2) CHECK (meta_reserva >= 0),
    estado_conversa    TEXT          NOT NULL DEFAULT 'OCIOSO',
    contexto_conversa  JSONB,        -- dados do fluxo em andamento (ex.: gasto incompleto)
    criado_em          TIMESTAMPTZ   NOT NULL DEFAULT now()
);

-- ---------------------------------------------------------------------------
-- meio_pagamento: espelha o record MeioPagamento
-- ---------------------------------------------------------------------------
CREATE TABLE meio_pagamento (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    usuario_id      BIGINT   NOT NULL REFERENCES usuario (id),
    nome            TEXT     NOT NULL CHECK (btrim(nome) <> ''),
    tipo            TEXT     NOT NULL CHECK (tipo IN ('CREDITO', 'IMEDIATO')),
    dia_fechamento  SMALLINT NOT NULL,
    dia_vencimento  SMALLINT NOT NULL,
    ativo           BOOLEAN  NOT NULL DEFAULT true,
    UNIQUE (usuario_id, nome),
    -- mesma regra do construtor compacto do record
    CHECK (
        (tipo = 'CREDITO'  AND dia_fechamento BETWEEN 1 AND 31 AND dia_vencimento BETWEEN 1 AND 31)
     OR (tipo = 'IMEDIATO' AND dia_fechamento = 0 AND dia_vencimento = 0)
    )
);

-- ---------------------------------------------------------------------------
-- recorrencia: molde que gera uma compra por mês (assinatura, débito automático)
-- ---------------------------------------------------------------------------
CREATE TABLE recorrencia (
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    usuario_id         BIGINT        NOT NULL REFERENCES usuario (id),
    meio_pagamento_id  BIGINT        NOT NULL REFERENCES meio_pagamento (id),
    descricao          TEXT          NOT NULL CHECK (btrim(descricao) <> ''),
    categoria          TEXT          NOT NULL,
    valor              NUMERIC(12,2) NOT NULL CHECK (valor > 0),
    dia_cobranca       SMALLINT      NOT NULL CHECK (dia_cobranca BETWEEN 1 AND 31),
    mes_inicio         DATE          NOT NULL CHECK (EXTRACT(DAY FROM mes_inicio) = 1),
    mes_fim            DATE          CHECK (EXTRACT(DAY FROM mes_fim) = 1),
    CHECK (mes_fim IS NULL OR mes_fim >= mes_inicio)
);

-- ---------------------------------------------------------------------------
-- compra: o que aconteceu. As parcelas dizem quando o dinheiro sai.
-- ---------------------------------------------------------------------------
CREATE TABLE compra (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    usuario_id           BIGINT        NOT NULL REFERENCES usuario (id),
    meio_pagamento_id    BIGINT        NOT NULL REFERENCES meio_pagamento (id),
    data_compra          DATE          NOT NULL,
    descricao            TEXT          NOT NULL CHECK (btrim(descricao) <> ''),
    categoria            TEXT          NOT NULL,
    valor_total          NUMERIC(12,2) NOT NULL CHECK (valor_total > 0),
    quantidade_parcelas  SMALLINT      NOT NULL CHECK (quantidade_parcelas >= 1),
    -- preenchidos só nas compras geradas por recorrência
    recorrencia_id       BIGINT        REFERENCES recorrencia (id),
    mes_recorrencia      DATE          CHECK (EXTRACT(DAY FROM mes_recorrencia) = 1),
    criado_em            TIMESTAMPTZ   NOT NULL DEFAULT now(),
    CHECK ((recorrencia_id IS NULL) = (mes_recorrencia IS NULL)),
    -- garante a geração idempotente: uma compra por recorrência por mês
    UNIQUE (recorrencia_id, mes_recorrencia)
);

CREATE INDEX compra_usuario_data_idx ON compra (usuario_id, data_compra);

-- ---------------------------------------------------------------------------
-- parcela: espelha o record Parcela. Apagar a compra apaga as parcelas.
-- ---------------------------------------------------------------------------
CREATE TABLE parcela (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    compra_id   BIGINT        NOT NULL REFERENCES compra (id) ON DELETE CASCADE,
    numero      SMALLINT      NOT NULL CHECK (numero >= 1),
    valor       NUMERIC(12,2) NOT NULL CHECK (valor > 0),
    mes_fatura  DATE          NOT NULL CHECK (EXTRACT(DAY FROM mes_fatura) = 1),
    UNIQUE (compra_id, numero)
);

CREATE INDEX parcela_mes_fatura_idx ON parcela (mes_fatura);

-- ---------------------------------------------------------------------------
-- ajuste: renda ou aporte fora do padrão num mês. Soma ao valor do perfil.
-- ---------------------------------------------------------------------------
CREATE TABLE ajuste (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    usuario_id  BIGINT        NOT NULL REFERENCES usuario (id),
    mes         DATE          NOT NULL CHECK (EXTRACT(DAY FROM mes) = 1),
    tipo        TEXT          NOT NULL CHECK (tipo IN ('RENDA', 'APORTE')),
    valor       NUMERIC(12,2) NOT NULL CHECK (valor <> 0),
    descricao   TEXT          NOT NULL CHECK (btrim(descricao) <> '')
);

CREATE INDEX ajuste_usuario_mes_idx ON ajuste (usuario_id, mes);

COMMIT;
