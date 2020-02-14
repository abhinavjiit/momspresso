package com.mycity4kids.models.request;

import com.google.gson.annotations.SerializedName;


public class ShortStoryConfigRequest {

    @SerializedName("created_by")
    private String created_by;
    @SerializedName("short_story_id")
    private String short_story_id;
    @SerializedName("font_size_title")
    private int font_size_title;
    @SerializedName("font_size_body")
    private int font_size_body;
    @SerializedName("font_alignment")
    private String font_alignment;
    @SerializedName("font_colour")
    private String font_colour;
    @SerializedName("coordinate_x")
    private float coordinate_x;
    @SerializedName("coordinate_y")
    private float coordinate_y;
    @SerializedName("user_id")
    private String user_id;
    @SerializedName("category_image")
    private int category_image;

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getShort_story_id() {
        return short_story_id;
    }

    public void setShort_story_id(String short_story_id) {
        this.short_story_id = short_story_id;
    }

    public int getFont_size_title() {
        return font_size_title;
    }

    public void setFont_size_title(int font_size_title) {
        this.font_size_title = font_size_title;
    }

    public int getFont_size_body() {
        return font_size_body;
    }

    public void setFont_size_body(int font_size_body) {
        this.font_size_body = font_size_body;
    }

    public String getFont_alignment() {
        return font_alignment;
    }

    public void setFont_alignment(String font_alignment) {
        this.font_alignment = font_alignment;
    }

    public String getFont_colour() {
        return font_colour;
    }

    public void setFont_colour(String font_colour) {
        this.font_colour = font_colour;
    }

    public float getCoordinate_x() {
        return coordinate_x;
    }

    public void setCoordinate_x(float coordinate_x) {
        this.coordinate_x = coordinate_x;
    }

    public float getCoordinate_y() {
        return coordinate_y;
    }

    public void setCoordinate_y(float coordinate_y) {
        this.coordinate_y = coordinate_y;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getCategory_image() {
        return category_image;
    }

    public void setCategory_image(int category_image) {
        this.category_image = category_image;
    }
}
