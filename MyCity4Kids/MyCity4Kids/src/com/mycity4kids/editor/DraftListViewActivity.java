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

import com.google.gson.Gson;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.models.editor.ArticleDraftList;
import com.mycity4kids.models.editor.ArticleDraftListResponse;
import com.mycity4kids.models.editor.ArticleDraftRequest;
import com.mycity4kids.models.response.BaseResponse;
import com.mycity4kids.models.response.DraftListResponse;
import com.mycity4kids.models.response.DraftListResult;
import com.mycity4kids.models.response.DraftResponse;
import com.mycity4kids.models.user.UserModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDraftAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by anshul on 3/15/16.
 */
public class DraftListViewActivity extends BaseActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    ArrayList<DraftListResult> draftList;
    private UserModel userModel;
    ListView draftListview;
    DraftListAdapter adapter;
    int position;
    Toolbar mToolbar;
    ImageView addDraft;
    TextView noDrafts;

    @Override
    protected void updateUi(Response response) {
        switch (response.getDataType()) {

            case AppConstants.ARTICLE_DRAFT_LIST_REQUEST: {
            /*    if (response.getResponseObject() instanceof ArticleDraftListResponse) {
                    ArticleDraftListResponse responseModel = (ArticleDraftListResponse) response
                            .getResponseObject();
                    removeProgressDialog();
                    if (responseModel.getResponseCode() != 200) {
                        showToast(getString(R.string.toast_response_error));
                        return;
                    } else {
                        if (!StringUtils.isNullOrEmpty(responseModel.getResult().getMessage())) {
                            Log.i("Draft message", responseModel.getResult().getMessage());
                        }

                        processDraftResponse(responseModel);
                    }
                }
                break;
            */
            }
            case AppConstants.ARTICLE_DRAFT_REQUEST: {
                /*if (response.getResponseObject() instanceof ParentingDetailResponse) {
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
                    }
                }
                break;*/
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
        addDraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT > 15) {
                    Intent intent1 = new Intent(DraftListViewActivity.this, EditorPostActivity.class);
                    Bundle bundle5 = new Bundle();
                    bundle5.putString(EditorPostActivity.TITLE_PARAM, "");
                    bundle5.putString(EditorPostActivity.CONTENT_PARAM, "");
                    bundle5.putString(EditorPostActivity.TITLE_PLACEHOLDER_PARAM,
                            getString(R.string.example_post_title_placeholder));
                    bundle5.putString(EditorPostActivity.CONTENT_PLACEHOLDER_PARAM,
                            getString(R.string.example_post_content_placeholder));
                    bundle5.putInt(EditorPostActivity.EDITOR_PARAM, EditorPostActivity.USE_NEW_EDITOR);
                    bundle5.putString("from", "DraftListViewActivity");
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
                    Intent intent = new Intent(DraftListViewActivity.this, EditorPostActivity.class);
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
        showProgressDialog(getResources().getString(R.string.please_wait));
       /* ArticleDraftRequest articleDraftRequest = new ArticleDraftRequest();
        *//**
         * this case will case in pagination case: for sorting
         *//*
        articleDraftRequest.setUser_id("" + userModel.getUser().getId());
        DraftListController _controller = new DraftListController(this, this);

        _controller.getData(AppConstants.ARTICLE_DRAFT_LIST_REQUEST, articleDraftRequest);*/
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        // prepare call in Retrofit 2.0
        ArticleDraftAPI getDraftListAPI = retrofit.create(ArticleDraftAPI.class);
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog();
            showToast(getString(R.string.error_network));
            return;
        }
        Call<DraftListResponse> call = getDraftListAPI.getDraftsList( AppConstants.LIVE_URL+"v1/articles/"+ SharedPrefUtils.getUserDetailModel(getApplicationContext()).getDynamoId());
        //asynchronous call
        call.enqueue(new Callback<DraftListResponse>() {
                         @Override
                         public void onResponse(Call<DraftListResponse> call, retrofit2.Response<DraftListResponse> response) {
                             int statusCode = response.code();
                             removeProgressDialog();
                            // ResponseBody responseModel = (ResponseBody) response.body();
                    /*         String responseData = null;
                             try {
                                 responseData = new String(response.body().bytes());
                             } catch (IOException e) {
                                 e.printStackTrace();
                             }
                             JSONObject jsonObject = null;
                             try {
                                 jsonObject = new JSONObject(responseData);

                                 JSONArray dataObj = null;

                                 dataObj = jsonObject.getJSONObject("result").optJSONArray("data");


                                 if (null == dataObj) {

                                     jsonObject.getJSONObject("result").remove("data");


                                     jsonObject.getJSONObject("result").put("data", new JSONArray());

                                 }

                                 responseData = jsonObject.toString();
                             }catch (JSONException e) {
                                 e.printStackTrace();
                             }*/
                        //     ArticleDraftListResponse responseModel = new Gson().fromJson(responseData, ArticleDraftListResponse.class);
                             DraftListResponse responseModel=response.body();
                             if (responseModel.getCode() != 200) {
                                 showToast(getString(R.string.toast_response_error));
                                 return;
                             } else {
                                 if (!StringUtils.isNullOrEmpty(responseModel.getData().getMsg())) {
                                     Log.i("Draft message",responseModel.getData().getMsg());
                                 }

                                processDraftResponse(responseModel);

                         }}


                         @Override
                         public void onFailure(Call<DraftListResponse> call, Throwable t) {
                             removeProgressDialog();

                         }
                     }
        );

    }

    public void deleteDraftAPI(DraftListResult draftObject, int p) {
        position = p;
        showProgressDialog(getResources().getString(R.string.please_wait));
        ArticleDraftRequest articleDraftRequest = new ArticleDraftRequest();
        /**
         * this case will case in pagination case: for sorting
         */
        /*articleDraftRequest.setUser_id("" + userModel.getUser().getId());
        articleDraftRequest.setId(draftObject.getId());
        articleDraftRequest.setBody(draftObject.getBody());
        articleDraftRequest.setTitle(draftObject.getTitle());
        articleDraftRequest.setStatus("1");
        ArticleDraftController _controller = new ArticleDraftController(this, this);

        _controller.getData(AppConstants.ARTICLE_DRAFT_REQUEST, articleDraftRequest);*/
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        // prepare call in Retrofit 2.0
        ArticleDraftAPI articleDraftAPI = retrofit.create(ArticleDraftAPI.class);
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            showToast(getString(R.string.error_network));
            return;
        }
        Call<DraftResponse> call = articleDraftAPI.deleteDraft(
                AppConstants.LIVE_URL+"v1/articles/"+draftObject.getId());


        //asynchronous call
        call.enqueue(new Callback<DraftResponse>() {
                         @Override
                         public void onResponse(Call<DraftResponse> call, retrofit2.Response<DraftResponse> response) {
                             int statusCode = response.code();

                             DraftResponse responseModel = (DraftResponse) response.body();

                             removeProgressDialog();

                             if (responseModel.getCode() != 200) {
                                 showToast(getString(R.string.toast_response_error));
                                 return;
                             } else {
                                 if (!StringUtils.isNullOrEmpty(responseModel.getData().getMsg())) {
                                     //  SharedPrefUtils.setProfileImgUrl(EditorPostActivity.this, responseModel.getResult().getMessage());
                                     Log.i("Draft message", responseModel.getData().getMsg());
                                 }
                                 draftList.remove(position);
                                 adapter.notifyDataSetChanged();
                             }

                         }


                         @Override
                         public void onFailure(Call<DraftResponse> call, Throwable t) {

                         }
                     }
        );
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

    private void processDraftResponse(DraftListResponse responseModel) {
        draftList = responseModel.getData().getResult();

        if (draftList.size() == 0) {
            noDrafts.setVisibility(View.VISIBLE);
        } else {
            noDrafts.setVisibility(View.GONE);
            adapter = new DraftListAdapter(this, draftList);
            draftListview.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

    }
}
