/*package com.kelltontech.download;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.kelltontech.model.BaseModel;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.database.BaseTable;

public class FileCacheTable extends BaseTable {

	private final int DAYS_TO_DELETE = 30 ; 
	private final int REF_COUNT_TO_DELETE = 10 ;
	
	public FileCacheTable(BaseApplication pApplication, String pTableName) {
		super(pApplication, pTableName);
	}

	@Override
	protected ContentValues getContentValues(BaseModel pModel) 
	{
		FileDownloadRqRsModel fileModel = (FileDownloadRqRsModel) pModel;
		ContentValues values = new ContentValues();
		values.put( DB_COLUMN_FILE_URL, fileModel.getFileUrl());
		values.put( DB_COLUMN_FILE_BLOB, fileModel.getFileData());
		values.put( DB_COLUMN_FILE_ACCESS_TIME , fileModel.getAccessTime());
		values.put( DB_COLUMN_FILE_REFERENCE_COUNT , fileModel.getReferenceCount());
		
		return values ;
	}

	@Override
	protected ArrayList<BaseModel> getAllData(String pSelection, String[] pSelectionArgs) {
		Cursor cursor = null;
		ArrayList<BaseModel> fileList = new ArrayList<BaseModel>();
		try
		{
			cursor = mWritableDatabase.query(true, mTableName, null, pSelection, pSelectionArgs, null, null, null, null);
			FileDownloadRqRsModel model = null;

			while (cursor.moveToNext())
			{
				model = new FileDownloadRqRsModel(cursor.getString(cursor.getColumnIndex( DB_COLUMN_FILE_URL)) ) ;
				model.setFileUrl(cursor.getString(cursor.getColumnIndex( DB_COLUMN_FILE_URL)));
				model.setAccessTime(cursor.getString(cursor.getColumnIndex( DB_COLUMN_FILE_ACCESS_TIME)));
				model.setReferenceCount(cursor.getInt(cursor.getColumnIndex( DB_COLUMN_FILE_REFERENCE_COUNT)));
				model.setFileData(cursor.getBlob(cursor.getColumnIndex( DB_COLUMN_FILE_BLOB)));
				
				fileList.add(model);
			}
		}
		catch (Exception e)
		{
			Log.e(mTableName, "getAllData()", e);
		}
		finally
		{
			closeCursor(cursor);
		}
		return fileList;
	}
	*//**
	 * This function is used to save a file into database
	 * 
	 * @param file_model holds the file property that we need to add in database like its URL , ByteArray.
	 *//*
	public void addFile(FileDownloadRqRsModel file_model) 
	{
		if(!isExists(file_model.getFileUrl()))
		{
			// image model is being created to add image object to database 
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" , Locale.getDefault() ) ; // to add in a specific manner by which it can used in database by time attribute
			String dateFromCheck = sdf.format(new Date(System. currentTimeMillis())) ;
			file_model.setAccessTime(dateFromCheck) ; 
			file_model.setReferenceCount(0) ; 
			insertData(file_model) ;			
		}
	}
	*//**
	 * This function is used to delete all those images , which are not used from a long time in application.
	 * <br/>
	 * This function is get called when memory gets low in device
	 *//*
	public void refineImages() 
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" , Locale.getDefault() ) ;
		String dateFromCheck = sdf.format(new Date(System. currentTimeMillis() -  86400 * DAYS_TO_DELETE )) ; 
		String sql_query = "delete from " + mTableName + " where " + DB_COLUMN_FILE_ACCESS_TIME + " <= DATETIME('" + dateFromCheck + "') and " + DB_COLUMN_FILE_REFERENCE_COUNT + " <= " + REF_COUNT_TO_DELETE ;
		mWritableDatabase.execSQL(sql_query) ;
	}
	*//**
	 * This function is used to get file model object from database containing files detail like blob & url.
	 * @param file_url url where the file exists
	 * @return FileDownloadRqRsModel object or null in case if not exist in database
	 *//*
	public FileDownloadRqRsModel getFile(final String file_url )  
	{
		FileDownloadRqRsModel _file = null ;
		
		String query = "select * from " + mTableName + " where " + DB_COLUMN_FILE_URL + " = '" +  file_url + "' " ;
		Cursor cr = mWritableDatabase.rawQuery(query, null) ;
		
		while(cr.moveToNext())
		{
			_file = new FileDownloadRqRsModel(cr.getString(1)) ; 
			_file.setFileUrl(cr.getString(1)) ; 
			_file.setAccessTime(cr.getString(2)) ;  
			_file.setFileData(cr.getBlob(3)) ;  
			_file.setReferenceCount(cr.getInt(4)) ; 
		}
		updateRefCount(file_url);
		return _file ; 
	}
	private boolean isExists(String image_url) 
	{
		String query = "select * from " + mTableName + " where " + DB_COLUMN_FILE_URL + " = '" +  image_url + "' " ;
		Cursor cr = mWritableDatabase.rawQuery(query, null) ;
		if(cr != null && cr.moveToNext())
		{
			updateRefCount(image_url);
			return true ;
		}
		return false;
	}

	private void updateRefCount(String image_url) 
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" , Locale.getDefault() ) ;
		String dateToInsert = sdf.format(new Date(System. currentTimeMillis() )) ;
		
		String str = "update " + mTableName + " set " + DB_COLUMN_FILE_ACCESS_TIME + " = '" + dateToInsert + "'" + " , " + DB_COLUMN_FILE_REFERENCE_COUNT + " = " + DB_COLUMN_FILE_REFERENCE_COUNT + " + 1 where " + DB_COLUMN_FILE_URL + " = '" + image_url + "'"   ;
		mWritableDatabase.execSQL(str) ; 
	}
}
*/