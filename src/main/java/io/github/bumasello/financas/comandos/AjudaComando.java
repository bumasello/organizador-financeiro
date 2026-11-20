package io.github.bumasello.financas.comandos;

import java.util.List;

/**
 * AjudaComando
 */
public class AjudaComando implements Comando {

    @Override
    public Resposta executar(long chatId, String texto) {
        String respostaTexto = """
                /perfil - cadastrar perfil
                /disponivel - quanto ainda posso gastar no mês
                /objetivo - atualizar aporte e/ou meta
                /start - cadastrar um usuário
                /cartao - cadastrar um novo cartão
                /gasto - cadastrar uma nova compra
                /ajuda - esta lista
                """;

        List<String> botoes = List.of("/disponivel", "/perfil", "/objetivo", "/cartao", "/gasto", "/ajuda");

        return new Resposta(respostaTexto, botoes);
    }
}
