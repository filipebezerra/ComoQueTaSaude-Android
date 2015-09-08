package br.com.comoqueta.comoquetasaude.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import br.com.comoqueta.comoquetasaude.android.R;
import br.com.comoqueta.comoquetasaude.android.activities.MapActivity;
import br.com.comoqueta.comoquetasaude.android.activities.StreetViewActivity;
import br.com.comoqueta.comoquetasaude.android.models.UnidadeAtendimento;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InformacoesUnidadeAtendimentoFragment extends Fragment {
    private static final String TAG = InformacoesUnidadeAtendimentoFragment.class.getName();
    public static final String EXTRA_UNIDADE_ATENDIMENTO = TAG + ".unidade_atendimento";

    @Bind(R.id.main_container) CoordinatorLayout mMainContainer;
    @Bind(R.id.unit_name) TextView mNomeUnidadeTextView;
    @Bind(R.id.address) TextView mEnderecoTextView;
    @Bind(R.id.bairro) TextView mBairroTextView;
    @Bind(R.id.complemento) TextView mComplementoTextView;
    @Bind(R.id.phone1) TextView mPhone1TextView;
    @Bind(R.id.phone2) TextView mPhone2TextView;
    @Bind(R.id.open) TextView mHorarioAberturaTextView;
    @Bind(R.id.close) TextView mHorarioFechamentoTextView;

    private UnidadeAtendimento mUnidadeAtendimento;

    public static InformacoesUnidadeAtendimentoFragment newInstance(
            @NonNull UnidadeAtendimento unidade) {
        InformacoesUnidadeAtendimentoFragment fragment =
                new InformacoesUnidadeAtendimentoFragment();

        Bundle arguments = new Bundle();
        arguments.putParcelable(EXTRA_UNIDADE_ATENDIMENTO, unidade);

        fragment.setArguments(arguments);

        return fragment;
    }

    public InformacoesUnidadeAtendimentoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info_unidade_atendimento, container, false);
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
                mEnderecoTextView.setText(mUnidadeAtendimento.getLogradouro());
                mBairroTextView.setText(mUnidadeAtendimento.getBairro());
                if (!TextUtils.isEmpty(mUnidadeAtendimento.getComplemento())) {
                    mComplementoTextView.setText(mUnidadeAtendimento.getComplemento());
                } else {
                    mComplementoTextView.setVisibility(View.GONE);
                }
                mPhone1TextView.setText(mUnidadeAtendimento.getTelefone1());
                Linkify.addLinks(mPhone1TextView, Linkify.PHONE_NUMBERS);
                mPhone2TextView.setText(mUnidadeAtendimento.getTelefone2());
                Linkify.addLinks(mPhone2TextView, Linkify.PHONE_NUMBERS);
                mHorarioAberturaTextView.setText(mUnidadeAtendimento.getHorarioAbertura());
                mHorarioFechamentoTextView.setText(mUnidadeAtendimento.getHorarioFechamento());
            }
        }
    }

    @OnClick({ R.id.view_in_map, R.id.view_in_street })
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.view_in_map:
                if (mUnidadeAtendimento.getGeoLocalizacao() == null) {
                    Snackbar.make(mMainContainer,
                            "Desculpe, a localização desta unidade de atendimento está indisponível",
                            Snackbar.LENGTH_LONG)
                            .show();
                } else {

                    Intent mapIntent = new Intent(getActivity(), MapActivity.class);
                    mapIntent.putExtra(MapActivity.EXTRA_UNIDADE_ATENDIMENTO, mUnidadeAtendimento);
                    startActivity(mapIntent);
                }
                break;
            case R.id.view_in_street:
                if (mUnidadeAtendimento.getGeoLocalizacao() == null) {
                    Snackbar.make(mMainContainer,
                            "Desculpe, a localização desta unidade de atendimento está indisponível",
                            Snackbar.LENGTH_LONG)
                            .show();
                } else {
                    Intent streetViewIntent = new Intent(getActivity(), StreetViewActivity.class);
                    streetViewIntent.putExtra(StreetViewActivity.EXTRA_UNIDADE_ATENDIMENTO,
                            mUnidadeAtendimento);
                    startActivity(streetViewIntent);
                }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
