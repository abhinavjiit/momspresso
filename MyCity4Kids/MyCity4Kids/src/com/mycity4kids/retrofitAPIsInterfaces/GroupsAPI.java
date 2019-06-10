package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.request.AddGpPostCommentOrReplyRequest;
import com.mycity4kids.models.request.AddGroupPostRequest;
import com.mycity4kids.models.request.CreateUpdateGroupRequest;
import com.mycity4kids.models.request.DeleteGpPostCommentOrReplyRequest;
import com.mycity4kids.models.request.EditGpPostCommentOrReplyRequest;
import com.mycity4kids.models.request.GroupActionsPatchRequest;
import com.mycity4kids.models.request.GroupActionsRequest;
import com.mycity4kids.models.request.GroupCommentActionsRequest;
import com.mycity4kids.models.request.GroupNotificationToggleRequest;
import com.mycity4kids.models.request.GroupReportContentRequest;
import com.mycity4kids.models.request.GroupsCategoryUpdateRequest;
import com.mycity4kids.models.request.JoinGroupRequest;
import com.mycity4kids.models.request.ReportedContentModerationRequest;
import com.mycity4kids.models.request.UpdateGroupMemberRoleRequest;
import com.mycity4kids.models.request.UpdateGroupMembershipRequest;
import com.mycity4kids.models.request.UpdateGroupPostRequest;
import com.mycity4kids.models.request.UpdatePostContentRequest;
import com.mycity4kids.models.request.UpdatePostSettingsRequest;
import com.mycity4kids.models.request.UpdateUserPostSettingsRequest;
import com.mycity4kids.models.request.UpdateUsersGpLevelNotificationSettingRequest;
import com.mycity4kids.models.response.AddGpPostCommentReplyResponse;
import com.mycity4kids.models.response.AddGroupPostResponse;
import com.mycity4kids.models.response.GroupDetailResponse;
import com.mycity4kids.models.response.GroupIdCategoryIdMappingResponse;
import com.mycity4kids.models.response.GroupPostCommentResponse;
import com.mycity4kids.models.response.GroupPostResponse;
import com.mycity4kids.models.response.GroupsActionResponse;
import com.mycity4kids.models.response.GroupsActionVoteResponse;
import com.mycity4kids.models.response.GroupsCategoryMappingResponse;
import com.mycity4kids.models.response.GroupsJoinResponse;
import com.mycity4kids.models.response.GroupsListingResponse;
import com.mycity4kids.models.response.GroupsMembershipResponse;
import com.mycity4kids.models.response.GroupsReportContentResponse;
import com.mycity4kids.models.response.GroupsReportedContentResponse;
import com.mycity4kids.models.response.GroupsSettingResponse;
import com.mycity4kids.models.response.SetupBlogResponse;
import com.mycity4kids.models.response.UserPostSettingResponse;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by hemant on 12/4/18.
 */

public interface GroupsAPI {

    // Basic Group Functionalities
    @GET("/api/v1/groups/group")
    Call<GroupsListingResponse> getGroupList(@Query("$skip") int skip,
                                             @Query("$limit") int limit);

    @GET("/api/v1/groups/members")
    Call<GroupsMembershipResponse> getJoinedGroupList(@Query("userId") String userId,
                                                      @Query("status") String status,
                                                      @Query("$skip") int skip,
                                                      @Query("$limit") int limit);

    @GET("/api/v1/groups/members")
    Call<GroupsMembershipResponse> getTop4JoinedGroupList(@Query("userId") String userId,
                                                          @Query("status") String status);

    @GET("/api/v1/groups/group")
    Call<GroupsListingResponse> getTop4SuggestedGroupsSingleExclusion(@Query("id[$ne]") String gp0);

    @GET("/api/v1/groups/group")
    Call<GroupsListingResponse> getTop4SuggestedGroups(@Query("id[$notIn]") List<String> groupIdList);

    @GET("/api/v1/groups/group?{params}")
    Call<GroupsListingResponse> getTop4SuggestedGroupss(@Path("params") String param);

    @POST("/api/v1/groups/group")
    Call<SetupBlogResponse> createGroup();

    @GET("/api/v1/groups/group/{groupId}")
    Call<GroupDetailResponse> getGroupById(@Path("groupId") int groupId);

    @PATCH("/api/v1/groups/group/{groupId}")
    Call<GroupDetailResponse> updateGroup(@Path("groupId") int groupId,
                                          @Body CreateUpdateGroupRequest body);

    @PATCH("/api/v1/groups/group/{groupId}")
    Call<GroupDetailResponse> updateGroupNotification(@Path("groupId") int groupId,
                                                      @Body GroupNotificationToggleRequest body);

    //Group Settings Functionalities
    @GET("/api/v1/groups/settings/")
    Call<GroupsSettingResponse> getGroupsAllSetting(@Query("groupId") String groupId);

    @GET("/api/v1/groups/settings/{settingsId}")
    Call<GroupsListingResponse> getSingleGroupSettings(@Path("settingsId") String settingsId);

