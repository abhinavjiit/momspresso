package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.facebook.UiLifecycleHelper;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.DateTimeUtils;
import com.kelltontech.utils.facebook.listener.FacebookLoginListener;
import com.kelltontech.utils.facebook.listener.FacebookPostListener;
import com.kelltontech.utils.facebook.model.FacebookUtils;
import com.kelltontech.utils.facebook.model.UserInfo;
import com.mycity4kids.R;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.loading.TaskImageLoader;
import com.mycity4kids.models.parentingstop.ParentingArticleListModel;
import com.mycity4kids.widget.BitmapLruCache;

/**
 * @author ArshVardhan
 * @email ArshVardhan.Atreya@kelltontech.com
 * @createdDate 27-03-2014
 * @modifiedDate 28-03-2014
 * @description The TopPicksDetailActivity screen displays the details of the
 *              Top Picks Item clicked by the user on the TopPicksActivity
 *              screen
 */

public class TopPicksDetailActivity extends BaseActivity implements
OnClickListener {

	private ParentingArticleListModel topPicksData;
	private TextView txvArticleTitle;
	private TextView txvAuthorName;
	private TextView txvPublishDate;
	private TextView txvDescription;
	private ImageView imvAuthorThumb;
	private NetworkImageView imvNetworkAuthorThumb;
	ImageLoader.ImageCache imageCache;
	ImageLoader imageLoader;
	TaskImageLoader loader;
	private UiLifecycleHelper mUiHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_top_picks_detail);
		txvArticleTitle = (TextView) findViewById(R.id.txvArticleTitle);
		txvAuthorName = (TextView) findViewById(R.id.txvAuthorName);
		txvPublishDate = (TextView) findViewById(R.id.txvPublishDate);
		txvDescription = (TextView) findViewById(R.id.txvDescription);
		imvAuthorThumb = (ImageView) findViewById(R.id.imvAuthorThumb);
		imvNetworkAuthorThumb = (NetworkImageView) findViewById(R.id.imvAuthorThumb1);
		((ImageView) findViewById(R.id.imgBack)).setOnClickListener(this);
		((ImageView) findViewById(R.id.img_twitter)).setOnClickListener(this);
		((ImageView) findViewById(R.id.img_fb)).setOnClickListener(this);

		imageCache = new BitmapLruCache();
		imageLoader = new ImageLoader(Volley.newRequestQueue(this), imageCache);
		mUiHelper = new UiLifecycleHelper(this, FacebookUtils.callback);
		mUiHelper.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			String topPicksId = bundle.getString(Constants.EXTRA_TOP_PICKS_ID);
			if (topPicksId != null) {
				topPicksData = (ParentingArticleListModel) bundle
						.get(Constants.EXTRA_TOP_PICKS_DATA_MODEL);

			}
		}

		txvArticleTitle.setText(topPicksData.getArticleTitle());
		txvAuthorName.setText(topPicksData.getAuthorFirstName() + " "
				+ topPicksData.getAuthorLastName());

		String PublishDateValues = DateTimeUtils.changeDate(topPicksData
				.getArticleCreatedDate());

		if (PublishDateValues != null) {
			txvPublishDate.setText(PublishDateValues);
		}
		txvDescription.setText(topPicksData.getBody());
		// txvDescription.setText(Html.fromHtml(topPicksData.getBody()));

		if (topPicksData.getAuthorProfileImg() != null) {
			imvAuthorThumb.setVisibility(View.GONE);
			imvNetworkAuthorThumb.setVisibility(View.VISIBLE);
			imvNetworkAuthorThumb.setDefaultImageResId(R.drawable.default_img);
			try {
				imvNetworkAuthorThumb.setImageUrl(
						topPicksData.getAuthorProfileImg(), imageLoader);

			} catch (Exception ex) {
				imvNetworkAuthorThumb
				.setErrorImageResId(R.drawable.default_img);

			}
		} else {
			imvAuthorThumb.setVisibility(View.VISIBLE);
			imvNetworkAuthorThumb.setVisibility(View.GONE);
		}

	}

	@Override
	protected void updateUi(Response response) {
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgBack: 
			finish();
			break;

		case R.id.img_font_size: 
			showToast("Under Construction.");
			break;

		case R.id.img_follow: 
			showToast("Under Construction.");
			break;

		case R.id.img_share: 
			Intent shareIntent =   new Intent(android.content.Intent.ACTION_SEND);  
			shareIntent.setType("text/plain");  
			shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "mycity4Kids android app");     
			String shareMessage = "I have just discovered an article on : " +topPicksData.getArticleTitle();
			/*+mBusinessInfoModel.getName()==null?"":mBusinessInfoModel.getName()+" in mycity4kids app. Check it out "+mBusinessInfoModel.getWeb_url()==null?"":mBusinessInfoModel.getWeb_url();*/  
			shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,shareMessage);    
			startActivity(Intent.createChooser(shareIntent,"MyCity4Kids"));  
			break;

		case R.id.img_fb: 
			showProgressDialog(getString(R.string.loading_));
			FacebookUtils.loginFacebook(TopPicksDetailActivity.this,new FacebookLoginListener() {
				@Override
				public void doAfterLogin(UserInfo userInfo) {
					System.out.println("Test");
					Log.d("dfacebook", "Login Comleted");
					FacebookUtils.postOnWall(TopPicksDetailActivity.this,new FacebookPostListener() {
						@Override
						public void doAfterPostOnWall(boolean status) {
							Log.d("dfacebook", "Post On Wall  Comleted");
							removeProgressDialog();
							System.out.println(status);
						}
					}, "","");
				}
			});
			break;

		case R.id.img_twitter: 
//			Twitter.getInstance(Constants.TWITTER_OAUTH_KEY,
//					Constants.TWITTER_OAUTH_SECRET, Constants.CALLBACK_URL,
//					TopPicksDetailActivity.this).doLogin(new ITLogin() {
//
//						@Override
//						public void success(TwitterUser user) {
//							showToast("Tweet has been successfully posted.");
//						}
//					}, "twitter post message");
			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int _requestCode, int _resultCode,
			Intent _data) {
		super.onActivityResult(_requestCode, _resultCode, _data);// 64206,0 -1
		if (_resultCode == 0) {
			// removeProgressDialog();
		}
		mUiHelper.onActivityResult(_requestCode, _resultCode, _data,
				FacebookUtils.dialogCallback);
	}
}
