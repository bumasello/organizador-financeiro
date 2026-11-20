package io.github.bumasello.financas.comandos;

import java.sql.SQLException;

import io.github.bumasello.financas.domain.EstadoConversa;
import io.github.bumasello.financas.repo.UsuarioRepository;

/**
 * GastoComando
 */
public class GastoComando implements Comando {
    private final UsuarioRepository usuarioRepository;

    public GastoComando(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Resposta executar(long chatId, String texto) {
        if (chatId == 0) {
            throw new IllegalArgumentException("chatId nao pode ser zero");
        }

        try {
            this.usuarioRepository.atualizaEstado(chatId, EstadoConversa.GASTO_DESCRICAO);
            return new Resposta("Diga em poucas palavras o que foi comprado.");

        } catch (SQLException e) {
            e.printStackTrace();

            return new Resposta("Não consigo iniciar agora. Tente novamente mais tarde!");
        }
    }

}
