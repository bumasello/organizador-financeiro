package io.github.bumasello.financas.comandos;

import java.sql.SQLException;
import java.util.Optional;

import io.github.bumasello.financas.domain.Usuario;
import io.github.bumasello.financas.repo.UsuarioRepository;

/**
 * StartComando
 */
public class StartComando implements Comando {
    private final UsuarioRepository usuarioRepository;

    public StartComando(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Resposta executar(long chatId, String texto) {

        try {

            Optional<Usuario> optionalUsuario = this.usuarioRepository.buscaPorChatId(chatId);

            if (optionalUsuario.isPresent()) {
                Usuario usuario = optionalUsuario.get();
                String respostaTexto = "Usuário já cadastrado! Situação: " + usuario.situacao();
                return new Resposta(respostaTexto);
            }

            this.usuarioRepository.cadastraChatId(chatId);

            return new Resposta("Usuário cadastrado com sucesso!");

        } catch (SQLException e) {
            e.printStackTrace();
            return new Resposta("Não foi possível cadastrar usuário");
        }

    }

}
