package com.mycity4kids.dbtable;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.kelltontech.model.BaseModel;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.database.BaseTable;
import com.mycity4kids.models.profile.SignUpModel;
import com.mycity4kids.models.user.UserModel;

import java.util.ArrayList;

public class TableFamily extends BaseTable {
    private static final String PRIMARY_KEY = "_id";
    private static final String FAMILY_NAME = "name";
    private static final String FAMILY_IMAGE = "image";
    private static final String FAMILY_PASSWORD = "family_pswd";
    private static final String FAMILY_CITY = "family_city";
    private static final String PINCODE = "pincode";
    private static final String FAMILY_ID = "fid";


    /**
     * Table Name:-
     */

    public static final String FAMILY_TABLE = "familyTable";

    public static final String CREATE_FAMILY_TABLE = "create table if not exists " +
            FAMILY_TABLE + "(" + PRIMARY_KEY + " integer primary key," +
            FAMILY_NAME + " text ," +
            FAMILY_IMAGE + " text  ," +
            PINCODE + " text," +
            FAMILY_CITY + " text," +
            FAMILY_ID + " integer," +
            FAMILY_PASSWORD + " text )";

    public static final String DROP_QUERY = "Drop table if exists " + FAMILY_TABLE;

    public TableFamily(BaseApplication pApplication) {
        super(pApplication, FAMILY_TABLE);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected ContentValues getContentValues(BaseModel pModel) {
        UserModel.FamilyInfo familyModel = ( UserModel.FamilyInfo) pModel;
        ContentValues _contentValue = new ContentValues();
        _contentValue.put(FAMILY_NAME, familyModel.getFamily_name());
        _contentValue.put(FAMILY_IMAGE, familyModel.getFamily_pic());
        _contentValue.put(FAMILY_PASSWORD, familyModel.getFamily_password());
        _contentValue.put(PINCODE, familyModel.getPincode());
        _contentValue.put(FAMILY_CITY, familyModel.getCity());
        _contentValue.put(FAMILY_ID, familyModel.getId());
        return _contentValue;
    }

    @Override
    protected ArrayList<BaseModel> getAllData(String pSelection,
                                              String[] pSelectionArgs) {
        // TODO Auto-generated method stub
        return null;
    }

    public  UserModel.FamilyInfo getFamily() {
        UserModel.FamilyInfo model = new UserModel().new FamilyInfo();
        Cursor _cursor = null;
        try {
            String CREATE_QUERY = "select * from " + FAMILY_TABLE;
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);
            while (_cursor.moveToNext()) {
                model.setFamily_name(_cursor.getString(_cursor.getColumnIndex(FAMILY_NAME)));
                model.setFamily_pic(_cursor.getString(_cursor.getColumnIndex(FAMILY_IMAGE)));
                model.setFamily_password(_cursor.getString(_cursor.getColumnIndex(FAMILY_PASSWORD)));
                model.setPincode(_cursor.getString(_cursor.getColumnIndex(PINCODE)));
                model.setCity(_cursor.getString(_cursor.getColumnIndex(FAMILY_CITY)));
                model.setId(_cursor.getInt(_cursor.getColumnIndex(FAMILY_ID)));
            }
        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return model;
    }
    public void updateVal(SignUpModel.Family model) {
        SignUpModel.User kid = new SignUpModel().new User();
        Cursor _cursor = null;
        try {
            ContentValues values = new ContentValues();

            values.put(FAMILY_NAME, model.getFamily_name());
            values.put(FAMILY_PASSWORD, model.getFamily_password());
            values.put(FAMILY_IMAGE, model.getFamily_image());

            int rowsEffected = mWritableDatabase.update(FAMILY_TABLE, values, null,null);
            Log.e(FAMILY_TABLE, "rowsEffected: " + rowsEffected);
        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }

    }

    public void updatePincode(String pincode)
    {
        try {
            ContentValues values = new ContentValues();

            values.put(PINCODE, pincode);

            int rowsEffected = mWritableDatabase.update(FAMILY_TABLE, values, null,null);
            Log.e(FAMILY_TABLE, "rowsEffected: " + rowsEffected);
        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {

        }
    }

}
