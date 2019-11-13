package com.mycity4kids.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.mycity4kids.R


class RecyclerViewAdapter(private var list: ArrayList<Model>, private var mListener: RecyclerViewClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), KmStickyListener {
    override fun getItemCount(): Int {
        return list.size
    }

    private val HEADER = 0
    private val CONTENT = 1

    override fun getHeaderPositionForItem(itemPosition: Int?): Int {
        var headerPosition: Int = 0
        for (i in itemPosition!! downTo 1) {
            if (isHeader(i)) {
                headerPosition = i
                return headerPosition
            }
        }
        return headerPosition
    }

    override fun getHeaderLayout(headerPosition: Int?): Int {
//        return if (headerPosition == 0) {
        return R.layout.item_header1
//        } else {
//            R.layout.item_header2
//        }
    }

    override fun bindHeaderData(header: View?, headerPosition: Int?) {

    }

    override fun isHeader(itemPosition: Int?): Boolean {
        return list.get(itemPosition!!).type == HEADER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView: View
        return if (viewType == HEADER) {
            itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_header1, parent, false)
            HeaderViewHolder(itemView)
        } else {
            itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_header2, parent, false)
            ProfileViewHolder(itemView, mListener)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == HEADER) {
            (holder as HeaderViewHolder).bind(list[position])
        } else {
            (holder as ProfileViewHolder).bind(list[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].type
    }

    internal inner class HeaderViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView

        init {
            title = itemView.findViewById<View>(R.id.title_header) as TextView
        }

        fun bind(model: Model) {
            title.text = model.title
        }
    }

    inner class ProfileViewHolder(itemView: View, listener: RecyclerViewClickListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var post: TextView

        init {
            post = itemView.findViewById<View>(R.id.title_post) as TextView
        }

        fun bind(model: Model) {
            post.text = model.title
        }

        override fun onClick(v: View) {
            mListener.onClick(v, adapterPosition)
        }
    }

    interface RecyclerViewClickListener {
        fun onClick(view: View, position: Int)
    }
}