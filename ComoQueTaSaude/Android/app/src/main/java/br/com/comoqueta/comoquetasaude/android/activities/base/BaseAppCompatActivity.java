package br.com.comoqueta.comoquetasaude.android.activities.base;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import br.com.comoqueta.comoquetasaude.android.R;
import butterknife.ButterKnife;

/**
 * Base abstract class for every Activity in this application. This class provide layout binding and
 * {@link Toolbar} as {@link ActionBar} pre-configuration, if present in the layout.
 *
 * @author Filipe Bezerra
 * @version 0.0.1, 03/08/2015
 * @since 0.0.1
 */
public abstract class BaseAppCompatActivity extends AppCompatActivity {
    /**
     * Set up the {@link Toolbar} as {@link ActionBar} if present in the provided layout. Also set
     * up the up indicator as the home navigation with an icon if present.
     */
    private void setUpToolbarAsActionBar() {
        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            final ActionBar actionBar = getSupportActionBar();
            final int drawableId = getUpIndicatorDrawableResId();

            if (actionBar != null && drawableId != 0) {
                actionBar.setHomeAsUpIndicator(drawableId);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        ButterKnife.bind(this);
        setUpToolbarAsActionBar();
    }

    /**
     * Adds a {@link Fragment} to this activity's layout.
     *
     * @param containerViewId The container view to where add the fragment.
     * @param fragment The fragment to be added.
     * @param backStackName An optional name for this back stack state, or null
     * @return Returns the identifier of this transaction's back stack entry, if {@link
     * FragmentTransaction#addToBackStack(String)} had been called.  Otherwise, returns a negative
     * number.
     */
    protected int addFragment(@IdRes int containerViewId, @NonNull Fragment fragment,
            @Nullable String backStackName) {
        return getSupportFragmentManager().beginTransaction()
                .add(containerViewId, fragment)
                .addToBackStack(backStackName)
                .commit();
    }

    /**
     * Replaces a {@link Fragment} to this activity's layout.
     *
     * @param containerViewId The container view to where add the fragment.
     * @param fragment The fragment to be added.
     * @param backStackName An optional name for this back stack state, or null
     * @return Returns the identifier of this transaction's back stack entry, if {@link
     * FragmentTransaction#addToBackStack(String)} had been called.  Otherwise, returns a negative
     * number.
     */
    protected int replaceFragment(@IdRes int containerViewId, @NonNull Fragment fragment,
            @Nullable String backStackName) {
        return getSupportFragmentManager().beginTransaction()
                .replace(containerViewId, fragment)
                .addToBackStack(backStackName)
                .commit();
    }

    /**
     * Define the drawable to be inflated to the {@link Toolbar} as an up indicator for home
     * navigation. By default this method returns 0, which means no up indicator.
     *
     * @return The drawable resource id
     */
    protected @DrawableRes int getUpIndicatorDrawableResId() {
        return 0;
    }

    /**
     * Provide the content view layout to be inflated in {@link #onCreate(Bundle)} of this
     * activity.
     *
     * @return The layout resource id
     */
    protected abstract @LayoutRes int getLayoutResId();
}
