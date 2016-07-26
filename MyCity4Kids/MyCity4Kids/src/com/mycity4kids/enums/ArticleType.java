package com.mycity4kids.enums;

/**
 * Created by anshul on 7/13/16.
 */
public enum  ArticleType {
    DRAFT(0),UNDER_MODERATION(1), UNAPPROVED(2),UNPUBLISHED(3),PUBLISHED(4);
    private final int type;
    ArticleType(int id) { this.type = id; }
    public int getValue() { return type; }
}
