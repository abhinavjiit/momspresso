package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.response.GroupsMembershipResponse;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;
import com.mycity4kids.ui.GroupMembershipStatus;
import com.mycity4kids.ui.GroupMembershipStatus.IMembershipStatus;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.widget.MomspressoButtonWidget;
import com.mycity4kids.widget.ShareButtonWidget;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 2/8/17.
 */
public class ArticleModerationOrShareActivity extends BaseActivity implements View.OnClickListener, IMembershipStatus {

    private String shareUrl;
    private RelativeLayout root;
    private TextView createMoreHeaderTextViewModeration;
    private MomspressoButtonWidget createMoreButtonModeration;
    private TextView createMoreHeaderTextView;
    private MomspressoButtonWidget createMoreButton;
    private int groupId;
    private ConstraintLayout youAreDoneView;
    private MomspressoButtonWidget gotoYourBlog;
    private ImageView headerImageView;
    private ImageView back;
    private TextView moderationGuideLines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_moderation_share_activity);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setActivity(this);
        shareUrl = getIntent().getStringExtra("shareUrl");
        createMoreHeaderTextViewModeration = findViewById(R.id.createMoreHeaderTextViewModeration);
        createMoreButtonModeration = findViewById(R.id.createMoreButtonModeration);
        createMoreHeaderTextView = findViewById(R.id.createMoreHeaderTextView);
        createMoreButton = findViewById(R.id.createMoreButton);
        youAreDoneView = findViewById(R.id.youAreDoneView);
        headerImageView = findViewById(R.id.headerImageView);
        moderationGuideLines = findViewById(R.id.moderationGuideLines);
        back = findViewById(R.id.back);
        ImageView cancelImageModeration = findViewById(R.id.cancelImageModeration);
        gotoYourBlog = findViewById(R.id.gotoYourBlog);
        TextView wayToGoTextView = findViewById(R.id.wayToGoTextView);
        LinearLayout moderationContainer = findViewById(R.id.moderationContainer);
        LinearLayout publishContainer = findViewById(R.id.publishContainer);
        ShareButtonWidget facebookImageView = findViewById(R.id.facebookImageView);
        facebookImageView.setOnClickListener(this);
        ShareButtonWidget whatsappImageView = findViewById(R.id.whatsappImageView);
        whatsappImageView.setOnClickListener(this);
        ShareButtonWidget genericImageView = findViewById(R.id.genericImageView);
        genericImageView.setOnClickListener(this);
        TextView laterTextView = findViewById(R.id.laterTextView);
        laterTextView.setOnClickListener(this);
        TextView okayTextView = findViewById(R.id.okayTextView);
        okayTextView.setOnClickListener(this);
        gotoYourBlog.setOnClickListener(this);
        checkCreatorGroupStatus();
        if (StringUtils.isNullOrEmpty(shareUrl)) {
            wayToGoTextView.setText("WAY TO GO!");
            headerImageView.setImageResource(R.drawable.ic_moderation);
            moderationContainer.setVisibility(View.VISIBLE);
            publishContainer.setVisibility(View.GONE);
        } else {
            headerImageView.setImageResource(R.drawable.ic_live_content);
            wayToGoTextView.setText("Congratulation! Your Article is live now");
            moderationContainer.setVisibility(View.GONE);
            publishContainer.setVisibility(View.VISIBLE);
        }
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (youAreDoneView.getVisibility() == View.VISIBLE) {
                    youAreDoneView.setVisibility(View.GONE);
                }
            }
        }, 3000);
        createMoreButton.setOnClickListener(this);
        createMoreButtonModeration.setOnClickListener(this);
        cancelImageModeration.setOnClickListener(this);
        back.setOnClickListener(this);
        moderationGuideLines.setOnClickListener(this);
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
            case R.id.gotoYourBlog: {
                Utils.shareEventTracking(this, "Post creation", "Create_Android", "PCA_View_blog");
                handleDeeplinks(shareUrl);
                break;
            }
            case R.id.moderationGuideLines: {
                Utils.shareEventTracking(this, "Post creation", "Create_Android", "PCA_Moderation_Guide");
                handleDeeplinks("https://www.momspresso.com/moderation-rules");
                break;
            }
            case R.id.back: {
                launchHome();
                break;
            }
            case R.id.cancelImageModeration: {
                youAreDoneView.setVisibility(View.INVISIBLE);
                break;
            }
            case R.id.createMoreButton: {
                if (createMoreButton.getTag() == "already_join") {
                    Utils.shareEventTracking(this, "Post creation", "Create_Android", "PCS_Create_AM");
                    Intent articleCreationIntent = new Intent(this, ArticleChallengeOrTopicSelectionActivity.class);
                    articleCreationIntent.setFlags(
                            (Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    startActivity(articleCreationIntent);
                } else {
                    Utils.shareEventTracking(this, "Post creation", "Create_Android", "PCA_Support_Group_AM");
                    Intent intent = new Intent(this, GroupsSummaryActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("groupId", groupId);
                    intent.putExtra("comingFrom", "story");
                    startActivity(intent);
                }
                break;
            }
            case R.id.createMoreButtonModeration: {
                if (createMoreButtonModeration.getTag() == "already_join") {
                    Utils.shareEventTracking(this, "Post creation", "Create_Android", "PCS_Suppport_Group_M");
                    Intent articleCreationIntent = new Intent(this, ArticleChallengeOrTopicSelectionActivity.class);
                    articleCreationIntent.setFlags(
                            (Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    startActivity(articleCreationIntent);
                } else {
                    Utils.shareEventTracking(this, "Post creation", "Create_Android", "PCA_Suppport_Group_M");
                    Intent intent = new Intent(this, GroupsSummaryActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("groupId", groupId);
                    intent.putExtra("comingFrom", "story");
                    startActivity(intent);
                }
                break;
            }
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
                launchHome();
                break;
            default:
                break;
        }
    }

    private void launchHome() {
        Intent intent = new Intent(ArticleModerationOrShareActivity.this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("showInviteDialog", true);
        intent.putExtra("source", AppConstants.CONTENT_TYPE_ARTICLE);
        startActivity(intent);
        finish();
    }

    private void checkCreatorGroupStatus() {
        GroupMembershipStatus groupMembershipStatus = new GroupMembershipStatus(this);
        groupId = AppUtils.getCreatorGroupIdForLanguage();
        groupMembershipStatus.checkMembershipStatus(groupId,
                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
    }

    @Override
    public void onMembershipStatusFetchSuccess(GroupsMembershipResponse body, int groupId) {
        if (StringUtils.isNullOrEmpty(shareUrl)) {
            createMoreButtonModeration.setVisibility(View.VISIBLE);
            createMoreHeaderTextViewModeration.setVisibility(View.VISIBLE);
            if (body.getData().getResult() == null || body.getData().getResult().isEmpty() || body.getData().getResult()
                    .get(0).getStatus().equals("2")) {
                createMoreButtonModeration.setText(getString(R.string.join_creator_group));
                createMoreHeaderTextViewModeration.setText(getString(R.string.get_tips_ideas));
                createMoreButtonModeration.setTag("please_join");
            } else {
                createMoreButtonModeration.setText(getString(R.string.create_more));
                createMoreHeaderTextViewModeration.setText(getString(R.string.dont_stop_magic));
                createMoreButtonModeration.setTag("already_join");
            }
        } else {
            createMoreButton.setVisibility(View.VISIBLE);
            createMoreHeaderTextView.setVisibility(View.VISIBLE);
            if (body.getData().getResult() == null || body.getData().getResult().isEmpty() || body.getData().getResult()
                    .get(0).getStatus().equals("2")) {
                createMoreButton.setText(getString(R.string.join_creator_group));
                createMoreHeaderTextView.setText(getString(R.string.get_tips_ideas));
                createMoreButton.setTag("please_join");
            } else {
                createMoreButton.setText(getString(R.string.create_more));
                createMoreHeaderTextView.setText(getString(R.string.dont_stop_magic));
                createMoreButton.setTag("already_join");
            }
        }
    }

    @Override
    public void onMembershipStatusFetchFail() {
        if (StringUtils.isNullOrEmpty(shareUrl)) {
            createMoreButtonModeration.setVisibility(View.VISIBLE);
            createMoreHeaderTextViewModeration.setVisibility(View.VISIBLE);
            createMoreButtonModeration.setText(getString(R.string.join_creator_group));
            createMoreHeaderTextViewModeration.setText(getString(R.string.get_tips_ideas));
            createMoreButtonModeration.setTag("please_join");
        } else {
            createMoreButton.setVisibility(View.VISIBLE);
            createMoreHeaderTextView.setVisibility(View.VISIBLE);
            createMoreButton.setText(getString(R.string.join_creator_group));
            createMoreHeaderTextView.setText(getString(R.string.get_tips_ideas));
            createMoreButton.setTag("please_join");
        }
    }
}
