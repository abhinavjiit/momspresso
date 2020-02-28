package com.mycity4kids.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.models.collectionsModels.UserCollectionsModel
import com.squareup.picasso.Picasso

class CollectionsAdapter(activity: Context) : BaseAdapter() {

    private var mInflater: LayoutInflater = BaseApplication.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    var context: Context = activity
    private var userCollectionsTopicList = ArrayList<UserCollectionsModel>()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        val viewHolder: ViewHolder
        if (view == null) {
            view = mInflater.inflate(R.layout.user_collections_grid_view, parent, false)
            viewHolder = ViewHolder()
            viewHolder.tagImageView = view.findViewById<ImageView>(R.id.tagImageView)
            viewHolder.topicsNameTextView = view.findViewById<TextView>(R.id.topicsNameTextView)
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }
        try {
            Picasso.get().load(userCollectionsTopicList[position].imageUrl).placeholder(R.drawable.default_article).error(R.drawable.default_article)
                    .fit().into(viewHolder.tagImageView)
        } catch (e: Exception) {
            viewHolder.tagImageView?.setImageDrawable(ContextCompat.getDrawable(BaseApplication.getAppContext(), R.drawable.default_article))
        }
        viewHolder.topicsNameTextView?.text = userCollectionsTopicList[position].name
        viewHolder.tagImageView?.clipToOutline = true
        return view!!
    }

    fun getUserColletions(collectionsTopics: ArrayList<UserCollectionsModel>) {
        userCollectionsTopicList = collectionsTopics
    }

    override fun getItem(position: Int): Any {
        return userCollectionsTopicList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return userCollectionsTopicList.size
    }

    class ViewHolder {

        var tagImageView: ImageView? = null
        var topicsNameTextView: TextView? = null
    }
}
