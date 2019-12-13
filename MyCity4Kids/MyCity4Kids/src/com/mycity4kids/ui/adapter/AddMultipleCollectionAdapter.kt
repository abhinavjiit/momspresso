package com.mycity4kids.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.models.response.ArticleListingResult
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.add_multiple_collection_item.view.*
import kotlinx.android.synthetic.main.user_collection_items_list_adapter.view.articleAuthorName
import kotlinx.android.synthetic.main.user_collection_items_list_adapter.view.articleImageView
import kotlinx.android.synthetic.main.user_collection_items_list_adapter.view.articleTitleTextView
import kotlinx.android.synthetic.main.user_collection_items_list_adapter.view.articleVideoShortStoryIcon
import kotlinx.android.synthetic.main.user_collection_items_list_adapter.view.root
import kotlinx.android.synthetic.main.user_collection_items_list_adapter.view.viewCountTextView

class AddMultipleCollectionAdapter(var context: Context, var recyclerViewClickListner: RecyclerViewClick) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var dataList = ArrayList<ArticleListingResult>()
    private var mInflater: LayoutInflater = BaseApplication.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view = mInflater.inflate(R.layout.add_multiple_collection_item, parent, false)
        return ViewHolderCollection(view, recyclerViewClickListner)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolderCollection) {
            if (null != dataList[position].imageUrl) {
                Picasso.with(context).load(dataList[position].imageUrl.thumbMin).placeholder(R.drawable.default_article).error(R.drawable.default_article).into((holder).articleImageView)
            } else {
                (holder).articleImageView.setBackgroundResource(R.drawable.article_default)
            }
            holder.articleTitleTextView.text = dataList[position].title
            holder.viewCountTextView.text = dataList[position].articleCount
            holder.articleAuthorName.text = dataList[position].userName

            if (dataList[position].isCollectionItemSelected) {
                holder.checkBoxImageView.setImageResource(R.drawable.ic_done)
            } else {
                holder.checkBoxImageView.setImageResource(R.drawable.ic_rectangle)

            }

            holder.root.setOnClickListener {
                recyclerViewClickListner.onclick(position)
            }
        }


    }

    fun setListData(dataList: ArrayList<ArticleListingResult>) {
        this.dataList = dataList
    }

    class ViewHolderCollection(mView: View, recyclerViewClickListner: RecyclerViewClick) : RecyclerView.ViewHolder(mView) {
        var articleTitleTextView: TextView = mView.articleTitleTextView
        var articleImageView: ImageView = mView.articleImageView
        var articleAuthorName: TextView = mView.articleAuthorName
        var viewCountTextView: TextView = mView.viewCountTextView
        var articleVideoShortStoryIcon: ImageView = mView.articleVideoShortStoryIcon
        var root: RelativeLayout = mView.root
        var checkBoxImageView: ImageView = mView.checkBoxImageView

    }

    interface RecyclerViewClick {
        fun onclick(position: Int)
    }


}