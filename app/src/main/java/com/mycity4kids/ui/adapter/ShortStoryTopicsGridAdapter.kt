package com.mycity4kids.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.mycity4kids.R
import com.mycity4kids.models.ExploreTopicsModel
import com.squareup.picasso.Picasso

class ShortStoryTopicsGridAdapter : BaseAdapter() {

    private var topicsData = ArrayList<ExploreTopicsModel>()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        val viewHolder: ViewHolder
        if (view == null) {
            viewHolder = ViewHolder()
            view = LayoutInflater.from(parent!!.context).inflate(R.layout.explore_topics_grid_item, parent, false)
            viewHolder.tagsImageView = view.findViewById(R.id.tagImageView)
            viewHolder.topicsNameTextView = view.findViewById(R.id.topicsNameTextView)
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }
        viewHolder.topicsNameTextView?.text = topicsData[position].display_name.toUpperCase()
        try {
            Picasso.get().load(topicsData[position].extraData[0].categoryBackImage.app).placeholder(R.drawable.default_article).error(R.drawable.default_article).fit().into(viewHolder.tagsImageView)
        } catch (e: Exception) {
            viewHolder.tagsImageView?.setImageResource(R.drawable.default_article)
        }
        return view!!
    }

    override fun getItem(position: Int): Any {
        return topicsData[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return topicsData.size
    }

    fun setTopicsData(topicsData: ArrayList<ExploreTopicsModel>) {
        this.topicsData = topicsData
    }

    internal inner class ViewHolder {
        var tagsImageView: ImageView? = null
        var topicsNameTextView: TextView? = null
    }
}
