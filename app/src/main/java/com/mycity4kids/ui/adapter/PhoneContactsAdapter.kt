package com.mycity4kids.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.RecyclerView
import com.crashlytics.android.Crashlytics
import com.mycity4kids.R
import com.mycity4kids.models.ContactModel

/**
 * Created by hemant on 19/7/17.
 */
class PhoneContactsAdapter(private val mListener: RecyclerViewClickListener) :
    RecyclerView.Adapter<PhoneContactsAdapter.ContactsViewHolder>() {
    private var list: List<ContactModel>? = null

    fun setListData(list: List<ContactModel>?) {
        this.list = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        val v0 =
            LayoutInflater.from(parent.context).inflate(R.layout.phone_contact_item, parent, false)
        return ContactsViewHolder(v0, mListener)
    }

    inner class ContactsViewHolder(itemView: View, listener: RecyclerViewClickListener) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal var usernameTextView: TextView
        internal var phoneTextView: TextView
        internal var selectCheckbox: AppCompatCheckBox

        init {
            usernameTextView = itemView.findViewById<View>(R.id.usernameTextView) as TextView
            phoneTextView = itemView.findViewById<View>(R.id.phoneTextView) as TextView
            selectCheckbox = itemView.findViewById<View>(R.id.selectCheckbox) as AppCompatCheckBox
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            mListener.onClick(v, adapterPosition)
        }
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        try {
            holder.usernameTextView.text = list?.get(position)?.name
            holder.phoneTextView.text = list?.get(position)?.number

            if (list?.get(position)?.isSelected == null) {
                holder.selectCheckbox.isChecked = false
            } else {
                holder.selectCheckbox.isChecked = list?.get(position)?.isSelected!!
            }
            holder.selectCheckbox.setOnClickListener { v ->
                val cb: AppCompatCheckBox = v as AppCompatCheckBox
                val contact: ContactModel = cb.getTag() as ContactModel
                contact.isSelected = cb.isChecked
                list?.get(position)?.isSelected = cb.isChecked
            }

            holder.selectCheckbox.tag = list?.get(position)
        } catch (e: Exception) {
            Crashlytics.logException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    interface RecyclerViewClickListener {
        fun onClick(view: View, position: Int)
    }
}
