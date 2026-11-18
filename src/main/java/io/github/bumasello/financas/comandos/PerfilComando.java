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
    public Resposta executar(long chatId, String texto) {
        BigDecimal renda;
        String[] partes = texto.strip().split(" ", 2);

        if (partes.length == 1) {
            return new Resposta("Use: /perfil 2.500,00");
        }

        String valorRenda = partes[1];
        try {
            renda = FormatoMoeda.ler(valorRenda);

            if (renda.compareTo(BigDecimal.ZERO) < 0) {
                return new Resposta("A renda não pode ser negativa.");
            }

        } catch (NumberFormatException e) {
            return new Resposta("Não entendi o valor");
        }

        try {
            this.usuarioRepository.atualizaRenda(chatId, renda);
            String respostaTexto = "Renda atualizada: " + FormatoMoeda.formatar(renda);
            return new Resposta(respostaTexto);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Resposta("Não consegui atualizar a sua renda no momento. Tente novamente mais tarde!");
        }
    }
}
