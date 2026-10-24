package dio.fintrack.repository;

import dio.fintrack.exception.FinTrackException;
import dio.fintrack.model.Conta;
import dio.fintrack.model.Transacao;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ExportadorCSV implements Exportavel {

    private static final String SEPARADOR = ";";

    @Override
    public Path exportar(Conta conta, String nomeArquivo) throws FinTrackException {
        if (nomeArquivo == null || nomeArquivo.isBlank()) {
            throw new FinTrackException("Informe o nome do arquivo");
        }
        String nome = nomeArquivo.trim();
        if (!nome.toLowerCase().endsWith(".csv")) {
            nome += ".csv";
        }

        List<String> linhas = new ArrayList<>();

        linhas.add("\uFEFFid;tipo;data;descricao;categoria;valor");
        for (Transacao t : conta.listar()) {
            linhas.add(String.join(SEPARADOR,
                    String.valueOf(t.getId()),
                    t.getTipo().name(),
                    t.getData().toString(),
                    escapar(t.getDescricao()),
                    escapar(t.getCategoria().getNome()),
                    formatarValor(t.getValor())));
        }

        try {
            Path caminho = Path.of(nome).toAbsolutePath();
            Files.write(caminho, linhas, StandardCharsets.UTF_8);
            return caminho;
        } catch (IOException | InvalidPathException e) {
            throw new FinTrackException("Não foi possível gravar o arquivo: " + e.getMessage());
        }
    }

    private String escapar(String texto) {
        if (texto.contains(SEPARADOR) || texto.contains("\"") || texto.contains("\n")) {
            return "\"" + texto.replace("\"", "\"\"") + "\"";
        }
        return texto;
    }

    private String formatarValor(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP).toPlainString().replace('.', ',');
    }
}