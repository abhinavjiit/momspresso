package com.mycity4kids.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Topics implements Parcelable {

    private String id;
    private String title;
    private String display_name;

    @SerializedName("public")
    private String publicVisibility;

    private String showInMenu;
    private ArrayList<Topics> child = new ArrayList<>();
    private String parentId;
    private String parentName;
    private boolean isSelected;
    private String slug;
    private boolean prevKey = false;
    private List<ExtraData> extraData;
    private String sponsoredCategoryImage;


    private String sponsoredCategoryBadge;
    private String is_live;
    private String mapped_category;


    public static class ExtraData implements Parcelable {
        private Challenges challenge;
        private CategoryTag categoryTag;

        public CategoryTag getCategoryTag() {
            return categoryTag;
        }

        public void setCategoryTag(CategoryTag categoryTag) {
            this.categoryTag = categoryTag;
        }

        public ExtraData(Challenges challenge, CategoryTag categoryTag) {
            this.categoryTag = categoryTag;
            this.challenge = challenge;
        }

        public Challenges getChallenge() {

            return challenge;
        }

        public void setChallenge(Challenges challenge) {
            this.challenge = challenge;
        }


        public static class CategoryTag implements Parcelable {
            private String categoryImage;
            private String categoryBadge;


            public CategoryTag(String categoryImage, String categoryBadge) {
                this.categoryImage = categoryImage;
                this.categoryBadge = categoryBadge;
            }


            public String getCategoryImage() {
                return categoryImage;
            }

            public void setCategoryImage(String categoryImage) {
                this.categoryImage = categoryImage;
            }

            public String getCategoryBadge() {
                return categoryBadge;
            }

            public void setCategoryBadge(String categoryBadge) {
                this.categoryBadge = categoryBadge;
            }

            protected CategoryTag(Parcel in) {
                categoryImage = in.readString();
                categoryBadge = in.readString();


            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(categoryImage);
                dest.writeString(categoryBadge);

            }

            @Override
            public int describeContents() {
                return 0;
            }

            public static final Creator<CategoryTag> CREATOR = new Creator<CategoryTag>() {
                @Override
                public CategoryTag createFromParcel(Parcel in) {
                    return new CategoryTag(in);
                }

                @Override
                public CategoryTag[] newArray(int size) {
                    return new CategoryTag[size];
                }
            };
        }


        public static class Challenges implements Parcelable {
            private String active;
            private String videoUrl;
            private String imageUrl;
            private int type;
            private String desc;
            private String is_live;
            private String mapped_category;
            private int max_duration = 5000;

            public int getMax_duration() {
                return max_duration;
            }

            public void setMax_duration(int max_duration) {
                this.max_duration = max_duration;
            }

            public String getMapped_category() {
                return mapped_category;
            }

            public void setMapped_category(String mapped_category) {
                this.mapped_category = mapped_category;
            }

            public String getRules() {
                return rules;
            }

            public void setRules(String rules) {
                this.rules = rules;
            }

            private String rules;


            public Challenges(String active, String videoUrl, String imageUrl, int type, String desc) {
                this.active = active;
                this.videoUrl = videoUrl;
                this.imageUrl = imageUrl;
                this.type = type;

                this.desc = desc;
            }

            public String getActive() {
                return active;
            }

            public void setActive(String active) {
                this.active = active;
            }

            public String getVideoUrl() {
                return videoUrl;
            }

            public void setVideoUrl(String videoUrl) {
                this.videoUrl = videoUrl;
            }


            public String getImageUrl() {
                return imageUrl;

            }

            public void setImageUrl(String imageUrl) {
                this.imageUrl = imageUrl;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public String getDesc() {
                return desc;
            }

            public String getIs_live() {
                return is_live;
            }

            public void setIs_live(String is_live) {
                this.is_live = is_live;
            }

            public void setDesc(String desc) {
                this.desc = desc;
            }

            protected Challenges(Parcel in) {
                active = in.readString();
                videoUrl = in.readString();
                imageUrl = in.readString();
                type = in.readInt();
                desc = in.readString();
                rules = in.readString();
                is_live = in.readString();
                mapped_category = in.readString();
                max_duration = in.readInt();


            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(active);
                dest.writeString(videoUrl);
                dest.writeString(imageUrl);
                dest.writeInt(type);
                dest.writeString(desc);
                dest.writeString(rules);
                dest.writeString(is_live);
                dest.writeString(mapped_category);
                dest.writeInt(max_duration);

            }

            @Override
            public int describeContents() {
                return 0;
            }

            public static final Creator<Challenges> CREATOR = new Creator<Challenges>() {
                @Override
                public Challenges createFromParcel(Parcel in) {
                    return new Challenges(in);
                }

                @Override
                public Challenges[] newArray(int size) {
                    return new Challenges[size];
                }
            };
        }


        public ExtraData(Parcel in) {
            challenge = in.readParcelable(Challenges.class.getClassLoader());

        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(challenge, flags);
        }


        public static final Creator<ExtraData> CREATOR = new Creator<ExtraData>() {
            @Override
            public ExtraData createFromParcel(Parcel in) {
                return new ExtraData(in);
            }

            @Override
            public ExtraData[] newArray(int size) {
                return new ExtraData[size];
            }
        };
    }


    public Topics() {

    }

    public Topics(String id, String title, boolean isSelected, ArrayList<Topics> child, String parentId, String parentName) {
        this.id = id;
        this.title = title;
        this.isSelected = isSelected;
        this.child = child;
        this.parentId = parentId;
        this.parentName = parentName;
        // this.extraData=extraData;
        //this.extraData = extraData;
    }

    protected Topics(Parcel in) {
        id = in.readString();
        title = in.readString();
        child = in.createTypedArrayList(Topics.CREATOR);
        parentId = in.readString();
        parentName = in.readString();
        display_name = in.readString();
        publicVisibility = in.readString();
        showInMenu = in.readString();
        isSelected = in.readByte() != 0;
        slug = in.readString();
        //   extraData = in.readParcelable(ExtraData.class.getClassLoader());

        in.readTypedList(this.extraData, ExtraData.CREATOR);
        is_live = in.readString();
        prevKey = in.readByte() != 0;
        mapped_category = in.readString();


        //extraData = in.readParcelable(ExtraData.class.getClassLoader());

        //  extraData = in.createTypedArrayList(ExploreTopicsModel.CREATOR);


    }


 /*   public ExtraData getExtraData() {
        return extraData;
    }

    public void setExtraData(ExtraData extraData) {
        this.extraData = extraData;
    }*/

    public List<ExtraData> getExtraData() {
        return extraData;
    }

    public void setExtraData(List<ExtraData> extraData) {
        this.extraData = extraData;
    }

    public static final Creator<Topics> CREATOR = new Creator<Topics>() {
        @Override
        public Topics createFromParcel(Parcel in) {
            return new Topics(in);
        }

        @Override
        public Topics[] newArray(int size) {
            return new Topics[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getMapped_category() {
        return mapped_category;
    }

    public void setMapped_category(String mapped_category) {
        this.mapped_category = mapped_category;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIs_live() {
        return is_live;
    }

    public void setIs_live(String is_live) {
        this.is_live = is_live;
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
/*
    public ArrayList<ExploreTopicsModel> getExtraData() {
        return extraData;
    }

    public void setExtraData(ArrayList<ExploreTopicsModel> extraData) {
        this.extraData = extraData;
    }*/

    public void setChild(ArrayList<Topics> child) {
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

    public boolean isPrevKey() {
        return prevKey;
    }

    public void setPrevKey(boolean prevKey) {
        this.prevKey = prevKey;
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
        //  dest.writeParcelable(extraData, flags);

        dest.writeTypedList(extraData);
        dest.writeString(is_live);
        dest.writeByte((byte) (prevKey ? 1 : 0));
        dest.writeString(mapped_category);
        //  dest.writeTypedList(extraData);
    }
}