package com.kelltontech.utils;

import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class for date-time related methods.
 */
public class DateTimeUtils {

    public static final long ONE_DAY_MILLIS = 24 * 60 * 60 * 1000;

    /**
     * Constants to be used in
     * {@link DateTimeUtils#getFormattedDate(long, String)} method.
     */
    public interface Format {
        String DD_Mmm_YYYY = "dd MMM yyyy";
        String DD_Mmm_YYYY_Dow = "dd MMM yyyy, E";
        String DayWeek_DD_Mmm_YYYY = "EEEE, dd MMM yyyy";
        String day_of_week_3_chars = "E";
        String DD = "dd";
        String MMM = "MMM";
        String Dow = "E";
        String Mmmm_YYYY = "MMMM yyyy";
        String DD_Mmmm_YYYY_HH_MM = "dd MMM yyy kk:mm";
    }

    /**
     * @param pEpochMillis
     * @param pFormat      Possible values for pFormat are:
     *                     <ul>
     *                     <li>{@link Format#DD_Mmm_YYYY_Dow}</li>
     *                     <li>{@link Format#day_of_week_3_chars}</li>
     *                     </ul>
     * @return date string as per pEpochMillis, formatted as per pFormat.
     */
    public static String getFormattedDate(long pEpochMillis, String pFormat) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(pEpochMillis);
        calendar.add(Calendar.HOUR_OF_DAY, -8);
        return DateFormat.format(pFormat, calendar).toString();
    }

    /**
     * @param pEpochMillis
     * @param pFormat      Possible values for pFormat are:
     *                     <ul>
     *                     <li>{@link Format#DD_Mmm_YYYY_Dow}</li>
     *                     <li>{@link Format#day_of_week_3_chars}</li>
     *                     </ul>
     * @return date string as per calendar, formatted as per pFormat.
     */
    public static String getFormattedDate(Calendar calendar, String pFormat) {
        return DateFormat.format(pFormat, calendar).toString();
    }

    /**
     * @param pDaysOffset
     * @return mid-night epoch millis of start of today+pDaysOffset
     */
    public static long getMidNightMillis(int pDaysOffset) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime() + pDaysOffset * ONE_DAY_MILLIS);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * @param pYear
     * @param pMonthIndex
     * @param pDayOfMonth
     * @return mid-night epoch millis as per parameters
     */
    public static long getMidNightMillis(int pYear, int pMonthIndex, int pDayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, pYear);
        calendar.set(Calendar.MONTH, pMonthIndex);
        calendar.set(Calendar.DAY_OF_MONTH, pDayOfMonth);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * @param pEpochMillis
     * @return int array with year, monthIndex and day-of-month
     */
    public static int[] getDateFields(long pEpochMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(pEpochMillis);
        int[] fields = new int[3];
        fields[0] = calendar.get(Calendar.YEAR);
        fields[1] = calendar.get(Calendar.MONTH);
        fields[2] = calendar.get(Calendar.DAY_OF_MONTH);
        /*
		 * fields[3] = calendar.get(Calendar.HOUR_OF_DAY); fields[4] =
		 * calendar.get(Calendar.MINUTE); fields[5] =
		 * calendar.get(Calendar.SECOND); fields[6] =
		 * calendar.get(Calendar.MILLISECOND); fields[7] =
		 * calendar.get(Calendar.DAY_OF_WEEK);
		 */
        return fields;
    }

    /**
     * Gets epoch millis for a string representing a date in
     * yyyy-MM-ddTHH:mm:ssZ format
     *
     * @param dateStr date in format yyyy-MM-ddTHH:mm:ss Any one-char separator can
     *                be used in place of '-', 'T' and ':'
     */
    public static long parseExtendedDate(String dateStr) {
        /**
         * 2013-01-01 0123456789
         */
        int year = StringUtils.parseInt(dateStr, 0, 4);
        int month = StringUtils.parseInt(dateStr, 5, 7);
        int day = StringUtils.parseInt(dateStr, 8, 10);
        int hour = StringUtils.parseInt(dateStr, 11, 13);
        int minute = StringUtils.parseInt(dateStr, 14, 16);
        int second = StringUtils.parseInt(dateStr, 17, 19);

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1); // Jan is 1 but jan-index is 0
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    public static String changeDateInddMMyyyy(String inputDate) {
        String outputDateStr = null;
        try {
            java.text.DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            java.text.DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.US);
            Date date = inputFormat.parse(inputDate);
            outputDateStr = outputFormat.format(date);

        } catch (Exception e) {
            return null;
        }
        return outputDateStr;
    }

    public static String changeDate(String inputDate) {
        String outputDateStr = null;
        try {
            java.text.DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            java.text.DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
            Date date = inputFormat.parse(inputDate);
            outputDateStr = outputFormat.format(date);

        } catch (Exception e) {
            return null;
        }
        return outputDateStr;
    }

    public static Date stringToDate(String inputDate) {
        Date date = null;
        try {
            java.text.DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            //java.text.DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy",Locale.US);
            date = inputFormat.parse(inputDate);
            // outputDateStr = outputFormat.format(date);

        } catch (Exception e) {
            return null;
        }
        return date;
    }

    public static String getSeperateDate(String inputDate) {
        String outputDateStr = null;
        try {
            java.text.DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            java.text.DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
            Date date = inputFormat.parse(inputDate);
            outputDateStr = outputFormat.format(date);
        } catch (Exception e) {
            return null;
        }
        return outputDateStr;
    }

    public static String getSeperateTime(String inputDate) {
        String outputTimeStr = null;
        try {
            java.text.DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            java.text.DateFormat outputFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
            Date date = inputFormat.parse(inputDate);
            outputTimeStr = outputFormat.format(date);
        } catch (Exception e) {
            return null;
        }
        return outputTimeStr;
    }

    public static String getDateFromTimestamp(long timeStampStr) {

        try {
            java.text.DateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
            Date netDate = new Date(timeStampStr * 1000);
            return sdf.format(netDate);
        } catch (Exception ex) {
            return "xx";
        }
    }
}
