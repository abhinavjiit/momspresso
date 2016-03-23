package com.mycity4kids.editor;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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


import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.controller.ArticleDraftController;
import com.mycity4kids.controller.DraftListController;
import com.mycity4kids.controller.ParentingStopController;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.models.editor.ArticleDraftList;
import com.mycity4kids.models.editor.ArticleDraftListResponse;
import com.mycity4kids.models.editor.ArticleDraftRequest;
import com.mycity4kids.models.forgot.CommonResponse;
import com.mycity4kids.models.parentingdetails.ParentingDetailResponse;
import com.mycity4kids.models.parentingstop.CommonParentingList;
import com.mycity4kids.models.parentingstop.CommonParentingResponse;
import com.mycity4kids.models.parentingstop.ParentingRequest;
import com.mycity4kids.models.user.UserModel;
import com.mycity4kids.preference.SharedPrefUtils;

import java.util.ArrayList;

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

    @Override
    protected void updateUi(Response response) {
        switch (response.getDataType()) {

            case AppConstants.ARTICLE_DRAFT_LIST_REQUEST: {
                if (response.getResponseObject() instanceof ArticleDraftListResponse) {
                    ArticleDraftListResponse responseModel = (ArticleDraftListResponse) response
                            .getResponseObject();
                    if (responseModel.getResponseCode() != 200) {
                        showToast(getString(R.string.toast_response_error));
                        return;
                    } else {
                        if (!StringUtils.isNullOrEmpty(responseModel.getResult().getMessage())) {
                            //  SharedPrefUtils.setProfileImgUrl(EditorPostActivity.this, responseModel.getResult().getMessage());
                            Log.i("Draft message", responseModel.getResult().getMessage());
                        }
                        removeProgressDialog();
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
                        removeProgressDialog();
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
        addDraft=(ImageView) findViewById(R.id.addDraft);
        noDrafts=(TextView) findViewById(R.id.noDraftsTextView);
        UserTable userTable = new UserTable((BaseApplication) this.getApplication());
        userModel = userTable.getAllUserData();
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
                Intent intent1 = new Intent(DraftListView.this, EditorPostActivity.class);
                Bundle bundle5 = new Bundle();
                bundle5.putString(EditorPostActivity.TITLE_PARAM, "");
                bundle5.putString(EditorPostActivity.CONTENT_PARAM, "");
                bundle5.putString(EditorPostActivity.TITLE_PLACEHOLDER_PARAM,
                        getString(R.string.example_post_title_placeholder));
                bundle5.putString(EditorPostActivity.CONTENT_PLACEHOLDER_PARAM,
                        getString(R.string.example_post_content_placeholder));
                bundle5.putInt(EditorPostActivity.EDITOR_PARAM, EditorPostActivity.USE_NEW_EDITOR);
                bundle5.putString("from","DraftListView");
                intent1.putExtras(bundle5);
                startActivity(intent1);
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
                Intent intent = new Intent(DraftListView.this, EditorPostActivity.class);
                intent.putExtra("draftItem", draftList.get(position));
                intent.putExtra("from", "draftList");
                startActivity(intent);
            }
        });
    }

    private void hitDraftListingApi() {
        showProgressDialog(getResources().getString(R.string.please_wait));
        ArticleDraftRequest articleDraftRequest = new ArticleDraftRequest();
        /**
         * this case will case in pagination case: for sorting
         */
        articleDraftRequest.setUser_id("" + userModel.getUser().getId());
/*
        articleDraftRequest.setCity_id(SharedPrefUtils.getCurrentCityModel(getActivity()).getId());
        _parentingModel.setPage("" + pPageCount);*/
        DraftListController _controller = new DraftListController(this, this);

        _controller.getData(AppConstants.ARTICLE_DRAFT_LIST_REQUEST, articleDraftRequest);
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
