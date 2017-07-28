package com.mycity4kids.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.mycity4kids.R;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.adapter.RankingPagerAdapter;
import com.mycity4kids.ui.adapter.UserArticlesPagerAdapter;

/**
 * Created by hemant on 28/7/17.
 */
public class RankingHomeFragment extends BaseFragment {

    private String userId;

    private View view;
    private LayoutInflater mInflator;
    private TabLayout tabLayout;
    private ViewPager viewPager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.ranking_home_fragment, container, false);
        Utils.pushOpenScreenEvent(getActivity(), "Dashboard Fragment", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "");
        mInflator = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        userId = SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId();
        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        viewPager = (ViewPager) view.findViewById(R.id.pager);

        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.ranking_tabs_your_rank_label)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.ranking_tabs_page_views_label)));
        UserArticlesPagerAdapter adapter = new UserArticlesPagerAdapter(getChildFragmentManager(), tabLayout.getTabCount(), "3bb7f780887e453a8e4c0666e5aa9e1d");
        RankingPagerAdapter rankingPagerAdapter = new RankingPagerAdapter(getChildFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(rankingPagerAdapter);
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

        return view;
    }

    @Override
    protected void updateUi(Response response) {

    }
}
