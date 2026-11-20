package io.github.bumasello.financas.conversa;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.github.bumasello.financas.comandos.FormatoMoeda;
import io.github.bumasello.financas.comandos.Resposta;
import io.github.bumasello.financas.domain.CalculadoraFatura;
import io.github.bumasello.financas.domain.Compra;
import io.github.bumasello.financas.domain.EstadoConversa;
import io.github.bumasello.financas.domain.MeioPagamento;
import io.github.bumasello.financas.domain.Parcela;
import io.github.bumasello.financas.domain.TipoPagamento;
import io.github.bumasello.financas.domain.Usuario;
import io.github.bumasello.financas.repo.CompraRepository;
import io.github.bumasello.financas.repo.MeioPagamentoRepository;
import io.github.bumasello.financas.repo.UsuarioRepository;

/**
 * ConversaGasto
 */
public class ConversaGasto extends ConversaComRascunho<RascunhoGasto> {
    private static final ZoneId FUSO_SAO_PAULO = ZoneId.of("America/Sao_Paulo");
    private static final List<String> CATEGORIAS = List.of("Alimentação", "Transporte", "Lazer", "Moradia", "Saúde",
            "Compras", "Outros");
    private static final DateTimeFormatter FORMATADOR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final MeioPagamentoRepository meioPagamentoRepository;
    private final CompraRepository compraRepository;

