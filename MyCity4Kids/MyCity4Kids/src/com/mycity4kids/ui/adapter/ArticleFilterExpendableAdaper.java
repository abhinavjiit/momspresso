package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.dbtable.TableNotes;
import com.mycity4kids.newmodels.parentingmodel.ArticleFilterListModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;

/**
 * Created by manish.soni on 22-07-2015.
 */
public class ArticleFilterExpendableAdaper extends BaseExpandableListAdapter {

    Boolean flag = false;
    Calendar calendar;
    private Context _context;
    private ArrayList<ArticleFilterListModel.FilterTopic> _listDataHeader; // header titles
    // child data in format of header title, child title
    private LinkedHashMap<ArticleFilterListModel.FilterTopic, ArrayList<ArticleFilterListModel.SubFilerList>> _listDataChild;
    TableNotes tableNotes;

    public ArticleFilterExpendableAdaper(Context context, ArrayList<ArticleFilterListModel.FilterTopic> listDataHeader,
                                         LinkedHashMap<ArticleFilterListModel.FilterTopic, ArrayList<ArticleFilterListModel.SubFilerList>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;

    }


    @Override
    public int getGroupCount() {
        return this._listDataHeader == null ? 0 : this._listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        return this._listDataChild.get(this._listDataHeader.get(groupPosition)) == null ? 0 : this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ArticleFilterListModel.FilterTopic headerTitle = (ArticleFilterListModel.FilterTopic) getGroup(groupPosition);

        final GroupViewHolder groupHolder;

        LayoutInflater infalInflater = (LayoutInflater) this._context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = infalInflater.inflate(R.layout.aa_article_filter_header, null);
            groupHolder = new GroupViewHolder();

            groupHolder.header = (TextView) convertView.findViewById(R.id.header);
            groupHolder.icon = (ImageView) convertView.findViewById(R.id.icon);

            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupViewHolder) convertView.getTag();
        }
        groupHolder.header.setText(headerTitle.getName());

        if (headerTitle.getSubcategory() == null) {
            groupHolder.icon.setVisibility(View.GONE);
        } else {
            groupHolder.icon.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    class GroupViewHolder {
        TextView header;
        ImageView icon;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ArticleFilterListModel.SubFilerList filterItems = (ArticleFilterListModel.SubFilerList) getChild(groupPosition, childPosition);

        final ViewHolder holder;

        LayoutInflater infalInflater = (LayoutInflater) this._context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {

            convertView = infalInflater.inflate(R.layout.aa_article_filter_child, null);
            holder = new ViewHolder();

            holder.child = (TextView) convertView.findViewById(R.id.child_text);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.child.setText(filterItems.getName());


        return convertView;
    }

    class ViewHolder {
        TextView child;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


//    public void notifyDataChange(ArrayList<String> headerData, LinkedHashMap<String, ArrayList<AppointmentMappingModel>> childData) {
//
//        this._listDataHeader = headerData;
//        this._listDataChild = childData;
//
//        notifyDataSetChanged();
//
//    }

}
