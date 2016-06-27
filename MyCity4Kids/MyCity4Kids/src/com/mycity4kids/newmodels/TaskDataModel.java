package com.mycity4kids.newmodels;

import android.os.Parcel;
import android.os.Parcelable;

import com.kelltontech.model.BaseModel;
import com.mycity4kids.models.basemodel.BaseDataModel;

import java.util.ArrayList;

/**
 * Created by khushboo.goyal on 24-06-2015.
 */
public class TaskDataModel extends BaseDataModel {


    private ArrayList<TaskData> Task;
    private ArrayList<TaskList> TaskList;
    private ArrayList<TaskDelete> TaskDeleted;

    public ArrayList<TaskDataModel.TaskList> getTaskList() {
        return TaskList;
    }

    public void setTaskList(ArrayList<TaskDataModel.TaskList> taskList) {
        TaskList = taskList;
    }

    public ArrayList<TaskData> getTask() {
        return Task;
    }

    public void setTask(ArrayList<TaskData> task) {
        Task = task;
    }

    public class TaskData extends BaseDataModel {
        private TaskDetail Task;
        ArrayList<Notes> TaskNote;
        ArrayList<WhoToRemind> TaskWhomRemind;
        ArrayList<Attendee> TaskAttendee;
        ArrayList<Files> TaskFile;
        ArrayList<TaskCompleted> TaskExcludedDate;

        public ArrayList<TaskCompleted> getTaskExcludedDate() {
            return TaskExcludedDate;
        }

        public void setTaskExcludedDate(ArrayList<TaskCompleted> taskExcludedDate) {
            TaskExcludedDate = taskExcludedDate;
        }

        public TaskDetail getTask() {
            return Task;
        }

        public void setTask(TaskDetail task) {
            Task = task;
        }

        public ArrayList<Notes> getTaskNote() {
            return TaskNote;
        }

        public void setTaskNote(ArrayList<Notes> taskNote) {
            TaskNote = taskNote;
        }

        public ArrayList<WhoToRemind> getTaskWhomRemind() {
            return TaskWhomRemind;
        }

        public void setTaskWhomRemind(ArrayList<WhoToRemind> taskWhomRemind) {
            TaskWhomRemind = taskWhomRemind;
        }

        public ArrayList<Attendee> getTaskAttendee() {
            return TaskAttendee;
        }

        public void setTaskAttendee(ArrayList<Attendee> taskAttendee) {
            TaskAttendee = taskAttendee;
        }

        public ArrayList<Files> getTaskFile() {
            return TaskFile;
        }

        public void setTaskFile(ArrayList<Files> taskFile) {
            TaskFile = taskFile;
        }
    }

    public class TaskDetail extends BaseDataModel {


        private int offline_id;
        private int id;
        private String task_name = "";
        //        String locality = "";
        private long due_date = 0;

        //        long endtime = 0;
        private String reminder = "";
        private String is_recurring = "";
        private String repeate = "";
        private String repeate_untill = "";
        private String repeate_num = "";
        private String repeate_frequency;

        private int user_id;
        private int family_id;
        private int task_list_id;
        private int active;
        private String listName;

        public int getOffline_id() {
            return offline_id;
        }

