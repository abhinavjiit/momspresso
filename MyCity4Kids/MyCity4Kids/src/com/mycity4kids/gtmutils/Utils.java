package com.mycity4kids.gtmutils;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tagmanager.DataLayer;
import com.google.android.gms.tagmanager.TagManager;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.preference.SharedPrefUtils;

import org.json.JSONObject;

/**
 * Created by anshul on 2/11/16.
 */
public class Utils {

    private Utils() {
        // private constructor.G
    }

    private static final String BADGE_NAME = "Badge name";

    public static void initialLanguageSelection(Context context, String currentScreen, String source, String CTA, String platform, String lang, String userId, String timestamp, String event) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, event, GTMTags.Current_Screen, currentScreen, GTMTags.Source, source, GTMTags.CTA, CTA, GTMTags.Platform, platform, GTMTags.Language, lang, GTMTags.USER_ID, userId, GTMTags.Timestamp, timestamp));
        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(GTMTags.Current_Screen, currentScreen);
            jsonObject.put(GTMTags.Source, source);
            jsonObject.put(GTMTags.CTA, CTA);
            jsonObject.put(GTMTags.Platform, platform);
            jsonObject.put(GTMTags.Language, lang);
            jsonObject.put(GTMTags.USER_ID, userId);
            jsonObject.put(GTMTags.Timestamp, timestamp);
            mixpanel.track(event, jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString(GTMTags.Current_Screen, currentScreen);
        bundle.putString(GTMTags.Source, source);
        bundle.putString(GTMTags.CTA, CTA);
        bundle.putString(GTMTags.Platform, platform);
        bundle.putString(GTMTags.Language, lang);
        bundle.putString(GTMTags.USER_ID, userId);
        bundle.putString(GTMTags.Timestamp, timestamp);
        mFirebaseAnalytics.logEvent(event, bundle);
    }

    public static void campaignEvent(Context context, String currentScreen, String source, String CTA, String campaignName, String platform, String lang, String userId, String timestamp, String event) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, event, GTMTags.Current_Screen, currentScreen, GTMTags.Source, source, GTMTags.CTA, CTA, GTMTags.Campaign_Name, campaignName, GTMTags.Platform, platform, GTMTags.Language, lang, GTMTags.USER_ID, userId, GTMTags.Timestamp, timestamp));
        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(GTMTags.Current_Screen, currentScreen);
            jsonObject.put(GTMTags.Source, source);
            jsonObject.put(GTMTags.CTA, CTA);
            jsonObject.put(GTMTags.Campaign_Name, campaignName);
            jsonObject.put(GTMTags.Platform, platform);
            jsonObject.put(GTMTags.Language, lang);
            jsonObject.put(GTMTags.USER_ID, userId);
            jsonObject.put(GTMTags.Timestamp, timestamp);
            mixpanel.track(event, jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString(GTMTags.Current_Screen, currentScreen);
        bundle.putString(GTMTags.Source, source);
        bundle.putString(GTMTags.CTA, CTA);
        bundle.putString(GTMTags.Campaign_Name, campaignName);
        bundle.putString(GTMTags.Platform, platform);
        bundle.putString(GTMTags.Language, lang);
        bundle.putString(GTMTags.USER_ID, userId);
        bundle.putString(GTMTags.Timestamp, timestamp);
        mFirebaseAnalytics.logEvent(event, bundle);
    }

    public static void momVlogEvent(Context context, String currentScreen, String CTA, String videoId, String platform, String lang, String userId, String timestamp, String event, String categoryId, String challengeId) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, event, GTMTags.Current_Screen, currentScreen, GTMTags.videoId, videoId, GTMTags.CTA, CTA, GTMTags.challengeId, challengeId, GTMTags.Platform, platform, GTMTags.Language, lang, GTMTags.USER_ID, userId, GTMTags.Timestamp, timestamp, GTMTags.categoryId, categoryId));
        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(GTMTags.Current_Screen, currentScreen);
            jsonObject.put(GTMTags.videoId, videoId);
            jsonObject.put(GTMTags.CTA, CTA);
            jsonObject.put(GTMTags.challengeId, challengeId);
            jsonObject.put(GTMTags.Platform, platform);
            jsonObject.put(GTMTags.Language, lang);
            jsonObject.put(GTMTags.USER_ID, userId);
            jsonObject.put(GTMTags.Timestamp, timestamp);
            jsonObject.put(GTMTags.categoryId, categoryId);
            mixpanel.track(event, jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString(GTMTags.Current_Screen, currentScreen);
        bundle.putString(GTMTags.videoId, videoId);
        bundle.putString(GTMTags.CTA, CTA);
        bundle.putString(GTMTags.challengeId, challengeId);
        bundle.putString(GTMTags.Platform, platform);
        bundle.putString(GTMTags.Language, lang);
        bundle.putString(GTMTags.USER_ID, userId);
        bundle.putString(GTMTags.Timestamp, timestamp);
        bundle.putString(GTMTags.categoryId, categoryId);
        mFirebaseAnalytics.logEvent(event, bundle);

    }

    public static void groupsEvent(Context context, String currentScreen, String CTA, String platform, String lang, String userId, String timestamp, String event, String groupId, String postId) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, event, GTMTags.Current_Screen, currentScreen, GTMTags.CTA, CTA, GTMTags.GroupId, groupId, GTMTags.Platform, platform, GTMTags.Language, lang, GTMTags.USER_ID, userId, GTMTags.Timestamp, timestamp, GTMTags.PostId, postId));
        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(GTMTags.Current_Screen, currentScreen);
            jsonObject.put(GTMTags.CTA, CTA);
            jsonObject.put(GTMTags.GroupId, groupId);
            jsonObject.put(GTMTags.Platform, platform);
            jsonObject.put(GTMTags.Language, lang);
            jsonObject.put(GTMTags.USER_ID, userId);
            jsonObject.put(GTMTags.Timestamp, timestamp);
            jsonObject.put(GTMTags.PostId, postId);
            mixpanel.track(event, jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString(GTMTags.Current_Screen, currentScreen);
        bundle.putString(GTMTags.CTA, CTA);
        bundle.putString(GTMTags.GroupId, groupId);
        bundle.putString(GTMTags.Platform, platform);
        bundle.putString(GTMTags.Language, lang);
        bundle.putString(GTMTags.USER_ID, userId);
        bundle.putString(GTMTags.Timestamp, timestamp);
        bundle.putString(GTMTags.PostId, postId);
        mFirebaseAnalytics.logEvent(event, bundle);
    }

    public static void pushAppOpenEvent(Context context, String user) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "AppLaunch", GTMTags.USER_ID, user));

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString(GTMTags.USER_ID, user);
        mFirebaseAnalytics.logEvent("AppLaunch", bundle);
    }

    public static void timeSpending(Context context, String user) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.ScreenName, "ForYouTime", GTMTags.USER_ID, user));

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString(GTMTags.USER_ID, user);
        mFirebaseAnalytics.logEvent("ForYouTime", bundle);
    }

    /**
     * Push an "openScreen" event with the given screen name. Tags that match that event will fire.
     */
    public static void pushOpenScreenEvent(Context context, String screenName, String user) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "AppScreenOpened", GTMTags.TagScreen, screenName, GTMTags.USER_ID, user));

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString(GTMTags.USER_ID, user);
        bundle.putString(GTMTags.TagScreen, screenName);
        mFirebaseAnalytics.logEvent("AppScreenOpened", bundle);
    }

    public static void pushViewArticleEvent(Context context, String screenName, String user, String articleId, String listingType, String index, String author) {
        Log.d("pushViewArticleEvent", "" + screenName + " --- " + listingType + " --- " + author);
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "ArticleClick", GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.ArticleId, articleId,
                GTMTags.TagListingType, listingType, GTMTags.TagIndex, index, GTMTags.Author, author));

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user);
            jsonObject.put("screen", screenName);
            jsonObject.put("articleId", articleId);
            jsonObject.put("listingType", listingType);
            jsonObject.put("index", index);
            jsonObject.put("author", author);
            mixpanel.track("ArticleClick", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        bundle.putString("articleId", articleId);
        bundle.putString("listingType", listingType);
        bundle.putString("index", index);
        bundle.putString("author", author);
        mFirebaseAnalytics.logEvent("ArticleClick", bundle);
    }

    public static void pushViewShortStoryEvent(Context context, String screenName, String user, String articleId, String listingType, String index, String author) {
        Log.d("pushViewShortStoryEvent", "" + screenName + " --- " + listingType + " --- " + author);
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "ShortStoryClick", GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.ArticleId, articleId,
                GTMTags.TagListingType, listingType, GTMTags.TagIndex, index, GTMTags.Author, author));

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user);
            jsonObject.put("screen", screenName);
            jsonObject.put("articleId", articleId);
            jsonObject.put("listingType", listingType);
            jsonObject.put("index", index);
            jsonObject.put("author", author);
            mixpanel.track("ShortStoryClick", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        bundle.putString("articleId", articleId);
        bundle.putString("listingType", listingType);
        bundle.putString("index", index);
        bundle.putString("author", author);
        mFirebaseAnalytics.logEvent("ShortStoryClick", bundle);
    }

    public static void pushReportShortStoryEvent(Context context, String screenName, String user, String articleId, String reason, String type) {
        Log.d("pushViewArticleEvent", "" + screenName + " --- " + reason + " --- " + type);
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "ReportContent", GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.ArticleId, articleId,
                GTMTags.TagListingType, reason, GTMTags.TagIndex, type));

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user);
            jsonObject.put("screen", screenName);
            jsonObject.put("articleId", articleId);
            jsonObject.put("reportReason", reason);
            jsonObject.put("reportContentType", type);
            mixpanel.track("ReportContent", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        bundle.putString("articleId", articleId);
        bundle.putString("reportReason", reason);
        bundle.putString("reportContentType", type);

        mFirebaseAnalytics.logEvent("ReportContent", bundle);
    }

    public static void pushShortStoryCommentReplyChangeEvent(Context context, String screenName, String user, String articleId, String action, String type) {
        Log.d("ShortStoryCommentEvent", "" + screenName + " --- ");
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "StoryCommentReplyChangeEvent", GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.ArticleId, articleId,
                GTMTags.TagListingType, action, GTMTags.TagIndex, type));

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user);
            jsonObject.put("screen", screenName);
            jsonObject.put("articleId", articleId);
            jsonObject.put("contentAction", action);
            jsonObject.put("reportContentType", type);
            mixpanel.track("StoryCommentReplyChangeEvent", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        bundle.putString("articleId", articleId);
        bundle.putString("contentAction", action);
        bundle.putString("reportContentType", type);

        mFirebaseAnalytics.logEvent("StoryCommentReplyChangeEvent", bundle);
    }


    public static void pushArticleCommentReplyChangeEvent(Context context, String screenName, String user, String articleId, String action, String type) {
        Log.d("ShortStoryCommentEvent", "" + screenName + " --- ");
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "ArticleCommentReplyChangeEvent", GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.ArticleId, articleId,
                GTMTags.TagListingType, action, GTMTags.TagIndex, type));

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user);
            jsonObject.put("screen", screenName);
            jsonObject.put("articleId", articleId);
            jsonObject.put("contentAction", action);
            jsonObject.put("reportContentType", type);
            mixpanel.track("ArticleCommentReplyChangeEvent", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        bundle.putString("articleId", articleId);
        bundle.putString("contentAction", action);
        bundle.putString("reportContentType", type);

        mFirebaseAnalytics.logEvent("ArticleCommentReplyChangeEvent", bundle);
    }

    public static void pushViewTopicArticlesEvent(Context context, String screenName, String user, String topic) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "ViewTopicArticles", GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.TagTopic, topic));

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user);
            jsonObject.put("screen", screenName);
            jsonObject.put("topic", topic);
            mixpanel.track("ViewTopicArticles", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        bundle.putString("topic", topic);
        mFirebaseAnalytics.logEvent("ViewTopicArticles", bundle);
    }

    public static void pushViewQuickLinkArticlesEvent(Context context, String screenName, String user, String listingType) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "ViewQuicklinkArticles", GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.TagListingType, listingType));

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user);
            jsonObject.put("screen", screenName);
            jsonObject.put("listingType", listingType);
            mixpanel.track("ViewQuicklinkArticles", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        bundle.putString("listingType", listingType);
        mFirebaseAnalytics.logEvent("ViewQuicklinkArticles", bundle);
    }

    public static void pushArticleLoadedEvent(Context context, String screenName, String user, String articleId, String author, String language) {
        Log.d("pushArticleLoadedEvent", "" + screenName + " --- " + articleId + " --- " + language);
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "ArticleDetailLoaded",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.ArticleId, articleId, GTMTags.Author, author, GTMTags.TagLanguage, language));

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user);
            jsonObject.put("screen", screenName);
            jsonObject.put("articleId", articleId);
            jsonObject.put("author", author);
            jsonObject.put("articleLanguage", language);
            mixpanel.track("ArticleDetailLoaded", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        bundle.putString("articleId", articleId);
        bundle.putString("author", author);
        bundle.putString("articleLanguage", language);

        mFirebaseAnalytics.logEvent("ArticleDetailLoaded", bundle);
    }

    public static void pushStoryLoadedEvent(Context context, String screenName, String user, String articleId, String author, String language) {
        Log.d("pushStoryLoadedEvent", "" + screenName + " --- " + articleId + " --- " + language);
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "StoryDetailLoaded",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.ArticleId, articleId, GTMTags.Author, author, GTMTags.TagLanguage, language));

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user);
            jsonObject.put("screen", screenName);
            jsonObject.put("articleId", articleId);
            jsonObject.put("author", author);
            jsonObject.put("storyLanguage", language);
            mixpanel.track("StoryDetailLoaded", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        bundle.putString("articleId", articleId);
        bundle.putString("author", author);
        bundle.putString("articleLanguage", language);

        mFirebaseAnalytics.logEvent("StoryDetailLoaded", bundle);
    }

    public static void pushArticleSwipeEvent(Context context, String screenName, String user, String articleId, String fromIndex, String toIndex) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "ArticleSwiped",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.ArticleId, articleId, GTMTags.TagFromIndex, fromIndex, GTMTags.TagIndex, toIndex));

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user);
            jsonObject.put("screen", screenName);
            jsonObject.put("articleId", articleId);
            jsonObject.put("fromIndex", fromIndex);
            jsonObject.put("index", toIndex);
            mixpanel.track("ArticleSwiped", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        bundle.putString("articleId", articleId);
        bundle.putString("fromIndex", fromIndex);
        bundle.putString("index", toIndex);

        mFirebaseAnalytics.logEvent("ArticleSwiped", bundle);
    }

    public static void pushPlayArticleAudioEvent(Context context, String screenName, String user, String articleId, String author, String language) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "PlayArticleAudio",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.ArticleId, articleId, GTMTags.TagLanguage, language, GTMTags.Author, author));

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user);
            jsonObject.put("screen", screenName);
            jsonObject.put("articleId", articleId);
            jsonObject.put("author", author);
            jsonObject.put("articleLanguage", language);
            mixpanel.track("PlayArticleAudio", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        bundle.putString("articleId", articleId);
        bundle.putString("author", author);
        bundle.putString("articleLanguage", language);
        mFirebaseAnalytics.logEvent("PlayArticleAudio", bundle);
    }

    public static void pushStopArticleAudioEvent(Context context, String screenName, String user, String articleId, String author, String language, String duration) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "StopArticleAudio",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.ArticleId, articleId, GTMTags.TagLanguage, language, GTMTags.Author, author, GTMTags.TagDuration, duration));

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user);
            jsonObject.put("screen", screenName);
            jsonObject.put("articleId", articleId);
            jsonObject.put("author", author);
            jsonObject.put("articleLanguage", language);
            mixpanel.track("StopArticleAudio", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        bundle.putString("articleId", articleId);
        bundle.putString("author", author);
        bundle.putString("articleLanguage", language);
        mFirebaseAnalytics.logEvent("StopArticleAudio", bundle);
    }

    public static void pushShareArticleEvent(Context context, String screenName, String user, String articleId, String author, String shareMedium) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "ShareArticle",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.ArticleId, articleId, GTMTags.TagShareMedium, shareMedium, GTMTags.Author, author));

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user);
            jsonObject.put("screen", screenName);
            jsonObject.put("articleId", articleId);
            jsonObject.put("author", author);
            jsonObject.put("shareMedium", shareMedium);
            mixpanel.track("ShareArticle", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        bundle.putString("articleId", articleId);
        bundle.putString("author", author);
        bundle.putString("shareMedium", shareMedium);
        mFirebaseAnalytics.logEvent("ShareArticle", bundle);
    }

    public static void pushShareStoryEvent(Context context, String screenName, String user, String articleId, String author, String shareMedium) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "ShareStory",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.ArticleId, articleId, GTMTags.TagShareMedium, shareMedium, GTMTags.Author, author));

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user);
            jsonObject.put("screen", screenName);
            jsonObject.put("articleId", articleId);
            jsonObject.put("author", author);
            jsonObject.put("shareMedium", shareMedium);
            mixpanel.track("ShareStory", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        bundle.putString("articleId", articleId);
        bundle.putString("author", author);
        bundle.putString("shareMedium", shareMedium);
        mFirebaseAnalytics.logEvent("ShareStory", bundle);
    }

    public static void pushBookmarkArticleEvent(Context context, String screenName, String user, String articleId, String author) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "BookmarkArticle",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.ArticleId, articleId, GTMTags.Author, author));

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user);
            jsonObject.put("screen", screenName);
            jsonObject.put("articleId", articleId);
            jsonObject.put("author", author);
            mixpanel.track("BookmarkArticle", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        bundle.putString("articleId", articleId);
        bundle.putString("author", author);
        mFirebaseAnalytics.logEvent("BookmarkArticle", bundle);
    }

    public static void pushUnbookmarkArticleEvent(Context context, String screenName, String user, String articleId, String author) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "UnbookmarkArticle",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.ArticleId, articleId, GTMTags.Author, author));

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user);
            jsonObject.put("screen", screenName);
            jsonObject.put("articleId", articleId);
            jsonObject.put("author", author);
            mixpanel.track("UnbookmarkArticle", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        bundle.putString("articleId", articleId);
        bundle.putString("author", author);
        mFirebaseAnalytics.logEvent("UnbookmarkArticle", bundle);
    }

    public static void pushLikeArticleEvent(Context context, String screenName, String user, String articleId, String author) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "LikeArticle",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.ArticleId, articleId, GTMTags.Author, author));

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user);
            jsonObject.put("screen", screenName);
            jsonObject.put("articleId", articleId);
            jsonObject.put("author", author);
            mixpanel.track("LikeArticle", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        bundle.putString("articleId", articleId);
        bundle.putString("author", author);
        mFirebaseAnalytics.logEvent("LikeArticle", bundle);
    }

    public static void pushLikeStoryEvent(Context context, String screenName, String user, String articleId, String author) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "LikeStory",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.ArticleId, articleId, GTMTags.Author, author));

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user);
            jsonObject.put("screen", screenName);
            jsonObject.put("articleId", articleId);
            jsonObject.put("author", author);
            mixpanel.track("LikeStory", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        bundle.putString("articleId", articleId);
        bundle.putString("author", author);
        mFirebaseAnalytics.logEvent("LikeStory", bundle);
    }

    public static void pushUnlikeArticleEvent(Context context, String screenName, String user, String articleId, String author) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "UnlikeArticle",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.ArticleId, articleId, GTMTags.Author, author));

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user);
            jsonObject.put("screen", screenName);
            jsonObject.put("articleId", articleId);
            jsonObject.put("author", author);
            mixpanel.track("UnlikeArticle", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        bundle.putString("articleId", articleId);
        bundle.putString("author", author);
        mFirebaseAnalytics.logEvent("UnlikeArticle", bundle);
    }

    public static void pushWatchLaterArticleEvent(Context context, String screenName, String user, String articleId, String author) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "WatchLater",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.ArticleId, articleId, GTMTags.Author, author));

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user);
            jsonObject.put("screen", screenName);
            jsonObject.put("articleId", articleId);
            jsonObject.put("author", author);
            mixpanel.track("WatchLater", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        bundle.putString("articleId", articleId);
        bundle.putString("author", author);
        mFirebaseAnalytics.logEvent("WatchLater", bundle);
    }

    public static void pushRemoveWatchLaterArticleEvent(Context context, String screenName, String user, String articleId, String author) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "RemoveWatchLater",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.ArticleId, articleId, GTMTags.Author, author));

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user);
            jsonObject.put("screen", screenName);
            jsonObject.put("articleId", articleId);
            jsonObject.put("author", author);
            mixpanel.track("RemoveWatchLater", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        bundle.putString("articleId", articleId);
        bundle.putString("author", author);
        mFirebaseAnalytics.logEvent("RemoveWatchLater", bundle);
    }

    public static void pushCommentArticleEvent(Context context, String screenName, String user, String articleId, String author) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "CommentOnArticle",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.ArticleId, articleId, GTMTags.Author, author));

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user);
            jsonObject.put("screen", screenName);
            jsonObject.put("articleId", articleId);
            jsonObject.put("author", author);
            mixpanel.track("CommentOnArticle", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        bundle.putString("articleId", articleId);
        bundle.putString("author", author);
        mFirebaseAnalytics.logEvent("CommentOnArticle", bundle);
    }

    public static void pushReplyCommentArticleEvent(Context context, String screenName, String user, String articleId, String author) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "ReplyToComment",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.ArticleId, articleId, GTMTags.Author, author));

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user);
            jsonObject.put("screen", screenName);
            jsonObject.put("articleId", articleId);
            jsonObject.put("author", author);
            mixpanel.track("ReplyToComment", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        bundle.putString("articleId", articleId);
        bundle.putString("author", author);
        mFirebaseAnalytics.logEvent("ReplyToComment", bundle);
    }

    public static void pushFollowAuthorEvent(Context context, String screenName, String user, String author) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "FollowAuthor",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.Author, author));

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user);
            jsonObject.put("screen", screenName);
            jsonObject.put("author", author);
            mixpanel.track("FollowAuthor", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        bundle.putString("author", author);
        mFirebaseAnalytics.logEvent("FollowAuthor", bundle);
    }

    public static void pushUnfollowAuthorEvent(Context context, String screenName, String user, String author) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "UnfollowAuthor",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.Author, author));

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user);
            jsonObject.put("screen", screenName);
            jsonObject.put("author", author);
            mixpanel.track("UnfollowAuthor", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        bundle.putString("author", author);
        mFirebaseAnalytics.logEvent("UnfollowAuthor", bundle);
    }

    public static void pushFollowTopicEvent(Context context, String screenName, String user, String topic) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "FollowTopic",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.TagTopic, topic));

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user);
            jsonObject.put("screen", screenName);
            jsonObject.put("topic", topic);
            mixpanel.track("FollowTopic", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        bundle.putString("topic", topic);
        mFirebaseAnalytics.logEvent("FollowTopic", bundle);
    }

    public static void pushUnfollowTopicEvent(Context context, String screenName, String user, String topic) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "UnfollowTopic",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.TagTopic, topic));

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user);
            jsonObject.put("screen", screenName);
            jsonObject.put("topic", topic);
            mixpanel.track("UnfollowTopic", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        bundle.putString("topic", topic);
        mFirebaseAnalytics.logEvent("UnfollowTopic", bundle);
    }

    public static void pushEnableNotificationEvent(Context context, String screenName, String user, String notificationType) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "EnableNotification",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.NotificationType, notificationType));

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user);
            jsonObject.put("screen", screenName);
            jsonObject.put("notificationType", notificationType);
            mixpanel.track("EnableNotification", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        bundle.putString("notificationType", notificationType);
        mFirebaseAnalytics.logEvent("EnableNotification", bundle);
    }

    public static void pushDisableNotificationEvent(Context context, String screenName, String user, String notificationType) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "DisableNotification",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.NotificationType, notificationType));

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user);
            jsonObject.put("screen", screenName);
            jsonObject.put("notificationType", notificationType);
            mixpanel.track("DisableNotification", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        bundle.putString("notificationType", notificationType);
        mFirebaseAnalytics.logEvent("DisableNotification", bundle);
    }

    public static void pushEnableSubscriptionEvent(Context context, String screenName, String user, String subsriptionType) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "EnableSubscription",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.TagSubscriptionType, subsriptionType));

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user);
            jsonObject.put("screen", screenName);
            jsonObject.put("subscriptionType", subsriptionType);
            mixpanel.track("EnableSubscription", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        bundle.putString("subscriptionType", subsriptionType);
        mFirebaseAnalytics.logEvent("EnableSubscription", bundle);
    }

    public static void pushDisableSubscriptionEvent(Context context, String screenName, String user, String subsriptionType) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "DisableSubscription",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.TagSubscriptionType, subsriptionType));

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user);
            jsonObject.put("screen", screenName);
            jsonObject.put("subscriptionType", subsriptionType);
            mixpanel.track("DisableSubscription", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        bundle.putString("subscriptionType", subsriptionType);
        mFirebaseAnalytics.logEvent("DisableSubscription", bundle);
    }

    public static void pushEnableLanguageEvent(Context context, String screenName, String user, String language) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "EnableLanguage",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.TagLanguage, language));

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user);
            jsonObject.put("screen", screenName);
            jsonObject.put("preferredLanguage", language);
            mixpanel.track("EnableLanguage", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        bundle.putString("preferredLanguage", language);
        mFirebaseAnalytics.logEvent("EnableLanguage", bundle);
    }

    public static void pushDisableLanguageEvent(Context context, String screenName, String user, String language) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "DisableLanguage",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.TagLanguage, language));

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user);
            jsonObject.put("screen", screenName);
            jsonObject.put("preferredLanguage", language);
            mixpanel.track("DisableLanguage", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        bundle.putString("preferredLanguage", language);
        mFirebaseAnalytics.logEvent("DisableLanguage", bundle);
    }

    public static void pushBlogSetupSubmitEvent(Context context, String screenName, String user) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "BlogSetupSubmit",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user));

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user);
            jsonObject.put("screen", screenName);
            mixpanel.track("BlogSetupSubmit", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        mFirebaseAnalytics.logEvent("BlogSetupSubmit", bundle);
    }

    public static void pushBlogSetupSuccessEvent(Context context, String screenName, String user) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "BlogSetupSuccess",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user));

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user);
            jsonObject.put("screen", screenName);
            mixpanel.track("BlogSetupSuccess", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        mFirebaseAnalytics.logEvent("BlogSetupSuccess", bundle);
    }

    public static void pushPublishArticleEvent(Context context, String screenName, String user, String listingType) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "PublishArticle",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.TagListingType, listingType));

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user);
            jsonObject.put("screen", screenName);
            jsonObject.put("articleStatus", listingType);
            mixpanel.track("PublishArticle", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        bundle.putString("articleStatus", listingType);
        mFirebaseAnalytics.logEvent("PublishArticle", bundle);
    }

    public static void pushPublishStoryEvent(Context context, String screenName, String user, String listingType) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "PublishStory",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.TagListingType, listingType));

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user);
            jsonObject.put("screen", screenName);
            jsonObject.put("articleStatus", listingType);
            mixpanel.track("PublishStory", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        bundle.putString("articleStatus", listingType);
        mFirebaseAnalytics.logEvent("PublishStory", bundle);
    }

    public static void pushRemoveDraftEvent(Context context, String screenName, String user, String draftId) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "RemoveDraft",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.ArticleId, draftId));

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user);
            jsonObject.put("screen", screenName);
            jsonObject.put("draftId", draftId);
            mixpanel.track("RemoveDraft", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        bundle.putString("draftId", draftId);
        mFirebaseAnalytics.logEvent("RemoveDraft", bundle);
    }

    public static void pushEditDraftEvent(Context context, String screenName, String user, String draftId) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "EditDraft",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.ArticleId, draftId));

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user);
            jsonObject.put("screen", screenName);
            jsonObject.put("draftId", draftId);
            mixpanel.track("EditDraft", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        bundle.putString("draftId", draftId);
        mFirebaseAnalytics.logEvent("EditDraft", bundle);
    }

    public static void pushSuggestedTopicClickEvent(Context context, String screenName, String user, String language) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "SuggestedTopicEditorLaunch",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.TagLanguage, language));

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user);
            jsonObject.put("screen", screenName);
            jsonObject.put("suggestedLang", language);
            mixpanel.track("SuggestedTopicEditorLaunch", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        bundle.putString("suggestedLang", language);

        mFirebaseAnalytics.logEvent("SuggestedTopicEditorLaunch", bundle);
    }

    public static void pushLanguageChangeEvent(Context context, String screenName, String user, String language) {
        Log.d("pushLanguageChangeEvent", "" + screenName + " --- " + language);
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf(GTMTags.TagEvent, "AppLanguageChanged",
                GTMTags.TagScreen, screenName, GTMTags.USER_ID, user, GTMTags.TagLanguage, language));

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user);
            jsonObject.put("screen", screenName);
            jsonObject.put("appLanguage", language);
            mixpanel.track("AppLanguageChanged", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        bundle.putString("suggestedLang", language);

        mFirebaseAnalytics.logEvent("AppLanguageChanged", bundle);

    }

    public static void pushEvent(Context context, GTMEventType event, String user, String eventValue) {
        Log.d("GTMTopic", "" + event + "---" + user + "---" + eventValue);
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf("event", event, GTMTags.USER_ID, user, GTMTags.ScreenName, eventValue));

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString(GTMTags.USER_ID, user);
        bundle.putString(GTMTags.ScreenName, eventValue);

        mFirebaseAnalytics.logEvent(event.toString(), bundle);
    }

    public static void pushEventNotificationClick(Context context, GTMEventType event, String user, String screenName, String notificationType) {
        Log.d("GTMNotification", "" + event + "-" + user + "-" + screenName + "-" + notificationType);
        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user);
            jsonObject.put("screen", screenName);
            jsonObject.put("notificationType", notificationType);
            mixpanel.track("NotificationClick", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        bundle.putString("notificationType", notificationType);
        mFirebaseAnalytics.logEvent("NotificationClick", bundle);
    }

    public static void pushTopicFollowUnfollowEvent(Context context, GTMEventType event, String screenName, String user, String categoryNameIdCombination) {
        Log.d("GTMTopic", "" + event + "---" + screenName + "---" + user + "---" + categoryNameIdCombination);
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf("event", event, GTMTags.ScreenName, screenName, GTMTags.USER_ID, user, GTMTags.TopicChosen, categoryNameIdCombination));
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        bundle.putString(GTMTags.TopicChosen, categoryNameIdCombination);
        mFirebaseAnalytics.logEvent(event.toString(), bundle);
    }

    public static void pushSortListingEvent(Context context, GTMEventType event, String user, String screenName, String sortType) {
        Log.d("GTMSort", "" + event + "---" + user + "---" + screenName + "---" + sortType);
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf("event", event, GTMTags.USER_ID, user, GTMTags.ScreenName, screenName, GTMTags.Type, sortType));

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        bundle.putString("screen", screenName);
        bundle.putString(GTMTags.Type, sortType);
        mFirebaseAnalytics.logEvent(event.toString(), bundle);
    }

    public static void pushGenericEvent(Context context, String event, String user, String screenName) {
        try {
            MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("screen", screenName);
                jsonObject.put(GTMTags.Timestamp, String.valueOf(System.currentTimeMillis()));
                mixpanel.track(event, jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }

            FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
            Bundle bundle = new Bundle();
            bundle.putString("userId", user);
            bundle.putString("screen", screenName);
            bundle.putString(GTMTags.Timestamp, "" + System.currentTimeMillis());
            bundle.putString(GTMTags.Language, "" + SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()));
            mFirebaseAnalytics.logEvent(event, bundle);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void pushProfileEvents(Context context, String event, String currentScreen, String CTA, String params) {
        try {
            MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(GTMTags.Current_Screen, currentScreen);
                jsonObject.put(GTMTags.CTA, CTA);
                jsonObject.put(BADGE_NAME, params);
                jsonObject.put(GTMTags.Platform, "android");
                jsonObject.put(GTMTags.Language, SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()));
                jsonObject.put(GTMTags.USER_ID, SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                jsonObject.put(GTMTags.Timestamp, String.valueOf(System.currentTimeMillis()));
                mixpanel.track(event, jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }

            FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
            Bundle bundle = new Bundle();
            bundle.putString(GTMTags.Current_Screen, currentScreen);
            bundle.putString(GTMTags.CTA, CTA);
            bundle.putString(BADGE_NAME, params);
            bundle.putString(GTMTags.Platform, "android");
            bundle.putString(GTMTags.Language, SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()));
            bundle.putString(GTMTags.Timestamp, String.valueOf(System.currentTimeMillis()));
            mFirebaseAnalytics.logEvent(event, bundle);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void pushNotificationClickEvent(Context context, String type, String user, String screenName) {
        try {
            MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("screen", screenName);
                jsonObject.put("type", type);
                jsonObject.put(GTMTags.Timestamp, String.valueOf(System.currentTimeMillis()));
                mixpanel.track("PushNotification", jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }

            FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
            Bundle bundle = new Bundle();
            bundle.putString("userId", user);
            bundle.putString("screen", screenName);
            bundle.putString("type", type);
            bundle.putString(GTMTags.Timestamp, "" + System.currentTimeMillis());
            bundle.putString(GTMTags.Language, "" + SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()));
            mFirebaseAnalytics.logEvent("PushNotification", bundle);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void pushNotificationCenterItemClickEvent(Context context, String type, String user, String screenName) {
        try {
            MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("screen", screenName);
                jsonObject.put("type", type);
                jsonObject.put(GTMTags.Timestamp, String.valueOf(System.currentTimeMillis()));
                mixpanel.track("NotificationCenterClick", jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }

            FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
            Bundle bundle = new Bundle();
            bundle.putString("userId", user);
            bundle.putString("screen", screenName);
            bundle.putString("type", type);
            bundle.putString(GTMTags.Timestamp, "" + System.currentTimeMillis());
            bundle.putString(GTMTags.Language, "" + SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()));
            mFirebaseAnalytics.logEvent("NotificationCenterClick", bundle);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
