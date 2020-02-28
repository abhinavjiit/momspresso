package com.mycity4kids.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.squareup.picasso.Picasso

class CollectionImageThumbnailAdapter(var activity: Context) : BaseAdapter() {
    private var mInflater: LayoutInflater = BaseApplication.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    var context: Context = activity
    private var collectionsImageList = ArrayList<String>()
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        val viewHolder: ViewHolder
        if (view == null) {
            view = mInflater.inflate(R.layout.collection_thumbnail_change_adapter, parent, false)
            viewHolder = ViewHolder()
            viewHolder.tagImageView = view.findViewById<ImageView>(R.id.tagImageView)
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }
        try {
            Picasso.get().load(collectionsImageList[position]).placeholder(R.drawable.default_article).error(R.drawable.default_article)
                    .fit().into(viewHolder.tagImageView)
        } catch (e: Exception) {
            viewHolder.tagImageView?.setImageDrawable(ContextCompat.getDrawable(BaseApplication.getAppContext(), R.drawable.default_article))
        }
        return view!!
    }

    override fun getItem(position: Int): Any {
        return collectionsImageList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return collectionsImageList.size
    }

    fun setImages(dataList: ArrayList<String>) {
        collectionsImageList = dataList
    }

    class ViewHolder {

        var tagImageView: ImageView? = null
    }
}