package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.hb.views.PinnedSectionListView;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.editor.DraftListAdapter;
import com.mycity4kids.editor.EditorPostActivity;
import com.mycity4kids.enums.ArticleType;
import com.mycity4kids.models.editor.ArticleDraftRequest;
import com.mycity4kids.models.response.ArticleDraftResponse;
import com.mycity4kids.models.response.DraftListResponse;
import com.mycity4kids.models.response.PublishDraftObject;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDraftAPI;
import com.mycity4kids.ui.adapter.CommentsListAdapter;
import com.mycity4kids.ui.adapter.PublishedArticleListAdapter;
import com.mycity4kids.ui.adapter.ReviewsListAdapter;

import java.io.File;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 16/3/16.
 */
public class BloggerDashboardActivity extends BaseActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    @Override
    protected void updateUi(Response response) {

    }

    private static final int LIST_TYPE_DRAFT = 0;
    private static final int LIST_TYPE_PUBLISHED = 1;
    private static final int LIST_TYPE_COMMENTS = 2;
    private static final int LIST_TYPE_REVIEWS = 3;

    private Toolbar mToolbar;
    private TextView bloggerNameTextView, rankingTextView, viewCountTextView, followersViewCount;
    private ImageView bloggerImageView;
    View vSeparator1, vSeparator2;
    TabLayout tabLayout;
    ViewPager viewPager;
    ImageView addDraft;
    public static final int ADD_MEDIA_ACTIVITY_REQUEST_CODE = 1111;
    Uri imageUri;
    Bitmap finalBitmap;
    File file;
    PinnedSectionListView draftListview, publishedArticleListView, bookmarksListView, reviewsCommentsListView;
    ArrayList<PublishDraftObject> draftList;
    int position;
    TextView noDrafts;
    DraftListAdapter adapter;
    PublishedArticleListAdapter publishedArticleListAdapter;
    CommentsListAdapter commentsListAdapter;
    ReviewsListAdapter reviewsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blogger_dashboard);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        draftListview = (PinnedSectionListView) findViewById(R.id.draftListView);
        publishedArticleListView = (PinnedSectionListView) findViewById(R.id.publishedArticleListView);
        bookmarksListView = (PinnedSectionListView) findViewById(R.id.bookmarkedListView);
        reviewsCommentsListView = (PinnedSectionListView) findViewById(R.id.reviewCommentsListView);

        noDrafts = (TextView) findViewById(R.id.noDraftsTextView);
        View header = getLayoutInflater().inflate(R.layout.header_blogger_dashboard, null);
        draftListview.addHeaderView(header);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hitDraftListingApi();
        hitPublishedArticleApi();
        hitBookmarkedArticlesApi();
        hitReviewCommentsApi();

        draftListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                if (position == 0) {
                    // Header Item

                } else if (position == 1) {
                    // Pinned Section Item
                    ((LinearLayout) findViewById(R.id.publishedItemLinearlayout)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onSectionHeaderItemClick(LIST_TYPE_PUBLISHED);
                        }
                    });
                    ((LinearLayout) findViewById(R.id.commentsItemLinearlayout)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onSectionHeaderItemClick(LIST_TYPE_COMMENTS);
                        }
                    });
                    ((LinearLayout) findViewById(R.id.reviewItemLinearLayout)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onSectionHeaderItemClick(LIST_TYPE_REVIEWS);
                        }
                    });
                } else {
                    // List view items
                    if (Build.VERSION.SDK_INT > 15) {
                        Intent intent = new Intent(BloggerDashboardActivity.this, EditorPostActivity.class);
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
            }
        });
    }

    private void onSectionHeaderItemClick(int listType) {

        if (listType == LIST_TYPE_DRAFT) {

        } else if (listType == LIST_TYPE_PUBLISHED) {

        } else if (listType == LIST_TYPE_COMMENTS) {

        } else if (listType == LIST_TYPE_COMMENTS) {

        }
    }

    private void hitReviewCommentsApi() {
        showProgressDialog(getResources().getString(R.string.please_wait));
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        // prepare call in Retrofit 2.0
        ArticleDraftAPI getDraftListAPI = retrofit.create(ArticleDraftAPI.class);
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog();
            showToast(getString(R.string.error_network));
            return;
        }

        Call<DraftListResponse> call = getDraftListAPI.getDraftsList(ArticleType.DRAFT.getValue() + "'" + ArticleType.UNDER_MODERATION.getValue()
                + "'" + ArticleType.UNAPPROVED.getValue() + "'" + ArticleType.UNPUBLISHED.getValue());
