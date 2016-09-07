package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.preference.SharedPrefUtils;

/**
 * 
 * @author deepanker.chaudhary
 *
 */
public class AddCommentReplyActivity extends BaseActivity implements OnClickListener{
	private boolean isComment;
	private String parentId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utils.pushOpenScreenEvent(AddCommentReplyActivity.this, "Comment Reply", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");

		try {
			setContentView(R.layout.activity_comment_reply);
			((TextView)findViewById(R.id.add_comment)).setOnClickListener(this);
			((LinearLayout)findViewById(R.id.touchLout)).setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					setResult(RESULT_CANCELED);
					finish();

					return false;
				}
			});

			Bundle bundle = getIntent().getExtras();
			if(bundle!=null){
				isComment= bundle.getBoolean(Constants.IS_COMMENT, false);
				parentId=bundle.getString(Constants.PARENT_ID);
				
				if(isComment){
					((EditText)findViewById(R.id.editCommentTxt)).setHint(getString(R.string.write_comment));
				}else{
					((EditText)findViewById(R.id.editCommentTxt)).setHint(getString(R.string.add_reply));
				}

			}



		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	@Override
	protected void updateUi(Response response) {
		/*removeProgressDialog();
		if( response==null){
			showToast("Something went wrong from server");
			return;
		}


		switch (response.getDataType()) {
		case AppConstants.COMMENT_REPLY_REQUEST:
			CommonResponse responseData=(CommonResponse)response.getResponseObject();
			if(responseData.getResponseCode()==200){


			}else if(responseData.getResponseCode()==400){
				String message=	responseData.getResult().getMessage();
				if(!StringUtils.isNullOrEmpty(message)){
					showToast(message);
				}else{
					showToast(getString(R.string.went_wrong));
				}

			}
			break;


		default:
			break;
		}*/

	}
	@Override
	public void onBackPressed() {

		setResult(RESULT_CANCELED);
		finish();
		super.onBackPressed();
	}

	@Override
	public void onClick(View v) {
		try {


			switch (v.getId()) {
			case R.id.add_comment:
				if(isValid()){
					String contentData=	((EditText)findViewById(R.id.editCommentTxt)).getText().toString();
					Intent intent=new Intent();
					intent.putExtra(Constants.ARTICLE_BLOG_CONTENT, contentData);
					intent.putExtra(Constants.PARENT_ID, parentId);
					setResult(RESULT_OK,intent);
					finish();
				}
				break;

			default:
				break;
			}
		} catch (Exception e) {
			Log.i("onClick", e.getMessage());
		}

	}

	private boolean isValid(){

		if(((EditText)findViewById(R.id.editCommentTxt)).getText().toString().length()==0){
			if(isComment){
				showToast("Please add a comment");
				return false;
			}else{
				showToast("Please add a reply");
				return false;
			}

		}
		return true;
	}

}
