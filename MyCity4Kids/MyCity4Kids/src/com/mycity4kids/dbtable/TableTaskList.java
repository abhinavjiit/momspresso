package com.mycity4kids.dbtable;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.kelltontech.model.BaseModel;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.database.BaseTable;
import com.mycity4kids.newmodels.TaskListModel;

import java.util.ArrayList;

/**
 * Created by manish.soni on 10-07-2015.
 */
public class TableTaskList extends BaseTable {

    private static final String LIST_ID = "list_id";
    private static final String LIST_NAME = "list_name";
    private static final String SERVER_ID = "server_id";
    private static final String USER_ID = "user_id";
    private static final String FAMILY_ID = "family_id";


    /**
     * Table Name:-
     */

    public static final String TASK_LIST_TABLE = "taskListTable";

    public static final String CREATE_TASK_LIST_TABLE = "create table if not exists " +
            TASK_LIST_TABLE + " ( " +
//            PRIMARY_KEY + " integer primary key," +
            LIST_ID + " integer not null ," +
            LIST_NAME + " integer not null ," +
            USER_ID + " integer not null ," +
            FAMILY_ID + " integer not null ," +
            SERVER_ID + " text ,"
            + " UNIQUE(" + LIST_ID + " , " + USER_ID + "))";

    public static final String DROP_QUERY = "Drop table if exists " + TASK_LIST_TABLE;

    public TableTaskList(BaseApplication pApplication) {
        super(pApplication, TASK_LIST_TABLE);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected ContentValues getContentValues(BaseModel pModel) {
        TaskListModel taskListModel = (TaskListModel) pModel;

        ContentValues _contentValue = new ContentValues();
        _contentValue.put(LIST_NAME, taskListModel.getList_name());
        _contentValue.put(LIST_ID, taskListModel.getId());
        _contentValue.put(USER_ID, taskListModel.getUser_id());
        _contentValue.put(FAMILY_ID, taskListModel.getFamily_id());

        return _contentValue;
    }


    public void AddTaskList(String name, int id, int userid, int serverid) {

        try {
            mWritableDatabase.beginTransaction();
            ContentValues _contentValue = new ContentValues();
            _contentValue.put(LIST_NAME, name);
            _contentValue.put(LIST_ID, id);
            _contentValue.put(USER_ID, userid);
            _contentValue.put(SERVER_ID, serverid);

            mWritableDatabase.insert(TASK_LIST_TABLE, null, _contentValue);

            mWritableDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            mWritableDatabase.endTransaction();
        }

    }

    public void updateList(String name,int id)
    {
        try {
            ContentValues values = new ContentValues();

            values.put(LIST_NAME, name);

            int rowsEffected = mWritableDatabase.update(TASK_LIST_TABLE, values, LIST_ID+" = "+id,null);
            Log.e(TASK_LIST_TABLE, "rowsEffected: " + rowsEffected);
        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {

        }
    }


    @Override
    protected ArrayList<BaseModel> getAllData(String pSelection, String[] pSelectionArgs) {
        return null;
    }

    public void deleteList(int pId) {
        Cursor _cursor = null;
        try {
            int i = mWritableDatabase.delete(TASK_LIST_TABLE, LIST_ID + " = " + pId, null);
        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }

    }


    public TaskListModel getListbyID(int id) {

        TaskListModel taskListModel = new TaskListModel();


        Cursor _cursor = null;
        try {
            String CREATE_QUERY = "select * from " + TASK_LIST_TABLE + " where " + LIST_ID + " = " + id;
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);
            while (_cursor.moveToNext()) {

                taskListModel.setId(id);
                taskListModel.setList_name(_cursor.getString(_cursor.getColumnIndex(LIST_NAME)));
                taskListModel.setUser_id(_cursor.getInt(_cursor.getColumnIndex(USER_ID)));
                taskListModel.setFamily_id(_cursor.getInt(_cursor.getColumnIndex(FAMILY_ID)));

            }
        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return taskListModel;

    }

//    public void deleteListbyID(int id) {
//
//        Cursor _cursor = null;
//        try {
//
//            int i = mWritableDatabase.delete(TASK_LIST_TABLE, LIST_ID + " = " + id, null);
//
//        } catch (Exception e) {
//            Log.e("", "" + e);
//        } finally {
//            closeCursor(_cursor);
//        }
//
//    }

    public ArrayList<TaskListModel> getAllList(String userId) {

        ArrayList<TaskListModel> taskListModel = new ArrayList<>();

        Cursor _cursor = null;
        try {
            String CREATE_QUERY = "select * from " + TASK_LIST_TABLE;
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);
            while (_cursor.moveToNext()) {

                TaskListModel ListModel = new TaskListModel();

                ListModel.setId(_cursor.getInt(_cursor.getColumnIndex(LIST_ID)));
                ListModel.setList_name(_cursor.getString(_cursor.getColumnIndex(LIST_NAME)));
                ListModel.setUser_id(_cursor.getInt(_cursor.getColumnIndex(USER_ID)));
                ListModel.setFamily_id(_cursor.getInt(_cursor.getColumnIndex(FAMILY_ID)));

                Cursor _cursor_Count = null;
                try {
                    String CREATE_QUERY_TASKCOUNT = "select * from taskTable where list_id = " + ListModel.getId();
                    _cursor_Count = mWritableDatabase.rawQuery(CREATE_QUERY_TASKCOUNT, null);

                    ListModel.setSize(_cursor_Count.getCount());

                } catch (Exception e) {
                    Log.e("", "" + e);
                } finally {
                    closeCursor(_cursor_Count);
                }

                taskListModel.add(ListModel);

            }
        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return taskListModel;

    }

    public String getListName(int id) {

        String name = "";
        Cursor _cursor = null;
        try {
            String CREATE_QUERY = "select * from " + TASK_LIST_TABLE + " where " + LIST_ID + " = " + id;
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);

            while (_cursor.moveToNext()) {
                name = _cursor.getString(_cursor.getColumnIndex(LIST_NAME));
                break;

            }

        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return name;

    }
}
