package com.mycity4kids.reminders;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.kelltontech.utils.StringUtils;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.dbtable.TableAdult;
import com.mycity4kids.dbtable.TableKids;
import com.mycity4kids.dbtable.TableNotes;
import com.mycity4kids.models.user.KidsInfo;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.newmodels.AppointmentMappingModel;
import com.mycity4kids.newmodels.AppoitmentDataModel;
import com.mycity4kids.newmodels.AttendeeModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

/**
 * Created by kapil.vij on 07-08-2015.
 */
public class AppointmentManager {


    public static final String LOCAL_BROADCAST_APPOINTMENT_UPDATED = "local_broadcast_appointment_updated";
    private static AppointmentManager mAppointmentManager;
    private static Context mContext;
    // haspmap of dates mapped with appointments
    private LinkedHashMap<String, ArrayList<AppointmentMappingModel>> mOriginalList;
    private ArrayList<String> mDatesData;

    public AppointmentManager(Context context) {
        mContext = context;
    }

    public synchronized static AppointmentManager getInstance(Context context) {
        if (mAppointmentManager == null) {
            mAppointmentManager = new AppointmentManager(context);
        }
        return mAppointmentManager;
    }

    public void setAppointmentMap(LinkedHashMap<String, ArrayList<AppointmentMappingModel>> pOriginalList) {
        //if (pOriginalList != null && pOriginalList.entrySet().size() > 0)
        try {
            mOriginalList = (LinkedHashMap<String, ArrayList<AppointmentMappingModel>>) pOriginalList.clone();
        } catch (Exception e) {
            // mOriginalList.clear();
            e.printStackTrace();
        }

    }


