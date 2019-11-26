package com.mycity4kids.profile

import android.accounts.NetworkErrorException
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.crashlytics.android.Crashlytics
import com.facebook.shimmer.ShimmerFrameLayout
import com.kelltontech.utils.ToastUtils
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.response.BadgeListResponse
import com.mycity4kids.retrofitAPIsInterfaces.BadgeAPI
import com.mycity4kids.utils.AppUtils
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import java.util.*

class BadgesDialogFragment : DialogFragment(), View.OnClickListener {

    private lateinit var badgeImageView: ImageView
    private lateinit var badgeBgImageView: ImageView
    private lateinit var badgeTitleTextView: TextView
    private lateinit var badgeDescTextView: TextView
    private lateinit var whatsappShareImageView: ImageView
    private lateinit var facebookShareImageView: ImageView
    private lateinit var instagramShareImageView: ImageView
    private lateinit var genericShareImageView: ImageView
    private lateinit var badgesShimmerContainer: ShimmerFrameLayout

    var userId: String? = null
    var badgeId: String? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.badge_dialog_fragment, container,
                false)

        badgeImageView = rootView.findViewById(R.id.badgeImageView)
        badgeBgImageView = rootView.findViewById(R.id.badgeBgImageView)
        badgeTitleTextView = rootView.findViewById(R.id.badgeTitleTextView)
        badgeDescTextView = rootView.findViewById(R.id.badgeDescTextView)
        whatsappShareImageView = rootView.findViewById(R.id.whatsappShareImageView)
        facebookShareImageView = rootView.findViewById(R.id.facebookShareImageView)
        instagramShareImageView = rootView.findViewById(R.id.instagramShareImageView)
        genericShareImageView = rootView.findViewById(R.id.genericShareImageView)
        badgesShimmerContainer = rootView.findViewById(R.id.badgesShimmerContainer)

        whatsappShareImageView.setOnClickListener(this)
        facebookShareImageView.setOnClickListener(this)
        instagramShareImageView.setOnClickListener(this)
        genericShareImageView.setOnClickListener(this)

        val bundle = arguments
        userId = bundle?.getString(Constants.USER_ID)
        badgeId = bundle?.getString("id")

//        val badgeData = bundle?.getParcelable<BadgeListResponse.BadgeListData.BadgeListResult>("badgeData")

        if (userId.isNullOrBlank() || badgeId.isNullOrBlank()) {
            activity?.let {
                ToastUtils.showToast(it, it.getString(R.string.empty_screen), Toast.LENGTH_SHORT)
            }
            dismiss()
        }
        badgesShimmerContainer.startShimmerAnimation()
        fetchBadgeDetail(userId!!, badgeId!!)

        return rootView
    }

    private fun fetchBadgeDetail(userId: String, badgeId: String) {
        val retrofit = BaseApplication.getInstance().retrofit
        val badgeAPI = retrofit.create(BadgeAPI::class.java)
        val badgeListResponseCall = badgeAPI.getBadgeDetail(userId, badgeId)
        badgeListResponseCall.enqueue(object : Callback<BadgeListResponse> {
            override fun onResponse(call: Call<BadgeListResponse>, response: retrofit2.Response<BadgeListResponse>) {
                try {
                    badgesShimmerContainer.visibility = View.GONE
                    if (response.body() == null) {
                        if (response.raw() != null) {
                            val nee = NetworkErrorException(response.raw().toString())
                            Crashlytics.logException(nee)
                        }
                        return
                    }
                    val responseModel = response.body() as BadgeListResponse
                    if (responseModel.code == 200 && Constants.SUCCESS == responseModel.status) {
                        if (responseModel.data != null && !responseModel.data.isEmpty() && responseModel.data[0] != null) {
                            showBadgeDialog(responseModel.data[0].result)
                        } else {
                        }
                    }
                } catch (e: Exception) {
                    Crashlytics.logException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }

            override fun onFailure(call: Call<BadgeListResponse>, t: Throwable) {
                badgesShimmerContainer.visibility = View.GONE
                Crashlytics.logException(t)
                Log.d("MC4kException", Log.getStackTraceString(t))
            }

        })
    }

    private fun showBadgeDialog(result: ArrayList<BadgeListResponse.BadgeListData.BadgeListResult>?) {
        activity?.let {
            Picasso.with(it).load(result?.get(0)?.badge_image_url).error(R.drawable.default_article)
                    .fit().into(badgeImageView)
            Picasso.with(it).load(result?.get(0)?.badge_bg_url).error(R.drawable.default_article)
                    .fit().into(badgeBgImageView)
            if (AppUtils.isPrivateProfile(userId)) {
                badgeTitleTextView.setText(result?.get(0)?.getBadge_title()?.user)
                badgeDescTextView.setText(result?.get(0)?.getBadge_desc()?.user)
            } else {
                badgeTitleTextView.setText(result?.get(0)?.getBadge_title()?.other)
                badgeDescTextView.setText(result?.get(0)?.getBadge_desc()?.other)
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onClick(view: View?) {
        when {
            view?.id == R.id.whatsappShareImageView -> {
            }
            view?.id == R.id.facebookShareImageView -> {
            }
            view?.id == R.id.instagramShareImageView -> {
            }
            view?.id == R.id.genericShareImageView -> {
            }
        }
    }
}