//asynchronous call
        call.enqueue(new Callback<DraftListResponse>() {
                         @Override
                         public void onResponse(Call<DraftListResponse> call, retrofit2.Response<DraftListResponse> response) {
                             int statusCode = response.code();
                             removeProgressDialog();

                             DraftListResponse responseModel = response.body();
                             if (responseModel.getCode() != 200) {
                                 showToast(getString(R.string.toast_response_error));
                                 return;
                             } else {
                                 if (!StringUtils.isNullOrEmpty(responseModel.getData().getMsg())) {
                                     Log.i("Draft message", responseModel.getData().getMsg());
                                 }
                                 processReviewsResponse(responseModel);

                             }
                         }

                         @Override
                         public void onFailure(Call<DraftListResponse> call, Throwable t) {
                             removeProgressDialog();

                         }
                     }
        );
    }

    private void hitBookmarkedArticlesApi() {
        showProgressDialog(getResources().getString(R.string.please_wait));
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        // prepare call in Retrofit 2.0
        ArticleDraftAPI getDraftListAPI = retrofit.create(ArticleDraftAPI.class);
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog();
            showToast(getString(R.string.error_network));
            return;
        }

        Call<DraftListResponse> call = getDraftListAPI.getDraftsList(ArticleType.DRAFT.getValue() + "'" + ArticleType.UNDER_MODERATION.getValue()
                + "'" + ArticleType.UNAPPROVED.getValue() + "'" + ArticleType.UNPUBLISHED.getValue());
//asynchronous call
        call.enqueue(new Callback<DraftListResponse>() {
                         @Override
                         public void onResponse(Call<DraftListResponse> call, retrofit2.Response<DraftListResponse> response) {
                             int statusCode = response.code();
                             removeProgressDialog();

                             DraftListResponse responseModel = response.body();
                             if (responseModel.getCode() != 200) {
                                 showToast(getString(R.string.toast_response_error));
                                 return;
                             } else {
                                 if (!StringUtils.isNullOrEmpty(responseModel.getData().getMsg())) {
                                     Log.i("Draft message", responseModel.getData().getMsg());
                                 }
                                 processCommentsResponse(responseModel);

                             }
                         }

                         @Override
                         public void onFailure(Call<DraftListResponse> call, Throwable t) {
                             removeProgressDialog();

                         }
                     }
        );
    }

    private void hitPublishedArticleApi() {
        showProgressDialog(getResources().getString(R.string.please_wait));
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        // prepare call in Retrofit 2.0
        ArticleDraftAPI getDraftListAPI = retrofit.create(ArticleDraftAPI.class);
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog();
            showToast(getString(R.string.error_network));
            return;
        }

        Call<DraftListResponse> call = getDraftListAPI.getDraftsList(ArticleType.DRAFT.getValue() + "'" + ArticleType.UNDER_MODERATION.getValue()
                + "'" + ArticleType.UNAPPROVED.getValue() + "'" + ArticleType.UNPUBLISHED.getValue());
