package br.com.comoqueta.comoquetasaude.android.activities.base;

import android.os.Bundle;
import br.com.comoqueta.comoquetasaude.android.ui.helpers.SystemUiHelper;

import static br.com.comoqueta.comoquetasaude.android.ui.helpers.SystemUiHelper.FLAG_IMMERSIVE_STICKY;
import static br.com.comoqueta.comoquetasaude.android.ui.helpers.SystemUiHelper.LEVEL_HIDE_STATUS_BAR;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.0.1, 06/08/2015
 * @since 0.0.1
 */
public abstract class ImmersiveActivity extends BaseAppCompatActivity
        implements SystemUiHelper.OnVisibilityChangeListener {

    protected SystemUiHelper mSystemUiHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSystemUiHelper = new SystemUiHelper(this, LEVEL_HIDE_STATUS_BAR, FLAG_IMMERSIVE_STICKY,
                this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSystemUiHelper.hide();
    }

    @Override
    public void onVisibilityChange(boolean visible) {
        if (visible) {
            mSystemUiHelper.delayHide(3000);
        }
    }
}
