package br.com.comoqueta.comoquetasaude.android.bus;

import com.parse.ParseException;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 19/08/2015
 * @since #
 */
public class FindingUnidadePreferidaFailedEvent {
    private final ParseException mException;

    public FindingUnidadePreferidaFailedEvent(ParseException e) {
        this.mException = e;
    }

    public ParseException getException() {
        return mException;
    }
}
