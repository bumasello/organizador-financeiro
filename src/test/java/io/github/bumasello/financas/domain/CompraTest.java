package io.github.bumasello.financas.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class CompraTest {
    private final LocalDate DATE = LocalDate.of(2026, 10, 24);

    @Test
    public void devemSerIguais() {
        Compra x = new Compra(DATE, "Tatuagem", "Tatuagem", new BigDecimal("1800.00"), 10);
        Compra z = new Compra(DATE, "Tatuagem", "Tatuagem", new BigDecimal("1800.00"), 10);

        assertEquals(x, z);
    }

    @Test
    public void devemTerMesmaData() {
        Compra x = new Compra(DATE, "Tatuagem", "Tatuagem", new BigDecimal("1800.00"), 10);

        assertEquals(DATE, x.dataCompra());
    }

    @Test
    public void naoDeveLancarErro() {

        assertDoesNotThrow(
                () -> new Compra(DATE, "Tatuagem", "Tatuagem", new BigDecimal("1800.00"), 10));

    }

    @Test
    public void deveLancarErroDataCompraNula() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Compra(null, "Tatuagem", "Tatuagem", new BigDecimal("1800.00"), 10);
        });
    }

    @Test
    public void deveLancarErroDescricaoVazia() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Compra(DATE, "", "Tatuagem", new BigDecimal("1800.00"), 10);
        });
    }

    @Test
    public void deveLancarErroDescricaoComEspacos() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Compra(DATE, "    ", "Tatuagem", new BigDecimal("1800.00"), 10);
        });
    }

    @Test
    public void deveLancarErroDescricaoNula() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Compra(DATE, null, "Tatuagem", new BigDecimal("1800.00"), 10);
        });
    }

    @Test
    public void deveLancarErroCategoriaVazia() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Compra(DATE, "Tatuagem", "", new BigDecimal("1800.00"), 10);
        });
    }

    @Test
    public void deveLancarErroCategoriaComEspacos() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Compra(DATE, "Tatuagem", "   ", new BigDecimal("1800.00"), 10);
        });
    }

    @Test
    public void deveLancarErroCategoriaNula() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Compra(DATE, "Tatuagem", null, new BigDecimal("1800.00"), 10);
        });
    }

    @Test
    public void deveLancarErroValorTotalNulo() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Compra(DATE, "Tatuagem", "Tatuagem", null, 10);
        });
    }

    @Test
    public void deveLancarErroValorTotalMenorQueZero() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Compra(DATE, "Tatuagem", "Tatuagem", new BigDecimal("-1"), 10);
        });
    }

    @Test
    public void deveLancarErroValorTotalIgualZero() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Compra(DATE, "Tatuagem", "Tatuagem", new BigDecimal("0"), 10);
        });
    }

    @Test
    public void deveLancarErroQuantidadeParcelasMenorQueUm() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Compra(DATE, "Tatuagem", "Tatuagem", new BigDecimal("1800.00"), 0);
        });
    }
}
