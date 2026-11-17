package io.github.bumasello.financas.comandos;

/**
 * AjudaComando
 */
public class AjudaComando implements Comando {

    @Override
    public String executar(long chatId, String texto) {
        return """
                /disponivel - quanto ainda posso gastar no mês
                /ajuda - esta lista
                """;
    }
}
