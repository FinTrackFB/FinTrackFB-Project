package dio.fintrack.model;

import dio.fintrack.exception.ValorInvalidoException;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CategoriaTest {

    @Test
    void deveGuardarONomeSemEspacosNasPontas() throws Exception {
        assertEquals("Alimentação", new Categoria("  Alimentação  ").getNome());
    }

    @Test
    void deveRejeitarNomeVazioOuNulo() {
        assertThrows(ValorInvalidoException.class, () -> new Categoria(""));
        assertThrows(ValorInvalidoException.class, () -> new Categoria("   "));
        assertThrows(ValorInvalidoException.class, () -> new Categoria(null));
    }

    @Test
    void categoriasComMesmoNomeSaoIguaisIgnorandoMaiusculas() throws Exception {
        Categoria a = new Categoria("Alimentação");
        Categoria b = new Categoria("alimentação");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void categoriasComNomesDiferentesNaoSaoIguais() throws Exception {
        assertNotEquals(new Categoria("Alimentação"), new Categoria("Transporte"));
    }

    @Test
    void setNaoDeveRepetirCategoriasIguais() throws Exception {
        Set<Categoria> categorias = new HashSet<>();
        categorias.add(new Categoria("Transporte"));
        categorias.add(new Categoria("TRANSPORTE"));

        assertEquals(1, categorias.size());
    }
}
