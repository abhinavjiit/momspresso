package com.mycity4kids.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.kelltontech.model.BaseModel;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.database.SaveTableDataUtils;
import com.mycity4kids.dbtable.CityTable;
import com.mycity4kids.interfaces.OnUIView;
import com.mycity4kids.models.VersionApiModel;
import com.mycity4kids.models.category.Activities;
import com.mycity4kids.models.category.AdvancedSearch;
import com.mycity4kids.models.category.AgeGroup;
import com.mycity4kids.models.category.CategoryData;
import com.mycity4kids.models.category.CategoryModel;
import com.mycity4kids.models.category.DateValue;
import com.mycity4kids.models.category.GroupCategoryData;
import com.mycity4kids.models.category.MainCategory;
import com.mycity4kids.models.category.MainFilters;
import com.mycity4kids.models.category.SortBy;
import com.mycity4kids.models.category.SubCategories;
import com.mycity4kids.models.city.CityData;
import com.mycity4kids.models.city.MetroCity;
import com.mycity4kids.models.configuration.ConfigurationApiModel;
import com.mycity4kids.models.configuration.ConfigurationData;
import com.mycity4kids.models.locality.Localities;
import com.mycity4kids.models.locality.LocalityData;
import com.mycity4kids.preference.SharedPrefUtils;

import java.util.ArrayList;
/**
 * this asynctask  i am using for save a huge data in sqlite database
 * 
 * @author Deepanker Chaudhary
 *
 */
public class HeavyDbTask extends AsyncTask<Void, Void, String>{
	private BaseModel mBaseModel;
	private Context mContext;
	private OnUIView mOnUIView;


	public HeavyDbTask(Context pContext,BaseModel pBaseModel,OnUIView pOnUiView){
		mBaseModel=pBaseModel;
		mContext=pContext;
		mOnUIView=pOnUiView;
	}



