package com.kellton.api.initials;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.MailTo;
import android.net.Uri;
import android.net.http.SslError;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.kellton.api.interfaces.ILogin;
import com.kellton.api.model.MeetupUser;
import com.kellton.socialapis.BuildConfig;

public class MeetUp extends DefaultClass {

	private static MeetUp _up;
	private final static String LOG_TAG = "MeetUp" ; 
	private String key;
	private String secret;
	private String callbackURL ;
	private WebView web_view; 
	private ILogin login ; 

	private OAuthService service;
	private Token requestToken;
	
	
	/**
	 * @return the callbackURL
	 */
	public String getCallbackURL() {
		return callbackURL;
	}

	/**
	 * @param callbackURL the callbackURL to set
	 */
	public void setCallbackURL(String callbackURL) {
		this.callbackURL = callbackURL;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the secret
	 */
	public String getSecret() {
		return secret;
	}

	/**
	 * @param secret
	 *            the secret to set
	 */
	public void setSecret(String secret) {
		this.secret = secret;
	}

	/**
	 * @return the context
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * @param context
	 *            the context to set
	 */
	public void setContext(Context context) {
		this.context = context;
	}

	private MeetUp() {

	}

	private MeetUp(String consumer_key, String consumer_secret, String callback_url , Context context) {
		_up = new MeetUp();
		key = consumer_key;
		secret = consumer_secret;
		callbackURL = callback_url ; 
		this.context = context;
	}

	public static MeetUp getInstance(String consumer_key, String consumer_secret, String callback_url , Context context) {
//		if (_up == null) {
//			_up = new MeetUp(consumer_key, consumer_secret, callback_url , context);
//		}
		_up = new MeetUp(consumer_key, consumer_secret, callback_url , context);
		return _up;
	}
	public void doLogin(final ILogin ilogin) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					showProgress("Loading...") ; 
					login = ilogin ; 
					service = new ServiceBuilder()
					.provider(MeetupApi.class)
					.apiKey( key )
					.apiSecret( secret )
					.callback( callbackURL )
					.build();

					requestToken = service.getRequestToken();
					final String authURL = service.getAuthorizationUrl(requestToken);
					
					((Activity)context).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							
							 web_view = new WebView(context) ; 
					        web_view.getSettings().setDefaultFontSize(16);
							web_view.getSettings().setBuiltInZoomControls(true);
							web_view.getSettings().setGeolocationEnabled(true);
							web_view.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

							/**
							 * DOM Storage, Cache mode and RenderPriority.
							 */
							web_view.getSettings().setDomStorageEnabled(true);
							web_view.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

							/**
							 * Enable AppCache. Set cache size to 8 mb by default. should be more
							 * than enough.
							 */
							String appCachePath = context.getCacheDir().getAbsolutePath();
							web_view.getSettings().setAppCachePath(appCachePath);
							web_view.getSettings().setAllowFileAccess(true);
							web_view.getSettings().setAppCacheEnabled(true);

							/**
							 * Enable Database and set Database path.
							 */
							web_view.getSettings().setDatabaseEnabled(true);

							web_view.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

							// _internationalWebView.addJavascriptInterface(new
							// MmtJsDelegate(),AppConstants.MMT_JS_DELEGATE_NAME);

							/**
							 * WebChromeClient with overridden methods for updating quota.
							 */
							web_view.setWebChromeClient(new WebChromeClient() {
								@Override
								public void onGeolocationPermissionsShowPrompt(String origin,
										GeolocationPermissions.Callback callback) {
									callback.invoke(origin, true, false);
								}
							}); 
							web_view.setWebViewClient(new MeetUpWebViewClient());
							
							web_view.setFocusable(true);
							web_view.setFocusableInTouchMode(true);
							web_view.requestFocus();
							ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT) ;
					        ((Activity)context).addContentView(web_view, params) ;
					        web_view.loadUrl(authURL + requestToken.getToken() ) ;		
						}
					}) ; 
				} catch (Exception e) {
					e.printStackTrace() ; 
				}
			}
		}).start() ;
	}
	/**
	 *
	 */
	private class MeetUpWebViewClient extends WebViewClient {

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			if (BuildConfig.DEBUG) {
				Log.i(LOG_TAG , "onReceivedError: " + failingUrl + ", "
						+ description);
			}
			super.onReceivedError(view, errorCode, description, failingUrl);
		}

		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) 
		{

			handler.proceed();
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			if (BuildConfig.DEBUG) {
				Log.i(LOG_TAG, "onPageStarted: " + url);
			}
			showProgress("Loading...");
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.i(LOG_TAG, "shouldOverrideUrlLoading: " + url);

			/**
			 * Opening dialer screen. It does not require n/w check.
			 */
			if (url.startsWith("tel:")) {
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
				context.startActivity(intent);
				return true;
			}

			/**
			 * Opening mail screen. It does not require n/w check.
			 */
			if (url.startsWith("mailto:")) {
				MailTo mt = MailTo.parse(url);
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.putExtra(Intent.EXTRA_EMAIL, new String[] { mt.getTo() });
				intent.putExtra(Intent.EXTRA_TEXT, mt.getBody());
				intent.putExtra(Intent.EXTRA_SUBJECT, mt.getSubject());
				intent.putExtra(Intent.EXTRA_CC, mt.getCc());
				intent.setType("message/rfc822");
				context.startActivity(intent);
				view.reload();
				return true;
			}
			if(url.equals("https://www.meetup.com/") || url.equals("https://www.meetup.com") || url.equals("http://www.meetup.com/") || url.equals("http://www.meetup.com") ) { 
				web_view.setVisibility(View.GONE) ; 
				web_view.destroy() ;
				removeProgress() ;
				return true ;
			}
			if(url.startsWith(callbackURL)) {
				web_view.setVisibility(View.GONE) ; 
				web_view.destroy() ;
				removeProgress() ;
				login.success(getUser(url)) ; 
				return true ;
			}
			if(url.startsWith("https://www.meetup.com/") || url.startsWith("https://secure.meetup.com/") || url.startsWith("http://www.meetup.com/") || url.startsWith("http://secure.meetup.com/")) {
				
				return false ;
			} else {
				showToast("This action can't be performed") ; 
				return true ;	
			}
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);

			// Set cookie for maintain login state
			if (BuildConfig.DEBUG) {
				Log.i(LOG_TAG, "onPageFinished: " + url);
			}
			removeProgress() ; 
		}
	}
	public MeetupUser getUser(String url) {
		MeetupUser user = new MeetupUser() ;
		String arr[] = url.split("\\?") ;
		arr = arr[arr.length - 1].split("\\=") ;
		if(arr.length >= 2) {
			user.setToken(arr[1].split("\\&")[0]) ;
		}
		user.setVerifier(arr[arr.length - 1]) ; 
		Log.i(LOG_TAG, "User auth fetched " + user.getToken() + "  Verifier " + user.getVerifier() );
		return user ;
	}
}
