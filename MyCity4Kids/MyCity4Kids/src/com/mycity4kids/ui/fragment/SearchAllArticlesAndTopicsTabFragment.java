package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.response.SearchArticleResult;
import com.mycity4kids.models.response.SearchResponse;
import com.mycity4kids.models.response.SearchTopicResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.SearchArticlesAuthorsAPI;
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity;
import com.mycity4kids.ui.activity.FilteredTopicsArticleListingActivity;
import com.mycity4kids.ui.activity.SearchAllActivity;
import com.mycity4kids.ui.adapter.SearchAllArticlesTopicsListingAdapter;
import com.mycity4kids.ui.adapter.SearchArticlesListingAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant.parmar on 21-04-2016.
 */
public class SearchAllArticlesAndTopicsTabFragment extends BaseFragment implements View.OnClickListener, SearchAllArticlesTopicsListingAdapter.RecyclerViewClickListener {

    SearchArticlesListingAdapter articlesListingAdapter;
    ArrayList<SearchArticleResult> articleDataModelsNew;
    RecyclerView recyclerView;
    TextView noBlogsTextView;
    String sortType;
    String searchName = "";
    private int limit = 3;
    private int nextPageNumber = 2;
    private boolean isReuqestRunning = false;
    private boolean fragmentResume = false;
    private boolean fragmentVisible = false;
    boolean isLastPageReached = true;
    private int articleShowMoreIndex = 0;
    private int topicShowMoreIndex = 0;
    private SearchAllArticlesTopicsListingAdapter adapter;
    private ArrayList<SearchArticleTopicResult> data = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = null;
        view = getActivity().getLayoutInflater().inflate(R.layout.search_all_article_topic_tab_fragment, container, false);

        Utils.pushOpenScreenEvent(getActivity(), "Search Articles Fragment Listing", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "");


        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        noBlogsTextView = (TextView) view.findViewById(R.id.noBlogsTextView);

