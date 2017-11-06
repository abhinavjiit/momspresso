package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ConnectivityUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.editor.EditorPostActivity;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.response.BlogPageResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.BlogPageAPI;
import com.mycity4kids.ui.activity.DashboardActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by manish.soni on 30-07-2015.
 */
public class AddArticleVideoFragment extends BaseFragment implements View.OnClickListener {

    private ImageView setUpBlogImageView, writeArticleImageView, uploadVideoImageView, suggestedTopicImageView;
    private TextView writeArticleTextView, uploadVideoTextView, becomeBloggerTextView, suggestedTopicTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_article_video_fragment, container, false);

        writeArticleImageView = (ImageView) view.findViewById(R.id.writeArticleImageView);
        uploadVideoImageView = (ImageView) view.findViewById(R.id.uploadVideoImageView);
        setUpBlogImageView = (ImageView) view.findViewById(R.id.setUpBlogImageView);
        suggestedTopicImageView = (ImageView) view.findViewById(R.id.suggestedTopicImageView);
        writeArticleTextView = (TextView) view.findViewById(R.id.writeArticleTextView);
        uploadVideoTextView = (TextView) view.findViewById(R.id.uploadVideoTextView);
        becomeBloggerTextView = (TextView) view.findViewById(R.id.becomeBloggerTextView);
        suggestedTopicTextView = (TextView) view.findViewById(R.id.suggestedTopicTextView);

        writeArticleImageView.setOnClickListener(this);
        writeArticleTextView.setOnClickListener(this);
        uploadVideoImageView.setOnClickListener(this);
        uploadVideoTextView.setOnClickListener(this);
        setUpBlogImageView.setOnClickListener(this);
        becomeBloggerTextView.setOnClickListener(this);
        suggestedTopicImageView.setOnClickListener(this);
        suggestedTopicTextView.setOnClickListener(this);

        if ("0".equals(SharedPrefUtils.getUserDetailModel(getActivity()).getUserType()) && !SharedPrefUtils.getBecomeBloggerFlag(getActivity())) {
            writeArticleImageView.setVisibility(View.INVISIBLE);
            writeArticleTextView.setVisibility(View.INVISIBLE);
            setUpBlogImageView.setVisibility(View.VISIBLE);
            becomeBloggerTextView.setVisibility(View.VISIBLE);
        } else {
            writeArticleImageView.setVisibility(View.VISIBLE);
            writeArticleTextView.setVisibility(View.VISIBLE);
            setUpBlogImageView.setVisibility(View.INVISIBLE);
            becomeBloggerTextView.setVisibility(View.INVISIBLE);
        }

        //checkBlogPageSetup();
        return view;
    }

    private void checkBlogPageSetup() {
        showProgressDialog(getResources().getString(R.string.please_wait));
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();

        BlogPageAPI getBlogPageAPI = retrofit.create(BlogPageAPI.class);
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            removeProgressDialog();
//            showToast(getString(R.string.error_network));
            return;
        }

        Call<BlogPageResponse> call = getBlogPageAPI.getBlogPage("v1/users/blogPage/" + SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId());
        call.enqueue(new Callback<BlogPageResponse>() {
                         @Override
                         public void onResponse(Call<BlogPageResponse> call, retrofit2.Response<BlogPageResponse> response) {
                             removeProgressDialog();
                             BlogPageResponse responseModel = response.body();
                             if (responseModel.getCode() == 200 && Constants.SUCCESS.equals(responseModel.getStatus())) {
                                 if (responseModel.getData().getResult().getIsSetup() == 1) {
                                     writeArticleImageView.setVisibility(View.VISIBLE);
                                     setUpBlogImageView.setVisibility(View.INVISIBLE);
                                 } else if (responseModel.getData().getResult().getIsSetup() == 0) {
                                     setUpBlogImageView.setVisibility(View.VISIBLE);
                                     writeArticleImageView.setVisibility(View.INVISIBLE);
                                 }
                             } else {
                                 setUpBlogImageView.setVisibility(View.INVISIBLE);
                                 writeArticleImageView.setVisibility(View.INVISIBLE);
                             }
                         }

                         @Override
                         public void onFailure(Call<BlogPageResponse> call, Throwable t) {
                             setUpBlogImageView.setVisibility(View.INVISIBLE);
                             writeArticleImageView.setVisibility(View.INVISIBLE);
                             removeProgressDialog();
                             Crashlytics.logException(t);
                             Log.d("MC4KException", Log.getStackTraceString(t));
                         }
                     }
        );
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.becomeBloggerTextView:
            case R.id.setUpBlogImageView: {
                BecomeBloggerFragment becomeBloggerFragment = new BecomeBloggerFragment();
                Bundle searchBundle = new Bundle();
                becomeBloggerFragment.setArguments(searchBundle);
                ((DashboardActivity) getActivity()).addFragment(becomeBloggerFragment, searchBundle, true);
            }
            break;
            case R.id.writeArticleTextView:
            case R.id.writeArticleImageView: {
                Intent intent = new Intent(getActivity(), EditorPostActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(EditorPostActivity.TITLE_PARAM, "");
                bundle.putString(EditorPostActivity.CONTENT_PARAM, "");
                bundle.putString(EditorPostActivity.TITLE_PLACEHOLDER_PARAM,
                        getString(R.string.example_post_title_placeholder));
                bundle.putString(EditorPostActivity.CONTENT_PLACEHOLDER_PARAM,
                        getString(R.string.example_post_content_placeholder));
                bundle.putInt(EditorPostActivity.EDITOR_PARAM, EditorPostActivity.USE_NEW_EDITOR);
                bundle.putString("from", "dashboard");
                intent.putExtras(bundle);
                startActivity(intent);
            }
            break;
            case R.id.uploadVideoTextView:
            case R.id.uploadVideoImageView: {
                if (SharedPrefUtils.getFirstVideoUploadFlag(getActivity())) {
                    launchAddVideoOptions();
                } else {
                    UploadVideoInfoFragment uploadVideoInfoFragment = new UploadVideoInfoFragment();
                    Bundle searchBundle = new Bundle();
                    uploadVideoInfoFragment.setArguments(searchBundle);
                    ((DashboardActivity) getActivity()).addFragment(uploadVideoInfoFragment, searchBundle, true);
                }
            }
            break;
            case R.id.suggestedTopicImageView:
            case R.id.suggestedTopicTextView: {
                SuggestedTopicsFragment suggestedTopicsFragment = new SuggestedTopicsFragment();
                ((DashboardActivity) getActivity()).addFragment(suggestedTopicsFragment, null, true);
            }
            break;
        }
    }

    public void launchAddVideoOptions() {
        ChooseVideoUploadOptionDialogFragment chooseVideoUploadOptionDialogFragment = new ChooseVideoUploadOptionDialogFragment();
        FragmentManager fm = getChildFragmentManager();
        Bundle _args = new Bundle();
        _args.putString("activity", "dashboard");
        chooseVideoUploadOptionDialogFragment.setArguments(_args);
        chooseVideoUploadOptionDialogFragment.setCancelable(true);
        chooseVideoUploadOptionDialogFragment.show(fm, "Choose video option");
    }

}
