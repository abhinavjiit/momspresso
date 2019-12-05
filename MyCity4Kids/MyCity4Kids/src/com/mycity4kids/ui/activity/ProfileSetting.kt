package com.mycity4kids.ui.activity

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.browser.customtabs.CustomTabsIntent
import com.crashlytics.android.Crashlytics
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseActivity
import com.kelltontech.utils.ConnectivityUtils
import com.kelltontech.utils.StringUtils
import com.kelltontech.utils.ToastUtils
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.constants.Constants
import com.mycity4kids.controller.LogoutController
import com.mycity4kids.dbtable.*
import com.mycity4kids.facebook.FacebookUtils
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.models.campaignmodels.TotalPayoutResponse
import com.mycity4kids.models.logout.LogoutResponse
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.CampaignAPI
import com.mycity4kids.ui.rewards.activity.RewardsContainerActivity
import com.mycity4kids.utils.AppUtils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback

class ProfileSetting : BaseActivity(), GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private var personal_info: TextView? = null
    private var payment_details: TextView? = null
    private var social_accounts: TextView? = null
    private var help: TextView? = null
    private var appSettingsTextView: TextView? = null
    private var report_spam: TextView? = null
    private var about: TextView? = null
    private var app_version: TextView? = null
    private var myMoneyContainer: TextView? = null
    private var logout_layout: LinearLayout? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private var totalPayout = 0
    private var activityTextView: TextView? = null
    private var readArticlesTextView: TextView? = null
    private var isRewardAdded: String? = null
    private var toolbar: Toolbar? = null

    internal var getTotalPayout: Callback<TotalPayoutResponse> = object : Callback<TotalPayoutResponse> {
        override fun onResponse(call: Call<TotalPayoutResponse>, response: retrofit2.Response<TotalPayoutResponse>) {
            removeProgressDialog()
            if (response == null || response.body() == null) {
                showToast(getString(R.string.went_wrong))
                return
            }
            try {
                val responseData = response.body()
                if (responseData!!.code == 200 && Constants.SUCCESS == responseData.status) {
                    if (responseData.data.size > 0) {
                        totalPayout = responseData.data[0].result[0].total_payout
                    }
                }
            } catch (e: Exception) {
                showToast(getString(R.string.server_went_wrong))
                Crashlytics.logException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }

        }

        override fun onFailure(call: Call<TotalPayoutResponse>, t: Throwable) {
            showToast(getString(R.string.server_went_wrong))
            Crashlytics.logException(t)
            Log.d("MC4kException", Log.getStackTraceString(t))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setting)

        Utils.pushGenericEvent(this, "Show_Settings_Detail", SharedPrefUtils.getUserDetailModel(this).dynamoId,
                "ProfileSetting")

        toolbar = findViewById(R.id.toolbar)
        personal_info = findViewById(R.id.personal_info)
        myMoneyContainer = findViewById(R.id.mymoney_info)
        payment_details = findViewById(R.id.payment_details)
        social_accounts = findViewById(R.id.social_accounts)
        appSettingsTextView = findViewById(R.id.appSettingsTextView)
        help = findViewById(R.id.help)
        report_spam = findViewById(R.id.report_spam)
        about = findViewById(R.id.about)
        app_version = findViewById(R.id.app_version)
        logout_layout = findViewById(R.id.logout_layout)
        activityTextView = findViewById(R.id.activityTextView)
        readArticlesTextView = findViewById(R.id.readArticlesTextView)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        if (intent.extras!!.containsKey("isRewardAdded")) {
            isRewardAdded = intent.getStringExtra("isRewardAdded")
        }
        fetchTotalEarning()
        app_version!!.text = resources.getString(R.string.app_version) + " " + AppUtils.getAppVersion(BaseApplication.getAppContext())

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        try {
            mGoogleApiClient = GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build()
        } catch (e: Exception) {

        }

        readArticlesTextView?.text = getString(R.string.read_articles).toLowerCase().capitalize()
        activityTextView?.text = getString(R.string.myprofile_section_activity_label).toLowerCase().capitalize()

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        myMoneyContainer!!.setOnClickListener(this)
        personal_info!!.setOnClickListener(this)
        payment_details!!.setOnClickListener(this)
        social_accounts!!.setOnClickListener(this)
        appSettingsTextView!!.setOnClickListener(this)
        help!!.setOnClickListener(this)
        report_spam!!.setOnClickListener(this)
        about!!.setOnClickListener(this)
        logout_layout!!.setOnClickListener(this)
        activityTextView!!.setOnClickListener(this)
        readArticlesTextView!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.backImageView -> onBackPressed()
            R.id.personal_info -> {
                val personalIntent = Intent(this, RewardsContainerActivity::class.java)
                personalIntent.putExtra("showProfileInfo", true)
                startActivity(personalIntent)
            }
            R.id.mymoney_info -> {
                val intent = Intent(this, EditProfileNewActivity::class.java)
                intent.putExtra("isComingfromCampaign", true)
                intent.putExtra("isRewardAdded", "1")
                startActivity(intent)
            }
            R.id.payment_details -> {
                val paymentIntent = Intent(this, RewardsContainerActivity::class.java)
                paymentIntent.putExtra("pageNumber", 4)
                paymentIntent.putExtra("pageLimit", 4)
                startActivity(paymentIntent)
            }
            R.id.social_accounts -> {
                val socialIntent = Intent(this, RewardsContainerActivity::class.java)
                socialIntent.putExtra("pageNumber", 3)
                socialIntent.putExtra("pageLimit", 3)
                startActivity(socialIntent)
            }
            R.id.appSettingsTextView -> {
                val notificationIntent = Intent(this, AppSettingsActivity::class.java)
                notificationIntent.putExtra("source", "settings")
                startActivity(notificationIntent)
            }
            //            case R.id.topic_of_interest:
            //                Intent subscribeTopicIntent = new Intent(this, SubscribeTopicsActivity.class);
            //                subscribeTopicIntent.putExtra("source", "settings");
            //                startActivity(subscribeTopicIntent);
            //                break;
            R.id.help -> {
                val intent1 = Intent(this, ProfileWebViewActivity::class.java)
                intent1.putExtra(Constants.WEB_VIEW_URL, "https://www.momspresso.com/home/faq")
                intent1.putExtra("title","Help")
                startActivity(intent1)
            }
            R.id.report_spam -> {
                val spamIntent = Intent(this, ReportSpamActivity::class.java)
                startActivity(spamIntent)
            }
            R.id.about -> {
                val intent1 = Intent(this, ProfileWebViewActivity::class.java)
                intent1.putExtra(Constants.WEB_VIEW_URL, "https://www.momspresso.com/aboutus")
                intent1.putExtra("title","About")
                startActivity(intent1)
            }
            R.id.logout_layout -> logoutUser()

            R.id.readArticlesTextView -> {
                val readArticleIntent = Intent(this, UserReadArticlesContentActivity::class.java)
                readArticleIntent.putExtra("isPrivateProfile", true)
                readArticleIntent.putExtra(Constants.AUTHOR_ID, SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId)
                startActivity(readArticleIntent)
            }

            R.id.activityTextView -> {
                val intent5 = Intent(this, UserActivitiesActivity::class.java)
                intent5.putExtra(Constants.AUTHOR_ID, SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId)
                startActivity(intent5)
            }
        }
    }

    private fun logoutUser() {
        if (ConnectivityUtils.isNetworkEnabled(this)) {
            val _controller = LogoutController(this, this)
            val dialog = AlertDialog.Builder(this, R.style.MyAlertDialogStyle)

            dialog.setMessage(resources.getString(R.string.logout_msg)).setNegativeButton(R.string.new_yes) { dialog, which ->
                dialog.cancel()
                showProgressDialog(resources.getString(R.string.please_wait))
                _controller.getData(AppConstants.LOGOUT_REQUEST, "")
            }.setPositiveButton(R.string.new_cancel) { dialog, which ->
                // do nothing
                dialog.cancel()
            }.setIcon(android.R.drawable.ic_dialog_alert)
            val alert11 = dialog.create()
            alert11.show()
            alert11.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.app_red))
            alert11.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.canceltxt_color))
        } else {
            ToastUtils.showToast(this, getString(R.string.error_network))
        }
    }

    override fun updateUi(response: Response?) {
        removeProgressDialog()
        if (response == null) {
            Toast.makeText(this, resources.getString(R.string.server_error), Toast.LENGTH_SHORT).show()
            return
        }
        val responseData = response.responseObject as LogoutResponse
        val message = responseData.result.message
        if (responseData.responseCode == 200) {
            val mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN)
            try {
                val jsonObject = JSONObject()
                jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId)
                mixpanel.track("UserLogout", jsonObject)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            FacebookUtils.logout(this)
            gPlusSignOut()

            val pushToken = SharedPrefUtils.getDeviceToken(BaseApplication.getAppContext())
            val homeCoach = SharedPrefUtils.isCoachmarksShownFlag(BaseApplication.getAppContext(), "home")
            val topicsCoach = SharedPrefUtils.isCoachmarksShownFlag(BaseApplication.getAppContext(), "topics")
            val topicsArticleCoach = SharedPrefUtils.isCoachmarksShownFlag(BaseApplication.getAppContext(), "topics_article")
            val articleCoach = SharedPrefUtils.isCoachmarksShownFlag(BaseApplication.getAppContext(), "article_details")
            val groupsCoach = SharedPrefUtils.isCoachmarksShownFlag(BaseApplication.getAppContext(), "groups")
            val appLocale = SharedPrefUtils.getAppLocale(BaseApplication.getAppContext())

            SharedPrefUtils.clearPrefrence(BaseApplication.getAppContext())
            SharedPrefUtils.setDeviceToken(BaseApplication.getAppContext(), pushToken)
            SharedPrefUtils.setCoachmarksShownFlag(BaseApplication.getAppContext(), "home", homeCoach)
            SharedPrefUtils.setCoachmarksShownFlag(BaseApplication.getAppContext(), "topics", topicsCoach)
            SharedPrefUtils.setCoachmarksShownFlag(BaseApplication.getAppContext(), "topics_article", topicsArticleCoach)
            SharedPrefUtils.setCoachmarksShownFlag(BaseApplication.getAppContext(), "article_details", articleCoach)
            SharedPrefUtils.setCoachmarksShownFlag(BaseApplication.getAppContext(), "groups", groupsCoach)
            SharedPrefUtils.setAppLocale(BaseApplication.getAppContext(), appLocale)
            /**
             * delete table from local also;
             */
            val _tables = UserTable(this.applicationContext as BaseApplication)
            _tables.deleteAll()

            val _familytables = TableFamily(this.applicationContext as BaseApplication)
            _familytables.deleteAll()

            val _adulttables = TableAdult(this.applicationContext as BaseApplication)
            _adulttables.deleteAll()

            val _kidtables = TableKids(this.applicationContext as BaseApplication)
            _kidtables.deleteAll()

            TableAppointmentData(BaseApplication.getInstance()).deleteAll()
            TableNotes(BaseApplication.getInstance()).deleteAll()
            TableFile(BaseApplication.getInstance()).deleteAll()
            TableAttendee(BaseApplication.getInstance()).deleteAll()
            TableWhoToRemind(BaseApplication.getInstance()).deleteAll()


            TableTaskData(BaseApplication.getInstance()).deleteAll()
            TableTaskList(BaseApplication.getInstance()).deleteAll()
            TaskTableAttendee(BaseApplication.getInstance()).deleteAll()
            TaskTableWhoToRemind(BaseApplication.getInstance()).deleteAll()
            TaskTableFile(BaseApplication.getInstance()).deleteAll()
            TaskTableNotes(BaseApplication.getInstance()).deleteAll()
            TaskCompletedTable(BaseApplication.getInstance()).deleteAll()
            TableApiEvents(BaseApplication.getInstance()).deleteAll()

            ExternalCalendarTable(BaseApplication.getInstance()).deleteAll()

            // clear cachee
            BaseApplication.setBlogResponse(null)
            BaseApplication.setBusinessREsponse(null)
            BaseApplication.getInstance().branchData = null
            BaseApplication.getInstance().branchLink = null

            // clear all sessions

            if (StringUtils.isNullOrEmpty(message)) {
                Toast.makeText(this, getString(R.string.went_wrong), Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }

            // set logout flag
            SharedPrefUtils.setLogoutFlag(BaseApplication.getAppContext(), true)
            val intent = Intent(this, ActivityLogin::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

            this.finish()

        } else if (responseData.responseCode == 400) {
            if (StringUtils.isNullOrEmpty(message)) {
                Toast.makeText(this, getString(R.string.went_wrong), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun gPlusSignOut() {
        if (mGoogleApiClient != null && mGoogleApiClient!!.isConnected) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient)
            mGoogleApiClient!!.disconnect()
            mGoogleApiClient!!.connect()
        }
    }

    public override fun onStop() {
        super.onStop()
        if (mGoogleApiClient != null && mGoogleApiClient!!.isConnected) {
            mGoogleApiClient!!.stopAutoManage(this)
            mGoogleApiClient!!.disconnect()
        }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {

    }

    private fun fetchTotalEarning() {
        showProgressDialog(resources.getString(R.string.please_wait))
        val userId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId
        val retrofit = BaseApplication.getInstance().retrofit
        val campaignAPI = retrofit.create(CampaignAPI::class.java)
        val call = campaignAPI.getTotalPayout(userId)
        call.enqueue(getTotalPayout)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return true
    }
}
