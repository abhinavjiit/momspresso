package com.mycity4kids.ui.activity

import android.Manifest
import android.accounts.NetworkErrorException
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.models.ContactModel
import com.mycity4kids.models.request.PhoneContactRequest
import com.mycity4kids.retrofitAPIsInterfaces.ContactSyncAPI
import com.mycity4kids.ui.adapter.PhoneContactsAdapter
import kotlinx.android.synthetic.main.phone_contact_activity.*
import okhttp3.ResponseBody
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PhoneContactsActivity : BaseActivity(), EasyPermissions.PermissionCallbacks,
    PhoneContactsAdapter.RecyclerViewClickListener, View.OnClickListener {

    private val RC_CONTACT_PERMISSION = 123
    private var contactModelArrayList: ArrayList<ContactModel>? = null
    private var allPhoneList: ArrayList<String>? = null
    private lateinit var adapter: PhoneContactsAdapter
    private var source: String? = null
    private var eventScreen: String? = ""
    private var eventSuffix: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.phone_contact_activity)

        try {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            sendInviteTextView?.setOnClickListener(this)

            source = intent.getStringExtra("source")
            eventScreen = intent.getStringExtra("eventScreen")
            eventSuffix = intent.getStringExtra("eventSuffix")

            contactModelArrayList = ArrayList()
            allPhoneList = ArrayList()
            adapter = PhoneContactsAdapter(this)
            val llm = LinearLayoutManager(this)
            llm.orientation = RecyclerView.VERTICAL
            recyclerView?.layoutManager = llm
            recyclerView?.adapter = adapter

            setUpFilterView()

            if (hasContactPermission()) {
                getAllContacts()
            } else {
                EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.permission_contact_rationale),
                    RC_CONTACT_PERMISSION,
                    Manifest.permission.READ_CONTACTS
                )
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.d(
                "FileNotFoundException",
                Log.getStackTraceString(e)
            )
        }
    }

    private fun setUpFilterView() {
        filterEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(query: Editable?) {
            }

            override fun beforeTextChanged(query: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(query: CharSequence?, p1: Int, p2: Int, p3: Int) {
                adapter.filter.filter(query)
            }
        })
    }

    private fun hasContactPermission(): Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.READ_CONTACTS)
    }

    private fun getAllContacts() {
        try {
            val projection = arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER
            )

            var phone: Cursor? = null
            try {
                phone = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    projection,
                    null,
                    null,
                    null
                )
            } catch (e: SecurityException) {
            }

            phone?.let { phones ->
                try {
                    val normalizedNumbersAlreadyFound: HashSet<String> = HashSet()
                    val indexOfNormalizedNumber: Int =
                        phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER)
                    val indexOfDisplayName: Int =
                        phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                    val indexOfDisplayNumber: Int =
                        phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    while (phones.moveToNext()) {
                        val normalizedNumber: String? = phones.getString(indexOfNormalizedNumber)
                        normalizedNumber?.let { normNumber ->
                            if (normalizedNumbersAlreadyFound.add(normNumber)) {
                                val displayName: String? = phones.getString(indexOfDisplayName)
                                val displayNumber: String? = phones.getString(indexOfDisplayNumber)
                                val contactModel = ContactModel()
                                displayNumber?.let {
                                    contactModel.name = displayName
                                    contactModel.number = it
                                    contactModelArrayList!!.add(contactModel)
                                    allPhoneList!!.add(it)
                                }
                            }
                        }
                    }
                } finally {
                    phones.close()
                }
                phones.close()
            }

            allPhoneList?.let {
                val contactSynRequest = PhoneContactRequest()
                contactSynRequest.contactList = it
                val retro = BaseApplication.getInstance().retrofit
                val contactSyncAPI = retro.create(ContactSyncAPI::class.java)
                val contactSyncCall = contactSyncAPI.syncContacts(contactSynRequest)
                contactSyncCall.enqueue(object : Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        FirebaseCrashlytics.getInstance().recordException(t)
                        Log.d(
                            "FileNotFoundException",
                            Log.getStackTraceString(t)
                        )
                    }

                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                    }
                })
            }
            contactModelArrayList?.sortBy { it.name }
            adapter.setListData(contactModelArrayList)
            adapter.notifyDataSetChanged()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.d(
                "FileNotFoundException",
                Log.getStackTraceString(e)
            )
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        getAllContacts()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            val yes = getString(R.string.lang_sel_yes)
            val no = getString(R.string.new_cancel)
        }
    }

    override fun onClick(view: View, position: Int) {
    }

    override fun onClick(p0: View?) {
        val selectedContactList = ArrayList<String>()
        contactModelArrayList?.let { list ->
            for (i in 0 until list.size) {
                val contactModel = list[i]
                if (contactModel.isSelected) {
                    contactModel.number?.let { selectedContactList.add(it) }
                }
            }
        }

        if (selectedContactList.isNotEmpty()) {

            Utils.shareEventTracking(
                this,
                eventScreen,
                "Invite_Android",
                "CTA_Final_InviteContact_$eventSuffix"
            )

            val contactSynRequest = PhoneContactRequest()
            contactSynRequest.contactList = selectedContactList
            if (source == "shareApp") {
                contactSynRequest.notifType = "3"
            } else {
                contactSynRequest.notifType = "1"
            }
            val retro = BaseApplication.getInstance().retrofit
            val contactSyncAPI = retro.create(ContactSyncAPI::class.java)
            val contactSyncCall = contactSyncAPI.sendInvite(contactSynRequest)
            contactSyncCall.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.body() == null) {
                        val nee =
                            NetworkErrorException(response.raw().toString())
                        FirebaseCrashlytics.getInstance().recordException(nee)
                        showToast(getString(R.string.error_network))
                        return
                    }
                    try {
                        if (response.isSuccessful) {
                            showToast(getString(R.string.invite_contact_invitation_sent))
                            finish()
                        }
                    } catch (e: Exception) {
                        showToast(getString(R.string.toast_response_error))
                        FirebaseCrashlytics.getInstance().recordException(e)
                        Log.d("MC4kException", Log.getStackTraceString(e))
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    showToast(getString(R.string.toast_response_error))
                    FirebaseCrashlytics.getInstance().recordException(t)
                    Log.d(
                        "FileNotFoundException",
                        Log.getStackTraceString(t)
                    )
                }
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
