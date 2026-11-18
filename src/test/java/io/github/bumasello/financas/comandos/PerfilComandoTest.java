package io.github.bumasello.financas.comandos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * PerfilComandoTest
 */
public class PerfilComandoTest {

    @Test
    public void exemploDeUso() {
        String texto = "/perfil ";

        assertEquals("Use: /perfil 2.500,00", new PerfilComando(null).executar(1L, texto).texto());
    }

    @Test
    public void rendaNegativa() {
        String texto = "/perfil -300,00";

        assertEquals("A renda não pode ser negativa.", new PerfilComando(null).executar(1L, texto).texto());
    }

    @Test
    public void naoEntendiOValor() {
        String texto = "/perfil abc";

        assertEquals("Não entendi o valor", new PerfilComando(null).executar(1L, texto).texto());
    }
}
