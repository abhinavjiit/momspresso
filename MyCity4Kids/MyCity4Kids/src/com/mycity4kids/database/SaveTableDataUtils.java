package com.mycity4kids.database;

import java.util.ArrayList;

import android.content.Context;

import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.dbtable.ActivititiesTable;
import com.mycity4kids.dbtable.AdvancedSearchTable;
import com.mycity4kids.dbtable.AgeGroupTable;
import com.mycity4kids.dbtable.CategoryListTable;
import com.mycity4kids.dbtable.CityTable;
import com.mycity4kids.dbtable.DateTable;
import com.mycity4kids.dbtable.FilterTable;
import com.mycity4kids.dbtable.LocalityTable;
import com.mycity4kids.dbtable.SortByTable;
import com.mycity4kids.dbtable.SubCategoryTable;
import com.mycity4kids.models.category.Activities;
import com.mycity4kids.models.category.AdvancedSearch;
import com.mycity4kids.models.category.AgeGroup;
import com.mycity4kids.models.category.CategoryModel;
import com.mycity4kids.models.category.DateValue;
import com.mycity4kids.models.category.MainFilters;
import com.mycity4kids.models.category.SortBy;
import com.mycity4kids.models.category.SubCategories;
import com.mycity4kids.models.category.SubCategory;
import com.mycity4kids.models.city.MetroCity;
import com.mycity4kids.models.locality.Localities;
/**
 * 
 * @author Deepanker Chaudhary
 *
 */
public class SaveTableDataUtils {
	private Context mContext;
	private CategoryListTable mCategoryListTable;
	private SubCategoryTable mSubCategoryTable; 
	private AdvancedSearchTable maAdvancedSearchTable;
	private FilterTable mFilterTable;
	private SortByTable mSortByTable;
	private LocalityTable mlLocationTable;
	private CityTable mCityTable;
	private AgeGroupTable mAgeGroupTable;
    private ActivititiesTable mActivititiesTable;
    private DateTable mDateTable;

	public SaveTableDataUtils(Context pContext) {
		mContext=pContext;
		mCategoryListTable=new CategoryListTable((BaseApplication)mContext.getApplicationContext());
		mSubCategoryTable=new SubCategoryTable((BaseApplication)mContext.getApplicationContext());
		maAdvancedSearchTable=new AdvancedSearchTable((BaseApplication)mContext.getApplicationContext());
		mFilterTable=new FilterTable((BaseApplication)mContext.getApplicationContext());
		mSortByTable=new SortByTable((BaseApplication)mContext.getApplicationContext());
		mlLocationTable=new LocalityTable((BaseApplication)mContext.getApplicationContext());
		mCityTable=new CityTable((BaseApplication)mContext.getApplicationContext());
		mAgeGroupTable=new AgeGroupTable((BaseApplication)mContext.getApplicationContext());
        mActivititiesTable=new ActivititiesTable((BaseApplication)mContext.getApplicationContext());
        mDateTable=new DateTable((BaseApplication)mContext.getApplicationContext());

	}

	public void saveCategoryData(CategoryModel pCategoryModel){
		try {
			mCategoryListTable.beginTransaction();
			mCategoryListTable.insertWithOnConflict(pCategoryModel);
			mCategoryListTable.setTransactionSuccessful();
		} 
		finally{
			mCategoryListTable.endTransaction();
		}

	}
	/**
	 * this table save sub category data with the category id 
	 * category Id will work as a forgen key - Deepanker Chaudhary
	 * @param pSubCategoriesList
	 * @param CategoryId
	 */
	public void saveSubCategoryData(ArrayList<SubCategories> pSubCategoriesList,int pCategoryId){

		try {
			mSubCategoryTable.beginTransaction();
			for(SubCategories categoriesList:pSubCategoriesList){
				SubCategory subCategory = categoriesList.getSubCategory();
				subCategory.setCategoryId(pCategoryId);
				mSubCategoryTable.insertWithOnConflict(subCategory);

			}
			mSubCategoryTable.setTransactionSuccessful();
		} 
		finally{
			mSubCategoryTable.endTransaction();
		}

	}
	/**
	 * save advanced search list in database with category id
	 * @param pAdvancedSearchList
	 * @param pCategoryId
	 */
	public void saveAdvancedSearchData(ArrayList<AdvancedSearch> pAdvancedSearchList,int pCategoryId){
		try {
			
			maAdvancedSearchTable.beginTransaction();
			for(AdvancedSearch advancedSearchList:pAdvancedSearchList){
				advancedSearchList.setCategoryId(pCategoryId);
				maAdvancedSearchTable.insertWithOnConflict(advancedSearchList);

			}
			maAdvancedSearchTable.setTransactionSuccessful();

		} 
		finally{
			maAdvancedSearchTable.endTransaction();
		}

	}

