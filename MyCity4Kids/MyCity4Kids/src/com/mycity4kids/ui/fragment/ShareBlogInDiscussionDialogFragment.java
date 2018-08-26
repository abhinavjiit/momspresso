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
import com.mycity4kids.models.request.AddGroupPostRequest;
import com.mycity4kids.models.request.ForYouArticleRemoveRequest;
import com.mycity4kids.models.response.AddGroupPostResponse;
import com.mycity4kids.models.response.ForYourArticleRemoveResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.RecommendationAPI;
import com.mycity4kids.ui.activity.GroupDetailsActivity;
import com.mycity4kids.utils.AppUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Hemant.Parmar on 08-06-2015.
 */
public class ShareBlogInDiscussionDialogFragment extends DialogFragment implements OnClickListener {


    private int groupId;
    private int position;
    private String articleUrl;
    private IForYourArticleRemove listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.share_blog_discussion_fragment_dialog, container,
                false);
        TextView okayTextView = (TextView) rootView.findViewById(R.id.okayTextView);
        TextView cancelTextView = (TextView) rootView.findViewById(R.id.cancelTextView);

        Bundle extras = getArguments();
        String reason = null;
        if (extras != null) {
            groupId = extras.getInt("groupId");
            position = extras.getInt("position");
            articleUrl = extras.getString("articleUrl");
        }

        okayTextView.setOnClickListener(this);
        cancelTextView.setOnClickListener(this);
        getDialog().getWindow().setBackgroundDrawableResource(R.color.transparent);
        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.okayTextView:
                postBlogInDiscussion();
                break;
            case R.id.cancelTextView:
                dismiss();
                break;
        }
    }

    private void postBlogInDiscussion() {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);

        AddGroupPostRequest addGroupPostRequest = new AddGroupPostRequest();
        addGroupPostRequest.setContent(articleUrl);
        addGroupPostRequest.setType("0");
        addGroupPostRequest.setGroupId(groupId);
        addGroupPostRequest.setUserId(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());

        Call<AddGroupPostResponse> call = groupsAPI.createPost(addGroupPostRequest);
        call.enqueue(postAdditionResponseCallback);
//        articleId
    }

    private Callback<AddGroupPostResponse> postAdditionResponseCallback = new Callback<AddGroupPostResponse>() {
        @Override
        public void onResponse(Call<AddGroupPostResponse> call, retrofit2.Response<AddGroupPostResponse> response) {
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    AddGroupPostResponse responseModel = response.body();
//                    setResult(RESULT_OK);
//                    onBackPressed();
//                    processGroupListingResponse(responseModel);
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                if (isAdded())
                    ((GroupDetailsActivity) getActivity()).showToast(getString(R.string.went_wrong));
//                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<AddGroupPostResponse> call, Throwable t) {
            if (isAdded())
                ((GroupDetailsActivity) getActivity()).showToast(getString(R.string.went_wrong));
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    public void setListener(IForYourArticleRemove listener) {
        this.listener = listener;
    }

    public interface IForYourArticleRemove {
        void onForYouArticleRemoved(int position);
    }
}