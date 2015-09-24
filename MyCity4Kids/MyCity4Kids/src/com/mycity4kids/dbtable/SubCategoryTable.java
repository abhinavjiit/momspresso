package com.mycity4kids.dbtable;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.kelltontech.model.BaseModel;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.database.BaseTable;
import com.mycity4kids.models.category.SubCategory;

import java.util.ArrayList;

public class SubCategoryTable extends BaseTable{
//	public static final String PRIMARY_KEY="_id";
	public static final String SUB_CATEGORY_ID="subCategoryId";
	public static final String SUB_CATEGORY_NAME="subCategoryName";
	public static final String CATEGORY_ID="categoryId";
	
	/**
	 * 
	 * Table Name:-
	 */
	
	public static final String SUB_CATEOGTY_TABLE="subCategoryTable";
	
	public static final String CREATE_SUB_CATEGORY_TABLE = "create table if not exists " +
			SUB_CATEOGTY_TABLE + "(" +
//			PRIMARY_KEY+" integer primary key," +
			SUB_CATEGORY_ID +" integer primary key,"+
			CATEGORY_ID +" integer not null ,"+
			SUB_CATEGORY_NAME+" text not null ," +
			" CONSTRAINT unq UNIQUE (" + SUB_CATEGORY_ID + ", " + CATEGORY_ID + " )" +
			" )";

	public static final String DROP_QUERY = "Drop table if exists " + SUB_CATEOGTY_TABLE;
	

	public SubCategoryTable(BaseApplication pApplication) {
		super(pApplication, SUB_CATEOGTY_TABLE);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ContentValues getContentValues(BaseModel pModel) {
		SubCategory subCategoryModel=(SubCategory)pModel;
		ContentValues _contentValue=new ContentValues();
		_contentValue.put(SUB_CATEGORY_ID,subCategoryModel.getId());
		_contentValue.put(SUB_CATEGORY_NAME, subCategoryModel.getName());
		_contentValue.put(CATEGORY_ID, subCategoryModel.getCategoryId());
		return _contentValue; 
	}

	@Override
	protected ArrayList<BaseModel> getAllData(String pSelection,
			String[] pSelectionArgs) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public ArrayList<SubCategory> getAllSubCategory(int categoryId) {
		ArrayList<SubCategory> categoryList=new ArrayList<SubCategory>();
		Cursor _cursor = null;
		try{
		String CREATE_QUERY="select * from "+SUB_CATEOGTY_TABLE+" where "+CATEGORY_ID+"=?"; 
		 _cursor=mWritableDatabase.rawQuery(CREATE_QUERY, new String[]{String.valueOf(categoryId)});
		 while (_cursor.moveToNext()){
			 SubCategory subCategoryModel=new SubCategory();
			 subCategoryModel.setId(_cursor.getInt(_cursor.getColumnIndex(SUB_CATEGORY_ID)));
			 subCategoryModel.setName(_cursor.getString(_cursor.getColumnIndex(SUB_CATEGORY_NAME)));
			 categoryList.add(subCategoryModel);
		 }
	}
	catch( Exception e ) { Log.e( SUB_CATEOGTY_TABLE, "" + e ); }
	finally { closeCursor(_cursor); }
	return categoryList;
	}

	

}
