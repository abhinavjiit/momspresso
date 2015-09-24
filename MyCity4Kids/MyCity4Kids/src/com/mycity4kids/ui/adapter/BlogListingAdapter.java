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
import com.kelltontech.utils.DateTimeUtils;
import com.mycity4kids.R;
import com.mycity4kids.loading.TaskImageLoader;
import com.mycity4kids.models.parentingstop.ParentingArticleListModel;
import com.mycity4kids.widget.BitmapLruCache;
/**
 * 
 * @author deepanker.chaudhary
 *
 */
public class BlogListingAdapter extends BaseAdapter {

	ImageLoader.ImageCache imageCache;
	ImageLoader imageLoader;
	private ArrayList<ParentingArticleListModel> mBlogListData;
	private Context mContext;
	private LayoutInflater mInflator;
	TaskImageLoader loader;

	public BlogListingAdapter(Context pContext) {
		imageCache = new BitmapLruCache();
		imageLoader = new ImageLoader(Volley.newRequestQueue(pContext),imageCache);
		mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext = pContext;
	}

	public void setListData(ArrayList<ParentingArticleListModel> pBlogListData) {
		mBlogListData = pBlogListData;
	}

	@Override
	public int getCount() {
		return mBlogListData == null ? 0 : mBlogListData.size();
	}

	@Override
	public Object getItem(int position) {
		return mBlogListData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {

		ViewHolder holder;
		if (view == null) {
			view = mInflator.inflate(R.layout.list_item_articles, null);
			holder = new ViewHolder();
			holder.txvArticleTitle = (TextView) view.findViewById(R.id.txvArticleTitle);
			holder.txvAuthorName = (TextView) view.findViewById(R.id.txvAuthorName);
			holder.txvPublishDate = (TextView) view.findViewById(R.id.txvPublishDate);
			holder.imvAuthorThumb = (ImageView) view.findViewById(R.id.imvAuthorThumb);
			holder.imvNetworkAuthorThumb = (NetworkImageView) view.findViewById(R.id.imvAuthorThumb1);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		holder.txvArticleTitle.setText(mBlogListData.get(position).getArticleTitle());

		holder.txvAuthorName.setText(mBlogListData.get(position).getAuthorFirstName()+ " "+ mBlogListData.get(position).getAuthorLastName());



		String PublishDateValues = DateTimeUtils.changeDate(mBlogListData.get(position).getArticleCreatedDate());
		if (PublishDateValues != null) {
			holder.txvPublishDate.setText(PublishDateValues);
		}

		if (mBlogListData.get(position).getAuthorProfileImg() != null) {
			holder.imvAuthorThumb.setVisibility(View.GONE);
			holder.imvNetworkAuthorThumb.setVisibility(View.VISIBLE);
			holder.imvNetworkAuthorThumb.setDefaultImageResId(R.drawable.default_img);
			try {
				holder.imvNetworkAuthorThumb.setImageUrl(mBlogListData.get(position).getAuthorProfileImg(), imageLoader);

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
		ImageView imvAuthorThumb;
		NetworkImageView imvNetworkAuthorThumb;
	}
}
