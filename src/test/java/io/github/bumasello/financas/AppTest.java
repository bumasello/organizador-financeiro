package io.github.bumasello.financas;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.checkerframework.checker.interning.qual.CompareToMethod;
import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {

    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }

    @Test
    public void problemaDouble() {
        assertNotEquals(0.3, 0.1 + 0.2);
    }

    @Test
    public void testandoConstrutor() {
        BigDecimal x = new BigDecimal("0.1");
        BigDecimal y = new BigDecimal(0.1);
        // System.out.println(x);
        // System.out.println(y);

        assertNotEquals(x, y);
    }

    @Test
    public void testandoEscala() {
        BigDecimal x = new BigDecimal("2.0");
        BigDecimal y = new BigDecimal("2.00");
        // System.out.println(x);
        // System.out.println(y);

        assertNotEquals(x, y);

    }

    @Test
    public void testandoCompareTo() {
        BigDecimal x = new BigDecimal("2.0");
        BigDecimal y = new BigDecimal("2.00");
        // System.out.println(x);
        // System.out.println(y);

        assertEquals(0, x.compareTo(y));

    }

    @Test
    public void verificandoDivisao() {
        BigDecimal x = new BigDecimal("100");
        BigDecimal y = new BigDecimal("3");
        assertThrows(ArithmeticException.class, () -> x.divide(y));
        // assertDoesNotThrow(() -> x.divide(y));

    }

    @Test
    public void verificandoArredondamento() {

        BigDecimal x = new BigDecimal("200");
        BigDecimal y = new BigDecimal("3");

        // System.out.println(x.divide(y, 2, RoundingMode.DOWN));
        // System.out.println(x.divide(y, 2, RoundingMode.HALF_UP));

        BigDecimal parcelaBase = x.divide(y, 2, RoundingMode.DOWN);
        BigDecimal resto = x.subtract(parcelaBase.multiply(y));
        System.out.println(resto);

        assertEquals(new BigDecimal("66.66"), x.divide(y, 2, RoundingMode.DOWN));
        assertEquals(new BigDecimal("66.67"), x.divide(y, 2, RoundingMode.HALF_UP));
        assertEquals(0, resto.compareTo(new BigDecimal("0.02")));
    }
}
