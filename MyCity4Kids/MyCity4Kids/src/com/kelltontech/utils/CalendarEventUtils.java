package com.kelltontech.utils;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

/**
 * <!-- permissions for calendar events -->
 * <uses-permission android:name="android.permission.READ_CALENDAR" />
 * <uses-permission android:name="android.permission.WRITE_CALENDAR" />
 * 
 * Android-14 has added CalendarContract.Events like classes for column-names. 
 */
public class CalendarEventUtils {
	
	private static final String LOG_TAG = "CalendarEventUtils"; 
	
	/**
	 * This method can be used to fetch id and names of calendars, that are configured in device.
	 * List of calendar-names can be provided to user for selecting a calendar to add event.
	 * @param pContext
	 */
	public static Calendars getCalendars( Context pContext ) {
		Calendars calendars = new Calendars();
		Cursor cursor = null;
		try {
			ContentResolver cr = pContext.getContentResolver();
			cursor = cr.query( getCalendarsUri(), new String[]{ "_id", "displayname" }, null, null, null);
	
			if ( cursor.moveToFirst() ) {
				int numCalendars = cursor.getCount();
				calendars.calendar_ids	= new long[numCalendars];
				calendars.display_names	= new String[numCalendars];
	
				for (int i = 0; i < numCalendars; i++) {
					calendars.calendar_ids[i] = cursor.getInt(0);
					calendars.display_names[i] = cursor.getString(1);
					cursor.moveToNext();
				}
			}
		} catch(Exception e) {
			Log.e( LOG_TAG, "getCalendars()", e );
		} finally {
			if( cursor != null ) {
				cursor.close();
			}
		}
		return calendars;
	}
	
	/**
	 * Adds the event to calendar.
	 * @param pContext
	 * @param pEvent 
	 */
	public static long addEvent( Context pContext, Event pEvent ) {
		try {
			
			if( pEvent.calendar_id == -1 ) {
				Calendars allCalendars = getCalendars(pContext);
				pEvent.calendar_id = allCalendars.calendar_ids[0];
			}
			
			ContentValues cv = new ContentValues();
			
			cv.put("calendar_id", 	pEvent.calendar_id );
			cv.put("title", 		pEvent.title );
			cv.put("description", 	pEvent.description );
			cv.put("eventLocation", pEvent.eventLocation );
			
			cv.put("dtstart", 		pEvent.dtstart );
			cv.put("dtend", 		pEvent.dtend );
			cv.put("eventTimezone", pEvent.eventTimezone );
			
			cv.put("hasAlarm", 		pEvent.hasAlarm );
			cv.put("visibility", 	pEvent.visibility );
			cv.put("allDay", 		pEvent.allDay );
			
			cv.put("eventStatus", 	pEvent.eventStatus );
			cv.put("transparency", 	pEvent.transparency );
	
			Uri eventUri = pContext.getContentResolver().insert( getEventsUri(), cv);
			
	    	return Long.parseLong(eventUri.getLastPathSegment());
		} catch(Exception e) {
			Log.e( LOG_TAG, "addEvent()", e );
			return -1;
		}
	}
	
