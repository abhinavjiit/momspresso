package com.kellton.api.views;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

public class BaseWebDialog extends Dialog {

	private static final float[] DIMENSIONS_LANDSCAPE = { 460, 260 };
	private static final float[] DIMENSIONS_PORTRAIT = { 280, 420 };
	private static final FrameLayout.LayoutParams FILL = new FrameLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
	public Context context;
	
	private LinearLayout mTwDlgRootLayout;
	public WebView mWebView;
	
	private ProgressDialog mProgressDialog ;
	private boolean mWbVwPrgDlgRunning = false;
	
    private WebDialogListener mTwDialogListener;
    private String mUrl;
    
	public BaseWebDialog(Context context) {
		super(context);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        mTwDlgRootLayout = new LinearLayout(getContext());
        mTwDlgRootLayout.setOrientation(LinearLayout.VERTICAL);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setUpTitle();
        setUpWebView();

        Display display = getWindow().getWindowManager().getDefaultDisplay();
        final float scale = getContext().getResources().getDisplayMetrics().density;
        float[] dimensions = (display.getWidth() < display.getHeight()) ? DIMENSIONS_PORTRAIT : DIMENSIONS_LANDSCAPE;

        addContentView(mTwDlgRootLayout, new FrameLayout.LayoutParams( (int) (dimensions[0] * scale + 0.5f), (int) (dimensions[1] * scale + 0.5f)));
        
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setMessage("Processing...");
        mProgressDialog.setCancelable(false);
        
        mWebView.loadUrl(mUrl);
	}
	private void setUpWebView() {
        mWebView = new WebView(getContext());
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.getSettings().setJavaScriptEnabled(true);
        CookieManager.getInstance().setAcceptCookie(false);
        mWebView.setLayoutParams(FILL);
        mTwDlgRootLayout.addView(mWebView);
    }
	@Override
    protected void onStop() {
        mWbVwPrgDlgRunning = false;
        super.onStop();
    }

    public void onBackPressed() {
        if(!mWbVwPrgDlgRunning){
        	BaseWebDialog.this.dismiss();
            mTwDialogListener.onBackPressed();
        }
    }
	public interface WebDialogListener {
		public void onComplete(String value);
		public void onError(String value);
		public void onBackPressed();
	}
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
