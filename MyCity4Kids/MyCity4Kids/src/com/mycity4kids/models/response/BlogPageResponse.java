package com.mycity4kids.models.response;

/**
 * Created by anshul on 7/11/16.
 */
public class BlogPageResponse extends BaseResponse{
    public BlogPageData getData() {
        return data;
    }

    public void setData(BlogPageData data) {
        this.data = data;
    }

    BlogPageData data;
}
