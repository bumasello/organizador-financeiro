package io.github.bumasello.financas.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class CalculadoraFatura {
    private static final BigDecimal UM_CENTAVO = new BigDecimal("0.01");

    public static YearMonth mesDaFatura(MeioPagamento meio, LocalDate dataCompra) {
        YearMonth mesCompra = YearMonth.from(dataCompra);
        YearMonth mesVencimento, mesFechamento;
        int diasNoMes = mesCompra.lengthOfMonth();
        int fechamentoEfetivo = Math.min(meio.diaFechamento(), diasNoMes);

        if (meio.tipo() == TipoPagamento.IMEDIATO) {
            return mesCompra;
        }

        if (dataCompra.getDayOfMonth() < fechamentoEfetivo) {
            mesFechamento = mesCompra;
        } else {
            mesFechamento = mesCompra.plusMonths(1);
        }

        if (meio.diaVencimento() < meio.diaFechamento()) {
            mesVencimento = mesFechamento.plusMonths(1);

        } else {
            mesVencimento = mesFechamento;
        }

        return mesVencimento;
    }

    public static List<BigDecimal> dividirEmParcelas(BigDecimal total, int quantidade) {
        if (quantidade < 1) {
            throw new IllegalArgumentException("Quantidade deve ser maior ou igual a 1");
        }

        if (total.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Total deve ser maior que zero");
        }

        BigDecimal totalMinimo = CalculadoraFatura.UM_CENTAVO.multiply(BigDecimal.valueOf(quantidade));

        if (total.compareTo(totalMinimo) < 0) {
            throw new IllegalArgumentException("Deve ter pelo menos 1 centavo por parcela");
        }

        List<BigDecimal> parcelas = new ArrayList<>();
        BigDecimal qnt = BigDecimal.valueOf(quantidade);
        BigDecimal base = total.divide(qnt, 2, RoundingMode.DOWN);
        BigDecimal resto = total.subtract(base.multiply(qnt));
        int centavosRestantes = resto.movePointRight(2).intValueExact();

        for (int i = 0; i < quantidade; i++) {
            if (i < centavosRestantes) {
                parcelas.add(base.add(UM_CENTAVO));
            } else {
                parcelas.add(base);
            }
        }

        return parcelas;
    }
}
