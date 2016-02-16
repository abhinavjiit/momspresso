package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.enums.ParentingFilterType;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.category.CategoryModel;
import com.mycity4kids.models.category.GroupCategoryModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.BusinessListActivityKidsResources;
import com.mycity4kids.ui.activity.ParentingArticlesActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class CategoryListAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private ArrayList<GroupCategoryModel> mCategoryList;
    private HashMap<GroupCategoryModel, ArrayList<CategoryModel>> mSubCategoryList;
    private ArrayList<CategoryModel> childList;
    private LayoutInflater mInflater;

    public CategoryListAdapter(Context pContext, ArrayList<GroupCategoryModel> pCategoryList, HashMap<GroupCategoryModel, ArrayList<CategoryModel>> pChildArray) {
        mContext = pContext;
        mCategoryList = pCategoryList;
        mSubCategoryList = pChildArray;
        mInflater = LayoutInflater.from(pContext);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        CategoryModel subCategoryChile = mSubCategoryList.get(this.mCategoryList.get(groupPosition)).get(childPosition);

        return subCategoryChile;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        //	childList=mSubCategoryList.get(this.mCategoryList.get(groupPosition));
        final CategoryModel subCategory = (CategoryModel) getChild(groupPosition, childPosition);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.custom_child_cell, null);
        }
        RelativeLayout childParentLout = (RelativeLayout) convertView.findViewById(R.id.childLayout);


        TextView subCategoryName = (TextView) convertView.findViewById(R.id.sub_category_txt);
        ImageView _childImg = (ImageView) convertView.findViewById(R.id.sub_category_img);
        subCategoryName.setText(subCategory.getCategoryName());
        if (getImgResourceId("gc_" + subCategory.getCategoryName()) > 0) {
            _childImg.setImageResource(getImgResourceId("gc_" + subCategory.getCategoryName()));
        } else {
            _childImg.setImageResource(R.drawable.gc_birthdays);
        }

        childParentLout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                activityStartAccordingToChildCategory(subCategory);
                /*Intent intent = new Intent(mContext, BusinessListActivity.class);
				intent.putExtra(Constants.EXTRA_CATEGORY_ID, subCategory.getCategoryId());
				mContext.startActivity(intent);*/

            }
        });

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // TODO Auto-generated method stub
        return mSubCategoryList.get(this.mCategoryList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mCategoryList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return mCategoryList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final GroupCategoryModel _categoryData = (GroupCategoryModel) getGroup(groupPosition);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.custom_group_cell, null);
        }
        if (getChildrenCount(groupPosition) == 0) {
            ((RelativeLayout) convertView.findViewById(R.id.group_lout)).setVisibility(View.GONE);
            ((RelativeLayout) convertView.findViewById(R.id.child_lout)).setVisibility(View.VISIBLE);
            TextView _childTxt = (TextView) convertView.findViewById(R.id.without_child_txt);
            ImageView _childImg = (ImageView) convertView.findViewById(R.id.child_img);
            _childTxt.setText(_categoryData.getCategoryGroup());

            if (getImgResourceId("gc_" + _categoryData.getCategoryGroup()) > 0) {
                _childImg.setImageResource(getImgResourceId("gc_" + _categoryData.getCategoryGroup()));
            } else {
                _childImg.setImageResource(R.drawable.gc_birthdays);
            }

            ((RelativeLayout) convertView.findViewById(R.id.child_lout)).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    activityAccordingToCategoty(_categoryData);
					/*Intent intent = new Intent(mContext, BusinessListActivity.class);
					intent.putExtra(Constants.EXTRA_CATEGORY_ID, _categoryData.getCategoryId());
					mContext.startActivity(intent);*/
                    //	((HomeCategoryActivity) mContext).hitBusinessListingApi(_categoryData.getMainCategoryId());
                }
            });
            ((ImageView) convertView.findViewById(R.id.plus_minus_img)).setVisibility(View.GONE);
        } else {
            ((ImageView) convertView.findViewById(R.id.plus_minus_img)).setVisibility(View.VISIBLE);
            ((RelativeLayout) convertView.findViewById(R.id.group_lout)).setVisibility(View.VISIBLE);
            ((RelativeLayout) convertView.findViewById(R.id.child_lout)).setVisibility(View.GONE);

            ((RelativeLayout) convertView.findViewById(R.id.child_lout)).setOnClickListener(null);


            if (isExpanded) {
                ((ImageView) convertView.findViewById(R.id.plus_minus_img)).setImageResource(R.drawable.uparrow);
            } else {
                ((ImageView) convertView.findViewById(R.id.plus_minus_img)).setImageResource(R.drawable.downarrow);
            }

            TextView categoryName = (TextView) convertView.findViewById(R.id.category_txt);
            categoryName.setText(_categoryData.getCategoryGroup());
            ImageView _groupImg = (ImageView) convertView.findViewById(R.id.group_img);
//			CheckedTextView checkImg=(CheckedTextView)convertView.findViewById(R.id.plus_minus_img);
//			checkImg.setChecked(isExpanded);

            if (getImgResourceId("gc_" + _categoryData.getCategoryGroup()) > 0) {
                _groupImg.setImageResource(getImgResourceId("gc_" + _categoryData.getCategoryGroup()));
            } else {
                _groupImg.setImageResource(R.drawable.gc_birthdays);
            }
        }


		/*if(childList.size()==0 || childList.isEmpty()){
			 ((LinearLayout)convertView.findViewById(R.id.lout)).setVisibility(View.VISIBLE);
			 ((CheckedTextView) convertView.findViewById(R.id.textView1)).setVisibility(View.GONE);
			}else{
				 ((LinearLayout)convertView.findViewById(R.id.lout)).setVisibility(View.GONE);
				 ((CheckedTextView) convertView.findViewById(R.id.textView1)).setVisibility(View.VISIBLE);
			}*/


        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return true;
    }


    private int getImgResourceId(String iconName) {
        if (iconName.contains("&")) {
            //String iconName1=iconName.replace("\\s", "_");
            iconName = iconName.trim().replaceAll("[\\s]", "");
            iconName = iconName.replaceAll("[\\W]", "_");
        } else if (iconName.contains(" ")) {
            if (iconName.contains("-")) {
                iconName = iconName.trim().replaceAll("[\\W]", "_");
            }
            iconName = iconName.trim().replaceAll("[\\s]", "_");
        }
        String icon = iconName.toLowerCase();
        int resId = 0;
        try {
            resId = mContext.getResources().getIdentifier(icon, "drawable", mContext.getPackageName());
        } catch (Exception e) {
            return resId;
        }
        return resId;
    }

    private void activityAccordingToCategoty(GroupCategoryModel _categoryData) {
        if (_categoryData.getCategoryGroup().contains("Parenting") || _categoryData.getCategoryGroup().equalsIgnoreCase("Parenting")) {
            Intent intent = new Intent(mContext, ParentingArticlesActivity.class);
            mContext.startActivity(intent);

        }/*else if(_categoryData.getCategoryGroup().contains("Shop")||_categoryData.getCategoryGroup().equalsIgnoreCase("Where to Shop")){
			//mContext.startActivity(new Intent(mContext,ParentingArticlesActivity.class));
		}*//*else if(_categoryData.getCategoryGroup().contains("Health")||_categoryData.getCategoryGroup().equalsIgnoreCase("Health and Wellness")){
			//mContext.startActivity(new Intent(mContext,ParentingArticlesActivity.class));
		}*//*else if(_categoryData.getCategoryGroup().contains("Fun")||_categoryData.getCategoryGroup().equalsIgnoreCase("Fun Places to Go")){
			//mContext.startActivity(new Intent(mContext,ParentingArticlesActivity.class));
		}*//*else if(_categoryData.getCategoryGroup().contains("Events")||_categoryData.getCategoryGroup().equalsIgnoreCase("Events & Workshops")){
			//mContext.startActivity(new Intent(mContext,ParentingArticlesActivity.class));
		}*//*else if(_categoryData.getCategoryGroup().contains("Daycare")||_categoryData.getCategoryGroup().equalsIgnoreCase("Daycare")){
			//mContext.startActivity(new Intent(mContext,ParentingArticlesActivity.class));
		}*//*else if(_categoryData.getCategoryGroup().contains("Birthdays")||_categoryData.getCategoryGroup().equalsIgnoreCase("Birthdays")){
			//mContext.startActivity(new Intent(mContext,ParentingArticlesActivity.class));
		}*/ else if (_categoryData.getCategoryGroup().contains("Events") || _categoryData.getCategoryGroup().equalsIgnoreCase("Events & Workshops")) {
            Intent intent = new Intent(mContext, BusinessListActivityKidsResources.class);
            intent.putExtra(Constants.PAGE_TYPE, Constants.EVENT_PAGE_TYPE);
            intent.putExtra(Constants.EXTRA_CATEGORY_ID, _categoryData.getCategoryId());
            intent.putExtra(Constants.CATEGOTY_NAME, _categoryData.getCategoryGroup());
            mContext.startActivity(intent);
        } else {
            Intent intent = new Intent(mContext, BusinessListActivityKidsResources.class);
            intent.putExtra(Constants.EXTRA_CATEGORY_ID, _categoryData.getCategoryId());
            intent.putExtra(Constants.PAGE_TYPE, Constants.BUSINESS_PAGE_TYPE);
            intent.putExtra(Constants.CATEGOTY_NAME, _categoryData.getCategoryGroup());
            switch (_categoryData.getCategoryId())
            {
                case 7:
                    Log.e("Category", _categoryData.getCategoryGroup());
                    Utils.pushEvent(mContext, GTMEventType.FUNPLACES_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(mContext).getId()+"", "");
                    break;
                case 8:
                    Log.e("Category", _categoryData.getCategoryGroup());
                    Utils.pushEvent(mContext, GTMEventType.BIRTHDAY_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(mContext).getId()+"", "");
                    break;
                case 9:
                    Log.e("Category", _categoryData.getCategoryGroup());
                    Utils.pushEvent(mContext, GTMEventType.WHERETOSHOP_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(mContext).getId()+"", "");
                    break;
                case 2:
                    Log.e("Category", _categoryData.getCategoryGroup());
                    Utils.pushEvent(mContext, GTMEventType.DAYCARE_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(mContext).getId()+"", "");
                    break;
                case 55:
                    Log.e("Category", _categoryData.getCategoryGroup());
                    Utils.pushEvent(mContext, GTMEventType.HEALTH_WELLNESS_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(mContext).getId()+"", "");
                    break;

            }
            //Log.e("Category", _categoryData.getCategoryGroup());
            mContext.startActivity(intent);
        }

    }

    private void activityStartAccordingToChildCategory(CategoryModel subCategory) {
        if (subCategory.getCategoryName().contains("Articles") || subCategory.getCategoryName().equalsIgnoreCase("Articles")) {
            Intent intent = new Intent(mContext, ParentingArticlesActivity.class);
            intent.putExtra(Constants.PARENTING_TYPE, ParentingFilterType.ARTICLES);
            Log.e("Category Name", subCategory.getCategoryName());
            mContext.startActivity(intent);

        } else if (subCategory.getCategoryName().contains("Blogs") || subCategory.getCategoryName().equalsIgnoreCase("Blogs")) {
            Intent intent = new Intent(mContext, ParentingArticlesActivity.class);
            intent.putExtra(Constants.PARENTING_TYPE, ParentingFilterType.BLOGS);
            mContext.startActivity(intent);
        } else {
            Intent intent = new Intent(mContext, BusinessListActivityKidsResources.class);
            intent.putExtra(Constants.EXTRA_CATEGORY_ID, subCategory.getCategoryId());
            intent.putExtra(Constants.PAGE_TYPE, Constants.BUSINESS_PAGE_TYPE);
            intent.putExtra(Constants.CATEGOTY_NAME, subCategory.getCategoryName());
            switch (subCategory.getCategoryId())
            {
                case 4:
                    Log.e("Category", subCategory.getCategoryName());
                    Utils.pushEvent(mContext, GTMEventType.HOBBIES_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(mContext).getId()+"", "");
                    break;
                case 5:
                    Log.e("Category", subCategory.getCategoryName());
                    Utils.pushEvent(mContext, GTMEventType.SPORTS_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(mContext).getId()+"", "");
                    break;
                case 57:
                    Log.e("Category", subCategory.getCategoryName());
                    Utils.pushEvent(mContext, GTMEventType.ENHANCED_LEARNING_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(mContext).getId()+"", "");
                    break;
                case 3:
                    Log.e("Category",subCategory.getCategoryName());
                    Utils.pushEvent(mContext, GTMEventType.TUTIONS_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(mContext).getId()+"", "");
                    break;
                case 248:
                    Log.e("Category", subCategory.getCategoryName());
                    Utils.pushEvent(mContext, GTMEventType.PLAY_SCHOOLS_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(mContext).getId()+"", "");
                    break;
                case 53:
                    Log.e("Category", subCategory.getCategoryName());
                    Utils.pushEvent(mContext, GTMEventType.SCHOOLS_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(mContext).getId()+"", "");
                    break;

            }
            Log.e("Category Name",subCategory.getCategoryName());
            mContext.startActivity(intent);
        }

    }


}
