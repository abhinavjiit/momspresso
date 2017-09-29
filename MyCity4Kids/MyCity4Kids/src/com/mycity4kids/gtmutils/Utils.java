package com.mycity4kids.gtmutils;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.tagmanager.DataLayer;
import com.google.android.gms.tagmanager.TagManager;

/**
 * Created by anshul on 2/11/16.
 */
public class Utils {
    private Utils() {
        // private constructor.
    }

    public static void pushAppOpenEvent(Context context, String user) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "AppLaunch", GTMTags.USER_ID, user));
    }

    /**
     * Push an "openScreen" event with the given screen name. Tags that match that event will fire.
     */
    public static void pushOpenScreenEvent(Context context, String screenName, String user) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "AppScreenOpened", GTMTags.TagScreen, screenName, GTMTags.USER_ID, user));
    }

    public static void pushTopMenuClickEvent(Context context, String screenName, String user) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "MenuClick", GTMTags.TagScreen, screenName, GTMTags.USER_ID, user));
    }

    public static void pushViewArticleEvent(Context context, String screenName, String user, String articleId, String listingType, String index, String author) {
        Log.d("ArticleArticle", "" + screenName + " --- " + listingType + " --- " + author);
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "ArticleClick", GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.ArticleId, articleId,
                GTMTags.TagListingType, listingType, GTMTags.TagIndex, index, GTMTags.Author, author));
    }

    public static void pushViewTopicArticlesEvent(Context context, String screenName, String user, String topic) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "ViewTopicArticles", GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.TagTopic, topic));
    }

    public static void pushFilterTopicArticlesEvent(Context context, String screenName, String user, String topic, String parentTopic) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "FilterTopicArticles", GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.TagTopic, topic, GTMTags.TagParentTopic, parentTopic));
    }

    public static void pushViewQuickLinkArticlesEvent(Context context, String screenName, String user, String linkType) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "ViewQuicklinkArticles", GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.TagLinkType, linkType));
    }

    public static void pushArticleLoadedEvent(Context context, String screenName, String user, String articleId, String author, String language) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "ArticleDetailLoaded",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.ArticleId, articleId, GTMTags.Author, author, GTMTags.TagLanguage, language));
    }

    public static void pushArticleSwipeEvent(Context context, String screenName, String user, String articleId, String fromindex, String toIndex) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "ArticleSwiped",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.ArticleId, articleId, GTMTags.TagFromIndex, fromindex, GTMTags.TagIndex, toIndex));
    }

    public static void pushPlayArticleAudioEvent(Context context, String screenName, String user, String articleId, String language, String author) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "PlayArticleAudio",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.ArticleId, articleId, GTMTags.TagLanguage, language, GTMTags.Author, author));
    }

    public static void pushStopArticleAudioEvent(Context context, String screenName, String user, String articleId, String language, String author, String duration) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "StopArticleAudio",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.ArticleId, articleId, GTMTags.TagLanguage, language, GTMTags.Author, author, GTMTags.TagDuration, duration));
    }

    public static void pushShareArticleEvent(Context context, String screenName, String user, String articleId, String shareMedium, String author) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "ShareArticle",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.ArticleId, articleId, GTMTags.TagShareMedium, shareMedium, GTMTags.Author, author));
    }

    public static void pushBookmarkArticleEvent(Context context, String screenName, String user, String articleId, String author) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "BookmarkArticle",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.ArticleId, articleId, GTMTags.Author, author));
    }

    public static void pushUnbookmarkArticleEvent(Context context, String screenName, String user, String articleId, String author) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "UnbookmarkArticle",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.ArticleId, articleId, GTMTags.Author, author));
    }

    public static void pushLikekArticleEvent(Context context, String screenName, String user, String articleId, String author) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "LikeArticle",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.ArticleId, articleId, GTMTags.Author, author));
    }

    public static void pushWatchLaterArticleEvent(Context context, String screenName, String user, String articleId, String author) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "WatchLater",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.ArticleId, articleId, GTMTags.Author, author));
    }

    public static void pushRemoveWatchLaterArticleEvent(Context context, String screenName, String user, String articleId, String author) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "RemoveWatchLater",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.ArticleId, articleId, GTMTags.Author, author));
    }

    public static void pushCommentArticleEvent(Context context, String screenName, String user, String articleId, String author) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "CommentOnArticle",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.ArticleId, articleId, GTMTags.Author, author));
    }

    public static void pushReplyCommentArticleEvent(Context context, String screenName, String user, String articleId, String author) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "ReplyToComment",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.ArticleId, articleId, GTMTags.Author, author));
    }

    public static void pushFollowAuthorEvent(Context context, String screenName, String user, String articleId, String author) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "FollowAuthor",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.ArticleId, articleId, GTMTags.Author, author));
    }

    public static void pushUnfollowAuthorEvent(Context context, String screenName, String user, String articleId, String author) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "UnfollowAuthor",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.ArticleId, articleId, GTMTags.Author, author));
    }


    public static void pushEvent(Context context, GTMEventType event, String user, String eventValue) {
        Log.d("GTMTopic", "" + event + "---" + user + "---" + eventValue);
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf("event", event, GTMTags.USER_ID, user, GTMTags.ScreenName, eventValue));
    }

    public static void pushEventTopicChoose(Context context, GTMEventType event, String user, String ScreenName, String eventValue) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf("event", event, GTMTags.USER_ID, user, GTMTags.ScreenName, ScreenName, GTMTags.TopicChosen, eventValue));

    }

    public static void pushEventRelatedArticle(Context context, GTMEventType event, String user, String ScreenName, String eventValue, int position) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf("event", event, GTMTags.USER_ID, user, GTMTags.ScreenName, ScreenName, GTMTags.HandledUrl, eventValue, GTMTags.INDEX, position));
    }

    public static void pushEventFollowUnfollowTopic(Context context, GTMEventType event, String user, String ScreenName, String topicAction, String categoryNameIdCombination) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf("event", event, GTMTags.USER_ID, user, GTMTags.ScreenName, ScreenName, GTMTags.TopicAction, topicAction, GTMTags.TopicChosen, categoryNameIdCombination));
    }

    public static void pushArticleDetailsTimeSpent(Context context, GTMEventType event, String user, String ScreenName, String titleSlug, String timeSpent, String estimatedTime) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf("event", event, GTMTags.USER_ID, user, GTMTags.ScreenName, ScreenName, GTMTags.HandledUrl, titleSlug, GTMTags.TimeSpent, timeSpent, GTMTags.EstimatedTime, estimatedTime));
    }

    public static void pushEventSubscriptionSettings(Context context, GTMEventType event, String user, String ScreenName, String subscriptionType) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf("event", event, GTMTags.USER_ID, user, GTMTags.ScreenName, ScreenName, GTMTags.Type, subscriptionType));
    }

    public static void pushEventFeedLanguage(Context context, GTMEventType event, String user, String ScreenName, String language) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf("event", event, GTMTags.USER_ID, user, GTMTags.ScreenName, ScreenName, GTMTags.FeedLanguage, language));
    }

    public static void pushEventNotificationClick(Context context, GTMEventType event, String user, String screenName, String notificationType) {
        Log.d("GTMNotification", "" + event + "-" + user + "-" + screenName + "-" + notificationType);
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf("event", event, GTMTags.USER_ID, user, GTMTags.ScreenName, screenName, GTMTags.Type, notificationType));
    }

    public static void pushOpenArticleEvent(Context context, GTMEventType event, String screenName, String user, String articleId, String index, String from) {
        Log.d("GTMOpenArticle", "" + event + "---" + screenName + "---" + user + "---" + articleId + "---" + index + "---" + from);
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf("event", event, GTMTags.USER_ID, user, GTMTags.ScreenName, screenName, GTMTags.ArticleId, articleId, GTMTags.INDEX, index, GTMTags.LaunchedFrom, from));
    }

    public static void pushOpenArticleListingEvent(Context context, GTMEventType event, String screenName, String user, String listingName, String index) {
        Log.d("GTMListing", "" + event + "---" + screenName + "---" + user + "---" + listingName + "---" + index);
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf("event", event, GTMTags.ScreenName, screenName, GTMTags.USER_ID, user, GTMTags.Type, listingName, GTMTags.INDEX, index));
    }

    public static void pushAuthorFollowUnfollowEvent(Context context, GTMEventType event, String screenName, String user, String articleId, String author) {
        Log.d("GTMAuthor", "" + event + "---" + screenName + "---" + user + "---" + articleId + "---" + author);
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf("event", event, GTMTags.ScreenName, screenName, GTMTags.USER_ID, user, GTMTags.ArticleId, articleId, GTMTags.Author, author));
    }

    public static void pushArticleLikeUnlikeEvent(Context context, GTMEventType event, String screenName, String user, String articleId, String author) {
        Log.d("GTMLike", "" + event + "---" + screenName + "---" + user + "---" + articleId + "---" + author);
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf("event", event, GTMTags.ScreenName, screenName, GTMTags.USER_ID, user, GTMTags.ArticleId, articleId, GTMTags.Author, author));
    }

    public static void pushArticleBookmarkUnbookmarkEvent(Context context, GTMEventType event, String screenName, String user, String articleId, String author) {
        Log.d("GTMBookmark", "" + event + "---" + screenName + "---" + user + "---" + articleId + "---" + author);
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf("event", event, GTMTags.ScreenName, screenName, GTMTags.USER_ID, user, GTMTags.ArticleId, articleId, GTMTags.Author, author));
    }

    public static void pushArticleShareEvent(Context context, GTMEventType event, String user, String screenName, String shareUrl, String author, String type) {
        Log.d("GTMShare", "" + event + "---" + screenName + "---" + user + "--- " + shareUrl + " ---" + author + "---" + type);
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf("event", event, GTMTags.USER_ID, user, GTMTags.ScreenName, screenName, GTMTags.HandledUrl, shareUrl, GTMTags.Author, author, GTMTags.Type, type));
    }

    public static void pushTopicFollowUnfollowEvent(Context context, GTMEventType event, String screenName, String user, String categoryNameIdCombination) {
        Log.d("GTMTopic", "" + event + "---" + screenName + "---" + user + "---" + categoryNameIdCombination);
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf("event", event, GTMTags.ScreenName, screenName, GTMTags.USER_ID, user, GTMTags.TopicChosen, categoryNameIdCombination));
    }

    public static void pushBloggerDetailsOpenedEvent(Context context, GTMEventType event, String user, String screenName, String author) {
        Log.d("GTMBlogger", "" + event + "---" + user + "---" + screenName + "---" + author);
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf("event", event, GTMTags.USER_ID, user, GTMTags.ScreenName, screenName, GTMTags.Author, author));
    }

    public static void pushSortListingEvent(Context context, GTMEventType event, String user, String screenName, String sortType) {
        Log.d("GTMSort", "" + event + "---" + user + "---" + screenName + "---" + sortType);
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf("event", event, GTMTags.USER_ID, user, GTMTags.ScreenName, screenName, GTMTags.Type, sortType));
    }
}
