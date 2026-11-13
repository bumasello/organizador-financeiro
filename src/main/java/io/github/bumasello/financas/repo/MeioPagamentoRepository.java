package io.github.bumasello.financas.repo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import io.github.bumasello.financas.domain.MeioPagamento;
import io.github.bumasello.financas.domain.TipoPagamento;

/**
 * MeioPagamentoRepository
 */
public class MeioPagamentoRepository {
    private final Database database;

    public MeioPagamentoRepository(Database database) {
        this.database = database;
    }

    public List<MeioPagamento> buscaPorUsuario(long usuarioId) throws SQLException {
        List<MeioPagamento> meios = new ArrayList<>();

        String sql = """
                SELECT id, nome, tipo, dia_fechamento, dia_vencimento
                FROM meio_pagamento
                WHERE usuario_id = ?
                AND ativo IS TRUE;
                """;

        if (usuarioId <= 0) {
            throw new IllegalArgumentException("usuarioId não pode ser zero ou negativo");
        }

        try (Connection conn = this.database.abrirConn(); PreparedStatement pstmt = conn.prepareStatement(sql);) {
            pstmt.setLong(1, usuarioId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String nome = rs.getString("nome");
                    TipoPagamento tipo = TipoPagamento.valueOf(rs.getString("tipo"));
                    int diaFechamento = rs.getInt("dia_fechamento");
                    int diaVencimento = rs.getInt("dia_vencimento");

                    MeioPagamento meioPagamento = new MeioPagamento(nome, tipo, diaFechamento, diaVencimento);

                    meios.add(meioPagamento);
                }
            }
        }
        return meios;
    }

    public long salvar(long usuarioId, MeioPagamento meio) throws SQLException {

        String sql = """
                INSERT INTO meio_pagamento
                (usuario_id, nome, tipo, dia_fechamento, dia_vencimento)
                VALUES (?, ?, ?, ?, ?)
                RETURNING id
                """;

        if (usuarioId <= 0) {
            throw new IllegalArgumentException("usuarioId não pode ser zero ou negativo");
        }

        try (Connection conn = this.database.abrirConn();
                PreparedStatement pstmt = conn.prepareStatement(sql);) {
            pstmt.setLong(1, usuarioId);
            pstmt.setString(2, meio.nome());
            pstmt.setString(3, meio.tipo().name());
            pstmt.setInt(4, meio.diaFechamento());
            pstmt.setInt(5, meio.diaVencimento());

            try (ResultSet rs = pstmt.executeQuery();) {
                rs.next();
                return rs.getLong(1);
            }
        }

    }
}
