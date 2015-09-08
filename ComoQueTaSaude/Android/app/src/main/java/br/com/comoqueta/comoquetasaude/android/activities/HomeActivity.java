package br.com.comoqueta.comoquetasaude.android.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import br.com.comoqueta.comoquetasaude.android.R;
import br.com.comoqueta.comoquetasaude.android.activities.base.BaseAppCompatActivity;
import br.com.comoqueta.comoquetasaude.android.bus.BusProvider;
import br.com.comoqueta.comoquetasaude.android.bus.SearchEvent;
import br.com.comoqueta.comoquetasaude.android.fragments.ListaUnidadesAtendimentoFragment;
import br.com.comoqueta.comoquetasaude.android.ui.helpers.SystemUiHelper;
import br.com.comoqueta.comoquetasaude.android.util.AndroidUtils;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseUser;
import io.doorbell.android.Doorbell;
import java.util.ArrayList;
import java.util.List;

import static android.support.v4.view.ViewPager.SCROLL_STATE_IDLE;
import static com.google.android.gms.location.LocationServices.FusedLocationApi;

public class HomeActivity extends BaseAppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, OnConnectionFailedListener,
        LocationListener, SystemUiHelper.OnVisibilityChangeListener {

    private SearchView mSearchView;
    private MenuItem mSearchMenuItem;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    @Bind(R.id.main_container) CoordinatorLayout mMainContainer;
    @Bind(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @Bind(R.id.viewpager) ViewPager mViewPager;
    @Bind(R.id.nav_view) NavigationView mNavigationView;

    @Bind(R.id.nome) TextView mNomeUsuarioText;
    @Bind(R.id.email) TextView mEmailUsuarioText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeContentView();
        buildGoogleApiClient();

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    private void initializeContentView() {
        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);

        if (mNavigationView != null) {
            setupDrawerContent(mNavigationView);
        }

        if (mViewPager != null) {
            setupViewPager();
        }

        TabLayout tabLayout = ButterKnife.findById(this, R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mSystemUiHelper.delayHide(3000);

        if (!AndroidUtils.isUserLearnedDrawerPref(this)) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            AndroidUtils.savePrefUserLearnedDrawer(this);
        }
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleSearchQuery(intent);
    }

    public void handleSearchQuery(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            onSearch(intent);
        }
    }

    private void onSearch(Intent intent) {
        final String query = intent.getStringExtra(SearchManager.QUERY);
        BusProvider.provideBus().post(new SearchEvent(query, true));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_global, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchMenuItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(mSearchMenuItem);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setQueryRefinementEnabled(true);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                BusProvider.provideBus().post(new SearchEvent(newText, false));
                return false;
            }
        });
        mSearchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                if (TextUtils.isEmpty(mSearchView.getQuery())) {
                    CharSequence newQuery = mSearchView.getSuggestionsAdapter()
                            .convertToString(mSearchView.getSuggestionsAdapter().getCursor());
                    mSearchView.setQuery(newQuery, false);
                }
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager() {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new ListaUnidadesAtendimentoFragment(), "Unidades de Atendimento");
        //adapter.addFragment(new ListaUnidadesFavoritasFragment(), "Atendimento Preferido");
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                    int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == SCROLL_STATE_IDLE) {
                    if (mViewPager.getCurrentItem() == 0) {
                        mNavigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
                    } else {
                        //mNavigationView.getMenu().findItem(R.id.nav_favorites).setChecked(true);
                    }
                }
            }
        });
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.nav_home:
                                mViewPager.setCurrentItem(0, true);
                                break;
                            //case R.id.nav_favorites:
                                //mViewPager.setCurrentItem(1, true);
                                //break;
                            case R.id.nav_rate:
                                AndroidUtils.showThisAppOnPlayStore(HomeActivity.this);
                                break;
                            case R.id.nav_feedback:
                                new Doorbell(HomeActivity.this, 2012,
                                        getString(R.string.doorbell_api_key))
                                        .show();
                                break;
                            case R.id.nav_contact:
                                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                        "mailto", "comoqueta@outlook.com", null));

                                if (emailIntent.resolveActivity(getPackageManager()) != null) {
                                    startActivity(Intent.createChooser(emailIntent, "Enviar email…"));
                                } else {
                                    Snackbar.make(mMainContainer,
                                            "Você não possui aplicativo para enviar e-mail",
                                            Snackbar.LENGTH_SHORT).show();
                                }
                                break;
                            case R.id.nav_share:
                                final Intent shareIntent = AndroidUtils.createShareIntent(
                                        HomeActivity.this);
                                startActivity(shareIntent);
                                break;
                        }

                        if (menuItem.isCheckable()) {
                            menuItem.setChecked(true);
                        }

                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (!ParseAnonymousUtils.isLinked(currentUser)) {
            mNomeUsuarioText.setText(currentUser.getEmail());
        }
    }

    @Override
    public boolean onKeyUp(final int keyCode, final KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            } else {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mLastLocation != null) {
            final LatLng lastLatLng = new LatLng(mLastLocation.getLatitude(),
                    mLastLocation.getLongitude());
            final LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

            // less than 10 meters, returns
            if (SphericalUtil.computeDistanceBetween(lastLatLng, currentLatLng) < 10) {
                return;
            }
        }

        mLastLocation = location;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onVisibilityChange(boolean visible) {

    }

    private static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}
