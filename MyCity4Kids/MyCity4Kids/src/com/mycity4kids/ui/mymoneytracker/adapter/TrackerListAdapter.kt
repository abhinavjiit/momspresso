package com.mycity4kids.ui.mymoneytracker.adapter

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
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

class TrackerListAdapter(var context: Context, var trackerDataModel: ArrayList<TrackerDataModel>) : androidx.recyclerview.widget.RecyclerView.Adapter<ViewHolder>() {
    private var trackerData = ArrayList<TrackerDataModel>()
    private var errorStatus: Boolean = false

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
            trackerData.forEach {
                errorStatus = it.tracker_status == 7
            }
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
                        holder.textDate.visibility = View.VISIBLE
                    } else {
                        holder.textDate.visibility = View.GONE
                    }
                    setColorsAndImage(Constants.TrackerStatusMapping.findById(trackerDataModel.tracker_status), holder.imageStatus, context, holder.textDate, holder.textStatusName)
                    holder.textDateError.text = ""
                    if (Constants.TrackerStatusMapping.findById(trackerDataModel.tracker_status).equals(context.resources.getString(R.string.tracker_activity_proof_submitted_rejected))) {
                        if (errorStatus) {
                            holder.textstatusError.text = context.resources.getString(R.string.tracker_Activity_submit_corrected_proof)
                        } else {
                            holder.textstatusError.text = ""

                        }
                    } else {
                        holder.textstatusError.text = ""
                    }
                } else if (trackerDataModel.is_completed == 0) {
                    if (trackerDataModel.expected_time > 0) {
                        holder.textDate.setText(convertDate(trackerDataModel.expected_time))
                        holder.textDate.visibility = View.VISIBLE
                        holder.textDateError.text = context.resources.getString(R.string.tracker_Activity_expected_date)

                    } else {
                        holder.textDate.visibility = View.GONE
                        holder.textDateError.text = ""


                    }
                    holder.imageStatus.setImageDrawable(context.getDrawable(R.drawable.ic_circle_svg))
                    holder.imageStatus.setColorFilter(ContextCompat.getColor(context, R.color.campaign_expired), android.graphics.PorterDuff.Mode.SRC_IN);
                    holder.textstatusError.text = ""
                    /* if (Constants.TrackerStatusMapping.findById(trackerDataModel.tracker_status).equals(Constants.TrackerStatusMapping.APPLICATION_UNDER_REVIEW.id)) {
                         holder.textDateError.text = "Expected Approval"
                     } else {
                         holder.textDateError.text = ""
                     }*/
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
        "APPROVED".equals(statusCode, true) -> {
            imageStatus.setColorFilter(ContextCompat.getColor(context, R.color.campaign_subscribed), android.graphics.PorterDuff.Mode.SRC_IN);
            textDate.setTextColor(context.resources.getColor(R.color.campaign_subscribed))
            textStatusName.setTextColor(context.resources.getColor(R.color.campaign_subscribed))
        }
        "APPLIED".equals(statusCode, true) -> {
            imageStatus.setColorFilter(ContextCompat.getColor(context, R.color.campaign_applied_bg), android.graphics.PorterDuff.Mode.SRC_IN);
            textDate.setTextColor(context.resources.getColor(R.color.campaign_applied_bg))
            textStatusName.setTextColor(context.resources.getColor(R.color.campaign_applied_bg))
        }

        "PROOFS APPROVED".equals(statusCode, true) -> {
            imageStatus.setColorFilter(ContextCompat.getColor(context, R.color.campaign_proof_approval), android.graphics.PorterDuff.Mode.SRC_IN);
            textDate.setTextColor(context.resources.getColor(R.color.campaign_proof_approval))
            textStatusName.setTextColor(context.resources.getColor(R.color.campaign_proof_approval))
        }
        "PROOFS SUBMITTED REJECTED".equals(statusCode, true) -> {
            imageStatus.setColorFilter(ContextCompat.getColor(context, R.color.campaign_proof_reject_bg), android.graphics.PorterDuff.Mode.SRC_IN);
            textDate.setTextColor(context.resources.getColor(R.color.campaign_proof_reject_bg))
            textStatusName.setTextColor(context.resources.getColor(R.color.campaign_proof_reject_bg))
        }
        "APPLICATION UNDER REVIEW".equals(statusCode, true) -> {
            imageStatus.setColorFilter(ContextCompat.getColor(context, R.color.app_red), android.graphics.PorterDuff.Mode.SRC_IN);
            textDate.setTextColor(context.resources.getColor(R.color.app_red))
            textStatusName.setTextColor(context.resources.getColor(R.color.app_red))
        }
        "PROOFS UNDER REVIEW".equals(statusCode, true) -> {
            imageStatus.setColorFilter(ContextCompat.getColor(context, R.color.app_red), android.graphics.PorterDuff.Mode.SRC_IN);
            textDate.setTextColor(context.resources.getColor(R.color.app_red))
            textStatusName.setTextColor(context.resources.getColor(R.color.app_red))
        }
        "PAYMENT IN PROCESS".equals(statusCode, true) -> {
            imageStatus.setColorFilter(ContextCompat.getColor(context, R.color.campaign_payment_in_process), android.graphics.PorterDuff.Mode.SRC_IN);
            textDate.setTextColor(context.resources.getColor(R.color.campaign_payment_in_process))
            textStatusName.setTextColor(context.resources.getColor(R.color.campaign_payment_in_process))
        }
        "PAYMENT DONE".equals(statusCode, true) -> {
            imageStatus.setColorFilter(ContextCompat.getColor(context, R.color.campaign_payment_done), android.graphics.PorterDuff.Mode.SRC_IN);
            textDate.setTextColor(context.resources.getColor(R.color.campaign_payment_done))
            textStatusName.setTextColor(context.resources.getColor(R.color.campaign_payment_done))
        }

        "PROOFS SUBMITTED".equals(statusCode, true) -> {
            imageStatus.setColorFilter(ContextCompat.getColor(context, R.color.campaign_proof_submitted), android.graphics.PorterDuff.Mode.SRC_IN);
            textDate.setTextColor(context.resources.getColor(R.color.campaign_proof_submitted))
            textStatusName.setTextColor(context.resources.getColor(R.color.campaign_proof_submitted))
        }
        else -> {

        }
    }
}


class ViewHolder(private val view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
    var textDate = view.findViewById<TextView>(R.id.textDate)
    var textDateError = view.findViewById<TextView>(R.id.textDateError)
    var textStatusName = view.findViewById<TextView>(R.id.textStatusName)
    var textstatusError = view.findViewById<TextView>(R.id.textstatusError)
    var imageStatus = view.findViewById<ImageView>(R.id.imageStatus)
    var imageTop = view.findViewById<ImageView>(R.id.imageTop)
}