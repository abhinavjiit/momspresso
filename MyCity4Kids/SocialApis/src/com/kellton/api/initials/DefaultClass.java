package com.kellton.api.initials;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

public class DefaultClass {
	public Context context;
	private ProgressDialog mProgressDialog ; 
	
	public void showProgress(final String message) {
		((Activity)context).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(mProgressDialog == null ) {
					mProgressDialog = new ProgressDialog(context) ; 
					mProgressDialog.setCancelable(false) ; 
					mProgressDialog.setMessage(message) ; 
				}
				if(! mProgressDialog.isShowing()) {
					mProgressDialog.show() ;
				} 		
			}
		}) ; 
	}
	public void removeProgress() {
		if(mProgressDialog != null && mProgressDialog.isShowing()) {
			((Activity)context).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mProgressDialog.dismiss() ; 		
				}
			}) ;
		}
	}
	public void showToast(final String message) {
		((Activity)context).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context, message, Toast.LENGTH_SHORT).show() ; 		
			}
		}) ;
		 
	}
}
