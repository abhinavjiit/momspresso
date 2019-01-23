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
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseFragment
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.models.rewardsmodels.RewardsDetailsResultResonse
import com.mycity4kids.retrofitAPIsInterfaces.RewardsAPI
import com.mycity4kids.ui.rewards.activity.RewardsContainerActivity
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.internal.operators.observable.ObservableReplay.observeOn
import io.reactivex.schedulers.Schedulers
import org.jsoup.Connection

/**
 * A simple [Fragment] subclass.
 *
 */
class RewardsPersonalInfoFragment : BaseFragment() {
    override fun updateUi(response: Response?) {
    }

    lateinit var containerView: View
    lateinit var textSaveAndContinue: TextView
    lateinit var saveAndContinueListener: SaveAndContinueListener
    lateinit var editFirstName: EditText
    lateinit var editLastName: EditText
    lateinit var editPhone: EditText
    lateinit var editEmail: EditText
    lateinit var editLocation: EditText
    lateinit var editLanguage: EditText
    lateinit var textDOB: TextView
    lateinit var textVerify: TextView
    lateinit var genderSpinner: AppCompatSpinner
    lateinit var radioGroupWorkingStatus: RadioGroup


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
            BaseApplication.getInstance().retrofit.create(RewardsAPI::class.java).getRewardsapiData(userId).subscribeOn(AndroidSchedulers.mainThread()).observeOn(Schedulers.io()).subscribe(object : Observer<BaseResponseGeneric<RewardsDetailsResultResonse>> {
                override fun onComplete() {
                    removeProgressDialog()
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(response: BaseResponseGeneric<RewardsDetailsResultResonse>) {
                    if (response != null && response.code == 200 && Constants.SUCCESS == response.status) {
                        //response.data.
                    }
                }

                override fun onError(e: Throwable) {

                }

            })
        }
    }

    /*initialize XML components*/
    fun initializeXMLComponents() {
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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

    override fun onDestroy() {
        super.onDestroy()

    }
}

