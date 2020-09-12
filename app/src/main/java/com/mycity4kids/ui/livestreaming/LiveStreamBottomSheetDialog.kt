package com.mycity4kids.ui.livestreaming

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mycity4kids.R

class LiveStreamBottomSheetDialog :
    BottomSheetDialogFragment() {
    lateinit var contentTextView: TextView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(
            R.layout.live_stream_bottom_sheet_layout,
            container,
            false
        )
        val collapseSheetImageView =
            v.findViewById<ImageView>(R.id.collapseSheetImageView)
        contentTextView =
            v.findViewById(R.id.liveDescTextView)
        if (arguments != null) {
            val content = arguments!!.getString("content")
            contentTextView.setText(content)
        }
        collapseSheetImageView.setOnClickListener { v1: View? -> dismiss() }
        return v
    }
}
