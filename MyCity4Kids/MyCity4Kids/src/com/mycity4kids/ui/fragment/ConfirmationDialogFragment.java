package com.mycity4kids.ui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mycity4kids.R;

/**
 * Created by user on 08-06-2015.
 */
public class ConfirmationDialogFragment extends DialogFragment implements OnClickListener {

    //    private IConfirmationResult iConfirmationResult;
    private int position;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.confirmation_dialog, container,
                false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        position = getArguments().getInt("position");

        TextView continueTextView = (TextView) rootView.findViewById(R.id.continueTextView);
        TextView cancelTextView = (TextView) rootView.findViewById(R.id.cancelTextView);

        continueTextView.setOnClickListener(this);
        cancelTextView.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        iConfirmationResult = (IConfirmationResult) context;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancelTextView:
                dismiss();
                break;
            case R.id.continueTextView:
                IConfirmationResult iConfirmationResult = (IConfirmationResult) getParentFragment();
                iConfirmationResult.onContinue(position);
                dismiss();
                break;
        }
    }

    public interface IConfirmationResult {

        void onContinue(int position);
    }
}