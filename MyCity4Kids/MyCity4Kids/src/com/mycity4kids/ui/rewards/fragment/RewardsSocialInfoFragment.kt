package com.mycity4kids.ui.rewards.fragment


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatSpinner
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseFragment
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.models.rewardsmodels.RewardsDetailsResultResonse
import com.mycity4kids.retrofitAPIsInterfaces.RewardsAPI
import com.mycity4kids.ui.adapter.CustomSpinnerAdapter
import com.mycity4kids.ui.rewards.activity.RewardsContainerActivity
import com.mycity4kids.ui.rewards.dialog.PickerDialogFragment
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.event_details_activity.*
import org.apmem.tools.layouts.FlowLayout
import java.util.*
import kotlin.collections.ArrayList

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class RewardsSocialInfoFragment : BaseFragment(), PickerDialogFragment.OnClickDoneListener {
    override fun onItemClick(selectedValueName: ArrayList<String>, popupType: String) {
        if (popupType == Constants.PopListRequestType.INTEREST.name) {
            //preSelectedInterest = selectedValue
            setFloatingLayout(selectedValueName, popupType)
        } else if (popupType == Constants.PopListRequestType.DURABLES.name) {
            //preSelectedDurables = selectedValue
            setFloatingLayout(selectedValueName, popupType)
        }
    }

    private fun setFloatingLayout(preSelectedItems: ArrayList<String>, popupType: String) {
        if (popupType == Constants.PopListRequestType.INTEREST.name) {
            floatingInterest.removeAllViews()
            if (preSelectedItems.isNotEmpty()) {
                textEditInterest.visibility = View.VISIBLE
                linearInterest.visibility = View.VISIBLE
                editInterest.visibility = View.GONE
            } else {
                textEditInterest.visibility = View.GONE
                linearInterest.visibility = View.GONE
                editInterest.visibility = View.VISIBLE
            }
            preSelectedInterest.clear()
            preSelectedItems.forEach {
                var name = if (Constants.TypeOfInterest.findByName(it) != null) {
                    Constants.TypeOfInterest.findByName(it)
                } else {
                    null
                }
                if (name != null) {
                    preSelectedInterest.add(Constants.TypeOfInterest.findByName(it))
                }
                val subsubLL = LayoutInflater.from(activity).inflate(R.layout.topic_follow_unfollow_item, null) as LinearLayout
                val catTextView = subsubLL.getChildAt(0) as TextView
                catTextView.setText(it)
                catTextView.isSelected = true
                //subsubLL.tag = it
                floatingInterest.addView(subsubLL)
            }
        } else if (popupType == Constants.PopListRequestType.DURABLES.name) {
            floatingDurables.removeAllViews()
            if (preSelectedItems.isNotEmpty()) {
                textEditDurables.visibility = View.VISIBLE
                linearDurables.visibility = View.VISIBLE
                editDurables.visibility = View.GONE
            } else {
                textEditDurables.visibility = View.GONE
                linearDurables.visibility = View.GONE
                editDurables.visibility = View.VISIBLE
            }
            preSelectedDurables.clear()
            preSelectedItems.forEach {
                var name = if (Constants.TypeOfDurables.findByName(it) != null) {
                    Constants.TypeOfDurables.findByName(it)
                } else {
                    null
                }
                if (name != null) {
                    preSelectedDurables.add(Constants.TypeOfDurables.findByName(it))
                }
                val subsubLL = LayoutInflater.from(activity).inflate(R.layout.topic_follow_unfollow_item, null) as LinearLayout
                val catTextView = subsubLL.getChildAt(0) as TextView
                catTextView.setText(it)
                catTextView.isSelected = true
                //subsubLL.tag = it
                floatingDurables.addView(subsubLL)
            }
        }
    }

    override fun updateUi(response: Response?) {
    }

    private lateinit var containerView: View
    private lateinit var submitListener: SubmitListener
    private lateinit var layoutInstagram: LinearLayout
    private lateinit var layoutFacebook: LinearLayout
    private lateinit var layoutTwitter: LinearLayout
    private lateinit var layoutYoutube: LinearLayout
    private lateinit var editInterest: EditText
    private lateinit var editDurables: EditText
    private lateinit var editWebsite: EditText
    private lateinit var editInstagram: EditText
    private lateinit var editFacebook: EditText
    private lateinit var editTwitter: EditText
    private lateinit var editYoutube: EditText
    private lateinit var linearDurables: LinearLayout
    private lateinit var floatingDurables: FlowLayout
    private lateinit var linearInterest: LinearLayout
    private lateinit var floatingInterest: FlowLayout
    private lateinit var spinnerProfession: AppCompatSpinner
    private lateinit var spinnerHouseHold: AppCompatSpinner
    private lateinit var textEditInterest: TextView
    private lateinit var textEditDurables: TextView
    private var householdList = ArrayList<String>()
    private var professionList = ArrayList<String>()
    private lateinit var apiGetResponse: RewardsDetailsResultResonse
    private var preSelectedInterest = ArrayList<String>()
    private var preSelectedDurables = ArrayList<String>()

    companion object {
        fun newInstance() = RewardsSocialInfoFragment().apply {
            arguments = Bundle().apply {

            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        containerView = inflater.inflate(R.layout.fragment_rewards_social_info, container, false)

        /*initialize XML components*/
        initializeXMLComponents()

        /*fetch data from server*/
        fetchRewardsData()


        return containerView
    }

    /*fetch data from server*/
    private fun fetchRewardsData() {
        var userId = com.mycity4kids.preference.SharedPrefUtils.getUserDetailModel(activity)?.dynamoId
        if (userId != null) {
            showProgressDialog(resources.getString(R.string.please_wait))
            BaseApplication.getInstance().retrofit.create(RewardsAPI::class.java).getRewardsapiData("8ffb68f436724516850cdfdb5d064d69", 3).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<RewardsDetailsResultResonse>> {
                override fun onComplete() {
                    removeProgressDialog()
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(response: BaseResponseGeneric<RewardsDetailsResultResonse>) {
                    if (response != null && response.code == 200 && Constants.SUCCESS == response.status && response.data != null) {
                        apiGetResponse = response.data!!.result

                        /*setting values to components*/
                        setValuesToComponents()
                    } else {

                    }
                }

                override fun onError(e: Throwable) {

                }
            })
        }
    }

    private fun setValuesToComponents() {
        if (apiGetResponse.interest != null && apiGetResponse.interest!!.isNotEmpty()) {
            floatingInterest.removeAllViews()
            textEditInterest.visibility = View.VISIBLE
            editInterest.visibility = View.GONE
            linearInterest.visibility = View.VISIBLE
            apiGetResponse.interest!!.forEach {
                var interestName = Constants.TypeOfInterest.findById(it.toInt())
                preSelectedInterest.add(it)
                val subsubLL = LayoutInflater.from(activity).inflate(R.layout.topic_follow_unfollow_item, null) as LinearLayout
                val catTextView = subsubLL.getChildAt(0) as TextView
                catTextView.setText(interestName)
                catTextView.isSelected = true
                floatingInterest.addView(subsubLL)
            }
        } else {
            editInterest.visibility = View.VISIBLE
            linearInterest.visibility = View.GONE
            floatingInterest.visibility = View.GONE
            textEditInterest.visibility = View.GONE

        }

        if (apiGetResponse.durables != null && apiGetResponse.durables!!.isNotEmpty()) {
            floatingDurables.removeAllViews()
            editDurables.visibility = View.GONE
            linearDurables.visibility = View.VISIBLE
            textEditDurables.visibility = View.VISIBLE
            apiGetResponse.durables!!.forEach {
                var durablesName = Constants.TypeOfDurables.findById(it.toInt())
                preSelectedDurables.add(it)
                val subsubLL = LayoutInflater.from(activity).inflate(R.layout.topic_follow_unfollow_item, null) as LinearLayout
                val catTextView = subsubLL.getChildAt(0) as TextView
                catTextView.setText(durablesName)
                catTextView.isSelected = true
                //subsubLL.tag = it
                floatingDurables.addView(subsubLL)
            }
        } else {
            textEditDurables.visibility = View.GONE
            editDurables.visibility = View.VISIBLE
            linearDurables.visibility = View.GONE
            floatingDurables.visibility = View.GONE

        }
    }

    private fun initializeXMLComponents() {
        layoutInstagram = containerView.findViewById(R.id.layoutInstagram)
        layoutFacebook = containerView.findViewById(R.id.layoutFacebook)
        layoutYoutube = containerView.findViewById(R.id.layoutYoutube)
        layoutTwitter = containerView.findViewById(R.id.layoutTwitter)
        editInterest = containerView.findViewById(R.id.editInterest)
        editDurables = containerView.findViewById(R.id.editDurables)
        linearDurables = containerView.findViewById(R.id.linearDurables)
        floatingDurables = containerView.findViewById(R.id.floatingDurables)
        linearInterest = containerView.findViewById(R.id.linearInterest)
        floatingInterest = containerView.findViewById(R.id.floatingInterest)
        spinnerProfession = containerView.findViewById(R.id.spinnerProfession)
        spinnerHouseHold = containerView.findViewById(R.id.spinnerHouseHold)
        textEditInterest = containerView.findViewById(R.id.textEditInterest)
        textEditDurables = containerView.findViewById(R.id.textEditDurables)
        editInstagram = containerView.findViewById(R.id.editInstagram)
        editFacebook = containerView.findViewById(R.id.editFacebook)
        editTwitter = containerView.findViewById(R.id.editTwitter)
        editWebsite = containerView.findViewById(R.id.editWebsite)
        editYoutube = containerView.findViewById(R.id.editYoutube)

        textEditDurables.setOnClickListener {
            var fragment = PickerDialogFragment.newInstance(columnCount = 1, popType = Constants.PopListRequestType.DURABLES.name,
                    isSingleSelection = true, preSelectedItemIds = preSelectedDurables, context = this@RewardsSocialInfoFragment)
            fragment.show(fragmentManager, RewardsSocialInfoFragment::class.java.simpleName)
        }

        textEditInterest.setOnClickListener {
            var fragment = PickerDialogFragment.newInstance(columnCount = 1, popType = Constants.PopListRequestType.INTEREST.name,
                    isSingleSelection = true, preSelectedItemIds = preSelectedInterest, context = this@RewardsSocialInfoFragment)
            fragment.show(fragmentManager, RewardsSocialInfoFragment::class.java.simpleName)
        }

        var houseHoldIncomeArray = resources.getStringArray(R.array.household_income)
        houseHoldIncomeArray.forEach { str ->
            householdList.add(str)
        }
        val householdAdapter = CustomSpinnerAdapter(activity, householdList)
        spinnerHouseHold.adapter = householdAdapter
        spinnerHouseHold.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapter: AdapterView<*>, v: View,
                                        position: Int, id: Long) {
                spinnerHouseHold.setSelection(position)
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }

        var professionArray = resources.getStringArray(R.array.profession)
        professionArray.forEach { str ->
            professionList.add(str)
        }

        val professionAdapter = CustomSpinnerAdapter(activity, professionList)
        spinnerProfession.adapter = professionAdapter
        spinnerProfession.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapter: AdapterView<*>, v: View,
                                        position: Int, id: Long) {
                spinnerProfession.setSelection(position)
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }

        spinnerHouseHold.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapter: AdapterView<*>, v: View,
                                        position: Int, id: Long) {
                spinnerHouseHold.setSelection(position)
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }

        editInterest.setOnClickListener {
            if (preSelectedInterest.isNotEmpty()) {
                var fragment = PickerDialogFragment.newInstance(columnCount = 1, popType = Constants.PopListRequestType.INTEREST.name,
                        isSingleSelection = true, preSelectedItemIds = preSelectedInterest, context = this@RewardsSocialInfoFragment)
                fragment.show(fragmentManager, RewardsSocialInfoFragment::class.java.simpleName)
            } else {
                var fragment = PickerDialogFragment.newInstance(columnCount = 1, popType = Constants.PopListRequestType.INTEREST.name,
                        isSingleSelection = true, preSelectedItemIds = apiGetResponse?.interest!!, context = this@RewardsSocialInfoFragment)
                fragment.show(fragmentManager, RewardsSocialInfoFragment::class.java.simpleName)
            }
        }

        editDurables.setOnClickListener {
            if (preSelectedDurables.isNotEmpty()) {
                var fragment = PickerDialogFragment.newInstance(columnCount = 1, popType = Constants.PopListRequestType.DURABLES.name,
                        isSingleSelection = true, preSelectedItemIds = preSelectedDurables, context = this@RewardsSocialInfoFragment)
                fragment.show(fragmentManager, RewardsSocialInfoFragment::class.java.simpleName)
            } else {
                var fragment = PickerDialogFragment.newInstance(columnCount = 1, popType = Constants.PopListRequestType.DURABLES.name,
                        isSingleSelection = true, preSelectedItemIds = apiGetResponse?.durables!!, context = this@RewardsSocialInfoFragment)
                fragment.show(fragmentManager, RewardsSocialInfoFragment::class.java.simpleName)
            }
        }

        containerView.findViewById<TextView>(R.id.textSubmit).setOnClickListener {
            //submitListener.socialOnSubmitListener()
            //postDataofRewardsToServer()

        }
    }

    /*fetch data from server*/
    private fun postDataofRewardsToServer() {
        var userId = com.mycity4kids.preference.SharedPrefUtils.getUserDetailModel(activity)?.dynamoId
        if (userId != null) {
            showProgressDialog(resources.getString(R.string.please_wait))
            BaseApplication.getInstance().retrofit.create(RewardsAPI::class.java).sendRewardsapiData("8ffb68f436724516850cdfdb5d064d69", apiGetResponse, 3).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<RewardsDetailsResultResonse>> {
                override fun onComplete() {
                    removeProgressDialog()
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(response: BaseResponseGeneric<RewardsDetailsResultResonse>) {
                    if (response != null && response.code == 200 && Constants.SUCCESS == response.status && response.data != null) {
                        apiGetResponse = response.data!!.result

//                        /*setting values to components*/
//                        setValuesToComponents()
                    } else {

                    }
                }

                override fun onError(e: Throwable) {

                }
            })
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is RewardsContainerActivity) {
            submitListener = context
        }
    }

    interface SubmitListener {
        fun socialOnSubmitListener()
    }

}
