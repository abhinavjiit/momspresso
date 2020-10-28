package com.mycity4kids.ui.fragment


import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.ui.activity.BloggerGoldActivity
import com.mycity4kids.utils.AppUtils
import com.squareup.picasso.Picasso

class BloggerGoldDialogFragment : DialogFragment() {

    private lateinit var imageView: ImageView
    private lateinit var cancel: ImageView
    private lateinit var knowMoreCardView: CardView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.blogger_gold_dialog_fragment, container, false)
        imageView = view.findViewById(R.id.imageView)
        cancel = view.findViewById(R.id.cancel)
        knowMoreCardView = view.findViewById(R.id.knowMoreCardView)
        SharedPrefUtils.setBloggerGoldPopShown(BaseApplication.getAppContext(), true)

        Picasso.get().load(AppUtils.getBloggerGoldImageUrl()).into(imageView)

        knowMoreCardView.setOnClickListener {
            Utils.shareEventTracking(
                activity,
                "Home screen",
                "BirthdayBonanza_Android",
                "HomePopUp_BB"
            )
            val intent = Intent(activity, BloggerGoldActivity::class.java)
            startActivity(intent)
            dismiss()
        }

        cancel.setOnClickListener {
            dismiss()
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }
}