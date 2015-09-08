package br.com.comoqueta.comoquetasaude.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import br.com.comoqueta.comoquetasaude.android.R;
import br.com.comoqueta.comoquetasaude.android.activities.UnidadeAtendimentoActivity;
import br.com.comoqueta.comoquetasaude.android.adapters.UnidadesAtendimentoAdapter;
import br.com.comoqueta.comoquetasaude.android.bus.BusProvider;
import br.com.comoqueta.comoquetasaude.android.bus.LoadingListDataBeganEvent;
import br.com.comoqueta.comoquetasaude.android.bus.LoadingListDataCompletedEmptyEvent;
import br.com.comoqueta.comoquetasaude.android.bus.LoadingListDataCompletedEvent;
import br.com.comoqueta.comoquetasaude.android.bus.LoadingListDataFailedEvent;
import br.com.comoqueta.comoquetasaude.android.bus.NotifyLocationUnvailableEvent;
import br.com.comoqueta.comoquetasaude.android.bus.SearchEvent;
import br.com.comoqueta.comoquetasaude.android.bus.UnidadeAtendimentoSelectedEvent;
import br.com.comoqueta.comoquetasaude.android.models.UnidadeAtendimento;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.afollestad.materialdialogs.MaterialDialog;
import com.parse.ParseAnalytics;
import com.squareup.otto.Subscribe;
import java.util.HashMap;
import java.util.Map;

import static br.com.comoqueta.comoquetasaude.android.providers.SuggestionsProvider.AUTHORITY;
import static br.com.comoqueta.comoquetasaude.android.providers.SuggestionsProvider.MODE;

public class ListaUnidadesAtendimentoFragment extends Fragment {
    @Bind(R.id.recyclerview) protected RecyclerView mRecyclerView;
    private UnidadesAtendimentoAdapter mRecyclerAdapter;

    private MaterialDialog mDialog;

    public ListaUnidadesAtendimentoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_unidades_atendimento, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerAdapter = new UnidadesAtendimentoAdapter();
        mRecyclerView.setAdapter(mRecyclerAdapter);

        if (mDialog == null || !mDialog.isShowing()) {
            mDialog = new MaterialDialog.Builder(getActivity())
                    .content("Carregando unidades de atendimento...")
                    .progress(true, 0)
                    .cancelable(false)
                    .show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        BusProvider.provideBus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        BusProvider.provideBus().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void dismissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    @Subscribe
    public void OnLoadingListDataBeganEvent(LoadingListDataBeganEvent event) {
        //TODO register using Provide
        mDialog = new MaterialDialog.Builder(getActivity())
                .content("Carregando unidades de atendimento...")
                .progress(true, 0)
                .cancelable(false)
                .show();
    }

    @Subscribe
    public void OnLoadingListDataCompletedEvent(LoadingListDataCompletedEvent event) {
        dismissDialog();
    }

    @Subscribe
    public void OnLoadingListDataCompletedEmptyEvent(LoadingListDataCompletedEmptyEvent event) {
        ParseAnalytics.trackEventInBackground("LoadingListDataCompletedEmptyEvent");
        dismissDialog();
    }

    @Subscribe
    public void OnLoadingListDataFailedEvent(LoadingListDataFailedEvent event) {
        Map<String, String> dimensions = new HashMap<>();
        dimensions.put("errorCode", String.valueOf(event.getException().getCode()));
        dimensions.put("errorMessage", event.getException().getMessage());
        ParseAnalytics.trackEventInBackground("LoadingListDataFailedEvent", dimensions);

        dismissDialog();
        Snackbar
                .make(getView(), "Desculpe, não foi possível carregar as unidades de atendimento.",
                        Snackbar.LENGTH_LONG)
                .setAction("Tentar novamente", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mRecyclerAdapter.tryLoadData();
                    }
                })
                .show();
    }

    @Subscribe
    public void OnNotifyLocationUnvailableEvent(NotifyLocationUnvailableEvent event) {
        Map<String, String> dimensions = new HashMap<>();
        dimensions.put("idUnidade", event.getUnidadeAtendimento().getObjectId());
        dimensions.put("nomeUnidade", event.getUnidadeAtendimento().getNome());
        ParseAnalytics.trackEventInBackground("NotifyLocationUnvailableEvent", dimensions);

        Snackbar.make(getView(),
                "Desculpe, a localização desta unidade de atendimento está indisponível",
                Snackbar.LENGTH_LONG).show();
    }

    @Subscribe
    public void OnUnidadeAtendimentoSelectedEvent(UnidadeAtendimentoSelectedEvent event) {
        UnidadeAtendimento unidadeAtendimento = event.getUnidadeAtendimento();

        Map<String, String> dimensions = new HashMap<>();
        dimensions.put("idUnidade", unidadeAtendimento.getObjectId());
        dimensions.put("nomeUnidade", unidadeAtendimento.getNome());
        ParseAnalytics.trackEventInBackground("UnidadeAtendimentoSelectedEvent", dimensions);

        Intent unidadeAtendimentoIntent = new Intent(getActivity(), UnidadeAtendimentoActivity.class);
        unidadeAtendimentoIntent.putExtra(UnidadeAtendimentoActivity.EXTRA_UNIDADE_ATENDIMENTO,
                unidadeAtendimento);

        startActivity(unidadeAtendimentoIntent);
    }

    @Subscribe
    public void OnSearchEvent(SearchEvent event) {
        String query = event.getQuery();
        if (TextUtils.isEmpty(query)) {
            mRecyclerAdapter.clearFilter();
        } else {
            mRecyclerAdapter.getFilter().filter(query);

            if (event.isSubmitted()) {
                new SearchRecentSuggestions(getActivity(), AUTHORITY, MODE)
                        .saveRecentQuery(query, null);
            }
        }
    }
}
