package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.hb.views.PinnedSectionListView;
import com.mycity4kids.R;
import com.mycity4kids.editor.DraftListViewActivity;
import com.mycity4kids.editor.EditorPostActivity;
import com.mycity4kids.models.response.PublishDraftObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by anshul on 3/16/16.
 */
public class ReviewsListAdapter extends BaseAdapter implements PinnedSectionListView.PinnedSectionListAdapter {
    Context context;
    ArrayList<PublishDraftObject> draftlist;
    private LayoutInflater mInflator;
    DraftListViewActivity draftListView;
    TimeZone tz = TimeZone.getDefault();

    public ReviewsListAdapter(Context context, ArrayList<PublishDraftObject> draftlist) {
        this.context = context;
        this.draftlist = draftlist;
        mInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return draftlist == null ? 0 : draftlist.size();
    }

    @Override
    public Object getItem(int position) {
        return draftlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    ViewHolder holder=null;
    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        if (getItemViewType(position)==0)
        {
            if (view == null) {
                view = mInflator.inflate(R.layout.blogger_section_tabs, null);
            }
            else{}

        }else{
        /*    if (view == null) {*/
                view = mInflator.inflate(R.layout.draft_list_item, null);
                holder = new ViewHolder();
                holder.txvArticleTitle = (TextView) view.findViewById(R.id.txvArticleTitle);
                holder.txvAuthorName = (TextView) view.findViewById(R.id.txvAuthorName);
                holder.txvUpdateDate = (TextView) view.findViewById(R.id.txvPublishDate);
                holder.popupButton = view.findViewById(R.id.img_menu);
                holder.txvUnapproved = (TextView) view.findViewById(R.id.unapproved);
                holder.popupButton.setTag(getItem(position));

                //    popupButton.setOnClickListener((DraftListViewActivity)context);
                holder.popupButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final PopupMenu popup = new PopupMenu(context, holder.popupButton);
                        popup.getMenuInflater().inflate(R.menu.pop_menu_draft, popup.getMenu());
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {
                                int i = item.getItemId();
                                if (i == R.id.edit) {
                                    Intent intent = new Intent(context, EditorPostActivity.class);
                                    intent.putExtra("draftItem", (PublishDraftObject) getItem(position));
                                    intent.putExtra("from", "draftList");
                                    context.startActivity(intent);
                                    Log.e("edit", "clicked");
                                    //do something
                                    return true;
                                } else if (i == R.id.delete) {
                                    //do something
                                    ((DraftListViewActivity) context).deleteDraftAPI((PublishDraftObject) getItem(position), position);
                                    Log.e("delete", "clicked");
                                    return true;
                                } else {
                                    return onMenuItemClick(item);
                                }
                            }

                        });
                        popup.show();
                    }
                });

                view.setTag(holder);
          /*  }
            else*/{
                if (!draftlist.get(position).getTitle().toString().isEmpty()) {
                    holder.txvArticleTitle.setText(draftlist.get(position).getTitle());
                } else {
                    holder.txvArticleTitle.setText("Untitled Draft");
                }
                if (draftlist.get(position).getArticleType() == null) {
                    holder.txvUnapproved.setVisibility(View.INVISIBLE);
                    view.setBackgroundColor(Color.WHITE);
                    view.setClickable(false);
                    holder.popupButton.setClickable(true);
                    holder.txvArticleTitle.setTextColor(Color.BLACK);
                    holder.txvUpdateDate.setTextColor(context.getResources().getColor(R.color.gray2));
                } else {
                    switch (draftlist.get(position).getArticleType()) {
                        case "0": {
                            holder.txvUnapproved.setVisibility(View.INVISIBLE);
                            view.setBackgroundColor(Color.WHITE);
                            view.setClickable(false);
                            holder.popupButton.setClickable(true);
                            holder.txvArticleTitle.setTextColor(Color.BLACK);
                            holder.txvUpdateDate.setTextColor(context.getResources().getColor(R.color.gray2));
                            break;
                        }
                        case "1": {
                            holder.txvUnapproved.setVisibility(View.VISIBLE);
                            holder.txvUnapproved.setText("Under Moderation");
                            view.setBackgroundColor(context.getResources().getColor(R.color.gray_color));
                            holder.txvArticleTitle.setTextColor(context.getResources().getColor(R.color.faded_text));
                            holder.txvUpdateDate.setTextColor(context.getResources().getColor(R.color.faded_italic));
                            view.setClickable(true);
                            holder.popupButton.setClickable(false);
                            break;
                        }
                        case "2": {
                            holder.txvUnapproved.setVisibility(View.VISIBLE);
                            view.setBackgroundColor(Color.WHITE);
                            view.setClickable(false);
                            holder.popupButton.setClickable(true);
                            holder.txvArticleTitle.setTextColor(Color.BLACK);
                            holder.txvUpdateDate.setTextColor(context.getResources().getColor(R.color.gray2));
                            break;
                        }
                        case "3": {
                            holder.txvUnapproved.setVisibility(View.VISIBLE);
                            view.setBackgroundColor(Color.WHITE);
                            view.setClickable(false);
                            holder.popupButton.setClickable(true);
                            holder.txvArticleTitle.setTextColor(Color.BLACK);
                            holder.txvUpdateDate.setTextColor(context.getResources().getColor(R.color.gray2));
                            break;
                        }
                        default:
                            holder.txvUnapproved.setVisibility(View.INVISIBLE);
                            view.setBackgroundColor(Color.WHITE);
                            view.setClickable(false);
                            holder.popupButton.setClickable(true);
                            holder.txvArticleTitle.setTextColor(Color.BLACK);
                            holder.txvUpdateDate.setTextColor(context.getResources().getColor(R.color.gray2));
                            break;
                    }
                }

                try {

                    Calendar calendar1 = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                    SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm", Locale.US);
                    //    calendar1.setTime(sdf.parse(draftlist.get(position).getUpdatedTime()));
                    //   calendar1.add(Calendar.MILLISECOND, tz.getOffset(calendar1.getTimeInMillis()));
                    calendar1.setTimeInMillis(draftlist.get(position).getUpdatedTime()* 1000);

                    Long diff=    System.currentTimeMillis()-draftlist.get(position).getUpdatedTime()*1000;
                    if (diff/(1000 * 60 * 60)>24&&!sdf.format(System.currentTimeMillis()).equals(sdf.format((draftlist.get(position).getUpdatedTime()* 1000))))
                    {  holder.txvUpdateDate.setText(sdf.format(calendar1.getTime()));}
                    else {
                        holder.txvUpdateDate.setText(sdf1.format(calendar1.getTime()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }}
            }



        return view;
    }


    @Override
    public boolean isItemViewTypePinned(int viewType) {
        if (viewType==0)
        {
            return true;
        }
        else
        return false;
    }

    @Override
    public int getItemViewType(int position) {
       return draftlist.get(position).getItemType();

    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    class ViewHolder {
        TextView txvArticleTitle;
        TextView txvAuthorName;
        ImageView menu;
        TextView txvUpdateDate;
        TextView txvUnapproved;
        View popupButton;

    }
}
