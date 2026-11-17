package io.github.bumasello.financas.repo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import com.sun.net.httpserver.Authenticator.Result;

import io.github.bumasello.financas.domain.Situacao;
import io.github.bumasello.financas.domain.Usuario;

/**
 * UsuarioRepository
 */
public class UsuarioRepository {
    private final Database database;

    public UsuarioRepository(Database database) {
        this.database = database;
    }

    public void cadastraChatId(long chatId) throws SQLException {
        if (chatId == 0) {
            throw new IllegalArgumentException("chatId não pode ser zero");
        }

        String sql = """
                INSERT INTO usuario (telegram_chat_id)
                VALUES (?)
                """;

        try (Connection conn = this.database.abrirConn(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, chatId);

            pstmt.executeUpdate();
        }
    }

    public Optional<Usuario> buscaPorChatId(long chatId) throws SQLException {
        if (chatId == 0) {
            throw new IllegalArgumentException("chatId não pode ser zero");
        }

        String sql = """
                SELECT
                  id,
                  telegram_chat_id,
                  nome,
                  situacao,
                  renda_mensal,
                  dia_salario,
                  aporte_mensal,
                  meta_reserva
                FROM usuario
                WHERE telegram_chat_id = ?
                """;
        try (Connection conn = this.database.abrirConn(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, chatId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Usuario(rs.getLong("id"), rs.getLong("telegram_chat_id"),
                            rs.getString("nome"), Situacao.valueOf(rs.getString("situacao")),
                            rs.getBigDecimal("renda_mensal"), rs.getObject("dia_salario", Integer.class),
                            rs.getBigDecimal("aporte_mensal"), rs.getBigDecimal("meta_reserva")));
                }
            }
        }

        return Optional.empty();
    }

}
