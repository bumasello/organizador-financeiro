package io.github.bumasello.financas.conversa;

import io.github.bumasello.financas.comandos.Resposta;
import io.github.bumasello.financas.domain.EstadoConversa;

/**
 * Conversa
 */
public interface Conversa {
    Resposta responder(long chatId, EstadoConversa estadoConversa, String text);

}
