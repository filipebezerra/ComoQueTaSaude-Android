package br.com.comoqueta.comoquetasaude.android.activities;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import br.com.comoqueta.comoquetasaude.android.R;
import br.com.comoqueta.comoquetasaude.android.activities.base.BaseAppCompatActivity;
import br.com.comoqueta.comoquetasaude.android.fragments.AvaliacoesUnidadeAtendimentoFragment;
import br.com.comoqueta.comoquetasaude.android.fragments.InformacoesUnidadeAtendimentoFragment;
import br.com.comoqueta.comoquetasaude.android.models.UnidadeAtendimento;
import butterknife.Bind;
import butterknife.ButterKnife;
import java.util.ArrayList;
import java.util.List;

public class UnidadeAtendimentoActivity extends BaseAppCompatActivity {
    private static final String TAG = UnidadeAtendimentoActivity.class.getName();

    public static final String EXTRA_UNIDADE_ATENDIMENTO = TAG + ".unidade_atendimento";

    @Bind(R.id.main_container) protected CoordinatorLayout mMainContainer;
    @Bind(R.id.viewpager) protected ViewPager mViewPager;

    private UnidadeAtendimento mUnidadeAtendimento;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_unidade_atendimento;
    }

    @Override
    protected int getUpIndicatorDrawableResId() {
        return super.getUpIndicatorDrawableResId();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null && getIntent().getExtras() != null &&
                getIntent().hasExtra(EXTRA_UNIDADE_ATENDIMENTO)) {
            mUnidadeAtendimento = getIntent().getExtras().getParcelable(EXTRA_UNIDADE_ATENDIMENTO);
        } else {
            finish();
        }

        initializeContentView();
    }

    private void initializeContentView() {
        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);

        if (mViewPager != null) {
            setupViewPager();
        }

        TabLayout tabLayout = ButterKnife.findById(this, R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void setupViewPager() {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(AvaliacoesUnidadeAtendimentoFragment.newInstance(mUnidadeAtendimento),
                "Situação");
        adapter.addFragment(InformacoesUnidadeAtendimentoFragment.newInstance(mUnidadeAtendimento),
                "Informações");
        mViewPager.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
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
