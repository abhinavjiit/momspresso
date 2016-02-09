package com.mycity4kids.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kelltontech.model.BaseModel;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.controller.FamilyInvitationController;
import com.mycity4kids.dbtable.TableAdult;
import com.mycity4kids.dbtable.TableFamily;
import com.mycity4kids.dbtable.TableKids;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.models.user.KidsInfo;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.models.user.UserModel;
import com.mycity4kids.models.user.UserResponse;
import com.mycity4kids.newmodels.FamilyInvites;
import com.mycity4kids.newmodels.UserInviteModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.adapter.FamilyInvitesAdapter;
import com.mycity4kids.ui.fragment.PasswordDialogFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hemant on 22/1/16.
 */
public class ListFamilyInvitesActivity extends BaseActivity implements FamilyInvitesAdapter.InvitationResponse {

    ListView listView;
    TextView createFamilyTextView;
    UserInviteModel userInviteModel;
    ArrayList<FamilyInvites> familyInviteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invite_layout);
        userInviteModel = getIntent().getExtras().getParcelable("userInviteData");
//        UserInviteModel userInviteModel = (UserInviteModel) baseModel;
        listView = (ListView) findViewById(R.id.listView);
        View header = getLayoutInflater().inflate(R.layout.invite_list_header, null);
        createFamilyTextView = (TextView) header.findViewById(R.id.createFamilyTextView);
        listView.addHeaderView(header, null, false);
        List<FamilyInvites> list = new ArrayList<>();
        familyInviteList = userInviteModel.getFamilyInvites();
        FamilyInvitesAdapter adapter = new FamilyInvitesAdapter(this, R.layout.invite_item, familyInviteList, this);
        listView.setAdapter(adapter);


        createFamilyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(" Family Invite ", "Create Family");
            }
        });
    }

    @Override
    protected void updateUi(Response response) {
        if (response == null) {
            removeProgressDialog();
            showToast(getResources().getString(R.string.server_error));
            return;
        }

        switch (response.getDataType()) {
            case AppConstants.ACCEPT_OR_REJECT_INVITE_REQUEST:
                try {
                    UserResponse responseData = (UserResponse) response.getResponseObject();
                    if (responseData.getResponseCode() == 200) {
                        Toast.makeText(ListFamilyInvitesActivity.this, responseData.getResult().getMessage(), Toast.LENGTH_SHORT).show();

                        // if db not exists first save in db
                        TableKids kidsTable = new TableKids((BaseApplication) getApplicationContext());
                        ArrayList<KidsInfo> kidList = kidsTable.getAllKids();
                        if (kidList.isEmpty()) {
                            // db not exists
                            saveDatainDB(responseData);
                        }

                        // save in prefrences
                        UserInfo model = new UserInfo();
                        model.setId(responseData.getResult().getData().getUser().getId());
                        model.setFamily_id(responseData.getResult().getData().getUser().getFamily_id());
                        model.setEmail(responseData.getResult().getData().getUser().getEmail());
                        model.setMobile_number(responseData.getResult().getData().getUser().getMobile_number());
                        model.setColor_code(responseData.getResult().getData().getUser().getColor_code());
                        model.setSessionId(responseData.getResult().getData().getUser().getSessionId());
                        model.setFirst_name(responseData.getResult().getData().getUser().getFirst_name() + " " + responseData.getResult().getData().getUser().getLast_name());
                        SharedPrefUtils.setUserDetailModel(ListFamilyInvitesActivity.this, model);

                        // set city also

                        // then call getappoitmnt service
                        removeProgressDialog();
                        startSyncing();

                        Intent intent1 = new Intent(this, LoadingActivity.class);
                        // intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent1);
                        // finish();
                    } else if (responseData.getResponseCode() == 400) {
                        if (!StringUtils.isNullOrEmpty(responseData.getResult().getData().getError())) {
                            Toast.makeText(ListFamilyInvitesActivity.this, responseData.getResult().getData().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                        removeProgressDialog();
                    }

                } catch (Exception e) {
                    removeProgressDialog();
                    e.printStackTrace();
                }
                break;
        }
    }

    public void saveDatainDB(UserResponse model) {

        UserTable table = new UserTable(BaseApplication.getInstance());
        if (table.getRowsCount() > 0) {

            try {
                String profileimg = table.getAllUserData().getProfile().getProfile_image();
                if (!StringUtils.isNullOrEmpty(profileimg)) {
                    SharedPrefUtils.setProfileImgUrl(this, profileimg);
                }
            } catch (Exception e) {
            }

        }


        TableAdult adultTable = new TableAdult((BaseApplication) getApplicationContext());
        adultTable.deleteAll();
        try {

            adultTable.beginTransaction();
            for (UserModel.AdultsInfo user : model.getResult().getData().getAdult()) {

                adultTable.insertData(user.getUser());
            }
            adultTable.setTransactionSuccessful();
        } finally {
            adultTable.endTransaction();
        }

        // saving child data
        TableKids kidsTable = new TableKids((BaseApplication) getApplicationContext());
        kidsTable.deleteAll();
        try {
            kidsTable.beginTransaction();
            for (KidsInfo kids : model.getResult().getData().getKidsInformation()) {

                kidsTable.insertData(kids);

            }
            kidsTable.setTransactionSuccessful();
        } finally {
            kidsTable.endTransaction();
        }

        // saving family

        TableFamily familyTable = new TableFamily((BaseApplication) getApplicationContext());
        familyTable.deleteAll();
        try {

            SharedPrefUtils.setpinCode(ListFamilyInvitesActivity.this, model.getResult().getData().getUser().getPincode());
            familyTable.insertData(model.getResult().getData().getFamily());

        } catch (Exception e) {
            e.getMessage();
        }

    }

    @Override
    public void onInvitationResponse(String response, int position) {

        FamilyInvitationController familyInvitationController = new FamilyInvitationController(this, this);
        familyInvitationController.getData(AppConstants.ACCEPT_OR_REJECT_INVITE_REQUEST,
                familyInviteList.get(position), userInviteModel);
        showProgressDialog(getString(R.string.please_wait));
    }
}
