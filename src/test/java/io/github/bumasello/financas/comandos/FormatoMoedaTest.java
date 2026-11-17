package io.github.bumasello.financas.comandos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    public void semCentavos() {
        String valor = "2500";

        assertEquals(0, new BigDecimal(valor).compareTo(FormatoMoeda.ler(valor)));
    }

    @Test
    public void comCentavos() {
        String valor = "2500,50";

        assertEquals(0, new BigDecimal("2500.50").compareTo(FormatoMoeda.ler(valor)));
    }

    @Test
    public void comPontoEVirgula() {
        String valor = "2.500,00";
        assertEquals(0, new BigDecimal("2500.00").compareTo(FormatoMoeda.ler(valor)));
    }

    @Test
    public void numeroPequeno() {
        String valor = "5,5";

        assertEquals(0, new BigDecimal("5.5").compareTo(FormatoMoeda.ler(valor)));
    }

    @Test
    public void comRSNoComeco() {
        String valor = "R$ 2.500,50";

        assertEquals(0, new BigDecimal("2500.50").compareTo(FormatoMoeda.ler(valor)));
    }

    @Test
    public void comABC() {
        String valor = "abc";

        assertThrows(NumberFormatException.class, () -> {
            FormatoMoeda.ler(valor);
        });
    }
}
