package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.request.AddGpPostCommentOrReplyRequest;
import com.mycity4kids.models.request.AddGroupPostRequest;
import com.mycity4kids.models.request.CreateUpdateGroupRequest;
import com.mycity4kids.models.request.GroupActionsPatchRequest;
import com.mycity4kids.models.request.GroupActionsRequest;
import com.mycity4kids.models.request.GroupReportContentRequest;
import com.mycity4kids.models.request.GroupsCategoryUpdateRequest;
import com.mycity4kids.models.request.JoinGroupRequest;
import com.mycity4kids.models.request.ReportedContentModerationRequest;
import com.mycity4kids.models.request.UpdateGroupMembershipRequest;
import com.mycity4kids.models.request.UpdateGroupPostRequest;
import com.mycity4kids.models.request.UpdatePostSettingsRequest;
import com.mycity4kids.models.request.UpdateUserPostSettingsRequest;
import com.mycity4kids.models.response.AddGpPostCommentReplyResponse;
import com.mycity4kids.models.response.AddGroupPostResponse;
import com.mycity4kids.models.response.GroupDetailResponse;
import com.mycity4kids.models.response.GroupPostCommentResponse;
import com.mycity4kids.models.response.GroupPostResponse;
import com.mycity4kids.models.response.GroupsActionResponse;
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

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
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

    @GET("/api/v1/groups/grouplisting")
    Call<GroupsListingResponse> getJoinedGroupList(@Query("$skip") int skip,
                                                   @Query("$limit") int limit);

    @POST("/api/v1/groups/group")
    Call<SetupBlogResponse> createGroup();

    @GET("/api/v1/groups/group/{groupId}")
    Call<GroupDetailResponse> getGroupById(@Path("groupId") int groupId);

    @PATCH("/api/v1/groups/group/{groupId}")
    Call<GroupDetailResponse> updateGroup(@Path("groupId") int groupId,
                                          @Body CreateUpdateGroupRequest body);


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

    @GET("/api/v1/groups/post/{postId}")
    Call<GroupPostResponse> getSinglePost(@Path("postId") int postId);

    @POST("/api/v1/groups/post")
    Call<AddGroupPostResponse> createPost(@Body AddGroupPostRequest addGroupPostRequest);

    @PATCH("/api/v1/groups/post/{postId}")
    Call<GroupPostResponse> updatePost(@Path("postId") int postId,
                                       @Body UpdatePostSettingsRequest updatePostSettingsRequest);

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


    //User Settings
    @GET("/api/v1/groups/usersettings")
    Call<UserPostSettingResponse> getPostSettingForUser(@Query("postId") int postId);

    @POST("/api/v1/groups/usersettings")
    Call<UserPostSettingResponse> createNewPostSettingsForUser(@Body UpdateUserPostSettingsRequest joinGroupRequest);

    @PATCH("/api/v1/groups/usersettings/{userSettingId}")
    Call<UserPostSettingResponse> updatePostSettingsForUser(@Path("userSettingId") int userSettingId,
                                                            @Body UpdateUserPostSettingsRequest joinGroupRequest);

    //Post Comments
    @GET("/api/v1/groups/responsenested")
    Call<GroupPostCommentResponse> getPostComments(@Query("groupId") int groupId,
                                                   @Query("postId") int postId,
                                                   @Query("$skip") int skip,
                                                   @Query("$limit") int limit);

    @GET("/api/v1/groups/response")
    Call<GroupPostCommentResponse> getPostCommentReplies(@Query("groupId") int groupId,
                                                         @Query("postId") int postId,
                                                         @Query("parentId") int parentId,
                                                         @Query("$skip") int skip,
                                                         @Query("$limit") int limit);

    @POST("/api/v1/groups/response")
    Call<AddGpPostCommentReplyResponse> addPostCommentOrReply(@Body AddGpPostCommentOrReplyRequest addGpPostCommentOrReplyRequest);

//    @PUT("/api/v1/groups/usersettings/{userSettingId}")
//    Call<UserPostSettingResponse> updatePostSettingsForUser(@Path("userSettingId") int userSettingId,
//                                                            @Body UpdateUserPostSettingsRequest joinGroupRequest);


    //Group Action Items
    @POST("/api/v1/groups/action")
    Call<GroupsActionResponse> addAction(@Body GroupActionsRequest groupActionsRequest);

    @PATCH("/api/v1/groups/action/{actionId}")
    Call<GroupsActionResponse> patchAction(@Path("actionId") int actionId,
                                           @Body GroupActionsPatchRequest groupActionsRequest);

    //Groups Category Mapping
    @GET("/api/v1/groups/categorygroupmap/")
    Call<GroupsCategoryMappingResponse> getGroupCategories(@Query("groupId") int groupId);

    @POST("/api/v1/groups/categorygroupmap")
    Call<GroupsCategoryMappingResponse> addGroupCategory(@Body ArrayList<GroupsCategoryUpdateRequest> groupsCategoryUpdateRequest);

    //Report Content
    @GET("/api/v1/groups/moderation-view/")
    Call<GroupsReportedContentResponse> getReportedContent();

    @POST("/api/v1/groups/categorygroupmap")
    Call<GroupsReportContentResponse> reportContent(@Body GroupReportContentRequest groupReportContentRequest);

    @POST("/api/v1/groups/moderation-view/{contentId}")
    Call<GroupsReportedContentResponse> moderateReportedContent(@Path("contentId") int contentId,
                                                                @Body ReportedContentModerationRequest reportedContentModerationRequest);
}
