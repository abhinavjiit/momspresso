package com.mycity4kids.dbtable;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.kelltontech.model.BaseModel;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.database.BaseTable;
import com.mycity4kids.newmodels.AppoitmentDataModel;

import java.util.ArrayList;

/**
 * Created by manish.soni on 18-06-2015.
 */
public class TableNotes extends BaseTable {

    public static final String PRIMARY_KEY = "_id";
    public static final String EVENT_ID = "event_id";
    public static final String NOTES = "notes";
    public static final String SERVER_ID = "server_id";
    public static final String USER_ID = "user_id";


    /**
     * Table Name:-
     */

    public static final String NOTES_TABLE = "notesTable";

    public static final String CREATE_NOTES_TABLE = "create table if not exists " +
            NOTES_TABLE + "(" + PRIMARY_KEY + " integer primary key," +
            EVENT_ID + " integer not null ," +
            SERVER_ID + " integer not null ," +
            USER_ID + " integer not null ," +
            NOTES + " text not null )";

    public static final String DROP_QUERY = "Drop table if exists " + NOTES_TABLE;

    public TableNotes(BaseApplication pApplication) {
        super(pApplication, NOTES_TABLE);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected ContentValues getContentValues(BaseModel pModel) {
        AppoitmentDataModel.Notes attendeemodel = (AppoitmentDataModel.Notes) pModel;

        ContentValues _contentValue = new ContentValues();
        _contentValue.put(NOTES, attendeemodel.getNote());
        _contentValue.put(EVENT_ID, attendeemodel.getAppointment_id());
        _contentValue.put(USER_ID, attendeemodel.getUser_id());
        _contentValue.put(SERVER_ID, attendeemodel.getId());

        return _contentValue;
    }


    public void AddNotes(String notes, int eventid, int userid, int serverid) {
        try {
            mWritableDatabase.beginTransaction();
            ContentValues _contentValue = new ContentValues();
            _contentValue.put(NOTES, notes);
            _contentValue.put(EVENT_ID, eventid);
            _contentValue.put(USER_ID, userid);
            _contentValue.put(SERVER_ID, serverid);

            mWritableDatabase.insert(NOTES_TABLE, null, _contentValue);

            mWritableDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            mWritableDatabase.endTransaction();
        }

    }

    public void AddNoteList(BaseModel pModel) {
        AppoitmentDataModel.Notes attendeemodel = (AppoitmentDataModel.Notes) pModel;


//        if (getDataByAppointment(attendeemodel.getAppointment_id()).size() > 0) {
//
//            deleteAppointment(attendeemodel.getAppointment_id());
//        }

        try {
            mWritableDatabase.beginTransaction();
            ContentValues _contentValue = new ContentValues();
            _contentValue.put(NOTES, attendeemodel.getNote());
            _contentValue.put(EVENT_ID, attendeemodel.getAppointment_id());
            _contentValue.put(USER_ID, attendeemodel.getUser_id());
            _contentValue.put(SERVER_ID, attendeemodel.getId());


            mWritableDatabase.insert(NOTES_TABLE, null, _contentValue);

            mWritableDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            mWritableDatabase.endTransaction();
        }

    }


    @Override
    protected ArrayList<BaseModel> getAllData(String pSelection, String[] pSelectionArgs) {
        return null;
    }

    public void deleteAppointment(int pId) {


        Cursor _cursor = null;
        try {

            int i = mWritableDatabase.delete(NOTES_TABLE, EVENT_ID + " = " + pId, null);

        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }

    }


    public ArrayList<AppoitmentDataModel.Notes> getDataByAppointment(int id) {
        ArrayList<AppoitmentDataModel.Notes> fileList = new ArrayList<>();


        Cursor _cursor = null;
        try {
            String CREATE_QUERY = "select * from " + NOTES_TABLE + " where " + EVENT_ID + " = " + id;
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null, null);
            while (_cursor.moveToNext()) {


                AppoitmentDataModel.Notes files = new AppoitmentDataModel().new Notes();

                files.setAppointment_id(id);
                files.setNote(_cursor.getString(_cursor.getColumnIndex(NOTES)));
                files.setUser_id(_cursor.getInt(_cursor.getColumnIndex(USER_ID)));
                files.setId(_cursor.getInt(_cursor.getColumnIndex(SERVER_ID)));

                fileList.add(files);
            }
        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return fileList;

    }

    public ArrayList<AppoitmentDataModel.Notes> getDataByUserId(int id, int userid) {
        ArrayList<AppoitmentDataModel.Notes> fileList = new ArrayList<>();


        Cursor _cursor = null;
        try {
            String CREATE_QUERY = "select * from " + NOTES_TABLE + " where " + EVENT_ID + " = " + id + " AND " + USER_ID + " = " + userid;
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null, null);
            while (_cursor.moveToNext()) {


                AppoitmentDataModel.Notes files = new AppoitmentDataModel().new Notes();

                files.setAppointment_id(id);
                files.setNote(_cursor.getString(_cursor.getColumnIndex(NOTES)));
                files.setUser_id(_cursor.getInt(_cursor.getColumnIndex(USER_ID)));
                files.setId(_cursor.getInt(_cursor.getColumnIndex(SERVER_ID)));

                fileList.add(files);
            }
        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return fileList;

    }

    public int getCountById(int id) {

        int count = 0;
        Cursor _cursor_noteCount = null;
        try {
            String CREATE_QUERY = "select * from " + NOTES_TABLE + " where " + EVENT_ID + " = " + id;
            _cursor_noteCount = mWritableDatabase.rawQuery(CREATE_QUERY, null);

            count = _cursor_noteCount.getCount();

        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor_noteCount);
        }
        return count;
    }


}
