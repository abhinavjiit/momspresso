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

    /**
     * Push an "openScreen" event with the given screen name. Tags that match that event will fire.
     */
    public static void pushOpenScreenEvent(Context context, String screenName, String user) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf("event", "openScreen",        // Event, Name of Open Screen Event.
                "screenName", screenName, GTMTags.USER_ID, user));  // Name of screen name field, Screen name value.
    }

    /**
     * Push a "closeScreen" event with the given screen name. Tags that match that event will fire.
     */
    public static void pushCloseScreenEvent(Context context, String screenName) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.pushEvent("screenOpen", DataLayer.mapOf("screenName", screenName));
    }

    public static void pushEvent(Context context, GTMEventType event, String user, String eventValue) {
        Log.d("GTMTopic", "" + event + "---" + user + "---" + eventValue);
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf("event", event, GTMTags.USER_ID, user, GTMTags.ScreenName, eventValue));

    }

    public static void pushEventShareURL(Context context, GTMEventType event, String user, String ScreenName, String eventValue) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf("event", event, GTMTags.USER_ID, user, GTMTags.ScreenName, ScreenName, GTMTags.HandledUrl, eventValue));
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
