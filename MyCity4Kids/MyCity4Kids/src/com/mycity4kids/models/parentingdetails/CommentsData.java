package com.mycity4kids.models.parentingdetails;

import java.util.ArrayList;

public class CommentsData {
    private String id;
    private String node_id;
    private String parent_id;
    private String name;
    private String body;
    private String created;
    private String comment_type;
    private String profile_image;
    private ArrayList<CommentsData> replies;

    public ArrayList<CommentsData> getReplies() {
        return replies;
    }

    public void setReplies(ArrayList<CommentsData> replies) {
        this.replies = replies;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNode_id() {
        return node_id;
    }

    public void setNode_id(String node_id) {
        this.node_id = node_id;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getComment_type() {
        return comment_type;
    }

    public void setComment_type(String comment_type) {
        this.comment_type = comment_type;
    }

    public String getCreate() {
        return created;
    }

    public void setCreate(String create) {
        this.created = create;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }


}
