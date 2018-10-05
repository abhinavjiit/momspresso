package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.response.DraftListData;
import com.mycity4kids.models.response.DraftListResponse;
import com.mycity4kids.models.response.DraftListResult;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDraftAPI;
import com.mycity4kids.ui.activity.Mainprofile;
import com.mycity4kids.ui.adapter.Recyclenormal;

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

public class SheetFragment extends Fragment implements View.OnClickListener {
    LinearLayout linearLayout;
    private String contentType = "";
    ArrayList<DraftListResult> draftList;
    TextView textView;
    Recyclenormal adapter;
    RelativeLayout relativeLayout;

    View view;
    ShimmerFrameLayout mShimmerFrameLayout;
    RecyclerView recyclerView1;
    TextView test_profile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_bottom_sheet_dialog, container, false);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.bottomsheet);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.abhii);
        mShimmerFrameLayout = view.findViewById(R.id.shimmer);
        test_profile = (TextView) view.findViewById(R.id.test_profile);
        //contentType = getArguments().getString("contentType");
        recyclerView1 = (RecyclerView) view.findViewById(R.id.recyclerview);
        LinearLayoutManager ll = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView1.setLayoutManager(ll);
        if ("shortStory".equals(contentType)) {
        } else {

            adapter = new Recyclenormal(getActivity());
            recyclerView1.setAdapter(adapter);
        }
        test_profile.setOnClickListener(this);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
//        recyclerView.setLayoutManager(linearLayoutManager);
//         LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getActivity());
//        recyclerView1.setLayoutManager(linearLayoutManager);
        //  recyclerView1.setAdapter(new Recyclenormal(getActivity()));
//        if ("shortStory".equals(contentType)) {
//        } else {
//            adapter = new Recyclenormal(getActivity());
//            recyclerView1.setAdapter(adapter);
//        }
        draftList = new ArrayList<DraftListResult>();
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        ArticleDraftAPI userDraftArticleAPI = retro.create(ArticleDraftAPI.class);
        final Call<ResponseBody> call = userDraftArticleAPI.getDraftsList("0,1,2,4");
        call.enqueue(userDraftArticleResponseListener);


        // relativeLayout.setVisibility(view.getVisibility());
        // linearLayout = (LinearLayout) view.findViewById(R.id.linearr);

        // linearLayout.setOnClickListener(this);
        return view;
    }

    private Callback<ResponseBody> userDraftArticleResponseListener = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            // removeProgressDialog();
            //    if (mLodingView.getVisibility() == View.VISIBLE) {
            //      mLodingView.setVisibility(View.GONE);
            //}

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
                    mShimmerFrameLayout.stopShimmerAnimation();
                    mShimmerFrameLayout.setVisibility(View.GONE);
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
        public void onFailure(Call<ResponseBody> call, Throwable throwable) {

        }
    };


 /*   @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linearr:
                relativeLayout.setVisibility(View.INVISIBLE);

                //   Toast.makeText(getActivity(), "gdhg", Toast.LENGTH_SHORT).show();

        }
    }
    };*/

    private void processDraftResponse(DraftListResponse responseModel) {
        draftList = responseModel.getData().getResult();

        if (draftList.size() == 0 && recyclerView1.getVisibility() == View.VISIBLE) {
//            noDraftTextView.setVisibility(View.VISIBLE);
        } else {
            // if ("shortStory".equals(contentType)) {
            //   shortStoriesDraftAdapter.setListData(draftList);
            // shortStoriesDraftAdapter.notifyDataSetChanged();
            //} else {
            adapter.setListData(draftList);
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mShimmerFrameLayout.startShimmerAnimation();
    }

    @Override
    public void onPause() {
        mShimmerFrameLayout.stopShimmerAnimation();
        super.onPause();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.test_profile) {
            Intent intent = new Intent(getActivity(), Mainprofile.class);
            startActivity(intent);

        }
    }
}