package br.com.comoqueta.comoquetasaude.android.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import br.com.comoqueta.comoquetasaude.android.R;
import br.com.comoqueta.comoquetasaude.android.activities.MapActivity;
import br.com.comoqueta.comoquetasaude.android.activities.StreetViewActivity;
import br.com.comoqueta.comoquetasaude.android.bus.AddedUnidadePreferidaCompletedEvent;
import br.com.comoqueta.comoquetasaude.android.bus.AddingUnidadePreferidaFailedEvent;
import br.com.comoqueta.comoquetasaude.android.bus.BusProvider;
import br.com.comoqueta.comoquetasaude.android.bus.LoadingListDataBeganEvent;
import br.com.comoqueta.comoquetasaude.android.bus.LoadingListDataCompletedEmptyEvent;
import br.com.comoqueta.comoquetasaude.android.bus.LoadingListDataCompletedEvent;
import br.com.comoqueta.comoquetasaude.android.bus.LoadingListDataFailedEvent;
import br.com.comoqueta.comoquetasaude.android.bus.NotifyLocationUnvailableEvent;
import br.com.comoqueta.comoquetasaude.android.bus.RemovedUnidadePreferidaCompletedEvent;
import br.com.comoqueta.comoquetasaude.android.bus.RemovingUnidadePreferidaFailedEvent;
import br.com.comoqueta.comoquetasaude.android.bus.UnidadeAtendimentoSelectedEvent;
import br.com.comoqueta.comoquetasaude.android.models.UnidadeAtendimento;
import br.com.comoqueta.comoquetasaude.android.models.UnidadesFavoritas;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import timber.log.Timber;

import static br.com.comoqueta.comoquetasaude.android.models.UnidadeAtendimento.getQuery;
import static com.parse.ParseUser.getCurrentUser;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 15/08/2015
 * @since #
 */