        adapter = new SearchAllArticlesTopicsListingAdapter(getActivity(), this);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);
        if (getArguments() != null) {
            searchName = getArguments().getString(Constants.SEARCH_PARAM);
        }
        articleDataModelsNew = new ArrayList<SearchArticleResult>();

        if (StringUtils.isNullOrEmpty(searchName)) {

        } else if (!fragmentResume && fragmentVisible) {
            //only when first time fragment is created
            nextPageNumber = 1;
            newSearchTopicArticleListingApi(searchName, "all");
        }

        return view;
    }

    @Override
    protected void updateUi(Response response) {

    }

    private void newSearchTopicArticleListingApi(String searchName, String type) {
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            ((SearchAllActivity) getActivity()).showToast("No connectivity available");
            return;
        }

        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        SearchArticlesAuthorsAPI searchArticlesAuthorsAPI = retro.create(SearchArticlesAuthorsAPI.class);
        int from = 1;
        if (type.equals("article")) {
            from = articleShowMoreIndex;
            limit = 3;
        } else if (type.equals("topic")) {
            from = topicShowMoreIndex + 1;
            limit = 3;
        } else {
            from = 1;
            limit = 3;
        }

        Call<SearchResponse> call = searchArticlesAuthorsAPI.getAllSearchResult(searchName,
                type, from, from + limit);

        call.enqueue(searchArticlesResponseCallback);

    }

    Callback<SearchResponse> searchArticlesResponseCallback = new Callback<SearchResponse>() {
        @Override
        public void onResponse(Call<SearchResponse> call, retrofit2.Response<SearchResponse> response) {
            isReuqestRunning = false;
            if (response == null || response.body() == null) {
                ((SearchAllActivity) getActivity()).showToast("Something went wrong from server");
                return;
            }
            try {
                SearchResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processResponseArticle(responseData);
                    processResponseTopic(responseData);
                    adapter.setListData(data);
                    adapter.notifyDataSetChanged();
                    if (articleShowMoreIndex == 0 && topicShowMoreIndex == 0 && data.size() == 0) {
                        noBlogsTextView.setVisibility(View.VISIBLE);
                    }
                } else {
                    ((SearchAllActivity) getActivity()).showToast(responseData.getReason());
                }
            } catch (Exception e) {
                ((SearchAllActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }

        }

        @Override
        public void onFailure(Call<SearchResponse> call, Throwable t) {
            if (null != getActivity()) {
                ((SearchAllActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
            }
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void processResponseArticle(SearchResponse responseData) {

        ArrayList<SearchArticleResult> articleList = responseData.getData().getResult().getArticle();

        if (articleList != null && articleList.size() > 0) {
            if (data.isEmpty()) {
                SearchArticleTopicResult obj = new SearchArticleTopicResult();
                obj.setTitle(getString(R.string.search_article_label));
                obj.setListType(AppConstants.SEARCH_ITEM_TYPE_ARTICLE_HEADER);
                data.add(obj);
                articleShowMoreIndex = 1;
            } else {
                if (AppConstants.SEARCH_ITEM_TYPE_ARTICLE_SHOW_MORE.equals(data.get(articleShowMoreIndex).getListType())) {
                    data.remove(articleShowMoreIndex);
                }
            }
            for (int i = 0; i < articleList.size(); i++) {
                SearchArticleTopicResult obj = new SearchArticleTopicResult();
                obj.setId(articleList.get(i).getId());
                obj.setTitle(articleList.get(i).getTitle());
                obj.setImage(articleList.get(i).getImage());
                obj.setBody(articleList.get(i).getBody());
                obj.setBlogSlug(articleList.get(i).getBlogSlug());
                obj.setTitleSlug(articleList.get(i).getTitleSlug());
                obj.setUserId(articleList.get(i).getUserId());
                obj.setListType(AppConstants.SEARCH_ITEM_TYPE_ARTICLE);
                data.add(articleShowMoreIndex + i, obj);
            }
            if (articleList.size() == limit) {
                SearchArticleTopicResult obj = new SearchArticleTopicResult();
                obj.setTitle(getString(R.string.search_show_more));
                obj.setListType(AppConstants.SEARCH_ITEM_TYPE_ARTICLE_SHOW_MORE);
                data.add(articleShowMoreIndex + limit, obj);
                articleShowMoreIndex = articleShowMoreIndex + limit;
            }
        }

    }


    private void processResponseTopic(SearchResponse responseData) {

        ArrayList<SearchTopicResult> topicList = responseData.getData().getResult().getTopic();

        if (topicList != null && topicList.size() > 0) {
            if (data.isEmpty() || AppConstants.SEARCH_ITEM_TYPE_ARTICLE_SHOW_MORE.equals(data.get(data.size() - 1).getListType())) {
                SearchArticleTopicResult obj = new SearchArticleTopicResult();
                obj.setTitle(getString(R.string.search_topic_label));
                obj.setListType(AppConstants.SEARCH_ITEM_TYPE_TOPIC_HEADER);
                data.add(obj);
            } else {
                if (AppConstants.SEARCH_ITEM_TYPE_TOPIC_SHOW_MORE.equals(data.get(data.size() - 1).getListType())) {
                    data.remove(data.get(data.size() - 1));
                }
            }
            for (int i = 0; i < topicList.size(); i++) {
                SearchArticleTopicResult obj = new SearchArticleTopicResult();
                obj.setId(topicList.get(i).getId());
                obj.setTitle(topicList.get(i).getTitle());
                obj.setTitle(topicList.get(i).getDisplay_name());
                obj.setListType(AppConstants.SEARCH_ITEM_TYPE_TOPIC);
                data.add(obj);
            }
            if (topicList.size() == limit) {
                SearchArticleTopicResult obj = new SearchArticleTopicResult();
                obj.setTitle(getString(R.string.search_show_more));
                obj.setListType(AppConstants.SEARCH_ITEM_TYPE_TOPIC_SHOW_MORE);
                data.add(obj);
                topicShowMoreIndex = topicShowMoreIndex + limit;
            }
        }
    }

    public void refreshAllArticles(String searchText, String sortType) {
        if (null != data) {
            data.clear();
        }
        if (noBlogsTextView != null)
            noBlogsTextView.setVisibility(View.GONE);
        nextPageNumber = 1;
        isLastPageReached = true;
        searchName = searchText;
        newSearchTopicArticleListingApi(searchName, "all");
    }

    public void resetOnceLoadedFlag(String searchTxt) {
        if (null != data) {
            data.clear();
        }
        isLastPageReached = true;
        searchName = searchTxt;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    @Override
    public void onClick(View view, int position) {
        Log.d("Recylcer Click", "" + position);
        switch (data.get(position).getListType()) {
            case AppConstants.SEARCH_ITEM_TYPE_ARTICLE_HEADER:
                break;
            case AppConstants.SEARCH_ITEM_TYPE_TOPIC_HEADER:
                break;
            case AppConstants.SEARCH_ITEM_TYPE_ARTICLE_SHOW_MORE:
                newSearchTopicArticleListingApi(searchName, "article");
                break;
            case AppConstants.SEARCH_ITEM_TYPE_TOPIC_SHOW_MORE:
                newSearchTopicArticleListingApi(searchName, "topic");
                break;
            case AppConstants.SEARCH_ITEM_TYPE_ARTICLE: {
                Intent intent = new Intent(getActivity(), ArticleDetailsContainerActivity.class);
                SearchArticleTopicResult searchData = data.get(position);
                intent.putExtra(Constants.ARTICLE_ID, searchData.getId());
                intent.putExtra(Constants.AUTHOR_ID, searchData.getUserId());
                intent.putExtra(Constants.BLOG_SLUG, searchData.getBlogSlug());
                intent.putExtra(Constants.TITLE_SLUG, searchData.getTitleSlug());
                intent.putExtra(Constants.ARTICLE_OPENED_FROM, sortType);
                intent.putExtra(Constants.FROM_SCREEN, "Search Screen");
                intent.putExtra(Constants.ARTICLE_INDEX, "" + position);
                startActivity(intent);
            }
            break;
            case AppConstants.SEARCH_ITEM_TYPE_TOPIC: {
                Intent intent = new Intent(getActivity(), FilteredTopicsArticleListingActivity.class);
                SearchArticleTopicResult topic = data.get(position);
                intent.putExtra("selectedTopics", topic.getId());
                intent.putExtra("displayName", topic.getDisplay_name());
                intent.putExtra(Constants.FROM_SCREEN, "Search Screen");
                startActivity(intent);
            }
            break;

        }
    }

    public class SearchArticleTopicResult {
        private String id;
        private String userId;
        private String titleSlug;
        private String image;
        private String title;
        private String body;
        private String blogSlug;
        private String slug;
        private String display_name;
        private String listType;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getTitleSlug() {
            return titleSlug;
        }

        public void setTitleSlug(String titleSlug) {
            this.titleSlug = titleSlug;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public String getBlogSlug() {
            return blogSlug;
        }

        public void setBlogSlug(String blogSlug) {
            this.blogSlug = blogSlug;
        }

        public String getSlug() {
            return slug;
        }

        public void setSlug(String slug) {
            this.slug = slug;
        }

        public String getDisplay_name() {
            return display_name;
        }

        public void setDisplay_name(String display_name) {
            this.display_name = display_name;
        }

        public String getListType() {
            return listType;
        }

        public void setListType(String listType) {
            this.listType = listType;
        }
    }
}
