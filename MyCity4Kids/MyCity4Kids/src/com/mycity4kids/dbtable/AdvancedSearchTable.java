package com.mycity4kids.dbtable;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.kelltontech.model.BaseModel;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.database.BaseTable;
import com.mycity4kids.models.category.AdvancedSearch;

import java.util.ArrayList;

public class AdvancedSearchTable extends BaseTable{
//	private static final String PRIMARY_KEY="_id";
	private static final String SEARCH_KEY="searchKey";
	private static final String SEARCH_VALUE="searchValue";
	private static final String CATEGORY_ID="categoryId";
	/**
	 * 
	 * Table Name:-
	 */
	
	public static final String ADVANCED_SEARCH_TABLE="advancedSearchTable";
	
	public static final String CREATE_ADVANCED_SEARCH_TABLE = "create table if not exists " +
			ADVANCED_SEARCH_TABLE + "(" +
//			PRIMARY_KEY+" integer primary key," +
			CATEGORY_ID +" int not null ,"+
			SEARCH_KEY +" text not null ,"+
			SEARCH_VALUE+" text not null ," +
			" CONSTRAINT unq UNIQUE (" + SEARCH_KEY + ", " + CATEGORY_ID + " )" +
			" )";

	public static final String DROP_QUERY = "Drop table if exists " + ADVANCED_SEARCH_TABLE;

	public AdvancedSearchTable(BaseApplication pApplication) {
		super(pApplication, ADVANCED_SEARCH_TABLE);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ContentValues getContentValues(BaseModel pModel) {
		ContentValues _contentValue=new ContentValues();
		if(pModel instanceof AdvancedSearch){
		AdvancedSearch advancedSearchModel=(AdvancedSearch)pModel;
		_contentValue.put(SEARCH_KEY,advancedSearchModel.getKey());
		_contentValue.put(SEARCH_VALUE, advancedSearchModel.getValue());
		_contentValue.put(CATEGORY_ID, advancedSearchModel.getCategoryId());
		}
		return _contentValue; 
	}

	@Override
	protected ArrayList<BaseModel> getAllData(String pSelection,
			String[] pSelectionArgs) {
		// TODO Auto-generated method stub
		return null;
	}
	public ArrayList<AdvancedSearch> getAllAdvancedSearch(int categoryId) {
		ArrayList<AdvancedSearch> advancedSearchList=new ArrayList<AdvancedSearch>();
		Cursor _cursor = null;
		try{
		String CREATE_QUERY="select * from "+ADVANCED_SEARCH_TABLE+" where "+CATEGORY_ID+"=?"; 
		 _cursor=mWritableDatabase.rawQuery(CREATE_QUERY, new String[]{String.valueOf(categoryId)});
		 while (_cursor.moveToNext()){
			 AdvancedSearch advancedSearchModel=new AdvancedSearch();
			 advancedSearchModel.setKey(_cursor.getString(_cursor.getColumnIndex(SEARCH_KEY)));
			 advancedSearchModel.setValue(_cursor.getString(_cursor.getColumnIndex(SEARCH_VALUE)));
			 advancedSearchList.add(advancedSearchModel);
		 }
	}
	catch( Exception e ) { Log.e( ADVANCED_SEARCH_TABLE, "" + e ); }
	finally { closeCursor(_cursor); }
	return advancedSearchList;
	}
}
