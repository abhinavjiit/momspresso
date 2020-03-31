package com.mycity4kids.ui.activity

import android.Manifest
import android.accounts.NetworkErrorException
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crashlytics.android.Crashlytics
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.models.ContactModel
import com.mycity4kids.models.request.PhoneContactRequest
import com.mycity4kids.retrofitAPIsInterfaces.ContactSyncAPI
import com.mycity4kids.ui.adapter.PhoneContactsAdapter
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
    private var recyclerView: RecyclerView? = null
    private var toolbar: Toolbar? = null
    private var sendInviteTextView: TextView? = null
    private lateinit var adapter: PhoneContactsAdapter
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.phone_contact_activity)

        recyclerView = findViewById(R.id.recyclerView)
        sendInviteTextView = findViewById(R.id.sendInviteTextView)
        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        sendInviteTextView?.setOnClickListener(this)

        contactModelArrayList = ArrayList()
        allPhoneList = ArrayList()
        adapter = PhoneContactsAdapter(this)
        val llm = LinearLayoutManager(this)
        llm.orientation = RecyclerView.VERTICAL
        recyclerView?.layoutManager = llm
        recyclerView?.adapter = adapter

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
    }

    private fun hasContactPermission(): Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.READ_CONTACTS)
    }

    private fun getAllContacts() {

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER
        )

        var phone: Cursor? = null
        try {
            phone = getContentResolver().query(
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
                    Crashlytics.logException(t)
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
            val contactSynRequest = PhoneContactRequest()
            contactSynRequest.contactList = selectedContactList
            contactSynRequest.notifType = "1"
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
                        Crashlytics.logException(nee)
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
                        Crashlytics.logException(e)
                        Log.d("MC4kException", Log.getStackTraceString(e))
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    showToast(getString(R.string.toast_response_error))
                    Crashlytics.logException(t)
                    Log.d(
                        "FileNotFoundException",
                        Log.getStackTraceString(t)
                    )
                }
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)

        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu?.findItem(R.id.search)?.actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.maxWidth = Integer.MAX_VALUE

        // listening to search query text change
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        //noinspection SimplifiableIfStatement
        if (id == R.id.search) {
            return true
        }
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified) {
            searchView.isIconified = true
            return
        }
        super.onBackPressed()
    }
}