	/**
	 * Adds the pReminder to calendar.
	 * @param pContext
	 * @param pReminder 
	 */
	public static long addReminder( Context pContext, Reminder pReminder ) {
		try {
			ContentValues values = new ContentValues();
			values.put( "event_id", pReminder.event_id );
			values.put( "method", 	pReminder.method );
			values.put( "minutes", 	pReminder.minutes ); 
			
			ContentResolver cr = pContext.getContentResolver();
			Uri reminderUri = cr.insert( getRemindersUri(), values );
			
			return Long.parseLong(reminderUri.getLastPathSegment());
		} catch(Exception e) {
			Log.e( LOG_TAG, "addReminder()", e );
			return -1;
		}
	}
	
	
	/**
	 * Delete event(s) from the calendar.
	 * 1. If event_id is set, then the event with this event_id will be removed. There can be a single event with given event_id. 
	 * 2. If calendar_id is >0 in pEvent, then all events added in this calendar_id will be removed.
	 *    If calendar_id is 0 in pEvent, then all events in all calendars will be removed.
	 *    If calendar_id is -1 in pEvent, then all events added in default calendar will be removed. 
	 * 3. If title is set in pEvent, then all events with exactly same title will be removed.
	 * 4. If any-one or more of these 3 values are set, then these will not be ignored.
	 * @param pContext
	 * @param pEvent 
	 */
	public static int removeEvent( Context pContext, Event pEvent )
	{
		int numRemoved = 0;
		try {
			
			if( pEvent.calendar_id == -1 ) {
				Calendars allCalendars = getCalendars(pContext);
				pEvent.calendar_id = allCalendars.calendar_ids[0];
			}
			
			String selection = "";
			ArrayList<String> argsList = new ArrayList<String>();

			if( pEvent.calendar_id > 0 ) {  
				selection += "calendar_id" + " = ?";
				argsList.add ( pEvent.calendar_id + "" );
			}
			
			if( pEvent.event_id > 0 ) {
				if( ! selection.equals("") ) selection += " and ";
				selection += "_id" + " = ?";
				argsList.add ( pEvent.event_id + "" );
			}
			
			if( pEvent.title != null && pEvent.title.trim().length() > 0 ) {
				if( ! selection.equals("") ) selection += " and ";
				selection += "title" + " = ?";
				argsList.add ( pEvent.title.trim() );
			}
			
			String[] selectionArgs = new String[argsList.size()];
			argsList.toArray(selectionArgs);
			
			ContentResolver cr = pContext.getContentResolver();
			numRemoved = cr.delete( getEventsUri(), selection, selectionArgs );
		} catch (Exception e) { 
			Log.e( LOG_TAG, "removeEvent()", e );
		}
		return numRemoved;
	}
	
	
	//////////////////////////////////////// private methods to get different content Uris
	
	/**
	 * @return calendar's Uri as per Build.VERSION.SDK
	 */
	private static Uri getCalendarsUri() {
		if( Integer.parseInt(Build.VERSION.SDK) >= 8 )
            return Uri.parse("content://com.android.calendar/calendars");
        else
        	return Uri.parse("content://calendar/calendars");
	}
	
	/**
	 * @return event's Uri as per Build.VERSION.SDK
	 */
	private static Uri getEventsUri() {
		if( Integer.parseInt(Build.VERSION.SDK) >= 8 )
            return Uri.parse("content://com.android.calendar/events");
        else
        	return Uri.parse("content://calendar/events");
	}
	
	/**
	 * @return reminder's Uri as per Build.VERSION.SDK
	 */
	private static Uri getRemindersUri() {
		if( Integer.parseInt(Build.VERSION.SDK) >= 8 )
	        return Uri.parse( "content://com.android.calendar/reminders" );
	    else
	    	return Uri.parse( "content://calendar/reminders" );
	}
	
	///////////////////////////////////////// simple data-holding classes
	
	/**
	 * Class to return data from {@link CalendarEventUtils#getCalendars(Context)}
	 */
	public static class Calendars { 
		public long[] 	calendar_ids;
		public String[]	display_names;
	}

	/**
	 * Class to pass event data to {@link CalendarEventUtils#addReminder(Context, Event)}
	 */
	public static class Event {
		
		/**
		 * Use {@link CalendarEventUtils#getCalendars(Context)} for all calendars.
		 * Default value -1 will add event to device's default calendar, usually "My Calendar".
		 */
		public long calendar_id = -1;
		
		/**
		 * To remove events by {@link CalendarEventUtils#removeEvent(Context, Event)}.
		 */
		public long	event_id = 0;
		
		public String 	title 			= "";
		public String 	eventLocation 	= "";
		public String 	description 	= "";
		
		public long 	dtstart 		= 0;
		public long 	dtend	 		= 0;
		public String 	eventTimezone 	= "";
		
		/**
		 * default (0), confidential (1), private (2), public (3)
		 */
		public int visibility = 0;
		
		/**
		 * false(0), true(1)
		 */ 
		public int hasAlarm = 0;
		
		/**
		 * false(0), true(1)
		 */
		public int allDay = 0;
		
		public int eventStatus 	= 0;
		public int transparency = 0;
	}
	
	/**
	 * Class to pass Reminder data to {@link CalendarEventUtils#addEvent(Context, Event)}
	 */
	public static class Reminder {
		public long	event_id	= 0;
		public int 	method		= 0;
		public int 	minutes		= 0;
	}
}