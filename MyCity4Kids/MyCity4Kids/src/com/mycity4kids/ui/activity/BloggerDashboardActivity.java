package com.mycity4kids.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.editor.DraftListAdapter;
import com.mycity4kids.editor.EditorPostActivity;
import com.mycity4kids.enums.ArticleType;
import com.mycity4kids.filechooser.com.ipaulpro.afilechooser.utils.FileUtils;
import com.mycity4kids.models.editor.ArticleDraftRequest;
import com.mycity4kids.models.request.UpdateUserDetail;
import com.mycity4kids.models.response.ArticleDraftResponse;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.models.response.DraftListResponse;
import com.mycity4kids.models.response.ImageUploadResponse;
import com.mycity4kids.models.response.PublishDraftObject;
import com.mycity4kids.models.response.ReviewListingResult;
import com.mycity4kids.models.response.ReviewResponse;
import com.mycity4kids.models.response.SearchArticleResult;
import com.mycity4kids.models.response.UserCommentsResponse;
import com.mycity4kids.models.response.UserCommentsResult;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDraftAPI;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;
import com.mycity4kids.retrofitAPIsInterfaces.ImageUploadAPI;
import com.mycity4kids.retrofitAPIsInterfaces.UserAttributeUpdateAPI;
import com.mycity4kids.ui.adapter.CommentsListAdapter;
import com.mycity4kids.ui.adapter.PublishedArticleListAdapter;
import com.mycity4kids.ui.adapter.PublishedArticleListingAdapter;
import com.mycity4kids.ui.adapter.ReviewsListAdapter;
import com.mycity4kids.ui.adapter.UserCommentsAdapter;
import com.mycity4kids.utils.RoundedTransformation;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
    private TextView bloggerNameTextView, viewCountTextView, followersViewCount;
    private ImageView bloggerImageView;
    View vSeparator1, vSeparator2;
    TabLayout tabLayout;
    ViewPager viewPager;
    ImageView addDraft;
    public static final int ADD_MEDIA_ACTIVITY_REQUEST_CODE = 1111;
    Uri imageUri;
    Bitmap finalBitmap;
    File file;
    ListView draftListview, publishedArticleListView, bookmarksListView,commentsListView, reviewsListView;
    ArrayList<PublishDraftObject> draftList;
    ArrayList<ReviewListingResult> reviewList;
    ArrayList<UserCommentsResult> commentList;
    int position;
    TextView noDrafts;
    DraftListAdapter adapter;
    PublishedArticleListingAdapter articlesListingAdapter;
    UserCommentsAdapter commentsListAdapter;
    ReviewsListAdapter reviewsListAdapter;
    TextView rankingTextView, followersTextView, followingTextView, userBio, blogTitle, editProfileTextView;
    ImageView draftImageView, publishedImageView, commentsImageView, reviewImageView;
    LinearLayout draftItemLinearLayout, publishedItemLinearLayout, commentsItemLinearLayout, reviewItemLinearLayout;
    private String firstName, lastName, Bio, phoneNumber;
    private int nextPageNumber = 1;
    RelativeLayout mLodingView;
    private boolean isReuqestRunning = false;
    boolean isLastPageReached = true;
    private ProgressBar progressBar;
    ArrayList<ArticleListingResult> articleDataModelsNew;
    TextView noDraftTextView,noArticleTextView,noReviewsTextView,noCommentsTextView;
    TextView moreTextView;
    public String userId;
    public boolean isPrivateProfile;
    TextView follow,following;
    LinearLayout FollowersLinearLL,FollowingLinearLL;
    View header;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blogger_dashboard);
        userId=getIntent().getStringExtra(AppConstants.PUBLIC_PROFILE_USER_ID);
        if (userId==null||userId.equals(SharedPrefUtils.getUserDetailModel(BloggerDashboardActivity.this).getDynamoId()))
        {isPrivateProfile=true;
            userId=SharedPrefUtils.getUserDetailModel(BloggerDashboardActivity.this).getDynamoId();
        }
        else {
            isPrivateProfile=false;
        }
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        draftListview = (ListView) findViewById(R.id.draftListView);
        publishedArticleListView = (ListView) findViewById(R.id.publishedArticleListView);
        commentsListView = (ListView) findViewById(R.id.commentsListView);
        reviewsListView = (ListView) findViewById(R.id.reviewsListView);
        mLodingView = (RelativeLayout) findViewById(R.id.relativeLoadingView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
      //  noDrafts = (TextView) findViewById(R.id.noDraftsTextView);
        if (isPrivateProfile)
        {     header = getLayoutInflater().inflate(R.layout.header_blogger_dashboard, null);}
        else {
            header = getLayoutInflater().inflate(R.layout.header_blogger_dashboard_public, null);
        }
        header.setClickable(false);
        rankingTextView = (TextView) header.findViewById(R.id.rankingTextView);
        followersTextView = (TextView) header.findViewById(R.id.followersTextView);
        followingTextView = (TextView) header.findViewById(R.id.followingTextView);
        userBio = (TextView) header.findViewById(R.id.userBio);
        blogTitle = (TextView) header.findViewById(R.id.blogName);
        draftItemLinearLayout = (LinearLayout) header.findViewById(R.id.draftItemLinearlayout);
        publishedItemLinearLayout = (LinearLayout) header.findViewById(R.id.publishedItemLinearlayout);
        commentsItemLinearLayout = (LinearLayout) header.findViewById(R.id.commentsItemLinearlayout);
        reviewItemLinearLayout = (LinearLayout) header.findViewById(R.id.reviewItemLinearLayout);
        draftImageView = (ImageView) header.findViewById(R.id.draftImageView);
        publishedImageView = (ImageView) header.findViewById(R.id.publishedImageView);
        commentsImageView = (ImageView) header.findViewById(R.id.commentsImageView);
        reviewImageView = (ImageView) header.findViewById(R.id.reviewImageView);
        editProfileTextView = (TextView) header.findViewById(R.id.editProfileTextView);
        noDraftTextView=(TextView) header.findViewById(R.id.noDraftsTextView);
        noArticleTextView=(TextView) header.findViewById(R.id.noArticlesTextView);
        noCommentsTextView=(TextView) header.findViewById(R.id.noCommentsTextView);
        noReviewsTextView=(TextView) header.findViewById(R.id.noReviewTextView);
        moreTextView=(TextView) header.findViewById(R.id.more_text);
        follow=(TextView) header.findViewById(R.id.followTextView);
        following=(TextView) header.findViewById(R.id.followingTextView1);
        FollowersLinearLL=(LinearLayout) header.findViewById(R.id.FollowersLinearLL);
        FollowingLinearLL=(LinearLayout) header.findViewById(R.id.FollowingLinearLL);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");
        draftList=new ArrayList<>();
        reviewList=new ArrayList<>();
        commentList=new ArrayList<>();
        articleDataModelsNew = new ArrayList<>();
        adapter = new DraftListAdapter(this, draftList);
        reviewsListAdapter=new ReviewsListAdapter(this,reviewList);
        articlesListingAdapter= new PublishedArticleListingAdapter(this, new PublishedArticleListingAdapter.BtnClickListener() {
            @Override
            public void onBtnClick(int position) {

            }
        });


        if (isPrivateProfile)
        {
            follow.setVisibility(View.INVISIBLE);
            following.setVisibility(View.INVISIBLE);
            editProfileTextView.setVisibility(View.VISIBLE);
            draftItemLinearLayout.setVisibility(View.VISIBLE);
            draftListview.addHeaderView(header,null,false);
        }
        else {
            follow.setVisibility(View.VISIBLE);
            following.setVisibility(View.VISIBLE);
            editProfileTextView.setVisibility(View.GONE);
            draftListview.setVisibility(View.GONE);
//            draftItemLinearLayout.setVisibility(View.GONE);
            publishedArticleListView.addHeaderView(header,null,false);
            publishedArticleListView.setVisibility(View.VISIBLE);
            draftListview.setVisibility(View.GONE);
            publishedImageView.setColorFilter(getResources().getColor(R.color.red_selected));

        }


        reviewsListView.setAdapter(reviewsListAdapter);
        draftListview.setAdapter(adapter);
        bloggerImageView = (ImageView) findViewById(R.id.bloggerImageView);
        if (!StringUtils.isNullOrEmpty(SharedPrefUtils.getProfileImgUrl(this))) {
            Picasso.with(this).load(SharedPrefUtils.getProfileImgUrl(this)).placeholder(R.drawable.family_xxhdpi)
                    .error(R.drawable.family_xxhdpi).transform(new RoundedTransformation()).into(bloggerImageView);
        }
        bloggerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, ADD_MEDIA_ACTIVITY_REQUEST_CODE);
            }
        });
        draftItemLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                draftImageView.setColorFilter(getResources().getColor(R.color.red_selected));
                publishedImageView.setColorFilter(getResources().getColor(R.color.grey_icon_unselected));
                commentsImageView.setColorFilter(getResources().getColor(R.color.grey_icon_unselected));
                reviewImageView.setColorFilter(getResources().getColor(R.color.grey_icon_unselected));
                draftListview.setVisibility(View.VISIBLE);
                if(draftList.size()==0){
                    noDraftTextView.setVisibility(View.VISIBLE);
                    noArticleTextView.setVisibility(View.GONE);
                    noCommentsTextView.setVisibility(View.GONE);
                    noReviewsTextView.setVisibility(View.GONE);
                }
                else {
                    noDraftTextView.setVisibility(View.GONE);
                    noArticleTextView.setVisibility(View.GONE);
                    noCommentsTextView.setVisibility(View.GONE);
                    noReviewsTextView.setVisibility(View.GONE);
                }
                publishedArticleListView.setVisibility(View.GONE);

                commentsListView.setVisibility(View.GONE);
                reviewsListView.setVisibility(View.GONE);
            }
        });
        publishedItemLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                draftImageView.setColorFilter(getResources().getColor(R.color.grey_icon_unselected));
                publishedImageView.setColorFilter(getResources().getColor(R.color.red_selected));
                commentsImageView.setColorFilter(getResources().getColor(R.color.grey_icon_unselected));
                reviewImageView.setColorFilter(getResources().getColor(R.color.grey_icon_unselected));
                draftListview.setVisibility(View.GONE);
                if (publishedArticleListView.getHeaderViewsCount()==0) {
                    publishedArticleListView.addHeaderView(header,null,false);
                }
                if(articleDataModelsNew.size()==0){
                    noArticleTextView.setVisibility(View.VISIBLE);
                    noDraftTextView.setVisibility(View.GONE);
                    noCommentsTextView.setVisibility(View.GONE);
                    noReviewsTextView.setVisibility(View.GONE);
                }
                else {
                    noDraftTextView.setVisibility(View.GONE);
                    noArticleTextView.setVisibility(View.GONE);
                    noCommentsTextView.setVisibility(View.GONE);
                    noReviewsTextView.setVisibility(View.GONE);
                }
                header.setClickable(false);
                publishedArticleListView.setVisibility(View.VISIBLE);
                commentsListView.setVisibility(View.GONE);
                reviewsListView.setVisibility(View.GONE);
            }
        });
        commentsItemLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                draftImageView.setColorFilter(getResources().getColor(R.color.grey_icon_unselected));
                publishedImageView.setColorFilter(getResources().getColor(R.color.grey_icon_unselected));
                commentsImageView.setColorFilter(getResources().getColor(R.color.red_selected));
                reviewImageView.setColorFilter(getResources().getColor(R.color.grey_icon_unselected));
                draftListview.setVisibility(View.GONE);
                publishedArticleListView.setVisibility(View.GONE);
                if (commentsListView.getHeaderViewsCount()==0) {
                    commentsListView.addHeaderView(header,null,false);
                }
                if(commentList.size()==0){
                    noDraftTextView.setVisibility(View.GONE);
                    noArticleTextView.setVisibility(View.GONE);
                    noCommentsTextView.setVisibility(View.VISIBLE);
                    noReviewsTextView.setVisibility(View.GONE);
                }
                else {
                    noDraftTextView.setVisibility(View.GONE);
                    noArticleTextView.setVisibility(View.GONE);
                    noCommentsTextView.setVisibility(View.GONE);
                    noReviewsTextView.setVisibility(View.GONE);
                }
                header.setClickable(false);
                commentsListView.setVisibility(View.VISIBLE);
                reviewsListView.setVisibility(View.GONE);
            }
        });
        reviewItemLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                draftImageView.setColorFilter(getResources().getColor(R.color.grey_icon_unselected));
                publishedImageView.setColorFilter(getResources().getColor(R.color.grey_icon_unselected));
                commentsImageView.setColorFilter(getResources().getColor(R.color.grey_icon_unselected));
                reviewImageView.setColorFilter(getResources().getColor(R.color.red_selected));
                draftListview.setVisibility(View.GONE);
                publishedArticleListView.setVisibility(View.GONE);
                commentsListView.setVisibility(View.GONE);
                if (reviewsListView.getHeaderViewsCount()==0) {
                reviewsListView.addHeaderView(header,null,false);}
                header.setClickable(false);
                if(reviewList.size()==0){
                    noDraftTextView.setVisibility(View.GONE);
                    noArticleTextView.setVisibility(View.GONE);
                    noCommentsTextView.setVisibility(View.GONE);
                    noReviewsTextView.setVisibility(View.VISIBLE);
                }
                else {
                    noDraftTextView.setVisibility(View.GONE);
                    noArticleTextView.setVisibility(View.GONE);
                    noCommentsTextView.setVisibility(View.GONE);
                    noReviewsTextView.setVisibility(View.GONE);
                }
                reviewsListView.setVisibility(View.VISIBLE);
            }
        });
        editProfileTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BloggerDashboardActivity.this, EditProfieActivity.class);
                if (Bio != null && firstName != null && lastName != null) {
                    intent.putExtra("bio", Bio);
                    intent.putExtra("firstName", firstName);
                    intent.putExtra("lastName", lastName);
                    startActivity(intent);
                } else {
                    showToast("Please Wait");
                }
            }
        });
        publishedArticleListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
                if (visibleItemCount != 0 && loadMore && firstVisibleItem != 0 && !isReuqestRunning && isLastPageReached) {
                    mLodingView.setVisibility(View.VISIBLE);
                  hitPublishedArticleApi();
                    isReuqestRunning = true;
                }
            }
        });
        moreTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (moreTextView.getText().toString().equalsIgnoreCase("More")) {
                    userBio.setMaxLines(100);
                    userBio.setEllipsize(null);
                    moreTextView.setText("Less");
                } else {
                    userBio.setMaxLines(3);
                    userBio.setEllipsize(null);
                    moreTextView.setText("More");
                }
            }
        });
        FollowersLinearLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(BloggerDashboardActivity.this,FollowersAndFollowingListActivity.class);
                intent.putExtra(AppConstants.FOLLOW_LIST_TYPE,AppConstants.FOLLOWER_LIST);
                startActivity(intent);

            }
        });
        FollowingLinearLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(BloggerDashboardActivity.this,FollowersAndFollowingListActivity.class);
                intent.putExtra(AppConstants.FOLLOW_LIST_TYPE,AppConstants.FOLLOWING_LIST);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBloggerDashboardDetails();
        hitDraftListingApi();
        hitPublishedArticleApi();
        hitCommentsApi();
        hitReviewApi();

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
        commentsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // Header Item

                }else {
                Intent intent=new Intent(BloggerDashboardActivity.this,ArticlesAndBlogsDetailsActivity.class);
                intent.putExtra(Constants.ARTICLE_ID,commentList.get(position-1).getArticleId());
                intent.putExtra(Constants.AUTHOR_ID,commentList.get(position-1).getUserId());
                startActivity(intent);}
            }
        });
        reviewsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // Header Item

                }
                else {
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(BloggerDashboardActivity.this, BusinessDetailsActivity.class);
                    if (reviewList.get(position - 1).getType().equals("business")) {

                        bundle.putInt(Constants.CATEGORY_ID, SharedPrefUtils.getEventIdForCity(BloggerDashboardActivity.this));
                        bundle.putString(Constants.BUSINESS_OR_EVENT_ID, reviewList.get(position - 1).getReviewId());
                        bundle.putInt(Constants.PAGE_TYPE, 0);
                        bundle.putBoolean("isbusiness", true);
                        bundle.putString(Constants.DISTANCE, "0");
                    } else {
                        bundle.putInt(Constants.CATEGORY_ID, SharedPrefUtils.getEventIdForCity(BloggerDashboardActivity.this));
                        bundle.putString(Constants.BUSINESS_OR_EVENT_ID, reviewList.get(position - 1).getReviewId());
                        bundle.putInt(Constants.PAGE_TYPE, 1);
                        bundle.putBoolean("isbusiness", false);
                        bundle.putString(Constants.DISTANCE, "0");
                    }
                    intent.putExtras(bundle);
                    startActivity(intent);
                } }
        });
    }

    private void onSectionHeaderItemClick(int listType) {

        if (listType == LIST_TYPE_DRAFT) {

        } else if (listType == LIST_TYPE_PUBLISHED) {

        } else if (listType == LIST_TYPE_COMMENTS) {

        } else if (listType == LIST_TYPE_COMMENTS) {

        }
    }

    private void hitReviewApi() {
        showProgressDialog(getResources().getString(R.string.please_wait));
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        // prepare call in Retrofit 2.0
        BloggerDashboardAPI getReviewList = retrofit.create(BloggerDashboardAPI.class);
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog();
            showToast(getString(R.string.error_network));
            return;
        }

        Call<ReviewResponse> call = getReviewList.getUserReview(AppConstants.LIVE_URL + "apiservices/getUserReviews?userId="+userId);