//asynchronous call
        call.enqueue(new Callback<DraftListResponse>() {
                         @Override
                         public void onResponse(Call<DraftListResponse> call, retrofit2.Response<DraftListResponse> response) {
                             int statusCode = response.code();
                             removeProgressDialog();

                             DraftListResponse responseModel = response.body();
                             if (responseModel.getCode() != 200) {
                                 showToast(getString(R.string.toast_response_error));
                                 return;
                             } else {
                                 if (!StringUtils.isNullOrEmpty(responseModel.getData().getMsg())) {
                                     Log.i("Draft message", responseModel.getData().getMsg());
                                 }
                                 processPublisedArticlesResponse(responseModel);

                             }
                         }

                         @Override
                         public void onFailure(Call<DraftListResponse> call, Throwable t) {
                             removeProgressDialog();

                         }
                     }
        );
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

        Call<DraftListResponse> call = getDraftListAPI.getDraftsList(ArticleType.DRAFT.getValue() + "'" + ArticleType.UNDER_MODERATION.getValue() + "'" + ArticleType.UNAPPROVED.getValue() + "'" + ArticleType.UNPUBLISHED.getValue());

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
                             DraftListResponse responseModel = response.body();
                             if (responseModel.getCode() != 200) {
                                 showToast(getString(R.string.toast_response_error));
                                 return;
                             } else {
                                 if (!StringUtils.isNullOrEmpty(responseModel.getData().getMsg())) {
                                     Log.i("Draft message", responseModel.getData().getMsg());
                                 }

                                 processDraftResponse(responseModel);

                             }
                         }


                         @Override
                         public void onFailure(Call<DraftListResponse> call, Throwable t) {
                             removeProgressDialog();

                         }
                     }
        );

    }

    public void deleteDraftAPI(PublishDraftObject draftObject, int p) {
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

        Call<ArticleDraftResponse> call = articleDraftAPI.deleteDraft(
                AppConstants.LIVE_URL + "v1/articles/" + draftObject.getId());


        //asynchronous call
        call.enqueue(new Callback<ArticleDraftResponse>() {
                         @Override
                         public void onResponse(Call<ArticleDraftResponse> call, retrofit2.Response<ArticleDraftResponse> response) {
                             int statusCode = response.code();

                             ArticleDraftResponse responseModel = (ArticleDraftResponse) response.body();

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
                         public void onFailure(Call<ArticleDraftResponse> call, Throwable t) {

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
            ArrayList<PublishDraftObject> draftListNew = new ArrayList<PublishDraftObject>();

            PublishDraftObject draftObject = new PublishDraftObject();
            draftObject.setItemType(0);
            draftListNew.add(0, draftObject);
            draftListNew.addAll(draftList);
            noDrafts.setVisibility(View.GONE);
            adapter = new DraftListAdapter(this, draftListNew);
            draftListview.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

    }

    private void processPublisedArticlesResponse(DraftListResponse responseModel) {
        draftList = responseModel.getData().getResult();

        if (draftList.size() == 0) {
            noDrafts.setVisibility(View.VISIBLE);
        } else {
            ArrayList<PublishDraftObject> draftListNew = new ArrayList<PublishDraftObject>();

            PublishDraftObject draftObject = new PublishDraftObject();
            draftObject.setItemType(0);
            draftListNew.add(0, draftObject);
            draftListNew.addAll(draftList);
            noDrafts.setVisibility(View.GONE);
            publishedArticleListAdapter = new PublishedArticleListAdapter(this, draftListNew);
            publishedArticleListView.setAdapter(publishedArticleListAdapter);
            publishedArticleListAdapter.notifyDataSetChanged();
        }

    }

    private void processCommentsResponse(DraftListResponse responseModel) {
        draftList = responseModel.getData().getResult();

        if (draftList.size() == 0) {
            noDrafts.setVisibility(View.VISIBLE);
        } else {
            ArrayList<PublishDraftObject> draftListNew = new ArrayList<PublishDraftObject>();

            PublishDraftObject draftObject = new PublishDraftObject();
            draftObject.setItemType(0);
            draftListNew.add(0, draftObject);
            draftListNew.addAll(draftList);
            noDrafts.setVisibility(View.GONE);
            commentsListAdapter = new CommentsListAdapter(this, draftListNew);
            bookmarksListView.setAdapter(commentsListAdapter);
            commentsListAdapter.notifyDataSetChanged();
        }

    }

    private void processReviewsResponse(DraftListResponse responseModel) {
        draftList = responseModel.getData().getResult();

        if (draftList.size() == 0) {
            noDrafts.setVisibility(View.VISIBLE);
        } else {
            ArrayList<PublishDraftObject> draftListNew = new ArrayList<PublishDraftObject>();

            PublishDraftObject draftObject = new PublishDraftObject();
            draftObject.setItemType(0);
            draftListNew.add(0, draftObject);
            draftListNew.addAll(draftList);
            noDrafts.setVisibility(View.GONE);
            reviewsListAdapter = new ReviewsListAdapter(this, draftListNew);
            reviewsCommentsListView.setAdapter(reviewsListAdapter);
            reviewsListAdapter.notifyDataSetChanged();
        }

    }
    /*
        Utils.pushOpenScreenEvent(BloggerDashboardActivity.this, "Blogger Dashboard", SharedPrefUtils.getUserDetailModel(this).getId() + "");
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        rankingTextView = (TextView) findViewById(R.id.rankingTextView);
        viewCountTextView = (TextView) findViewById(R.id.viewCountTextView);
        followersViewCount = (TextView) findViewById(R.id.followersViewCount);
        vSeparator1 = (View) findViewById(R.id.vSeparator1);
        vSeparator2 = (View) findViewById(R.id.vSeparator2);
        addDraft = (ImageView) findViewById(R.id.addDraft);

        bloggerNameTextView = (TextView) findViewById(R.id.bloggerNameTextView);
        bloggerNameTextView.setText(SharedPrefUtils.getUserDetailModel(this).getFirst_name());

        bloggerImageView = (ImageView) findViewById(R.id.bloggerImageView);
        if (!StringUtils.isNullOrEmpty(SharedPrefUtils.getProfileImgUrl(this))) {
            Picasso.with(this).load(SharedPrefUtils.getProfileImgUrl(this)).placeholder(R.drawable.family_xxhdpi)
                    .error(R.drawable.family_xxhdpi).transform(new RoundedTransformation()).into(bloggerImageView);
        }
        bloggerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image*//*");
                startActivityForResult(intent, ADD_MEDIA_ACTIVITY_REQUEST_CODE);
            }
        });
        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.addTab(tabLayout.newTab().setText("Bookmarks (0)"));
        tabLayout.addTab(tabLayout.newTab().setText("Published (0)"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");
        addDraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT > 15) {
                    Intent intent1 = new Intent(BloggerDashboardActivity.this, EditorPostActivity.class);
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
    protected void onRestart() {
        super.onRestart();
    }

    private void getBloggerDashboardDetails() {

        showProgressDialog("please wait ...");
       *//* BloggerDashboardAndPublishedArticlesController _controller = new BloggerDashboardAndPublishedArticlesController(this, this);
        _controller.getData(AppConstants.GET_BLOGGER_DASHBOARD_REQUEST, 0);*//*
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        // prepare call in Retrofit 2.0
        BloggerDashboardAPI bloggerDashboardAPI = retrofit.create(BloggerDashboardAPI.class);
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog();
            showToast(getString(R.string.error_network));
            return;
        }
        Call<BloggerDashboardModel> call = bloggerDashboardAPI.getBloggerData("" + SharedPrefUtils.getUserDetailModel(BloggerDashboardActivity.this).getId());


        //asynchronous call
        call.enqueue(new Callback<BloggerDashboardModel>() {
                         @Override
                         public void onResponse(Call<BloggerDashboardModel> call, retrofit2.Response<BloggerDashboardModel> response) {
                             int statusCode = response.code();

                             BloggerDashboardModel responseData = (BloggerDashboardModel) response.body();

                             removeProgressDialog();

                             try {
                                 if (true) {
                                     removeProgressDialog();

//                                     if (responseData.getResult().getData().getRank() == 0) {
//                                         rankingTextView.setVisibility(View.INVISIBLE);
//                                         viewCountTextView.setVisibility(View.INVISIBLE);
//                                         followersViewCount.setVisibility(View.INVISIBLE);
//                                         vSeparator1.setVisibility(View.INVISIBLE);
//                                         vSeparator2.setVisibility(View.INVISIBLE);
//                                     } else {
//                                         rankingTextView.setVisibility(View.VISIBLE);
//                                         viewCountTextView.setVisibility(View.VISIBLE);
//                                         followersViewCount.setVisibility(View.VISIBLE);
//                                         vSeparator1.setVisibility(View.VISIBLE);
//                                         vSeparator2.setVisibility(View.VISIBLE);
//                                         rankingTextView.setText("" + responseData.getResult().getData().getRank());
//                                         viewCountTextView.setText("" + responseData.getResult().getData().getViews());
//                                         followersViewCount.setText("" + responseData.getResult().getData().getFollowers());
//                                     }

                                     tabLayout.getTabAt(0).setText("Bookmarks (" + 1 + ")");
                                     tabLayout.getTabAt(1).setText("Published (" + 11 + ")");

                                     final BloggerDashboardPagerAdapter adapter = new BloggerDashboardPagerAdapter
                                             (getSupportFragmentManager(), BloggerDashboardActivity.this, 11,
                                                     22);
                                     viewPager.setAdapter(adapter);
                                     viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                                     tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                                         @Override
                                         public void onTabSelected(TabLayout.Tab tab) {
                                             viewPager.setCurrentItem(tab.getPosition());
                                         }

                                         @Override
                                         public void onTabUnselected(TabLayout.Tab tab) {

                                         }

                                         @Override
                                         public void onTabReselected(TabLayout.Tab tab) {

                                         }
                                     });
                                 } else if (responseData.getResponseCode() == 400) {
                                     String message = responseData.getResult().getMessage();
                                     if (!StringUtils.isNullOrEmpty(message)) {
                                         showToast(message);
                                     } else {
                                         showToast(getString(R.string.went_wrong));
                                     }
                                 }
                             } catch (Exception e) {
                                 removeProgressDialog();
                                 Crashlytics.logException(e);
                                 Log.d("MC4KException", Log.getStackTraceString(e));
                                 showToast(getString(R.string.went_wrong));
                             }

                         }


                         @Override
                         public void onFailure(Call<BloggerDashboardModel> call, Throwable t) {

                         }
                     }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBloggerDashboardDetails();
    }

    @Override
    protected void updateUi(Response response) {
        removeProgressDialog();

        BloggerDashboardModel responseData;

        if (response == null) {
            showToast("Something went wrong from server");
            removeProgressDialog();
//            mLodingView.setVisibility(View.GONE);
            return;
        }

        switch (response.getDataType()) {
            case AppConstants.GET_BLOGGER_DASHBOARD_REQUEST:
           *//*     responseData = (BloggerDashboardModel) response.getResponseObject();
                try {
                    if (responseData.getResponseCode() == 200) {
                        removeProgressDialog();

                        if (responseData.getResult().getData().getRank() == 0) {
                            rankingTextView.setVisibility(View.INVISIBLE);
                            viewCountTextView.setVisibility(View.INVISIBLE);
                            followersViewCount.setVisibility(View.INVISIBLE);
                            vSeparator1.setVisibility(View.INVISIBLE);
                            vSeparator2.setVisibility(View.INVISIBLE);
                        } else {
                            rankingTextView.setVisibility(View.VISIBLE);
                            viewCountTextView.setVisibility(View.VISIBLE);
                            followersViewCount.setVisibility(View.VISIBLE);
                            vSeparator1.setVisibility(View.VISIBLE);
                            vSeparator2.setVisibility(View.VISIBLE);
                            rankingTextView.setText("" + responseData.getResult().getData().getRank());
                            viewCountTextView.setText("" + responseData.getResult().getData().getViews());
                            followersViewCount.setText("" + responseData.getResult().getData().getFollowers());
                        }

                        tabLayout.getTabAt(0).setText("Bookmarks (" + responseData.getResult().getData().getBookmarkCount() + ")");
                        tabLayout.getTabAt(1).setText("Published (" + responseData.getResult().getData().getArticleCount() + ")");
                        tabLayout.getTabAt(0).select();
                        final BloggerDashboardPagerAdapter adapter = new BloggerDashboardPagerAdapter
                                (getSupportFragmentManager(), this, responseData.getResult().getData().getBookmarkCount(),
                                        responseData.getResult().getData().getArticleCount());
                        viewPager.setAdapter(adapter);

                    } else if (responseData.getResponseCode() == 400) {
                        String message = responseData.getResult().getMessage();
                        if (!StringUtils.isNullOrEmpty(message)) {
                            showToast(message);
                        } else {
                            showToast(getString(R.string.went_wrong));
                        }
                    }
                } catch (Exception e) {
                    removeProgressDialog();
                    e.printStackTrace();
                    showToast(getString(R.string.went_wrong));
                }
                break;

            default:
                break;*//*
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(BloggerDashboardActivity.this, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void hidefloatingbutton(Boolean b) {
        if (b == true) {
            addDraft.setVisibility(View.INVISIBLE);
        } else {
            addDraft.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) {
            return;
        }

        //   mediaFile.setVideo(imageUri.toString().contains("video"));

        switch (requestCode) {
            case ADD_MEDIA_ACTIVITY_REQUEST_CODE:
                imageUri = data.getData();

                if (resultCode == Activity.RESULT_OK) {
                    try {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        Cursor cursor = this.getContentResolver().query(
                                selectedImage, filePathColumn, null, null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String filePath = cursor.getString(columnIndex);
                        cursor.close();
                        Log.e("File", "filePath: " + filePath);
                        filePath = filePath.replaceAll("[^a-zA-Z0-9.-/_]", "_");
                        file = new File(new URI("file://"
                                + filePath.replaceAll(" ", "%20")));

                        Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(BloggerDashboardActivity.this.getContentResolver(), imageUri);
                        //  sendUploadProfileImageRequest(imageBitmap);
                        float actualHeight = imageBitmap.getHeight();
                        float actualWidth = imageBitmap.getWidth();
                        float maxHeight = 243;
                        float maxWidth = 423;
                       *//* float maxHeight = 1300;
                        float maxWidth = 700;*//*
                        float imgRatio = actualWidth / actualHeight;
                        float maxRatio = maxWidth / maxHeight;

                        if (actualHeight > maxHeight || actualWidth > maxWidth) {
                            if (imgRatio < maxRatio) {
                                //adjust width according to maxHeight
                                imgRatio = maxHeight / actualHeight;
                                actualWidth = imgRatio * actualWidth;
                                actualHeight = maxHeight;
                            } else if (imgRatio > maxRatio) {
                                //adjust height according to maxWidth
                                imgRatio = maxWidth / actualWidth;
                                actualHeight = imgRatio * actualHeight;
                                actualWidth = maxWidth;
                            } else {
                                actualHeight = maxHeight;
                                actualWidth = maxWidth;
                            }
                        }
                        finalBitmap = Bitmap.createScaledBitmap(imageBitmap, (int) actualWidth, (int) actualHeight, true);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        finalBitmap.compress(Bitmap.CompressFormat.PNG, 75, stream);
                        //     byte[] byteArrayFromGallery = stream.toByteArray();

                        //   imageString = Base64.encodeToString(byteArrayFromGallery, Base64.DEFAULT);
                        String path = MediaStore.Images.Media.insertImage(BloggerDashboardActivity.this.getContentResolver(), finalBitmap, "Title", null);
                        Uri imageUriTemp=Uri.parse(path);
                        File file2= FileUtils.getFile(this,imageUriTemp);
                        sendUploadProfileImageRequest(file2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
                break;
        }
    }
    public void sendUploadProfileImageRequest(File file) {
        showProgressDialog(getString(R.string.please_wait));
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
       *//* originalImage.compress(Bitmap.CompressFormat.PNG, 75, bao);
        byte[] ba = bao.toByteArray();
        String imageString = Base64.encodeToString(ba, Base64.DEFAULT);*//*

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstants.LIVE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        RequestBody requestBodyFile = RequestBody.create(MEDIA_TYPE_PNG, file);
   //     RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), "" + userModel.getUser().getId());
        RequestBody imageType = RequestBody.create(MediaType.parse("text/plain"), "jpg");
        // prepare call in Retrofit 2.0
        ImageUploadAPI imageUploadAPI = retrofit.create(ImageUploadAPI.class);

        Call<ImageUploadResponse> call = imageUploadAPI.uploadImage(//userId,
                //  imageType,
                requestBodyFile);
        //asynchronous call
        call.enqueue(new Callback<ImageUploadResponse>() {
                         @Override
                         public void onResponse(Call<ImageUploadResponse> call, retrofit2.Response<ImageUploadResponse> response) {
                             int statusCode = response.code();
                             ImageUploadResponse responseModel = response.body();

                             removeProgressDialog();
                             if (responseModel.getCode() != 200) {
                                 showToast(getString(R.string.toast_response_error));
                                 return;
                             } else {
                                 if (!StringUtils.isNullOrEmpty(responseModel.getData().getUrl())) {
                                     Log.i("IMAGE_UPLOAD_REQUEST", responseModel.getData().getUrl());
                                 }
                                 setProfileImage( responseModel.getData().getUrl());
                              //   setProfileImage(responseModel.getData().getUrl());
                                 Picasso.with(BloggerDashboardActivity.this).load(responseModel.getData().getUrl()).placeholder(R.drawable.family_xxhdpi)
                                         .error(R.drawable.family_xxhdpi).transform(new RoundedTransformation()).into(bloggerImageView);
                                 showToast("Image successfully uploaded!");
                                 // ((BaseActivity) this()).showSnackbar(getView().findViewById(R.id.root), "You have successfully uploaded an image.");
                             }
                         }

                         @Override
                         public void onFailure(Call<ImageUploadResponse> call, Throwable t) {

                         }
                     }
        );

    }
  public void   setProfileImage(String url)
  {
      UpdateUserDetail updateUserDetail=new UpdateUserDetail();
      updateUserDetail.setAttributeName("profilePicUrl");
      updateUserDetail.setAttributeValue(url);
      updateUserDetail.setAttributeType("S");
      Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
      UserAttributeUpdateAPI userAttributeUpdateAPI= retrofit.create(UserAttributeUpdateAPI.class);
      Call<UserDetailResponse> call=userAttributeUpdateAPI.updateProfilePic(updateUserDetail);
      call.enqueue(new Callback<UserDetailResponse>() {
          @Override
          public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
              if (!response.body().getStatus().equals("success"))
              {
                  showToast(getString(R.string.toast_response_error));
              }
          }

          @Override
          public void onFailure(Call<UserDetailResponse> call, Throwable t) {

          }
      });

  }
*/
}
