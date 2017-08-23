package com.mycity4kids.ui.activity;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.PluginState;
import android.widget.ImageView;
import android.widget.TextView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.user.UserModel;
import com.mycity4kids.preference.SharedPrefUtils;

public class BookOrPayWebActivity extends BaseActivity implements OnClickListener{
	private String eCommerceUrl;
	private WebView bookOrPayWebView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utils.pushOpenScreenEvent(BookOrPayWebActivity.this, "Booking Payment Webpage", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");

		try {
			setContentView(R.layout.book_pay_web_view);
			bookOrPayWebView=(WebView)findViewById(R.id.book_or_pay_web_view);
			TextView headerTxt=(TextView)findViewById(R.id.txvHeaderText);
			headerTxt.setText("Payment");
			findViewById(R.id.cross_icon).setOnClickListener(this);
			Bundle bundle=getIntent().getExtras();

			if(bundle!=null){
				eCommerceUrl=bundle.getString(Constants.WEB_VIEW_ECOMMERECE);

				UserTable _table=new UserTable((BaseApplication)getApplicationContext());
				int count=_table.getCount();
				UserModel userModel = _table.getAllUserData();
				if(count>0){
					
					bookOrPayWebView.getSettings().setJavaScriptEnabled(true);
					bookOrPayWebView.getSettings().setLoadWithOverviewMode(true);
					bookOrPayWebView.getSettings().setUseWideViewPort(true);
					bookOrPayWebView.getSettings().setBuiltInZoomControls(true);
					bookOrPayWebView.getSettings().setPluginState(PluginState.ON);
					bookOrPayWebView.getSettings().setAllowFileAccess(true);
					bookOrPayWebView.setFocusable(true);
					bookOrPayWebView.setFocusableInTouchMode(true);
					bookOrPayWebView.getSettings().setDomStorageEnabled(true);
					bookOrPayWebView.setWebViewClient(new MyBrowse());
					String sessionId=userModel.getUser().getSessionId();
					if(!StringUtils.isNullOrEmpty(sessionId)){
						bookOrPayWebView.loadUrl(eCommerceUrl+"?session_id="+sessionId+"&user_id="+userModel.getUser().getId());
					}
					
				}else{
					//this case will never come.
					String message = "Please login first.";
					showToast(message);
				}
			}


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class MyBrowse extends WebViewClient{
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			super.onPageStarted(view, url, favicon);
			showProgressDialog("Loading...");
		}
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			view.requestFocus();
			removeProgressDialog();
		}
		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler,
				SslError error) {
			// TODO Auto-generated method stub
			super.onReceivedSslError(view, handler, error);
			handler.proceed();
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && bookOrPayWebView.canGoBack()) {
			bookOrPayWebView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void updateUi(Response response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cross_icon:
			finish();
			break;

		default:
			break;
		}

	}

}
