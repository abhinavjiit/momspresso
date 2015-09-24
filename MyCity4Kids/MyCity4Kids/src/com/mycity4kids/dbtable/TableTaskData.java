package com.mycity4kids.dbtable;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.kelltontech.model.BaseModel;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.database.BaseTable;
import com.mycity4kids.newmodels.AttendeeModel;
import com.mycity4kids.newmodels.TaskDataModel;
import com.mycity4kids.newmodels.TaskMappingModel;

import java.util.ArrayList;

/**
 * Created by manish.soni on 10-07-2015.
 */
public class TableTaskData extends BaseTable {

    private static final String PRIMARY_KEY = "_id";
    private static final String TASK_ID = "task_id";
    private static final String TASK_NAME = "task_name";
    private static final String TASK_LIST_ID = "list_id";
    private static final String STARTTIME = "start_time";
    private static final String REMINDER = "reminder";
    private static final String IS_RECURRING = "is_recurring";
    private static final String REPEAT = "repeat";
    private static final String REPEAT_FREQUENCY = "repeat_frequency";
    private static final String REPEAT_UNTIL = "repeat_until";
    private static final String REPEAT_NUM = "repeat_num";
    private static final String ACTIVE = "active";
    private static final String SYNC = "sync";
    private static final String USER_ID = "userid";

    /**
     * Table Name:-
     */

    public static final String TASK_TABLE = "taskTable";

    public static final String CREATE_TASK_TABLE = "create table if not exists " +
            TASK_TABLE + "(" + PRIMARY_KEY + " integer primary key," +
            TASK_NAME + " text ," +
            TASK_ID + " integer  ," +
            TASK_LIST_ID + " integer  ," +
            IS_RECURRING + " text  ," +
            STARTTIME + " long  ," +
            REMINDER + " text  ," +
            REPEAT + " text  ," +
            USER_ID + " text  ," +
            REPEAT_FREQUENCY + " text  ," +
            REPEAT_UNTIL + " text  ," +
            REPEAT_NUM + " text  ," +
            ACTIVE + " integer  ," +
            SYNC + " integer  )";

    public static final String DROP_QUERY = "Drop table if exists " + TASK_TABLE;

    public TableTaskData(BaseApplication pApplication) {
        super(pApplication, TASK_TABLE);
        // TODO Auto-generated constructor stub
    }


    protected ContentValues getContentValues(BaseModel pModel) {
        TaskDataModel.TaskDetail taskModel = (TaskDataModel.TaskDetail) pModel;

        ContentValues _contentValue = new ContentValues();
        _contentValue.put(TASK_NAME, taskModel.getTask_name());
        _contentValue.put(TASK_ID, taskModel.getId());
        _contentValue.put(TASK_LIST_ID, taskModel.getTask_list_id());
        _contentValue.put(IS_RECURRING, taskModel.getIs_recurring());
        _contentValue.put(STARTTIME, taskModel.getDue_date());
        _contentValue.put(REMINDER, taskModel.getReminder());
        _contentValue.put(REPEAT, taskModel.getRepeate());
        _contentValue.put(REPEAT_FREQUENCY, taskModel.getRepeate_frequency());
        _contentValue.put(REPEAT_NUM, taskModel.getRepeate_num());
        _contentValue.put(REPEAT_UNTIL, taskModel.getRepeate_untill());
        _contentValue.put(ACTIVE, 1);
        _contentValue.put(SYNC, 1);
        _contentValue.put(USER_ID, taskModel.getUser_id());
        return _contentValue;
    }


    public void AddTaskDetail(BaseModel pModel) {

        TaskDataModel.TaskDetail taskModel = (TaskDataModel.TaskDetail) pModel;


        if (getTaskbyId(taskModel.getId()).getId() > 0) {

            deleteTask(taskModel.getId());
        }

        try {
            mWritableDatabase.beginTransaction();
            ContentValues _contentValue = new ContentValues();
            _contentValue.put(TASK_NAME, taskModel.getTask_name());
            _contentValue.put(TASK_ID, taskModel.getId());
            _contentValue.put(TASK_LIST_ID, taskModel.getTask_list_id());
            _contentValue.put(IS_RECURRING, taskModel.getIs_recurring());
            _contentValue.put(STARTTIME, taskModel.getDue_date());
            _contentValue.put(REMINDER, taskModel.getReminder());
            _contentValue.put(REPEAT, taskModel.getRepeate());
            _contentValue.put(REPEAT_FREQUENCY, taskModel.getRepeate_frequency());
            _contentValue.put(REPEAT_NUM, taskModel.getRepeate_num());
            _contentValue.put(REPEAT_UNTIL, taskModel.getRepeate_untill());
            _contentValue.put(ACTIVE, taskModel.getActive());
            _contentValue.put(SYNC, 1);
            _contentValue.put(USER_ID, taskModel.getUser_id());

            mWritableDatabase.insert(TASK_TABLE, null, _contentValue);

            mWritableDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            mWritableDatabase.endTransaction();
        }

    }


    public long getMininumTimeStamp() {
        Cursor _cursor = null;
        try {
            String CREATE_QUERY = "SELECT MIN(start_time) AS TIME FROM " + TASK_TABLE;
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);
            while (_cursor.moveToNext()) {

                return _cursor.getLong(_cursor.getColumnIndex("TIME"));
            }


        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return 0;
    }

    @Override
    protected ArrayList<BaseModel> getAllData(String pSelection, String[] pSelectionArgs) {
        return null;
    }

    public ArrayList<Integer> getInActiveTasksData() {

        ArrayList<Integer> idList = new ArrayList<>();

        Cursor _cursor = null;
        try {
            String CREATE_QUERY = "select * from " + TASK_TABLE + " where " + ACTIVE + " = 0 ";
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);
            while (_cursor.moveToNext()) {
                idList.add(_cursor.getInt(_cursor.getColumnIndex(TASK_ID)));
            }

            return idList;
        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }


