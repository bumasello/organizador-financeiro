package io.github.bumasello.financas;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import io.github.bumasello.financas.telegram.FinancasBot;

public class App {
    public static void main(String[] args) throws TelegramApiException {
        String token = System.getenv("TELEGRAM_KEY");

        if (token == null || token.isBlank()) {
            throw new IllegalStateException("O token TELEGRAM_KEY não pode estar vazio");
        }

        System.out.println("Token carregado");

        TelegramClient client = new OkHttpTelegramClient(token);

        TelegramBotsLongPollingApplication bot = new TelegramBotsLongPollingApplication();

        bot.registerBot(token, new FinancasBot(client));
    }
}
