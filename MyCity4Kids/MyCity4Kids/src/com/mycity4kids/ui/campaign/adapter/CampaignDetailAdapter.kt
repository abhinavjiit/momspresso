package com.mycity4kids.ui.adapter

import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mycity4kids.R
import com.mycity4kids.models.campaignmodels.CampaignDetailDeliverable
import kotlinx.android.synthetic.main.deliverable_list_recycler_adapter.view.*
import java.text.SimpleDateFormat
import java.util.*

class CampaignDetailAdapter(private var deliverableList: List<List<CampaignDetailDeliverable>>?, val context: FragmentActivity?) : RecyclerView.Adapter<CampaignDetailAdapter.RewardHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CampaignDetailAdapter.RewardHolder {
        return RewardHolder(LayoutInflater.from(context).inflate(R.layout.deliverable_list_recycler_adapter, parent, false))
    }

    override fun getItemCount(): Int = deliverableList!!.get(0).size

    override fun onBindViewHolder(holder: CampaignDetailAdapter.RewardHolder, position: Int) {
//        val itemPhoto = deliverableList!!.get(0).[position]
        holder.bindPhoto(deliverableList!!.get(0))
    }

    //1
    inner class RewardHolder(private val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        //2
        private var deliverableList: List<CampaignDetailDeliverable>? = null

        //3
        init {

            view.setOnClickListener(this)
        }

        fun bindPhoto(deliverableList: List<CampaignDetailDeliverable>) {
            this.deliverableList = deliverableList
            val builder = StringBuilder()
            for (instructions in deliverableList.get(position).instructions!!) {
                builder.append("\u2022" + "  " + instructions + "\n")
            }
            (view.deliverable_text).setText(builder.toString())
            (view.deliverable_header).setText(deliverableList.get(position).name)

        }

        //4
        override fun onClick(v: View) {
            //val context = itemView.context
//            (context as CampaignContainerActivity).addCampaginDetailFragment(campaignList!!.id)
        }

        fun getDate(milliSeconds: Long, dateFormat: String): String {
            // Create a DateFormatter object for displaying date in specified format.
            val formatter = SimpleDateFormat(dateFormat)

            // Create a calendar object that will convert the date and time value in milliseconds to date.
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = milliSeconds
            return formatter.format(calendar.time)
        }


    }
}