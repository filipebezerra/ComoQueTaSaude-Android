package br.com.comoqueta.comoquetasaude.android.activities;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import br.com.comoqueta.comoquetasaude.android.R;
import br.com.comoqueta.comoquetasaude.android.activities.base.ImmersiveActivity;
import br.com.comoqueta.comoquetasaude.android.models.UnidadeAtendimento;
import butterknife.Bind;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;
import com.parse.ParseGeoPoint;

import static com.google.android.gms.location.LocationServices.FusedLocationApi;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 17/08/2015
 * @since #
 */
public class MapActivity extends ImmersiveActivity
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private static final String TAG = MapActivity.class.getName();

    public static final String EXTRA_UNIDADE_ATENDIMENTO = TAG + ".unidade_atendimento";

    private GoogleApiClient mGoogleApiClient;

    private Location mLastLocation;

    private boolean mIsMapReady = false;
    private GoogleMap mGoogleMap;

    private UnidadeAtendimento mUnidadeAtendimento;

    private LatLng mUnidadeAtendimentoLatLng;

    //TODO generalize into BaseAppCompatActivity
    @Bind(R.id.map_container) protected CoordinatorLayout mMainContainer;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_map;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildGoogleApiClient();

        if (getIntent() != null && getIntent().getExtras() != null &&
                getIntent().hasExtra(EXTRA_UNIDADE_ATENDIMENTO)) {
            mUnidadeAtendimento = getIntent().getExtras().getParcelable(EXTRA_UNIDADE_ATENDIMENTO);
            ParseGeoPoint geoLocalizacao = mUnidadeAtendimento.getGeoLocalizacao();

            if (geoLocalizacao != null) {
                mUnidadeAtendimentoLatLng = new LatLng(geoLocalizacao.getLatitude(),
                        geoLocalizacao.getLongitude());
            }
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
    protected void onStop() {
        super.onStop();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
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
        //TODO treat connections errors
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mIsMapReady = true;
        mGoogleMap = googleMap;
        mGoogleMap.setMyLocationEnabled(false);
        mGoogleMap.setTrafficEnabled(false);
        final UiSettings uiSettings = mGoogleMap.getUiSettings();
        uiSettings.setMapToolbarEnabled(false);

        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getSnippet().contains("indisponível")) {
                    if (mLastLocation == null) {
                        mLastLocation = FusedLocationApi.getLastLocation(mGoogleApiClient);
                    }

                    if (mLastLocation != null) {
                        double distanceInKilometersTo = mUnidadeAtendimento.getGeoLocalizacao()
                                .distanceInKilometersTo(
                                        new ParseGeoPoint(mLastLocation.getLatitude(),
                                                mLastLocation.getLongitude())
                                );

                        marker.setSnippet(
                                String.format("Distância: %f Km", distanceInKilometersTo));
                    }
                }

                return false;
            }
        });

        navigateToLocation();
    }

    private void navigateToLocation() {
        if (mUnidadeAtendimentoLatLng != null) {
            moveCamera(new LatLng(mUnidadeAtendimento.getGeoLocalizacao().getLatitude(),
                    mUnidadeAtendimento.getGeoLocalizacao().getLongitude()));
        }
    }

    private void moveCamera(final LatLng latLng) {
        if (mIsMapReady) {
            CameraPosition cameraPosition = CameraPosition.builder()
                    .tilt(65)
                    .bearing(175)
                    .zoom(14)
                    .target(latLng)
                    .build();

            animateCameraIfMapIsReady(cameraPosition);
        }
    }

    private void animateCameraIfMapIsReady(final CameraPosition cameraPosition) {
        final LatLng target = cameraPosition.target;
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                10000, new GoogleMap.CancelableCallback() {
                    @Override
                    public void onFinish() {
                        double distanceInKilometersTo = 0;

                        mLastLocation = FusedLocationApi.getLastLocation(mGoogleApiClient);

                        if (mLastLocation != null) {
                            distanceInKilometersTo = mUnidadeAtendimento.getGeoLocalizacao()
                                    .distanceInKilometersTo(
                                            new ParseGeoPoint(mLastLocation.getLatitude(),
                                                    mLastLocation.getLongitude())
                                    );

                        }

                        String distancia;

                        if (distanceInKilometersTo == 0) {
                            distancia = "Distância: indisponível";
                        } else {
                            distancia = String.format("Distância: %f Km", distanceInKilometersTo);
                        }

                        addMarker(target,
                                mUnidadeAtendimento.getNome(),
                                distancia,
                                R.mipmap.ic_launcher);
                    }

                    @Override
                    public void onCancel() {
                        Snackbar.make(mMainContainer, "Viagem à unidade de atendimento cancelada!",
                                Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    private void addCircle(final LatLng latLng) {
        mGoogleMap.addCircle(new CircleOptions()
                        .center(latLng)
                        .radius(500)
                        .strokeColor(Color.BLUE)
                        .strokeWidth(5)
                        .fillColor(Color.argb(64, 0, 0, 255))
        );
    }

    private void addMarker(final LatLng latLng, final String title, final String snippet,
            @DrawableRes int iconResId) {
        MarkerOptions marker = new MarkerOptions()
                .position(latLng)
                .draggable(false)
                .title(title)
                .snippet(snippet)
                .icon(getMakerIconFromResource(iconResId));

        mGoogleMap.addMarker(marker)
                .showInfoWindow();
    }

    private BitmapDescriptor getMakerIconFromResource(@DrawableRes int iconResId) {
        return BitmapDescriptorFactory.fromResource(iconResId);
    }
}
