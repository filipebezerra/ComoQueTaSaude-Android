package br.com.comoqueta.comoquetasaude.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import br.com.comoqueta.comoquetasaude.android.R;
import br.com.comoqueta.comoquetasaude.android.activities.NovaAvaliacaoActivity;
import br.com.comoqueta.comoquetasaude.android.models.UnidadeAtendimento;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.text.DateFormat;
import java.util.Calendar;

public class AvaliacoesUnidadeAtendimentoFragment extends Fragment {
    private static final String TAG = AvaliacoesUnidadeAtendimentoFragment.class.getName();
    public static final String EXTRA_UNIDADE_ATENDIMENTO = TAG + ".unidade_atendimento";

    @Bind(R.id.main_container) CoordinatorLayout mMainContainer;
    @Bind(R.id.unit_name) TextView mNomeUnidadeTextView;
    @Bind(R.id.status_date) TextView mAvaliacoesDoDiaTextView;

    private UnidadeAtendimento mUnidadeAtendimento;

    public static AvaliacoesUnidadeAtendimentoFragment newInstance(
            @NonNull UnidadeAtendimento unidade) {
        AvaliacoesUnidadeAtendimentoFragment fragment =
                new AvaliacoesUnidadeAtendimentoFragment();

        Bundle arguments = new Bundle();
        arguments.putParcelable(EXTRA_UNIDADE_ATENDIMENTO, unidade);

        fragment.setArguments(arguments);

        return fragment;
    }

    public AvaliacoesUnidadeAtendimentoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_avaliacoes_unidade_atendimento, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(EXTRA_UNIDADE_ATENDIMENTO)) {
            mUnidadeAtendimento = getArguments().getParcelable(EXTRA_UNIDADE_ATENDIMENTO);

            if (mUnidadeAtendimento != null) {
                mNomeUnidadeTextView.setText(mUnidadeAtendimento.getNome());
                mAvaliacoesDoDiaTextView.setText(String.format("Avaliações de hoje | %s",
                        DateFormat.getDateInstance().format(Calendar.getInstance().getTime())));
            }
        }
    }

    @OnClick(R.id.fab_rate)
    public void onClick(final View view) {
        Intent novaAvaliacaoIntent = new Intent(getActivity(), NovaAvaliacaoActivity.class);
        novaAvaliacaoIntent.putExtra(NovaAvaliacaoActivity.EXTRA_UNIDADE_ATENDIMENTO,
                mUnidadeAtendimento);
        startActivity(novaAvaliacaoIntent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
