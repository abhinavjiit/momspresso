package com.mycity4kids.ui.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.mycity4kids.R;
import com.mycity4kids.enums.SearchListType;
import com.mycity4kids.models.parentingfilter.FilterAuthors;
import com.mycity4kids.models.parentingfilter.FilterBlogs;
import com.mycity4kids.models.parentingfilter.FilterTags;
import com.mycity4kids.models.parentingfilter.FilterTopics;
import com.mycity4kids.widget.BitmapLruCache;
/**
 * 
 * @author deepanker.chaudhary
 * @param <T>
 *
 */
public class ParentingFilterAdapter<T> extends BaseAdapter {
	private LayoutInflater mInflator;
	private Context mContext;
	private ArrayList<T> mCommonListData;
	private SearchListType mTabListType;
	ImageLoader.ImageCache imageCache;
	ImageLoader imageLoader;

	public	ParentingFilterAdapter(Context pContext){
		mInflator=LayoutInflater.from(pContext);
		mContext=pContext;
		imageCache = new BitmapLruCache();
		imageLoader = new ImageLoader(Volley.newRequestQueue(pContext), imageCache);
	}

	public  void setSearchData(ArrayList<T>  commonListData, SearchListType tabType){
		mCommonListData=commonListData;
		mTabListType=tabType;
	}

	public SearchListType getCurrentListType(){
		return mTabListType==null?SearchListType.None:mTabListType;
	}

	@Override
	public int getCount() {
		return mCommonListData == null ? 0 : mCommonListData.size();
	}

	@Override
	public Object getItem(int position) {
		return mCommonListData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		ViewHolder holder;
		if (view == null) {
			view = mInflator.inflate(R.layout.custom_filter_search_cell, null);
			holder = new ViewHolder();


			holder.mSeachTxt = (TextView) view.findViewById(R.id.common_search_txt);
			holder.mCommonImg=(NetworkImageView)view.findViewById(R.id.common_img);
			holder.mAuthorNameTxt=(TextView)view.findViewById(R.id.author_name_txt);
            holder.mBlogsNameTxt=(TextView)view.findViewById(R.id.blog_name_txt);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		T type=mCommonListData.get(position);
		if(type instanceof FilterAuthors && mTabListType==SearchListType.Authors){
			FilterAuthors authors=(FilterAuthors)type;
			holder.mAuthorNameTxt.setVisibility(View.VISIBLE);
		    holder.mBlogsNameTxt.setVisibility(View.VISIBLE);
		    holder.mSeachTxt.setVisibility(View.GONE);
		    holder.mAuthorNameTxt.setText(authors.getName());
		    holder.mBlogsNameTxt.setText(authors.getBlog_title());
			holder.mCommonImg.setVisibility(View.VISIBLE);
			setServerImage(holder.mCommonImg,authors);

		}else if(type instanceof FilterTags && mTabListType==SearchListType.Tags){
			FilterTags tags=(FilterTags)type;
			
			holder.mAuthorNameTxt.setVisibility(View.GONE);
		    holder.mBlogsNameTxt.setVisibility(View.GONE);
		    holder.mSeachTxt.setVisibility(View.VISIBLE);
			holder.mSeachTxt.setText(tags.getName());
			holder.mCommonImg.setVisibility(View.GONE);
		}else if(type instanceof FilterTopics && mTabListType==SearchListType.Topics){
			FilterTopics topics=(FilterTopics)type;
			holder.mAuthorNameTxt.setVisibility(View.GONE);
		    holder.mBlogsNameTxt.setVisibility(View.GONE);
		    holder.mSeachTxt.setVisibility(View.VISIBLE);
			holder.mSeachTxt.setText(topics.getName());
			holder.mCommonImg.setVisibility(View.GONE);
		}else if(type instanceof FilterAuthors && mTabListType==SearchListType.Bloggers){
			FilterAuthors bloggers=(FilterAuthors)type;
			holder.mAuthorNameTxt.setVisibility(View.VISIBLE);
		    holder.mBlogsNameTxt.setVisibility(View.VISIBLE);
		    holder.mSeachTxt.setVisibility(View.GONE);
		    holder.mAuthorNameTxt.setText(bloggers.getName());
		    holder.mBlogsNameTxt.setText(bloggers.getBlog_title());
			holder.mCommonImg.setVisibility(View.VISIBLE);
			setServerImage(holder.mCommonImg,bloggers);
		}else if(type instanceof FilterBlogs && mTabListType==SearchListType.Blogs){
			FilterBlogs blogs=(FilterBlogs)type;
			holder.mAuthorNameTxt.setVisibility(View.GONE);
		    holder.mBlogsNameTxt.setVisibility(View.GONE);
		    holder.mSeachTxt.setVisibility(View.VISIBLE);
			holder.mSeachTxt.setText(blogs.getTitle());
			holder.mCommonImg.setVisibility(View.GONE);
		}


		return view;
	}

public void setServerImage(NetworkImageView imageView,FilterAuthors authorsData){
	if (authorsData.getAuthor_image() != null) {
		//holder.imvAuthorThumb.setVisibility(View.GONE);
		//holder.imvNetworkAuthorThumb.setVisibility(View.VISIBLE);
		imageView.setDefaultImageResId(R.drawable.default_img);
		try {
			imageView.setImageUrl(authorsData.getAuthor_image(), imageLoader);

		} catch (Exception ex) {
			imageView.setErrorImageResId(R.drawable.default_img);
		}
	} else {
		//holder.imvAuthorThumb.setVisibility(View.VISIBLE);
		//holder.imvNetworkAuthorThumb.setVisibility(View.GONE);
	}
}


	class ViewHolder{
		TextView mSeachTxt;
		NetworkImageView mCommonImg;
		TextView mAuthorNameTxt;
		TextView mBlogsNameTxt;

	}



}
