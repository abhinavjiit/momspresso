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
    Boolean newChanges = false;
    private BtnClickListener mClickListener = null;
    private final float density;
    Boolean isPrivateProfile;

    public PublishedArticleListingAdapter(Context pContext, BtnClickListener listener,Boolean mIsPrivateProfile) {

        density = pContext.getResources().getDisplayMetrics().density;

        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
        mClickListener = listener;
        this.newChanges = newChanges;
        isPrivateProfile=mIsPrivateProfile;
    }

    public void setListData(ArrayList<ArticleListingResult> mParentingLists) {
        mArticleListData = mParentingLists;
    }

    public void setNewListData(ArrayList<ArticleListingResult> mParentingLists_new) {
        articleDataModelsNew = mParentingLists_new;
    }

    public void addNewListData(ArrayList<ArticleListingResult> mParentingLists_new) {
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
            view = mInflator.inflate(R.layout.draft_list_item, null);
            holder = new ViewHolder();
            holder.txvArticleTitle = (TextView) view.findViewById(R.id.txvArticleTitle);
            //  holder.txvAuthorName = (TextView) view.findViewById(R.id.txvAuthorName);
            holder.txvPublishDate = (TextView) view.findViewById(R.id.txvPublishDate);
            holder.unapproved = (TextView) view.findViewById(R.id.unapproved);
            holder.unapproved.setVisibility(View.GONE);
            //   holder.imvAuthorThumb = (ImageView) view.findViewById(R.id.imvAuthorThumb);
//            holder.authorPic = (ImageView) view.findViewById(R.id.author_pic);
            holder.popupButton = view.findViewById(R.id.img_menu);
            if (!isPrivateProfile)
            {
                holder.popupButton.setVisibility(View.GONE);
            }
            else
            {
                holder.popupButton.setVisibility(View.VISIBLE);
            }
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
            //    calendar1.setTime(sdf.parse(draftlist.get(position).getUpdatedTime()));
            //   calendar1.add(Calendar.MILLISECOND, tz.getOffset(calendar1.getTimeInMillis()));
            calendar1.setTimeInMillis(articleDataModelsNew.get(position).getCreatedTime() * 1000);

            Long diff = System.currentTimeMillis() - articleDataModelsNew.get(position).getCreatedTime() * 1000;
            if (diff / (1000 * 60 * 60) > 24 && !sdf.format(System.currentTimeMillis()).equals(sdf.format((articleDataModelsNew.get(position).getCreatedTime() * 1000)))) {
                holder.txvPublishDate.setText(sdf.format(calendar1.getTime()));
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
        //   TextView txvAuthorName;
        TextView txvPublishDate;
        //      ImageView imvAuthorThumb;
        TextView unapproved;
        View popupButton;
//        ImageView authorPic;
    }

    public void refreshArticleList(ArrayList<ArticleListingResult> newList) {
        this.articleDataModelsNew = newList;
    }

    public interface BtnClickListener {
        void onBtnClick(int position);
    }
}
