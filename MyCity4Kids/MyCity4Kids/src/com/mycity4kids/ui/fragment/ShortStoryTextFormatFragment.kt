package com.mycity4kids.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.kelltontech.ui.BaseFragment
import com.mycity4kids.R
import com.mycity4kids.ui.activity.ShortStoriesCardActivity

class ShortStoryTextFormatFragment : BaseFragment() {

    private lateinit var textSizeUp: ImageView
    private lateinit var textSizeDown: ImageView
    private lateinit var textAlignLeft: ImageView
    private lateinit var textAlignCenter: ImageView
    private lateinit var textAlignRight: ImageView

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
            (context as ShortStoriesCardActivity).textAlign("LEFT")
        }

        textAlignCenter.setOnClickListener {
            (context as ShortStoriesCardActivity).textAlign("CENTER")
        }

        textAlignRight.setOnClickListener {
            (context as ShortStoriesCardActivity).textAlign("RIGHT")
        }

        return view
    }
}