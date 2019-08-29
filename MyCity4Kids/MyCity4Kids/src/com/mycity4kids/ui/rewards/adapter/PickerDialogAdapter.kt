package com.mycity4kids.ui.rewards.adapter

import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.mycity4kids.R
import kotlinx.android.synthetic.main.picker_dialog_cell.view.*

import kotlinx.android.synthetic.main.picker_dialog_fragment.view.*

class PickerDialogAdapter(private val selectedValue: ArrayList<String>?,
                          private val popupData: List<String>,
                          private val mListener: onItemClickListener)
    : androidx.recyclerview.widget.RecyclerView.Adapter<PickerDialogAdapter.ViewHolder>() {

    private var mOnClickListener: onItemClickListener
    private var popupAllData = emptyList<String>()
    private var selectedValues = ArrayList<String>()

    init {
        popupAllData = popupData
        selectedValues = selectedValue!!
        mOnClickListener = mListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.picker_dialog_cell, parent, false)
        return ViewHolder(view)
    }
    //AIR_CONDITIONER
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mView.setOnClickListener {
            mOnClickListener.onItemClick(popupAllData.get(holder.adapterPosition))
        }

        holder.isSelect.setOnClickListener {
            mOnClickListener.onItemClick(popupAllData.get(holder.adapterPosition))
        }
//        if(selectedValues.contains(popupAllData.get(holder.adapterPosition))){
////            holder.isSelect.isChecked = true
////        }else{
////            holder.isSelect.isChecked= false
////        }

        Log.e("iscontion true", selectedValues.contains(popupAllData.get(holder.adapterPosition)).toString() )
        holder.isSelect.isChecked = selectedValues.contains(popupAllData.get(holder.adapterPosition))
        holder.textValue.text = popupAllData.get(holder.adapterPosition)
    }

    override fun getItemCount(): Int = popupAllData.size

    inner class ViewHolder(val mView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(mView) {
        val isSelect: CheckBox = mView.checkIsSelect
        val textValue: TextView = mView.textValue

    }

    interface onItemClickListener {
        fun onItemClick(selectedValue: String)
    }

}
