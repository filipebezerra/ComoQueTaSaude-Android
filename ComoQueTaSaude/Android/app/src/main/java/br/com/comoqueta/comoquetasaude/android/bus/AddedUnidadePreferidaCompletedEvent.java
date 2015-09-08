package br.com.comoqueta.comoquetasaude.android.bus;

import br.com.comoqueta.comoquetasaude.android.models.UnidadeAtendimento;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 19/08/2015
 * @since #
 */
public class AddedUnidadePreferidaCompletedEvent {
    private final UnidadeAtendimento mUnidadeAtendimento;

    public AddedUnidadePreferidaCompletedEvent(UnidadeAtendimento unidadeAtendimento) {
        this.mUnidadeAtendimento = unidadeAtendimento;
    }

    public UnidadeAtendimento getUnidadeAtendimento() {
        return mUnidadeAtendimento;
    }
}
