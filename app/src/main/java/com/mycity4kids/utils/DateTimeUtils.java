package com.mycity4kids.utils;

import android.text.format.DateUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for date-time related methods.
 */
public class DateTimeUtils {

    public static String getDateFromTimestamp(long timeStampStr) {
        try {
            java.text.DateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
            Date netDate = new Date(timeStampStr * 1000);
            return sdf.format(netDate);
        } catch (Exception ex) {
            return "xx";
        }
    }

    public static String getDateTimeFromTimestamp(long timeStampStr) {
        try {
            java.text.DateFormat sdf = new SimpleDateFormat("hh:mm a | MMM dd, yyyy");
            Date netDate;
            if (timeStampStr > 1000000000000l) {
                netDate = new Date(timeStampStr);
            } else {
                netDate = new Date(timeStampStr * 1000);
            }
            return sdf.format(netDate);
        } catch (Exception ex) {
            return "xx";
        }
    }

    public static String getDateFromNanoMilliTimestamp(long timeStampStr) {
        try {
            java.text.DateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
            Date netDate;
            if (timeStampStr > 1000000000000l) {
                netDate = new Date(timeStampStr);
            } else {
                netDate = new Date(timeStampStr * 1000);
            }
            return sdf.format(netDate);
        } catch (Exception ex) {
            return "xx";
        }
    }

    public static String getFormattedDateGroups(long timeStampStr) {
        try {
            java.text.DateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
            Date netDate;
            if (timeStampStr > 1000000000000l) {
                netDate = new Date(timeStampStr);
            } else {
                netDate = new Date(timeStampStr * 1000);
            }

            String ss = "" + DateUtils.getRelativeTimeSpanString(netDate.getTime(),
                    System.currentTimeMillis(),
                    DateUtils.SECOND_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_RELATIVE);
            return ss;
        } catch (Exception ex) {
            return "xx";
        }
    }

    public static String getKidsDOBNanoMilliTimestamp(String timeStampStr) {
        try {
            Long dobTime = Long.parseLong(timeStampStr);
            java.text.DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date netDate;
            if (dobTime > 1000000000000l) {
                netDate = new Date(dobTime);
            } else {
                netDate = new Date(dobTime * 1000);
            }
            return sdf.format(netDate);
        } catch (Exception ex) {
            return "xx";
        }
    }

    public static String getDOBMilliTimestamp(String timeStampStr) {
        try {
            Long dobTime = Long.parseLong(timeStampStr);
            java.text.DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date netDate = new Date(dobTime);
            return sdf.format(netDate);
        } catch (Exception ex) {
            return "xx";
        }
    }

        public static long convertStringToTimestamp(String str_date) {
        try {
            java.text.DateFormat formatter;
            formatter = new SimpleDateFormat("dd-MM-yyyy");
            // you can change format of date
            Date date = formatter.parse(str_date);
            return date.getTime() / 1000;
        } catch (ParseException e) {
            System.out.println("Exception :" + e);
            return 0;
        }
    }

    public static long convertStringToMilliTimestamp(String str_date) {
        try {
            java.text.DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            // you can change format of date
            Date date = formatter.parse(str_date);
            return date.getTime();
        } catch (Exception e) {
            System.out.println("Exception :" + e);
            return 0;
        }
    }

    public static String getMMMDDFormatDate(String timeStampStr) {

        try {
            long tStamp = Long.parseLong(timeStampStr);
            java.text.DateFormat sdf = new SimpleDateFormat("MMM dd");
            Date netDate = new Date(tStamp * 1000);
            return sdf.format(netDate);
        } catch (Exception ex) {
            return "xx";
        }
    }
}
