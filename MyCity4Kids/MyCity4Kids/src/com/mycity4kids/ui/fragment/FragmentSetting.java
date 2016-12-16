package com.mycity4kids.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.LogoutController;
import com.mycity4kids.dbtable.ExternalCalendarTable;
import com.mycity4kids.dbtable.TableAdult;
import com.mycity4kids.dbtable.TableApiEvents;
import com.mycity4kids.dbtable.TableAppointmentData;
import com.mycity4kids.dbtable.TableAttendee;
import com.mycity4kids.dbtable.TableFamily;
import com.mycity4kids.dbtable.TableFile;
import com.mycity4kids.dbtable.TableKids;
import com.mycity4kids.dbtable.TableNotes;
import com.mycity4kids.dbtable.TableTaskData;
import com.mycity4kids.dbtable.TableTaskList;
import com.mycity4kids.dbtable.TableWhoToRemind;
import com.mycity4kids.dbtable.TaskCompletedTable;
import com.mycity4kids.dbtable.TaskTableAttendee;
import com.mycity4kids.dbtable.TaskTableFile;
import com.mycity4kids.dbtable.TaskTableNotes;
import com.mycity4kids.dbtable.TaskTableWhoToRemind;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.facebook.FacebookUtils;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.interfaces.IFacebookUser;
import com.mycity4kids.models.NotificationSettingsModel;
import com.mycity4kids.models.logout.LogoutResponse;
import com.mycity4kids.models.request.SocialConnectRequest;
import com.mycity4kids.models.response.BaseResponse;
import com.mycity4kids.models.response.NotificationSettingsResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.reminders.AppointmentManager;
import com.mycity4kids.retrofitAPIsInterfaces.LoginRegistrationAPI;
import com.mycity4kids.ui.activity.ActivityLogin;
import com.mycity4kids.ui.activity.EditProfieActivity;
import com.mycity4kids.ui.activity.FollowedTopicsListingActivity;
import com.mycity4kids.ui.activity.SettingsActivity;
import com.mycity4kids.ui.adapter.NotificationSettingsListAdapter;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;


/**
 * Created by khushboo.goyal on 08-06-2015.
 */
