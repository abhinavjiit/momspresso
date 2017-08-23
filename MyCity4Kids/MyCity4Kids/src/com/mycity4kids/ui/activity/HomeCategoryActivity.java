package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.dbtable.CategoryListTable;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.fragmentdialog.LoginFragmentDialog;
import com.mycity4kids.models.category.CategoryModel;
import com.mycity4kids.models.category.GroupCategoryModel;
import com.mycity4kids.models.category.SubCategory;
import com.mycity4kids.models.logout.LogoutResponse;
import com.mycity4kids.ui.adapter.CategoryListAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeCategoryActivity extends BaseActivity implements OnClickListener{
	private ExpandableListView mCategoryExpandList;
	private CategoryListTable mCategoryListTable;
	private TextView mNoResultTxt;
	private ArrayList<GroupCategoryModel> groupCategoryList;
	private HashMap<GroupCategoryModel,  ArrayList<CategoryModel>> categoryData;
	private ImageView mBusinessSearch;
	public LinearLayout mParentLout;


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		try {

			setContentView(R.layout.category_activity);


			/*if(dialogUpgrade.isHidden())
			{
				*//**
				 * this app rate dialog will user come 3.
				 *//*
				rateAppHandling();
			}*/



			//	Log.i("isHiddern", String.valueOf(dialog.isHidden()));
			mNoResultTxt=(TextView)findViewById(R.id.no_result);
			mBusinessSearch=(ImageView)findViewById(R.id.business_search_img);
			mParentLout=(LinearLayout)findViewById(R.id.parent_laout);
			findViewById(R.id.searchLout).setOnClickListener(this);


			findViewById(R.id.write_a_review).setOnClickListener(this);
			findViewById(R.id.parentingStop).setOnClickListener(this);

			setHeader();
			mCategoryExpandList=(ExpandableListView)findViewById(R.id.expandable_list);
			new HashMap<CategoryModel, ArrayList<SubCategory>>();
			categoryData=new HashMap<GroupCategoryModel, ArrayList<CategoryModel>>();
			mCategoryListTable=new CategoryListTable((BaseApplication)getApplicationContext());

			mBusinessSearch.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {


					Intent intent=new Intent(HomeCategoryActivity.this,AutoSuggestTransparentDialogActivity.class);
					startActivity(intent);

				}
			});



		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setHeader() {

	}

	@Override
	protected void onResume() {
		super.onResume();
		developHomeList();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void updateUi(Response response) {
		removeProgressDialog();
		if( response==null){

			showToast(getString(R.string.went_wrong));
			return;
		}
		switch (response.getDataType()) {
		case AppConstants.LOGOUT_REQUEST:
			LogoutResponse responseData = (LogoutResponse) response.getResponseObject();
			String message=responseData.getResult().getMessage();
			if (responseData.getResponseCode() == 200) {
				/**
				 * delete table from local also;
				 */
				UserTable _tables = new UserTable((BaseApplication)getApplicationContext());
				_tables.deleteAll();
				if(StringUtils.isNullOrEmpty(message)){
					Toast.makeText(this,getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();

				}else{
					Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
				}

			} else if (responseData.getResponseCode() == 400) {
				if(StringUtils.isNullOrEmpty(message)){
					Toast.makeText(this,getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
				}
			}
			break;

		default:
			break;
		}
	}

	private void developHomeList(){
		categoryData=	new HashMap<GroupCategoryModel, ArrayList<CategoryModel>>();
		groupCategoryList = mCategoryListTable.getGroupData();

		for(GroupCategoryModel _groupModel:groupCategoryList){
			ArrayList<CategoryModel> childCategoryList=mCategoryListTable.getCategoryData(_groupModel.getCategoryGroup());
			if(childCategoryList.size()<=1){
				childCategoryList=new ArrayList<CategoryModel>();
			}

			categoryData.put(_groupModel, childCategoryList);
		}
		if(groupCategoryList.isEmpty()){
			mNoResultTxt.setVisibility(View.VISIBLE);
			//mCategoryExpandList.setVisibility(View.GONE);
		}else{
			mNoResultTxt.setVisibility(View.GONE);
			//mCategoryExpandList.setVisibility(View.VISIBLE);

			CategoryListAdapter _categoryAdapter=new CategoryListAdapter(this, groupCategoryList, categoryData);
			mCategoryExpandList.setAdapter(_categoryAdapter);
			_categoryAdapter.notifyDataSetChanged();
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.parentingStop:
			startActivity(new Intent(HomeCategoryActivity.this, TopPicksActivity.class));//ParentingArticlesActivity
			break;
		case R.id.write_a_review:
			UserTable _table=new UserTable((BaseApplication)getApplicationContext());
			int count=_table.getCount();
			if(count<=0){
				/*Bundle args=new Bundle();
				args.putInt(Constants.CATEGORY_ID, mCategoryId);
				args.putInt(Constants.PAGE_TYPE, mEventOrBusiness);
				args.putString(Constants.BUSINESS_OR_EVENT_ID, detailsResponse.getId());
				args.putString(Constants.DISTANCE, mDistance);*/
				LoginFragmentDialog fragmentDialog = new LoginFragmentDialog();
				//	fragmentDialog.setArguments(args);
				fragmentDialog.show(this.getSupportFragmentManager(), "");
				return;
			}

			startActivity(new Intent(HomeCategoryActivity.this, WriteReviewActivity.class));
			break;
//		case R.id.alphaView:
//			mCategoryExpandList.bringToFront();
//			break;
		case R.id.searchLout:
			Intent intent=new Intent(HomeCategoryActivity.this,AutoSuggestTransparentDialogActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
}