package io.github.bumasello.financas.comandos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

/**
 * FormatoMoedaTest
 */
public class FormatoMoedaTest {

    @Test
    public void formataPositivo() {
        assertEquals("R$\u00A01.320,00", FormatoMoeda.formatar(new BigDecimal("1320.00")));
    }

    @Test
    public void formataNegativo() {
        assertEquals("-R$\u00A0300,00", FormatoMoeda.formatar(new BigDecimal("-300.00")));
    }

    @Test
    public void formataZero() {
        assertEquals("R$\u00A00,00", FormatoMoeda.formatar(new BigDecimal("0.00")));
    }

}
