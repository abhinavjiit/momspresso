package com.mycity4kids.newmodels;

import java.util.ArrayList;

/**
 * Created by khushboo.goyal on 22-06-2015.
 */
public class CompleteTaskRequestModel {

    private ArrayList<Todo> todolist;

    public ArrayList<Todo> getTodolist() {
        return todolist;
    }

    public void setTodolist(ArrayList<Todo> todolist) {
        this.todolist = todolist;
    }


    public class Todo {
        private CompletedTask Task;

        public CompletedTask getTask() {
            return Task;
        }

        public void setTask(CompletedTask task) {
            Task = task;
        }




    }

    public class CompletedTask {
        private int task_id;
        private ArrayList<Dates> excluded_date;


        public int getTask_id() {
            return task_id;
        }

        public void setTask_id(int task_id) {
            this.task_id = task_id;
        }
        public ArrayList<Dates> getExcluded_date() {
            return excluded_date;
        }

        public void setExcluded_date(ArrayList<Dates> excluded_date) {
            this.excluded_date = excluded_date;
        }




    }

    public class Dates {
        private String date;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }

}




