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
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseFragment;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.ViewAllCommentsActivity;
import com.mycity4kids.ui.adapter.AllCommentsPagerAdapter;
import com.mycity4kids.utils.AppUtils;
import java.util.ArrayList;

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
    private View topCommentCoachMark;
    private ArrayList<String> tags;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.all_comments_fragment, container, false);

        tabLayout = view.findViewById(R.id.tab_layout);
        closeImageView = view.findViewById(R.id.closeImageView);
        taggingCoachmark = view.findViewById(R.id.taggingCoachmark);
        topCommentCoachMark = view.findViewById(R.id.topCommentCoachMark);
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
        tags = getArguments().getStringArrayList("tags");

        if (!SharedPrefUtils.isCoachmarksShownFlag(BaseApplication.getAppContext(), "taggingCoachmark")) {
            taggingCoachmark.setVisibility(View.VISIBLE);
        } else {
            if (SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId()
                    .equals(authorId) && !SharedPrefUtils
                    .isCoachmarksShownFlag(BaseApplication.getAppContext(), "topCommentCoachMark")) {
                topCommentCoachMark.setVisibility(View.VISIBLE);
            }
        }

        taggingCoachmark.setOnClickListener(this);
        topCommentCoachMark.setOnClickListener(this);
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
                blogSlug, userType, authorId, tags);
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
                if (getActivity() instanceof ViewAllCommentsActivity) {
                    getActivity().onBackPressed();
                } else {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    fm.popBackStack();
                }
                break;
            case R.id.topCommentCoachMark:
                topCommentCoachMark.setVisibility(View.GONE);
                SharedPrefUtils.setCoachmarksShownFlag(BaseApplication.getAppContext(), "topCommentCoachMark", true);
                break;
            case R.id.taggingCoachmark:
                taggingCoachmark.setVisibility(View.GONE);
                SharedPrefUtils.setCoachmarksShownFlag(BaseApplication.getAppContext(), "taggingCoachmark", true);
                if (SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId()
                        .equals(authorId)) {
                    if (!SharedPrefUtils
                            .isCoachmarksShownFlag(BaseApplication.getAppContext(), "topCommentCoachMark")) {
                        topCommentCoachMark.setVisibility(View.VISIBLE);
                    }
                }
                break;
            default:
                break;
        }
    }
}