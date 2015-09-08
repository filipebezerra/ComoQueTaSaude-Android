package br.com.comoqueta.comoquetasaude.android.bus;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 19/08/2015
 * @since #
 */
public class SearchEvent {
    private final String mQuery;
    private boolean mSubmitted;

    public SearchEvent(String query, boolean submitted) {
        mQuery = query;
        mSubmitted = submitted;
    }

    public String getQuery() {
        return mQuery;
    }

    public boolean isSubmitted() {
        return mSubmitted;
    }
}
