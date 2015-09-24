package com.mycity4kids.fragmentdialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.enums.DialogButtonEvent;
import com.mycity4kids.enums.DialogEnum;
import com.mycity4kids.interfaces.IGetRateAndUpdateEvent;
/**
 * 
 * @author deepanker.chaudhary
 * we can add multiple dialog in this class 
 * according to ID.
 * for this first you have to add id in DialogEnum class
 * & according to that we will handle on click & dialog view.
 *
 */
public class RateAndUpdateDialog extends DialogFragment {
	private TextView mInputText;
	private Context mContext;
	private DialogEnum mId;
	private IGetRateAndUpdateEvent mIGetRateAndUpdateEvent;

	public void newInstance(Context pContext , DialogEnum pId ,IGetRateAndUpdateEvent IGetRateUpdate){
		mContext=pContext;
		mId=pId;
		mIGetRateAndUpdateEvent=IGetRateUpdate;
	}
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		switch (mId) {
		case RAME_ME_DIALOG:
			builder.setTitle(getString(R.string.rate_this));
			builder.setMessage(getString(R.string.rate_description));
			builder.setPositiveButton(getString(R.string.rate_now), okClick);
			builder.setNegativeButton(getString(R.string.later), okClick);

			break;
		case UPDATE_DIALOG:
			builder.setTitle(getString(R.string.update_title));
			builder.setMessage(getString(R.string.update_desctiption));
			builder.setPositiveButton(getString(R.string.install_now), okClick);
			builder.setNegativeButton(getString(R.string.later), okClick);
			break;
		default:
			break;
		}
		Dialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		return dialog;
		/*		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		mInputText = new TextView(getActivity());
		mInputText.setTextColor(Color.BLACK) ; 
		mInputText.setText(getString(R.string.gps_enabled_alert));
		mInputText.setPadding(30, 30, 30, 30);
		mInputText.setTextSize(20);
		builder.setPositiveButton("OK", okClick);
		builder.setNegativeButton("Select City", okClick);
		//builder.setCancelable(false);

		builder.setView(mInputText);
		Dialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		return dialog;*/

	}

	OnClickListener okClick=new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:

				switch (mId) {

				case RAME_ME_DIALOG:{
					mIGetRateAndUpdateEvent.getDialogTypeAndEvent(DialogEnum.RAME_ME_DIALOG, DialogButtonEvent.RATE_ME_OR_INSTALL);
				}

				break;

				case UPDATE_DIALOG:
				{
					mIGetRateAndUpdateEvent.getDialogTypeAndEvent(DialogEnum.UPDATE_DIALOG, DialogButtonEvent.RATE_ME_OR_INSTALL);
				}

				break;
				}
				getDialog().cancel();
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				switch (mId) {

				case RAME_ME_DIALOG:{
					mIGetRateAndUpdateEvent.getDialogTypeAndEvent(DialogEnum.RAME_ME_DIALOG, DialogButtonEvent.LATER);
				}

				break;

				case UPDATE_DIALOG:
				{
					mIGetRateAndUpdateEvent.getDialogTypeAndEvent(DialogEnum.UPDATE_DIALOG, DialogButtonEvent.LATER);
				}

				break;
				}

				break;

			default:
				break;
			}


		}
	};		







}
