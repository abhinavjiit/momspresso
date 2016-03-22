package com.mycity4kids.models.editor;

/**
 * Created by anshul on 3/20/16.
 */
public class BlogData  {
    private String id;
    private String user_id;
    private boolean first_article_moderated;
    private boolean status;
    private String blog_slug;
    private String created;
    private String publish_date_time;
    private String blog_image;

    public String getBlog_image() {
        return blog_image;
    }

    public String getBlog_slug() {
        return blog_slug;
    }

    public String getCreated() {
        return created;
    }

    public String getId() {
        return id;
    }

    public String getPublish_date_time() {
        return publish_date_time;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBlog_image(String blog_image) {
        this.blog_image = blog_image;
    }

    public void setBlog_slug(String blog_slug) {
        this.blog_slug = blog_slug;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public void setFirst_article_moderated(boolean first_article_moderated) {
        this.first_article_moderated = first_article_moderated;
    }

    public void setPublish_date_time(String publish_date_time) {
        this.publish_date_time = publish_date_time;
    }

}

