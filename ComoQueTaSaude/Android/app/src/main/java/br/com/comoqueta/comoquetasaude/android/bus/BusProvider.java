package br.com.comoqueta.comoquetasaude.android.bus;

import com.squareup.otto.Bus;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 07/08/2015
 * @since #
 */
public class BusProvider {
    private static Bus sBus = new Bus();

    public static Bus provideBus() {
        return sBus;
    }

    private BusProvider() {}
}
