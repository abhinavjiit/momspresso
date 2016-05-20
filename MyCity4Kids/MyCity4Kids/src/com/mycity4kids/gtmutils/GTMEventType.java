package com.mycity4kids.gtmutils;

/**
 * Created by anshul on 2/11/16.
 */
public enum  GTMEventType {
    MC4KToday_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "mc4kTodayClicked";
                }
            },
    CALENDAR_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "calendarClicked";
                }
            },
    TODO_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "todoClicked";
                }
            },
    UPCOMING_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "upcomingClicked";
                }
            },
    RESOURCES_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "resourcesClicked";
                }
            },
    BLOGS_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "blogsClicked";
                }
            },
    MEETCONTRIBUTORS_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "meetContributorsClicked";
                }
            },
    SETTINGS_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "settingsClicked";
                }
            },
    FEEDBACK_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "feedbackClicked";
                }
            },
    TELLFRIEND_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "tellFriendClicked";
                }
            },
    HELP_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "helpClicked";
                }
            },
    FAVOURITE_BLOG_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "blogFavouriteClicked";
                }
            },
    SHARE_BLOG_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "shareBlogClicked";
                }
            },
    FUNPLACES_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "funPlacesClicked";
                }
            },
    HOBBIES_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "hobbiesClicked";
                }
            },
    SPORTS_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "sportsClicked";
                }
            },
    ENHANCED_LEARNING_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "enhancedLearningClicked";
                }
            },
    TUTIONS_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "tutionsClicked";
                }
            },
    PLAY_SCHOOLS_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "playSchoolsClicked";
                }
            },
    SCHOOLS_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "schoolsClicked";
                }
            },
    BIRTHDAY_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "birthdayClicked";
                }
            },
    WHERETOSHOP_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "whereToShopClicked";
                }
            },
    DAYCARE_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "daycareClicked";
                }
            },
    HEALTH_WELLNESS_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "healthWellnessClicked";
                }
            },
    SHARE_SPOUCE_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "shareSpouceClicked";
                }
            },

    FILTER_RESOURCES_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "filterResourcesClicked";
                }
            },
    CALL_RESOURCES_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "callResourcesClicked";
                }
            },
    ADDPHOTOS_RESOURCES_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "addphotosResourcesClicked";
                }
            },
    WRITEREVIEW_RESOURCES_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "writeReviewResourcesClicked";
                }
            },
    FAVOURITE_RESOURCES_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "favouriteResourcesClicked";
                }
            },
    SHARE_RESOURCES_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "shareResourcesClicked";
                }
            },
    CALL_EVENT_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "callEventClicked";
                }
            },
    SHARE_EVENT_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "shareEventClicked";
                }
            },
    ADDPHOTOS_EVENT_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "addPhotosEventClicked";
                }
            },
    EVENTLIST_PLUS_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "eventListPlusClicked";
                }
            },
    ADDCALENDAR_EVENT_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "addCalendarEventClicked";
                }
            },
    RATE_APP_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "rateAppClicked";
                }
            },

    NOT_RATE_EVENT_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "notRateClicked";
                }
            },
    APPOINTMENT_NOTIFICATION_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "appointmentNotificationClicked";
                }
            },
    TASK_NOTIFICATION_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "taskNotificationClicked";
                }
            },
    FAMILY_NOTICATION_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "famliyNoticationClicked";
                }
            },
    BLOG_NOTIFICATION_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "blogNoticationClicked";
                }
            },
    NEWSLETTER_NOTICATION_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "newsletterClicked";
                }
            },
    WEEKLY_CALENDAR_NOTIFICATION_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "weeklyClicked";
                }
            },
    EVENT_DETAIL_NOTIFICATION_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "eventDetailNotificationClicked";
                }
            },
    UPCOMING_EVENTS_NOTIFICATION_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "upcomingEventsNotificationClicked";
                }
            },
    PLAN_WEEK_NOTIFICATION_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "planWeekNotificationClicked";
                }
            },
    PUBLISH_ARTICLE_BUTTON_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "publishArticleClicked";
                }
            },
    SETUP_BLOG_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "setupBlogClicked";
                }
            },
    ADD_BLOG_CLICKED_EVENT
            {
                @Override
                public String toString() {
                    return "addBlogClicked";
                }
            },


}
