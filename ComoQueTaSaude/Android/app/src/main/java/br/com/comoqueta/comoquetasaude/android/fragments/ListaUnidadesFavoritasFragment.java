package br.com.comoqueta.comoquetasaude.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import br.com.comoqueta.comoquetasaude.android.R;
import br.com.comoqueta.comoquetasaude.android.adapters.UnidadesAtendimentoFavoritasAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;

public class ListaUnidadesFavoritasFragment extends Fragment {
    @Bind(R.id.recyclerview)protected RecyclerView mRecyclerView;
    private UnidadesAtendimentoFavoritasAdapter mRecyclerAdapter;

    public ListaUnidadesFavoritasFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_unidades_favoritas, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mRecyclerAdapter = new UnidadesAtendimentoFavoritasAdapter());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
