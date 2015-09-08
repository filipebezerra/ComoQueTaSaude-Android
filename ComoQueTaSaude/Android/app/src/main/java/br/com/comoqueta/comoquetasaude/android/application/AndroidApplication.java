package br.com.comoqueta.comoquetasaude.android.application;

import android.app.Application;
import br.com.comoqueta.comoquetasaude.android.BuildConfig;
import br.com.comoqueta.comoquetasaude.android.R;
import br.com.comoqueta.comoquetasaude.android.models.Avaliacao;
import br.com.comoqueta.comoquetasaude.android.models.UnidadeAtendimento;
import br.com.comoqueta.comoquetasaude.android.models.UnidadesFavoritas;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseCrashReporting;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import timber.log.Timber;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 18/08/2015
 * @since #
 */
public class AndroidApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initializeLogging();
        initializeParse();
    }

    private void initializeLogging() {
        if (BuildConfig.DEBUG) {
            // Debug mode logging
            Timber.plant(new Timber.DebugTree());
            // Parse full logging
            Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);
        } else {
            //TODO create and plant Release mode logging?
            // Parse only error logging
            Parse.setLogLevel(Parse.LOG_LEVEL_ERROR);
        }
    }

    private void initializeParse() {
        // Register the customized Parse classes
        ParseObject.registerSubclass(UnidadeAtendimento.class);
        ParseObject.registerSubclass(Avaliacao.class);
        ParseObject.registerSubclass(UnidadesFavoritas.class);

        // Enable storing objects locally
        Parse.enableLocalDatastore(this);

        // Enable Crash Reporting
        ParseCrashReporting.enable(this);

        // Initialize parse
        Parse.initialize(this, getString(R.string.parse_application_id),
                getString(R.string.parse_client_key));

        // Enable login with facebook
        ParseFacebookUtils.initialize(this);

        // Enable login with Twitter
        ParseTwitterUtils.initialize(getString(R.string.twitter_consumer_key),
                getString(R.string.twitter_consumer_secret));

        // Enable anonymous user
        ParseUser.enableAutomaticUser();

        // Enable default access to public read to all objects created
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }
}
