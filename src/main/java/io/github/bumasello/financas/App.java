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

        long lchatId = Settings.getChatId();

        Database database = new Database(Settings.getDbUrl(), Settings.getDbUser(), Settings.getDbPass());

        String schema = database.schemaAtual();

        System.out.printf("Database conectado! Schema: %s%n", schema);

        TelegramClient client = new OkHttpTelegramClient(token);

        TelegramBotsLongPollingApplication bot = new TelegramBotsLongPollingApplication();

        DisponivelService disponivelService = new DisponivelService(new UsuarioRepository(database),
                new CompraRepository(database), new AjusteRepository(database));

        Map<String, Comando> commands = Map.of(
                "/disponivel", new DisponivelComando(disponivelService),
                "/ajuda", new AjudaComando());

        bot.registerBot(token, new FinancasBot(client, lchatId, commands));
    }
}
