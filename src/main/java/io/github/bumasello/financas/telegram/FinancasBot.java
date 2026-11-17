package io.github.bumasello.financas.telegram;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import io.github.bumasello.financas.comandos.Comando;

public class FinancasBot implements LongPollingUpdateConsumer {
    private final TelegramClient telegramClient;
    private final long chatIdPermitido;
    private final Map<String, Comando> comandos;

    public FinancasBot(TelegramClient client, long chatId, Map<String, Comando> comandos) {
        this.telegramClient = client;
        this.chatIdPermitido = chatId;
        this.comandos = comandos;

    }

    @Override
    public void consume(List<Update> updates) {
        for (Update update : updates) {
            processar(update);
        }
    }

    private void mandaMensagem(long chatId, String msg) {
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId),
                msg);

        try {
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void processar(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();

            String comandoDigitado = text.strip().split(" ")[0].toLowerCase(Locale.ROOT);
            long chatId = update.getMessage().getChatId();

            System.out.println(comandoDigitado);

            if (chatId != this.chatIdPermitido) {
                System.out.println("Chat Id ignorado: " + chatId);

                return;
            }

            Comando comando = this.comandos.get(comandoDigitado);

            if (comando != null) {
                String message = comando.executar(chatId, text);
                this.mandaMensagem(chatId, message);
                return;
            }

            this.mandaMensagem(chatId,
                    text);
        }
    }
}
