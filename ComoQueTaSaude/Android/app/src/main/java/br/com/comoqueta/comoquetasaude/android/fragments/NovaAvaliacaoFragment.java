package br.com.comoqueta.comoquetasaude.android.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import br.com.comoqueta.comoquetasaude.android.R;
import br.com.comoqueta.comoquetasaude.android.activities.NovaAvaliacaoActivity;
import br.com.comoqueta.comoquetasaude.android.models.Avaliacao;
import br.com.comoqueta.comoquetasaude.android.models.UnidadeAtendimento;
import br.com.comoqueta.comoquetasaude.android.widgets.TimePickerFragment;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import com.afollestad.materialdialogs.MaterialDialog;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.ui.ParseLoginBuilder;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import timber.log.Timber;

public class NovaAvaliacaoFragment extends Fragment {
    private static final String TAG = NovaAvaliacaoFragment.class.getName();

    private static final int REQUEST_CODE_GET_TIME = 100;

    private static final int REQUEST_CODE_LOGIN_SIGN_UP = 101;

    @Bind(R.id.main_container) CoordinatorLayout mMainContainer;
    @Bind(R.id.tempo_esperado_layout) TextInputLayout mTempoEsperadoLayout;
    @Bind(R.id.tempo_esperado) EditText mTempoEsperadoText;
    @Bind(R.id.descricao_layout) TextInputLayout mDescricaoLayout;
    @Bind(R.id.descricao) EditText mDescricaoText;

    @Bind(R.id.nota) RatingBar mNotaRatingBar;

    private static final String DIALOG_TIME = "DialogTime";

    public NovaAvaliacaoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nova_avaliacao, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mNotaRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    Snackbar.make(mMainContainer, String.valueOf(rating), Snackbar.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnTouch(R.id.tempo_esperado)
    public boolean onTouch() {
        FragmentManager manager = getFragmentManager();

        if (manager.findFragmentByTag(DIALOG_TIME) != null) {
            return false;
        }

        TimePickerFragment timeDialog = TimePickerFragment
                .newInstance(Calendar.getInstance().getTime());
        timeDialog.setTargetFragment(this, REQUEST_CODE_GET_TIME);
        timeDialog.show(getFragmentManager(), DIALOG_TIME);
        getFragmentManager().executePendingTransactions();

        return false;
    }

    @OnClick(R.id.fab_save)
    public void onClick(View view) {
        final ParseUser currentUser = ParseUser.getCurrentUser();

        if (ParseAnonymousUtils.isLinked(currentUser)) {
            new MaterialDialog.Builder(getActivity())
                    .title("Usuário anônimo")
                    .content(
                            "Você está logado como usuário anônimo. Para realizar uma avaliação é necessário cadastrar/logar.")
                    .positiveText("Cadastro/Login")
                    .negativeText("Cancelar")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            ParseLoginBuilder builder = new ParseLoginBuilder(getActivity());
                            startActivityForResult(builder.build(), REQUEST_CODE_LOGIN_SIGN_UP);
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {

                        }
                    })
                    .show();
            return;
        }

        boolean valid = true;

        if (TextUtils.isEmpty(mTempoEsperadoText.getText())) {
            mTempoEsperadoLayout.setError("É necessário informar o tempo de espera");
            mTempoEsperadoLayout.setErrorEnabled(true);
            valid = false;
        } else {
            mTempoEsperadoLayout.setErrorEnabled(false);
        }

        if (TextUtils.isEmpty(mDescricaoText.getText())) {
            mDescricaoLayout.setError("É necessário informar sua avaliação");
            mDescricaoLayout.setErrorEnabled(true);
            valid = false;
        } else {
            mDescricaoLayout.setErrorEnabled(false);
        }

        if (valid) {
            final MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                    .content("Enviando sua avaliação, aguarde...")
                    .progress(true, 0)
                    .cancelable(false)
                    .show();

            final UnidadeAtendimento unidade = ((NovaAvaliacaoActivity) getActivity())
                    .getUnidadeAtendimento();

            final Avaliacao novaAvaliacao = new Avaliacao()
                    .setAvaliacao(mDescricaoText.getText().toString())
                    .setTempoEspera(mTempoEsperadoText.getText().toString())
                    .setNota((double) mNotaRatingBar.getRating())
                    .setUsuario(currentUser)
                    .setUnidadeAtendimento(unidade);
            novaAvaliacao.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    if (e == null) {
                        //    unidade.addAvaliacao(novaAvaliacao)
                        //        .saveInBackground(new SaveCallback() {
                        //            @Override
                        //            public void done(ParseException e) {
                        //                if (e == null) {
                        Map<String, String> dimensions = new HashMap<>();
                        dimensions.put("idUnidade", unidade.getObjectId());
                        dimensions.put("nomeUnidade", unidade.getNome());
                        dimensions.put("idUsuario", currentUser.getObjectId());
                        dimensions.put("userName", currentUser.getUsername());

                        ParseAnalytics.trackEventInBackground("NovaAvaliacao", dimensions);

                        Snackbar.make(mMainContainer,
                                "Sua avaliação foi registrada com sucesso. Obrigado",
                                Snackbar.LENGTH_LONG).show();
                        getActivity().finish();
                        //                } else {
                        //                    Timber.d(e,
                        //                            "Exception, while saving new Avaliacao to the Parse database.");
                        //                    Snackbar.make(mMainContainer,
                        //                            "Desculpe, houve uma falha e sua avaliação não foi totalmente enviada.",
                        //                            Snackbar.LENGTH_LONG).show();
                        //                }
                        //            }
                        //        });
                    } else {
                        Timber.d(e,
                                "Exception, while saving new Avaliacao to the Parse database.");
                        Snackbar.make(mMainContainer,
                                "Desculpe, houve uma falha e sua avaliação não foi enviada.",
                                Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_GET_TIME:
                    Date time = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
                    mTempoEsperadoText.setText(String.format("%1$tH:%1$tM", time));
                    break;
                case REQUEST_CODE_LOGIN_SIGN_UP:
                    onClick(ButterKnife.findById(getActivity(), R.id.fab_save));
                    break;
            }
        } else if (requestCode == REQUEST_CODE_LOGIN_SIGN_UP) {
            Snackbar.make(mMainContainer, "Cadastro/Login cancelado", Snackbar.LENGTH_SHORT)
                    .show();
        }
    }
}
