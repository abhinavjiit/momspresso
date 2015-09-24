package com.mycity4kids.dbtable;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.kelltontech.model.BaseModel;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.database.BaseTable;
import com.mycity4kids.models.locality.Localities;
import com.mycity4kids.models.locality.LocalityModel;
import com.mycity4kids.models.locality.ZoneModel;

import java.util.ArrayList;

public class LocalityTable extends BaseTable{
//	private static final String PRIMARY_KEY="_id";
	private static final String ZONE_ID="zoneId";
	private static final String LOCALITY_ID="localityId";
	private static final String LOCALITY_NAME="localityName";
	private static final String ZONE_CITY="zoneCity";
	
	
	/**
	 * 
	 * Table Name:-
	 */
	
	public static final String LOCALITY_TABLE="localityTable";
	
	public static final String CREATE_LOCALITY_TABLE = "create table if not exists " +
			LOCALITY_TABLE + "(" +
//			PRIMARY_KEY+" integer primary key," +
			ZONE_ID +" integer ,"+
			LOCALITY_ID +" integer ,"+
			ZONE_CITY +" text ,"+
			LOCALITY_NAME+" text ," +
			" CONSTRAINT unq UNIQUE (" + ZONE_ID + ", " + LOCALITY_ID + " )" +
			")";

	public static final String DROP_QUERY = "Drop table if exists " + LOCALITY_TABLE;