        public void setOffline_id(int offline_id) {
            this.offline_id = offline_id;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTask_name() {
            return task_name;
        }

        public void setTask_name(String task_name) {
            this.task_name = task_name;
        }

        public long getDue_date() {
            return due_date;
        }

        public void setDue_date(long due_date) {
            this.due_date = due_date;
        }

        public String getReminder() {
            return reminder;
        }

        public void setReminder(String reminder) {
            this.reminder = reminder;
        }

        public String getIs_recurring() {
            return is_recurring;
        }

        public void setIs_recurring(String is_recurring) {
            this.is_recurring = is_recurring;
        }

        public String getRepeate() {
            return repeate;
        }

        public void setRepeate(String repeate) {
            this.repeate = repeate;
        }

        public String getRepeate_untill() {
            return repeate_untill;
        }

        public String getListName() {
            return listName;
        }

        public void setListName(String listName) {
            this.listName = listName;
        }

        public void setRepeate_untill(String repeate_untill) {
            this.repeate_untill = repeate_untill;


        }

        public String getRepeate_num() {
            return repeate_num;
        }

        public void setRepeate_num(String repeate_num) {
            this.repeate_num = repeate_num;
        }

        public String getRepeate_frequency() {
            return repeate_frequency;
        }

        public void setRepeate_frequency(String repeate_frequency) {
            this.repeate_frequency = repeate_frequency;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public int getFamily_id() {
            return family_id;
        }

        public void setFamily_id(int family_id) {
            this.family_id = family_id;
        }

        public int getTask_list_id() {
            return task_list_id;
        }

        public void setTask_list_id(int task_list_id) {
            this.task_list_id = task_list_id;
        }

        public int getActive() {
            return active;
        }

        public void setActive(int active) {
            this.active = active;
        }
    }

    public class Attendee extends BaseDataModel {
        private String uk_id;
        private String uk_type;
        private int id;
        private int task_id;

        public String getUk_id() {
            return uk_id;
        }

        public void setUk_id(String uk_id) {
            this.uk_id = uk_id;
        }

        public String getUk_type() {
            return uk_type;
        }

        public void setUk_type(String uk_type) {
            this.uk_type = uk_type;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getTask_id() {
            return task_id;
        }

        public void setTask_id(int task_id) {
            this.task_id = task_id;
        }
    }

    public static class Files extends BaseDataModel implements Parcelable {

        private int task_id;
        private String file_name;
        private String file_type;
        private String url;
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getTask_id() {
            return task_id;
        }

        public void setTask_id(int task_id) {
            this.task_id = task_id;
        }

        public String getFile_name() {
            return file_name;
        }

        public void setFile_name(String file_name) {
            this.file_name = file_name;
        }

        public String getFile_type() {
            return file_type;
        }

        public void setFile_type(String file_type) {
            this.file_type = file_type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }


        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(file_name);
            dest.writeString(file_type);
            dest.writeString(url);
            dest.writeInt(task_id);
            dest.writeInt(id);

        }

        public Files() {

        }

        public Files(Parcel in) {
            // TODO Auto-generated constructor stub
            super();
            file_name = in.readString();
            file_type = in.readString();
            url = in.readString();
            task_id = in.readInt();
            id = in.readInt();
        }

        @Override
        public int describeContents() {
            // TODO Auto-generated method stub
            return 0;
        }

        public static Parcelable.Creator<Files> CREATOR = new Parcelable.Creator<Files>() {
            @Override
            public Files createFromParcel(Parcel source) {
                return new Files(source);
            }

            @Override
            public Files[] newArray(int size) {
                return new Files[size];
            }
        };
    }

    public class WhoToRemind extends BaseDataModel {

        String user_id;
        int task_id;
        private int id;

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public int getTask_id() {
            return task_id;
        }

        public void setTask_id(int task_id) {
            this.task_id = task_id;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public class Notes extends BaseDataModel {

        private String user_id;
        private int id;
        private String note;
        private int task_id;

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public int getTask_id() {
            return task_id;
        }

        public void setTask_id(int task_id) {
            this.task_id = task_id;
        }
    }

    public class TaskList extends BaseModel {

        TaskListModel TaskList;

        public TaskListModel getTaskList() {
            return TaskList;
        }

        public void setTaskList(TaskListModel taskList) {
            TaskList = taskList;
        }
    }


    public class TaskCompleted extends BaseModel {
        private int task_id;
        private int id;
        private String excluded_date;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getTask_id() {
            return task_id;
        }

        public void setTask_id(int task_id) {
            this.task_id = task_id;
        }

        public String getExcluded_date() {
            return excluded_date;
        }

        public void setExcluded_date(String excluded_date) {
            this.excluded_date = excluded_date;
        }


    }

    public class TaskDelete {
        public TaskDel getTask() {
            return Task;
        }

        public void setTask(TaskDel task) {
            Task = task;
        }

        public TaskDel Task;


    }

    public ArrayList<TaskDelete> getTaskDeleted() {
        return TaskDeleted;
    }

    public void setTaskDeleted(ArrayList<TaskDelete> taskDeleted) {
        TaskDeleted = taskDeleted;
    }

    public class TaskDel {
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int id;
    }

}
