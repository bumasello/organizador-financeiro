package io.github.bumasello.financas.conversa;

import java.sql.SQLException;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.github.bumasello.financas.repo.UsuarioRepository;

/**
 * ConversaComRascunho
 */
public abstract class ConversaComRascunho<T> implements Conversa {
    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());
    protected final UsuarioRepository usuarioRepository;
    private final Class<T> tipoRascunho;

    public ConversaComRascunho(UsuarioRepository usuarioRepository, Class<T> tipoRascunho) {
        this.usuarioRepository = usuarioRepository;
        this.tipoRascunho = tipoRascunho;
    }

    protected Optional<T> lerRascunho(long chatId) throws JsonProcessingException, SQLException {

        Optional<String> optionalRascunho = this.usuarioRepository.buscaContextoPorChatId(chatId);

        if (optionalRascunho.isEmpty()) {
            return Optional.empty();
        }

        String rascunho = optionalRascunho.get();
        T novoRascunho = MAPPER.readValue(rascunho, this.tipoRascunho);

        return Optional.of(novoRascunho);
    }

    protected void gravarRascunho(long chatId, T rascunho) throws JsonProcessingException, SQLException {

        String rascunhoString = MAPPER.writeValueAsString(rascunho);
        this.usuarioRepository.atualizaContextoConversa(chatId, rascunhoString);

    }
}
