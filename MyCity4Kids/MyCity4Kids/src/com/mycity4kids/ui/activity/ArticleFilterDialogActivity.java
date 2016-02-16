package com.mycity4kids.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.ParentingFilterController;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.parentingstop.ParentingRequest;
import com.mycity4kids.newmodels.parentingmodel.ArticleFilterListModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.adapter.ArticleFilterExpendableAdaper;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by manish.soni on 22-07-2015.
 */
public class ArticleFilterDialogActivity extends BaseActivity implements View.OnClickListener {

    ExpandableListView expandableListView;
    ArticleFilterExpendableAdaper articleFilterExpendableAdaper;
    ArrayList<ArticleFilterListModel.FilterTopic> topicsList;
    LinkedHashMap<ArticleFilterListModel.FilterTopic, ArrayList<ArticleFilterListModel.SubFilerList>> filterSubItemList;
    ArticleFilterListModel finalData;
    EditText searchText;
    ImageView searchBtn;
    TextWatcher textWatcher;
    TextView cancel, reset, meetContributors;
    LinearLayout filterCancel;
    View divider1, divider2, divider3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        Utils.pushOpenScreenEvent(ArticleFilterDialogActivity.this, "Blogs Filter Dialogue Screen", SharedPrefUtils.getUserDetailModel(this).getId() + "");
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        setContentView(R.layout.filter_dialog_activity);
        expandableListView = (ExpandableListView) findViewById(R.id.article_filter_list);
        searchText = (EditText) findViewById(R.id.search_text);
        searchBtn = (ImageView) findViewById(R.id.search_btn);
        cancel = (TextView) findViewById(R.id.cancel_filter);
        reset = (TextView) findViewById(R.id.reset_filter);
        filterCancel = (LinearLayout) findViewById(R.id.cancel_reset);
        meetContributors = (TextView) findViewById(R.id.meet_contributors);
        divider1 = (View) findViewById(R.id.view1);
        divider2 = (View) findViewById(R.id.view2);
        divider3 = (View) findViewById(R.id.view3);
        meetContributors.setOnClickListener(this);
        expandableListView.setGroupIndicator(null);

        searchText.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);
        reset.setVisibility(View.GONE);
        filterCancel.setVisibility(View.GONE);
        meetContributors.setVisibility(View.GONE);
        divider1.setVisibility(View.GONE);
        divider2.setVisibility(View.GONE);
        divider3.setVisibility(View.GONE);

        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 0) {
                    searchBtn.setVisibility(View.GONE);
                } else {
                    searchBtn.setVisibility(View.VISIBLE);
                }
            }
        };

        searchText.addTextChangedListener(textWatcher);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousGroup)
                    expandableListView.collapseGroup(previousGroup);
                previousGroup = groupPosition;
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {

                ArticleFilterListModel.SubFilerList selectedItem = (ArticleFilterListModel.SubFilerList) articleFilterExpendableAdaper.getChild(i, i1);

                Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                intent.putExtra(Constants.FILTER_NAME, selectedItem.getName());
                intent.putExtra(Constants.RESET_FILTER, false);
                setResult(RESULT_OK, intent);
                finish();

                return false;
            }
        });

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int i) {
                ArticleFilterListModel.FilterTopic model = (ArticleFilterListModel.FilterTopic) articleFilterExpendableAdaper.getGroup(i);
                Intent intent = new Intent(ArticleFilterDialogActivity.this, LoadWebViewActivity.class);

                if (model.getIsNewsletter() != null && model.getIsNewsletter()) {
                    intent.putExtra(Constants.WEB_VIEW_URL, model.getId());
                    startActivity(intent);
                } else {
                    if (model.getSubcategory() == null || model.getSubcategory().size() == 0) {
                        intent.putExtra(Constants.FILTER_NAME, model.getName());
                        intent.putExtra(Constants.RESET_FILTER, false);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {

                    }
                }
            }
        });


        searchBtn.setOnClickListener(this);
        reset.setOnClickListener(this);
        cancel.setOnClickListener(this);

        finalData = ((BaseApplication) getApplication()).getFilterList();

        if (finalData == null) {
            hitFilterListapi();
        } else {
            setFilterDataAndUpdateViews();
        }

