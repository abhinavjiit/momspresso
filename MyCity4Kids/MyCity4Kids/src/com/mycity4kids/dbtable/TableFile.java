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
public class TableFile extends BaseTable {

    private static final String PRIMARY_KEY = "_id";
    private static final String EVENT_ID = "event_id";
    private static final String FILE_NAME = "file_name";
    private static final String FILE_URL = "file_url";
    private static final String SERVER_ID = "server_id";
    private static final String FILE_TYPE = "file_type";

    /**
     * Table Name:-
     */

    public static final String FILE_TABLE = "fileTable";

    public static final String CREATE_FILE_TABLE = "create table if not exists " +
            FILE_TABLE + "(" + PRIMARY_KEY + " integer primary key," +
            EVENT_ID + " integer ," +
            FILE_NAME + " text  ," +
            SERVER_ID + " integer ," +
            FILE_URL + " text  ," +
            FILE_TYPE + " text  )";

    public static final String DROP_QUERY = "Drop table if exists " + FILE_TABLE;

    public TableFile(BaseApplication pApplication) {
        super(pApplication, FILE_TABLE);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected ContentValues getContentValues(BaseModel pModel) {
        AppoitmentDataModel.Files filemodel = (AppoitmentDataModel.Files) pModel;

        ContentValues _contentValue = new ContentValues();
        _contentValue.put(FILE_NAME, filemodel.getFile_name());
        _contentValue.put(EVENT_ID, filemodel.getAppointment_id());
        _contentValue.put(FILE_URL, filemodel.getUrl());
        _contentValue.put(FILE_TYPE, filemodel.getFile_type());
        _contentValue.put(SERVER_ID, filemodel.getId());


        return _contentValue;
    }




    public void AddFileList(BaseModel pModel) {
        AppoitmentDataModel.Files filemodel = (AppoitmentDataModel.Files) pModel;


        if (getDataByAppointment(filemodel.getAppointment_id()).size() > 0) {

            deleteAppointment(filemodel.getAppointment_id());
        }

        try {
            mWritableDatabase.beginTransaction();
            ContentValues _contentValue = new ContentValues();
            _contentValue.put(FILE_NAME, filemodel.getFile_name());
            _contentValue.put(EVENT_ID, filemodel.getAppointment_id());
            _contentValue.put(FILE_URL, filemodel.getUrl());
            _contentValue.put(FILE_TYPE, filemodel.getFile_type());
            _contentValue.put(SERVER_ID, filemodel.getId());

            mWritableDatabase.insert(FILE_TABLE, null, _contentValue);

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

            int i = mWritableDatabase.delete(FILE_TABLE, EVENT_ID + " = " + pId, null);

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


    public ArrayList<AppoitmentDataModel.Files> getDataByAppointment(int id) {
        ArrayList<AppoitmentDataModel.Files> fileList = new ArrayList<>();


        Cursor _cursor = null;
        try {
            String CREATE_QUERY = "select * from " + FILE_TABLE + " where " + EVENT_ID + " = " + id;
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null, null);
            while (_cursor.moveToNext()) {


                AppoitmentDataModel.Files files = new AppoitmentDataModel.Files();

                files.setAppointment_id(id);
                files.setUrl(_cursor.getString(_cursor.getColumnIndex(FILE_URL)));
                files.setFile_name(_cursor.getString(_cursor.getColumnIndex(FILE_NAME)));
                files.setFile_type(_cursor.getString(_cursor.getColumnIndex(FILE_TYPE)));
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


}
