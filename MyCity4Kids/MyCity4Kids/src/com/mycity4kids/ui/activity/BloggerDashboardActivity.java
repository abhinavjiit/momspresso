package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.controller.BloggerDashboardAndPublishedArticlesController;
import com.mycity4kids.editor.EditorPostActivity;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.parentingdetails.ParentingDetailResponse;
import com.mycity4kids.newmodels.BloggerDashboardModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDraftAPI;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;
import com.mycity4kids.ui.adapter.BloggerDashboardPagerAdapter;
import com.mycity4kids.utils.RoundedTransformation;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 16/3/16.
 */
public class BloggerDashboardActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private TextView bloggerNameTextView, rankingTextView, viewCountTextView, followersViewCount;
    private ImageView bloggerImageView;
    View vSeparator1, vSeparator2;
    TabLayout tabLayout;
    ViewPager viewPager;
    ImageView addDraft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blogger_dashboard);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

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
       /* BloggerDashboardAndPublishedArticlesController _controller = new BloggerDashboardAndPublishedArticlesController(this, this);
        _controller.getData(AppConstants.GET_BLOGGER_DASHBOARD_REQUEST, 0);*/
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        // prepare call in Retrofit 2.0
        BloggerDashboardAPI bloggerDashboardAPI = retrofit.create(BloggerDashboardAPI.class);
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
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

                                     final BloggerDashboardPagerAdapter adapter = new BloggerDashboardPagerAdapter
                                             (getSupportFragmentManager(), BloggerDashboardActivity.this, responseData.getResult().getData().getBookmarkCount(),
                                                     responseData.getResult().getData().getArticleCount());
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
                                 e.printStackTrace();
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
           /*     responseData = (BloggerDashboardModel) response.getResponseObject();
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
                break;*/
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
}