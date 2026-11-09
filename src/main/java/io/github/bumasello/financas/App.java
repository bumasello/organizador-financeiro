package io.github.bumasello.financas;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import io.github.bumasello.financas.telegram.FinancasBot;

public class App {
    public static void main(String[] args) throws TelegramApiException {
        String token = System.getenv("TELEGRAM_KEY");
        String chatId = System.getenv("CHAT_ID");

        if (token == null || token.isBlank()) {
            throw new IllegalStateException("O token TELEGRAM_KEY não pode estar vazio");
        } else {

            System.out.println("Token carregado");
        }

        if (chatId == null || chatId.isBlank()) {
            throw new IllegalStateException("O CHAT_ID não pode estar vazio");
        } else {
            System.out.println("CHAT_ID carregado");
        }

        long lchatId = Long.parseLong(chatId);

        TelegramClient client = new OkHttpTelegramClient(token);

        TelegramBotsLongPollingApplication bot = new TelegramBotsLongPollingApplication();

        bot.registerBot(token, new FinancasBot(client, lchatId));

    }
}