    @POST("/api/v1/groups/settings/")
    Call<GroupsListingResponse> createGroupSettings();

    @PUT("/api/v1/groups/settings/")
    Call<GroupsListingResponse> updateGroupSettings();


    //Group Posts
    @GET("/api/v1/groups/post")
    Call<GroupPostResponse> getAllPostsForAGroup(@Query("groupId") int groupId,
                                                 @Query("$skip") int skip,
                                                 @Query("$limit") int limit);

    @GET("/api/v1/groups/post")
    Call<GroupPostResponse> getAllFilteredPostsForAGroup(@Query("groupId") int groupId,
                                                         @Query("$skip") int skip,
                                                         @Query("$limit") int limit,
                                                         @Query("type") String type);

    @GET("/api/v1/groups/post")
    Call<GroupPostResponse> getAllMyFeedPosts(@Query("$skip") int skip,
                                              @Query("$myFeed") int myFeed,
                                              @Query("$limit") int limit);
//    @Query("$myFeed") int myFeed,

    @GET("/api/v1/groups/post")
    Call<GroupPostResponse> getAllPollPosts(@Query("$skip") int skip,
                                            @Query("$limit") int limit,
                                            @Query("type") String type);

    @GET("/api/v1/groups/post/{postId}")
    Call<GroupPostResponse> getSinglePost(@Path("postId") int postId);

    @POST("/api/v1/groups/post")
    Call<AddGroupPostResponse> createPost(@Body AddGroupPostRequest addGroupPostRequest);

    @PATCH("/api/v1/groups/post/{postId}")
    Call<GroupPostResponse> updatePost(@Path("postId") int postId,
                                       @Body UpdatePostSettingsRequest updatePostSettingsRequest);

    @PATCH("/api/v1/groups/post/{postId}")
    Call<GroupPostResponse> updatePostContent(@Path("postId") int postId,
                                              @Body UpdatePostContentRequest updatePostContentRequest);

    @PATCH("/api/v1/groups/post/{postId}")
    Call<GroupPostResponse> disablePostComment(@Path("postId") int postId,
                                               @Body UpdateGroupPostRequest updateGroupPostRequest);

    //Group Members
    @GET("/api/v1/groups/members")
    Call<GroupsListingResponse> getAllMembers();


    @GET("/api/v1/groups/members")
    Call<GroupsMembershipResponse> getGroupMembersByStatus(@Query("groupId") int groupId,
                                                           @Query("status") String status,
                                                           @Query("$skip") int skip,
                                                           @Query("$limit") int limit);

    @GET("/api/v1/groups/members")
    Call<GroupsMembershipResponse> getUsersMembershipDetailsForGroup(@Query("groupId") int groupId,
                                                                     @Query("userId") String userId);

    @GET("/api/v1/groups/members/{membershipId}")
    Call<GroupsListingResponse> getSingleMember(@Path("membershipId") String membershipId);

    @POST("/api/v1/groups/members")
    Call<GroupsJoinResponse> createMember(@Body JoinGroupRequest joinGroupRequest);

    @PATCH("/api/v1/groups/members/{memberId}")
    Call<GroupsMembershipResponse> updateMember(@Path("memberId") int memberId,
                                                @Body UpdateGroupMembershipRequest updateGroupMembershipRequest);

    @PATCH("/api/v1/groups/members/{memberId}")
    Call<GroupsMembershipResponse> updateMemberRole(@Path("memberId") int memberId,
                                                    @Body UpdateGroupMemberRoleRequest updateGroupMemberRoleRequest);

    //User Settings
    @GET("/api/v1/groups/usersettings")
    Call<UserPostSettingResponse> getPostSettingForUser(@Query("postId") int postId);

    @GET("/api/v1/groups/usersettings")
    Call<UserPostSettingResponse> getGroupNotificationSettingForUser(@Query("groupId") int groupId,
                                                                     @Query("postId") int postId,
                                                                     @Query("userId") String userId);

    @POST("/api/v1/groups/usersettings")
    Call<ResponseBody> createNewPostSettingsForUser(@Body UpdateUserPostSettingsRequest joinGroupRequest);

    @POST("/api/v1/groups/usersettings")
    Call<ResponseBody> createNewGpSettingsForUser(@Body UpdateUsersGpLevelNotificationSettingRequest notificationSettingRequest);

    @PATCH("/api/v1/groups/usersettings/{userSettingId}")
    Call<UserPostSettingResponse> updatePostSettingsForUser(@Path("userSettingId") int userSettingId,
                                                            @Body UpdateUserPostSettingsRequest joinGroupRequest);

    @PATCH("/api/v1/groups/usersettings/{userSettingId}")
    Call<UserPostSettingResponse> updateNotificationSettingsOfGpForUser(@Path("userSettingId") int userSettingId,
                                                                        @Body UpdateUsersGpLevelNotificationSettingRequest joinGroupRequest);

