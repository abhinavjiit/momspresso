package com.mycity4kids.profile

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.kelltontech.utils.ToastUtils
import com.mycity4kids.R
import com.mycity4kids.constants.Constants
import com.mycity4kids.utils.AppUtils
import com.squareup.picasso.Picasso

class CrownDialogFragment : DialogFragment(), View.OnClickListener {

    private lateinit var crownImageView: ImageView
    private lateinit var crownBgImageView: ImageView
    private lateinit var crownTitleTextView: TextView
    private lateinit var crownDescTextView: TextView
    private lateinit var whatsappShareImageView: ImageView
    private lateinit var facebookShareImageView: ImageView
    private lateinit var instagramShareImageView: ImageView
    private lateinit var genericShareImageView: ImageView
    private lateinit var shareContainer: ConstraintLayout

    var userId: String? = null
    var crownData: Crown? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.crown_dialog_fragment, container,
                false)

        crownImageView = rootView.findViewById(R.id.crownImageView)
        crownBgImageView = rootView.findViewById(R.id.crownBgImageView)
        crownTitleTextView = rootView.findViewById(R.id.crownTitleTextView)
        crownDescTextView = rootView.findViewById(R.id.crownDescTextView)
        shareContainer = rootView.findViewById(R.id.shareContainer)
        whatsappShareImageView = rootView.findViewById(R.id.whatsappShareImageView)
        facebookShareImageView = rootView.findViewById(R.id.facebookShareImageView)
        instagramShareImageView = rootView.findViewById(R.id.instagramShareImageView)
        genericShareImageView = rootView.findViewById(R.id.genericShareImageView)

        whatsappShareImageView.setOnClickListener(this)
        facebookShareImageView.setOnClickListener(this)
        instagramShareImageView.setOnClickListener(this)
        genericShareImageView.setOnClickListener(this)

        val bundle = arguments
        userId = bundle?.getString(Constants.USER_ID)
        crownData = bundle?.getParcelable<Crown>("crown")

        if (userId.isNullOrBlank() || crownData == null) {
            activity?.let {
                ToastUtils.showToast(it, it.getString(R.string.empty_screen), Toast.LENGTH_SHORT)
            }
            dismiss()
        }
        populateCrownDetails(userId!!, crownData)

        return rootView
    }

    private fun populateCrownDetails(userId: String, result: Crown?) {
        activity?.let {
            Picasso.with(it).load(result?.bg_url).error(R.drawable.default_article)
                    .fit().into(crownBgImageView)
            Picasso.with(it).load(result?.image_url).error(R.drawable.default_article)
                    .fit().into(crownImageView)
            crownTitleTextView.text = result?.title
            crownDescTextView.text = result?.desc
            if (AppUtils.isPrivateProfile(userId)) {
                shareContainer.visibility = View.VISIBLE
            } else {
                shareContainer.visibility = View.GONE
            }
        }
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

    override fun onClick(view: View?) {
        when {
            view?.id == R.id.whatsappShareImageView -> {
            }
            view?.id == R.id.facebookShareImageView -> {
            }
            view?.id == R.id.instagramShareImageView -> {
            }
            view?.id == R.id.genericShareImageView -> {
            }
        }
    }
}