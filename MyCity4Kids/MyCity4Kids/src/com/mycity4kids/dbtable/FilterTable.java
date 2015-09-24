package com.mycity4kids.dbtable;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.kelltontech.model.BaseModel;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.database.BaseTable;
import com.mycity4kids.models.category.Filters;
import com.mycity4kids.models.category.MainFilters;

import java.util.ArrayList;

public class FilterTable extends BaseTable{
//	private static final String PRIMARY_KEY="_id";
	private static final String FILTER_KEY="filterId";
	private static final String FILTER_VALUE="filterName";
	private static final String CATEGORY_ID="categoryId";
	
	
	/**
	 * 
	 * Table Name:-
	 */
	
	public static final String FILTER_TABLE="filterTable";
	
	public static final String CREATE_FILTER_TABLE = "create table if not exists " +
			FILTER_TABLE + "(" +
//			PRIMARY_KEY+" integer primary key," +
			CATEGORY_ID +" int not null ,"+
			FILTER_KEY +" integer not null ,"+
			FILTER_VALUE+" text not null ," +
			" CONSTRAINT unq UNIQUE (" + FILTER_KEY + ", " + CATEGORY_ID + " )" +
			" )";

	public static final String DROP_QUERY = "Drop table if exists " + FILTER_TABLE;

	public FilterTable(BaseApplication pApplication) {
		super(pApplication, FILTER_TABLE);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ContentValues getContentValues(BaseModel pModel) {
		MainFilters filterModel=(MainFilters)pModel;
		ContentValues _contentValue=new ContentValues();
		_contentValue.put(FILTER_KEY ,filterModel.getFilter().getKey());
		_contentValue.put(FILTER_VALUE, filterModel.getFilter().getValue());
		_contentValue.put(CATEGORY_ID, filterModel.getCategoryId());
		return _contentValue; 
	}

	@Override
	protected ArrayList<BaseModel> getAllData(String pSelection,
			String[] pSelectionArgs) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public ArrayList<Filters> getAllFilters(int categoryId) {
		ArrayList<Filters> filterList=new ArrayList<Filters>();
		Cursor _cursor = null;
		try{
		String CREATE_QUERY="select * from "+FILTER_TABLE+" where "+CATEGORY_ID+"=?"; 
		 _cursor=mWritableDatabase.rawQuery(CREATE_QUERY, new String[]{String.valueOf(categoryId)});
		 while (_cursor.moveToNext()){
			 Filters filterModel=new Filters();
			 filterModel.setKey(_cursor.getInt(_cursor.getColumnIndex(FILTER_KEY)));
			 filterModel.setValue(_cursor.getString(_cursor.getColumnIndex(FILTER_VALUE)));
			 filterList.add(filterModel);
		 }
	}
	catch( Exception e ) { Log.e( FILTER_TABLE, "" + e ); }
	finally { closeCursor(_cursor); }
	return filterList;
	}
	
	

}
