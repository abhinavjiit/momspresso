package com.mycity4kids.models.response;

/**
 * Created by hemant on 3/1/17.
 */
public class HomeVideosListingResult {

    private String id;
    private String title;
    private String title_slug;
    private String user_id;
    private String uploaded_url;
    private String old_video_id;
    private String filename;
    private String approval_time;
    private String user_agent;
    private String published_status;
    private String publication_status;
    private String commentUri;
    private Author author;
    private String created_at;
    private String updated_at;

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

    public String getTitle_slug() {
        return title_slug;
    }

    public void setTitle_slug(String title_slug) {
        this.title_slug = title_slug;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUploaded_url() {
        return uploaded_url;
    }

    public void setUploaded_url(String uploaded_url) {
        this.uploaded_url = uploaded_url;
    }

    public String getOld_video_id() {
        return old_video_id;
    }

    public void setOld_video_id(String old_video_id) {
        this.old_video_id = old_video_id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getApproval_time() {
        return approval_time;
    }

    public void setApproval_time(String approval_time) {
        this.approval_time = approval_time;
    }

    public String getUser_agent() {
        return user_agent;
    }

    public void setUser_agent(String user_agent) {
        this.user_agent = user_agent;
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

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public class Author {
        private String id;
        private String firstName;
        private String lastName;
        private String blogTitleSlug;
        private String userType;
        private ProfilePic profilePic;

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
            return profilePic;
        }

        public void setProfilePic(ProfilePic profilePic) {
            this.profilePic = profilePic;
        }
    }

}
