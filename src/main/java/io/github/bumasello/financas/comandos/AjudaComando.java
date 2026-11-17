package io.github.bumasello.financas.comandos;

/**
 * AjudaComando
 */
public class AjudaComando implements Comando {

    @Override
    public String executar(long chatId, String texto) {
        return """
                /perfil - cadastrar perfil
                /disponivel - quanto ainda posso gastar no mês
                /start - cadastrar um usuário
                /ajuda - esta lista
                """;
    }
}
