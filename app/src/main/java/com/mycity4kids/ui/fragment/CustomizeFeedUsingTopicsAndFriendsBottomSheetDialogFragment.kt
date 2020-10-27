package com.mycity4kids.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mycity4kids.R
import com.mycity4kids.ui.activity.FindFbFriendsActivity
import com.mycity4kids.widget.MomspressoButtonWidget

class CustomizeFeedUsingTopicsAndFriendsBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private lateinit var chooseTopicsTextView: MomspressoButtonWidget
    private lateinit var findFriendsTextView: MomspressoButtonWidget
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_layout, container, false)
        chooseTopicsTextView = view.findViewById(R.id.chooseTopicsTextView)
        findFriendsTextView = view.findViewById(R.id.findFriendsTextView)
        chooseTopicsTextView.setOnClickListener {
            val chooseContentTopicsBottomSheetDialogFragment =
                ChooseContentTopicsBottomSheetDialogFragment()
            chooseContentTopicsBottomSheetDialogFragment.show(
                activity?.supportFragmentManager!!,
                "choose_topic"
            )
            dismiss()
        }
        findFriendsTextView.setOnClickListener {
            activity?.let {
                val intent = Intent(it, FindFbFriendsActivity::class.java)
                startActivity(intent)
            }
            dismiss()
        }
        return view
    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }
}