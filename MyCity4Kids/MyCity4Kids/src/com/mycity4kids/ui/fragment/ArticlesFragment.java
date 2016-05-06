package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
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
import android.widget.ImageView;
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
import com.mycity4kids.editor.EditorPostActivity;
import com.mycity4kids.enums.ParentingFilterType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.parentingfilter.ParentingSearchRequest;
import com.mycity4kids.models.parentingstop.CommonParentingList;
import com.mycity4kids.models.parentingstop.CommonParentingResponse;
import com.mycity4kids.models.parentingstop.ParentingRequest;
import com.mycity4kids.models.parentingstop.ParentingSort;
import com.mycity4kids.newmodels.parentingmodel.ArticleModelNew;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.ArticlesAndBlogsDetailsActivity;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.adapter.ArticlesListingAdapter;
import com.mycity4kids.ui.adapter.TabsPagerAdapter;

import java.util.ArrayList;

/**
 * @author deepaneker.chaudhary
 */
public class ArticlesFragment extends BaseFragment {
    private ArrayList<CommonParentingList> mParentingLists;
    private ArticlesListingAdapter mAdapter;
    private int mArticleTotalListCount = 1;
    private int mTotalPagesCount = 0;
    private int mPageCount = 1;
    private int mFirstPageCountFromSearch = 0;
    private boolean mIsRequestRunning;
    private RelativeLayout mLodingView;
    private TabWidget mTabWidget;
    private HorizontalScrollView mHorizontalScrollView;
    CommonParentingResponse parentingResponse;
    private boolean isCommingFromSearching;
    private ListView mArticleList;
    private ParentingSearchRequest mParentingSearchRequestModel;
    private ParentingSort mCurrentSortByModel = null;
    private boolean isCommingFromSort;
    public boolean isFirstRun = true;

//    new changes @ manish

    private TabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    TabsPagerAdapter tabsPagerAdapter;
    ArticleModelNew.AllArticles NewdataList;
    ImageView addDraft;
    String searchName = "";
    int currentPagePosition = 0;
    ArticleModelNew.AllArticles initialList;

    Boolean ifReset = false;
    private int lastPosition;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Utils.pushOpenScreenEvent(getActivity(), "Blogs Dashboard", SharedPrefUtils.getUserDetailModel(getActivity()).getId() + "");

