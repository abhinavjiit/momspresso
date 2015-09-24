package com.mycity4kids.dbtable;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.kelltontech.model.BaseModel;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.database.BaseTable;
import com.mycity4kids.newmodels.AppoitmentDataModel;
import com.mycity4kids.newmodels.AttendeeModel;

import java.util.ArrayList;

/**
 * Created by manish.soni on 18-06-2015.
 */
public class TableAttendee extends BaseTable {

    private static final String PRIMARY_KEY = "_id";
    private static final String EVENT_ID = "event_id";
    private static final String SERVER_ID = "server_id";
    private static final String USERKID_ID = "userkid_id";
    private static final String TYPE = "type";

    /**
     * Table Name:-
     */

    public static final String ATTTENDEE_TABLE = "attendeeTable";

    public static final String CREATE_ATTTENDEE_TABLE = "create table if not exists " +
            ATTTENDEE_TABLE + "(" + PRIMARY_KEY + " integer primary key," +
            EVENT_ID + " integer not null ," +
            SERVER_ID + " integer not null ," +
            USERKID_ID + " integer not null ," +
            TYPE + " text not null )";

    public static final String DROP_QUERY = "Drop table if exists " + ATTTENDEE_TABLE;

    public TableAttendee(BaseApplication pApplication) {
        super(pApplication, ATTTENDEE_TABLE);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected ContentValues getContentValues(BaseModel pModel) {
        AppoitmentDataModel.Attendee attendeemodel = (AppoitmentDataModel.Attendee) pModel;

        ContentValues _contentValue = new ContentValues();
        _contentValue.put(TYPE, attendeemodel.getUk_type());
        _contentValue.put(EVENT_ID, attendeemodel.getAppointment_id());
        _contentValue.put(USERKID_ID, attendeemodel.getUk_id());
        _contentValue.put(SERVER_ID, attendeemodel.getId());


        return _contentValue;
    }

    public void AddAttendeeList(BaseModel pModel) {
        AppoitmentDataModel.Attendee attendeemodel = (AppoitmentDataModel.Attendee) pModel;



        try {
            mWritableDatabase.beginTransaction();
            ContentValues _contentValue = new ContentValues();
            _contentValue.put(TYPE, attendeemodel.getUk_type());
            _contentValue.put(EVENT_ID, attendeemodel.getAppointment_id());
            _contentValue.put(USERKID_ID, attendeemodel.getUk_id());
            _contentValue.put(SERVER_ID, attendeemodel.getId());

            mWritableDatabase.insert(ATTTENDEE_TABLE, null, _contentValue);

            mWritableDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            mWritableDatabase.endTransaction();
        }

    }

    public void deleteAppointment(int pId) {


        Cursor _cursor = null;
        try {

            int i = mWritableDatabase.delete(ATTTENDEE_TABLE, EVENT_ID + " = " + pId, null);

        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }

    }


    @Override
    protected ArrayList<BaseModel> getAllData(String pSelection, String[] pSelectionArgs) {
        return null;
    }


    public ArrayList<AttendeeModel> getDataByAppointment(int id) {

        ArrayList<AttendeeModel> attendeeList = new ArrayList<>();


        Cursor _cursor = null;
        try {
            String CREATE_QUERY = "select userkid_id ,server_id, type,kt.colorCode as kidcolor,kt.name as kidname,ad.colorCode as usercolorcode,ad.name as username from attendeeTable at LEFT JOIN kidsTable kt  ON at.userkid_id == kt.kid_id LEFT JOIN adultTable ad ON at.userkid_id == ad.USERID where event_id = " + id;

            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);
            while (_cursor.moveToNext()) {

                AttendeeModel attendee = new AttendeeModel();

                attendee.setAppoitmentId(id);
                attendee.setId(_cursor.getInt(_cursor.getColumnIndex(USERKID_ID)));
                attendee.setServerid(_cursor.getInt(_cursor.getColumnIndex(SERVER_ID)));


                String type = _cursor.getString(_cursor.getColumnIndex(TYPE));
                attendee.setType(type);

                if (type.equals("user")) {
                    attendee.setName(_cursor.getString(_cursor.getColumnIndex("username")));
                    attendee.setColorCode(_cursor.getString(_cursor.getColumnIndex("usercolorcode")));
                } else if (type.equals("kid")) {
                    attendee.setName(_cursor.getString(_cursor.getColumnIndex("kidname")));
                    attendee.setColorCode(_cursor.getString(_cursor.getColumnIndex("kidcolor")));
                }

                attendeeList.add(attendee);

            }
        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return attendeeList;

    }


    public ArrayList<AppoitmentDataModel.Attendee> getData(int id) {

        ArrayList<AppoitmentDataModel.Attendee> attendeeList = new ArrayList<>();


        Cursor _cursor = null;
        try {
            String CREATE_QUERY = "select userkid_id ,server_id, type,kt.colorCode as kidcolor,kt.name as kidname,ad.colorCode as usercolorcode,ad.name as username from attendeeTable at LEFT JOIN kidsTable kt  ON at.userkid_id == kt.kid_id LEFT JOIN adultTable ad ON at.userkid_id == ad.USERID where event_id = " + id;

            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);
            while (_cursor.moveToNext()) {

                AppoitmentDataModel.Attendee attendee = new AppoitmentDataModel().new Attendee();

                attendee.setAppointment_id(id);
                attendee.setUk_id(_cursor.getInt(_cursor.getColumnIndex(USERKID_ID)));
                attendee.setId(_cursor.getInt(_cursor.getColumnIndex(SERVER_ID)));

                String type = _cursor.getString(_cursor.getColumnIndex(TYPE));
                attendee.setUk_type(type);

                attendeeList.add(attendee);

            }
        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return attendeeList;

    }

}
