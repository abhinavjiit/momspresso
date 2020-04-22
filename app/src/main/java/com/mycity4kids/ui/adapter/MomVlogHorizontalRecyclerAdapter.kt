package com.mycity4kids.ui.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mycity4kids.R
import com.mycity4kids.models.Topics
import java.util.ArrayList
import kotlinx.android.synthetic.main.mom_vlog_subcategory_layout.view.*

class MomVlogHorizontalRecyclerAdapter(
    private val clickListener: ClickListener,
    val context: Context
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var subCategoriesList = ArrayList<Topics>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.mom_vlog_subcategory_layout, parent, false)
        return MomVlogSubCategories(view, clickListener)
    }

    override fun getItemCount(): Int {
        return subCategoriesList.size
    }

    fun setListData(list: ArrayList<Topics>) {
        subCategoriesList = list
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MomVlogSubCategories) {
            try {
                if (subCategoriesList[position].selectedSubCategory) {
                    holder.subCategoryTextView.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.app_red
                        )
                    )
                    val myGrad: GradientDrawable =
                        holder.subCategoryTextView.background as GradientDrawable
                    myGrad.setStroke(2, Color.RED)

                    /* holder.subCategoryTextView.background =
                         context.getDrawable(R.drawable.update_profile_bg)*/
                } else {
                    holder.subCategoryTextView.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.ad_author_name_text
                        )
                    )
                    val myGrad: GradientDrawable =
                        holder.subCategoryTextView.background as GradientDrawable
                    myGrad.setStroke(
                        2,
                        ContextCompat.getColor(context, R.color.ad_author_name_text)
                    )
                }

                holder.subCategoryTextView.text = subCategoriesList[position].display_name

                holder.subCategoryTextView.setOnClickListener {
                    clickListener.onRecyclerClick(position)
                }
            } catch (e: Exception) {
            }
        }
    }

    class MomVlogSubCategories(view: View, clickListener: ClickListener) :
        RecyclerView.ViewHolder(view) {
        val subCategoryTextView: TextView = view.subCategoryTextView
    }

    interface ClickListener {
        fun onRecyclerClick(position: Int)
    }
}
