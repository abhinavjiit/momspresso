package com.mycity4kids.ui.fragment;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ConnectivityUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.parentingdetails.CommentsData;
import com.mycity4kids.models.response.UserCommentsResponse;
import com.mycity4kids.models.response.UserCommentsResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity;
import com.mycity4kids.ui.activity.UserActivitiesActivity;
import com.mycity4kids.ui.adapter.UsersCommentsRecycleAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 3/8/17.
 */
public class UsersCommentTabFragment extends BaseFragment implements UsersCommentsRecycleAdapter.RecyclerViewClickListener, EditCommentDialogFragment.IAddCommentReply {

    private ArrayList<UserCommentsResult> commentsList;
    private String authorId;
    private String paginationValue = "";
    private boolean isLastPageReached = false;
    private boolean isReuqestRunning = false;
    private int limit = 15;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private int editedCommentPosition = 0;
    private UsersCommentsRecycleAdapter adapter;

    private RecyclerView recyclerView;
    private TextView noBlogsTextView;
    private RelativeLayout mLodingView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.users_comment_tab_fragment, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        noBlogsTextView = (TextView) view.findViewById(R.id.noBlogsTextView);
        mLodingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);

//        userId = SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId();
        authorId = getArguments().getString(Constants.AUTHOR_ID);
        boolean isPrivate = getArguments().getBoolean("isPrivateProfile");

        adapter = new UsersCommentsRecycleAdapter(getActivity(), this, isPrivate);
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);
        commentsList = new ArrayList<UserCommentsResult>();
        paginationValue = "";
        getUsersComment();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = llm.getChildCount();
                    totalItemCount = llm.getItemCount();
                    pastVisiblesItems = llm.findFirstVisibleItemPosition();

                    if (!isReuqestRunning && !isLastPageReached) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            isReuqestRunning = true;
                            mLodingView.setVisibility(View.VISIBLE);
                            getUsersComment();
                        }
                    }
                }
            }
        });

        return view;
    }

    private void getUsersComment() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        // prepare call in Retrofit 2.0
        BloggerDashboardAPI getCommentsAPI = retrofit.create(BloggerDashboardAPI.class);
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
//            showToast(getString(R.string.error_network));
            return;
        }
        Call<UserCommentsResponse> call = getCommentsAPI.getUsersComments(authorId, limit, paginationValue);
        call.enqueue(usersCommentsResponseListener);
    }


    private Callback<UserCommentsResponse> usersCommentsResponseListener = new Callback<UserCommentsResponse>() {
        @Override
        public void onResponse(Call<UserCommentsResponse> call, retrofit2.Response<UserCommentsResponse> response) {

            mLodingView.setVisibility(View.GONE);
            isReuqestRunning = false;
            if (response == null || null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
//                showToast("Something went wrong from server");
                return;
            }
            try {
                UserCommentsResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processCommentsResponse(responseData);
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
        public void onFailure(Call<UserCommentsResponse> call, Throwable t) {
            mLodingView.setVisibility(View.GONE);
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void processCommentsResponse(UserCommentsResponse responseModel) {
        ArrayList<UserCommentsResult> dataCommentList = responseModel.getData().getResult();

        if (dataCommentList.size() == 0) {
            isLastPageReached = true;
            //  noDrafts.setVisibility(View.VISIBLE);
            if (null != commentsList && !commentsList.isEmpty()) {
                //No more next results for search from pagination

            } else {
                // No results for search
                commentsList.clear();
                commentsList.addAll(dataCommentList);
                adapter.setListData(commentsList);
                adapter.notifyDataSetChanged();
                noBlogsTextView.setVisibility(View.VISIBLE);
            }
        } else {
            noBlogsTextView.setVisibility(View.GONE);
            commentsList.addAll(dataCommentList);
            if (null != responseModel.getData().getPagination()) {
                paginationValue = responseModel.getData().getPagination();
            }
            if (AppConstants.PAGINATION_END_VALUE.equals(paginationValue)) {
                isLastPageReached = true;
            }
            adapter.setListData(commentsList);
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View view, int position) {
        switch (view.getId()) {
            case R.id.editCommentTextView:
//                commentsList.remove(position);
                editedCommentPosition = position;
                openCommentDialog(commentsList.get(position), "EDIT");
                adapter.notifyDataSetChanged();
                break;
            case R.id.rootView:
                Intent intent = new Intent(getActivity(), ArticleDetailsContainerActivity.class);
                intent.putExtra(Constants.ARTICLE_ID, commentsList.get(position).getArticleId());
                intent.putExtra(Constants.AUTHOR_ID, commentsList.get(position).getUserId());
                intent.putExtra(Constants.BLOG_SLUG, commentsList.get(position).getBlogTitleSlug());
                intent.putExtra(Constants.TITLE_SLUG, commentsList.get(position).getTitleSlug());
                intent.putExtra(Constants.FROM_SCREEN, "User Profile");
                if (true) {
                    intent.putExtra(Constants.ARTICLE_OPENED_FROM, "Private Comments");
                    intent.putExtra(Constants.FROM_SCREEN, "Private User Profile");
                } else {
                    intent.putExtra(Constants.ARTICLE_OPENED_FROM, "Public Comments");
                    intent.putExtra(Constants.FROM_SCREEN, "Public User Profile");
                }
                intent.putExtra(Constants.ARTICLE_INDEX, "" + position);
                startActivity(intent);
                break;
        }
    }

    private void openCommentDialog(UserCommentsResult comData, String opType) {
        try {
            CommentsData commentsData = new CommentsData();
            commentsData.setId(comData.getId());
            commentsData.setBody(comData.getUserComment());
            commentsData.setParent_id(comData.getParentId());
            commentsData.setArticleId(comData.getArticleId());
            commentsData.setUpdatedTime("" + comData.getUpdatedTime());
            commentsData.setName(comData.getUserName());

            EditCommentDialogFragment commentFrag = new EditCommentDialogFragment();
            commentFrag.setTargetFragment(UsersCommentTabFragment.this, 0);
            Bundle _args = new Bundle();
            _args.putString(Constants.ARTICLE_ID, comData.getArticleId());
            _args.putString("opType", opType);
            if (comData != null) {
                _args.putParcelable("commentData", commentsData);
            }
            commentFrag.setArguments(_args);
            FragmentManager fm = getChildFragmentManager();
            commentFrag.show(fm, "Replies");

        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    @Override
    public void onCommentAddition(CommentsData cd) {

    }

    @Override
    public void onCommentReplyEditSuccess(CommentsData cd) {
        commentsList.get(editedCommentPosition).setUserComment(cd.getBody());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onReplyAddition(CommentsData cd) {

    }
}
