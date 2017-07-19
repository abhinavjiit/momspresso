package com.mycity4kids.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ConnectivityUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.response.DraftListData;
import com.mycity4kids.models.response.DraftListResponse;
import com.mycity4kids.models.response.DraftListResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDraftAPI;
import com.mycity4kids.ui.activity.UserPublishedAndDraftsActivity;
import com.mycity4kids.ui.adapter.UserDraftArticleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant.parmar on 21-04-2016.
 */
public class UserDraftArticleTabFragment extends BaseFragment implements View.OnClickListener {

    ArrayList<DraftListResult> draftList;
    RecyclerView recyclerView;
    private RelativeLayout mLodingView;
    private UserDraftArticleAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = null;
        view = getActivity().getLayoutInflater().inflate(R.layout.user_draft_article_tab_fragment, container, false);

        Utils.pushOpenScreenEvent(getActivity(), "Search Articles Fragment Listing", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "");


        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mLodingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);

        adapter = new UserDraftArticleAdapter(getActivity());
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);
        draftList = new ArrayList<DraftListResult>();

        //only when first time fragment is created
        getUserDraftArticles();

        return view;
    }

    private void getUserDraftArticles() {
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            ((UserPublishedAndDraftsActivity) getActivity()).showToast("No connectivity available");
            return;
        }

        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        ArticleDraftAPI userDraftArticleAPI = retro.create(ArticleDraftAPI.class);
        final Call<ResponseBody> call = userDraftArticleAPI.getDraftsList("0,1,2,4");
        call.enqueue(userDraftArticleResponseListener);
    }

    @Override
    protected void updateUi(Response response) {

    }

    private Callback<ResponseBody> userDraftArticleResponseListener = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            removeProgressDialog();
            if (mLodingView.getVisibility() == View.VISIBLE) {
                mLodingView.setVisibility(View.GONE);
            }
            if (response == null || response.body() == null) {
                return;
            }
            try {
                String resData = new String(response.body().bytes());
                JSONObject jObject = new JSONObject(resData);
                int code = jObject.getInt("code");
                String status = jObject.getString("status");
                if (code == 200 && Constants.SUCCESS.equals(status)) {
                    DraftListResponse draftListResponse = new DraftListResponse();
                    DraftListData draftListData = new DraftListData();
                    JSONArray dataObj = jObject.optJSONArray("data");
                    if (null != dataObj) {
                        //Empty Draft List Handling
                        ArrayList<DraftListResult> emptyDraftList = new ArrayList<DraftListResult>();
                        draftListData.setResult(emptyDraftList);
                        draftListResponse.setData(draftListData);
                        processDraftResponse(draftListResponse);
                        return;
                    }

                    JSONArray resultJsonObject = jObject.getJSONObject("data").optJSONArray("result");
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
                                retMap = new Gson().fromJson(tagsArray.toString(), new TypeToken<ArrayList<HashMap<String, String>>>() {
                                }.getType());
                                draftitem.setTags(retMap);
                            } else {
                                JSONArray jsArray = resultJsonObject.getJSONObject(i).getJSONObject("tags").optJSONArray("tagsArr");
                                retMap = new Gson().fromJson(jsArray.toString(), new TypeToken<ArrayList<HashMap<String, String>>>() {
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
                } else {
//                    showToast(jObject.getString("reason"));
                }
            } catch (JSONException jsonexception) {
                Crashlytics.logException(jsonexception);
                Log.d("JSONException", Log.getStackTraceString(jsonexception));
//                showToast("Something went wrong while parsing response from server");
            } catch (Exception ex) {
                Crashlytics.logException(ex);
                Log.d("MC4kException", Log.getStackTraceString(ex));
//                showToast("Something went wrong from server");
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void processDraftResponse(DraftListResponse responseModel) {
        draftList = responseModel.getData().getResult();

        if (draftList.size() == 0 && recyclerView.getVisibility() == View.VISIBLE) {
//            noDraftTextView.setVisibility(View.VISIBLE);
        } else {
            adapter.setListData(draftList);
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.searchArticleShowMoreTextView:
//                if (!isReuqestRunning) {
//                    showMoreType = "article";
//                    isReuqestRunning = true;
//                    newSearchTopicArticleListingApi(searchName, "article");
//                }
//                break;
//            case R.id.searchTopicShowMoreTextView:
//                if (!isReuqestRunning) {
//                    showMoreType = "topic";
//                    isReuqestRunning = true;
//                    newSearchTopicArticleListingApi(searchName, "topic");
//                    break;
//                }
        }
    }
}