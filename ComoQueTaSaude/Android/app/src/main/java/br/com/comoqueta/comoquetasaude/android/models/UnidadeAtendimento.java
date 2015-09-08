package br.com.comoqueta.comoquetasaude.android.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import java.util.List;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 15/08/2015
 * @since #
 */
@ParseClassName(UnidadeAtendimento.PARSE_CLASS_NAME)
public class UnidadeAtendimento extends ParseObject implements Parcelable {
    public static final String PARSE_CLASS_NAME = "UnidadeAtendimento";

    private static final String KEY_NOME = "nome";
    private static final String KEY_LOGRADOURO = "logradouro";
    private static final String KEY_COMPLEMENTO = "complemento";
    private static final String KEY_BAIRRO = "bairro";
    private static final String KEY_TELEFONE1 = "telefone1";
    private static final String KEY_TELEFONE2 = "telefone2";
    private static final String KEY_HORARIO_ABERTURA = "horarioAbertura";
    private static final String KEY_HORARIO_FECHAMENTO = "horarioFechamento";
    private static final String KEY_ATENDIMENTO_DIA_TODO = "atendimentoDiaTodo";
    private static final String KEY_GEOLOCALIZACAO = "geoLocalicazao";
    private static final String KEY_AVALIACOES = "avaliacoes";

    private transient boolean isThisFavoriteToCurrentUser = false;

    // Required non-args constructor for Parse
    public UnidadeAtendimento() {
        super();
    }

    public String getNome() {
        return getString(KEY_NOME);
    }

    public UnidadeAtendimento setNome(String nome) {
        put(KEY_NOME, nome);
        return this;
    }

    public String getLogradouro() {
        return getString(KEY_LOGRADOURO);
    }

    public UnidadeAtendimento setLogradouro(String logradouro) {
        put(KEY_LOGRADOURO, logradouro);
        return this;
    }

    public String getComplemento() {
        return getString(KEY_COMPLEMENTO);
    }

    public UnidadeAtendimento setComplemento(String complemento) {
        put(KEY_COMPLEMENTO, complemento);
        return this;
    }

    public String getBairro() {
        return getString(KEY_BAIRRO);
    }

    public UnidadeAtendimento setBairro(String bairro) {
        put(KEY_BAIRRO, bairro);
        return this;
    }

    public String getTelefone1() {
        return getString(KEY_TELEFONE1);
    }

    public UnidadeAtendimento setTelefone1(String telefone1) {
        put(KEY_TELEFONE1, telefone1);
        return this;
    }

    public String getTelefone2() {
        return getString(KEY_TELEFONE2);
    }

    public UnidadeAtendimento setTelefone2(String telefone2) {
        put(KEY_TELEFONE2, telefone2);
        return this;
    }

    public String getHorarioAbertura() {
        return getString(KEY_HORARIO_ABERTURA);
    }

    public UnidadeAtendimento setHorarioAbertura(String horarioAbertura) {
        put(KEY_HORARIO_ABERTURA, horarioAbertura);
        return this;
    }

    public String getHorarioFechamento() {
        return getString(KEY_HORARIO_FECHAMENTO);
    }

    public UnidadeAtendimento setHorarioFechamento(String horarioFechamento) {
        put(KEY_HORARIO_FECHAMENTO, horarioFechamento);
        return this;
    }

    public Boolean getAtendimentoDiaTodo() {
        return getBoolean(KEY_ATENDIMENTO_DIA_TODO);
    }

    public UnidadeAtendimento setAtendimentoDiaTodo(Boolean atendimentoDiaTodo) {
        put(KEY_ATENDIMENTO_DIA_TODO, atendimentoDiaTodo);
        return this;
    }

    public ParseGeoPoint getGeoLocalizacao() {
        return getParseGeoPoint(KEY_GEOLOCALIZACAO);
    }

    public UnidadeAtendimento setGeoLocalizacao(LatLng geoLocalizacao) {
        put(KEY_GEOLOCALIZACAO,
                new ParseGeoPoint(geoLocalizacao.latitude, geoLocalizacao.longitude));
        return this;
    }

    public UnidadeAtendimento setAsFavoriteToCurrentUser(boolean favorite){
        isThisFavoriteToCurrentUser = favorite;
        return this;
    }

    public boolean isThisFavoriteToCurrentUser() {
        return isThisFavoriteToCurrentUser;
    }

    public UnidadeAtendimento addAvaliacao(Avaliacao avaliacao) {
        add(KEY_AVALIACOES, avaliacao);
        return this;
    }

    public List<Avaliacao> getAvaliacoes() {
        return getList(KEY_AVALIACOES);
    }

    public static ParseQuery<UnidadeAtendimento> getQuery(boolean getLocally) {
        ParseQuery<UnidadeAtendimento> query = ParseQuery.getQuery(UnidadeAtendimento.class);

        if (getLocally) {
            query.fromLocalDatastore();
        }

        return query;
    }
    
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getNome());
        dest.writeString(getLogradouro());
        dest.writeString(getComplemento() == null ? "" : getComplemento());
        dest.writeString(getBairro());
        dest.writeString(getTelefone1());
        dest.writeString(getTelefone2());
        dest.writeString(getHorarioAbertura());
        dest.writeString(getHorarioFechamento());
        dest.writeInt(getAtendimentoDiaTodo() ? 1 : 0);
        dest.writeDouble(getGeoLocalizacao() == null ? 0 : getGeoLocalizacao().getLatitude());
        dest.writeDouble(getGeoLocalizacao() == null ? 0 : getGeoLocalizacao().getLongitude());
    }

    private UnidadeAtendimento(Parcel in) {
        setNome(in.readString());
        setLogradouro(in.readString());
        setComplemento(in.readString());
        setBairro(in.readString());
        setTelefone1(in.readString());
        setTelefone2(in.readString());
        setHorarioAbertura(in.readString());
        setHorarioFechamento(in.readString());
        setAtendimentoDiaTodo(in.readInt() != 1);
        setGeoLocalizacao(new LatLng(in.readDouble(), in.readDouble()));
    }

    public static final Parcelable.Creator<UnidadeAtendimento> CREATOR =
            new Parcelable.Creator<UnidadeAtendimento>() {
                @Override
                public UnidadeAtendimento createFromParcel(Parcel source) {
                    return new UnidadeAtendimento(source);
                }

                @Override
                public UnidadeAtendimento[] newArray(int size) {
                    return new UnidadeAtendimento[size];
                }
            };
}
