package com.mycity4kids.ui.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.gson.internal.LinkedTreeMap;
import com.mycity4kids.base.BaseFragment;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.R;

import java.util.Map;

/**
 * Created by hemant on 6/7/18.
 */

public class EditGpJoiningFormTabFragment extends BaseFragment {

    private LinkedTreeMap<String, String> groupQAMap;
    private EditText groupQAEditText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.edit_gp_join_form_tab_fragment, null);
        groupQAEditText = (EditText) view.findViewById(R.id.groupQAEditText);
        if (null != getArguments().getSerializable("groupQA")) {
            groupQAMap = (LinkedTreeMap<String, String>) getArguments().getSerializable("groupQA");
            groupQAEditText.setText(groupQAMap.get("1"));
        } else {
            groupQAMap = new LinkedTreeMap<>();
        }
        return view;

    }

    public Map<String, String> getUpdatedDetails() {
        if (StringUtils.isNullOrEmpty(groupQAEditText.getText().toString())) {
            return null;
        }
        groupQAMap.put("1", groupQAEditText.getText().toString());
        return groupQAMap;
    }
}
