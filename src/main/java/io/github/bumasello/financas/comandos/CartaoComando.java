package io.github.bumasello.financas.comandos;

import java.sql.SQLException;

import io.github.bumasello.financas.domain.EstadoConversa;
import io.github.bumasello.financas.repo.UsuarioRepository;

/**
 * CartaoComando
 */
public class CartaoComando implements Comando {
    private final UsuarioRepository usuarioRepository;

    public CartaoComando(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Resposta executar(long chatId, String texto) {

        if (chatId == 0) {
            throw new IllegalArgumentException("chatId nao pode ser nulo");
        }

        try {
            this.usuarioRepository.atualizaEstado(chatId, EstadoConversa.ESCOLHENDO_CARTAO);

            return new Resposta("Primeiro diga o nome do cartão a ser cadastrado.");

        } catch (SQLException e) {
            e.printStackTrace();

            return new Resposta("Não consigo iniciar agora. Tente novamente mais tarde!");
        }

    }
}
