package io.github.bumasello.financas.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.YearMonth;

import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Test;

public class CalculadoraFaturaTest {

    @Test
    public void shouldBeSameMonth() {
        MeioPagamento x = new MeioPagamento("Nubank", TipoPagamento.CREDITO, 7, 14);

        LocalDate dataCompra = LocalDate.of(2026, 11, 3);

        assertEquals(YearMonth.of(2026, 11), CalculadoraFatura.mesDaFatura(x, dataCompra));
    }

    @Test
    public void shouldBeNextMonthClosingDay() {
        MeioPagamento x = new MeioPagamento("Nubank", TipoPagamento.CREDITO, 7, 14);

        LocalDate dataCompra = LocalDate.of(2026, 11, 7);

        assertEquals(YearMonth.of(2026, 12), CalculadoraFatura.mesDaFatura(x, dataCompra));
    }

    @Test
    public void shouldBeNextMonthAfterClosed() {
        MeioPagamento x = new MeioPagamento("Nubank", TipoPagamento.CREDITO, 7, 14);

        LocalDate dataCompra = LocalDate.of(2026, 11, 8);

        assertEquals(YearMonth.of(2026, 12), CalculadoraFatura.mesDaFatura(x, dataCompra));
    }

    @Test
    public void shouldBeSameMonthTipoImediato() {
        MeioPagamento x = new MeioPagamento("Nubank", TipoPagamento.IMEDIATO, 0, 0);

        LocalDate dataCompra = LocalDate.of(2026, 11, 8);

        assertEquals(YearMonth.of(2026, 11), CalculadoraFatura.mesDaFatura(x, dataCompra));
    }

    @Test
    public void shouldBeNextMonth() {
        MeioPagamento x = new MeioPagamento("Bradesco", TipoPagamento.CREDITO, 30, 10);

        LocalDate dataCompra = LocalDate.of(2026, 11, 15);

        assertEquals(YearMonth.of(2026, 12), CalculadoraFatura.mesDaFatura(x, dataCompra));
    }

    @Test
    public void shouldBeNextYear() {
        MeioPagamento x = new MeioPagamento("Bradesco", TipoPagamento.CREDITO, 30, 10);

        LocalDate dataCompra = LocalDate.of(2026, 11, 30);

        assertEquals(YearMonth.of(2027, 1), CalculadoraFatura.mesDaFatura(x, dataCompra));
    }

    @Test
    public void monthLengthDifferentThenThirtyOne() {
        MeioPagamento x = new MeioPagamento("Inter", TipoPagamento.CREDITO, 31, 10);

        LocalDate dataCompra = LocalDate.of(2026, 11, 29);

        assertEquals(YearMonth.of(2026, 12), CalculadoraFatura.mesDaFatura(x, dataCompra));
    }

    @Test
    public void compraFechamentoDiferenteDeTrintaUm() {
        MeioPagamento x = new MeioPagamento("Inter", TipoPagamento.CREDITO, 31, 10);

        LocalDate dataCompra = LocalDate.of(2026, 11, 30);

        assertEquals(YearMonth.of(2027, 1), CalculadoraFatura.mesDaFatura(x, dataCompra));
    }

    @Test
    public void monthFebruary() {
        MeioPagamento x = new MeioPagamento("Inter", TipoPagamento.CREDITO, 31, 10);

        LocalDate dataCompra = LocalDate.of(2027, 2, 28);

        assertEquals(YearMonth.of(2027, 4), CalculadoraFatura.mesDaFatura(x, dataCompra));
    }

    @Test
    public void anoBissexto() {
        MeioPagamento x = new MeioPagamento("Inter", TipoPagamento.CREDITO, 31, 10);

        LocalDate dataCompra = LocalDate.of(2028, 2, 28);

        assertEquals(YearMonth.of(2028, 3), CalculadoraFatura.mesDaFatura(x, dataCompra));
    }
}
