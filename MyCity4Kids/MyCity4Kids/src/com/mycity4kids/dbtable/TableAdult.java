package com.mycity4kids.dbtable;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.kelltontech.model.BaseModel;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.database.BaseTable;
import com.mycity4kids.models.profile.SignUpModel;
import com.mycity4kids.models.user.UserInfo;

import java.util.ArrayList;

public class TableAdult extends BaseTable {
    private static final String PRIMARY_KEY = "_id";
    private static final String ADULT_NAME = "name";
    private static final String ADULT_COLOR_CODE = "colorCode";
    private static final String ADULT_EMAIL = "EMAIL";
    private static final String ADULT_ID = "USERID";
    private static final String PINCODE = "pincode";

    /**
     * Table Name:-
     */

    public static final String ADULT_TABLE = "adultTable";

    public static final String CREATE_ADULT_TABLE = "create table if not exists " +
            ADULT_TABLE + "(" + PRIMARY_KEY + " integer primary key," +
            ADULT_NAME + " text ," +
            ADULT_COLOR_CODE + " text ," +
            PINCODE + " text ," +
            ADULT_ID + " integer ," +
            ADULT_EMAIL + " text  )";

    public static final String DROP_QUERY = "Drop table if exists " + ADULT_TABLE;

    public TableAdult(BaseApplication pApplication) {
        super(pApplication, ADULT_TABLE);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected ContentValues getContentValues(BaseModel pModel) {
        UserInfo adultModel = (UserInfo) pModel;
        ContentValues _contentValue = new ContentValues();
        _contentValue.put(ADULT_NAME, adultModel.getFirst_name() + " " + adultModel.getLast_name());
        _contentValue.put(ADULT_EMAIL, adultModel.getEmail());
        _contentValue.put(ADULT_ID, adultModel.getId());
        _contentValue.put(PINCODE, adultModel.getPincode());
        _contentValue.put(ADULT_COLOR_CODE, adultModel.getColor_code());
        return _contentValue;
    }

    public void updateVal(SignUpModel.User model) {
        SignUpModel.User kid = new SignUpModel().new User();
        Cursor _cursor = null;
        try {
            ContentValues values = new ContentValues();

            values.put(ADULT_NAME, model.getUsername());
            values.put(ADULT_EMAIL, model.getEmail());
            values.put(ADULT_COLOR_CODE, model.getColor_code());
            values.put(PINCODE, model.getPincode());

            int rowsEffected = mWritableDatabase.update(ADULT_TABLE, values, ADULT_ID + " = ? ", new String[]{String.valueOf(model.getId())});
            Log.e(ADULT_TABLE, "rowsEffected: " + rowsEffected);
        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }

    }


    @Override
    protected ArrayList<BaseModel> getAllData(String pSelection,
                                              String[] pSelectionArgs) {
        // TODO Auto-generated method stub
        return null;
    }

    public ArrayList<UserInfo> getAllAdults() {
        ArrayList<UserInfo> adultList = new ArrayList<>();
        Cursor _cursor = null;
        try {
            String CREATE_QUERY = "select * from " + ADULT_TABLE;
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);
            while (_cursor.moveToNext()) {
                UserInfo userModel = new UserInfo();
                userModel.setFirst_name(_cursor.getString(_cursor.getColumnIndex(ADULT_NAME)));
                userModel.setColor_code(_cursor.getString(_cursor.getColumnIndex(ADULT_COLOR_CODE)));
                userModel.setEmail(_cursor.getString(_cursor.getColumnIndex(ADULT_EMAIL)));
                userModel.setId(_cursor.getString(_cursor.getColumnIndex(ADULT_ID)));
                userModel.setPincode(_cursor.getString(_cursor.getColumnIndex(PINCODE)));

                adultList.add(userModel);
            }
        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return adultList;
    }

    public UserInfo getAdults(String id) {
        UserInfo adult = new UserInfo();
        Cursor _cursor = null;
        try {
            String CREATE_QUERY = "select * from " + ADULT_TABLE + " where " + ADULT_ID + " = " + id;

            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);
            while (_cursor.moveToNext()) {
                adult.setFirst_name(_cursor.getString(_cursor.getColumnIndex(ADULT_NAME)));
                adult.setColor_code(_cursor.getString(_cursor.getColumnIndex(ADULT_COLOR_CODE)));
                adult.setEmail(_cursor.getString(_cursor.getColumnIndex(ADULT_EMAIL)));
                adult.setId(_cursor.getString(_cursor.getColumnIndex(ADULT_ID)));
                adult.setPincode(_cursor.getString(_cursor.getColumnIndex(PINCODE)));

            }
        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return adult;
    }

    public int getAdultCount() {

        int count = 0;
        Cursor _cursor = null;
        try {
            String CREATE_QUERY = "select * from " + ADULT_TABLE;
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