        View view = inflater.inflate(R.layout.aa_fragment_article_new, null);
        addDraft = (ImageView) view.findViewById(R.id.addDraft);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mSlidingTabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);

        addDraft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT > 15) {
                    Intent intent1 = new Intent(getActivity(), EditorPostActivity.class);
                    Bundle bundle5 = new Bundle();
                    bundle5.putString(EditorPostActivity.TITLE_PARAM, "");
                    bundle5.putString(EditorPostActivity.CONTENT_PARAM, "");
                    bundle5.putString(EditorPostActivity.TITLE_PLACEHOLDER_PARAM,
                            getString(R.string.example_post_title_placeholder));
                    bundle5.putString(EditorPostActivity.CONTENT_PLACEHOLDER_PARAM,
                            getString(R.string.example_post_content_placeholder));
                    bundle5.putInt(EditorPostActivity.EDITOR_PARAM, EditorPostActivity.USE_NEW_EDITOR);
                    bundle5.putString("from", "DraftListViewActivity");
                    intent1.putExtras(bundle5);
                    startActivity(intent1);
                } else {
                    Intent viewIntent =
                            new Intent("android.intent.action.VIEW",
                                    Uri.parse("http://www.mycity4kids.com/parenting/admin/setupablog"));
                    startActivity(viewIntent);
                }
            }
        });

        initialList = new ArticleModelNew().new AllArticles();

        try {
            setRetainInstance(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        tabsPagerAdapter = new TabsPagerAdapter(getFragmentManager(), getActivity(), null, getActivity(), searchName);
        mViewPager.setAdapter(tabsPagerAdapter);
        mSlidingTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setCurrentItem(1);

//        NewAllArticleListingApi(mPageCount);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                int pos = position;

                mSlidingTabLayout.setScrollPosition(position, positionOffset, true);

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
//        mViewPager.setCurrentItem(1);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * send a request for articles with search filter :
     */


    public void isCommingFromSearch(ParentingSearchRequest requestModel, int pageCount, boolean isBackPressResetList) {
        showProgressDialog("Please wait...");
        if (isBackPressResetList) {
            mPageCount = 1;
            mParentingLists = new ArrayList<CommonParentingList>();
            hitArticleListingApi(mPageCount);
            isCommingFromSearching = false;
            isCommingFromSort = true;
        } else {
            mParentingSearchRequestModel = requestModel;
            isCommingFromSearching = true;
            mIsRequestRunning = true;
            isCommingFromSort = false;
            mFirstPageCountFromSearch = pageCount;
            //	mCurrentSortByModel=new ParentingSort();
            if (mCurrentSortByModel != null) {
                requestModel.setSortBy(mCurrentSortByModel.getKey());
            }
            requestModel.setPage("" + pageCount);
            mParentingLists = new ArrayList<CommonParentingList>();
            ParentingStopSearchController _controller = new ParentingStopSearchController(getActivity(), this);
            _controller.getData(AppConstants.PARENTING_STOP_SEARCH_REQUEST, requestModel);
        }
    }

    private void managePaginationInListing() {

        mArticleList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos,
                                    long id) {
                Intent intent = new Intent(getActivity(), ArticlesAndBlogsDetailsActivity.class);
                if (parent.getAdapter() instanceof ArticlesListingAdapter) {
                    CommonParentingList parentingListData = (CommonParentingList) ((ArticlesListingAdapter) parent.getAdapter()).getItem(pos);
                    intent.putExtra(Constants.ARTICLE_ID, parentingListData.getId());
                    intent.putExtra(Constants.PARENTING_TYPE, ParentingFilterType.ARTICLES);
                    intent.putExtra(Constants.PARENTING_TYPE, parentingListData.getBookmarkStatus());
                    startActivity(intent);

                }

            }
        });

        mArticleList.setOnScrollListener(new OnScrollListener() {

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
                            if (isCommingFromSearching) {
                                int pageCount = Integer.parseInt(mParentingSearchRequestModel.getPage());
                                int currentPageCount = ++pageCount;
                                /**
                                 * this count  will handle that if we are searching
                                 * (filter) data & if searching api will not give
                                 * any data at first time then
                                 * with the help of this count i will manage
                                 * listing it will work as an normal listing
                                 * without search.
                                 *
                                 */
                                mFirstPageCountFromSearch = ++pageCount;
                                if (mTotalPagesCount >= currentPageCount) {
                                    hitArticleSearchListing(currentPageCount, mParentingSearchRequestModel);
                                } else {
                                    mLodingView.setVisibility(View.GONE);
                                }
                            } else {
                                /*
                                 * i tag only false in case of normal article pagination no need to check
								 */
                                int currentPageCount = ++mPageCount;
                                if (mTotalPagesCount >= currentPageCount) {
                                    hitArticleListingApi(currentPageCount);
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
//        removeProgressDialog();

        CommonParentingResponse responseData;

        if (response == null) {
            ((DashboardActivity) getActivity()).showToast("Something went wrong from server");
            removeProgressDialog();
            mLodingView.setVisibility(View.GONE);
            mIsRequestRunning = false;
            return;
        }

        switch (response.getDataType()) {
            case AppConstants.PARENTING_STOP_ARTICLES_REQUEST:
            case AppConstants.PARENTING_STOP_SEARCH_REQUEST:
                responseData = (CommonParentingResponse) response.getResponseObject();
                try {
                    if (responseData.getResponseCode() == 200) {
                        if (mLodingView.getVisibility() == View.VISIBLE) {
                            mLodingView.setVisibility(View.GONE);
                        }
//                    getArticleResponse(responseData);
                        removeProgressDialog();
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
                        removeProgressDialog();
                        String message = responseData.getResult().getMessage();
                        if (!StringUtils.isNullOrEmpty(message)) {
                            ((DashboardActivity) getActivity()).showToast(message);
                        } else {
                            ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
                        }
                    }
                } catch (Exception e) {

                    removeProgressDialog();
                    e.printStackTrace();
                    ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));

                }
                break;

            case AppConstants.NEW_ALL_ARTICLES_REQUEST:
                try {

                    ArticleModelNew newResponseData = (ArticleModelNew) response.getResponseObject();
                    if (newResponseData.getResponseCode() == 200) {
//                    if (mLodingView.getVisibility() == View.VISIBLE) {
//                        mLodingView.setVisibility(View.GONE);
//                    }
                        removeProgressDialog();

                        initialList = newResponseData.getResult().getData().getData();
                        setUpPager(getView(), newResponseData.getResult().getData().getData());
//                    setUpTabColor();
                    } else if (newResponseData.getResponseCode() == 400) {

//                    if (mLodingView.getVisibility() == View.VISIBLE) {
//                        mLodingView.setVisibility(View.GONE);
//                    }
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
                        removeProgressDialog();
                        String message = newResponseData.getResult().getMessage();
                        if (!StringUtils.isNullOrEmpty(message)) {
                            ((DashboardActivity) getActivity()).showToast(message);
                        } else {
                            ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
                        }
                    }
                    removeProgressDialog();
                } catch (Exception e) {

                    removeProgressDialog();
                    e.printStackTrace();
                    ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));

                }
                break;


            case AppConstants.TOP_PICKS_REQUEST:
                responseData = (CommonParentingResponse) response.getResponseObject();
                if (responseData.getResponseCode() == 200) {
                    removeProgressDialog();

//                    updateCurrentArticlePageBySearch(responseData.getResult().getData().getData());


                } else if (responseData.getResponseCode() == 400) {
                    removeProgressDialog();
                    String message = responseData.getResult().getMessage();
                    if (!StringUtils.isNullOrEmpty(message)) {
                        ((DashboardActivity) getActivity()).showToast(message);
                    } else {
                        ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
                    }
                }
                break;

            default:
                break;
        }


    }


    private void manageTabForSort(final ArrayList<ParentingSort> sortList) {
        try {
            if (isCommingFromSort) {
                return;
            }
            //View tabChild = mTabWidget.getChildAt(0);
            /**
             * CR
             */
            /*if(tabChild!=null){
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
                    ParentingSort sortBy = sortList.get(i);
                    LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.tab_design, null);
                    ((TextView) layout.findViewById(R.id.tab_title)).setText(sortBy.getValue().toUpperCase());
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
                    /**
                     * below api 11 i add seperator manually so tab widget contain 6 if we have three tab
                     * so for this handling i am using isEven boolean; deepanker.chaudhary
                     */
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
                            View view = (View) v;
                            int position = (Integer) view.getTag();
                            Log.i("position", "" + position);
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

                            /**
                             * hit in case of sorting :-on tab click
                             */
                            mPageCount = 1;
                            /*int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                        if(currentapiVersion<=11){
							if(position==1){
								--position;
							}else if(p)
						}*/
                            mCurrentSortByModel = sortList.get(position);
                            mArticleList.setSelection(0);
                            isCommingFromSort = true;
                            showProgressDialog("Please Wait...");
                            mParentingLists = new ArrayList<CommonParentingList>();

                            if (isCommingFromSearching) {
                                hitArticleSearchListing(mPageCount, mParentingSearchRequestModel);
                            } else {
                                hitArticleSortListingApi(mPageCount, mCurrentSortByModel);
                            }

                        }
                    });

                }

            }
        } catch (Exception e) {
            Log.i("problem in sort", e.getMessage());
        }
    }

    /**
     * get articles data:
     *
     * @param responseData
     */
