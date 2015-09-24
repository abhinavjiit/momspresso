package com.mycity4kids.newmodels;

import java.util.ArrayList;

/**
 * Created by manish.soni on 16-07-2015.
 */
public class DeleteTaskModel {

    ArrayList<Tasks> tasks;

    public ArrayList<Tasks> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<Tasks> tasks) {
        this.tasks = tasks;
    }

    public class Tasks {

        int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
