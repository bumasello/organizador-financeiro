package io.github.bumasello.financas;

import java.sql.SQLException;
import java.util.Map;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import io.github.bumasello.financas.comandos.AjudaComando;
import io.github.bumasello.financas.comandos.Comando;
import io.github.bumasello.financas.comandos.DisponivelComando;
import io.github.bumasello.financas.comandos.ObjetivoComando;
import io.github.bumasello.financas.comandos.PerfilComando;
import io.github.bumasello.financas.comandos.StartComando;
import io.github.bumasello.financas.config.Settings;
import io.github.bumasello.financas.repo.AjusteRepository;
import io.github.bumasello.financas.repo.CompraRepository;
import io.github.bumasello.financas.repo.Database;
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

        DisponivelService disponivelService = new DisponivelService(usuarioRepository,
                new CompraRepository(database), new AjusteRepository(database));

        Map<String, Comando> commands = Map.of(
                "/disponivel", new DisponivelComando(disponivelService),
                "/perfil", new PerfilComando(new UsuarioRepository(database)), "/objetivo",
                new ObjetivoComando(usuarioRepository),
                "/start",
                new StartComando(new UsuarioRepository(database)),
                "/ajuda", new AjudaComando());

        bot.registerBot(token, new FinancasBot(client, new UsuarioRepository(database), commands));
    }
}
