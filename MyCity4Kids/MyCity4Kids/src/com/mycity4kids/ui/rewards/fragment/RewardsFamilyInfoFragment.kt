package com.mycity4kids.ui.rewards.fragment


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.opengl.Visibility
import android.os.Bundle
import android.support.v4.app.DialogFragment
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
import com.mycity4kids.ui.adapter.CustomSpinnerAdapter
import com.mycity4kids.ui.rewards.activity.RewardsContainerActivity
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.event_details_activity.*
import kotlinx.android.synthetic.main.fragment_rewards_family_info.*
import kotlinx.android.synthetic.main.fragment_rewards_personal_info.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

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
    private lateinit var checkAreYouExpecting: CheckBox
    private lateinit var layoutMotherExptectedDate: RelativeLayout
    private lateinit var editExpectedDate: EditText
    private lateinit var spinnernumberOfKids: Spinner
    private lateinit var checkNuclear: AppCompatRadioButton
    private lateinit var checkJoint: AppCompatRadioButton
    private lateinit var genderSpinner: AppCompatSpinner
    private lateinit var radioYes: AppCompatRadioButton
    private lateinit var radioNo: AppCompatRadioButton
    private lateinit var radioExpecting: AppCompatRadioButton
    private lateinit var linearKidsDetail: LinearLayout
    private lateinit var apiGetResponse: RewardsDetailsResultResonse
    lateinit var editMotherExpectedDate: EditText

    companion object {

        lateinit var textView: TextView
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
                checkNuclear.isChecked = true
            } else if (apiGetResponse.familyType == 2) {
                checkJoint.isChecked = true
            }
        }

        if (apiGetResponse.isMother != null) {
            if (apiGetResponse.isMother == 1) {
                radioYes.isChecked = true
            } else if (apiGetResponse.isMother == 2) {
                radioExpecting.isChecked = true
            }
        }
    }

    private fun initializeXMLComponents() {
        editExpectedDate = containerView.findViewById(R.id.editExpectedDate)
        spinnernumberOfKids = containerView.findViewById(R.id.spinnernumberOfKids)
        editMotherExpectedDate = containerView.findViewById(R.id.editMotherExpectedDate)
        //genderSpinner = containerView.findViewById(R.id.genderSpinner)
        layoutNumberOfKids = containerView.findViewById(R.id.layoutNumberOfKids)
        layoutExptectedDate = containerView.findViewById(R.id.layoutExptectedDate)
        layoutMotherExptectedDate = containerView.findViewById(R.id.layoutMotherExptectedDate)
        checkAreYouExpecting = containerView.findViewById(R.id.checkAreYouExpecting)
        linearKidsDetail = containerView.findViewById(R.id.linearKidsDetail)
        radioYes = containerView.findViewById(R.id.radioYes)
        radioNo = containerView.findViewById(R.id.radioNo)
        checkNuclear = containerView.findViewById(R.id.checkNuclear)
        checkJoint = containerView.findViewById(R.id.checkJoint)
        editMotherExpectedDate.setOnClickListener {
            showDatePickerDialog()
        }

        editExpectedDate.setOnClickListener { }

        containerView.findViewById<TextView>(R.id.textSubmit).setOnClickListener {
            submitListener.FamilyOnSubmit()
        }

        var numberOfChild = resources.getStringArray(R.array.number_of_child)
        var numberOfChildData = ArrayList<String>()
        numberOfChild.forEach { str ->
            numberOfChildData.add(str)
        }
        val householdAdapter = CustomSpinnerAdapter(activity, numberOfChildData)
        spinnernumberOfKids.adapter = householdAdapter
        spinnernumberOfKids.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapter: AdapterView<*>, v: View,
                                        position: Int, id: Long) {
                spinnernumberOfKids.setSelection(position)

                createKidsDetailDynamicView((spinnernumberOfKids.selectedItem.toString()).toInt())
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }

        checkAreYouExpecting.setOnClickListener {
            if (checkAreYouExpecting.isChecked) {
                layoutMotherExptectedDate.visibility = View.VISIBLE
            } else {
                layoutMotherExptectedDate.visibility = View.GONE
            }
            checkAreYouExpecting.isChecked = !checkAreYouExpecting.isChecked

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
                            //checkAreYouExpecting.visibility = View.GONE
                            //layoutMotherExptectedDate.visibility = View.GONE
                        }

                        R.id.radioYes -> {
                            layoutNumberOfKids.visibility = View.VISIBLE
                            layoutExptectedDate.visibility = View.GONE
                            //checkAreYouExpecting.visibility = View.VISIBLE
                            //layoutMotherExptectedDate.visibility = View.GONE
                        }

