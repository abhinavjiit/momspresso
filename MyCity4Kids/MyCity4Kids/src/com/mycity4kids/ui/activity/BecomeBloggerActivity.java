package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.editor.EditorPostActivity;
import com.mycity4kids.models.response.TrendingListingResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.fragment.BecomeBloggerFragment;
import com.mycity4kids.ui.fragment.BecomeBloggerTabFragment;

import java.util.ArrayList;

public class BecomeBloggerActivity extends BaseActivity {
    String[] titleArray;
    String[] descArray;
    private TextView getStartedTextView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_become_blogger);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        titleArray = getResources().getStringArray(R.array.become_blogger_title_array);
        descArray = getResources().getStringArray(R.array.become_blogger_desc_array);

        getStartedTextView = (TextView) findViewById(R.id.getStartedTextView);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        BecomeBloggerPagerAdapter adapter = new BecomeBloggerPagerAdapter
                (getSupportFragmentManager(), 4);
        viewPager.setClipToPadding(false);
        viewPager.setPadding(80, 20, 80, 20);
        viewPager.setPageMargin(30);
        viewPager.setAdapter(adapter);

        getStartedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefUtils.setBecomeBloggerFlag(BecomeBloggerActivity.this, true);
                Intent intent = new Intent(BecomeBloggerActivity.this, EditorPostActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(EditorPostActivity.TITLE_PARAM, "");
                bundle.putString(EditorPostActivity.CONTENT_PARAM, "");
                bundle.putString(EditorPostActivity.TITLE_PLACEHOLDER_PARAM,
                        getString(R.string.example_post_title_placeholder));
                bundle.putString(EditorPostActivity.CONTENT_PLACEHOLDER_PARAM,
                        getString(R.string.example_post_content_placeholder));
                bundle.putInt(EditorPostActivity.EDITOR_PARAM, EditorPostActivity.USE_NEW_EDITOR);
                bundle.putString("from", "DraftListViewActivity");
                intent.putExtras(bundle);
                FragmentManager fm = getSupportFragmentManager();
                fm.popBackStack();
                fm.popBackStack();
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void updateUi(Response response) {

    }


    private class BecomeBloggerPagerAdapter extends FragmentStatePagerAdapter {
        private int mNumOfTabs;
        private ArrayList<TrendingListingResult> trendingListingResults;

        public BecomeBloggerPagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {

            Bundle bundle = new Bundle();
            bundle.putString("title", titleArray[position]);
            bundle.putString("desc", descArray[position]);
            bundle.putInt("position", position);
            BecomeBloggerTabFragment tab1 = new BecomeBloggerTabFragment();
            tab1.setArguments(bundle);
            return tab1;
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }


}
