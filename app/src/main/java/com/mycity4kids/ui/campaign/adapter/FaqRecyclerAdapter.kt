package com.mycity4kids.ui.campaign.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mycity4kids.R
import com.mycity4kids.models.campaignmodels.QuestionAnswerResponse
import kotlinx.android.synthetic.main.fragment_item.view.*

class FaqRecyclerAdapter(
    private val faqsList: List<QuestionAnswerResponse>,
    private val context: Context
) :
    RecyclerView.Adapter<FaqRecyclerAdapter.ViewHolder>() {
    private val mOnClickListener: View.OnClickListener
    private var faqList: List<QuestionAnswerResponse>

    init {
        faqList = faqsList
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as QuestionAnswerResponse
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = faqList.get(position)
        holder.textQuestion.text = item.question
        holder.textAnswer.text = item.answer

        holder.one.setOnClickListener {
            if (holder.textAnswer.visibility == View.GONE) {
                holder.textAnswer.visibility = View.VISIBLE
                holder.imageDown.rotation = 180f
            } else {
                holder.textAnswer.visibility = View.GONE
                holder.imageDown.rotation = 0f
            }
        }

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = faqList.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val textQuestion: TextView = mView.textQuestion
        val textAnswer: TextView = mView.textAnswer
        val imageDown: ImageView = mView.imageDown
        val one: RelativeLayout = mView.one
    }
}
