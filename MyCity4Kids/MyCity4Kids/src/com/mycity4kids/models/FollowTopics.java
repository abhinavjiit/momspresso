package com.mycity4kids.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class FollowTopics {
    @SerializedName("id")
    private String id;
    @SerializedName("display_name")
    private String display_name;
    @SerializedName("showInMenu")
    private String showInMenu;
    @SerializedName("title")
    private String title;
    @SerializedName("slug")
    private String slug;
    @SerializedName("child")
    private ArrayList<Topics> child;
    @SerializedName("public")
    private String publicVisibility;
    @SerializedName("extraData")
    private Extra extraData;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getShowInMenu() {
        return showInMenu;
    }

    public void setShowInMenu(String showInMenu) {
        this.showInMenu = showInMenu;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public ArrayList<Topics> getChild() {
        return child;
    }

    public void setChild(ArrayList<Topics> child) {
        this.child = child;
    }

    public String getPublicVisibility() {
        return publicVisibility;
    }

    public void setPublicVisibility(String publicVisibility) {
        this.publicVisibility = publicVisibility;
    }

    public Extra getExtraData() {
        return extraData;
    }

    public void setExtraData(Extra extraData) {
        this.extraData = extraData;
    }

    public class Extra {
        @SerializedName("categoryBackImage")
        CategoryBackgroundImage categoryBackImage;

        public CategoryBackgroundImage getCategoryBackImage() {
            return categoryBackImage;
        }

        public void setCategoryBackImage(CategoryBackgroundImage categoryBackImage) {
            this.categoryBackImage = categoryBackImage;
        }

        public class CategoryBackgroundImage {
            @SerializedName("web")
            String web;
            @SerializedName("app")
            String app;
            @SerializedName("mob")
            String mob;

            public String getWeb() {
                return web;
            }

            public void setWeb(String web) {
                this.web = web;
            }

            public String getApp() {
                return app;
            }

            public void setApp(String app) {
                this.app = app;
            }

            public String getMob() {
                return mob;
            }

            public void setMob(String mob) {
                this.mob = mob;
            }
        }
    }
}
