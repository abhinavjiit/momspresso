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
import kotlinx.android.synthetic.main.analytics_dialog_fragment.*

class AnalyticsDialogFragment : DialogFragment() {

    private var screenName: String? = ""
    private var objective: String? = ""
    private var eventName: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        screenName = arguments?.getString("screen")
        objective = arguments?.getString("objective")
        eventName = arguments?.getString("event")
        return inflater.inflate(
            R.layout.analytics_dialog_fragment, container,
            false
        )
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
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        screenTextView.text = screenName
        objectiveTextView.text = objective
        eventTextView.text = eventName
    }
}
