package dio.fintrack.view;

import dio.fintrack.exception.FinTrackException;
import dio.fintrack.exception.ValorInvalidoException;
import dio.fintrack.model.Categoria;
import dio.fintrack.model.Transacao;
import dio.fintrack.service.ContaService;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public class MenuConsole {

    private final ContaService service;
    private final Scanner scanner = new Scanner(System.in);

    public MenuConsole(ContaService service) {
        this.service = service;
    }

    public void iniciar() {
        int opcao;
        do {
            mostrarMenu();
            opcao = lerOpcao();
            executar(opcao);
        } while (opcao != 0);
        System.out.println("Até logo!");
    }

    private void mostrarMenu() {
        System.out.println();
        System.out.println("===== FinTrack =====");
        System.out.println("1 - Registrar receita");
        System.out.println("2 - Registrar despesa");
        System.out.println("3 - Listar transações");
        System.out.println("4 - Buscar transação");
        System.out.println("5 - Consultar saldo");
        System.out.println("6 - Remover transação");
        System.out.println("7 - Relatório por categoria");
        System.out.println("8 - Exportar para CSV");
        System.out.println("0 - Sair");
    }

    private int lerOpcao() {
        System.out.print("Escolha uma opção: ");
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void executar(int opcao) {
        switch (opcao) {
            case 1 -> registrarReceita();
            case 2 -> registrarDespesa();
            case 3 -> listar();
            case 4 -> buscar();
            case 5 -> consultarSaldo();
            case 6 -> removerTransacao();
            case 7 -> relatorioPorCategoria();
            case 8 -> exportarCsv();
            case 0 -> { }
            default -> System.out.println("Opção inválida. Escolha um número do menu.");
        }
    }


    private void registrarReceita() {
        boolean concluido = false;
        while (!concluido) {
            try {
                System.out.println("--- Registrar receita ---");
                String descricao = lerTexto("Descrição: ");
                BigDecimal valor = lerValor();
                LocalDate data = lerData();
                String categoria = lerTexto("Categoria: ");
                String fonte = lerTexto("Fonte: ");

                service.registrarReceita(descricao, valor, data, categoria, fonte);
                System.out.println("Receita cadastrada! Saldo atual: " + dinheiro(service.saldo()));
                concluido = true;
            } catch (FinTrackException e) {
                System.out.println("Erro: " + e.getMessage());
                concluido = !tentarNovamente();
            }
        }
    }


    private void registrarDespesa() {
        boolean concluido = false;
        while (!concluido) {
            try {
                System.out.println("--- Registrar despesa ---");
                String descricao = lerTexto("Descrição: ");
                BigDecimal valor = lerValor();
                LocalDate data = lerData();
                String categoria = lerTexto("Categoria: ");
                String formaPagamento = lerTexto("Forma de pagamento: ");

                service.registrarDespesa(descricao, valor, data, categoria, formaPagamento);
                System.out.println("Despesa cadastrada! Saldo atual: " + dinheiro(service.saldo()));
                concluido = true;
            } catch (FinTrackException e) {
                System.out.println("Erro: " + e.getMessage());
                concluido = !tentarNovamente();
            }
        }
    }


    private void listar() {
        List<Transacao> transacoes = service.listar();
        if (transacoes.isEmpty()) {
            System.out.println("Nenhuma transação cadastrada");
            return;
        }
        transacoes.forEach(System.out::println);
    }


    private void buscar() {
        int id = lerId("Id da transação: ");
        try {
            System.out.println(service.buscar(id));
        } catch (FinTrackException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }


    private void consultarSaldo() {
        System.out.println("Total de receitas: " + dinheiro(service.totalReceitas()));
        System.out.println("Total de despesas: " + dinheiro(service.totalDespesas()));
        System.out.println("Saldo: " + dinheiro(service.saldo()));
    }


    private void removerTransacao() {
        int id = lerId("Id da transação a remover: ");
        try {
            service.remover(id);
            System.out.println("Transação removida. Saldo atual: " + dinheiro(service.saldo()));
        } catch (FinTrackException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }


    private void relatorioPorCategoria() {
        Map<Categoria, BigDecimal> gastos = service.gastosPorCategoria();
        if (gastos.isEmpty()) {
            System.out.println("Nenhuma despesa para exibir");
            return;
        }
        gastos.forEach((categoria, total) ->
                System.out.println(categoria + ": " + dinheiro(total)));
    }


    private void exportarCsv() {
        String nome = lerTexto("Nome do arquivo: ");
        try {
            Path caminho = service.exportarCsv(nome);
            System.out.println("Arquivo gerado em: " + caminho);
        } catch (FinTrackException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }



    private String lerTexto(String mensagem) {
        System.out.print(mensagem);
        return scanner.nextLine().trim();
    }

    private BigDecimal lerValor() throws ValorInvalidoException {
        String texto = lerTexto("Valor: ").replace(",", ".");
        try {
            return new BigDecimal(texto);
        } catch (NumberFormatException e) {
            throw new ValorInvalidoException("Valor inválido: digite apenas números, por exemplo 150.50");
        }
    }

    private LocalDate lerData() {
        while (true) {
            String texto = lerTexto("Data (aaaa-mm-dd): ");
            try {
                return LocalDate.parse(texto);
            } catch (DateTimeParseException e) {
                System.out.println("Data inválida. Use o formato aaaa-mm-dd, por exemplo 2026-10-24.");
            }
        }
    }

    private int lerId(String mensagem) {
        while (true) {
            String texto = lerTexto(mensagem);
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                System.out.println("O id deve ser um número inteiro.");
            }
        }
    }

    private boolean tentarNovamente() {
        return lerTexto("Tentar novamente? (s/n): ").equalsIgnoreCase("s");
    }

    private String dinheiro(BigDecimal valor) {
        return String.format(Locale.forLanguageTag("pt-BR"), "R$ %,.2f", valor);
    }
}
