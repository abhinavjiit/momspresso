package com.mycity4kids.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.logout.LogoutResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.reminders.AppointmentManager;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.activity.JoinFamilyActivity;

/**
 * Created by khushboo.goyal on 08-06-2015.
 */
//
public class FragmentSetting extends BaseFragment implements View.OnClickListener {
int cityId;
TextView cityChange;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.aa_setting, container, false);
        Utils.pushOpenScreenEvent(getActivity(), "Settings", SharedPrefUtils.getUserDetailModel(getActivity()).getId() + "");

        ((DashboardActivity) getActivity()).refreshMenu();

        ((TextView) view.findViewById(R.id.logout)).setOnClickListener(this);
        ((TextView) view.findViewById(R.id.family_details)).setOnClickListener(this);
      //  ((TextView) view.findViewById(R.id.sync_setting)).setOnClickListener(this);
        ((TextView) view.findViewById(R.id.external_cal)).setOnClickListener(this);
        ((TextView) view.findViewById(R.id.notification)).setOnClickListener(this);
        cityChange=(TextView) view.findViewById(R.id.cityChange);
        cityChange.setOnClickListener(this);
        cityId=SharedPrefUtils.getCurrentCityModel(getActivity()).getId();
        switch (cityId)
        {
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
           /* case 100:
                radioGroup.check(R.id.Others);
                break;*/
        }
        return view;
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

//                            TableAdult _tables = new TableAdult((BaseApplication) getActivity().getApplicationContext());
//                            if (_tables.getRowsCount() > 0) {
//
//                                showProgressDialog(getResources().getString(R.string.please_wait));
//                                String sessionId = SharedPrefUtils.getUserDetailModel(getActivity()).getSessionId();
//
//                                if (!StringUtils.isNullOrEmpty(sessionId))
//                                    _controller.getData(AppConstants.LOGOUT_REQUEST, sessionId);
//
//                            } else {
//                                Toast.makeText(getActivity(), "User not logged in ", Toast.LENGTH_SHORT).show();
//                            }

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

                ((DashboardActivity) getActivity()).replaceFragment(new FragmentFamilyDetail(), null, true);

                break;

         /*   case R.id.sync_setting:
                bundle = new Bundle();
                bundle.putBoolean(Constants.IS_COMMING_FROM_SETTING, true);
                ((DashboardActivity) getActivity()).replaceFragment(new SyncSettingFragment(), bundle, true);
                break;*/

            case R.id.external_cal:

                ((DashboardActivity) getActivity()).replaceFragment(new ExternalCalFragment(), null, true);
                break;

            case R.id.notification:
                bundle = new Bundle();
                bundle.putBoolean(Constants.IS_COMMING_FROM_SETTING, true);
                ((DashboardActivity) getActivity()).replaceFragment(new NotificationFragment(), bundle, true);
                break;
            case  R.id.cityChange:
                ((DashboardActivity) getActivity()).replaceFragment(new ChangeCityFragment(),null,true);
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

            startActivity(new Intent(getActivity(), JoinFamilyActivity.class));
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
}
