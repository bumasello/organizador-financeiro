package io.github.bumasello.financas.telegram;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import io.github.bumasello.financas.comandos.Comando;
import io.github.bumasello.financas.domain.Situacao;
import io.github.bumasello.financas.domain.Usuario;
import io.github.bumasello.financas.repo.UsuarioRepository;

public class FinancasBot implements LongPollingUpdateConsumer {
    private final TelegramClient telegramClient;
    private final UsuarioRepository usuarioRepository;
    private final Map<String, Comando> comandos;

    public FinancasBot(TelegramClient client, UsuarioRepository usuarioRepository, Map<String, Comando> comandos) {
        this.telegramClient = client;
        this.usuarioRepository = usuarioRepository;
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
            Comando comando = this.comandos.get(comandoDigitado);
            long chatId = update.getMessage().getChatId();

            if (comando == null) {
                this.mandaMensagem(chatId, "Digite um comando válido. /ajuda para listar os comandos");
                return;
            }

            if (comandoDigitado.equals("/start")) {
                String message = comando.executar(chatId, text).texto();
                this.mandaMensagem(chatId, message);
                return;
            }

            try {
                Optional<Usuario> optionalUsuario = this.usuarioRepository.buscaPorChatId(chatId);

                if (optionalUsuario.isEmpty()) {
                    this.mandaMensagem(chatId, "Digite /start para solicitar acesso");
                    return;
                }

                Usuario usuario = optionalUsuario.get();

                if (usuario.situacao() != Situacao.APROVADO) {
                    if (usuario.situacao() == Situacao.PENDENTE) {
                        this.mandaMensagem(chatId, "Seu acesso ainda não foi liberado.");
                        return;
                    }
                    if (usuario.situacao() == Situacao.BLOQUEADO) {
                        this.mandaMensagem(chatId, "Seu acesso está bloqueado.");
                        return;
                    }

                    this.mandaMensagem(chatId, "Você não tem acesso a este comando.");
                    return;
                }

                String message = comando.executar(chatId, text).texto();
                this.mandaMensagem(chatId, message);
                return;

            } catch (SQLException e) {
                e.printStackTrace();
                this.mandaMensagem(chatId,
                        "Não consegui verificar sua situação no momento. Tente novamente mais tarde!");
                return;
            }
        }
    }
}
