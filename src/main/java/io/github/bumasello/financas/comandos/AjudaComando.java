package io.github.bumasello.financas.comandos;

/**
 * AjudaComando
 */
public class AjudaComando implements Comando {

    @Override
    public Resposta executar(long chatId, String texto) {
        String respostaTexto = """
                /perfil - cadastrar perfil
                /disponivel - quanto ainda posso gastar no mês
                /start - cadastrar um usuário
                /ajuda - esta lista
                """;

        return new Resposta(respostaTexto);
    }
}
