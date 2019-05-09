package com.mycity4kids.ui.adapter

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mycity4kids.R
import com.mycity4kids.models.campaignmodels.CampaignDetailDeliverable
import kotlinx.android.synthetic.main.deliverable_list_recycler_adapter.view.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class CampaignDetailAdapter(private var deliverableList: List<List<CampaignDetailDeliverable>>?, val context: FragmentActivity?) : RecyclerView.Adapter<CampaignDetailAdapter.RewardHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CampaignDetailAdapter.RewardHolder {
        return RewardHolder(LayoutInflater.from(context).inflate(R.layout.deliverable_list_recycler_adapter, parent, false))
    }

    override fun getItemCount(): Int =
            deliverableList!!.get(0).size

    override fun onBindViewHolder(holder: CampaignDetailAdapter.RewardHolder, position: Int) {
//        val itemPhoto = deliverableList!!.get(0).[position]
        holder.bindPhoto(deliverableList!!.get(0))
    }

    inner class RewardHolder(private val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        private var deliverableList: List<CampaignDetailDeliverable>? = null
        private val urlPattern = Pattern.compile(
                "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                        + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                        + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
                Pattern.CASE_INSENSITIVE or Pattern.MULTILINE or Pattern.DOTALL)
        private var spannable: SpannableString? = null

        init {
            view.setOnClickListener(this)
        }

        fun bindPhoto(deliverableList: List<CampaignDetailDeliverable>) {
            this.deliverableList = deliverableList
            val builder = StringBuilder()
            if (deliverableList.get(position).instructions!!.size > 0) {
                for (instructions in deliverableList.get(position).instructions!!) {
                    if (instructions.isNotEmpty())
                        builder.append("\u2022" + "  " + instructions + "\n")
                }
                if (!builder.isEmpty()) {
                    getOffset(builder.toString())
//                (view.deliverable_text).setText(builder.toString())
                    (view.view).visibility = View.VISIBLE
                    (view.deliverable_text).visibility = View.VISIBLE
                    (view.deliverable_header).visibility = View.VISIBLE
                    (view.deliverable_text).setMovementMethod(LinkMovementMethod.getInstance());
                    (view.deliverable_header).setText(deliverableList.get(position).name)
                } else {
                    (view.view).visibility = View.GONE
                    (view.deliverable_text).visibility = View.GONE
                    (view.deliverable_header).visibility = View.GONE
                }
            }
        }

        //4
        override fun onClick(v: View) {
            //val context = itemView.context
//            (context as CampaignContainerActivity).addCampaginDetailFragment(campaignList!!.id)
        }


        private fun getOffset(instruction: String) {
            val matcher = urlPattern.matcher(instruction)
            var matchStart: Int? = null
            var matchEnd: Int? = null
            while (matcher.find()) {
                matchStart = matcher.start(1)
                matchEnd = matcher.end()
            }
            spannable = SpannableString(instruction)
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(p0: View?) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(instruction.substring(matchStart!!, matchEnd!!)))
                    context!!.startActivity(intent)
                }
            }
            if (matchStart != null && matchEnd != null) {
                spannable!!.setSpan(clickableSpan, matchStart, matchEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            };
            (view.deliverable_text).setText(spannable)
            (view.deliverable_text).isClickable = true
            (view.deliverable_text).setMovementMethod(LinkMovementMethod.getInstance());
            (view.deliverable_text).setHighlightColor(Color.TRANSPARENT);
        }

        fun getDate(milliSeconds: Long, dateFormat: String): String {
            // Create a DateFormatter object for displaying date in specified format.
            val formatter = SimpleDateFormat(dateFormat)

            // Create a calendar object that will convert the date and time value in milliseconds to date.
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = milliSeconds
            return formatter.format(calendar.time)
        }
    }
}