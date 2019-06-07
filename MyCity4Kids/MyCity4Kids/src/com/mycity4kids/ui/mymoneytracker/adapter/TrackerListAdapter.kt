package com.mycity4kids.ui.mymoneytracker.adapter

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.mycity4kids.R
import com.mycity4kids.constants.Constants
import com.mycity4kids.ui.mymoneytracker.model.TrackerDataModel
import java.text.SimpleDateFormat
import java.util.*

class TrackerListAdapter(var context: Context, var trackerDataModel: ArrayList<TrackerDataModel>) : RecyclerView.Adapter<ViewHolder>() {
    private var trackerData = ArrayList<TrackerDataModel>()

    init {
        trackerData = trackerDataModel
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.tracker_list_index, parent, false)
        return ViewHolder(view)
    }


    override fun getItemCount(): Int {
        return trackerData.size
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (!trackerData.isNullOrEmpty()) {
            var trackerDataModel = trackerData.get(position)
            if (trackerDataModel != null) {

                if (position == 0) {
                    holder.imageTop.visibility = View.VISIBLE
                    holder.imageStatus.setImageDrawable(context.getDrawable(R.drawable.ic_bullet_svg))
                    holder.imageTop.setColorFilter(ContextCompat.getColor(context, R.color.app_red), android.graphics.PorterDuff.Mode.SRC_IN);
                    //DrawableCompat.setTint(holder.imageTop.getDrawable(), ContextCompat.getColor(context, R.color.app_red));
                } else if (position == trackerData.size - 1) {
                    holder.imageStatus.setImageDrawable(context.getDrawable(R.drawable.ic_circle_svg))
                    holder.imageTop.visibility = View.GONE
                } else {
                    holder.imageStatus.setImageDrawable(context.getDrawable(R.drawable.ic_bullet_svg))
                    holder.imageTop.visibility = View.GONE
                }

                holder.textStatusName.text = Constants.TrackerStatusMapping.findById(trackerDataModel.tracker_status)
                if (trackerDataModel.is_completed == 1) {
                    if (trackerDataModel.completed_time > 0) {
                        holder.textDate.setText(convertDate(trackerDataModel.completed_time))
                    } else {

                    }
                    setColorsAndImage(Constants.TrackerStatusMapping.findById(trackerDataModel.tracker_status), holder.imageStatus, context, holder.textDate, holder.textStatusName)
                    holder.textDateError.text = ""
                    if (Constants.TrackerStatusMapping.findById(trackerDataModel.tracker_status).equals(Constants.TrackerStatusMapping.PROOF_SUBMITTED_REJECTED.name)) {
                        holder.textstatusError.text = "Please submit corrected proofs again."
                    } else {
                        holder.textstatusError.text = ""
                    }
                } else if (trackerDataModel.is_completed == 0) {
                    if (trackerDataModel.expected_time > 0) {
                        holder.textDateError.setText(convertDate(trackerDataModel.expected_time))
                    } else {

                    }
                    holder.imageStatus.setImageDrawable(context.getDrawable(R.drawable.ic_circle_svg))
                    holder.imageStatus.setColorFilter(ContextCompat.getColor(context, R.color.campaign_expired), android.graphics.PorterDuff.Mode.SRC_IN);
                    //DrawableCompat.setTint(holder.imageStatus.getDrawable(), ContextCompat.getColor(context, R.color.campaign_expired));
                    holder.textstatusError.text = ""
                    if (Constants.TrackerStatusMapping.findById(trackerDataModel.tracker_status).equals(Constants.TrackerStatusMapping.APPROVED.name)) {
                        holder.textDateError.text = "Expected Approval"
                    } else {
                        holder.textDateError.text = ""
                    }
                }
            }
        }
    }
}

fun convertDate(milliSeconds: Long): String {
    val formatter = SimpleDateFormat("dd MMM yyyy")
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = milliSeconds * 1000
    return formatter.format(calendar.time)
}

