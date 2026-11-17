package io.github.bumasello.financas.comandos;

/**
 * Comando
 */
public interface Comando {
    String executar(long chatId, String texto);

}
