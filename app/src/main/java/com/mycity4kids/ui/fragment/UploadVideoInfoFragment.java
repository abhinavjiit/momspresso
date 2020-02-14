package com.mycity4kids.ui.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mycity4kids.base.BaseFragment;
import com.mycity4kids.R;

/**
 * Created by hemant on 2/8/17.
 */
public class UploadVideoInfoFragment extends BaseFragment implements View.OnClickListener {
    private TextView getStartedTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.upload_video_info_fragment, container, false);

        getStartedTextView = (TextView) view.findViewById(R.id.getStartedTextView);

        getStartedTextView.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.getStartedTextView:
                ChooseVideoUploadOptionDialogFragment chooseVideoUploadOptionDialogFragment = new ChooseVideoUploadOptionDialogFragment();
                FragmentManager fm = getChildFragmentManager();
                Bundle _args = new Bundle();
                _args.putString("activity", "dashboard");
                chooseVideoUploadOptionDialogFragment.setArguments(_args);
                chooseVideoUploadOptionDialogFragment.setCancelable(true);
                chooseVideoUploadOptionDialogFragment.show(fm, "Choose video option");
                break;
        }
    }

}
