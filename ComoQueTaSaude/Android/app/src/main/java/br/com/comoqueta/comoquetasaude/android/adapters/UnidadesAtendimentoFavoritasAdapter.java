package br.com.comoqueta.comoquetasaude.android.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import br.com.comoqueta.comoquetasaude.android.R;
import br.com.comoqueta.comoquetasaude.android.activities.MapActivity;
import br.com.comoqueta.comoquetasaude.android.bus.BusProvider;
import br.com.comoqueta.comoquetasaude.android.bus.LoadingListDataBeganEvent;
import br.com.comoqueta.comoquetasaude.android.bus.LoadingListDataCompletedEmptyEvent;
import br.com.comoqueta.comoquetasaude.android.bus.LoadingListDataCompletedEvent;
import br.com.comoqueta.comoquetasaude.android.bus.LoadingListDataFailedEvent;
import br.com.comoqueta.comoquetasaude.android.bus.RemovedUnidadePreferidaCompletedEvent;
import br.com.comoqueta.comoquetasaude.android.bus.RemovingUnidadePreferidaFailedEvent;
import br.com.comoqueta.comoquetasaude.android.models.UnidadeAtendimento;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 15/08/2015
 * @since #
 */
public class UnidadesAtendimentoFavoritasAdapter
        extends RecyclerView.Adapter<UnidadesAtendimentoFavoritasAdapter.ViewHolder> {
    private static final String TAG = UnidadesAtendimentoFavoritasAdapter.class.getName();

    private List<UnidadeAtendimento> mUnidadesAtendimentoPreferidas = new ArrayList<>();
    private @NonNull Context mContext;

    public UnidadesAtendimentoFavoritasAdapter() {
        Timber.tag(TAG);
        loadDataLocally();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();

        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final UnidadeAtendimento unidade = mUnidadesAtendimentoPreferidas.get(position);

        holder.mUnitNameText.setText(unidade.getNome());
        holder.mAddressText.setText(unidade.getLogradouro());
        holder.mPhone1Text.setText(unidade.getTelefone1());
        holder.mPhone2Text.setText(unidade.getTelefone2());
        holder.mUnidadeAtendimentoPreferidaItem = unidade;

        Linkify.addLinks(holder.mPhone1Text, Linkify.PHONE_NUMBERS);
        Linkify.addLinks(holder.mPhone2Text, Linkify.PHONE_NUMBERS);
    }

    @Override
    public int getItemCount() {
        //TODO empty state? draw something cool
        return mUnidadesAtendimentoPreferidas.size();
    }

    private void loadDataLocally() {
        Timber.i("Loading UnidadesPreferidas data locally!");
        ParseQuery<UnidadeAtendimento> localQuery = getQuery(true);
        //TODO ordering?

        BusProvider.provideBus().post(new LoadingListDataBeganEvent());

        localQuery.findInBackground(new FindCallback<UnidadeAtendimento>() {
            @Override
            public void done(List<UnidadeAtendimento> localObjects, ParseException e) {
                if (e == null) {
                    if (localObjects == null || localObjects.size() == 0) {
                        loadFromNetwork();
                    } else {
                        Timber.i("Finished loading UnidadesPreferidas local data.");

                        mUnidadesAtendimentoPreferidas.addAll(localObjects);
                        notifyDataSetChanged();

                        BusProvider.provideBus().post(new LoadingListDataCompletedEvent());
                    }
                } else {
                    Timber.d(e, "Exception, while querying the UnidadesPreferidas List from Local database.");
                    BusProvider.provideBus().post(new LoadingListDataFailedEvent(e));
                    //TODO empty state? draw something cool
                }
            }
        });
    }

    @NonNull
    private ParseQuery<UnidadeAtendimento> getQuery(boolean findLocally) {
        // Usuário atual
        ParseUser currentUser = ParseUser.getCurrentUser();

        // Carrega relação do usuário atual com unidades favoritadas
        ParseRelation<UnidadeAtendimento> relation = currentUser.getRelation("UnidadesPreferidas");

        // Consulta das unidades favoritadas para o usuário atual
        ParseQuery<UnidadeAtendimento> localQuery = relation.getQuery();

        // Consulta feita localmente
        if (findLocally) {
            localQuery.fromLocalDatastore();
        }

        return localQuery;
    }

    private void loadFromNetwork() {
        Timber.i("Loading UnidadesPreferidas data from the Network!");
        ParseQuery<UnidadeAtendimento> query = getQuery(false);
        //TODO ordering?

        query.findInBackground(new FindCallback<UnidadeAtendimento>() {
            @Override
            public void done(List<UnidadeAtendimento> objects, ParseException e) {
                if (e == null) {
                    if (objects == null || objects.size() == 0) {
                        //TODO empty state? draw something cool

                        BusProvider.provideBus().post(new LoadingListDataCompletedEmptyEvent());
                    } else {
                        for (UnidadeAtendimento unidade : objects) {
                            mUnidadesAtendimentoPreferidas.add(unidade);
                            unidade.pinInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Timber.i("Saving UnidadesPreferidas data Locally!");
                                    } else {
                                        Timber.e(e,
                                                "Exception, while saving UnidadesPreferidas locally");
                                    }
                                }
                            });
                        }
                        notifyDataSetChanged();

                        BusProvider.provideBus().post(new LoadingListDataCompletedEvent());
                    }
                } else {
                    Timber.d(e, "Exception, while querying the UnidadesPreferidas List from Parse database.");
                    BusProvider.provideBus().post(new LoadingListDataFailedEvent(e));
                    //TODO empty state? draw something cool
                }
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.unit_name) protected TextView mUnitNameText;
        @Bind(R.id.address) protected TextView mAddressText;
        @Bind(R.id.phone1) protected TextView mPhone1Text;
        @Bind(R.id.phone2) protected TextView mPhone2Text;
        @Bind(R.id.toolbar) protected Toolbar mToolbarAsMenu;

        protected UnidadeAtendimento mUnidadeAtendimentoPreferidaItem;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mToolbarAsMenu.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_see_in_map:
                            Intent mapIntent = new Intent();
                            mapIntent.putExtra(MapActivity.EXTRA_UNIDADE_ATENDIMENTO,
                                    mUnidadeAtendimentoPreferidaItem);
                            mContext.startActivity(mapIntent);
                            break;
                        case R.id.action_remove_from_favorites:
                            removeUnidadeAtendimentoFromFavorites();
                            break;
                    }
                    return true;
                }
            });

            mToolbarAsMenu.inflateMenu(R.menu.menu_toolbar_lista_unidades_atendimento);
        }

        private void removeUnidadeAtendimentoFromFavorites() {
            if (mUnidadeAtendimentoPreferidaItem.isThisFavoriteToCurrentUser()) {
                // Redesenha o item da lista de unidades que foi removido
                final int position = mUnidadesAtendimentoPreferidas.indexOf(
                        mUnidadeAtendimentoPreferidaItem);
                mUnidadesAtendimentoPreferidas.remove(mUnidadeAtendimentoPreferidaItem);
                notifyItemRemoved(position);

                // Recupera a relação do usuário corrente com as unidades favoritadas
                final ParseUser currentUser = ParseUser.getCurrentUser();
                final ParseRelation<ParseObject> relation = currentUser
                        .getRelation("UnidadesPreferidas");

                // Remove a relação com o usuário corrente
                relation.remove(mUnidadeAtendimentoPreferidaItem);

                // Salva a remoção quando houver conexão
                currentUser.saveEventually(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Timber.i("UnidadeAtendimento %s:%s successfully removed to favorites",
                                    mUnidadeAtendimentoPreferidaItem.getObjectId(),
                                    mUnidadeAtendimentoPreferidaItem.getNome());

                            BusProvider.provideBus().post(new RemovedUnidadePreferidaCompletedEvent(
                                    mUnidadeAtendimentoPreferidaItem));
                        } else {
                            Timber.d(e,
                                    "Exception, while removing the UnidadesPreferidas "
                                            + " %s:%s to Parse database for user",
                                    mUnidadeAtendimentoPreferidaItem.getObjectId(),
                                    mUnidadeAtendimentoPreferidaItem.getNome(),
                                    currentUser.getUsername());

                            BusProvider.provideBus()
                                    .post(new RemovingUnidadePreferidaFailedEvent(e));
                        }
                    }
                });
            }
        }
    }
}
