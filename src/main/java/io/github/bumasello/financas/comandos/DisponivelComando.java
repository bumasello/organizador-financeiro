package io.github.bumasello.financas.comandos;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Optional;

import io.github.bumasello.financas.domain.ResumoMes;
import io.github.bumasello.financas.service.DisponivelService;

/**
 * DisponivelComando
 */
public class DisponivelComando implements Comando {

    private final DisponivelService disponivelService;

    public DisponivelComando(DisponivelService disponivelService) {
        this.disponivelService = disponivelService;
    }

    @Override
    public Resposta executar(long chatId, String texto) {
        YearMonth mesAtual = YearMonth.now(ZoneId.of("America/Sao_Paulo"));

        try {
            Optional<ResumoMes> optionalResumoMes = this.disponivelService.calcular(chatId, mesAtual);

            if (optionalResumoMes.isPresent()) {
                ResumoMes resumoMes = optionalResumoMes.get();

                BigDecimal disponivel = resumoMes.disponivel();

                String respostaTexto = "Disponível em "
                        + mesAtual.getMonth().getDisplayName(TextStyle.FULL, FormatoMoeda.LOCALE) + ": "
                        + FormatoMoeda.formatar(disponivel);

                return new Resposta(respostaTexto);
            } else {
                return new Resposta("Preencha seu perfil primeiro");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new Resposta("Não consegui consultar agora. Tente novamente mais tarde!");
        }

    }
}
