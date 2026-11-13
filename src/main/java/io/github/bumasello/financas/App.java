package io.github.bumasello.financas;

import java.sql.SQLException;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import io.github.bumasello.financas.config.Settings;
import io.github.bumasello.financas.repo.Database;
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

        bot.registerBot(token, new FinancasBot(client, lchatId));

    }
}
