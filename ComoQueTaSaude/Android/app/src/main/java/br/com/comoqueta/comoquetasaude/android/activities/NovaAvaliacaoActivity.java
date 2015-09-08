package br.com.comoqueta.comoquetasaude.android.activities;

import android.os.Bundle;
import android.view.MenuItem;
import br.com.comoqueta.comoquetasaude.android.R;
import br.com.comoqueta.comoquetasaude.android.activities.base.BaseAppCompatActivity;
import br.com.comoqueta.comoquetasaude.android.models.UnidadeAtendimento;

public class NovaAvaliacaoActivity extends BaseAppCompatActivity {
    private static final String TAG = NovaAvaliacaoActivity.class.getName();

    public static final String EXTRA_UNIDADE_ATENDIMENTO = TAG + ".unidade_atendimento";
    private UnidadeAtendimento mUnidadeAtendimento;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_nova_avaliacao;
    }

    @Override
    protected int getUpIndicatorDrawableResId() {
        return R.drawable.ic_close_white_24dp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null && getIntent().getExtras() != null &&
                getIntent().hasExtra(EXTRA_UNIDADE_ATENDIMENTO)) {
            mUnidadeAtendimento = getIntent().getExtras().getParcelable(EXTRA_UNIDADE_ATENDIMENTO);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public UnidadeAtendimento getUnidadeAtendimento() {
        return mUnidadeAtendimento;
    }
}
