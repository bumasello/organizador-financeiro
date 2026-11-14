package io.github.bumasello.financas.repo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Database
 */
public class Database {
    private final HikariDataSource dataSource;

    public Database(String url, String user, String pass) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(pass);
        config.setMaximumPoolSize(3);
        this.dataSource = new HikariDataSource(config);
    }

    public Connection abrirConn() throws SQLException {
        return this.dataSource.getConnection();

    }

    public String schemaAtual() throws SQLException {

        try (Connection conn = abrirConn();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT current_schema()");) {

            rs.next();

            return rs.getString(1);
        }

    }
}
