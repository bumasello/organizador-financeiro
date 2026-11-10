package io.github.bumasello.financas.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    @Test
    public void totalUmaVez() {
        assertEquals(List.of(new BigDecimal("100.00")),
                CalculadoraFatura.dividirEmParcelas(new BigDecimal("100.00"), 1));
    }

    @Test
    public void totalTresVezes() {
        List<BigDecimal> parcelas = List.of(new BigDecimal("33.34"), new BigDecimal("33.33"),
                new BigDecimal("33.33"));

        assertEquals(parcelas, CalculadoraFatura.dividirEmParcelas(new BigDecimal("100.00"), 3));
    }

    @Test
    public void total200TresVezes() {
        BigDecimal total = new BigDecimal("200.00");
        List<BigDecimal> parcelas = List.of(new BigDecimal("66.67"), new BigDecimal("66.67"),
                new BigDecimal("66.66"));

        assertEquals(parcelas, CalculadoraFatura.dividirEmParcelas(total, 3));
    }

    @Test
    public void total90TresVezes() {
        BigDecimal total = new BigDecimal("90.00");
        List<BigDecimal> parcelas = List.of(new BigDecimal("30.00"), new BigDecimal("30.00"),
                new BigDecimal("30.00"));

        assertEquals(parcelas, CalculadoraFatura.dividirEmParcelas(total, 3));
    }

    @Test
    public void total1800DezVezes() {
        BigDecimal total = new BigDecimal("1800.00");

        List<BigDecimal> parcelas = Collections.nCopies(10, new BigDecimal("180.00"));

        assertEquals(parcelas, CalculadoraFatura.dividirEmParcelas(total, 10));
    }

    @Test
    public void total10Parcelas() {
        BigDecimal total = new BigDecimal("1800.00");
        MeioPagamento x = new MeioPagamento("Nubank", TipoPagamento.CREDITO, 7, 14);
        LocalDate dataCompra = LocalDate.of(2026, 10, 24);
        List<Parcela> parcelas = CalculadoraFatura.parcelar(x, dataCompra, total, 10);

        assertEquals(new Parcela(1, new BigDecimal("180.00"), YearMonth.of(2026, 11)), parcelas.get(0));
        assertEquals(new Parcela(10, new BigDecimal("180.00"), YearMonth.of(2027, 8)),
                parcelas.get(parcelas.size() - 1));

        assertEquals(10, parcelas.size());
    }

    @Test
    public void parcelasQuebradas() {
        BigDecimal total = new BigDecimal("200.00");
        MeioPagamento x = new MeioPagamento("Bradesco", TipoPagamento.CREDITO, 30, 10);
        LocalDate dataCompra = LocalDate.of(2026, 11, 15);
        List<Parcela> parcelasEsperadas = List.of(
                new Parcela(1, new BigDecimal("66.67"), YearMonth.of(2026, 12)),
                new Parcela(2, new BigDecimal("66.67"), YearMonth.of(2027, 1)),
                new Parcela(3, new BigDecimal("66.66"), YearMonth.of(2027, 2)));

        assertEquals(parcelasEsperadas, CalculadoraFatura.parcelar(x, dataCompra, total, 3));
    }

    @Test
    public void parcelarComImediato() {
        BigDecimal total = new BigDecimal("50.00");
        MeioPagamento x = new MeioPagamento("Nubank", TipoPagamento.IMEDIATO, 0, 0);
        LocalDate dataCompra = LocalDate.of(2026, 11, 3);
        List<Parcela> parcelasEsperadas = List.of(
                new Parcela(1, new BigDecimal("50.00"), YearMonth.of(2026, 11)));

        assertEquals(parcelasEsperadas, CalculadoraFatura.parcelar(x, dataCompra, total, 1));
    }

    public void parcelarComImediato3Vezes() {
        BigDecimal total = new BigDecimal("50.00");
        MeioPagamento x = new MeioPagamento("Nubank", TipoPagamento.IMEDIATO, 0, 0);
        LocalDate dataCompra = LocalDate.of(2026, 11, 3);

        assertThrows(IllegalArgumentException.class, () -> {
            CalculadoraFatura.parcelar(x, dataCompra, total, 3);
        });
    }

    @Test
    public void verificaQuantidadePositiva() {
        assertThrows(IllegalArgumentException.class, () -> {
            CalculadoraFatura.dividirEmParcelas(new BigDecimal("100.00"), 0);
        });

    }

    @Test
    public void verificaTotalPositivo() {
        assertThrows(IllegalArgumentException.class, () -> {
            CalculadoraFatura.dividirEmParcelas(BigDecimal.valueOf(0), 2);
        });

    }

    @Test
    public void verificaValorMinimoParcela() {
        assertThrows(IllegalArgumentException.class, () -> {
            CalculadoraFatura.dividirEmParcelas(new BigDecimal("0.05"), 10);
        });

    }

    @Test
    public void verificaValorMinimoParcelaCorreto() {
        assertDoesNotThrow(() -> {
            CalculadoraFatura.dividirEmParcelas(new BigDecimal("0.10"), 10);
        });

    }
}