fun setColorsAndImage(statusCode: String, imageStatus: ImageView, context: Context, textDate: TextView, textStatusName: TextView) {
    when {
        "Approved".equals(statusCode, true) -> {
            imageStatus.setColorFilter(ContextCompat.getColor(context, R.color.campaign_subscribed), android.graphics.PorterDuff.Mode.SRC_IN);
            textDate.setTextColor(context.resources.getColor(R.color.campaign_subscribed))
            textStatusName.setTextColor(context.resources.getColor(R.color.campaign_subscribed))
        }
        "Applied".equals(statusCode, true) -> {
            imageStatus.setColorFilter(ContextCompat.getColor(context, R.color.campaign_applied_bg), android.graphics.PorterDuff.Mode.SRC_IN);
            textDate.setTextColor(context.resources.getColor(R.color.campaign_applied_bg))
            textStatusName.setTextColor(context.resources.getColor(R.color.campaign_applied_bg))
        }

        "Proof approval".equals(statusCode, true) -> {
            imageStatus.setColorFilter(ContextCompat.getColor(context, R.color.campaign_proof_approval), android.graphics.PorterDuff.Mode.SRC_IN);
            textDate.setTextColor(context.resources.getColor(R.color.campaign_proof_approval))
            textStatusName.setTextColor(context.resources.getColor(R.color.campaign_proof_approval))
        }
        "Proof submission rejected".equals(statusCode, true) -> {
            imageStatus.setColorFilter(ContextCompat.getColor(context, R.color.app_red), android.graphics.PorterDuff.Mode.SRC_IN);
            textDate.setTextColor(context.resources.getColor(R.color.app_red))
            textStatusName.setTextColor(context.resources.getColor(R.color.app_red))
        }
        "Application under review".equals(statusCode, true) -> {
            imageStatus.setColorFilter(ContextCompat.getColor(context, R.color.app_red), android.graphics.PorterDuff.Mode.SRC_IN);
            textDate.setTextColor(context.resources.getColor(R.color.app_red))
            textStatusName.setTextColor(context.resources.getColor(R.color.app_red))
        }
        "Proof under review".equals(statusCode, true) -> {
            imageStatus.setColorFilter(ContextCompat.getColor(context, R.color.app_red), android.graphics.PorterDuff.Mode.SRC_IN);
            textDate.setTextColor(context.resources.getColor(R.color.app_red))
            textStatusName.setTextColor(context.resources.getColor(R.color.app_red))
        }
        "Payment in process".equals(statusCode, true) -> {
            imageStatus.setColorFilter(ContextCompat.getColor(context, R.color.campaign_payment_in_process), android.graphics.PorterDuff.Mode.SRC_IN);
            textDate.setTextColor(context.resources.getColor(R.color.campaign_payment_in_process))
            textStatusName.setTextColor(context.resources.getColor(R.color.campaign_payment_in_process))
        }
        "Payment done".equals(statusCode, true) -> {
            imageStatus.setColorFilter(ContextCompat.getColor(context, R.color.campaign_payment_done), android.graphics.PorterDuff.Mode.SRC_IN);
            textDate.setTextColor(context.resources.getColor(R.color.campaign_payment_done))
            textStatusName.setTextColor(context.resources.getColor(R.color.campaign_payment_done))
        }

        "Proof submitted".equals(statusCode, true) -> {
            imageStatus.setColorFilter(ContextCompat.getColor(context, R.color.campaign_proof_submitted), android.graphics.PorterDuff.Mode.SRC_IN);
            textDate.setTextColor(context.resources.getColor(R.color.campaign_proof_submitted))
            textStatusName.setTextColor(context.resources.getColor(R.color.campaign_proof_submitted))
        }
        else -> {

        }
    }
}


class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    var textDate = view.findViewById<TextView>(R.id.textDate)
    var textDateError = view.findViewById<TextView>(R.id.textDateError)
    var textStatusName = view.findViewById<TextView>(R.id.textStatusName)
    var textstatusError = view.findViewById<TextView>(R.id.textstatusError)
    var imageStatus = view.findViewById<ImageView>(R.id.imageStatus)
    var imageTop = view.findViewById<ImageView>(R.id.imageTop)
}