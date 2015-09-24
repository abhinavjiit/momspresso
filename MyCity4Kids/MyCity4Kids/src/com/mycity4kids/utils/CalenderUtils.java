package com.mycity4kids.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by kapil.vij on 17-07-2015.
 */
public class CalenderUtils {

    public static List<String> getSixMonthDatesFromCalender(Calendar mCalendar, boolean isFromNextMonth) {
        List<String> allDays = new ArrayList<String>();
        Calendar calendarStart = (Calendar) mCalendar.clone();
        if (isFromNextMonth) {
            calendarStart.set(Calendar.DAY_OF_MONTH, 1);
            calendarStart.add(Calendar.MONTH, 1);
        }
        Calendar calenderEnd = (Calendar) mCalendar.clone();
        calenderEnd.add(Calendar.DAY_OF_MONTH, 1);
        calenderEnd.add(Calendar.MONTH, 6);

        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        mFormat.setTimeZone(TimeZone.getTimeZone("GMT+0530"));

        while (calenderEnd.getTimeInMillis() >= calendarStart.getTimeInMillis()) {
            // Add day to list
            allDays.add(mFormat.format(calendarStart.getTime()));
            // Move next day
            calendarStart.add(Calendar.DAY_OF_MONTH, 1);
        }

        return allDays;
    }
}
