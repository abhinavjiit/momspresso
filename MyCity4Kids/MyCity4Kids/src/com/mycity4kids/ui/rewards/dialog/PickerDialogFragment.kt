package com.mycity4kids.ui.rewards.dialog

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.recyclerview.R.attr.layoutManager
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mycity4kids.R
import com.mycity4kids.constants.Constants
import com.mycity4kids.ui.rewards.adapter.PickerDialogAdapter
import com.mycity4kids.ui.rewards.fragment.RewardsFamilyInfoFragment
import com.mycity4kids.ui.rewards.fragment.RewardsPersonalInfoFragment
import com.mycity4kids.ui.rewards.fragment.RewardsSocialInfoFragment
import kotlinx.android.synthetic.main.cropimage.*
import java.util.*
import kotlin.collections.ArrayList
import java.util.Arrays.asList
import java.util.Arrays.asList


/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [PickerDialogFragment.OnListFragmentInteractionListener] interface.
 */
class PickerDialogFragment : DialogFragment(), PickerDialogAdapter.onItemClickListener {
    override fun onItemClick(clickedText: String) {
        if (preSelectedItemNames != null && preSelectedItemNames!!.isNotEmpty()) {
            if (preSelectedItemNames!!.contains(clickedText)) {
                preSelectedItemNames!!.remove(clickedText)
            } else {
                preSelectedItemNames!!.add(clickedText)
            }
        }else{
            preSelectedItemNames!!.add(clickedText)
        }
        adapter.notifyDataSetChanged()
    }

    private var columnCount = 1
    private var popupType: String? = null
    private var isSingleSelection: Boolean = false
    private var preSelectedItemIds: ArrayList<String>? = null
    private var preSelectedItemNames = ArrayList<String>()
    private var popAllData = emptyList<String>()
    private lateinit var containerView: View
    private lateinit var saveTextView: TextView
    private lateinit var cancelTextView: TextView
    private lateinit var adapter: PickerDialogAdapter
    private lateinit var onClickDoneListener: OnClickDoneListener
    private lateinit var contex: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
            popupType = it.getString(ARG_POPUP_TYPE)
            isSingleSelection = it.getBoolean(ARG_IS_SINGLE_SELECTION)
            preSelectedItemIds = it.getStringArrayList(ARG_PRE_SELECTED_ITEMS_IDS)
        }

        when (popupType) {
            Constants.PopListRequestType.INTEREST.name -> {
                popAllData = resources.getStringArray(R.array.popup_interest_list).toList()
                if (preSelectedItemIds != null && preSelectedItemIds!!.isNotEmpty()) {
                    fillInterestItemNamesByIds(preSelectedItemIds!!)
                }
            }

            Constants.PopListRequestType.DURABLES.name -> {
                popAllData = resources.getStringArray(R.array.popup_durables_list).toList()
                if (preSelectedItemIds != null && preSelectedItemIds!!.isNotEmpty()) {
                    fillDurablesItemNamesByIds(preSelectedItemIds!!)
                }
            }

            Constants.PopListRequestType.LANGUAGE.name -> {
                popAllData = resources.getStringArray(R.array.popup_language_list).toList()
                if (preSelectedItemIds != null && preSelectedItemIds!!.isNotEmpty()) {
                    fillLanguageItemNamesByIds(preSelectedItemIds!!)
                }
            }
        }
    }

    private fun fillDurablesItemNamesByIds(preSelectedItemIds: ArrayList<String>) {
        (preSelectedItemIds).forEach { id ->
            id.map {
                preSelectedItemNames.add(Constants.TypeOfDurables.findById(id.toInt()))
            }
        }
    }

    private fun fillLanguageItemNamesByIds(preSelectedItemIds: ArrayList<String>) {
        (preSelectedItemIds).forEach { id ->
            preSelectedItemNames.add(Constants.TypeOfLanguagesWithContent.findById(id))
//            id.map {
//                preSelectedItemNames.add(Constants.TypeOfLanguages.findById(id))
//            }
        }
    }

    private fun fillInterestItemNamesByIds(preSelectedItemIds: ArrayList<String>) {
        (preSelectedItemIds).forEach { id ->
            id.map {
                preSelectedItemNames.add(Constants.TypeOfInterest.findById(id.toInt()))
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        containerView = inflater.inflate(R.layout.picker_dialog_fragment, container, false)

        // Set the adapter

        initializeXMLComponents()

        return containerView
    }

    private fun initializeXMLComponents() {
        var list = containerView.findViewById<RecyclerView>(R.id.list)
        if (list is RecyclerView) {
            with(list) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }

            }
        }
        adapter = PickerDialogAdapter(preSelectedItemNames, popAllData, this@PickerDialogFragment)
        list.adapter = adapter

        (containerView.findViewById<TextView>(R.id.cancelTextView)).setOnClickListener {
            dialog.dismiss()
        }

        (containerView.findViewById<TextView>(R.id.saveTextView)).setOnClickListener {
            onClickDoneListener.onItemClick(preSelectedItemNames, popupType!!)
            dialog.dismiss()
        }
    }

    companion object {
        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"
        const val ARG_POPUP_TYPE = "popup-type"
        const val ARG_IS_SINGLE_SELECTION = "is-single-selection"
        const val ARG_PRE_SELECTED_ITEMS_IDS = "pre-selected-items-ids"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int = 1, popType: String, isSingleSelection: Boolean = false, preSelectedItemIds: ArrayList<String>? = null, context: RewardsPersonalInfoFragment) =
                PickerDialogFragment().apply {
                    this.onClickDoneListener = context
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                        putString(ARG_POPUP_TYPE, popType)
                        putBoolean(ARG_IS_SINGLE_SELECTION, isSingleSelection)
                        putStringArrayList(ARG_PRE_SELECTED_ITEMS_IDS, preSelectedItemIds)
                    }
                }
    }

    interface OnClickDoneListener {
        fun onItemClick(selectedValue: ArrayList<String>, popupType: String)
    }

}
