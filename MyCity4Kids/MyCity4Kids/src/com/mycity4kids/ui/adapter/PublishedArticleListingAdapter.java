package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.kelltontech.utils.DateTimeUtils;
import com.mycity4kids.R;
import com.mycity4kids.models.response.ArticleListingResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by anshul on 8/3/16.
 */
public class PublishedArticleListingAdapter extends BaseAdapter {
    private ArrayList<ArticleListingResult> mArticleListData;
    private Context mContext;
    private LayoutInflater mInflator;
    ArrayList<ArticleListingResult> articleDataModelsNew;
    private BtnClickListener mClickListener = null;
    Boolean isPrivateProfile;

    public PublishedArticleListingAdapter(Context pContext, BtnClickListener listener, Boolean mIsPrivateProfile) {
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
        mClickListener = listener;
        isPrivateProfile = mIsPrivateProfile;
    }

    public void setListData(ArrayList<ArticleListingResult> mParentingLists) {
        mArticleListData = mParentingLists;
    }

    public void setNewListData(ArrayList<ArticleListingResult> mParentingLists_new) {
        articleDataModelsNew = mParentingLists_new;
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
            view = mInflator.inflate(R.layout.draft_list_item, null);
            holder = new ViewHolder();
            holder.txvArticleTitle = (TextView) view.findViewById(R.id.txvArticleTitle);
            holder.txvPublishDate = (TextView) view.findViewById(R.id.txvPublishDate);
            holder.unapproved = (TextView) view.findViewById(R.id.unapproved);
            holder.txvArticleViewCount = (TextView) view.findViewById(R.id.articleViewCountTextView);

            holder.txvArticleViewCount.setVisibility(View.VISIBLE);
            holder.unapproved.setVisibility(View.GONE);
            holder.popupButton = view.findViewById(R.id.img_menu);
            if (!isPrivateProfile) {
                holder.popupButton.setVisibility(View.GONE);
            } else {
                holder.popupButton.setVisibility(View.VISIBLE);
            }

            holder.txvArticleViewCount.setText(articleDataModelsNew.get(position).getArticleCount() + " Views");
            holder.popupButton.setTag(getItem(position));
            if (Build.VERSION.SDK_INT == 15) {
                holder.popupButton.setVisibility(View.GONE);
            }

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.txvArticleTitle.setText(articleDataModelsNew.get(position).getTitle());
        try {

            Calendar calendar1 = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm", Locale.US);
            calendar1.setTimeInMillis(articleDataModelsNew.get(position).getCreatedTime() * 1000);

            Long diff = System.currentTimeMillis() - articleDataModelsNew.get(position).getCreatedTime() * 1000;
            if (diff / (1000 * 60 * 60) > 24 && !sdf.format(System.currentTimeMillis()).equals(sdf.format((articleDataModelsNew.get(position).getCreatedTime() * 1000)))) {
                holder.txvPublishDate.setText(DateTimeUtils.getDateFromTimestamp(articleDataModelsNew.get(position).getCreatedTime()));
            } else {
                holder.txvPublishDate.setText(sdf1.format(calendar1.getTime()));
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    class ViewHolder {
        TextView txvArticleTitle;
        TextView txvPublishDate;
        TextView txvArticleViewCount;
        TextView unapproved;
        View popupButton;
    }

    public interface BtnClickListener {
        void onBtnClick(int position);
    }
}
