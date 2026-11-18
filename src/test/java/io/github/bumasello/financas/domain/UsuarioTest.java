package io.github.bumasello.financas.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

/**
 * UsuarioTest
 */
public class UsuarioTest {

    @Test
    public void devemSerIguais() {
        Usuario x = new Usuario(1, 1, "Bruno Masello", Situacao.APROVADO, new BigDecimal("1000.00"), 6,
                new BigDecimal("100.00"), new BigDecimal("10000.00"), EstadoConversa.OCIOSO);
        Usuario z = new Usuario(1, 1, "Bruno Masello", Situacao.APROVADO, new BigDecimal("1000.00"), 6,
                new BigDecimal("100.00"), new BigDecimal("10000.00"), EstadoConversa.OCIOSO);

        assertEquals(x, z);
    }

    @Test
    public void construtorCompleto() {
        assertDoesNotThrow(() -> {
            new Usuario(1, 1, "Bruno Masello", Situacao.APROVADO, new BigDecimal("1000.00"), 6,
                    new BigDecimal("100.00"), new BigDecimal("10000.00"), EstadoConversa.OCIOSO);

        });
    }

    @Test
    public void usuarioNovoSemPerfil() {
        assertDoesNotThrow(() -> {
            new Usuario(1, 1, Situacao.APROVADO, new BigDecimal("1000.00"), new BigDecimal("10000.00"));

        });
    }

    @Test
    public void situacaoNula() {

        assertThrows(IllegalArgumentException.class, () -> {
            new Usuario(1, 1, "Bruno Masello", null, new BigDecimal("1000.00"), 6,
                    new BigDecimal("100.00"), new BigDecimal("10000.00"), EstadoConversa.OCIOSO);
        });
    }

    @Test
    public void nomeVazio() {

        assertThrows(IllegalArgumentException.class, () -> {
            new Usuario(1, 1, "", Situacao.APROVADO, new BigDecimal("1000.00"), 6,
                    new BigDecimal("100.00"), new BigDecimal("10000.00"), EstadoConversa.OCIOSO);
        });
    }

    @Test
    public void nomeApenasEspacos() {

        assertThrows(IllegalArgumentException.class, () -> {
            new Usuario(1, 1, "    ", Situacao.APROVADO, new BigDecimal("1000.00"), 6,
                    new BigDecimal("100.00"), new BigDecimal("10000.00"), EstadoConversa.OCIOSO);
        });
    }

    @Test
    public void rendaMensalNegativa() {

        assertThrows(IllegalArgumentException.class, () -> {
            new Usuario(1, 1, "Bruno Masello", Situacao.APROVADO, new BigDecimal("-10.00"), 6,
                    new BigDecimal("100.00"), new BigDecimal("10000.00"), EstadoConversa.OCIOSO);
        });
    }

    @Test
    public void aporteMensalNegativo() {

        assertThrows(IllegalArgumentException.class, () -> {
            new Usuario(1, 1, "Bruno Masello", Situacao.APROVADO, new BigDecimal("1000.00"), 6,
                    new BigDecimal("-10.00"), new BigDecimal("10000.00"), EstadoConversa.OCIOSO);
        });
    }

    @Test
    public void aporteMensalNulo() {

        assertThrows(IllegalArgumentException.class, () -> {
            new Usuario(1, 1, "Bruno Masello", Situacao.APROVADO, new BigDecimal("1000.00"), 6,
                    null, new BigDecimal("-10.00"), EstadoConversa.OCIOSO);
        });
    }

    @Test
    public void metaReservaNegativa() {

        assertThrows(IllegalArgumentException.class, () -> {
            new Usuario(1, 1, "Bruno Masello", Situacao.APROVADO, new BigDecimal("1000.00"), 6,
                    new BigDecimal("100.00"), new BigDecimal("-10.00"), EstadoConversa.OCIOSO);
        });
    }

    @Test
    public void diaSalarioIncorretoMenor1() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Usuario(1, 1, "Bruno Masello", Situacao.APROVADO, new BigDecimal("1000.00"), 0,
                    new BigDecimal("100.00"), new BigDecimal("10000.00"), EstadoConversa.OCIOSO);
        });

    }

    @Test
    public void diaSalarioIncorretoMaior31() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Usuario(1, 1, "Bruno Masello", Situacao.APROVADO, new BigDecimal("1000.00"), 32,
                    new BigDecimal("100.00"), new BigDecimal("10000.00"), EstadoConversa.OCIOSO);
        });

    }

    @Test
    public void diaSalarioCorreto() {
        assertDoesNotThrow(() -> {
            new Usuario(1, 1, "Bruno Masello", Situacao.APROVADO, new BigDecimal("1000.00"), 6,
                    new BigDecimal("100.00"), new BigDecimal("10000.00"), EstadoConversa.OCIOSO);
        });

    }
}
