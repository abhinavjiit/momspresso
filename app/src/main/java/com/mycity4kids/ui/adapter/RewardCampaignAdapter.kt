package com.mycity4kids.ui.adapter

import android.accounts.NetworkErrorException
import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.Constants
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.models.campaignmodels.CampaignDataListResult
import com.mycity4kids.models.campaignmodels.ParticipateCampaignResponse
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.CampaignAPI
import com.mycity4kids.ui.campaign.activity.CampaignContainerActivity
import com.mycity4kids.widget.CustomFontTextView
import com.squareup.picasso.Picasso
import io.github.douglasjunior.androidSimpleTooltip.ArrowDrawable
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip
import kotlinx.android.synthetic.main.campaign_list_recycler_adapter.view.*
import retrofit2.Call
import retrofit2.Callback
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class RewardCampaignAdapter(
    private var campaignList: List<CampaignDataListResult>,
    val context: Activity?,
    val clickListener: ClickListener
) : RecyclerView.Adapter<RewardCampaignAdapter.RewardHolder>() {

    private var forYouStatus: Int = 0
    private lateinit var view1: TextView

    private lateinit var tooltip: SimpleTooltip
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardHolder {
        return RewardHolder(
            LayoutInflater.from(context).inflate(
                R.layout.campaign_list_recycler_adapter,
                parent,
                false
            )
        )
    }

    fun updateForYouStatus(forYouStatus: Int) {
        this.forYouStatus = forYouStatus
    }

    override fun getItemCount(): Int = campaignList.size

    override fun onBindViewHolder(holder: RewardHolder, position: Int) {
        val itemPhoto = campaignList[position]
        holder.bindPhoto(itemPhoto, position)
    }


    fun showToolTip() {
        tooltip = SimpleTooltip.Builder(context)
            .anchorView(view1)
            .contentView(R.layout.readthis_campaign_tooltip_layout)
            .margin(0f)
            .padding(0f)
            .arrowColor(
                ContextCompat.getColor(
                    (context as CampaignContainerActivity),
                    R.color.tooltip_border
                )
            )
            .arrowWidth(50f)
            .animated(false)
            .focusable(true)
            .dismissOnInsideTouch(false)
            .dismissOnOutsideTouch(false)
            .transparentOverlay(true)
            .arrowDirection(ArrowDrawable.RIGHT)
            .build()
        val text: TextView
        text = tooltip.findViewById(R.id.secondTextView)
        text.setText(R.string.list_campaign_tooltip_text)
        val okgotIt: TextView
        okgotIt = tooltip.findViewById(R.id.okgot)
        okgotIt.setOnClickListener {
            tooltip.dismiss()
            context.updateCoachmarkFlag(
                "listcampaigntooltip",
                true
            )
            clickListener.onRecyclerClick(0)
        }
        tooltip.show()
    }


    // 1
    inner class RewardHolder(private val view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

        private var campaignList: CampaignDataListResult? = null

        init {
            view.setOnClickListener(this)
            (view.share).setOnClickListener(this)
        }

        fun bindPhoto(campaignList: CampaignDataListResult, position: Int) {
            this.campaignList = campaignList
            Picasso.get().load(campaignList.imageUrl).placeholder(R.drawable.default_article)
                .error(R.drawable.default_article).into(view.campaign_header)
            Picasso.get().load(campaignList.brandDetails.imageUrl)
                .placeholder(R.drawable.default_article).error(R.drawable.default_article)
                .into(view.brand_img)
            (view.brand_name).setText(campaignList.brandDetails.name)
            (view.campaign_name).setText(campaignList.name)
            (view.amount).setText("" + campaignList.slotAvailable)
            setTextAndColor(campaignList.campaignStatus)
            compareDate(campaignList.campaignStatus)
            if (position == 0) {
                view1 = view.submission_status
            }
        }

        // 4
        override fun onClick(v: View) {
            if (v == (view.share)) {
                val userId =
                    SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId
                Utils.campaignEvent(
                    context,
                    "Campaign Detail",
                    "Campaign Listing",
                    "share",
                    campaignList!!.name,
                    "android",
                    SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                    SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                    System.currentTimeMillis().toString(),
                    "Show_Campaign_Listing"
                )

                context?.let {
                    val shareIntent = ShareCompat.IntentBuilder
                        .from(it)
                        .setType("text/plain")
                        .setChooserTitle("Share URL")
                        .setText("https://www.momspresso.com/mymoney/" + campaignList!!.nameSlug + "/" + campaignList!!.id + "?referrer=" + userId)
                        .intent

                    if (shareIntent.resolveActivity(context!!.packageManager) != null) {
                        it.startActivity(shareIntent)
                    }
                }
            } else {
                Utils.campaignEvent(
                    context,
                    "Campaign Detail",
                    "Campaign Listing",
                    "Click_listing_card",
                    campaignList!!.name,
                    "android",
                    SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                    SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                    System.currentTimeMillis().toString(),
                    "Show_Campaign_Listing"
                )
                if (campaignList!!.campaignStatus == 8)
                    showInviteDialog()
                else
                    (context as CampaignContainerActivity).addCampaginDetailFragment(
                        campaignList!!.id,
                        ""
                    )
            }
        }

        fun setTextAndColor(status: Int) {
            if (status == 0) {
                (view.submission_status).setText(context!!.resources.getString(R.string.campaign_details_expired))
                (view.submission_status).setBackgroundResource(R.drawable.campaign_expired)
                (view.view4).setBackgroundColor(context.resources.getColor(R.color.campaign_expired_background))
                (view.end_date_text).setBackgroundResource(R.drawable.campaign_detail_expired_bg)
                (view.amount).setBackgroundResource(R.drawable.campaign_detail_expired_bg)
            } else if (status == 1 || status == 18) {
                if (campaignList?.deliverableTypes?.get(0) == 5) {
                    (view.submission_status).setText(context!!.resources.getString(R.string.detail_take_survey))
                } else {
                    (view.submission_status).setText(context!!.resources.getString(R.string.campaign_details_apply_now))
                }
                (view.submission_status).setBackgroundResource(R.drawable.subscribe_now)
                (view.view4).setBackgroundColor(context.resources.getColor(R.color.campaign_list_buttons))
                (view.end_date_text).setBackgroundResource(R.drawable.campaign_detail_red_bg)
                (view.amount).setBackgroundResource(R.drawable.campaign_detail_red_bg)
            } else if (status == 2) {
                if (campaignList?.deliverableTypes?.get(0) == 5) {
                    (view.submission_status).setText(context!!.resources.getString(R.string.detail_take_survey))
                } else {
                    (view.submission_status).setText(context!!.resources.getString(R.string.campaign_details_submission_open))
                }
                (view.submission_status).setBackgroundResource(R.drawable.campaign_subscription_open)
                (view.view4).setBackgroundColor(context.resources.getColor(R.color.campaign_list_buttons))
                (view.end_date_text).setBackgroundResource(R.drawable.campaign_detail_red_bg)
                (view.amount).setBackgroundResource(R.drawable.campaign_detail_red_bg)
            } else if (status == 22) {
                if (campaignList?.deliverableTypes?.get(0) == 5) {
                    (view.submission_status).setText(context!!.resources.getString(R.string.detail_take_survey))
                } else {
                    (view.submission_status).setText(context!!.resources.getString(R.string.campaign_details_submission_open))
                }
                (view.submission_status).setBackgroundResource(R.drawable.campaign_subscription_open)
                (view.view4).setBackgroundColor(context.resources.getColor(R.color.campaign_list_buttons))
                (view.end_date_text).setBackgroundResource(R.drawable.campaign_detail_red_bg)
                (view.amount).setBackgroundResource(R.drawable.campaign_detail_red_bg)
            } else if (status == 3) {
                (view.submission_status).setText(context!!.resources.getString(R.string.campaign_details_applied))
                (view.submission_status).setBackgroundResource(R.drawable.campaign_applied)
                (view.view4).setBackgroundColor(context.resources.getColor(R.color.campaign_list_buttons))
                (view.end_date_text).setBackgroundResource(R.drawable.campaign_detail_red_bg)
                (view.amount).setBackgroundResource(R.drawable.campaign_detail_red_bg)
            } else if (status == 21) {
                (view.submission_status).setText(context!!.resources.getString(R.string.campaign_details_approved))
                (view.submission_status).setBackgroundResource(R.drawable.campaign_subscribed)
                (view.view4).setBackgroundColor(context.resources.getColor(R.color.campaign_list_buttons))
                (view.end_date_text).setBackgroundResource(R.drawable.campaign_detail_red_bg)
                (view.amount).setBackgroundResource(R.drawable.campaign_detail_red_bg)
            } else if (status == 4) {
                (view.submission_status).setText(context!!.resources.getString(R.string.campaign_details_application_full))
                (view.submission_status).setBackgroundResource(R.drawable.campaign_submission_full)
                (view.view4).setBackgroundColor(context.resources.getColor(R.color.campaign_list_buttons))
                (view.end_date_text).setBackgroundResource(R.drawable.campaign_detail_red_bg)
                (view.amount).setBackgroundResource(R.drawable.campaign_detail_red_bg)
            } else if (status == 5) {
                if (forYouStatus == 0) {
                    if (campaignList?.deliverableTypes?.get(0) == 5) {
                        (view.submission_status).setText(context!!.resources.getString(R.string.detail_take_survey))
                    } else {
                        (view.submission_status).setText(context!!.resources.getString(R.string.campaign_details_apply_now))
                    }
                    (view.submission_status).setBackgroundResource(R.drawable.subscribe_now)
                    (view.view4).setBackgroundColor(context.resources.getColor(R.color.campaign_list_buttons))
                    (view.end_date_text).setBackgroundResource(R.drawable.campaign_detail_red_bg)
                    (view.amount).setBackgroundResource(R.drawable.campaign_detail_red_bg)
                } else {
                    (view.submission_status).setText(context!!.resources.getString(R.string.campaign_details_ineligible))
                    (view.submission_status).setBackgroundResource(R.drawable.campaign_expired)
                    (view.view4).setBackgroundColor(context.resources.getColor(R.color.campaign_list_buttons))
                    (view.end_date_text).setBackgroundResource(R.drawable.campaign_detail_red_bg)
                    (view.amount).setBackgroundResource(R.drawable.campaign_detail_red_bg)
                }
            } else if (status == 6) {
                (view.submission_status).setText(context!!.resources.getString(R.string.campaign_list_proof_reject))
                (view.submission_status).setBackgroundResource(R.drawable.campaign_rejected)
                (view.view4).setBackgroundColor(context.resources.getColor(R.color.campaign_list_buttons))
                (view.end_date_text).setBackgroundResource(R.drawable.campaign_detail_red_bg)
                (view.amount).setBackgroundResource(R.drawable.campaign_detail_red_bg)
            } else if (status == 17) {
                (view.submission_status).setText(context!!.resources.getString(R.string.campaign_list_proof_reject))
                (view.submission_status).setBackgroundResource(R.drawable.campaign_rejected)
                (view.view4).setBackgroundColor(context.resources.getColor(R.color.campaign_list_buttons))
                (view.end_date_text).setBackgroundResource(R.drawable.campaign_detail_red_bg)
                (view.amount).setBackgroundResource(R.drawable.campaign_detail_red_bg)
            } else if (status == 7) {
                (view.submission_status).setText(context!!.resources.getString(R.string.campaign_details_completed))
                (view.submission_status).setBackgroundResource(R.drawable.campaign_completed)
                (view.view4).setBackgroundColor(context.resources.getColor(R.color.campaign_list_buttons))
                (view.end_date_text).setBackgroundResource(R.drawable.campaign_detail_red_bg)
                (view.amount).setBackgroundResource(R.drawable.campaign_detail_red_bg)
            } else if (status == 8) {
                (view.submission_status).setText(context!!.resources.getString(R.string.campaign_details_invite_only))
                (view.submission_status).setBackgroundResource(R.drawable.campaign_invite_only)
                (view.view4).setBackgroundColor(context.resources.getColor(R.color.campaign_list_buttons))
                (view.end_date_text).setBackgroundResource(R.drawable.campaign_detail_red_bg)
                (view.amount).setBackgroundResource(R.drawable.campaign_detail_red_bg)
            } else if (status == 9) {
                (view.submission_status).setText(context!!.resources.getString(R.string.campaign_list_proof_moderation))
                (view.submission_status).setBackgroundResource(R.drawable.campaign_subscription_open)
                (view.view4).setBackgroundColor(context.resources.getColor(R.color.campaign_list_buttons))
                (view.end_date_text).setBackgroundResource(R.drawable.campaign_detail_red_bg)
                (view.amount).setBackgroundResource(R.drawable.campaign_detail_red_bg)
            } else if (status == 16) {
                (view.submission_status).setText(context!!.resources.getString(R.string.campaign_list_proof_moderation))
                (view.submission_status).setBackgroundResource(R.drawable.campaign_subscription_open)
                (view.view4).setBackgroundColor(context.resources.getColor(R.color.campaign_list_buttons))
                (view.end_date_text).setBackgroundResource(R.drawable.campaign_detail_red_bg)
                (view.amount).setBackgroundResource(R.drawable.campaign_detail_red_bg)
            } else if (status == 10) {
                (view.submission_status).setText(context!!.resources.getString(R.string.campaign_list_proof_reject))
                (view.submission_status).setBackgroundResource(R.drawable.campaign_proof_rejected_bg)
                (view.view4).setBackgroundColor(context.resources.getColor(R.color.campaign_list_buttons))
                (view.end_date_text).setBackgroundResource(R.drawable.campaign_detail_red_bg)
                (view.amount).setBackgroundResource(R.drawable.campaign_detail_red_bg)
            }
        }

        fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
            val formatter = SimpleDateFormat(format, locale)
            return formatter.format(this)
        }

        fun getCurrentDateTime(): Date {
            return Calendar.getInstance().time
        }

        fun getDate(milliSeconds: Long, dateFormat: String): String {
            val formatter = SimpleDateFormat(dateFormat)

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = milliSeconds * 1000
            return formatter.format(calendar.time)
        }

        fun compareDate(campaignStatus: Int) {
            if ((getCurrentDateTime().toString("yyyy-MM-dd")).compareTo(
                    getDate(
                        campaignList!!.startTime,
                        "yyyy-MM-dd"
                    )
                ) > 0
            ) {
                when (campaignStatus) {
                    0, 1, 5, 4, 6, 8 -> (view.end_date).setText(context!!.resources.getString(R.string.application_end_date))
                    else -> {
                        (view.end_date).setText(context!!.resources.getString(R.string.submission_end_date))
                    }
                }
                (view.end_date_text).setText(getDate(campaignList!!.endTime, "dd MMM yyyy"))
            } else {
                (view.end_date).setText(context!!.resources.getString(R.string.start_date))
                (view.end_date_text).setText(getDate(campaignList!!.startTime, "dd MMM yyyy"))
            }
        }

        fun showInviteDialog() {
            if (context != null) {
                val dialog = Dialog(context)
                val window = dialog.getWindow()
                dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
                dialog.window!!.setGravity(Gravity.CENTER_HORIZONTAL)
                dialog.setContentView(R.layout.dialog_invite_only_campaign)
                dialog.setCancelable(true)
                val okBtn = dialog.findViewById<TextView>(R.id.click_ok)
                val crossIcon = dialog.findViewById<ImageView>(R.id.cross)
                crossIcon.setOnClickListener {
                    dialog.dismiss()
                }
                okBtn.setOnClickListener {
                    (context as CampaignContainerActivity).showProgressDialog(
                        context!!.resources.getString(
                            R.string.please_wait
                        )
                    )
                    val retro = BaseApplication.getInstance().retrofit
                    val campaignAPI = retro.create(CampaignAPI::class.java)
                    val call = campaignAPI.postSubscribeCampaign(campaignList!!.id)
                    call.enqueue(subscribeCampaign)
                    dialog.dismiss()
                }
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.show()
            }
        }

        val subscribeCampaign = object : Callback<ParticipateCampaignResponse> {
            override fun onResponse(
                call: Call<ParticipateCampaignResponse>,
                response: retrofit2.Response<ParticipateCampaignResponse>
            ) {
                (context as CampaignContainerActivity).removeProgressDialog()
                if (response == null || null == response.body()) {
                    val nee = NetworkErrorException(response.raw().toString())
                    FirebaseCrashlytics.getInstance().recordException(nee)
                    return
                }
                try {
                    val responseData = response.body()
                    if (responseData!!.code == 200 && Constants.SUCCESS == responseData.status) {
                        Toast.makeText(
                            context,
                            context!!.resources.getString(R.string.toast_campaign_invite_thankyou),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(context, responseData.reason, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }

            override fun onFailure(call: Call<ParticipateCampaignResponse>, t: Throwable) {
                (context as CampaignContainerActivity).removeProgressDialog()
                FirebaseCrashlytics.getInstance().recordException(t)
                Log.d("MC4kException", Log.getStackTraceString(t))
            }
        }
    }


    interface ClickListener {
        fun onRecyclerClick(position: Int)
    }
}
