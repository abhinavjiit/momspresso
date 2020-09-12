package com.mycity4kids.utils;

import android.text.format.DateUtils;
import android.util.Log;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.jetbrains.annotations.Nullable;

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

    public static String getDateTimeFromTimestampForLives(long timeStampStr) {
        try {
            java.text.DateFormat sdf = new SimpleDateFormat("dd MMM yy, hh:mm a");
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

    public static String timeSince(long date) {

        double seconds = Math.floor((new Date().getTime() - date * 1000) / 1000);

        double interval = seconds / 31536000;

        if (interval > 1) {
            return (int) Math.floor(interval) + " years";
        }
        interval = seconds / 2592000;
        if (interval > 1) {
            return (int) Math.floor(interval) + " months";
        }
        interval = seconds / 86400;
        if (interval > 1) {
            return (int) Math.floor(interval) + " days";
        }
        interval = seconds / 3600;
        if (interval > 1) {
            return (int) Math.floor(interval) + " hours";
        }
        interval = seconds / 60;
        if (interval > 1) {
            return (int) Math.floor(interval) + " minutes";
        }
        return (int) Math.floor(seconds) + " seconds";
    }

    public static String timeDiffInMinuteAndSeconds(long liveDatetime) {
        // Get msec from each, and subtract.
        long diff = liveDatetime * 1000 - new Date().getTime();
        if (diff < 0) {
            return "";
        }
        Log.e("dwada", "ndwndkwdkwndkndknwkdnwkdn ----- " + diff);
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000);
        return diffHours + ":" + diffMinutes;
    }
}
