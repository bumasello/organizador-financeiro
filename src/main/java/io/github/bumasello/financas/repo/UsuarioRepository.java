package io.github.bumasello.financas.repo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import io.github.bumasello.financas.domain.EstadoConversa;
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

    public void atualizaEstado(long chatId, EstadoConversa estadoConversa) throws SQLException {
        if (chatId == 0) {
            throw new IllegalArgumentException("chatId não pode ser zero");
        }

        if (estadoConversa == null) {
            throw new IllegalArgumentException("estadoConversa não pode ser nulo");
        }

        String sql = """
                UPDATE usuario
                SET estado_conversa = ?
                WHERE telegram_chat_id = ?
                """;

        try (Connection conn = this.database.abrirConn(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, estadoConversa.name());
            pstmt.setLong(2, chatId);

            pstmt.executeUpdate();
        }
    }

    public void atualizaMetaReserva(long chatId, BigDecimal metaReserva) throws SQLException {

        if (chatId == 0) {
            throw new IllegalArgumentException("chatId não pode ser zero");
        }

        if (metaReserva == null) {
            throw new IllegalArgumentException("metaReserva não pode ser nula");
        }

        String sql = """
                UPDATE usuario
                SET meta_reserva = ?
                WHERE telegram_chat_id = ?
                """;

        try (Connection conn = this.database.abrirConn(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, metaReserva);
            pstmt.setLong(2, chatId);

            pstmt.executeUpdate();
        }

    }

    public void atualizaAporte(long chatId, BigDecimal aporte) throws SQLException {
        if (chatId == 0) {
            throw new IllegalArgumentException("chatId não pode ser zero");
        }

        if (aporte == null) {
            throw new IllegalArgumentException("aporte não pode ser nulo");
        }

        String sql = """
                UPDATE usuario
                SET aporte_mensal = ?
                WHERE telegram_chat_id = ?
                """;

        try (Connection conn = this.database.abrirConn(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, aporte);
            pstmt.setLong(2, chatId);

            pstmt.executeUpdate();
        }

    }

    public void atualizaRenda(long chatId, BigDecimal renda) throws SQLException {
        if (chatId == 0) {
            throw new IllegalArgumentException("chatId não pode ser zero");
        }

        if (renda == null) {
            throw new IllegalArgumentException("renda não pode ser null");
        }

        String sql = """
                UPDATE usuario
                SET renda_mensal = ?
                WHERE telegram_chat_id = ?
                """;

        try (Connection conn = this.database.abrirConn(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, renda);
            pstmt.setLong(2, chatId);

            pstmt.executeUpdate();
        }
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
                  meta_reserva,
                  estado_conversa
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
                            rs.getBigDecimal("aporte_mensal"), rs.getBigDecimal("meta_reserva"),
                            EstadoConversa.valueOf(rs.getString("estado_conversa"))));
                }
            }
        }

        return Optional.empty();
    }

}
