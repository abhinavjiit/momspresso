package com.mycity4kids.gtmutils;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.tagmanager.DataLayer;
import com.google.android.gms.tagmanager.TagManager;
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

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user);
            jsonObject.put("screen", screenName);
            mixpanel.track("AppScreenOpened", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    }

    public static void pushEvent(Context context, GTMEventType event, String user, String eventValue) {
        Log.d("GTMTopic", "" + event + "---" + user + "---" + eventValue);
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf("event", event, GTMTags.USER_ID, user, GTMTags.ScreenName, eventValue));
    }

    public static void pushEventNotificationClick(Context context, GTMEventType event, String user, String screenName, String notificationType) {
        Log.d("GTMNotification", "" + event + "-" + user + "-" + screenName + "-" + notificationType);
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf("event", event, GTMTags.USER_ID, user, GTMTags.ScreenName, screenName, GTMTags.Type, notificationType));

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
    }

    public static void pushTopicFollowUnfollowEvent(Context context, GTMEventType event, String screenName, String user, String categoryNameIdCombination) {
        Log.d("GTMTopic", "" + event + "---" + screenName + "---" + user + "---" + categoryNameIdCombination);
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf("event", event, GTMTags.ScreenName, screenName, GTMTags.USER_ID, user, GTMTags.TopicChosen, categoryNameIdCombination));
    }

    public static void pushSortListingEvent(Context context, GTMEventType event, String user, String screenName, String sortType) {
        Log.d("GTMSort", "" + event + "---" + user + "---" + screenName + "---" + sortType);
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf("event", event, GTMTags.USER_ID, user, GTMTags.ScreenName, screenName, GTMTags.Type, sortType));
    }
}
