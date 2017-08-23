package com.mycity4kids.reminders;

import android.util.Log;

import com.kelltontech.utils.StringUtils;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.enums.DayOfWeek;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by kapil.vij on 09-07-2015.
 */
public class ReminderUtils {

    public static long calculateNextReminderTime(int reminderType, long startTimeMillis, String reminderBefore, String repeat, String repeatFrequency, String repeatNum, String repeatUntill) {
        long nextReminderTime = startTimeMillis;
        if (!StringUtils.isNullOrEmpty(reminderBefore)) {
            nextReminderTime = calculateTimeDifference(startTimeMillis, reminderBefore);
        }

        Calendar calendarStartTime = Calendar.getInstance(TimeZone.getTimeZone("GMT+0530"));
        calendarStartTime.setTimeInMillis(startTimeMillis);

        Calendar calenderCurrentTime = Calendar.getInstance(TimeZone.getTimeZone("GMT+0530"));
        calenderCurrentTime.setTimeInMillis(System.currentTimeMillis());
        calenderCurrentTime.set(Calendar.HOUR_OF_DAY, calendarStartTime.get(Calendar.HOUR_OF_DAY));
        calenderCurrentTime.set(Calendar.MINUTE, calendarStartTime.get(Calendar.MINUTE));
        calenderCurrentTime.set(Calendar.SECOND, calendarStartTime.get(Calendar.SECOND));

        if (StringUtils.isNullOrEmpty(repeatUntill)) {
            return 0;
        }

        if (repeat.equalsIgnoreCase("No Repeat")) {
            return 0;
        } else if (repeat.equalsIgnoreCase("Days")) {
            String[] daysArray = repeatFrequency.split(",");
            int dayOfWeek = calendarStartTime.get(Calendar.DAY_OF_WEEK);
            for (int i = 0; i < daysArray.length; i++) {
                String days = daysArray[i];
                if (days.equalsIgnoreCase(DayOfWeek.MONDAY.getValue())) {
                    calenderCurrentTime.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                } else if (days.equalsIgnoreCase(DayOfWeek.TUESDAY.getValue())) {
                    calenderCurrentTime.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
                } else if (days.equalsIgnoreCase(DayOfWeek.WEDNESDAY.getValue())) {
                    calenderCurrentTime.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
                } else if (days.equalsIgnoreCase(DayOfWeek.THURSDAY.getValue())) {
                    calenderCurrentTime.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
                } else if (days.equalsIgnoreCase(DayOfWeek.FRIDAY.getValue())) {
                    calenderCurrentTime.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
                } else if (days.equalsIgnoreCase(DayOfWeek.SATURDAY.getValue())) {
                    calenderCurrentTime.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                } else if (days.equalsIgnoreCase(DayOfWeek.SUNDAY.getValue())) {
                    calenderCurrentTime.add(Calendar.WEEK_OF_MONTH, 1);
                    calenderCurrentTime.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                }
                if (calenderCurrentTime.getTimeInMillis() > System.currentTimeMillis()) {
                    break;
                }
                if (i == daysArray.length - 1) {
                    calenderCurrentTime.add(Calendar.WEEK_OF_MONTH, 1);
                    i = -1;
                }
            }
            Log.e("dayOfWeek", "Date " + calenderCurrentTime.getTime());
            nextReminderTime = calenderCurrentTime.getTimeInMillis();
            // calculate timestamp of next week day depending on todays week day
        } else if (repeat.equalsIgnoreCase("Daily")) {
            // calculate timestamp of tommorrow
            calenderCurrentTime.add(Calendar.DAY_OF_YEAR, 1);
            nextReminderTime = calenderCurrentTime.getTimeInMillis();
        } else if (repeat.equalsIgnoreCase("Weekly")) {
            // calculate timestamp of next week from current
            calenderCurrentTime.set(Calendar.DAY_OF_WEEK, calendarStartTime.get(Calendar.DAY_OF_WEEK));
            calenderCurrentTime.add(Calendar.WEEK_OF_YEAR, 1);
            nextReminderTime = calenderCurrentTime.getTimeInMillis();
        } else if (repeat.equalsIgnoreCase("Monthly")) {
            // calculate timestamp of next month from current
            calenderCurrentTime.set(Calendar.DAY_OF_MONTH, calendarStartTime.get(Calendar.DAY_OF_MONTH));
            calenderCurrentTime.add(Calendar.MONTH, 1);
            nextReminderTime = calenderCurrentTime.getTimeInMillis();
        } else if (repeat.equalsIgnoreCase("Yearly")) {
            if (reminderType == Constants.REMINDER_KIDS_BIRTHDAY) {
                // calculate timestamp of the next birthday.
                if (calendarStartTime.get(Calendar.MONTH) < calenderCurrentTime.get(Calendar.MONTH)) {
                    calendarStartTime.set(Calendar.YEAR, calenderCurrentTime.get(Calendar.YEAR));
                    calendarStartTime.add(Calendar.YEAR, 1);
                } else if (calendarStartTime.get(Calendar.MONTH) == calenderCurrentTime.get(Calendar.MONTH)) {
                    if (calendarStartTime.get(Calendar.DAY_OF_MONTH) <= calenderCurrentTime.get(Calendar.DAY_OF_MONTH)) {
                        calendarStartTime.set(Calendar.YEAR, calenderCurrentTime.get(Calendar.YEAR));
                        calendarStartTime.add(Calendar.YEAR, 1);
                    } else {
                        calendarStartTime.set(Calendar.YEAR, calenderCurrentTime.get(Calendar.YEAR));
                    }
                } else {
                    calendarStartTime.set(Calendar.YEAR, calenderCurrentTime.get(Calendar.YEAR));
                }
            } else {
                // calculate timestamp of next year from start
                calendarStartTime.set(Calendar.YEAR, calenderCurrentTime.get(Calendar.YEAR));
                calendarStartTime.add(Calendar.YEAR, 1);
            }
            nextReminderTime = calendarStartTime.getTimeInMillis();
        } else if (repeat.equalsIgnoreCase("Other")) {
            int repeatCount = Integer.parseInt(repeatNum);
            do {
                if (repeatFrequency.equalsIgnoreCase("months")) {
                    calendarStartTime.set(Calendar.MONTH, repeatCount);
                } else if (repeatFrequency.equalsIgnoreCase("weeks")) {
                    calendarStartTime.set(Calendar.WEEK_OF_YEAR, repeatCount);
                } else if (repeatFrequency.equalsIgnoreCase("days")) {
                    calendarStartTime.set(Calendar.DAY_OF_MONTH, repeatCount);
                }
            } while (calendarStartTime.getTimeInMillis() < calenderCurrentTime.getTimeInMillis());
            nextReminderTime = calendarStartTime.getTimeInMillis();
        }

        if (!repeatUntill.equalsIgnoreCase("forever")) {
            try {

                Calendar startTime = Calendar.getInstance(TimeZone.getTimeZone("GMT+0530"));
                startTime.setTimeInMillis(startTimeMillis);

                Calendar currentTime = Calendar.getInstance(TimeZone.getTimeZone("GMT+0530"));
                currentTime.setTimeInMillis(System.currentTimeMillis());
                currentTime.set(Calendar.HOUR_OF_DAY, startTime.get(Calendar.HOUR_OF_DAY));
                currentTime.set(Calendar.MINUTE, startTime.get(Calendar.MINUTE));
                currentTime.set(Calendar.SECOND, startTime.get(Calendar.SECOND));

                SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
                Date date = formatter.parse(repeatUntill);
                Calendar calenderRepeatUntill = Calendar.getInstance(TimeZone.getTimeZone("GMT+0530"));
                calenderRepeatUntill.setTime(date);
                calenderRepeatUntill.set(Calendar.HOUR_OF_DAY, startTime.get(Calendar.HOUR_OF_DAY));
                calenderRepeatUntill.set(Calendar.MINUTE, startTime.get(Calendar.MINUTE));
                calenderRepeatUntill.set(Calendar.SECOND, startTime.get(Calendar.SECOND));

                if (!(calenderRepeatUntill.getTimeInMillis() > currentTime.getTimeInMillis() && calenderRepeatUntill.getTimeInMillis() > nextReminderTime)) {
                    return 0;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return nextReminderTime;
    }

    public static long calculateTimeDifference(long startTimeMillis, String reminderBeforeMins) {
        return startTimeMillis - (Integer.parseInt(reminderBeforeMins) * 60 * 1000);
    }

    private static long getDateDiff(String date1, String date2) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
        Date dateFirst = null;
        Date dateSecond = null;
        try {
            dateFirst = sdf.parse(date1);
            dateSecond = sdf.parse(date2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        TimeUnit timeUnit = TimeUnit.DAYS;
        long diffInMillies = dateSecond.getTime() - dateFirst.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    private static String getFormattedDate(String currentFormat, String requiredFormat, String curDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(currentFormat, Locale.US);
            SimpleDateFormat requiredSdf = new SimpleDateFormat(requiredFormat, Locale.US);
            Date date = sdf.parse(curDate);
            return requiredSdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return curDate;
        }
    }

}
