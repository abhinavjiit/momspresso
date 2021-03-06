package com.mycity4kids.vlogs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mycity4kids.R
import com.mycity4kids.models.Topics

class VideoChallengeSelectionVerticalAdapter(
    private val listener: ContentChallengeSelectionHorizontalAdapter.RecyclerViewClickListener
) : RecyclerView.Adapter<VideoChallengeSelectionVerticalAdapter.ViewHolder>() {
    private var categoryWiseChallengeList: ArrayList<Topics>? = null
    private var source: String = ""

    fun setListData(categoryWiseChallengeList: ArrayList<Topics>) {
        this.categoryWiseChallengeList = categoryWiseChallengeList
    }

    fun setSource(source: String) {
        this.source = source
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.video_challenge_selection_vertical_item,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.challengeRecyclerView.adapter = ContentChallengeSelectionHorizontalAdapter(
            listener,
            categoryWiseChallengeList?.get(position)?.id,
            categoryWiseChallengeList?.get(position)?.child!!,
            source
        )
        holder.challengeRecyclerView.layoutManager = LinearLayoutManager(
            holder.challengeRecyclerView.context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        holder.challengeRecyclerView.setHasFixedSize(true)
        holder.categoryTextView.text = categoryWiseChallengeList?.get(position)?.display_name
    }

    override fun getItemCount(): Int {
        return if (categoryWiseChallengeList == null) 0 else categoryWiseChallengeList!!.size
    }

    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var challengeRecyclerView: RecyclerView =
            itemView.findViewById<View>(R.id.challengeRecyclerView) as RecyclerView
        var categoryTextView: TextView =
            itemView.findViewById<View>(R.id.categoryTextView) as TextView
    }
}