public class UnidadesAtendimentoAdapter
        extends RecyclerView.Adapter<UnidadesAtendimentoAdapter.ViewHolder>
        implements Filterable {

    private static final String TAG = UnidadesAtendimentoAdapter.class.getName();

    private List<UnidadeAtendimento> mUnidadesAtendimento = new ArrayList<>();
    private List<UnidadeAtendimento> mUnidadesAtendimentoFiltradas = new ArrayList<>();
    private Context mContext;

    public UnidadesAtendimentoAdapter() {
        Timber.tag(TAG);
        new LoadDataTask().execute();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final UnidadeAtendimento unidade = mUnidadesAtendimentoFiltradas.size() == 0 ?
                mUnidadesAtendimento.get(position) :
                mUnidadesAtendimentoFiltradas.get(position);

        holder.mItemPosition = position;
        holder.mUnitNameText.setText(unidade.getNome());
        //holder.mAddressText.setText(unidade.getLogradouro());
        holder.mPhone1Text.setText(unidade.getTelefone1());
        holder.mPhone2Text.setText(unidade.getTelefone2());
        holder.itemView.setTag(unidade);

        Linkify.addLinks(holder.mPhone1Text, Linkify.PHONE_NUMBERS);
        Linkify.addLinks(holder.mPhone2Text, Linkify.PHONE_NUMBERS);

        holder.inflateMenu();
    }

    @Override
    public int getItemCount() {
        //TODO empty state? draw something cool
        return mUnidadesAtendimentoFiltradas.size() == 0 ?
                mUnidadesAtendimento.size() :
                mUnidadesAtendimentoFiltradas.size();
    }

    public void tryLoadData() {
        new LoadDataTask().execute();
    }

    @Override
    public Filter getFilter() {
        return new UnidadesFilter(this, mUnidadesAtendimento);
    }

    public void clearFilter() {
        if (mUnidadesAtendimentoFiltradas.size() != 0) {
            mUnidadesAtendimentoFiltradas.clear();
            notifyDataSetChanged();
        }
    }

    class UnidadesFilter extends Filter {

        private final UnidadesAtendimentoAdapter mAdapter;
        private final List<UnidadeAtendimento> mOriginalList;
        private final List<UnidadeAtendimento> mFilteredList;

        public UnidadesFilter(UnidadesAtendimentoAdapter adapter, List<UnidadeAtendimento> list) {
            super();
            mAdapter = adapter;
            mOriginalList = new LinkedList<>(list);
            mFilteredList = new ArrayList<>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            mFilteredList.clear();
            final FilterResults results = new FilterResults();

            if (constraint.length() == 0) {
                mFilteredList.addAll(mOriginalList);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();

                for (final UnidadeAtendimento unidade : mOriginalList) {
                    if (unidade.getNome().toLowerCase().contains(constraint)) {
                        mFilteredList.add(unidade);
                    }
                }
            }

            results.values = mFilteredList;
            results.count = mFilteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mAdapter.mUnidadesAtendimentoFiltradas.clear();
            mAdapter.mUnidadesAtendimentoFiltradas.addAll(
                    (ArrayList<UnidadeAtendimento>) results.values);
            mAdapter.notifyDataSetChanged();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements Toolbar.OnMenuItemClickListener {
        @Bind(R.id.unit_name) TextView mUnitNameText;
        //@Bind(R.id.address) TextView mAddressText;
        @Bind(R.id.phone1) TextView mPhone1Text;
        @Bind(R.id.phone2) TextView mPhone2Text;
        @Bind(R.id.toolbar) Toolbar mToolbarAsMenu;

        int mItemPosition;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BusProvider.provideBus().post(new UnidadeAtendimentoSelectedEvent(
                            (UnidadeAtendimento) v.getTag()));
                }
            });
        }

        private UnidadeAtendimento getItemFromList(int itemPosition) {
            return mUnidadesAtendimento.get(itemPosition);
        }

        private void inflateMenu() {
            mToolbarAsMenu.setOnMenuItemClickListener(this);
            mToolbarAsMenu.inflateMenu(R.menu.menu_toolbar_lista_unidades_atendimento);
            invalidateToolbarMenu();
        }

        private void invalidateToolbarMenu() {
            final boolean isFavorite = getItemFromList(mItemPosition).isThisFavoriteToCurrentUser();

            mToolbarAsMenu.getMenu()
                    .findItem(R.id.action_add_to_favorites)
                    .setVisible(!isFavorite);
            mToolbarAsMenu.getMenu()
                    .findItem(R.id.action_remove_from_favorites)
                    .setVisible(isFavorite);
        }

        void addUnidadeAtendimentoToFavorites() {
            final UnidadeAtendimento itemFromList = getItemFromList(mItemPosition);

            if (!itemFromList.isThisFavoriteToCurrentUser()) {
                // Salva a unidade na relação de favoritos assim que houver conexão
                try {
                    new UnidadesFavoritas()
                            .addUnidadeFavorita(itemFromList, new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Timber.i(
                                                "UnidadeAtendimento %s:%s successfully added to favorites",
                                                itemFromList.getObjectId(), itemFromList.getNome());

                                        // Marca unidade como favorita para o usuário corrente
                                        itemFromList.setAsFavoriteToCurrentUser(true);

                                        // Atualiza a visibilidade dos menus
                                        invalidateToolbarMenu();

                                        BusProvider.provideBus()
                                                .post(new AddedUnidadePreferidaCompletedEvent(
                                                        itemFromList));
                                    } else {
                                        ParseUser currentUser = getCurrentUser();
                                        Timber.d(e,
                                                "Exception, while adding the UnidadesPreferidas "
                                                        + " %s:%s to Parse database for user",
                                                itemFromList.getObjectId(), itemFromList.getNome(),
                                                currentUser.getUsername());

                                        BusProvider.provideBus()
                                                .post(new AddingUnidadePreferidaFailedEvent(e));
                                    }
                                }
                            });
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        void removeUnidadeAtendimentoFromFavorites() {
            final UnidadeAtendimento itemFromList = getItemFromList(mItemPosition);

            if (itemFromList.isThisFavoriteToCurrentUser()) {
                try {
                    new UnidadesFavoritas()
                            .removeUnidadeFavorita(itemFromList, new DeleteCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Timber.i(
                                                "UnidadeAtendimento %s:%s successfully removed to favorites",
                                                itemFromList.getObjectId(), itemFromList.getNome());

                                        // Desmarca unidade como favorita para o usuário corrente
                                        itemFromList.setAsFavoriteToCurrentUser(false);

                                        // Atualiza a visibilidade dos menus
                                        invalidateToolbarMenu();

                                        BusProvider.provideBus()
                                                .post(new RemovedUnidadePreferidaCompletedEvent(
                                                        itemFromList));
                                    } else {
                                        ParseUser currentUser = getCurrentUser();
                                        Timber.d(e,
                                                "Exception, while adding the UnidadesPreferidas "
                                                        + " %s:%s to Parse database for user",
                                                itemFromList.getObjectId(), itemFromList.getNome(),
                                                currentUser.getUsername());

                                        BusProvider.provideBus()
                                                .post(new RemovingUnidadePreferidaFailedEvent(
                                                        e));
                                    }
                                }
                            });
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            final UnidadeAtendimento itemFromList = getItemFromList(mItemPosition);

            switch (item.getItemId()) {
                case R.id.action_see_in_map:
                    if (itemFromList.getGeoLocalizacao() == null) {
                        BusProvider.provideBus().post(new NotifyLocationUnvailableEvent(itemFromList));
                    } else {
                        Intent mapIntent = new Intent(mContext, MapActivity.class);
                        mapIntent.putExtra(MapActivity.EXTRA_UNIDADE_ATENDIMENTO, itemFromList);
                        mContext.startActivity(mapIntent);
                    }
                    break;
                case R.id.action_see_in_street:
                    if (itemFromList.getGeoLocalizacao() == null) {
                        BusProvider.provideBus().post(new NotifyLocationUnvailableEvent(itemFromList));
                    } else {
                        Intent streetViewIntent = new Intent(mContext, StreetViewActivity.class);
                        streetViewIntent.putExtra(StreetViewActivity.EXTRA_UNIDADE_ATENDIMENTO,
                                itemFromList);
                        mContext.startActivity(streetViewIntent);
                    }
                    break;
                case R.id.action_add_to_favorites:
                    addUnidadeAtendimentoToFavorites();
                    break;
                case R.id.action_remove_from_favorites:
                    removeUnidadeAtendimentoFromFavorites();
                    break;
            }

            return true;
        }
    }

    class LoadDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            //TODO make UnidadesAtendimentoListFragment implements Provide bus strategy
            BusProvider.provideBus().post(new LoadingListDataBeganEvent());
        }

        @Override
        protected Void doInBackground(Void... params) {
            loadDataLocally();
            return null;
        }

        private void loadDataLocally() {
            Timber.i("Loading UnidadeAtendimento data locally!");
            ParseQuery<UnidadeAtendimento> localQuery = getQuery(true);
            //TODO ordering?

            localQuery.findInBackground(new FindCallback<UnidadeAtendimento>() {
                @Override
                public void done(List<UnidadeAtendimento> localObjects, ParseException e) {
                    if (e == null) {
                        if (localObjects == null || localObjects.size() == 0) {
                            loadFromNetwork();
                        } else {
                            Timber.i("Finished loading local UnidadeAtendimento data.");

                            for (final UnidadeAtendimento unidade : localObjects) {
                                //TODO always find Unidades favoritas no banco remoto
                                setUnidadeAsFavoriteToCurrentUser(unidade, false);
                                mUnidadesAtendimento.add(unidade);
                            }

                            notifyDataSetChanged();

                            BusProvider.provideBus().post(new LoadingListDataCompletedEvent());
                        }
                    } else {
                        Timber.d(e, "Exception, while querying the UnidadeAtendimento List "
                                + "from Local database.");
                        BusProvider.provideBus().post(new LoadingListDataFailedEvent(e));
                        //TODO empty state? draw something cool
                    }
                }
            });
        }

        private void loadFromNetwork() {
            Timber.i("Loading UnidadeAtendimento data from the Network!");
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
                                setUnidadeAsFavoriteToCurrentUser(unidade, false);
                                mUnidadesAtendimento.add(unidade);
                                unidade.pinInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Timber.i("Saving UnidadeAtendimento data Locally!");
                                        } else {
                                            Timber.e(e,
                                                    "Exception, while saving UnidadeAtendimento locally");
                                        }
                                    }
                                });
                            }
                            notifyDataSetChanged();

                            BusProvider.provideBus().post(new LoadingListDataCompletedEvent());
                        }
                    } else {
                        Timber.d(e,
                                "Exception, while querying the UnidadeAtendimento List from Parse database.");
                        BusProvider.provideBus().post(new LoadingListDataFailedEvent(e));
                        //TODO empty state? draw something cool
                    }
                }
            });
        }

        private void setUnidadeAsFavoriteToCurrentUser(final UnidadeAtendimento unidade,
                boolean findLocally) {
            try {
                if (UnidadesFavoritas.isUnidadeFavoritaToCurrentUser(unidade)) {
                    unidade.setAsFavoriteToCurrentUser(true);
                }
            } catch (ParseException e) {
                e.printStackTrace();
                //TODO log error
                //TODO notify error
            }
        }
    }
}
