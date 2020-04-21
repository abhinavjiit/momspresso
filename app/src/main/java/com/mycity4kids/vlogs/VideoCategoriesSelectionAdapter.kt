package com.mycity4kids.vlogs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mycity4kids.R
import com.mycity4kids.models.Topics
import com.squareup.picasso.Picasso

class VideoCategoriesSelectionAdapter(private val listener: RecyclerViewClickListener) :
    RecyclerView.Adapter<VideoCategoriesSelectionAdapter.CategoryViewHolder>() {

    private var categoriesList: ArrayList<Topics>? = null

    fun setListData(categoriesList: ArrayList<Topics>?) {
        this.categoriesList = categoriesList
    }

    override fun getItemCount(): Int {
        return if (categoriesList == null) 0 else categoriesList!!.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val v0 = LayoutInflater.from(parent.context)
            .inflate(R.layout.video_category_selection_item, parent, false)
        return CategoryViewHolder(v0, listener)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.categoryTextView.setText(categoriesList?.get(position)?.display_name)
        // useless backend can't do shit. tired of checking null, empty, json object or json array.
        try {
            Picasso.get().load(categoriesList?.get(position)?.extraData?.get(0)?.categoryBackImage?.app).placeholder(
                R.drawable.default_article
            ).error(R.drawable.default_article)
                .fit().into(holder.categoryImageView)
        } catch (e: Exception) {
            holder.categoryImageView.setImageDrawable(
                ContextCompat.getDrawable(
                    holder.categoryImageView.context, R.drawable.default_article
                )
            )
        }
    }

    inner class CategoryViewHolder internal constructor(
        view: View,
        val listener: RecyclerViewClickListener
    ) : RecyclerView.ViewHolder(view), View.OnClickListener {
        var categoryImageView: ImageView = view.findViewById(R.id.categoryImageView)
        var categoryTextView: TextView = view.findViewById(R.id.categoryTextView)

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onCategoryItemClick(v, adapterPosition)
            }
        }
    }

    interface RecyclerViewClickListener {
        fun onCategoryItemClick(view: View, position: Int)
    }
}
