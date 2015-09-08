package br.com.comoqueta.comoquetasaude.android.activities;

import android.content.Intent;
import android.os.Handler;
import br.com.comoqueta.comoquetasaude.android.R;
import br.com.comoqueta.comoquetasaude.android.activities.base.ImmersiveActivity;

public class SplashScreenActivity extends ImmersiveActivity implements Runnable {
    private static int SPLASH_TIME_OUT = 3000;
    private static Handler sHandler = new Handler();

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_splash_screen;
    }

    @Override
    protected void onResume() {
        super.onResume();
        sHandler.postDelayed(this, SPLASH_TIME_OUT);
    }

    @Override
    public void run() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}
