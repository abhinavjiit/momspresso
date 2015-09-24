package com.mycity4kids.dbtable;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.kelltontech.model.BaseModel;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.database.BaseTable;
import com.mycity4kids.newmodels.AppoitmentDataModel;

import java.util.ArrayList;

public class TableApiEvents extends BaseTable {
    private static final String API_EVENT_ID = "api_event_id";
    private static final String PRIMARY_KEY = "_id";

    /**
     * Table Name:-
     */

    public static final String API_EVENTS_TABLE = "api_events";

    public static final String CREATE_API_EVENTS_TABLE = "create table if not exists " +
            API_EVENTS_TABLE + "(" + PRIMARY_KEY + " integer primary key," +
            API_EVENT_ID + " text )";

    public static final String DROP_QUERY = "Drop table if exists " + API_EVENTS_TABLE;

    public TableApiEvents(BaseApplication pApplication) {
        super(pApplication, API_EVENTS_TABLE);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected ContentValues getContentValues(BaseModel pModel) {
        AppoitmentDataModel.AppointmentDetail appointmentModel = (AppoitmentDataModel.AppointmentDetail) pModel;
        ContentValues _contentValue = new ContentValues();
        _contentValue.put(API_EVENT_ID, appointmentModel.getApi_event_id());

        return _contentValue;
    }

    @Override
    protected ArrayList<BaseModel> getAllData(String pSelection,
                                              String[] pSelectionArgs) {
        // TODO Auto-generated method stub
        return null;
    }

    public ArrayList<String> getApiEventIdList() {
        ArrayList<String> eventListing = new ArrayList<>();
        Cursor _cursor = null;
        try {

            String CREATE_QUERY = "select " + API_EVENT_ID + " from " + API_EVENTS_TABLE;
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);
            while (_cursor.moveToNext()) {
                eventListing.add(_cursor.getString(_cursor.getColumnIndex(API_EVENT_ID)));
            }
        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return eventListing;

    }

    public void deleteAppointment(String pId) {

        Cursor _cursor = null;
        try {
            int i = mWritableDatabase.delete(API_EVENTS_TABLE, API_EVENT_ID + " = '" + pId + "'", null);
        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }

    }
}
