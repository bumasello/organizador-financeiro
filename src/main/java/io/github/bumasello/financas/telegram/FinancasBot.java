package io.github.bumasello.financas.telegram;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import io.github.bumasello.financas.comandos.Comando;
import io.github.bumasello.financas.domain.ResumoMes;
import io.github.bumasello.financas.service.DisponivelService;

public class FinancasBot implements LongPollingUpdateConsumer {
    private final TelegramClient telegramClient;
    private final long chatIdPermitido;
    private final Comando comando;

    public FinancasBot(TelegramClient client, long chatId, Comando comando) {
        this.telegramClient = client;
        this.chatIdPermitido = chatId;
        this.comando = comando;

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
            long chatId = update.getMessage().getChatId();

            if (chatId != this.chatIdPermitido) {
                System.out.println("Chat Id ignorado: " + chatId);

                return;
            }

            if (text.equals("/disponivel")) {
                String message = this.comando.executar(chatId, text);
                this.mandaMensagem(chatId, message);
                return;
            }

            this.mandaMensagem(chatId,
                    text);
        }
    }
}
