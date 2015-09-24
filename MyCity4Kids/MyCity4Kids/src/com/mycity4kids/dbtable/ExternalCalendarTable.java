package com.mycity4kids.dbtable;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.kelltontech.model.BaseModel;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.database.BaseTable;
import com.mycity4kids.newmodels.ExternalAccountInfoModel;

import java.util.ArrayList;

/**
 * Created by manish.soni on 06-08-2015.
 */
public class ExternalCalendarTable extends BaseTable {
    private static final String PRIMARY_KEY = "_id";
    private static final String USER_ID = "userLogin_id";
    private static final String SESSION = "session";
    private static final String IS_FB_ACCOUNT = "is_fb_account";


    /**
     * Table Name:-
     */

    public static final String EXTERNAL_CALENDAR_TABLE = "externalCalendarTable";

    public static final String CREATE_EXTERNAL_CALENDAR_TABLE = "create table if not exists " +
            EXTERNAL_CALENDAR_TABLE + "(" + PRIMARY_KEY + " integer primary key autoincrement," +
            USER_ID + " text not null," +
            IS_FB_ACCOUNT + " text not null," +
            SESSION + " text not null )";

    public static final String DROP_QUERY = "Drop table if exists " + EXTERNAL_CALENDAR_TABLE;

    public ExternalCalendarTable(BaseApplication pApplication) {
        super(pApplication, EXTERNAL_CALENDAR_TABLE);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected ContentValues getContentValues(BaseModel pModel) {
        ExternalAccountInfoModel _Model = (ExternalAccountInfoModel) pModel;
        ContentValues _contentValue = new ContentValues();
        _contentValue.put(USER_ID, _Model.getUserId());
        _contentValue.put(IS_FB_ACCOUNT, _Model.getIsFacebook());
        _contentValue.put(SESSION, _Model.getSession());
        return _contentValue;
    }

    @Override
    protected ArrayList<BaseModel> getAllData(String pSelection, String[] pSelectionArgs) {
        ArrayList<BaseModel> _cityList = new ArrayList<BaseModel>();
        Cursor _cursor = null;
        try {
            String CREATE_QUERY = "select * from " + EXTERNAL_CALENDAR_TABLE;
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);
            while (_cursor.moveToNext()) {
                ExternalAccountInfoModel _Model = new ExternalAccountInfoModel();
                _Model.setId(_cursor.getInt(_cursor.getColumnIndex(PRIMARY_KEY)));
                _Model.setUserId(_cursor.getString(_cursor.getColumnIndex(USER_ID)));
                _Model.setIsFacebook(_cursor.getString(_cursor.getColumnIndex(IS_FB_ACCOUNT)));
                _Model.setSession(_cursor.getString(_cursor.getColumnIndex(SESSION)));
                _cityList.add(_Model);
            }
        } catch (Exception e) {
            Log.e(EXTERNAL_CALENDAR_TABLE, "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return _cityList;
    }

    public ArrayList<ExternalAccountInfoModel> getAllExternalUserData() {
        ArrayList<ExternalAccountInfoModel> _List = new ArrayList<ExternalAccountInfoModel>();
        Cursor _cursor = null;
        try {
            String CREATE_QUERY = "select * from " + EXTERNAL_CALENDAR_TABLE;
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);
            while (_cursor.moveToNext()) {
                ExternalAccountInfoModel _Model = new ExternalAccountInfoModel();
                _Model.setId(_cursor.getInt(_cursor.getColumnIndex(PRIMARY_KEY)));
                _Model.setUserId(_cursor.getString(_cursor.getColumnIndex(USER_ID)));
                _Model.setIsFacebook(_cursor.getString(_cursor.getColumnIndex(IS_FB_ACCOUNT)));
                _Model.setSession(_cursor.getString(_cursor.getColumnIndex(SESSION)));
                _List.add(_Model);
            }
        } catch (Exception e) {
            Log.e(EXTERNAL_CALENDAR_TABLE, "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return _List;
    }


    public boolean checkAccountExists(String userid, boolean isFacebook) {
        Cursor _cursor = null;
        try {
            String CREATE_QUERY = "select * from " + EXTERNAL_CALENDAR_TABLE + " where " + USER_ID + " ='" + userid + "' and " + IS_FB_ACCOUNT + " ='" + isFacebook+"'";
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);
            if (_cursor.getCount() > 0)
                return true;

        } catch (Exception e) {
            Log.e(EXTERNAL_CALENDAR_TABLE, "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return false;
    }


    public void deleteBy_Id(int pId) {

        Cursor _cursor = null;
        try {

            int i = mWritableDatabase.delete(EXTERNAL_CALENDAR_TABLE, PRIMARY_KEY + " = " + pId, null);

        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }

    }
}