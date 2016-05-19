package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.models.parentingstop.CommonParentingList;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by anshul on 5/12/16.
 */
public class ArticleListingDashboardAdapter extends BaseAdapter {

    private ArrayList<CommonParentingList> mArticleListData;
    private Context mContext;
    private LayoutInflater mInflator;
    ArrayList<CommonParentingList> articleDataModelsNew;
    Boolean newChanges = false;

    private final float density;

    public ArticleListingDashboardAdapter(Context pContext, Boolean newChanges) {

        density = pContext.getResources().getDisplayMetrics().density;

        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
        this.newChanges = newChanges;
    }

    public void setListData(ArrayList<CommonParentingList> mParentingLists) {
        mArticleListData = mParentingLists;
    }

    public void setNewListData(ArrayList<CommonParentingList> mParentingLists_new) {
        articleDataModelsNew = mParentingLists_new;
    }

    public void addNewListData(ArrayList<CommonParentingList> mParentingLists_new) {
        articleDataModelsNew.addAll(mParentingLists_new);
    }

    @Override
    public int getCount() {
        return articleDataModelsNew == null ? 0 : articleDataModelsNew.size();
    }

    @Override
    public Object getItem(int position) {
        return articleDataModelsNew.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        final ViewHolder holder;
        if (view == null) {
            view = mInflator.inflate(R.layout.card_item_article_dashboard, null);
            holder = new ViewHolder();
            holder.txvArticleTitle = (TextView) view.findViewById(R.id.txvArticleTitle);
           /* holder.txvAuthorName = (TextView) view.findViewById(R.id.txvAuthorName);
            holder.txvPublishDate = (TextView) view.findViewById(R.id.txvPublishDate);
            holder.imvAuthorThumb = (ImageView) view.findViewById(R.id.imvAuthorThumb);*/
//            holder.authorPic = (ImageView) view.findViewById(R.id.author_pic);


            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (newChanges) {

            holder.txvArticleTitle.setText(articleDataModelsNew.get(position).getTitle());
/*
            if (StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getAuthor_name()) || articleDataModelsNew.get(position).getAuthor_name().toString().trim().equalsIgnoreCase("")) {
                holder.txvAuthorName.setText("NA");
            } else {
                holder.txvAuthorName.setText(articleDataModelsNew.get(position).getAuthor_name());
            }*/

           /* holder.txvAuthorName.setTextColor(Color.parseColor(articleDataModelsNew.get(position).getAuthor_color_code()));
            holder.txvPublishDate.setText(articleDataModelsNew.get(position).getCreated());*/
            if (!StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getThumbnail_image())) {
                Picasso.with(mContext).load(articleDataModelsNew.get(position).getThumbnail_image()).placeholder(R.drawable.default_article).resize((int) (101 * density), (int) (67 * density)).centerCrop().into(holder.imvAuthorThumb);
            } else {
                holder.imvAuthorThumb.setBackgroundResource(R.drawable.article_default);
            }
//            if (!StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getAuthor_image())) {
//                Picasso.with(mContext).load(articleDataModelsNew.get(position).getAuthor_image()).placeholder(R.drawable.blue_man_icon).resize((int) (14 * density), (int) (14 * density)).centerCrop().into(holder.authorPic);
//            } else {
//                holder.imvAuthorThumb.setBackgroundResource(R.drawable.default_user);
//            }

           /* holder.txvAuthorName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, BlogDetailActivity.class);
                    intent.putExtra(Constants.IS_COMMING_FROM_LISTING, false);
                    if (!StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getAuthor_type())) {
                        if (articleDataModelsNew.get(position).getAuthor_type().trim().equalsIgnoreCase("Blogger")) {
                            intent.putExtra(Constants.ARTICLE_NAME, articleDataModelsNew.get(position).getBlog_name());
                            intent.putExtra(Constants.FILTER_TYPE, "blogs");
                        } else {
                            intent.putExtra(Constants.ARTICLE_NAME, articleDataModelsNew.get(position).getAuthor_name());
                            intent.putExtra(Constants.FILTER_TYPE, "authors");
                        }
                    }
                    mContext.startActivity(intent);
                }
            });
*/

        } else {
//            holder.txvArticleTitle.setText(mArticleListData.get(position).getTitle());
//            holder.txvAuthorName.setText(mArticleListData.get(position).getAuthor_name());
//
//		/*String PublishDateValues = DateTimeUtils.changeDate(mArticleListData.get(position).getArticleCreatedDate());
//        if (PublishDateValues != null) {
//			holder.txvPublishDate.setText(PublishDateValues);
//		}*/
//            holder.txvPublishDate.setText(mArticleListData.get(position).getCreated());
//            if (mArticleListData.get(position).getAuthor_image() != null) {
//                holder.imvAuthorThumb.setVisibility(View.GONE);
//                holder.imvNetworkAuthorThumb.setVisibility(View.VISIBLE);
//                holder.imvNetworkAuthorThumb.setDefaultImageResId(R.drawable.default_img);
//                try {
//                    holder.imvNetworkAuthorThumb.setImageUrl(mArticleListData.get(position).getAuthor_image(), imageLoader);
//                } catch (Exception ex) {
//                    holder.imvNetworkAuthorThumb.setErrorImageResId(R.drawable.default_img);
//                }
//            } else {
//                holder.imvAuthorThumb.setVisibility(View.VISIBLE);
////                holder.imvNetworkAuthorThumb.setVisibility(View.GONE);
//            }
        }

        return view;
    }

    class ViewHolder {
        TextView txvArticleTitle;
        TextView txvAuthorName;
        TextView txvPublishDate;
        ImageView imvAuthorThumb;
//        ImageView authorPic;
    }

    public void refreshArticleList(ArrayList<CommonParentingList> newList) {
        this.articleDataModelsNew = newList;
    }

}
