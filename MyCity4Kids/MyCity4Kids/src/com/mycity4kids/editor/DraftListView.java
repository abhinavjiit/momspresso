package com.mycity4kids.editor;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;


import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.controller.ArticleDraftController;
import com.mycity4kids.controller.DraftListController;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.models.editor.ArticleDraftList;
import com.mycity4kids.models.editor.ArticleDraftListResponse;
import com.mycity4kids.models.editor.ArticleDraftRequest;
import com.mycity4kids.models.parentingdetails.ParentingDetailResponse;
import com.mycity4kids.models.user.UserModel;
import com.mycity4kids.retrofitAPIsInterfaces.GetDraftsListAPI;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import okhttp3.Interceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by anshul on 3/15/16.
 */
public class DraftListView extends BaseActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    ArrayList<ArticleDraftList> draftList;
    private UserModel userModel;
    ListView draftListview;
    DraftListAdapter adapter;
    int position;
    Toolbar mToolbar;
    ImageView addDraft;
    TextView noDrafts;
    BaseApplication baseApplication;
    private static Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = null;

    @Override
    protected void updateUi(Response response) {
        switch (response.getDataType()) {

            case AppConstants.ARTICLE_DRAFT_LIST_REQUEST: {
                if (response.getResponseObject() instanceof ArticleDraftListResponse) {
                    ArticleDraftListResponse responseModel = (ArticleDraftListResponse) response
                            .getResponseObject();
                    removeProgressDialog();
                    if (responseModel.getResponseCode() != 200) {
                        showToast(getString(R.string.toast_response_error));
                        return;
                    } else {
                        if (!StringUtils.isNullOrEmpty(responseModel.getResult().getMessage())) {
                            //  SharedPrefUtils.setProfileImgUrl(EditorPostActivity.this, responseModel.getResult().getMessage());
                            Log.i("Draft message", responseModel.getResult().getMessage());
                        }

                       /* draftList = responseModel.getResult().getData();

                        adapter = new DraftListAdapter(this, draftList);
                        draftListview.setAdapter(adapter);*/
                        processDraftResponse(responseModel);
                        //setProfileImage(originalImage);
                        //showToast("Draft Successfully saved");

                        //  finish();
                    }
                }
                break;
            }
            case AppConstants.ARTICLE_DRAFT_REQUEST: {
                if (response.getResponseObject() instanceof ParentingDetailResponse) {
                    ParentingDetailResponse responseModel = (ParentingDetailResponse) response
                            .getResponseObject();
                    removeProgressDialog();
                    if (responseModel.getResponseCode() != 200) {
                        showToast(getString(R.string.toast_response_error));
                        return;
                    } else {
                        if (!StringUtils.isNullOrEmpty(responseModel.getResult().getMessage())) {
                            //  SharedPrefUtils.setProfileImgUrl(EditorPostActivity.this, responseModel.getResult().getMessage());
                            Log.i("Draft message", responseModel.getResult().getMessage());
                        }
                        draftList.remove(position);
                        adapter.notifyDataSetChanged();
                        //   removeProgressDialog();
                        //  draftId=responseModel.getResult().getData().getId()+"";

                        //setProfileImage(originalImage);
                        //  showToast("Draft Successfully saved");
                        /*if (fromBackpress) {
                            super.onBackPressed();
                        }*/
                        //  finish();
                    }
                }
                break;
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.draft_listview);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Drafts");
        draftListview = (ListView) findViewById(R.id.draftListview);
        addDraft = (ImageView) findViewById(R.id.addDraft);
        noDrafts = (TextView) findViewById(R.id.noDraftsTextView);
        UserTable userTable = new UserTable((BaseApplication) this.getApplication());
        userModel = userTable.getAllUserData();
        baseApplication = (BaseApplication) getApplication();
        REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                okhttp3.Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control",
                                String.format("max-age=%d", 60))
                        .build();
            }
        };
      /*  hitDraftListingApi();
        draftListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(DraftListView.this, EditorPostActivity.class);
                intent.putExtra("draftItem", draftList.get(position));
                intent.putExtra("from", "draftList");
                startActivity(intent);
            }
        });*/
        addDraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT > 15) {
                    Intent intent1 = new Intent(DraftListView.this, EditorPostActivity.class);
                    Bundle bundle5 = new Bundle();
                    bundle5.putString(EditorPostActivity.TITLE_PARAM, "");
                    bundle5.putString(EditorPostActivity.CONTENT_PARAM, "");
                    bundle5.putString(EditorPostActivity.TITLE_PLACEHOLDER_PARAM,
                            getString(R.string.example_post_title_placeholder));
                    bundle5.putString(EditorPostActivity.CONTENT_PLACEHOLDER_PARAM,
                            getString(R.string.example_post_content_placeholder));
                    bundle5.putInt(EditorPostActivity.EDITOR_PARAM, EditorPostActivity.USE_NEW_EDITOR);
                    bundle5.putString("from", "DraftListView");
                    intent1.putExtras(bundle5);
                    startActivity(intent1);
                } else {
                    Intent viewIntent =
                            new Intent("android.intent.action.VIEW",
                                    Uri.parse("http://www.mycity4kids.com/parenting/admin/setupablog"));
                    startActivity(viewIntent);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // according to fragment change it

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        hitDraftListingApi();
        draftListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (Build.VERSION.SDK_INT > 15) {
                    Intent intent = new Intent(DraftListView.this, EditorPostActivity.class);
                    intent.putExtra("draftItem", draftList.get(position));
                    intent.putExtra("from", "draftList");
                    startActivity(intent);
                } else {
                    Intent viewIntent =
                            new Intent("android.intent.action.VIEW",
                                    Uri.parse("http://www.mycity4kids.com/parenting/admin/setupablog"));
                    startActivity(viewIntent);
                }
            }
        });
    }

    private void hitDraftListingApi() {
        //  showProgressDialog(getResources().getString(R.string.please_wait));
   /*     OkHttpClient client = new OkHttpClient
                .Builder()
                .cache(new Cache(baseApplication.getCacheDir(), 10 * 1024 * 1024)) // 10 MB
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        if (ConnectivityUtils.isNetworkEnabled(DraftListView.this)) {
                            request = request.newBuilder().header("Cache-Control", "public, max-age=" + 60).build();
                        } else {
                            request = request.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7).build();
                        }
                        return chain.proceed(request);
                    }
                })
                .addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();*/

        // prepare call in Retrofit 2.0
    /*    Retrofit retrofit=BaseApplication.getInstance().getRetrofitInstance();
        GetDraftsListAPI getDraftsListAPI = retrofit.create(GetDraftsListAPI.class);

        Call<ArticleDraftListResponse> call = getDraftsListAPI.getDraftsList("" + userModel.getUser().getId());
        //asynchronous call
        call.enqueue(new Callback<ArticleDraftListResponse>() {
                         @Override
                         public void onResponse(Call<ArticleDraftListResponse> call, retrofit2.Response<ArticleDraftListResponse> response) {
                             int statusCode = response.code();
                             ArticleDraftListResponse responseModel = response.body();

                             removeProgressDialog();
                             if (responseModel.getResponseCode() != 200) {
                                 showToast(getString(R.string.toast_response_error));
                                 return;
                             } else {
                                 if (!StringUtils.isNullOrEmpty(responseModel.getResult().getMessage())) {
                                     //  SharedPrefUtils.setProfileImgUrl(EditorPostActivity.this, responseModel.getResult().getMessage());
                                     Log.i("Retrofit Publish Message", responseModel.getResult().getMessage());
                                 }
                                 if (responseModel.getResponse().toString().equals("success")) {
                                     processDraftResponse(responseModel);



                                     showToast(responseModel.getResult().getMessage());

                                 } else {
                                     showToast(responseModel.getResult().getMessage().toString());
                                 }


                             }

                         }


                         @Override
                         public void onFailure(Call<ArticleDraftListResponse> call, Throwable t) {

                         }
                     }
        );*/

        String url = AppConstants.BASE_URL + "apiblogs/getDraftLists";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("response Volley", response);

              //  processDraftResponse();

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }


        })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userId", "" + userModel.getUser().getId());
                return params;
            }

            @Override
            protected com.android.volley.Response<String> parseNetworkResponse(NetworkResponse response) {

                Map<String,String> responseHeader=response.headers;
                String hhh = new String(response.data);
              //  return super.parseNetworkResponse(response);
                return com.android.volley.Response.success(hhh, parseIgnoreCacheHeaders(response));
            }

            public  Cache.Entry parseIgnoreCacheHeaders(NetworkResponse response) {
                long now = System.currentTimeMillis();

                Map<String, String> headers = response.headers;
                long serverDate = 0;
                String serverEtag = null;
                String headerValue;

                headerValue = headers.get("Date");
                if (headerValue != null) {
                    serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
                }

                serverEtag = headers.get("ETag");

                final long cacheHitButRefreshed = 5 * 60 * 1000; // in 3 minutes cache will be hit, but also refreshed on background
                final long cacheExpired = 10 * 60 * 1000; // in 24 hours this cache entry expires completely
                final long softExpire = now + cacheHitButRefreshed;
                final long ttl = now + cacheExpired;

                Cache.Entry entry = new Cache.Entry();
                entry.data = response.data;
                entry.etag = serverEtag;
                entry.softTtl = softExpire;
                entry.ttl = ttl;
                entry.serverDate = serverDate;
                entry.responseHeaders = headers;

                return entry;
            }
        };

     /*   if (BaseApplication.getInstance().getRequestQueue().getCache().get(Request.Method.POST + ":" + url) != null) {
            //response exists
            String cachedResponse = new String(BaseApplication.getInstance().getRequestQueue().getCache().get(Request.Method.POST + ":" + url).data);
            // results.setText("From Cache: " + cachedResponse);
            Log.e("in cache", "yo");
        } else {
            //no response
            BaseApplication.getInstance().add(stringRequest);
        }*/
        BaseApplication.getInstance().add(stringRequest);

        /*ArticleDraftRequest articleDraftRequest = new ArticleDraftRequest();

        articleDraftRequest.setUser_id("" + userModel.getUser().getId());

        DraftListController _controller = new DraftListController(this, this);

        _controller.getData(AppConstants.ARTICLE_DRAFT_LIST_REQUEST, articleDraftRequest);*/
    }

    public void deleteDraftAPI(ArticleDraftList draftObject, int p) {
        position = p;
        showProgressDialog(getResources().getString(R.string.please_wait));
        ArticleDraftRequest articleDraftRequest = new ArticleDraftRequest();
        /**
         * this case will case in pagination case: for sorting
         */
        articleDraftRequest.setUser_id("" + userModel.getUser().getId());
        articleDraftRequest.setId(draftObject.getId());
        articleDraftRequest.setBody(draftObject.getBody());
        articleDraftRequest.setTitle(draftObject.getTitle());
        articleDraftRequest.setStatus("1");
/*
        articleDraftRequest.setCity_id(SharedPrefUtils.getCurrentCityModel(getActivity()).getId());
        _parentingModel.setPage("" + pPageCount);*/
        ArticleDraftController _controller = new ArticleDraftController(this, this);

        _controller.getData(AppConstants.ARTICLE_DRAFT_REQUEST, articleDraftRequest);
    }

    @Override
    public void onClick(final View v) {
        v.post(new Runnable() {
            @Override
            public void run() {
                showPopupMenu(v);
            }
        });
    }

    private void showPopupMenu(View view) {

        PopupMenu popup = new PopupMenu(this, view);

        popup.getMenuInflater().inflate(R.menu.pop_menu_draft, popup.getMenu());

        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                Log.e("huhuhu", "bhbhbhbhb");
                //    archive(item);
                return true;
            case R.id.delete:
                Log.e("huhuhu", "nnnnnnn");
                //   delete(item);
                return true;
            default:
                return false;
        }
    }

    private void processDraftResponse(ArticleDraftListResponse responseModel) {
        draftList = responseModel.getResult().getData();


        if (draftList.size() == 0) {
  /*          articleDataModelsNew = dataList;
            articlesListingAdapter.setNewListData(articleDataModelsNew);
            articlesListingAdapter.notifyDataSetChanged();*/
            noDrafts.setVisibility(View.VISIBLE);
            //((DashboardActivity) getActivity()).showToast(responseData.getResult().getMessage());
        } else {
            noDrafts.setVisibility(View.GONE);


            adapter = new DraftListAdapter(this, draftList);
            draftListview.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

    }

}