        return null;
    }


    public TaskDataModel.TaskDetail getTaskbyId(int id) {
        TaskDataModel.TaskDetail taskDetail = new TaskDataModel().new TaskDetail();
        Cursor _cursor = null;
        try {
            String CREATE_QUERY = "select * from " + TASK_TABLE + " where " + TASK_ID + " = " + id;
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);
            while (_cursor.moveToNext()) {
                taskDetail.setId(_cursor.getInt(_cursor.getColumnIndex(TASK_ID)));
                taskDetail.setTask_name(_cursor.getString(_cursor.getColumnIndex(TASK_NAME)));
                taskDetail.setDue_date(_cursor.getLong(_cursor.getColumnIndex(STARTTIME)));
                taskDetail.setReminder(_cursor.getString(_cursor.getColumnIndex(REMINDER)));
                taskDetail.setIs_recurring(_cursor.getString(_cursor.getColumnIndex(IS_RECURRING)));
                taskDetail.setRepeate(_cursor.getString(_cursor.getColumnIndex(REPEAT)));
                taskDetail.setRepeate_frequency(_cursor.getString(_cursor.getColumnIndex(REPEAT_FREQUENCY)));
                taskDetail.setRepeate_num(_cursor.getString(_cursor.getColumnIndex(REPEAT_NUM)));
                taskDetail.setRepeate_untill(_cursor.getString(_cursor.getColumnIndex(REPEAT_UNTIL)));
//                taskDetail.setActive(_cursor.getInt(_cursor.getColumnIndex(ACTIVE)));
                taskDetail.setTask_list_id(_cursor.getInt(_cursor.getColumnIndex(TASK_LIST_ID)));

                int listId = (_cursor.getInt(_cursor.getColumnIndex(TASK_LIST_ID)));

                Cursor _cursorList = null;
                try {
                    String CREATE_QUERY_LIST = "select * from taskListTable where list_id =" + listId;

                    _cursorList = mWritableDatabase.rawQuery(CREATE_QUERY_LIST, null);
                    while (_cursorList.moveToNext()) {


                        taskDetail.setListName(_cursorList.getString(_cursorList.getColumnIndex("list_name")));
                    }
                } catch (Exception e) {
                    Log.e("", "" + e);
                } finally {
                    closeCursor(_cursorList);
                }

                //appointmentDetail.setOffline_id(_cursor.getInt(_cursor.getColumnIndex(PRIMARY_KEY)));
            }
        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return taskDetail;

    }


    public ArrayList<TaskMappingModel> allDataBTWNdays(long first, long second, Boolean isListSelected, int IdList, int userId) {

        ArrayList<TaskMappingModel> allData = new ArrayList<TaskMappingModel>();

        Cursor _cursor = null;

        try {

            String CREATE_QUERY = "";

            if (isListSelected) {
                CREATE_QUERY = " select * from " + TASK_TABLE + " where " + STARTTIME + " >= " + first + " and " + STARTTIME + " <= " + second + " and active = 1 and list_id = " + IdList + " OR is_recurring = 'yes' and active = 1 and list_id = " + IdList + "  order by start_time asc";
            } else {
                CREATE_QUERY = " select * from " + TASK_TABLE + " where " + STARTTIME + " >= " + first + " and " + STARTTIME + " <= " + second + " and active = 1 OR is_recurring = 'yes'  order by start_time asc";
            }

            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);

            if (_cursor.moveToFirst()) {
                do {

                    TaskMappingModel taskMappingModel = new TaskMappingModel();

//                    taskMappingModel.setpId(_cursor.getInt(_cursor.getColumnIndex(PRIMARY_KEY)));
                    taskMappingModel.setTask_id(_cursor.getInt(_cursor.getColumnIndex(TASK_ID)));
                    taskMappingModel.setTaskName(_cursor.getString(_cursor.getColumnIndex(TASK_NAME)));
                    taskMappingModel.setTaskDate(_cursor.getLong(_cursor.getColumnIndex(STARTTIME)));
                    taskMappingModel.setIs_recurring(_cursor.getString(_cursor.getColumnIndex(IS_RECURRING)));
                    taskMappingModel.setRepeat(_cursor.getString(_cursor.getColumnIndex(REPEAT)));
                    taskMappingModel.setRepeate_num(_cursor.getString(_cursor.getColumnIndex(REPEAT_NUM)));
                    taskMappingModel.setRepeate_frequency(_cursor.getString(_cursor.getColumnIndex(REPEAT_FREQUENCY)));
                    taskMappingModel.setRepeate_untill(_cursor.getString(_cursor.getColumnIndex(REPEAT_UNTIL)));
                    taskMappingModel.setActive(_cursor.getInt(_cursor.getColumnIndex(ACTIVE)));
                    int listId = (_cursor.getInt(_cursor.getColumnIndex(TASK_LIST_ID)));

//                    fetching attendees BD

                    ArrayList<AttendeeModel> attendeeList = new ArrayList<>();
                    Cursor _cursorAttendee = null;
                    try {
                        String CREATE_QUERY_ATTENDEE = "select userkid_id ,server_id, type,kt.colorCode as kidcolor,kt.name as kidname,ad.colorCode as usercolorcode,ad.name as username from taskAttendeeTable at LEFT JOIN kidsTable kt  ON at.userkid_id == kt.kid_id LEFT JOIN adultTable ad ON at.userkid_id == ad.USERID where event_id = " + taskMappingModel.getTask_id();

                        _cursorAttendee = mWritableDatabase.rawQuery(CREATE_QUERY_ATTENDEE, null);
                        while (_cursorAttendee.moveToNext()) {

                            AttendeeModel attendee = new AttendeeModel();

                            attendee.setAppoitmentId(taskMappingModel.getTask_id());
                            attendee.setId(_cursorAttendee.getInt(_cursorAttendee.getColumnIndex("userkid_id")));
                            attendee.setServerid(_cursorAttendee.getInt(_cursorAttendee.getColumnIndex("server_id")));

                            String type = _cursorAttendee.getString(_cursorAttendee.getColumnIndex("type"));

                            if (type.equals("user")) {
                                attendee.setName(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("username")));
                                attendee.setColorCode(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("usercolorcode")));
                            } else if (type.equals("kid")) {
                                attendee.setName(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("kidname")));
                                attendee.setColorCode(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("kidcolor")));
                            }

                            attendeeList.add(attendee);
                        }
                    } catch (Exception e) {
                        Log.e("", "" + e);
                    } finally {
                        closeCursor(_cursorAttendee);
                    }

                    taskMappingModel.setAttendees(attendeeList);


                    Cursor _cursorList = null;
                    try {
                        String CREATE_QUERY_LIST = "select * from taskListTable where list_id = " + listId;

                        _cursorList = mWritableDatabase.rawQuery(CREATE_QUERY_LIST, null);
                        while (_cursorList.moveToNext()) {


                            taskMappingModel.setTaskListname(_cursorList.getString(_cursorList.getColumnIndex("list_name")));
                        }
                    } catch (Exception e) {
                        Log.e("", "" + e);
                    } finally {
                        closeCursor(_cursorList);
                    }

                    Cursor _cursor_noteCount = null;
                    try {
                        String CREATE_QUERY_COUNT = "select * from TaskNotesTable where event_id = " + taskMappingModel.getTask_id();
                        _cursor_noteCount = mWritableDatabase.rawQuery(CREATE_QUERY_COUNT, null);

                        taskMappingModel.setNumberNotes(_cursor_noteCount.getCount());

                    } catch (Exception e) {
                        Log.e("", "" + e);
                    } finally {
                        closeCursor(_cursor_noteCount);
                    }


                    allData.add(taskMappingModel);

                } while (_cursor.moveToNext());
            }

        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return allData;
    }


    public ArrayList<TaskDataModel.TaskDetail> getAll() {

        ArrayList<TaskDataModel.TaskDetail> allData = new ArrayList<>();
        Cursor _cursor = null;
        try {

            String CREATE_QUERY = "select * from " + TASK_TABLE;
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);

            if (_cursor.moveToFirst()) {
                do {

                    TaskDataModel.TaskDetail taskDetail = new TaskDataModel().new TaskDetail();

                    taskDetail.setId(_cursor.getInt(_cursor.getColumnIndex(TASK_ID)));
                    taskDetail.setTask_name(_cursor.getString(_cursor.getColumnIndex(TASK_NAME)));
                    taskDetail.setDue_date(_cursor.getLong(_cursor.getColumnIndex(STARTTIME)));
                    taskDetail.setReminder(_cursor.getString(_cursor.getColumnIndex(REMINDER)));
                    taskDetail.setIs_recurring(_cursor.getString(_cursor.getColumnIndex(IS_RECURRING)));
                    taskDetail.setRepeate(_cursor.getString(_cursor.getColumnIndex(REPEAT)));
                    taskDetail.setRepeate_frequency(_cursor.getString(_cursor.getColumnIndex(REPEAT_FREQUENCY)));
                    taskDetail.setRepeate_num(_cursor.getString(_cursor.getColumnIndex(REPEAT_NUM)));
                    taskDetail.setRepeate_untill(_cursor.getString(_cursor.getColumnIndex(REPEAT_UNTIL)));
                    allData.add(taskDetail);

                } while (_cursor.moveToNext());
            }

        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return allData;
    }


    public ArrayList<TaskMappingModel> getBackDaysData(long first, Boolean isListSelected, int idList, int userId) {

        ArrayList<TaskMappingModel> allData = new ArrayList<TaskMappingModel>();

        Cursor _cursor = null;

        try {
            String CREATE_QUERY = "";

            if (isListSelected) {
                CREATE_QUERY = " select * from " + TASK_TABLE + " where " + STARTTIME + " < " + first + " and active = 1 and list_id = " + idList + " order by start_time desc";
            } else {
                CREATE_QUERY = " select * from " + TASK_TABLE + " where " + STARTTIME + " < " + first + " and active = 1" + " order by start_time desc";
            }

            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);

            if (_cursor.moveToFirst()) {
                do {

                    TaskMappingModel taskMappingModel = new TaskMappingModel();

//                    taskMappingModel.setpId(_cursor.getInt(_cursor.getColumnIndex(PRIMARY_KEY)));
                    taskMappingModel.setTask_id(_cursor.getInt(_cursor.getColumnIndex(TASK_ID)));
                    taskMappingModel.setTaskName(_cursor.getString(_cursor.getColumnIndex(TASK_NAME)));
                    taskMappingModel.setTaskDate(_cursor.getLong(_cursor.getColumnIndex(STARTTIME)));

                    taskMappingModel.setIs_recurring(_cursor.getString(_cursor.getColumnIndex(IS_RECURRING)));
                    taskMappingModel.setRepeat(_cursor.getString(_cursor.getColumnIndex(REPEAT)));
                    taskMappingModel.setRepeate_num(_cursor.getString(_cursor.getColumnIndex(REPEAT_NUM)));
                    taskMappingModel.setRepeate_frequency(_cursor.getString(_cursor.getColumnIndex(REPEAT_FREQUENCY)));
                    taskMappingModel.setRepeate_untill(_cursor.getString(_cursor.getColumnIndex(REPEAT_UNTIL)));
//                    taskMappingModel.setIs_recurring(_cursor.getString(_cursor.getColumnIndex(IS_RECURRING)));
//                    taskMappingModel.setRepeat(_cursor.getString(_cursor.getColumnIndex(REPEAT)));
//                    taskMappingModel.setRepeate_num(_cursor.getString(_cursor.getColumnIndex(REPEAT_NUM)));
//                    taskMappingModel.setRepeate_frequency(_cursor.getString(_cursor.getColumnIndex(REPEAT_FREQUENCY)));
//                    taskMappingModel.setRepeate_untill(_cursor.getString(_cursor.getColumnIndex(REPEAT_UNTIL)));
                    taskMappingModel.setActive(_cursor.getInt(_cursor.getColumnIndex(ACTIVE)));
                    int listId = (_cursor.getInt(_cursor.getColumnIndex(TASK_LIST_ID)));

//                    fetching attendees BD

                    ArrayList<AttendeeModel> attendeeList = new ArrayList<>();
                    Cursor _cursorAttendee = null;
                    try {
                        String CREATE_QUERY_ATTENDEE = "select userkid_id ,server_id, type,kt.colorCode as kidcolor,kt.name as kidname,ad.colorCode as usercolorcode,ad.name as username from taskAttendeeTable at LEFT JOIN kidsTable kt  ON at.userkid_id == kt.kid_id LEFT JOIN adultTable ad ON at.userkid_id == ad.USERID where event_id = " + taskMappingModel.getTask_id();

                        _cursorAttendee = mWritableDatabase.rawQuery(CREATE_QUERY_ATTENDEE, null);
                        while (_cursorAttendee.moveToNext()) {

                            AttendeeModel attendee = new AttendeeModel();

                            attendee.setAppoitmentId(taskMappingModel.getTask_id());
                            attendee.setId(_cursorAttendee.getInt(_cursorAttendee.getColumnIndex("userkid_id")));
                            attendee.setServerid(_cursorAttendee.getInt(_cursorAttendee.getColumnIndex("server_id")));

                            String type = _cursorAttendee.getString(_cursorAttendee.getColumnIndex("type"));

                            if (type.equals("user")) {
                                attendee.setName(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("username")));
                                attendee.setColorCode(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("usercolorcode")));
                            } else if (type.equals("kid")) {
                                attendee.setName(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("kidname")));
                                attendee.setColorCode(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("kidcolor")));
                            }

                            attendeeList.add(attendee);
                        }
                    } catch (Exception e) {
                        Log.e("", "" + e);
                    } finally {
                        closeCursor(_cursorAttendee);
                    }

                    taskMappingModel.setAttendees(attendeeList);


                    Cursor _cursorList = null;
                    try {
                        String CREATE_QUERY_LIST = "select * from taskListTable where list_id = " + listId;

                        _cursorList = mWritableDatabase.rawQuery(CREATE_QUERY_LIST, null);
                        while (_cursorList.moveToNext()) {


                            taskMappingModel.setTaskListname(_cursorList.getString(_cursorList.getColumnIndex("list_name")));
                        }
                    } catch (Exception e) {
                        Log.e("", "" + e);
                    } finally {
                        closeCursor(_cursorList);
                    }

                    Cursor _cursor_noteCount = null;
                    try {
                        String CREATE_QUERY_COUNT = "select * from TaskNotesTable where event_id = " + taskMappingModel.getTask_id();
                        _cursor_noteCount = mWritableDatabase.rawQuery(CREATE_QUERY_COUNT, null);

                        taskMappingModel.setNumberNotes(_cursor_noteCount.getCount());

                    } catch (Exception e) {
                        Log.e("", "" + e);
                    } finally {
                        closeCursor(_cursor_noteCount);
                    }


                    allData.add(taskMappingModel);
                } while (_cursor.moveToNext());
            }

        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return allData;
    }

    public ArrayList<TaskMappingModel> allDataBTWNdays_Search(long first, long second, Boolean flag, int IdList, String name) {

        ArrayList<TaskMappingModel> allData = new ArrayList<TaskMappingModel>();

        Cursor _cursor = null;
        try {
            String CREATE_QUERY = "";
            if (flag) {
                CREATE_QUERY = " select * from " + TASK_TABLE + " where " + STARTTIME + " >= " + first + " and " + STARTTIME + " <= " + second + " and active = 1 and list_id = " + IdList + " and task_name like '%" + name + "%'" + " order by start_time asc";
            } else {
                CREATE_QUERY = " select * from " + TASK_TABLE + " where " + STARTTIME + " >= " + first + " and " + STARTTIME + " <= " + second + " and active = 1 and task_name like '%" + name + "%'" + " order by start_time asc";
            }

            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);
            if (_cursor.moveToFirst()) {
                do {
                    TaskMappingModel taskMappingModel = new TaskMappingModel();

//                    taskMappingModel.setpId(_cursor.getInt(_cursor.getColumnIndex(PRIMARY_KEY)));
                    taskMappingModel.setTask_id(_cursor.getInt(_cursor.getColumnIndex(TASK_ID)));
                    taskMappingModel.setTaskName(_cursor.getString(_cursor.getColumnIndex(TASK_NAME)));
                    taskMappingModel.setTaskDate(_cursor.getLong(_cursor.getColumnIndex(STARTTIME)));
//                    taskMappingModel.setIs_recurring(_cursor.getString(_cursor.getColumnIndex(IS_RECURRING)));
//                    taskMappingModel.setRepeat(_cursor.getString(_cursor.getColumnIndex(REPEAT)));
//                    taskMappingModel.setRepeate_num(_cursor.getString(_cursor.getColumnIndex(REPEAT_NUM)));
//                    taskMappingModel.setRepeate_frequency(_cursor.getString(_cursor.getColumnIndex(REPEAT_FREQUENCY)));
//                    taskMappingModel.setRepeate_untill(_cursor.getString(_cursor.getColumnIndex(REPEAT_UNTIL)));
                    taskMappingModel.setActive(_cursor.getInt(_cursor.getColumnIndex(ACTIVE)));
                    int listId = (_cursor.getInt(_cursor.getColumnIndex(TASK_LIST_ID)));

//                    fetching attendees BD

                    ArrayList<AttendeeModel> attendeeList = new ArrayList<>();
                    Cursor _cursorAttendee = null;
                    try {
                        String CREATE_QUERY_ATTENDEE = "select userkid_id ,server_id, type,kt.colorCode as kidcolor,kt.name as kidname,ad.colorCode as usercolorcode,ad.name as username from taskAttendeeTable at LEFT JOIN kidsTable kt  ON at.userkid_id == kt.kid_id LEFT JOIN adultTable ad ON at.userkid_id == ad.USERID where event_id = " + taskMappingModel.getTask_id();

                        _cursorAttendee = mWritableDatabase.rawQuery(CREATE_QUERY_ATTENDEE, null);
                        while (_cursorAttendee.moveToNext()) {

                            AttendeeModel attendee = new AttendeeModel();
                            attendee.setAppoitmentId(taskMappingModel.getTask_id());
                            attendee.setId(_cursorAttendee.getInt(_cursorAttendee.getColumnIndex("userkid_id")));
                            attendee.setServerid(_cursorAttendee.getInt(_cursorAttendee.getColumnIndex("server_id")));
                            String type = _cursorAttendee.getString(_cursorAttendee.getColumnIndex("type"));

                            if (type.equals("user")) {
                                attendee.setName(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("username")));
                                attendee.setColorCode(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("usercolorcode")));
                            } else if (type.equals("kid")) {
                                attendee.setName(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("kidname")));
                                attendee.setColorCode(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("kidcolor")));
                            }
                            attendeeList.add(attendee);
                        }
                    } catch (Exception e) {
                        Log.e("", "" + e);
                    } finally {
                        closeCursor(_cursorAttendee);
                    }
                    taskMappingModel.setAttendees(attendeeList);
                    Cursor _cursorList = null;
                    try {
                        String CREATE_QUERY_LIST = "select * from taskListTable where list_id = " + listId;

                        _cursorList = mWritableDatabase.rawQuery(CREATE_QUERY_LIST, null);
                        while (_cursorList.moveToNext()) {
                            taskMappingModel.setTaskListname(_cursorList.getString(_cursorList.getColumnIndex("list_name")));
                        }
                    } catch (Exception e) {
                        Log.e("", "" + e);
                    } finally {
                        closeCursor(_cursorList);
                    }

                    Cursor _cursor_noteCount = null;
                    try {
                        String CREATE_QUERY_COUNT = "select * from TaskNotesTable where event_id = " + taskMappingModel.getTask_id();
                        _cursor_noteCount = mWritableDatabase.rawQuery(CREATE_QUERY_COUNT, null);

                        taskMappingModel.setNumberNotes(_cursor_noteCount.getCount());

                    } catch (Exception e) {
                        Log.e("", "" + e);
                    } finally {
                        closeCursor(_cursor_noteCount);
                    }


                    allData.add(taskMappingModel);

                } while (_cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return allData;
    }


    public ArrayList<TaskMappingModel> getBackDaysData_Search(long first, Boolean flag, int idList, String name) {

        ArrayList<TaskMappingModel> allData = new ArrayList<TaskMappingModel>();

        Cursor _cursor = null;

        try {
            String CREATE_QUERY = "";

            if (flag) {
                CREATE_QUERY = " select * from " + TASK_TABLE + " where " + STARTTIME + " < " + first + " and active = 1 and list_id = " + idList + " and task_name like '%" + name + "%'" + " order by start_time desc";
            } else {
                CREATE_QUERY = " select * from " + TASK_TABLE + " where " + STARTTIME + " < " + first + " and active = 1 " + " and task_name like '%" + name + "%'" + " order by start_time desc";
            }
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);

            if (_cursor.moveToFirst()) {
                do {

                    TaskMappingModel taskMappingModel = new TaskMappingModel();

//                    taskMappingModel.setpId(_cursor.getInt(_cursor.getColumnIndex(PRIMARY_KEY)));
                    taskMappingModel.setTask_id(_cursor.getInt(_cursor.getColumnIndex(TASK_ID)));
                    taskMappingModel.setTaskName(_cursor.getString(_cursor.getColumnIndex(TASK_NAME)));
                    taskMappingModel.setTaskDate(_cursor.getLong(_cursor.getColumnIndex(STARTTIME)));
//                    taskMappingModel.setIs_recurring(_cursor.getString(_cursor.getColumnIndex(IS_RECURRING)));
//                    taskMappingModel.setRepeat(_cursor.getString(_cursor.getColumnIndex(REPEAT)));
//                    taskMappingModel.setRepeate_num(_cursor.getString(_cursor.getColumnIndex(REPEAT_NUM)));
//                    taskMappingModel.setRepeate_frequency(_cursor.getString(_cursor.getColumnIndex(REPEAT_FREQUENCY)));
//                    taskMappingModel.setRepeate_untill(_cursor.getString(_cursor.getColumnIndex(REPEAT_UNTIL)));
                    taskMappingModel.setActive(_cursor.getInt(_cursor.getColumnIndex(ACTIVE)));
                    int listId = (_cursor.getInt(_cursor.getColumnIndex(TASK_LIST_ID)));

//                    fetching attendees BD

                    ArrayList<AttendeeModel> attendeeList = new ArrayList<>();
                    Cursor _cursorAttendee = null;
                    try {
                        String CREATE_QUERY_ATTENDEE = "select userkid_id ,server_id, type,kt.colorCode as kidcolor,kt.name as kidname,ad.colorCode as usercolorcode,ad.name as username from taskAttendeeTable at LEFT JOIN kidsTable kt  ON at.userkid_id == kt.kid_id LEFT JOIN adultTable ad ON at.userkid_id == ad.USERID where event_id = " + taskMappingModel.getTask_id();

                        _cursorAttendee = mWritableDatabase.rawQuery(CREATE_QUERY_ATTENDEE, null);
                        while (_cursorAttendee.moveToNext()) {

                            AttendeeModel attendee = new AttendeeModel();

                            attendee.setAppoitmentId(taskMappingModel.getTask_id());
                            attendee.setId(_cursorAttendee.getInt(_cursorAttendee.getColumnIndex("userkid_id")));
                            attendee.setServerid(_cursorAttendee.getInt(_cursorAttendee.getColumnIndex("server_id")));

                            String type = _cursorAttendee.getString(_cursorAttendee.getColumnIndex("type"));
                            if (type.equals("user")) {
                                attendee.setName(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("username")));
                                attendee.setColorCode(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("usercolorcode")));
                            } else if (type.equals("kid")) {
                                attendee.setName(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("kidname")));
                                attendee.setColorCode(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("kidcolor")));
                            }
                            attendeeList.add(attendee);
                        }
                    } catch (Exception e) {
                        Log.e("", "" + e);
                    } finally {
                        closeCursor(_cursorAttendee);
                    }
                    taskMappingModel.setAttendees(attendeeList);

                    Cursor _cursorList = null;
                    try {
                        String CREATE_QUERY_LIST = "select * from taskListTable where list_id = " + listId;
                        _cursorList = mWritableDatabase.rawQuery(CREATE_QUERY_LIST, null);
                        while (_cursorList.moveToNext()) {
                            taskMappingModel.setTaskListname(_cursorList.getString(_cursorList.getColumnIndex("list_name")));
                        }
                    } catch (Exception e) {
                        Log.e("", "" + e);
                    } finally {
                        closeCursor(_cursorList);
                    }

                    Cursor _cursor_noteCount = null;
                    try {
                        String CREATE_QUERY_COUNT = "select * from TaskNotesTable where event_id = " + taskMappingModel.getTask_id();
                        _cursor_noteCount = mWritableDatabase.rawQuery(CREATE_QUERY_COUNT, null);

                        taskMappingModel.setNumberNotes(_cursor_noteCount.getCount());

                    } catch (Exception e) {
                        Log.e("", "" + e);
                    } finally {
                        closeCursor(_cursor_noteCount);
                    }


                    allData.add(taskMappingModel);
                } while (_cursor.moveToNext());
            }

        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return allData;
    }

    public void inActiveAppointment(int pId) {
        Cursor _cursor = null;
        try {
            ContentValues valuse = new ContentValues();
            valuse.put(ACTIVE, 0);
            mWritableDatabase.update(TASK_TABLE, valuse, TASK_ID + " = " + pId, null);
        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }
    }

    public void ActiveAppointment(int pId) {
        Cursor _cursor = null;
        try {
            ContentValues valuse = new ContentValues();
            valuse.put(ACTIVE, 1);
            mWritableDatabase.update(TASK_TABLE, valuse, TASK_ID + " = " + pId, null);
        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }
    }


    public void CompleteTaskFlag(int pId, int flag) {
        Cursor _cursor = null;
        try {
            ContentValues valuse = new ContentValues();
            valuse.put(ACTIVE, flag);
            mWritableDatabase.update(TASK_TABLE, valuse, TASK_ID + " = " + pId, null);
        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }
    }


