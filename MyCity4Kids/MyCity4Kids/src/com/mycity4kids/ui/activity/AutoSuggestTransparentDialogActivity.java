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
import com.mycity4kids.dbtable.LocalityTable;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.autosuggest.AutoSuggestResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.adapter.SubLocalityAdapter;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class AutoSuggestTransparentDialogActivity extends BaseActivity implements OnFocusChangeListener,OnClickListener{

	private ListView mSearchList;
	private EditText mQuerySearchEtxt;
	private EditText mLocalitySearchEtxt;
	private AutoSuggestController mAutoSuggestController;
	private ImageView mBusinessSearch;
	private ProgressBar mProgressBar;
	private LinearLayout mLoutProgress;
	private LinearLayout mLocalitySearchLout;
	boolean isContainCommaQuery=false;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Utils.pushOpenScreenEvent(AutoSuggestTransparentDialogActivity.this, "Resource Search", SharedPrefUtils.getUserDetailModel(this).getId() + "");

		try {
			setContentView(R.layout.transparent_auto_search_activity);
			mSearchList=(ListView)findViewById(R.id.searchList);
			mQuerySearchEtxt=(EditText)findViewById(R.id.query_search);
			mQuerySearchEtxt.addTextChangedListener(textWatcher);
			mQuerySearchEtxt.setOnFocusChangeListener(this);
			mLocalitySearchEtxt=(EditText)findViewById(R.id.locality_search);
			mLocalitySearchEtxt.setOnFocusChangeListener(this);
			mLocalitySearchEtxt.addTextChangedListener(textWatcher);
			mLocalitySearchEtxt.setOnFocusChangeListener(this);
			mBusinessSearch=(ImageView)findViewById(R.id.business_search_img);
			mProgressBar=(ProgressBar)findViewById(R.id.progress_bar);
			mLoutProgress=(LinearLayout)findViewById(R.id.lout_f_Progress);
			mLocalitySearchLout=(LinearLayout)findViewById(R.id.localityLayout);
			((TextView)findViewById(R.id.btnCancel)).setOnClickListener(this);
			((TextView)findViewById(R.id.btnSubmit)).setOnClickListener(this);

			mAutoSuggestController=new AutoSuggestController(this, this);

			mSearchList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int pos, long id) {



					if(parent.getAdapter() instanceof ArrayAdapter<?>){

						/**
						 * its related to search query adapter:
						 */
						String whichAdapterDataIsLoaded=(String)parent.getTag();
						String listItem = (String)parent.getAdapter().getItem(pos).toString();
						mQuerySearchEtxt.removeTextChangedListener(textWatcher);
						if(!listItem.contains(","))
						{
							isContainCommaQuery=false;
							mQuerySearchEtxt.setText(listItem);

							mSearchList.setVisibility(View.GONE);
						}else{
							isContainCommaQuery=true;
							StringTokenizer tokens = new StringTokenizer(listItem, ",");
							String first = tokens.nextToken();
							String second = tokens.nextToken();
							if(!StringUtils.isNullOrEmpty(first) && !StringUtils.isNullOrEmpty(second)){
								mQuerySearchEtxt.setText(first.trim());
								mLocalitySearchEtxt.setText(second.trim());
							}

						}
						mQuerySearchEtxt.addTextChangedListener(textWatcher);


					} else if(parent.getAdapter() instanceof SubLocalityAdapter){
						///if(pos == 0 && ((SubLocalityAdapter)parent.getAdapter()).getSublocalities().get(0).equals("Current Location")) {
						String queryData=mQuerySearchEtxt.getText().toString();
						String locality=mLocalitySearchEtxt.getText().toString();
						if(!queryData.equals(""))
						{
							Intent intent =new Intent(AutoSuggestTransparentDialogActivity.this,BusinessListActivityKidsResources.class);
							intent.putExtra("query", queryData);
							if(locality.equals("") || !isContainCommaQuery){
								String localityData=(String)parent.getAdapter().getItem(pos);
								if(!StringUtils.isNullOrEmpty(localityData))
								{ 
									
									if(pos!=0){
										intent.putExtra("locality",localityData);
									}
								
								}
							}else{
								intent.putExtra("locality",locality);
							}

							intent.putExtra("isSearchListing", true);
							startActivity(intent);
						}else{
							showToast(getString(R.string.no_query));
						}
						///	}else{
						/*	mLocalitySearchEtxt.removeTextChangedListener(textWatcher);
							String listItem = ((SubLocalityAdapter)parent.getAdapter()).getSublocalities().get(pos);
							mLocalitySearchEtxt.setText(listItem);
							mLocalitySearchEtxt.addTextChangedListener(textWatcher);
						}*/
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


						//if(isContainCommaQuery || localityData.equals(""))
					//	{
							Intent intent =new Intent(AutoSuggestTransparentDialogActivity.this,BusinessListActivityKidsResources.class);
							intent.putExtra("query", queryData);
							intent.putExtra("locality", localityData);
							intent.putExtra("isSearchListing", true);
							startActivity(intent);
							//	}else{
							//		showToast("Please give correct data!");
							//		return;
							//	}
					//	}
					}
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}







	@Override
	protected void updateUi(Response response) {
		// TODO Auto-generated method stub
		if( response==null){

			showToast(getString(R.string.went_wrong));
			return;


		}
		AutoSuggestResponse responseData=(AutoSuggestResponse)response.getResponseObject();
		String message = responseData.getResult().getMessage();
		if(responseData.getResponseCode()==200){
			ArrayList<String> queryList=responseData.getResult().getData().getSuggest();
			if(!queryList.isEmpty()){
				mSearchList.setVisibility(View.VISIBLE);
				mLoutProgress.setVisibility(View.GONE);
				ArrayAdapter<String> mQueryAdapter=new ArrayAdapter<String>(this, R.layout.text_for_locality,queryList);
				mSearchList.setAdapter(mQueryAdapter);
				mQueryAdapter.notifyDataSetChanged();
			}else{
				mLoutProgress.setVisibility(View.GONE);
				mSearchList.setVisibility(View.GONE);
			}

		}else if(responseData.getResponseCode()==400){
			mSearchList.setVisibility(View.GONE);
			mLoutProgress.setVisibility(View.GONE);
		/*	if(StringUtils.isNullOrEmpty(message)){
			//	Toast.makeText(AutoSuggestTransparentDialogActivity.this, R.string.event_list, Toast.LENGTH_SHORT).show();
			}else{
		//		Toast.makeText(AutoSuggestTransparentDialogActivity.this, message, Toast.LENGTH_SHORT).show();
			}*/
			
		}
	}



	TextWatcher textWatcher=new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {


			/**
			 * this will call a query listing from api.
			 */
			if(mQuerySearchEtxt.getText().hashCode()==s.hashCode()){
				if(ConnectivityUtils.isNetworkEnabled(AutoSuggestTransparentDialogActivity.this)){
					if(!StringUtils.isNullOrEmpty(s.toString())){
						//	String queryData=s.toString();
						/*if(queryData.contains(",")){
							StringTokenizer tokens = new StringTokenizer(queryData, ",");
							String first = tokens.nextToken();
							String second = tokens.nextToken();
							if(!StringUtils.isNullOrEmpty(first) && !StringUtils.isNullOrEmpty(second)){
								mQuerySearchEtxt.setText(first);
								mLocalitySearchEtxt.setText(second);
							}

						}else{*/
						mLoutProgress.setVisibility(View.VISIBLE);
						if(mAutoSuggestController != null)
							mAutoSuggestController.setCanceled(true);
						mAutoSuggestController.getData(AppConstants.AUTO_SUGGEST_REQUEST,s.toString());
						//	}

					}else{
						mSearchList.setVisibility(View.GONE);
					}
				}else{
					showToast(getString(R.string.error_network));
					return;
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
					adapter=new SubLocalityAdapter(AutoSuggestTransparentDialogActivity.this, localitiesName);
					mSearchList.setAdapter(adapter);
				}

				if(s!=null && !(s.toString().equals(""))) {
					if(!localitiesName.isEmpty()){
						if(adapter==null) {
							adapter=new SubLocalityAdapter(AutoSuggestTransparentDialogActivity.this, localitiesName);
						}
						adapter.notifyDataSetChanged();
						mSearchList.setVisibility(View.VISIBLE);
						mSearchList.setAdapter(adapter);
						adapter.notifyDataSetChanged();
						mSearchList.setTag(Constants.LOCALITY_LIST_TAG);

					}else{
						mSearchList.setVisibility(View.GONE);
						mLoutProgress.setVisibility(View.GONE);

					}


				}else{
					mSearchList.setVisibility(View.GONE);
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

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if(v==mQuerySearchEtxt){
			mSearchList.setVisibility(View.GONE);
			mLocalitySearchEtxt.setHint(getResources().getString(R.string.locality_txt));
			mQuerySearchEtxt.setHint("");
			mLocalitySearchEtxt.setHint("In which location?");
			mLocalitySearchLout.setVisibility(View.VISIBLE);

		}else if(v==mLocalitySearchEtxt){

			ArrayList<String> localitiesName=new ArrayList<String>();
			localitiesName.add("Current Location");
			if(localitiesName.size() ==1 ) {
				SubLocalityAdapter	adapter=new SubLocalityAdapter(AutoSuggestTransparentDialogActivity.this, localitiesName);
				mSearchList.setAdapter(adapter);
			}
			mSearchList.setVisibility(View.VISIBLE);
			mLocalitySearchEtxt.setHint("");
			mQuerySearchEtxt.setHint("What are you looking for ?");

		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnCancel:
			Constants.IS_SEARCH_LISTING=false;
			finish();
			break;
		case R.id.btnSubmit:
			String queryData=mQuerySearchEtxt.getText().toString();
			String localityData=mLocalitySearchEtxt.getText().toString();
			if(StringUtils.isNullOrEmpty(queryData)){
				showToast(getString(R.string.no_query));
				return;
			}
			if(!StringUtils.isNullOrEmpty(queryData)||!StringUtils.isNullOrEmpty(localityData))
			{
				/**
				 * CR done
				 */
				
			//	if(isContainCommaQuery||localityData.equals(""))	{
					Intent intent =new Intent(AutoSuggestTransparentDialogActivity.this,BusinessListActivityKidsResources.class);
					intent.putExtra("query", queryData);
					intent.putExtra("locality", localityData);
					intent.putExtra("isSearchListing", true);
					startActivity(intent);
			//	}else{
			//		showToast("Please give correct data!");
			//		return;
			//	}
			}


			break;

		default:
			break;
		}

	}





}
