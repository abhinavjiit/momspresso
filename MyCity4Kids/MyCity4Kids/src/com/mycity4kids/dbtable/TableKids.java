package com.mycity4kids.dbtable;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.kelltontech.model.BaseModel;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.database.BaseTable;
import com.mycity4kids.models.profile.KidsInformation;
import com.mycity4kids.models.user.KidsInfo;

import java.util.ArrayList;

public class TableKids extends BaseTable {
    private static final String PRIMARY_KEY = "_id";
    private static final String KID_NAME = "name";
    private static final String KID_COLOR_CODE = "colorCode";
    private static final String KID_BDAY = "bday";
    private static final String KIDID = "kid_id";

    /**
     * Table Name:-
     */

    public static final String KIDS_TABLE = "kidsTable";

    public static final String CREATE_KIDS_TABLE = "create table if not exists " +
            KIDS_TABLE + "(" + PRIMARY_KEY + " integer primary key," +
            KID_NAME + " text ," +
            KID_COLOR_CODE + " text  ," +
            KIDID + " integer  ," +
            KID_BDAY + " text )";

    public static final String DROP_QUERY = "Drop table if exists " + KIDS_TABLE;

    public TableKids(BaseApplication pApplication) {
        super(pApplication, KIDS_TABLE);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected ContentValues getContentValues(BaseModel pModel) {
        KidsInfo kidModel = (KidsInfo) pModel;
        ContentValues _contentValue = new ContentValues();
        _contentValue.put(KID_NAME, kidModel.getName());
        _contentValue.put(KID_COLOR_CODE, kidModel.getColor_code());
        _contentValue.put(KID_BDAY, kidModel.getDate_of_birth());
        _contentValue.put(KIDID, kidModel.getId());
        return _contentValue;
    }

    @Override
    protected ArrayList<BaseModel> getAllData(String pSelection,
                                              String[] pSelectionArgs) {
        // TODO Auto-generated method stub
        return null;
    }

    public ArrayList<KidsInfo> getAllKids() {
        ArrayList<KidsInfo> kidlist = new ArrayList<>();
        Cursor _cursor = null;
        try {
            String CREATE_QUERY = "select * from " + KIDS_TABLE;
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);
            while (_cursor.moveToNext()) {
                KidsInfo kidModel = new KidsInfo();
                kidModel.setName(_cursor.getString(_cursor.getColumnIndex(KID_NAME)));
                kidModel.setColor_code(_cursor.getString(_cursor.getColumnIndex(KID_COLOR_CODE)));
                kidModel.setDate_of_birth(_cursor.getString(_cursor.getColumnIndex(KID_BDAY)));
                kidModel.setId(_cursor.getString(_cursor.getColumnIndex(KIDID)));
                kidlist.add(kidModel);
            }
        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return kidlist;
    }

    public KidsInfo getKids(String id) {
        KidsInfo kid = new KidsInfo();
        Cursor _cursor = null;
        try {
            String CREATE_QUERY = "select * from " + KIDS_TABLE + " where " + KIDID + " = " + id;
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);
            while (_cursor.moveToNext()) {
                kid.setName(_cursor.getString(_cursor.getColumnIndex(KID_NAME)));
                kid.setColor_code(_cursor.getString(_cursor.getColumnIndex(KID_COLOR_CODE)));
                kid.setDate_of_birth(_cursor.getString(_cursor.getColumnIndex(KID_BDAY)));
                kid.setId(_cursor.getString(_cursor.getColumnIndex(KIDID)));
            }
        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return kid;
    }


    public void updateKIDS(KidsInformation model) {
        KidsInfo kid = new KidsInfo();
        Cursor _cursor = null;
        try {
            ContentValues values = new ContentValues();

            values.put(KID_BDAY, model.getDob());
            values.put(KID_COLOR_CODE, model.getColor_code());
            values.put(KID_NAME, model.getName());


            int rowsEffected = mWritableDatabase.update(KIDS_TABLE, values, KIDID + " = ? ", new String[]{String.valueOf(model.getKidid())});
            Log.e(KIDS_TABLE, "rowsEffected: " + rowsEffected);
        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }

    }

    public int getKidsCount() {

        int count = 0;
        Cursor _cursor = null;
        try {
            String CREATE_QUERY = "select * from " + KIDS_TABLE;
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);
            while (_cursor.moveToNext()) {
                count = _cursor.getCount();
            }
        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return count;
    }

}