//    public AppoitmentDataModel.AppointmentDetail getAppointment(int event_id) {
//
//        TaskDataModel.TaskDetail taskDetail = new TaskDataModel().new TaskDetail();
//
//        Cursor _cursor = null;
//
//        try {
//            String CREATE_QUERY = " select * from " + TASK_TABLE + " where event_id = " + event_id;
//            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null, null);
//
//            if (_cursor.moveToFirst()) {
//                do {
//
//                    appointmentDetail.setId(_cursor.getInt(_cursor.getColumnIndex(TASK_ID)));
//                    appointmentDetail.setAppointment_name(_cursor.getString(_cursor.getColumnIndex(TASK_NAME)));
//                    appointmentDetail.setStarttime(_cursor.getLong(_cursor.getColumnIndex(STARTTIME)));
//                    appointmentDetail.setReminder(_cursor.getString(_cursor.getColumnIndex(REMINDER)));
//                    appointmentDetail.setIs_recurring(_cursor.getString(_cursor.getColumnIndex(IS_RECURRING)));
//                    appointmentDetail.setRepeate(_cursor.getString(_cursor.getColumnIndex(REPEAT)));
//                    appointmentDetail.setRepeate_frequency(_cursor.getString(_cursor.getColumnIndex(REPEAT_FREQUENCY)));
//                    appointmentDetail.setRepeate_num(_cursor.getString(_cursor.getColumnIndex(REPEAT_NUM)));
//                    appointmentDetail.setRepeate_untill(_cursor.getString(_cursor.getColumnIndex(REPEAT_UNTIL)));
//
//                } while (_cursor.moveToNext());
//            }
//
//        } catch (Exception e) {
//            Log.e("", "" + e);
//        } finally {
//            closeCursor(_cursor);
//        }
//        return appointmentDetail;
//    }


    public void deleteTask(int pId) {
        Cursor _cursor = null;
        try {
            int i = mWritableDatabase.delete(TASK_TABLE, TASK_ID + " = " + pId, null);
        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }
    }

    public void deleteTaskByListId(int pId) {
        Cursor _cursor = null;
        try {
            int i = mWritableDatabase.delete(TASK_TABLE, TASK_LIST_ID + " = " + pId, null);

        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }
    }


