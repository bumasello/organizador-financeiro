package io.github.bumasello.financas.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Compra
 */
public record Compra(LocalDate dataCompra, String descricao, String categoria, BigDecimal valorTotal,
        int quantidadeParcelas) {

    public Compra {
        if (dataCompra == null) {
            throw new IllegalArgumentException("dataCompra não pode ser nula");
        }

        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("descricao precisa estar preenchida. descricao: "
                    + descricao);
        }

        if (categoria == null || categoria.isBlank()) {
            throw new IllegalArgumentException("categoria precisa estar preenchida. categoria: " + categoria);
        }

        if (valorTotal == null || valorTotal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "valorTotal precisa estar preenchido e precisa ser positivo. valorTotal: " + valorTotal);
        }

        if (quantidadeParcelas < 1) {
            throw new IllegalArgumentException("quantidadeParcelas deve ser 1 ou maior");
        }
    }
}
