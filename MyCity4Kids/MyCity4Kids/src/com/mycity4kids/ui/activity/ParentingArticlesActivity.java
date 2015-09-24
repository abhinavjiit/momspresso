package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
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
import com.mycity4kids.constants.Constants;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.enums.ParentingFilterType;
import com.mycity4kids.enums.SearchListType;
import com.mycity4kids.models.logout.LogoutResponse;
import com.mycity4kids.models.parentingfilter.FilterAuthors;
import com.mycity4kids.models.parentingfilter.FilterBlogs;
import com.mycity4kids.models.parentingfilter.FilterTags;
import com.mycity4kids.models.parentingfilter.FilterTopics;
import com.mycity4kids.models.parentingfilter.ParentingSearchRequest;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.fragment.ArticlesFragment;
import com.mycity4kids.ui.fragment.BlogsFragment;
import com.mycity4kids.widget.Header;

/**
 * @author deepanker.chaudhary
 */
public class ParentingArticlesActivity extends BaseActivity implements OnClickListener {
    public LinearLayout mParentLout;
    private Header header;
    ArticlesFragment articleFragment = new ArticlesFragment();
    BlogsFragment blogFragment = new BlogsFragment();
    private final int FOR_SEARCH_SCREEN = 1;
    private ParentingFilterType parentingType;
    private boolean isComeFromSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT < 11) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.parenting_articles);
        TextView headerTxt = (TextView) findViewById(R.id.header_txt);
        ((ImageView) findViewById(R.id.imgBack)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.img_search)).setOnClickListener(this);
        /**
         * According to cr these are removed deepanker
         */
        //	mParentLout=(LinearLayout)findViewById(R.id.parentLout);
        //	((CheckedTextView)findViewById(R.id.txvArticles)).setOnClickListener(this);
        //	((CheckedTextView)findViewById(R.id.txvBlogs)).setOnClickListener(this);
        //	((CheckedTextView)findViewById(R.id.txvArticles)).setChecked(true);
        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }


            /**
             * sliding functionality also removed according CR>
             */
            //	setHeader();
            /**
             * it is a CR: before this i add only one article fragment in onCreate
             * & we had tabs article & blogs . But now tabs will be remove & we open specific page
             * according to tap.
             *
             */
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                parentingType = (ParentingFilterType) bundle.getSerializable(Constants.PARENTING_TYPE);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                if (parentingType == ParentingFilterType.ARTICLES) {
                    headerTxt.setText("Articles");
                    transaction.add(R.id.fragment_container, articleFragment).commit();
                } else if (parentingType == ParentingFilterType.BLOGS) {
                    headerTxt.setText("Blogs");
                    transaction.add(R.id.fragment_container, blogFragment).commit();
                }


            }


        }


    }

    /**
     * backpress handling for fragments; Deepanker.chaudhary
     */

    @Override
    public void onBackPressed() {

        if (isComeFromSearch) {
            isComeFromSearch = false;
            if (parentingType == ParentingFilterType.ARTICLES) {
                if (articleFragment.isAdded()) {
                    articleFragment.isCommingFromSearch(null, 1, true);//1 is page count.
                }

            } else if (parentingType == ParentingFilterType.BLOGS) {
                if (blogFragment.isAdded()) {
                    blogFragment.isCommingFromSearch(null, 1, true);
                }
            }
        } else {
            super.onBackPressed();
        }


        /**
         * this was backpress handling when we were using two tabs in screen
         * but now no need for it.because we load only one fragment in one time.
         */
        //	android.support.v4.app.FragmentManager fm=getSupportFragmentManager();

		/*if (fm.getBackStackEntryCount() > 0) {

			if(((CheckedTextView)findViewById(R.id.txvArticles)).isChecked()){
				fm.popBackStack("blogs",FragmentManager.POP_BACK_STACK_INCLUSIVE);
				super.onBackPressed();  
			}else{
				Log.i("MainActivity", "popping backstack");

				fm.popBackStack("blogs",FragmentManager.POP_BACK_STACK_INCLUSIVE);

				((CheckedTextView)findViewById(R.id.txvBlogs)).setChecked(false);
				((CheckedTextView)findViewById(R.id.txvArticles)).setChecked(true);
			}

		} else {
			Log.i("MainActivity", "nothing on backstack, calling super");
			super.onBackPressed();  
		}*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        /*if(header!=null && mParentLout!=null){
        header.closeDrawer();
		mParentLout.setBackgroundColor(Color.WHITE);
		}*/
    }


    @Override
    protected void updateUi(Response response) {
        /*if(header!=null){
            header.closeDrawer();
		}*/
        removeProgressDialog();
        if (response == null) {

            showToast(getString(R.string.went_wrong));
            return;
        }
        switch (response.getDataType()) {
            case AppConstants.LOGOUT_REQUEST:
                LogoutResponse responseData = (LogoutResponse) response.getResponseObject();
                String message = responseData.getResult().getMessage();
                if (responseData.getResponseCode() == 200) {
                    /**
                     * delete table from local also;
                     */
                    UserTable _tables = new UserTable((BaseApplication) getApplicationContext());
                    _tables.deleteAll();
                    if (StringUtils.isNullOrEmpty(message)) {
                        Toast.makeText(this, getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    }

                } else if (responseData.getResponseCode() == 400) {
                    if (StringUtils.isNullOrEmpty(message)) {
                        Toast.makeText(this, getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            default:
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == FOR_SEARCH_SCREEN) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    /**
                     * this flag i am using for backpress handling according to
                     * client CR.
                     *
                     */
                    isComeFromSearch = true;

                    SearchListType searchListType = (SearchListType) bundle.getSerializable(Constants.PARENTING_SEARCH_LIST_TYPE);
                    ParentingFilterType parentingtype = (ParentingFilterType) bundle.getSerializable(Constants.PARENTING_TYPE);
                    Parcelable filterData = bundle.getParcelable(Constants.PARENTING_FILER_DATA);
                    String commonQureryForSearch = null;
                    switch (searchListType) {
                        case Authors:
                            FilterAuthors authorData = (FilterAuthors) filterData;
                            commonQureryForSearch = authorData.getName();
                            break;
                        case Topics:
                            FilterTopics topicsData = (FilterTopics) filterData;
                            commonQureryForSearch = topicsData.getName();
                            break;
                        case Tags:
                            FilterTags tagsData = (FilterTags) filterData;
                            commonQureryForSearch = tagsData.getName();
                            break;
                        case Blogs:
                            FilterBlogs blogsData = (FilterBlogs) filterData;
                            commonQureryForSearch = blogsData.getTitle();
                            break;
                        case Bloggers:
                            FilterAuthors bloggersData = (FilterAuthors) filterData;
                            commonQureryForSearch = bloggersData.getName();
                            break;

                        default:
                            break;
                    }
                    /*Bundle args=new Bundle();
                    args.putSerializable(Constants.PARENTING_SEARCH_LIST_TYPE, searchListType);
					args.putSerializable(Constants.PARENTING_TYPE, parentingtype);
					args.putString(Constants.PARENTING_SEARCH_QUERY, commonQureryForSearch);
					args.putBoolean(Constants.IS_PARENTING_COMMING_FROM_SEARCH, true);
					FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();*/
                    ParentingSearchRequest _searchRequest = new ParentingSearchRequest();
                    _searchRequest.setCityId(SharedPrefUtils.getCurrentCityModel(this).getId());
                    _searchRequest.setParentingType(parentingtype.getParentingType());
                    _searchRequest.setFilerType(searchListType.getSearchListType());
                    _searchRequest.setQuery(commonQureryForSearch);
                    if (parentingtype == ParentingFilterType.ARTICLES) {
                        //articleFragment.setArguments(args);
                        if (articleFragment.isAdded()) {
                            articleFragment.isCommingFromSearch(_searchRequest, 1, false);//1 is page count.
                            /*  transaction.add(R.id.fragment_container, articleFragment);
			    	  articleFragment.setRetainInstance(true);
	                  transaction.commit();*/
                        }


                    } else if (parentingtype == ParentingFilterType.BLOGS) {
                        //blogFragment.setArguments(args);
                        if (blogFragment.isAdded()) {
                            blogFragment.isCommingFromSearch(_searchRequest, 1, false);
							/*transaction.addToBackStack("blogs");
							transaction.replace(R.id.fragment_container, blogFragment);
							blogFragment.setRetainInstance(true);
							transaction.commit();*/
                        }
                    }


                }
            } else if (resultCode == RESULT_CANCELED) {
                //do nothing
            }
        }

    }


    private void setHeader() {
        header = (Header) findViewById(R.id.header);
        header.inflateHeader();
        header.openSlidingDrawer();
        header.updateHeaderText("Parenting Stops", true);
        header.updateSelectedCity(SharedPrefUtils.getCurrentCityModel(ParentingArticlesActivity.this).getName(), false);
        ((ImageView) header.findViewById(R.id.search_click)).setVisibility(View.VISIBLE);
        ((ImageView) header.findViewById(R.id.search_click)).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        //FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        try {


            switch (v.getId()) {
                /**
                 * according to CR > Tabs removed.
                 * I am not deleting the code may be in future it's needed according to client.
                 */
			/*case R.id.txvArticles:
				((CheckedTextView)findViewById(R.id.txvArticles)).setChecked(true);
				((CheckedTextView)findViewById(R.id.txvBlogs)).setChecked(false);
				//  ArticlesFragment articleFragment = new ArticlesFragment();
				//   transaction.addToBackStack(null);
				if(!articleFragment.isAdded()){
					transaction.replace(R.id.fragment_container, articleFragment);
					articleFragment.setRetainInstance(true);
					transaction.show(articleFragment);
					transaction.hide(blogFragment);
					transaction.commit();
				}else{
					transaction.show(articleFragment);
					transaction.hide(blogFragment);
					transaction.commit();
				}

				break;

			case R.id.txvBlogs:
				((CheckedTextView)findViewById(R.id.txvBlogs)).setChecked(true);
				((CheckedTextView)findViewById(R.id.txvArticles)).setChecked(false);
				//    BlogsFragment _blogFragment=new BlogsFragment();
				//   _blogFragment.setArguments(getIntent().getExtras());
				transaction=getSupportFragmentManager().beginTransaction();
				transaction.addToBackStack("blogs");
			//	transaction.hide(articleFragment);

				if(!blogFragment.isAdded()){
					transaction.add(R.id.fragment_container, blogFragment);
					blogFragment.setRetainInstance(true);
					transaction.show(blogFragment);
					transaction.hide(articleFragment);
					transaction.commit();
				}else{
					transaction.show(blogFragment);
					transaction.hide(articleFragment);
					transaction.commit();
				}


				break;*/
			/*case R.id.search_click:
				Intent intent=new Intent(this,ArticleBlogSearchDialogActivity.class);

				if(((CheckedTextView)findViewById(R.id.txvArticles)).isChecked()){
					intent.putExtra(Constants.PARENTING_TYPE,ParentingFilterType.ARTICLES);
				}else if(((CheckedTextView)findViewById(R.id.txvBlogs)).isChecked()){
					intent.putExtra(Constants.PARENTING_TYPE,ParentingFilterType.BLOGS);
				}
				startActivityForResult(intent,FOR_SEARCH_SCREEN);

				break;*/
                case R.id.img_search:
                    Intent intent = new Intent(this, ArticleBlogSearchDialogActivity.class);

                    if (parentingType == ParentingFilterType.ARTICLES) {
                        intent.putExtra(Constants.PARENTING_TYPE, ParentingFilterType.ARTICLES);
                    } else if (parentingType == ParentingFilterType.BLOGS) {
                        intent.putExtra(Constants.PARENTING_TYPE, ParentingFilterType.BLOGS);
                    }
                    startActivityForResult(intent, FOR_SEARCH_SCREEN);
                    break;
                case R.id.imgBack:
                    if (isComeFromSearch) {
                        isComeFromSearch = false;
                        if (parentingType == ParentingFilterType.ARTICLES) {
                            if (articleFragment.isAdded()) {
                                articleFragment.isCommingFromSearch(null, 1, true);//1 is page count.
                            }

                        } else if (parentingType == ParentingFilterType.BLOGS) {
                            if (blogFragment.isAdded()) {
                                blogFragment.isCommingFromSearch(null, 1, true);
                            }
                        }
                    } else {
                        finish();
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
