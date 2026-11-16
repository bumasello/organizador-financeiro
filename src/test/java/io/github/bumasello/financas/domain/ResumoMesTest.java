package io.github.bumasello.financas.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

/**
 * ResumoMesTest
 */
public class ResumoMesTest {
    private final BigDecimal renda = new BigDecimal("2600.00");
    private final BigDecimal ajusteRenda = new BigDecimal("1500.00");
    private final BigDecimal aporte = new BigDecimal("1000.00");
    private final BigDecimal ajusteAporte = new BigDecimal("2500.00");
    private final BigDecimal parcelas = new BigDecimal("180.00");

    @Test
    public void mesComTodosValores() {
        ResumoMes resumo = new ResumoMes(this.renda, this.ajusteRenda, this.aporte, this.ajusteAporte, this.parcelas);

        assertEquals(new BigDecimal("420.00"), resumo.disponivel());
    }

    @Test
    public void mesComValoresZerados() {
        ResumoMes resumo = new ResumoMes(this.renda, new BigDecimal("0.00"), this.aporte,
                new BigDecimal("0.00"), this.parcelas);

        assertEquals(new BigDecimal("1420.00"), resumo.disponivel());
    }

    @Test
    public void mesDisponivelNegativo() {
        ResumoMes resumo = new ResumoMes(this.renda, new BigDecimal("0.00"), this.aporte,
                new BigDecimal("0.00"), new BigDecimal("1800.00"));

        assertEquals(new BigDecimal("-200.00"), resumo.disponivel());

    }

    @Test
    public void rendaNula() {

        assertThrows(IllegalArgumentException.class, () -> {
            new ResumoMes(null, this.ajusteRenda, this.aporte, this.ajusteAporte, this.parcelas);
        });
    }

    @Test
    public void ajusteRendaNulo() {

        assertThrows(IllegalArgumentException.class, () -> {
            new ResumoMes(this.renda, null, this.aporte, this.ajusteAporte, this.parcelas);
        });
    }

    @Test
    public void aporteNulo() {

        assertThrows(IllegalArgumentException.class, () -> {
            new ResumoMes(this.renda, this.ajusteRenda, null, this.ajusteAporte, this.parcelas);
        });
    }

    @Test
    public void ajusteAporteNulo() {

        assertThrows(IllegalArgumentException.class, () -> {
            new ResumoMes(this.renda, this.ajusteRenda, this.aporte, null, this.parcelas);
        });
    }

    @Test
    public void parcelasNula() {

        assertThrows(IllegalArgumentException.class, () -> {
            new ResumoMes(this.renda, this.ajusteRenda, this.aporte, this.ajusteAporte, null);
        });
    }
}
