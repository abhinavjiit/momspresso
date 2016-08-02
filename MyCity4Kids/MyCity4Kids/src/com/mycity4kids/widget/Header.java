package com.mycity4kids.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.ui.IScreen;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.controller.LogoutController;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.drawer.SlidingDrawer;
import com.mycity4kids.models.user.UserModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.AddBusinesEventActivity;
import com.mycity4kids.ui.activity.HomeCategoryActivity;
import com.mycity4kids.ui.activity.ProfileActivity;
import com.mycity4kids.ui.activity.RecentlyViewedActivity;
import com.mycity4kids.ui.activity.SelectLocationActivity;
import com.mycity4kids.ui.activity.WriteReviewActivity;

public class Header extends RelativeLayout implements OnClickListener {
	public static final String TAG = Header.class.getSimpleName();
	private Spinner mCitySpinner;
	// private ImageView mPerformSpinner;
	private TextView txvHeaderText;
	private TextView txvSelectedCity;
	SlidingDrawer _slidingDrawer;
	private TextView loginTxt;
	private TextView logout;
	private TextView userName;
	private FrameLayout userImgFrmLayout;
	private NetworkImageView userImage;

	public Header(Context context) {
		super(context);
	}

	public Header(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
	}

	public Header(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void inflateHeader() {
		//	mParentLayout=mParentLout;
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.header_bar, this);
		view.findViewById(R.id.imgNavigationBar).setOnClickListener(this);
		txvHeaderText = (TextView) view.findViewById(R.id.txvHeaderText);
		txvSelectedCity = (TextView) view.findViewById(R.id.txvSelectedCity);
		txvSelectedCity.setOnClickListener(this);
		/*if(_slidingDrawer.isDrawerOpened()){
        	 mParentLout.setBackgroundColor(Color.WHITE);
         }*/
		// mPerformSpinner=(ImageView)view.findViewById(R.id.perform_click);
		// mCitySpinner=(Spinner)view.findViewById(R.id.city_spinner);
		// cityDropDownList();

	}

	public void updateHeaderText(String headerText, boolean visibility) {
		if (visibility) {
			txvHeaderText.setVisibility(View.VISIBLE);
			txvHeaderText.setText(headerText);

		} else {
			txvHeaderText.setVisibility(View.GONE);
		}
	}

	public void updateSelectedCity(String headerText, boolean visibility) {
		if (visibility) {
			if(headerText.contains("Delhi") && headerText.contains("-")){

				String[] headerCityName=headerText.split("-");
				txvSelectedCity.setText(headerCityName[0]+" "+headerCityName[1].toUpperCase());
			}else{
				txvSelectedCity.setText(headerText);
			}
			txvSelectedCity.setVisibility(View.VISIBLE);

		} else {
			txvSelectedCity.setVisibility(View.GONE);
		}
	}

