package com.mycity4kids.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.LogoutController;
import com.mycity4kids.dbtable.ExternalCalendarTable;
import com.mycity4kids.dbtable.TableAdult;
import com.mycity4kids.dbtable.TableApiEvents;
import com.mycity4kids.dbtable.TableAppointmentData;
import com.mycity4kids.dbtable.TableAttendee;
import com.mycity4kids.dbtable.TableFamily;
import com.mycity4kids.dbtable.TableFile;
import com.mycity4kids.dbtable.TableKids;
import com.mycity4kids.dbtable.TableNotes;
import com.mycity4kids.dbtable.TableTaskData;
import com.mycity4kids.dbtable.TableTaskList;
import com.mycity4kids.dbtable.TableWhoToRemind;
import com.mycity4kids.dbtable.TaskCompletedTable;
import com.mycity4kids.dbtable.TaskTableAttendee;
import com.mycity4kids.dbtable.TaskTableFile;
import com.mycity4kids.dbtable.TaskTableNotes;
import com.mycity4kids.dbtable.TaskTableWhoToRemind;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.facebook.FacebookUtils;
import com.mycity4kids.models.campaignmodels.TotalPayoutResponse;
import com.mycity4kids.models.logout.LogoutResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.CampaignAPI;
import com.mycity4kids.ui.rewards.activity.RewardsContainerActivity;
import com.mycity4kids.utils.AppUtils;

