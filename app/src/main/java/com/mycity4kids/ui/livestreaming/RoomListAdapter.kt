package com.mycity4kids.ui.livestreaming

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mycity4kids.R
import kotlinx.android.synthetic.main.room_item.view.*

class RoomListAdapter(
    var clickListener: RecyclerViewItemClickListener
) :
    RecyclerView.Adapter<RoomListAdapter.ViewHolder>() {
    private lateinit var roomList: ArrayList<String>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.room_item,
            parent,
            false
        )
        return ViewHolder(view)
    }

    fun setListData(listData: ArrayList<String>) {
        roomList = listData
    }

    override fun getItemCount(): Int {
        return roomList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.roomNameTextView.text = roomList[position]
    }

    inner class ViewHolder internal constructor(
        view: View
    ) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

        val roomNameTextView: TextView = view.roomNameTextView

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            view?.let {
                clickListener.onClick(it, adapterPosition)
            }
        }
    }

    interface RecyclerViewItemClickListener {
        fun onClick(view: View, position: Int)
    }
}
