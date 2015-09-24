package com.mycity4kids.dbtable;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.kelltontech.model.BaseModel;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.database.BaseTable;
import com.mycity4kids.models.city.MetroCity;

import java.util.ArrayList;

public class CityTable extends BaseTable{
//	private static final String PRIMARY_KEY="_id";
	private static final String CITY_ID="cityId";
	private static final String CITY_NAME="cityName";


	/**
	 * 
	 * Table Name:-
	 */

	public static final String CITY_TABLE="cityTable";

	public static final String CREATE_CITY_TABLE = "create table if not exists " +
			CITY_TABLE + "(" +
//			PRIMARY_KEY+" integer primary key," +
			CITY_ID +" integer primary key,"+
			CITY_NAME+" text not null )";

	public static final String DROP_QUERY = "Drop table if exists " + CITY_TABLE;

	public CityTable(BaseApplication pApplication) {
		super(pApplication, CITY_TABLE);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ContentValues getContentValues(BaseModel pModel) {
		MetroCity _cityModel=(MetroCity)pModel;
		ContentValues _contentValue=new ContentValues();
		_contentValue.put(CITY_ID,_cityModel.getId());
		_contentValue.put(CITY_NAME,_cityModel.getName());
		return _contentValue; 
	}

	@Override
	protected ArrayList<BaseModel> getAllData(String pSelection,String[] pSelectionArgs) {
		ArrayList<BaseModel> _cityList=new ArrayList<BaseModel>();
		Cursor _cursor=null;
		try{
			String CREATE_QUERY="select * from "+CITY_TABLE;
			_cursor=mWritableDatabase.rawQuery(CREATE_QUERY, null);
			while (_cursor.moveToNext()){
				MetroCity _cityModel=new MetroCity();
				_cityModel.setId(_cursor.getInt(_cursor.getColumnIndex(CITY_ID)));
				_cityModel.setName(_cursor.getString(_cursor.getColumnIndex(CITY_NAME)));
				_cityList.add(_cityModel);
			}
		}
		catch( Exception e ) { Log.e( CITY_TABLE, "" + e ); }
		finally { closeCursor(_cursor); }
		return _cityList;
	}

	public ArrayList<BaseModel> getAllCityData(MetroCity cityModel) {
		ArrayList<BaseModel> _cityList=new ArrayList<BaseModel>();
		Cursor _cursor=null;
		try{
			String CREATE_QUERY="select * from "+CITY_TABLE;
			_cursor=mWritableDatabase.rawQuery(CREATE_QUERY, null);
			while (_cursor.moveToNext()){
				MetroCity _cityModel=new MetroCity();
				int cityId=_cursor.getInt(_cursor.getColumnIndex(CITY_ID));

				if(cityId!=cityModel.getId())
				{
					_cityModel.setId(cityId);
					_cityModel.setName(_cursor.getString(_cursor.getColumnIndex(CITY_NAME)));
					_cityList.add(_cityModel);
				}
			}
		}
		catch( Exception e ) { Log.e( CITY_TABLE, "" + e ); }
		finally { closeCursor(_cursor); }
		return _cityList;
	}


	public int getTotalCount(){
		Cursor _cursor=null;
		int count=0;
		try{
			String CREATE_QUERY="select * from "+CITY_TABLE;
			_cursor=mWritableDatabase.rawQuery(CREATE_QUERY, null);
			count=_cursor.getCount();
		}

		catch( Exception e ) { Log.e( CITY_TABLE, "" + e ); }
		finally { closeCursor(_cursor); }
		return count;
	}
}