//asynchronous call
        call.enqueue(new Callback<ReviewResponse>() {
                         @Override
                         public void onResponse(Call<ReviewResponse> call, retrofit2.Response<ReviewResponse> response) {
                             int statusCode = response.code();
                             removeProgressDialog();

                             ReviewResponse responseModel = response.body();
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
                         public void onFailure(Call<ReviewResponse> call, Throwable t) {
                             removeProgressDialog();

                         }
                     }
        );
    }

    private void hitCommentsApi() {
        showProgressDialog(getResources().getString(R.string.please_wait));
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        // prepare call in Retrofit 2.0
        BloggerDashboardAPI getCommentsAPI = retrofit.create(BloggerDashboardAPI.class);
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog();
            showToast(getString(R.string.error_network));
            return;
        }
        Call<UserCommentsResponse> call = getCommentsAPI.getUserComments(AppConstants.LIVE_URL+"v1/comments/"+userId);
//asynchronous call
        call.enqueue(new Callback<UserCommentsResponse>() {
                         @Override
                         public void onResponse(Call<UserCommentsResponse> call, retrofit2.Response<UserCommentsResponse> response) {
                             int statusCode = response.code();
                             removeProgressDialog();

                             UserCommentsResponse responseModel = response.body();
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
                         public void onFailure(Call<UserCommentsResponse> call, Throwable t) {
                             removeProgressDialog();

                         }
                     }
        );
    }

    private void hitPublishedArticleApi() {
        if (nextPageNumber == 1 && null != progressBar) {
            progressBar.setVisibility(View.VISIBLE);
        }
        showProgressDialog(getResources().getString(R.string.please_wait));
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        // prepare call in Retrofit 2.0
        BloggerDashboardAPI getPublishedArticles = retrofit.create(BloggerDashboardAPI.class);
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog();
            showToast(getString(R.string.error_network));
            return;
        }
        int from = (nextPageNumber - 1) * 15 + 1;
      //  Call<ArticleListingResponse> call = getPublishedArticles.getPublishedArticles(AppConstants.LIVE_URL+"v1/articles/user/"+ SharedPrefUtils.getUserDetailModel(getApplicationContext()).getDynamoId()+"?sort=0&start="+from+"&end="+(from+14));
        Call<ArticleListingResponse> call = getPublishedArticles.getPublishedArticles(AppConstants.LIVE_URL+"v1/articles/user/"+ userId+"?sort=0&start="+from+"&end="+(from+14));