//        if (!StringUtils.isNullOrEmpty(topicsJson)) {
//            topicsList = new Gson().fromJson(topicsJson, new TypeToken<List<ArticleFilterListModel.FilterTopic>>() {
//            }.getType());
//            setFilterDataAndUpdateViews();
//        } else {
//            hitFilterListapi();
//        }

    }


    @Override
    protected void updateUi(Response response) {
        if (response == null) {
            showToast("Something went wrong from server");
            removeProgressDialog();
            finish();
            return;
        }

        switch (response.getDataType()) {

            case AppConstants.PARENTING_FILTER_LIST:
                ArticleFilterListModel newResponseData = (ArticleFilterListModel) response.getResponseObject();
                if (newResponseData.getResponseCode() == 200) {
                    removeProgressDialog();
//                    topicsList = newResponseData.getResult().getData().getTopics();
//                    SharedPrefUtils.setArticleFiltersData(this, new Gson().toJson(topicsList));

                    finalData = newResponseData;
                    topicsList = finalData.getResult().getData().getTopics();
                    ArticleFilterListModel.FilterTopic extentNewsletter = new ArticleFilterListModel().new FilterTopic();
                    extentNewsletter.setName(finalData.getResult().getData().getNewsletter().getText());
                    extentNewsletter.setId(finalData.getResult().getData().getNewsletter().getUrl());
                    extentNewsletter.setIsNewsletter(true);
                    topicsList.add(extentNewsletter);
                    finalData.getResult().getData().setTopics(topicsList);

                    ((BaseApplication) getApplication()).setFilterList(finalData);
//                    finalData = newResponseData;
                    setFilterDataAndUpdateViews();

                } else if (newResponseData.getResponseCode() == 400) {

                    removeProgressDialog();
                    String message = newResponseData.getResult().getMessage();
                    if (!StringUtils.isNullOrEmpty(message)) {
                        showToast(message);
                    } else {
                        showToast(getString(R.string.went_wrong));
                    }
                }

                break;

            default:
                break;
        }

    }


    public void hitFilterListapi() {

        showProgressDialog(getString(R.string.please_wait));

        ParentingRequest _parentingModel = new ParentingRequest();
        _parentingModel.setCity_id(SharedPrefUtils.getCurrentCityModel(this).getId());

        ParentingFilterController _controller = new ParentingFilterController(this, this);
        _controller.getData(AppConstants.PARENTING_FILTER_LIST, _parentingModel);

    }

    public void setFilterDataAndUpdateViews() {

        topicsList = finalData.getResult().getData().getTopics();

        filterSubItemList = new LinkedHashMap<>();
        for (int i = 0; i < topicsList.size(); i++) {
            filterSubItemList.put(topicsList.get(i), topicsList.get(i).getSubcategory());
        }

//        ArticleFilterListModel.FilterTopic extentNewsletter = new ArticleFilterListModel().new FilterTopic();
//        extentNewsletter.setName(finalData.getResult().getData().getNewsletter().getText());
//        extentNewsletter.setId(finalData.getResult().getData().getNewsletter().getUrl());
//        extentNewsletter.setIsNewsletter(true);
//        topicsList.add(extentNewsletter);

        articleFilterExpendableAdaper = new ArticleFilterExpendableAdaper(this, topicsList, filterSubItemList);
        expandableListView.setVisibility(View.VISIBLE);
        expandableListView.setAdapter(articleFilterExpendableAdaper);

        searchText.setVisibility(View.VISIBLE);
        cancel.setVisibility(View.VISIBLE);
        reset.setVisibility(View.VISIBLE);
        filterCancel.setVisibility(View.VISIBLE);
        meetContributors.setVisibility(View.VISIBLE);
        divider1.setVisibility(View.VISIBLE);
        divider2.setVisibility(View.VISIBLE);
        divider3.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {

        Intent intent;

        switch (view.getId()) {

            case R.id.search_btn:

                if (String.valueOf(searchText.getText().toString().trim()).equalsIgnoreCase("")) {

                    ToastUtils.showToast(this, "Enter text to search...");

                } else {
                    InputMethodManager imm = (InputMethodManager) getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);

                    intent = new Intent(getApplicationContext(), DashboardActivity.class);
                    intent.putExtra(Constants.FILTER_NAME, searchText.getText().toString());
                    intent.putExtra(Constants.RESET_FILTER, false);
                    intent.putExtra(Constants.IS_MEET_CONTRIBUTORS_SELECTED, false);
                    setResult(RESULT_OK, intent);
                    finish();
                }

                break;
            case R.id.reset_filter:

                intent = new Intent(getApplicationContext(), DashboardActivity.class);
                intent.putExtra(Constants.FILTER_NAME, searchText.getText().toString());
                intent.putExtra(Constants.RESET_FILTER, true);
                intent.putExtra(Constants.IS_MEET_CONTRIBUTORS_SELECTED, false);
                intent.putExtra(Constants.IS_FIRST_RUN, false);
                setResult(RESULT_OK, intent);
                finish();

                break;
            case R.id.cancel_filter:

                finish();

                break;

            case R.id.meet_contributors:

                intent = new Intent(getApplicationContext(), DashboardActivity.class);
                intent.putExtra(Constants.FILTER_NAME, "");
                intent.putExtra(Constants.RESET_FILTER, false);
                intent.putExtra(Constants.IS_MEET_CONTRIBUTORS_SELECTED, true);
                setResult(RESULT_OK, intent);
                finish();

                break;

        }

    }
}