package io.github.bumasello.financas.comandos;

/**
 * Comando
 */
public interface Comando {
    Resposta executar(long chatId, String texto);

}
