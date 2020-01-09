package com.mycity4kids.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseFragment
import com.mycity4kids.R
import com.mycity4kids.models.response.ShortShortiesBackgroundThumbnail
import com.mycity4kids.ui.activity.ShortStoriesCardActivity
import com.mycity4kids.ui.adapter.ShortStoriesAdapter

class ShortStoryTextFormatFragment : BaseFragment() {

    private lateinit var textSizeUp: TextView
    private lateinit var textSizeDown: TextView
    private lateinit var textAlignLeft: TextView
    private lateinit var textAlignCenter: TextView
    private lateinit var textAlignRight: TextView

    override fun updateUi(response: Response?) {

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.short_story_text_format_fragment, container, false)
        textSizeUp =view.findViewById(R.id.textsize_up)
        textSizeDown =view.findViewById(R.id.textsize_down)
        textAlignLeft =view.findViewById(R.id.align_left)
        textAlignCenter =view.findViewById(R.id.align_center)
        textAlignRight =view.findViewById(R.id.align_right)

        textSizeUp.setOnClickListener {
            (context as ShortStoriesCardActivity).increaseTextSize()
        }

        textSizeDown.setOnClickListener {
            (context as ShortStoriesCardActivity).decreaseTextSize()
        }

        textAlignLeft.setOnClickListener {
            (context as ShortStoriesCardActivity).textAlign(0)
        }

        textAlignCenter.setOnClickListener {
            (context as ShortStoriesCardActivity).textAlign(1)
        }

        textAlignRight.setOnClickListener {
            (context as ShortStoriesCardActivity).textAlign(2)
        }

        return view
    }
}