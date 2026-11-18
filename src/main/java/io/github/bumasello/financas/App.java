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
import io.github.bumasello.financas.comandos.ObjetivoComando;
import io.github.bumasello.financas.comandos.PerfilComando;
import io.github.bumasello.financas.comandos.StartComando;
import io.github.bumasello.financas.config.Settings;
import io.github.bumasello.financas.conversa.Conversa;
import io.github.bumasello.financas.conversa.ConversaCartao;
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

        DisponivelService disponivelService = new DisponivelService(usuarioRepository,
                new CompraRepository(database), new AjusteRepository(database));

        Map<String, Comando> comandos = Map.of(
                "/disponivel", new DisponivelComando(disponivelService),
                "/perfil", new PerfilComando(usuarioRepository), "/objetivo",
                new ObjetivoComando(usuarioRepository),
                "/start",
                new StartComando(usuarioRepository),
                "/cartao", new CartaoComando(usuarioRepository),
                "/ajuda", new AjudaComando());

        ConversaObjetivo conversaObjetivo = new ConversaObjetivo(usuarioRepository);
        ConversaCartao conversaCartao = new ConversaCartao(meioPagamentoRepository, usuarioRepository);

        Map<EstadoConversa, Conversa> conversas = Map.of(
                EstadoConversa.ESCOLHENDO_OBJETIVO, conversaObjetivo,
                EstadoConversa.AGUARDANDO_APORTE, conversaObjetivo,
                EstadoConversa.AGUARDANDO_META, conversaObjetivo,
                EstadoConversa.AGUARDANDO_APORTE_DOS_DOIS, conversaObjetivo,
                EstadoConversa.ESCOLHENDO_CARTAO, conversaCartao,
                EstadoConversa.ESCOLHENDO_TIPO_PAGAMENTO, conversaCartao,
                EstadoConversa.ESCOLHENDO_DIA_FECHAMENTO, conversaCartao,
                EstadoConversa.ESCOLHENDO_DIA_VENCIMENTO, conversaCartao);

        bot.registerBot(token,
                new FinancasBot(client, usuarioRepository, comandos,
                        conversas));
    }
}