//
public class FragmentSetting extends BaseFragment implements View.OnClickListener, IFacebookUser {
    int cityId;
    TextView cityChange;
    String bio, firstName, lastName, phoneNumber;
    private String accessToken;
    private TextView facebookConnectTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.aa_setting, container, false);
        Utils.pushOpenScreenEvent(getActivity(), "Settings", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "");

        // ((SettingsActivity) getActivity()).refreshMenu();
        bio = getArguments().getString("bio");
        firstName = getArguments().getString("firstName");
        lastName = getArguments().getString("lastName");
        phoneNumber = getArguments().getString("phoneNumber");
        facebookConnectTextView = (TextView) view.findViewById(R.id.facebookConnect);

        ((TextView) view.findViewById(R.id.logout)).setOnClickListener(this);
        ((TextView) view.findViewById(R.id.family_details)).setOnClickListener(this);
        ((TextView) view.findViewById(R.id.viewFollowingTopics)).setOnClickListener(this);
        ((TextView) view.findViewById(R.id.editProfile)).setOnClickListener(this);
        ((TextView) view.findViewById(R.id.notifications)).setOnClickListener(this);
        facebookConnectTextView.setOnClickListener(this);

        if ("1".equals(SharedPrefUtils.getFacebookConnectedFlag(getActivity()))) {
            facebookConnectTextView.setText("CONNECT");
        } else {
            facebookConnectTextView.setText("CONNECTED");
        }

        cityChange = (TextView) view.findViewById(R.id.cityChange);
        cityChange.setOnClickListener(this);
        cityId = SharedPrefUtils.getCurrentCityModel(getActivity()).getId();
        setHasOptionsMenu(false);

        checkFacebookConnectedStatus();

        switch (cityId) {
            case 1:
                cityChange.setText("Change City (Delhi-NCR)");
                break;
            case 2:
                cityChange.setText("Change City (Bangalore)");
                break;
            case 3:
                cityChange.setText("Change City (Mumbai)");
                break;
            case 4:
                cityChange.setText("Change City (Pune)");
                break;
            case 5:
                cityChange.setText("Change City (Hyderabad)");
                break;
            case 6:
                cityChange.setText("Change City (Chennai)");
                break;
            case 7:
                cityChange.setText("Change City (Kolkata)");
                break;
            case 8:
                cityChange.setText("Change City (Jaipur)");
                break;
            case 9:
                cityChange.setText("Change City (Ahmedabad)");
                break;
        }
        return view;
    }

    private void checkFacebookConnectedStatus() {

    }

    @Override
    public void onClick(View v) {

        Bundle bundle;

        switch (v.getId()) {
            case R.id.logout:
                if (ConnectivityUtils.isNetworkEnabled(getActivity())) {
                    final LogoutController _controller = new LogoutController(getActivity(), this);
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

                    dialog.setMessage(getResources().getString(R.string.logout_msg)).setNegativeButton(R.string.new_yes
                            , new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    showProgressDialog(getResources().getString(R.string.please_wait));
                                    _controller.getData(AppConstants.LOGOUT_REQUEST, "");
                                }
                            }).setPositiveButton(R.string.new_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                            dialog.cancel();
                        }
                    }).setIcon(android.R.drawable.ic_dialog_alert);
                    AlertDialog alert11 = dialog.create();
                    alert11.show();
                    alert11.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.home_light_blue));
                    alert11.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.canceltxt_color));
                } else {
                    ToastUtils.showToast(getActivity(), getString(R.string.error_network));
                }
                break;
            case R.id.family_details:
                ((SettingsActivity) getActivity()).replaceFragment(new FragmentFamilyDetail(), null, true);
                break;
            case R.id.editProfile:
                Intent intent = new Intent(getActivity(), EditProfieActivity.class);
                intent.putExtra("bio", bio);
                intent.putExtra("firstName", firstName);
                intent.putExtra("lastName", lastName);
                intent.putExtra("phoneNumber", phoneNumber);
                startActivity(intent);
                break;
            case R.id.cityChange:
                ((SettingsActivity) getActivity()).replaceFragment(new ChangeCityFragment(), null, true);
                break;
            case R.id.notifications:
                ((SettingsActivity) getActivity()).replaceFragment(new NotificationSettingsFragment(), null, true);
                break;
            case R.id.viewFollowingTopics:
                Intent intenrt = new Intent(getActivity(), FollowedTopicsListingActivity.class);
                startActivity(intenrt);
                break;
            case R.id.facebookConnect:
                if ("CONNECT".equals(facebookConnectTextView.getText().toString())) {
                    Utils.pushEvent(getActivity(), GTMEventType.FACEBOOK_CONNECT_EVENT, SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId(), "settings");
                    FacebookUtils.facebookLogin(getActivity(), this);
                }
                break;
            default:
                break;
        }

    }

    @Override
    protected void updateUi(Response response) {
        removeProgressDialog();
        if (response == null) {
            Toast.makeText(getActivity(), getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            return;
        }
        LogoutResponse responseData = (LogoutResponse) response.getResponseObject();
        String message = responseData.getResult().getMessage();
        if (responseData.getResponseCode() == 200) {
            String pushToken = SharedPrefUtils.getDeviceToken(getActivity());
            SharedPrefUtils.clearPrefrence(getActivity());
            SharedPrefUtils.setDeviceToken(getActivity(), pushToken);
            /**
             * delete table from local also;
             */
            UserTable _tables = new UserTable((BaseApplication) getActivity().getApplicationContext());
            _tables.deleteAll();

            TableFamily _familytables = new TableFamily((BaseApplication) getActivity().getApplicationContext());
            _familytables.deleteAll();

            TableAdult _adulttables = new TableAdult((BaseApplication) getActivity().getApplicationContext());
            _adulttables.deleteAll();

            TableKids _kidtables = new TableKids((BaseApplication) getActivity().getApplicationContext());
            _kidtables.deleteAll();

            new TableAppointmentData(BaseApplication.getInstance()).deleteAll();
            new TableNotes(BaseApplication.getInstance()).deleteAll();
            new TableFile(BaseApplication.getInstance()).deleteAll();
            new TableAttendee(BaseApplication.getInstance()).deleteAll();
            new TableWhoToRemind(BaseApplication.getInstance()).deleteAll();


            new TableTaskData(BaseApplication.getInstance()).deleteAll();
            new TableTaskList(BaseApplication.getInstance()).deleteAll();
            new TaskTableAttendee(BaseApplication.getInstance()).deleteAll();
            new TaskTableWhoToRemind(BaseApplication.getInstance()).deleteAll();
            new TaskTableFile(BaseApplication.getInstance()).deleteAll();
            new TaskTableNotes(BaseApplication.getInstance()).deleteAll();
            new TaskCompletedTable(BaseApplication.getInstance()).deleteAll();
            new TableApiEvents(BaseApplication.getInstance()).deleteAll();

            new ExternalCalendarTable(BaseApplication.getInstance()).deleteAll();

            // clear cachee
            AppointmentManager.getInstance(getActivity()).clearList();
            BaseApplication.setBlogResponse(null);
            BaseApplication.setBusinessREsponse(null);

            // clear all sessions

            if (StringUtils.isNullOrEmpty(message)) {
                Toast.makeText(getActivity(), getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }

            // set logout flag
            SharedPrefUtils.setLogoutFlag(getActivity(), true);
            Intent intent = new Intent(getActivity(), ActivityLogin.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            getActivity().finish();

        } else if (responseData.getResponseCode() == 400) {
            if (StringUtils.isNullOrEmpty(message)) {
                Toast.makeText(getActivity(), getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void getFacebookUser(GraphUser user) {
        try {
            if (user != null) {

                Session session = Session.getActiveSession();
                if (session.isOpened()) {
                    accessToken = session.getAccessToken();
                }

                SocialConnectRequest socialConnectRequest = new SocialConnectRequest();
                socialConnectRequest.setToken(accessToken);
                socialConnectRequest.setReferer("fb");

                Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                LoginRegistrationAPI socialConnectAPI = retrofit.create(LoginRegistrationAPI.class);
                Call<BaseResponse> call = socialConnectAPI.socialConnect(socialConnectRequest);
                call.enqueue(socialConnectResponseListener);

            }
        } catch (Exception e) {
            // e.printStackTrace();
            removeProgressDialog();
//            showToast("Try again later.");
        }
    }

    private Callback<BaseResponse> socialConnectResponseListener = new Callback<BaseResponse>() {
        @Override
        public void onResponse(Call<BaseResponse> call, retrofit2.Response<BaseResponse> response) {
            if (response == null || response.body() == null) {
//                showToast("Something went wrong from server");
                return;
            }
            try {
                BaseResponse responseData = (BaseResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    Log.d("socialConnectListen", "SUCCESS");
                    facebookConnectTextView.setText("CONNECTED");
                } else {
                    Log.d("socialConnectListener", "FAILURE");
                    facebookConnectTextView.setText("CONNECT");
                }
            } catch (Exception e) {
                removeProgressDialog();
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<BaseResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0) {
            removeProgressDialog();
        }
        FacebookUtils.onActivityResult(getActivity(), requestCode, resultCode, data);
    }

}
