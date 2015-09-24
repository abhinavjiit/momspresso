package com.mycity4kids.dbtable;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.kelltontech.model.BaseModel;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.database.BaseTable;
import com.mycity4kids.models.category.DateValue;

import java.util.ArrayList;

public class DateTable extends BaseTable{
//	private static final String PRIMARY_KEY="_id";
	private static final String DATE_KEY="dateKey";
	private static final String DATE_VALUE="dateValue";
	private static final String CATEGORY_ID="categoryId";
	
	
	/**
	 * 
	 * Table Name:-
	 */
	
	public static final String DATE_TABLE="dateTable";
	
	public static final String CREATE_DATE_TABLE = "create table if not exists " +
			DATE_TABLE + "(" +
//			PRIMARY_KEY+" integer primary key," +
			CATEGORY_ID +" int not null ,"+
			DATE_KEY +" text not null ,"+
			DATE_VALUE+" text not null ," +
			" CONSTRAINT unq UNIQUE (" + DATE_KEY + ", " + CATEGORY_ID + " )" +
			" )";

	public static final String DROP_QUERY = "Drop table if exists " + DATE_TABLE;

	public DateTable(BaseApplication pApplication) {
		super(pApplication, DATE_TABLE);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ContentValues getContentValues(BaseModel pModel) {
		DateValue filterModel=(DateValue)pModel;
		ContentValues _contentValue=new ContentValues();
		_contentValue.put(DATE_KEY ,filterModel.getKey());
		_contentValue.put(DATE_VALUE, filterModel.getValue());
		_contentValue.put(CATEGORY_ID, filterModel.getCategoryId());
		return _contentValue; 
	}

	@Override
	protected ArrayList<BaseModel> getAllData(String pSelection,
			String[] pSelectionArgs) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public ArrayList<DateValue> getAllDateValues(int categoryId) {
		ArrayList<DateValue> dateValueList=new ArrayList<DateValue>();
		Cursor _cursor = null;
		try{
		String CREATE_QUERY="select * from "+DATE_TABLE+" where "+CATEGORY_ID+"=?"; 
		 _cursor=mWritableDatabase.rawQuery(CREATE_QUERY, new String[]{String.valueOf(categoryId)});
		 while (_cursor.moveToNext()){
			 DateValue dateModel=new DateValue();
			 dateModel.setKey(_cursor.getString(_cursor.getColumnIndex(DATE_KEY)));
			 dateModel.setValue(_cursor.getString(_cursor.getColumnIndex(DATE_VALUE)));
			 dateValueList.add(dateModel);
		 }
	}
	catch( Exception e ) { Log.e( DATE_TABLE, "" + e ); }
	finally { closeCursor(_cursor); }
	return dateValueList;
	}
	

}
