package com.mycity4kids.ui.fragment;

import android.accounts.NetworkErrorException;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.request.ForYouArticleRemoveRequest;
import com.mycity4kids.models.response.ForYourArticleRemoveResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.RecommendationAPI;
import com.mycity4kids.utils.AppUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by user on 08-06-2015.
 */
public class ForYouInfoDialogFragment extends DialogFragment implements OnClickListener {


    private String articleId;
    private int position;
    private IForYourArticleRemove listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.for_you_reason_fragment_dialog, container,
                false);
        Utils.pushOpenScreenEvent(getActivity(), "Upload video Option Menu", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "");
        TextView okayTextView = (TextView) rootView.findViewById(R.id.okayTextView);
        TextView foryouReasonTextView = (TextView) rootView.findViewById(R.id.forYouReasonTextView);
        TextView dontTextView = (TextView) rootView.findViewById(R.id.dontTextView);

        Bundle extras = getArguments();
        String reason = null;
        if (extras != null) {
            reason = extras.getString("reason");
            articleId = extras.getString("articleId");
            position = extras.getInt("position");
        }
        foryouReasonTextView.setText(AppUtils.fromHtml("" + reason));

        okayTextView.setOnClickListener(this);
        dontTextView.setOnClickListener(this);
        getDialog().getWindow().setBackgroundDrawableResource(R.color.transparent);
        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.okayTextView:
                dismiss();
                break;
            case R.id.dontTextView:
                removeArticleFromForYouFeed();
                break;
        }
    }

    private void removeArticleFromForYouFeed() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        RecommendationAPI recommendationAPI = retrofit.create(RecommendationAPI.class);

        ForYouArticleRemoveRequest forYouArticleRemoveRequest = new ForYouArticleRemoveRequest();
        forYouArticleRemoveRequest.setArticleId(articleId);

        Call<ForYourArticleRemoveResponse> callRecentVideoArticles = recommendationAPI.removeFromForYouFeed(forYouArticleRemoveRequest);
        callRecentVideoArticles.enqueue(forYouArticleRemoveResponseListener);
//        articleId
    }

    private Callback<ForYourArticleRemoveResponse> forYouArticleRemoveResponseListener = new Callback<ForYourArticleRemoveResponse>() {
        @Override
        public void onResponse(Call<ForYourArticleRemoveResponse> call, Response<ForYourArticleRemoveResponse> response) {
            if (response == null || null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                return;
            }
            try {
                ForYourArticleRemoveResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    listener.onForYouArticleRemoved(position);
                    dismiss();
//                    notificationCenterResultArrayList.addAll(responseData.getData().getResult());
//                    notificationCenterListAdapter.notifyDataSetChanged();
                } else {
//                    showToast(responseData.getReason());
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
//                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<ForYourArticleRemoveResponse> call, Throwable t) {

        }
    };

    public void setListener(IForYourArticleRemove listener) {
        this.listener = listener;
    }

    public interface IForYourArticleRemove {
        void onForYouArticleRemoved(int position);
    }
}