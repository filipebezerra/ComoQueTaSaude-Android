package br.com.comoqueta.comoquetasaude.android.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import bolts.Task;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import java.util.List;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 20/08/2015
 * @since #
 */
@ParseClassName(UnidadesFavoritas.PARSE_CLASS_NAME)
public class UnidadesFavoritas extends ParseObject {
    public static final String PARSE_CLASS_NAME = "UnidadesFavoritas";

    private static final String KEY_USUARIO = "usuario";
    private static final String KEY_UNIDADE_ATENDIMENTO = "unidadeAtendimento";

    public UnidadesFavoritas() {
        super();
    }

    public void addUnidadeFavorita(@NonNull UnidadeAtendimento unidadeAtendimento,
            @Nullable SaveCallback saveCallback) throws ParseException {
        addUnidadeFavorita(unidadeAtendimento, ParseUser.getCurrentUser(), saveCallback);
    }

    public void addUnidadeFavorita(@NonNull UnidadeAtendimento unidadeAtendimento,
            @NonNull ParseUser usuario, @Nullable SaveCallback saveCallback) throws ParseException {
        put(KEY_USUARIO, usuario);
        put(KEY_UNIDADE_ATENDIMENTO, unidadeAtendimento);

        if (saveCallback == null) {
            save();
        } else {
            saveInBackground(saveCallback);
        }
    }

    public void removeUnidadeFavorita(@NonNull UnidadeAtendimento unidadeAtendimento,
            @Nullable DeleteCallback deleteCallback) throws ParseException {
        removeUnidadeFavorita(unidadeAtendimento, ParseUser.getCurrentUser(), deleteCallback);
    }

    public void removeUnidadeFavorita(@NonNull UnidadeAtendimento unidadeAtendimento,
            @NonNull ParseUser usuario, @Nullable DeleteCallback deleteCallback) throws ParseException {
        List<UnidadesFavoritas> unidadesFavoritases = getQuery(false)
                .whereEqualTo(KEY_USUARIO, usuario)
                .whereEqualTo(KEY_UNIDADE_ATENDIMENTO, unidadeAtendimento)
                .find();

        if (deleteCallback == null) {
            deleteAll(unidadesFavoritases);
        } else {
            deleteAllInBackground(unidadesFavoritases, deleteCallback);
        }
    }

    public static Task<List<UnidadesFavoritas>> findTodasUnidadesFavoritas(
            boolean findLocally,
            @Nullable FindCallback<UnidadesFavoritas> findCallback) {
        return findTodasUnidadesFavoritas(ParseUser.getCurrentUser(), findLocally, findCallback);
    }

    public static Task<List<UnidadesFavoritas>> findTodasUnidadesFavoritas(
            @NonNull ParseUser usuario,
            boolean findLocally,
            @Nullable FindCallback<UnidadesFavoritas> findCallback) {

        ParseQuery<UnidadesFavoritas> query = getQuery(findLocally)
                .whereEqualTo(KEY_USUARIO, usuario);

        if (findCallback == null) {
            return query.findInBackground();
        } else {
            query.findInBackground(findCallback);
            return null;
        }
    }

    public static boolean isUnidadeFavoritaToCurrentUser(@NonNull UnidadeAtendimento
            unidadeAtendimento) throws ParseException {
        UnidadesFavoritas result = getQuery(false)
                .whereEqualTo(KEY_UNIDADE_ATENDIMENTO, unidadeAtendimento)
                .whereEqualTo(KEY_USUARIO, ParseUser.getCurrentUser())
                .getFirst();

        return result != null;
    }

    public static ParseQuery<UnidadesFavoritas> getQuery(boolean fromLocalDatabase) {
        ParseQuery<UnidadesFavoritas> query = ParseQuery.getQuery(UnidadesFavoritas.class);
        if (fromLocalDatabase) {
            query.fromLocalDatastore();
        }
        return query;
    }
}
