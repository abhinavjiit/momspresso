package com.mycity4kids.ui.rewards.fragment


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatRadioButton
import android.support.v7.widget.AppCompatSpinner
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseFragment

import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.models.rewardsmodels.RewardsDetailsResultResonse
import com.mycity4kids.retrofitAPIsInterfaces.RewardsAPI
import com.mycity4kids.ui.rewards.activity.RewardsContainerActivity
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class RewardsFamilyInfoFragment : BaseFragment() {
    override fun updateUi(response: Response?) {
    }

    private lateinit var containerView: View
    private lateinit var submitListener: SubmitListener
    private lateinit var layoutNumberOfKids: RelativeLayout
    private lateinit var layoutExptectedDate: RelativeLayout
    private lateinit var textAreYouExpecting: TextView
    private lateinit var layoutMotherExptectedDate: TextView
    private lateinit var editExpectedDate: EditText
    private lateinit var editnumberOfKids: EditText
    private lateinit var editMotherExpectedDate: EditText
    private lateinit var checkNuclear: AppCompatRadioButton
    private lateinit var checkJoint: AppCompatRadioButton
    private lateinit var genderSpinner: AppCompatSpinner
    private lateinit var radioYes : AppCompatRadioButton
    private lateinit var radioExpecting : AppCompatRadioButton
    private lateinit var apiGetResponse: RewardsDetailsResultResonse

    companion object {
        @JvmStatic
        fun newInstance() =
                RewardsFamilyInfoFragment().apply {
                    arguments = Bundle().apply {

                    }
                }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        containerView = inflater.inflate(R.layout.fragment_rewards_family_info, container, false)

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
            BaseApplication.getInstance().retrofit.create(RewardsAPI::class.java).getRewardsapiData("8ffb68f436724516850cdfdb5d064d69", 1).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<RewardsDetailsResultResonse>> {
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
        if (apiGetResponse.familyType != null) {
            if (apiGetResponse.familyType == 1) {
                checkNuclear.isChecked=true
            } else if(apiGetResponse.familyType==2){
                checkJoint.isChecked=true
            }
        }

        if(apiGetResponse.isMother!=null){
            if(apiGetResponse.isMother==1){
                radioYes.isChecked=true
            }else if(apiGetResponse.isMother==2){
                radioExpecting.isChecked=true
            }
        }
    }

    private fun initializeXMLComponents() {
        editExpectedDate = containerView.findViewById(R.id.editExpectedDate)
        editnumberOfKids = containerView.findViewById(R.id.editnumberOfKids)
        editMotherExpectedDate = containerView.findViewById(R.id.editMotherExpectedDate)
        genderSpinner = containerView.findViewById(R.id.genderSpinner)
        layoutNumberOfKids = containerView.findViewById(R.id.layoutNumberOfKids)
        layoutExptectedDate = containerView.findViewById(R.id.layoutExptectedDate)
        layoutMotherExptectedDate = containerView.findViewById(R.id.layoutMotherExptectedDate)
        textAreYouExpecting = containerView.findViewById(R.id.textAreYouExpecting)
        checkNuclear = containerView.findViewById(R.id.checkNuclear)
        checkJoint = containerView.findViewById(R.id.checkJoint)
        containerView.findViewById<TextView>(R.id.textSubmit).setOnClickListener {
            submitListener.FamilyOnSubmit()
        }

        containerView.findViewById<RadioGroup>(R.id.radioGroupFamilyType)
                .setOnCheckedChangeListener { radioGroup, i ->
                    when (i) {
                        0 -> {

                        }

                        1 -> {

                        }

                    }
                }

        containerView.findViewById<RadioGroup>(R.id.radioGroupAreMother)
                .setOnCheckedChangeListener { radioGroup, i ->
                    when (i) {
                        R.id.radioNo -> {
                            layoutNumberOfKids.visibility = View.GONE
                            layoutExptectedDate.visibility = View.GONE
                            textAreYouExpecting.visibility = View.GONE
                            layoutMotherExptectedDate.visibility = View.GONE
                        }

                        R.id.radioYes -> {
                            layoutNumberOfKids.visibility = View.VISIBLE
                            layoutExptectedDate.visibility = View.GONE
                            textAreYouExpecting.visibility = View.VISIBLE
                            layoutMotherExptectedDate.visibility = View.GONE
                        }

                        R.id.radioExpecting -> {
                            layoutMotherExptectedDate.visibility = View.VISIBLE
                            layoutNumberOfKids.visibility = View.GONE
                            layoutExptectedDate.visibility = View.VISIBLE
                            textAreYouExpecting.visibility = View.GONE
                        }
                    }
                }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is RewardsContainerActivity) {
            submitListener = context
        }
    }

    interface SubmitListener {
        fun FamilyOnSubmit()
    }
}
