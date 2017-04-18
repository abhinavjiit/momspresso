package com.mycity4kids.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.SubscriptionAndLanguageSettingsModel;
import com.mycity4kids.models.request.SubscriptionUpdateRequest;
import com.mycity4kids.models.request.UpdateUserDetailsRequest;
import com.mycity4kids.models.response.SubscriptionSettingsResponse;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.SubscriptionsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.UserAttributeUpdateAPI;
import com.mycity4kids.ui.adapter.SubscriptionSettingsListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 2/4/16.
 */
public class SubscriptionSettingsFragment extends BaseFragment implements View.OnClickListener {

    private ListView subscriptionListView;
    private EditText subscriptionEmailEditText;
    private TextView subscriptionEmailTextView;
    private TextView editSubscriptionEmailTextView;

    ArrayList<SubscriptionAndLanguageSettingsModel> subscriptionSettingsList;
    SubscriptionSettingsListAdapter subscriptionSettingsListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.subscription_settings, container, false);
        Utils.pushOpenScreenEvent(getActivity(), "Subscription Settings", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "");
        setHasOptionsMenu(true);
        subscriptionSettingsList = new ArrayList<>();
        subscriptionListView = (ListView) view.findViewById(R.id.subscriptionSettingListView);
        subscriptionEmailTextView = (TextView) view.findViewById(R.id.subscriptionEmailTextView);
        subscriptionEmailEditText = (EditText) view.findViewById(R.id.subscriptionEmailEditText);
        editSubscriptionEmailTextView = (TextView) view.findViewById(R.id.editSubscriptionEmailTextView);

        editSubscriptionEmailTextView.setOnClickListener(this);
        subscriptionEmailTextView.setText(SharedPrefUtils.getUserDetailModel(getActivity()).getSubscriptionEmail());
        subscriptionEmailEditText.setText(SharedPrefUtils.getUserDetailModel(getActivity()).getSubscriptionEmail());

        checkSubscriptionStatus();
        return view;
    }

    private void checkSubscriptionStatus() {

        showProgressDialog("Please wait ...");
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        SubscriptionsAPI subscriptionsAPI = retrofit.create(SubscriptionsAPI.class);
        Call<SubscriptionSettingsResponse> call = subscriptionsAPI.getSubscriptionList(SharedPrefUtils.getUserDetailModel(getActivity()).getEmail());
        call.enqueue(subscriptionSettingsResponseCallback);
    }

    private Callback<SubscriptionSettingsResponse> subscriptionSettingsResponseCallback = new Callback<SubscriptionSettingsResponse>() {
        @Override
        public void onResponse(Call<SubscriptionSettingsResponse> call, retrofit2.Response<SubscriptionSettingsResponse> response) {

            removeProgressDialog();
            if (response == null || response.body() == null) {
                return;
            }
            try {
                SubscriptionSettingsResponse responseData = (SubscriptionSettingsResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                    for (Map.Entry<String, Object> entry : responseData.getData().getResult().entrySet()) {
                        if (entry.getValue() instanceof String) {
                            Log.d("Notification Items = ", entry.getKey() + "/" + entry.getValue());

                            SubscriptionAndLanguageSettingsModel subscriptionAndLanguageSettingsModel = new SubscriptionAndLanguageSettingsModel();
                            subscriptionAndLanguageSettingsModel.setStatus((String) entry.getValue());
                            subscriptionAndLanguageSettingsModel.setOriginalStatus((String) entry.getValue());
                            subscriptionAndLanguageSettingsModel.setName(entry.getKey());
                            switch (entry.getKey()) {
                                case "newsletters":
                                    subscriptionAndLanguageSettingsModel.setDisplayName("Subscribe to the Top Reads Of The Month");
                                    break;
                                case "trending":
                                    subscriptionAndLanguageSettingsModel.setDisplayName("Subscribe to the Trending Blog Of The Day");
                                    break;
                                case "momspresso":
                                    subscriptionAndLanguageSettingsModel.setDisplayName("Subscribe to the Momspresso Videos");
                                    break;
                                case "editorial":
                                    subscriptionAndLanguageSettingsModel.setDisplayName("Subscribe to the Best In Your City");
                                    break;
                                default:
                                    subscriptionAndLanguageSettingsModel.setDisplayName(entry.getKey());
                            }
                            subscriptionSettingsList.add(subscriptionAndLanguageSettingsModel);
                        } else if (entry.getValue() instanceof Map) {
                            Map<String, String> retMap = new Gson().fromJson(entry.getValue().toString(), new TypeToken<HashMap<String, String>>() {
                            }.getType());

                            for (Map.Entry<String, String> langEntry : retMap.entrySet()) {
                                SubscriptionAndLanguageSettingsModel subscriptionAndLanguageSettingsModel = new SubscriptionAndLanguageSettingsModel();
                                subscriptionAndLanguageSettingsModel.setStatus(langEntry.getValue());
                                subscriptionAndLanguageSettingsModel.setOriginalStatus(langEntry.getValue());
                                subscriptionAndLanguageSettingsModel.setName(langEntry.getKey());
                                switch (langEntry.getKey()) {
                                    case "hindi":
                                        subscriptionAndLanguageSettingsModel.setDisplayName("Hindi");
                                        break;
                                    case "bengali":
                                        subscriptionAndLanguageSettingsModel.setDisplayName("Bangla");
                                        break;
                                    case "marathi":
                                        subscriptionAndLanguageSettingsModel.setDisplayName("Marathi");
                                        break;
                                    default:
                                        subscriptionAndLanguageSettingsModel.setDisplayName(entry.getKey());
                                }
                                subscriptionSettingsList.add(subscriptionAndLanguageSettingsModel);
                            }
                        }
                    }
                    subscriptionSettingsListAdapter = new SubscriptionSettingsListAdapter(getActivity(), subscriptionSettingsList);
                    subscriptionListView.setAdapter(subscriptionSettingsListAdapter);
                } else {

                }
            } catch (Exception e) {
                removeProgressDialog();
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<SubscriptionSettingsResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    @Override
    protected void updateUi(Response response) {
    }

    public void updateSubscriptions() {
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            Toast.makeText(getActivity(), R.string.error_network, Toast.LENGTH_SHORT).show();
            return;
        }
        String sEmail = subscriptionEmailEditText.getText().toString();
        final Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        if ("CANCEL".equals(editSubscriptionEmailTextView.getText().toString())) {
            if (StringUtils.isNullOrEmpty(sEmail) || !StringUtils.isValidEmail(sEmail)) {
                subscriptionEmailEditText.setFocusableInTouchMode(true);
                subscriptionEmailEditText.setError("Please enter a valid email id");
                subscriptionEmailEditText.requestFocus();
                return;
            } else {
                UpdateUserDetailsRequest updateUserDetailsRequest = new UpdateUserDetailsRequest();
                updateUserDetailsRequest.setSubscriptionEmail(sEmail);
                UserAttributeUpdateAPI userAPI = retrofit.create(UserAttributeUpdateAPI.class);
                Call<UserDetailResponse> userUpdateCall = userAPI.updateSubscriptionEmail(updateUserDetailsRequest);
                userUpdateCall.enqueue(updateSubscriptionEmailResponseListener);

                Log.d("GTM Subs Email Change", ":" + sEmail);
                Utils.pushEvent(getActivity(), GTMEventType.SUBSCRIPTION_EMAIL_CHANGED_EVENT, SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId(),
                        "Subscription Settings");
            }
        }

        SubscriptionsAPI subscriptionsAPI = retrofit.create(SubscriptionsAPI.class);
        SubscriptionUpdateRequest subscriptionUpdateRequest = new SubscriptionUpdateRequest();

        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < subscriptionSettingsList.size(); i++) {
            map.put(subscriptionSettingsList.get(i).getName(), subscriptionSettingsList.get(i).getStatus());
        }

        subscriptionUpdateRequest.setEmail(SharedPrefUtils.getUserDetailModel(getActivity()).getEmail());
        subscriptionUpdateRequest.setSubscribe(map);

        Call<SubscriptionSettingsResponse> call = subscriptionsAPI.updateSubscriptions(subscriptionUpdateRequest);
        call.enqueue(updateSubscriptionsReponseListener);
        Log.d("GTM Subscription", ":" + map.values());
        for (int i = 0; i < subscriptionSettingsList.size(); i++) {
            if (!subscriptionSettingsList.get(i).getStatus().equals(subscriptionSettingsList.get(i).getOriginalStatus())) {
                if ("1".equals(subscriptionSettingsList.get(i).getStatus())) {
                    Log.d("GTM Subscription Added", ":" + subscriptionSettingsList.get(i).getDisplayName());
                    Utils.pushEventSubscriptionSettings(getActivity(), GTMEventType.EMAIL_SUBSCRIBE_EVENT, SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId(),
                            "Subscription Settings", subscriptionSettingsList.get(i).getName());
                } else {
                    Log.d("GTM Subscription Remove", ":" + subscriptionSettingsList.get(i).getDisplayName());
                    Utils.pushEventSubscriptionSettings(getActivity(), GTMEventType.EMAIL_UNSUBSCRIBE_EVENT, SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId(),
                            "Subscription Settings", subscriptionSettingsList.get(i).getName());
                }
            }
            subscriptionSettingsList.get(i).setOriginalStatus(subscriptionSettingsList.get(i).getStatus());
        }

    }

    private Callback<UserDetailResponse> updateSubscriptionEmailResponseListener = new Callback<UserDetailResponse>() {
        @Override
        public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
            if (response == null || response.body() == null) {
                return;
            }
            try {
                UserDetailResponse responseData = (UserDetailResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    editSubscriptionEmailTextView.setText("EDIT");
                    subscriptionEmailEditText.setVisibility(View.INVISIBLE);
                    subscriptionEmailTextView.setVisibility(View.VISIBLE);
                    subscriptionEmailTextView.setText(subscriptionEmailEditText.getText().toString());
                    UserInfo userInfo = SharedPrefUtils.getUserDetailModel(getActivity());
                    userInfo.setSubscriptionEmail(subscriptionEmailEditText.getText().toString());
                    SharedPrefUtils.setUserDetailModel(getActivity(), userInfo);
                } else {
                    Toast.makeText(getActivity(), "Error while updating email", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Error while updating email", Toast.LENGTH_SHORT).show();
                removeProgressDialog();
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<UserDetailResponse> call, Throwable t) {
            removeProgressDialog();
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private Callback<SubscriptionSettingsResponse> updateSubscriptionsReponseListener = new Callback<SubscriptionSettingsResponse>() {
        @Override
        public void onResponse(Call<SubscriptionSettingsResponse> call, retrofit2.Response<SubscriptionSettingsResponse> response) {
            if (response == null || response.body() == null) {
//                showToast("Something went wrong from server");
                return;
            }
            try {
                SubscriptionSettingsResponse responseData = (SubscriptionSettingsResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    if (null != getActivity()) {
                        Toast.makeText(getActivity(), "Subscription settings updated successfully", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (null != getActivity()) {
                        Toast.makeText(getActivity(), "Error while updating subscription settings", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                if (null != getActivity()) {
                    Toast.makeText(getActivity(), "Error while updating subscription settings", Toast.LENGTH_SHORT).show();
                }
                removeProgressDialog();
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<SubscriptionSettingsResponse> call, Throwable t) {
            if (null != getActivity()) {
                Toast.makeText(getActivity(), "Error while updating subscription settings", Toast.LENGTH_SHORT).show();
            }
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editSubscriptionEmailTextView:
                if ("EDIT".equals(editSubscriptionEmailTextView.getText().toString())) {
                    editSubscriptionEmailTextView.setText("CANCEL");
                    subscriptionEmailEditText.setVisibility(View.VISIBLE);
                    subscriptionEmailTextView.setVisibility(View.INVISIBLE);
                    subscriptionEmailEditText.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(subscriptionEmailEditText, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    editSubscriptionEmailTextView.setText("EDIT");
                    subscriptionEmailEditText.setVisibility(View.INVISIBLE);
                    subscriptionEmailTextView.setVisibility(View.VISIBLE);
                    subscriptionEmailTextView.setText(SharedPrefUtils.getUserDetailModel(getActivity()).getSubscriptionEmail());
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(subscriptionEmailEditText.getWindowToken(), 0);
                }
                break;
            default:
                break;
        }
    }

}