	public void openSlidingDrawer() {
		LayoutInflater layoutInflater = LayoutInflater.from(getContext());
		View view = layoutInflater.inflate(R.layout.sliding_drawer, null);
		_slidingDrawer = new SlidingDrawer((Activity) getContext(), view);
		_slidingDrawer.setSlideTarget(SlidingDrawer.SLIDE_TARGET_CONTENT);
		/*if(pParentLayout!=null){
			_slidingDrawer.setHomeLinearLayout(pParentLayout);
		}*/
		((TextView) view.findViewById(R.id.home_txt)).setOnClickListener(this);
		((TextView) view.findViewById(R.id.review_txt)).setOnClickListener(this);
		((TextView) view.findViewById(R.id.logout_txt)).setOnClickListener(this);
		((TextView) view.findViewById(R.id.facebook_txt)).setOnClickListener(this);
		((TextView) view.findViewById(R.id.rate_txt)).setOnClickListener(this);
		logout=(TextView)view.findViewById(R.id.logout_txt);
		logout.setOnClickListener(this);
		((TextView) view.findViewById(R.id.twitter_txt)).setOnClickListener(this);
		userImgFrmLayout=(FrameLayout)view.findViewById(R.id.profileFrmLout);
		userImgFrmLayout.setOnClickListener(this);
		userImage=(NetworkImageView)view.findViewById(R.id.profile_img);
		//userImage.setDefaultImageResId(R.drawable.default_img);
		userImage.setDefaultImageResId(R.drawable.default_icon);
		userName=(TextView)view.findViewById(R.id.username_txt);
		loginTxt=(TextView)view.findViewById(R.id.login_txt);
		loginTxt.setOnClickListener(this);
		userName.setOnClickListener(this);
		((TextView)view.findViewById(R.id.add_business_txt)).setOnClickListener(this);
		((TextView)view.findViewById(R.id.recently_txt)).setOnClickListener(this);
		((TextView)view.findViewById(R.id.feedback_txt)).setOnClickListener(this);
		((TextView)view.findViewById(R.id.invite_txt)).setOnClickListener(this);

		UserTable _table = new UserTable((BaseApplication) getContext().getApplicationContext());
		UserModel userData = _table.getAllUserData();
		if(_table.getCount()>0 && userData != null)
		{   
			logout.setVisibility(View.VISIBLE);
			loginTxt.setVisibility(View.GONE);
			userName.setVisibility(View.VISIBLE);
			userImgFrmLayout.setVisibility(View.VISIBLE);
			String firstName=userData.getUser().getFirst_name();
			String lastName=userData.getUser().getLast_name();

			if (!StringUtils.isNullOrEmpty(userData.getProfile().getProfile_image())) {
			//	userImage.setDefaultImageResId(R.drawable.default_img);
				userImage.setDefaultImageResId(R.drawable.default_icon);
				try {
					userImage.setImageUrl(userData.getProfile().getProfile_image(), new ImageLoader(Volley.newRequestQueue(getContext()),new BitmapLruCache()));
					Log.i("Login Image URL", userData.getProfile().getProfile_image());

				} catch (Exception ex) {
					userImage.setErrorImageResId(R.drawable.default_icon);
				//	userImage.setDefaultImageResId(R.drawable.default_img);
				}
			} else {
			//	userImage.setDefaultImageResId(R.drawable.default_img);
				userImage.setErrorImageResId(R.drawable.default_icon);
			}

			if(!StringUtils.isNullOrEmpty(firstName) && !StringUtils.isNullOrEmpty(lastName)){
				userName.setText("Hi! "+firstName+" "+lastName);
			//	userName.setText("Hi! "+firstName);
			}else if(!StringUtils.isNullOrEmpty(firstName) && StringUtils.isNullOrEmpty(lastName)){
				userName.setText("Hi! "+firstName);
			}else{
				userName.setText("Hi! User");
			}
		}else{
			logout.setVisibility(View.GONE);
			loginTxt.setVisibility(View.VISIBLE);
			userName.setVisibility(View.GONE);
			userImgFrmLayout.setVisibility(View.GONE);
			userName.setText("Hi!");
		}

	}

	public void loginLogout(){
		UserTable _table = new UserTable((BaseApplication) getContext().getApplicationContext());
		UserModel userData = _table.getAllUserData();
		if(_table.getCount()>0 && userData != null)
		{   
			if(!StringUtils.isNullOrEmpty(SharedPrefUtils.getProfileImgUrl(getContext())) && 
					!(SharedPrefUtils.getProfileImgUrl(getContext()).equals(userData.getProfile().getProfile_image()))) {
				try {
					userImage.setImageUrl(SharedPrefUtils.getProfileImgUrl(getContext()), new ImageLoader(Volley.newRequestQueue(getContext()),new BitmapLruCache()));
				} catch (Exception ex) {
					userImage.setErrorImageResId(R.drawable.default_img);
				}
			}

			logout.setVisibility(View.VISIBLE);
			loginTxt.setVisibility(View.GONE);
			userName.setVisibility(View.VISIBLE);
			userImgFrmLayout.setVisibility(View.VISIBLE);
			String firstName=userData.getUser().getFirst_name();
			String lastName=userData.getUser().getLast_name();
			if(!StringUtils.isNullOrEmpty(firstName) && !StringUtils.isNullOrEmpty(lastName)){
				userName.setText("Hi! "+firstName+" "+lastName);
			}else if(!StringUtils.isNullOrEmpty(firstName) && StringUtils.isNullOrEmpty(lastName)){
				userName.setText("Hi! "+firstName);
			}else{
				userName.setText("Hi! User");
			}
		}else{
			logout.setVisibility(View.GONE);
			loginTxt.setVisibility(View.VISIBLE);
			userName.setVisibility(View.GONE);
			userImgFrmLayout.setVisibility(View.GONE);
			userName.setText("Hi!");
		}
	}


	public void closeDrawer() {
		_slidingDrawer.closeDrawer();
	}

	public SlidingDrawer openDrawer(){
		if(_slidingDrawer!=null){
			return _slidingDrawer;
		}
		return null;
	}


