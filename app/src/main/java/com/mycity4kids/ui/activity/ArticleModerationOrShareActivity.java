package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.FragmentManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;
import com.mycity4kids.ui.fragment.InviteFriendsDialogFragment;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.StringUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 2/8/17.
 */
public class ArticleModerationOrShareActivity extends BaseActivity implements View.OnClickListener {

    private String shareUrl;
    private String authorId;
    private String authorName;
    private RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_moderation_share_activity);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setActivity(this);

        shareUrl = getIntent().getStringExtra("shareUrl");
        authorId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId();
        authorName = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getFirst_name()
                + " "
                + SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getLast_name();

        LinearLayout moderationContainer = (LinearLayout) findViewById(R.id.moderationContainer);
        LinearLayout publishContainer = (LinearLayout) findViewById(R.id.publishContainer);
        ImageView facebookImageView = (ImageView) findViewById(R.id.facebookImageView);
        facebookImageView.setOnClickListener(this);
        ImageView whatsappImageView = (ImageView) findViewById(R.id.whatsappImageView);
        whatsappImageView.setOnClickListener(this);
        ImageView twitterImageView = (ImageView) findViewById(R.id.twitterImageView);
        twitterImageView.setOnClickListener(this);
        ImageView instagramImageView = (ImageView) findViewById(R.id.instagramImageView);
        instagramImageView.setOnClickListener(this);
        TextView laterTextView = (TextView) findViewById(R.id.laterTextView);
        laterTextView.setOnClickListener(this);
        TextView okayTextView = (TextView) findViewById(R.id.okayTextView);
        okayTextView.setOnClickListener(this);

        if (StringUtils.isNullOrEmpty(shareUrl)) {
            moderationContainer.setVisibility(View.VISIBLE);
            publishContainer.setVisibility(View.GONE);
        } else {
            moderationContainer.setVisibility(View.GONE);
            publishContainer.setVisibility(View.VISIBLE);
            launchInviteFriendsDialog();
        }
        instagramImageView.setVisibility(View.GONE);
    }

    private void launchInviteFriendsDialog() {
        InviteFriendsDialogFragment inviteFriendsDialogFragment = new InviteFriendsDialogFragment();
        Bundle args = new Bundle();
        inviteFriendsDialogFragment.setArguments(args);
        inviteFriendsDialogFragment.setCancelable(true);
        FragmentManager fm = getSupportFragmentManager();
        inviteFriendsDialogFragment.show(fm, "Invite Friends");
        Utils.pushGenericEvent(this, "Show_InvitePopup_PostBlogCreation",
                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                "ArticleModerationOrShareActivity");
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((BaseApplication) getApplication()).setView(root);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.facebookImageView:
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    Utils.pushShareArticleEvent(this, "PublishSuccessScreen",
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId() + "",
                            shareUrl, authorId + "~" + authorName, "Facebook");
                    ShareLinkContent content = new ShareLinkContent.Builder()
                            .setContentUrl(Uri.parse(shareUrl))
                            .build();
                    new ShareDialog(this).show(content);
                }
                break;
            case R.id.whatsappImageView:
                if (StringUtils.isNullOrEmpty(shareUrl)) {
                    Toast.makeText(this, getString(R.string.moderation_or_share_whatsapp_fail), Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Utils.pushShareArticleEvent(this, "PublishSuccessScreen", authorId + "", shareUrl,
                            authorId + "~" + authorName, "Whatsapp");
                    Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                    whatsappIntent.setType("text/plain");
                    whatsappIntent.setPackage("com.whatsapp");
                    whatsappIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.check_out_blog) + shareUrl);
                    try {
                        startActivity(whatsappIntent);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(this, getString(R.string.moderation_or_share_whatsapp_not_installed),
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.twitterImageView:
                if (StringUtils.isNullOrEmpty(shareUrl)) {
                    Toast.makeText(this, getString(R.string.moderation_or_share_twitter_fail), Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Utils.pushShareArticleEvent(this, "PublishSuccessScreen",
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId() + "",
                            shareUrl, authorId + "~" + authorName, "Twitter");
                    String tweetUrl = String.format("https://twitter.com/intent/tweet?text=%s&url=%s",
                            urlEncode(getString(R.string.check_out_blog)),
                            urlEncode(shareUrl));
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));
                    List<ResolveInfo> matches = this.getPackageManager().queryIntentActivities(intent, 0);
                    for (ResolveInfo info : matches) {
                        if (info.activityInfo.packageName.toLowerCase().startsWith("com.twitter")) {
                            intent.setPackage(info.activityInfo.packageName);
                        }
                    }
                    startActivity(intent);
                }
                break;
            case R.id.laterTextView:
            case R.id.okayTextView:
                Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                BloggerDashboardAPI bloggerDashboardApi = retrofit.create(BloggerDashboardAPI.class);
                Call<UserDetailResponse> call = bloggerDashboardApi.getBloggerData(
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                call.enqueue(getUserDetailsResponseCallback);
                break;
            default:
                break;
        }
    }

    public void shareProfileUrl() {
        String shareText = getString(
                R.string.profile_follow_author,
                authorName,
                (AppConstants.USER_PROFILE_SHARE_BASE_URL + authorId));
        AppUtils.shareGenericLinkWithSuccessStatus(this, shareText);
    }

    private Callback<UserDetailResponse> getUserDetailsResponseCallback = new Callback<UserDetailResponse>() {
        @Override
        public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
            removeProgressDialog();
            if (response.body() == null) {
                return;
            }

            UserDetailResponse responseData = response.body();
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                Intent intent = new Intent(ArticleModerationOrShareActivity.this, DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                showToast("" + responseData.getReason());
            }
        }

        @Override
        public void onFailure(Call<UserDetailResponse> call, Throwable t) {
            removeProgressDialog();
            FirebaseCrashlytics.getInstance().recordException(t);
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
