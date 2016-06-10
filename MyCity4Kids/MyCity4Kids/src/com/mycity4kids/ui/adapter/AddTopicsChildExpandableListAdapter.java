package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mycity4kids.R;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.Topics;

import java.util.HashMap;
import java.util.List;

/**
 * Created by hemant on 31/5/16.
 */
public class AddTopicsChildExpandableListAdapter extends BaseExpandableListAdapter {

    private final LayoutInflater mInflator;
    private final Context mContext;
    private List<Topics> mSubCategoriesList;
    private HashMap<Topics, List<Topics>> mSubCategoriesChildMap;
    private int totalItemsSelected = 0;

    public AddTopicsChildExpandableListAdapter(Context context) {
        mInflator = LayoutInflater.from(context);
        mContext = context;
    }

    /**
     * @param mSubCategoriesList
     * @param mSubCategoriesChildMap
     */
    public void setTopicsData(List<Topics> mSubCategoriesList, HashMap<Topics, List<Topics>> mSubCategoriesChildMap) {
        this.mSubCategoriesList = mSubCategoriesList;
        this.mSubCategoriesChildMap = mSubCategoriesChildMap;
    }

    @Override
    public int getGroupCount() {
        return mSubCategoriesList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mSubCategoriesChildMap.get(this.mSubCategoriesList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mSubCategoriesList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        Topics localityChild = mSubCategoriesChildMap.get(this.mSubCategoriesList.get(groupPosition)).get(childPosition);
        return localityChild;
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

    class ViewHolder {
        TextView txvZoneName;
        TextView txtLocalityName;
        CheckBox childChkBox;
        ImageView groupCheckedTxv;
    }

    class Positions {
        int groupPosition;
        int childPosition;

        Positions(int groupPosition, int childPosition) {
            this.groupPosition = groupPosition;
            this.childPosition = childPosition;
        }
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflator.inflate(R.layout.topics_category_item, null);
            holder.txvZoneName = (TextView) convertView.findViewById(R.id.categoryTopicName);
            holder.groupCheckedTxv = (ImageView) convertView.findViewById(R.id.plus_minus_category);
            holder.childChkBox = (CheckBox) convertView.findViewById(R.id.categoryChkBox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Topics subCategoryItem = (Topics) getGroup(groupPosition);

        if (mSubCategoriesChildMap.get(subCategoryItem).size() == 0) {
            int count = mSubCategoriesChildMap.get(subCategoryItem).size();
            holder.txvZoneName.setText(subCategoryItem.getTitle() + " (" + count + ")");
            holder.groupCheckedTxv.setVisibility(View.GONE);
            holder.childChkBox.setVisibility(View.VISIBLE);
            holder.childChkBox.setChecked(subCategoryItem.isSelected());
            holder.childChkBox.setTag(new Positions(groupPosition, 0));
            holder.childChkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleSubCategoryCheckBoxClick((CheckBox) v);
                }
            });
        } else {
            int count = mSubCategoriesChildMap.get(subCategoryItem).size();
            holder.txvZoneName.setText(subCategoryItem.getTitle() + " (" + count + ")");
            holder.groupCheckedTxv.setVisibility(View.VISIBLE);
            holder.childChkBox.setVisibility(View.GONE);
            if (isExpanded) {
                holder.groupCheckedTxv.setImageResource(R.drawable.uparrow);
            } else {
                holder.groupCheckedTxv.setImageResource(R.drawable.downarrow);
            }
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflator.inflate(R.layout.topics_child_item, null);
            holder.childChkBox = (CheckBox) convertView.findViewById(R.id.childChkBox);
            holder.txtLocalityName = (TextView) convertView.findViewById(R.id.topicChildName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Topics localitySubCategoryChild = (Topics) getChild(groupPosition, childPosition);

        holder.txtLocalityName.setText(localitySubCategoryChild.getTitle());

        holder.childChkBox.setChecked(localitySubCategoryChild.isSelected());
        holder.childChkBox.setTag(new Positions(groupPosition, childPosition));

        holder.childChkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleChildCheckboxClick((CheckBox) v);
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private void handleSubCategoryCheckBoxClick(CheckBox pChildCheckBox) {
        boolean isChecked = pChildCheckBox.isChecked();

        if (isChecked) {
            totalItemsSelected++;
        } else {
            totalItemsSelected--;
        }
        if (totalItemsSelected > Constants.MAX_ARTICLE_CATEGORIES) {
            pChildCheckBox.setChecked(false);
            totalItemsSelected--;
            Toast.makeText(mContext, "maximum applicable categories reached", Toast.LENGTH_SHORT).show();
            return;
        }

        int groupPos = ((Positions) pChildCheckBox.getTag()).groupPosition;
        Topics subCategoryItem = (Topics) getGroup(groupPos);
        subCategoryItem.setIsSelected(isChecked);
    }

    private void handleChildCheckboxClick(CheckBox pChildCheckBox) {
        boolean isChecked = pChildCheckBox.isChecked();
        int groupPos = ((Positions) pChildCheckBox.getTag()).groupPosition;
        int childPos = ((Positions) pChildCheckBox.getTag()).childPosition;

        if (isChecked) {
            totalItemsSelected++;
        } else {
            totalItemsSelected--;
        }
        if (totalItemsSelected > Constants.MAX_ARTICLE_CATEGORIES) {
            pChildCheckBox.setChecked(false);
            totalItemsSelected--;
            Toast.makeText(mContext, "maximum applicable categories reached", Toast.LENGTH_SHORT).show();
            return;
        }

        Topics localitySubCategoryChild = (Topics) getChild(groupPos, childPos);
        localitySubCategoryChild.setIsSelected(isChecked);
    }

    public HashMap<Topics, List<Topics>> getUpdatedMap() {
        return mSubCategoriesChildMap;
    }
}
