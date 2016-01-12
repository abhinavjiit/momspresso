package com.chatPlatform.models;

import com.couchbase.lite.Document;

/**
 * Created by anshul on 22/12/15.
 */
public class Group {

        Document groupDoc;
        Document chatDoc;
        int unreadMsgCount;
    String groupDescription;
    String groupName;
    int groupSize;

    public int getGroupSize() {
        return groupSize;
    }

    public void setGroupSize(int groupSize) {
        this.groupSize = groupSize;
    }

    public Document getChatDoc() {
            return chatDoc;
        }
    public void setChatDoc(Document chatDoc) {
            this.chatDoc = chatDoc;
        }
    public Document getGroupDoc() {
            return groupDoc;
        }
    public void setGroupDoc(Document groupDoc) {
            this.groupDoc = groupDoc;
        }
    public int getUnreadMsgCount() {
            return unreadMsgCount;
        }
    public void setUnreadMsgCount(int unreadMsgCount) {
            this.unreadMsgCount = unreadMsgCount;
        }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}

