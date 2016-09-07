package com.mycity4kids.ui.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.LayoutParams;
import android.widget.TextView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.ParentingFilterSearchController;
import com.mycity4kids.enums.ParentingFilterType;
import com.mycity4kids.enums.SearchListType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.parentingfilter.ArticleBlogFilterData;
import com.mycity4kids.models.parentingfilter.ArticleBlogFilterResponse;
import com.mycity4kids.models.parentingfilter.FilterAuthors;
import com.mycity4kids.models.parentingfilter.FilterBlogs;
import com.mycity4kids.models.parentingfilter.FilterTags;
import com.mycity4kids.models.parentingfilter.FilterTopics;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.adapter.ParentingFilterAdapter;
/**
 * 
 * @author deepanker.chaudhary
 * @param <T>
 *  */
public class ArticleBlogSearchDialogActivity<T> extends BaseActivity implements OnClickListener{
	//private ListView mSearchParentingList;
	private ParentingFilterAdapter<T> mAdapter;
	private EditText mSearchEditTxt;
	private ParentingFilterType mParentingType;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utils.pushOpenScreenEvent(ArticleBlogSearchDialogActivity.this, "Blog Search", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");

		try {
			setContentView(R.layout.activity_article_blog_search);
			((ImageView)findViewById(R.id.imgBack)).setOnClickListener(this);
			TextView headerTxt=(TextView)findViewById(R.id.header_txt);
			((ImageView)findViewById(R.id.img_search)).setVisibility(View.GONE);
			ListView mSearchParentingList=(ListView)findViewById(R.id.searchParentingList);
			mSearchEditTxt=(EditText)findViewById(R.id.search_edit_txt);
			mSearchEditTxt.addTextChangedListener(new GenericTextWatcher(mSearchEditTxt));
			mAdapter=new ParentingFilterAdapter<T>(this);
			mSearchParentingList.setAdapter(mAdapter);
			Bundle bundle=getIntent().getExtras();
			if(bundle!=null){
				//	String parentingType=bundle.getString(Constants.PARENTING_TYPE);
				mParentingType=(ParentingFilterType)bundle.getSerializable(Constants.PARENTING_TYPE);
				showProgressDialog(getString(R.string.fetching_data));
				ParentingFilterSearchController _controller=new ParentingFilterSearchController(this, this);
				_controller.getData(AppConstants.ARTICLE_SEARCH_FILTER_REQUEST, mParentingType.getParentingType());

				if(mParentingType==ParentingFilterType.ARTICLES){
					headerTxt.setText("Articles");
					mSearchEditTxt.setHint("Search by Author");
				}else if(mParentingType==ParentingFilterType.BLOGS){
					headerTxt.setText("Blogs");
					mSearchEditTxt.setHint("Search by Blogger");
				}

			}
			mSearchParentingList.setOnItemClickListener(new OnItemClickListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int pos, long id) {
					SearchListType currentListType=((ParentingFilterAdapter<T>)parent.getAdapter()).getCurrentListType();
					T currentItem=(T) ((ParentingFilterAdapter<T>)parent.getAdapter()).getItem(pos);
					Intent intent=new Intent();

					switch (currentListType) {
					case Authors:
						FilterAuthors authorData=(FilterAuthors)currentItem;
						intent.putExtra(Constants.PARENTING_FILER_DATA, authorData);
						break;
					case Topics:
						FilterTopics topicsData=(FilterTopics)currentItem;
						intent.putExtra(Constants.PARENTING_FILER_DATA, topicsData);
						break;
					case Tags:
						FilterTags tagsData=(FilterTags)currentItem;
						intent.putExtra(Constants.PARENTING_FILER_DATA, tagsData);
						break;
					case Blogs:
						FilterBlogs blogsData=(FilterBlogs)currentItem;
						intent.putExtra(Constants.PARENTING_FILER_DATA, blogsData);
						break;
					case Bloggers:
						FilterAuthors bloggersData=(FilterAuthors)currentItem;
						intent.putExtra(Constants.PARENTING_FILER_DATA, bloggersData);
						break;

						default:
							break;
					}

					/*if(currentListType==SearchListType.Authors){
						FilterAuthors authorData=(FilterAuthors)currentItem;
						intent.putExtra("FilterData", authorData);
					}else if(currentListType==SearchListType.Tags){
						FilterTags tagsData=(FilterTags)currentItem;
						intent.putExtra("FilterData", tagsData);
					}
					else if(currentListType==SearchListType.Topics){
						FilterTopics topicsData=(FilterTopics)currentItem;
						intent.putExtra("FilterData", topicsData);
					}
					else if(currentListType==SearchListType.Blogs){
						FilterBlogs blogsData=(FilterBlogs)currentItem;
						intent.putExtra("FilterData", blogsData);
					}
					else if(currentListType==SearchListType.Bloggers){
						FilterAuthors bloggersData=(FilterAuthors)currentItem;
						intent.putExtra("FilterData", bloggersData);
					}
*/
					intent.putExtra(Constants.PARENTING_TYPE, mParentingType);
					intent.putExtra(Constants.PARENTING_SEARCH_LIST_TYPE, currentListType);
					setResult(RESULT_OK,intent);
					finish();

				}
			});


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void updateUi(Response response) {
		removeProgressDialog();
		if( response==null){
			showToast("Something went wrong from server");
			return;
		}


		switch (response.getDataType()) {
		case AppConstants.ARTICLE_SEARCH_FILTER_REQUEST:
			ArticleBlogFilterResponse responseData=(ArticleBlogFilterResponse)response.getResponseObject();
			if(responseData.getResponseCode()==200){

				RadioGroup tabContainer=(RadioGroup)this.findViewById(R.id.tab_container_radio_grp);
				getResponseAndUpdateOnUi(responseData,tabContainer);


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
		}

	}
	/**
	 * this method create tabs & we load list according to selected tab:
	 * @param responseData
	 * @param tabContainer
	 */

	@SuppressWarnings("unchecked")
	private void getResponseAndUpdateOnUi(ArticleBlogFilterResponse responseData, RadioGroup tabContainer) {
		HolderForFilter<T> holder=null;
		LayoutInflater inflater=LayoutInflater.from(this);
		RadioGroup.LayoutParams params=new RadioGroup.LayoutParams(0, LayoutParams.WRAP_CONTENT);
		params.weight=1;

		ArticleBlogFilterData responseLists = responseData.getResult().getData();
		ArrayList<FilterAuthors> authorList = responseLists.getAuthors();
		ArrayList<FilterTopics> topicsList = responseLists.getTopics();
		ArrayList<FilterTags> tagLists = responseLists.getTags();
		ArrayList<FilterAuthors> bloggersList = responseLists.getBloggers();
		ArrayList<FilterBlogs> blogsList = responseLists.getBlogs();
		if(authorList!=null && authorList.size()>0){
			holder=new HolderForFilter<T>();
			RadioButton tabRadioBtn=(RadioButton)inflater.inflate(R.layout.custom_radio_tab_design, null);
			tabRadioBtn.setLayoutParams(params);
			tabRadioBtn.setId(1);
			tabRadioBtn.setBackgroundResource(R.drawable.radio_btn_tab_selector);
			tabRadioBtn.setOnClickListener(this);
			tabRadioBtn.setChecked(true);
			tabRadioBtn.setText("AUTHORS");
			holder.tabType=SearchListType.Authors;
			holder.tabLists=(ArrayList<T>) authorList;
			tabRadioBtn.setTag(holder);
			tabContainer.addView(tabRadioBtn);
			View divider = new View(this);
			divider.setBackgroundResource(R.color.white_color);
			tabContainer.addView(divider,1,60);
			/**
			 * update on list
			 */
			mAdapter.setSearchData((ArrayList<T>) authorList,SearchListType.Authors);
			mAdapter.notifyDataSetChanged();
			/**
			 * add tag in Search Edit text which we will use at search time 
			 * 
			 */
			mSearchEditTxt.setTag(holder);
		}
		if(topicsList!=null && topicsList.size()>0){
			holder=new HolderForFilter<T>();
			RadioButton tabRadioBtn=(RadioButton)inflater.inflate(R.layout.custom_radio_tab_design, null);
			tabRadioBtn.setLayoutParams(params);
			tabRadioBtn.setId(0);
			tabRadioBtn.setBackgroundResource(R.drawable.radio_btn_tab_selector);
			tabRadioBtn.setOnClickListener(this);
			tabRadioBtn.setText("TOPICS");
			//tabRadioBtn.setTag(SearchListType.Topics);
			holder.tabType=SearchListType.Topics;
			holder.tabLists=(ArrayList<T>) topicsList;
			tabRadioBtn.setTag(holder);
			//tabRadioBtn.setBottom(android.R.color.transparent);
			tabContainer.addView(tabRadioBtn);
			View divider = new View(this);
			divider.setBackgroundResource(R.color.white_color);
			tabContainer.addView(divider,1,60);
		}


		if(bloggersList!=null && bloggersList.size()>0){
			holder=new HolderForFilter<T>();
			RadioButton tabRadioBtn=(RadioButton)inflater.inflate(R.layout.custom_radio_tab_design, null);
			//tabRadioBtn.setBottom(android.R.color.transparent);
			tabRadioBtn.setLayoutParams(params);
			tabRadioBtn.setId(2);
			tabRadioBtn.setBackgroundResource(R.drawable.radio_btn_tab_selector);
			tabRadioBtn.setOnClickListener(this);
			tabRadioBtn.setText("BLOGGERS");
			//tabRadioBtn.setTag(SearchListType.Bloggers);
			holder.tabType=SearchListType.Bloggers;
			holder.tabLists=(ArrayList<T>) bloggersList;
			tabRadioBtn.setTag(holder);
			tabRadioBtn.setChecked(true);
			tabContainer.addView(tabRadioBtn);
			View divider = new View(this);
			divider.setBackgroundResource(R.color.white_color);
			tabContainer.addView(divider,1,60);
			mAdapter.setSearchData((ArrayList<T>) bloggersList,SearchListType.Bloggers);
			mAdapter.notifyDataSetChanged();
			mSearchEditTxt.setTag(holder);

		}
		if(blogsList!=null && blogsList.size()>0){
			holder=new HolderForFilter<T>();
			RadioButton tabRadioBtn=(RadioButton)inflater.inflate(R.layout.custom_radio_tab_design, null);
			tabRadioBtn.setLayoutParams(params);
			tabRadioBtn.setId(3);
			///tabRadioBtn.setBottom(android.R.color.transparent);
			tabRadioBtn.setBackgroundResource(R.drawable.radio_btn_tab_selector);
			tabRadioBtn.setOnClickListener(this);
			tabRadioBtn.setText("BLOGS");
			//	tabRadioBtn.setTag(SearchListType.Blogs);
			holder.tabType=SearchListType.Blogs;
			holder.tabLists=(ArrayList<T>) blogsList;
			tabRadioBtn.setTag(holder);
			tabContainer.addView(tabRadioBtn);
			View divider = new View(this);
			divider.setBackgroundResource(R.color.white_color);
			tabContainer.addView(divider,1,60);
		}

		if(tagLists!=null && tagLists.size()>0){
			holder=new HolderForFilter<T>();
			RadioButton tabRadioBtn=(RadioButton)inflater.inflate(R.layout.custom_radio_tab_design, null);
			tabRadioBtn.setLayoutParams(params);
			tabRadioBtn.setId(4);
			//tabRadioBtn.setBottom(android.R.color.transparent);
			tabRadioBtn.setBackgroundResource(R.drawable.radio_btn_tab_selector);
			tabRadioBtn.setOnClickListener(this);
			tabRadioBtn.setText("TAGS");
			//	tabRadioBtn.setTag(SearchListType.Tags);
			holder.tabType=SearchListType.Tags;
			holder.tabLists=(ArrayList<T>) tagLists;
			tabRadioBtn.setTag(holder);
			tabContainer.addView(tabRadioBtn);

		}


	}

	@Override
	public void onClick(View v) {
		try {

			switch (v.getId()) {
			
			case 0:
				mSearchEditTxt.setText("");
				mSearchEditTxt.setHint("Search by Topics");
				manageTabs(v);
				break;
			case 1:
				mSearchEditTxt.setText("");
				mSearchEditTxt.setHint("Search by Authors");
				manageTabs(v);
				break;
			case 2:
				mSearchEditTxt.setText("");
				mSearchEditTxt.setHint("Search by Blogger");
				manageTabs(v);
				break;
			case 3:
				mSearchEditTxt.setText("");
				mSearchEditTxt.setHint("Search by Blogs");
				manageTabs(v);
				break;
			case 4:
				mSearchEditTxt.setText("");
				mSearchEditTxt.setHint("Search by Tags");
				manageTabs(v);
				break;
				
			case R.id.imgBack:
				finish();
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * update data on list according to selected tab
	 * @param view
	 */
	@SuppressWarnings("unchecked")
	private void manageTabs(View view){
		if(view instanceof RadioButton){
			RadioButton tabBtn=(RadioButton)view;
			HolderForFilter<T> holder = (HolderForFilter<T>) tabBtn.getTag();
			SearchListType tabType=holder.tabType;
			ArrayList<T> tempArrayList=new ArrayList<T>();
			tempArrayList.addAll((ArrayList<T>)holder.tabLists);
			if(mAdapter!=null)
				mAdapter.setSearchData(tempArrayList,holder.tabType);
			mAdapter.notifyDataSetChanged();
			mSearchEditTxt.setTag(holder);
			Log.i("tab name", tabType.toString());
		}
	}

	@SuppressWarnings("hiding")
	private class HolderForFilter<T>{
		private SearchListType tabType;
		private ArrayList<T> tabLists;
	}

	/**
	 * Search functionality will work from here:
	 */
	public class GenericTextWatcher implements TextWatcher{
		private EditText mSearchEdiText;

		public  GenericTextWatcher(EditText pEditTxt){
			this.mSearchEdiText=pEditTxt;
		}
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			search(s.toString(),mSearchEdiText);

		}
		@Override
		public void afterTextChanged(Editable s) {


		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}



	}

	/**
	 * Search logic according to selected tab
	 * @param text
	 * @param mSearchEdiText
	 */

	@SuppressWarnings("unchecked")
	private void search(String text, EditText mSearchEdiText) {

		ArticleBlogSearchDialogActivity<T>.HolderForFilter<T> holder=( ArticleBlogSearchDialogActivity<T>.HolderForFilter<T>)mSearchEdiText.getTag();
		ArrayList<T> listData=(ArrayList<T>)holder.tabLists;
		SearchListType listType=(SearchListType)holder.tabType;
		System.out.println(listType.toString());
		System.out.println(listData.toString());
		if (text.length() != 0) {
			ArrayList<T> tempSearchList=new ArrayList<T>();
			if(listType==SearchListType.Authors){
				ArrayList<FilterAuthors> authorList=(ArrayList<FilterAuthors>)listData;
				if(authorList!=null && authorList.size()>0){
					for(FilterAuthors authorsData : authorList){
						String tempData=authorsData.getName();
						if(!StringUtils.isNullOrEmpty(tempData) && (tempData.toLowerCase()).startsWith(text)){
							tempSearchList.add((T) authorsData);
						}
					}
					mAdapter.setSearchData(tempSearchList, listType);
					mAdapter.notifyDataSetChanged();
				}


			}else if(listType==SearchListType.Topics){
				ArrayList<FilterTopics> topicsList=(ArrayList<FilterTopics>)listData;
				if(topicsList!=null && topicsList.size()>0){
					for(FilterTopics authorsData : topicsList){
						String tempData=authorsData.getName();
						if(!StringUtils.isNullOrEmpty(tempData) && (tempData.toLowerCase()).startsWith(text)){
							tempSearchList.add((T) authorsData);
						}
					}
					mAdapter.setSearchData(tempSearchList, listType);
					mAdapter.notifyDataSetChanged();
				}
			}
			else if(listType==SearchListType.Tags){
				ArrayList<FilterTags> tagsList=(ArrayList<FilterTags>)listData;
				if(tagsList!=null && tagsList.size()>0){
					for(FilterTags tagsData : tagsList){
						String tempData=tagsData.getName();
						if(!StringUtils.isNullOrEmpty(tempData) && (tempData.toLowerCase()).startsWith(text)){
							tempSearchList.add((T) tagsData);
						}
					}
					mAdapter.setSearchData(tempSearchList, listType);
					mAdapter.notifyDataSetChanged();
				}
			}
			else if(listType==SearchListType.Blogs){
				ArrayList<FilterBlogs> blogsList=(ArrayList<FilterBlogs>)listData;
				if(blogsList!=null && blogsList.size()>0){
					for(FilterBlogs blogsData : blogsList){
						String tempData=blogsData.getTitle();
						if(!StringUtils.isNullOrEmpty(tempData) && (tempData.toLowerCase()).startsWith(text)){
							tempSearchList.add((T) blogsData);
						}
					}
					mAdapter.setSearchData(tempSearchList, listType);
					mAdapter.notifyDataSetChanged();
				}
			}
			else if(listType==SearchListType.Bloggers){
				ArrayList<FilterAuthors> bloggersList=(ArrayList<FilterAuthors>)listData;
				if(bloggersList!=null && bloggersList.size()>0){
					for(FilterAuthors bloggerssData : bloggersList){
						String tempData=bloggerssData.getName();
						if(!StringUtils.isNullOrEmpty(tempData) && (tempData.toLowerCase()).startsWith(text)){
							tempSearchList.add((T) bloggerssData);
						}
					}
					mAdapter.setSearchData(tempSearchList, listType);
					mAdapter.notifyDataSetChanged();
				}
			}

		} else if (text.length() == 0) {
			mAdapter.setSearchData(listData, listType);
			mAdapter.notifyDataSetChanged();
		}

	}



}
