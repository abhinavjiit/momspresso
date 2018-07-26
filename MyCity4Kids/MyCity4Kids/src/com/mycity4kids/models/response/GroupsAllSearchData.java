package com.mycity4kids.models.response;

import java.util.List;

public class GroupsAllSearchData {

    public class GroupsAllSearchResult {
        private List<GroupResult> group;
        private List<GroupPostResult> post;
        private List<GroupPostCommentResult> response;

        public List<GroupResult> getGroup() {
            return group;
        }

        public void setGroup(List<GroupResult> group) {
            this.group = group;
        }

        public List<GroupPostResult> getPost() {
            return post;
        }

        public void setPost(List<GroupPostResult> post) {
            this.post = post;
        }

        public List<GroupPostCommentResult> getResponse() {
            return response;
        }

        public void setResponse(List<GroupPostCommentResult> response) {
            this.response = response;
        }
    }


}