	public LocalityTable(BaseApplication pApplication) {
		super(pApplication, LOCALITY_TABLE);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ContentValues getContentValues(BaseModel pModel) {
		Localities _localityModel=(Localities)pModel;
		ContentValues _contentValue=new ContentValues();
		_contentValue.put(ZONE_ID,_localityModel.getZoneId());
		_contentValue.put(LOCALITY_ID,_localityModel.getId());
		_contentValue.put(LOCALITY_NAME,_localityModel.getName());
		_contentValue.put(ZONE_CITY,_localityModel.getZoneCity());
		return _contentValue; 
		
	}

	@Override
	protected ArrayList<BaseModel> getAllData(String pSelection,
			String[] pSelectionArgs) {
		
		return null;
	}
	/**
	 * this method will give all localites name according to search element :=
	 * @param pSearchElement:- this query we are using during auto search Implementation
	 * @return
	 */
	
	public ArrayList<String> getLocalitiesName(String pSearchElement){
		ArrayList<String> _localitiesList=new ArrayList<String>();
		Cursor _cursor=null;
		try {
			String CRETE_LOCALITY_QUERY="select "+LOCALITY_NAME +" from "+ LOCALITY_TABLE+" where "+LOCALITY_NAME+
					" like "+"'%"+pSearchElement +"%'";
			_cursor=mWritableDatabase.rawQuery(CRETE_LOCALITY_QUERY, null);
			while(_cursor.moveToNext()){
				_localitiesList.add(_cursor.getString(_cursor.getColumnIndex(LOCALITY_NAME)));
			}
		} catch( Exception e ) { Log.e( LOCALITY_TABLE, "" + e ); }
		finally { closeCursor(_cursor); }
		
		return _localitiesList;
	}
	/**
	 * this will give all zone cities :-
	 * @return
	 */
	public ArrayList<String> getZoneCities(){
		ArrayList<String> _zoneCityList=new ArrayList<String>();
		Cursor _cursor=null;
		try {
			String ZONE_CITY_QUERY="select distinct "+ZONE_CITY+" from "+LOCALITY_TABLE;
			_cursor=mWritableDatabase.rawQuery(ZONE_CITY_QUERY, null);
			while(_cursor.moveToNext()){
				_zoneCityList.add(_cursor.getString(_cursor.getColumnIndex(ZONE_CITY)));
			}
		} catch( Exception e ) { Log.e( LOCALITY_TABLE, "" + e ); }
		finally { closeCursor(_cursor); }
		
		return _zoneCityList;
	}
	
	
	public ArrayList<ZoneModel> getZoneModel(){
		ArrayList<ZoneModel> _zoneCityList=new ArrayList<ZoneModel>();
		Cursor _cursor=null;
		try {
			String ZONE_CITY_QUERY="select distinct "+ZONE_CITY+","+ZONE_ID +" from "+LOCALITY_TABLE;
			_cursor=mWritableDatabase.rawQuery(ZONE_CITY_QUERY, null);
			while(_cursor.moveToNext()){
				ZoneModel _model=new ZoneModel();
				_model.setZoneCity(_cursor.getString(_cursor.getColumnIndex(ZONE_CITY)));
				_model.setZoneId(_cursor.getInt(_cursor.getColumnIndex(ZONE_ID)));
				_zoneCityList.add(_model);
			}
		} catch( Exception e ) { Log.e( LOCALITY_TABLE, "" + e ); }
		finally { closeCursor(_cursor); }
		
		return _zoneCityList;
	}
	public ArrayList<LocalityModel> getLocalityMoedel(int zoneId){
		ArrayList<LocalityModel> _localityList=new ArrayList<LocalityModel>();
		Cursor _cursor=null;
		try {
			String LOCALITY_QUERY="select "+LOCALITY_ID+","+LOCALITY_NAME +" from "+LOCALITY_TABLE+" where "+ZONE_ID+"!="+
		LOCALITY_ID+" and "+ZONE_ID+"=?";
			_cursor=mWritableDatabase.rawQuery(LOCALITY_QUERY, new String[]{String.valueOf(zoneId)});
			while(_cursor.moveToNext()){
				LocalityModel _model=new LocalityModel();
				_model.setLocalityName(_cursor.getString(_cursor.getColumnIndex(LOCALITY_NAME)));
				_model.setLocalityId(_cursor.getInt(_cursor.getColumnIndex(LOCALITY_ID)));
				_localityList.add(_model);
			}
		} catch( Exception e ) { Log.e( LOCALITY_TABLE, "" + e ); }
		finally { closeCursor(_cursor); }
		
		return _localityList;
	}
	public ArrayList<LocalityModel> getLocalityModel(){
		ArrayList<LocalityModel> _localityList=new ArrayList<LocalityModel>();
		Cursor _cursor=null;
		try {
			String LOCALITY_QUERY="select "+LOCALITY_ID+","+LOCALITY_NAME +" from "+LOCALITY_TABLE+" where "+ZONE_ID+"!="+LOCALITY_ID;
			_cursor=mWritableDatabase.rawQuery(LOCALITY_QUERY, null);
			while(_cursor.moveToNext()){
				LocalityModel _model=new LocalityModel();
				_model.setLocalityName(_cursor.getString(_cursor.getColumnIndex(LOCALITY_NAME)));
				_model.setLocalityId(_cursor.getInt(_cursor.getColumnIndex(LOCALITY_ID)));
				_localityList.add(_model);
			}
		} catch( Exception e ) { Log.e( LOCALITY_TABLE, "" + e ); }
		finally { closeCursor(_cursor); }
		
		return _localityList;
	}
	public ArrayList<Localities> getLocalities(){
		ArrayList<Localities> _localityList=new ArrayList<Localities>();
		Cursor _cursor=null;
		try {
			String LOCALITY_QUERY="select "+LOCALITY_ID+","+LOCALITY_NAME +" from "+LOCALITY_TABLE+" where "+ZONE_ID+"!="+LOCALITY_ID;
			_cursor=mWritableDatabase.rawQuery(LOCALITY_QUERY, null);
			while(_cursor.moveToNext()){
				Localities _model=new Localities();
				_model.setName(_cursor.getString(_cursor.getColumnIndex(LOCALITY_NAME)));
				_model.setId(_cursor.getInt(_cursor.getColumnIndex(LOCALITY_ID)));
				_localityList.add(_model);
			}
		} catch( Exception e ) { Log.e( LOCALITY_TABLE, "" + e ); }
		finally { closeCursor(_cursor); }
		
		return _localityList;
	}
	
	
}
