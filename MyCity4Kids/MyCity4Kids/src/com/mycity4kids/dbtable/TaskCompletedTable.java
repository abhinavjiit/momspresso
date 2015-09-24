package com.mycity4kids.dbtable;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.kelltontech.model.BaseModel;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.database.BaseTable;
import com.mycity4kids.newmodels.TaskDataModel;

import java.util.ArrayList;

/**
 * Created by manish.soni on 18-06-2015.
 */
public class TaskCompletedTable extends BaseTable {

    private static final String PRIMARY_KEY = "_id";
    private static final String TASK__ID = "task_id";
    private static final String DATE = "date";
    private static final String SYNC = "sync";


    /**
     * Table Name:-
     */
    //UNIQUE (task_id, date) ON CONFLICT REPLACE
    public static final String TASK_COMPLETE_TABLE = "TaskCompleteTable";

    public static final String CREATE_TASK_COMPLETE_TABLE = "create table if not exists " +
            TASK_COMPLETE_TABLE + "(" + PRIMARY_KEY + " integer primary key," +
            TASK__ID + " integer not null ," +
            SYNC + " integer not null ," +
            DATE + " text not null,UNIQUE (task_id, date) ON CONFLICT REPLACE )";

    public static final String DROP_QUERY = "Drop table if exists " + TASK_COMPLETE_TABLE;

    public TaskCompletedTable(BaseApplication pApplication) {
        super(pApplication, TASK_COMPLETE_TABLE);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected ContentValues getContentValues(BaseModel pModel) {
        TaskDataModel.TaskCompleted model = (TaskDataModel.TaskCompleted) pModel;

        ContentValues _contentValue = new ContentValues();
        _contentValue.put(DATE, model.getExcluded_date());
        _contentValue.put(TASK__ID, model.getTask_id());
        _contentValue.put(SYNC, 1);


        return _contentValue;
    }


       public void updateSyncFlag(String date, int id) {
        try {
            ContentValues values = new ContentValues();

            values.put(SYNC, 1);

            int rowsEffected = mWritableDatabase.update(TASK_COMPLETE_TABLE, values, DATE + " ='" + date + "' AND " + TASK__ID + " = " + id, null);
            Log.e(TASK_COMPLETE_TABLE, "rowsEffected: " + rowsEffected);
        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {

        }
    }

    @Override
    protected ArrayList<BaseModel> getAllData(String pSelection, String[] pSelectionArgs) {
        return null;
    }


    public void deleteTask(String date, int id) {


        Cursor _cursor = null;
        try {

            int i = mWritableDatabase.delete(TASK_COMPLETE_TABLE, TASK__ID + " = " + id + " AND " + DATE + " ='" + date+"'", null);

        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }

    }

    public void AddTasks(String date, int taskid) {
        try {
            mWritableDatabase.beginTransaction();
            ContentValues _contentValue = new ContentValues();
            _contentValue.put(DATE, date);
            _contentValue.put(TASK__ID, taskid);
            _contentValue.put(SYNC, 0);
            mWritableDatabase.insert(TASK_COMPLETE_TABLE, null, _contentValue);

            mWritableDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            mWritableDatabase.endTransaction();
        }

    }



    public ArrayList<String> getDatesById(int id) {

        ArrayList<String> fileList = new ArrayList<>();
        Cursor _cursor = null;
        try {
            String CREATE_QUERY = "select * from " + TASK_COMPLETE_TABLE + " where " + TASK__ID + " = " + id + " AND " + SYNC + " = 0";
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);
            while (_cursor.moveToNext()) {

                fileList.add(_cursor.getString(_cursor.getColumnIndex(DATE)));

            }
        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return fileList;

    }

    public ArrayList<String> getCompletedDatesById(int id) {

        ArrayList<String> fileList = new ArrayList<>();
        Cursor _cursor = null;
        try {
            String CREATE_QUERY = "select * from " + TASK_COMPLETE_TABLE + " where " + TASK__ID + " = " + id + " AND " + SYNC + " = 1";
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);
            while (_cursor.moveToNext()) {

                fileList.add(_cursor.getString(_cursor.getColumnIndex(DATE)));

            }
        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return fileList;

    }


    public ArrayList<Integer> getIdList() {

        ArrayList<Integer> fileList = new ArrayList<>();
        Cursor _cursor = null;
        try {
            String CREATE_QUERY = "select DISTINCT " + TASK__ID + " from " + TASK_COMPLETE_TABLE + " WHERE " + SYNC + " = 0";
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);
            while (_cursor.moveToNext()) {

                fileList.add(_cursor.getInt(_cursor.getColumnIndex(TASK__ID)));

            }
        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return fileList;

    }


}
