package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.AutoSuggestController;
import com.mycity4kids.dbtable.CategoryListTable;
import com.mycity4kids.dbtable.LocalityTable;
import com.mycity4kids.models.autosuggest.AutoSuggestResponse;
import com.mycity4kids.models.category.CategoryModel;
import com.mycity4kids.models.category.GroupCategoryModel;
import com.mycity4kids.models.category.SubCategory;
import com.mycity4kids.ui.adapter.CategoryListAdapter;
import com.mycity4kids.ui.adapter.SubLocalityAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeCategoryTransparentDialogActivity extends BaseActivity implements OnFocusChangeListener,OnClickListener{
	
	private ExpandableListView mCategoryExpandList;
	private CategoryListTable mCategoryListTable;
	private ListView mSearchList;
	private EditText mQuerySearchEtxt;
	private EditText mLocalitySearchEtxt;
	private TextView mNoResultTxt;
	private AutoSuggestController mAutoSuggestController;
	private ArrayList<GroupCategoryModel> groupCategoryList;
	private HashMap<GroupCategoryModel,  ArrayList<CategoryModel>> categoryData;
	private ImageView mBusinessSearch;
	private ProgressBar mProgressBar;

	private LinearLayout mLocalitySearchLout;
	FrameLayout alphaLout;
	private View alphaView;
	
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			try {
				setContentView(R.layout.category_transparent_activity);
				mSearchList=(ListView)findViewById(R.id.searchList);
				mQuerySearchEtxt=(EditText)findViewById(R.id.query_search);
				mQuerySearchEtxt.addTextChangedListener(textWatcher);
				mQuerySearchEtxt.setOnFocusChangeListener(this);
				mLocalitySearchEtxt=(EditText)findViewById(R.id.locality_search);
				mLocalitySearchEtxt.addTextChangedListener(textWatcher);
				mLocalitySearchEtxt.setOnFocusChangeListener(this);
				mNoResultTxt=(TextView)findViewById(R.id.no_result);
				mBusinessSearch=(ImageView)findViewById(R.id.business_search_img);
				mProgressBar=(ProgressBar)findViewById(R.id.progress_bar);
				mLocalitySearchLout=(LinearLayout)findViewById(R.id.localityLayout);
				alphaLout = (FrameLayout) findViewById(R.id.alphaLout);
				alphaView = findViewById(R.id.alphaView);
				alphaView.setOnClickListener(this);
				//((Button)findViewById(R.id.btnCancel)).setOnClickListener(this);
				//((Button)findViewById(R.id.btnSubmit)).setOnClickListener(this);
				((ImageView)findViewById(R.id.write_a_review)).setOnClickListener(this);
				((ImageView)findViewById(R.id.parentingStop)).setOnClickListener(this);
				//setHeader();
				
				mCategoryExpandList=(ExpandableListView)findViewById(R.id.expandable_list);
				new HashMap<CategoryModel, ArrayList<SubCategory>>();
				categoryData=new HashMap<GroupCategoryModel, ArrayList<CategoryModel>>();
				mAutoSuggestController=new AutoSuggestController(this, this);
				mCategoryListTable=new CategoryListTable((BaseApplication)getApplicationContext());
				
				mSearchList.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int pos, long id) {

						if(parent.getAdapter() instanceof ArrayAdapter<?>){

							/**
							 * its related to search query adapter:
							 */
							String whichAdapterDataIsLoaded=(String)parent.getTag();

							if(whichAdapterDataIsLoaded.equals(Constants.SEARCH_LIST_TAG)){
								mQuerySearchEtxt.removeTextChangedListener(textWatcher);
								String listItem = (String)parent.getAdapter().getItem(pos).toString();
								mQuerySearchEtxt.setText(listItem);
								mQuerySearchEtxt.addTextChangedListener(textWatcher);

							}/*else if(whichAdapterDataIsLoaded.equals(Constants.LOCALITY_LIST_TAG)){
							 *//**
							 * related to loacality Adapter;
							 *//*
								mLocalitySearchEtxt.removeTextChangedListener(textWatcher);
								String listItem = (String)parent.getAdapter().getItem(pos).toString();
								mLocalitySearchEtxt.setText(listItem);
								mLocalitySearchEtxt.addTextChangedListener(textWatcher);


							}*/
							//mCategoryExpandList.setVisibility(View.VISIBLE);
							alphaLout.setVisibility(View.VISIBLE);
							mSearchList.setVisibility(View.GONE);

						} else if(parent.getAdapter() instanceof SubLocalityAdapter){
							if(pos == 0 && ((SubLocalityAdapter)parent.getAdapter()).getSublocalities().get(0).equals("Current Location")) {
								String queryData=mQuerySearchEtxt.getText().toString();
								if(!queryData.equals(""))
								{
									Intent intent =new Intent(HomeCategoryTransparentDialogActivity.this,BusinessListActivity.class);
									intent.putExtra("query", queryData);
									intent.putExtra("locality", "");
									intent.putExtra("isSearchListing", true);
									startActivity(intent);
								}else{
									showToast(getString(R.string.no_query));
								}
							}else{
								mLocalitySearchEtxt.removeTextChangedListener(textWatcher);
								String listItem = ((SubLocalityAdapter)parent.getAdapter()).getSublocalities().get(pos);
								mLocalitySearchEtxt.setText(listItem);
								mLocalitySearchEtxt.addTextChangedListener(textWatcher);
							}
						}
					}
				});
				
				mBusinessSearch.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						String queryData=mQuerySearchEtxt.getText().toString();
						String localityData=mLocalitySearchEtxt.getText().toString();
						if(StringUtils.isNullOrEmpty(queryData)){
							showToast(getString(R.string.no_query));
							return;
						}
						if(!StringUtils.isNullOrEmpty(queryData)||!StringUtils.isNullOrEmpty(localityData))
						{
							Intent intent =new Intent(HomeCategoryTransparentDialogActivity.this,BusinessListActivity.class);
							intent.putExtra("query", queryData);
							intent.putExtra("locality", localityData);
							intent.putExtra("isSearchListing", true);
							startActivity(intent);
						}
					}
				});

			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		/*private void setHeader() {
			header=(Header)findViewById(R.id.header);
			header.inflateHeader();
			header.openSlidingDrawer();
			header.updateHeaderText("", false);
			header.updateSelectedCity(SharedPrefUtils.getCurrentCityModel(HomeCategoryTransparentDialogActivity.this).getName(), true);
		}*/

		@Override
		protected void onResume() {
			// TODO Auto-generated method stub
			super.onResume();
			developHomeList();
		}
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void updateUi(Response response) {
			// TODO Auto-generated method stub
			if( response==null){

				showToast(getString(R.string.went_wrong));
			//	mCategoryExpandList.setVisibility(View.VISIBLE);
				alphaLout.setVisibility(View.VISIBLE);
				mSearchList.setVisibility(View.GONE);
				mProgressBar.setVisibility(View.GONE);
			}
			AutoSuggestResponse responseData=(AutoSuggestResponse)response.getResponseObject();
			if(responseData.getResponseCode()==200){
				ArrayList<String> queryList=responseData.getResult().getData().getSuggest();
				if(!queryList.isEmpty()){
					alphaLout.setVisibility(View.GONE);
					//mCategoryExpandList.setVisibility(View.GONE);
					mSearchList.setVisibility(View.VISIBLE);
					mProgressBar.setVisibility(View.GONE);
					ArrayAdapter<String> mQueryAdapter=new ArrayAdapter<String>(this, R.layout.text_for_locality,queryList);
					mSearchList.setAdapter(mQueryAdapter);
					mSearchList.setTag(Constants.SEARCH_LIST_TAG);
					mQueryAdapter.notifyDataSetChanged();
				}else{
					//	mNoResultTxt.setVisibility(View.VISIBLE);
					mProgressBar.setVisibility(View.GONE);
				//	mCategoryExpandList.setVisibility(View.VISIBLE);
					alphaLout.setVisibility(View.VISIBLE);
					mSearchList.setVisibility(View.GONE);
				}

			}else if(responseData.getResponseCode()==400){
			//	mCategoryExpandList.setVisibility(View.VISIBLE);
				alphaLout.setVisibility(View.VISIBLE);
				mSearchList.setVisibility(View.GONE);
				mProgressBar.setVisibility(View.GONE);
				showToast(getString(R.string.no_data));
			}
		}
		
		TextWatcher textWatcher=new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				/**
				 * this will call a query listing from api.
				 */
				if(mQuerySearchEtxt.getText().hashCode()==s.hashCode()){
					if(ConnectivityUtils.isNetworkEnabled(HomeCategoryTransparentDialogActivity.this)){
						if(!StringUtils.isNullOrEmpty(s.toString())){
							mProgressBar.setVisibility(View.VISIBLE);
						//	mCategoryExpandList.setVisibility(View.GONE);
							alphaLout.setVisibility(View.GONE);
							if(mAutoSuggestController != null)
								mAutoSuggestController.setCanceled(true);
							mAutoSuggestController.getData(AppConstants.AUTO_SUGGEST_REQUEST,s.toString());
						}else{
							//mCategoryExpandList.setVisibility(View.VISIBLE);
							/*alphaLout.setVisibility(View.VISIBLE);
							mSearchList.setVisibility(View.GONE);*/
							
							startActivity(new Intent(HomeCategoryTransparentDialogActivity.this, HomeCategoryTransparentDialogActivity.class));
						}
					}else{
						showToast(getString(R.string.error_network));
					}

					/**
					 * it will call locality list from local db.
					 */
				}else if(mLocalitySearchEtxt.getText().hashCode()==s.hashCode()){
					SubLocalityAdapter adapter=null;
					LocalityTable _localitiesTable=new LocalityTable((BaseApplication)getApplicationContext());
					ArrayList<String> localitiesName=new ArrayList<String>();
					localitiesName.add("Current Location");
					ArrayList<String> localitiesNameDb=_localitiesTable.getLocalitiesName(s.toString().trim());
					if(localitiesNameDb != null && localitiesName.size() != 0 ) {
						localitiesName.addAll(localitiesNameDb) ; 
					}
					if(localitiesName.size() ==1 ) {
						adapter=new SubLocalityAdapter(HomeCategoryTransparentDialogActivity.this, localitiesName);
						mSearchList.setAdapter(adapter);
					}

					if(s!=null && !(s.toString().equals(""))) {
						if(!localitiesName.isEmpty()){
							if(adapter==null) {
								adapter=new SubLocalityAdapter(HomeCategoryTransparentDialogActivity.this, localitiesName);
							}
							adapter.notifyDataSetChanged();
						//	mCategoryExpandList.setVisibility(View.GONE);
							alphaLout.setVisibility(View.GONE);
							mSearchList.setVisibility(View.VISIBLE);
							mSearchList.setAdapter(adapter);
							adapter.notifyDataSetChanged();
							mSearchList.setTag(Constants.LOCALITY_LIST_TAG);
							mNoResultTxt.setVisibility(View.GONE);

						}else{
							mSearchList.setVisibility(View.GONE);
							mNoResultTxt.setVisibility(View.VISIBLE);
							mProgressBar.setVisibility(View.GONE);

						}
					}else{
						//mCategoryExpandList.setVisibility(View.VISIBLE);
						alphaLout.setVisibility(View.VISIBLE);
						mSearchList.setVisibility(View.GONE);
						mNoResultTxt.setVisibility(View.GONE);
					}

				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		};
		
		private void developHomeList(){
			categoryData=	new HashMap<GroupCategoryModel, ArrayList<CategoryModel>>();
			groupCategoryList = mCategoryListTable.getGroupData();

			for(GroupCategoryModel _groupModel:groupCategoryList){
				ArrayList<CategoryModel> childCategoryList=mCategoryListTable.getCategoryData(_groupModel.getCategoryGroup());
				if(childCategoryList.size()==1){
					childCategoryList=new ArrayList<CategoryModel>();
				}

				categoryData.put(_groupModel, childCategoryList);
			}
			if(groupCategoryList.isEmpty()){
				mNoResultTxt.setVisibility(View.VISIBLE);
				//mCategoryExpandList.setVisibility(View.GONE);
				alphaLout.setVisibility(View.GONE);
			}else{
				mNoResultTxt.setVisibility(View.GONE);
				alphaLout.setVisibility(View.VISIBLE);
				//mCategoryExpandList.setVisibility(View.VISIBLE);

				CategoryListAdapter _categoryAdapter=new CategoryListAdapter(this, groupCategoryList, categoryData);
				mCategoryExpandList.setAdapter(_categoryAdapter);
				_categoryAdapter.notifyDataSetChanged();
			}
		}
}
