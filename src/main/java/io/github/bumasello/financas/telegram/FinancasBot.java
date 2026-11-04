package io.github.bumasello.financas.telegram;

import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class FinancasBot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;

    public FinancasBot(TelegramClient client) {
        this.telegramClient = client;
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            System.out.println(update.getMessage().getText());
            System.out.println(update.getMessage().getChatId());

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
