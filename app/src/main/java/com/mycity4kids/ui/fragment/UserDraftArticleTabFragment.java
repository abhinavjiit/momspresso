package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseFragment;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.editor.NewEditor;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.response.ArticleDraftResponse;
import com.mycity4kids.models.response.DraftListData;
import com.mycity4kids.models.response.DraftListResponse;
import com.mycity4kids.models.response.DraftListResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDraftAPI;
import com.mycity4kids.retrofitAPIsInterfaces.ShortStoryAPI;
import com.mycity4kids.ui.activity.AddShortStoryActivity;
import com.mycity4kids.ui.activity.UserDraftsContentActivity;
import com.mycity4kids.ui.adapter.UserDraftArticleAdapter;
import com.mycity4kids.ui.adapter.UserDraftShortStoriesAdapter;
import com.mycity4kids.utils.ConnectivityUtils;
import com.mycity4kids.utils.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant.parmar on 21-04-2016.
 */
public class UserDraftArticleTabFragment extends BaseFragment implements View.OnClickListener,
        UserDraftArticleAdapter.RecyclerViewClickListener, ConfirmationDialogFragment.IConfirmationResult,
        UserDraftShortStoriesAdapter.SSRecyclerViewClickListener {

    ArrayList<DraftListResult> draftList;
    RecyclerView recyclerView;
    TextView noBlogsTextView;
    private RelativeLayout loadingView;
    private UserDraftArticleAdapter adapter;
    private UserDraftShortStoriesAdapter shortStoriesDraftAdapter;
    private String contentType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = null;
        view = getActivity().getLayoutInflater().inflate(R.layout.user_draft_article_tab_fragment, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        loadingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);
        noBlogsTextView = (TextView) view.findViewById(R.id.noBlogsTextView);

        contentType = getArguments().getString("contentType");

        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(llm);

        if ("shortStory".equals(contentType)) {
            shortStoriesDraftAdapter = new UserDraftShortStoriesAdapter(getActivity(), this);
            recyclerView.setAdapter(shortStoriesDraftAdapter);
        } else {
            adapter = new UserDraftArticleAdapter(getActivity(), this);
            recyclerView.setAdapter(adapter);
        }

        draftList = new ArrayList<>();
        return view;
    }

    @Override
    public void onResume() {
        if ("shortStory".equals(contentType)) {
            getUserDraftStories();
        } else {
            getUserDraftArticles();
        }

        super.onResume();
    }

    private void getUserDraftStories() {
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            ((UserDraftsContentActivity) getActivity()).showToast(getString(R.string.connectivity_unavailable));
            return;
        }
        if (isAdded()) {
            showProgressDialog(getString(R.string.please_wait));
        }

        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        ShortStoryAPI shortStoryApi = retro.create(ShortStoryAPI.class);
        final Call<ResponseBody> call = shortStoryApi.getDraftsList("0,1,2,4");
        call.enqueue(userDraftArticleResponseListener);
    }

    private void getUserDraftArticles() {
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            ((UserDraftsContentActivity) getActivity()).showToast(getString(R.string.connectivity_unavailable));
            return;
        }
        if (isAdded()) {
            showProgressDialog(getString(R.string.please_wait));
        }

        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        ArticleDraftAPI userDraftArticleApi = retro.create(ArticleDraftAPI.class);
        final Call<ResponseBody> call = userDraftArticleApi.getDraftsList("0,1,2,4");
        call.enqueue(userDraftArticleResponseListener);
    }

    private Callback<ResponseBody> userDraftArticleResponseListener = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            removeProgressDialog();
            if (loadingView.getVisibility() == View.VISIBLE) {
                loadingView.setVisibility(View.GONE);
            }
            if (response.body() == null) {
                return;
            }
            try {
                String resData = new String(response.body().bytes());
                JSONObject jsonObject = new JSONObject(resData);
                int code = jsonObject.getInt("code");
                String status = jsonObject.getString("status");
                if (code == 200 && Constants.SUCCESS.equals(status)) {
                    DraftListResponse draftListResponse = new DraftListResponse();
                    DraftListData draftListData = new DraftListData();
                    JSONArray dataObj = jsonObject.optJSONArray("data");
                    if (null != dataObj) {
                        //Empty Draft List Handling
                        ArrayList<DraftListResult> emptyDraftList = new ArrayList<DraftListResult>();
                        draftListData.setResult(emptyDraftList);
                        draftListResponse.setData(draftListData);
                        processDraftResponse(draftListResponse);
                        return;
                    }

                    JSONArray resultJsonObject = jsonObject.getJSONObject("data").optJSONArray("result");
                    ArrayList<DraftListResult> draftList = new ArrayList<DraftListResult>();
                    ArrayList<Map<String, String>> retMap;
                    for (int i = 0; i < resultJsonObject.length(); i++) {
                        DraftListResult draftitem = new DraftListResult();
                        draftitem.setId(resultJsonObject.getJSONObject(i).getString("id"));
                        draftitem.setArticleType(resultJsonObject.getJSONObject(i).getString("articleType"));
                        draftitem.setCreatedTime(resultJsonObject.getJSONObject(i).getString("createdTime"));
                        draftitem.setUpdatedTime(resultJsonObject.getJSONObject(i).getLong("updatedTime"));
                        draftitem.setBody(resultJsonObject.getJSONObject(i).getString("body"));
                        draftitem.setTitle(resultJsonObject.getJSONObject(i).getString("title"));
                        if (resultJsonObject.getJSONObject(i).has("itemType")) {
                            draftitem.setItemType(resultJsonObject.getJSONObject(i).getInt("itemType"));
                        }
                        //Different formats of tags array Handling :(
                        if (resultJsonObject.getJSONObject(i).has("tags")) {
                            JSONArray tagsArray = resultJsonObject.getJSONObject(i).optJSONArray("tags");
                            if (null != tagsArray) {
                                retMap = new Gson().fromJson(tagsArray.toString(),
                                        new TypeToken<ArrayList<HashMap<String, String>>>() {
                                        }.getType());
                                draftitem.setTags(retMap);
                            } else {
                                JSONArray jsArray = resultJsonObject.getJSONObject(i).getJSONObject("tags")
                                        .optJSONArray("tagsArr");
                                retMap = new Gson().fromJson(jsArray.toString(),
                                        new TypeToken<ArrayList<HashMap<String, String>>>() {
                                        }.getType());
                                draftitem.setTags(retMap);
                            }
                        } else {
                            // no tags key in the json
                            retMap = new ArrayList<Map<String, String>>();
                            draftitem.setTags(retMap);
                        }
                        draftList.add(draftitem);
                    }
                    draftListData.setResult(draftList);
                    draftListResponse.setData(draftListData);
                    processDraftResponse(draftListResponse);
                }
            } catch (JSONException jsonexception) {
                FirebaseCrashlytics.getInstance().recordException(jsonexception);
                Log.d("JSONException", Log.getStackTraceString(jsonexception));
            } catch (Exception ex) {
                FirebaseCrashlytics.getInstance().recordException(ex);
                Log.d("MC4kException", Log.getStackTraceString(ex));
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void processDraftResponse(DraftListResponse responseModel) {
        draftList = responseModel.getData().getResult();
        if (draftList.size() == 0 && recyclerView.getVisibility() == View.VISIBLE) {
            noBlogsTextView.setVisibility(View.VISIBLE);
        } else {
            noBlogsTextView.setVisibility(View.GONE);
            if ("shortStory".equals(contentType)) {
                shortStoriesDraftAdapter.setListData(draftList);
                shortStoriesDraftAdapter.notifyDataSetChanged();
            } else {
                adapter.setListData(draftList);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onClick(View view, int position) {
        switch (view.getId()) {
            case R.id.editDraftTextView:
                Utils.pushEditDraftEvent(getActivity(), "DraftList",
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                        draftList.get(position).getId());
                Intent intent = new Intent(getActivity(), NewEditor.class);
                intent.putExtra("draftItem", draftList.get(position));
                intent.putExtra("from", "draftList");
                startActivity(intent);
                break;
            case R.id.deleteDraftImageView:
                Bundle args = new Bundle();
                args.putInt("position", position);
                ConfirmationDialogFragment confirmationDialogFragment = new ConfirmationDialogFragment();
                confirmationDialogFragment.setArguments(args);
                confirmationDialogFragment.setCancelable(true);
                FragmentManager fm = getChildFragmentManager();
                confirmationDialogFragment.show(fm, "Delete Draft");
                break;
            default:
                break;
        }
    }

    private void deleteDraftApi(DraftListResult draftObject, final int position) {
        showProgressDialog(getResources().getString(R.string.please_wait));
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        ArticleDraftAPI articleDraftApi = retrofit.create(ArticleDraftAPI.class);
        Call<ArticleDraftResponse> call = articleDraftApi.deleteDraft(
                AppConstants.LIVE_URL + "v1/articles/" + draftObject.getId());

        call.enqueue(
                new Callback<ArticleDraftResponse>() {
                    @Override
                    public void onResponse(Call<ArticleDraftResponse> call,
                            retrofit2.Response<ArticleDraftResponse> response) {
                        removeProgressDialog();
                        if (response.body() == null) {
                            return;
                        }
                        ArticleDraftResponse responseModel = response.body();

                        if (responseModel.getCode() != 200 || !Constants.SUCCESS
                                .equals(responseModel.getStatus())) {
                            if (!StringUtils.isNullOrEmpty(responseModel.getReason())) {
                                Toast.makeText(getActivity(),
                                        responseModel.getReason() + "", Toast.LENGTH_SHORT).show();

                            }
                        } else {
                            draftList.remove(position);
                            if ("shortStory".equals(contentType)) {
                                shortStoriesDraftAdapter.notifyDataSetChanged();
                            } else {
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ArticleDraftResponse> call, Throwable t) {
                        FirebaseCrashlytics.getInstance().recordException(t);
                        Log.d("MC4kException", Log.getStackTraceString(t));
                    }
                }
        );
    }


    @Override
    public void onContinue(int position) {
        Utils.pushRemoveDraftEvent(getActivity(), "DraftList",
                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                draftList.get(position).getId());
        deleteDraftApi(draftList.get(position), position);
    }

    @Override
    public void onShortStoryClick(View view, int position) {
        switch (view.getId()) {
            case R.id.editDraftTextView:
                if (Build.VERSION.SDK_INT >= 21) {
                    Utils.pushEditDraftEvent(getActivity(), "DraftList",
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                            draftList.get(position).getId());
                    Intent intent = new Intent(getActivity(), AddShortStoryActivity.class);
                    intent.putExtra("draftItem", draftList.get(position));
                    intent.putExtra("from", "draftList");
                    startActivity(intent);
                } else {
                    Intent viewIntent =
                            new Intent("android.intent.action.VIEW",
                                    Uri.parse("http://www.momspresso.com/parenting/admin/setupablog"));
                    startActivity(viewIntent);
                }
                break;
            case R.id.deleteDraftImageView:
                Bundle args = new Bundle();
                args.putInt("position", position);
                ConfirmationDialogFragment confirmationDialogFragment = new ConfirmationDialogFragment();
                confirmationDialogFragment.setArguments(args);
                confirmationDialogFragment.setCancelable(true);
                FragmentManager fm = getChildFragmentManager();
                confirmationDialogFragment.show(fm, "Delete Draft");
                break;
            default:
                break;
        }
    }
}
