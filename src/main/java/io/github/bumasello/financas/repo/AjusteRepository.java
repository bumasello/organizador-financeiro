package io.github.bumasello.financas.repo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.YearMonth;

import io.github.bumasello.financas.domain.TipoAjuste;

/**
 * AjusteRepository
 */
public class AjusteRepository {
    private final Database database;

    public AjusteRepository(Database database) {
        this.database = database;
    }

    public BigDecimal totalMes(long usuarioId, YearMonth mes, TipoAjuste tipo) throws SQLException {

        if (usuarioId <= 0) {
            throw new IllegalArgumentException("usuarioId não pode ser zero ou negativo");
        }

        if (mes == null) {
            throw new IllegalArgumentException("mes não pode ser nulo");
        }

        if (tipo == null) {
            throw new IllegalArgumentException("tipo nao pode ser nulo");
        }

        String sql = """
                SELECT COALESCE(SUM(valor), 0::NUMERIC(12, 2))
                FROM ajuste
                WHERE mes = ?
                  AND usuario_id = ?
                  AND tipo = ?
                """;

        try (Connection conn = this.database.abrirConn(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setObject(1, mes.atDay(1));
            pstmt.setLong(2, usuarioId);
            pstmt.setString(3, tipo.name());

            try (ResultSet rs = pstmt.executeQuery()) {
                rs.next();
                return rs.getBigDecimal(1);
            }
        }

    }
}
