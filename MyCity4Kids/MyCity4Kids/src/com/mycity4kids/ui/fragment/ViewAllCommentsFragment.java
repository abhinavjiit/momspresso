package com.mycity4kids.ui.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.adapter.AllCommentsPagerAdapter;
import com.mycity4kids.utils.AppUtils;

/**
 * Created by hemant on 13/10/17.
 */

public class ViewAllCommentsFragment extends BaseFragment implements View.OnClickListener {

    private View view;
    private TabLayout tabLayout;
    private ImageView closeImageView;

    private String userId;
    private String mycityCommentURL;
    private String fbCommentURL;
    private String articleId;
    private String author;
    private String blogSlug;
    private String titleSlug;
    private String userType;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.all_comments_fragment, container, false);

        userId = SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId();
        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        closeImageView = (ImageView) view.findViewById(R.id.closeImageView);

        closeImageView.setOnClickListener(this);

        fbCommentURL = getArguments().getString("fbCommentURL");
        mycityCommentURL = getArguments().getString("mycityCommentURL");
        articleId = getArguments().getString(Constants.ARTICLE_ID);
        author = getArguments().getString(Constants.AUTHOR);
        blogSlug = getArguments().getString(Constants.BLOG_SLUG);
        titleSlug = getArguments().getString(Constants.TITLE_SLUG);
        userType = getArguments().getString("userType");

        addCommentTabs();
        return view;
    }

    private void addCommentTabs() {
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);

        tabLayout.addTab(tabLayout.newTab().setText(BaseApplication.getAppContext().getString(R.string.app_name)));
        tabLayout.addTab(tabLayout.newTab().setText(BaseApplication.getAppContext().getString(R.string.ad_bottom_bar_facebook)));

        AppUtils.changeTabsFont(tabLayout);
//        wrapTabIndicatorToTitle(tabLayout, 25, 25);
        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);
        final AllCommentsPagerAdapter adapter = new AllCommentsPagerAdapter
                (getChildFragmentManager(), tabLayout.getTabCount(), mycityCommentURL, fbCommentURL, articleId, author, "article", titleSlug, blogSlug, userType);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }


    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.closeImageView:
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
                break;
        }
    }
}
