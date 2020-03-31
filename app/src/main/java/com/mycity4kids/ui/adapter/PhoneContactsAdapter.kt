package com.mycity4kids.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.RecyclerView
import com.crashlytics.android.Crashlytics
import com.mycity4kids.R
import com.mycity4kids.models.ContactModel
import java.util.Locale
import kotlin.collections.ArrayList

/**
 * Created by hemant on 19/7/17.
 */
class PhoneContactsAdapter(private val mListener: RecyclerViewClickListener) :
    RecyclerView.Adapter<PhoneContactsAdapter.ContactsViewHolder>(), Filterable {
    private var list: List<ContactModel>? = null
    private var filteredList: List<ContactModel>? = null

    fun setListData(list: List<ContactModel>?) {
        this.list = list
        this.filteredList = list
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
            holder.usernameTextView.text = filteredList?.get(position)?.name
            holder.phoneTextView.text = filteredList?.get(position)?.number

            if (filteredList?.get(position)?.isSelected == null) {
                holder.selectCheckbox.isChecked = false
            } else {
                holder.selectCheckbox.isChecked = filteredList?.get(position)?.isSelected!!
            }
            holder.selectCheckbox.setOnClickListener { v ->
                val cb: AppCompatCheckBox = v as AppCompatCheckBox
                val contact: ContactModel = cb.getTag() as ContactModel
                contact.isSelected = cb.isChecked
                filteredList?.get(position)?.isSelected = cb.isChecked
            }

            holder.selectCheckbox.tag = filteredList?.get(position)
        } catch (e: Exception) {
            Crashlytics.logException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
    }

    override fun getItemCount(): Int {
        return filteredList?.size ?: 0
    }

    interface RecyclerViewClickListener {
        fun onClick(view: View, position: Int)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    filteredList = list
                } else {
                    val localFilteredList: MutableList<ContactModel> = ArrayList()
                    list?.let {
                        for (row in it) {
                            // name match condition. this might differ depending on your requirement
                            // here we are looking for name or phone number match
                            if (row.name?.toLowerCase(Locale.getDefault())?.contains(
                                    charString.toLowerCase(
                                        Locale.getDefault()
                                    )
                                )!! || row.number?.contains(charSequence)!!
                            ) {
                                localFilteredList.add(row)
                            }
                        }
                    }

                    filteredList = localFilteredList
                }
                val filterResults =
                    FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence,
                filterResults: FilterResults
            ) {
                filteredList = filterResults.values as List<ContactModel>
                notifyDataSetChanged()
            }
        }
    }
}
