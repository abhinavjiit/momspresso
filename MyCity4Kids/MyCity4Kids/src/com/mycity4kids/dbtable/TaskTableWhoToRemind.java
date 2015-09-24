package com.mycity4kids.dbtable;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.kelltontech.model.BaseModel;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.database.BaseTable;
import com.mycity4kids.newmodels.AttendeeModel;
import com.mycity4kids.newmodels.TaskDataModel;

import java.util.ArrayList;

/**
 * Created by manish.soni on 18-06-2015.
 */
public class TaskTableWhoToRemind extends BaseTable {

    private static final String PRIMARY_KEY = "_id";
    private static final String EVENT_ID = "event_id";
    private static final String ADULT_ID = "userkid_id";
    private static final String SERVER_ID = "server_id";

    /**
     * Table Name:-
     */

    public static final String TASK_WHO_TO_REMIND_TABLE = "TaskWhoToRemindTable";

    public static final String CREATE_TASK_WHOTO_REMIND_TABLE = "create table if not exists " +
            TASK_WHO_TO_REMIND_TABLE + "(" + PRIMARY_KEY + " integer primary key," +
            EVENT_ID + " integer not null ," +
            SERVER_ID + " integer not null ," +
            ADULT_ID + " integer not null )";

    public static final String DROP_QUERY = "Drop table if exists " + TASK_WHO_TO_REMIND_TABLE;

    public TaskTableWhoToRemind(BaseApplication pApplication) {
        super(pApplication, TASK_WHO_TO_REMIND_TABLE);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected ContentValues getContentValues(BaseModel pModel) {
        TaskDataModel.WhoToRemind attendeemodel = (TaskDataModel.WhoToRemind) pModel;

        ContentValues _contentValue = new ContentValues();

        _contentValue.put(EVENT_ID, attendeemodel.getTask_id());
        _contentValue.put(ADULT_ID, attendeemodel.getUser_id());
        _contentValue.put(SERVER_ID, attendeemodel.getId());


        return _contentValue;
    }


    public void AddReminderList(BaseModel pModel) {
        TaskDataModel.WhoToRemind attendeemodel = (TaskDataModel.WhoToRemind) pModel;


        if (getDataByTask(attendeemodel.getTask_id()).size() > 0) {

            deleteTask(attendeemodel.getTask_id());
        }

        try {
            mWritableDatabase.beginTransaction();
            ContentValues _contentValue = new ContentValues();

            _contentValue.put(EVENT_ID, attendeemodel.getTask_id());
            _contentValue.put(ADULT_ID, attendeemodel.getUser_id());
            _contentValue.put(SERVER_ID, attendeemodel.getId());

            mWritableDatabase.insert(TASK_WHO_TO_REMIND_TABLE, null, _contentValue);

            mWritableDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            mWritableDatabase.endTransaction();
        }

    }


    public void deleteTask(int pId) {


        Cursor _cursor = null;
        try {

            int i = mWritableDatabase.delete(TASK_WHO_TO_REMIND_TABLE, EVENT_ID + " = " + pId, null);

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

    public ArrayList<AttendeeModel> getDataByTask(int id) {

        ArrayList<AttendeeModel> attendeeList = new ArrayList<>();


        Cursor _cursor = null;
        try {
            String CREATE_QUERY = "select * from TaskWhoToRemindTable  INNER JOIN adultTable  ON TaskWhoToRemindTable.userkid_id == adultTable.USERID where event_id=" + id;
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);
            while (_cursor.moveToNext()) {

                AttendeeModel attendee = new AttendeeModel();

                attendee.setAppoitmentId(id);
                attendee.setId(_cursor.getInt(_cursor.getColumnIndex(ADULT_ID)));
                attendee.setName(_cursor.getString(_cursor.getColumnIndex("name")));
                attendee.setColorCode(_cursor.getString(_cursor.getColumnIndex("colorCode")));
                attendee.setServerid(_cursor.getInt(_cursor.getColumnIndex(SERVER_ID)));

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
