package io.github.bumasello.financas.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class MeioPagamentoTest {

    @Test
    public void shouldBeEqual() {
        MeioPagamento x = new MeioPagamento("Nubank", TipoPagamento.CREDITO, 7, 14);
        MeioPagamento z = new MeioPagamento("Nubank", TipoPagamento.CREDITO, 7, 14);

        assertEquals(x, z);
    }

    @Test
    public void shouldMatch() {
        MeioPagamento x = new MeioPagamento("Nubank", TipoPagamento.CREDITO, 7, 14);

        assertEquals(7, x.diaFechamento());
        assertEquals(TipoPagamento.CREDITO, x.tipo());
    }

    @Test
    public void shouldNotThrow() {
        assertDoesNotThrow(() -> new MeioPagamento("Nubank", TipoPagamento.IMEDIATO, 0, 0));
        assertDoesNotThrow(() -> new MeioPagamento("Nubank", TipoPagamento.CREDITO, 1, 31));
        assertDoesNotThrow(() -> new MeioPagamento("Nubank", TipoPagamento.CREDITO, 31, 1));
    }

    @Test
    public void nameCanNotBeEmptyStrings() {
        assertThrows(IllegalArgumentException.class, () -> new MeioPagamento("", TipoPagamento.IMEDIATO, 0, 0));
    }

    @Test
    public void nameCanNotBeOnlyWhiteSpaces() {
        assertThrows(IllegalArgumentException.class, () -> new MeioPagamento("    ", TipoPagamento.IMEDIATO, 0, 0));
    }

    @Test
    public void tipoCanNotBeNull() {
        assertThrows(IllegalArgumentException.class, () -> new MeioPagamento("Nubank", null, 0, 0));
    }

    @Test
    public void tipoCreditoCanNotHaveDayZero() {
        assertThrows(IllegalArgumentException.class, () -> new MeioPagamento("Nubank", TipoPagamento.CREDITO, 0, 2));
        assertThrows(IllegalArgumentException.class, () -> new MeioPagamento("Nubank", TipoPagamento.CREDITO, 2, 0));
    }

    @Test
    public void tipoCreditoCanNotHaveDayHigherThenThirtyOne() {

        assertThrows(IllegalArgumentException.class, () -> new MeioPagamento("Nubank", TipoPagamento.CREDITO, 32, 2));
        assertThrows(IllegalArgumentException.class, () -> new MeioPagamento("Nubank", TipoPagamento.CREDITO, 2, 32));
    }

    @Test
    public void tipoImeditadoCantHaveDayDifferentThenZero() {
        assertThrows(IllegalArgumentException.class, () -> new MeioPagamento("Nubank", TipoPagamento.IMEDIATO, 2, 0));
        assertThrows(IllegalArgumentException.class, () -> new MeioPagamento("Nubank", TipoPagamento.IMEDIATO, 0, 2));
    }

}
