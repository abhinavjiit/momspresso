package com.mycity4kids.editor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.models.editor.ArticleDraftList;

import java.util.ArrayList;

/**
 * Created by anshul on 3/16/16.
 */
public class DraftListAdapter extends BaseAdapter {
    Context context;
    ArrayList<ArticleDraftList> draftlist;
    private LayoutInflater mInflator;
    DraftListView draftListView;

    DraftListAdapter(Context context, ArrayList<ArticleDraftList> draftlist)
    {
        this.context=context;
        this.draftlist=draftlist;
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

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            view = mInflator.inflate(R.layout.draft_list_item, null);
            holder = new ViewHolder();
            holder.txvArticleTitle = (TextView) view.findViewById(R.id.txvArticleTitle);
            holder.txvAuthorName = (TextView) view.findViewById(R.id.txvAuthorName);
            holder.txvUpdateDate=(TextView) view.findViewById(R.id.txvPublishDate);
            final View popupButton = view.findViewById(R.id.img_menu);
            popupButton.setTag(getItem(position));

        //    popupButton.setOnClickListener((DraftListView)context);
            popupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final PopupMenu popup = new PopupMenu(context, popupButton);
                    popup.getMenuInflater().inflate(R.menu.pop_menu_draft, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            int i = item.getItemId();
                            if (i == R.id.edit) {
                                Intent intent=new Intent(context,EditorPostActivity.class);
                                intent.putExtra("draftItem",(ArticleDraftList)getItem(position));
                                intent.putExtra("from","draftList");
                                context.startActivity(intent);
                                Log.e("edit", "clicked");
                                //do something
                                return true;
                            } else if (i == R.id.delete) {
                                //do something
                                ((DraftListView) context).deleteDraftAPI((ArticleDraftList)getItem(position),position);
                                Log.e("delete", "clicked");
                                return true;
                            } else {
                                return onMenuItemClick(item);
                            }
                        }

                    });
                    popup.show();
                }});

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if (!draftlist.get(position).getTitle().toString().isEmpty())
        {holder.txvArticleTitle.setText(draftlist.get(position).getTitle());}
        else {
            holder.txvArticleTitle.setText("Untitled Draft");
        }
        holder.txvUpdateDate.setText(draftlist.get(position).getUpdatedDate());
        return view;
    }

    public int getPosition(){
        return  1;
    }

    class ViewHolder {
        TextView txvArticleTitle;
        TextView txvAuthorName;
        ImageView menu;
        TextView txvUpdateDate;

    }
}
