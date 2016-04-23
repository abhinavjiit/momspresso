package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.BuildConfig;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.forgot.CommonResponse;
import com.mycity4kids.newmodels.bloggermodel.BlogArticleList.BlogArticleListResponse;
import com.mycity4kids.newmodels.bloggermodel.BlogItemModel;
import com.squareup.picasso.Picasso;

/**
 * Created by manish.soni on 24-08-2015.
 */
public class ExpendedBlogDescriptionActivity extends BaseActivity implements View.OnClickListener {

    private float density;
    private int screenWidth;
    BlogItemModel blogDetails;
    TextView bloggerName, authorType, description, less, bloggerTitle, authorRank, authorFollower;
    ImageView bloggerCover, bloggerImage, backIcon;
    TextView bloggerFollow;
    RelativeLayout aboutLayout;
    private String autFollow = "";
    private boolean isFollowing;
    String lastFollowStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog_description_activity);

        blogDetails = getIntent().getParcelableExtra(Constants.BLOG_DETAILS);
        autFollow = getIntent().getStringExtra("AUTHOR_FOLLOW");


        bloggerName = (TextView) findViewById(R.id.blogger_name);
        bloggerTitle = (TextView) findViewById(R.id.blogger_title);
        authorType = (TextView) findViewById(R.id.author_type);
        bloggerImage = (ImageView) findViewById(R.id.blogger_profile);
        bloggerCover = (ImageView) findViewById(R.id.blogger_bg);
        bloggerFollow = (TextView) findViewById(R.id.blog_follow);
        description = (TextView) findViewById(R.id.blogger_desc);
        less = (TextView) findViewById(R.id.less);
        aboutLayout = (RelativeLayout) findViewById(R.id.about_desc_layout);
//        backButton = (ImageView) findViewById(R.id.back_btn);
        authorRank = (TextView) findViewById(R.id.author_rank);
        authorFollower = (TextView) findViewById(R.id.author_follow);
        backIcon = (ImageView) findViewById(R.id.on_back);
        backIcon.setOnClickListener(this);


        authorFollower.setVisibility(View.GONE);

        density = getResources().getDisplayMetrics().density;
        screenWidth = getResources().getDisplayMetrics().widthPixels;

        bloggerFollow.setOnClickListener(this);
        less.setOnClickListener(this);

        setBlogData();

