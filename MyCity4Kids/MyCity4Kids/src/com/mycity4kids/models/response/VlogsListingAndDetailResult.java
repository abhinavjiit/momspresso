package com.mycity4kids.models.response;

import java.util.ArrayList;

/**
 * Created by hemant on 3/1/17.
 */
public class VlogsListingAndDetailResult {

    private String id;
    private String title;
    private String title_slug;
    private String url;
    private String published_time;
    private String approval_time;
    private String published_status;
    private String publication_status;
    private String commentUri;
    private Author author;
    private String view_count;
    private ArrayList<String> category_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleSlug() {
        return title_slug;
    }

    public void setTitleSlug(String titleSlug) {
        this.title_slug = titleSlug;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPublished_time() {
        return published_time;
    }

    public void setPublished_time(String published_time) {
        this.published_time = published_time;
    }

    public String getPublished_status() {
        return published_status;
    }

    public void setPublished_status(String published_status) {
        this.published_status = published_status;
    }

    public String getPublication_status() {
        return publication_status;
    }

    public void setPublication_status(String publication_status) {
        this.publication_status = publication_status;
    }

    public String getCommentUri() {
        return commentUri;
    }

    public void setCommentUri(String commentUri) {
        this.commentUri = commentUri;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getView_count() {
        return view_count;
    }

    public void setView_count(String view_count) {
        this.view_count = view_count;
    }

    public ArrayList<String> getCategory_id() {
        return category_id;
    }

    public void setCategory_id(ArrayList<String> category_id) {
        this.category_id = category_id;
    }

    public class Author {
        private String id;
        private String firstName;
        private String lastName;
        private String blogTitleSlug;
        private String userType;
        private ProfilePic profilePicUrl;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getBlogTitleSlug() {
            return blogTitleSlug;
        }

        public void setBlogTitleSlug(String blogTitleSlug) {
            this.blogTitleSlug = blogTitleSlug;
        }

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
        }

        public ProfilePic getProfilePic() {
            return profilePicUrl;
        }

        public void setProfilePic(ProfilePic profilePic) {
            this.profilePicUrl = profilePic;
        }
    }

}