    public ConversaGasto(UsuarioRepository usuarioRepository, MeioPagamentoRepository meioPagamentoRepository,
            CompraRepository compraRepository) {
        super(usuarioRepository, RascunhoGasto.class);
        this.meioPagamentoRepository = meioPagamentoRepository;
        this.compraRepository = compraRepository;
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
                case GASTO_DESCRICAO -> {
                    String descricaoGasto = texto.strip();

                    RascunhoGasto rascunhoGasto = new RascunhoGasto(descricaoGasto, null, null, null, null);
                    try {

                        this.gravarRascunho(chatId, rascunhoGasto);
                        this.usuarioRepository.atualizaEstado(chatId, EstadoConversa.GASTO_VALOR);
                        return new Resposta("Qual o valor?");
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                        return new Resposta("Não consegui entender o que foi digitado. Pode digitar novamente?");
                    }
                }
                case GASTO_VALOR -> {
                    String valorString = texto.strip();

                    try {
                        BigDecimal valor = FormatoMoeda.ler(valorString);

                        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                            return new Resposta("Digite apenas valores positivos.");
                        }

                        try {

                            Optional<RascunhoGasto> optionalRascunho = this.lerRascunho(chatId);

                            if (optionalRascunho.isEmpty()) {
                                this.usuarioRepository.atualizaEstado(chatId, EstadoConversa.OCIOSO);

                                String resposta = """
                                            Digite /gasto para registrar um novo gasto.
                                        """;
                                return new Resposta(
                                        resposta);
                            }

                            RascunhoGasto rascunhoGasto = optionalRascunho.get();

                            RascunhoGasto novoRascunho = new RascunhoGasto(rascunhoGasto.descricao(), valor, null, null,
                                    null);

                            this.gravarRascunho(chatId, novoRascunho);
                            this.usuarioRepository.atualizaEstado(chatId, EstadoConversa.GASTO_DATA_HOJE);

                            return new Resposta("Fez a compra hoje?", List.of("Sim", "Não"));
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                            return new Resposta("Não consegui entender o que foi digitado. Pode digitar novamente?");
                        }

                    } catch (NumberFormatException e) {
                        return new Resposta(
                                "Digite apenas valores em formato de moeda. Exemplo: 'R$ 1.200,00', '1.200,00', '1200,00', '1200', etc...");
                    }
                }
                case GASTO_DATA_HOJE -> {
                    switch (texto.strip().toLowerCase()) {
                        case "sim" -> {
                            try {
                                Optional<RascunhoGasto> optionalRascunho = this.lerRascunho(chatId);

                                if (optionalRascunho.isEmpty()) {
                                    this.usuarioRepository.atualizaEstado(chatId, EstadoConversa.OCIOSO);

                                    String resposta = """
                                                Digite /gasto para registrar um novo gasto.
                                            """;
                                    return new Resposta(
                                            resposta);
                                }

                                RascunhoGasto rascunhoGasto = optionalRascunho.get();

                                RascunhoGasto novoRascunhoGasto = new RascunhoGasto(rascunhoGasto.descricao(),
                                        rascunhoGasto.valor(), LocalDate.now(FUSO_SAO_PAULO), null, null);

                                this.gravarRascunho(chatId, novoRascunhoGasto);
                                this.usuarioRepository.atualizaEstado(chatId, EstadoConversa.GASTO_CATEGORIA);

                                return new Resposta("Qual a categoria do gasto?", CATEGORIAS);
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                                return new Resposta(
                                        "Não consegui entender o que foi digitado. Pode digitar novamente?");
                            }

                        }
                        case "não" -> {
                            this.usuarioRepository.atualizaEstado(chatId, EstadoConversa.GASTO_DATA);
                            return new Resposta("Qual a data do gasto? (dd/mm/aaaa)");
                        }
                        default -> {
                            return new Resposta("Não entendi. Pode digitar novamente?");
                        }
                    }
                }
                case GASTO_DATA -> {
                    String diaString = texto.strip();

                    try {
                        LocalDate data = LocalDate.parse(diaString, FORMATADOR);
                        try {
                            Optional<RascunhoGasto> optionalRascunho = this.lerRascunho(chatId);

                            if (optionalRascunho.isEmpty()) {
                                this.usuarioRepository.atualizaEstado(chatId, EstadoConversa.OCIOSO);
                                String resposta = """
                                            Digite /gasto para registrar um novo gasto.
                                        """;
                                return new Resposta(
                                        resposta);

                            }

                            RascunhoGasto rascunhoGasto = optionalRascunho.get();

                            RascunhoGasto novoRascunhoGasto = new RascunhoGasto(
                                    rascunhoGasto.descricao(),
                                    rascunhoGasto.valor(),
                                    data,
                                    null,
                                    null);

                            this.gravarRascunho(chatId, novoRascunhoGasto);
                            this.usuarioRepository.atualizaEstado(chatId, EstadoConversa.GASTO_CATEGORIA);
                            return new Resposta("Qual a categoria do gasto?", CATEGORIAS);
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                            return new Resposta(
                                    "Não consegui entender o que foi digitado. Pode digitar novamente?");
                        }

                    } catch (DateTimeException e) {
                        e.printStackTrace();
                        return new Resposta(
                                "Não consegui entender a data digitada. Pode digitar novamente? Exemplo: 19/11/2026");
                    }

                }
                case GASTO_CATEGORIA -> {
                    String categoria = texto.strip();

                    Optional<String> categoriaVerificada = CATEGORIAS.stream()
                            .filter(m -> m.equalsIgnoreCase(categoria)).findFirst();

                    if (categoriaVerificada == null) {
                        return new Resposta(
                                "Por favor, selecione uma categoria válida.", CATEGORIAS);
                    }

                    try {

                        Optional<RascunhoGasto> optionalRascunho = this.lerRascunho(chatId);

                        if (optionalRascunho.isEmpty()) {
                            this.usuarioRepository.atualizaEstado(chatId, EstadoConversa.OCIOSO);
                            String resposta = """
                                    Digite /gasto para registrar um novo gasto.
                                    """;
                            return new Resposta(
                                    resposta);

                        }

                        RascunhoGasto rascunhoGasto = optionalRascunho.get();

                        RascunhoGasto novoRascunhoGasto = new RascunhoGasto(
                                rascunhoGasto.descricao(),
                                rascunhoGasto.valor(),
                                rascunhoGasto.data(),
                                categoria,
                                null);

                        this.gravarRascunho(chatId, novoRascunhoGasto);
                        this.usuarioRepository.atualizaEstado(chatId, EstadoConversa.GASTO_MEIO);

                        Optional<Usuario> optionalUsuario = this.usuarioRepository.buscaPorChatId(chatId);

                        if (optionalUsuario.isEmpty()) {
                            this.usuarioRepository.atualizaEstado(chatId, EstadoConversa.OCIOSO);
                            String resposta = """
                                    Digite /start para registrar um novo usuário.
                                    """;
                            return new Resposta(
                                    resposta);

                        }

                        Usuario usuario = optionalUsuario.get();

                        List<MeioPagamento> meios = this.meioPagamentoRepository.buscaPorUsuario(usuario.id());

                        List<String> nomes = meios.stream().map(MeioPagamento::nome).toList();

                        if (nomes.isEmpty()) {
                            this.usuarioRepository.limpaContextoConversa(chatId);
                            this.usuarioRepository.atualizaEstado(chatId, EstadoConversa.OCIOSO);
                            return new Resposta(
                                    "Você ainda não tem cartões cadastrados. Cadastre um com /cartao e depois registre o gasto");
                        }

                        return new Resposta("Qual foi o método de pagamento?", nomes);

                    } catch (JsonProcessingException e) {
                        return new Resposta(
                                "Não consegui entender a data digitada. Pode selecionar novamente?");
                    }
                }
                case GASTO_MEIO -> {
                    String valorMeio = texto.strip().toLowerCase();

                    Optional<Usuario> optionalUsuario = this.usuarioRepository.buscaPorChatId(chatId);

                    if (optionalUsuario.isEmpty()) {
                        this.usuarioRepository.atualizaEstado(chatId, EstadoConversa.OCIOSO);
                        String resposta = """
                                Digite /start para registrar um novo usuário.
                                """;
                        return new Resposta(
                                resposta);

                    }

                    Usuario usuario = optionalUsuario.get();

                    List<MeioPagamento> meios = this.meioPagamentoRepository.buscaPorUsuario(usuario.id());

                    List<String> nomes = meios.stream().map(MeioPagamento::nome).toList();

                    Optional<MeioPagamento> optionalMeio = meios.stream()
                            .filter(m -> m.nome().equalsIgnoreCase(valorMeio))
                            .findFirst();

                    if (optionalMeio.isEmpty()) {
                        return new Resposta("Pode selecionar o cartão novamente?", nomes);
                    }

                    MeioPagamento meio = optionalMeio.get();

                    try {
                        Optional<RascunhoGasto> optionalRascunho = this.lerRascunho(chatId);

                        if (optionalRascunho.isEmpty()) {
                            this.usuarioRepository.atualizaEstado(chatId, EstadoConversa.OCIOSO);
                            String resposta = """
                                    Digite /gasto para registrar um novo gasto.
                                    """;
                            return new Resposta(
                                    resposta);

                        }

                        RascunhoGasto rascunhoGasto = optionalRascunho.get();

                        RascunhoGasto novoRascunhoGasto = new RascunhoGasto(
                                rascunhoGasto.descricao(),
                                rascunhoGasto.valor(),
                                rascunhoGasto.data(),
                                rascunhoGasto.categoria(),
                                meio.nome());

                        if (meio.tipo() == TipoPagamento.IMEDIATO) {
                            // não tem como não existir, pois já foi procurado anteriormente
                            Optional<Long> optionalMeioSalvo = this.meioPagamentoRepository.buscaIdPorNome(
                                    usuario.id(),
                                    meio.nome().toLowerCase(Locale.ROOT));

                            if (optionalMeioSalvo.isEmpty()) {
                                throw new IllegalStateException("Meio de pagamento não encontrado: " + meio.nome());
                            }

                            Long meioSalvo = optionalMeioSalvo.get();

                            Compra compra = new Compra(novoRascunhoGasto.data(), novoRascunhoGasto.descricao(),
                                    novoRascunhoGasto.categoria(), novoRascunhoGasto.valor(), 1);

                            List<Parcela> parcelas = CalculadoraFatura.parcelar(meio, novoRascunhoGasto.data(),
                                    novoRascunhoGasto.valor(), 1);

                            this.compraRepository.salvar(usuario.id(), meioSalvo, compra, parcelas);
                            this.usuarioRepository.atualizaEstado(chatId, EstadoConversa.OCIOSO);
                            this.usuarioRepository.limpaContextoConversa(chatId);
                            return new Resposta("Gasto registrado com sucesso!");
                        }

                        this.gravarRascunho(chatId, novoRascunhoGasto);
                        this.usuarioRepository.atualizaEstado(chatId, EstadoConversa.GASTO_PARCELAS);
                        return new Resposta(
                                "Digite apenas o número de parcelas dessa compra. Exemplo: 1, 3, 6, 10, 12, etc..");

                    } catch (JsonProcessingException e) {
                        return new Resposta(
                                "Não consegui entender o valor selecionado. Pode selecionar novamente?");
                    }
                }
                case GASTO_PARCELAS -> {
                    int numeroParcelas;

                    try {
                        numeroParcelas = Integer.parseInt(texto.strip());
                    } catch (NumberFormatException e) {
                        return new Resposta("Por favor, digite apenas valores númericos, sem letras.");
                    }

                    if (numeroParcelas < 1) {
                        return new Resposta(
                                "Por favor, digite um número positivo.");
                    }

                    try {
                        Optional<Usuario> optionalUsuario = this.usuarioRepository.buscaPorChatId(chatId);

                        if (optionalUsuario.isEmpty()) {
                            this.usuarioRepository.atualizaEstado(chatId, EstadoConversa.OCIOSO);
                            String resposta = """
                                    Digite /start para registrar um novo usuário.
                                    """;
                            return new Resposta(
                                    resposta);

                        }

                        Usuario usuario = optionalUsuario.get();

                        List<MeioPagamento> meios = this.meioPagamentoRepository.buscaPorUsuario(usuario.id());

                        List<String> nomes = meios.stream().map(MeioPagamento::nome).toList();

                        Optional<RascunhoGasto> optionalRascunho = this.lerRascunho(chatId);

                        if (optionalRascunho.isEmpty()) {
                            this.usuarioRepository.atualizaEstado(chatId, EstadoConversa.OCIOSO);
                            String resposta = """
                                    Digite /gasto para registrar um novo gasto.
                                    """;
                            return new Resposta(
                                    resposta);

                        }

                        RascunhoGasto rascunhoGasto = optionalRascunho.get();

                        Optional<MeioPagamento> optionalMeio = meios.stream()
                                .filter(m -> m.nome().equalsIgnoreCase(rascunhoGasto.nomeMeio()))
                                .findFirst();

                        if (optionalMeio.isEmpty()) {
                            return new Resposta("Pode selecionar o cartão novamente?", nomes);
                        }

                        MeioPagamento meio = optionalMeio.get();

                        Optional<Long> optionalMeioSalvo = this.meioPagamentoRepository.buscaIdPorNome(
                                usuario.id(),
                                meio.nome().toLowerCase(Locale.ROOT));

                        if (optionalMeioSalvo.isEmpty()) {
                            throw new IllegalStateException("Meio de pagamento não encontrado: " + meio.nome());
                        }

                        Long meioSalvo = optionalMeioSalvo.get();

                        Compra compra = new Compra(rascunhoGasto.data(), rascunhoGasto.descricao(),
                                rascunhoGasto.categoria(), rascunhoGasto.valor(), numeroParcelas);

                        List<Parcela> parcelas = CalculadoraFatura.parcelar(meio, rascunhoGasto.data(),
                                rascunhoGasto.valor(), numeroParcelas);

                        this.compraRepository.salvar(usuario.id(), meioSalvo, compra, parcelas);
                        this.usuarioRepository.atualizaEstado(chatId, EstadoConversa.OCIOSO);
                        this.usuarioRepository.limpaContextoConversa(chatId);
                        return new Resposta("Gasto registrado com sucesso!");
                    } catch (JsonProcessingException e) {
                        return new Resposta(
                                "Não consegui entender o valor digitado. Pode digitar novamente?");
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

}
