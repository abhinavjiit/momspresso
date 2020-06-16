package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.StringUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 2/8/17.
 */
public class ArticleModerationOrShareActivity extends BaseActivity implements View.OnClickListener {

    private String shareUrl;
    private RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_moderation_share_activity);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setActivity(this);

        shareUrl = getIntent().getStringExtra("shareUrl");

        LinearLayout moderationContainer = findViewById(R.id.moderationContainer);
        LinearLayout publishContainer = findViewById(R.id.publishContainer);
        ImageView facebookImageView = findViewById(R.id.facebookImageView);
        facebookImageView.setOnClickListener(this);
        ImageView whatsappImageView = findViewById(R.id.whatsappImageView);
        whatsappImageView.setOnClickListener(this);
        ImageView genericImageView = findViewById(R.id.genericImageView);
        genericImageView.setOnClickListener(this);
        TextView laterTextView = findViewById(R.id.laterTextView);
        laterTextView.setOnClickListener(this);
        TextView okayTextView = findViewById(R.id.okayTextView);
        okayTextView.setOnClickListener(this);

        if (StringUtils.isNullOrEmpty(shareUrl)) {
            moderationContainer.setVisibility(View.VISIBLE);
            publishContainer.setVisibility(View.GONE);
        } else {
            moderationContainer.setVisibility(View.GONE);
            publishContainer.setVisibility(View.VISIBLE);
        }
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
                    Utils.shareEventTracking(this, "Post creation",
                            "Share_Android", "PCA_Facebook_Share");
                    ShareLinkContent content = new ShareLinkContent.Builder().setContentUrl(Uri.parse(
                            AppUtils.getUtmParamsAppendedShareUrl(shareUrl, "PCA_Facebook_Share", "Share_Android")))
                            .build();
                    new ShareDialog(this).show(content);
                }
                break;
            case R.id.whatsappImageView:
                if (StringUtils.isNullOrEmpty(shareUrl)) {
                    Toast.makeText(this, getString(R.string.moderation_or_share_whatsapp_fail), Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Utils.shareEventTracking(this, "Post creation",
                            "Share_Android", "PCA_Whatsapp_Share");
                    Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                    whatsappIntent.setType("text/plain");
                    whatsappIntent.setPackage("com.whatsapp");
                    whatsappIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.check_out_blog) + AppUtils
                            .getUtmParamsAppendedShareUrl(shareUrl, "PCA_Whatsapp_Share", "Share_Android"));
                    try {
                        startActivity(whatsappIntent);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(this, getString(R.string.moderation_or_share_whatsapp_not_installed),
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.genericImageView:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.check_out_blog) + AppUtils
                        .getUtmParamsAppendedShareUrl(shareUrl, "PCA_Generic_Share", "Share_Android"));
                startActivity(Intent.createChooser(shareIntent, "Momspresso"));
                Utils.shareEventTracking(this, "Post creation", "Share_Android", "PCA_Generic_Share");
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
                intent.putExtra("showInviteDialog", true);
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
}