//    public ArrayList<AppointmentMappingModel> filterByName(int id, String type, long first, long second) {
//
//        ArrayList<AppointmentMappingModel> allData = new ArrayList<AppointmentMappingModel>();
//        Cursor _cursor = null;
//        Cursor _cursor_inner = null;
//        int eventId;
//
//        try {
//            String CREATE_QUERY = " select * from attendeeTable where userkid_id = " + id + " and type like '%" + type + "%'";
//            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null, null);
//
//            if (_cursor.moveToFirst()) {
//                do {
//
//                    eventId = _cursor.getInt(_cursor.getColumnIndex("event_id"));
//
////                    String CREATE_QUERY_INNER = " select * from " + APPOINTMENT_TABLE + " where event_id = " + eventId + " and " + STARTTIME + " >= " + first + " and " + STARTTIME + " <= " + second + " or is_recurring = 'yes' ";
//                    String CREATE_QUERY_INNER = " select * from " + APPOINTMENT_TABLE + " where event_id = " + eventId + " and " + STARTTIME + " >= " + first + " and " + ENDTIME + " <= " + second;
//                    _cursor_inner = mWritableDatabase.rawQuery(CREATE_QUERY_INNER, null, null);
//
//                    if (_cursor_inner.moveToFirst()) {
//                        do {
//                            AppointmentMappingModel appointmentModel = new AppointmentMappingModel();
//
//                            appointmentModel.setpId(_cursor_inner.getInt(_cursor_inner.getColumnIndex(PRIMARY_KEY)));
//                            appointmentModel.setEventId(_cursor_inner.getInt(_cursor_inner.getColumnIndex(EVENT_ID)));
//                            appointmentModel.setAppointment_name(_cursor_inner.getString(_cursor_inner.getColumnIndex(APPOINTMENT_NAME)));
//                            appointmentModel.setLocality(_cursor_inner.getString(_cursor_inner.getColumnIndex(LOCATION)));
//                            appointmentModel.setStarttime(_cursor_inner.getLong(_cursor_inner.getColumnIndex(STARTTIME)));
//                            appointmentModel.setEndtime(_cursor_inner.getLong(_cursor_inner.getColumnIndex(ENDTIME)));
//                            appointmentModel.setIs_recurring(_cursor_inner.getString(_cursor_inner.getColumnIndex(IS_RECURRING)));
//                            appointmentModel.setRepeat(_cursor_inner.getString(_cursor_inner.getColumnIndex(REPEAT)));
//                            appointmentModel.setRepeate_num(_cursor_inner.getString(_cursor_inner.getColumnIndex(REPEAT_NUM)));
//                            appointmentModel.setRepeate_frequency(_cursor_inner.getString(_cursor_inner.getColumnIndex(REPEAT_FREQUENCY)));
//                            appointmentModel.setRepeate_untill(_cursor_inner.getString(_cursor_inner.getColumnIndex(REPEAT_UNTIL)));
//
//
//                            Cursor _cursorAttendee = null;
//                            ArrayList<AttendeeModel> attendeeList = new ArrayList<>();
//                            try {
//                                String CREATE_QUERY_ATTENDEE = "select userkid_id ,server_id, type,kt.colorCode as kidcolor,kt.name as kidname,ad.colorCode as usercolorcode,ad.name as username from attendeeTable at LEFT JOIN kidsTable kt  ON at.userkid_id == kt.kid_id LEFT JOIN adultTable ad ON at.userkid_id == ad.USERID where event_id = " + appointmentModel.getEventId();
//
//                                _cursorAttendee = mWritableDatabase.rawQuery(CREATE_QUERY_ATTENDEE, null, null);
//                                while (_cursorAttendee.moveToNext()) {
//
//                                    AttendeeModel attendee = new AttendeeModel();
//                                    attendee.setAppoitmentId(appointmentModel.getEventId());
//                                    attendee.setId(_cursorAttendee.getInt(_cursorAttendee.getColumnIndex("userkid_id")));
//                                    attendee.setServerid(_cursorAttendee.getInt(_cursorAttendee.getColumnIndex("server_id")));
//
//                                    String type_uk = _cursorAttendee.getString(_cursorAttendee.getColumnIndex("type"));
//
//                                    if (type_uk.equals("user")) {
//                                        attendee.setName(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("username")));
//                                        attendee.setColorCode(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("usercolorcode")));
//                                    } else if (type_uk.equals("kid")) {
//                                        attendee.setName(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("kidname")));
//                                        attendee.setColorCode(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("kidcolor")));
//                                    }
//
//                                    attendeeList.add(attendee);
//                                }
//                            } catch (Exception e) {
//                                Log.e("", "" + e);
//                            } finally {
//                                closeCursor(_cursorAttendee);
//                            }
//                            appointmentModel.setAttendee(attendeeList);
//                            allData.add(appointmentModel);
//
//                        } while (_cursor_inner.moveToNext());
//                    }
//                } while (_cursor.moveToNext());
//            }
//
//        } catch (Exception e) {
//            Log.e("", "" + e);
//        } finally {
//            closeCursor(_cursor);
//        }
//        return allData;
//    }
//
//
//    public ArrayList<AppointmentMappingModel> filterByNameCalDay(int id, String type, long first, long second) {
//
//        ArrayList<AppointmentMappingModel> allData = new ArrayList<AppointmentMappingModel>();
//        Cursor _cursor = null;
//        Cursor _cursor_inner = null;
//        int eventId;
//
//        try {
//            String CREATE_QUERY = " select * from attendeeTable where userkid_id = " + id + " and type like '%" + type + "%'";
//            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null, null);
//
//            if (_cursor.moveToFirst()) {
//                do {
//
//                    eventId = _cursor.getInt(_cursor.getColumnIndex("event_id"));
//
//                    String CREATE_QUERY_INNER = " select * from " + APPOINTMENT_TABLE + " where event_id = " + eventId;
////                    String CREATE_QUERY_INNER = " select * from " + APPOINTMENT_TABLE + " where event_id = " + eventId + " and " + STARTTIME + " >= " + first + " and " + ENDTIME + " <= " + second;
//                    _cursor_inner = mWritableDatabase.rawQuery(CREATE_QUERY_INNER, null, null);
//
//                    if (_cursor_inner.moveToFirst()) {
//                        do {
//                            AppointmentMappingModel appointmentModel = new AppointmentMappingModel();
//
//                            appointmentModel.setpId(_cursor_inner.getInt(_cursor_inner.getColumnIndex(PRIMARY_KEY)));
//                            appointmentModel.setEventId(_cursor_inner.getInt(_cursor_inner.getColumnIndex(EVENT_ID)));
//                            appointmentModel.setAppointment_name(_cursor_inner.getString(_cursor_inner.getColumnIndex(APPOINTMENT_NAME)));
//                            appointmentModel.setLocality(_cursor_inner.getString(_cursor_inner.getColumnIndex(LOCATION)));
//                            appointmentModel.setStarttime(_cursor_inner.getLong(_cursor_inner.getColumnIndex(STARTTIME)));
//                            appointmentModel.setEndtime(_cursor_inner.getLong(_cursor_inner.getColumnIndex(ENDTIME)));
//                            appointmentModel.setIs_recurring(_cursor_inner.getString(_cursor_inner.getColumnIndex(IS_RECURRING)));
//                            appointmentModel.setRepeat(_cursor_inner.getString(_cursor_inner.getColumnIndex(REPEAT)));
//                            appointmentModel.setRepeate_num(_cursor_inner.getString(_cursor_inner.getColumnIndex(REPEAT_NUM)));
//                            appointmentModel.setRepeate_frequency(_cursor_inner.getString(_cursor_inner.getColumnIndex(REPEAT_FREQUENCY)));
//                            appointmentModel.setRepeate_untill(_cursor_inner.getString(_cursor_inner.getColumnIndex(REPEAT_UNTIL)));
//
//
//                            Cursor _cursorAttendee = null;
//                            ArrayList<AttendeeModel> attendeeList = new ArrayList<>();
//                            try {
//                                String CREATE_QUERY_ATTENDEE = "select userkid_id ,server_id, type,kt.colorCode as kidcolor,kt.name as kidname,ad.colorCode as usercolorcode,ad.name as username from attendeeTable at LEFT JOIN kidsTable kt  ON at.userkid_id == kt.kid_id LEFT JOIN adultTable ad ON at.userkid_id == ad.USERID where event_id = " + appointmentModel.getEventId();
//
//                                _cursorAttendee = mWritableDatabase.rawQuery(CREATE_QUERY_ATTENDEE, null, null);
//                                while (_cursorAttendee.moveToNext()) {
//
//                                    AttendeeModel attendee = new AttendeeModel();
//                                    attendee.setAppoitmentId(appointmentModel.getEventId());
//                                    attendee.setId(_cursorAttendee.getInt(_cursorAttendee.getColumnIndex("userkid_id")));
//                                    attendee.setServerid(_cursorAttendee.getInt(_cursorAttendee.getColumnIndex("server_id")));
//
//                                    String type_uk = _cursorAttendee.getString(_cursorAttendee.getColumnIndex("type"));
//
//                                    if (type_uk.equals("user")) {
//                                        attendee.setName(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("username")));
//                                        attendee.setColorCode(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("usercolorcode")));
//                                    } else if (type_uk.equals("kid")) {
//                                        attendee.setName(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("kidname")));
//                                        attendee.setColorCode(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("kidcolor")));
//                                    }
//
//                                    attendeeList.add(attendee);
//                                }
//                            } catch (Exception e) {
//                                Log.e("", "" + e);
//                            } finally {
//                                closeCursor(_cursorAttendee);
//                            }
//                            appointmentModel.setAttendee(attendeeList);
//                            allData.add(appointmentModel);
//
//                        } while (_cursor_inner.moveToNext());
//                    }
//                } while (_cursor.moveToNext());
//            }
//
//        } catch (Exception e) {
//            Log.e("", "" + e);
//        } finally {
//            closeCursor(_cursor);
//        }
//        return allData;
//    }
//
//    public ArrayList<AppointmentMappingModel> filterByName_1(int id, String type, long first, long second) {
//
//        ArrayList<AppointmentMappingModel> allData = new ArrayList<AppointmentMappingModel>();
//        Cursor _cursor = null;
//        Cursor _cursor_inner = null;
//        int eventId;
//
//        try {
//            String CREATE_QUERY = " select * from attendeeTable where userkid_id = " + id + " and type like '%" + type + "%'";
//            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null, null);
//
//            if (_cursor.moveToFirst()) {
//                do {
//
//                    eventId = _cursor.getInt(_cursor.getColumnIndex("event_id"));
//
////                    String CREATE_QUERY_INNER = " select * from " + APPOINTMENT_TABLE + " where event_id = " + eventId + " and " + STARTTIME + " >= " + first + " and " + ENDTIME + " <= " + second + " or is_recurring = 'yes' ";
//                    String CREATE_QUERY_INNER = " select * from " + APPOINTMENT_TABLE + " where event_id = " + eventId + " and " + STARTTIME + " >= " + first + " and " + ENDTIME + " <= " + second;
//                    _cursor_inner = mWritableDatabase.rawQuery(CREATE_QUERY_INNER, null, null);
//
//                    if (_cursor_inner.moveToFirst()) {
//                        do {
//                            AppointmentMappingModel appointmentModel = new AppointmentMappingModel();
//
//                            appointmentModel.setpId(_cursor_inner.getInt(_cursor_inner.getColumnIndex(PRIMARY_KEY)));
//                            appointmentModel.setEventId(_cursor_inner.getInt(_cursor_inner.getColumnIndex(EVENT_ID)));
//                            appointmentModel.setAppointment_name(_cursor_inner.getString(_cursor_inner.getColumnIndex(APPOINTMENT_NAME)));
//                            appointmentModel.setLocality(_cursor_inner.getString(_cursor_inner.getColumnIndex(LOCATION)));
//                            appointmentModel.setStarttime(_cursor_inner.getLong(_cursor_inner.getColumnIndex(STARTTIME)));
//                            appointmentModel.setEndtime(_cursor_inner.getLong(_cursor_inner.getColumnIndex(ENDTIME)));
//                            appointmentModel.setIs_recurring(_cursor_inner.getString(_cursor_inner.getColumnIndex(IS_RECURRING)));
//                            appointmentModel.setRepeat(_cursor_inner.getString(_cursor_inner.getColumnIndex(REPEAT)));
//                            appointmentModel.setRepeate_num(_cursor_inner.getString(_cursor_inner.getColumnIndex(REPEAT_NUM)));
//                            appointmentModel.setRepeate_frequency(_cursor_inner.getString(_cursor_inner.getColumnIndex(REPEAT_FREQUENCY)));
//                            appointmentModel.setRepeate_untill(_cursor_inner.getString(_cursor_inner.getColumnIndex(REPEAT_UNTIL)));
//
//
//                            Cursor _cursorAttendee = null;
//                            ArrayList<AttendeeModel> attendeeList = new ArrayList<>();
//                            try {
//                                String CREATE_QUERY_ATTENDEE = "select userkid_id ,server_id, type,kt.colorCode as kidcolor,kt.name as kidname,ad.colorCode as usercolorcode,ad.name as username from attendeeTable at LEFT JOIN kidsTable kt  ON at.userkid_id == kt.kid_id LEFT JOIN adultTable ad ON at.userkid_id == ad.USERID where event_id = " + appointmentModel.getEventId();
//
//                                _cursorAttendee = mWritableDatabase.rawQuery(CREATE_QUERY_ATTENDEE, null, null);
//                                while (_cursorAttendee.moveToNext()) {
//
//                                    AttendeeModel attendee = new AttendeeModel();
//                                    attendee.setAppoitmentId(appointmentModel.getEventId());
//                                    attendee.setId(_cursorAttendee.getInt(_cursorAttendee.getColumnIndex("userkid_id")));
//                                    attendee.setServerid(_cursorAttendee.getInt(_cursorAttendee.getColumnIndex("server_id")));
//
//                                    String type_uk = _cursorAttendee.getString(_cursorAttendee.getColumnIndex("type"));
//
//                                    if (type_uk.equals("user")) {
//                                        attendee.setName(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("username")));
//                                        attendee.setColorCode(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("usercolorcode")));
//                                    } else if (type_uk.equals("kid")) {
//                                        attendee.setName(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("kidname")));
//                                        attendee.setColorCode(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("kidcolor")));
//                                    }
//
//                                    attendeeList.add(attendee);
//                                }
//                            } catch (Exception e) {
//                                Log.e("", "" + e);
//                            } finally {
//                                closeCursor(_cursorAttendee);
//                            }
//                            appointmentModel.setAttendee(attendeeList);
//                            allData.add(appointmentModel);
//
//                        } while (_cursor_inner.moveToNext());
//                    }
//                } while (_cursor.moveToNext());
//            }
//
//        } catch (Exception e) {
//            Log.e("", "" + e);
//        } finally {
//            closeCursor(_cursor);
//        }
//        return allData;
//    }
//
//    public ArrayList<AppointmentMappingModel> filterByName_2(int id, String type) {
//
//        ArrayList<AppointmentMappingModel> allData = new ArrayList<AppointmentMappingModel>();
//        Cursor _cursor = null;
//        Cursor _cursor_inner = null;
//        int eventId;
//
//        try {
//            String CREATE_QUERY = " select * from attendeeTable where userkid_id = " + id + " and type like '%" + type + "%'";
//            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null, null);
//
//            if (_cursor.moveToFirst()) {
//                do {
//
//                    eventId = _cursor.getInt(_cursor.getColumnIndex("event_id"));
//
////                    String CREATE_QUERY_INNER = " select * from " + APPOINTMENT_TABLE + " where event_id = " + eventId + " and " + STARTTIME + " >= " + first + " and " + ENDTIME + " <= " + second + " or is_recurring = 'yes' ";
//                    String CREATE_QUERY_INNER = " select * from " + APPOINTMENT_TABLE + " where event_id = " + eventId;
//                    _cursor_inner = mWritableDatabase.rawQuery(CREATE_QUERY_INNER, null, null);
//
//                    if (_cursor_inner.moveToFirst()) {
//                        do {
//                            AppointmentMappingModel appointmentModel = new AppointmentMappingModel();
//
//                            appointmentModel.setpId(_cursor_inner.getInt(_cursor_inner.getColumnIndex(PRIMARY_KEY)));
//                            appointmentModel.setEventId(_cursor_inner.getInt(_cursor_inner.getColumnIndex(EVENT_ID)));
//                            appointmentModel.setAppointment_name(_cursor_inner.getString(_cursor_inner.getColumnIndex(APPOINTMENT_NAME)));
//                            appointmentModel.setLocality(_cursor_inner.getString(_cursor_inner.getColumnIndex(LOCATION)));
//                            appointmentModel.setStarttime(_cursor_inner.getLong(_cursor_inner.getColumnIndex(STARTTIME)));
//                            appointmentModel.setEndtime(_cursor_inner.getLong(_cursor_inner.getColumnIndex(ENDTIME)));
//                            appointmentModel.setIs_recurring(_cursor_inner.getString(_cursor_inner.getColumnIndex(IS_RECURRING)));
//                            appointmentModel.setRepeat(_cursor_inner.getString(_cursor_inner.getColumnIndex(REPEAT)));
//                            appointmentModel.setRepeate_num(_cursor_inner.getString(_cursor_inner.getColumnIndex(REPEAT_NUM)));
//                            appointmentModel.setRepeate_frequency(_cursor_inner.getString(_cursor_inner.getColumnIndex(REPEAT_FREQUENCY)));
//                            appointmentModel.setRepeate_untill(_cursor_inner.getString(_cursor_inner.getColumnIndex(REPEAT_UNTIL)));
//
//
//                            Cursor _cursorAttendee = null;
//                            ArrayList<AttendeeModel> attendeeList = new ArrayList<>();
//                            try {
//                                String CREATE_QUERY_ATTENDEE = "select userkid_id ,server_id, type,kt.colorCode as kidcolor,kt.name as kidname,ad.colorCode as usercolorcode,ad.name as username from attendeeTable at LEFT JOIN kidsTable kt  ON at.userkid_id == kt.kid_id LEFT JOIN adultTable ad ON at.userkid_id == ad.USERID where event_id = " + appointmentModel.getEventId();
//
//                                _cursorAttendee = mWritableDatabase.rawQuery(CREATE_QUERY_ATTENDEE, null, null);
//                                while (_cursorAttendee.moveToNext()) {
//
//                                    AttendeeModel attendee = new AttendeeModel();
//                                    attendee.setAppoitmentId(appointmentModel.getEventId());
//                                    attendee.setId(_cursorAttendee.getInt(_cursorAttendee.getColumnIndex("userkid_id")));
//                                    attendee.setServerid(_cursorAttendee.getInt(_cursorAttendee.getColumnIndex("server_id")));
//
//                                    String type_uk = _cursorAttendee.getString(_cursorAttendee.getColumnIndex("type"));
//
//                                    if (type_uk.equals("user")) {
//                                        attendee.setName(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("username")));
//                                        attendee.setColorCode(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("usercolorcode")));
//                                    } else if (type_uk.equals("kid")) {
//                                        attendee.setName(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("kidname")));
//                                        attendee.setColorCode(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("kidcolor")));
//                                    }
//
//                                    attendeeList.add(attendee);
//                                }
//                            } catch (Exception e) {
//                                Log.e("", "" + e);
//                            } finally {
//                                closeCursor(_cursorAttendee);
//                            }
//                            appointmentModel.setAttendee(attendeeList);
//                            allData.add(appointmentModel);
//
//                        } while (_cursor_inner.moveToNext());
//                    }
//                } while (_cursor.moveToNext());
//            }
//
//        } catch (Exception e) {
//            Log.e("", "" + e);
//        } finally {
//            closeCursor(_cursor);
//        }
//        return allData;
//    }

    public ArrayList<TaskMappingModel> searchByName(String name, long first, long second) {

        ArrayList<TaskMappingModel> allData = new ArrayList<TaskMappingModel>();

        Cursor _cursor = null;

        try {
            String CREATE_QUERY = " select * from " + TASK_TABLE + " where " + STARTTIME + " >= " + first + " and " + STARTTIME + " <= " + second + " and " + TASK_NAME + " like '%" + name + "%'";
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);

            if (_cursor.moveToFirst()) {
                do {

                    TaskMappingModel taskMappingModel = new TaskMappingModel();

//                    taskMappingModel.setpId(_cursor.getInt(_cursor.getColumnIndex(PRIMARY_KEY)));
                    taskMappingModel.setTask_id(_cursor.getInt(_cursor.getColumnIndex(TASK_ID)));
                    taskMappingModel.setTaskName(_cursor.getString(_cursor.getColumnIndex(TASK_NAME)));
                    taskMappingModel.setTaskDate(_cursor.getLong(_cursor.getColumnIndex(STARTTIME)));
//                    taskMappingModel.setIs_recurring(_cursor.getString(_cursor.getColumnIndex(IS_RECURRING)));
//                    taskMappingModel.setRepeat(_cursor.getString(_cursor.getColumnIndex(REPEAT)));
//                    taskMappingModel.setRepeate_num(_cursor.getString(_cursor.getColumnIndex(REPEAT_NUM)));
//                    taskMappingModel.setRepeate_frequency(_cursor.getString(_cursor.getColumnIndex(REPEAT_FREQUENCY)));
//                    taskMappingModel.setRepeate_untill(_cursor.getString(_cursor.getColumnIndex(REPEAT_UNTIL)));
                    taskMappingModel.setActive(_cursor.getInt(_cursor.getColumnIndex(ACTIVE)));
                    int listId = (_cursor.getInt(_cursor.getColumnIndex(TASK_LIST_ID)));
//                    fetching attendees BD

                    ArrayList<AttendeeModel> attendeeList = new ArrayList<>();
                    Cursor _cursorAttendee = null;
                    try {
                        String CREATE_QUERY_ATTENDEE = "select userkid_id ,server_id, type,kt.colorCode as kidcolor,kt.name as kidname,ad.colorCode as usercolorcode,ad.name as username from attendeeTable at LEFT JOIN kidsTable kt  ON at.userkid_id == kt.kid_id LEFT JOIN adultTable ad ON at.userkid_id == ad.USERID where event_id = " + taskMappingModel.getTask_id();

                        _cursorAttendee = mWritableDatabase.rawQuery(CREATE_QUERY_ATTENDEE, null);
                        while (_cursorAttendee.moveToNext()) {

                            AttendeeModel attendee = new AttendeeModel();

                            attendee.setAppoitmentId(taskMappingModel.getTask_id());
                            attendee.setId(_cursorAttendee.getInt(_cursorAttendee.getColumnIndex("userkid_id")));
                            attendee.setServerid(_cursorAttendee.getInt(_cursorAttendee.getColumnIndex("server_id")));

                            String type = _cursorAttendee.getString(_cursorAttendee.getColumnIndex("type"));

                            if (type.equals("user")) {
                                attendee.setName(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("username")));
                                attendee.setColorCode(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("usercolorcode")));
                            } else if (type.equals("kid")) {
                                attendee.setName(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("kidname")));
                                attendee.setColorCode(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("kidcolor")));
                            }

                            attendeeList.add(attendee);
                        }
                    } catch (Exception e) {
                        Log.e("", "" + e);
                    } finally {
                        closeCursor(_cursorAttendee);
                    }

                    taskMappingModel.setAttendees(attendeeList);

                    Cursor _cursor_noteCount = null;
                    try {
                        String CREATE_QUERY_COUNT = "select * from TaskNotesTable where event_id = " + taskMappingModel.getTask_id();
                        _cursor_noteCount = mWritableDatabase.rawQuery(CREATE_QUERY_COUNT, null);

                        taskMappingModel.setNumberNotes(_cursor_noteCount.getCount());

                    } catch (Exception e) {
                        Log.e("", "" + e);
                    } finally {
                        closeCursor(_cursor_noteCount);
                    }


                    allData.add(taskMappingModel);

                } while (_cursor.moveToNext());
            }

        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return allData;
    }

}