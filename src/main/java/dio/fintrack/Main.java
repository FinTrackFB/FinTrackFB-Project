package dio.fintrack;

import dio.fintrack.exception.FinTrackException;
import dio.fintrack.repository.ExportadorCSV;
import dio.fintrack.repository.RepositorioSQLite;
import dio.fintrack.service.ContaService;
import dio.fintrack.view.MenuConsole;

public class Main {

    public static void main(String[] args) {
        try {
            ContaService service = new ContaService(new RepositorioSQLite(), new ExportadorCSV());
            new MenuConsole(service).iniciar();
        } catch (FinTrackException e) {
            System.out.println("Não foi possível iniciar o sistema: " + e.getMessage());
        }
    }
}