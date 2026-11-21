package io.github.bumasello.financas.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * RecorrenciaTest
 */
public class RecorrenciaTest {

    @Test
    public void deveRetornarTrue() {
        Recorrencia recorrencia = new Recorrencia("Tim", new BigDecimal("159.00"), "moradia", 15,
                YearMonth.of(2026, 11), null);

        assertTrue(recorrencia.ativaEm(YearMonth.of(2026, 11)));
    }

    @Test
    public void deveRetornarFalse() {
        Recorrencia recorrencia = new Recorrencia("Tim", new BigDecimal("159.00"), "moradia", 15,
                YearMonth.of(2026, 11), null);

        assertFalse(recorrencia.ativaEm(YearMonth.of(2026, 10)));
    }

    @Test
    public void deveRetornarTrueAntesDoFim() {
        Recorrencia recorrencia = new Recorrencia("Tim", new BigDecimal("159.00"), "moradia", 15,
                YearMonth.of(2026, 11), YearMonth.of(2027, 11));

        assertTrue(recorrencia.ativaEm(YearMonth.of(2026, 12)));
    }

    @Test
    public void deveRetornarTrueNoUltimoMes() {
        Recorrencia recorrencia = new Recorrencia("Tim", new BigDecimal("159.00"), "moradia", 15,
                YearMonth.of(2026, 11), YearMonth.of(2027, 11));

        assertTrue(recorrencia.ativaEm(YearMonth.of(2027, 11)));
    }

    @Test
    public void deveRetornarFalseDepoisDoFim() {
        Recorrencia recorrencia = new Recorrencia("Tim", new BigDecimal("159.00"), "moradia", 15,
                YearMonth.of(2026, 11), YearMonth.of(2027, 11));

        assertFalse(recorrencia.ativaEm(YearMonth.of(2027, 12)));
    }

    @Test
    public void deveRetornarDiaInputado() {
        Recorrencia recorrencia = new Recorrencia("Tim", new BigDecimal("159.00"), "moradia", 13,
                YearMonth.of(2026, 11), YearMonth.of(2027, 11));

        assertEquals(LocalDate.of(2026, 11, 13), recorrencia.dataCobranca(YearMonth.of(2026, 11)));
    }

    @Test
    public void deveRetornarDia28() {
        Recorrencia recorrencia = new Recorrencia("Tim", new BigDecimal("159.00"), "moradia", 31,
                YearMonth.of(2026, 11), YearMonth.of(2027, 11));

        assertEquals(LocalDate.of(2027, 2, 28), recorrencia.dataCobranca(YearMonth.of(2027, 2)));
    }

    @Test
    public void deveRetornarDia29Bissexto() {
        Recorrencia recorrencia = new Recorrencia("Tim", new BigDecimal("159.00"), "moradia", 31,
                YearMonth.of(2026, 11), YearMonth.of(2027, 11));

        assertEquals(LocalDate.of(2028, 2, 29), recorrencia.dataCobranca(YearMonth.of(2028, 2)));
    }

    @Test
    public void deveRetornarAListaDeMesesSemFim() {
        Recorrencia recorrencia = new Recorrencia("Tim", new BigDecimal("159.00"), "moradia", 31,
                YearMonth.of(2026, 11), null);

        List<YearMonth> lista = List.of(YearMonth.of(2026, 11), YearMonth.of(2026, 12), YearMonth.of(2027, 1),
                YearMonth.of(2027, 2));

        assertEquals(lista, recorrencia.mesesAte(YearMonth.of(2027, 2)));
    }

    @Test
    public void deveRetornarAListaDeMesesComFim() {
        Recorrencia recorrencia = new Recorrencia("Tim", new BigDecimal("159.00"), "moradia", 31,
                YearMonth.of(2026, 11), YearMonth.of(2027, 1));

        List<YearMonth> lista = List.of(YearMonth.of(2026, 11), YearMonth.of(2026, 12), YearMonth.of(2027, 1));

        assertEquals(lista, recorrencia.mesesAte(YearMonth.of(2027, 2)));
    }

    @Test
    public void deveRetornarAListaVazia() {
        Recorrencia recorrencia = new Recorrencia("Tim", new BigDecimal("159.00"), "moradia", 31,
                YearMonth.of(2026, 11), YearMonth.of(2027, 1));

        List<YearMonth> lista = List.of();

        assertEquals(lista, recorrencia.mesesAte(YearMonth.of(2026, 10)));
    }

    @Test
    public void fimAntesDoInicio() {

        assertThrows(IllegalArgumentException.class, () -> {
            new Recorrencia("Tim", new BigDecimal("159.00"), "moradia", 31,
                    YearMonth.of(2026, 11), YearMonth.of(2026, 10));
        });
    }
}