//    private void getArticleResponse(CommonParentingResponse responseData) {
//        //	parentingResponse = responseData ;
//        ArrayList<CommonParentingList> dataList = responseData.getResult().getData().getData();
//        ArrayList<ParentingSort> sortList = responseData.getResult().getData().getSort();
//        mParentingLists.addAll(dataList);
//
//        manageTabForSort(sortList);
//
//        mArticleTotalListCount = responseData.getResult().getData().getTotal_articles();
//        mTotalPagesCount = responseData.getResult().getData().getPage_count();
//        mAdapter.setListData(mParentingLists);
//        mAdapter.notifyDataSetChanged();
//        mIsRequestRunning = false;
//    }

    /**
     * it will send a request for articles with pages;
     *
     * @param pPageCount
     */

    private void hitArticleListingApi(int pPageCount) {
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
        mIsRequestRunning = true;
        _controller.getData(AppConstants.PARENTING_STOP_ARTICLES_REQUEST, _parentingModel);
    }

    private void NewAllArticleListingApi(int pPageCount) {
        showProgressDialog(getString(R.string.please_wait));
        ParentingRequest _parentingModel = new ParentingRequest();

        if (mCurrentSortByModel != null) {
            _parentingModel.setSoty_by(mCurrentSortByModel.getKey());
        }

        _parentingModel.setCity_id(SharedPrefUtils.getCurrentCityModel(getActivity()).getId());
        _parentingModel.setPage("" + pPageCount);
        ParentingStopController _controller = new ParentingStopController(getActivity(), this);
        _controller.getData(AppConstants.NEW_ALL_ARTICLES_REQUEST, _parentingModel);
    }


    /**
     * this function will  call in case of sorting
     *
     * @param pPageCount
     * @param sortBy
     */
    private void hitArticleSortListingApi(int pPageCount, ParentingSort sortBy) {

        ParentingRequest _parentingModel = new ParentingRequest();
        _parentingModel.setCity_id(SharedPrefUtils.getCurrentCityModel(getActivity()).getId());
        _parentingModel.setPage("" + pPageCount);
        _parentingModel.setSoty_by(sortBy.getKey());
        ParentingStopController _controller = new ParentingStopController(getActivity(), this);
        mIsRequestRunning = true;
        _controller.getData(AppConstants.PARENTING_STOP_ARTICLES_REQUEST, _parentingModel);
    }

    /**
     * send a request for articles with search filter :
     *
     * @param pPageCount
     * @param pSearchRequestModle
     */
    private void hitArticleSearchListing(int pPageCount, ParentingSearchRequest pSearchRequestModle) {
        pSearchRequestModle.setPage("" + pPageCount);
        if (mCurrentSortByModel != null) {
            pSearchRequestModle.setSortBy(mCurrentSortByModel.getKey());
        }
        ParentingStopSearchController _controller = new ParentingStopSearchController(getActivity(), this);
        _controller.getData(AppConstants.PARENTING_STOP_SEARCH_REQUEST, pSearchRequestModle);
        mIsRequestRunning = true;
    }


    void setUpPager(View view, ArticleModelNew.AllArticles newdataList) {

//        lastPosition = mViewPager.getpo

        tabsPagerAdapter = new TabsPagerAdapter(getFragmentManager(), getActivity(), newdataList, getActivity(), searchName);
        mViewPager.setAdapter(tabsPagerAdapter);
        mSlidingTabLayout.setupWithViewPager(mViewPager);
//        mViewPager.setCurrentItem(1);
        mViewPager.setOffscreenPageLimit(4);

        if (isFirstRun) {
            mSlidingTabLayout.post(new Runnable() {
                @Override
                public void run() {
                    mViewPager.setCurrentItem(1);
                }
            });
        } else {
            mSlidingTabLayout.post(new Runnable() {
                @Override
                public void run() {
                    mViewPager.setCurrentItem(lastPosition);
                }
            });
        }
    }

    public void refreshBlogList() {
        tabsPagerAdapter.refreshBookmarkedBlogList();
    }
}
