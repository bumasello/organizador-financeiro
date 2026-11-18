package io.github.bumasello.financas.comandos;

import java.sql.SQLException;
import java.util.List;

import io.github.bumasello.financas.domain.EstadoConversa;
import io.github.bumasello.financas.repo.UsuarioRepository;

/**
 * ObjetivoComando
 */
public class ObjetivoComando implements Comando {

    private final UsuarioRepository usuarioRepository;

    public ObjetivoComando(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Resposta executar(long chatId, String texto) {
        List<String> botoes = List.of("Aporte", "Meta", "Ambos", "Cancelar");
        try {

            this.usuarioRepository.atualizaEstado(chatId, EstadoConversa.ESCOLHENDO_OBJETIVO);

            return new Resposta("O que você quer atualizar?", botoes);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Resposta("Não consigo iniciar agora. Tente novamente mais tarde!");
        }

    }

}
