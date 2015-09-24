package com.mycity4kids.sync;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.kelltontech.utils.ConnectivityUtils;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.dbtable.TableTaskData;
import com.mycity4kids.dbtable.TaskCompletedTable;
import com.mycity4kids.dbtable.TaskTableAttendee;
import com.mycity4kids.dbtable.TaskTableFile;
import com.mycity4kids.dbtable.TaskTableNotes;
import com.mycity4kids.dbtable.TaskTableWhoToRemind;
import com.mycity4kids.models.forgot.CommonResponse;
import com.mycity4kids.newmodels.CompleteTaskRequestModel;
import com.mycity4kids.newmodels.DeleteTaskModel;
import com.mycity4kids.newmodels.TaskResponse;

import java.util.ArrayList;

/**
 * Created by kapil.vij on 17-07-2015.
 */
public class CompletedTasksService extends IntentService implements UpdateListener {

    private final static String TAG = CompletedTasksService.class.getSimpleName();

    ArrayList<Integer> taskIdlist;
    ArrayList<Integer> deletedTasksList;

    public CompletedTasksService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (ConnectivityUtils.isNetworkEnabled(this)) {
            hitApiRequest(AppConstants.TASKS_COMPLETE_REQUEST);
            hitApiRequest(AppConstants.DELETE_TASK_REQUEST);
        }

    }


    public void CallService(int requestType, String url, String json) {
        ApiHandler handler = new ApiHandler(this, this, requestType);
        handler.execute(url, json);
    }

    private void hitApiRequest(int requestType) {

        switch (requestType) {
            case AppConstants.TASKS_COMPLETE_REQUEST:

                //get from db
                TaskCompletedTable table = new TaskCompletedTable(BaseApplication.getInstance());
                taskIdlist = table.getIdList();

                if (!taskIdlist.isEmpty()) {

                    CompleteTaskRequestModel mainModel = new CompleteTaskRequestModel();

                    ArrayList<CompleteTaskRequestModel.Todo> todoCompleteList = new ArrayList<>();

                    for (int taskid : taskIdlist) {

                        // get dates
                        ArrayList<String> dbDateList = table.getDatesById(taskid);

                        CompleteTaskRequestModel.Todo todo = new CompleteTaskRequestModel().new Todo();

                        CompleteTaskRequestModel.CompletedTask modelCompletedTask = new CompleteTaskRequestModel().new CompletedTask();

                        ArrayList<CompleteTaskRequestModel.Dates> modelDateList = new ArrayList<>();


                        for (String date : dbDateList) {
                            CompleteTaskRequestModel.Dates dateModel = new CompleteTaskRequestModel().new Dates();
                            dateModel.setDate(date);

                            modelDateList.add(dateModel);
                        }
                        modelCompletedTask.setTask_id(taskid);
                        modelCompletedTask.setExcluded_date(modelDateList);
                        todo.setTask(modelCompletedTask);

                        todoCompleteList.add(todo);


                    }

                    mainModel.setTodolist(todoCompleteList);

                    String data = new Gson().toJson(mainModel);
                    CallService(requestType, AppConstants.TASK_COMPLETE_URL, data);


                }


                break;
            case AppConstants.DELETE_TASK_REQUEST:

                // get from db

                TableTaskData taskData = new TableTaskData(BaseApplication.getInstance());
                deletedTasksList = taskData.getInActiveTasksData();

                if (deletedTasksList != null && !deletedTasksList.isEmpty()) {
                    DeleteTaskModel taskModel = new DeleteTaskModel();
                    ArrayList<DeleteTaskModel.Tasks> tasksArrayList = new ArrayList<>();



                    for (int val : deletedTasksList) {
                        DeleteTaskModel.Tasks model = new DeleteTaskModel().new Tasks();
                        model.setId(val);
                        tasksArrayList.add(model);
                    }

                    taskModel.setTasks(tasksArrayList);

                    String data = new Gson().toJson(taskModel);
                    CallService(requestType, AppConstants.DELETE_TASK_URL, data);

                }


                break;
        }


    }


    @Override
    public void updateView(String jsonString, int requestType) {
        switch (requestType) {
            case AppConstants.TASKS_COMPLETE_REQUEST:
                try {

                    CommonResponse responseData = new Gson().fromJson(jsonString, CommonResponse.class);

                    if (responseData.getResponseCode() == 200) {
                        // mark sync as 1
                        for (int taskid : taskIdlist) {

                            TaskCompletedTable table = new TaskCompletedTable(BaseApplication.getInstance());
                            ArrayList<String> dbDateList = table.getDatesById(taskid);

                            for (String date : dbDateList) {

                                table.updateSyncFlag(date, taskid);

                            }


                        }

                    } else {
                        Log.e(TAG, "response failed task complete");
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case AppConstants.DELETE_TASK_REQUEST:
                try {


                    TaskResponse responseData = new Gson().fromJson(jsonString, TaskResponse.class);
                    if (responseData.getResponseCode() == 200) {
                        // delete from db
                        TableTaskData tableTask = new TableTaskData(BaseApplication.getInstance());
                        for (int taskId : deletedTasksList) {
                            // delete  in db

                            tableTask.deleteTask(taskId);

                            // get from attendee table
                            TaskTableAttendee attendeeTable = new TaskTableAttendee(BaseApplication.getInstance());
                            attendeeTable.deleteTask(taskId);


                            // get from whotoRemond table
                            TaskTableWhoToRemind whotoRemindTable = new TaskTableWhoToRemind(BaseApplication.getInstance());
                            whotoRemindTable.deleteTask(taskId);

                            // get from FILES

                            TaskTableFile fileTable = new TaskTableFile(BaseApplication.getInstance());
                            fileTable.deleteTask(taskId);
                            // note

                            TaskTableNotes notesTable = new TaskTableNotes(BaseApplication.getInstance());
                            notesTable.deleteTask(taskId);

                        }


                    } else if (responseData.getResponseCode() == 400) {
                        Log.e(TAG, "response failed getAppointment");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;


        }
    }


}
