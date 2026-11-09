package io.github.bumasello.financas.telegram;

import java.util.List;

import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class FinancasBot implements LongPollingUpdateConsumer {
    private final TelegramClient telegramClient;
    private final long chatIdPermitido;

    public FinancasBot(TelegramClient client, long chatId) {
        this.telegramClient = client;
        this.chatIdPermitido = chatId;

    }

    @Override
    public void consume(List<Update> updates) {
        for (Update update : updates) {
            processar(update);
        }
    }

    private void processar(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            System.out.println(update.getMessage().getText());
            System.out.println(update.getMessage().getChatId());

            if (update.getMessage().getChatId() != this.chatIdPermitido) {
                // Ideal seria um print no console, mas deixei para eu ver funcionando
                // sendMessage = new
                // SendMessage(String.valueOf(update.getMessage().getChatId()),
                // "ChatId não está na Whitelist");
                //
                // try {
                // telegramClient.execute(sendMessage);
                // } catch (TelegramApiException e) {
                // e.printStackTrace();
                // }
                System.out.println("Chat Id ignorado: " + update.getMessage().getChatId());

                return;
            }

            SendMessage sendMessage = new SendMessage(String.valueOf(update.getMessage().getChatId()),
                    update.getMessage().getText());

            try {
                telegramClient.execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