	/**
	 * save filter list data in FilterTable
	 * @param pFilterList
	 * @param pCategoryId
	 */

	public void saveFilterData(ArrayList<MainFilters> pFilterList,int pCategoryId){
		try {
			mFilterTable.beginTransaction();
			for(MainFilters filterModel:pFilterList){
				filterModel.setCategoryId(pCategoryId);
				mFilterTable.insertWithOnConflict(filterModel);

			}
			mFilterTable.setTransactionSuccessful();
		} 
		finally{
			mFilterTable.endTransaction();
		}

	}
	/**
	 * Save sortBy list data in SortByTable
	 * @param pSortBy
	 * @param pCategoryId
	 */
	public void saveSoryByData(ArrayList<SortBy> pSortBy,int pCategoryId){
		try {
			mSortByTable.beginTransaction();
			for(SortBy sortByModel:pSortBy){
				sortByModel.setCategoryId(pCategoryId);
				mSortByTable.insertWithOnConflict(sortByModel);

			}	
			mSortByTable.setTransactionSuccessful();

		} 
		finally{
			mSortByTable.endTransaction();
		}

	}
	/**
	 * Save Location data in LocalityTable
	 * @param localitiesCityList
	 * @param zoneId
	 * @param zoneName
	 */
	public void saveLocationTableData(ArrayList<Localities> localitiesCityList,int zoneId,String zoneName){
		try {
			mlLocationTable.beginTransaction();
			if(localitiesCityList!=null){
				for(Localities localitiesModel:localitiesCityList){
					localitiesModel.setZoneId(zoneId);
					localitiesModel.setZoneCity(zoneName);
					mlLocationTable.insertWithOnConflict(localitiesModel);

				}
			}else{
				
				Localities _model=new Localities();
				_model.setId(zoneId);
				_model.setName(zoneName);
				_model.setZoneId(zoneId);
				_model.setZoneCity(zoneName);
				mlLocationTable.insertWithOnConflict(_model);
			}
			mlLocationTable.setTransactionSuccessful();

		} 
		finally{
			mlLocationTable.endTransaction();
		}


	}

	/**
	 * Save City  Data in CityTable
	 * @param cityModel
	 */
	public void saveCityTableData(MetroCity cityModel){
		
		try {
			mCityTable.beginTransaction();
			mCityTable.insertWithOnConflict(cityModel);
			mCityTable.setTransactionSuccessful();
		} 
		finally{
			mCityTable.endTransaction();
		}



	}
	
	/**
	 * save advanced search list in database with category id
	 * @param pAdvancedSearchList
	 * @param pCategoryId
	 */
	public void saveAgeGroupData(ArrayList<AgeGroup> pAgeGroupList,int pCategoryId){
		try {
			
			mAgeGroupTable.beginTransaction();
			for(AgeGroup ageGroupModel:pAgeGroupList){
				ageGroupModel.setCategoryId(pCategoryId);
				
				mAgeGroupTable.insertWithOnConflict(ageGroupModel);

			}
			mAgeGroupTable.setTransactionSuccessful();

		} 
		finally{
			mAgeGroupTable.endTransaction();
		}

	}
	
	
	public void saveActivities(ArrayList<Activities> pActivitiesList,int pCategoryId){
		try {
			
			mActivititiesTable.beginTransaction();
			for(Activities activitymodel:pActivitiesList){
				activitymodel.setCategoryId(pCategoryId);
				
				mActivititiesTable.insertWithOnConflict(activitymodel);

			}
			mActivititiesTable.setTransactionSuccessful();

		} 
		finally{
			mActivititiesTable.endTransaction();
		}

	}
	
	public void saveDateValue(ArrayList<DateValue> pDateList,int pCategoryId){
		try {
			
			mDateTable.beginTransaction();
			for(DateValue dateModel:pDateList){
				dateModel.setCategoryId(pCategoryId);
				
				mDateTable.insertWithOnConflict(dateModel);

			}
			mDateTable.setTransactionSuccessful();

		} 
		finally{
			mDateTable.endTransaction();
		}

	}

	/**
	 * delete all category tables : this will be use when there is change in version then we delete all data & insert again.
	 */

	public void deleteAllCategoryTable(){
		mCategoryListTable.deleteAll();
		mSubCategoryTable.deleteAll();
		maAdvancedSearchTable.deleteAll();
		mFilterTable.deleteAll();
		mSortByTable.deleteAll();
		mAgeGroupTable.deleteAll();
	    mDateTable.deleteAll();
	    mActivititiesTable.deleteAll();
	}
	/**
	 * delete Locality table:
	 */
	public void deleteLocalityTable(){
		mlLocationTable.deleteAll();
	}
	/**
	 * delete city table :
	 */

	public void deleteCityTable(){
		mCityTable.deleteAll();
	}
  
}
