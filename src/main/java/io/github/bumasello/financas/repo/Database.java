package io.github.bumasello.financas.repo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database
 */
public class Database {
    private final String url;
    private final String user;
    private final String pass;

    public Database(String url, String user, String pass) {
        this.url = url;
        this.user = user;
        this.pass = pass;
    }

    public Connection abrirConn() throws SQLException {
        return DriverManager.getConnection(this.url, this.user, this.pass);

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
