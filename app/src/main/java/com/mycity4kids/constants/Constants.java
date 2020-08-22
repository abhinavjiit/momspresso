package com.mycity4kids.constants;

public class Constants {

    public static final String CATEGORY_ID = "category_id";
    public static final String ARTICLE_ID = "article_id";
    public static final String CHALLENGE_ID = "challenge";
    public static final String AUTHOR_ID = "authorId";
    public static final String VIDEO_ID = "videoId";

    public static final String URL = "newsletter_url";

    public static final String BLOG_SLUG = "blogSlug";
    public static final String TITLE_SLUG = "titleSlug";
    public static final String FROM_SCREEN = "fromScreen";
    public static final String ARTICLE_OPENED_FROM = "articleOpenedFrom";
    public static final String ARTICLE_INDEX = "index";
    public static final String AUTHOR = "author";
    public static final String STREAM_URL = "streamUrl";

    public static final String WEB_VIEW_URL = "web_view_url";

    public static final int FILTER_BLOG = 5;
    public static final int BLOG_FOLLOW_STATUS = 6;

    public static final String SUCCESS_MESSAGE = "updated successfully";

    public static final String USER_ID = "userId";
    public static final String MODE = "mode";

    public static String TAB_POSITION = "tab_position";
    public static final String KEY_RECENT = "recent";
    public static final String KEY_POPULAR = "popular";
    public static final String KEY_TRENDING = "trending";
    public static final String KEY_CHALLENGE = "challenge";
    public static final String KEY_FOR_YOU = "foryou";
    public static final String KEY_FOLLOWING = "following";
    public static final String KEY_IN_YOUR_CITY = "inYourCity";
    public static final String KEY_EDITOR_PICKS = "editorPicks";
    public static final String KEY_TODAYS_BEST = "todaysBest";
    public static final String KEY_MOMSPRESSO = "momspresso";


    public static String SORT_TYPE = "sorttype";
    public static String SEARCH_PARAM = "search_param";

    public static String FILTER_NAME = "filter_name";

    public static String FILTER_BLOG_SORT_TYPE = "filter_blog_sort";
    public static String DEEPLINK_URL = "deeplink_url";
    public static String SUGGESTED_TOPICS_FRAGMENT = "suggested_topics";
    public static String SETTINGS_FRAGMENT = "fragment_settings";
    public static String PROFILE_FRAGMENT = "fragment_profile";
    public static String GROUP_LISTING_FRAGMENT = "fragment_group_listing";
    public static String CREATE_CONTENT_PROMPT = "createContentPrompt";
    public static String DISCOVER_CONTENT = "discoverContent";

    //API Failure status
    public static String FAILURE = "failure";
    //API Success status
    public static String SUCCESS = "success";

    public enum TypeOfDurables {
        AIR_CONDITIONER(1, "Air Conditioner"), REFRIGERATOR(2, "Refrigerator"), CAR(3, "Car"), TWO_WHEELER(4,
                "Two wheeler"), AIR_PURIFIER(5, "Air Purifier"),
        WASHING_MACHINE(6, "Washing Machine"), TV(7, "TV"), MUSIC_SYSTEM(8, "Music System"), WATER_HEATER(9,
                "Water Heater"), MOBILE_PHONE(10, "Mobile phone"),
        CAMERA(11, "Camera"), MICROWAVE_OVEN(12, "Microwave Oven"), LAPTOP(13, "Laptop");
        private final String name;
        private final int id;

        TypeOfDurables(int i, String name) {
            this.name = name;
            this.id = i;
        }

        public int getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public static String findById(int id) {
            for (TypeOfDurables typeOfDurables : TypeOfDurables.values()) {
                if (id == typeOfDurables.getId()) {
                    return typeOfDurables.name;
                }
            }
            return "";
        }

        public static String findByName(String name) {
            for (TypeOfDurables typeOfDurables : TypeOfDurables.values()) {
                if (name.toLowerCase().equalsIgnoreCase(typeOfDurables.getName())) {
                    return typeOfDurables.id + "";
                }
            }
            return null;
        }
    }

    public enum TypeOfInterest {
        BEAUTY(0, "Beauty and personal care"), EDUCATION(1, "Child’s education"), FAMILY(2, "Family matters"), FINANCES(
                3, "Finances"), FOOD(4, "Food"),
        HEALTH(5, "Health"), HAME_CARE(6, "Home care"), HYGIENE(7, "Hygiene"), PARENTING(8, "Parenting"), TRAVEL(9,
                "Travel"),
        SOCIAL_EVENTS(10, "Upcoming social events");
        private final String name;
        private final int id;

        TypeOfInterest(int i, String name) {
            this.name = name;
            this.id = i;
        }

        public int getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public static String findById(int id) {
            for (TypeOfInterest typeOfInterest : TypeOfInterest.values()) {
                if (id == typeOfInterest.getId()) {
                    return typeOfInterest.name;
                }
            }
            return "";
        }

