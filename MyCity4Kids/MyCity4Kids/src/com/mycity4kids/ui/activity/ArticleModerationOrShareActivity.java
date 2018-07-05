package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.plus.PlusShare;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;
import com.mycity4kids.utils.AppUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

//import com.facebook.widget.FacebookDialog;

/**
 * Created by hemant on 2/8/17.
 */
public class ArticleModerationOrShareActivity extends BaseActivity implements View.OnClickListener {
    private String shareUrl;
    private String source;
    private String authorId, authorName;
    private String title, body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_moderation_share_activity);

        shareUrl = getIntent().getStringExtra("shareUrl");
        source = getIntent().getStringExtra("source");
        title = getIntent().getStringExtra("title");
        body = getIntent().getStringExtra("body");

        authorId = SharedPrefUtils.getUserDetailModel(this).getDynamoId();
        authorName = SharedPrefUtils.getUserDetailModel(this).getFirst_name() + " " + SharedPrefUtils.getUserDetailModel(this).getLast_name();

        LinearLayout moderationContainer = (LinearLayout) findViewById(R.id.moderationContainer);
        LinearLayout publishContainer = (LinearLayout) findViewById(R.id.publishContainer);

        ImageView fImageView = (ImageView) findViewById(R.id.facebookImageView);
        ImageView gImageView = (ImageView) findViewById(R.id.googlePlusImageView);
        ImageView whatsappImageView = (ImageView) findViewById(R.id.whatsappImageView);
        ImageView twitterImageView = (ImageView) findViewById(R.id.twitterImageView);
        ImageView instagramImageView = (ImageView) findViewById(R.id.instagramImageView);
        TextView laterTextView = (TextView) findViewById(R.id.laterTextView);
        TextView okayTextView = (TextView) findViewById(R.id.okayTextView);

        if (StringUtils.isNullOrEmpty(shareUrl)) {
            moderationContainer.setVisibility(View.VISIBLE);
            publishContainer.setVisibility(View.GONE);
        } else {
            moderationContainer.setVisibility(View.GONE);
            publishContainer.setVisibility(View.VISIBLE);
        }

        if ("addStory".equals(source)) {
            instagramImageView.setVisibility(View.VISIBLE);
        } else {
            instagramImageView.setVisibility(View.GONE);
        }

        fImageView.setOnClickListener(this);
        gImageView.setOnClickListener(this);
        whatsappImageView.setOnClickListener(this);
        twitterImageView.setOnClickListener(this);
        instagramImageView.setOnClickListener(this);
        laterTextView.setOnClickListener(this);
        okayTextView.setOnClickListener(this);
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.facebookImageView:
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    if ("addStory".equals(source)) {
                        Utils.pushShareStoryEvent(this, "PublishSuccessScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "", shareUrl, authorId + "~" + authorName, "Facebook");
                    } else {
                        Utils.pushShareArticleEvent(this, "PublishSuccessScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "", shareUrl, authorId + "~" + authorName, "Facebook");
                    }

                    ShareLinkContent content = new ShareLinkContent.Builder()
                            .setContentUrl(Uri.parse(shareUrl))
                            .build();
                    new ShareDialog(this).show(content);
                }
                break;
            case R.id.googlePlusImageView:
                if (StringUtils.isNullOrEmpty(shareUrl)) {
                    Toast.makeText(this, getString(R.string.moderation_or_share_gplus_fail), Toast.LENGTH_SHORT).show();
                } else {
                    if ("addStory".equals(source)) {
                        Utils.pushShareStoryEvent(this, "PublishSuccessScreen", authorId + "", shareUrl, authorId + "~" + authorName, "GPlus");
                    } else {
                        Utils.pushShareArticleEvent(this, "PublishSuccessScreen", authorId + "", shareUrl, authorId + "~" + authorName, "GPlus");
                    }

                    Intent shareIntent = new PlusShare.Builder(this)
                            .setType("text/plain")
                            .setText(getString(R.string.check_out_blog))
                            .setContentUrl(Uri.parse(shareUrl))
                            .getIntent();
                    startActivityForResult(shareIntent, 0);
                }
                break;
            case R.id.whatsappImageView:
                if (StringUtils.isNullOrEmpty(shareUrl)) {
                    Toast.makeText(this, getString(R.string.moderation_or_share_whatsapp_fail), Toast.LENGTH_SHORT).show();
                } else {
                    if ("addStory".equals(source)) {
//                        Utils.pushShareStoryEvent(this, "PublishSuccessScreen", authorId + "", shareUrl, authorId + "~" + authorName, "Whatsapp");
                        AppUtils.shareStoryWithWhatsApp(ArticleModerationOrShareActivity.this, shareUrl, "PublishSuccessScreen", authorId,
                                shareUrl, authorId, authorName);
                    } else {
                        Utils.pushShareArticleEvent(this, "PublishSuccessScreen", authorId + "", shareUrl, authorId + "~" + authorName, "Whatsapp");
                        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                        whatsappIntent.setType("text/plain");
                        whatsappIntent.setPackage("com.whatsapp");
                        whatsappIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.check_out_blog) + shareUrl);
                        try {
                            startActivity(whatsappIntent);
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(this, getString(R.string.moderation_or_share_whatsapp_not_installed), Toast.LENGTH_SHORT).show();
                        }
                    }


                }
                break;
            case R.id.instagramImageView:
                try {
                    AppUtils.drawMultilineTextToBitmap(title, body, authorName);
                } catch (Exception e) {
                    Crashlytics.logException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                    return;
                }
                AppUtils.shareStoryWithInstagram(this, "PublishSuccessScreen", authorId, shareUrl, authorId, authorName);
                break;
            case R.id.twitterImageView:
                if (StringUtils.isNullOrEmpty(shareUrl)) {
                    Toast.makeText(this, getString(R.string.moderation_or_share_twitter_fail), Toast.LENGTH_SHORT).show();
                } else {
                    if ("addStory".equals(source)) {
                        Utils.pushShareStoryEvent(this, "PublishSuccessScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "", shareUrl, authorId + "~" + authorName, "Twitter");
                    } else {
                        Utils.pushShareArticleEvent(this, "PublishSuccessScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "", shareUrl, authorId + "~" + authorName, "Twitter");
                    }

                    // Create intent using ACTION_VIEW and a normal Twitter url:
                    String tweetUrl = String.format("https://twitter.com/intent/tweet?text=%s&url=%s",
                            urlEncode(getString(R.string.check_out_blog)),
                            urlEncode(shareUrl));
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));
                    // Narrow down to official Twitter app, if available:
                    List<ResolveInfo> matches = this.getPackageManager().queryIntentActivities(intent, 0);
                    for (ResolveInfo info : matches) {
                        if (info.activityInfo.packageName.toLowerCase().startsWith("com.twitter")) {
                            intent.setPackage(info.activityInfo.packageName);
                        }
                    }
                    startActivity(intent);
                }
                break;
            case R.id.laterTextView: {
                Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                BloggerDashboardAPI bloggerDashboardAPI = retrofit.create(BloggerDashboardAPI.class);
                Call<UserDetailResponse> call = bloggerDashboardAPI.getBloggerData(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
                call.enqueue(getUserDetailsResponseCallback);
            }
            break;
            case R.id.okayTextView: {
                Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                BloggerDashboardAPI bloggerDashboardAPI = retrofit.create(BloggerDashboardAPI.class);
                Call<UserDetailResponse> call = bloggerDashboardAPI.getBloggerData(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
                call.enqueue(getUserDetailsResponseCallback);
            }
            break;
        }
    }

    private Callback<UserDetailResponse> getUserDetailsResponseCallback = new Callback<UserDetailResponse>() {
        @Override
        public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
                return;
            }

            UserDetailResponse responseData = response.body();
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                if (responseData.getData().get(0).getResult().getKids() == null || responseData.getData().get(0).getResult().getKids().size() == 0) {
                    Intent intent = new Intent(ArticleModerationOrShareActivity.this, CompleteBloggerProfileActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(ArticleModerationOrShareActivity.this, DashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            } else {
                showToast("" + responseData.getReason());
            }
        }

        @Override
        public void onFailure(Call<UserDetailResponse> call, Throwable t) {
//            noDataFoundTextView.setVisibility(View.VISIBLE);
            removeProgressDialog();
//            showToast(getString(R.string.server_went_wrong));
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };


    private static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.d("UnsupEncodinException", "UTF-8 should always be supported");
            throw new RuntimeException("URLEncoder.encode() failed for " + s);
        }
    }
}
