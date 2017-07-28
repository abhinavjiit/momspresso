package com.mycity4kids.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.ui.fragment.FragmentMC4KHomeNew;
import com.mycity4kids.ui.fragment.RankingHomeFragment;

/**
 * Created by hemant on 28/7/17.
 */
public class RankingActivity extends BaseActivity {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ranking_activity);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        replaceFragment(new RankingHomeFragment(), null, false);
    }

    @Override
    protected void updateUi(Response response) {

    }
}
