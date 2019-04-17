package com.mycity4kids.ui.adapter

import android.content.Context
import android.content.Intent
import android.provider.ContactsContract
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mycity4kids.R
import com.mycity4kids.models.campaignmodels.AllCampaignDataResponse
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.campaign_list_recycler_adapter.view.*

class RewardCampaignAdapter(private var campaignList: ArrayList<AllCampaignDataResponse>, val context : Context) : RecyclerView.Adapter<RewardCampaignAdapter.RewardHolder>()  {

//    private var campaignList: ArrayList<AllCampaignDataResponse> ?= null
    fun updateList(campaignList: ArrayList<AllCampaignDataResponse>){
        this.campaignList = campaignList
    System.out.println("----" + campaignList.size)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardCampaignAdapter.RewardHolder {
//        val inflatedView = parent.inflate(R.layout.campaign_list_recycler_adapter, false)
//        return RewardHolder(inflatedView)

        return RewardHolder(LayoutInflater.from(context).inflate(R.layout.campaign_list_recycler_adapter, parent, false))
    }

    override fun getItemCount(): Int = campaignList.size

    override fun onBindViewHolder(holder: RewardCampaignAdapter.RewardHolder, position: Int) {
        val itemPhoto = campaignList[position]
        holder.bindPhoto(itemPhoto)
    }

    //1
    class RewardHolder(private val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        //2
        private var campaignList: AllCampaignDataResponse? = null

        //3
        init {
            view.setOnClickListener(this)
        }

        fun bindPhoto(campaignList: AllCampaignDataResponse) {
            this.campaignList = campaignList
            Picasso.with(view.context).load(campaignList.image_url).into(view.campaign_header)
        }

        //4
        override fun onClick(v: View) {
            val context = itemView.context
            Log.d("RecyclerView", "CLICK!")
        }

        companion object {
            //5
            private val PHOTO_KEY = "PHOTO"
        }
    }
}