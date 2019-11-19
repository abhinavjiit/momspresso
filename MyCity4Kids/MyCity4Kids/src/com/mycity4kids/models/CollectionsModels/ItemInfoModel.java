package com.mycity4kids.models.CollectionsModels;

import com.mycity4kids.models.response.ImageURL;

public class ItemInfoModel {

    private Author author;
    private String title;
    private String thumbnail;
    private String userName;
    private ImageURL imageUrl;
    private String storyImage;


    public String getStoryImage() {
        return storyImage;
    }

    public void setStoryImage(String storyImage) {
        this.storyImage = storyImage;
    }

    public ImageURL getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(ImageURL imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public class Author {

        String blogTitleSlug;
        ProfilePic profilePicUrl;
        String firstName;

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

        String lastName;


        public class ProfilePic {

            String clientApp;

            public String getClientApp() {
                return clientApp;
            }

            public void setClientApp(String clientApp) {
                this.clientApp = clientApp;
            }
        }


    }


}
