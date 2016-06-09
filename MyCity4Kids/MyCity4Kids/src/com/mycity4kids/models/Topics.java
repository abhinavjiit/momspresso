package com.mycity4kids.models;

import java.util.ArrayList;

public class Topics {

    private int id;
    private String title;
    private ArrayList<Topics> child;
    private int parentId;
    private String parentName;
    private boolean isSelected;

    public Topics(int id, String title, boolean isSelected, ArrayList<Topics> child, int parentId, String parentName) {
        this.id = id;
        this.title = title;
        this.isSelected = isSelected;
        this.child = child;
        this.parentId = parentId;
        this.parentName = parentName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Topics> getChild() {
        return child;
    }

    public void setChild(ArrayList<Topics> child) {
        this.child = child;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

}