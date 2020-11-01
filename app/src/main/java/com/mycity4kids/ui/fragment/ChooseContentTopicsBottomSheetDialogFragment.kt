package com.mycity4kids.ui.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mycity4kids.R
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.ui.activity.DashboardActivity
import com.mycity4kids.utils.ToastUtils
import com.mycity4kids.widget.MomspressoButtonWidget

class ChooseContentTopicsBottomSheetDialogFragment : BottomSheetDialogFragment(),
    View.OnClickListener {

    private var isSkipped: Boolean = false
    private lateinit var blogContainer: ConstraintLayout
    private lateinit var storyContainer: ConstraintLayout
    private lateinit var vlogContainer: ConstraintLayout
    private lateinit var blogSelectImageView: ImageView
    private lateinit var storySelectImageView: ImageView
    private lateinit var vlogSelectImageView: ImageView
    private var blogContainerSelected = false
    private var storyContainerSelected = false
    private var vlogContainerSelected = false
    private var selectedContainers = 0
    private lateinit var selectTopicsTextView: MomspressoButtonWidget
    private lateinit var mayBeLaterTextView: TextView
    private var selectedContentContainers = ArrayList<String>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.choose_topics_bottom_sheet_dialog_fragment_layout,
            container,
            false
        )
        dialog?.setOnShowListener {
            val dialog = dialog as BottomSheetDialog
            val bottomSheet = dialog.findViewById<View>(R.id.design_bottom_sheet)
            bottomSheet?.let {
                dialog.behavior.peekHeight = it.height
            }
        }
        Utils.shareEventTracking(activity, "Home screen", "Read_Android", "Format_Options")
        blogContainer = view.findViewById(R.id.blogContainer)
        storyContainer = view.findViewById(R.id.storyContainer)
        vlogContainer = view.findViewById(R.id.vlogContainer)
        blogSelectImageView = view.findViewById(R.id.blogSelectImageView)
        storySelectImageView = view.findViewById(R.id.storySelectImageView)
        vlogSelectImageView = view.findViewById(R.id.vlogSelectImageView)
        selectTopicsTextView = view.findViewById(R.id.selectTopicsTextView)
        mayBeLaterTextView = view.findViewById(R.id.mayBeLaterTextView)


        blogContainer.setOnClickListener(this)
        storyContainer.setOnClickListener(this)
        vlogContainer.setOnClickListener(this)
        selectTopicsTextView.setOnClickListener(this)
        mayBeLaterTextView.setOnClickListener(this)
        return view
    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.mayBeLaterTextView -> {
                isSkipped = true
                Utils.shareEventTracking(activity, "Home screen", "Read_Android", "Format_Options")
                dismiss()
            }
            R.id.blogContainer -> {
                activity?.let {
                    if (!blogContainerSelected) {
                        Utils.shareEventTracking(
                            activity,
                            "Home screen",
                            "Read_Android",
                            "Format_Options_Blog"
                        )
                        selectedContainers++
                        selectedContentContainers.add("blog")
                        blogContainer.background = ResourcesCompat.getDrawable(
                            it.resources!!, R.drawable.selected_item_rounded_border_layout,
                            null
                        )
                        blogSelectImageView.setImageResource(R.drawable.ic_done)
                        blogContainerSelected = true
                    } else {
                        selectedContainers--
                        selectedContentContainers.remove("blog")
                        blogContainer.background = ResourcesCompat.getDrawable(
                            it.resources!!, R.drawable.campaign_border_round_rectangular_layout,
                            null
                        )
                        blogSelectImageView.setImageResource(R.drawable.ic_rectangle)
                        blogContainerSelected = false
                    }
                    if (selectedContainers > 0) {
                        selectTopicsTextView.setBackgroundColor(
                            ContextCompat.getColor(
                                it,
                                R.color.app_red
                            )
                        )
                    } else {
                        selectTopicsTextView.setBackgroundColor(
                            ContextCompat.getColor(
                                it,
                                R.color.grey_faded
                            )
                        )
                    }
                }
            }
            R.id.storyContainer -> {
                activity?.let {
                    if (!storyContainerSelected) {
                        Utils.shareEventTracking(
                            activity,
                            "Home screen",
                            "Read_Android",
                            "Format_Options_100WS"
                        )
                        selectedContainers++
                        selectedContentContainers.add("story")
                        storyContainer.background = ResourcesCompat.getDrawable(
                            it.resources!!, R.drawable.selected_item_rounded_border_layout,
                            null
                        )
                        storySelectImageView.setImageResource(R.drawable.ic_done)
                        storyContainerSelected = true
                    } else {
                        selectedContainers--
                        selectedContentContainers.remove("story")
                        storyContainer.background = ResourcesCompat.getDrawable(
                            it.resources!!, R.drawable.campaign_border_round_rectangular_layout,
                            null
                        )
                        storySelectImageView.setImageResource(R.drawable.ic_rectangle)
                        storyContainerSelected = false
                    }
                    if (selectedContainers > 0) {
                        selectTopicsTextView.setBackgroundColor(
                            ContextCompat.getColor(
                                it,
                                R.color.app_red
                            )
                        )
                    } else {
                        selectTopicsTextView.setBackgroundColor(
                            ContextCompat.getColor(
                                it,
                                R.color.grey_faded
                            )
                        )
                    }
                }
            }
            R.id.vlogContainer -> {
                activity?.let {
                    if (!vlogContainerSelected) {
                        Utils.shareEventTracking(
                            activity,
                            "Home screen",
                            "Read_Android",
                            "Format_Options_Vlog"
                        )
                        selectedContainers++
                        selectedContentContainers.add("vlog")
                        vlogContainer.background = ResourcesCompat.getDrawable(
                            it.resources!!, R.drawable.selected_item_rounded_border_layout,
                            null
                        )
                        vlogSelectImageView.setImageResource(R.drawable.ic_done)
                        vlogContainerSelected = true
                    } else {
                        selectedContainers--
                        selectedContentContainers.remove("vlog")
                        vlogSelectImageView.setImageResource(R.drawable.ic_rectangle)
                        vlogContainer.background = ResourcesCompat.getDrawable(
                            it.resources!!, R.drawable.campaign_border_round_rectangular_layout,
                            null
                        )
                        vlogContainerSelected = false
                    }
                    if (selectedContainers > 0) {
                        selectTopicsTextView.setBackgroundColor(
                            ContextCompat.getColor(
                                it,
                                R.color.app_red
                            )
                        )
                    } else {
                        selectTopicsTextView.setBackgroundColor(
                            ContextCompat.getColor(
                                it,
                                R.color.grey_faded
                            )
                        )
                    }
                }
            }
            R.id.selectTopicsTextView -> {
                val list = (selectedContentContainers.toMutableList()).sortedBy {
                    it.toString()
                }
                activity?.let {
                    if (selectedContainers == 0) {
                        ToastUtils.showToast(it, "select minimum one topic")
                    } else {
                        isSkipped = true
                        Utils.shareEventTracking(
                            activity,
                            "Home screen",
                            "Read_Android",
                            "Format_Options_Select_Topic_CTA"
                        )
                        (it as DashboardActivity).selectedContentTopics(
                            list.toMutableList() as ArrayList<String>,
                            selectedContainers
                        )
                        dismiss()
                    }
                }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (!isSkipped) {
            Utils.shareEventTracking(
                activity,
                "Home screen",
                "Read_Android",
                "Format_Options_Close"
            )
        }
    }
}