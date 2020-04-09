package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseFragment;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.editor.EditorPostActivity;
import com.mycity4kids.editor.NewEditor;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.parentingdetails.ImageData;
import com.mycity4kids.models.response.ArticleDetailResult;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.models.response.ShortStoryDetailResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;
import com.mycity4kids.retrofitAPIsInterfaces.ShortStoryAPI;
import com.mycity4kids.ui.activity.AddShortStoryActivity;
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity;
import com.mycity4kids.ui.activity.ShortStoryContainerActivity;
import com.mycity4kids.ui.activity.UserPublishedContentActivity;
import com.mycity4kids.ui.adapter.UserPublishedArticleAdapter;
import com.mycity4kids.ui.adapter.UserPublishedShortStoriesAdapter;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ConnectivityUtils;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.widget.FeedNativeAd;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant.parmar on 21-04-2016.
 */
public class UserPublishedArticleTabFragment extends BaseFragment implements View.OnClickListener,
        UserPublishedArticleAdapter.RecyclerViewClickListener, UserPublishedArticleAdapter.IEditVlog,
        UserPublishedShortStoriesAdapter.IEditShortStory, UserPublishedShortStoriesAdapter.SSRecyclerViewClickListener {

    private static final String EDITOR_TYPE = "editor_type";
    private FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
    private ArrayList<ArticleListingResult> articleDataModelsNew;
    private RecyclerView recyclerView;
    private RelativeLayout mLodingView;
    private TextView noBlogsTextView;

    private UserPublishedArticleAdapter adapter;
    private UserPublishedShortStoriesAdapter shortStoriesAdapter;

    private int nextPageNumber = 0;
    private boolean isReuqestRunning = false;
    private boolean isPrivateProfile;
    private String authorId;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;

    private FeedNativeAd feedNativeAd;
    private String contentType;
    private String userDynamoId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_published_article_tab_fragment, container, false);

        userDynamoId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId();
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mLodingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);
        noBlogsTextView = (TextView) view.findViewById(R.id.noBlogsTextView);

        if (getArguments() != null) {
            authorId = getArguments().getString(Constants.AUTHOR_ID);
            isPrivateProfile = getArguments().getBoolean("isPrivateProfile", false);
            contentType = getArguments().getString("contentType");
        }

        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(llm);
        articleDataModelsNew = new ArrayList<>();

        nextPageNumber = 0;
        if ("shortStory".equals(contentType)) {
            shortStoriesAdapter = new UserPublishedShortStoriesAdapter(getActivity(), this, this, isPrivateProfile
            );
            recyclerView.setAdapter(shortStoriesAdapter);
            getUserPublishedShortStories();
        } else {
            adapter = new UserPublishedArticleAdapter(getActivity(), this, this, isPrivateProfile, feedNativeAd);
            recyclerView.setAdapter(adapter);
            getUserPublishedArticles();
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = llm.getChildCount();
                    totalItemCount = llm.getItemCount();
                    pastVisiblesItems = llm.findFirstVisibleItemPosition();

                    if (!isReuqestRunning) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            isReuqestRunning = true;
                            mLodingView.setVisibility(View.VISIBLE);
                            if ("shortStory".equals(contentType)) {
                                getUserPublishedShortStories();
                            } else {
                                getUserPublishedArticles();
                            }

                        }
                    }
                }
            }
        });
        return view;
    }

    private void getUserPublishedShortStories() {
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            ((UserPublishedContentActivity) getActivity()).showToast(getString(R.string.connectivity_unavailable));
            return;
        }

        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        ShortStoryAPI shortStoryAPI = retro.create(ShortStoryAPI.class);
        int from = 15 * nextPageNumber + 1;
        final Call<ArticleListingResponse> call = shortStoryAPI
                .getAuthorsPublishedStories(authorId, 0, from, from + 14);
        call.enqueue(userPublishedArticleResponseListener);
    }

    private void getUserPublishedArticles() {
        if (isAdded()) {
            if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
                ((UserPublishedContentActivity) getActivity()).showToast(getString(R.string.connectivity_unavailable));
                return;
            }
        }

        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        BloggerDashboardAPI userpublishedArticlesAPI = retro.create(BloggerDashboardAPI.class);
        int from = 15 * nextPageNumber + 1;
        final Call<ArticleListingResponse> call = userpublishedArticlesAPI
                .getAuthorsPublishedArticles(authorId, 0, from, from + 14);
        call.enqueue(userPublishedArticleResponseListener);
    }

    private Callback<ArticleListingResponse> userPublishedArticleResponseListener = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
            removeProgressDialog();
            isReuqestRunning = false;
            mLodingView.setVisibility(View.GONE);
            if (response.body() == null) {
                return;
            }
            try {
                ArticleListingResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    if ("shortStory".equals(contentType)) {
                        processPublishedStoriesResponse(responseData);
                    } else {
                        processPublisedArticlesResponse(responseData);
                    }
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ArticleListingResponse> call, Throwable t) {
            mLodingView.setVisibility(View.GONE);
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void processPublishedStoriesResponse(ArticleListingResponse responseData) {
        ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();
        if (dataList.size() == 0) {
            if (null == articleDataModelsNew || articleDataModelsNew.isEmpty()) {
                // No results
                articleDataModelsNew.addAll(dataList);
                shortStoriesAdapter.setListData(articleDataModelsNew);
                shortStoriesAdapter.notifyDataSetChanged();
                if (isAdded()) {
                    noBlogsTextView.setText(getString(R.string.short_s_no_published));
                }
                noBlogsTextView.setVisibility(View.VISIBLE);
            }
        } else {
            if (nextPageNumber == 1) {
                articleDataModelsNew.addAll(dataList);
                shortStoriesAdapter.setListData(articleDataModelsNew);
                shortStoriesAdapter.notifyDataSetChanged();
            } else {
                articleDataModelsNew.addAll(dataList);
            }
            shortStoriesAdapter.setListData(articleDataModelsNew);
            nextPageNumber = nextPageNumber + 1;
            shortStoriesAdapter.notifyDataSetChanged();
        }
    }

    private void processPublisedArticlesResponse(ArticleListingResponse responseData) {
        ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();
        if (dataList.size() == 0) {
            if (null == articleDataModelsNew || articleDataModelsNew.isEmpty()) {
                // No results
                articleDataModelsNew.addAll(dataList);
                adapter.setListData(articleDataModelsNew);
                adapter.notifyDataSetChanged();
                noBlogsTextView.setVisibility(View.VISIBLE);
            }

        } else {
            if (nextPageNumber == 1) {
                articleDataModelsNew.addAll(dataList);
                adapter.setListData(articleDataModelsNew);
                adapter.notifyDataSetChanged();
            } else {
                articleDataModelsNew.addAll(dataList);
            }
            adapter.setListData(articleDataModelsNew);
            nextPageNumber = nextPageNumber + 1;
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onClick(View view, int position) {
        switch (view.getId()) {
            case R.id.rootLayout:
                Intent intent = new Intent(getActivity(), ArticleDetailsContainerActivity.class);
                intent.putExtra(Constants.ARTICLE_ID, articleDataModelsNew.get(position).getId());
                intent.putExtra(Constants.AUTHOR_ID, articleDataModelsNew.get(position).getUserId());
                intent.putExtra(Constants.BLOG_SLUG, articleDataModelsNew.get(position).getBlogPageSlug());
                intent.putExtra(Constants.TITLE_SLUG, articleDataModelsNew.get(position).getTitleSlug());
                if (authorId.equals(userDynamoId)) {
                    intent.putExtra(Constants.ARTICLE_OPENED_FROM, "PrivatePublishedArticles");
                    intent.putExtra(Constants.FROM_SCREEN, "PrivateUserArticlesScreen");
                } else {
                    intent.putExtra(Constants.ARTICLE_OPENED_FROM, "PublicPublishedArticles");
                    intent.putExtra(Constants.FROM_SCREEN, "PublicUserArticlesScreen");
                }
                intent.putExtra(Constants.ARTICLE_INDEX, "" + position);
                intent.putParcelableArrayListExtra("pagerListData", articleDataModelsNew);
                intent.putExtra(Constants.AUTHOR,
                        articleDataModelsNew.get(position).getUserId() + "~" + articleDataModelsNew.get(position)
                                .getUserName());
                startActivity(intent);
                break;
            case R.id.editPublishedTextView:
                Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                ArticleDetailsAPI articleDetailsAPI = retrofit.create(ArticleDetailsAPI.class);
                Call<ArticleDetailResult> call = articleDetailsAPI
                        .getArticleDetailsFromRedis(articleDataModelsNew.get(position).getId(), "articleId");
                call.enqueue(articleDetailResponseCallback);
                break;
            case R.id.shareArticleImageView:
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareUrl = AppUtils.getShareUrl(articleDataModelsNew.get(position).getUserType(),
                        articleDataModelsNew.get(position).getBlogPageSlug(),
                        articleDataModelsNew.get(position).getTitleSlug());
                String shareMessage;
                if (StringUtils.isNullOrEmpty(shareUrl)) {
                    shareMessage = getString(R.string.check_out_blog) + "\"" +
                            articleDataModelsNew.get(position).getTitle() + "\" by " + articleDataModelsNew
                            .get(position).getUserName() + ".";
                } else {
                    shareMessage = getString(R.string.check_out_blog) + "\"" +
                            articleDataModelsNew.get(position).getTitle() + "\" by " + articleDataModelsNew
                            .get(position).getUserName() + ".\nRead Here: " + shareUrl;
                }
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "Momspresso"));
                if (isPrivateProfile) {
                    Utils.pushShareArticleEvent(getActivity(), "PrivateUserArticlesScreen", userDynamoId + "",
                            articleDataModelsNew.get(position).getId(),
                            authorId + "~" + articleDataModelsNew.get(position).getUserName(), "-");
                } else {
                    Utils.pushShareArticleEvent(getActivity(), "PublicUserArticlesScreen", userDynamoId + "",
                            articleDataModelsNew.get(position).getId(),
                            authorId + "~" + articleDataModelsNew.get(position).getUserName(), "-");
                }
                break;
            default:
                break;
        }
    }

    Callback<ArticleDetailResult> articleDetailResponseCallback = new Callback<ArticleDetailResult>() {
        @Override
        public void onResponse(Call<ArticleDetailResult> call, retrofit2.Response<ArticleDetailResult> response) {
            removeProgressDialog();
            if (response.body() == null) {
                return;
            }
            try {
                ArticleDetailResult responseData = response.body();
                getResponseUpdateUi(responseData);
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ArticleDetailResult> call, Throwable t) {
            removeProgressDialog();
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void getResponseUpdateUi(ArticleDetailResult detailsResponse) {
        ArticleDetailResult detailData = detailsResponse;
        ArrayList<ImageData> imageList = detailData.getBody().getImage();

        String bodyDescription = detailData.getBody().getText();
        String bodyDesc = bodyDescription;
        String content;
        if (imageList.size() > 0) {
            for (ImageData images : imageList) {
                if (bodyDescription.contains(images.getKey())) {
                    bodyDesc = bodyDesc.replace(images.getKey(),
                            "<p style='text-align:center'><img src=" + images.getValue()
                                    + " style=\"width: 100%;\"+></p>");
                }
            }

            String bodyImgTxt = "<html><head></head><body>" + bodyDesc + "</body></html>";
            content = bodyImgTxt;

        } else {
            String bodyImgTxt = "<html><head></head><body>" + bodyDesc + "</body></html>";
            content = bodyImgTxt;
        }

        String editorType = firebaseRemoteConfig.getString(EDITOR_TYPE);
        Intent intent;
        if ((!StringUtils.isNullOrEmpty(editorType) && "1".equals(editorType)) || AppUtils
                .isUserBucketedInNewEditor(firebaseRemoteConfig)) {
            intent = new Intent(getActivity(), NewEditor.class);
        } else {
            intent = new Intent(getActivity(), EditorPostActivity.class);
        }
        intent.putExtra("from", "publishedList");
        intent.putExtra("title", detailData.getTitle());
        intent.putExtra("content", content);
        intent.putExtra("thumbnailUrl", detailData.getImageUrl().getThumbMax());
        intent.putExtra("articleId", detailData.getId());
        intent.putExtra("tag", new Gson().toJson(detailData.getTags()));
        intent.putExtra("cities", new Gson().toJson(detailData.getCities()));
        startActivity(intent);
    }

    @Override
    public void onShortStoryClick(View view, int position) {
        switch (view.getId()) {
            case R.id.rootLayout:
                Intent intent = new Intent(getActivity(), ShortStoryContainerActivity.class);
                intent.putExtra(Constants.ARTICLE_ID, articleDataModelsNew.get(position).getId());
                intent.putExtra(Constants.AUTHOR_ID, articleDataModelsNew.get(position).getUserId());
                intent.putExtra(Constants.BLOG_SLUG, articleDataModelsNew.get(position).getBlogPageSlug());
                intent.putExtra(Constants.TITLE_SLUG, articleDataModelsNew.get(position).getTitleSlug());
                if (authorId.equals(userDynamoId)) {
                    intent.putExtra(Constants.ARTICLE_OPENED_FROM, "PrivatePublishedArticles");
                    intent.putExtra(Constants.FROM_SCREEN, "PrivateUserArticlesScreen");
                } else {
                    intent.putExtra(Constants.ARTICLE_OPENED_FROM, "PublicPublishedArticles");
                    intent.putExtra(Constants.FROM_SCREEN, "PublicUserArticlesScreen");
                }
                intent.putExtra(Constants.ARTICLE_INDEX, "" + position);
                intent.putParcelableArrayListExtra("pagerListData", articleDataModelsNew);
                intent.putExtra(Constants.AUTHOR,
                        articleDataModelsNew.get(position).getUserId() + "~" + articleDataModelsNew.get(position)
                                .getUserName());
                startActivity(intent);
                break;
            case R.id.editPublishedTextView:
                Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                ShortStoryAPI shortStoryAPI = retrofit.create(ShortStoryAPI.class);
                Call<ShortStoryDetailResult> call = shortStoryAPI
                        .getShortStoryDetails(articleDataModelsNew.get(position).getId(), "articleId");
                call.enqueue(ssDetailResponseCallbackRedis);
                break;
            case R.id.shareArticleImageView:
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");

                String shareUrl = AppUtils.getShortStoryShareUrl(articleDataModelsNew.get(position).getUserType(),
                        articleDataModelsNew.get(position).getBlogPageSlug(),
                        articleDataModelsNew.get(position).getTitleSlug());
                String shareMessage;
                if (StringUtils.isNullOrEmpty(shareUrl)) {
                    shareMessage = getString(R.string.check_out_short_story) + "\"" +
                            articleDataModelsNew.get(position).getTitle() + "\" by " + articleDataModelsNew
                            .get(position).getUserName() + ".";
                } else {
                    shareMessage = getString(R.string.check_out_short_story) + "\"" +
                            articleDataModelsNew.get(position).getTitle() + "\" by " + articleDataModelsNew
                            .get(position).getUserName() + ".\nRead Here: " + shareUrl;
                }
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "Momspresso"));
                if (isPrivateProfile) {
                    Utils.pushShareArticleEvent(getActivity(), "PrivateUserArticlesScreen", userDynamoId + "",
                            articleDataModelsNew.get(position).getId(),
                            authorId + "~" + articleDataModelsNew.get(position).getUserName(), "-");
                } else {
                    Utils.pushShareArticleEvent(getActivity(), "PublicUserArticlesScreen", userDynamoId + "",
                            articleDataModelsNew.get(position).getId(),
                            authorId + "~" + articleDataModelsNew.get(position).getUserName(), "-");
                }
                break;
        }
    }

    Callback<ShortStoryDetailResult> ssDetailResponseCallbackRedis = new Callback<ShortStoryDetailResult>() {
        @Override
        public void onResponse(Call<ShortStoryDetailResult> call, retrofit2.Response<ShortStoryDetailResult> response) {
            removeProgressDialog();
            if (response.body() == null) {
                return;
            }
            try {
                ShortStoryDetailResult responseData = response.body();
                Intent intent = new Intent(getActivity(), AddShortStoryActivity.class);
                intent.putExtra("from", "publishedList");
                intent.putExtra("title", responseData.getTitle());
                intent.putExtra("body", responseData.getBody());
                intent.putExtra("articleId", responseData.getId());
                intent.putExtra("tag", new Gson().toJson(responseData.getTags()));
                intent.putExtra("cities", new Gson().toJson(responseData.getCities()));
                startActivity(intent);
            } catch (Exception e) {
                removeProgressDialog();
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }

        }

        @Override
        public void onFailure(Call<ShortStoryDetailResult> call, Throwable t) {
            removeProgressDialog();
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void chooseImageOptionPopUp(View view, int position) {
        final PopupMenu popup = new PopupMenu(getActivity(), view);
        popup.getMenuInflater().inflate(R.menu.edit_vlog_details_menu, popup.getMenu());
        Typeface myTypeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/" + "oswald_regular.ttf");

        for (int i = 0; i < popup.getMenu().size(); i++) {
            MenuItem menuItem = popup.getMenu().getItem(i);
            SpannableString spannableString = new SpannableString(
                    view.getContext().getString(R.string.user_article_published_edit));
            spannableString.setSpan(new CustomTypeFace("", myTypeface), 0, spannableString.length(),
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            menuItem.setTitle(spannableString);
        }

        popup.getMenu().findItem(R.id.disable_comment).setVisible(false);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int i = item.getItemId();
                if (i == R.id.edit_vlog) {
                    if ("shortStory".equals(contentType)) {
                        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                        ShortStoryAPI shortStoryAPI = retrofit.create(ShortStoryAPI.class);
                        Call<ShortStoryDetailResult> call = shortStoryAPI
                                .getShortStoryDetails(articleDataModelsNew.get(position).getId(), "articleId");
                        call.enqueue(ssDetailResponseCallbackRedis);
                    } else {
                        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                        ArticleDetailsAPI articleDetailsAPI = retrofit.create(ArticleDetailsAPI.class);
                        Call<ArticleDetailResult> call = articleDetailsAPI
                                .getArticleDetailsFromRedis(articleDataModelsNew.get(position).getId(), "articleId");
                        call.enqueue(articleDetailResponseCallback);
                    }
                    return true;
                } else {
                    return true;
                }
            }

        });

        MenuPopupHelper menuHelper = new MenuPopupHelper(getContext(), (MenuBuilder) popup.getMenu(), view);
        menuHelper.setForceShowIcon(true);
        menuHelper.show();
    }

    @Override
    public void onBlogEdit(int position, ImageView imageView) {
        chooseImageOptionPopUp(imageView, position);

    }

    @Override
    public void onStoryEdit(int position, ImageView imageView) {
        chooseImageOptionPopUp(imageView, position);
    }

    private class CustomTypeFace extends TypefaceSpan {

        private final Typeface typeface;

        public CustomTypeFace(String family, Typeface type) {
            super(family);
            typeface = type;
        }

        @Override
        public void updateDrawState(TextPaint textPaint) {
            applyCustomTypeFace(textPaint, typeface);
        }

        @Override
        public void updateMeasureState(TextPaint textPaint) {
            applyCustomTypeFace(textPaint, typeface);
        }

        private void applyCustomTypeFace(Paint paint, Typeface typeface) {
            int oldStyle;
            Typeface old = paint.getTypeface();
            if (old == null) {
                oldStyle = 0;
            } else {
                oldStyle = old.getStyle();
            }

            int fake = oldStyle & ~typeface.getStyle();
            if ((fake & Typeface.BOLD) != 0) {
                paint.setFakeBoldText(true);
            }

            if ((fake & Typeface.ITALIC) != 0) {
                paint.setTextSkewX(-0.25f);
            }
            paint.setTypeface(typeface);
        }
    }
}