    public void clearList() {
        try {
            mOriginalList = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void addAppointmentdata(LinkedHashMap<String, ArrayList<AppointmentMappingModel>> pOriginalList) {
        if (pOriginalList != null && pOriginalList.entrySet().size() > 0) {
            if (mOriginalList != null)
                mOriginalList.putAll(pOriginalList);
        }

    }

    public LinkedHashMap<String, ArrayList<AppointmentMappingModel>> getAppointmentMap() {
        return mOriginalList;
    }

    public void updateAppointmentMapById(AppointmentMappingModel appointmentMappingModel, boolean isEdit) {
        // remove existing mapping from
        if (isEdit) {
            // remove all mappings if it is not an social mdeia event
            removeAppointmentDataFromMap(appointmentMappingModel, false);
        }
        // add new mappings of appointment with dates
        new UpdateDataTask().execute(appointmentMappingModel);

    }

    public void removeAppointmentDataFromMap(AppointmentMappingModel appointmentMappingModel, boolean isBroadcastRequired) {
        int eventId = appointmentMappingModel.getEventId();
       // if (eventId != 0) {
            Iterator<Map.Entry<String, ArrayList<AppointmentMappingModel>>> iter = mOriginalList.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, ArrayList<AppointmentMappingModel>> entry = iter.next();
                if (entry.getValue().contains(appointmentMappingModel)) {
                    entry.getValue().remove(appointmentMappingModel);
                    if (entry.getValue().size() == 0) {
                        iter.remove();
                    }
                }
            }
        if (isBroadcastRequired) {
            Intent intent = new Intent(LOCAL_BROADCAST_APPOINTMENT_UPDATED);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        }
    }

    public class UpdateDataTask extends AsyncTask<AppointmentMappingModel, Void, Void> {

        @Override
        protected Void doInBackground(AppointmentMappingModel... appointmentMappingModel) {
            try {
                updateData(appointmentMappingModel[0]);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent intent = new Intent(LOCAL_BROADCAST_APPOINTMENT_UPDATED);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        }
    }

    public void updateData(AppointmentMappingModel appointmentMappingModel) throws ParseException {

        Set<AppointmentMappingModel> tempAppointmentModels;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = 0; i < mDatesData.size(); i++) {

            try {

                tempAppointmentModels = new HashSet<AppointmentMappingModel>();
                String isRecurring = appointmentMappingModel.getIs_recurring();

                if (checkCurrentDateValid(mDatesData.get(i), appointmentMappingModel.getStarttime())) {


                    java.sql.Timestamp tempTimestamp = new java.sql.Timestamp(formatter.parse(mDatesData.get(i)).getTime());
                    if (appointmentMappingModel.getStarttime() >= tempTimestamp.getTime() && appointmentMappingModel.getStarttime() <= (tempTimestamp.getTime() + 86280000)) {
                        tempAppointmentModels.add(appointmentMappingModel);

                    } else {
                        // condition until check pick date

                        if (!StringUtils.isNullOrEmpty(appointmentMappingModel.getRepeate_untill())) {
                            if (appointmentMappingModel.getRepeate_untill().equalsIgnoreCase("forever")) {
                                if (appointmentMappingModel.getRepeat().equalsIgnoreCase("No Repeat")) {
                                    // no repeat means its non recurring, break loop
//                                    break;
                                } else if (appointmentMappingModel.getRepeat().equalsIgnoreCase("Days")) {

                                    String[] daysArray = appointmentMappingModel.getRepeate_frequency().split(",");
                                    for (String day : daysArray) {
                                        boolean result = chkDays(day, mDatesData.get(i));
                                        if (result)
                                            tempAppointmentModels.add(appointmentMappingModel);
                                    }


                                } else if (appointmentMappingModel.getRepeat().equalsIgnoreCase("Daily")) {
                                    tempAppointmentModels.add(appointmentMappingModel);
                                } else if (appointmentMappingModel.getRepeat().equalsIgnoreCase("Weekly")) {
                                    // check start date
                                    long starttime = appointmentMappingModel.getStarttime();
                                    boolean result = getValues(starttime, "weekly", mDatesData.get(i));
                                    if (result)
                                        tempAppointmentModels.add(appointmentMappingModel);

                                } else if (appointmentMappingModel.getRepeat().equalsIgnoreCase("Monthly")) {

                                    long starttime = appointmentMappingModel.getStarttime();
                                    boolean result = getValues(starttime, "monthly", mDatesData.get(i));
                                    if (result)
                                        tempAppointmentModels.add(appointmentMappingModel);

                                } else if (appointmentMappingModel.getRepeat().equalsIgnoreCase("Yearly")) {

                                    long starttime = appointmentMappingModel.getStarttime();
                                    boolean result = getValues(starttime, "yearly", mDatesData.get(i));
                                    if (result)
                                        tempAppointmentModels.add(appointmentMappingModel);

                                } else if (appointmentMappingModel.getRepeat().equalsIgnoreCase("Other")) {


                                    long starttime = appointmentMappingModel.getStarttime();
                                    boolean result = getOtherValues(starttime, appointmentMappingModel.getRepeate_frequency(), mDatesData.get(i), Integer.parseInt(appointmentMappingModel.getRepeate_num()));
                                    if (result)
                                        tempAppointmentModels.add(appointmentMappingModel);
                                }

                            } else {
                                String pickdate = appointmentMappingModel.getRepeate_untill();
                                String currentdate = mDatesData.get(i);

                                SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd");
                                SimpleDateFormat f2 = new SimpleDateFormat("dd MMMM yyyy");

                                Date dateCurrent = new Date();
                                dateCurrent = (Date) f1.parse(currentdate);

                                Date dateUntil = (Date) f2.parse(pickdate);
                                String untilFinal = f1.format(dateUntil);

                                Calendar calendarCurrent = Calendar.getInstance(TimeZone.getTimeZone("GMT+0530"));
                                calendarCurrent.clear();
                                calendarCurrent.setTime(dateCurrent);
                                calendarCurrent.set(Calendar.HOUR_OF_DAY, 23);
                                calendarCurrent.set(Calendar.MINUTE, 58);

                                Calendar calendarUntil = Calendar.getInstance(TimeZone.getTimeZone("GMT+0530"));
                                calendarUntil.clear();
                                calendarUntil.setTime(dateUntil);
                                calendarUntil.set(Calendar.HOUR_OF_DAY, 23);
                                calendarUntil.set(Calendar.MINUTE, 58);

                                if (calendarCurrent.getTimeInMillis() <= calendarUntil.getTimeInMillis()) {

                                    if (appointmentMappingModel.getRepeat().equalsIgnoreCase("No Repeat")) {
                                        // no repeat means its non recurring, break loop
//                                        break;
                                    } else if (appointmentMappingModel.getRepeat().equalsIgnoreCase("Days")) {

                                        String[] daysArray = appointmentMappingModel.getRepeate_frequency().split(",");
                                        for (String day : daysArray) {
                                            boolean result = chkDays(day, mDatesData.get(i));
                                            if (result)
                                                tempAppointmentModels.add(appointmentMappingModel);
                                        }

                                    } else if (appointmentMappingModel.getRepeat().equalsIgnoreCase("Daily")) {
                                        tempAppointmentModels.add(appointmentMappingModel);
                                    } else if (appointmentMappingModel.getRepeat().equalsIgnoreCase("Weekly")) {
                                        // check start date
                                        long starttime = appointmentMappingModel.getStarttime();
                                        boolean result = getValues(starttime, "weekly", mDatesData.get(i));
                                        if (result)
                                            tempAppointmentModels.add(appointmentMappingModel);

                                    } else if (appointmentMappingModel.getRepeat().equalsIgnoreCase("Monthly")) {

                                        long starttime = appointmentMappingModel.getStarttime();
                                        boolean result = getValues(starttime, "monthly", mDatesData.get(i));
                                        if (result)
                                            tempAppointmentModels.add(appointmentMappingModel);

                                    } else if (appointmentMappingModel.getRepeat().equalsIgnoreCase("Yearly")) {

                                        long starttime = appointmentMappingModel.getStarttime();
                                        boolean result = getValues(starttime, "yearly", mDatesData.get(i));
                                        if (result)
                                            tempAppointmentModels.add(appointmentMappingModel);

                                    } else if (appointmentMappingModel.getRepeat().equalsIgnoreCase("Other")) {


                                        long starttime = appointmentMappingModel.getStarttime();
                                        boolean result = getOtherValues(starttime, appointmentMappingModel.getRepeate_frequency(), mDatesData.get(i), Integer.parseInt(appointmentMappingModel.getRepeate_num()));
                                        if (result)
                                            tempAppointmentModels.add(appointmentMappingModel);
                                    }

                                }
                                if (calendarCurrent.getTimeInMillis() == calendarUntil.getTimeInMillis()) {
                                    // if we reached the end date of recurring event, then break the loop
//                                    break;
                                }
                            }
                        } else {
                            // if its non recurring, break the loop
//                            break;
                        }

                    }
                }
                if (tempAppointmentModels.size() > 0) {
                    ArrayList<AppointmentMappingModel> previousAppointments = null;
                    if (mOriginalList != null)
                        previousAppointments = mOriginalList.get(mDatesData.get(i));
                    if (previousAppointments == null) {
                        mOriginalList.put(mDatesData.get(i), new ArrayList<AppointmentMappingModel>(tempAppointmentModels));

                    } else {
                        // add according to timestamp
                        tempAppointmentModels.addAll(previousAppointments);
                        ArrayList<AppointmentMappingModel> finalAppointmentModelsByTimestamp = getSorted(mDatesData.get(i), new ArrayList<AppointmentMappingModel>(tempAppointmentModels));
                        mOriginalList.put(mDatesData.get(i), finalAppointmentModelsByTimestamp);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        // do sorting of dates
        doDateSorting();

    }


    public void doDateSorting() {
        TreeMap<String, ArrayList<AppointmentMappingModel>> sortedValues = new TreeMap<>(mOriginalList);
        mOriginalList.clear();
        mOriginalList.putAll(sortedValues);
    }

    private String getTime(long timeStampStr) {

        try {
            DateFormat sdf = new SimpleDateFormat("h:mma");
            Date netDate = (new Date(timeStampStr));
            return sdf.format(netDate).toUpperCase();
        } catch (Exception ex) {
            return "xx";
        }
    }

    public long convertTimeStamp(CharSequence date, CharSequence time) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd h:mma");

        String temp = date + " " + time;
        Date tempDate = formatter.parse(temp);

        java.sql.Timestamp timestamp = new java.sql.Timestamp(tempDate.getTime());

        Log.d("TimeStamp", String.valueOf(timestamp.getTime()));

        long time_stamp = timestamp.getTime();
        return time_stamp;
    }

    public ArrayList<AppointmentMappingModel> getSorted(String date, ArrayList<AppointmentMappingModel> dataList) {


        for (int i = 0; i < dataList.size(); i++) {
            try {
                dataList.get(i).setTemptime(convertTimeStamp(date, getTime(dataList.get(i).getStarttime())));
            } catch (Exception e) {
                e.getMessage();
            }

        }

        // now sorted by timeastamp
        AppointmentMappingModel swapModel;
        for (int i = 0; i < dataList.size(); i++) {
            for (int j = i + 1; j < dataList.size(); j++) {
                if (dataList.get(i).getTemptime() > dataList.get(j).getTemptime()) {
                    swapModel = dataList.get(i);
                    dataList.set(i, dataList.get(j));
                    dataList.set(j, swapModel);
                }

            }
        }

        return dataList;

    }

    public void addDatesDate(List<String> allDates) {
        if (mDatesData == null) {
            mDatesData = new ArrayList<String>();
        }
        mDatesData.addAll(allDates);
    }

    public boolean checkCurrentDateValid(String currentdate, long startdate) {
        boolean result = false;

        SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd");

        String taskstartdate = f1.format(startdate);

        Date dateCurrent = new Date();
        Date dateStart = new Date();
        try {

            dateCurrent = (Date) f1.parse(currentdate);
            dateStart = (Date) f1.parse(taskstartdate);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Calendar calendarCurrent = Calendar.getInstance(TimeZone.getTimeZone("GMT+0530"));
        calendarCurrent.clear();
        calendarCurrent.setTime(dateCurrent);
        calendarCurrent.set(Calendar.HOUR_OF_DAY, 23);
        calendarCurrent.set(Calendar.MINUTE, 58);

        Calendar calendarStart = Calendar.getInstance(TimeZone.getTimeZone("GMT+0530"));
        calendarStart.clear();
        calendarStart.setTime(dateStart);
        calendarStart.set(Calendar.HOUR_OF_DAY, 23);
        calendarStart.set(Calendar.MINUTE, 58);

        if (calendarCurrent.getTimeInMillis() >= calendarStart.getTimeInMillis()) {
            return true;
        }

        return result;
    }

    public boolean chkDays(String appointmentday, String cureentdate) throws ParseException {
        boolean result = false;

        appointmentday = appointmentday.replace(" ", "");

        SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd");

        Date date = new Date();
        date = (Date) f1.parse(cureentdate);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        String dayOfTheWeek = sdf.format(date);

        if (dayOfTheWeek.equalsIgnoreCase(appointmentday))
            result = true;

        return result;
    }

    public boolean getValues(long apointmenttime, String repeat, String cureentdate) throws ParseException {

        boolean result = false;

        SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd");

        String appointmentDate = f1.format(apointmenttime);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(apointmenttime);

        Date date = new Date();
        date = (Date) f1.parse(cureentdate);

        Calendar calendar1 = Calendar.getInstance(TimeZone.getTimeZone("GMT+0530"));
        calendar1.clear();
        calendar1.setTime(date);
        calendar1.set(Calendar.HOUR_OF_DAY, 23);
        calendar1.set(Calendar.MINUTE, 58);


        if (repeat.equalsIgnoreCase("monthly")) {
            while (calendar.getTimeInMillis() <= calendar1.getTimeInMillis()) {
                if (f1.format(calendar.getTime()).equals(cureentdate)) {

                    result = true;
                    break;


                }
                calendar.add(Calendar.MONTH, 1);
            }

        } else if (repeat.equalsIgnoreCase("yearly")) {
            while (calendar.getTimeInMillis() <= calendar1.getTimeInMillis()) {
                if (f1.format(calendar.getTime()).equals(cureentdate)) {

                    result = true;
                    break;


                }
                calendar.add(Calendar.YEAR, 1);
            }

        } else if (repeat.equalsIgnoreCase("weekly")) {
            while (calendar.getTimeInMillis() <= calendar1.getTimeInMillis()) {
                if (f1.format(calendar.getTime()).equals(cureentdate)) {

                    result = true;
                    break;


                }
                calendar.add(Calendar.DAY_OF_MONTH, 7);
            }

        }


        return result;
    }

    public boolean getOtherValues(long apointmenttime, String repeat, String cureentdate, int count) throws ParseException {

        boolean result = false;

        SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd");

        String appointmentDate = f1.format(apointmenttime);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(apointmenttime);

        Date date = new Date();
        date = (Date) f1.parse(cureentdate);

        Calendar calendar1 = Calendar.getInstance(TimeZone.getTimeZone("GMT+0530"));
        calendar1.clear();
        calendar1.setTime(date);
        calendar1.set(Calendar.HOUR_OF_DAY, 23);
        calendar1.set(Calendar.MINUTE, 58);


        if (repeat.equalsIgnoreCase("months")) {
            while (calendar.getTimeInMillis() <= calendar1.getTimeInMillis()) {
                if (f1.format(calendar.getTime()).equals(cureentdate)) {

                    result = true;
                    break;


                }
                calendar.add(Calendar.MONTH, count);
            }

        } else if (repeat.equalsIgnoreCase("weeks")) {
            while (calendar.getTimeInMillis() <= calendar1.getTimeInMillis()) {
                if (f1.format(calendar.getTime()).equals(cureentdate)) {

                    result = true;
                    break;


                }
                calendar.add(Calendar.WEEK_OF_YEAR, count);
            }

        } else if (repeat.equalsIgnoreCase("days")) {
            while (calendar.getTimeInMillis() <= calendar1.getTimeInMillis()) {
                if (f1.format(calendar.getTime()).equals(cureentdate)) {

                    result = true;
                    break;


                }
                calendar.add(Calendar.DAY_OF_MONTH, count);
            }

        }


        return result;
    }


    // convert appointment model server to appointment mapping model local
    public void createAppointmentMappingModelAndUpdate(AppoitmentDataModel.AppointmentData appointmentData, boolean isEdit) {

        TableNotes notesTable = new TableNotes(BaseApplication.getInstance());

        AppointmentMappingModel appointmentMappingModel = new AppointmentMappingModel();

//                        appointmentModel.setpId();
        appointmentMappingModel.setEventId(appointmentData.getAppointment().getId());
        appointmentMappingModel.setExternalId("0");
        appointmentMappingModel.setAppointment_name(appointmentData.getAppointment().getAppointment_name());
        appointmentMappingModel.setLocality(appointmentData.getAppointment().getLocality());
        appointmentMappingModel.setStarttime(appointmentData.getAppointment().getStarttime());
        appointmentMappingModel.setEndtime(appointmentData.getAppointment().getEndtime());
        appointmentMappingModel.setIs_recurring(appointmentData.getAppointment().getIs_recurring());
        appointmentMappingModel.setRepeat(appointmentData.getAppointment().getRepeate());
        appointmentMappingModel.setRepeate_num(appointmentData.getAppointment().getRepeate_num());
        appointmentMappingModel.setRepeate_frequency(appointmentData.getAppointment().getRepeate_frequency());
        appointmentMappingModel.setRepeate_untill(appointmentData.getAppointment().getRepeate_untill());
        appointmentMappingModel.setHasNotes(notesTable.getCountById(appointmentData.getAppointment().getId()) >= 1);
        ArrayList<AppoitmentDataModel.Attendee> attendees = appointmentData.getAppointmentAttendee();
        if (attendees != null && attendees.size() > 0) {
            ArrayList<AttendeeModel> attendeeLocalModels = new ArrayList<AttendeeModel>();
            TableKids kidsTable = new TableKids((BaseApplication.getInstance()));
            TableAdult tableAdult = new TableAdult((BaseApplication.getInstance()));

            for (AppoitmentDataModel.Attendee attendee : attendees) {
                AttendeeModel attendeeLocalModel = new AttendeeModel();
                attendeeLocalModel.setAppoitmentId(appointmentMappingModel.getEventId());
                attendeeLocalModel.setId(attendee.getUk_id());
                attendeeLocalModel.setServerid(attendee.getId());
                String type = attendee.getUk_type();

                if (type.equals("user")) {
                    UserInfo adultData = tableAdult.getAdults(attendee.getUk_id());
                    attendeeLocalModel.setName(adultData.getFirst_name());
                    attendeeLocalModel.setColorCode(adultData.getColor_code());
                } else if (type.equals("kid")) {
                    KidsInfo kidsData = kidsTable.getKids(attendee.getUk_id());
                    attendeeLocalModel.setName(kidsData.getName());
                    attendeeLocalModel.setColorCode(kidsData.getColor_code());
                }
                attendeeLocalModels.add(attendeeLocalModel);
            }

            appointmentMappingModel.setAttendee(attendeeLocalModels);
        }

        updateAppointmentMapById(appointmentMappingModel, isEdit);
    }


//    private void putAppointmentInList(String mdate, ArrayList<AppointmentMappingModel> mAppointmentModels) {
//
//        LinkedHashMap<String, ArrayList<AppointmentMappingModel>> newOriginalList = new LinkedHashMap<>();
//        DateFormat mformat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
//        Date mDate = null;
//        Date pDate = null;
//
//        Boolean ifAddetToLast = true;
//
//        Calendar selectedCal = Calendar.getInstance();
//        Calendar particularCal = Calendar.getInstance();
//        try {
//            pDate = mformat.parse(mdate);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        particularCal.setTime(pDate);
//
//        for (Map.Entry<String, ArrayList<AppointmentMappingModel>> entry : mOriginalList.entrySet()) {
//
//            try {
//                mDate = mformat.parse(entry.getKey());
//                selectedCal.setTime(mDate);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//
//            if (particularCal.getTimeInMillis() < selectedCal.getTimeInMillis()) {
//                newOriginalList.put(mdate, mAppointmentModels);
//                ifAddetToLast = false;
//            }
//            newOriginalList.put(entry.getKey(), entry.getValue());
//        }
//
//        if (ifAddetToLast) {
//            newOriginalList.put(mdate, mAppointmentModels);
//        }
//
//        mOriginalList.clear();
//        mOriginalList = newOriginalList;
//
//    }

}
