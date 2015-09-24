package com.mycity4kids.ui.dialog;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.mycity4kids.R;
import com.mycity4kids.listener.OnButtonClicked;


public class PhotoOptionsDialog extends DialogFragment implements OnClickListener {

	private OnButtonClicked onButtonClicked;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// remove title and frame from dialog-fragment
		setStyle(STYLE_NO_TITLE, 0);
	}
	
	public void onActivityCreated(Bundle arg0) {
		super.onActivityCreated(arg0);
		getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_photo_edit_options, container, false);
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));

		view.findViewById(R.id.btnCamera).setOnClickListener(this);
		view.findViewById(R.id.btnGallery).setOnClickListener(this);
		view.findViewById(R.id.btnCancel).setOnClickListener(this);

		return view;
	}

	@Override
	public void onClick(View pClickSource) {
		switch (pClickSource.getId()) {
		case R.id.btnGallery:
			onButtonClicked.onButtonCLick(R.id.btnGallery);
			dismiss();
			break;
		case R.id.btnCamera:
			onButtonClicked.onButtonCLick(R.id.btnCamera);
			dismiss();
			break;
		case R.id.btnCancel:
			dismiss();
			break;

		}
	}

	public void setonButtonClickListener(OnButtonClicked onButtonClicked) {
		this.onButtonClicked = onButtonClicked;
	}
	
}