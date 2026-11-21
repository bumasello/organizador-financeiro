package io.github.bumasello.financas.repo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import io.github.bumasello.financas.domain.Recorrencia;

/**
 * RecorrenciaRepository
 */
public class RecorrenciaRepository {
    private final Database database;

    public RecorrenciaRepository(Database database) {
        this.database = database;
    }

    public List<RecorrenciaSalva> listaPorUsuario(long usuarioId) throws SQLException {
        if (usuarioId == 0) {
            throw new IllegalArgumentException("usuarioId não pode ser zero");
        }

        List<RecorrenciaSalva> listaRecorrencia = new ArrayList<>();

        String sql = """
                SELECT
                  id,
                  meio_pagamento_id,
                  descricao,
                  categoria,
                  valor,
                  dia_cobranca,
                  mes_inicio,
                  mes_fim
                FROM recorrencia
                WHERE usuario_id = ?
                ORDER BY id
                """;

        try (Connection conn = this.database.abrirConn(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, usuarioId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Recorrencia recorrencia = new Recorrencia(rs.getString("descricao"), rs.getBigDecimal("valor"),
                            rs.getString("categoria"), rs.getInt("dia_cobranca"),
                            YearMonth.from(rs.getObject("mes_inicio", LocalDate.class)),
                            rs.getObject("mes_fim") == null ? null
                                    : YearMonth.from(rs.getObject("mes_fim", LocalDate.class)));

                    RecorrenciaSalva recorrenciaSalva = new RecorrenciaSalva(rs.getLong("id"),
                            rs.getLong("meio_pagamento_id"), recorrencia);

                    listaRecorrencia.add(recorrenciaSalva);
                }
            }
        }

        return listaRecorrencia;
    }

    public long salvar(long usuarioId, long meioPagamentoId, Recorrencia recorrencia) throws SQLException {

        if (usuarioId == 0) {
            throw new IllegalArgumentException("usuarioId não pode ser zero");
        }

        if (meioPagamentoId == 0) {
            throw new IllegalArgumentException("meioPagamentoId não pode ser zero");
        }

        if (recorrencia == null) {
            throw new IllegalArgumentException("recorrencia nao pode ser nulo");
        }

        String sql = """
                INSERT INTO recorrencia (
                  usuario_id,
                  meio_pagamento_id,
                  descricao,
                  categoria,
                  valor,
                  dia_cobranca,
                  mes_inicio,
                  mes_fim
                )
                VALUES (
                  ?,
                  ?,
                  ?,
                  ?,
                  ?,
                  ?,
                  ?,
                  ?
                )
                RETURNING id;
                """;
        try (Connection conn = this.database.abrirConn(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, usuarioId);
            pstmt.setLong(2, meioPagamentoId);
            pstmt.setString(3, recorrencia.descricao());
            pstmt.setString(4, recorrencia.categoria());
            pstmt.setBigDecimal(5, recorrencia.valor());
            pstmt.setInt(6, recorrencia.diaCobranca());
            pstmt.setObject(7, recorrencia.mesInicio().atDay(1));
            pstmt.setObject(8, recorrencia.mesFim() == null ? null : recorrencia.mesFim().atDay(1));

            try (ResultSet rs = pstmt.executeQuery();) {
                rs.next();
                return rs.getLong(1);

            }
        }
    }

}
