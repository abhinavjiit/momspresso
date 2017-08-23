package com.mycity4kids.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabWidget;
import android.widget.TextView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.ParentingStopController;
import com.mycity4kids.models.parentingstop.CommonParentingList;
import com.mycity4kids.models.parentingstop.CommonParentingResponse;
import com.mycity4kids.models.parentingstop.ParentingRequest;
import com.mycity4kids.models.parentingstop.ParentingSort;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.adapter.TopPicksListingAdapter;

import java.util.ArrayList;

/**
 * @author ArshVardhan
 * @email ArshVardhan.Atreya@kelltontech.com
 * @createdDate 25-03-2014
 * @modifiedDate 28-03-2014
 * @description The TopPicksActivity screen lists all the reviews for the
 *              current/selected location/city 
 *              CR Done By- deepanker-chaudhary
 */

public class TopPicksActivity extends BaseActivity implements OnClickListener {

	private ArrayList<CommonParentingList> mTopPicksList;
	private ListView topPicksListView;
	private TopPicksListingAdapter topPicksAdapter;
	private int mTopPicksListCount = 1;
	private int mPageCount = 1;
	private boolean mIsRequestRunning;
	private View rltLoadingView;
	private int mTotalPagesCount=0;
	private TabWidget mTabWidget;
	private HorizontalScrollView mHorizontalScrollView;
	private ParentingSort mCurrentSortByModel=null;
	private boolean isCommingFromSort;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_top_picks);
		findViewById(R.id.imgBack).setOnClickListener(this);
		mTabWidget=(TabWidget)findViewById(android.R.id.tabs);
		mHorizontalScrollView=(HorizontalScrollView)findViewById(R.id.horizontalList);
		mTopPicksList = new ArrayList<CommonParentingList>();
		ParentingRequest _topPickModel = new ParentingRequest();
		_topPickModel.setCity_id(SharedPrefUtils.getCurrentCityModel(this).getId());
		_topPickModel.setPage("" + 1);
		ParentingStopController _controller = new ParentingStopController(this,this);
		showProgressDialog(getString(R.string.please_wait));
		_controller.getData(AppConstants.TOP_PICKS_REQUEST, _topPickModel);

		Constants.IS_PAGE_AVAILABLE = true;
		manageView();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void manageView() {
		rltLoadingView = findViewById(R.id.rltLoadingView);
		findViewById(R.id.imgLoader).startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_indefinitely));
		topPicksListView = (ListView) findViewById(R.id.topPicksListView);
		topPicksAdapter = new TopPicksListingAdapter(TopPicksActivity.this);
		topPicksListView.setVisibility(View.VISIBLE);
		topPicksListView.setAdapter(topPicksAdapter);
		topPicksListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (Constants.IS_PAGE_AVAILABLE) {
					if (view.getCount() < mTopPicksListCount) {

						boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
						if (visibleItemCount != 0 && loadMore && !mIsRequestRunning && firstVisibleItem != 0) {

							rltLoadingView.setVisibility(View.VISIBLE);
							int currentPageCount=++mPageCount;
							if(mTotalPagesCount>=currentPageCount)
							{
								hitBusinessListingApi(currentPageCount);
							}
						}
					}
				}
			}
		});
		topPicksListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
			}
		});
	}



	@Override
	protected void updateUi(Response response) {
		mIsRequestRunning = false;
		rltLoadingView.setVisibility(View.GONE);
		removeProgressDialog();
		if (response == null) {
			showToast("Something went wrong from server");
		}
		//ParentingResponse responseData = (ParentingResponse) response.getResponseObject();

		CommonParentingResponse responseData=(CommonParentingResponse)response.getResponseObject();
		switch (response.getDataType()) {
		case AppConstants.TOP_PICKS_REQUEST:

			if (responseData.getResponseCode() == Constants.HTTP_RESPONSE_SUCCESS) {

				updateResponseOnUI(responseData);
				/*mTopPicksListCount = responseData.getResult().getData().getTotal_articles();
				ArrayList<Data> dataList = responseData.getResult().getData().getData();

				for (Data dataModel : dataList) {
					ParentingArticleListModel _mainModel = new ParentingArticleListModel();
					_mainModel.setArticleId(dataModel.getArticle().getId());
					_mainModel.setArticleCreatedDate(dataModel.getArticle().getCreated());
					_mainModel.setArticleTitle(dataModel.getArticle().getTitle());
					_mainModel.setBody(dataModel.getArticle().getBody());
					_mainModel.setAboutAuthor(dataModel.getAuthor().getAbout_user());
					_mainModel.setAuthorFirstName(dataModel.getAuthor().getFirst_name());
					_mainModel.setAuthorLastName(dataModel.getAuthor().getLast_name());
					_mainModel.setAuthorId(dataModel.getAuthor().getId());
					_mainModel.setAuthorProfileImg(dataModel.getAuthor().getProfile_image());

					mTopPicksList.add(_mainModel);
				}

				topPicksAdapter.setListData(mTopPicksList);

				topPicksAdapter.notifyDataSetChanged();
				if (mTopPicksList.isEmpty()) {
					((TextView) findViewById(R.id.txvNoData)).setVisibility(View.VISIBLE);

					topPicksListView.setVisibility(View.GONE);
				} else {
					((TextView) findViewById(R.id.txvNoData)).setVisibility(View.GONE);

					topPicksListView.setVisibility(View.VISIBLE);
				}*/

			} else if (responseData.getResponseCode() == 400) {
				Constants.IS_PAGE_AVAILABLE = false;
				if (mTopPicksList.isEmpty()) {
					findViewById(R.id.txvNoData).setVisibility(View.VISIBLE);

					topPicksListView.setVisibility(View.GONE);
				} else {
					findViewById(R.id.txvNoData).setVisibility(View.GONE);

					topPicksListView.setVisibility(View.VISIBLE);
				}
				break;
			} else {
				ToastUtils.showToast(TopPicksActivity.this,getString(R.string.toast_response_error));

				finish();
			}
			break;
		}

	}

	/**
	 * get articles data:
	 * @param responseData
	 */
	private void updateResponseOnUI(CommonParentingResponse responseData){
		mTopPicksListCount = responseData.getResult().getData().getTotal_articles();
		mTotalPagesCount=responseData.getResult().getData().getPage_count();
		ArrayList<CommonParentingList> dataList = responseData.getResult().getData().getData();
		ArrayList<ParentingSort> sortList = responseData.getResult().getData().getSort();

		mTopPicksList.addAll(dataList);
		manageTabForSort(sortList);

		topPicksAdapter.setListData(mTopPicksList);
		topPicksAdapter.notifyDataSetChanged();
		mIsRequestRunning = false;
		if (mTopPicksList.isEmpty()) {
			findViewById(R.id.txvNoData).setVisibility(View.VISIBLE);
			topPicksListView.setVisibility(View.GONE);
		} else {
			findViewById(R.id.txvNoData).setVisibility(View.GONE);
			topPicksListView.setVisibility(View.VISIBLE);
		}

	}



	private void manageTabForSort(final ArrayList<ParentingSort> sortList){
		try {
			if(isCommingFromSort){
				return;
			}
		if(sortList!=null){
			if(mTabWidget!=null){
				mTabWidget.removeAllViews();
			}
			LayoutInflater inflater=LayoutInflater.from(this);
			for(int i = 0 ; i < sortList.size() ; i++ ) {
				ParentingSort sortBy = sortList.get(i);
				LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.tab_design, null) ;
				((TextView)layout.findViewById(R.id.tab_title)).setText(sortBy.getValue().toUpperCase());
				layout.setBackgroundResource(R.drawable.detail_tab_selector);
				int currentapiVersion = android.os.Build.VERSION.SDK_INT;
				if(currentapiVersion<=8){
					View divider = new View(this);
					divider.setBackgroundResource(R.drawable.separator);
					mTabWidget.setStripEnabled(false);
					mTabWidget.setDividerDrawable(null);
					mTabWidget.addView(divider,1,57);
					mTabWidget.addView(layout);
				}else{
					mTabWidget.addView(layout);
					mTabWidget.setDividerDrawable(R.drawable.fragment_tab_border);

				}

			}


			for(int i=0;i<mTabWidget.getTabCount();i++){
				final int j=i;
				mTabWidget.getChildAt(j).setTag(j);
				mTabWidget.getChildAt(j).setOnClickListener(new OnClickListener() {

					

					@SuppressWarnings("unused")
					@Override
					public void onClick(View v) {
						View view= v;
						int position=(Integer) view.getTag();
						mTabWidget.getChildAt(j).setSelected(true);

						for(int i=0;i<mTabWidget.getTabCount();i++){
							if(position!=i){
								mTabWidget.getChildAt(i).setSelected(false);
							}
						}


						if(view!=null){
							final int width = mHorizontalScrollView.getWidth();
							final int scrollPos = view.getLeft() - (width - view.getWidth()) / 2;
							mHorizontalScrollView.scrollTo(scrollPos, 0);
						} else {
							mHorizontalScrollView.scrollBy(position, 0);
						}

						/**
						 * hit in case of sorting :-on tab click
						 */
						mPageCount=1;

						mCurrentSortByModel=sortList.get(position);
						showProgressDialog("Please Wait...");
						mTopPicksList=new ArrayList<CommonParentingList>();
						topPicksListView.setSelection(0);
						isCommingFromSort=true;
						hitTopPicsSortListingApi(mPageCount,mCurrentSortByModel);


					}
				});


			}


		}
		} catch (Exception e) {
			Log.i("Problem in Sort", e.getMessage());
		}
	}

	public void hitBusinessListingApi(int page) {
		if (!ConnectivityUtils.isNetworkEnabled(TopPicksActivity.this)) {
			ToastUtils.showToast(TopPicksActivity.this,getString(R.string.toast_network_error));
			return;
		}
		ParentingRequest _topPickModel = new ParentingRequest();
		/**
		 * this case will case in pagination case: for sorting
		 */
		if(mCurrentSortByModel!=null){
			_topPickModel.setSoty_by(mCurrentSortByModel.getKey());
		}
		_topPickModel.setCity_id(SharedPrefUtils.getCurrentCityModel(this).getId());

		_topPickModel.setPage(page + "");
		ParentingStopController _controller = new ParentingStopController(this,this);

		_controller.getData(AppConstants.TOP_PICKS_REQUEST, _topPickModel);
		mIsRequestRunning = true;
	}

	/**
	 * this function will  call in case of sorting
	 * @param pPageCount
	 * @param sortBy
	 */
	private void hitTopPicsSortListingApi(int pPageCount,ParentingSort sortBy){
		if (!ConnectivityUtils.isNetworkEnabled(TopPicksActivity.this)) {
			ToastUtils.showToast(TopPicksActivity.this,getString(R.string.toast_network_error));
			return;
		}
		ParentingRequest _parentingModel=new ParentingRequest();


		_parentingModel.setCity_id(SharedPrefUtils.getCurrentCityModel(this).getId());
		_parentingModel.setPage(""+pPageCount);
		_parentingModel.setSoty_by(sortBy.getKey());
		ParentingStopController _controller=new ParentingStopController(this, this);
		mIsRequestRunning = true;
		_controller.getData(AppConstants.TOP_PICKS_REQUEST, _parentingModel);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgBack:
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case Constants.SHOW_TOP_PICKS_DETAIL:
			if (resultCode == Activity.RESULT_OK) {


			}
			break;
		}
	}

}