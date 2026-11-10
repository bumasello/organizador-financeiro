package io.github.bumasello.financas.domain;

import java.math.BigDecimal;
import java.time.YearMonth;

public record Parcela(int numero, BigDecimal valor, YearMonth mesFatura) {
    public Parcela {
        if (mesFatura == null) {
            throw new IllegalArgumentException("mesFatura não pode ser nulo");
        }

        if (numero < 1) {
            throw new IllegalArgumentException("numero precisa ser maior ou igual a 1");
        }

        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("valor deve ser positivo");
        }

    }
}
