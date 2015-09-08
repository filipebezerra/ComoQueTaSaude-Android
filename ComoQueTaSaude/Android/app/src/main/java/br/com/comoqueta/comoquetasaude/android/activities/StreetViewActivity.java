package br.com.comoqueta.comoquetasaude.android.activities;

import android.os.Bundle;
import br.com.comoqueta.comoquetasaude.android.R;
import br.com.comoqueta.comoquetasaude.android.activities.base.ImmersiveActivity;
import br.com.comoqueta.comoquetasaude.android.models.UnidadeAtendimento;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.parse.ParseGeoPoint;

public class StreetViewActivity extends ImmersiveActivity
        implements OnStreetViewPanoramaReadyCallback {
    private static final String TAG = StreetViewActivity.class.getName();

    public static final String EXTRA_UNIDADE_ATENDIMENTO = TAG + ".unidade_atendimento";

    private StreetViewPanorama mStreetViewPanorama;

    private UnidadeAtendimento mUnidadeAtendimento;

    private LatLng mUnidadeAtendimentoLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null && getIntent().getExtras() != null &&
                getIntent().hasExtra(EXTRA_UNIDADE_ATENDIMENTO)) {
            mUnidadeAtendimento = getIntent().getExtras().getParcelable(EXTRA_UNIDADE_ATENDIMENTO);
            ParseGeoPoint geoLocalizacao = mUnidadeAtendimento.getGeoLocalizacao();

            if (geoLocalizacao != null) {
                mUnidadeAtendimentoLatLng = new LatLng(geoLocalizacao.getLatitude(),
                        geoLocalizacao.getLongitude());
            }
        }

        SupportStreetViewPanoramaFragment fragment = (SupportStreetViewPanoramaFragment)
                getSupportFragmentManager().findFragmentById(R.id.street_view_panorama_fragment);

        fragment.getStreetViewPanoramaAsync(this);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_street_view;
    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        mStreetViewPanorama = streetViewPanorama;

        mStreetViewPanorama.setPosition(mUnidadeAtendimentoLatLng);

        StreetViewPanoramaCamera camera = StreetViewPanoramaCamera.builder()
                .bearing(180)
                .build();

        mStreetViewPanorama.animateTo(camera, 1000);
    }
}
