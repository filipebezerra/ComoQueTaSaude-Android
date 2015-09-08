package br.com.comoqueta.comoquetasaude.android.bus;

import br.com.comoqueta.comoquetasaude.android.models.UnidadeAtendimento;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 18/08/2015
 * @since #
 */
public class UnidadeAtendimentoSelectedEvent {
    private final UnidadeAtendimento mUnidadeAtendimento;

    public UnidadeAtendimentoSelectedEvent(UnidadeAtendimento unidadeAtendimento) {
        this.mUnidadeAtendimento = unidadeAtendimento;
    }

    public UnidadeAtendimento getUnidadeAtendimento() {
        return mUnidadeAtendimento;
    }
}