//                        R.id.radioExpecting -> {
//                            layoutMotherExptectedDate.visibility = View.VISIBLE
//                            layoutNumberOfKids.visibility = View.GONE
//                            layoutExptectedDate.visibility = View.VISIBLE
//                            checkAreYouExpecting.visibility = View.GONE
//                        }
                    }
                }
    }

    fun createKidsDetailDynamicView(numberOfViews: Int) {
        val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        linearKidsDetail.removeAllViews()
        for (i in 1..numberOfViews) {
            val indexView = inflater.inflate(R.layout.dynamic_child_view, null)
            var textHeader = indexView.findViewById<TextView>(R.id.textHeader)
            var spinnerGender = indexView.findViewById<Spinner>(R.id.spinnerGender)
            var textDOB = indexView.findViewById<TextView>(R.id.textDOB)

            textHeader.setText(String.format(resources.getString(R.string.kids_number), i))
            val genderList = java.util.ArrayList<String>()
            genderList.add("Male")
            genderList.add("Female")

            val spinAdapter = CustomSpinnerAdapter(activity, genderList)
            spinnerGender.adapter = spinAdapter
            spinnerGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(adapter: AdapterView<*>, v: View,
                                            position: Int, id: Long) {
                    spinnerGender.setSelection(position)
                }

                override fun onNothingSelected(arg0: AdapterView<*>) {

                }
            }

            textDOB.setOnClickListener {
                RewardsFamilyInfoFragment.textView = textDOB
                showDatePickerDialog()
            }

            linearKidsDetail.addView(indexView)
        }
    }

    class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

        internal var cancel: Boolean = false
        internal val c = Calendar.getInstance()
        internal var curent_year = c.get(Calendar.YEAR)
        internal var current_month = c.get(Calendar.MONTH)
        internal var current_day = c.get(Calendar.DAY_OF_MONTH)


        @SuppressLint("NewApi")
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // Use the current date as the default date in the picker
            val dlg = DatePickerDialog(activity, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, this, curent_year, current_month, current_day)
            dlg.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dlg.datePicker.maxDate = c.timeInMillis
            return dlg
        }

        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
            if (textDOB != null) {
                val sel_date = "" + day + "-" + (month + 1) + "-" + year
                if (chkTime(sel_date)) {
                    RewardsFamilyInfoFragment.textView.setText("" + day + "-" + (month + 1) + "-" + year)
                } else {
                    RewardsFamilyInfoFragment.textView.setText("" + current_day + "-" + (current_month + 1) + "-" + curent_year)
                }
            }
        }

        fun chkTime(time: String): Boolean {
            var result = true

            val currentime = "" + System.currentTimeMillis() / 1000
            if (Integer.parseInt(currentime) < Integer.parseInt(convertDate(time)))
                result = false

            return result
        }

        fun convertDate(convertdate: String): String {
            var timestamp = ""
            try {
                val formatter = SimpleDateFormat("dd-MM-yyyy")
                val dateobj = formatter.parse(convertdate)
                timestamp = "" + dateobj.time / 1000
                return timestamp
            } catch (e: ParseException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

            return timestamp
        }
    }

    fun showDatePickerDialog() {
        val newFragment = DatePickerFragment()
        newFragment.show(activity.supportFragmentManager, "datePicker")
    }

    /*fetch data from server*/
    private fun postDataofRewardsToServer() {
        var userId = com.mycity4kids.preference.SharedPrefUtils.getUserDetailModel(activity)?.dynamoId
        if (userId != null) {
            showProgressDialog(resources.getString(R.string.please_wait))
            BaseApplication.getInstance().retrofit.create(RewardsAPI::class.java).sendRewardsapiData("8ffb68f436724516850cdfdb5d064d69", apiGetResponse, 2).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<RewardsDetailsResultResonse>> {
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
        fun FamilyOnSubmit()
    }
}
