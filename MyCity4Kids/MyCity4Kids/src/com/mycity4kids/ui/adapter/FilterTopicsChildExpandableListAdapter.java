package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.models.Topics;

import java.util.HashMap;
import java.util.List;

/**
 * Created by hemant on 31/5/16.
 */
public class FilterTopicsChildExpandableListAdapter extends BaseExpandableListAdapter {

    private final LayoutInflater mInflator;
    private final Context mContext;
    private List<Topics> mSubCategoriesList;
    private HashMap<Topics, List<Topics>> mSubCategoriesChildMap;

    public FilterTopicsChildExpandableListAdapter(Context context) {
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
        boolean areAllCheckedFlag = true;
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
        holder.childChkBox.setVisibility(View.GONE);
        final Topics subCategoryItem = (Topics) getGroup(groupPosition);
        if (groupPosition == 0) {
            int count = mSubCategoriesList.size() - 1;
//            holder.txvZoneName.setText(subCategoryItem.getTitle() + " (" + count + ")");
            holder.txvZoneName.setText(subCategoryItem.getTitle().toUpperCase());
            holder.groupCheckedTxv.setVisibility(View.GONE);
            holder.childChkBox.setVisibility(View.GONE);
//            for (int i = 1; i < mSubCategoriesList.size(); i++) {
//                if (!mSubCategoriesList.get(i).isSelected() || !areAllChecked(mSubCategoriesList.get(i))) {
//                    areAllCheckedFlag = false;
//                    break;
//                }
//            }
//            if (areAllCheckedFlag) {
//                holder.childChkBox.setChecked(true);
//            } else {
//                holder.childChkBox.setChecked(false);
//            }
//
//            holder.childChkBox.setTag(new Positions(groupPosition, 0));
//            holder.childChkBox.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    handleSubCategoryCheckBoxClick((CheckBox) v);
//                }
//            });

        } else {
            if (mSubCategoriesChildMap.get(subCategoryItem).size() == 0) {
                int count = mSubCategoriesChildMap.get(subCategoryItem).size();
//                holder.txvZoneName.setText(subCategoryItem.getTitle() + " (" + count + ")");
                holder.txvZoneName.setText(subCategoryItem.getTitle().toUpperCase());
                holder.groupCheckedTxv.setVisibility(View.GONE);
                holder.childChkBox.setVisibility(View.GONE);
//                holder.childChkBox.setChecked(subCategoryItem.isSelected());
//                holder.childChkBox.setTag(new Positions(groupPosition, 0));
//                holder.childChkBox.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        handleSubCategoryCheckBoxClick((CheckBox) v);
//                    }
//                });
            } else {
                int count = mSubCategoriesChildMap.get(subCategoryItem).size() - 1;
//                holder.txvZoneName.setText(subCategoryItem.getTitle() + " (" + count + ")");
                holder.txvZoneName.setText(subCategoryItem.getTitle().toUpperCase());
                holder.groupCheckedTxv.setVisibility(View.VISIBLE);
                holder.childChkBox.setVisibility(View.GONE);
                if (count == 0) {
                    holder.groupCheckedTxv.setImageResource(0);
                } else {
                    if (isExpanded) {
                        holder.groupCheckedTxv.setImageResource(R.drawable.uparrow);
                    } else {
                        holder.groupCheckedTxv.setImageResource(R.drawable.downarrow);
                    }
                }
            }
        }

        return convertView;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public int getGroupCount() {
        return mSubCategoriesList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mSubCategoriesList.get(groupPosition);
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

        if (childPosition != 0)
            holder.txtLocalityName.setText(localitySubCategoryChild.getTitle().toUpperCase());
        else {
            int count = mSubCategoriesChildMap.get((Topics) getGroup(groupPosition)).size() - 1;
//            holder.txtLocalityName.setText(localitySubCategoryChild.getTitle() + " (" + count + ")");
            holder.txtLocalityName.setText(localitySubCategoryChild.getTitle().toUpperCase());
        }
        holder.childChkBox.setVisibility(View.GONE);

//        holder.childChkBox.setChecked(localitySubCategoryChild.isSelected());
//        holder.childChkBox.setTag(new Positions(groupPosition, childPosition));
//
//        holder.childChkBox.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                handleChildCheckboxClick((CheckBox) v);
//            }
//        });
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mSubCategoriesChildMap.get(this.mSubCategoriesList.get(groupPosition)).size();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        Topics localityChild = mSubCategoriesChildMap.get(this.mSubCategoriesList.get(groupPosition)).get(childPosition);
        return localityChild;
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
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private void handleSubCategoryCheckBoxClick(CheckBox pChildCheckBox) {
        boolean isChecked = pChildCheckBox.isChecked();
        int groupPos = ((Positions) pChildCheckBox.getTag()).groupPosition;
//        int childPos = ((Positions) pChildCheckBox.getTag()).childPosition;
        boolean areAllCheckedFlag = true;
        Topics subCategoryItem = (Topics) getGroup(groupPos);
        /**
         * select & unselect for all checkbox;
         */
        if (groupPos == 0) {
            subCategoryItem.setIsSelected(isChecked);
            setSubCategoryAndChildrenState(groupPos, subCategoryItem, isChecked);
        }
        if (isChecked) {
            subCategoryItem.setIsSelected(isChecked);
            for (int i = 1; i < mSubCategoriesList.size(); i++) {
                if (!areAllChecked(mSubCategoriesList.get(i))) {
                    areAllCheckedFlag = false;
                    break;
                }
            }
            if (areAllCheckedFlag) {
                Topics allSubCategory = (Topics) getGroup(0);
                allSubCategory.setIsSelected(isChecked);
            }
        } else {
            Topics allSubCategory = (Topics) getGroup(0);
            allSubCategory.setIsSelected(isChecked);
            for (int i = 1; i < mSubCategoriesList.size(); i++) {
                if (isNoneChecked(mSubCategoriesList.get(i))) {
                    mSubCategoriesList.get(i).setIsSelected(isChecked);
                }
            }
        }
        notifyDataSetChanged();
    }

    private void handleChildCheckboxClick(CheckBox pChildCheckBox) {
        boolean isChecked = pChildCheckBox.isChecked();
        int groupPos = ((Positions) pChildCheckBox.getTag()).groupPosition;
        int childPos = ((Positions) pChildCheckBox.getTag()).childPosition;

        Topics zoneModel = (Topics) getGroup(groupPos);
        /**
         * select & unselect for all checkbox;
         */
        if (childPos == 0) {
            zoneModel.setIsSelected(isChecked);
            setChildrenState(zoneModel, isChecked);
        } else {
            Topics localitySubCategoryChild = (Topics) getChild(groupPos, childPos);
            localitySubCategoryChild.setIsSelected(isChecked);
        }
        if (isChecked) {
            zoneModel.setIsSelected(isChecked);

            if (childPos != 0 && areAllChecked(zoneModel)) {
                Topics allSubCategoryChild = (Topics) getChild(groupPos, 0);
                allSubCategoryChild.setIsSelected(isChecked);
                zoneModel.setIsSelected(true);
//                checkAllSubcategoriesAndChildSelected();
            }
        } else {
            zoneModel.setIsSelected(false);
            Topics allSubCategoryChild = (Topics) getChild(groupPos, 0);
            allSubCategoryChild.setIsSelected(isChecked);
            if (childPos != 0 && isNoneChecked(zoneModel)) {
                zoneModel.setIsSelected(isChecked);
            }
        }
        notifyDataSetChanged();
    }

    private void setSubCategoryAndChildrenState(int gPosition, Topics subCategoryItem, boolean isChecked) {
        if (gPosition == 0) {
            for (int i = 0; i < mSubCategoriesList.size(); i++) {
                mSubCategoriesList.get(i).setIsSelected(isChecked);
                setChildrenState(mSubCategoriesList.get(i), isChecked);
            }
        }
    }

    /**
     * @param zoneModel
     * @param newState
     */
    private void setChildrenState(Topics zoneModel, boolean newState) {
        List<Topics> subCategoryChildren = mSubCategoriesChildMap.get(zoneModel);
        for (int i = 0; i < subCategoryChildren.size(); i++) {
            subCategoryChildren.get(i).setIsSelected(newState);
        }
    }

    /**
     * @param zoneModel
     * @return
     */
    private boolean areAllChecked(Topics zoneModel) {
        List<Topics> subCategoryChildren = mSubCategoriesChildMap.get(zoneModel);
        for (int i = 1; i < subCategoryChildren.size(); i++) {
            if (!subCategoryChildren.get(i).isSelected()) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param zoneModel
     * @return
     */
    private boolean isNoneChecked(Topics zoneModel) {
        List<Topics> subCategoryChildren = mSubCategoriesChildMap.get(zoneModel);
        for (int i = 1; i < subCategoryChildren.size(); i++) {
            if (subCategoryChildren.get(i).isSelected()) {
                return false;
            }
        }
        return true;
    }

    public HashMap<Topics, List<Topics>> getUpdatedMap() {
        return mSubCategoriesChildMap;
    }
}
