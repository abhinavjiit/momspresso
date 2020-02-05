package com.mycity4kids.ui.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kelltontech.ui.BaseFragment;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;

/**
 * Created by hemant on 28/7/17.
 */
public class ImproveRankPageViewsSocialFragment extends BaseFragment {

    String[] infoArray;
    private String[] secondInfoArray;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.improve_rank_page_view_social_fragment, container,
                false);

        LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.mainLinearLayout);
        LinearLayout sl = (LinearLayout) rootView.findViewById(R.id.secondLinearLayout);
        TextView headerTextView = (TextView) rootView.findViewById(R.id.headerTextView);
        TextView secondHeaderTextView = (TextView) rootView.findViewById(R.id.secondHeaderTextView);
        View separatorView = rootView.findViewById(R.id.separatorView);


        String infoType = getArguments().getString(AppConstants.ANALYTICS_INFO_TYPE);

        switch (infoType) {
            case AppConstants.ANALYTICS_INFO_IMPROVE_PAGE_VIEWS:
                infoArray = getResources().getStringArray(R.array.improve_page_views_array);
                headerTextView.setText(getString(R.string.analytics_popup_improve_page_views));
                break;
            case AppConstants.ANALYTICS_INFO_RANK_CALCULATION:
                infoArray = getResources().getStringArray(R.array.rank_calculation_array);
                headerTextView.setText(getString(R.string.analytics_popup_rank_calculation));
                sl.setVisibility(View.VISIBLE);
                secondHeaderTextView.setVisibility(View.VISIBLE);
                separatorView.setVisibility(View.VISIBLE);
                secondHeaderTextView.setText(getString(R.string.analytics_popup_improve_rank));
                secondInfoArray = getResources().getStringArray(R.array.improve_rank_array);
                break;
            case AppConstants.ANALYTICS_INFO_IMPROVE_RANK:
                infoArray = getResources().getStringArray(R.array.improve_rank_array);
                headerTextView.setText(getString(R.string.analytics_popup_improve_rank));
                break;
            case AppConstants.ANALYTICS_INFO_IMPROVE_SOCIAL_SHARE:
                infoArray = getResources().getStringArray(R.array.improve_social_share_array);
                headerTextView.setText(getString(R.string.analytics_popup_improve_social));
                break;
            case AppConstants.ANALYTICS_INFO_INCREASE_FOLLOWERS:
                infoArray = getResources().getStringArray(R.array.increase_followers_array);
                headerTextView.setText(getString(R.string.analytics_popup_increase_followers));
                break;
        }

        for (int i = 0; i < infoArray.length; i++) {
            LinearLayout view = (LinearLayout) inflater.inflate(R.layout.include_analytics_info_item, null);
            TextView textView = ((TextView) view.getChildAt(1));
            textView.setText(infoArray[i]);
            ll.addView(view);
        }

        if (secondInfoArray != null && secondHeaderTextView.length() > 0) {
            for (int i = 0; i < secondInfoArray.length; i++) {
                LinearLayout view = (LinearLayout) inflater.inflate(R.layout.include_analytics_info_item, null);
                TextView textView = ((TextView) view.getChildAt(1));
                textView.setText(secondInfoArray[i]);
                sl.addView(view);
            }
        }

        return rootView;
    }
}
