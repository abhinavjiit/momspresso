package com.mycity4kids.ui.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.asynctask.HeavyDbTask;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.controller.ConfigurationController;
import com.mycity4kids.dbtable.CityTable;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.interfaces.OnUIView;
import com.mycity4kids.models.VersionApiModel;
import com.mycity4kids.models.city.City;
import com.mycity4kids.models.city.MetroCity;
import com.mycity4kids.models.configuration.ConfigurationApiModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.adapter.LocationListAdapter;
import com.mycity4kids.utils.NearMyCity;
import com.mycity4kids.utils.NearMyCity.FetchCity;
import com.mycity4kids.utils.location.GPSTracker;

public class SelectLocationActivity extends BaseActivity implements FetchCity{

	private ArrayList<MetroCity> cityList;
	private boolean isFromSplashScreen=false;
	private String currentCity;
	private ListView locationList;
	private TextView headerText;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_location);
		Utils.pushOpenScreenEvent(SelectLocationActivity.this, "Select Location", SharedPrefUtils.getUserDetailModel(this).getId() + "");

		locationList = (ListView) findViewById(R.id.locationList);
		GPSTracker getCurrentLocation = new GPSTracker(SelectLocationActivity.this);
		double _latitude = getCurrentLocation.getLatitude();
		double _longitude = getCurrentLocation.getLongitude();
		new NearMyCity(SelectLocationActivity.this, _latitude, _longitude, SelectLocationActivity.this);
		headerText = (TextView) findViewById(R.id.txvHeaderText);
		headerText.setText("Select Location");

		/*LocationListAdapter adapter = new LocationListAdapter(SelectLocationActivity.this);
		adapter.setData(getLocationList());
		locationList.setAdapter(adapter);*/
		Bundle data = getIntent().getExtras();
		if(data!=null){
			isFromSplashScreen=getIntent().getExtras().getBoolean("isFromSplash");
		}


		final ConfigurationController _controller=new ConfigurationController(SelectLocationActivity.this, this);

		locationList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				try {
					

				VersionApiModel versionApiModel=SharedPrefUtils.getSharedPrefVersion(SelectLocationActivity.this);
				if (!(position == 0 || position == 2)) {
					MetroCity cityModel = cityList.get(position);
					if(isFromSplashScreen){
						SharedPrefUtils.setCurrentCityModel(SelectLocationActivity.this, cityModel);
						/**
						 * hit for Locality API;
						 */

						versionApiModel.setCityId(SharedPrefUtils.getCurrentCityModel(SelectLocationActivity.this).getId());

						showProgressDialog("Please Wait...");
						PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
					    String	version = pInfo.versionName;
					    versionApiModel.setAppUpdateVersion(version);
						_controller.getData(AppConstants.LOCATION_SEARCH_REQUEST, versionApiModel);

					}else{

						/**
						 * CR :deepanker
						 */
						/*
						 *//**
						 * for this time for near me i've set city id =1 :- i will change it
						 *//*
                    if(position==1){
                    	GPSTracker getCurrentLocation = new GPSTracker(SelectLocationActivity.this);
                		double _latitude = getCurrentLocation.getLatitude();
                		double _longitude = getCurrentLocation.getLongitude();
                    	new NearMyCity(SelectLocationActivity.this, _latitude, _longitude, SelectLocationActivity.this);
                    	return;

                    }*/

						int currentCityId=SharedPrefUtils.getCurrentCityModel(SelectLocationActivity.this).getId();
                        String currentCityName=SharedPrefUtils.getCurrentCityModel(SelectLocationActivity.this).getName();

						int cityId = cityModel.getId();
						if(cityId!=currentCityId){
							MetroCity metroCity=new MetroCity();
							metroCity.setId(currentCityId);
							metroCity.setName(currentCityName);
							SharedPrefUtils.setCurrentCityModel(SelectLocationActivity.this, cityModel);
							/**
							 * hit for Locality API;
							 */

							versionApiModel.setCityId(SharedPrefUtils.getCurrentCityModel(SelectLocationActivity.this).getId());

							showProgressDialog("Please Wait...");
							PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
						    String	version = pInfo.versionName;
						    versionApiModel.setAppUpdateVersion(version);
							_controller.getData(AppConstants.LOCATION_SEARCH_REQUEST, versionApiModel);

						}else{
							if(isFromSplashScreen){
								Intent intent =new Intent(SelectLocationActivity.this,HomeCategoryActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
								startActivity(intent);
								finish();
							}else{
								finish();
							}

						}
					}

				}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});
		findViewById(R.id.cross_icon).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				try {
					
				
				if(isFromSplashScreen){
					
					VersionApiModel versionApiModel=SharedPrefUtils.getSharedPrefVersion(SelectLocationActivity.this);
					versionApiModel.setCityId(SharedPrefUtils.getCurrentCityModel(SelectLocationActivity.this).getId());
					ConfigurationController _controller=new ConfigurationController(SelectLocationActivity.this, SelectLocationActivity.this);
					showProgressDialog("Please Wait...");
					PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
				    String	version = pInfo.versionName;
				    versionApiModel.setAppUpdateVersion(version);
					_controller.getData(AppConstants.LOCATION_SEARCH_REQUEST, versionApiModel);

					
					
					/*Intent intent =new Intent(SelectLocationActivity.this,HomeCategoryActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(intent);
					finish();*/
				}else{
					finish();
				}
				
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		removeProgressDialog();
	}


	public ArrayList<MetroCity> getLocationList(City cityModel ) {
		/**
		 * citymodel we are still geting bcz may be it can be change according to client requirement;it is coming but this 
		 * time we are not using it;cityModel replace by MetroCity
		 */
		MetroCity currentSaveModel = SharedPrefUtils.getCurrentCityModel(SelectLocationActivity.this);


		CityTable cityTable = new CityTable((BaseApplication) getApplicationContext());
		//		ArrayList<BaseModel> baseList=_cityTable.getAllData();
		//cityList = (ArrayList<MetroCity>) (ArrayList<?>)cityTable.getAllCityData(cityModel);
		cityList = (ArrayList<MetroCity>) (ArrayList<?>)cityTable.getAllCityData(currentSaveModel);

		MetroCity metroCity = new MetroCity();
		metroCity.setName("Current Location");
		cityList.add(0, metroCity);
		metroCity = new MetroCity();
		if(!StringUtils.isNullOrEmpty(currentSaveModel.getName()))
		{
		 MetroCity	metroCityCurrent = new MetroCity();
			if(currentSaveModel.getName().contains("Delhi") && currentSaveModel.getName().contains("-")){
				String[] headerCityName=currentSaveModel.getName().split("-");
				metroCityCurrent.setName(headerCityName[0]+" "+headerCityName[1].toUpperCase());
			}else{
				metroCityCurrent.setName(currentSaveModel.getName());
			}
			metroCityCurrent.setId(currentSaveModel.getId());
			cityList.add(1, metroCityCurrent);
		}
	
		metroCity = new MetroCity();
		metroCity.setName("Select a City");
		cityList.add(2, metroCity);

		return cityList;
	}



	@Override
	protected void updateUi(Response response) {

		if( response==null){
			removeProgressDialog();
			showToast("Something went wrong from server");
		}
		switch (response.getDataType()) {
		/**
		 * Location Search request & configuration request almost same > 
		 * We get all category & all localities when we will change current city.
		 */

		case AppConstants.LOCATION_SEARCH_REQUEST:
			Object responseObject = response.getResponseObject();
			if(responseObject instanceof ConfigurationApiModel ){
				ConfigurationApiModel _configurationResponse=(ConfigurationApiModel)responseObject;

				/**
				 * Save data into tables :-
				 */
				HeavyDbTask _heavyDbTask=new HeavyDbTask(this,_configurationResponse, new OnUIView() {

					@Override
					public void comeBackOnUI() {
						removeProgressDialog();
						if(isFromSplashScreen){
						
							
							Intent intent =new Intent(SelectLocationActivity.this,HomeCategoryActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
							startActivity(intent);
							finish();
						}else{
							finish();
						}

					}
				});
				_heavyDbTask.execute();


			}
			break;

		default:
			break;
		}

	}
	@Override
	public void nearCity(City cityModel) {




		LocationListAdapter adapter = new LocationListAdapter(SelectLocationActivity.this);

		/**
		 * First we were showing current location according to  current lat long around 100 km 
		 * But now requirement change : so we will show current location which we have select; 
		 * In case of future we will use it but for this time its comming but  i am not using it
		 */
		adapter.setData(getLocationList(cityModel));
		locationList.setAdapter(adapter);

		/**
		 * CR :
		 */

		/*VersionApiModel versionApiModel = SharedPrefUtils.getSharedPrefVersion(SelectLocationActivity.this);
		int cityId = cityModel.getCityId();

		 *//**
		 * save current city id in shared preference
		 *//*
		MetroCity model = new MetroCity();
		model.setId(cityModel.getCityId());
		model.setName(cityModel.getCityName());
		  *//**
		  * this city model will be save only one time on splash:
		  *//*
		SharedPrefUtils.setCurrentCityModel(SelectLocationActivity.this, model);



		if (cityId > 0) {
			versionApiModel.setCityId(cityId);
			if(!ConnectivityUtils.isNetworkEnabled(SelectLocationActivity.this)){
				ToastUtils.showToast(SelectLocationActivity.this, getString(R.string.error_network));
				return;

			}
			int currentCityId=SharedPrefUtils.getCurrentCityModel(SelectLocationActivity.this).getId();

			 ConfigurationController _controller = new ConfigurationController(this, this);

				if(cityId!=currentCityId){
					SharedPrefUtils.setCurrentCityModel(SelectLocationActivity.this, model);
		   *//**
		   * hit for Locality API;
		   *//*

					versionApiModel.setCityId(SharedPrefUtils.getCurrentCityModel(SelectLocationActivity.this).getId());

					showProgressDialog("Please Wait...");

					_controller.getData(AppConstants.LOCATION_SEARCH_REQUEST, versionApiModel);

				}else{
					if(isFromSplashScreen){
						Intent intent =new Intent(SelectLocationActivity.this,HomeCategoryActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
						startActivity(intent);
						finish();
					}else{
						finish();
					}

				}*/


		//	}

	}
}