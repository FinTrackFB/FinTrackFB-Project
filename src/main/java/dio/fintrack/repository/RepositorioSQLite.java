package dio.fintrack.repository;

import dio.fintrack.db.ConexaoBancoDados;
import dio.fintrack.exception.FinTrackException;
import dio.fintrack.exception.TransacaoNaoEncontradaException;
import dio.fintrack.model.Categoria;
import dio.fintrack.model.Despesa;
import dio.fintrack.model.Receita;
import dio.fintrack.model.Tipo;
import dio.fintrack.model.Transacao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RepositorioSQLite implements RepositorioTransacoes {

    private final String url;

    public RepositorioSQLite() {
        this(ConexaoBancoDados.URL_PADRAO);
    }


    public RepositorioSQLite(String url) {
        this.url = url;
    }

    @Override
    public void salvar(Transacao transacao) throws FinTrackException {
        String sql = "INSERT INTO transacoes "
                + "(tipo, descricao, valor, data, categoria, fonte, forma_pagamento) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";


        try (Connection conexao = ConexaoBancoDados.conection(url);
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setString(1, transacao.getTipo().name());
            ps.setString(2, transacao.getDescricao());
            ps.setBigDecimal(3, transacao.getValor());
            ps.setString(4, transacao.getData().toString());
            ps.setString(5, transacao.getCategoria().getNome());


            if (transacao instanceof Receita receita) {
                ps.setString(6, receita.getFonte());
                ps.setNull(7, Types.VARCHAR);
            } else if (transacao instanceof Despesa despesa) {
                ps.setNull(6, Types.VARCHAR);
                ps.setString(7, despesa.getFormaPagamento());
            } else {
                throw new FinTrackException("Tipo de transação não suportado");
            }

            ps.executeUpdate();
            transacao.setId(ultimoIdGerado(conexao));
        } catch (SQLException e) {
            throw new FinTrackException("Erro ao salvar no banco de dados: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Transacao> listarTodas() throws FinTrackException {
        List<Transacao> transacoes = new ArrayList<>();
        String sql = "SELECT * FROM transacoes ORDER BY id";

        try (Connection conexao = ConexaoBancoDados.conection(url);
             Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                transacoes.add(montarTransacao(rs));
            }
        } catch (SQLException e) {
            throw new FinTrackException("Erro ao ler do banco de dados: " + e.getMessage(), e);
        } catch (IllegalArgumentException | DateTimeException e) {
            throw new FinTrackException("Dados inválidos no banco de dados: " + e.getMessage(), e);
        }
        return transacoes;
    }

    @Override
    public void remover(int id) throws FinTrackException {
        String sql = "DELETE FROM transacoes WHERE id = ?";

        try (Connection conexao = ConexaoBancoDados.conection(url);
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new TransacaoNaoEncontradaException(id);
            }
        } catch (SQLException e) {
            throw new FinTrackException("Erro ao remover do banco de dados: " + e.getMessage(), e);
        }
    }


    private Transacao montarTransacao(ResultSet rs) throws SQLException, FinTrackException {
        Tipo tipo = Tipo.valueOf(rs.getString("tipo"));
        String descricao = rs.getString("descricao");
        BigDecimal valor = rs.getBigDecimal("valor");
        LocalDate data = LocalDate.parse(rs.getString("data"));
        Categoria categoria = new Categoria(rs.getString("categoria"));

        Transacao transacao = switch (tipo) {
            case RECEITA -> new Receita(descricao, valor, data, categoria, rs.getString("fonte"));
            case DESPESA -> new Despesa(descricao, valor, data, categoria, rs.getString("forma_pagamento"));
        };
        transacao.setId(rs.getInt("id"));
        return transacao;
    }

    private int ultimoIdGerado(Connection conexao) throws SQLException {
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
            rs.next();
            return rs.getInt(1);
        }
    }
}
