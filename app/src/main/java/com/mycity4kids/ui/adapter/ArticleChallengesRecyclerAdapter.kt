package com.mycity4kids.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mycity4kids.R
import com.mycity4kids.models.Topics
import com.mycity4kids.ui.adapter.ArticleChallengesRecyclerAdapter.ArticleChallengesViewHolder
import java.util.ArrayList

class ArticleChallengesRecyclerAdapter(
    private val context: Context,
    private val articleChallengesList: ArrayList<Topics>
) : RecyclerView.Adapter<ArticleChallengesViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ArticleChallengesViewHolder {
        val view =
            (parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                .inflate(R.layout.article_challenge_item, parent, false)
        return ArticleChallengesViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ArticleChallengesViewHolder,
        position: Int
    ) {
        holder.noChallengeAddedText.visibility = View.GONE
        holder.useThePictureTextView.visibility = View.GONE
        holder.useThePictureTextView.setText(R.string.use_picture_word_to_upload_one)
        holder.StorytextViewLayout.visibility = View.VISIBLE
        holder.yourStoryTextView.visibility = View.GONE
        holder.yourStoryTextView.setText(R.string.choose_challenge_label)
    }

    override fun getItemCount(): Int {
        return articleChallengesList.size
    }

    inner class ArticleChallengesViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val mainView: RelativeLayout
        val rootView: RelativeLayout
        val titleContainer: RelativeLayout
        val storyTitleTextView: TextView
        val storyBodyTextView: TextView
        val getStartedTextView: TextView
        val imageBody: ImageView
        val storytitle: TextView
        val titleTextUnderLine: View
        val previousAndThisWeekTextView: TextView
        val yourStoryTextView: TextView
        val StorytextViewLayout: LinearLayout
        val useThePictureTextView: TextView
        val noChallengeAddedText: TextView
        val liveTextViewVideoChallenge: TextView
        val challengeNameTextMomVlog: TextView
        override fun onClick(view: View) {}

        init {
            rootView =
                itemView.findViewById<View>(R.id.rootView) as RelativeLayout
            noChallengeAddedText =
                itemView.findViewById<View>(R.id.noChallengeAddedText) as TextView
            mainView =
                itemView.findViewById<View>(R.id.mainView) as RelativeLayout
            titleContainer =
                itemView.findViewById<View>(R.id.titleContainer) as RelativeLayout
            storyBodyTextView =
                itemView.findViewById<View>(R.id.storyBodyTextView) as TextView
            storyTitleTextView =
                itemView.findViewById<View>(R.id.storyTitleTextView) as TextView
            imageBody =
                itemView.findViewById<View>(R.id.imageBody) as ImageView
            getStartedTextView =
                itemView.findViewById<View>(R.id.getStartedTextView) as TextView
            storytitle =
                itemView.findViewById<View>(R.id.storytitle) as TextView
            titleTextUnderLine =
                itemView.findViewById(R.id.TittleText_Line) as View
            previousAndThisWeekTextView =
                itemView.findViewById<View>(R.id.this_week_previous_week_textView) as TextView
            yourStoryTextView =
                itemView.findViewById<View>(R.id.your_100_word_story_text) as TextView
            StorytextViewLayout =
                itemView.findViewById<View>(R.id.whats_your_story_text_linear_layout) as LinearLayout
            useThePictureTextView =
                itemView.findViewById<View>(R.id.use_the_picture_textView) as TextView
            liveTextViewVideoChallenge =
                itemView.findViewById<View>(R.id.liveTextViewVideoChallenge) as TextView
            challengeNameTextMomVlog =
                itemView.findViewById<View>(R.id.challengeNameTextMomVlog) as TextView
            getStartedTextView.setOnClickListener(this)
            mainView.setOnClickListener(this)
        }
    }
}
