package io.github.bumasello.financas.comandos;

import java.util.List;

/**
 * AjudaComando
 */
public class AjudaComando implements Comando {

    @Override
    public Resposta executar(long chatId, String texto) {

        List<String> botoes = List.of("/disponivel", "/perfil", "/ajuda");

        return new Resposta("Segue a lista de comandos disponíveis.", botoes);
    }
}
