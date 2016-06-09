package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.newmodels.PublishedArticlesModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by hemant on 19/3/16.
 */
public class PublishedArticlesListAdapter extends BaseAdapter {

    private Context mContext;
    ArrayList<PublishedArticlesModel.PublishedArticleData> mPublishedArticlesList;
    private LayoutInflater mInflator;
    private final float density;
    private BtnClickListener mClickListener = null;


    class ViewHolder {
        TextView txvArticleTitle;
        TextView txvViewCount;
        TextView txvPublishDate;
        ImageView imvAuthorThumb;
        View popupButton;
    }

    public PublishedArticlesListAdapter(Context context, BtnClickListener listener) {
        mContext = context;
        mInflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        density = context.getResources().getDisplayMetrics().density;
        mClickListener = listener;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        final ViewHolder holder;
        if (view == null) {
            view = mInflator.inflate(R.layout.published_articles_item, null);
            holder = new ViewHolder();
            holder.txvArticleTitle = (TextView) view.findViewById(R.id.txvArticleTitle);
            holder.txvPublishDate = (TextView) view.findViewById(R.id.txvPublishDate);
            holder.imvAuthorThumb = (ImageView) view.findViewById(R.id.imvAuthorThumb);
            holder.txvViewCount = (TextView) view.findViewById(R.id.txvViewCount);
            holder.popupButton = view.findViewById(R.id.img_menu);
            holder.popupButton.setTag(getItem(position));
            if (Build.VERSION.SDK_INT == 15) {
                holder.popupButton.setVisibility(View.GONE);
            }
            //    popupButton.setOnClickListener((DraftListViewActivity)context);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.txvArticleTitle.setText(mPublishedArticlesList.get(position).getTitle());
        holder.txvViewCount.setText(mPublishedArticlesList.get(position).getCurrent_views() + " Views");
        holder.txvPublishDate.setText(mPublishedArticlesList.get(position).getCreated());
        if (!StringUtils.isNullOrEmpty(mPublishedArticlesList.get(position).getThumbnail_image())) {
            Picasso.with(mContext).load(mPublishedArticlesList.get(position).getThumbnail_image())
                    .placeholder(R.drawable.default_article)
                    .into(holder.imvAuthorThumb);
        } else {
            holder.imvAuthorThumb.setBackgroundResource(R.drawable.article_default);
        }
        holder.popupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popup = new PopupMenu(mContext, holder.popupButton);
                popup.getMenuInflater().inflate(R.menu.pop_up_menu_published, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int i = item.getItemId();
                        if (i == R.id.edit) {
                            mClickListener.onBtnClick(position);
                               /* Intent intent=new Intent(mContext,EditorPostActivity.class);
                               // intent.putExtra("publishedItem",(PublishedArticlesModel.PublishedArticleData)getItem(position));
                                intent.putExtra("from","publishedList");

                                intent.putExtra(Constants.ARTICLE_ID, String.valueOf( ( (PublishedArticlesModel.PublishedArticleData)getItem(position)).getId()));
                                intent.putExtra(Constants.PARENTING_TYPE, ParentingFilterType.ARTICLES);
                                mContext.startActivity(intent);
                                Log.e("edit", "clicked");
                                //do something*/
                            return true;
                        } else {
                            return onMenuItemClick(item);
                        }
                    }

                });
                popup.show();
            }
        });

        return view;
    }

    public void setNewListData(ArrayList<PublishedArticlesModel.PublishedArticleData> mPublishedArticlesList) {
        this.mPublishedArticlesList = mPublishedArticlesList;
    }

    @Override
    public int getCount() {
        return mPublishedArticlesList == null ? 0 : mPublishedArticlesList.size();
    }

    @Override
    public Object getItem(int position) {
        return mPublishedArticlesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public interface BtnClickListener {
        void onBtnClick(int position);
    }
}
