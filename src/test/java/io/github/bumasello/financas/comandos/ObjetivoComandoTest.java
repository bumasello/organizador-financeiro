package io.github.bumasello.financas.comandos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * ObjetivoComandoTest
 */
public class ObjetivoComandoTest {

    @Test
    public void exemploDeUso() {

        String texto = "/objetivo 1000.00 2000.00 3000.00";

        assertEquals("Use: /objetivo valor1 valor2", new ObjetivoComando(null).executar(123L, texto).texto());
    }

    @Test
    public void exemploNegativoAporte() {

        String texto = "/objetivo -1000.00 2000.00";

        assertEquals("O aporte não pode ser negativo.", new ObjetivoComando(null).executar(123L, texto).texto());
    }

    @Test
    public void exemploNegativoMetaReserva() {

        String texto = "/objetivo 1000.00 -2000.00";

        assertEquals("A meta reserva não pode ser negativa.", new ObjetivoComando(null).executar(123L, texto).texto());
    }

    @Test
    public void exemploValorIncorreto() {

        String texto = "/objetivo abc -2000.00";
        String[] partes = texto.strip().split(" ");

        assertEquals("Não entendi um desses valores: " + partes[1] + " e " + partes[2],
                new ObjetivoComando(null).executar(123L, texto).texto());
    }
}