    //Post Comments
    @GET("/api/v1/groups/responsenested")
    Call<GroupPostCommentResponse> getPostComments(@Query("groupId") int groupId,
                                                   @Query("postId") int postId,
                                                   @Query("$skip") int skip,
                                                   @Query("$limit") int limit);

    @GET("/api/v1/groups/responsenested")
    Call<GroupPostCommentResponse> getSinglePostComments(@Query("groupId") int groupId,
                                                         @Query("postId") int postId,
                                                         @Query("id") int responseId);

    @GET("/api/v1/groups/response")
    Call<GroupPostCommentResponse> getPostCommentReplies(@Query("groupId") int groupId,
                                                         @Query("postId") int postId,
                                                         @Query("parentId") int parentId,
                                                         @Query("$skip") int skip,
                                                         @Query("$limit") int limit);

    @POST("/api/v1/groups/response")
    Call<AddGpPostCommentReplyResponse> addPostCommentOrReply(@Body AddGpPostCommentOrReplyRequest addGpPostCommentOrReplyRequest);

    @PATCH("/api/v1/groups/response/{responseId}")
    Call<AddGpPostCommentReplyResponse> editPostCommentOrReply(@Path("responseId") int responseId,
                                                               @Body EditGpPostCommentOrReplyRequest editGpPostCommentOrReplyRequest);

    @PATCH("/api/v1/groups/response/{responseId}")
    Call<AddGpPostCommentReplyResponse> deleteCommentOrReply(@Path("responseId") int responseId,
                                                             @Body DeleteGpPostCommentOrReplyRequest deleteGpPostCommentOrReplyRequest);
//    @PUT("/api/v1/groups/usersettings/{userSettingId}")
//    Call<UserPostSettingResponse> updatePostSettingsForUser(@Path("userSettingId") int userSettingId,
//                                                            @Body UpdateUserPostSettingsRequest joinGroupRequest);


    //Group Action Items
    @POST("/api/v1/groups/action")
    Call<GroupsActionResponse> addAction(@Body GroupActionsRequest groupActionsRequest);

    //Group Action Items
    @POST("/api/v1/groups/action")
    Call<GroupsActionResponse> addCommentAction(@Body GroupCommentActionsRequest groupActionsRequest);

    @POST("/api/v1/groups/action-vote")
    Call<GroupsActionVoteResponse> addActionVote(@Body GroupActionsRequest groupActionsRequest);

    @PATCH("/api/v1/groups/action/{actionId}")
    Call<GroupsActionResponse> patchAction(@Path("actionId") int actionId,
                                           @Body GroupActionsPatchRequest groupActionsRequest);

    //Groups Category Mapping
    @GET("/api/v1/groups/categorygroupmap/")
    Call<GroupsCategoryMappingResponse> getGroupCategories(@Query("groupId") int groupId);

    @POST("/api/v1/groups/categorygroupmap")
    Call<GroupsCategoryMappingResponse> addGroupCategory(@Body ArrayList<GroupsCategoryUpdateRequest> groupsCategoryUpdateRequest);

    @DELETE("/api/v1/groups/categorygroupmap/")
    Call<ResponseBody> removeGroupCategory(@Query("groupId") int groupId);

    //Report Content
    @GET("/api/v1/groups/moderation-view/")
    Call<GroupsReportedContentResponse> getReportedContent(@Query("groupId") int groupId,
                                                           @Query("$skip") int skip,
                                                           @Query("$limit") int limit);

    @POST("/api/v1/groups/report")
    Call<GroupsReportContentResponse> reportContent(@Body GroupReportContentRequest groupReportContentRequest);

    @PATCH("/api/v1/groups/moderation-view/{contentId}")
    Call<ResponseBody> moderateReportedContent(@Path("contentId") int contentId,
                                               @Body ReportedContentModerationRequest reportedContentModerationRequest);

    //Groups Search
    @GET("/api/v1/groups/search/")
    Call<GroupPostResponse> searchWithinGroup(@Query("q") String query,
                                              @Query("ofType") String ofType,
                                              @Query("isActive") int isActive,
                                              @Query("groupId") int groupId,
                                              @Query("$skip") int skip,
                                              @Query("$limit") int limit);

    @GET("/api/v1/groups/search/")
    Call<GroupsListingResponse> searchGroups(@Query("q") String query,
                                             @Query("ofType") String ofType,
                                             @Query("isActive") int isActive,
                                             @Query("$skip") int skip,
                                             @Query("$limit") int limit);

    @GET("/api/v1/groups/groups-banner/")
    Call<GroupIdCategoryIdMappingResponse> getGroupIdForSingleCategory(@Query("platform") String query,
                                                                       @Query("categoryId") String ofType,
                                                                       @Query("position") String position);

    @GET("/api/v1/groups/groups-banner/")
    Call<GroupIdCategoryIdMappingResponse> getGroupIdForMultipleCategories(@Query("platform") String query,
                                                                           @Query("categoryId[$in]") List<String> groupIdList,
                                                                           @Query("position") String position);
}
