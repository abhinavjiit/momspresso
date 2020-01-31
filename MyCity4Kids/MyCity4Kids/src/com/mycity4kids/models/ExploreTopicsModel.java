package com.mycity4kids.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ExploreTopicsModel implements Parcelable {

    private String id;
    private String title;
    private String display_name;

    @SerializedName("public")
    private String publicVisibility;
    private String showInMenu;
    private ArrayList<ExploreTopicsModel> child;
    private String parentId;
    private String parentName;
    private boolean isSelected;
    private String slug;

    private List<ExtraData> extraData;

    public class ExtraData  {
        private Challenges challenges;
        private String metaTitle;
        private String metaDescription;
        private String max_duration = "60";
        private CategoryImage categoryBackImage;

        public String getMetaTitle() {
            return metaTitle;
        }

        public void setMetaTitle(String metaTitle) {
            this.metaTitle = metaTitle;
        }

        public String getMetaDescription() {
            return metaDescription;
        }

        public void setMetaDescription(String metaDescription) {
            this.metaDescription = metaDescription;
        }

        public CategoryImage getCategoryBackImage() {
            return categoryBackImage;
        }

        public void setCategoryBackImage(CategoryImage categoryBackImage) {
            this.categoryBackImage = categoryBackImage;
        }

        public Challenges getChallenges() {
            return challenges;
        }

        public void setChallenges(Challenges challenges) {
            this.challenges = challenges;
        }

        public String getMax_duration() {
            return max_duration;
        }

        public void setMax_duration(String max_duration) {
            this.max_duration = max_duration;
        }

        public class CategoryImage {
            private String web;
            private String app;
            private String mob;

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

        public class Challenges {

            private String active;
            private String type;
            private String desc;
            private String imageUrl;

            public String getActive() {
                return active;
            }

            public String getDesc() {
                return desc;
            }

            public String getImageUrl() {
                return imageUrl;
            }

            public String getType() {
                return type;
            }

            public void setActive(String active) {
                this.active = active;
            }

            public void setDesc(String desc) {
                this.desc = desc;
            }

            public void setImageUrl(String imageUrl) {
                this.imageUrl = imageUrl;
            }

            public void setType(String type) {
                this.type = type;
            }
        }
    }

    public ExploreTopicsModel() {

    }

    public ExploreTopicsModel(String id, String title, boolean isSelected, ArrayList<ExploreTopicsModel> child, String parentId, String parentName) {
        this.id = id;
        this.title = title;
        this.isSelected = isSelected;
        this.child = child;
        this.parentId = parentId;
        this.parentName = parentName;
    }

    protected ExploreTopicsModel(Parcel in) {
        id = in.readString();
        title = in.readString();
        child = in.createTypedArrayList(ExploreTopicsModel.CREATOR);
        parentId = in.readString();
        parentName = in.readString();
        display_name = in.readString();
        publicVisibility = in.readString();
        showInMenu = in.readString();
        isSelected = in.readByte() != 0;
        slug = in.readString();
    }

    public static final Creator<ExploreTopicsModel> CREATOR = new Creator<ExploreTopicsModel>() {
        @Override
        public ExploreTopicsModel createFromParcel(Parcel in) {
            return new ExploreTopicsModel(in);
        }

        @Override
        public ExploreTopicsModel[] newArray(int size) {
            return new ExploreTopicsModel[size];
        }
    };

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

    public ArrayList<ExploreTopicsModel> getChild() {
        return child;
    }

    public void setChild(ArrayList<ExploreTopicsModel> child) {
        this.child = child;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
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

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getPublicVisibility() {
        return publicVisibility;
    }

    // Access level while tagging a topic to a newly written article
    public void setPublicVisibility(String publicVisibility) {
        this.publicVisibility = publicVisibility;
    }

    // Access level for showing in Menu. From where a user can view articles tagged under this topic.
    public String getShowInMenu() {
        return showInMenu;
    }

    public void setShowInMenu(String showInMenu) {
        this.showInMenu = showInMenu;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public List<ExtraData> getExtraData() {
        return extraData;
    }

    public void setExtraData(List<ExtraData> extraData) {
        this.extraData = extraData;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeTypedList(child);
        dest.writeString(parentId);
        dest.writeString(parentName);
        dest.writeString(display_name);
        dest.writeString(publicVisibility);
        dest.writeString(showInMenu);
        dest.writeByte((byte) (isSelected ? 1 : 0));
        dest.writeString(slug);
    }
}