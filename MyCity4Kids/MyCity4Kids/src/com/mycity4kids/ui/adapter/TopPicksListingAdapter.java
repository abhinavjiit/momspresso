package com.mycity4kids.ui.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.loading.TaskImageLoader;
import com.mycity4kids.models.parentingstop.CommonParentingList;
import com.mycity4kids.widget.BitmapLruCache;

public class TopPicksListingAdapter extends BaseAdapter {

	ImageLoader.ImageCache imageCache;
	ImageLoader imageLoader;
	private ArrayList<CommonParentingList> mTopPicksData;
	private LayoutInflater mInflator;
	TaskImageLoader loader;

	public TopPicksListingAdapter(Context pContext) {
		imageCache = new BitmapLruCache();
		imageLoader = new ImageLoader(Volley.newRequestQueue(pContext),imageCache);
		mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setListData(ArrayList<CommonParentingList> mTopPicksList) {
		mTopPicksData = mTopPicksList;
	}

	@Override
	public int getCount() {
		return mTopPicksData == null ? 0 : mTopPicksData.size();
	}

	@Override
	public Object getItem(int position) {
		return mTopPicksData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {

		ViewHolder holder;
		if (view == null) {
			view = mInflator.inflate(R.layout.list_item_top_picks, null);
			holder = new ViewHolder();
			holder.txvArticleTitle = (TextView) view.findViewById(R.id.txvArticleTitle);
					
			holder.txvAuthorName = (TextView) view.findViewById(R.id.txvAuthorName);
					
			holder.txvPublishDate = (TextView) view.findViewById(R.id.txvPublishDate);
					
			// holder.txvDescription = (TextView)
			// view.findViewById(R.id.txvDescription);
			holder.imvAuthorThumb = (ImageView) view.findViewById(R.id.imvAuthorThumb);
					
			holder.imvNetworkAuthorThumb = (NetworkImageView) view.findViewById(R.id.imvAuthorThumb1);
					
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		holder.txvArticleTitle.setText(mTopPicksData.get(position).getTitle());
				
		holder.txvAuthorName.setText(mTopPicksData.get(position).getAuthor_name());
		
		if (!StringUtils.isNullOrEmpty(mTopPicksData.get(position).getCreated())) {
			holder.txvPublishDate.setText(mTopPicksData.get(position).getCreated());
		}

		if (mTopPicksData.get(position).getAuthor_image() != null) {
			holder.imvAuthorThumb.setVisibility(View.GONE);
			holder.imvNetworkAuthorThumb.setVisibility(View.VISIBLE);
			holder.imvNetworkAuthorThumb.setDefaultImageResId(R.drawable.default_img);
			try {
			holder.imvNetworkAuthorThumb.setImageUrl(mTopPicksData.get(position).getAuthor_image(), imageLoader);
					
			} catch (Exception ex) {
				holder.imvNetworkAuthorThumb.setErrorImageResId(R.drawable.default_img);
			}
		} else {
			holder.imvAuthorThumb.setVisibility(View.VISIBLE);
			holder.imvNetworkAuthorThumb.setVisibility(View.GONE);
		}
		return view;
	}

	class ViewHolder {
		TextView txvArticleTitle;
		TextView txvAuthorName;
		TextView txvPublishDate;
		// TextView txvDescription;
		ImageView imvAuthorThumb;
		NetworkImageView imvNetworkAuthorThumb;
	}
}
