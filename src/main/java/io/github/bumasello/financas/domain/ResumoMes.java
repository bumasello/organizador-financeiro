package io.github.bumasello.financas.domain;

import java.math.BigDecimal;

/**
 * ResumoMes
 */
public record ResumoMes(BigDecimal renda, BigDecimal ajustesRenda, BigDecimal aporte, BigDecimal ajustesAporte,
        BigDecimal parcelas) {

    public ResumoMes {
        if (renda == null) {
            throw new IllegalArgumentException("renda nao pode ser nula");
        }
        if (ajustesRenda == null) {
            throw new IllegalArgumentException("ajustesRenda nao pode ser nulo");
        }
        if (aporte == null) {
            throw new IllegalArgumentException("aporte nao pode ser nulo");
        }
        if (ajustesAporte == null) {
            throw new IllegalArgumentException("ajustesAporte nao pode ser nulo");
        }
        if (parcelas == null) {
            throw new IllegalArgumentException("parcelas nao pode ser nula");
        }
    }

    public BigDecimal disponivel() {
        BigDecimal disponivel = renda.add(ajustesRenda).subtract(aporte).subtract(ajustesAporte).subtract(parcelas);

        return disponivel;
    }
}
