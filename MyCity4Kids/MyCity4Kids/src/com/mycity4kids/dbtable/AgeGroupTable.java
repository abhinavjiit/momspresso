package com.mycity4kids.dbtable;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.kelltontech.model.BaseModel;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.database.BaseTable;
import com.mycity4kids.models.category.AgeGroup;

import java.util.ArrayList;

public class AgeGroupTable extends BaseTable{
//	private static final String PRIMARY_KEY="_id";
	private static final String AGE_GROUP_KEY="ageKey";
	private static final String AGE_GROUP_VALUE="ageValue";
	private static final String CATEGORY_ID="categoryId";


	/**
	 * 
	 * Table Name:-
	 */

	public static final String AGE_GROUP_TABLE="ageGroupTable";

	public static final String CREATE_AGE_GROUP_TABLE = "create table if not exists " +
			AGE_GROUP_TABLE + "(" +
//			PRIMARY_KEY+" integer primary key," +
			AGE_GROUP_KEY +" integer not null,"+
			CATEGORY_ID +" int not null ,"+
			AGE_GROUP_VALUE+" text not null,"+
			" CONSTRAINT unq UNIQUE (" + AGE_GROUP_KEY + ", " + CATEGORY_ID + " )" +
			" )";

	public static final String DROP_QUERY = "Drop table if exists " + AGE_GROUP_TABLE;
	public AgeGroupTable(BaseApplication pApplication) {
		super(pApplication, AGE_GROUP_TABLE);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ContentValues getContentValues(BaseModel pModel) {
		AgeGroup ageGroup=(AgeGroup)pModel;
		ContentValues _contentValue=new ContentValues();
		_contentValue.put(AGE_GROUP_KEY,ageGroup.getKey());
		_contentValue.put(AGE_GROUP_VALUE, ageGroup.getValue());
		_contentValue.put(CATEGORY_ID, ageGroup.getCategoryId());
		return _contentValue;
	}

	@Override
	protected ArrayList<BaseModel> getAllData(String pSelection,
			String[] pSelectionArgs) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public ArrayList<AgeGroup> getAgeGroupData(int categoryId){
		ArrayList<AgeGroup> _ageGroupList=new ArrayList<AgeGroup>();
		Cursor _cursor=null;
		try {
			String CREATE_QUERY="select * from "+AGE_GROUP_TABLE+" where "+CATEGORY_ID+"=?";
			_cursor=mWritableDatabase.rawQuery(CREATE_QUERY, new String[]{String.valueOf(categoryId)});
			while(_cursor.moveToNext()){
				AgeGroup _ageGroup=new AgeGroup();
				_ageGroup.setKey(_cursor.getString(_cursor.getColumnIndex(AGE_GROUP_KEY)));
				_ageGroup.setValue(_cursor.getString(_cursor.getColumnIndex(AGE_GROUP_VALUE)));
				_ageGroupList.add(_ageGroup);
			}
		} catch( Exception e ) { Log.e( AGE_GROUP_TABLE, "" + e ); }
		finally { closeCursor(_cursor); }
		
		return _ageGroupList;
	}
	

}
