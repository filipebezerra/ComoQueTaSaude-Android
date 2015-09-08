package br.com.comoqueta.comoquetasaude.android.models;

import android.test.ApplicationTestCase;
import bolts.Task;
import br.com.comoqueta.comoquetasaude.android.application.AndroidApplication;
import com.parse.ParseException;
import com.parse.ParseUser;
import java.util.List;

import static br.com.comoqueta.comoquetasaude.android.models.UnidadesFavoritas.findTodasUnidadesFavoritas;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 19/08/2015
 * @since #
 */
public class UserTest extends ApplicationTestCase<AndroidApplication> {
    private ParseUser currentUser;
    private List<UnidadeAtendimento> unidadeAtendimentos;

    public UserTest() {
        super(AndroidApplication.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        createApplication();
        currentUser = ParseUser.getCurrentUser();
        unidadeAtendimentos = UnidadeAtendimento.getQuery(false).find();
    }

    public void testPreconditions() {
        assertNotNull("Usuário atual não foi criado", currentUser);
        assertNotNull("Consulta das unidades de atendimento retornou NULL", unidadeAtendimentos);
        assertEquals("Consulta das unidades de atendimento não retornou 12 itens", 12, unidadeAtendimentos.size());
    }

    public void testMarkAllUnidadesAtendimentoAsFavorite() {
        for (UnidadeAtendimento unidade : unidadeAtendimentos) {
            try {
                new UnidadesFavoritas().addUnidadeFavorita(unidade, currentUser, null);
            } catch (ParseException e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
        }

        Task<List<UnidadesFavoritas>> todasUnidadesFavoritas = findTodasUnidadesFavoritas(false, null);
        assertNotNull("Consulta das unidades favoritas retornou NULL", todasUnidadesFavoritas);

        final Exception error = todasUnidadesFavoritas.getError();
        assertNull(String.format("Consulta das unidades favoritas retornou o error %s",
                        error.getMessage()), error);
        assertEquals("Consulta das unidades favoritas não retornou 12 itens", 12,
                todasUnidadesFavoritas.getResult().size());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
