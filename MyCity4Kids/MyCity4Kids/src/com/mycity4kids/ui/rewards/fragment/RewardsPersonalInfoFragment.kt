package com.mycity4kids.ui.rewards.fragment


import android.app.Application
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.AppCompatSpinner
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import com.google.api.client.util.DateTime
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseFragment
import com.kelltontech.utils.DateTimeUtils
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.models.rewardsmodels.RewardsDetailsResultResonse
import com.mycity4kids.retrofitAPIsInterfaces.RewardsAPI
import com.mycity4kids.ui.adapter.CustomSpinnerAdapter
import com.mycity4kids.ui.rewards.activity.RewardsContainerActivity
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.internal.operators.observable.ObservableReplay.observeOn
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.aa_rate_app.view.*
import kotlinx.android.synthetic.main.group_about_item.*
import org.jsoup.Connection
import java.util.*

/**
 * A simple [Fragment] subclass.
 *
 */
class RewardsPersonalInfoFragment : BaseFragment() {
    override fun updateUi(response: Response?) {
    }

    private lateinit var containerView: View
    private lateinit var textSaveAndContinue: TextView
    private lateinit var saveAndContinueListener: SaveAndContinueListener
    private lateinit var editFirstName: EditText
    private lateinit var editLastName: EditText
    private lateinit var editPhone: EditText
    private lateinit var editEmail: EditText
    private lateinit var editLocation: EditText
    private lateinit var editLanguage: EditText
    private lateinit var textDOB: TextView
    private lateinit var textVerify: TextView
    private lateinit var genderSpinner: AppCompatSpinner
    private lateinit var radioGroupWorkingStatus: RadioGroup
    private lateinit var apiGetResponse: RewardsDetailsResultResonse


    companion object {
        @JvmStatic
        fun newInstance() =
                RewardsPersonalInfoFragment().apply {
                    arguments = Bundle().apply {

                    }
                }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        // Inflate the layout for this fragment
        containerView = inflater.inflate(R.layout.fragment_rewards_personal_info, container, false)

        /*initialize XML components with clicks*/
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
            BaseApplication.getInstance().retrofit.create(RewardsAPI::class.java).getRewardsapiData("a66ac4980fb54dec85dccb3b894d793a", 1).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<RewardsDetailsResultResonse>> {
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

    /*setting values to components*/
    private fun setValuesToComponents() {
        if (!apiGetResponse.firstName.isNullOrBlank()) editFirstName.setText(apiGetResponse.firstName)
        if (!apiGetResponse.lastName.isNullOrBlank()) editLastName.setText(apiGetResponse.lastName)
        if (!apiGetResponse.contact.isNullOrBlank()) editPhone.setText(apiGetResponse.contact)
        if (!apiGetResponse.email.isNullOrBlank()) editEmail.setText(apiGetResponse.email)
        if (apiGetResponse.dob != null && apiGetResponse.dob!! > 0) textDOB.setText(DateTimeUtils.getDateFromTimestamp(apiGetResponse.dob!!.toLong()))
        if (!apiGetResponse.location.isNullOrBlank()) editLocation.setText(apiGetResponse.location)
        if (apiGetResponse.motherTongue.isNullOrBlank()) editLanguage.setText(apiGetResponse.motherTongue)
        if (apiGetResponse.workStatus != null) {
            if (apiGetResponse.workStatus == 0) {
                radioGroupWorkingStatus.check(R.id.radioNotWorking)
            } else if (apiGetResponse.workStatus == 1) {
                radioGroupWorkingStatus.check(R.id.radiokWorking)
            }
        }
    }

    /*initialize XML components with clicks*/
    private fun initializeXMLComponents() {
        editFirstName = containerView.findViewById(R.id.editFirstName)
        editLastName = containerView.findViewById(R.id.editLastName)
        editPhone = containerView.findViewById(R.id.editPhone)
        editFirstName = containerView.findViewById(R.id.editFirstName)
        editEmail = containerView.findViewById(R.id.editEmail)
        editLocation = containerView.findViewById(R.id.editLocation)
        editLanguage = containerView.findViewById(R.id.editLanguage)
        genderSpinner = containerView.findViewById(R.id.genderSpinner)
        textDOB = containerView.findViewById(R.id.textDOB)
        radioGroupWorkingStatus = containerView.findViewById(R.id.radioGroupWorkingStatus)
        textVerify = containerView.findViewById(R.id.textVerify)
        (containerView.findViewById<TextView>(R.id.textSaveAndContinue)).setOnClickListener {
            saveAndContinueListener.profileOnSaveAndContinue()
        }

        val genderList = ArrayList<String>()
        genderList.add("Male")
        genderList.add("Female")

        val spinAdapter = CustomSpinnerAdapter(activity, genderList)
        genderSpinner.adapter = spinAdapter
        genderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapter: AdapterView<*>, v: View,
                                        position: Int, id: Long) {
                genderSpinner.selectedItemId =
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is RewardsContainerActivity) {
            saveAndContinueListener = context
        }
    }


    interface SaveAndContinueListener {
        fun profileOnSaveAndContinue()
    }

}