	@Override
	public void onClick(View v) {
		try {
			
		
		switch (v.getId()) {
		case R.id.imgNavigationBar:

			loginLogout();
			//	mParentLayout.setBackgroundColor(getContext().getResources().getColor(R.color.transparent_color));
			_slidingDrawer.openDrawer();
			// Toast.makeText(getContext(), "Under Implementation",
			// Toast.LENGTH_SHORT).show();
			break;
		case R.id.txvSelectedCity:
			getContext().startActivity(new Intent(getContext(), SelectLocationActivity.class));

			break;

		case R.id.home_txt:
			if(getContext() instanceof HomeCategoryActivity){
				_slidingDrawer.closeDrawer();
			}else{
				getContext().startActivity(new Intent(getContext(), HomeCategoryActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			}



			//	getContext().startActivity(new Intent(getContext(), HomeCategoryActivity.class));

			break;
		case R.id.review_txt:
			
			//	Toast.makeText((Activity) getContext(), "Under Implementation. ",Toast.LENGTH_SHORT).show();
			getContext().startActivity(new Intent(getContext(), WriteReviewActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

			break;

		case R.id.add_business_txt:

			getContext().startActivity(new Intent(getContext(), AddBusinesEventActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

			break;
		case R.id.recently_txt:

			getContext().startActivity(new Intent(getContext(), RecentlyViewedActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

			break;
		case R.id.login_txt:

//			getContext().startActivity(new Intent(getContext(), LandingLoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			// ((HomeCategoryActivity)getContext()).finish();
			break;
		case R.id.facebook_txt:
			String fbUrl = "https://www.facebook.com/mycity4kids";
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(fbUrl));
			getContext().startActivity(new Intent(i).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

			break;
		case R.id.twitter_txt:

			String twitterUrl = "https://twitter.com/mycity4kids";
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(twitterUrl));
			getContext().startActivity(new Intent(intent).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

			break;
		case R.id.feedback_txt:
			//	Toast.makeText((Activity) getContext(), "Under Implementation. ",Toast.LENGTH_SHORT).show();

			Intent intentEmail=new Intent(Intent.ACTION_SEND);
			String[] recipients={"feedback@mycity4kids.com"};
			intentEmail.putExtra(Intent.EXTRA_EMAIL, recipients);
			intentEmail.putExtra(Intent.EXTRA_SUBJECT,"mycity4kids mobile app");
			//intentEmail.putExtra(Intent.EXTRA_TEXT,"I just downloaded the amazing mycity4kids mobile app. Check it out @: http://www.mycity4kids.com/mobile ");
			//intentEmail.putExtra(Intent.EXTRA_CC,"ghi");
			intentEmail.setType("text/html");
			getContext().startActivity(Intent.createChooser(intentEmail, "Send mail").setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

			break;
		case R.id.invite_txt:
			Intent shareIntent =   new Intent(android.content.Intent.ACTION_SEND);  
			shareIntent.setType("text/plain");  
			shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "mycity4kids android app");     
			String shareMessage = "I just downloaded the amazing mycity4kids mobile app. Check it out http://www.mycity4kids.com/mobile";  
			shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,shareMessage);    
			getContext().startActivity(Intent.createChooser(shareIntent,"mycity4kids").setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));     

			break;
		case R.id.rate_txt:
			/**
			 * a try/catch block here because an Exception will be thrown if the Play Store is not installed on the target device.
			 */
			String appPackage = getContext().getPackageName();
			try {
				Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackage));
				getContext().startActivity(rateIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			} catch (Exception e) {
				Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackage));
				getContext().startActivity(rateIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			}
			
			  

			break;
		case R.id.logout_txt:
			LogoutController _controller=new LogoutController((Activity)getContext(), (IScreen)getContext());
			UserTable _tables = new UserTable((BaseApplication) getContext().getApplicationContext());
			if(_tables.getCount()>0)
			{
				((BaseActivity)getContext()).showProgressDialog("Please Wait...");
				String sessionId=_tables.getAllUserData().getUser().getSessionId();
				_controller.getData(AppConstants.LOGOUT_REQUEST, sessionId);
			//	_tables.deleteAll();
				//Toast.makeText((Activity) getContext(), "Logout successful. ",Toast.LENGTH_SHORT).show();
				/*Intent intent1 = new Intent(getContext(), HomeCategoryActivity.class);
				intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				getContext().startActivity(intent1);*/
			}else{
				Toast.makeText((Activity) getContext(), "User not logged in ",Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.profileFrmLout:
		case R.id.username_txt:
			getContext().startActivity(new Intent(getContext(), ProfileActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			break;
		}
		
		
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
		

}
