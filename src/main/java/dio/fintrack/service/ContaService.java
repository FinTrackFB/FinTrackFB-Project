package dio.fintrack.service;

import dio.fintrack.exception.FinTrackException;
import dio.fintrack.model.Categoria;
import dio.fintrack.model.Conta;
import dio.fintrack.model.Despesa;
import dio.fintrack.model.Receita;
import dio.fintrack.model.Tipo;
import dio.fintrack.model.Transacao;
import dio.fintrack.repository.Exportavel;
import dio.fintrack.repository.RepositorioTransacoes;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ContaService {

    private final Conta conta = new Conta();
    private final RepositorioTransacoes repositorio;
    private final Exportavel exportador;


    public ContaService(RepositorioTransacoes repositorio, Exportavel exportador) throws FinTrackException {
        this.repositorio = repositorio;
        this.exportador = exportador;
        for (Transacao transacao : repositorio.listarTodas()) {
            conta.adicionar(transacao);
        }
    }

    public Receita registrarReceita(String descricao, BigDecimal valor, LocalDate data, String categoria, String fonte) throws FinTrackException {
        Receita receita = new Receita(descricao, valor, data, new Categoria(categoria), fonte);
        repositorio.salvar(receita);
        conta.adicionar(receita);
        return receita;
    }

    public Despesa registrarDespesa(String descricao, BigDecimal valor, LocalDate data, String categoria, String formaPagamento) throws FinTrackException {
        Despesa despesa = new Despesa(descricao, valor, data, new Categoria(categoria), formaPagamento);
        repositorio.salvar(despesa);
        conta.adicionar(despesa);
        return despesa;
    }
    public Set<Categoria> categoriasEmUso() {
        return conta.categoriasEmUso();
    }

    public List<Transacao> listar() {
        return conta.listar();
    }

    public Transacao buscar(int id) throws FinTrackException {
        return conta.buscarPorId(id);
    }

    public Transacao remover(int id) throws FinTrackException {
        conta.buscarPorId(id);
        repositorio.remover(id);
        return conta.remover(id);
    }

    public BigDecimal saldo() {
        return conta.calcularSaldo();
    }

    public BigDecimal totalReceitas() {
        return conta.totalPorTipo(Tipo.RECEITA);
    }

    public BigDecimal totalDespesas() {
        return conta.totalPorTipo(Tipo.DESPESA);
    }

    public Map<Categoria, BigDecimal> gastosPorCategoria() {
        return conta.gastosPorCategoria();
    }

    public Path exportarCsv(String nomeArquivo) throws FinTrackException {
        return exportador.exportar(conta, nomeArquivo);
    }
}
