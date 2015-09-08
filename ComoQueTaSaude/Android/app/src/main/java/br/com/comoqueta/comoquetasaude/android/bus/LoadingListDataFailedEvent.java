package br.com.comoqueta.comoquetasaude.android.bus;

import com.parse.ParseException;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 18/08/2015
 * @since #
 */
public class LoadingListDataFailedEvent {
    private final ParseException mException;

    public LoadingListDataFailedEvent(ParseException e) {
        mException = e;
    }

    public ParseException getException() {
        return mException;
    }
}
