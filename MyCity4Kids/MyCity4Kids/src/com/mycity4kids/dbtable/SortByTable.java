package com.mycity4kids.dbtable;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.kelltontech.model.BaseModel;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.database.BaseTable;
import com.mycity4kids.models.category.SortBy;

import java.util.ArrayList;

public class SortByTable extends BaseTable{
//	private static final String PRIMARY_KEY="_id";
	private static final String SEARCH_KEY="searchKey";
	private static final String SEARCH_VALUE="searchValue";
	private static final String CATEGORY_ID="categoryId";
	/**
	 * 
	 * Table Name:-
	 */

	public static final String SORT_BY_TABLE="sortByTable";

	public static final String CREATE_SORT_BY_TABLE = "create table if not exists " +
			SORT_BY_TABLE + "(" +
//			PRIMARY_KEY+" integer primary key," +
			CATEGORY_ID +" int not null ,"+
			SEARCH_KEY +" text not null ,"+
			SEARCH_VALUE+" text not null ," +
			" CONSTRAINT unq UNIQUE (" + SEARCH_KEY + ", " + CATEGORY_ID + " )" +
			" )";

	public static final String DROP_QUERY = "Drop table if exists " + SORT_BY_TABLE;
	public SortByTable(BaseApplication pApplication) {
		super(pApplication, SORT_BY_TABLE);
	}

	@Override
	protected ContentValues getContentValues(BaseModel pModel) {
		SortBy sortByModel=(SortBy)pModel;
		ContentValues _contentValue=new ContentValues();
		_contentValue.put(SEARCH_KEY, sortByModel.getKey());
		_contentValue.put(SEARCH_VALUE,sortByModel.getValue());
		_contentValue.put(CATEGORY_ID,sortByModel.getCategoryId());

		return _contentValue; 
	}

	@Override
	protected ArrayList<BaseModel> getAllData(String pSelection, String[] pSelectionArgs) {
		return null;
	}

	public ArrayList<SortBy> getSortByOptionsFromCategoryId(int categoryId){
		ArrayList<SortBy> sortByValues=new ArrayList<SortBy>();
		Cursor _cursor = null;
		try{
		String CREATE_QUERY="select * from "+SORT_BY_TABLE+" where "+CATEGORY_ID+"=?"; 
		 _cursor=mWritableDatabase.rawQuery(CREATE_QUERY, new String[]{String.valueOf(categoryId)});
		 while (_cursor.moveToNext()){
			 SortBy sortByModel=new SortBy();
			 sortByModel.setKey(_cursor.getString(_cursor.getColumnIndex(SEARCH_KEY)));
			 sortByModel.setValue(_cursor.getString(_cursor.getColumnIndex(SEARCH_VALUE)));
			 sortByValues.add(sortByModel);
		 }
	}
	catch( Exception e ) { Log.e( SORT_BY_TABLE, "" + e ); }
	finally { closeCursor(_cursor); }
	return sortByValues;
	}

}
