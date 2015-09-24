package com.mycity4kids.newmodels;

import com.kelltontech.model.BaseModel;

/**
 * Created by manish.soni on 10-07-2015.
 */
public class TaskListModel extends BaseModel {

    int id;
    String list_name;
    int user_id;
    int family_id;
    String list_for;

    int size;

    public TaskListModel() {

    }

    public TaskListModel(int id, String name, int size) {

        this.id = id;
        this.list_name = name;
        this.size = size;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getList_name() {
        return list_name;
    }

    public void setList_name(String list_name) {
        this.list_name = list_name;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getFamily_id() {
        return family_id;
    }

    public void setFamily_id(int family_id) {
        this.family_id = family_id;
    }

    public String getList_for() {
        return list_for;
    }

    public void setList_for(String list_for) {
        this.list_for = list_for;
    }
}
