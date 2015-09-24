
package com.mycity4kids.dbtable;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.kelltontech.model.BaseModel;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.database.BaseTable;
import com.mycity4kids.newmodels.AppointmentMappingModel;
import com.mycity4kids.newmodels.AppoitmentDataModel;
import com.mycity4kids.newmodels.AttendeeModel;
import com.mycity4kids.preference.SharedPrefUtils;

import java.util.ArrayList;

/**
 * Created by manish.soni on 18-06-2015.
 */
public class TableAppointmentData extends BaseTable {

    private static final String PRIMARY_KEY = "_id";
    private static final String EVENT_ID = "event_id";
    private static final String EXTERNAL_EVENT_ID = "external_event_id";
    private static final String APPOINTMENT_NAME = "appointment_name";
    private static final String LOCATION = "location";
    private static final String STARTTIME = "start_time";
    private static final String ENDTIME = "end_time";
    private static final String REMINDER = "reminder";
    private static final String IS_RECURRING = "is_recurring";
    private static final String REPEAT = "repeat";
    private static final String REPEAT_FREQUENCY = "repeat_frequency";
    private static final String REPEAT_UNTIL = "repeat_until";
    private static final String REPEAT_NUM = "repeat_num";
    private static final String SYNC = "sync";
    private static final String USER_ID = "userid";
    private static final String API_EVENT_ID = "api_event_id";
    private static final String IS_BIRTHDAY = "is_birthday";
    private static final String IS_HOLIDAY = "is_holiday";
    private static final String IS_FACEBOOK = "is_facebook";
    private static final String GOOGLE_EVENT = "google_event";

    /**
     * Table Name:-
     */
//
    public static final String APPOINTMENT_TABLE = "appointmentdataTable";

    public static final String CREATE_APPOINTMENT_TABLE = "create table if not exists " +
            APPOINTMENT_TABLE + "(" + PRIMARY_KEY + " integer primary key," +
            APPOINTMENT_NAME + " text ," +
            EVENT_ID + " integer  ," +
            EXTERNAL_EVENT_ID + " text  ," +
            API_EVENT_ID + " text  ," +
            LOCATION + " text  ," +
            IS_RECURRING + " text  ," +
            STARTTIME + " long  ," +
            ENDTIME + " long  ," +
            REMINDER + " text  ," +
            REPEAT + " text  ," +
            USER_ID + " text  ," +
            REPEAT_FREQUENCY + " text  ," +
            REPEAT_UNTIL + " text  ," +
            REPEAT_NUM + " text  ," +
            IS_BIRTHDAY + " integer  ," +
            IS_HOLIDAY + " integer  ," +
            IS_FACEBOOK + " integer  ," +
            GOOGLE_EVENT + " integer  ," +
            SYNC + " integer  )";

    public static final String DROP_QUERY = "Drop table if exists " + APPOINTMENT_TABLE;
    private final BaseApplication pApplication;

    public TableAppointmentData(BaseApplication pApplication) {
        super(pApplication, APPOINTMENT_TABLE);
        this.pApplication = pApplication;
        // TODO Auto-generated constructor stub
    }


    protected ContentValues getContentValues(BaseModel pModel) {
        AppoitmentDataModel.AppointmentDetail appointmentModel = (AppoitmentDataModel.AppointmentDetail) pModel;

        ContentValues _contentValue = new ContentValues();
        _contentValue.put(APPOINTMENT_NAME, appointmentModel.getAppointment_name());
        _contentValue.put(EVENT_ID, appointmentModel.getId());
        _contentValue.put(EXTERNAL_EVENT_ID, appointmentModel.getExternal_id());
        _contentValue.put(API_EVENT_ID, appointmentModel.getApi_event_id());
        _contentValue.put(LOCATION, appointmentModel.getLocality());
        _contentValue.put(IS_RECURRING, appointmentModel.getIs_recurring());
        _contentValue.put(STARTTIME, appointmentModel.getStarttime());
        _contentValue.put(ENDTIME, appointmentModel.getEndtime());
        _contentValue.put(REMINDER, appointmentModel.getReminder());
        _contentValue.put(REPEAT, appointmentModel.getRepeate());
        _contentValue.put(REPEAT_FREQUENCY, appointmentModel.getRepeate_frequency());
        _contentValue.put(REPEAT_NUM, appointmentModel.getRepeate_num());
        _contentValue.put(REPEAT_UNTIL, appointmentModel.getRepeate_untill());
        _contentValue.put(SYNC, 1);
        _contentValue.put(USER_ID, appointmentModel.getUser_id());
        _contentValue.put(IS_BIRTHDAY, appointmentModel.getIs_bday());
        _contentValue.put(IS_HOLIDAY, appointmentModel.getIs_holiday());
        _contentValue.put(GOOGLE_EVENT, appointmentModel.getIs_google());
        _contentValue.put(IS_FACEBOOK, 0);


        return _contentValue;
    }


    public void updateData(BaseModel pModel) {
        AppoitmentDataModel.AppointmentDetail appointmentModel = (AppoitmentDataModel.AppointmentDetail) pModel;

        try {
            ContentValues _contentValue = new ContentValues();
            _contentValue.put(APPOINTMENT_NAME, appointmentModel.getAppointment_name());
            _contentValue.put(LOCATION, appointmentModel.getLocality());
            _contentValue.put(STARTTIME, appointmentModel.getStarttime());
            _contentValue.put(ENDTIME, appointmentModel.getEndtime());

            int rowsEffected = mWritableDatabase.update(APPOINTMENT_TABLE, _contentValue, EXTERNAL_EVENT_ID + " ='" + appointmentModel.getExternal_id() + "'", null);
            Log.e(APPOINTMENT_TABLE, "updated rowsEffected: " + rowsEffected);
        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
        }

    }


//    public void AddAppoitmentDetail(BaseModel pModel) {
//        AppoitmentDataModel.AppointmentDetail appointmentModel = (AppoitmentDataModel.AppointmentDetail) pModel;
//
//
////        if (getDataByAppointment(appointmentModel.getId()).getId() > 0) {
////
////            deleteAppointment(appointmentModel.getId());
////        }
//
//        try {
//            mWritableDatabase.beginTransaction();
//            ContentValues _contentValue = new ContentValues();
//            _contentValue.put(APPOINTMENT_NAME, appointmentModel.getAppointment_name());
//            _contentValue.put(EVENT_ID, appointmentModel.getId());
//            _contentValue.put(EXTERNAL_EVENT_ID, appointmentModel.getExternal_id());
//            _contentValue.put(API_EVENT_ID, appointmentModel.getApi_event_id());
//            _contentValue.put(LOCATION, appointmentModel.getLocality());
//            _contentValue.put(IS_RECURRING, appointmentModel.getIs_recurring());
//            _contentValue.put(STARTTIME, appointmentModel.getStarttime());
//            _contentValue.put(ENDTIME, appointmentModel.getEndtime());
//            _contentValue.put(REMINDER, appointmentModel.getReminder());
//            _contentValue.put(REPEAT, appointmentModel.getRepeate());
//            _contentValue.put(REPEAT_FREQUENCY, appointmentModel.getRepeate_frequency());
//            _contentValue.put(REPEAT_NUM, appointmentModel.getRepeate_num());
//            _contentValue.put(REPEAT_UNTIL, appointmentModel.getRepeate_untill());
//            _contentValue.put(SYNC, 1);
//            _contentValue.put(USER_ID, appointmentModel.getUser_id());
//
//            mWritableDatabase.insert(APPOINTMENT_TABLE, null, _contentValue);
//
//            mWritableDatabase.setTransactionSuccessful();
//        } catch (Exception e) {
//            e.printStackTrace();
//
//        } finally {
//            mWritableDatabase.endTransaction();
//        }
//
//    }