import org.apache.commons.lang.WordUtils;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class ProfileSetting extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private ImageView backImageView;
    private TextView personal_info, mymoney_info, payment_details, social_accounts, notification_settings, topic_of_interest, help,
            report_spam, about, app_version;
    private LinearLayout logout_layout;
    private GoogleApiClient mGoogleApiClient;
    private int totalPayout = 0;
    private TextView activityTextView, readArticlesTextView;
    private String isRewardAdded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);
        backImageView = findViewById(R.id.backImageView);
        personal_info = findViewById(R.id.personal_info);
        mymoney_info = findViewById(R.id.mymoney_info);
        payment_details = findViewById(R.id.payment_details);
        social_accounts = findViewById(R.id.social_accounts);
        notification_settings = findViewById(R.id.notification_settings);
        topic_of_interest = findViewById(R.id.topic_of_interest);
        help = findViewById(R.id.help);
        report_spam = findViewById(R.id.report_spam);
        about = findViewById(R.id.about);
        app_version = findViewById(R.id.app_version);
        logout_layout = findViewById(R.id.logout_layout);
        activityTextView = findViewById(R.id.activityTextView);
        readArticlesTextView = findViewById(R.id.readArticlesTextView);

        if (getIntent().getExtras().containsKey("isRewardAdded")) {
            isRewardAdded = getIntent().getStringExtra("isRewardAdded");
        }
        fetchTotalEarning();
        app_version.setText(getResources().getString(R.string.app_version) + " " + AppUtils.getAppVersion(BaseApplication.getAppContext()));

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        try {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        } catch (Exception e) {

        }

        activityTextView.setText(WordUtils.capitalizeFully(getString(R.string.myprofile_section_activity_label)));
        readArticlesTextView.setText(WordUtils.capitalizeFully(getString(R.string.read_articles)));
        backImageView.setOnClickListener(this);
        personal_info.setOnClickListener(this);
        mymoney_info.setOnClickListener(this);
        payment_details.setOnClickListener(this);
        social_accounts.setOnClickListener(this);
        notification_settings.setOnClickListener(this);
        topic_of_interest.setOnClickListener(this);
        help.setOnClickListener(this);
        report_spam.setOnClickListener(this);
        about.setOnClickListener(this);
        logout_layout.setOnClickListener(this);
        activityTextView.setOnClickListener(this);
        readArticlesTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backImageView:
                onBackPressed();
                break;
            case R.id.personal_info:
                Intent personalIntent = new Intent(this, RewardsContainerActivity.class);
                personalIntent.putExtra("pageLimit", 1);
                personalIntent.putExtra("pageNumber", 1);
                startActivity(personalIntent);
                break;
            case R.id.mymoney_info:
                Intent intent = new Intent(this, EditProfileNewActivity.class);
                intent.putExtra("isComingfromCampaign", true);
                intent.putExtra("isRewardAdded", "1");
                startActivity(intent);
                break;
            case R.id.payment_details:
                Intent paymentIntent = new Intent(this, RewardsContainerActivity.class);
                paymentIntent.putExtra("pageNumber", 4);
                paymentIntent.putExtra("pageLimit", 4);
                startActivity(paymentIntent);
                break;
            case R.id.social_accounts:
                Intent socialIntent = new Intent(this, RewardsContainerActivity.class);
                socialIntent.putExtra("pageNumber", 3);
                socialIntent.putExtra("pageLimit", 3);
                startActivity(socialIntent);
                break;
            case R.id.notification_settings:
                Intent notificationIntent = new Intent(this, NotificationProfileSetting.class);
                notificationIntent.putExtra("source", "settings");
                startActivity(notificationIntent);
                break;
            case R.id.topic_of_interest:
                Intent subscribeTopicIntent = new Intent(this, SubscribeTopicsActivity.class);
                subscribeTopicIntent.putExtra("source", "settings");
                startActivity(subscribeTopicIntent);
                break;
            case R.id.help:
                onBackPressed();
                break;
            case R.id.report_spam:
                Intent spamIntent = new Intent(this, ReportSpamActivity.class);
                startActivity(spamIntent);
                break;
            case R.id.about:
                Intent intent1 = new Intent(this, LoadWebViewActivity.class);
                intent1.putExtra(Constants.WEB_VIEW_URL, "https://www.momspresso.com");
                startActivity(intent1);
                break;
            case R.id.logout_layout:
                logoutUser();
                break;

            case R.id.readArticlesTextView:
                Intent readArticleIntent = new Intent(this, UserReadArticlesContentActivity.class);
                readArticleIntent.putExtra("isPrivateProfile", true);
                readArticleIntent.putExtra(Constants.AUTHOR_ID, SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                startActivity(readArticleIntent);
                break;

            case R.id.activityTextView:
                Intent intent5 = new Intent(this, UserActivitiesActivity.class);
                intent5.putExtra(Constants.AUTHOR_ID, SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                startActivity(intent5);
                break;
        }
    }

    private void logoutUser() {
        if (ConnectivityUtils.isNetworkEnabled(this)) {
            final LogoutController _controller = new LogoutController(this, this);
            AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);

            dialog.setMessage(getResources().getString(R.string.logout_msg)).setNegativeButton(R.string.new_yes
                    , new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            showProgressDialog(getResources().getString(R.string.please_wait));
                            _controller.getData(AppConstants.LOGOUT_REQUEST, "");
                        }
                    }).setPositiveButton(R.string.new_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // do nothing
                    dialog.cancel();
                }
            }).setIcon(android.R.drawable.ic_dialog_alert);
            AlertDialog alert11 = dialog.create();
            alert11.show();
            alert11.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.app_red));
            alert11.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.canceltxt_color));
        } else {
            ToastUtils.showToast(this, getString(R.string.error_network));
        }
    }

    @Override
    protected void updateUi(Response response) {
        removeProgressDialog();
        if (response == null) {
            Toast.makeText(this, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            return;
        }
        LogoutResponse responseData = (LogoutResponse) response.getResponseObject();
        String message = responseData.getResult().getMessage();
        if (responseData.getResponseCode() == 200) {
            MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                mixpanel.track("UserLogout", jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }

            FacebookUtils.logout(this);
            gPlusSignOut();

            String pushToken = SharedPrefUtils.getDeviceToken(BaseApplication.getAppContext());
            boolean homeCoach = SharedPrefUtils.isCoachmarksShownFlag(BaseApplication.getAppContext(), "home");
            boolean topicsCoach = SharedPrefUtils.isCoachmarksShownFlag(BaseApplication.getAppContext(), "topics");
            boolean topicsArticleCoach = SharedPrefUtils.isCoachmarksShownFlag(BaseApplication.getAppContext(), "topics_article");
            boolean articleCoach = SharedPrefUtils.isCoachmarksShownFlag(BaseApplication.getAppContext(), "article_details");
            boolean groupsCoach = SharedPrefUtils.isCoachmarksShownFlag(BaseApplication.getAppContext(), "groups");
            String appLocale = SharedPrefUtils.getAppLocale(BaseApplication.getAppContext());

            SharedPrefUtils.clearPrefrence(BaseApplication.getAppContext());
            SharedPrefUtils.setDeviceToken(BaseApplication.getAppContext(), pushToken);
            SharedPrefUtils.setCoachmarksShownFlag(BaseApplication.getAppContext(), "home", homeCoach);
            SharedPrefUtils.setCoachmarksShownFlag(BaseApplication.getAppContext(), "topics", topicsCoach);
            SharedPrefUtils.setCoachmarksShownFlag(BaseApplication.getAppContext(), "topics_article", topicsArticleCoach);
            SharedPrefUtils.setCoachmarksShownFlag(BaseApplication.getAppContext(), "article_details", articleCoach);
            SharedPrefUtils.setCoachmarksShownFlag(BaseApplication.getAppContext(), "groups", groupsCoach);
            SharedPrefUtils.setAppLocale(BaseApplication.getAppContext(), appLocale);
            /**
             * delete table from local also;
             */
            UserTable _tables = new UserTable((BaseApplication) this.getApplicationContext());
            _tables.deleteAll();

            TableFamily _familytables = new TableFamily((BaseApplication) this.getApplicationContext());
            _familytables.deleteAll();

            TableAdult _adulttables = new TableAdult((BaseApplication) this.getApplicationContext());
            _adulttables.deleteAll();

            TableKids _kidtables = new TableKids((BaseApplication) this.getApplicationContext());
            _kidtables.deleteAll();

            new TableAppointmentData(BaseApplication.getInstance()).deleteAll();
            new TableNotes(BaseApplication.getInstance()).deleteAll();
            new TableFile(BaseApplication.getInstance()).deleteAll();
            new TableAttendee(BaseApplication.getInstance()).deleteAll();
            new TableWhoToRemind(BaseApplication.getInstance()).deleteAll();


            new TableTaskData(BaseApplication.getInstance()).deleteAll();
            new TableTaskList(BaseApplication.getInstance()).deleteAll();
            new TaskTableAttendee(BaseApplication.getInstance()).deleteAll();
            new TaskTableWhoToRemind(BaseApplication.getInstance()).deleteAll();
            new TaskTableFile(BaseApplication.getInstance()).deleteAll();
            new TaskTableNotes(BaseApplication.getInstance()).deleteAll();
            new TaskCompletedTable(BaseApplication.getInstance()).deleteAll();
            new TableApiEvents(BaseApplication.getInstance()).deleteAll();

            new ExternalCalendarTable(BaseApplication.getInstance()).deleteAll();

            // clear cachee
            BaseApplication.setBlogResponse(null);
            BaseApplication.setBusinessREsponse(null);
            BaseApplication.getInstance().setBranchData(null);
            BaseApplication.getInstance().setBranchLink(null);

            // clear all sessions

            if (StringUtils.isNullOrEmpty(message)) {
                Toast.makeText(this, getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }

            // set logout flag
            SharedPrefUtils.setLogoutFlag(BaseApplication.getAppContext(), true);
            Intent intent = new Intent(this, ActivityLogin.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            this.finish();

        } else if (responseData.getResponseCode() == 400) {
            if (StringUtils.isNullOrEmpty(message)) {
                Toast.makeText(this, getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void gPlusSignOut() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage(this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void fetchTotalEarning() {
        showProgressDialog(getResources().getString(R.string.please_wait));
        String userId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId();
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        CampaignAPI campaignAPI = retrofit.create(CampaignAPI.class);
        Call<TotalPayoutResponse> call = campaignAPI.getTotalPayout(userId);
        call.enqueue(getTotalPayout);
    }

    Callback<TotalPayoutResponse> getTotalPayout = new Callback<TotalPayoutResponse>() {
        @Override
        public void onResponse(Call<TotalPayoutResponse> call, retrofit2.Response<TotalPayoutResponse> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
                showToast(getString(R.string.went_wrong));
                return;
            }
            try {
                TotalPayoutResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    if (responseData.getData().size() > 0) {
                        totalPayout = responseData.getData().get(0).getResult().get(0).getTotal_payout();
                    }
                }
            } catch (Exception e) {
                showToast(getString(R.string.server_went_wrong));
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<TotalPayoutResponse> call, Throwable t) {
            showToast(getString(R.string.server_went_wrong));
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };
}
