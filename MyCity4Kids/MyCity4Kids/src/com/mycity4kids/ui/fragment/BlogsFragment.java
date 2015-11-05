package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabWidget;
import android.widget.TextView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.ParentingStopController;
import com.mycity4kids.controller.ParentingStopSearchController;
import com.mycity4kids.enums.ParentingFilterType;
import com.mycity4kids.models.parentingfilter.ParentingSearchRequest;
import com.mycity4kids.models.parentingstop.CommonParentingList;
import com.mycity4kids.models.parentingstop.CommonParentingResponse;
import com.mycity4kids.models.parentingstop.ParentingArticleListModel;
import com.mycity4kids.models.parentingstop.ParentingRequest;
import com.mycity4kids.models.parentingstop.ParentingSort;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.ArticlesAndBlogsDetailsActivity;
import com.mycity4kids.ui.activity.ParentingArticlesActivity;
import com.mycity4kids.ui.adapter.ArticlesListingAdapter;

import java.util.ArrayList;

/**
 * @author deepanker.chaudhary
 */
public class BlogsFragment extends BaseFragment {
    private ArrayList<CommonParentingList> mParentingLists;
    private ArticlesListingAdapter mAdapter;
    private int mArticleTotalListCount = 1;
    private int mPageCount = 1;
    private boolean mIsRequestRunning;
    private View mLodingView;
    private TabWidget mTabWidget;
    private HorizontalScrollView mHorizontalScrollView;
    private ParentingSearchRequest mParentingSearchRequestModel;
    private boolean isCommingFromSearching;
    private int mFirstPageCountFromSearch = 0;
    private int mTotalPagesCount = 0;
    private ListView mBlogListview;
    private ParentingSort mCurrentSortByModel = null;
    private boolean isCommingFromSort;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_blogs, null);

        try {


            mBlogListview = (ListView) view.findViewById(R.id.blogs_list);
            mLodingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);
            mTabWidget = (TabWidget) view.findViewById(android.R.id.tabs);
            mHorizontalScrollView = (HorizontalScrollView) view.findViewById(R.id.horizontalList);
            new ArrayList<ParentingArticleListModel>();
            mParentingLists = new ArrayList<CommonParentingList>();
            mAdapter = new ArticlesListingAdapter(getActivity(), false);
            mBlogListview.setAdapter(mAdapter);
            Constants.IS_PAGE_AVAILABLE = true;
            showProgressDialog(getString(R.string.please_wait));
            /*Bundle bundle=getArguments();

		if(bundle!=null ){
			 *//**
             * hit for search Parenting blogs listing
             *//*
			boolean isCommingFromPareningSearch=bundle.getBoolean(Constants.IS_PARENTING_COMMING_FROM_SEARCH,false);
			SearchListType searchListType=(SearchListType) bundle.getSerializable(Constants.PARENTING_SEARCH_LIST_TYPE);
			ParentingFilterType parentingType=(ParentingFilterType)bundle.getSerializable(Constants.PARENTING_TYPE);
			String queryForSearch=bundle.getString(Constants.PARENTING_SEARCH_QUERY);

			ParentingSearchRequest _searchRequest=new ParentingSearchRequest();
			_searchRequest.setCityId(SharedPrefUtils.getCurrentCityModel(getActivity()).getId());
			_searchRequest.setParentingType(parentingType.getParentingType());
			_searchRequest.setFilerType(searchListType.getSearchListType());
			_searchRequest.setQuery(queryForSearch);
			_searchRequest.setCommingFromSearch(isCommingFromPareningSearch);
			hitBlogsSearchListing(mPageCount, _searchRequest);
			mBlogListview.setTag(_searchRequest);

		}else{*/
            hitBlogsListingApi(mPageCount);
            //	}

            managePaginationInListing();
			/*
		if(getArguments()!=null){
			ParentingResponse data=(ParentingResponse) getArguments().getSerializable("key");
			System.out.println(data);
		}*/


        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    private void managePaginationInListing() {


        mBlogListview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos,
                                    long id) {
                Intent intent = new Intent(getActivity(), ArticlesAndBlogsDetailsActivity.class);
                if (parent.getAdapter() instanceof ArticlesListingAdapter) {
                    CommonParentingList parentingListData = (CommonParentingList) ((ArticlesListingAdapter) parent.getAdapter()).getItem(pos);
                    intent.putExtra(Constants.ARTICLE_ID, parentingListData.getId());
                    intent.putExtra(Constants.PARENTING_TYPE, ParentingFilterType.BLOGS);
                    startActivity(intent);

                }

            }
        });


        mBlogListview.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (Constants.IS_PAGE_AVAILABLE) {
                    if (view.getCount() <= mArticleTotalListCount) {

                        boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
                        if (visibleItemCount != 0 && loadMore && !mIsRequestRunning && firstVisibleItem != 0) {
                            mLodingView.setVisibility(View.VISIBLE);
                            /**'
                             *this is related to search listing pagination because
                             *i add request modle in it.
                             *
                             */

                            /**'
                             *this is related to search listing pagination because
                             *i add request modle in it.
                             *
                             */
                            if (isCommingFromSearching) {
                                int pageCount = Integer.parseInt(mParentingSearchRequestModel.getPage());
                                int currentPageCount = ++pageCount;
                                mFirstPageCountFromSearch = ++pageCount;
                                if (mTotalPagesCount >= currentPageCount) {
                                    hitBlogsSearchListing(currentPageCount, mParentingSearchRequestModel);
                                } else {
                                    mLodingView.setVisibility(View.GONE);
                                }
                            } else {
								/*
								 * i tag only false in case of normal article pagination no need to check
								 */
                                int currentPageCount = ++mPageCount;
                                if (mTotalPagesCount >= currentPageCount) {
                                    hitBlogsListingApi(currentPageCount);
                                } else {
                                    mLodingView.setVisibility(View.GONE);
                                }


                            }

                        }
                    }
                }
            }
        });
    }

    @Override
    protected void updateUi(Response response) {
        removeProgressDialog();
        removeProgressDialog();
        if (response == null) {
            ((ParentingArticlesActivity) getActivity()).showToast("Something went wrong from server");
            return;
        }


        switch (response.getDataType()) {
            case AppConstants.PARENTING_STOP_BLOGS_REQUEST:
            case AppConstants.PARENTING_STOP_SEARCH_REQUEST:
                CommonParentingResponse responseData = (CommonParentingResponse) response.getResponseObject();
                if (responseData.getResponseCode() == 200) {
                    if (mLodingView.getVisibility() == View.VISIBLE) {
                        mLodingView.setVisibility(View.GONE);
                    }
                    getBlogResponse(responseData);


                } else if (responseData.getResponseCode() == 400) {
                    if (mLodingView.getVisibility() == View.VISIBLE) {
                        mLodingView.setVisibility(View.GONE);
                    }
                    /**
                     * if search result will fail for first time then we
                     * are showing as it is list which comes at first time
                     * so for this handling ;this function relate.
                     */
                    Constants.IS_PAGE_AVAILABLE = false;
                    if (mFirstPageCountFromSearch <= 1 && isCommingFromSearching) {
                        isCommingFromSearching = false;
                        Constants.IS_PAGE_AVAILABLE = true;
                        mIsRequestRunning = false;
                    }

                    String message = responseData.getResult().getMessage();
                    if (!StringUtils.isNullOrEmpty(message)) {
                        ((ParentingArticlesActivity) getActivity()).showToast(message);
                    } else {
                        ((ParentingArticlesActivity) getActivity()).showToast(getString(R.string.went_wrong));
                    }

                }
                break;

            default:
                break;
        }


    }

    /**
     * @param requestModel-this        comes from parentingArticlesActicity
     * @param pPageCount-              this is count for pagination -
     * @param isBackPressResetListing- this is boolean value which i use for backpress. on back press i reset first list.
     *                                 deepanker.chaudhary
     */

    public void isCommingFromSearch(ParentingSearchRequest requestModel, int pPageCount, boolean isBackPressResetListing) {
        showProgressDialog("Please wait...");
        if (isBackPressResetListing) {
            mPageCount = 1;
            mParentingLists = new ArrayList<CommonParentingList>();
            hitBlogsListingApi(mPageCount);
            isCommingFromSearching = false;
            isCommingFromSort = true;
        } else {
            mParentingSearchRequestModel = requestModel;
            isCommingFromSearching = true;
            mIsRequestRunning = true;
            isCommingFromSort = false;
            //	mCurrentSortByModel=new ParentingSort();
            if (mCurrentSortByModel != null) {
                requestModel.setSortBy(mCurrentSortByModel.getKey());
            }
            mFirstPageCountFromSearch = pPageCount;
            requestModel.setPage("" + pPageCount);
            mParentingLists = new ArrayList<CommonParentingList>();
            ParentingStopSearchController _controller = new ParentingStopSearchController(getActivity(), this);
            _controller.getData(AppConstants.PARENTING_STOP_SEARCH_REQUEST, requestModel);
        }
    }

    /**
     * get articles data:
     *
     * @param responseData
     */
    private void getBlogResponse(CommonParentingResponse responseData) {
        ArrayList<CommonParentingList> dataList = responseData.getResult().getData().getData();
        ArrayList<ParentingSort> sortList = responseData.getResult().getData().getSort();
        mParentingLists.addAll(dataList);
        manageTabForSort(sortList);
        mArticleTotalListCount = responseData.getResult().getData().getTotal_articles();
        mTotalPagesCount = responseData.getResult().getData().getPage_count();
        mIsRequestRunning = false;
        mAdapter.setListData(mParentingLists);
        mAdapter.notifyDataSetChanged();
    }

    private void manageTabForSort(final ArrayList<ParentingSort> sortList) {
        try {

            if (isCommingFromSort) {
                return;
            }
            /**
             * this will unselected tab when we come from search:
             */
			/*View tabChild = mTabWidget.getChildAt(0);
			if(tabChild!=null){
				if(!isCommingFromSearching){

					mTabWidget.getChildAt(0).setSelected(true);
				}else{
					mTabWidget.getChildAt(0).setSelected(false);
				}
			}*/
            if (sortList != null) {
                if (mTabWidget != null) {
                    mTabWidget.removeAllViews();
                }
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                for (int i = 0; i < sortList.size(); i++) {
                    ParentingSort by = sortList.get(i);
                    LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.tab_design, null);
                    ((TextView) layout.findViewById(R.id.tab_title)).setText(by.getValue().toUpperCase());
                    layout.setBackgroundResource(R.drawable.detail_tab_selector);
                    int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                    if (currentapiVersion < 11) {
                        View divider = new View(getActivity());
                        divider.setBackgroundResource(R.drawable.separator);
                        mTabWidget.setStripEnabled(false);
                        mTabWidget.setDividerDrawable(null);
                        mTabWidget.addView(divider, 1, 57);
                        mTabWidget.addView(layout);
                    } else {
                        mTabWidget.addView(layout);
                        mTabWidget.setDividerDrawable(R.drawable.fragment_tab_border);

                    }


                }


                boolean isEven = true;
                int k = -1;
                int l = -1;
                for (int i = 0; i < mTabWidget.getTabCount(); i++) {
                    if (i == 0) {
                        mTabWidget.getChildAt(0).setSelected(true);
                    }
                    final int j = i;
                    int currentapiVersion = android.os.Build.VERSION.SDK_INT;

                    if (currentapiVersion < 11) {
                        if (isEven) {
                            mTabWidget.getChildAt(j).setTag(l);
                            isEven = false;
                        } else {
                            mTabWidget.getChildAt(j).setTag(++k);
                            isEven = true;
                        }

                    } else {
                        mTabWidget.getChildAt(j).setTag(j);
                    }
                    mTabWidget.getChildAt(j).setOnClickListener(new OnClickListener() {

                        @SuppressWarnings("unused")
                        @Override
                        public void onClick(View v) {
                            //	isCommingFromSearching=false;
                            final View view = (View) v;
                            final int position = (Integer) view.getTag();
                            if (position == -1) {
                                return;
                            }
                            mTabWidget.getChildAt(j).setSelected(true);
                            for (int i = 0; i < mTabWidget.getTabCount(); i++) {
                                if (position != i) {
                                    mTabWidget.getChildAt(i).setSelected(false);
                                }
                            }


                            if (view != null) {
                                final int width = mHorizontalScrollView.getWidth();
                                final int scrollPos = view.getLeft() - (width - view.getWidth()) / 2;
                                mHorizontalScrollView.scrollTo(scrollPos, 0);
                            } else {
                                mHorizontalScrollView.scrollBy(position, 0);
                            }


                            mPageCount = 1;

                            mCurrentSortByModel = sortList.get(position);
                            mBlogListview.setSelection(0);
                            isCommingFromSort = true;
                            showProgressDialog("Please Wait...");
                            mParentingLists = new ArrayList<CommonParentingList>();
                            if (isCommingFromSearching) {
                                hitBlogsSearchListing(mPageCount, mParentingSearchRequestModel);
                            } else {
                                hitBlogsListingApi(mPageCount);
                            }


                        }
                    });


                }


            }
        } catch (Exception e) {
            Log.i("problem in blog sort", e.getMessage());
        }
    }

    /**
     * it will send a request for articles with pages;
     *
     * @param pPageCount
     */

    private void hitBlogsListingApi(int pPageCount) {
        mIsRequestRunning = true;
        ParentingRequest _parentingModel = new ParentingRequest();
        /**
         * this case will case in pagination case: for sorting
         */
        if (mCurrentSortByModel != null) {
            _parentingModel.setSoty_by(mCurrentSortByModel.getKey());
        }
        _parentingModel.setCity_id(SharedPrefUtils.getCurrentCityModel(getActivity()).getId());
        _parentingModel.setPage("" + pPageCount);
        ParentingStopController _controller = new ParentingStopController(getActivity(), this);

        _controller.getData(AppConstants.PARENTING_STOP_BLOGS_REQUEST, _parentingModel);

    }


    /**
     * send a request for blogs with search filter :
     *
     * @param pPageCount
     * @param pSearchRequestModle
     */
    private void hitBlogsSearchListing(int pPageCount, ParentingSearchRequest pSearchRequestModle) {
        pSearchRequestModle.setPage("" + pPageCount);
        mIsRequestRunning = true;
        if (mCurrentSortByModel != null) {
            pSearchRequestModle.setSortBy(mCurrentSortByModel.getKey());
        }
        ParentingStopSearchController _controller = new ParentingStopSearchController(getActivity(), this);
        _controller.getData(AppConstants.PARENTING_STOP_SEARCH_REQUEST, pSearchRequestModle);

    }

}
