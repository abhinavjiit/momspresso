package com.mycity4kids.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mycity4kids.dbtable.TableAppointmentData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;


public class BaseDbHelper extends SQLiteOpenHelper implements DBManifest {
    String LOG_TAG = "BaseDbHelper";

    public BaseDbHelper(Context pContext) {
        super(pContext, DB_NAME, null, DB_VERSION);

//		if( ! isDataExisting() ) copyDefaultDataBase(pContext);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(LOG_TAG, "Tables creation start.");
        int size = CREATE_QUERIES.length;
        for (int i = 0; i < size; i++) {
            db.execSQL(CREATE_QUERIES[i]);
        }
        Log.i(LOG_TAG, "Tables creation end.");
    }

    //TableAdult.CREATE_ADULT_TABLE, TableKids.CREATE_KIDS_TABLE, TableFamily.CREATE_FAMILY_TABLE,TableAttendee.CREATE_ATTTENDEE_TABLE,TableWhoToRemind.CREATE_WHOTO_REMIND_TABLE,TableFile.CREATE_FILE_TABLE,TableNotes.CREATE_NOTES_TABLE,TableAppointmentData.CREATE_APPOINTMENT_TABLE};

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(LOG_TAG, "DB upgrade.");

//        for (int i = 0; i < DROP_QUERIES.length; i++){
//            db.execSQL(DROP_QUERIES[i]);
//        }
//
//        onCreate(db);

//        if (oldVersion < 2) {
//            db.execSQL("ALTER TABLE " + UserTable.USER_TABLE + " ADD COLUMN userImageUrl TEXT");
//
//            db.execSQL(TableAdult.CREATE_ADULT_TABLE);
//            db.execSQL(TableKids.CREATE_KIDS_TABLE);
//            db.execSQL(TableFamily.CREATE_FAMILY_TABLE);
//            db.execSQL(TableAttendee.CREATE_ATTTENDEE_TABLE);
//            db.execSQL(TableWhoToRemind.CREATE_WHOTO_REMIND_TABLE);
//            db.execSQL(TableFile.CREATE_FILE_TABLE);
//            db.execSQL(TableNotes.CREATE_NOTES_TABLE);
//            db.execSQL(TableAppointmentData.CREATE_APPOINTMENT_TABLE);
//        } else if (oldVersion < 9) {
//            db.execSQL("ALTER TABLE " + UserTable.USER_TABLE + " ADD (is_birthday integer,is_holiday integer,is_facebook integer,google_event integer");
//
//        }

        if (oldVersion <=2) {
            for (int i = 0; i < DROP_QUERIES.length; i++) {
                db.execSQL(DROP_QUERIES[i]);
            }
            onCreate(db);
        } else if (oldVersion < 9) {
           // db.execSQL("ALTER TABLE " + TableAppointmentData.APPOINTMENT_TABLE + " ADD COLUMN (is_birthday integer,is_holiday integer,is_facebook integer,google_event integer)");

            db.execSQL("ALTER TABLE " + TableAppointmentData.APPOINTMENT_TABLE + " ADD COLUMN is_birthday INTEGER");
            db.execSQL("ALTER TABLE " + TableAppointmentData.APPOINTMENT_TABLE + " ADD COLUMN is_holiday INTEGER");
            db.execSQL("ALTER TABLE " + TableAppointmentData.APPOINTMENT_TABLE + " ADD COLUMN is_facebook INTEGER");
            db.execSQL("ALTER TABLE " + TableAppointmentData.APPOINTMENT_TABLE + " ADD COLUMN google_event INTEGER");


        }


    }

    /**
     * @param pContext
     */
    public void copyDefaultDataBase(Context pContext) {
        InputStream defaultDbInputStream = null;
        OutputStream outputStreamOnActualDb = null;

        try {
            /**
             * Default DB file is kept in assets/defaultDatabase directory with same name as DATABASE_NAME.
             * It's name can be different.
             */
            defaultDbInputStream = pContext.getAssets().open("defaultDatabase/" + DB_NAME);
            Log.i(LOG_TAG, "Default DB File read.");

            String actualDbFilePath = getWritableDatabase().getPath();
            outputStreamOnActualDb = new FileOutputStream(actualDbFilePath);

            /**
             * Following will copy default-db-file over actual-db-file.
             */
            byte[] tempBuffer = new byte[1024];
            int readLength = 0;
            Log.i(LOG_TAG, "Copy start.");
            while ((readLength = defaultDbInputStream.read(tempBuffer)) > 0) {
                outputStreamOnActualDb.write(tempBuffer, 0, readLength);
            }
            defaultDbInputStream.close();
            outputStreamOnActualDb.flush();
            outputStreamOnActualDb.close();
            Log.i(LOG_TAG, "Copy end.");

            if (!isDataExisting()) {
                File actualDbFileObj = new File(actualDbFilePath);
                Log.i(LOG_TAG, "Removed: " + actualDbFileObj.delete());
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "" + e);
        } finally {
            try {
                if (defaultDbInputStream != null) defaultDbInputStream.close();
                if (outputStreamOnActualDb != null) {
                    outputStreamOnActualDb.flush();
                    outputStreamOnActualDb.close();
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     *
     */
    private boolean isDataExisting() {
        SQLiteDatabase database = null;
        Cursor cursor = null;
        boolean dataExists = false;
        try {
            database = getReadableDatabase();
            String columnName = "rowCount";
            for (String tabelName : TABLE_NAMES) {
                cursor = database.rawQuery("select count(*) as " + columnName + "  from " + tabelName, null);
                if (cursor.moveToNext()) {
                    dataExists = cursor.getInt(cursor.getColumnIndex(columnName)) > 0;
                }
                cursor.close();
                if (dataExists) break;
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "" + e);
        } finally {
            if (database != null) database.close();
            if (cursor != null && !cursor.isClosed()) cursor.close();
        }
        return dataExists;
    }
}