//        if (description.getLineCount() > 4) {
//            description.setMaxLines(4);
//            description.setText("More");
//        }else {
//            moreDesc.setVisibility(View.INVISIBLE);
//        }

        less.setText("Less");

        less.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExpendedBlogDescriptionActivity.this, BlogDetailActivity.class);
                if (!blogDetails.getUser_following_status().equalsIgnoreCase(lastFollowStatus)) {
                    intent.putExtra(Constants.BLOG_STATUS, blogDetails.getUser_following_status());
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    finish();
                }

                overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
            }
        });

    }

    @Override
    protected void updateUi(Response response) {

        BlogArticleListResponse responseData;

        if (response == null) {
            showToast("Something went wrong from server");
            removeProgressDialog();
            return;
        }

        switch (response.getDataType()) {

            case AppConstants.ARTICLE_BLOG_FOLLOW_REQUEST:
                CommonResponse followData = (CommonResponse) response.getResponseObject();
                removeProgressDialog();
                if (followData.getResponseCode() == 200) {
                    ToastUtils.showToast(this, followData.getResult().getMessage(), Toast.LENGTH_SHORT);
                    if (BuildConfig.DEBUG) {
                        Log.e("follow response", followData.getResult().getMessage());
                    }

                    if (isFollowing) {
                        isFollowing = false;
                        blogDetails.setUser_following_status("0");
                        ((TextView) findViewById(R.id.blog_follow)).setText("FOLLOW");
                    } else {
                        isFollowing = true;
                        blogDetails.setUser_following_status("1");
                        ((TextView) findViewById(R.id.blog_follow)).setText("UNFOLLOW");
                    }


                } else if (followData.getResponseCode() == 400) {
                    String message = followData.getResult().getMessage();
                    if (BuildConfig.DEBUG) {
                        Log.e("follow response", followData.getResult().getMessage());
                    }
                    if (!StringUtils.isNullOrEmpty(message)) {
                        showToast(message);
                    } else {
                        showToast(getString(R.string.went_wrong));
                    }
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {

        Intent intent;

        switch (view.getId()) {

            case R.id.blog_follow:
                followAPICall(String.valueOf(blogDetails.getId()));
                break;
            case R.id.facebook_:
                if (!StringUtils.isNullOrEmpty(blogDetails.getFacebook_id())) {
                    intent = new Intent(this, LoadWebViewActivity.class);
                    intent.putExtra(Constants.WEB_VIEW_URL, blogDetails.getFacebook_id());
                    startActivity(intent);
                } else {
                    ToastUtils.showToast(this, "No link found..", Toast.LENGTH_SHORT);
                }

                break;

            case R.id.twitter_:
                if (!StringUtils.isNullOrEmpty(blogDetails.getTwitter_id())) {
                    intent = new Intent(this, LoadWebViewActivity.class);
                    intent.putExtra(Constants.WEB_VIEW_URL, blogDetails.getTwitter_id());
                    startActivity(intent);
                } else {
                    ToastUtils.showToast(this, "No link found..", Toast.LENGTH_SHORT);
                }
                break;
            case R.id.rss_:
                break;

            case R.id.on_back:
                intent = new Intent(ExpendedBlogDescriptionActivity.this, BlogDetailActivity.class);
                if (!blogDetails.getUser_following_status().equalsIgnoreCase(lastFollowStatus)) {
                    intent.putExtra(Constants.BLOG_STATUS, blogDetails.getUser_following_status());
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    finish();
                }
                overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);

                break;


        }

    }


    public void setBlogData() {

        bloggerName.setText(blogDetails.getFirst_name() + " " + blogDetails.getLast_name());
        bloggerName.setTextColor(Color.WHITE);
        bloggerTitle.setText(blogDetails.getBlog_title());
//        mTitleView.setText(blogDetails.getBlog_title() + "");
//        Picasso.with(getApplicationContext()).load(blogDetails.getProfile_image()).placeholder(R.drawable.default_img).resize((int) (90 * density), (int) (100 * density)).centerCrop().into(bloggerImage);
//        Picasso.with(getApplicationContext()).load(blogDetails.getCover_image()).resize(screenWidth, (int) (240 * density)).centerCrop().into(bloggerCover);

        if (!StringUtils.isNullOrEmpty(blogDetails.getProfile_image())) {
            Picasso.with(this).load(blogDetails.getProfile_image()).resize((int) (90 * density), (int) (100 * density)).centerCrop().into(bloggerImage);
        } else {
            Picasso.with(this).load(R.drawable.default_img).resize((int) (90 * density), (int) (100 * density)).centerCrop().into(bloggerImage);
        }
        if (!StringUtils.isNullOrEmpty(blogDetails.getCover_image())) {
            Picasso.with(this).load(blogDetails.getCover_image()).resize(screenWidth, (int) (230 * density)).centerCrop().placeholder(R.drawable.blog_bgnew).into(bloggerCover);
        } else {
            Picasso.with(this).load(R.drawable.blog_bgnew).resize(screenWidth, (int) (230 * density)).centerCrop().into(bloggerCover);
        }

        authorRank.setText(String.valueOf(blogDetails.getAuthor_rank()));

        authorType.setText(blogDetails.getAuthor_type().toUpperCase());
        authorType.setBackgroundColor(Color.parseColor(blogDetails.getAuthor_color_code()));

        if (StringUtils.isNullOrEmpty(blogDetails.getAbout_user())) {
            aboutLayout.setVisibility(View.GONE);
        } else {
            aboutLayout.setVisibility(View.VISIBLE);
            description.setText(blogDetails.getAbout_user());
        }

        if (!StringUtils.isNullOrEmpty(autFollow)) {
            authorFollower.setVisibility(View.VISIBLE);
            authorFollower.setText(autFollow);
        }

        lastFollowStatus = blogDetails.getUser_following_status();

        if (!StringUtils.isNullOrEmpty(blogDetails.getUser_following_status())) {
            if (blogDetails.getUser_following_status().equalsIgnoreCase("0")) {
                ((TextView) findViewById(R.id.blog_follow)).setText("FOLLOW");
                isFollowing = false;
            } else {
                ((TextView) findViewById(R.id.blog_follow)).setText("UNFOLLOW");
                isFollowing = true;
            }
        }

    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(ExpendedBlogDescriptionActivity.this, BlogDetailActivity.class);
        if (!blogDetails.getUser_following_status().equalsIgnoreCase(lastFollowStatus)) {
            intent.putExtra(Constants.BLOG_STATUS, blogDetails.getUser_following_status());
            setResult(RESULT_OK, intent);
            finish();
        } else {
            finish();
        }
    }

}
