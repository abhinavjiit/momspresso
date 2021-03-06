package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseFragment;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.response.SearchArticleResult;
import com.mycity4kids.models.response.SearchResponse;
import com.mycity4kids.models.response.SearchTopicResult;
import com.mycity4kids.retrofitAPIsInterfaces.SearchArticlesAuthorsAPI;
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity;
import com.mycity4kids.ui.activity.FilteredTopicsArticleListingActivity;
import com.mycity4kids.ui.activity.SearchAllActivity;
import com.mycity4kids.ui.activity.ShortStoryContainerActivity;
import com.mycity4kids.ui.adapter.SearchAllArticlesTopicsListingAdapter;
import com.mycity4kids.utils.ConnectivityUtils;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.widget.NpaLinearLayoutManager;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant.parmar on 21-04-2016.
 */
public class SearchAllArticlesAndTopicsTabFragment extends BaseFragment implements View.OnClickListener,
        SearchAllArticlesTopicsListingAdapter.RecyclerViewClickListener {

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
    private boolean isShowMoreTopicRequest = false;
    private boolean isShowMoreArticleRequest = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_all_article_topic_tab_fragment, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        noBlogsTextView = (TextView) view.findViewById(R.id.noBlogsTextView);

        adapter = new SearchAllArticlesTopicsListingAdapter(getActivity(), this);
        NpaLinearLayoutManager llm = new NpaLinearLayoutManager(getActivity());
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

    private void newSearchTopicArticleListingApi(String searchName, String type) {
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            if (isAdded()) {
                ((SearchAllActivity) getActivity()).showToast(getString(R.string.connectivity_unavailable));
            }
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
                if (isAdded()) {
                    ((SearchAllActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
                }
                return;
            }
            try {
                if (!isShowMoreTopicRequest && !isShowMoreArticleRequest) {
                    if (data != null) {
                        data.clear();
                    }
                }
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
                if (isAdded()) {
                    ((SearchAllActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
                }
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }

        }

        @Override
        public void onFailure(Call<SearchResponse> call, Throwable t) {
            if (null != getActivity()) {
                ((SearchAllActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
            }
            isShowMoreArticleRequest = false;
            isShowMoreTopicRequest = false;
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void processResponseArticle(SearchResponse responseData) {

        ArrayList<SearchArticleResult> articleList = responseData.getData().getResult().getArticle();

//        if (isShowMoreArticleRequest) {
//            if (articleList == null || articleList.isEmpty()) {
//                data.remove(articleShowMoreIndex);
//            }
//        }
        if (articleList != null && articleList.size() >= 0) {
            if (data.isEmpty()) {
                Log.d("FastSearch", "data empty");
                SearchArticleTopicResult obj = new SearchArticleTopicResult();
                obj.setTitle(getString(R.string.search_article_label));
                obj.setListType(AppConstants.SEARCH_ITEM_TYPE_ARTICLE_HEADER);
                data.add(obj);
                articleShowMoreIndex = 1;
            } else {
                if (AppConstants.SEARCH_ITEM_TYPE_ARTICLE_SHOW_MORE
                        .equals(data.get(articleShowMoreIndex).getListType())) {
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
                obj.setContentType(articleList.get(i).getContentType());
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
        Log.d("FastSearch", "data added");

    }


    private void processResponseTopic(SearchResponse responseData) {

        ArrayList<SearchTopicResult> topicList = responseData.getData().getResult().getTopic();

        if (topicList != null && topicList.size() > 0) {
            if (data.isEmpty() || AppConstants.SEARCH_ITEM_TYPE_ARTICLE_SHOW_MORE
                    .equals(data.get(data.size() - 1).getListType())) {
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
            Log.d("FastSearch", "data cleared");
            data.clear();
        }
        if (noBlogsTextView != null) {
            noBlogsTextView.setVisibility(View.GONE);
        }
        nextPageNumber = 1;
        isShowMoreArticleRequest = false;
        isShowMoreTopicRequest = false;
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
        try {
            switch (data.get(position).getListType()) {
                case AppConstants.SEARCH_ITEM_TYPE_ARTICLE_HEADER:
                    break;
                case AppConstants.SEARCH_ITEM_TYPE_TOPIC_HEADER:
                    break;
                case AppConstants.SEARCH_ITEM_TYPE_ARTICLE_SHOW_MORE:
                    isShowMoreArticleRequest = true;
                    newSearchTopicArticleListingApi(searchName, "article");
                    break;
                case AppConstants.SEARCH_ITEM_TYPE_TOPIC_SHOW_MORE:
                    isShowMoreTopicRequest = true;
                    newSearchTopicArticleListingApi(searchName, "topic");
                    break;
                case AppConstants.SEARCH_ITEM_TYPE_ARTICLE: {
                    SearchArticleTopicResult searchData = data.get(position);
                    if ("1".equals(searchData.getContentType())) {
                        Intent intent = new Intent(getActivity(), ShortStoryContainerActivity.class);
                        intent.putExtra(Constants.ARTICLE_ID, searchData.getId());
                        intent.putExtra(Constants.AUTHOR_ID, searchData.getUserId());
                        intent.putExtra(Constants.BLOG_SLUG, searchData.getBlogSlug());
                        intent.putExtra(Constants.TITLE_SLUG, searchData.getTitleSlug());
                        intent.putExtra(Constants.ARTICLE_OPENED_FROM, "SearchScreen");
                        intent.putExtra(Constants.FROM_SCREEN, "SearchScreen");
                        intent.putExtra(Constants.ARTICLE_INDEX, "" + position);
                        intent.putExtra(Constants.AUTHOR, searchData.getUserId() + "~");
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getActivity(), ArticleDetailsContainerActivity.class);
                        intent.putExtra(Constants.ARTICLE_ID, searchData.getId());
                        intent.putExtra(Constants.AUTHOR_ID, searchData.getUserId());
                        intent.putExtra(Constants.BLOG_SLUG, searchData.getBlogSlug());
                        intent.putExtra(Constants.TITLE_SLUG, searchData.getTitleSlug());
                        intent.putExtra(Constants.ARTICLE_OPENED_FROM, "SearchScreen");
                        intent.putExtra(Constants.FROM_SCREEN, "SearchScreen");
                        intent.putExtra(Constants.ARTICLE_INDEX, "" + position);
                        intent.putExtra(Constants.AUTHOR, searchData.getUserId() + "~");
                        startActivity(intent);
                    }
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
        } catch (Exception t) {
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
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
        private String contentType;

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

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }
    }
}
