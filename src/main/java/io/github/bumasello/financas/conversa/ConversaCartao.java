package io.github.bumasello.financas.conversa;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bumasello.financas.comandos.Resposta;
import io.github.bumasello.financas.domain.EstadoConversa;
import io.github.bumasello.financas.domain.MeioPagamento;
import io.github.bumasello.financas.domain.TipoPagamento;
import io.github.bumasello.financas.domain.Usuario;
import io.github.bumasello.financas.repo.MeioPagamentoRepository;
import io.github.bumasello.financas.repo.UsuarioRepository;

/**
 * ConversaCartao
 */
public class ConversaCartao implements Conversa {
    private final MeioPagamentoRepository meioPagamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public ConversaCartao(MeioPagamentoRepository meioPagamentoRepository, UsuarioRepository usuarioRepository) {
        this.meioPagamentoRepository = meioPagamentoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
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
                case ESCOLHENDO_CARTAO -> {
                    try {
                        String nomeCartao = texto.strip();

                        try {
                            RascunhoCartao rascunhoCartao = new RascunhoCartao(nomeCartao, null, null, null);
                            this.gravarRascunho(chatId, rascunhoCartao);

                            this.usuarioRepository.atualizaEstado(chatId, EstadoConversa.ESCOLHENDO_TIPO_PAGAMENTO);
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                            return new Resposta("Não consegui entender o que foi digitado. Pode digitar novamente?");
                        }

                        String resposta = """
                                Digite agora a forma de pagamento do cartão: CREDITO ou IMEDIATO
                                        CREDITO - Pagamento para faturas próximas
                                        IMEDIATO - Pagamento no Débito, PIX, etc...
                                        """;

                        return new Resposta(resposta, List.of("CREDITO", "IMEDIATO"));
                    } catch (NumberFormatException e) {
                        return new Resposta("Não entendi o valor.");
                    }
                }
                case ESCOLHENDO_TIPO_PAGAMENTO -> {
                    switch (texto.strip().toLowerCase()) {
                        case "credito" -> {
                            try {
                                Optional<RascunhoCartao> optionalRascunho = this.lerRascunho(chatId);

                                if (optionalRascunho.isEmpty()) {
                                    this.usuarioRepository.atualizaEstado(chatId, EstadoConversa.OCIOSO);
                                    String resposta = """
                                                Não encontramos cartão cadastrado.
                                                Digite /cartao para cadastrar um novo cartão.
                                            """;
                                    return new Resposta(
                                            resposta);

                                }

                                RascunhoCartao rascunho = optionalRascunho.get();
                                RascunhoCartao novoRascunho = new RascunhoCartao(rascunho.nome(), TipoPagamento.CREDITO,
                                        null, null);

                                this.gravarRascunho(chatId, novoRascunho);
                                this.usuarioRepository.atualizaEstado(chatId, EstadoConversa.ESCOLHENDO_DIA_FECHAMENTO);
                                return new Resposta("Qual seria o dia de Fechamento da fatura do cartão?");
                            } catch (JsonProcessingException e) {
                                return new Resposta("Não entendi o valor.");
                            }

                        }
                        case "imediato" -> {
                            try {
                                Optional<RascunhoCartao> optionalRascunho = this.lerRascunho(chatId);

                                if (optionalRascunho.isEmpty()) {
                                    this.usuarioRepository.atualizaEstado(chatId, EstadoConversa.OCIOSO);
                                    String resposta = """
                                                Não encontramos cartão cadastrado.
                                                Digite /cartao para cadastrar um novo cartão.
                                            """;
                                    return new Resposta(
                                            resposta);

                                }

                                RascunhoCartao rascunho = optionalRascunho.get();
                                Optional<Usuario> optionalUsuario = this.usuarioRepository.buscaPorChatId(chatId);

                                if (optionalUsuario.isEmpty()) {
                                    return new Resposta("Usuário não encontrado. Digite /start para se cadastrar.");
                                }

                                MeioPagamento meio = new MeioPagamento(rascunho.nome(),
                                        TipoPagamento.IMEDIATO,
                                        0, 0);

                                Usuario usuario = optionalUsuario.get();

                                this.meioPagamentoRepository.salvar(usuario.id(), meio);

                                this.usuarioRepository.limpaContextoConversa(chatId);
                                this.usuarioRepository.atualizaEstado(chatId, EstadoConversa.OCIOSO);
                                return new Resposta("Cartão cadastrado com sucesso!");
                            } catch (JsonProcessingException e) {
                                return new Resposta("Não entendi o valor.");
                            }
                        }
                        default -> {
                            return new Resposta("Selecione uma das opções: CREDITO ou IMEDIATO");
                        }
                    }
                }
                case ESCOLHENDO_DIA_FECHAMENTO -> {
                    int dia;
                    try {
                        dia = Integer.parseInt(texto);

                        if (dia < 1 || dia > 31) {
                            return new Resposta("O dia de fechamento deve estar entre 1 e 31.");
                        }
                    } catch (NumberFormatException e) {
                        return new Resposta("Digite apenas valores numéricos entre 1 e 31.");
                    }

                    try {
                        Optional<RascunhoCartao> optionalRascunho = this.lerRascunho(chatId);

                        if (optionalRascunho.isEmpty()) {
                            this.usuarioRepository.atualizaEstado(chatId, EstadoConversa.OCIOSO);
                            String resposta = """
                                        Não encontramos cartão cadastrado.
                                        Digite /cartao para cadastrar um novo cartão.
                                    """;
                            return new Resposta(
                                    resposta);

                        }

                        RascunhoCartao rascunho = optionalRascunho.get();
                        RascunhoCartao novoRascunho = new RascunhoCartao(rascunho.nome(), rascunho.tipo(), dia,
                                null);

                        this.gravarRascunho(chatId, novoRascunho);

                        this.usuarioRepository.atualizaEstado(chatId, EstadoConversa.ESCOLHENDO_DIA_VENCIMENTO);
                        return new Resposta("Qual seria o dia de Vencimento da fatura do cartão?");
                    } catch (JsonProcessingException e) {
                        return new Resposta("Não entendi o valor.");
                    }

                }
                case ESCOLHENDO_DIA_VENCIMENTO -> {
                    int dia;
                    try {
                        dia = Integer.parseInt(texto);

                        if (dia < 1 || dia > 31) {
                            return new Resposta("O dia de vencimento deve estar entre 1 e 31.");
                        }
                    } catch (NumberFormatException e) {
                        return new Resposta("Digite apenas valores numéricos entre 1 e 31.");
                    }

                    try {
                        Optional<RascunhoCartao> optionalRascunho = this.lerRascunho(chatId);

                        if (optionalRascunho.isEmpty()) {
                            this.usuarioRepository.atualizaEstado(chatId, EstadoConversa.OCIOSO);
                            String resposta = """
                                        Não encontramos cartão cadastrado.
                                        Digite /cartao para cadastrar um novo cartão.
                                    """;
                            return new Resposta(
                                    resposta);

                        }

                        RascunhoCartao rascunhoCartao = optionalRascunho.get();

                        Optional<Usuario> optionalUsuario = this.usuarioRepository.buscaPorChatId(chatId);

                        if (optionalUsuario.isEmpty()) {
                            return new Resposta("Usuário não encontrado. Digite /start para se cadastrar.");
                        }

                        Usuario usuario = optionalUsuario.get();

                        MeioPagamento meio = new MeioPagamento(rascunhoCartao.nome(), rascunhoCartao.tipo(),
                                rascunhoCartao.diaFechamento(), dia);

                        this.meioPagamentoRepository.salvar(usuario.id(), meio);

                        this.usuarioRepository.limpaContextoConversa(chatId);
                        this.usuarioRepository.atualizaEstado(chatId, EstadoConversa.OCIOSO);
                        return new Resposta("Forma de pagamento cadastrada com sucesso!");
                    } catch (JsonProcessingException e) {
                        return new Resposta("Não entendi o valor.");
                    }

                }
                default -> {
                    return new Resposta("Não entendi. Pode digitar novamente?");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return new Resposta("Não consigo continuar agora. Tente novamente mais tarde!");
        }
    }

    private Optional<RascunhoCartao> lerRascunho(long chatId) throws JsonProcessingException, SQLException {

        Optional<String> optionalRascunho = this.usuarioRepository.buscaContextoPorChatId(chatId);

        if (optionalRascunho.isEmpty()) {
            return Optional.empty();
        }

        String rascunho = optionalRascunho.get();
        RascunhoCartao novoRascunho = MAPPER.readValue(rascunho, RascunhoCartao.class);

        return Optional.of(novoRascunho);
    }

    private void gravarRascunho(long chatId, RascunhoCartao rascunho) throws JsonProcessingException, SQLException {

        String rascunhoString = MAPPER.writeValueAsString(rascunho);
        this.usuarioRepository.atualizaContextoConversa(chatId, rascunhoString);

    }

}
