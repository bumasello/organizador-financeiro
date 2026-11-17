package io.github.bumasello.financas.comandos;

import java.math.BigDecimal;
import java.sql.SQLException;

import io.github.bumasello.financas.repo.UsuarioRepository;

/**
 * PerfilComando
 */
public class PerfilComando implements Comando {
    private final UsuarioRepository usuarioRepository;

    public PerfilComando(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public String executar(long chatId, String texto) {
        BigDecimal renda;
        String[] partes = texto.strip().split(" ", 2);

        if (partes.length == 1) {
            return "Use: /perfil 2.500,00";
        }

        String valorRenda = partes[1];
        try {
            renda = FormatoMoeda.ler(valorRenda);

            if (renda.compareTo(BigDecimal.ZERO) < 0) {
                return "A renda não pode ser negativa.";
            }

        } catch (NumberFormatException e) {
            return "Não entendi o valor";
        }

        try {
            this.usuarioRepository.atualizaRenda(chatId, renda);
            return "Renda atualizada: " + FormatoMoeda.formatar(renda);
        } catch (SQLException e) {
            e.printStackTrace();
            return "Não consegui atualizar a sua renda no momento. Tente novamente mais tarde!";
        }
    }
}
