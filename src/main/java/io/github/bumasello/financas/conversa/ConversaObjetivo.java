package io.github.bumasello.financas.conversa;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

import io.github.bumasello.financas.comandos.Resposta;
import io.github.bumasello.financas.domain.EstadoConversa;
import io.github.bumasello.financas.repo.UsuarioRepository;

/**
 * ConversaObjetivo
 */
public class ConversaObjetivo {

    private final UsuarioRepository usuarioRepository;

    public ConversaObjetivo(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Resposta responder(long chatId, EstadoConversa estado, String texto) {

        if (chatId == 0) {
            throw new IllegalArgumentException("chatId não pode ser zero");
        }

        if (estado == null) {
            throw new IllegalArgumentException("estado nao pode ser nulo");
        }

        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("texto nao pode ser nulo ou vazio");
        }

        try {
            switch (estado) {
                case ESCOLHENDO_OBJETIVO -> {
                    switch (texto.strip().toLowerCase(Locale.ROOT)) {
                        case "aporte" -> {
                            usuarioRepository.atualizaEstado(chatId, EstadoConversa.AGUARDANDO_APORTE);
                            return new Resposta("Qual o novo valor do aporte mensal?");
                        }
                        case "meta" -> {
                            usuarioRepository.atualizaEstado(chatId, EstadoConversa.AGUARDANDO_META);
                            return new Resposta("Qual o novo valor da meta mensal?");
                        }
                        case "ambos" -> {
                            usuarioRepository.atualizaEstado(chatId, EstadoConversa.AGUARDANDO_APORTE_DOS_DOIS);
                            return new Resposta("Qual o novo valor do aporte mensal?");
                        }
                        case "cancelar" -> {
                            usuarioRepository.atualizaEstado(chatId, EstadoConversa.OCIOSO);
                            return new Resposta("Tudo bem, nada foi alterado.");
                        }
                        default -> {
                            return new Resposta("Não entendi. O que você quer atualizar?",
                                    List.of("Aporte", "Meta", "Ambos", "Cancelar"));
                        }
                    }
                }
                default -> {
                    usuarioRepository.atualizaEstado(chatId, EstadoConversa.OCIOSO);
                    return new Resposta("Ainda não sei continuar daqui...");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return new Resposta("Não consigo continuar agora. Tente novamente mais tarde!");
        }

    }

}
