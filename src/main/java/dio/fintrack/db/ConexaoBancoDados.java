package dio.fintrack.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexaoBancoDados {


    public static final String URL_PADRAO = "jdbc:sqlite:fintrack-poo.db";

    public static Connection conection() throws SQLException {
        return conection(URL_PADRAO);
    }

    public static Connection conection(String url) throws SQLException {
        Connection conexao = DriverManager.getConnection(url);
        criarTabelaSeNaoExistir(conexao);
        return conexao;
    }

    private static void criarTabelaSeNaoExistir(Connection conexao) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS transacoes ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "tipo VARCHAR(10) NOT NULL,"
                + "descricao VARCHAR(100) NOT NULL,"
                + "valor DECIMAL(10,2) NOT NULL,"
                + "data DATE NOT NULL,"
                + "categoria VARCHAR(100) NOT NULL,"
                + "fonte VARCHAR(100),"
                + "forma_pagamento VARCHAR(100)"
                + ")";
        try (Statement stmt = conexao.createStatement()) {
            stmt.execute(sql);
        }
    }
}