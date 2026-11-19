package io.github.bumasello.financas.conversa;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * RascunhoGasto
 */
public record RascunhoGasto(String descricao, BigDecimal valor, LocalDate data, String categoria, String nomeMeio) {
}
