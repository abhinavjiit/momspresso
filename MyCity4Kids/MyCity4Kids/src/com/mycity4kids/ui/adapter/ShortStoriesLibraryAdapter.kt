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
import com.mycity4kids.models.response.Categories
import com.mycity4kids.models.response.Images
import com.mycity4kids.models.response.ShortShortiesBackgroundThumbnail
import com.squareup.picasso.Picasso

class ShortStoriesLibraryAdapter(activity: Context, showCategory: Boolean) : BaseAdapter() {

    private var mInflater: LayoutInflater = BaseApplication.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    var context: Context = activity
    var showCategory: Boolean = showCategory
    private var userCollectionsTopicList = ArrayList<Images>()
    private var libraryCollectionList = ArrayList<Categories>()


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        val viewHolder: ViewHolder
        if (view == null) {
            view = mInflater.inflate(R.layout.short_stories_thumbnail_adapter, parent, false)
            viewHolder = ViewHolder()
            viewHolder.tagImageView = view.findViewById<ImageView>(R.id.tagImageView)
            viewHolder.topicsNameTextView = view.findViewById<TextView>(R.id.topicsNameTextView)
            view.tag = viewHolder

        } else {
            viewHolder = view.tag as ViewHolder
        }
        if (showCategory) {
            viewHolder.topicsNameTextView?.visibility = View.VISIBLE
        }
        try {
            Picasso.with(BaseApplication.getAppContext()).load(libraryCollectionList[position].image_url).placeholder(R.drawable.default_article).error(R.drawable.default_article)
                    .fit().into(viewHolder.tagImageView)
        } catch (e: Exception) {
            viewHolder.tagImageView?.setImageDrawable(ContextCompat.getDrawable(BaseApplication.getAppContext(), R.drawable.default_article))
        }
        viewHolder.topicsNameTextView?.text = libraryCollectionList.get(position).name
        viewHolder.tagImageView?.clipToOutline = true
        return view!!
    }

    fun getUserColletions(collectionsTopics: ArrayList<Images>) {
        userCollectionsTopicList = collectionsTopics

    }

    fun getLibraryColletions(collectionsTopics: ArrayList<Categories>) {
        libraryCollectionList = collectionsTopics

    }

    override fun getItem(position: Int): Any {
        return libraryCollectionList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return libraryCollectionList.size
    }

    class ViewHolder {

        var tagImageView: ImageView? = null
        var topicsNameTextView: TextView? = null


    }
}


