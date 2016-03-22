package com.mycity4kids.models.editor;

import com.mycity4kids.models.CommonMessage;

/**
 * Created by anshul on 3/20/16.
 */
public class BlogDataResult extends CommonMessage {
    private BlogData data;

    public BlogData getData() {
        return data;
    }

    public void setData(BlogData data) {
        this.data = data;
    }
}
