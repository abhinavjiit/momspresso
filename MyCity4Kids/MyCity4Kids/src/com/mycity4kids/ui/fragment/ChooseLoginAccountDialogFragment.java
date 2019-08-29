package com.mycity4kids.ui.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.models.response.UserDetailResult;
import com.mycity4kids.ui.activity.ActivityLogin;
import com.mycity4kids.ui.adapter.ChooseLoginAccountAdapter;

import java.util.ArrayList;

/**
 * Created by manish.soni on 08-09-2015.
 */
public class ChooseLoginAccountDialogFragment extends DialogFragment implements View.OnClickListener {

    private ListView cityListView;
    private TextView toolbarTitleTextView;
    private ProgressDialog mProgressDialog;
    private ChooseLoginAccountAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.choose_login_account_dialog, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        cityListView = (ListView) rootView.findViewById(R.id.cityListView);

        final ArrayList<UserDetailResult> accountList = getArguments().getParcelableArrayList("accountList");

        adapter = new ChooseLoginAccountAdapter(getActivity(), accountList);
        cityListView.setAdapter(adapter);
        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((ActivityLogin) getActivity()).loginWithAccount(accountList.get(position));
                dismiss();
            }
        });

        return rootView;
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {

        }
    }

}