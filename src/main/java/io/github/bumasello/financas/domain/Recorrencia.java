package io.github.bumasello.financas.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * Recorrencia
 */
public record Recorrencia(String descricao, BigDecimal valor, String categoria, int diaCobranca, YearMonth mesInicio,
        YearMonth mesFim) {

    public Recorrencia {
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("descricao nao pode ser nula e precisa ser preenchida corretamente");
        }

        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("valor nao pode ser nulo e precisa ser positivo");
        }

        if (categoria == null || categoria.isBlank()) {
            throw new IllegalArgumentException("categoria nao pode ser nula e precisa ser preenchida corretamente");
        }

        if (diaCobranca < 1 || diaCobranca > 31) {
            throw new IllegalArgumentException("diaCobranca precisa estar entre 1 e 31");
        }

        if (mesInicio == null) {
            throw new IllegalArgumentException("mesInicio não pode ser nulo");
        }

        if (mesFim != null && (mesFim.isBefore(mesInicio))) {
            throw new IllegalArgumentException("mesFim não pode ser antes do mesInicio");
        }

    }

    public boolean ativaEm(YearMonth mes) {
        if (mes == null) {
            throw new IllegalArgumentException("mes não pode ser nulo");
        }

        YearMonth inicio = mesInicio();
        if (inicio != null && mes.isBefore(inicio)) {
            return false;
        }

        YearMonth fim = mesFim();
        if (fim != null && mes.isAfter(fim)) {
            return false;
        }

        return true;
    }

    public LocalDate dataCobranca(YearMonth mes) {
        if (mes == null) {
            throw new IllegalArgumentException("mes não pode ser nulo");
        }

        int day = Math.min(diaCobranca, mes.lengthOfMonth());
        return mes.atDay(day);
    }

    public List<YearMonth> mesesAte(YearMonth limite) {
        if (limite == null) {
            throw new IllegalArgumentException("limite não pode ser nulo");
        }

        YearMonth meses = mesInicio();
        List<YearMonth> listaMeses = new ArrayList<>();

        YearMonth teto = limite;
        YearMonth fim = mesFim();
        if (fim != null && fim.isBefore(teto)) {
            teto = fim;
        }

        while (!meses.isAfter(teto)) {
            listaMeses.add(meses);
            meses = meses.plusMonths(1);
        }

        return listaMeses;
    }
}
