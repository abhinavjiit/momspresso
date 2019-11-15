package com.mycity4kids.ui.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.mycity4kids.R

class AddCollectionPopUpDialogFragment : DialogFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.add_collection_name_pop_up, container,
                false)

        val bundle = arguments
        return rootView
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            //            dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_bg_rounded_corners));
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        }

    }
}