        public static String findByName(String name) {
            for (TypeOfInterest typeOfInterest : TypeOfInterest.values()) {
                if (name.toLowerCase().equalsIgnoreCase(typeOfInterest.getName().toLowerCase())) {
                    return typeOfInterest.id + "";
                }
            }
            return null;
        }
    }


    public enum TypeOfLanguages {
        LOCALE_ENGLISH("en", "English"), LOCALE_HINDI("hi", "Hindi"), LOCALE_MARATHI("mr", "Marathi"), LOCALE_BENGALI(
                "bn", "Bangali"), LOCALE_TAMIL("ta", "Tamil"), LOCALE_TELUGU("te", "Telgu"),
        LOCALE_KANNADA("kn", "Kannada"), LOCALE_MALAYALAM("ml", "Malayalam"), LOCALE_GUJARATI("gu",
                "Gujarati"), LOCALE_PUNJABI("pa", "Punjabi");
        private final String name;
        private final String id;

        TypeOfLanguages(String i, String name) {
            this.name = name;
            this.id = i;
        }

        public String getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public static String findById(String id) {
            for (TypeOfLanguages typeOfLanguages : TypeOfLanguages.values()) {
                if (id.trim().toLowerCase().equalsIgnoreCase(typeOfLanguages.getId())) {
                    return typeOfLanguages.name;
                }
            }
            return "";//ਪੰਜਾਬ
        }

        public static String findByName(String name) {
            for (TypeOfLanguagesWithContent typeOfLanguages : TypeOfLanguagesWithContent.values()) {
                if (name.trim().toLowerCase().equalsIgnoreCase(typeOfLanguages.getName())) {
                    return typeOfLanguages.id;
                }
            }
            return "";
        }
    }

    public enum TypeOfLanguagesWithContent {
        LOCALE_ENGLISH("en", "English"), LOCALE_HINDI("hi", "हिंदी"), LOCALE_MARATHI("mr", "मराठी"), LOCALE_BENGALI(
                "bn", "বাংলা"), LOCALE_TAMIL("ta", "தமிழ்"), LOCALE_TELUGU("te", "తెలుగు"),
        LOCALE_KANNADA("kn", "ಕನ್ನಡ"), LOCALE_MALAYALAM("ml", "മലയാളം"), LOCALE_GUJARATI("gu",
                "ગુજરાતી"), LOCALE_PUNJABI("pa", "ਪੰਜਾਬੀ");
        private final String name;
        private final String id;

        TypeOfLanguagesWithContent(String i, String name) {
            this.name = name;
            this.id = i;
        }

        public String getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public static String findById(String id) {
            for (TypeOfLanguagesWithContent typeOfLanguages : TypeOfLanguagesWithContent.values()) {
                if (id.trim().toLowerCase().equalsIgnoreCase(typeOfLanguages.getId())) {
                    return typeOfLanguages.name;
                }
            }
            return "";
        }

        public static String findByName(String name) {
            for (TypeOfLanguages typeOfLanguages : TypeOfLanguages.values()) {
                if (name.trim().toLowerCase().equalsIgnoreCase(typeOfLanguages.getName())) {
                    return typeOfLanguages.id;
                }
            }
            return "";
        }
    }

    public enum TrackerStatusMapping {
        APPROVED(2, "APPROVED"), APPLIED(3, "APPLIED"), PROOF_APPPROVAL(7, "PROOFS APPROVED"), PROOF_SUBMITTED_REJECTED(
                10, "PROOFS SUBMITTED REJECTED"),
        APPLICATION_UNDER_REVIEW(11, "APPLICATION UNDER REVIEW"), PROOF_UNDER_REVIEW(12, "PROOFS UNDER REVIEW"),
        PAYMENT_IN_PROCESS(13, "PAYMENT IN PROCESS"), PAYMENT_DONE(14, "PAYMENT DONE"), PROOF_SUBMITTED(15,
                "PROOFS SUBMITTED");
        private final String name;
        private final int id;

        TrackerStatusMapping(int i, String name) {
            this.name = name;
            this.id = i;
        }

        public int getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public static String findById(int id) {
            for (TrackerStatusMapping trackerStatusMapping : TrackerStatusMapping.values()) {
                if (id == trackerStatusMapping.id) {
                    return trackerStatusMapping.name;
                }
            }
            return "";
        }

        public static int findByName(String name) {
            for (TrackerStatusMapping trackerStatusMapping : TrackerStatusMapping.values()) {
                if (name.trim().toLowerCase().equalsIgnoreCase(trackerStatusMapping.getName())) {
                    return trackerStatusMapping.id;
                }
            }
            return 0;
        }
    }

    public enum PopListRequestType {
        INTEREST,
        DURABLES,
        INCOME,
        PROFESSION,
        LANGUAGE
    }

    public enum SocialPlatformName {
        facebook,
        twitter,
        youtube,
        instagram,
        website
    }

}
