package com.mycity4kids.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.mycity4kids.BuildConfig;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseFragment;
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

    private String mycityCommentUrl;
    private String fbCommentUrl;
    private String articleId;
    private String author;
    private String blogSlug;
    private String titleSlug;
    private String userType;
    private String contentType;
    private String authorId;
    private RelativeLayout taggingCoachmark;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.all_comments_fragment, container, false);

        tabLayout = view.findViewById(R.id.tab_layout);
        closeImageView = view.findViewById(R.id.closeImageView);
        taggingCoachmark = view.findViewById(R.id.taggingCoachmark);

        closeImageView.setOnClickListener(this);
        taggingCoachmark.setOnClickListener(this);
        fbCommentUrl = getArguments().getString("fbCommentURL");
        mycityCommentUrl = getArguments().getString("mycityCommentURL");
        articleId = getArguments().getString(Constants.ARTICLE_ID);
        author = getArguments().getString(Constants.AUTHOR);
        blogSlug = getArguments().getString(Constants.BLOG_SLUG);
        titleSlug = getArguments().getString(Constants.TITLE_SLUG);
        userType = getArguments().getString("userType");
        contentType = getArguments().getString("contentType");
        authorId = getArguments().getString(Constants.AUTHOR_ID);

        if (!SharedPrefUtils.isCoachmarksShownFlag(BaseApplication.getAppContext(), "taggingCoachmark")) {
            taggingCoachmark.setVisibility(View.VISIBLE);
        } else {
            taggingCoachmark.setVisibility(View.GONE);
        }
        addCommentTabs();
        return view;
    }

    private void addCommentTabs() {
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);

        tabLayout.addTab(tabLayout.newTab().setText(BaseApplication.getAppContext().getString(R.string.app_name)));
        tabLayout.addTab(tabLayout.newTab()
                .setText(BaseApplication.getAppContext().getString(R.string.ad_bottom_bar_facebook)));

        AppUtils.changeTabsFont(tabLayout);
        final ViewPager viewPager = view.findViewById(R.id.pager);
        final AllCommentsPagerAdapter adapter = new AllCommentsPagerAdapter(getChildFragmentManager(),
                tabLayout.getTabCount(), mycityCommentUrl, fbCommentUrl, articleId, author, contentType, titleSlug,
                blogSlug, userType, authorId);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.closeImageView:
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
                break;
            case R.id.taggingCoachmark:
                taggingCoachmark.setVisibility(View.GONE);
                SharedPrefUtils.setCoachmarksShownFlag(BaseApplication.getAppContext(), "taggingCoachmark", true);
                break;
            default:
                break;
        }
    }
}
