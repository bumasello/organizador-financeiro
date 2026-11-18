package io.github.bumasello.financas.conversa;

import io.github.bumasello.financas.domain.TipoPagamento;

/**
 * RascunhoCartao
 */
public record RascunhoCartao(String nome, TipoPagamento tipo, Integer diaFechamento, Integer diaVencimento) {
}
