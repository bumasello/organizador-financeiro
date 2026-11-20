package io.github.bumasello.financas;

import java.sql.SQLException;
import java.util.Map;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import io.github.bumasello.financas.comandos.AjudaComando;
import io.github.bumasello.financas.comandos.CartaoComando;
import io.github.bumasello.financas.comandos.Comando;
import io.github.bumasello.financas.comandos.DisponivelComando;
import io.github.bumasello.financas.comandos.GastoComando;
import io.github.bumasello.financas.comandos.ObjetivoComando;
import io.github.bumasello.financas.comandos.PerfilComando;
import io.github.bumasello.financas.comandos.StartComando;
import io.github.bumasello.financas.config.Settings;
import io.github.bumasello.financas.conversa.Conversa;
import io.github.bumasello.financas.conversa.ConversaCartao;
import io.github.bumasello.financas.conversa.ConversaGasto;
import io.github.bumasello.financas.conversa.ConversaObjetivo;
import io.github.bumasello.financas.domain.EstadoConversa;
import io.github.bumasello.financas.repo.AjusteRepository;
import io.github.bumasello.financas.repo.CompraRepository;
import io.github.bumasello.financas.repo.Database;
import io.github.bumasello.financas.repo.MeioPagamentoRepository;
import io.github.bumasello.financas.repo.UsuarioRepository;
import io.github.bumasello.financas.service.DisponivelService;
import io.github.bumasello.financas.telegram.FinancasBot;

public class App {
    public static void main(String[] args) throws TelegramApiException, SQLException {
        String token = Settings.getTelegramKey();

        Database database = new Database(Settings.getDbUrl(), Settings.getDbUser(), Settings.getDbPass());

        String schema = database.schemaAtual();

        System.out.printf("Database conectado! Schema: %s%n", schema);

        TelegramClient client = new OkHttpTelegramClient(token);

        TelegramBotsLongPollingApplication bot = new TelegramBotsLongPollingApplication();
        UsuarioRepository usuarioRepository = new UsuarioRepository(database);
        MeioPagamentoRepository meioPagamentoRepository = new MeioPagamentoRepository(database);
        CompraRepository compraRepository = new CompraRepository(database);

        DisponivelService disponivelService = new DisponivelService(usuarioRepository,
                new CompraRepository(database), new AjusteRepository(database));

        Map<String, Comando> comandos = Map.of(
                "/disponivel", new DisponivelComando(disponivelService),
                "/perfil", new PerfilComando(usuarioRepository), "/objetivo",
                new ObjetivoComando(usuarioRepository),
                "/start",
                new StartComando(usuarioRepository),
                "/gasto", new GastoComando(usuarioRepository),
                "/cartao", new CartaoComando(usuarioRepository),
                "/ajuda", new AjudaComando());

        ConversaObjetivo conversaObjetivo = new ConversaObjetivo(usuarioRepository);
        ConversaCartao conversaCartao = new ConversaCartao(meioPagamentoRepository, usuarioRepository);
        ConversaGasto conversaGasto = new ConversaGasto(usuarioRepository, meioPagamentoRepository, compraRepository);

        Map<EstadoConversa, Conversa> conversas = Map.ofEntries(
                Map.entry(EstadoConversa.ESCOLHENDO_OBJETIVO, conversaObjetivo),
                Map.entry(EstadoConversa.AGUARDANDO_APORTE, conversaObjetivo),
                Map.entry(EstadoConversa.AGUARDANDO_META, conversaObjetivo),
                Map.entry(EstadoConversa.AGUARDANDO_APORTE_DOS_DOIS, conversaObjetivo),
                Map.entry(EstadoConversa.ESCOLHENDO_CARTAO, conversaCartao),
                Map.entry(EstadoConversa.ESCOLHENDO_TIPO_PAGAMENTO, conversaCartao),
                Map.entry(EstadoConversa.ESCOLHENDO_DIA_FECHAMENTO, conversaCartao),
                Map.entry(EstadoConversa.ESCOLHENDO_DIA_VENCIMENTO, conversaCartao),
                Map.entry(EstadoConversa.GASTO_DESCRICAO, conversaGasto),
                Map.entry(EstadoConversa.GASTO_DATA, conversaGasto),
                Map.entry(EstadoConversa.GASTO_MEIO, conversaGasto),
                Map.entry(EstadoConversa.GASTO_VALOR, conversaGasto),
                Map.entry(EstadoConversa.GASTO_PARCELAS, conversaGasto),
                Map.entry(EstadoConversa.GASTO_CATEGORIA, conversaGasto),
                Map.entry(EstadoConversa.GASTO_DATA_HOJE, conversaGasto));

        bot.registerBot(token,
                new FinancasBot(client, usuarioRepository, comandos,
                        conversas));
    }
}
