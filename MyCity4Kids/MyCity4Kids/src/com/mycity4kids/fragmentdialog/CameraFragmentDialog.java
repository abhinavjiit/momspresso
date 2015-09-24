package com.mycity4kids.fragmentdialog;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.mycity4kids.R;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.interfaces.IOnSubmitGallery;

public class CameraFragmentDialog extends DialogFragment implements OnClickListener{
	IOnSubmitGallery IOnSubmitListner;

	
	public void setSubmitListner(IOnSubmitGallery IOnSubmitListner){
		this.IOnSubmitListner=IOnSubmitListner;
		
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Dialog dialog = new Dialog(getActivity());  
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);  
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  
				WindowManager.LayoutParams.FLAG_FULLSCREEN);  
		dialog.setContentView(R.layout.custom_gallery_dialog);  
		dialog.getWindow().setBackgroundDrawable(  
				new ColorDrawable(Color.TRANSPARENT));  
		  dialog.show();  
		 ((Button) dialog.findViewById(R.id.albumBtn)).setOnClickListener(this);  
		 ( (Button) dialog.findViewById(R.id.cameraBtn)).setOnClickListener(this);
		 ( (Button) dialog.findViewById(R.id.cancelBtn)).setOnClickListener(this);

		
		return dialog;  

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.albumBtn:
			getDialog().dismiss();
			IOnSubmitListner.setOnSubmitListner(Constants.ALBUM_TYPE);
			break;
		case R.id.cameraBtn:
			getDialog().dismiss();
			IOnSubmitListner.setOnSubmitListner(Constants.GALLERY_TYPE);
			break;
		case R.id.cancelBtn:
			getDialog().dismiss();
			break;
		default:
			break;
		}

	}
}
