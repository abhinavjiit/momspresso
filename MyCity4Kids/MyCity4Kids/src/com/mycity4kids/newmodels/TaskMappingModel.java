package com.mycity4kids.newmodels;

import java.util.ArrayList;

/**
 * Created by manish.soni on 08-07-2015.
 */
public class TaskMappingModel implements Cloneable {

    int task_id;
    Boolean isChecked;
    boolean isCompleted;
    int active;
    String taskName;
    long taskDate;
    String taskListname;
    ArrayList<AttendeeModel> attendees;
    int numberNotes = 0;
    String is_recurring;
    String repeat;
    String repeate_untill;
    String showDate = "";
    String repeate_num;
    String repeate_frequency;

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public String getShowDate() {
        return showDate;
    }

    public void setShowDate(String showDate) {
        this.showDate = showDate;
    }

    public String getIs_recurring() {
        return is_recurring;
    }

    public void setIs_recurring(String is_recurring) {
        this.is_recurring = is_recurring;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public String getRepeate_untill() {
        return repeate_untill;
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

    public TaskMappingModel() {

    }

    public TaskMappingModel(String name, long date, String listName) {

        this.taskName = name;
        this.taskDate = date;
        this.taskListname = listName;

    }

    public int getTask_id() {
        return task_id;
    }

    public void setTask_id(int task_id) {
        this.task_id = task_id;
    }

    public Boolean getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(Boolean isChecked) {
        this.isChecked = isChecked;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public long getTaskDate() {
        return taskDate;
    }

    public void setTaskDate(long taskDate) {
        this.taskDate = taskDate;
    }

    public String getTaskListname() {
        return taskListname;
    }

    public void setTaskListname(String taskListname) {
        this.taskListname = taskListname;
    }

    public int getNumberNotes() {
        return numberNotes;
    }

    public void setNumberNotes(int numberNotes) {
        this.numberNotes = numberNotes;
    }

    public ArrayList<AttendeeModel> getAttendees() {
        return attendees;
    }

    public void setAttendees(ArrayList<AttendeeModel> attendees) {
        this.attendees = attendees;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }
}
