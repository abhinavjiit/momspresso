package com.mycity4kids.ui.activity;

import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.adapter.SearchAllPagerAdapter;
import com.mycity4kids.utils.AppUtils;

/**
 * Created by hemant on 19/4/16.
 */
public class SearchAllActivity extends BaseActivity implements View.OnClickListener {

    Toolbar mToolbar;
    private TabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private SearchAllPagerAdapter tabsPagerAdapter;
    private ImageView searchImageView;
    private EditText searchEditText;
    String searchParam;
    int tabPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.pushOpenScreenEvent(SearchAllActivity.this, "Search Articles/Authors", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");

        setContentView(R.layout.search_articles_authors_activity);

        searchParam = getIntent().getStringExtra(Constants.FILTER_NAME);
        tabPosition = getIntent().getIntExtra(Constants.TAB_POSITION, 0);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mSlidingTabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        searchImageView = (ImageView) findViewById(R.id.searchImageView);
        searchEditText = (EditText) findViewById(R.id.searchEditText);
        tabsPagerAdapter = new SearchAllPagerAdapter(getSupportFragmentManager(), this, null, this, searchParam);

        setSupportActionBar(mToolbar);
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.back_arroow);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.black_color), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        mViewPager.setAdapter(tabsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mSlidingTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(tabPosition);

        searchEditText.setText(searchParam);
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    requestSearch();
                }
                return false;
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
//                requestSearch();
            }
        });

        AppUtils.changeTabsFont(this, mSlidingTabLayout);
        searchImageView.setOnClickListener(this);
    }


    private void changeTabsFont() {
        //Typeface font = Typeface.createFromAsset(getAssets(), "fonts/androidnation.ttf");
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "fonts/" + "oswald_regular.ttf");
        ViewGroup vg = (ViewGroup) mSlidingTabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(myTypeface, Typeface.NORMAL);
                }
            }
        }
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchImageView:
                requestSearch();
                break;
        }
    }

    private void requestSearch() {
        if (StringUtils.isNullOrEmpty(searchEditText.getText().toString())) {
//            showToast("Please enter a valid search parameter");
        } else {
            tabsPagerAdapter.refreshArticlesAuthors(searchEditText.getText().toString(), mViewPager.getCurrentItem());
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