//asynchronous call
        call.enqueue(new Callback<ArticleListingResponse>() {
                         @Override
                         public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
                             int statusCode = response.code();
                             removeProgressDialog();
                             isReuqestRunning = false;
                             progressBar.setVisibility(View.INVISIBLE);
                             if (mLodingView.getVisibility() == View.VISIBLE) {
                                 mLodingView.setVisibility(View.GONE);
                             }
                             ArticleListingResponse responseModel = response.body();
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
                         public void onFailure(Call<ArticleListingResponse> call, Throwable t) {
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

        Call<DraftListResponse> call = getDraftListAPI.getDraftsList("0,1,2,4");

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
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        // prepare call in Retrofit 2.0
        ArticleDraftAPI articleDraftAPI = retrofit.create(ArticleDraftAPI.class);
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            showToast(getString(R.string.error_network));
            return;
        }

        Call<ArticleDraftResponse> call = articleDraftAPI.deleteDraft(
                AppConstants.LIVE_URL + "v1/articles/" + draftObject.getId());

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

        if (draftList.size() == 0&&draftListview.getVisibility()==View.VISIBLE) {
            noDraftTextView.setVisibility(View.VISIBLE);
        } else {

//            noDrafts.setVisibility(View.GONE);
            adapter = new DraftListAdapter(this, draftList);
            draftListview.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

    }

    private void processPublisedArticlesResponse(ArticleListingResponse responseData) {
        ArrayList<ArticleListingResult> dataList = responseData.getData().getResult();

        if (dataList.size() == 0) {

            isLastPageReached = false;
            if (null != articleDataModelsNew && !articleDataModelsNew.isEmpty()) {
                //No more next results for search from pagination

            } else  {
                // No results for search

                articleDataModelsNew = dataList;
                articlesListingAdapter.setNewListData(articleDataModelsNew);
                publishedArticleListView.setAdapter(articlesListingAdapter);
                articlesListingAdapter.setListData(dataList);
                articlesListingAdapter.notifyDataSetChanged();
                if(publishedArticleListView.getVisibility()==View.VISIBLE){
                    noArticleTextView.setVisibility(View.VISIBLE);
                }
//                noDrafts.setVisibility(View.VISIBLE);
//                noDrafts.setText("No articles found");
            }

//            articleDataModelsNew = dataList;
//            articlesListingAdapter.setNewListData(articleDataModelsNew);
//            articlesListingAdapter.notifyDataSetChanged();
//            noBlogsTextView.setVisibility(View.VISIBLE);
//            noBlogsTextView.setText("No articles found");
        } else {
//            noDrafts.setVisibility(View.GONE);
//            totalPageCount = responseData.getResult().getData().getPage_count();
            if (nextPageNumber == 1) {
                articleDataModelsNew = dataList;
                articlesListingAdapter.setNewListData(articleDataModelsNew);
                publishedArticleListView.setAdapter(articlesListingAdapter);
                articlesListingAdapter.notifyDataSetChanged();

            } else {
                articleDataModelsNew.addAll(dataList);
            }
            articlesListingAdapter.setListData(articleDataModelsNew);
            nextPageNumber = nextPageNumber + 1;
            articlesListingAdapter.notifyDataSetChanged();
        }

    }

    private void processCommentsResponse(UserCommentsResponse responseModel) {
        commentList = responseModel.getData().getResult();

        if (commentList.size() == 0 && commentsListView.getVisibility()==View.VISIBLE) {
            noCommentsTextView.setVisibility(View.VISIBLE);
          //  noDrafts.setVisibility(View.VISIBLE);
        } else {
          //  noDrafts.setVisibility(View.GONE);
            commentsListAdapter = new UserCommentsAdapter(this, commentList);
            commentsListView.setAdapter(commentsListAdapter);
            commentsListAdapter.notifyDataSetChanged();
        }

    }

    private void processReviewsResponse(ReviewResponse responseModel) {
        reviewList = responseModel.getData().getResult();

        if (reviewList.size() == 0 && reviewsListView.getVisibility()==View.VISIBLE) {
            noReviewsTextView.setVisibility(View.VISIBLE);
         //   noDrafts.setVisibility(View.VISIBLE);
        } else {
        //    noDrafts.setVisibility(View.GONE);
            reviewsListAdapter = new ReviewsListAdapter(this, reviewList);
            reviewsListView.setAdapter(reviewsListAdapter);
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
*/
    private void getBloggerDashboardDetails() {

        showProgressDialog("please wait ...");
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        // prepare call in Retrofit 2.0
        BloggerDashboardAPI bloggerDashboardAPI = retrofit.create(BloggerDashboardAPI.class);
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog();
            showToast(getString(R.string.error_network));
            return;
        }
    //    Call<UserDetailResponse> call = bloggerDashboardAPI.getBloggerData(AppConstants.LIVE_URL + "v1/users/dashboard/" + SharedPrefUtils.getUserDetailModel(getApplicationContext()).getDynamoId());

        Call<UserDetailResponse> call = bloggerDashboardAPI.getBloggerData(AppConstants.LIVE_URL + "v1/users/dashboard/" + userId);
        //asynchronous call
        call.enqueue(new Callback<UserDetailResponse>() {
                         @Override
                         public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
                             int statusCode = response.code();

                             UserDetailResponse responseData = (UserDetailResponse) response.body();

                             removeProgressDialog();

                             if (responseData.getCode() != 200) {
                                 showToast(getString(R.string.toast_response_error));
                                 return;
                             } else {
                                 rankingTextView.setText(responseData.getData().getResult().getRank());
                                 followersTextView.setText(responseData.getData().getResult().getFollowersCount());
                                 followingTextView.setText(responseData.getData().getResult().getFollowingCount());
                                 blogTitle.setText(responseData.getData().getResult().getBlogTitle());
                                 getSupportActionBar().setTitle(responseData.getData().getResult().getFirstName());
                                 Bio = responseData.getData().getResult().getUserBio();
                                 firstName = responseData.getData().getResult().getFirstName();
                                 lastName = responseData.getData().getResult().getLastName();
                                 phoneNumber = responseData.getData().getResult().getPhoneNumber();
                                 if (responseData.getData().getResult().getUserBio()==null || responseData.getData().getResult().getUserBio().isEmpty())
                                 {
                                     userBio.setVisibility(View.GONE);
                                     moreTextView.setVisibility(View.GONE);
                                 }else{
                                     userBio.setText(responseData.getData().getResult().getUserBio());
                                     userBio.setVisibility(View.VISIBLE);
                                 }
                                 if (userBio.getLineCount() >= 3) {
                                     moreTextView.setVisibility(View.VISIBLE);
                                     userBio.setMaxLines(3);
                                     userBio.setEllipsize(null);
                                     moreTextView.setText("More");
                                 } else {
                                     userBio.setMaxLines(3);
                                     userBio.setEllipsize(null);
                                     moreTextView.setVisibility(View.GONE);
                                 }

                             }

                         }


                         @Override
                         public void onFailure(Call<UserDetailResponse> call, Throwable t) {

                         }
                     }
        );
    }

    /*


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
    */
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
                       /* float maxHeight = 1300;
                        float maxWidth = 700;*/
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
                        Uri imageUriTemp = Uri.parse(path);
                        File file2 = FileUtils.getFile(this, imageUriTemp);
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
       /* originalImage.compress(Bitmap.CompressFormat.PNG, 75, bao);
        byte[] ba = bao.toByteArray();
        String imageString = Base64.encodeToString(ba, Base64.DEFAULT);*/

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
                                 setProfileImage(responseModel.getData().getUrl());
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

    public void setProfileImage(String url) {
        UpdateUserDetail updateUserDetail = new UpdateUserDetail();
        updateUserDetail.setAttributeName("profilePicUrl");
        updateUserDetail.setAttributeValue(url);
        updateUserDetail.setAttributeType("S");
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        UserAttributeUpdateAPI userAttributeUpdateAPI = retrofit.create(UserAttributeUpdateAPI.class);
        Call<UserDetailResponse> call = userAttributeUpdateAPI.updateProfilePic(updateUserDetail);
        call.enqueue(new Callback<UserDetailResponse>() {
            @Override
            public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
                if (!response.body().getStatus().equals("success")) {
                    showToast(getString(R.string.toast_response_error));
                }
            }

            @Override
            public void onFailure(Call<UserDetailResponse> call, Throwable t) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(isPrivateProfile)
        {  MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_profile_menu, menu);}
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.bookmarkListItem:
                //  newGame();
                Intent intent = new Intent(BloggerDashboardActivity.this, BookmarkListActivity.class);
                startActivity(intent);
                return true;
            case R.id.settings:

               // showHelp();
                Intent intent1= new Intent(BloggerDashboardActivity.this, SettingsActivity.class);
                intent1.putExtra("load_fragment",Constants.SETTINGS_FRAGMENT);
                intent1.putExtra("bio",Bio);
                intent1.putExtra("firstName",firstName);
                intent1.putExtra("lastName",lastName);
                startActivity(intent1);

                return true;
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
 /*   @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
       *//* menu.add(0, BUTTON_ID_LOG_HTML, 0, "Log HTML")
                .setIcon(R.drawable.ic_log_html)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);*//*
        inflater.inflate(org.wordpress.android.editor.R.menu.menu_editor, menu);
        getActionBar().setTitle("Write an Article");
        getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(org.wordpress.android.editor.R.color.primary)));
        super.onCreateOptionsMenu(menu, inflater);
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Intent intent = new Intent(BloggerDashboardActivity.this, DashboardActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
//        finish();
    }
}
