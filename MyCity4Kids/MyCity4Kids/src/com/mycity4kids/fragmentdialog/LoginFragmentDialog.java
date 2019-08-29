package com.mycity4kids.fragmentdialog;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

import com.mycity4kids.R;
import com.mycity4kids.constants.Constants;

public class LoginFragmentDialog extends DialogFragment implements OnClickListener{
	private int mCategoryId;
	private int mBusinessOrEventType;
	private String mBusinessOrEventId;
	private String mDistance;


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		Bundle bundle=getArguments();
		if(bundle!=null){
			mCategoryId=bundle.getInt(Constants.CATEGORY_ID);
			mBusinessOrEventType=bundle.getInt(Constants.PAGE_TYPE);
			mBusinessOrEventId=bundle.getString(Constants.BUSINESS_OR_EVENT_ID);
			mDistance=bundle.getString(Constants.DISTANCE);
		}

		final Dialog dialog = new Dialog(getActivity());
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		dialog.setContentView(R.layout.login_alert_dialog);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		dialog.show();
		dialog.findViewById(R.id.login_btn).setOnClickListener(this);
		dialog.findViewById(R.id.cancelBtn).setOnClickListener(this);


		return dialog;

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cancelBtn:
			getDialog().dismiss();
			break;
		default:
			break;
		}

	}
}
