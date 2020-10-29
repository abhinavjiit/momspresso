package com.mycity4kids.ui.fragment

import android.accounts.NetworkErrorException
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseFragment
import com.mycity4kids.models.SelectContentTopicsModel
import com.mycity4kids.models.SelectContentTopicsSubModel
import com.mycity4kids.models.Topics
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI
import com.mycity4kids.ui.activity.DashboardActivity
import com.mycity4kids.ui.activity.EditorAddFollowedTopicsActivity
import com.mycity4kids.ui.activity.ProfileSetting
import com.mycity4kids.ui.activity.SelectContentTopicsActivity
import com.mycity4kids.utils.AppUtils
import com.mycity4kids.utils.ToastUtils
import com.mycity4kids.widget.MomspressoButtonWidget
import com.mycity4kids.widget.ShareButtonWidget
import okhttp3.ResponseBody
import org.apmem.tools.layouts.FlowLayout
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class SelectOrAddVlogTopicsFragment : BaseFragment() {

    private lateinit var linearLayout: LinearLayout
    private var selectedSubCategories = ArrayList<Topics>()
    private lateinit var gotoMyFeed: MomspressoButtonWidget
    private lateinit var back: TextView

    override
    fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.select_vlog_topics_fragment, container, false)
        linearLayout = view.findViewById(R.id.linearLayout)
        gotoMyFeed = view.findViewById(R.id.gotoMyFeed)
        back = view.findViewById(R.id.back)
        val comingFor = arguments?.getString("comingFor")
        if ("add" == comingFor) {
            //setting flow
            gotoMyFeed.setText("Save")
        } else {
            //select dashboard flow
            gotoMyFeed.setText("Go to my feed")
        }
        getTopicCategories()
        gotoMyFeed.setOnClickListener {
            if (isValid()) {
                saveDataToServer()
            } else
                activity?.let { ToastUtils.showToast(it, "choose minimum three topics") }
        }
        back.setOnClickListener {
            activity?.let {
                if (it is SelectContentTopicsActivity)
                    (it).previousPageOnBackClick()
                else if (it is EditorAddFollowedTopicsActivity)
                    it.finish()

            }
        }
        return view
    }


    private fun getTopicCategories() {
        BaseApplication.getInstance().retrofit.create(ArticleDetailsAPI::class.java).getAllTopicsCategorySubCategory(
            "2",
            0
        ).enqueue(contentCategorySubCategoryCallBack)
    }

    private val contentCategorySubCategoryCallBack = object : Callback<SelectContentTopicsModel> {
        override fun onResponse(
            call: Call<SelectContentTopicsModel>,
            response: Response<SelectContentTopicsModel>
        ) {
            val res = response.body()

            val data = res?.data
            Log.d("data", data.toString())
            data?.result?.let {
                setDataIntoUi(it)

            } ?: run {
                Log.e("Data_Tag", "data null")
            }
        }

        override fun onFailure(call: Call<SelectContentTopicsModel>, e: Throwable) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
    }

    private fun setDataIntoUi(data: ArrayList<Topics>) {
        for (i in 0 until data.size) {
            activity?.let {
                val textView = TextView(it)
                val parentParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                parentParams.setMargins(10, 10, 10, 10)
                textView.layoutParams = parentParams
                textView.textSize = 16f
                textView.text = data[i].display_name.toUpperCase(Locale.ROOT)
                textView.setTextColor(ContextCompat.getColor(it, R.color.campaign_515151))
                val face = Typeface.createFromAsset(
                    resources.assets,
                    "fonts/Roboto-Medium.ttf"
                )
                textView.typeface = face
                val ll = LinearLayout(it)
                val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                params.bottomMargin = 50
                ll.layoutParams = params
                ll.orientation = LinearLayout.VERTICAL
                ll.addView(textView)
                val flowLayout = FlowLayout(context)
                val topicsList = data[i].child
                AppUtils.updateFollowingTopics(topicsList)
                for (j in 0 until topicsList.size) {
                    getSubCategories(
                        it,
                        topicsList[j].display_name,
                        flowLayout,
                        topicsList[j].id,
                        topicsList[j].isSelected,
                        topicsList[j]
                    )
                }
                ll.addView(flowLayout)
                linearLayout.addView(ll)
            }
        }
    }

    private fun getSubCategories(
        context: Context,
        displayName: String,
        flowLayout: FlowLayout,
        id: String, isSelected: Boolean,
        topic: Topics
    ) {
        val shareButtonWidget = ShareButtonWidget(context)
        val shareTextView = shareButtonWidget.findViewById<TextView>(R.id.shareTextView)
        val layoutParams = shareTextView.layoutParams
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
        shareTextView.layoutParams = layoutParams
        shareButtonWidget.tag = topic
        shareButtonWidget.setText(displayName)
        shareButtonWidget.setButtonStartImage(null)
        shareButtonWidget.setTextSizeInSP(14)
        shareButtonWidget.setTextGravity(Gravity.CENTER)
        shareButtonWidget.setTextColor(ContextCompat.getColor(context, R.color.app_red))
        shareButtonWidget.setButtonRadiusInDP(4f)
        shareButtonWidget.setBorderColor(ContextCompat.getColor(context, R.color.app_red))
        shareButtonWidget.setBorderThicknessInDP(1f)
        shareButtonWidget.elevation = 0.0f
        if (!isSelected) {
            shareButtonWidget.isSelected = false
            shareButtonWidget.setButtonBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.white_color
                )
            )
            shareButtonWidget.setTextColor(ContextCompat.getColor(context, R.color.app_red))
        } else if (isSelected) {
            topic.setIsSelected(true)
            selectedSubCategories.add(topic)
            shareButtonWidget.isSelected = true
            shareButtonWidget.setButtonBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.app_red
                )
            )
            shareButtonWidget.setTextColor(ContextCompat.getColor(context, R.color.white))
        }
        shareButtonWidget.setOnClickListener {
            if (!it.isSelected) {
                val topics = it.tag as Topics
                if (selectedSubCategories.contains(topics))
                    selectedSubCategories.remove(topics)
                topics.setIsSelected(true)
                selectedSubCategories.add(topics)
                it.isSelected = true
                shareButtonWidget.setButtonBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.app_red
                    )
                )
                shareButtonWidget.setTextColor(ContextCompat.getColor(context, R.color.white))
                Log.d("subCate____", selectedSubCategories.toString())

            } else {
                val topics = it.tag as Topics
                selectedSubCategories.remove(topics)
                topics.setIsSelected(false)
                selectedSubCategories.add(topics)
                it.isSelected = false
                shareButtonWidget.setButtonBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.white_color
                    )
                )
                shareButtonWidget.setTextColor(ContextCompat.getColor(context, R.color.app_red))
                Log.d("subCate____", selectedSubCategories.toString())
            }
        }

        val params: FlowLayout.LayoutParams = FlowLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(10, 20, 10, 20)
        shareButtonWidget.layoutParams = params
        flowLayout.addView(shareButtonWidget)
    }


    private fun saveDataToServer() {
        showProgressDialog("please wait")
        val list = ArrayList<SelectContentTopicsSubModel>()
        selectedSubCategories.forEach {
            val status = if (it.isSelected)
                "1"
            else
                "0"
            val selectContentTopicsSubModel =
                SelectContentTopicsSubModel(itemType = "2", id = it.id, status = status)
            list.add(selectContentTopicsSubModel)
        }
        val topicsList = SelectContentTopicsModel(null)
        topicsList.topics = list
        BaseApplication.getInstance().retrofit.create(ArticleDetailsAPI::class.java).saveContentSelectedAllCategories(
            SharedPrefUtils.getUserDetailModel(BaseApplication.getInstance()).dynamoId, topicsList
        ).enqueue(postTopicsCategoryToServerCallback)
    }

    private val postTopicsCategoryToServerCallback = object : Callback<ResponseBody> {
        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            removeProgressDialog()
            FirebaseCrashlytics.getInstance().recordException(t)
            Log.d("MC4kException", Log.getStackTraceString(t))
        }

        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
            removeProgressDialog()
            if (null == response.body()) {
                val nee =
                    NetworkErrorException(response.raw().toString())
                FirebaseCrashlytics.getInstance().recordException(nee)
                return
            }
            try {
                val res = String(response.body()?.bytes()!!)
                val responsee = JSONObject(res)
                val code = responsee.getInt("code")
                val status = responsee.getString("status")
                val data = responsee.getJSONObject("data")
                val msg = data.getString("msg")
                if (code == 200 && status == "success") {
                    activity?.let {
                        ToastUtils.showToast(it, msg)
                        if (it is SelectContentTopicsActivity)
                            gotoMyFollowingFeed(it)
                        else if (it is EditorAddFollowedTopicsActivity)
                            gotoProfileSetting(it)
                    } ?: run {
                        Log.d("tag", "something went wrong")
                    }
                } else {
                    activity?.let {
                        ToastUtils.showToast(it, "something went wrong")
                    }
                }
                Log.d("Respo", response.body().toString())

            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }

        }
    }


    private fun gotoMyFollowingFeed(context: Context) {
        val intent = Intent(context, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.putExtra("comingFor", "followingFeed")
        startActivity(intent)
    }

    private fun gotoProfileSetting(activity: EditorAddFollowedTopicsActivity) {
        val int = Intent(activity, ProfileSetting::class.java)
        int.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(int)
        activity.finish()
    }

    private fun isValid(): Boolean {
        if (selectedSubCategories.isEmpty() || selectedSubCategories.size < 3) { //|| selectedSubCategories.size < 3
            return false
        }
        return true
    }


}