package com.mycity4kids.ui.livestreaming

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.facebook.FacebookSdk.getApplicationContext
import com.mycity4kids.R
import com.mycity4kids.base.BaseFragment
import com.mycity4kids.utils.AppUtils

class UpcomingLiveAboutTabFragment : BaseFragment() {

    lateinit var guestContainer: LinearLayout
    lateinit var titleTextView: TextView
    lateinit var descTextView: TextView
    lateinit var guestLabel: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.upcoming_lives_about_fragment,
            container,
            false
        )

        titleTextView = view.findViewById(R.id.titleTextView)
        descTextView = view.findViewById(R.id.descTextView)
        guestLabel = view.findViewById(R.id.guestLabel)
        guestContainer = view.findViewById(R.id.guestContainer)
        val item = arguments?.getParcelable<LiveStreamResult>("item")
        titleTextView.text = item?.name
        descTextView.text = item?.description

        if (item?.attendees != null && item.attendees.isNotEmpty()) {
            guestLabel.visibility = View.VISIBLE
            addGuestFields(item.attendees)
        } else {
            guestLabel.visibility = View.GONE
        }
        return view
    }

    private fun addGuestFields(attendees: List<Attendee>) {
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        params.setMargins(
            AppUtils.dpTopx(16.0f),
            AppUtils.dpTopx(20.0f),
            AppUtils.dpTopx(16.0f),
            0
        )
        for (i in attendees.indices) {
            val layoutInflater =
                getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            val guestView = layoutInflater.inflate(R.layout.live_attendes_item, null)
            guestView.layoutParams = params
            guestView.findViewById<TextView>(R.id.guestNameTextView).text = attendees[i].first_name
            guestView.findViewById<TextView>(R.id.guestDesignationTextView).text =
                attendees[i].designation
            guestView.findViewById<TextView>(R.id.guestDescriptionTextView).text =
                attendees[i].about
            guestContainer.addView(guestView)
        }
    }
}
