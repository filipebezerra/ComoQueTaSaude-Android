package br.com.comoqueta.comoquetasaude.android.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 19/08/2015
 * @since #
 */
@ParseClassName(Avaliacao.PARSE_CLASS_NAME)
public class Avaliacao extends ParseObject {
    public static final String PARSE_CLASS_NAME = "Avaliacao";

    private static final String KEY_TEMPO_ESPERA = "tempoEspera";
    private static final String KEY_AVALIACAO = "avaliacao";
    private static final String KEY_NOTA = "nota";
    private static final String KEY_USUARIO = "usuario";
    private static final String KEY_UNIDADE_ATENDIMENTO = "unidadeAtendimento";

    public Avaliacao() {
        super();
    }

    public Avaliacao setTempoEspera(String tempoEspera) {
        put(KEY_TEMPO_ESPERA, tempoEspera);
        return this;
    }

    public String getTempoEspera() {
        return getString(KEY_TEMPO_ESPERA);
    }

    public Avaliacao setAvaliacao(String avaliacao) {
        put(KEY_AVALIACAO, avaliacao);
        return this;
    }

    public String getAvaliacao() {
        return getString(KEY_AVALIACAO);
    }

    public Avaliacao setNota(Double nota) {
        put(KEY_NOTA, nota);
        return this;
    }

    public Double getNota() {
        return getDouble(KEY_NOTA);
    }

    public Avaliacao setUsuario(ParseUser usuario) {
        put(KEY_USUARIO, usuario);
        return this;
    }

    public ParseUser getUsuario() {
        return getParseUser(KEY_USUARIO);
    }

    public Avaliacao setUnidadeAtendimento(UnidadeAtendimento unidade) {
        put(KEY_UNIDADE_ATENDIMENTO, unidade);
        return this;
    }

    public UnidadeAtendimento getUnidadeAtendimento() {
        return (UnidadeAtendimento) getParseObject(KEY_UNIDADE_ATENDIMENTO);
    }
}
