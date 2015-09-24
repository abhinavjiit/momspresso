package com.mycity4kids.dbtable;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.kelltontech.model.BaseModel;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.database.BaseTable;
import com.mycity4kids.models.category.Activities;

import java.util.ArrayList;

public class ActivititiesTable extends BaseTable{
//	private static final String PRIMARY_KEY="_id";
	private static final String ACTIVITY_ID="acitivityId";
	private static final String ACTIVITY_NAME="activityName";
	private static final String CATEGORY_ID="categoryId";
	
	
	/**
	 * 
	 * Table Name:-
	 */
	
	public static final String ACTIVITY_TABLE="activityTable";
	
	public static final String CREATE_ACTIVITY_TABLE = "create table if not exists " +
			ACTIVITY_TABLE + "(" +
//			PRIMARY_KEY+" integer primary key," +
			CATEGORY_ID +" int not null ,"+
			ACTIVITY_ID +" int not null ,"+
			ACTIVITY_NAME+" text not null ," +
			" CONSTRAINT unq UNIQUE (" + ACTIVITY_ID + ", " + CATEGORY_ID + " )" +
			" )";

	public static final String DROP_QUERY = "Drop table if exists " + ACTIVITY_TABLE;

	public ActivititiesTable(BaseApplication pApplication) {
		super(pApplication, ACTIVITY_TABLE);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ContentValues getContentValues(BaseModel pModel) {
		Activities activityModel=(Activities)pModel;
		ContentValues _contentValue=new ContentValues();
		_contentValue.put(ACTIVITY_ID ,activityModel.getId());
		_contentValue.put(ACTIVITY_NAME, activityModel.getName());
		_contentValue.put(CATEGORY_ID, activityModel.getCategoryId());
		return _contentValue; 
	}

	@Override
	protected ArrayList<BaseModel> getAllData(String pSelection,
			String[] pSelectionArgs) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public ArrayList<Activities> getAllActivity(int categoryId) {
		ArrayList<Activities> activityList=new ArrayList<Activities>();
		Cursor _cursor = null;
		try{
		String CREATE_QUERY="select * from "+ACTIVITY_TABLE+" where "+CATEGORY_ID+"=?"; 
		 _cursor=mWritableDatabase.rawQuery(CREATE_QUERY, new String[]{String.valueOf(categoryId)});
		 while (_cursor.moveToNext()){
			 Activities activityModel=new Activities();
			 activityModel.setId(_cursor.getInt(_cursor.getColumnIndex(ACTIVITY_ID)));
			 activityModel.setName(_cursor.getString(_cursor.getColumnIndex(ACTIVITY_NAME)));
			 activityList.add(activityModel);
		 }
	}
	catch( Exception e ) { Log.e( ACTIVITY_TABLE, "" + e ); }
	finally { closeCursor(_cursor); }
	return activityList;
	}
	
}
