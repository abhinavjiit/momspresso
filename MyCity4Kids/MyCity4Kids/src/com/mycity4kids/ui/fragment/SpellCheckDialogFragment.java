package com.mycity4kids.ui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mycity4kids.R;

/**
 * Created by user on 08-06-2015.
 */
public class SpellCheckDialogFragment extends DialogFragment implements OnClickListener {

    private ISpellcheckResult iSpellcheckResult;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.spell_check_custom_dialog, container,
                false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView continuePublishTextView = (TextView) rootView.findViewById(R.id.continuePublishTextView);
        TextView cancelTextView = (TextView) rootView.findViewById(R.id.cancelTextView);

        continuePublishTextView.setOnClickListener(this);
        cancelTextView.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        iSpellcheckResult = (ISpellcheckResult) context;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancelTextView:
                dismiss();
                break;
            case R.id.continuePublishTextView:
                iSpellcheckResult.onContinuePublish();
                dismiss();
                break;
        }
    }

    public interface ISpellcheckResult {

        void onContinuePublish();
    }
}