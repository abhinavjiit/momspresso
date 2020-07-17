package com.mycity4kids.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mycity4kids.R
import com.mycity4kids.ui.adapter.SuggestedTopicsRecyclerAdapter.SuggestedTopicsViewHolder
import com.mycity4kids.widget.MomspressoButtonWidget

class SuggestedTopicsRecyclerAdapter(
    private val suggestedTopicsList: ArrayList<String>,
    val recyclerViewClickListener: RecyclerViewClickListener
) :
    RecyclerView.Adapter<SuggestedTopicsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestedTopicsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.suggested_topic_item,
            parent,
            false
        )
        return SuggestedTopicsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return suggestedTopicsList.size
    }

    override fun onBindViewHolder(holder: SuggestedTopicsViewHolder, position: Int) {
        if (position % 5 == 0) {
            holder.suggestedTopicWidget.setBackgroundColor(
                ContextCompat.getColor(
                    holder.suggestedTopicWidget.context,
                    R.color.suggested_topic_bg_1
                )
            )
            holder.suggestedTopicWidget.setBorderColor(
                ContextCompat.getColor(
                    holder.suggestedTopicWidget.context,
                    R.color.suggested_topic_border_1
                )
            )
        } else if (position % 5 == 1) {
            holder.suggestedTopicWidget.setBackgroundColor(
                ContextCompat.getColor(
                    holder.suggestedTopicWidget.context,
                    R.color.suggested_topic_bg_2
                )
            )
            holder.suggestedTopicWidget.setBorderColor(
                ContextCompat.getColor(
                    holder.suggestedTopicWidget.context,
                    R.color.suggested_topic_border_2
                )
            )
        } else if (position % 5 == 2) {
            holder.suggestedTopicWidget.setBackgroundColor(
                ContextCompat.getColor(
                    holder.suggestedTopicWidget.context,
                    R.color.suggested_topic_bg_3
                )
            )
            holder.suggestedTopicWidget.setBorderColor(
                ContextCompat.getColor(
                    holder.suggestedTopicWidget.context,
                    R.color.suggested_topic_border_3
                )
            )
        } else if (position % 5 == 3) {
            holder.suggestedTopicWidget.setBackgroundColor(
                ContextCompat.getColor(
                    holder.suggestedTopicWidget.context,
                    R.color.suggested_topic_bg_4
                )
            )
            holder.suggestedTopicWidget.setBorderColor(
                ContextCompat.getColor(
                    holder.suggestedTopicWidget.context,
                    R.color.suggested_topic_border_4
                )
            )
        } else {
            holder.suggestedTopicWidget.setBackgroundColor(
                ContextCompat.getColor(
                    holder.suggestedTopicWidget.context,
                    R.color.suggested_topic_bg_5
                )
            )
            holder.suggestedTopicWidget.setBorderColor(
                ContextCompat.getColor(
                    holder.suggestedTopicWidget.context,
                    R.color.suggested_topic_border_5
                )
            )
        }
        holder.suggestedTopicWidget.setText(suggestedTopicsList[position])
    }

    inner class SuggestedTopicsViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val suggestedTopicWidget: MomspressoButtonWidget =
            itemView.findViewById<View>(R.id.suggestedTopicWidget) as MomspressoButtonWidget

        init {
            suggestedTopicWidget.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            recyclerViewClickListener.onSuggestedTopicClick()
        }
    }

    interface RecyclerViewClickListener {
        fun onSuggestedTopicClick()
    }
}
