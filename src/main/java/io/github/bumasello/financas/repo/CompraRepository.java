package io.github.bumasello.financas.repo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import io.github.bumasello.financas.domain.Compra;
import io.github.bumasello.financas.domain.Parcela;

/**
 * CompraRepository
 */
public class CompraRepository {
    private final Database database;

    public CompraRepository(Database database) {
        this.database = database;
    }

    public long salvar(long usuarioId, long meioPagamentoId, Compra compra, List<Parcela> parcelas)
            throws SQLException {
        if (usuarioId <= 0) {
            throw new IllegalArgumentException("usuarioId não pode ser zero ou negativo");
        }
        if (meioPagamentoId <= 0) {
            throw new IllegalArgumentException("meioPagamentoId não pode ser zero ou negativo");
        }

        long compraId;
        String sqlCompra = """
                INSERT INTO compra
                  (usuario_id, meio_pagamento_id, data_compra, descricao, categoria, valor_total, quantidade_parcelas)
                VALUES
                  (?, ?, ?, ?, ?, ?, ?)
                RETURNING id
                """;

        String sqlParcela = """
                INSERT INTO parcela
                  (compra_id, numero, valor, mes_fatura)
                VALUES
                  (?, ?, ?, ?)
                """;

        try (Connection conn = this.database.abrirConn();) {
            conn.setAutoCommit(false);
            try (
                    PreparedStatement pstmtCompra = conn.prepareStatement(sqlCompra);
                    PreparedStatement pstmtParcela = conn.prepareStatement(sqlParcela)) {
                pstmtCompra.setLong(1, usuarioId);
                pstmtCompra.setLong(2, meioPagamentoId);
                pstmtCompra.setObject(3, compra.dataCompra());
                pstmtCompra.setString(4, compra.descricao());
                pstmtCompra.setString(5, compra.categoria());
                pstmtCompra.setBigDecimal(6, compra.valorTotal());
                pstmtCompra.setInt(7, compra.quantidadeParcelas());

                try (ResultSet rsCompra = pstmtCompra.executeQuery();) {
                    if (rsCompra.next()) {
                        compraId = rsCompra.getLong(1);
                    } else {
                        throw new SQLException("Falha ao inserir compra, nenhum ID retornado");
                    }
                }
                for (Parcela p : parcelas) {
                    pstmtParcela.setLong(1, compraId);
                    pstmtParcela.setInt(2, p.numero());
                    pstmtParcela.setBigDecimal(3, p.valor());
                    pstmtParcela.setObject(4, p.mesFatura().atDay(1));

                    pstmtParcela.addBatch();
                }

                pstmtParcela.executeBatch();

                conn.commit();
                return compraId;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

        }

    }
}
