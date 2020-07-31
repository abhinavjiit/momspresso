package com.mycity4kids.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.mycity4kids.R
import com.mycity4kids.models.response.NotificationCenterResult
import kotlinx.android.synthetic.main.notification_setting_adapter_layout.view.*

class NotificationCategoryAdapter(val clickListener: RecyclerViewClick) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var notificationCategory: ArrayList<NotificationCenterResult>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.notification_setting_adapter_layout,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if (notificationCategory == null) 0 else notificationCategory?.size!!
    }


    fun setNotificationCategoryListData(notificationCategory: ArrayList<NotificationCenterResult>) {
        this.notificationCategory = notificationCategory
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.apply {
                categoryTextView.text = notificationCategory?.get(position)?.name
                switchTextView.isChecked = notificationCategory?.get(position)?.disabled!! == false
            }
        }
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryTextView: TextView = view.categoryTextView
        val switchTextView: SwitchCompat = view.switchTextView

        init {
            switchTextView.setOnClickListener {
                clickListener.onRecyclerClick(
                    adapterPosition,
                    notificationCategory?.get(adapterPosition)?.id!!,
                    notificationCategory?.get(adapterPosition)?.disabled!!
                )
            }
        }
    }

    interface RecyclerViewClick {
        fun onRecyclerClick(position: Int, id: String, notificationOn: Boolean)
    }

}