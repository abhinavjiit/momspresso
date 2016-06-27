package com.mycity4kids.reminders;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.dbtable.TableAppointmentData;
import com.mycity4kids.dbtable.TableTaskData;
import com.mycity4kids.dbtable.TableWhoToRemind;
import com.mycity4kids.dbtable.TaskTableWhoToRemind;
import com.mycity4kids.newmodels.AppointmentMappingModel;
import com.mycity4kids.newmodels.AttendeeModel;
import com.mycity4kids.newmodels.TaskDataModel;
import com.mycity4kids.newmodels.TaskMappingModel;
import com.mycity4kids.preference.SharedPrefUtils;

import java.util.ArrayList;

/**
 * Created by kapil.vij on 08-07-2015.
 */
public class BootCompleteReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        // get all appoitmnet

        TableAppointmentData appointmentTable = new TableAppointmentData(BaseApplication.getInstance());
        ArrayList<AppointmentMappingModel> model = appointmentTable.getAll();

        for (AppointmentMappingModel data : model) {

            long startTimeMillis = data.getStarttime();
            String reminderBefore = data.getReminder();
            String recurring = data.getIs_recurring();
            String repeat = data.getRepeat();
            String repeatFrequency = data.getRepeate_frequency();
            String repeatNum = data.getRepeate_num();
            String repeatUntill = data.getRepeate_untill();
            int reminderId = data.getEventId();
            String appointmentName = data.getAppointment_name();

            // get who to remind from id
            TableWhoToRemind whotoRemindTable = new TableWhoToRemind(BaseApplication.getInstance());
            ArrayList<AttendeeModel> whoToRemindList = whotoRemindTable.getDataByAppointment(reminderId);

            for (AttendeeModel dataModel : whoToRemindList) {
                if (dataModel.getId().equals(SharedPrefUtils.getUserDetailModel(context).getId())) {
                    Reminder.with(context).info(Constants.REMINDER_TYPE_APPOINTMENT, appointmentName).startTime(startTimeMillis).setRepeatBehavior(repeat, repeatUntill, repeatFrequency, repeatNum).remindBefore(reminderBefore).setRecurring(recurring).create(reminderId);
                    break;
                }
            }

        }

        // get all tasks
        TableTaskData taskTable = new TableTaskData(BaseApplication.getInstance());
        ArrayList<TaskDataModel.TaskDetail> taskModel = taskTable.getAll();

        for (TaskDataModel.TaskDetail data : taskModel) {
            long startTimeMillis = data.getDue_date();
            String reminderBefore = data.getReminder();
            String recurring = data.getIs_recurring();
            String repeat = data.getRepeate();
            String repeatFrequency = data.getRepeate_frequency();
            String repeatNum = data.getRepeate_num();
            String repeatUntill = data.getRepeate_untill();
            int reminderId = data.getId();
            String taskName = data.getTask_name();

            TaskTableWhoToRemind whotoRemindTable = new TaskTableWhoToRemind(BaseApplication.getInstance());
            ArrayList<AttendeeModel> whoToRemindList = whotoRemindTable.getDataByTask(reminderId);

            for (AttendeeModel dataModel : whoToRemindList) {
                if (dataModel.getId().equals(SharedPrefUtils.getUserDetailModel(context).getId())) {
                    Reminder.with(context).info(Constants.REMINDER_TYPE_TASKS, taskName).startTime(startTimeMillis).setRepeatBehavior(repeat, repeatUntill, repeatFrequency, repeatNum).remindBefore(reminderBefore).setRecurring(recurring).create(reminderId);
                    break;

                }

            }
        }


    }
}