	@Override
	protected String doInBackground(Void... params) {
		if(mBaseModel instanceof ConfigurationApiModel)
		{
			ConfigurationApiModel _configurationResponse=(ConfigurationApiModel)mBaseModel;
			if(_configurationResponse.getResponseCode()==200){
				ConfigurationData _configurationData=_configurationResponse.getResult().getData();
				//				ArrayList<CategoryData> categoryData = _configurationData.getCategoryApi().getData();
				ArrayList<GroupCategoryData> groupCategoryData = _configurationData.getCategoryApi().getData();
				ArrayList<CityData> cityData =_configurationData.getCityApi().getData();
				ArrayList<LocalityData> localityData=_configurationData.getLocalityApi().getData();
				/**
				 * this is app upgrade flag.
				 * which we save in shared preference 
				 * & check on HomeCategoryActivity.deepanker.chaudhary
				 */
//				boolean isAutoUpdateAvailable=_configurationData.getAppVersionApi().getData().isAuto_update();
//                 SharedPrefUtils.setAppUgrade(mContext, isAutoUpdateAvailable);

				/**
				 * this is a utility class in which i have created save & delete method for tables . 
				 */
				SaveTableDataUtils saveTableUtils=new SaveTableDataUtils(mContext);

				/**
				 * current version api model will get from this 
				 */
				VersionApiModel _versionModel=SharedPrefUtils.getSharedPrefVersion(BaseApplication.getAppContext());






				/**
				 * save category data into tables 
				 */
				if(groupCategoryData.size()>0 && groupCategoryData!=null)
				{
					/**
					 * delete all category table when version will be greater than 0.0 
					 * because 0.0 version will be only first time & that time there is no
					 * data in table so no need to delete tables:
					 */
					if(_versionModel.getCategoryVersion()>0.0)
					{
						saveTableUtils.deleteAllCategoryTable();
					}

					for(GroupCategoryData groupData:groupCategoryData){
						String groupName=groupData.getGroupCategory().getName();
						ArrayList<CategoryData>  categoryData=	groupData.getGroupCategory().getMainCategories();

					/*	if(categoryData.size()>0 && !categoryData.isEmpty())
						{*/
							for(CategoryData  resultList : categoryData)
							{
								if (resultList.getName().equalsIgnoreCase("Events")){
									SharedPrefUtils.setEventIdForCity(BaseApplication.getAppContext(), resultList.getMainCategory().getId());
								}
								saveAllCategoryDataInDb(saveTableUtils,resultList,groupName);

							}
					//	}
						/**
						 * in this case we are getting only groups & put into table:
						 */
						/*else
						{
							saveDataIntoCategoryTable(saveTableUtils,groupName);
						}*/

					}
				}
				/**
				 * Save Locality Data into table:- (LocalityTable)
				 */

				if(localityData.size()>0 && localityData!=null)
				{  
					if(_versionModel.getLocalityVersion()>0.0)
					{
						saveTableUtils.deleteLocalityTable();
					}

					for(LocalityData localityList:localityData){
						saveAllLocalityDataInDb(saveTableUtils,localityList);
					}
				}
				/**
				 * Save City Data into table:- (LocalityTable)
				 */
				if(cityData.size()>0 && cityData!=null)
				{
					/**
					 * delete city table 
					 */
					if(_versionModel.getCityVersion()>0.0)
					{
						saveTableUtils.deleteCityTable();
					}
                 CityTable _table=new CityTable((BaseApplication)mContext.getApplicationContext());
                int count=   _table.getTotalCount();
                 if(count>0){
                	 _table.deleteAll();
                 }
					for(CityData cityList:cityData){
						
						saveAllCityDatainDb(saveTableUtils,cityList);
					}
				}

				/**
				 * save current version data in shared preference
				 */
				saveCurrentVersionInSharedPref(_configurationData);


			}else if(_configurationResponse.getResponseCode()==400){
//				Toast.makeText(mContext, "Category not fetched.", Toast.LENGTH_LONG).show();

			}
		}
		return null;
	}
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		mOnUIView.comeBackOnUI();	

	}

	/**
	 * this method will save current version in shared preference
	 * @param _configurationData
	 */

	private void saveCurrentVersionInSharedPref(ConfigurationData _configurationData) {
		float categoryVersion= _configurationData.getCategoryApi().getVersion();
		float cityVersion= _configurationData.getCityApi().getVersion();
		float localityVersion= _configurationData.getLocalityApi().getVersion();
		VersionApiModel _versionModel=new VersionApiModel();
		_versionModel.setCategoryVersion(categoryVersion);
		_versionModel.setCityVersion(cityVersion);
		_versionModel.setLocalityVersion(localityVersion);
		SharedPrefUtils.setSharedPrefVesion(BaseApplication.getAppContext(), _versionModel);

	}

	/**
	 * This method we use save category data into 5 tables-Deepanker
	 * @param saveTableUtils
	 * @param resultList
	 */

	private void saveAllCategoryDataInDb(SaveTableDataUtils saveTableUtils,CategoryData resultList,String groupName){
		MainCategory mainCategory = resultList.getMainCategory();
		String internalCategoryName=resultList.getName();
		int categoryId=mainCategory.getId();
		String categoryName=mainCategory.getName();
		CategoryModel categoryModel=new CategoryModel();
		categoryModel.setMainCategoryId(categoryId);
	//	categoryModel.setCategoryName(categoryName);
		categoryModel.setCategoryName(internalCategoryName);
		categoryModel.setGroupName(groupName);
		ArrayList<MainFilters> filterList = mainCategory.getFilters();
		ArrayList<SortBy> soryByList = mainCategory.getSortBy();
		ArrayList<AdvancedSearch> advancedSearchList = mainCategory.getAdvancedSearch();
		ArrayList<SubCategories> calegoriesList = mainCategory.getSubCategories();
		ArrayList<AgeGroup> ageGroupList=mainCategory.getAgeGroup();
		ArrayList<Activities> activitiesList=mainCategory.getActivities();           
		ArrayList<DateValue> dateList=mainCategory.getDateValue();
		
		saveTableUtils.saveCategoryData(categoryModel);
		if(advancedSearchList!=null && !(advancedSearchList.isEmpty())){
			saveTableUtils.saveAdvancedSearchData(advancedSearchList, categoryId);
		}
		if(filterList!=null && !(filterList.isEmpty())){
		saveTableUtils.saveFilterData(filterList, categoryId);
		}
		if(soryByList!=null && !(soryByList.isEmpty())){
		saveTableUtils.saveSoryByData(soryByList, categoryId);
		}
		if(calegoriesList!=null && !(calegoriesList.isEmpty())){
		saveTableUtils.saveSubCategoryData(calegoriesList, categoryId);
		}
		if(ageGroupList!=null && !(ageGroupList.isEmpty())){
		saveTableUtils.saveAgeGroupData(ageGroupList, categoryId);
		}
		if(activitiesList!=null && !(activitiesList.isEmpty())){
			saveTableUtils.saveActivities(activitiesList, categoryId);
			}
		if(dateList!=null && !(dateList.isEmpty())){
			saveTableUtils.saveDateValue(dateList, categoryId);
			}
		

	}
	
	private void saveDataIntoCategoryTable(SaveTableDataUtils saveTableUtils,String groupName){
		CategoryModel categoryModel=new CategoryModel();
		categoryModel.setMainCategoryId(0);
		categoryModel.setCategoryName("");
		categoryModel.setGroupName(groupName);
		saveTableUtils.saveCategoryData(categoryModel);
	}

	/**
	 * this method will sace locality data into single table . (LocalityTable)
	 * @param saveTableUtils
	 * @param localityList
	 */

	private void saveAllLocalityDataInDb(SaveTableDataUtils saveTableUtils,LocalityData localityList) {

		int zoneId=localityList.getId();
		String zoneName=localityList.getName();

		/**
		 * in first entry we assume zoneName=localityName & zoneId=localityId
		 */
		saveTableUtils.saveLocationTableData(null, zoneId, zoneName);
		/**
		 * insert total localities with zoneId & zoneName
		 */
		ArrayList<Localities> localities=localityList.getLocalities();
		saveTableUtils.saveLocationTableData(localities, zoneId, zoneName);

	}

	/**
	 * save city data into CityTable:
	 * @param saveTableUtils
	 * @param cityList
	 */

	private void saveAllCityDatainDb(SaveTableDataUtils saveTableUtils,CityData cityList) {
		MetroCity _cityModel=cityList.getMetroCity();
		saveTableUtils.saveCityTableData(_cityModel);
	}

}
