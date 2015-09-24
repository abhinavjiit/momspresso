package com.mycity4kids.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.kelltontech.model.BaseModel;
import com.mycity4kids.application.BaseApplication;

public abstract class BaseTable implements DBManifest {
    protected SQLiteDatabase mWritableDatabase;
    protected String mTableName;

    /**
     * Get the global instance of the SQLiteDatabase.
     *
     * @param callerActivity
     */
    public BaseTable(BaseApplication pApplication, String pTableName) {
        mWritableDatabase = pApplication.getWritableDbInstance();
        mTableName = pTableName;
    }

    /**
     * @return the pPrimaryKey of newly inserted row, SqliteConfig.DB_INVALID_ID in case of any error
     */
    public final Object insertData(BaseModel pModel) {
        try {
            return mWritableDatabase.insert(mTableName, null, getContentValues(pModel));
        } catch (Exception e) {
            Log.e(mTableName, "insertData()", e);
            return DB_INVALID_ID;
        }
    }

    public final Object insertWithOnConflict(BaseModel pModel) {
        try {
            return mWritableDatabase.insertWithOnConflict(mTableName, null, getContentValues(pModel), SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception e) {
            Log.e(mTableName, "insertWithOnConflict()", e);
            return DB_INVALID_ID;
        }
    }


	/*public final Object updateData(BaseModel pModel) {
        try {
			return mWritableDatabase.update(mTableName,getContentValues(pModel),);
		} catch (Exception e) {
			Log.e(mTableName, "insertData()", e);
			return DB_INVALID_ID;
		}
	}*/

    /**
     * added by Deepanker Chaudhary
     * These three method is used when we want to insert a large data in
     * sqlite database. (Batch file transaction)
     */
    public final void beginTransaction() {
        mWritableDatabase.beginTransaction();
    }


    public final void endTransaction() {
        mWritableDatabase.endTransaction();
    }

    public final void setTransactionSuccessful() {
        mWritableDatabase.setTransactionSuccessful();
    }

    /**
     * @return array list of all data in table
     */
    public final ArrayList<BaseModel> getAllData() {
        return getAllData(null, null);
    }

    /**
     * @return the number of rows deleted
     */
    public final int deleteAll() {
        try {
            return mWritableDatabase.delete(mTableName, null, null);
        } catch (Exception e) {
            Log.e(mTableName, "deleteAll()", e);
            return 0;
        }
    }

    public void deleteAllTables() {

        for (String name : DBManifest.TABLE_NAMES) {
            try {
                mWritableDatabase.delete(name, "1", null);
            } catch (Exception e) {
                Log.e(mTableName, "deleteAlltable()", e);

            }
        }
    }

    /**
     * @return count of total rows in table, -1 in case of any exception.
     */
    public final int getRowsCount() {
        String columnName = "rowsCount";
        String query = "select count(*) as " + columnName + "  from " + mTableName;
        int rowsCount = -1;
        Cursor cursor = null;
        try {
            cursor = mWritableDatabase.rawQuery(query, null);
            if (cursor.moveToNext()) {
                rowsCount = cursor.getInt(cursor.getColumnIndex(columnName));
            }
        } catch (Exception e) {
            Log.e(mTableName, "getRowsCount()", e);
        } finally {
            closeCursor(cursor);
        }
        return rowsCount;
    }

    /**
     * Closes the pCursor.
     *
     * @param pCursor
     */
    protected final void closeCursor(Cursor pCursor) {
        if (pCursor != null && !pCursor.isClosed()) pCursor.close();
    }

    /**
     * Helper method to create content value from BaseModel
     */
    protected abstract ContentValues getContentValues(BaseModel pModel);

    /**
     * @return array list of data selected from table
     */
    protected abstract ArrayList<BaseModel> getAllData(String pSelection, String[] pSelectionArgs);
}
