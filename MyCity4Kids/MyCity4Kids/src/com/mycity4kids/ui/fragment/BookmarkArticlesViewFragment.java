package com.mycity4kids.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.controller.ParentingStopController;
import com.mycity4kids.models.parentingstop.CommonParentingResponse;
import com.mycity4kids.models.parentingstop.ParentingRequest;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.DashboardActivity;

/**
 * Created by hemant on 2/11/15.
 */
public class BookmarkArticlesViewFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        view = getActivity().getLayoutInflater().inflate(R.layout.new_article_layout, container, false);

        String testURL = "http://webserve.mycity4kids.com/apiparentingstop/articles?city_id=1&page=1&pincode=110044&sort=trending&user_id=152625";

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void hitArticleListingApi(int pPageCount, String SortKey) {
        ParentingRequest _parentingModel = new ParentingRequest();
        /**
         * this case will case in pagination case: for sorting
         */
        if (SortKey != null) {
            _parentingModel.setSoty_by("trending");
        }

        _parentingModel.setCity_id(SharedPrefUtils.getCurrentCityModel(getActivity()).getId());
        _parentingModel.setPage("" + pPageCount);
        ParentingStopController _controller = new ParentingStopController(getActivity(), this);
//        mIsRequestRunning = true;
        _controller.getData(AppConstants.PARENTING_STOP_ARTICLES_REQUEST, _parentingModel);

    }

    @Override
    protected void updateUi(Response response) {

        CommonParentingResponse responseData;
        if (response == null) {
            ((DashboardActivity) getActivity()).showToast("Something went wrong from server");
            removeProgressDialog();
            return;
        }
        Log.d("BOOKMARk RES", response.toString());

    }
}
