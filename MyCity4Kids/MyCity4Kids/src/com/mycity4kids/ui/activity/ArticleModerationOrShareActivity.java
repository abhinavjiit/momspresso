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
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_moderation_share_activity);

        shareUrl = getIntent().getStringExtra("shareUrl");

        LinearLayout moderationContainer = (LinearLayout) findViewById(R.id.moderationContainer);
        LinearLayout publishContainer = (LinearLayout) findViewById(R.id.publishContainer);

        ImageView fImageView = (ImageView) findViewById(R.id.facebookImageView);
        ImageView gImageView = (ImageView) findViewById(R.id.googlePlusImageView);
        ImageView whatsappImageView = (ImageView) findViewById(R.id.whatsappImageView);
        ImageView twitterImageView = (ImageView) findViewById(R.id.twitterImageView);
        TextView laterTextView = (TextView) findViewById(R.id.laterTextView);
        TextView okayTextView = (TextView) findViewById(R.id.okayTextView);

        if (StringUtils.isNullOrEmpty(shareUrl)) {
            moderationContainer.setVisibility(View.VISIBLE);
            publishContainer.setVisibility(View.GONE);
        } else {
            moderationContainer.setVisibility(View.GONE);
            publishContainer.setVisibility(View.VISIBLE);
        }

        fImageView.setOnClickListener(this);
        gImageView.setOnClickListener(this);
        whatsappImageView.setOnClickListener(this);
        twitterImageView.setOnClickListener(this);
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
                    ShareLinkContent content = new ShareLinkContent.Builder()
                            .setContentUrl(Uri.parse(shareUrl))
                            .build();
                    new ShareDialog(this).show(content);
                }

//                if (FacebookDialog.canPresentShareDialog(this, FacebookDialog.ShareDialogFeature.SHARE_DIALOG) && !StringUtils.isNullOrEmpty(shareUrl)) {
//                    FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(
//                            this).setName("mycity4kids")
//                            .setDescription("Check out this interesting blog post")
//                            .setLink(shareUrl).build();
//                    shareDialog.present();
//                } else {
//                    Toast.makeText(this, "Unable to share with facebook.", Toast.LENGTH_SHORT).show();
//                }
                break;
            case R.id.googlePlusImageView:
                if (StringUtils.isNullOrEmpty(shareUrl)) {
                    Toast.makeText(this, "Unable to share with google plus.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent shareIntent = new PlusShare.Builder(this)
                            .setType("text/plain")
                            .setText("mycity4kids\n" +
                                    "\n" +
                                    "Check out this interesting blog post ")
                            .setContentUrl(Uri.parse(shareUrl))
                            .getIntent();
                    startActivityForResult(shareIntent, 0);
                }
                break;
            case R.id.whatsappImageView:
                if (StringUtils.isNullOrEmpty(shareUrl)) {
                    Toast.makeText(this, "Unable to share with whatsapp.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                    whatsappIntent.setType("text/plain");
                    whatsappIntent.setPackage("com.whatsapp");
                    whatsappIntent.putExtra(Intent.EXTRA_TEXT, "mycity4kids\n\nCheck out this interesting blog post\n " + shareUrl);
                    try {
                        startActivity(whatsappIntent);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(this, "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.twitterImageView:
                if (StringUtils.isNullOrEmpty(shareUrl)) {
                    Toast.makeText(this, "Unable to share with twitter.", Toast.LENGTH_SHORT).show();
                } else {
                    // Create intent using ACTION_VIEW and a normal Twitter url:
                    String tweetUrl = String.format("https://twitter.com/intent/tweet?text=%s&url=%s",
                            urlEncode("mycity4kids\n\nCheck out this interesting blog post\n "),
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
