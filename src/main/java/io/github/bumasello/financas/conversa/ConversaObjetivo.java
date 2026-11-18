package io.github.bumasello.financas.conversa;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

import io.github.bumasello.financas.comandos.FormatoMoeda;
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
                            return new Resposta("Qual o novo valor do aporte mensal? Digite 'Cancelar' para desistir.");
                        }
                        case "meta" -> {
                            usuarioRepository.atualizaEstado(chatId, EstadoConversa.AGUARDANDO_META);
                            return new Resposta("Qual o novo valor da meta? Digite 'Cancelar' para desistir.");
                        }
                        case "ambos" -> {
                            usuarioRepository.atualizaEstado(chatId, EstadoConversa.AGUARDANDO_APORTE_DOS_DOIS);
                            return new Resposta("Qual o novo valor do aporte mensal? Digite 'Cancelar' para desistir.");
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
                case AGUARDANDO_APORTE -> {
                    if (texto.strip().equalsIgnoreCase("cancelar")) {
                        usuarioRepository.atualizaEstado(chatId, EstadoConversa.OCIOSO);
                        return new Resposta("Tudo bem, nada foi alterado.");
                    }
                    BigDecimal valor;
                    try {
                        valor = FormatoMoeda.ler(texto);
                    } catch (NumberFormatException e) {
                        return new Resposta("Não entendi o valor. Digite, por exemplo, 1.500,00");
                    }
                    if (valor.compareTo(BigDecimal.ZERO) < 0) {
                        return new Resposta("O valor mensal não pode ser menor que zero!");
                    }

                    this.usuarioRepository.atualizaAporte(chatId, valor);
                    this.usuarioRepository.atualizaEstado(chatId, EstadoConversa.OCIOSO);
                    return new Resposta("Aporte atualizado: " + FormatoMoeda.formatar(valor));
                }
                case AGUARDANDO_META -> {
                    if (texto.strip().equalsIgnoreCase("cancelar")) {
                        usuarioRepository.atualizaEstado(chatId, EstadoConversa.OCIOSO);
                        return new Resposta("Tudo bem, nada foi alterado.");
                    }
                    BigDecimal valor;
                    try {
                        valor = FormatoMoeda.ler(texto);
                    } catch (NumberFormatException e) {
                        return new Resposta("Não entendi o valor. Digite, por exemplo, 1.500,00");
                    }
                    if (valor.compareTo(BigDecimal.ZERO) < 0) {
                        return new Resposta("O valor da meta não pode ser menor que zero!");
                    }

                    this.usuarioRepository.atualizaMetaReserva(chatId, valor);
                    this.usuarioRepository.atualizaEstado(chatId, EstadoConversa.OCIOSO);
                    return new Resposta("Meta atualizada: " + FormatoMoeda.formatar(valor));
                }
                case AGUARDANDO_APORTE_DOS_DOIS -> {
                    if (texto.strip().equalsIgnoreCase("cancelar")) {
                        usuarioRepository.atualizaEstado(chatId, EstadoConversa.OCIOSO);
                        return new Resposta("Tudo bem, nada foi alterado.");
                    }
                    BigDecimal valor;
                    try {
                        valor = FormatoMoeda.ler(texto);
                    } catch (NumberFormatException e) {
                        return new Resposta("Não entendi o valor. Digite, por exemplo, 1.500,00");
                    }
                    if (valor.compareTo(BigDecimal.ZERO) < 0) {
                        return new Resposta("O valor mensal não pode ser menor que zero!");
                    }

                    this.usuarioRepository.atualizaAporte(chatId, valor);
                    this.usuarioRepository.atualizaEstado(chatId, EstadoConversa.AGUARDANDO_META);
                    return new Resposta("Aporte atualizado: " + FormatoMoeda.formatar(valor)
                            + ". Agora, qual o novo valor da meta? Digite 'Cancelar' para desistir.");
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
