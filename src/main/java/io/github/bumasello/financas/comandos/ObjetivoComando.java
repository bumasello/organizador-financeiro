package io.github.bumasello.financas.comandos;

import java.math.BigDecimal;
import java.sql.SQLException;

import io.github.bumasello.financas.repo.UsuarioRepository;

/**
 * ObjetivoComando
 */
public class ObjetivoComando implements Comando {

    private final UsuarioRepository usuarioRepository;

    public ObjetivoComando(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public String executar(long chatId, String texto) {
        String[] partes = texto.strip().split(" ");

        if (partes.length < 3 || partes.length > 3) {
            return "Use: /objetivo valor1 valor2";
        }

        try {
            BigDecimal aporte = FormatoMoeda.ler(partes[1]);
            BigDecimal metaReserva = FormatoMoeda.ler(partes[2]);

            if (aporte.compareTo(BigDecimal.ZERO) < 0) {
                return "O aporte não pode ser negativo.";
            }

            if (metaReserva.compareTo(BigDecimal.ZERO) < 0) {
                return "A meta reserva não pode ser negativa.";
            }

            try {
                this.usuarioRepository.atualizaAporte(chatId, aporte);
                this.usuarioRepository.atualizaMetaReserva(chatId, metaReserva);

                return "Aporte atualizado: " + FormatoMoeda.formatar(aporte) + " e Meta Reserva atualizada: "
                        + FormatoMoeda.formatar(metaReserva);
            } catch (SQLException e) {
                e.printStackTrace();
                return "Não consegui atualizar a seus dados no momento. Tente novamente mais tarde!";
            }
        } catch (NumberFormatException e) {
            return "Não entendi um desses valores: " + partes[1] + " e " + partes[2];
        }
    }

}
