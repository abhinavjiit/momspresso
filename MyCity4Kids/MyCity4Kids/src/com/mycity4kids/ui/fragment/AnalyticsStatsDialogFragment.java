package com.mycity4kids.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;

/**
 * Created by hemant on 14/12/16.
 */
public class AnalyticsStatsDialogFragment extends DialogFragment {

    String[] infoArray;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.improve_analytics_stats_dialog, container,
                false);

        LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.mainLinearLayout);
        TextView headerTextView = (TextView) rootView.findViewById(R.id.headerTextView);
        TextView cancelTextView = (TextView) rootView.findViewById(R.id.cancelTextView);

        String infoType = getArguments().getString(AppConstants.ANALYTICS_INFO_TYPE);

        switch (infoType) {
            case AppConstants.ANALYTICS_INFO_IMPROVE_PAGE_VIEWS:
                infoArray = getResources().getStringArray(R.array.improve_page_views_array);
                headerTextView.setText(getString(R.string.analytics_popup_improve_page_views));
                break;
            case AppConstants.ANALYTICS_INFO_RANK_CALCULATION:
                infoArray = getResources().getStringArray(R.array.rank_calculation_array);
                headerTextView.setText(getString(R.string.analytics_popup_rank_calculation));
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

        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
