package com.kellton.api.initials;

import java.util.EnumSet;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.LinkedInApi;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
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

import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.client.enumeration.ProfileField;
import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.schema.Person;
import com.kellton.api.interfaces.ILinkedinLogin;
import com.kellton.api.model.People;
import com.kellton.socialapis.BuildConfig;

public class Linkedin extends DefaultClass {

	private static Linkedin _up;
	private final static String LOG_TAG = "Linkedin" ; 
	private String key;
	private String secret;
	private String callbackURL ;
	private WebView web_view; 
	private ILinkedinLogin login ;
	
	private OAuthService service;
	private Token requestToken;
	private LinkedInApiClientFactory factory ; 
	private LinkedInApiClient client;  
	
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

	private Linkedin() {

	}

	private Linkedin(String consumer_key, String consumer_secret, String callback_url , Context context) {
		_up = new Linkedin();
		key = consumer_key;
		secret = consumer_secret;
		callbackURL = callback_url ; 
		this.context = context;
		factory = LinkedInApiClientFactory.newInstance(key, secret) ; 
	}

	public static Linkedin getInstance(String consumer_key, String consumer_secret , String callback_url , Context context) {
//		if (_up == null) {
//			_up = new Linkedin(consumer_key, consumer_secret, callback_url , context);
//		}
		_up = new Linkedin(consumer_key, consumer_secret, callback_url , context);
		return _up;
	}
	public void doLogin(final ILinkedinLogin ilogin) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					showProgress("Loading...") ; 
					login = ilogin ; 
					service = new ServiceBuilder()
					.provider(LinkedInApi.class)
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
							web_view.setWebViewClient(new LinkedinWebViewClient());
							
							web_view.setFocusable(true);
							web_view.setFocusableInTouchMode(true);
							web_view.requestFocus();
							ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT) ;
					        ((Activity)context).addContentView(web_view, params) ;
					        web_view.loadUrl(authURL) ;		
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
	private class LinkedinWebViewClient extends WebViewClient {

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
				fetchUser(url);
				return true ;
			}
			return false ;
		}

		private void fetchUser(final String url) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						final People people = getUser(url) ;
						Verifier v = new Verifier(people.getAccessSecret());
						Token accessToken = service.getAccessToken(requestToken, v);
						LinkedInAccessToken inAccessToken = new LinkedInAccessToken(accessToken.getToken(), accessToken.getSecret() ) ; 
						client = factory.createLinkedInApiClient(inAccessToken); 
					    Person p = client.getProfileForCurrentUser(EnumSet.of(
				                ProfileField.ID, ProfileField.FIRST_NAME,
				                ProfileField.LAST_NAME, ProfileField.HEADLINE,
				                ProfileField.INDUSTRY, ProfileField.PICTURE_URL,
				                ProfileField.POSITIONS_COMPANY_NAME ,   
				                ProfileField.POSITIONS, ProfileField.CURRENT_STATUS,
				                ProfileField.DATE_OF_BIRTH, ProfileField.LOCATION_NAME,
				                ProfileField.MAIN_ADDRESS, ProfileField.LOCATION_COUNTRY));
					    people.setPerson(p);
					    people.setAccessToken(inAccessToken.getToken()) ; 
					    people.setAccessSecret(inAccessToken.getTokenSecret()) ;
					    people.setExpiry(inAccessToken.getExpirationTime()) ;
					    ((Activity)context).runOnUiThread(new Runnable() {
							@Override
							public void run() {
							    login.success(people);
							    removeProgress();
							}
						}) ;
					} catch (Exception e) {
						e.printStackTrace();
						login.success(null);
					}
				}
			}).start() ; 
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
	public People getUser(String url) {
		People people = new People() ; 
		Uri uri = Uri.parse(url) ;
		people.setAccessToken(uri.getQueryParameter("oauth_token")); 
		people.setAccessSecret(uri.getQueryParameter("oauth_verifier")); 
		return people ;
	}
}
