package io.github.bumasello.financas.domain;

import java.time.LocalDate;
import java.time.YearMonth;

public class CalculadoraFatura {

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
}