    @Override
    protected ArrayList<BaseModel> getAllData(String pSelection, String[] pSelectionArgs) {
        return null;
    }


    public ArrayList<String> getApiEventIdList() {
        ArrayList<String> eventListing = new ArrayList<>();
        Cursor _cursor = null;
        try {

            String CREATE_QUERY = "select " + API_EVENT_ID + " from " + APPOINTMENT_TABLE;
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


    public ArrayList<String> getExternalEventIdList() {
        ArrayList<String> eventListing = new ArrayList<>();
        Cursor _cursor = null;
        try {

            String CREATE_QUERY = "select " + EXTERNAL_EVENT_ID + " from " + APPOINTMENT_TABLE;
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);
            while (_cursor.moveToNext()) {
                eventListing.add(_cursor.getString(_cursor.getColumnIndex(EXTERNAL_EVENT_ID)));
            }
        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return eventListing;

    }


    public AppoitmentDataModel.AppointmentDetail getDataByAppointment(int id, String externalId, Boolean isExternalEvent) {
        AppoitmentDataModel.AppointmentDetail appointmentDetail = new AppoitmentDataModel().new AppointmentDetail();
        Cursor _cursor = null;
        String CREATE_QUERY = "";
        try {
            if (isExternalEvent) {
                CREATE_QUERY = "select * from " + APPOINTMENT_TABLE + " where " + EXTERNAL_EVENT_ID + " = '" + externalId + "'";
            } else {
                CREATE_QUERY = "select * from " + APPOINTMENT_TABLE + " where " + EVENT_ID + " = " + id;
            }
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);
            while (_cursor.moveToNext()) {
                appointmentDetail.setId(_cursor.getInt(_cursor.getColumnIndex(EVENT_ID)));
                appointmentDetail.setExternal_id(_cursor.getString(_cursor.getColumnIndex(EXTERNAL_EVENT_ID)));
                appointmentDetail.setApi_event_id(_cursor.getString(_cursor.getColumnIndex(API_EVENT_ID)));
                appointmentDetail.setAppointment_name(_cursor.getString(_cursor.getColumnIndex(APPOINTMENT_NAME)));
                appointmentDetail.setLocality(_cursor.getString(_cursor.getColumnIndex(LOCATION)));
                appointmentDetail.setStarttime(_cursor.getLong(_cursor.getColumnIndex(STARTTIME)));
                appointmentDetail.setEndtime(_cursor.getLong(_cursor.getColumnIndex(ENDTIME)));
                appointmentDetail.setReminder(_cursor.getString(_cursor.getColumnIndex(REMINDER)));
                appointmentDetail.setIs_recurring(_cursor.getString(_cursor.getColumnIndex(IS_RECURRING)));
                appointmentDetail.setRepeate(_cursor.getString(_cursor.getColumnIndex(REPEAT)));
                appointmentDetail.setRepeate_frequency(_cursor.getString(_cursor.getColumnIndex(REPEAT_FREQUENCY)));
                appointmentDetail.setRepeate_num(_cursor.getString(_cursor.getColumnIndex(REPEAT_NUM)));
                appointmentDetail.setRepeate_untill(_cursor.getString(_cursor.getColumnIndex(REPEAT_UNTIL)));
                appointmentDetail.setIs_holiday(_cursor.getInt(_cursor.getColumnIndex(IS_HOLIDAY)));
                appointmentDetail.setIs_bday(_cursor.getInt(_cursor.getColumnIndex(IS_BIRTHDAY)));
                //appointmentDetail.setOffline_id(_cursor.getInt(_cursor.getColumnIndex(PRIMARY_KEY)));
            }
        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return appointmentDetail;

    }


    public ArrayList<AppointmentMappingModel> getAll() {
        ArrayList<AppointmentMappingModel> allData = new ArrayList<AppointmentMappingModel>();

        Cursor _cursor = null;

        try {
            String CREATE_QUERY = " select * from " + APPOINTMENT_TABLE;
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);

            while (_cursor.moveToNext()) {
                AppointmentMappingModel appointmentModel = new AppointmentMappingModel();

                appointmentModel.setpId(_cursor.getInt(_cursor.getColumnIndex(PRIMARY_KEY)));
                appointmentModel.setEventId(_cursor.getInt(_cursor.getColumnIndex(EVENT_ID)));
                appointmentModel.setExternalId(_cursor.getString(_cursor.getColumnIndex(EXTERNAL_EVENT_ID)));
                appointmentModel.setAppointment_name(_cursor.getString(_cursor.getColumnIndex(APPOINTMENT_NAME)));
                appointmentModel.setLocality(_cursor.getString(_cursor.getColumnIndex(LOCATION)));
                appointmentModel.setStarttime(_cursor.getLong(_cursor.getColumnIndex(STARTTIME)));
                appointmentModel.setEndtime(_cursor.getLong(_cursor.getColumnIndex(ENDTIME)));
                appointmentModel.setIs_recurring(_cursor.getString(_cursor.getColumnIndex(IS_RECURRING)));
                appointmentModel.setRepeat(_cursor.getString(_cursor.getColumnIndex(REPEAT)));
                appointmentModel.setRepeate_num(_cursor.getString(_cursor.getColumnIndex(REPEAT_NUM)));
                appointmentModel.setRepeate_frequency(_cursor.getString(_cursor.getColumnIndex(REPEAT_FREQUENCY)));
                appointmentModel.setRepeate_untill(_cursor.getString(_cursor.getColumnIndex(REPEAT_UNTIL)));
                appointmentModel.setIs_holiday(_cursor.getInt(_cursor.getColumnIndex(IS_HOLIDAY)));
                appointmentModel.setIs_bday(_cursor.getInt(_cursor.getColumnIndex(IS_BIRTHDAY)));
                allData.add(appointmentModel);
            }
        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }

        return allData;
    }


    public ArrayList<AppointmentMappingModel> allDataBTWNdays(long first, long second) {

        ArrayList<AppointmentMappingModel> allData = new ArrayList<AppointmentMappingModel>();
        TableNotes notesTable = new TableNotes(pApplication);
        Cursor _cursor = null;

        try {
            String CREATE_QUERY = " select * from " + APPOINTMENT_TABLE + " where " + STARTTIME + " >= " + first + " and " + STARTTIME + " <= " + second + " OR is_recurring = 'yes' ";
//            String CREATE_QUERY = " select * from " + APPOINTMENT_TABLE + " a INNER JOIN " + TableNotes.NOTES_TABLE + " b ON a." + EVENT_ID + "=b." + TableNotes.EVENT_ID + " where " + STARTTIME + " >= " + first + " and " + STARTTIME + " <= " + second + " OR is_recurring = 'yes' ";

            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);

            if (_cursor.moveToFirst()) {
                do {

                    AppointmentMappingModel appointmentModel = new AppointmentMappingModel();

                    appointmentModel.setpId(_cursor.getInt(_cursor.getColumnIndex(PRIMARY_KEY)));
                    appointmentModel.setEventId(_cursor.getInt(_cursor.getColumnIndex(EVENT_ID)));
                    appointmentModel.setExternalId(_cursor.getString(_cursor.getColumnIndex(EXTERNAL_EVENT_ID)));
                    appointmentModel.setAppointment_name(_cursor.getString(_cursor.getColumnIndex(APPOINTMENT_NAME)));
                    appointmentModel.setLocality(_cursor.getString(_cursor.getColumnIndex(LOCATION)));
                    appointmentModel.setStarttime(_cursor.getLong(_cursor.getColumnIndex(STARTTIME)));
                    appointmentModel.setEndtime(_cursor.getLong(_cursor.getColumnIndex(ENDTIME)));
                    appointmentModel.setIs_recurring(_cursor.getString(_cursor.getColumnIndex(IS_RECURRING)));
                    appointmentModel.setRepeat(_cursor.getString(_cursor.getColumnIndex(REPEAT)));
                    appointmentModel.setRepeate_num(_cursor.getString(_cursor.getColumnIndex(REPEAT_NUM)));
                    appointmentModel.setRepeate_frequency(_cursor.getString(_cursor.getColumnIndex(REPEAT_FREQUENCY)));
                    appointmentModel.setRepeate_untill(_cursor.getString(_cursor.getColumnIndex(REPEAT_UNTIL)));
                    appointmentModel.setIs_holiday(_cursor.getInt(_cursor.getColumnIndex(IS_HOLIDAY)));
                    appointmentModel.setIs_bday(_cursor.getInt(_cursor.getColumnIndex(IS_BIRTHDAY)));


                    appointmentModel.setHasNotes(notesTable.getCountById(appointmentModel.getEventId()) >= 1);
//                    fetching attendees BD
                    ArrayList<AttendeeModel> attendeeList = new ArrayList<>();
                    if (appointmentModel.getEventId() != 0) {

                        Cursor _cursorAttendee = null;
                        try {
                            String CREATE_QUERY_ATTENDEE = "select userkid_id ,server_id, type,kt.colorCode as kidcolor,kt.name as kidname,ad.colorCode as usercolorcode,ad.name as username from attendeeTable at LEFT JOIN kidsTable kt  ON at.userkid_id == kt.kid_id LEFT JOIN adultTable ad ON at.userkid_id == ad.USERID where event_id = " + appointmentModel.getEventId();

                            _cursorAttendee = mWritableDatabase.rawQuery(CREATE_QUERY_ATTENDEE, null);
                            while (_cursorAttendee.moveToNext()) {

                                AttendeeModel attendee = new AttendeeModel();

                                attendee.setAppoitmentId(appointmentModel.getEventId());
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
                    } else {
                        AttendeeModel attendee = new AttendeeModel();
                        attendee.setId(SharedPrefUtils.getUserDetailModel(pApplication.getApplicationContext()).getId());
                        attendee.setName(SharedPrefUtils.getUserDetailModel(pApplication.getApplicationContext()).getFirst_name());
                        attendee.setServerid(0);
                        attendee.setAppoitmentId(0);
//                        attendee.setType("");
                        attendee.setColorCode(SharedPrefUtils.getUserDetailModel(pApplication.getApplicationContext()).getColor_code());
                        attendeeList.add(attendee);
                    }
                    appointmentModel.setAttendee(attendeeList);

                    allData.add(appointmentModel);

                } while (_cursor.moveToNext());
            }

        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return allData;
    }

    public ArrayList<AppointmentMappingModel> allDataBTWNdaysHome(long first, long second) {
        TableNotes notesTable = new TableNotes(pApplication);
        ArrayList<AppointmentMappingModel> allData = new ArrayList<AppointmentMappingModel>();

        Cursor _cursor = null;

        try {
            String CREATE_QUERY = " select * from " + APPOINTMENT_TABLE + " where " + STARTTIME + " >= " + first + " and " + STARTTIME + " <= " + second + " OR is_recurring = 'yes' ";
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);

            if (_cursor.moveToFirst()) {
                do {

                    AppointmentMappingModel appointmentModel = new AppointmentMappingModel();

                    appointmentModel.setpId(_cursor.getInt(_cursor.getColumnIndex(PRIMARY_KEY)));
                    appointmentModel.setEventId(_cursor.getInt(_cursor.getColumnIndex(EVENT_ID)));
                    appointmentModel.setExternalId(_cursor.getString(_cursor.getColumnIndex(EXTERNAL_EVENT_ID)));
                    appointmentModel.setAppointment_name(_cursor.getString(_cursor.getColumnIndex(APPOINTMENT_NAME)));
                    appointmentModel.setLocality(_cursor.getString(_cursor.getColumnIndex(LOCATION)));
                    appointmentModel.setStarttime(_cursor.getLong(_cursor.getColumnIndex(STARTTIME)));
                    appointmentModel.setEndtime(_cursor.getLong(_cursor.getColumnIndex(ENDTIME)));
                    appointmentModel.setIs_recurring(_cursor.getString(_cursor.getColumnIndex(IS_RECURRING)));
                    appointmentModel.setRepeat(_cursor.getString(_cursor.getColumnIndex(REPEAT)));
                    appointmentModel.setRepeate_num(_cursor.getString(_cursor.getColumnIndex(REPEAT_NUM)));
                    appointmentModel.setRepeate_frequency(_cursor.getString(_cursor.getColumnIndex(REPEAT_FREQUENCY)));
                    appointmentModel.setRepeate_untill(_cursor.getString(_cursor.getColumnIndex(REPEAT_UNTIL)));
                    appointmentModel.setHasNotes(notesTable.getCountById(appointmentModel.getEventId()) >= 1);
                    appointmentModel.setIs_holiday(_cursor.getInt(_cursor.getColumnIndex(IS_HOLIDAY)));
                    appointmentModel.setIs_bday(_cursor.getInt(_cursor.getColumnIndex(IS_BIRTHDAY)));
//                    fetching attendees BD

//                    ArrayList<AttendeeModel> attendeeList = new ArrayList<>();
//                    Cursor _cursorAttendee = null;
//                    try {
//                        String CREATE_QUERY_ATTENDEE = "select userkid_id ,server_id, type,kt.colorCode as kidcolor,kt.name as kidname,ad.colorCode as usercolorcode,ad.name as username from attendeeTable at LEFT JOIN kidsTable kt  ON at.userkid_id == kt.kid_id LEFT JOIN adultTable ad ON at.userkid_id == ad.USERID where event_id = " + appointmentModel.getEventId();
//
//                        _cursorAttendee = mWritableDatabase.rawQuery(CREATE_QUERY_ATTENDEE, null);
//                        while (_cursorAttendee.moveToNext()) {
//
//                            AttendeeModel attendee = new AttendeeModel();
//
//                            attendee.setAppoitmentId(appointmentModel.getEventId());
//                            attendee.setId(_cursorAttendee.getInt(_cursorAttendee.getColumnIndex("userkid_id")));
//                            attendee.setServerid(_cursorAttendee.getInt(_cursorAttendee.getColumnIndex("server_id")));
//
//                            String type = _cursorAttendee.getString(_cursorAttendee.getColumnIndex("type"));
//
//                            if (type.equals("user")) {
//                                attendee.setName(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("username")));
//                                attendee.setColorCode(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("usercolorcode")));
//                            } else if (type.equals("kid")) {
//                                attendee.setName(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("kidname")));
//                                attendee.setColorCode(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("kidcolor")));
//                            }
//
//                            attendeeList.add(attendee);
//                        }
//                    } catch (Exception e) {
//                        Log.e("", "" + e);
//                    } finally {
//                        closeCursor(_cursorAttendee);
//                    }
//
//                    appointmentModel.setAttendee(attendeeList);
//
//                    allData.add(appointmentModel);

                    ArrayList<AttendeeModel> attendeeList = new ArrayList<>();
                    if (appointmentModel.getEventId() != 0) {

                        Cursor _cursorAttendee = null;
                        try {
                            String CREATE_QUERY_ATTENDEE = "select userkid_id ,server_id, type,kt.colorCode as kidcolor,kt.name as kidname,ad.colorCode as usercolorcode,ad.name as username from attendeeTable at LEFT JOIN kidsTable kt  ON at.userkid_id == kt.kid_id LEFT JOIN adultTable ad ON at.userkid_id == ad.USERID where event_id = " + appointmentModel.getEventId();

                            _cursorAttendee = mWritableDatabase.rawQuery(CREATE_QUERY_ATTENDEE, null);
                            while (_cursorAttendee.moveToNext()) {

                                AttendeeModel attendee = new AttendeeModel();

                                attendee.setAppoitmentId(appointmentModel.getEventId());
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
                    } else {
                        AttendeeModel attendee = new AttendeeModel();
                        attendee.setId(SharedPrefUtils.getUserDetailModel(pApplication.getApplicationContext()).getId());
                        attendee.setName(SharedPrefUtils.getUserDetailModel(pApplication.getApplicationContext()).getFirst_name());
                        attendee.setServerid(0);
                        attendee.setAppoitmentId(0);
                        attendee.setColorCode(SharedPrefUtils.getUserDetailModel(pApplication.getApplicationContext()).getColor_code());
                        attendeeList.add(attendee);
                    }
                    appointmentModel.setAttendee(attendeeList);

                    allData.add(appointmentModel);

                } while (_cursor.moveToNext());
            }

        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return allData;
    }

    public AppoitmentDataModel.AppointmentDetail getAppointment(int event_id) {

        AppoitmentDataModel.AppointmentDetail appointmentDetail = new AppoitmentDataModel().new AppointmentDetail();

        Cursor _cursor = null;

        try {
            String CREATE_QUERY = " select * from " + APPOINTMENT_TABLE + " where event_id = " + event_id;
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);

            if (_cursor.moveToFirst()) {
                do {

                    appointmentDetail.setId(_cursor.getInt(_cursor.getColumnIndex(EVENT_ID)));
                    appointmentDetail.setExternal_id(_cursor.getString(_cursor.getColumnIndex(EXTERNAL_EVENT_ID)));
                    appointmentDetail.setAppointment_name(_cursor.getString(_cursor.getColumnIndex(APPOINTMENT_NAME)));
                    appointmentDetail.setLocality(_cursor.getString(_cursor.getColumnIndex(LOCATION)));
                    appointmentDetail.setStarttime(_cursor.getLong(_cursor.getColumnIndex(STARTTIME)));
                    appointmentDetail.setEndtime(_cursor.getLong(_cursor.getColumnIndex(ENDTIME)));
                    appointmentDetail.setReminder(_cursor.getString(_cursor.getColumnIndex(REMINDER)));
                    appointmentDetail.setIs_recurring(_cursor.getString(_cursor.getColumnIndex(IS_RECURRING)));
                    appointmentDetail.setRepeate(_cursor.getString(_cursor.getColumnIndex(REPEAT)));
                    appointmentDetail.setRepeate_frequency(_cursor.getString(_cursor.getColumnIndex(REPEAT_FREQUENCY)));
                    appointmentDetail.setRepeate_num(_cursor.getString(_cursor.getColumnIndex(REPEAT_NUM)));
                    appointmentDetail.setRepeate_untill(_cursor.getString(_cursor.getColumnIndex(REPEAT_UNTIL)));

                } while (_cursor.moveToNext());
            }

        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return appointmentDetail;
    }


    public void deleteAppointment(String pId, Boolean isExternal) {

        //Cursor _cursor = null;
        try {
            int i;
            if (isExternal) {
                i = mWritableDatabase.delete(APPOINTMENT_TABLE, EXTERNAL_EVENT_ID + " = '" + pId + "'", null);
            } else {
                i = mWritableDatabase.delete(APPOINTMENT_TABLE, EVENT_ID + "=" + Integer.parseInt(pId), null);
            }
            Log.e("delete appoitmnet", "" + i);
        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            // closeCursor(_cursor);
        }

    }

    public ArrayList<AppointmentMappingModel> filterByName(int id, String type, long first, long second) {

        ArrayList<AppointmentMappingModel> allData = new ArrayList<AppointmentMappingModel>();
        Cursor _cursor = null;
        Cursor _cursor_inner = null;
        int eventId;

        try {
            String CREATE_QUERY = " select * from attendeeTable where userkid_id = " + id + " and type like '%" + type + "%'";
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);

            if (_cursor.moveToFirst()) {
                do {

                    try {
                        eventId = _cursor.getInt(_cursor.getColumnIndex("event_id"));

//                    String CREATE_QUERY_INNER = " select * from " + APPOINTMENT_TABLE + " where event_id = " + eventId + " and " + STARTTIME + " >= " + first + " and " + STARTTIME + " <= " + second + " or is_recurring = 'yes' ";
                        String CREATE_QUERY_INNER = " select * from " + APPOINTMENT_TABLE + " where event_id = " + eventId + " and is_recurring = 'yes' ";

                        _cursor_inner = mWritableDatabase.rawQuery(CREATE_QUERY_INNER, null);

                        if (_cursor_inner.moveToFirst()) {
                            do {
                                AppointmentMappingModel appointmentModel = new AppointmentMappingModel();

                                appointmentModel.setpId(_cursor_inner.getInt(_cursor_inner.getColumnIndex(PRIMARY_KEY)));
                                appointmentModel.setEventId(_cursor_inner.getInt(_cursor_inner.getColumnIndex(EVENT_ID)));
                                appointmentModel.setExternalId(_cursor_inner.getString(_cursor_inner.getColumnIndex(EXTERNAL_EVENT_ID)));
                                appointmentModel.setAppointment_name(_cursor_inner.getString(_cursor_inner.getColumnIndex(APPOINTMENT_NAME)));
                                appointmentModel.setLocality(_cursor_inner.getString(_cursor_inner.getColumnIndex(LOCATION)));
                                appointmentModel.setStarttime(_cursor_inner.getLong(_cursor_inner.getColumnIndex(STARTTIME)));
                                appointmentModel.setEndtime(_cursor_inner.getLong(_cursor_inner.getColumnIndex(ENDTIME)));
                                appointmentModel.setIs_recurring(_cursor_inner.getString(_cursor_inner.getColumnIndex(IS_RECURRING)));
                                appointmentModel.setRepeat(_cursor_inner.getString(_cursor_inner.getColumnIndex(REPEAT)));
                                appointmentModel.setRepeate_num(_cursor_inner.getString(_cursor_inner.getColumnIndex(REPEAT_NUM)));
                                appointmentModel.setRepeate_frequency(_cursor_inner.getString(_cursor_inner.getColumnIndex(REPEAT_FREQUENCY)));
                                appointmentModel.setRepeate_untill(_cursor_inner.getString(_cursor_inner.getColumnIndex(REPEAT_UNTIL)));
                                appointmentModel.setIs_holiday(_cursor.getInt(_cursor.getColumnIndex(IS_HOLIDAY)));
                                appointmentModel.setIs_bday(_cursor.getInt(_cursor.getColumnIndex(IS_BIRTHDAY)));
                                ArrayList<AttendeeModel> attendeeList = new ArrayList<>();
                                if (appointmentModel.getEventId() != 0) {

                                    Cursor _cursorAttendee = null;
                                    try {
                                        String CREATE_QUERY_ATTENDEE = "select userkid_id ,server_id, type,kt.colorCode as kidcolor,kt.name as kidname,ad.colorCode as usercolorcode,ad.name as username from attendeeTable at LEFT JOIN kidsTable kt  ON at.userkid_id == kt.kid_id LEFT JOIN adultTable ad ON at.userkid_id == ad.USERID where event_id = " + appointmentModel.getEventId();

                                        _cursorAttendee = mWritableDatabase.rawQuery(CREATE_QUERY_ATTENDEE, null);
                                        while (_cursorAttendee.moveToNext()) {

                                            AttendeeModel attendee = new AttendeeModel();

                                            attendee.setAppoitmentId(appointmentModel.getEventId());
                                            attendee.setId(_cursorAttendee.getInt(_cursorAttendee.getColumnIndex("userkid_id")));
                                            attendee.setServerid(_cursorAttendee.getInt(_cursorAttendee.getColumnIndex("server_id")));

                                            String type_uk = _cursorAttendee.getString(_cursorAttendee.getColumnIndex("type"));

                                            if (type_uk.equals("user")) {
                                                attendee.setName(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("username")));
                                                attendee.setColorCode(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("usercolorcode")));
                                            } else if (type_uk.equals("kid")) {
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
                                } else {
                                    AttendeeModel attendee = new AttendeeModel();
                                    attendee.setId(SharedPrefUtils.getUserDetailModel(pApplication.getApplicationContext()).getId());
                                    attendee.setName(SharedPrefUtils.getUserDetailModel(pApplication.getApplicationContext()).getFirst_name());
                                    attendee.setServerid(0);
                                    attendee.setAppoitmentId(0);
                                    attendee.setColorCode(SharedPrefUtils.getUserDetailModel(pApplication.getApplicationContext()).getColor_code());
                                    attendeeList.add(attendee);
                                }
                                appointmentModel.setAttendee(attendeeList);

                                allData.add(appointmentModel);

//                                ArrayList<AttendeeModel> attendeeList = new ArrayList<>();
//                                try {
//                                    String CREATE_QUERY_ATTENDEE = "select userkid_id ,server_id, type,kt.colorCode as kidcolor,kt.name as kidname,ad.colorCode as usercolorcode,ad.name as username from attendeeTable at LEFT JOIN kidsTable kt  ON at.userkid_id == kt.kid_id LEFT JOIN adultTable ad ON at.userkid_id == ad.USERID where event_id = " + appointmentModel.getEventId();
//
//                                    _cursorAttendee = mWritableDatabase.rawQuery(CREATE_QUERY_ATTENDEE, null);
//                                    while (_cursorAttendee.moveToNext()) {
//
//                                        AttendeeModel attendee = new AttendeeModel();
//                                        attendee.setAppoitmentId(appointmentModel.getEventId());
//                                        attendee.setId(_cursorAttendee.getInt(_cursorAttendee.getColumnIndex("userkid_id")));
//                                        attendee.setServerid(_cursorAttendee.getInt(_cursorAttendee.getColumnIndex("server_id")));
//
//                                        String type_uk = _cursorAttendee.getString(_cursorAttendee.getColumnIndex("type"));
//
//                                        if (type_uk.equals("user")) {
//                                            attendee.setName(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("username")));
//                                            attendee.setColorCode(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("usercolorcode")));
//                                        } else if (type_uk.equals("kid")) {
//                                            attendee.setName(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("kidname")));
//                                            attendee.setColorCode(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("kidcolor")));
//                                        }
//
//                                        attendeeList.add(attendee);
//                                    }
//                                } catch (Exception e) {
//                                    Log.e("", "" + e);
//                                } finally {
//                                    closeCursor(_cursorAttendee);
//                                }
//                                appointmentModel.setAttendee(attendeeList);
//
//                                allData.add(appointmentModel);
                            } while (_cursor_inner.moveToNext());

                        }
                    } catch (Exception e0) {
                        Log.e("", "DB issue" + e0);
                    } finally {
                        closeCursor(_cursor_inner);
                    }

                } while (_cursor.moveToNext());
            }

        } catch (Exception e) {
            Log.e("", "DB issue" + e);
        } finally {
            closeCursor(_cursor);
        }
        return allData;
    }


    public ArrayList<AppointmentMappingModel> filterByNameCalDay(int id, String type, long first, long second) {

        ArrayList<AppointmentMappingModel> allData = new ArrayList<AppointmentMappingModel>();
        Cursor _cursor = null;
        Cursor _cursor_inner = null;
        int eventId;

        try {
            String CREATE_QUERY = " select * from attendeeTable where userkid_id = " + id + " and  type like '%" + type + "%'";
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);

            if (_cursor.moveToFirst()) {
                do {

                    eventId = _cursor.getInt(_cursor.getColumnIndex("event_id"));

                    String CREATE_QUERY_INNER = " select * from " + APPOINTMENT_TABLE + " where event_id = " + eventId;
//                    String CREATE_QUERY_INNER = " select * from " + APPOINTMENT_TABLE + " where event_id = " + eventId + " and " + STARTTIME + " >= " + first + " and " + ENDTIME + " <= " + second;
                    _cursor_inner = mWritableDatabase.rawQuery(CREATE_QUERY_INNER, null);

                    if (_cursor_inner.moveToFirst()) {
                        do {
                            AppointmentMappingModel appointmentModel = new AppointmentMappingModel();

                            appointmentModel.setpId(_cursor_inner.getInt(_cursor_inner.getColumnIndex(PRIMARY_KEY)));
                            appointmentModel.setEventId(_cursor_inner.getInt(_cursor_inner.getColumnIndex(EVENT_ID)));
                            appointmentModel.setExternalId(_cursor_inner.getString(_cursor_inner.getColumnIndex(EXTERNAL_EVENT_ID)));
                            appointmentModel.setAppointment_name(_cursor_inner.getString(_cursor_inner.getColumnIndex(APPOINTMENT_NAME)));
                            appointmentModel.setLocality(_cursor_inner.getString(_cursor_inner.getColumnIndex(LOCATION)));
                            appointmentModel.setStarttime(_cursor_inner.getLong(_cursor_inner.getColumnIndex(STARTTIME)));
                            appointmentModel.setEndtime(_cursor_inner.getLong(_cursor_inner.getColumnIndex(ENDTIME)));
                            appointmentModel.setIs_recurring(_cursor_inner.getString(_cursor_inner.getColumnIndex(IS_RECURRING)));
                            appointmentModel.setRepeat(_cursor_inner.getString(_cursor_inner.getColumnIndex(REPEAT)));
                            appointmentModel.setRepeate_num(_cursor_inner.getString(_cursor_inner.getColumnIndex(REPEAT_NUM)));
                            appointmentModel.setRepeate_frequency(_cursor_inner.getString(_cursor_inner.getColumnIndex(REPEAT_FREQUENCY)));
                            appointmentModel.setRepeate_untill(_cursor_inner.getString(_cursor_inner.getColumnIndex(REPEAT_UNTIL)));


                            ArrayList<AttendeeModel> attendeeList = new ArrayList<>();
                            if (appointmentModel.getEventId() != 0) {

                                Cursor _cursorAttendee = null;
                                try {
                                    String CREATE_QUERY_ATTENDEE = "select userkid_id ,server_id, type,kt.colorCode as kidcolor,kt.name as kidname,ad.colorCode as usercolorcode,ad.name as username from attendeeTable at LEFT JOIN kidsTable kt  ON at.userkid_id == kt.kid_id LEFT JOIN adultTable ad ON at.userkid_id == ad.USERID where event_id = " + appointmentModel.getEventId();

                                    _cursorAttendee = mWritableDatabase.rawQuery(CREATE_QUERY_ATTENDEE, null);
                                    while (_cursorAttendee.moveToNext()) {

                                        AttendeeModel attendee = new AttendeeModel();

                                        attendee.setAppoitmentId(appointmentModel.getEventId());
                                        attendee.setId(_cursorAttendee.getInt(_cursorAttendee.getColumnIndex("userkid_id")));
                                        attendee.setServerid(_cursorAttendee.getInt(_cursorAttendee.getColumnIndex("server_id")));

                                        String type_uk = _cursorAttendee.getString(_cursorAttendee.getColumnIndex("type"));

                                        if (type_uk.equals("user")) {
                                            attendee.setName(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("username")));
                                            attendee.setColorCode(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("usercolorcode")));
                                        } else if (type_uk.equals("kid")) {
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
                            } else {
                                AttendeeModel attendee = new AttendeeModel();
                                attendee.setId(SharedPrefUtils.getUserDetailModel(pApplication.getApplicationContext()).getId());
                                attendee.setName(SharedPrefUtils.getUserDetailModel(pApplication.getApplicationContext()).getFirst_name());
                                attendee.setServerid(0);
                                attendee.setAppoitmentId(0);
                                attendee.setColorCode(SharedPrefUtils.getUserDetailModel(pApplication.getApplicationContext()).getColor_code());
                                attendeeList.add(attendee);
                            }
                            appointmentModel.setAttendee(attendeeList);

                            allData.add(appointmentModel);

//                            Cursor _cursorAttendee = null;
//                            ArrayList<AttendeeModel> attendeeList = new ArrayList<>();
//                            try {
//                                String CREATE_QUERY_ATTENDEE = "select userkid_id ,server_id, type,kt.colorCode as kidcolor,kt.name as kidname,ad.colorCode as usercolorcode,ad.name as username from attendeeTable at LEFT JOIN kidsTable kt  ON at.userkid_id == kt.kid_id LEFT JOIN adultTable ad ON at.userkid_id == ad.USERID where event_id = " + appointmentModel.getEventId();
//
//                                _cursorAttendee = mWritableDatabase.rawQuery(CREATE_QUERY_ATTENDEE, null);
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

                        } while (_cursor_inner.moveToNext());
                    }
                } while (_cursor.moveToNext());
            }

        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return allData;
    }

    public ArrayList<AppointmentMappingModel> filterByName_1(int id, String type, long first, long second) {

        ArrayList<AppointmentMappingModel> allData = new ArrayList<AppointmentMappingModel>();
        Cursor _cursor = null;
        Cursor _cursor_inner = null;
        int eventId;

        try {
            String CREATE_QUERY = " select * from attendeeTable where userkid_id = " + id + " and type like '%" + type + "%'";
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);

            if (_cursor.moveToFirst()) {
                do {

                    eventId = _cursor.getInt(_cursor.getColumnIndex("event_id"));

//                    String CREATE_QUERY_INNER = " select * from " + APPOINTMENT_TABLE + " where event_id = " + eventId + " and " + STARTTIME + " >= " + first + " and " + ENDTIME + " <= " + second + " or is_recurring = 'yes' ";
                    String CREATE_QUERY_INNER = " select * from " + APPOINTMENT_TABLE + " where event_id = " + eventId + " and " + STARTTIME + " >= " + first + " and " + ENDTIME + " <= " + second;
                    _cursor_inner = mWritableDatabase.rawQuery(CREATE_QUERY_INNER, null);

                    if (_cursor_inner.moveToFirst()) {
                        do {
                            AppointmentMappingModel appointmentModel = new AppointmentMappingModel();

                            appointmentModel.setpId(_cursor_inner.getInt(_cursor_inner.getColumnIndex(PRIMARY_KEY)));
                            appointmentModel.setEventId(_cursor_inner.getInt(_cursor_inner.getColumnIndex(EVENT_ID)));
                            appointmentModel.setExternalId(_cursor_inner.getString(_cursor_inner.getColumnIndex(EXTERNAL_EVENT_ID)));
                            appointmentModel.setAppointment_name(_cursor_inner.getString(_cursor_inner.getColumnIndex(APPOINTMENT_NAME)));
                            appointmentModel.setLocality(_cursor_inner.getString(_cursor_inner.getColumnIndex(LOCATION)));
                            appointmentModel.setStarttime(_cursor_inner.getLong(_cursor_inner.getColumnIndex(STARTTIME)));
                            appointmentModel.setEndtime(_cursor_inner.getLong(_cursor_inner.getColumnIndex(ENDTIME)));
                            appointmentModel.setIs_recurring(_cursor_inner.getString(_cursor_inner.getColumnIndex(IS_RECURRING)));
                            appointmentModel.setRepeat(_cursor_inner.getString(_cursor_inner.getColumnIndex(REPEAT)));
                            appointmentModel.setRepeate_num(_cursor_inner.getString(_cursor_inner.getColumnIndex(REPEAT_NUM)));
                            appointmentModel.setRepeate_frequency(_cursor_inner.getString(_cursor_inner.getColumnIndex(REPEAT_FREQUENCY)));
                            appointmentModel.setIs_holiday(_cursor.getInt(_cursor.getColumnIndex(IS_HOLIDAY)));
                            appointmentModel.setIs_bday(_cursor.getInt(_cursor.getColumnIndex(IS_BIRTHDAY)));
                            appointmentModel.setRepeate_untill(_cursor_inner.getString(_cursor_inner.getColumnIndex(REPEAT_UNTIL)));
//                            Cursor _cursorAttendee = null;
//                            ArrayList<AttendeeModel> attendeeList = new ArrayList<>();
//                            try {
//                                String CREATE_QUERY_ATTENDEE = "select userkid_id ,server_id, type,kt.colorCode as kidcolor,kt.name as kidname,ad.colorCode as usercolorcode,ad.name as username from attendeeTable at LEFT JOIN kidsTable kt  ON at.userkid_id == kt.kid_id LEFT JOIN adultTable ad ON at.userkid_id == ad.USERID where event_id = " + appointmentModel.getEventId();
//
//                                _cursorAttendee = mWritableDatabase.rawQuery(CREATE_QUERY_ATTENDEE, null);
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

                            ArrayList<AttendeeModel> attendeeList = new ArrayList<>();
                            if (appointmentModel.getEventId() != 0) {

                                Cursor _cursorAttendee = null;
                                try {
                                    String CREATE_QUERY_ATTENDEE = "select userkid_id ,server_id, type,kt.colorCode as kidcolor,kt.name as kidname,ad.colorCode as usercolorcode,ad.name as username from attendeeTable at LEFT JOIN kidsTable kt  ON at.userkid_id == kt.kid_id LEFT JOIN adultTable ad ON at.userkid_id == ad.USERID where event_id = " + appointmentModel.getEventId();

                                    _cursorAttendee = mWritableDatabase.rawQuery(CREATE_QUERY_ATTENDEE, null);
                                    while (_cursorAttendee.moveToNext()) {

                                        AttendeeModel attendee = new AttendeeModel();

                                        attendee.setAppoitmentId(appointmentModel.getEventId());
                                        attendee.setId(_cursorAttendee.getInt(_cursorAttendee.getColumnIndex("userkid_id")));
                                        attendee.setServerid(_cursorAttendee.getInt(_cursorAttendee.getColumnIndex("server_id")));

                                        String type_uk = _cursorAttendee.getString(_cursorAttendee.getColumnIndex("type"));

                                        if (type_uk.equals("user")) {
                                            attendee.setName(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("username")));
                                            attendee.setColorCode(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("usercolorcode")));
                                        } else if (type_uk.equals("kid")) {
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
                            } else {
                                AttendeeModel attendee = new AttendeeModel();
                                attendee.setId(SharedPrefUtils.getUserDetailModel(pApplication.getApplicationContext()).getId());
                                attendee.setName(SharedPrefUtils.getUserDetailModel(pApplication.getApplicationContext()).getFirst_name());
                                attendee.setServerid(0);
                                attendee.setAppoitmentId(0);
                                attendee.setColorCode(SharedPrefUtils.getUserDetailModel(pApplication.getApplicationContext()).getColor_code());
                                attendeeList.add(attendee);
                            }
                            appointmentModel.setAttendee(attendeeList);

                            allData.add(appointmentModel);

                        } while (_cursor_inner.moveToNext());
                    }
                } while (_cursor.moveToNext());
            }

        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return allData;
    }

    public ArrayList<AppointmentMappingModel> filterByName_2(int id, String type) {

        ArrayList<AppointmentMappingModel> allData = new ArrayList<AppointmentMappingModel>();
        Cursor _cursor = null;
        Cursor _cursor_inner = null;
        int eventId;

        try {
            String CREATE_QUERY = " select * from attendeeTable where userkid_id = " + id + " and type like '%" + type + "%'";
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);

            if (_cursor.moveToFirst()) {
                do {

                    eventId = _cursor.getInt(_cursor.getColumnIndex("event_id"));

//                    String CREATE_QUERY_INNER = " select * from " + APPOINTMENT_TABLE + " where event_id = " + eventId + " and " + STARTTIME + " >= " + first + " and " + ENDTIME + " <= " + second + " or is_recurring = 'yes' ";
                    String CREATE_QUERY_INNER = " select * from " + APPOINTMENT_TABLE + " where event_id = " + eventId;
                    _cursor_inner = mWritableDatabase.rawQuery(CREATE_QUERY_INNER, null);

                    if (_cursor_inner.moveToFirst()) {
                        do {
                            AppointmentMappingModel appointmentModel = new AppointmentMappingModel();

                            appointmentModel.setpId(_cursor_inner.getInt(_cursor_inner.getColumnIndex(PRIMARY_KEY)));
                            appointmentModel.setEventId(_cursor_inner.getInt(_cursor_inner.getColumnIndex(EVENT_ID)));
                            appointmentModel.setExternalId(_cursor_inner.getString(_cursor_inner.getColumnIndex(EXTERNAL_EVENT_ID)));
                            appointmentModel.setAppointment_name(_cursor_inner.getString(_cursor_inner.getColumnIndex(APPOINTMENT_NAME)));
                            appointmentModel.setLocality(_cursor_inner.getString(_cursor_inner.getColumnIndex(LOCATION)));
                            appointmentModel.setStarttime(_cursor_inner.getLong(_cursor_inner.getColumnIndex(STARTTIME)));
                            appointmentModel.setEndtime(_cursor_inner.getLong(_cursor_inner.getColumnIndex(ENDTIME)));
                            appointmentModel.setIs_recurring(_cursor_inner.getString(_cursor_inner.getColumnIndex(IS_RECURRING)));
                            appointmentModel.setRepeat(_cursor_inner.getString(_cursor_inner.getColumnIndex(REPEAT)));
                            appointmentModel.setRepeate_num(_cursor_inner.getString(_cursor_inner.getColumnIndex(REPEAT_NUM)));
                            appointmentModel.setRepeate_frequency(_cursor_inner.getString(_cursor_inner.getColumnIndex(REPEAT_FREQUENCY)));
                            appointmentModel.setRepeate_untill(_cursor_inner.getString(_cursor_inner.getColumnIndex(REPEAT_UNTIL)));
                            appointmentModel.setIs_holiday(_cursor.getInt(_cursor.getColumnIndex(IS_HOLIDAY)));
                            appointmentModel.setIs_bday(_cursor.getInt(_cursor.getColumnIndex(IS_BIRTHDAY)));
                            ArrayList<AttendeeModel> attendeeList = new ArrayList<>();
                            if (appointmentModel.getEventId() != 0) {

                                Cursor _cursorAttendee = null;
                                try {
                                    String CREATE_QUERY_ATTENDEE = "select userkid_id ,server_id, type,kt.colorCode as kidcolor,kt.name as kidname,ad.colorCode as usercolorcode,ad.name as username from attendeeTable at LEFT JOIN kidsTable kt  ON at.userkid_id == kt.kid_id LEFT JOIN adultTable ad ON at.userkid_id == ad.USERID where event_id = " + appointmentModel.getEventId();

                                    _cursorAttendee = mWritableDatabase.rawQuery(CREATE_QUERY_ATTENDEE, null);
                                    while (_cursorAttendee.moveToNext()) {

                                        AttendeeModel attendee = new AttendeeModel();

                                        attendee.setAppoitmentId(appointmentModel.getEventId());
                                        attendee.setId(_cursorAttendee.getInt(_cursorAttendee.getColumnIndex("userkid_id")));
                                        attendee.setServerid(_cursorAttendee.getInt(_cursorAttendee.getColumnIndex("server_id")));

                                        String type_uk = _cursorAttendee.getString(_cursorAttendee.getColumnIndex("type"));

                                        if (type_uk.equals("user")) {
                                            attendee.setName(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("username")));
                                            attendee.setColorCode(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("usercolorcode")));
                                        } else if (type_uk.equals("kid")) {
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
                            } else {
                                AttendeeModel attendee = new AttendeeModel();
                                attendee.setId(SharedPrefUtils.getUserDetailModel(pApplication.getApplicationContext()).getId());
                                attendee.setName(SharedPrefUtils.getUserDetailModel(pApplication.getApplicationContext()).getFirst_name());
                                attendee.setServerid(0);
                                attendee.setAppoitmentId(0);
                                attendee.setColorCode(SharedPrefUtils.getUserDetailModel(pApplication.getApplicationContext()).getColor_code());
                                attendeeList.add(attendee);
                            }
                            appointmentModel.setAttendee(attendeeList);

                            allData.add(appointmentModel);

//                            Cursor _cursorAttendee = null;
//                            ArrayList<AttendeeModel> attendeeList = new ArrayList<>();
//                            try {
//                                String CREATE_QUERY_ATTENDEE = "select userkid_id ,server_id, type,kt.colorCode as kidcolor,kt.name as kidname,ad.colorCode as usercolorcode,ad.name as username from attendeeTable at LEFT JOIN kidsTable kt  ON at.userkid_id == kt.kid_id LEFT JOIN adultTable ad ON at.userkid_id == ad.USERID where event_id = " + appointmentModel.getEventId();
//
//                                _cursorAttendee = mWritableDatabase.rawQuery(CREATE_QUERY_ATTENDEE, null);
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

                        } while (_cursor_inner.moveToNext());
                    }
                } while (_cursor.moveToNext());
            }

        } catch (Exception e) {
            Log.e("", "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return allData;
    }

    public ArrayList<AppointmentMappingModel> searchByName(String name, long first, long second) {

        ArrayList<AppointmentMappingModel> allData = new ArrayList<AppointmentMappingModel>();

        Cursor _cursor = null;

        try {
            String CREATE_QUERY = " select * from " + APPOINTMENT_TABLE + " where " + STARTTIME + " >= " + first + " and " + ENDTIME + " <= " + second + " and " + APPOINTMENT_NAME + " like '%" + name + "%'";
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);

            if (_cursor.moveToFirst()) {
                do {

                    AppointmentMappingModel appointmentModel = new AppointmentMappingModel();

                    appointmentModel.setpId(_cursor.getInt(_cursor.getColumnIndex(PRIMARY_KEY)));
                    appointmentModel.setEventId(_cursor.getInt(_cursor.getColumnIndex(EVENT_ID)));
                    appointmentModel.setExternalId(_cursor.getString(_cursor.getColumnIndex(EXTERNAL_EVENT_ID)));
                    appointmentModel.setAppointment_name(_cursor.getString(_cursor.getColumnIndex(APPOINTMENT_NAME)));
                    appointmentModel.setLocality(_cursor.getString(_cursor.getColumnIndex(LOCATION)));
                    appointmentModel.setStarttime(_cursor.getLong(_cursor.getColumnIndex(STARTTIME)));
                    appointmentModel.setEndtime(_cursor.getLong(_cursor.getColumnIndex(ENDTIME)));
                    appointmentModel.setIs_recurring(_cursor.getString(_cursor.getColumnIndex(IS_RECURRING)));
                    appointmentModel.setRepeat(_cursor.getString(_cursor.getColumnIndex(REPEAT)));
                    appointmentModel.setRepeate_num(_cursor.getString(_cursor.getColumnIndex(REPEAT_NUM)));
                    appointmentModel.setRepeate_frequency(_cursor.getString(_cursor.getColumnIndex(REPEAT_FREQUENCY)));
                    appointmentModel.setRepeate_untill(_cursor.getString(_cursor.getColumnIndex(REPEAT_UNTIL)));
//                    fetching attendees BD

//                    ArrayList<AttendeeModel> attendeeList = new ArrayList<>();
//                    Cursor _cursorAttendee = null;
//                    try {
//                        String CREATE_QUERY_ATTENDEE = "select userkid_id ,server_id, type,kt.colorCode as kidcolor,kt.name as kidname,ad.colorCode as usercolorcode,ad.name as username from attendeeTable at LEFT JOIN kidsTable kt  ON at.userkid_id == kt.kid_id LEFT JOIN adultTable ad ON at.userkid_id == ad.USERID where event_id = " + appointmentModel.getEventId();
//
//                        _cursorAttendee = mWritableDatabase.rawQuery(CREATE_QUERY_ATTENDEE, null);
//                        while (_cursorAttendee.moveToNext()) {
//
//                            AttendeeModel attendee = new AttendeeModel();
//
//                            attendee.setAppoitmentId(appointmentModel.getEventId());
//                            attendee.setId(_cursorAttendee.getInt(_cursorAttendee.getColumnIndex("userkid_id")));
//                            attendee.setServerid(_cursorAttendee.getInt(_cursorAttendee.getColumnIndex("server_id")));
//
//                            String type = _cursorAttendee.getString(_cursorAttendee.getColumnIndex("type"));
//
//                            if (type.equals("user")) {
//                                attendee.setName(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("username")));
//                                attendee.setColorCode(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("usercolorcode")));
//                            } else if (type.equals("kid")) {
//                                attendee.setName(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("kidname")));
//                                attendee.setColorCode(_cursorAttendee.getString(_cursorAttendee.getColumnIndex("kidcolor")));
//                            }
//
//                            attendeeList.add(attendee);
//                        }
//                    } catch (Exception e) {
//                        Log.e("", "" + e);
//                    } finally {
//                        closeCursor(_cursorAttendee);
//                    }
//
//                    appointmentModel.setAttendee(attendeeList);
//
//                    allData.add(appointmentModel);

                    ArrayList<AttendeeModel> attendeeList = new ArrayList<>();
                    if (appointmentModel.getEventId() != 0) {

                        Cursor _cursorAttendee = null;
                        try {
                            String CREATE_QUERY_ATTENDEE = "select userkid_id ,server_id, type,kt.colorCode as kidcolor,kt.name as kidname,ad.colorCode as usercolorcode,ad.name as username from attendeeTable at LEFT JOIN kidsTable kt  ON at.userkid_id == kt.kid_id LEFT JOIN adultTable ad ON at.userkid_id == ad.USERID where event_id = " + appointmentModel.getEventId();

                            _cursorAttendee = mWritableDatabase.rawQuery(CREATE_QUERY_ATTENDEE, null);
                            while (_cursorAttendee.moveToNext()) {

                                AttendeeModel attendee = new AttendeeModel();

                                attendee.setAppoitmentId(appointmentModel.getEventId());
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
                    } else {
                        AttendeeModel attendee = new AttendeeModel();
                        attendee.setId(SharedPrefUtils.getUserDetailModel(pApplication.getApplicationContext()).getId());
                        attendee.setName(SharedPrefUtils.getUserDetailModel(pApplication.getApplicationContext()).getFirst_name());
                        attendee.setServerid(0);
                        attendee.setAppoitmentId(0);
                        attendee.setColorCode(SharedPrefUtils.getUserDetailModel(pApplication.getApplicationContext()).getColor_code());
                        attendeeList.add(attendee);
                    }
                    appointmentModel.setAttendee(attendeeList);

                    allData.add(appointmentModel);

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