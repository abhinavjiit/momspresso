package com.mycity4kids.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;
import androidx.fragment.app.DialogFragment;
import com.mycity4kids.R;
import com.mycity4kids.models.response.UserDetailResult;
import com.mycity4kids.ui.adapter.ChooseLoginAccountAdapter;
import com.mycity4kids.ui.login.LoginActivity;
import java.util.ArrayList;

/**
 * Created by manish.soni on 08-09-2015.
 */
public class ChooseLoginAccountDialogFragment extends DialogFragment {

    private ListView cityListView;
    private ChooseLoginAccountAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.choose_login_account_dialog, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        cityListView = rootView.findViewById(R.id.cityListView);

        final ArrayList<UserDetailResult> accountList = getArguments().getParcelableArrayList("accountList");

        adapter = new ChooseLoginAccountAdapter(accountList);
        cityListView.setAdapter(adapter);
        cityListView.setOnItemClickListener((parent, view, position, id) -> {
            ((LoginActivity) getActivity()).loginWithAccount(accountList.get(position));
            dismiss();
        });

        return rootView;
    }
}