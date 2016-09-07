package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.mycity4kids.R;
import com.mycity4kids.editor.EditorPostActivity;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.newmodels.parentingmodel.ArticleModelNew;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.adapter.TabsPagerAdapter;

/**
 * @author deepaneker.chaudhary
 */
public class ArticlesFragment extends BaseFragment {

    private TabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    TabsPagerAdapter tabsPagerAdapter;
    ImageView addDraft;
    String searchName = "";
    ArticleModelNew.AllArticles initialList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Utils.pushOpenScreenEvent(getActivity(), "Blogs Dashboard", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "");

        View view = inflater.inflate(R.layout.aa_fragment_article_new, null);
        addDraft = (ImageView) view.findViewById(R.id.addDraft);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mSlidingTabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);

        addDraft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT > 15) {
                    Intent intent1 = new Intent(getActivity(), EditorPostActivity.class);
                    Bundle bundle5 = new Bundle();
                    bundle5.putString(EditorPostActivity.TITLE_PARAM, "");
                    bundle5.putString(EditorPostActivity.CONTENT_PARAM, "");
                    bundle5.putString(EditorPostActivity.TITLE_PLACEHOLDER_PARAM,
                            getString(R.string.example_post_title_placeholder));
                    bundle5.putString(EditorPostActivity.CONTENT_PLACEHOLDER_PARAM,
                            getString(R.string.example_post_content_placeholder));
                    bundle5.putInt(EditorPostActivity.EDITOR_PARAM, EditorPostActivity.USE_NEW_EDITOR);
                    bundle5.putString("from", "DraftListViewActivity");
                    intent1.putExtras(bundle5);
                    startActivity(intent1);
                } else {
                    Intent viewIntent =
                            new Intent("android.intent.action.VIEW",
                                    Uri.parse("http://www.mycity4kids.com/parenting/admin/setupablog"));
                    startActivity(viewIntent);
                }
            }
        });

        initialList = new ArticleModelNew().new AllArticles();

        try {
            setRetainInstance(true);
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4KException", Log.getStackTraceString(e));
        }

        tabsPagerAdapter = new TabsPagerAdapter(getChildFragmentManager(), getActivity(), null, getActivity(), searchName);
        mViewPager.setAdapter(tabsPagerAdapter);
        mSlidingTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setCurrentItem(1);

//        NewAllArticleListingApi(mPageCount);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                int pos = position;

                mSlidingTabLayout.setScrollPosition(position, positionOffset, true);

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
//        mViewPager.setCurrentItem(1);

        return view;
    }

    @Override
    protected void updateUi(Response response) {

    }

    public void refreshBlogList() {
        tabsPagerAdapter.refreshBookmarkedBlogList();
    }
}
