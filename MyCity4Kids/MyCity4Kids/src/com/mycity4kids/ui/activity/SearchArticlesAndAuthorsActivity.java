package com.mycity4kids.ui.activity;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.ui.adapter.SearchArticlesAndAuthorsPagerAdapter;
import com.mycity4kids.ui.adapter.TabsPagerAdapter;

/**
 * Created by hemant on 19/4/16.
 */
public class SearchArticlesAndAuthorsActivity extends BaseActivity implements View.OnClickListener {

    Toolbar mToolbar;
    private TabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private SearchArticlesAndAuthorsPagerAdapter tabsPagerAdapter;
    private ImageView searchImageView;
    private EditText searchEditText;
    String searchParam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_articles_authors_activity);

        searchParam = getIntent().getStringExtra(Constants.FILTER_NAME);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mSlidingTabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        searchImageView = (ImageView) findViewById(R.id.searchImageView);
        searchEditText = (EditText) findViewById(R.id.searchEditText);
        tabsPagerAdapter = new SearchArticlesAndAuthorsPagerAdapter(getSupportFragmentManager(), this, null, this, searchParam);

        setSupportActionBar(mToolbar);
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.black_color), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        mViewPager.setAdapter(tabsPagerAdapter);
        mViewPager.setOffscreenPageLimit(4);
        mSlidingTabLayout.setupWithViewPager(mViewPager);

        searchEditText.setText(searchParam);
        searchImageView.setOnClickListener(this);
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchImageView:
                if (StringUtils.isNullOrEmpty(searchEditText.getText().toString())) {
                    showToast("Please enter a valid search parameter");
                } else {
                    tabsPagerAdapter.refreshArticlesAuthors(searchEditText.getText().toString(), mViewPager.getCurrentItem());
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
