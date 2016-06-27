package com.mycity4kids.dbtable;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.kelltontech.model.BaseModel;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.database.BaseTable;
import com.mycity4kids.models.profile.SaveProfileRequest;
import com.mycity4kids.models.user.Profile;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.models.user.UserModel;
import com.mycity4kids.models.user.UserResponse;

import java.util.ArrayList;

public class
		UserTable extends BaseTable{
	private static final String PRIMARY_KEY="_id";
	private static final String USER_ID="userId";
	private static final String USER_MOBILE ="userMobile";
	private static final String USER_CITY_ID="userCityID";
	private static final String USER_FIRST_NAME="userName";
	private static final String USER_LAST_NAME="userlastname";
	private static final String USER_EMAIL="useremail";
	private static final String USER_LOCALITY_ID="userlocality";
	private static final String PROFILE_ID="profileId";
	private static final String SESSION_ID="sessionId";
	private static final String USER_IMAGE_URL="userImageUrl";
	/**000000000000 v
	 * 
	 * Table Name:-
	 */

	public static final String USER_TABLE="userTable";

	public static final String CREATE_USER_TABLE = "create table if not exists " +
			USER_TABLE + "(" + PRIMARY_KEY+" integer primary key," +
			USER_ID +" integer ,"+
			USER_MOBILE +" text ,"+
			USER_CITY_ID +" integer ,"+
			USER_FIRST_NAME +" text ,"+
			USER_LAST_NAME +" text ,"+
			USER_EMAIL +" text ,"+
			PROFILE_ID +" text ,"+
			SESSION_ID +" text ,"+
			USER_IMAGE_URL +" text ,"+
			USER_LOCALITY_ID +" integer  )";

	public static final String DROP_QUERY = "Drop table if exists " + USER_TABLE;

	public UserTable(BaseApplication pApplication) {
		super(pApplication, USER_TABLE);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ContentValues getContentValues(BaseModel pModel) {
		UserResponse userModel =(UserResponse)pModel;
		ContentValues _contentValue=new ContentValues();

		_contentValue.put(USER_ID,userModel.getResult().getData().getUser().getId());
		_contentValue.put(USER_CITY_ID,"");   //getuser.getcityid
		_contentValue.put(USER_EMAIL,userModel.getResult().getData().getUser().getEmail());
		_contentValue.put(USER_FIRST_NAME,userModel.getResult().getData().getUser().getFirst_name());
		_contentValue.put(USER_LAST_NAME,userModel.getResult().getData().getUser().getLast_name());
		_contentValue.put(SESSION_ID,userModel.getResult().getData().getUser().getSessionId());
		_contentValue.put(USER_LOCALITY_ID,""); //getprofile.getlocality
		_contentValue.put(PROFILE_ID,userModel.getResult().getData().getProfile().getId());
		_contentValue.put(USER_MOBILE,"");  //getprofile.getmobileno
		_contentValue.put(USER_IMAGE_URL, userModel.getResult().getData().getProfile().getProfile_image()); // getprofile.fgetprogfileimg
		return _contentValue; 
	}

	@Override
	protected ArrayList<BaseModel> getAllData(String pSelection,String[] pSelectionArgs) {
		ArrayList<BaseModel> _userList=new ArrayList<BaseModel>();
		Cursor _cursor=null;
		try{
			String CREATE_QUERY="select * from "+USER_TABLE;
			_cursor=mWritableDatabase.rawQuery(CREATE_QUERY, null);
			while (_cursor.moveToNext()){
				UserModel _userModel=new UserModel();

				//				_userModel.setId(_cursor.getInt(_cursor.getColumnIndex(USER_ID)));
				//				_userModel.setFirst_name(_cursor.getString(_cursor.getColumnIndex(USER_FIRST_NAME)));
				//				_userModel.getProfile().setLocality_id(_cursor.getString(_cursor.getColumnIndex(USER_LOCALITY_ID)));
				//				_userModel.setLast_name(_cursor.getString(_cursor.getColumnIndex(USER_LAST_NAME)));
				//				_userModel.setMcity_id(_cursor.getInt(_cursor.getColumnIndex(USER_CITY_ID)));
				//				_userModel.setEmail(_cursor.getString(_cursor.getColumnIndex(USER_EMAIL)));
				//				_userModel.setMobile_number(_cursor.getString(_cursor.getColumnIndex(USER_MOBILE)));

				_userList.add(_userModel);
			}
		}
		catch( Exception e ) { Log.e( USER_TABLE, "" + e ); }
		finally { closeCursor(_cursor); }
		return _userList;
	}
	
	
	public int updateUserTable(SaveProfileRequest requestModel){
		try {
			return mWritableDatabase.update(mTableName,getContentValuesSaveProfile(requestModel), USER_ID+"="+requestModel.getUserId(),null);
		} catch (Exception e) {
			Log.e(mTableName, "insertData()", e);
			return -1;
		}
	}
	

	private ContentValues getContentValuesSaveProfile(SaveProfileRequest requestModel) {
		
		ContentValues _contentValue=new ContentValues();

		_contentValue.put(USER_FIRST_NAME,requestModel.getName());
		_contentValue.put(USER_LAST_NAME, "");
		return _contentValue; 
		
		
	}

	public UserModel getAllUserData() {
		UserModel userDataList=new UserModel();
		Cursor _cursor = null;
		try{
			String CREATE_QUERY="select * from "+USER_TABLE; 
			_cursor=mWritableDatabase.rawQuery(CREATE_QUERY, null);
			while (_cursor.moveToNext()){
				UserInfo userInfo=new UserInfo();
				Profile profile =new Profile();
				userInfo.setEmail(_cursor.getString(_cursor.getColumnIndex(USER_EMAIL)));
				userInfo.setFirst_name(_cursor.getString(_cursor.getColumnIndex(USER_FIRST_NAME)));
				userInfo.setLast_name(_cursor.getString(_cursor.getColumnIndex(USER_LAST_NAME)));
				userInfo.setId(_cursor.getString(_cursor.getColumnIndex(USER_ID)));
				userInfo.setMobile_number(_cursor.getString(_cursor.getColumnIndex(USER_MOBILE)));
				userInfo.setSessionId(_cursor.getString(_cursor.getColumnIndex(SESSION_ID)));
				userInfo.setProfileId(_cursor.getString(_cursor.getColumnIndex(PROFILE_ID)));
				profile.setProfile_image(_cursor.getString(_cursor.getColumnIndex(USER_IMAGE_URL)));
				profile.setLocality_id(_cursor.getString(_cursor.getColumnIndex(USER_LOCALITY_ID)));
				//profile.setId(_cursor.getString(_cursor.getColumnIndex(PROFILE_ID)));
				userDataList.setUser(userInfo);
				userDataList.setProfile(profile);
			}
		}
		catch( Exception e ) { Log.e( USER_TABLE, "" + e ); }
		finally { closeCursor(_cursor); }
		return userDataList;
	}



	public int getCount(){

		Cursor _cursor=null;
		int count=0;
		try{
			String CREATE_QUERY="select * from "+USER_TABLE;
			_cursor=mWritableDatabase.rawQuery(CREATE_QUERY, null);
			count=_cursor.getCount();
		}
		catch( Exception e ) { Log.e( USER_TABLE, "" + e ); }
		finally { closeCursor(_cursor); }
		return count;
	}

	public int  getUserId(){
		Cursor _cursor=null;
		int userId=0;
		try {
			String CREATE_QUERY="select "+USER_ID+" from "+USER_TABLE;
			_cursor=mWritableDatabase.rawQuery(CREATE_QUERY, null);
			while(_cursor.moveToNext()){
				userId=_cursor.getInt(_cursor.getColumnIndex(USER_ID));
			}
		}catch( Exception e ) { Log.e( USER_TABLE, "" + e ); }
		finally { closeCursor(_cursor); }
		return userId;
	}
}
